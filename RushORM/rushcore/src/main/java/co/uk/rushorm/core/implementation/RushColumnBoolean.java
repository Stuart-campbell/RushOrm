package co.uk.rushorm.core.implementation;

import co.uk.rushorm.core.RushColumn;
import co.uk.rushorm.core.RushStringSanitizer;

/**
 * Created by Stuart on 06/01/15.
 */
public class RushColumnBoolean implements RushColumn<Boolean> {
    @Override
    public String sqlColumnType() {
        return "boolean";
    }

    @Override
    public String serialize(Boolean object, RushStringSanitizer stringSanitizer) {
        return stringSanitizer.sanitize(Boolean.toString(object));
    }

    @Override
    public Boolean deserialize(String value) {
        return Boolean.parseBoolean(value);
    }

    @Override
    public Class[] classesColumnSupports() {
        return new Class[]{Boolean.class, boolean.class};
    }
}
