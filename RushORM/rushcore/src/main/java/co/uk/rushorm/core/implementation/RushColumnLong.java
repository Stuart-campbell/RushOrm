package co.uk.rushorm.core.implementation;

import co.uk.rushorm.core.RushColumn;
import co.uk.rushorm.core.RushStringSanitizer;

/**
 * Created by Stuart on 06/01/15.
 */
public class RushColumnLong implements RushColumn<Long> {
    @Override
    public String sqlColumnType() {
        return "long";
    }

    @Override
    public String serialize(Long object, RushStringSanitizer stringSanitizer) {
        return Long.toString(object);
    }

    @Override
    public Long deserialize(String value) {
        return Long.parseLong(value);
    }

    @Override
    public Class[] classesColumnSupports() {
        return new Class[]{Long.class, long.class};
    }
}
