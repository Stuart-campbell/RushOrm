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
        Utils.setUp(getContext());
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

    public void testUnresolvedConflicts() throws Exception {
        TestObject original = new TestObject();
        original.stringField = "version1";
        original.save();

        TestObject loadedOriginal = new RushSearch().findSingle(TestObject.class);
        loadedOriginal.stringField = "version2";
        loadedOriginal.save();

        List<RushConflict> conflicts = original.saveOnlyWithoutConflict();
        List<RushConflict> conflicts2 = original.saveOnlyWithoutConflict();

        assertTrue(conflicts2.size() == 1);
    }

    public void testResolvedConflicts() throws Exception {
        TestObject original = new TestObject();
        original.stringField = "version1";
        original.save();

        TestObject loadedOriginal = new RushSearch().findSingle(TestObject.class);
        loadedOriginal.stringField = "version2";
        loadedOriginal.save();

        List<RushConflict> conflicts = original.saveOnlyWithoutConflict();
        conflicts.get(0).resolve();
        
        List<RushConflict> conflicts2 = original.saveOnlyWithoutConflict();
        assertTrue(conflicts2.size() == 0);
    }

    public void testResolveWithBackgroundSaveConflicts() throws Exception {
        TestObject original = new TestObject();
        original.stringField = "version1";
        original.save();

        TestObject loadedOriginal = new RushSearch().findSingle(TestObject.class);
        loadedOriginal.stringField = "version2";
        loadedOriginal.save();

        List<RushConflict> conflicts = original.saveOnlyWithoutConflict();
        conflicts.get(0).resolve();

        loadedOriginal.stringField = "version3";
        loadedOriginal.save();
        
        List<RushConflict> conflicts2 = original.saveOnlyWithoutConflict();
        assertTrue(conflicts2.size() == 1);
    }
    
}
