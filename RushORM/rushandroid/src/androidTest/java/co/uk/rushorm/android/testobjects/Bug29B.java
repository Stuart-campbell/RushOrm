package co.uk.rushorm.android.testobjects;

import co.uk.rushorm.core.RushObject;
import co.uk.rushorm.core.annotations.RushTableAnnotation;

/**
 * Created by Stuart on 18/07/15.
 */
@RushTableAnnotation
public class Bug29B extends RushObject {
    public String name;

    public Bug29B(String name) {
        this.name = name;
    }

    public Bug29B() {
    }
}
