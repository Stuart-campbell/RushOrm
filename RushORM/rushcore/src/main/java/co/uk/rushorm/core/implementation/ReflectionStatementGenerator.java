package co.uk.rushorm.core.implementation;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

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

    private class Join {
        private final Rush parent;
        private final Rush child;
        private final String name;
        private Join(Rush parent, Rush child,String name){
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
    public void generateSaveOrUpdate(Rush rush, SaveCallback saveCallback) {

        List<Join> joins = new ArrayList<>();
        List<Rush> rushObjects = new ArrayList<>();

        generateSaveOrUpdate(rush, saveCallback, rushObjects, joins);

        for(Join join : joins) {
            saveCallback.joinStatementCreated(statementForJoin(join));
        }
    }

    private void generateSaveOrUpdate(Rush rush, SaveCallback saveCallback, List<Rush> rushObjects, List<Join> joins) {

        if(rushObjects.contains(rush)) {
            // Exit if object is referenced by child
            return;
        }

        rushObjects.add(rush);

        List<String> columns = new ArrayList<>();
        List<String> values = new ArrayList<>();
        List<Field> fields = new ArrayList<>();

        ReflectionUtils.getAllFields(fields, rush.getClass());
        for (Field field : fields) {
            if(!field.isAnnotationPresent(RushIgnore.class)) {
                field.setAccessible(true);
                String joinTableName = joinFromField(joins, rush, field);
                if(joinTableName == null) {
                    if(rushColumns.supportsField(field)) {
                        try {
                            String value = rushColumns.valueFromField(rush, field, rushStringSanitizer);
                            columns.add(field.getName());
                            values.add(value);
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    }
                }else {
                    if(rush.getId() > 0) {
                        // Clear join tables and re save rows to catch any deleted or changed children
                        saveCallback.deleteJoinStatementCreated(String.format(DELETE_JOIN_TEMPLATE, joinTableName, rush.getId()));
                    }
                    for (Join join : joins) {
                        // Recourse through all children and save
                        generateSaveOrUpdate(join.child, saveCallback, rushObjects, joins);
                    }
                }
            }
        }

        // Save self
        if(rush.getId() < 0) {
            saveCallback.statementCreatedForRush(createStatement(rush, columns, values), rush);
        }else if(columns.size() > 0) {
            saveCallback.statementCreatedForRush(updateStatement(rush, columns, values), rush);
        }
    }

    private String createStatement(Rush rush, List<String> columns, List<String> values) {
        if(columns.size() < 1){
            return String.format(INSERT_BLANK_TEMPLATE, ReflectionUtils.tableNameForClass(rush.getClass()));
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
        return String.format(INSERT_TEMPLATE, ReflectionUtils.tableNameForClass(rush.getClass()), columnString.toString(), valueString.toString());
    }

    private String updateStatement(Rush rush, List<String> columns, List<String> values) {
        StringBuilder string = new StringBuilder();
        for(int i = 0; i < columns.size(); i ++) {
            string.append(columns.get(i))
                    .append("=")
                    .append(values.get(i));
            if(i < columns.size() - 1) {
                string.append(",");
            }
        }
        return String.format(UPDATE_TEMPLATE, ReflectionUtils.tableNameForClass(rush.getClass()), string.toString(), rush.getId());
    }

    private String statementForJoin(Join join) {
        return String.format(JOIN_TEMPLATE, ReflectionUtils.tableNameForClass(join.parent.getClass()), ReflectionUtils.tableNameForClass(join.child.getClass()), join.name, join.parent.getId(), join.child.getId());
    }

    private String joinFromField(List<Join> joins, Rush rush, Field field) {

        if(Rush.class.isAssignableFrom(field.getType())){
            try {
                Rush child = (Rush)field.get(rush);
                if(child != null) {
                    Join join = new Join(rush, child, field.getName());
                    joins.add(join);
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            return ReflectionUtils.joinTableNameForClass(rush.getClass(), field.getType(), field);
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

            if (Rush.class.isAssignableFrom(listClass)) {

                try {

                    List<Rush> children = (List<Rush>)field.get(rush);
                    if(children != null) {
                        for (Rush child : children) {
                            Join join = new Join(rush, child, field.getName());
                            joins.add(join);
                        }
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }

            return ReflectionUtils.joinTableNameForClass(rush.getClass(), listClass, field);
        }
        return null;
    }

    @Override
    public void generateDelete(Rush rush, DeleteCallback deleteCallback) {

        if(rush.getId() < 0) {
            return;
        }

        List<Field> fields = new ArrayList<>();
        List<Rush> children = new ArrayList<>();

        ReflectionUtils.getAllFields(fields, rush.getClass());
        for (Field field : fields) {
            field.setAccessible(true);
            if(!field.isAnnotationPresent(RushIgnore.class)) {
                if(Rush.class.isAssignableFrom(field.getType())){
                    try {
                        Rush child = (Rush)field.get(rush);
                        if(child != null) {
                            deleteCallback.deleteJoinStatementCreated(deleteJoin(rush, child, field));
                            if(!field.isAnnotationPresent(RushDisableAutodelete.class)) {
                                children.add(child);
                            }
                        }
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }else if(field.isAnnotationPresent(RushList.class)) {
                    try {

                        List<Rush> fieldChildren = (List<Rush>) field.get(rush);
                        if (fieldChildren != null && fieldChildren.size() > 0) {
                            Rush child = fieldChildren.get(0);
                            deleteCallback.deleteJoinStatementCreated(deleteJoin(rush, child, field));
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
        deleteCallback.statementCreatedForRush(deleteStatement(rush), rush);
        for(Rush child : children) {
            deleteCallback.deleteChild(child);
        }
    }

    private String deleteJoin(Rush parent, Rush child, Field field){
        return String.format(DELETE_JOIN_TEMPLATE, ReflectionUtils.joinTableNameForClass(parent.getClass(), child.getClass(), field), parent.getId());
    }

    private String deleteStatement(Rush rush) {
        return String.format(DELETE_TEMPLATE, ReflectionUtils.tableNameForClass(rush.getClass()), rush.getId());
    }
}
