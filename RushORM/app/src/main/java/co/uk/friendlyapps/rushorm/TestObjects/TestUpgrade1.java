package co.uk.friendlyapps.rushorm.TestObjects;

import java.util.List;

import co.uk.rushorm.core.RushObject;
import co.uk.rushorm.core.annotations.RushList;
import co.uk.rushorm.core.annotations.RushRenamed;

/**
 * Created by Stuart on 17/12/14.
 */
@RushRenamed(names = {"co.uk.friendlyapps.rushorm.TestObjects.TestBase1"})
public class TestUpgrade1 extends RushObject {
    public String stringField;
    public double doubleField;
    public int intField;
    public long longField;
    public short shortField;
    public boolean booleanField;

    public TestBase2 testBase2;

    @RushList(classname = "co.uk.friendlyapps.rushorm.TestObjects.TestBase2")
    public List<TestBase2> testBase2List;
}
