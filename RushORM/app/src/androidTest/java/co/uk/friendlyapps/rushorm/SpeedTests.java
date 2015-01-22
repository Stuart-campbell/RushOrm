package co.uk.friendlyapps.rushorm;

import android.app.Application;
import android.test.ApplicationTestCase;

import java.util.ArrayList;
import java.util.Date;

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

        assertTrue("Save time of 1001 rows : " + Double.toString(time), time < 10);
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

        assertTrue("Delete time of 1001 rows : " + Double.toString(time), time < 10);
    }
}
