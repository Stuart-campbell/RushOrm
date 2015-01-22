package co.uk.rushorm.core.implementation;

import java.lang.reflect.Field;

import co.uk.rushorm.core.RushTable;
import co.uk.rushorm.core.RushColumn;
import co.uk.rushorm.core.RushStringSanitizer;

/**
 * Created by Stuart on 06/01/15.
 */
public class RushColumnDouble implements RushColumn {
    @Override
    public String sqlColumnType() {
        return "double";
    }

    @Override
    public String valueFormField(RushTable rushTable, Field field, RushStringSanitizer stringSanitizer) throws IllegalAccessException {
        return Double.toString(field.getDouble(rushTable));
    }

    @Override
    public <T> void setField(T rush, Field field, String value) throws IllegalAccessException {
        field.setDouble(rush, Double.parseDouble(value));
    }

    @Override
    public Class[] classesColumnSupports() {
        return new Class[]{Double.class, double.class};
    }
}
