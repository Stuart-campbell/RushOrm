package co.uk.rushexample.testobjects;

import java.util.List;

import co.uk.rushorm.core.RushObject;
import co.uk.rushorm.core.annotations.RushList;
import co.uk.rushorm.core.annotations.RushTableAnnotation;

/**
 * Created by Stuart on 31/01/15.
 */
@RushTableAnnotation
public class TestRootObject extends RushObject {

    @RushList(classname = "co.uk.rushexample.testobjects.TestObject")
    public List<TestObject> objects;

}
