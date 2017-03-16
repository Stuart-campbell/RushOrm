package co.uk.rushorm.android.testobjects;

import co.uk.rushorm.core.RushObject;

/**
 * Created by stuartc on 19/02/15.
 */
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
