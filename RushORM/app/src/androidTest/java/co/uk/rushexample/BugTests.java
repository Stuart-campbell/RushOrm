package co.uk.rushexample;

import android.app.Application;
import android.test.ApplicationTestCase;

import co.uk.rushexample.testobjects.Bug6;
import co.uk.rushexample.testobjects.TestObject;
import co.uk.rushorm.android.RushAndroid;
import co.uk.rushorm.core.RushSearch;

/**
 * Created by Stuart on 18/02/15.
 */
public class BugTests extends ApplicationTestCase<Application> {

    public BugTests() {
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

    public void testBug6() throws Exception {

        Bug6 user = new Bug6(null);
        user.save();

        Bug6 loadedUser = new RushSearch().findSingle(Bug6.class);

        assertNotNull(loadedUser);
    }


}
