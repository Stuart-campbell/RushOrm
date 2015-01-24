package co.uk.rushorm.core.implementation;

import co.uk.rushorm.core.RushColumn;
import co.uk.rushorm.core.RushStringSanitizer;

/**
 * Created by Stuart on 06/01/15.
 */
public class RushColumnDouble implements RushColumn<Double> {
    @Override
    public String sqlColumnType() {
        return "double";
    }

    @Override
    public String serialize(Double object, RushStringSanitizer stringSanitizer) {
        return Double.toString(object);
    }

    @Override
    public Double deserialize(String value) {
        return Double.parseDouble(value);
    }
/*
    @Override
    public String valueFromField(RushTable rushTable, Field field, RushStringSanitizer stringSanitizer) throws IllegalAccessException {
        return Double.toString(field.getDouble(rushTable));
    }

    @Override
    public <T> void setField(T rush, Field field, String value) throws IllegalAccessException {
        field.setDouble(rush, Double.parseDouble(value));
    }
*/
    @Override
    public Class[] classesColumnSupports() {
        return new Class[]{Double.class, double.class};
    }
}
