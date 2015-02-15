package co.uk.rushorm.core.implementation;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;


/**
 * Created by Stuart on 11/12/14.
 */
public class ReflectionUtils {

    public static final String RUSH_TABLE_PREFIX = "rush_";

    public static final String RUSH_ID = "id";

    public static String tableNameForClass(Class clazz) {
        return tableNameForClass(clazz.getName());
    }

    public static String tableNameForClass(String name) {
        return RUSH_TABLE_PREFIX + name.replace(".", "_").replace("$", "_");
    }

    public static String joinTableNameForClass(Class parent, Class child, Field field) {
        return joinTableNameForClass(parent, child, field.getName());
    }

    public static String joinTableNameForClass(Class parent, Class child, String field) {
        return joinTableNameForClass(tableNameForClass(parent), tableNameForClass(child), field);
    }

    public static String joinTableNameForClass(String parentName, String childName, String fieldName) {
        return parentName + "_" + childName + "_" + fieldName;
    }

    protected static void getAllFields(List<Field> fields, Class<?> type) {
        if (type.getSuperclass() != null) {
            fields.addAll(Arrays.asList(type.getDeclaredFields()));
            getAllFields(fields, type.getSuperclass());
        }
    }
}
