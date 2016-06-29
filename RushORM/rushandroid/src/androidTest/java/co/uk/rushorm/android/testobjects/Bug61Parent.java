package co.uk.rushorm.android.testobjects;

import co.uk.rushorm.core.RushObject;
import co.uk.rushorm.core.annotations.RushTableAnnotation;

/**
 * Created by Stuart on 02/08/15.
 */
@RushTableAnnotation
public class Bug61Parent extends RushObject {
    private long serverID;
    private String name;

    private Bug61Child lBug61Child;
    private long age;
    private Bug61Child rBug61Child;

    public Bug61Parent(){}

    public void setAge(int age) {
        this.age = age;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setlBug61Child(Bug61Child lBug61Child) {
        this.lBug61Child = lBug61Child;
    }

    public void setrBug61Child(Bug61Child rBug61Child) {
        this.rBug61Child = rBug61Child;
    }

    public void setServerID(int serverID) {
        this.serverID = serverID;
    }

    public Bug61Child getlBug61Child() {
        return lBug61Child;
    }

    public Bug61Child getrBug61Child() {
        return rBug61Child;
    }
}
