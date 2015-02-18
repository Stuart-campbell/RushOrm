package co.uk.rushorm.core.implementation;

import co.uk.rushorm.core.RushColumn;
import co.uk.rushorm.core.RushStringSanitizer;

/**
 * Created by stuartc on 18/02/15.
 */
public class RushColumnFloat implements RushColumn<Float> {
    @Override
    public String sqlColumnType() {
        return "float";
    }

    @Override
    public String serialize(Float object, RushStringSanitizer stringSanitizer) {
        return Float.toString(object);
    }

    @Override
    public Float deserialize(String value) {
        return Float.parseFloat(value);
    }

    @Override
    public Class[] classesColumnSupports() {
        return new Class[]{Float.class, float.class};
    }
}
