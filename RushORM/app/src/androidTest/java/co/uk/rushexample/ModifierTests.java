package co.uk.rushexample;

import android.app.Application;
import android.test.ApplicationTestCase;

import co.uk.rushexample.testobjects.TestModifiers;
import co.uk.rushorm.android.RushAndroid;
import co.uk.rushorm.core.RushSearch;

/**
 * Created by Stuart on 23/01/15.
 */
public class ModifierTests extends ApplicationTestCase<Application> {

    public ModifierTests() {
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
        getContext().deleteDatabase("rush.db");
        super.tearDown();
    }

    public void testPublic() throws Exception {

        TestModifiers object = new TestModifiers();
        object.publicString = "test";
        object.save();
        long id = object.getId();

        TestModifiers loadedObject = new RushSearch().whereId(id).findSingle(TestModifiers.class);
        assertTrue(loadedObject.publicString.equals("test"));
    }

    public void testProtected() throws Exception {

        TestModifiers object = new TestModifiers("test", "test", "test");
        object.save();
        long id = object.getId();

        TestModifiers loadedObject = new RushSearch().whereId(id).findSingle(TestModifiers.class);
        assertTrue(loadedObject.getProtectedString().equals("test"));
    }

    public void testNormal() throws Exception {

        TestModifiers object = new TestModifiers("test", "test", "test");
        object.save();
        long id = object.getId();

        TestModifiers loadedObject = new RushSearch().whereId(id).findSingle(TestModifiers.class);
        assertTrue(loadedObject.getString().equals("test"));
    }

    public void testPrivate() throws Exception {

        TestModifiers object = new TestModifiers("test", "test", "test");
        object.save();
        long id = object.getId();

        TestModifiers loadedObject = new RushSearch().whereId(id).findSingle(TestModifiers.class);
        assertTrue(loadedObject.getPrivateString().equals("test"));
    }
}
