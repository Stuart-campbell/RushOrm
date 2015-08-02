package co.uk.rushexample.testobjects;

import co.uk.rushorm.core.RushObject;
import co.uk.rushorm.core.annotations.RushTableAnnotation;

/**
 * Created by Stuart on 02/08/15.
 */
@RushTableAnnotation
public class Bug61Child extends RushObject {
    private long serverID;
    private String size;

    public Bug61Child(){}

    public Bug61Child(long id, String size) {
        this.size = size;
        this.serverID = id;
    }

    public long getServerID() {
        return serverID;
    }

    public String getSize() {
        return size;
    }
}
