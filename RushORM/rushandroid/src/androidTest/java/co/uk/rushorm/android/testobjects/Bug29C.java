package co.uk.rushorm.android.testobjects;

import java.util.ArrayList;
import java.util.List;

import co.uk.rushorm.core.RushObject;
import co.uk.rushorm.core.annotations.RushList;

/**
 * Created by Stuart on 18/07/15.
 */
public class Bug29C extends RushObject {

    public String name;

    @RushList(classType = Bug29A.class)
    public List<Bug29A> bug29As = new ArrayList<>();

    public Bug29C(List<Bug29A> bug29As) {
        this.bug29As = bug29As;
    }

    public Bug29C() {
    }
}
