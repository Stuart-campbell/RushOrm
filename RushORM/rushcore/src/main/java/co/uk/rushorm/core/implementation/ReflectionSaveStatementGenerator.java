package co.uk.rushorm.core.implementation;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import co.uk.rushorm.core.AnnotationCache;
import co.uk.rushorm.core.Rush;
import co.uk.rushorm.core.RushColumns;
import co.uk.rushorm.core.RushMetaData;
import co.uk.rushorm.core.RushSaveStatementGenerator;
import co.uk.rushorm.core.RushSaveStatementGeneratorCallback;
import co.uk.rushorm.core.RushStringSanitizer;

/**
 * Created by stuartc on 11/12/14.
 */
public class ReflectionSaveStatementGenerator implements RushSaveStatementGenerator {

    private static final String MULTIPLE_INSERT_UPDATE_TEMPLATE = "INSERT OR REPLACE INTO %s " +
            "(%s)\n" +
            "VALUES %s;";

    private static final String MULTIPLE_INSERT_JOIN_TEMPLATE = "INSERT INTO %s " +
            "(parent, child)\n" +
            "VALUES %s;";

    private final Map<Class, AnnotationCache> annotationCache;

    private final RushStringSanitizer rushStringSanitizer;
    private final RushColumns rushColumns;

    public ReflectionSaveStatementGenerator(RushStringSanitizer rushStringSanitizer, RushColumns rushColumns, Map<Class, AnnotationCache> annotationCache) {
        this.rushStringSanitizer = rushStringSanitizer;
        this.rushColumns = rushColumns;
        this.annotationCache = annotationCache;
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

    protected class BasicUpdate {
        protected final List<String> values;
        protected final Rush object;
        protected final RushMetaData rushMetaData;

        private BasicUpdate(List<String> values, Rush object, RushMetaData rushMetaData) {
            this.values = values;
            this.object = object;
            this.rushMetaData = rushMetaData;
        }
    }

    private void addJoin(Map<String, List<BasicJoin>> joins, BasicJoin basicJoin) {
        if(!joins.containsKey(basicJoin.table)) {
            joins.put(basicJoin.table, new ArrayList<BasicJoin>());
        }
        joins.get(basicJoin.table).add(basicJoin);
    }

    @Override
    public void generateSaveOrUpdate(List<? extends Rush> objects, RushSaveStatementGeneratorCallback saveCallback) {

        List<Rush> rushObjects = new ArrayList<>();

        Map<Class, List<BasicUpdate>> updateValues = new HashMap<>();
        Map<Class, List<String>> columns = new HashMap<>();

        Map<String, List<String>> joinDeletes = new HashMap<>();
        Map<String, List<BasicJoin>> joinValues = new HashMap<>();

        for(Rush rush : objects) {
            generateSaveOrUpdate(rush, rushObjects, updateValues, columns, joinDeletes, joinValues, saveCallback);
        }

        ReflectionUtils.deleteManyJoins(joinDeletes, saveCallback);
        createOrUpdateObjects(updateValues, columns, saveCallback);

        createManyJoins(joinValues, saveCallback);

    }

    private void generateSaveOrUpdate(Rush rush, List<Rush> rushObjects,
                                      Map<Class, List<BasicUpdate>> updateValuesMap,
                                      Map<Class, List<String>> columnsMap,
                                      Map<String, List<String>> joinDeletesMap,
                                      Map<String, List<BasicJoin>> joinValuesMap,
                                      RushSaveStatementGeneratorCallback saveCallback) {

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
                    if (rush.getId() != null) {
                        // Clear join tables and re save rows to catch any deleted or changed children
                        if (!joinDeletesMap.containsKey(joinTableName)) {
                            joinDeletesMap.put(joinTableName, new ArrayList<String>());
                        }
                        joinDeletesMap.get(joinTableName).add(rush.getId());
                    }
                    for(BasicJoin join : joins) {
                        generateSaveOrUpdate(join.child, rushObjects, updateValuesMap, columnsMap, joinDeletesMap, joinValuesMap, saveCallback);
                        addJoin(joinValuesMap, join);
                    }
                }
            }
        }

        if (!columnsMap.containsKey(rush.getClass())) {
            columnsMap.put(rush.getClass(), columns);
        }

        if (!updateValuesMap.containsKey(rush.getClass())) {
            updateValuesMap.put(rush.getClass(), new ArrayList<BasicUpdate>());
        }

        RushMetaData rushMetaData = saveCallback.getMetaData(rush);
        if(rushMetaData == null) {
            rushMetaData = new RushMetaData();
            saveCallback.addRush(rush, rushMetaData);
        }
            updateValuesMap.get(rush.getClass()).add(new BasicUpdate(values, rush, rushMetaData));
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

    private void createManyJoins(Map<String, List<BasicJoin>> joinValues, final RushSaveStatementGeneratorCallback saveCallback) {

        for (final Map.Entry<String, List<BasicJoin>> entry : joinValues.entrySet()) {
            final StringBuilder columnsString = new StringBuilder();
            final List<BasicJoin> values = entry.getValue();

            ReflectionUtils.doLoop(values.size(), ReflectionUtils.GROUP_SIZE, new ReflectionUtils.LoopCallBack() {
                @Override
                public void start() {
                    columnsString.delete(0, columnsString.length());
                }

                @Override
                public void actionAtIndex(int index) {
                    columnsString.append("('")
                            .append(values.get(index).parent.getId())
                            .append("','")
                            .append(values.get(index).child.getId())
                            .append("')");
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

    protected void createOrUpdateObjects(Map<Class, List<BasicUpdate>> valuesMap, final Map<Class, List<String>> columnsMap, final RushSaveStatementGeneratorCallback saveCallback) {

        for (final Map.Entry<Class, List<BasicUpdate>> entry : valuesMap.entrySet()) {

            StringBuilder columnsBuilder = new StringBuilder();
            columnsBuilder.append(ReflectionUtils.RUSH_ID)
                    .append(",")
                    .append(ReflectionUtils.RUSH_CREATED)
                    .append(",")
                    .append(ReflectionUtils.RUSH_UPDATED)
                    .append(",")
                    .append(ReflectionUtils.RUSH_VERSION)
                    .append(commaSeparated(columnsMap.get(entry.getKey())));

            final String columns = columnsBuilder.toString();

            final StringBuilder valuesString = new StringBuilder();
            final List<BasicUpdate> creates = entry.getValue();

            ReflectionUtils.doLoop(creates.size(), ReflectionUtils.GROUP_SIZE, new ReflectionUtils.LoopCallBack() {
                @Override
                public void start() {
                    valuesString.delete(0, valuesString.length());
                }

                @Override
                public void actionAtIndex(int index) {

                    RushMetaData rushMetaData = creates.get(index).rushMetaData;
                    rushMetaData.save();

                    valuesString.append("('")
                            .append(rushMetaData.getId())
                            .append("',")
                            .append(rushMetaData.getCreated())
                            .append(",")
                            .append(rushMetaData.getUpdated())
                            .append(",")
                            .append(rushMetaData.getVersion())
                            .append(commaSeparated(creates.get(index).values))
                            .append(")");
                }

                @Override
                public void join() {
                    valuesString.append(", ");
                }

                @Override
                public void doAction() {
                    String sql = String.format(MULTIPLE_INSERT_UPDATE_TEMPLATE,
                            ReflectionUtils.tableNameForClass(entry.getKey()),
                            columns,
                            valuesString.toString());

                    saveCallback.createdOrUpdateStatement(sql);
                }
            });
        }
    }

    private String commaSeparated(List<String> values) {
        StringBuilder string = new StringBuilder();
        for (int i = 0; i < values.size(); i++) {
            string.append(",")
                    .append(values.get(i));
        }
        return string.toString();
    }
}
