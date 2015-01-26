package co.uk.friendlyapps.rushorm.TestObjects;

import java.util.List;

import co.uk.rushorm.core.RushObject;
import co.uk.rushorm.core.annotations.RushDisableAutodelete;
import co.uk.rushorm.core.annotations.RushIgnore;
import co.uk.rushorm.core.annotations.RushList;
import co.uk.rushorm.core.annotations.RushTableAnnotation;

/**
 * Created by Stuart on 15/12/14.
 */
@RushTableAnnotation
public class TestObject extends RushObject {

    public String stringField;
    public double doubleField;
    public int intField;
    public long longField;
    public short shortField;
    public boolean booleanField;
    public TestChildObject childObject;

    @RushDisableAutodelete
    public TestChildObject childObject2;

    @RushIgnore
    public String ignoredField;

    @RushList(classname = "co.uk.friendlyapps.rushorm.TestObjects.TestChildObject")
    public List<TestChildObject> children;

}
