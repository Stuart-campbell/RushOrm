package co.uk.rushexample;

import android.app.Application;
import android.test.ApplicationTestCase;

import java.util.ArrayList;

import co.uk.rushexample.testobjects.TestSelfReference;
import co.uk.rushorm.core.RushSearch;
import co.uk.rushexample.testobjects.TestChildObject;
import co.uk.rushexample.testobjects.TestObject;
import co.uk.rushorm.android.RushAndroid;

/**
 * Created by Stuart on 16/12/14.
 */
public class DeleteTests extends ApplicationTestCase<Application> {

    public DeleteTests() {
        super(Application.class);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        getContext().deleteDatabase("rush.db");
        Thread.sleep(200);
        RushAndroid.initialize(getContext());
    }

    @Override
    public void tearDown() throws Exception {
        getContext().deleteDatabase("rush.db");
        super.tearDown();
    }

    public void testDeleteSingle() throws Exception {

        TestObject testObject = new TestObject();
        testObject.stringField = "string";
        testObject.save();
        String id = testObject.getId();

        TestObject loadedObject = new RushSearch().whereId(id).findSingle(TestObject.class);
        loadedObject.delete();

        TestObject deletedObject = new RushSearch().whereId(id).findSingle(TestObject.class);
        assertNull(deletedObject);
    }

    public void testDeleteWithChild() throws Exception {

        TestObject testObject = new TestObject();
        testObject.childObject = new TestChildObject();
        testObject.save();
        String id = testObject.getId();
        String childId = testObject.childObject.getId();

        TestObject loadedObject = new RushSearch().whereId(id).findSingle(TestObject.class);
        loadedObject.delete();

        TestChildObject childObject = new RushSearch().whereId(childId).findSingle(TestChildObject.class);
        assertNull(childObject);
    }

    public void testDeleteWithChildren() throws Exception {

        TestObject testObject = new TestObject();
        testObject.children = new ArrayList<>();
        testObject.children.add(new TestChildObject());
        testObject.save();
        String id = testObject.getId();
        String childId = testObject.children.get(0).getId();

        TestObject loadedObject = new RushSearch().whereId(id).findSingle(TestObject.class);
        loadedObject.delete();

        TestChildObject childObject = new RushSearch().whereId(childId).findSingle(TestChildObject.class);
        assertNull(childObject);
    }

    public void testSaveDeletedChild() throws Exception {

        TestObject testObject = new TestObject();
        testObject.childObject = new TestChildObject();
        testObject.save();
        String childId = testObject.childObject.getId();

        TestChildObject childObject = new RushSearch().whereId(childId).findSingle(TestChildObject.class);
        childObject.delete();

        testObject.save();

        TestChildObject loadedChildObject = new RushSearch().whereId(childId).findSingle(TestChildObject.class);

        assertTrue(loadedChildObject != null);
    }

    public void testDeleteChild() throws Exception {

        TestObject testObject = new TestObject();
        testObject.childObject = new TestChildObject();
        testObject.save();
        String id = testObject.getId();
        String childId = testObject.childObject.getId();

        TestChildObject childObject = new RushSearch().whereId(childId).findSingle(TestChildObject.class);
        childObject.delete();

        TestObject loadedObject = new RushSearch().whereId(id).findSingle(TestObject.class);
        assertNull(loadedObject.childObject);
    }


    public void testDeleteWithChildrenAndAutoDeleteOff() throws Exception {

        TestObject testObject = new TestObject();
        testObject.childObject2 = new TestChildObject();
        testObject.save();
        String id = testObject.getId();
        String childId = testObject.childObject2.getId();

        TestObject loadedObject = new RushSearch().whereId(id).findSingle(TestObject.class);
        loadedObject.delete();

        TestChildObject childObject = new RushSearch().whereId(childId).findSingle(TestChildObject.class);
        assertNotNull(childObject);
    }

    public void testCircularReferenceDelete() throws Exception {
        TestSelfReference object = new TestSelfReference();
        object.child = object;
        object.save();
        String id = object.getId();

        TestSelfReference loadedObject = new RushSearch().whereId(id).findSingle(TestSelfReference.class);
        loadedObject.delete();

        assertTrue(loadedObject.getId() == null && loadedObject.child.getId() == null);
    }
}
