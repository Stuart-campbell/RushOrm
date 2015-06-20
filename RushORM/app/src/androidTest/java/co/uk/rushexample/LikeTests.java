package co.uk.rushexample;

import android.app.Application;
import android.test.ApplicationTestCase;

import java.util.List;

import co.uk.rushexample.testobjects.TestObject;
import co.uk.rushorm.core.RushSearch;

/**
 * Created by Stuart on 20/06/15.
 */
public class LikeTests extends ApplicationTestCase<Application> {

    public LikeTests() {
        super(Application.class);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        Utils.setUp(getContext());
    }

    @Override
    public void tearDown() throws Exception {
        super.tearDown();
    }

    public void testLikeString() throws Exception {
        TestObject testObject = new TestObject();
        testObject.stringField = "abcdef";
        testObject.save();

        TestObject testObject2 = new TestObject();
        testObject2.stringField = "zzcdzz";
        testObject2.save();

        TestObject testObject3 = new TestObject();
        testObject3.stringField = "abzzef";
        testObject3.save();

        List<TestObject> objects = new RushSearch().whereLike("stringField", "__cd%").find(TestObject.class);

        assertTrue(objects.size() == 2);
    }

    public void testContains() throws Exception {
        TestObject testObject = new TestObject();
        testObject.stringField = "My String";
        testObject.save();

        TestObject testObject2 = new TestObject();
        testObject2.stringField = "My House";
        testObject2.save();

        TestObject testObject3 = new TestObject();
        testObject3.stringField = "House My";
        testObject3.save();

        List<TestObject> objects = new RushSearch().whereStartsWith("stringField", "My").find(TestObject.class);

        assertTrue(objects.size() == 2);
    }

    public void testStartsWith() throws Exception {
        TestObject testObject = new TestObject();
        testObject.stringField = "My String";
        testObject.save();

        TestObject testObject2 = new TestObject();
        testObject2.stringField = "My House";
        testObject2.save();

        TestObject testObject3 = new TestObject();
        testObject3.stringField = "House My";
        testObject3.save();

        List<TestObject> objects = new RushSearch().whereEndsWith("stringField", "My").find(TestObject.class);

        assertTrue(objects.size() == 1);
    }

    public void testEndsWith() throws Exception {
        TestObject testObject = new TestObject();
        testObject.stringField = "My String";
        testObject.save();

        TestObject testObject2 = new TestObject();
        testObject2.stringField = "My House";
        testObject2.save();

        TestObject testObject3 = new TestObject();
        testObject3.stringField = "House My";
        testObject3.save();

        List<TestObject> objects = new RushSearch().whereContains("stringField", "se").find(TestObject.class);

        assertTrue(objects.size() == 2);
    }
}