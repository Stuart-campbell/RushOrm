package co.uk.rushorm.core.implementation;

import java.lang.reflect.Field;

import co.uk.rushorm.core.RushTable;
import co.uk.rushorm.core.RushColumn;
import co.uk.rushorm.core.RushStringSanitizer;

/**
 * Created by Stuart on 06/01/15.
 */
public class RushColumnLong implements RushColumn {
    @Override
    public String sqlColumnType() {
        return "long";
    }

    @Override
    public String valueFormField(RushTable rushTable, Field field, RushStringSanitizer stringSanitizer) throws IllegalAccessException {
        return Long.toString(field.getLong(rushTable));
    }

    @Override
    public <T> void setField(T rush, Field field, String value) throws IllegalAccessException {
        field.setLong(rush, Long.parseLong(value));
    }

    @Override
    public Class[] classesColumnSupports() {
        return new Class[]{Long.class, long.class};
    }
}
