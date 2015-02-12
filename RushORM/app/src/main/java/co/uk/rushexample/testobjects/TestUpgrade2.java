package co.uk.rushexample.testobjects;

import java.util.List;

import co.uk.rushorm.core.RushObject;
import co.uk.rushorm.core.annotations.RushList;
import co.uk.rushorm.core.annotations.RushRenamed;

/**
 * Created by Stuart on 18/12/14.
 */
@RushRenamed(names = {"co.uk.rushexample.testobjects.TestBase1"})
public class TestUpgrade2 extends RushObject {
    @RushRenamed(names = {"stringField"})
    public String stringFieldNamed;
    @RushRenamed(names = {"doubleField"})
    public double doubleFieldNamed;
    @RushRenamed(names = {"intField"})
    public int intFieldNamed;
    @RushRenamed(names = {"longField"})
    public long longFieldNamed;
    @RushRenamed(names = {"shortField"})
    public short shortFieldNamed;
    @RushRenamed(names = {"booleanField"})
    public boolean booleanFieldNamed;
    @RushRenamed(names = {"testBase2"})
    public TestBase2 testBase2Names;
    @RushRenamed(names = {"testBase2List"})
    @RushList(classname = "co.uk.rushexample.testobjects.TestBase2")
    public List<TestBase2> testBase2ListNamed;
}
