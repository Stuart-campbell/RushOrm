package co.uk.rushexample;

import android.app.Application;
import android.test.ApplicationTestCase;

import java.util.Arrays;
import java.util.List;

import co.uk.rushexample.testobjects.Bug29A;
import co.uk.rushexample.testobjects.Bug29B;
import co.uk.rushexample.testobjects.Bug29C;
import co.uk.rushexample.testobjects.Bug34;
import co.uk.rushexample.testobjects.Bug6;
import co.uk.rushexample.testobjects.Bug61Child;
import co.uk.rushexample.testobjects.Bug61Parent;
import co.uk.rushexample.testobjects.Bug7Child;
import co.uk.rushexample.testobjects.Bug7Parent;
import co.uk.rushorm.core.RushCore;
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

    public void testBug34() throws Exception {

        Bug34 object = new Bug34();
        object.save();

        List<Bug34> loaded = new RushSearch().find(Bug34.class);

        assertTrue(loaded.size() == 1);
    }

    public void testBug29test1() throws Exception {

        Bug29B bug29B = new Bug29B("bug29B");
        bug29B.save();

        Bug29B bug29B2 = new Bug29B("bug29B2");
        bug29B2.save();

        // Create Bug29A object with the list of 2 Bug29B objects
        Bug29A bug29A = new Bug29A("bug29A",
                Arrays.asList(new RushSearch().whereEqual("name", "bug29B").findSingle(Bug29B.class),
                        new RushSearch().whereEqual("name", "bug29B2").findSingle(Bug29B.class)));
        bug29A.save();

        // Create Bug29C object with the list of SAME Bug29A objects
        // If we load Bug29A object once, and add to list, everything works OK
        // But if we load exactly the same object twice, problem occurs.
        Bug29C bug29C = new Bug29C(Arrays.asList(new RushSearch().whereEqual("name", "bug29A").findSingle(Bug29A.class),
                new RushSearch().whereEqual("name", "bug29A").findSingle(Bug29A.class)));

        // It seems here 2 exactly the same E objects are saving
        bug29C.save();

        Bug29A bug29ALoaded = new RushSearch().findSingle(Bug29A.class);
        assertTrue(bug29ALoaded.bug29Bs.size() == 2);
    }

    public void testBug61() throws Exception {

        Bug61Child b1 = new Bug61Child(1,"A");
        Bug61Child b2 = new Bug61Child(2,"B");

        RushCore.getInstance().registerObjectWithId(b1, String.valueOf(b1.getServerID()));
        RushCore.getInstance().registerObjectWithId(b2, String.valueOf(b2.getServerID()));

        b1.save();
        b2.save();

        // load women

        Bug61Parent betty = new Bug61Parent();
        betty.setAge(22);
        betty.setName("betty");
        betty.setlBug61Child(b1);
        betty.setrBug61Child(b2);
        betty.setServerID(12);// not important now
        betty.save();

        Bug61Parent wilma = new Bug61Parent();
        wilma.setName("Wilma");
        wilma.setAge(32);
        wilma.setlBug61Child(b2);
        wilma.setrBug61Child(b1);
        betty.setServerID(13);// not important now
        wilma.save();

        // get all the ladies
        List<Bug61Parent> ladies = new RushSearch().find(Bug61Parent.class);

        assertTrue(ladies.get(0).getlBug61Child().getSize().equals("A")
                && ladies.get(0).getrBug61Child().getSize().equals("B")
                && ladies.get(1).getlBug61Child().getSize().equals("B")
                && ladies.get(1).getrBug61Child().getSize().equals("A"));
    }

}
