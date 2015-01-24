package co.uk.rushorm.core.implementation;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import co.uk.rushorm.core.Rush;
import co.uk.rushorm.core.RushColumn;
import co.uk.rushorm.core.RushColumns;
import co.uk.rushorm.core.RushStringSanitizer;

/**
 * Created by Stuart on 06/01/15.
 */
public class RushColumnsImplementation implements RushColumns {

    private Map<Class, RushColumn> columnMap = new HashMap<>();

    public RushColumnsImplementation(List<RushColumn> columns) {
        for (RushColumn rushColumn : columns) {
            for(Class clazz : rushColumn.classesColumnSupports()) {
                columnMap.put(clazz, rushColumn);
            }
        }
    }

    @Override
    public boolean supportsField(Field field) {
        return columnMap.containsKey(field.getType());
    }

    @Override
    public String sqlColumnType(Field field) {
        return columnMap.get(field.getType()).sqlColumnType();
    }

    @Override
    public String valueFromField(Rush rush, Field field, RushStringSanitizer stringSanitizer) throws IllegalAccessException {
        Object value = field.get(rush);
        String string = null;
        if(value != null) {
            string = columnMap.get(field.getType()).serialize(value, stringSanitizer);
        }
        return string;
    }

    @Override
    public <T> void setField(T rush, Field field, String value) throws IllegalAccessException {
        field.set(rush, columnMap.get(field.getType()).deserialize(value));
    }
}
