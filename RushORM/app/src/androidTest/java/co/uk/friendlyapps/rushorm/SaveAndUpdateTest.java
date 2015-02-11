package co.uk.friendlyapps.rushorm;

import android.app.Application;
import android.test.ApplicationTestCase;

import java.util.ArrayList;
import java.util.List;

import co.uk.rushorm.core.RushSearch;
import co.uk.friendlyapps.rushorm.testobjects.TestChildObject;
import co.uk.friendlyapps.rushorm.testobjects.TestObject;
import co.uk.rushorm.android.RushAndroid;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class SaveAndUpdateTest extends ApplicationTestCase<Application> {

    public SaveAndUpdateTest() {
        super(Application.class);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        getContext().deleteDatabase("rush.db");
        RushAndroid.initialize(getContext());
    }

    @Override
    public void tearDown() throws Exception {
        super.tearDown();
    }

    public void testSaveString() throws Exception {

        TestObject testObject = new TestObject();
        testObject.stringField = "string";
        testObject.save();
        long id = testObject.getId();

        TestObject loadedObject = new RushSearch().whereId(id).findSingle(TestObject.class);
        assertTrue(loadedObject.stringField.equals("string"));
    }

    public void testUpdateString() throws Exception {

        TestObject testObject = new TestObject();
        testObject.stringField = "string";
        testObject.save();
        long id = testObject.getId();

        TestObject loadedObject = new RushSearch().whereId(id).findSingle(TestObject.class);
        loadedObject.stringField = "new string";
        loadedObject.save();

        TestObject updatedLoadedObject = new RushSearch().whereId(id).findSingle(TestObject.class);
        assertTrue(updatedLoadedObject.stringField.equals("new string"));
    }

    public void testSaveInt() throws Exception {

        TestObject testObject = new TestObject();
        testObject.intField = 10;
        testObject.save();
        long id = testObject.getId();

        TestObject loadedObject = new RushSearch().whereId(id).findSingle(TestObject.class);

        assertTrue(loadedObject.intField == 10);
    }

    public void testUpdateInt() throws Exception {

        TestObject testObject = new TestObject();
        testObject.intField = 10;
        testObject.save();
        long id = testObject.getId();

        TestObject loadedObject = new RushSearch().whereId(id).findSingle(TestObject.class);
        loadedObject.intField = 5;
        loadedObject.save();

        TestObject updatedLoadedObject = new RushSearch().whereId(id).findSingle(TestObject.class);
        assertTrue(updatedLoadedObject.intField == 5);
    }

    public void testSaveDouble() throws Exception {

        TestObject testObject = new TestObject();
        testObject.doubleField = 10.05;
        testObject.save();
        long id = testObject.getId();

        TestObject loadedObject = new RushSearch().whereId(id).findSingle(TestObject.class);

        assertTrue(loadedObject.doubleField == 10.05);
    }

    public void testUpdateDouble() throws Exception {

        TestObject testObject = new TestObject();
        testObject.doubleField = 10.05;
        testObject.save();
        long id = testObject.getId();

        TestObject loadedObject = new RushSearch().whereId(id).findSingle(TestObject.class);
        loadedObject.doubleField = 8.78;
        loadedObject.save();

        TestObject updatedLoadedObject = new RushSearch().whereId(id).findSingle(TestObject.class);
        assertTrue(updatedLoadedObject.doubleField == 8.78);
    }

    public void testSaveShort() throws Exception {

        TestObject testObject = new TestObject();
        testObject.shortField = 1;
        testObject.save();
        long id = testObject.getId();

        TestObject loadedObject = new RushSearch().whereId(id).findSingle(TestObject.class);

        assertTrue(loadedObject.shortField == 1);
    }

    public void testUpdateShort() throws Exception {

        TestObject testObject = new TestObject();
        testObject.shortField = 1;
        testObject.save();
        long id = testObject.getId();

        TestObject loadedObject = new RushSearch().whereId(id).findSingle(TestObject.class);
        loadedObject.shortField = 0;
        loadedObject.save();

        TestObject updatedLoadedObject = new RushSearch().whereId(id).findSingle(TestObject.class);
        assertTrue(updatedLoadedObject.shortField == 0);
    }

    public void testSaveLong() throws Exception {

        TestObject testObject = new TestObject();
        testObject.longField = 1000000000;
        testObject.save();
        long id = testObject.getId();

        TestObject loadedObject = new RushSearch().whereId(id).findSingle(TestObject.class);

        assertTrue(loadedObject.longField == 1000000000);
    }

    public void testUpdateLong() throws Exception {

        TestObject testObject = new TestObject();
        testObject.longField = 1000000000;
        testObject.save();
        long id = testObject.getId();

        TestObject loadedObject = new RushSearch().whereId(id).findSingle(TestObject.class);
        loadedObject.longField = 1000000001;
        loadedObject.save();

        TestObject updatedLoadedObject = new RushSearch().whereId(id).findSingle(TestObject.class);
        assertTrue(updatedLoadedObject.longField == 1000000001);
    }

    public void testSaveBoolean() throws Exception {

        TestObject testObject = new TestObject();
        testObject.booleanField = true;
        testObject.save();
        long id = testObject.getId();

        TestObject loadedObject = new RushSearch().whereId(id).findSingle(TestObject.class);

        assertTrue(loadedObject.booleanField);
    }

    public void testUpdateBoolean() throws Exception {

        TestObject testObject = new TestObject();
        testObject.booleanField = true;
        testObject.save();
        long id = testObject.getId();

        TestObject loadedObject = new RushSearch().whereId(id).findSingle(TestObject.class);
        loadedObject.booleanField = false;
        loadedObject.save();

        TestObject updatedLoadedObject = new RushSearch().whereId(id).findSingle(TestObject.class);
        assertTrue(!updatedLoadedObject.booleanField);
    }

    public void testSaveChild() throws Exception {

        TestObject testObject = new TestObject();
        testObject.childObject = new TestChildObject();
        testObject.save();
        long id = testObject.getId();

        TestObject loadedObject = new RushSearch().whereId(id).findSingle(TestObject.class);

        assertTrue(loadedObject.childObject != null);
    }

    public void testSaveChild2() throws Exception {

        TestObject testObject1 = new TestObject();
        testObject1.save();

        TestObject testObject = new TestObject();
        testObject.childObject = new TestChildObject();
        testObject.save();
        long id = testObject.getId();

        TestObject loadedObject = new RushSearch().whereId(id).findSingle(TestObject.class);

        assertTrue(loadedObject.childObject != null);
    }

    public void testUpdateChildRemove() throws Exception {

        TestObject testObject = new TestObject();
        testObject.childObject = new TestChildObject();
        testObject.save();
        long id = testObject.getId();

        TestObject loadedObject = new RushSearch().whereId(id).findSingle(TestObject.class);
        loadedObject.childObject = null;
        loadedObject.save();

        TestObject updatedLoadedObject = new RushSearch().whereId(id).findSingle(TestObject.class);
        assertTrue(updatedLoadedObject.childObject == null);
    }

    public void testUpdateChildChange() throws Exception {

        TestObject testObject = new TestObject();
        testObject.childObject = new TestChildObject();
        testObject.save();
        long id = testObject.getId();

        TestObject loadedObject = new RushSearch().whereId(id).findSingle(TestObject.class);
        long childId = loadedObject.childObject.getId();
        loadedObject.childObject = new TestChildObject();
        loadedObject.save();

        TestObject updatedLoadedObject = new RushSearch().whereId(id).findSingle(TestObject.class);
        long newChildId = updatedLoadedObject.childObject.getId();
        assertTrue(newChildId != childId);
    }

    public void testSaveChildrenSizeOne() throws Exception {

        TestObject testObject = new TestObject();
        testObject.children = new ArrayList<>();
        testObject.children.add(new TestChildObject());
        testObject.save();
        long id = testObject.getId();

        TestObject loadedObject = new RushSearch().whereId(id).findSingle(TestObject.class);

        assertTrue(loadedObject.children.size() == 1);
    }

    public void testSaveChildrenSizeTwo() throws Exception {

        TestObject testObject = new TestObject();
        testObject.children = new ArrayList<>();
        testObject.children.add(new TestChildObject());
        testObject.children.add(new TestChildObject());
        testObject.save();
        long id = testObject.getId();

        TestObject loadedObject = new RushSearch().whereId(id).findSingle(TestObject.class);

        assertTrue(loadedObject.children.size() == 2);
    }

    public void testRushIgnore() throws Exception {

        TestObject testObject = new TestObject();
        testObject.ignoredField = "Hello";
        testObject.save();
        long id = testObject.getId();

        TestObject loadedObject = new RushSearch().whereId(id).findSingle(TestObject.class);
        assertNull(loadedObject.ignoredField);
    }

    public void testRandomSetOfActionsTogether() throws Exception {

        TestObject testObject = new TestObject();
        testObject.ignoredField = "Hello";
        testObject.stringField = "Object 1";
        testObject.save();

        TestObject testObject2 = new TestObject();
        testObject2.stringField = "Object 1";
        testObject2.save();

        TestObject testObject3 = new TestObject();
        testObject3.stringField = "Object 2";
        testObject3.childObject = new TestChildObject();
        testObject3.childObject2 = new TestChildObject();
        testObject3.save();

        TestObject testObject4 = new TestObject();
        testObject4.stringField = "Object 4";
        testObject4.childObject = new TestChildObject();
        testObject4.save();

        TestObject loadedObject = new RushSearch().whereEqual("stringField", "Object 2").findSingle(TestObject.class);
        assertNotNull(loadedObject.childObject);
        assertNotNull(loadedObject.childObject2);

        TestObject loadedObject2 = new RushSearch().whereEqual("stringField", "Object 4").findSingle(TestObject.class);
        assertNotNull(loadedObject2.childObject);
        assertNull(loadedObject2.childObject2);

        List<TestObject> objects = new RushSearch().whereEqual("stringField", "Object 1").find(TestObject.class);
        assertTrue(objects.size() == 2);
    }

}