package co.uk.rushorm.android.testobjects;

import java.util.ArrayList;
import java.util.List;

import co.uk.rushorm.core.RushObject;
import co.uk.rushorm.core.annotations.RushList;

/**
 * Created by Stuart on 18/07/15.
 */
public class Bug29A extends RushObject{

    public String name;

    @RushList(classType = Bug29B.class)
    public List<Bug29B> bug29Bs = new ArrayList<>();

    public Bug29A(String name, List<Bug29B> bug29Bs) {
        this.name = name;
        this.bug29Bs = bug29Bs;
    }

    public Bug29A() {  }
}
