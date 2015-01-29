package co.uk.rushorm.core.implementation;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    private static final String INSERT_BLANK_TEMPLATE = "INSERT INTO %s " +
            "DEFAULT VALUES";

    private static final String INSERT_TEMPLATE = "INSERT INTO %s " +
            "(%s)\n" +
            "VALUES (%s);";

    private static final String UPDATE_TEMPLATE = "UPDATE %s \n" +
            "SET %s\n" +
            "WHERE id=%d;";

    private static final String JOIN_TEMPLATE = "INSERT INTO %s_%s_%s \n" +
            "(parent, child)\n" +
            "VALUES (%d, %d);";

    private static final String DELETE_JOIN_TEMPLATE = "DELETE FROM %s \n" +
            "WHERE parent=%d;";

    private static final String DELETE_TEMPLATE = "DELETE FROM %s \n" +
            "WHERE id=%d;";
/*
    private class Join {
        private final Rush parent;
        private final Rush child;
        private final String name;

        private Join(Rush parent, Rush child, String name) {
            this.parent = parent;
            this.child = child;
            this.name = name;
        }
    }*/

    private final RushStringSanitizer rushStringSanitizer;
    private final RushColumns rushColumns;

    public ReflectionStatementGenerator(RushStringSanitizer rushStringSanitizer, RushColumns rushColumns) {
        this.rushStringSanitizer = rushStringSanitizer;
        this.rushColumns = rushColumns;
    }

    private class BasicJoin {
        private final Rush parent;
        private final Rush child;

        private BasicJoin(Rush parent, Rush child) {
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

    @Override
    public void generateSaveOrUpdate(Rush rush, SaveCallback saveCallback) {

        List<Rush> rushObjects = new ArrayList<>();

        Map<Class, List<BasicCreate>> createValues = new HashMap<>();
        Map<Class, List<BasicUpdate>> updateValues = new HashMap<>();
        Map<Class, List<String>> columns = new HashMap<>();

        Map<String, List<Long>> joinDeletes = new HashMap<>();
        Map<String, List<BasicJoin>> joinValues = new HashMap<>();

        generateSaveOrUpdate(rush, saveCallback, rushObjects, createValues, updateValues, columns, joinDeletes, joinValues);


        deleteManyJoins(joinDeletes, saveCallback);
        createObjects(createValues, columns, saveCallback);
        updateObjects(updateValues, columns, saveCallback);
        createManyJoins(joinValues, saveCallback);

/*
        for (Join join : joins) {
            saveCallback.joinStatementCreated(statementForJoin(join));
        }*/
    }

    private void generateSaveOrUpdate(Rush rush, SaveCallback saveCallback,
                                      List<Rush> rushObjects,
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
        for (Field field : fields) {
            if (!field.isAnnotationPresent(RushIgnore.class)) {
                field.setAccessible(true);
                String joinTableName = joinFromField(joinValuesMap, rush, field);
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
                        //saveCallback.deleteJoinStatementCreated(String.format(DELETE_JOIN_TEMPLATE, joinTableName, rush.getId()));
                        if (!joinDeletesMap.containsKey(joinTableName)) {
                            joinDeletesMap.put(joinTableName, new ArrayList<Long>());
                        }
                        joinDeletesMap.get(joinTableName).add(rush.getId());
                    }
                    for (Map.Entry<String, List<BasicJoin>> entry : joinValuesMap.entrySet())
                    {
                        for (BasicJoin join : entry.getValue()) {
                            // Recourse through all children and save
                            generateSaveOrUpdate(join.child, saveCallback, rushObjects, createValuesMap, updateValuesMap, columnsMap, joinDeletesMap, joinValuesMap);
                        }
                    }

                }
            }
        }

        if (!columnsMap.containsKey(rush.getClass())) {
            columnsMap.put(rush.getClass(), columns);
        }

        // Save self
        if (rush.getId() < 0) {
            if (!createValuesMap.containsKey(rush.getClass())) {
                createValuesMap.put(rush.getClass(), new ArrayList<BasicCreate>());
            }
            createValuesMap.get(rush.getClass()).add(new BasicCreate(values, rush));
            //saveCallback.statementCreatedForRush(createStatement(rush, columns, values), rush);
        } else if (columns.size() > 0) {
            if (!updateValuesMap.containsKey(rush.getClass())) {
                updateValuesMap.put(rush.getClass(), new ArrayList<BasicUpdate>());
            }
            updateValuesMap.get(rush.getClass()).add(new BasicUpdate(values, rush.getId()));
            //saveCallback.statementCreatedForRush(updateStatement(rush, columns, values), rush);
        }
    }
/*
    private String createStatement(Rush rush, List<String> columns, List<String> values) {
        if (columns.size() < 1) {
            return String.format(INSERT_BLANK_TEMPLATE, ReflectionUtils.tableNameForClass(rush.getClass()));
        }
        StringBuilder columnString = new StringBuilder();
        StringBuilder valueString = new StringBuilder();
        for (int i = 0; i < columns.size(); i++) {
            columnString.append(columns.get(i));
            valueString.append(values.get(i));
            if (i < columns.size() - 1) {
                columnString.append(",");
                valueString.append(",");
            }
        }
        return String.format(INSERT_TEMPLATE, ReflectionUtils.tableNameForClass(rush.getClass()), columnString.toString(), valueString.toString());
    }

    private String updateStatement(Rush rush, List<String> columns, List<String> values) {
        StringBuilder string = new StringBuilder();
        for (int i = 0; i < columns.size(); i++) {
            string.append(columns.get(i))
                    .append("=")
                    .append(values.get(i));
            if (i < columns.size() - 1) {
                string.append(",");
            }
        }
        return String.format(UPDATE_TEMPLATE, ReflectionUtils.tableNameForClass(rush.getClass()), string.toString(), rush.getId());
    }

    private String statementForJoin(Join join) {
        return String.format(JOIN_TEMPLATE, ReflectionUtils.tableNameForClass(join.parent.getClass()), ReflectionUtils.tableNameForClass(join.child.getClass()), join.name, join.parent.getId(), join.child.getId());
    }
*/
    private String joinFromField(Map<String, List<BasicJoin>> joins, Rush rush, Field field) {

        if (Rush.class.isAssignableFrom(field.getType())) {
            String tableName = ReflectionUtils.joinTableNameForClass(rush.getClass(), field.getType(), field);
            try {
                Rush child = (Rush) field.get(rush);
                if (child != null) {
                    addJoin(joins, tableName, new BasicJoin(rush, child));
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            return tableName;
        } else if (field.isAnnotationPresent(RushList.class)) {

            // One to many join table
            RushList rushList = field.getAnnotation(RushList.class);
            Class listClass;
            try {
                listClass = Class.forName(rushList.classname());
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                throw new RushListAnnotationDoesNotMatchClassException();
            }

            String tableName = ReflectionUtils.joinTableNameForClass(rush.getClass(), listClass, field);
            if (Rush.class.isAssignableFrom(listClass)) {
                try {
                    List<Rush> children = (List<Rush>) field.get(rush);
                    if (children != null) {
                        for (Rush child : children) {
                            addJoin(joins, tableName, new BasicJoin(rush, child));
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

    private void addJoin(Map<String, List<BasicJoin>> joins, String table, BasicJoin basicJoin) {
        if(!joins.containsKey(table)) {
            joins.put(table, new ArrayList<BasicJoin>());
        }
        joins.get(table).add(basicJoin);
    }

    @Override
    public void generateDelete(Rush rush, DeleteCallback deleteCallback) {

        if (rush.getId() < 0) {
            return;
        }

        List<Field> fields = new ArrayList<>();
        List<Rush> children = new ArrayList<>();

        ReflectionUtils.getAllFields(fields, rush.getClass());
        for (Field field : fields) {
            field.setAccessible(true);
            if (!field.isAnnotationPresent(RushIgnore.class)) {
                if (Rush.class.isAssignableFrom(field.getType())) {
                    try {
                        Rush child = (Rush) field.get(rush);
                        if (child != null) {
                            deleteCallback.deleteJoinStatementCreated(deleteJoin(rush, child, field));
                            if (!field.isAnnotationPresent(RushDisableAutodelete.class)) {
                                children.add(child);
                            }
                        }
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                } else if (field.isAnnotationPresent(RushList.class)) {
                    try {

                        List<Rush> fieldChildren = (List<Rush>) field.get(rush);
                        if (fieldChildren != null && fieldChildren.size() > 0) {
                            Rush child = fieldChildren.get(0);
                            deleteCallback.deleteJoinStatementCreated(deleteJoin(rush, child, field));
                            if (!field.isAnnotationPresent(RushDisableAutodelete.class)) {
                                children.addAll(fieldChildren);
                            }
                        }
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        deleteCallback.statementCreatedForRush(deleteStatement(rush), rush);
        for (Rush child : children) {
            deleteCallback.deleteChild(child);
        }
    }

    private String deleteJoin(Rush parent, Rush child, Field field) {
        return String.format(DELETE_JOIN_TEMPLATE, ReflectionUtils.joinTableNameForClass(parent.getClass(), child.getClass(), field), parent.getId());
    }

    private String deleteStatement(Rush rush) {
        return String.format(DELETE_TEMPLATE, ReflectionUtils.tableNameForClass(rush.getClass()), rush.getId());
    }


    /**
     * **** Test *******
     */
    private static final String MULTIPLE_INSERT_TEMPLATE = "INSERT INTO %s " +
            "(%s)\n" +
            "VALUES %s;";

    private static final String MULTIPLE_UPDATE_TEMPLATE = "UPDATE %s " +
            "%s;";

    private void createObjects(Map<Class, List<BasicCreate>> valuesMap, Map<Class, List<String>> columnsMap, SaveCallback saveCallback) {

        for (Map.Entry<Class, List<BasicCreate>> entry : valuesMap.entrySet()) {
            columnsMap.get(entry.getKey()).add(0, "id");
            long lastId = saveCallback.lastTableId(ReflectionUtils.tableNameForClass(entry.getKey()));

            StringBuilder columnsString = new StringBuilder();
            List<BasicCreate> creates = entry.getValue();
            for (int i = 0; i < creates.size(); i++) {
                lastId++;

                saveCallback.addRush(creates.get(i).rush, lastId);
                creates.get(i).values.add(0, Long.toString(lastId));
                columnsString.append("(")
                        .append(commaSeparated(creates.get(i).values))
                        .append(")");

                if(i % 500 == 0) {
                    create(columnsString.toString(), entry.getKey(), columnsMap.get(entry.getKey()), saveCallback);
                    columnsString = new StringBuilder();
                }else if(i < creates.size() - 1) {
                    columnsString.append(", ");
                }
            }
            if(columnsString.length() > 0) {
                create(columnsString.toString(), entry.getKey(), columnsMap.get(entry.getKey()), saveCallback);
            }
        }
    }

    private void create(String middleBit, Class clazz, List<String> columns, SaveCallback saveCallback) {
        String sql = String.format(MULTIPLE_INSERT_TEMPLATE,
                ReflectionUtils.tableNameForClass(clazz),
                commaSeparated(columns),
                middleBit);

        saveCallback.statementCreatedForRush(sql);
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

    private void updateObjects(Map<Class, List<BasicUpdate>> valuesMap, Map<Class, List<String>> columnsMap, SaveCallback saveCallback) {

        for (Map.Entry<Class, List<BasicUpdate>> entry : valuesMap.entrySet()) {
            StringBuilder columnsString = new StringBuilder();

            List<BasicUpdate> values = entry.getValue();
            for (int i = 0; i < values.size(); i ++) {
                columnsString.append("\nSet ")
                        .append(updateSection(columnsMap.get(entry.getKey()), values.get(i).values))
                        .append(" Where id=")
                        .append(values.get(i).id);
                if(i % 500 == 0) {
                    update(ReflectionUtils.tableNameForClass(entry.getKey()), columnsString.toString(), saveCallback);
                    columnsString = new StringBuilder();
                } else if(i < values.size() - 1) {
                    columnsString.append(", ");
                }
            }
            if(columnsString.length() > 0) {
                update(ReflectionUtils.tableNameForClass(entry.getKey()), columnsString.toString(), saveCallback);
            }
        }
    }

    private void update(String table, String columns, SaveCallback saveCallback) {
        String sql = String.format(MULTIPLE_UPDATE_TEMPLATE,table,columns);
        saveCallback.statementCreatedForRush(sql);
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

    private static final String MULTIPLE_DELETE_JOIN_TEMPLATE = "DELETE FROM %s \n" +
            "WHERE %s;";

    private void deleteManyJoins(Map<String, List<Long>> joinDeletes, SaveCallback saveCallback) {

        for (Map.Entry<String, List<Long>> entry : joinDeletes.entrySet()) {
            StringBuilder columnsString = new StringBuilder();

            List<Long> ids = entry.getValue();
            for (int i = 0; i < ids.size(); i ++) {
                columnsString.append("parent=")
                        .append(ids.get(i));

                if(i < ids.size() - 1) {
                     columnsString.append(" OR ");
                 }
            }

            String sql = String.format(MULTIPLE_DELETE_JOIN_TEMPLATE, entry.getKey(),
                    columnsString.toString());

            saveCallback.deleteJoinStatementCreated(sql);

        }
    }

    private static final String MULTIPLE_INSERT_JOIN_TEMPLATE = "INSERT INTO %s " +
            "(parent, child)\n" +
            "VALUES %s;";

    private void createManyJoins(Map<String, List<BasicJoin>> joinValues, SaveCallback saveCallback) {

        for (Map.Entry<String, List<BasicJoin>> entry : joinValues.entrySet()) {
            StringBuilder columnsString = new StringBuilder();
            List<BasicJoin> values = entry.getValue();
            for (int i = 0; i < values.size(); i ++) {
                columnsString.append("(")
                        .append(values.get(i).parent.getId())
                        .append(",")
                        .append(values.get(i).child.getId())
                        .append(")");

                if(i % 500 == 0) {
                    createJoins(entry.getKey(), columnsString.toString(), saveCallback);
                    columnsString = new StringBuilder();
                } else if(i < values.size() - 1) {
                    columnsString.append(", ");
                }
            }

            if(columnsString.length() > 0) {
                createJoins(entry.getKey(), columnsString.toString(), saveCallback);
            }
        }
    }

    private void createJoins(String table, String columns, SaveCallback saveCallback) {
        String sql = String.format(MULTIPLE_INSERT_JOIN_TEMPLATE, table,
                columns);

        saveCallback.statementCreatedForRush(sql);
    }

}
