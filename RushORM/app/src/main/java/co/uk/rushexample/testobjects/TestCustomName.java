package co.uk.rushexample.testobjects;

import java.util.List;

import co.uk.rushorm.core.RushObject;
import co.uk.rushorm.core.annotations.RushCustomTableName;
import co.uk.rushorm.core.annotations.RushTableAnnotation;

/**
 * Created by Stuart on 05/05/15.
 */
@RushTableAnnotation
@RushCustomTableName(name = "MyCustomTableName")
public class TestCustomName extends RushObject {

    public TestObject testObject;
    public List<TestChildObject> testChildObjects;
}
