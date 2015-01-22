package co.uk.rushorm.core.implementation;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import co.uk.rushorm.core.RushTable;
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

    private class Join {
        private final RushTable parent;
        private final RushTable child;
        private final String name;
        private Join(RushTable parent, RushTable child,String name){
            this.parent = parent;
            this.child = child;
            this.name = name;
        }
    }

    private final RushStringSanitizer rushStringSanitizer;
    private final RushColumns rushColumns;

    public ReflectionStatementGenerator(RushStringSanitizer rushStringSanitizer, RushColumns rushColumns) {
        this.rushStringSanitizer = rushStringSanitizer;
        this.rushColumns = rushColumns;
    }

    @Override
    public void generateSaveOrUpdate(RushTable rushTable, SaveCallback saveCallback) {

        List<Join> joins = new ArrayList<>();
        List<RushTable> rushTableObjects = new ArrayList<>();

        generateSaveOrUpdate(rushTable, saveCallback, rushTableObjects, joins);

        for(Join join : joins) {
            saveCallback.joinStatementCreated(statementForJoin(join));
        }
    }

    private void generateSaveOrUpdate(RushTable rushTable, SaveCallback saveCallback, List<RushTable> rushTableObjects, List<Join> joins) {

        if(rushTableObjects.contains(rushTable)) {
            // Exit if object is referenced by child
            return;
        }

        rushTableObjects.add(rushTable);

        List<String> columns = new ArrayList<>();
        List<String> values = new ArrayList<>();
        List<Field> fields = new ArrayList<>();

        ReflectionUtils.getAllFields(fields, rushTable.getClass());
        for (Field field : fields) {
            if(!field.isAnnotationPresent(RushIgnore.class)) {

                String joinTableName = joinFromField(joins, rushTable, field);
                if(joinTableName == null) {
                    if(rushColumns.supportsField(field)) {
                        try {
                            field.setAccessible(true);
                            String value = rushColumns.valueFormField(rushTable, field, rushStringSanitizer);
                            field.setAccessible(false);
                            columns.add(field.getName());
                            values.add(value);
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    }
                }else {
                    if(rushTable.getId() > 0) {
                        // Clear join tables and re save rows to catch any deleted or changed children
                        saveCallback.deleteJoinStatementCreated(String.format(DELETE_JOIN_TEMPLATE, joinTableName, rushTable.getId()));
                    }
                    for (Join join : joins) {
                        // Recourse through all children and save
                        generateSaveOrUpdate(join.child, saveCallback, rushTableObjects, joins);
                    }
                }
            }
        }

        // Save self
        if(rushTable.getId() < 0) {
            saveCallback.statementCreatedForRush(createStatement(rushTable, columns, values), rushTable);
        }else if(columns.size() > 0) {
            saveCallback.statementCreatedForRush(updateStatement(rushTable, columns, values), rushTable);
        }
    }

    private String createStatement(RushTable rushTable, List<String> columns, List<String> values) {
        if(columns.size() < 1){
            return String.format(INSERT_BLANK_TEMPLATE, ReflectionUtils.tableNameForClass(rushTable.getClass()));
        }
        StringBuilder columnString = new StringBuilder();
        StringBuilder valueString = new StringBuilder();
        for(int i = 0; i < columns.size(); i ++) {
            columnString.append(columns.get(i));
            valueString.append(values.get(i));
            if(i < columns.size() - 1) {
                columnString.append(",");
                valueString.append(",");
            }
        }
        return String.format(INSERT_TEMPLATE, ReflectionUtils.tableNameForClass(rushTable.getClass()), columnString.toString(), valueString.toString());
    }

    private String updateStatement(RushTable rushTable, List<String> columns, List<String> values) {
        StringBuilder string = new StringBuilder();
        for(int i = 0; i < columns.size(); i ++) {
            string.append(columns.get(i))
                    .append("=")
                    .append(values.get(i));
            if(i < columns.size() - 1) {
                string.append(",");
            }
        }
        return String.format(UPDATE_TEMPLATE, ReflectionUtils.tableNameForClass(rushTable.getClass()), string.toString(), rushTable.getId());
    }

    private String statementForJoin(Join join) {
        return String.format(JOIN_TEMPLATE, ReflectionUtils.tableNameForClass(join.parent.getClass()), ReflectionUtils.tableNameForClass(join.child.getClass()), join.name, join.parent.getId(), join.child.getId());
    }

    private String joinFromField(List<Join> joins, RushTable rushTable, Field field) {

        if(RushTable.class.isAssignableFrom(field.getType())){
            try {
                RushTable child = (RushTable)field.get(rushTable);
                if(child != null) {
                    Join join = new Join(rushTable, child, field.getName());
                    joins.add(join);
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            return ReflectionUtils.joinTableNameForClass(rushTable.getClass(), field.getType(), field);
        }else if(field.isAnnotationPresent(RushList.class)) {

            // One to many join table
            RushList rushList = field.getAnnotation(RushList.class);
            Class listClass;
            try {
                listClass = Class.forName(rushList.classname());
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                throw new RushListAnnotationDoesNotMatchClassException();
            }

            if (RushTable.class.isAssignableFrom(listClass)) {

                try {

                    List<RushTable> children = (List<RushTable>)field.get(rushTable);
                    if(children != null) {
                        for (RushTable child : children) {
                            Join join = new Join(rushTable, child, field.getName());
                            joins.add(join);
                        }
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }

            return ReflectionUtils.joinTableNameForClass(rushTable.getClass(), listClass, field);
        }
        return null;
    }

    @Override
    public void generateDelete(RushTable rushTable, DeleteCallback deleteCallback) {

        if(rushTable.getId() < 0) {
            return;
        }

        List<Field> fields = new ArrayList<>();
        List<RushTable> children = new ArrayList<>();

        ReflectionUtils.getAllFields(fields, rushTable.getClass());
        for (Field field : fields) {
            if(!field.isAnnotationPresent(RushIgnore.class)) {
                if(RushTable.class.isAssignableFrom(field.getType())){
                    try {
                        RushTable child = (RushTable)field.get(rushTable);
                        if(child != null) {
                            deleteCallback.deleteJoinStatementCreated(deleteJoin(rushTable, child, field));
                            if(!field.isAnnotationPresent(RushDisableAutodelete.class)) {
                                children.add(child);
                            }
                        }
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }else if(field.isAnnotationPresent(RushList.class)) {
                    try {

                        List<RushTable> fieldChildren = (List<RushTable>) field.get(rushTable);
                        if (fieldChildren != null && fieldChildren.size() > 0) {
                            RushTable child = fieldChildren.get(0);
                            deleteCallback.deleteJoinStatementCreated(deleteJoin(rushTable, child, field));
                            if(!field.isAnnotationPresent(RushDisableAutodelete.class)) {
                                children.addAll(fieldChildren);
                            }
                        }
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        deleteCallback.statementCreatedForRush(deleteStatement(rushTable), rushTable);
        for(RushTable child : children) {
            deleteCallback.deleteChild(child);
        }
    }

    private String deleteJoin(RushTable parent, RushTable child, Field field){
        return String.format(DELETE_JOIN_TEMPLATE, ReflectionUtils.joinTableNameForClass(parent.getClass(), child.getClass(), field), parent.getId());
    }

    private String deleteStatement(RushTable rushTable) {
        return String.format(DELETE_TEMPLATE, ReflectionUtils.tableNameForClass(rushTable.getClass()), rushTable.getId());
    }
}
