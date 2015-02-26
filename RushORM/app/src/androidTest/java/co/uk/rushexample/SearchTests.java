package co.uk.rushexample;

import android.app.Application;
import android.test.ApplicationTestCase;

import java.util.ArrayList;
import java.util.List;

import co.uk.rushorm.core.RushCore;
import co.uk.rushorm.core.RushSearch;
import co.uk.rushexample.testobjects.TestChildObject;
import co.uk.rushexample.testobjects.TestObject;
import co.uk.rushorm.android.RushAndroid;

/**
 * Created by Stuart on 15/12/14.
 */
public class SearchTests extends ApplicationTestCase<Application> {

    public SearchTests() {
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

    public void testFindByString() throws Exception {

        TestObject testObject = new TestObject();
        testObject.stringField = "string";
        testObject.save();

        List<TestObject> results = new RushSearch().whereEqual("stringField", "string").find(TestObject.class);

        assertTrue(results.size() == 1);
    }

    public void testFindByStringTwo() throws Exception {

        TestObject testObject = new TestObject();
        testObject.stringField = "string";
        testObject.save();

        TestObject testObject2 = new TestObject();
        testObject2.stringField = "string";
        testObject2.save();

        List<TestObject> results = new RushSearch().whereEqual("stringField", "string").find(TestObject.class);

        assertTrue(results.size() == 2);
    }

    public void testFindByInt() throws Exception {

        TestObject testObject = new TestObject();
        testObject.intField = 5;
        testObject.save();

        List<TestObject> results = new RushSearch().whereEqual("intField", 5).find(TestObject.class);

        assertTrue(results.size() == 1);
    }
    public void testFindByIntTwo() throws Exception {

        TestObject testObject = new TestObject();
        testObject.intField = 5;
        testObject.save();

        TestObject testObject2 = new TestObject();
        testObject2.intField = 5;
        testObject2.save();

        List<TestObject> results = new RushSearch().whereEqual("intField", 5).find(TestObject.class);

        assertTrue(results.size() == 2);
    }

    public void testFindByDouble() throws Exception {

        TestObject testObject = new TestObject();
        testObject.doubleField = 5.1234;
        testObject.save();

        List<TestObject> results = new RushSearch().whereEqual("doubleField", 5.1234).find(TestObject.class);

        assertTrue(results.size() == 1);
    }
    public void testFindByDoubleTwo() throws Exception {

        TestObject testObject = new TestObject();
        testObject.doubleField = 5.1234;
        testObject.save();

        TestObject testObject2 = new TestObject();
        testObject2.doubleField = 5.1234;
        testObject2.save();

        List<TestObject> results = new RushSearch().whereEqual("doubleField", 5.1234).find(TestObject.class);

        assertTrue(results.size() == 2);
    }

    public void testFindByChild() throws Exception {

        TestChildObject testChildObject = new TestChildObject();

        TestObject testObject = new TestObject();
        testObject.childObject = testChildObject;
        testObject.save();

        List<TestObject> results = new RushSearch().whereEqual("childObject", testChildObject).find(TestObject.class);

        assertTrue(results.size() == 1);
    }

    public void testFindByChildTwo() throws Exception {

        TestChildObject testChildObject = new TestChildObject();

        TestObject testObject = new TestObject();
        testObject.childObject = testChildObject;
        testObject.save();

        TestObject testObject2 = new TestObject();
        testObject2.childObject = testChildObject;
        testObject2.save();

        List<TestObject> results = new RushSearch().whereEqual("childObject", testChildObject).find(TestObject.class);

        assertTrue(results.size() == 2);
    }

    public void testFindByChildInList() throws Exception {

        TestChildObject testChildObject = new TestChildObject();

        TestObject testObject = new TestObject();
        testObject.children = new ArrayList<>();
        testObject.children.add(testChildObject);
        testObject.save();

        List<TestObject> results = new RushSearch().whereEqual("children", testChildObject).find(TestObject.class);

        assertTrue(results.size() == 1);
    }

    public void testFindByStringAndInt() throws Exception {

        TestObject testObject = new TestObject();
        testObject.stringField = "string";
        testObject.intField = 5;
        testObject.save();

        List<TestObject> results = new RushSearch()
                .whereEqual("stringField", "string")
                .and()
                .whereEqual("intField", 5)
                .find(TestObject.class);

        assertTrue(results.size() == 1);
    }

    public void testFindByStringAndIntWrongInt() throws Exception {

        TestObject testObject = new TestObject();
        testObject.stringField = "string";
        testObject.save();

        List<TestObject> results = new RushSearch()
                .whereEqual("stringField", "string")
                .and()
                .whereEqual("intField", 5)
                .find(TestObject.class);

        assertTrue(results.size() == 0);
    }

    public void testFindByStringAndIntWrongString() throws Exception {

        TestObject testObject = new TestObject();
        testObject.intField = 5;
        testObject.save();

        List<TestObject> results = new RushSearch()
                .whereEqual("stringField", "string")
                .and()
                .whereEqual("intField", 5)
                .find(TestObject.class);

        assertTrue(results.size() == 0);
    }

    public void testFindByStringOrIntWrongInt() throws Exception {

        TestObject testObject = new TestObject();
        testObject.stringField = "string";
        testObject.save();

        List<TestObject> results = new RushSearch()
                .whereEqual("stringField", "string")
                .or()
                .whereEqual("intField", 5)
                .find(TestObject.class);

        assertTrue(results.size() == 1);
    }

    public void testFindByStringOrIntWrongString() throws Exception {

        TestObject testObject = new TestObject();
        testObject.intField = 5;
        testObject.save();

        List<TestObject> results = new RushSearch()
                .whereEqual("stringField", "string")
                .or()
                .whereEqual("intField", 5)
                .find(TestObject.class);

        assertTrue(results.size() == 1);
    }

    public void testFindByIntAndDoubleOrStringWrongString() throws Exception {

        TestObject testObject = new TestObject();
        testObject.intField = 5;
        testObject.booleanField = true;
        testObject.stringField = "string";
        testObject.save();

        List<TestObject> results = new RushSearch()
                .whereEqual("stringField", "string")
                .and()
                .startGroup()
                .whereEqual("intField", 4)
                .or()
                .whereEqual("booleanField", true)
                .endGroup()
                .find(TestObject.class);

        assertTrue(results.size() == 1);
    }

    public void testOrderDescByInt() throws Exception {

        TestObject testObject = new TestObject();
        testObject.intField = 1;
        testObject.save();

        TestObject testObject1 = new TestObject();
        testObject1.intField = 2;
        testObject1.save();

        List<TestObject> results = new RushSearch()
                .orderDesc("intField")
                .find(TestObject.class);


        TestObject firstChild = results.get(0);
        assertTrue(firstChild.intField == 2);
    }

    public void testOrderAscByInt() throws Exception {

        TestObject testObject = new TestObject();
        testObject.intField = 1;
        testObject.save();

        TestObject testObject1 = new TestObject();
        testObject1.intField = 2;
        testObject1.save();

        List<TestObject> results = new RushSearch()
                .orderAsc("intField")
                .find(TestObject.class);


        TestObject firstChild = results.get(0);
        assertTrue(firstChild.intField == 1);
    }

    public void testExample1() throws Exception {

        TestObject testObject = new TestObject();
        testObject.intField = 1;
        testObject.save();

        TestObject testObject1 = new TestObject();
        testObject1.intField = 2;
        testObject1.stringField = "Hello world";
        testObject1.save();

        TestObject testObject2 = new TestObject();
        testObject2.intField = 6;
        testObject2.stringField = "Hello world";
        testObject2.save();

        TestObject testObject3 = new TestObject();
        testObject3.intField = 8;
        testObject3.save();

        String id = testObject.getId();

        // Get all objects with id 1 or stringField "Hello world" and intField greater than 5 order ascending by intField
        List<TestObject> objects = new RushSearch()
                .whereId(id)
                .or()
                .startGroup()
                .whereEqual("stringField", "Hello world")
                .and()
                .whereGreaterThan("intField", 5)
                .endGroup()
                .orderDesc("intField")
                .find(TestObject.class);

        TestObject firstChild = objects.get(0);
        assertTrue(firstChild.intField == 6);
    }

    public void testExample2() throws Exception {

        List<TestObject> objects = new ArrayList<>();

        TestObject testObject = new TestObject();
        testObject.intField = 1;
        objects.add(testObject);

        TestObject testObject1 = new TestObject();
        testObject1.intField = 2;
        testObject1.stringField = "Hello world";
        objects.add(testObject1);

        TestObject testObject2 = new TestObject();
        testObject2.intField = 6;
        testObject2.stringField = "Hello world";
        objects.add(testObject2);

        TestObject testObject3 = new TestObject();
        testObject3.intField = 8;
        objects.add(testObject3);

        RushCore.getInstance().save(objects);

        String id = testObject.getId();

        // Get all objects with id 1 or stringField "Hello world" and intField greater than 5 order ascending by intField
        List<TestObject> loadedObjects = new RushSearch()
                .whereId(id)
                .or()
                .startGroup()
                .whereEqual("stringField", "Hello world")
                .and()
                .whereGreaterThan("intField", 5)
                .endGroup()
                .orderDesc("intField")
                .find(TestObject.class);

        TestObject firstChild = loadedObjects.get(0);
        assertTrue(firstChild.intField == 6);
    }
}
