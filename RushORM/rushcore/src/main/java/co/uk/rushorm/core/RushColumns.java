package co.uk.rushorm.core;

import java.lang.reflect.Field;

/**
 * Created by Stuart on 06/01/15.
 */
public interface RushColumns {

    public boolean supportsField(Field field);
    public String sqlColumnType(Field field);
    public String valueFromField(Rush rush, Field field, RushStringSanitizer stringSanitizer) throws IllegalAccessException;
    public <T> void setField(T rush, Field field, String value) throws IllegalAccessException;

}
