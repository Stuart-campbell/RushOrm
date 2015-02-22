package co.uk.rushorm.core.implementation;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import co.uk.rushorm.core.AnnotationCache;
import co.uk.rushorm.core.Rush;
import co.uk.rushorm.core.RushColumns;
import co.uk.rushorm.core.RushDeleteStatementGenerator;
import co.uk.rushorm.core.RushSaveStatementGenerator;
import co.uk.rushorm.core.RushStringSanitizer;

/**
 * Created by Stuart on 16/02/15.
 */
public class ReflectionDeleteStatementGenerator implements RushDeleteStatementGenerator {

    private static final String MULTIPLE_DELETE_TEMPLATE = "DELETE FROM %s \n" +
            "WHERE %s;";

    @Override
    public void generateDelete(List<? extends Rush> objects, Map<Class, AnnotationCache> annotationCache, RushDeleteStatementGenerator.Callback callback) {
        Map<String, List<String>> joinDeletes = new HashMap<>();
        Map<String, List<String>> deletes = new HashMap<>();

        for (Rush object : objects) {
            generateDelete(object, annotationCache, deletes, joinDeletes, callback);
        }

        ReflectionUtils.deleteManyJoins(joinDeletes, callback);
        deleteMany(deletes, callback);
    }

    public void generateDelete(Rush rush, Map<Class, AnnotationCache> annotationCache, Map<String, List<String>> deletes, Map<String, List<String>> joinDeletes, RushDeleteStatementGenerator.Callback callback) {

        if (rush.getId() == null) {
            return;
        }

        String id = rush.getId();
        callback.removeRush(rush);

        List<Field> fields = new ArrayList<>();
        ReflectionUtils.getAllFields(fields, rush.getClass());

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
                                generateDelete(child, annotationCache, deletes, joinDeletes, callback);
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
                                    generateDelete(child, annotationCache, deletes, joinDeletes, callback);
                                }
                            }
                        }
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
                if(joinTableName != null) {
                    if (!joinDeletes.containsKey(joinTableName)) {
                        joinDeletes.put(joinTableName, new ArrayList<String>());
                    }
                    joinDeletes.get(joinTableName).add(id);
                }
            }
        }

        String table = ReflectionUtils.tableNameForClass(rush.getClass());
        if(!deletes.containsKey(table)) {
            deletes.put(table, new ArrayList<String>());
        }
        deletes.get(table).add(id);

    }

    private void deleteMany(Map<String, List<String>> deletes, final RushDeleteStatementGenerator.Callback saveCallback) {

        for (final Map.Entry<String, List<String>> entry : deletes.entrySet()) {
            final StringBuilder columnsString = new StringBuilder();
            final List<String> values = entry.getValue();

            ReflectionUtils.doLoop(values.size(), ReflectionUtils.GROUP_SIZE, new ReflectionUtils.LoopCallBack() {
                @Override
                public void start() {
                    columnsString.delete(0, columnsString.length());
                }

                @Override
                public void actionAtIndex(int index) {
                    columnsString.append(ReflectionUtils.RUSH_ID)
                            .append("='")
                            .append(values.get(index))
                            .append("'");
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
