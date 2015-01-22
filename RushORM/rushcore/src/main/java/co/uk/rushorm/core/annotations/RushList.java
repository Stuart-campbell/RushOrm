package co.uk.rushorm.core.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by stuartc on 11/12/14.
 */

@Retention(RetentionPolicy.RUNTIME)
public @interface RushList {
    public String classname();
}
