package co.uk.rushexample;

import android.app.Application;
import android.test.ApplicationTestCase;

import java.util.ArrayList;
import java.util.List;

import co.uk.rushexample.testobjects.TestChildObject;
import co.uk.rushexample.testobjects.TestObject;
import co.uk.rushexample.testobjects.TestPageListObject;
import co.uk.rushorm.core.RushCore;
import co.uk.rushorm.core.RushPageList;
import co.uk.rushorm.core.RushSearch;

/**
 * Created by Stuart on 12/04/15.
 */
public class RushListFieldTests extends ApplicationTestCase<Application> {

    public RushListFieldTests() {
        super(Application.class);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        Utils.setUp(getContext());
    }

    public void testCount() throws Exception{

        List<TestObject> objects = new ArrayList<>();
        for (int i = 0; i < 10; i ++) {
            objects.add(new TestObject());
        }
        RushCore.getInstance().save(objects);

        long count = new RushSearch().count(TestObject.class);
        assertTrue(count == 10);
    }

    public void testAdd() throws Exception{

        TestPageListObject testPageListObject = new TestPageListObject();
        testPageListObject.children = new RushPageList<>(testPageListObject, "children", TestChildObject.class);

        for (int i = 0; i < 10; i ++) {
            testPageListObject.children.add(new TestChildObject());
        }

        TestPageListObject testPageListObject1 = new RushSearch().findSingle(TestPageListObject.class);
        assertTrue(testPageListObject1.children.size() == 10);
    }

    public void testAddAll() throws Exception{

        TestPageListObject testPageListObject = new TestPageListObject();
        testPageListObject.children = new RushPageList<>(testPageListObject, "children", TestChildObject.class);

        List<TestChildObject> objects = new ArrayList<>();
        for (int i = 0; i < 10; i ++) {
            objects.add(new TestChildObject());
        }
        testPageListObject.children.addAll(objects);

        TestPageListObject testPageListObject1 = new RushSearch().findSingle(TestPageListObject.class);
        assertTrue(testPageListObject1.children.size() == 10);
    }

    public void testLoop() throws Exception{

        TestPageListObject testPageListObject = new TestPageListObject();
        testPageListObject.children = new RushPageList<>(testPageListObject, "children", TestChildObject.class);

        List<TestChildObject> objects = new ArrayList<>();
        for (int i = 0; i < 10; i ++) {
            objects.add(new TestChildObject());
        }
        testPageListObject.children.addAll(objects);

        TestPageListObject testPageListObject1 = new RushSearch().findSingle(TestPageListObject.class);

        for (TestChildObject object : testPageListObject1.children) {
            assertNotNull(object);
        }
    }

    public void testLongLoop() throws Exception{

        TestPageListObject testPageListObject = new TestPageListObject();
        testPageListObject.children = new RushPageList<>(testPageListObject, "children", TestChildObject.class);

        List<TestChildObject> objects = new ArrayList<>();
        for (int i = 0; i < 1000; i ++) {
            objects.add(new TestChildObject());
        }
        testPageListObject.children.addAll(objects);

        TestPageListObject testPageListObject1 = new RushSearch().findSingle(TestPageListObject.class);

        for (TestChildObject object : testPageListObject1.children) {
            assertNotNull(object);
        }
    }

    public void testDoubleLongLoop() throws Exception{

        TestPageListObject testPageListObject = new TestPageListObject();
        testPageListObject.children = new RushPageList<>(testPageListObject, "children", TestChildObject.class);

        List<TestChildObject> objects = new ArrayList<>();
        for (int i = 0; i < 1000; i ++) {
            objects.add(new TestChildObject());
        }
        testPageListObject.children.addAll(objects);

        TestPageListObject testPageListObject1 = new RushSearch().findSingle(TestPageListObject.class);

        for (TestChildObject object : testPageListObject1.children) {
            assertNotNull(object);
        }
        for (TestChildObject object : testPageListObject1.children) {
            assertNotNull(object);
        }
    }

    public void testRemove() throws Exception{

        TestPageListObject testPageListObject = new TestPageListObject();
        testPageListObject.children = new RushPageList<>(testPageListObject, "children", TestChildObject.class);

        List<TestChildObject> objects = new ArrayList<>();
        for (int i = 0; i < 10; i ++) {
            objects.add(new TestChildObject());
        }
        TestChildObject testChildObject = new TestChildObject();
        testPageListObject.children.addAll(objects);
        testPageListObject.children.add(testChildObject);

        TestPageListObject testPageListObject1 = new RushSearch().findSingle(TestPageListObject.class);
        testPageListObject1.children.remove(testChildObject);

        assertTrue(testPageListObject1.children.size() == 10);
    }

}
