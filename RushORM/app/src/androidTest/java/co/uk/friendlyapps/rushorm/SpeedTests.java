package co.uk.friendlyapps.rushorm;

import android.app.Application;
import android.test.ApplicationTestCase;
import android.util.Log;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import co.uk.rushorm.core.RushCore;
import co.uk.rushorm.core.RushSearch;
import co.uk.friendlyapps.rushorm.TestObjects.TestChildObject;
import co.uk.friendlyapps.rushorm.TestObjects.TestObject;
import co.uk.rushorm.android.RushAndroid;

/**
 * Created by Stuart on 16/12/14.
 */
public class SpeedTests extends ApplicationTestCase<Application> {

    public SpeedTests() {
        super(Application.class);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        getContext().deleteDatabase("rush.db");
        Thread.sleep(100);
        RushAndroid.initialize(getContext());
    }

    @Override
    public void tearDown() throws Exception {
        super.tearDown();
    }

    public void testSave1000Rows() throws Exception {

        Date date = new Date();

        TestObject testObject = new TestObject();
        testObject.children = new ArrayList<>();
        for(int i = 0; i < 1000; i ++){
            testObject.children.add(new TestChildObject());
        }
        testObject.save();

        double time = (new Date().getTime() - date.getTime()) / 1000.0;

        Log.i("SPEED_TEST", "Save 1001 - " + Double.toString(time));

        assertTrue("Save time of 1001 rows : " + Double.toString(time), time < 10);
    }

    public void testSave1000ChildrenRows() throws Exception {

        Date date = new Date();

        for (int i = 0; i < 1000; i++) {
            TestChildObject testObject = new TestChildObject();
            testObject.save();
        }

        double time = (new Date().getTime() - date.getTime()) / 1000.0;

        Log.i("SPEED_TEST", "Save 1000 children - " + Double.toString(time));

        assertTrue("Save Children time of 1000 rows : " + Double.toString(time), time < 20);
    }

    public void testSave1000ChildrenInTransitionRows() throws Exception {

        Date date = new Date();
        List<TestChildObject> list = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            list.add(new TestChildObject());
        }

        RushCore.getInstance().save(list);

        double time = (new Date().getTime() - date.getTime()) / 1000.0;

        Log.i("SPEED_TEST", "Save 1000 children in transaction - " + Double.toString(time));

        assertTrue("Save Children in transition time of 100 rows : " + Double.toString(time), time < 10);
    }

    public void testSave1000ObjectInTransitionRows() throws Exception {

        Date date = new Date();
        List<TestObject> list = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            list.add(new TestObject());
        }

        RushCore.getInstance().save(list);

        double time = (new Date().getTime() - date.getTime()) / 1000.0;

        Log.i("SPEED_TEST", "Save 1000 object in transaction - " + Double.toString(time));

        assertTrue("Save Object in transition time of 100 rows : " + Double.toString(time), time < 10);
    }

    public void testLoad1000Rows() throws Exception {

        TestObject testObject = new TestObject();
        testObject.children = new ArrayList<>();
        for(int i = 0; i < 1000; i ++){
            testObject.children.add(new TestChildObject());
        }
        testObject.save();
        long id = testObject.getId();


        Date date = new Date();
        TestObject loadedObject = new RushSearch().whereId(id).findSingle(TestObject.class);
        double time = (new Date().getTime() - date.getTime()) / 1000.0;

        Log.i("SPEED_TEST", "Load 1000 - " + Double.toString(time));

        assertTrue("Load time of 1001 rows : " + Double.toString(time), time < 10);
    }

    public void testDelete1000Rows() throws Exception {

        TestObject testObject = new TestObject();
        testObject.children = new ArrayList<>();
        for(int i = 0; i < 1000; i ++){
            testObject.children.add(new TestChildObject());
        }
        testObject.save();
        long id = testObject.getId();

        TestObject loadedObject = new RushSearch().whereId(id).findSingle(TestObject.class);
        Date date = new Date();
        loadedObject.delete();
        double time = (new Date().getTime() - date.getTime()) / 1000.0;

        Log.i("SPEED_TEST", "Delete 1000 - " + Double.toString(time));

        assertTrue("Delete time of 1001 rows : " + Double.toString(time), time < 10);
    }
}
