package co.uk.rushorm.core.implementation;

import co.uk.rushorm.core.RushColumn;
import co.uk.rushorm.core.RushStringSanitizer;

/**
 * Created by Stuart on 06/01/15.
 */
public class RushColumnInt implements RushColumn<Integer> {
    @Override
    public String sqlColumnType() {
        return "integer";
    }

    @Override
    public String serialize(Integer object, RushStringSanitizer stringSanitizer) {
        return Integer.toString(object);
    }

    @Override
    public Integer deserialize(String value) {
        return Integer.parseInt(value);
    }

    @Override
    public Class[] classesColumnSupports() {
        return new Class[]{Integer.class, int.class};
    }
}
