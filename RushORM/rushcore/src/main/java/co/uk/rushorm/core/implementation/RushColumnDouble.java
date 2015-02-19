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

    @Override
    public Class[] classesColumnSupports() {
        return new Class[]{Double.class, double.class};
    }
}
