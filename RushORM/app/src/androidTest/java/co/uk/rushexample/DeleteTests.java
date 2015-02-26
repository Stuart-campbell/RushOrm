package co.uk.rushexample;

import android.app.Application;
import android.test.ApplicationTestCase;

import java.util.ArrayList;
import java.util.List;

import co.uk.rushexample.testobjects.TestSelfReference;
import co.uk.rushorm.core.RushCore;
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
        Utils.setUp(getContext());
    }

    @Override
    public void tearDown() throws Exception {
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

    public void testDeleteList() throws Exception {
        List<TestObject> objects = new ArrayList<>();
        objects.add(new TestObject());

        RushCore.getInstance().save(objects);

        List<TestObject> loadedObjects = new RushSearch().find(TestObject.class);
        loadedObjects.add(new TestObject());
        RushCore.getInstance().delete(loadedObjects);

        List<TestObject> loadedObjects2 = new RushSearch().find(TestObject.class);
        assertTrue(loadedObjects2.size() == 0);
    }

}
