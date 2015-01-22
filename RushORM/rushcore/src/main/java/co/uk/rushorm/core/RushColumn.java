package co.uk.rushorm.core;

import java.lang.reflect.Field;

/**
 * Created by Stuart on 06/01/15.
 */
public interface RushColumn {

    public String sqlColumnType();
    public String valueFormField(RushTable rushTable, Field field, RushStringSanitizer stringSanitizer) throws IllegalAccessException;
    public <T> void setField(T rush, Field field, String value) throws IllegalAccessException;
    public Class[] classesColumnSupports();
}
