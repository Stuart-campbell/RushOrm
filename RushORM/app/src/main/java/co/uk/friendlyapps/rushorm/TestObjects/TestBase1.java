package co.uk.friendlyapps.rushorm.TestObjects;

import java.util.List;

import co.uk.rushorm.core.RushTable;
import co.uk.rushorm.core.annotations.RushList;

/**
 * Created by Stuart on 17/12/14.
 */
public class TestBase1 extends RushTable {
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
