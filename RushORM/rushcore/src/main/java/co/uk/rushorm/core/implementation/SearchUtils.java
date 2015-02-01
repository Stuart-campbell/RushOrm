package co.uk.rushorm.core.implementation;

import java.lang.reflect.Field;

import co.uk.rushorm.core.Rush;
import co.uk.rushorm.core.annotations.RushList;
import co.uk.rushorm.core.exceptions.RushListAnnotationDoesNotMatchClassException;

/**
 * Created by Stuart on 14/12/14.
 */
public class SearchUtils {

    private static final String BASIC_WHERE_TEMPLATE = "SELECT * from %s \n" +
            "WHERE %s;";


    private static final String SELECT_CHILDREN = "SELECT %s.* from %s \n" +
            "JOIN %s ON %s.id = %s.child \n" +
            "WHERE parent=%d;";


    public static <T> String find(Class<T> rush, long id) {
        return String.format(BASIC_WHERE_TEMPLATE, ReflectionUtils.tableNameForClass(rush), "id=" + Long.toString(id));
    }

    public static String findChildren(Rush parent, Field field) {

        Class childClass;
        if(Rush.class.isAssignableFrom(field.getType())){
            childClass = field.getType();
        }else {
            // One to many join table
            RushList rushList = field.getAnnotation(RushList.class);
            try {
                childClass = Class.forName(rushList.classname());
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                throw new RushListAnnotationDoesNotMatchClassException();
            }
        }

        String table = ReflectionUtils.joinTableNameForClass(parent.getClass(), childClass, field);
        String childTable = ReflectionUtils.tableNameForClass(childClass);
        return String.format(SELECT_CHILDREN, childTable, childTable, table, childTable, table, parent.getId());
    }
}
