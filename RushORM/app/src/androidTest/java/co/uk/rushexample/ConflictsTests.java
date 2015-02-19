package co.uk.rushexample;

import android.app.Application;
import android.test.ApplicationTestCase;

import java.util.List;

import co.uk.rushexample.testobjects.TestObject;
import co.uk.rushorm.android.RushAndroid;
import co.uk.rushorm.core.RushConflict;
import co.uk.rushorm.core.RushSearch;

/**
 * Created by Stuart on 17/02/15.
 */
public class ConflictsTests extends ApplicationTestCase<Application> {

    public ConflictsTests() {
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

    public void testNoConflicts() throws Exception {
        TestObject original = new TestObject();
        original.stringField = "version1";
        original.save();

        TestObject loadedOriginal = new RushSearch().findSingle(TestObject.class);
        loadedOriginal.stringField = "version2";

        List<RushConflict> conflicts = loadedOriginal.saveOnlyWithoutConflict();

        assertTrue(conflicts.size() == 0);
    }

    public void testConflicts() throws Exception {
        TestObject original = new TestObject();
        original.stringField = "version1";
        original.save();

        TestObject loadedOriginal = new RushSearch().findSingle(TestObject.class);
        loadedOriginal.stringField = "version2";
        loadedOriginal.save();

        List<RushConflict> conflicts = original.saveOnlyWithoutConflict();

        assertTrue(conflicts.size() == 1);
    }

}
