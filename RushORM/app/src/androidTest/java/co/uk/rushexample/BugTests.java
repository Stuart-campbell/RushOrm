package co.uk.rushexample;

import android.app.Application;
import android.test.ApplicationTestCase;

import co.uk.rushexample.testobjects.Bug6;
import co.uk.rushexample.testobjects.Bug7Child;
import co.uk.rushexample.testobjects.Bug7Parent;
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
        Utils.setUp(getContext());
    }

    @Override
    public void tearDown() throws Exception {
        super.tearDown();
    }

    public void testBug6() throws Exception {

        Bug6 user = new Bug6(null);
        user.save();

        Bug6 loadedUser = new RushSearch().findSingle(Bug6.class);

        assertNotNull(loadedUser);
    }

    public void testBug7() throws Exception {

        Bug7Parent parent = new Bug7Parent();
        parent.save();

        Bug7Child firstChild = new Bug7Child();
        firstChild.save();

        Bug7Child secondChild = new Bug7Child();
        secondChild.save();

        parent.add(firstChild);
        parent.add(secondChild);
        parent.save();

        parent = new RushSearch().find(Bug7Parent.class).get(0);
        parent.getChildren().get(0).setString("test1");
        parent.save();

        parent = new RushSearch().find(Bug7Parent.class).get(0);
        
        assertNotNull(parent.getChildren().get(0).getString().equals("test1"));
    }
}
