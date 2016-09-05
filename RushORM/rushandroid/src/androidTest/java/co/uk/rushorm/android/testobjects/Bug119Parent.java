package co.uk.rushorm.android.testobjects;

import java.util.List;

import co.uk.rushorm.core.RushObject;
import co.uk.rushorm.core.annotations.RushList;

/**
 * Created by stuartc on 05/09/2016.
 */

public class Bug119Parent extends RushObject {

    private String name;

    @RushList(classType = Bug119Child.class)
    private List<Bug119Child> events;

    public Bug119Parent() {
    }

    public Bug119Parent(String name, List<Bug119Child> events) {
        this.name = name;
        this.events = events;
    }

    public void addEvent(Bug119Child event) {
        events.add(event);
    }
}
