package co.uk.rushorm.core.implementation;

import java.lang.reflect.Field;

import co.uk.rushorm.core.RushTable;
import co.uk.rushorm.core.RushColumn;
import co.uk.rushorm.core.RushStringSanitizer;

/**
 * Created by Stuart on 06/01/15.
 */
public class RushColumnBoolean implements RushColumn {
    @Override
    public String sqlColumnType() {
        return "boolean";
    }

    @Override
    public String valueFormField(RushTable rushTable, Field field, RushStringSanitizer stringSanitizer) throws IllegalAccessException {
        return "'" + Boolean.toString(field.getBoolean(rushTable)) + "'";
    }

    @Override
    public <T> void setField(T rush, Field field, String value) throws IllegalAccessException {
        field.setBoolean(rush, Boolean.parseBoolean(value));
    }

    @Override
    public Class[] classesColumnSupports() {
        return new Class[]{Boolean.class, boolean.class};
    }
}
