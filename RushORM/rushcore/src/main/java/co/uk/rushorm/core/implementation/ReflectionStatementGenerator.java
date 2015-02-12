package co.uk.rushorm.core.implementation;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import co.uk.rushorm.core.AnnotationCache;
import co.uk.rushorm.core.Rush;
import co.uk.rushorm.core.RushColumns;
import co.uk.rushorm.core.RushStringSanitizer;
import co.uk.rushorm.core.annotations.RushDisableAutodelete;
import co.uk.rushorm.core.annotations.RushIgnore;
import co.uk.rushorm.core.annotations.RushList;
import co.uk.rushorm.core.exceptions.RushListAnnotationDoesNotMatchClassException;
import co.uk.rushorm.core.RushStatementGenerator;

/**
 * Created by stuartc on 11/12/14.
 */
public class ReflectionStatementGenerator implements RushStatementGenerator {

    private static final int GROUP_SIZE = 250;

    private static final String MULTIPLE_INSERT_TEMPLATE = "INSERT INTO %s " +
            "(%s)\n" +
            "VALUES %s;";

    private static final String MULTIPLE_UPDATE_TEMPLATE = "UPDATE %s " +
            "%s;";

    private static final String MULTIPLE_DELETE_JOIN_TEMPLATE = "DELETE FROM %s \n" +
            "WHERE %s;";

    private static final String MULTIPLE_INSERT_JOIN_TEMPLATE = "INSERT INTO %s " +
            "(parent, child)\n" +
            "VALUES %s;";

    private static final String MULTIPLE_DELETE_TEMPLATE = "DELETE FROM %s \n" +
            "WHERE %s;";

    private final Map<Class, AnnotationCache> annotationCache;

    private final RushStringSanitizer rushStringSanitizer;
    private final RushColumns rushColumns;

    public ReflectionStatementGenerator(RushStringSanitizer rushStringSanitizer, RushColumns rushColumns, Map<Class, AnnotationCache> annotationCache) {
        this.rushStringSanitizer = rushStringSanitizer;
        this.rushColumns = rushColumns;
        this.annotationCache = annotationCache;
    }

    private interface LoopCallBack {
        public void start();
        public void actionAtIndex(int index);
        public void join();
        public void doAction();
    }

    private void doLoop(int max, int interval, LoopCallBack callBack) {
        callBack.start();
        for (int i = 0; i < max; i ++) {
            callBack.actionAtIndex(i);
            if(i > 0 && i % interval == 0) {
                callBack.doAction();
                callBack.start();
            } else if(i < max - 1) {
                callBack.join();
            }
        }
        if(max == 1 || (max - 1) % interval != 0) {
            callBack.doAction();
        }
    }

    private void deleteManyJoins(Map<String, List<Long>> joinDeletes, final Callback saveCallback) {

        for (final Map.Entry<String, List<Long>> entry : joinDeletes.entrySet()) {
            final StringBuilder columnsString = new StringBuilder();

            final List<Long> ids = entry.getValue();

            doLoop(ids.size(), GROUP_SIZE, new LoopCallBack() {
                @Override
                public void start() {
                    columnsString.delete(0, columnsString.length());
                }

                @Override
                public void actionAtIndex(int index) {
                    columnsString.append("parent=")
                            .append(ids.get(index));
                }

                @Override
                public void join() {
                    columnsString.append(" OR ");
                }

                @Override
                public void doAction() {
                    String sql = String.format(MULTIPLE_DELETE_JOIN_TEMPLATE, entry.getKey(),
                            columnsString.toString());

                    saveCallback.deleteStatement(sql);
                }
            });
        }
    }

    /*** Save ***/
    private class BasicJoin {
        private final String table;
        private final Rush parent;
        private final Rush child;

        private BasicJoin(String table, Rush parent, Rush child) {
            this.table = table;
            this.parent = parent;
            this.child = child;
        }
    }

    private class BasicUpdate {
        private final List<String> values;
        private final long id;

        private BasicUpdate(List<String> values, long id) {
            this.values = values;
            this.id = id;
        }
    }

    private class BasicCreate {
        private final List<String> values;
        private final Rush rush;

        private BasicCreate(List<String> values, Rush rush) {
            this.values = values;
            this.rush = rush;
        }
    }

    private void addJoin(Map<String, List<BasicJoin>> joins, BasicJoin basicJoin) {
        if(!joins.containsKey(basicJoin.table)) {
            joins.put(basicJoin.table, new ArrayList<BasicJoin>());
        }
        joins.get(basicJoin.table).add(basicJoin);
    }

    @Override
    public void generateSaveOrUpdate(List<? extends Rush> objects, Callback saveCallback) {

        List<Rush> rushObjects = new ArrayList<>();

        Map<Class, List<BasicCreate>> createValues = new HashMap<>();
        Map<Class, List<BasicUpdate>> updateValues = new HashMap<>();
        Map<Class, List<String>> columns = new HashMap<>();

        Map<String, List<Long>> joinDeletes = new HashMap<>();
        Map<String, List<BasicJoin>> joinValues = new HashMap<>();

        for(Rush rush : objects) {
            generateSaveOrUpdate(rush, rushObjects, createValues, updateValues, columns, joinDeletes, joinValues);
        }

        deleteManyJoins(joinDeletes, saveCallback);
        createObjects(createValues, columns, saveCallback);
        updateObjects(updateValues, columns, saveCallback);
        createManyJoins(joinValues, saveCallback);

    }

    private void generateSaveOrUpdate(Rush rush, List<Rush> rushObjects,
                                      Map<Class, List<BasicCreate>> createValuesMap,
                                      Map<Class, List<BasicUpdate>> updateValuesMap,
                                      Map<Class, List<String>> columnsMap,
                                      Map<String, List<Long>> joinDeletesMap,
                                      Map<String, List<BasicJoin>> joinValuesMap) {

        if (rushObjects.contains(rush)) {
            // Exit if object is referenced by child
            return;
        }

        rushObjects.add(rush);

        List<String> columns = new ArrayList<>();
        List<String> values = new ArrayList<>();
        List<Field> fields = new ArrayList<>();

        ReflectionUtils.getAllFields(fields, rush.getClass());

        if(!annotationCache.containsKey(rush.getClass())) {
            annotationCache.put(rush.getClass(), new AnnotationCache(rush.getClass(), fields));
        }

        for (Field field : fields) {
            if (!annotationCache.get(rush.getClass()).getFieldToIgnore().contains(field.getName())) {
                field.setAccessible(true);
                List<BasicJoin> joins = new ArrayList<>();
                String joinTableName = joinFromField(joins, rush, field);
                if (joinTableName == null) {
                    if (rushColumns.supportsField(field)) {
                        try {
                            String value = rushColumns.valueFromField(rush, field, rushStringSanitizer);
                            columns.add(field.getName());
                            values.add(value);
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    if (rush.getId() > 0) {
                        // Clear join tables and re save rows to catch any deleted or changed children
                        if (!joinDeletesMap.containsKey(joinTableName)) {
                            joinDeletesMap.put(joinTableName, new ArrayList<Long>());
                        }
                        joinDeletesMap.get(joinTableName).add(rush.getId());
                    }
                    for(BasicJoin join : joins) {
                        generateSaveOrUpdate(join.child, rushObjects, createValuesMap, updateValuesMap, columnsMap, joinDeletesMap, joinValuesMap);
                        addJoin(joinValuesMap, join);
                    }
                }
            }
        }

        if (!columnsMap.containsKey(rush.getClass())) {
            columnsMap.put(rush.getClass(), columns);
        }

        if (rush.getId() < 0) {
            if (!createValuesMap.containsKey(rush.getClass())) {
                createValuesMap.put(rush.getClass(), new ArrayList<BasicCreate>());
            }
            createValuesMap.get(rush.getClass()).add(new BasicCreate(values, rush));
        } else if (columns.size() > 0) {
            if (!updateValuesMap.containsKey(rush.getClass())) {
                updateValuesMap.put(rush.getClass(), new ArrayList<BasicUpdate>());
            }
            updateValuesMap.get(rush.getClass()).add(new BasicUpdate(values, rush.getId()));
        }
    }

    private String joinFromField(List<BasicJoin> joins, Rush rush, Field field) {

        if (Rush.class.isAssignableFrom(field.getType())) {
            String tableName = ReflectionUtils.joinTableNameForClass(rush.getClass(), field.getType(), field);
            try {
                Rush child = (Rush) field.get(rush);
                if (child != null) {
                    joins.add(new BasicJoin(tableName, rush, child));
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            return tableName;
        }

        if(annotationCache.get(rush.getClass()).getListsFields().containsKey(field.getName())) {
            Class listClass = annotationCache.get(rush.getClass()).getListsFields().get(field.getName());
            String tableName = ReflectionUtils.joinTableNameForClass(rush.getClass(), listClass, field);
            if (Rush.class.isAssignableFrom(listClass)) {
                try {
                    List<Rush> children = (List<Rush>) field.get(rush);
                    if (children != null) {
                        for (Rush child : children) {
                            joins.add(new BasicJoin(tableName, rush, child));
                        }
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
            return tableName;
        }
        return null;
    }

    private void createObjects(Map<Class, List<BasicCreate>> valuesMap, final Map<Class, List<String>> columnsMap, final Callback saveCallback) {

        for (final Map.Entry<Class, List<BasicCreate>> entry : valuesMap.entrySet()) {
            columnsMap.get(entry.getKey()).add(0, "id");
            final long nextId = saveCallback.lastIdInTable(ReflectionUtils.tableNameForClass(entry.getKey())) + 1;

            final StringBuilder columnsString = new StringBuilder();
            final List<BasicCreate> creates = entry.getValue();

            doLoop(creates.size(), GROUP_SIZE, new LoopCallBack() {
                @Override
                public void start() {
                    columnsString.delete(0, columnsString.length());
                }

                @Override
                public void actionAtIndex(int index) {
                    saveCallback.addRush(creates.get(index).rush, nextId + index);
                    creates.get(index).values.add(0, Long.toString(nextId + index));
                    columnsString.append("(")
                            .append(commaSeparated(creates.get(index).values))
                            .append(")");
                }

                @Override
                public void join() {
                    columnsString.append(", ");
                }

                @Override
                public void doAction() {
                    String sql = String.format(MULTIPLE_INSERT_TEMPLATE,
                            ReflectionUtils.tableNameForClass(entry.getKey()),
                            commaSeparated(columnsMap.get(entry.getKey())),
                            columnsString.toString());

                    saveCallback.createdOrUpdateStatement(sql);
                }
            });
        }
    }

    private String commaSeparated(List<String> values) {
        StringBuilder string = new StringBuilder();
        for (int i = 0; i < values.size(); i++) {
            string.append(values.get(i));
            if (i < values.size() - 1) {
                string.append(",");
            }
        }
        return string.toString();
    }

    private void updateObjects(Map<Class, List<BasicUpdate>> valuesMap, final Map<Class, List<String>> columnsMap, final Callback saveCallback) {

        for (final Map.Entry<Class, List<BasicUpdate>> entry : valuesMap.entrySet()) {
            final StringBuilder columnsString = new StringBuilder();

            final List<BasicUpdate> values = entry.getValue();

            doLoop(values.size(), GROUP_SIZE, new LoopCallBack() {
                @Override
                public void start() {
                    columnsString.delete(0, columnsString.length());
                }

                @Override
                public void actionAtIndex(int index) {
                    columnsString.append("\nSet ")
                            .append(updateSection(columnsMap.get(entry.getKey()), values.get(index).values))
                            .append(" Where id=")
                            .append(values.get(index).id);
                }

                @Override
                public void join() {
                    columnsString.append(", ");
                }

                @Override
                public void doAction() {
                    String sql = String.format(MULTIPLE_UPDATE_TEMPLATE,
                            ReflectionUtils.tableNameForClass(entry.getKey()),
                            columnsString.toString());
                    saveCallback.createdOrUpdateStatement(sql);
                }
            });
        }
    }

    private String updateSection(List<String> columns, List<String> values) {
        StringBuilder string = new StringBuilder();
        for (int i = 0; i < columns.size(); i++) {
            string.append(columns.get(i))
                    .append("=")
                    .append(values.get(i));
            if (i < columns.size() - 1) {
                string.append(",");
            }
        }
        return string.toString();
    }

    private void createManyJoins(Map<String, List<BasicJoin>> joinValues, final Callback saveCallback) {

        for (final Map.Entry<String, List<BasicJoin>> entry : joinValues.entrySet()) {
            final StringBuilder columnsString = new StringBuilder();
            final List<BasicJoin> values = entry.getValue();

            doLoop(values.size(), GROUP_SIZE, new LoopCallBack() {
                @Override
                public void start() {
                    columnsString.delete(0, columnsString.length());
                }

                @Override
                public void actionAtIndex(int index) {
                    columnsString.append("(")
                            .append(values.get(index).parent.getId())
                            .append(",")
                            .append(values.get(index).child.getId())
                            .append(")");
                }

                @Override
                public void join() {
                    columnsString.append(", ");
                }

                @Override
                public void doAction() {
                    String sql = String.format(MULTIPLE_INSERT_JOIN_TEMPLATE, entry.getKey(),
                            columnsString.toString());
                    saveCallback.createdOrUpdateStatement(sql);
                }
            });
        }
    }

    /*** Delete ***/
    @Override
    public void generateDelete(List<? extends Rush> objects, Callback callback) {
        Map<String, List<Long>> joinDeletes = new HashMap<>();
        Map<String, List<Long>> deletes = new HashMap<>();

        for (Rush object : objects) {
            generateDelete(object, deletes, joinDeletes, callback);
        }

        deleteManyJoins(joinDeletes, callback);
        deleteMany(deletes, callback);
    }

    public void generateDelete(Rush rush, Map<String, List<Long>> deletes, Map<String, List<Long>> joinDeletes, Callback callback) {

        if (rush.getId() < 0) {
            return;
        }

        long id = rush.getId();
        callback.removeRush(rush);

        List<Field> fields = new ArrayList<>();
        ReflectionUtils.getAllFields(fields, rush.getClass());

        if(!annotationCache.containsKey(rush.getClass())) {
            annotationCache.put(rush.getClass(), new AnnotationCache(rush.getClass(), fields));
        }

        for (Field field : fields) {
            field.setAccessible(true);
            if (!annotationCache.get(rush.getClass()).getFieldToIgnore().contains(field.getName())) {
                String joinTableName = null;
                if (Rush.class.isAssignableFrom(field.getType())) {
                    try {
                        Rush child = (Rush) field.get(rush);
                        if (child != null) {
                            joinTableName = ReflectionUtils.joinTableNameForClass(rush.getClass(), child.getClass(), field);
                            if (!annotationCache.get(rush.getClass()).getDisableAutoDelete().contains(field.getName())) {
                                generateDelete(child, deletes, joinDeletes, callback);
                            }
                        }
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                } else if (annotationCache.get(rush.getClass()).getListsFields().containsKey(field.getName())) {
                    try {
                        List<Rush> fieldChildren = (List<Rush>) field.get(rush);
                        if (fieldChildren != null && fieldChildren.size() > 0) {
                            joinTableName = ReflectionUtils.joinTableNameForClass(rush.getClass(), annotationCache.get(rush.getClass()).getListsFields().get(field.getName()), field);
                            if (!annotationCache.get(rush.getClass()).getDisableAutoDelete().contains(field.getName())) {
                                for(Rush child : fieldChildren) {
                                    generateDelete(child, deletes, joinDeletes, callback);
                                }
                            }
                        }
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
                if(joinTableName != null) {
                    if (!joinDeletes.containsKey(joinTableName)) {
                        joinDeletes.put(joinTableName, new ArrayList<Long>());
                    }
                    joinDeletes.get(joinTableName).add(id);
                }
            }
        }

        String table = ReflectionUtils.tableNameForClass(rush.getClass());
        if(!deletes.containsKey(table)) {
            deletes.put(table, new ArrayList<Long>());
        }
        deletes.get(table).add(id);

    }

    private void deleteMany(Map<String, List<Long>> deletes, final Callback saveCallback) {

        for (final Map.Entry<String, List<Long>> entry : deletes.entrySet()) {
            final StringBuilder columnsString = new StringBuilder();
            final List<Long> values = entry.getValue();

            doLoop(values.size(), GROUP_SIZE, new LoopCallBack() {
                @Override
                public void start() {
                    columnsString.delete(0, columnsString.length());
                }

                @Override
                public void actionAtIndex(int index) {
                    columnsString.append("id=")
                            .append(values.get(index));
                }

                @Override
                public void join() {
                    columnsString.append(" OR ");
                }

                @Override
                public void doAction() {
                    String sql = String.format(MULTIPLE_DELETE_TEMPLATE, entry.getKey(),
                            columnsString.toString());
                    saveCallback.deleteStatement(sql);
                }
            });
        }
    }
}
