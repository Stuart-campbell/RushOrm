package co.uk.rushorm.core.implementation;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import co.uk.rushorm.core.RushTable;
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
    public String valueFormField(RushTable rushTable, Field field, RushStringSanitizer stringSanitizer) throws IllegalAccessException {
        return columnMap.get(field.getType()).valueFormField(rushTable, field, stringSanitizer);
    }

    @Override
    public <T> void setField(T rush, Field field, String value) throws IllegalAccessException {
        columnMap.get(field.getType()).setField(rush, field, value);
    }
}
