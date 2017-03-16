package co.uk.rushorm.android.testobjects;

import java.util.List;

import co.uk.rushorm.core.RushObject;
import co.uk.rushorm.core.annotations.RushCustomTableName;

/**
 * Created by Stuart on 05/05/15.
 */
@RushCustomTableName(name = "MyCustomTableName")
public class TestCustomName extends RushObject {

    public TestObject testObject;
    public List<TestChildObject> testChildObjects;
}
