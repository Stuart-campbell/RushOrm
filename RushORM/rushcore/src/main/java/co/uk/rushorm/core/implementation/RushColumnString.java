package co.uk.rushorm.core.implementation;

import co.uk.rushorm.core.RushColumn;
import co.uk.rushorm.core.RushStringSanitizer;

/**
 * Created by Stuart on 06/01/15.
 */
public class RushColumnString implements RushColumn<String> {
    @Override
    public String sqlColumnType() {
        return "varchar(255)";
    }

    @Override
    public String serialize(String object, RushStringSanitizer stringSanitizer) {
        return stringSanitizer.sanitize(object);
    }

    @Override
    public String deserialize(String value) {
        return value;
    }

    @Override
    public Class[] classesColumnSupports() {
        return new Class[]{String.class};
    }
}
