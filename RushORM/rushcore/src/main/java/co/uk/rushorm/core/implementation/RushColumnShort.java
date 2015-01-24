package co.uk.rushorm.core.implementation;

import co.uk.rushorm.core.RushColumn;
import co.uk.rushorm.core.RushStringSanitizer;

/**
 * Created by Stuart on 06/01/15.
 */
public class RushColumnShort implements RushColumn<Short> {
    @Override
    public String sqlColumnType() {
        return "short";
    }

    @Override
    public String serialize(Short object, RushStringSanitizer stringSanitizer) {
        return Short.toString(object);
    }

    @Override
    public Short deserialize(String value) {
        return Short.parseShort(value);
    }

    @Override
    public Class[] classesColumnSupports() {
        return new Class[]{Short.class, short.class};
    }
}
