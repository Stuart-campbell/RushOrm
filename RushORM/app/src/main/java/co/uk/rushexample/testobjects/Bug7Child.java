package co.uk.rushexample.testobjects;

import co.uk.rushorm.core.RushObject;
import co.uk.rushorm.core.annotations.RushTableAnnotation;

/**
 * Created by stuartc on 19/02/15.
 */
@RushTableAnnotation
public class Bug7Child extends RushObject {

    private String string;

    public Bug7Child(String string) {
        this.string = string;
    }

    public Bug7Child() {
    }

    public String getString() {
        return string;
    }

    public void setString(String string) {
        this.string = string;
    }
}
