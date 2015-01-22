package co.uk.friendlyapps.rushorm.TestObjects;

import java.util.List;

import co.uk.rushorm.core.RushTable;
import co.uk.rushorm.core.annotations.RushList;
import co.uk.rushorm.core.annotations.RushRenamed;

/**
 * Created by Stuart on 18/12/14.
 */
@RushRenamed(names = {"co.uk.friendlyapps.rushorm.TestObjects.TestBase1"})
public class TestUpgrade3 extends RushTable {
    public String stringField;
    public double doubleField;
    public int intField;
    public long longField;
    public short shortField;
    public boolean booleanField;

    public TestUpgrade5 testBase2;

    @RushList(classname = "co.uk.friendlyapps.rushorm.TestObjects.TestUpgrade5")
    public List<TestUpgrade5> testBase2List;
}
