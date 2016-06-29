package co.uk.rushexample;

import android.app.Application;
import android.test.ApplicationTestCase;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import co.uk.rushexample.testobjects.TestChildObject;
import co.uk.rushexample.testobjects.TestObject;
import co.uk.rushorm.android.RushAndroid;
import co.uk.rushorm.core.Rush;
import co.uk.rushorm.core.RushColumn;
import co.uk.rushorm.core.RushColumns;
import co.uk.rushorm.core.RushCore;
import co.uk.rushorm.core.RushSearch;
import co.uk.rushorm.core.implementation.RushColumnBooleanNumerical;

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
        Utils.setUp(getContext());
    }

    public void testSerialize() throws Exception {

        TestObject testObject = new TestObject();
        testObject.stringField = "string";
        testObject.dateField = new Date();
        testObject.childObject = new TestChildObject();
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

    public void testSerialize1000() throws Exception {

        TestObject testObject = new TestObject();
        testObject.children = new ArrayList<>();
        for(int i = 0; i < 1000; i ++){
            testObject.children.add(new TestChildObject());
        }
        testObject.save();

        List<TestObject> objects = new ArrayList<>();
        objects.add(testObject);
        
        String jsonString = RushCore.getInstance().serialize(objects);

        assertTrue(jsonString != null);
    }

    public void testDeserialize1000() throws Exception {

        TestObject testObject = new TestObject();
        testObject.children = new ArrayList<>();
        for(int i = 0; i < 1000; i ++){
            testObject.children.add(new TestChildObject());
        }
        testObject.save();

        List<TestObject> objects = new ArrayList<>();
        objects.add(testObject);

        String jsonString = RushCore.getInstance().serialize(objects);

        List<Rush> deserializeObject = RushCore.getInstance().deserialize(jsonString);
        TestObject deserialized = (TestObject)deserializeObject.get(0);
        assertTrue(deserialized.children.size() == 1000);
    }

    public void testDeserialize0() throws Exception {

        List<TestObject> objects = new ArrayList<>();
        String jsonString = RushCore.getInstance().serialize(objects);

        List<Rush> deserializeObject = RushCore.getInstance().deserialize(jsonString);
        assertTrue(deserializeObject.isEmpty());
    }

    public void testSerializeMix() throws Exception {

        TestObject testObject = new TestObject();
        testObject.stringField = "string";
        testObject.save();

        List<Rush> objects = new ArrayList<>();
        objects.add(testObject);
        TestChildObject child = new TestChildObject();
        child.save();
        objects.add(child);
        objects.add(new TestChildObject());
        
        String jsonString = RushCore.getInstance().serialize(objects);

        assertTrue(jsonString != null);
    }

    public void testDeserializeMix() throws Exception {

        TestObject testObject = new TestObject();
        testObject.stringField = "string";
        testObject.save();

        List<Rush> objects = new ArrayList<>();
        objects.add(testObject);
        TestChildObject child = new TestChildObject();
        child.save();
        objects.add(child);
        objects.add(new TestChildObject());

        String jsonString = RushCore.getInstance().serialize(objects);
        List<Rush> deserializeObject = RushCore.getInstance().deserialize(jsonString);
        assertTrue(deserializeObject.size() == 3);
    }

    public void testBoolDeserialize() throws Exception {

        RushColumnBooleanNumerical column = new RushColumnBooleanNumerical();
        Boolean value = column.deserialize("true");
        assertTrue(value);
    }

    public void testRegisteringObject() throws Exception {

        TestObject testObject = new TestObject();
        RushCore.getInstance().registerObjectWithId(testObject, "1");
        testObject.save();

        TestObject testObject2 = new TestObject();
        RushCore.getInstance().registerObjectWithId(testObject2, "1");
        testObject2.save();

        List<TestObject> objects = new RushSearch().find(TestObject.class);

        assertTrue(objects.size() == 1);
    }

}
