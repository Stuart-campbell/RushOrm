package co.uk.rushorm.core.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by Stuart on 22/02/15.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface RushClassSerializationName {
    public String name();
}
