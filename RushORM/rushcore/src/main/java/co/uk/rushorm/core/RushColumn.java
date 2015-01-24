package co.uk.rushorm.core;

import java.lang.reflect.Field;

/**
 * Created by Stuart on 06/01/15.
 */
public interface RushColumn<T> {

    public String sqlColumnType();
    public String serialize(T object, RushStringSanitizer stringSanitizer);
    public T deserialize(String value);
    public Class[] classesColumnSupports();


}
