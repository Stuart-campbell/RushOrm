package co.uk.rushorm.android.testobjects;

import java.util.List;

import co.uk.rushorm.core.RushObject;
import co.uk.rushorm.core.annotations.RushList;

/**
 * Created by Stuart on 31/01/15.
 */
public class TestRootObject extends RushObject {

    @RushList(classType = TestObject.class)
    public List<TestObject> objects;

}
