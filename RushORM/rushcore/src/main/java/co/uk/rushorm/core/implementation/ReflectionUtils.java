package co.uk.rushorm.core.implementation;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import co.uk.rushorm.core.RushSaveStatementGenerator;
import co.uk.rushorm.core.RushStatementGeneratorCallback;


/**
 * Created by Stuart on 11/12/14.
 */
public class ReflectionUtils {

    public static final String RUSH_TABLE_PREFIX = "rush_";

    public static final String RUSH_ID = "rush_id";
    public static final String RUSH_CREATED = "rush_created";
    public static final String RUSH_UPDATED = "rush_updated";
    public static final String RUSH_VERSION = "rush_version";

    public static final int GROUP_SIZE = 250;

    private static final String MULTIPLE_DELETE_JOIN_TEMPLATE = "DELETE FROM %s \n" +
            "WHERE %s;";

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

    public interface LoopCallBack {
        public void start();
        public void actionAtIndex(int index);
        public void join();
        public void doAction();
    }

    public static void doLoop(int max, int interval, LoopCallBack callBack) {
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

    public static void deleteManyJoins(Map<String, List<String>> joinDeletes, final RushStatementGeneratorCallback callback) {

        for (final Map.Entry<String, List<String>> entry : joinDeletes.entrySet()) {
            final StringBuilder columnsString = new StringBuilder();

            final List<String> ids = entry.getValue();

            doLoop(ids.size(), GROUP_SIZE, new LoopCallBack() {
                @Override
                public void start() {
                    columnsString.delete(0, columnsString.length());
                }

                @Override
                public void actionAtIndex(int index) {
                    columnsString.append("parent='")
                            .append(ids.get(index))
                            .append("'");
                }

                @Override
                public void join() {
                    columnsString.append(" OR ");
                }

                @Override
                public void doAction() {
                    String sql = String.format(MULTIPLE_DELETE_JOIN_TEMPLATE, entry.getKey(),
                            columnsString.toString());

                    callback.deleteStatement(sql);
                }
            });
        }
    }
}
