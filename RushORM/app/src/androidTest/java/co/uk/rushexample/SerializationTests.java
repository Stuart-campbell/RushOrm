package co.uk.rushexample;

import android.app.Application;
import android.test.ApplicationTestCase;

import java.util.ArrayList;
import java.util.List;

import co.uk.rushexample.testobjects.TestObject;
import co.uk.rushorm.android.RushAndroid;
import co.uk.rushorm.core.Rush;
import co.uk.rushorm.core.RushCore;

/**
 * Created by Stuart on 18/02/15.
 */
public class SerializationTests extends ApplicationTestCase<Application> {

    public SerializationTests() {
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

    public void testSerialize() throws Exception {

        TestObject testObject = new TestObject();
        testObject.stringField = "string";
        testObject.save();

        List<TestObject> objects = new ArrayList<>();
        objects.add(testObject);

        String jsonString = RushCore.getInstance().serialize(objects);

        assertTrue(jsonString != null);
    }

    public void testDeserialize() throws Exception {

        TestObject testObject = new TestObject();
        testObject.stringField = "string";
        testObject.save();

        List<TestObject> objects = new ArrayList<>();
        objects.add(testObject);

        String jsonString = RushCore.getInstance().serialize(objects);

        List<Rush> deserializeObject = RushCore.getInstance().deserialize(jsonString);

        assertTrue(deserializeObject.size() == 1);
    }
}
