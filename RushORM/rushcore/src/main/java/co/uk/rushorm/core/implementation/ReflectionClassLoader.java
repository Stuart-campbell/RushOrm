package co.uk.rushorm.core.implementation;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import co.uk.rushorm.core.RushTable;
import co.uk.rushorm.core.RushClassLoader;
import co.uk.rushorm.core.RushColumns;
import co.uk.rushorm.core.RushStatementRunner;
import co.uk.rushorm.core.annotations.RushIgnore;
import co.uk.rushorm.core.annotations.RushList;

/**
 * Created by Stuart on 14/12/14.
 */
public class ReflectionClassLoader implements RushClassLoader {

    private final RushColumns rushColumns;

    public ReflectionClassLoader(RushColumns rushColumns) {
        this.rushColumns = rushColumns;
    }

    @Override
    public <T> List<T> loadClasses(Class<T> clazz, RushStatementRunner.ValuesCallback valuesCallback, LoadCallback callback) {
        try {

            List<T> results = new ArrayList<>();
            while(valuesCallback.hasNext()) {
                List<String> valuesList = valuesCallback.next();
                T object = loadClass(clazz, valuesList, callback);
                results.add(object);
            }
            valuesCallback.close();

            return results;
        } catch (InstantiationException | ClassNotFoundException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    private <T> T loadClass(Class<T> clazz, List<String> values, LoadCallback callback) throws IllegalAccessException, InstantiationException, ClassNotFoundException {

        long id = Long.parseLong(values.get(0));
        T object = clazz.newInstance();
        callback.didLoadObject((RushTable) object, id);

        List<Field> fields = new ArrayList<>();
        ReflectionUtils.getAllFields(fields, clazz);
        int counter = 1;
        for (Field field : fields) {

            if (!field.isAnnotationPresent(RushIgnore.class)) {
                if (!hasJoin(field)) {
                    if(rushColumns.supportsField(field)) {
                        String value = values.get(counter);
                        if(value != null && !value.equals("null")) {
                            field.setAccessible(true);
                            rushColumns.setField(object, field, value);
                            field.setAccessible(false);
                        }
                    }
                    counter++;
                }else{
                    loadJoinField((RushTable) object, field, callback);
                }
            }
        }
        return object;
    }

    private void loadJoinField(RushTable object, Field field, LoadCallback callback) throws IllegalAccessException, InstantiationException, ClassNotFoundException {
        String sql = SearchUtils.findChildren(object, field);
        RushStatementRunner.ValuesCallback valuesCallback = callback.runStatement(sql);
        if (valuesCallback.hasNext()) {
            if (RushTable.class.isAssignableFrom(field.getType())) {
                field.set(object, loadClass(field.getType(), valuesCallback.next(), callback));
                valuesCallback.close();
            } else {
                RushList rushList = field.getAnnotation(RushList.class);
                Class listClass;
                listClass = Class.forName(rushList.classname());
                field.set(object, loadClasses(listClass, valuesCallback, callback));
            }
        }
    }

    private boolean hasJoin(Field field) {
        return RushTable.class.isAssignableFrom(field.getType()) || field.isAnnotationPresent(RushList.class);
    }
}
