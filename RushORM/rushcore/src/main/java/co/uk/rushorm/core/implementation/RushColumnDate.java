package co.uk.rushorm.core.implementation;

import java.lang.reflect.Field;
import java.util.Date;

import co.uk.rushorm.core.RushTable;
import co.uk.rushorm.core.RushColumn;
import co.uk.rushorm.core.RushStringSanitizer;

/**
 * Created by Stuart on 06/01/15.
 */
public class RushColumnDate implements RushColumn {
    @Override
    public String sqlColumnType() {
        return "long";
    }

    @Override
    public String valueFormField(RushTable rushTable, Field field, RushStringSanitizer stringSanitizer) throws IllegalAccessException {
        return Long.toString(((Date)field.get(rushTable)).getTime());
    }

    @Override
    public <T> void setField(T rush, Field field, String value) throws IllegalAccessException {
        Date date = new Date(Long.parseLong(value));
        field.set(rush, date);
    }

    @Override
    public Class[] classesColumnSupports() {
        return new Class[]{Date.class};
    }
}
