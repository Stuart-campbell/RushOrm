package co.uk.rushorm.android.testobjects;

import java.util.ArrayList;
import java.util.List;

import co.uk.rushorm.core.RushObject;
import co.uk.rushorm.core.annotations.RushList;

/**
 * Created by stuartc on 19/02/15.
 */
public class Bug7Parent extends RushObject {

    @RushList(classType = Bug7Child.class)
    private List<Bug7Child> children;

    public Bug7Parent() {
        this.children = new ArrayList<Bug7Child>();
    }

    public boolean add(Bug7Child child) {
        return children.add(child);
    }

    public boolean remove(Bug7Child child) {
        return children.remove(child);
    }

    public List<Bug7Child> getChildren() {
        return children;
    }

}


