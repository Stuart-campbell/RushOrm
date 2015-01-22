package co.uk.rushorm.core.implementation;

import java.lang.reflect.Field;

import co.uk.rushorm.core.RushTable;
import co.uk.rushorm.core.RushColumn;
import co.uk.rushorm.core.RushStringSanitizer;

/**
 * Created by Stuart on 06/01/15.
 */
public class RushColumnInt implements RushColumn {
    @Override
    public String sqlColumnType() {
        return "integer";
    }

    @Override
    public String valueFormField(RushTable rushTable, Field field, RushStringSanitizer stringSanitizer) throws IllegalAccessException {
        return Integer.toString(field.getInt(rushTable));
    }

    @Override
    public <T> void setField(T rush, Field field, String value) throws IllegalAccessException {
        field.setInt(rush, Integer.parseInt(value));
    }

    @Override
    public Class[] classesColumnSupports() {
        return new Class[]{Integer.class, int.class};
    }
}
