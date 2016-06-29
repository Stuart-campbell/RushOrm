package co.uk.rushorm.android;

import android.app.Application;
import android.content.Context;
import android.test.ApplicationTestCase;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import co.uk.rushorm.android.testobjects.MyClass;
import co.uk.rushorm.android.testobjects.SetupObject;
import co.uk.rushorm.android.testobjects.TestCustomColumn;
import co.uk.rushorm.core.InitializeListener;
import co.uk.rushorm.core.Rush;
import co.uk.rushorm.core.RushColumn;
import co.uk.rushorm.core.RushCore;
import co.uk.rushorm.core.RushSearch;
import co.uk.rushorm.core.RushStringSanitizer;
import co.uk.rushorm.android.testobjects.TestBase1;
import co.uk.rushorm.android.testobjects.TestBase2;
import co.uk.rushorm.android.testobjects.TestUpgrade1;
import co.uk.rushorm.android.testobjects.TestUpgrade2;
import co.uk.rushorm.android.testobjects.TestUpgrade3;
import co.uk.rushorm.android.testobjects.TestUpgrade4;
import co.uk.rushorm.android.testobjects.TestUpgrade5;

/**
 * Created by Stuart on 17/12/14.
 */
public class UpgradeTests extends ApplicationTestCase<Application> {

    public UpgradeTests() {
        super(Application.class);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        getContext().deleteDatabase("rush.db");
    }

    public void testUpgradeTableNameString() throws Exception {
        List<Class<? extends Rush>> classes = new ArrayList<>();
        classes.add(TestBase1.class);
        classes.add(TestBase2.class);
        initializeUpgrade(getContext(), classes);

        TestBase1 test1 = new TestBase1();
        test1.stringField = "Test1";
        test1.save();
        String id = test1.getId();

        List<Class<? extends Rush>> classes2 = new ArrayList<>();
        classes2.add(TestUpgrade1.class);
        classes2.add(TestBase2.class);
        initializeUpgrade(getContext(), classes2);

        TestUpgrade1 test2 = new RushSearch().whereId(id).findSingle(TestUpgrade1.class);
        assertTrue(test2.stringField.equals("Test1"));
    }

    public void testUpgradeTableNameDouble() throws Exception {
        List<Class<? extends Rush>> classes = new ArrayList<>();
        classes.add(TestBase1.class);
        classes.add(TestBase2.class);
        initializeUpgrade(getContext(), classes);

        TestBase1 test1 = new TestBase1();
        test1.doubleField = 10.05;
        test1.save();
        String id = test1.getId();

        List<Class<? extends Rush>> classes2 = new ArrayList<>();
        classes2.add(TestUpgrade1.class);
        classes2.add(TestBase2.class);
        initializeUpgrade(getContext(), classes2);

        TestUpgrade1 test2 = new RushSearch().whereId(id).findSingle(TestUpgrade1.class);
        assertTrue(test2.doubleField == 10.05);
    }

    public void testUpgradeTableNameInt() throws Exception {
        List<Class<? extends Rush>> classes = new ArrayList<>();
        classes.add(TestBase1.class);
        classes.add(TestBase2.class);
        initializeUpgrade(getContext(), classes);

        TestBase1 test1 = new TestBase1();
        test1.intField = 5;
        test1.save();
        String id = test1.getId();

        List<Class<? extends Rush>> classes2 = new ArrayList<>();
        classes2.add(TestUpgrade1.class);
        classes2.add(TestBase2.class);
        initializeUpgrade(getContext(), classes2);

        TestUpgrade1 test2 = new RushSearch().whereId(id).findSingle(TestUpgrade1.class);
        assertTrue(test2.intField == 5);
    }

    public void testUpgradeTableNameLong() throws Exception {
        List<Class<? extends Rush>> classes = new ArrayList<>();
        classes.add(TestBase1.class);
        classes.add(TestBase2.class);
        initializeUpgrade(getContext(), classes);

        TestBase1 test1 = new TestBase1();
        test1.longField = 1000000000;
        test1.save();
        String id = test1.getId();

        List<Class<? extends Rush>> classes2 = new ArrayList<>();
        classes2.add(TestUpgrade1.class);
        classes2.add(TestBase2.class);
        initializeUpgrade(getContext(), classes2);

        TestUpgrade1 test2 = new RushSearch().whereId(id).findSingle(TestUpgrade1.class);
        assertTrue(test2.longField == 1000000000);
    }

    public void testUpgradeTableNameShort() throws Exception {
        List<Class<? extends Rush>> classes = new ArrayList<>();
        classes.add(TestBase1.class);
        classes.add(TestBase2.class);
        initializeUpgrade(getContext(), classes);

        TestBase1 test1 = new TestBase1();
        test1.shortField = 1;
        test1.save();
        String id = test1.getId();

        List<Class<? extends Rush>> classes2 = new ArrayList<>();
        classes2.add(TestUpgrade1.class);
        classes2.add(TestBase2.class);
        initializeUpgrade(getContext(), classes2);

        TestUpgrade1 test2 = new RushSearch().whereId(id).findSingle(TestUpgrade1.class);
        assertTrue(test2.shortField == 1);
    }

    public void testUpgradeTableNameBoolean() throws Exception {
        List<Class<? extends Rush>> classes = new ArrayList<>();
        classes.add(TestBase1.class);
        classes.add(TestBase2.class);
        initializeUpgrade(getContext(), classes);

        TestBase1 test1 = new TestBase1();
        test1.booleanField = true;
        test1.save();
        String id = test1.getId();

        List<Class<? extends Rush>> classes2 = new ArrayList<>();
        classes2.add(TestUpgrade1.class);
        classes2.add(TestBase2.class);
        initializeUpgrade(getContext(), classes2);

        TestUpgrade1 test2 = new RushSearch().whereId(id).findSingle(TestUpgrade1.class);
        assertTrue(test2.booleanField);
    }

    public void testUpgradeTableNameChild() throws Exception {
        List<Class<? extends Rush>> classes = new ArrayList<>();
        classes.add(TestBase1.class);
        classes.add(TestBase2.class);
        initializeUpgrade(getContext(), classes);

        TestBase1 test1 = new TestBase1();
        test1.testBase2 = new TestBase2();
        test1.save();
        String id = test1.getId();
        String testBase2Id = test1.testBase2.getId();

        List<Class<? extends Rush>> classes2 = new ArrayList<>();
        classes2.add(TestUpgrade1.class);
        classes2.add(TestBase2.class);
        initializeUpgrade(getContext(), classes2);

        TestUpgrade1 test2 = new RushSearch().whereId(id).findSingle(TestUpgrade1.class);
        assertTrue(test2.testBase2.getId().equals(testBase2Id));
    }

    public void testUpgradeTableNameChildren() throws Exception {
        List<Class<? extends Rush>> classes = new ArrayList<>();
        classes.add(TestBase1.class);
        classes.add(TestBase2.class);
        initializeUpgrade(getContext(), classes);

        TestBase1 test1 = new TestBase1();
        test1.testBase2List = new ArrayList<>();
        test1.testBase2List.add(new TestBase2());
        test1.testBase2List.add(new TestBase2());
        test1.save();
        String id = test1.getId();

        List<Class<? extends Rush>> classes2 = new ArrayList<>();
        classes2.add(TestUpgrade1.class);
        classes2.add(TestBase2.class);
        initializeUpgrade(getContext(), classes2);

        TestUpgrade1 test2 = new RushSearch().whereId(id).findSingle(TestUpgrade1.class);
        assertTrue(test2.testBase2List.size() == 2);
    }

    public void testUpgradeNameString() throws Exception {
        List<Class<? extends Rush>> classes = new ArrayList<>();
        classes.add(TestBase1.class);
        classes.add(TestBase2.class);
        initializeUpgrade(getContext(), classes);

        TestBase1 test1 = new TestBase1();
        test1.stringField = "Test1";
        test1.save();
        String id = test1.getId();

        List<Class<? extends Rush>> classes2 = new ArrayList<>();
        classes2.add(TestUpgrade2.class);
        classes2.add(TestBase2.class);
        initializeUpgrade(getContext(), classes2);

        TestUpgrade2 test2 = new RushSearch().whereId(id).findSingle(TestUpgrade2.class);
        assertTrue(test2.stringFieldNamed.equals("Test1"));
    }

    public void testUpgradeNameDouble() throws Exception {
        List<Class<? extends Rush>> classes = new ArrayList<>();
        classes.add(TestBase1.class);
        classes.add(TestBase2.class);
        initializeUpgrade(getContext(), classes);

        TestBase1 test1 = new TestBase1();
        test1.doubleField = 10.05;
        test1.save();
        String id = test1.getId();

        List<Class<? extends Rush>> classes2 = new ArrayList<>();
        classes2.add(TestUpgrade2.class);
        classes2.add(TestBase2.class);
        initializeUpgrade(getContext(), classes2);

        TestUpgrade2 test2 = new RushSearch().whereId(id).findSingle(TestUpgrade2.class);
        assertTrue(test2.doubleFieldNamed == 10.05);
    }

    public void testUpgradeNameInt() throws Exception {
        List<Class<? extends Rush>> classes = new ArrayList<>();
        classes.add(TestBase1.class);
        classes.add(TestBase2.class);
        initializeUpgrade(getContext(), classes);

        TestBase1 test1 = new TestBase1();
        test1.intField = 5;
        test1.save();
        String id = test1.getId();

        List<Class<? extends Rush>> classes2 = new ArrayList<>();
        classes2.add(TestUpgrade2.class);
        classes2.add(TestBase2.class);
        initializeUpgrade(getContext(), classes2);

        TestUpgrade2 test2 = new RushSearch().whereId(id).findSingle(TestUpgrade2.class);
        assertTrue(test2.intFieldNamed == 5);
    }

    public void testUpgradeNameLong() throws Exception {
        List<Class<? extends Rush>> classes = new ArrayList<>();
        classes.add(TestBase1.class);
        classes.add(TestBase2.class);
        initializeUpgrade(getContext(), classes);

        TestBase1 test1 = new TestBase1();
        test1.longField = 1000000000;
        test1.save();
        String id = test1.getId();

        List<Class<? extends Rush>> classes2 = new ArrayList<>();
        classes2.add(TestUpgrade2.class);
        classes2.add(TestBase2.class);
        initializeUpgrade(getContext(), classes2);

        TestUpgrade2 test2 = new RushSearch().whereId(id).findSingle(TestUpgrade2.class);
        assertTrue(test2.longFieldNamed == 1000000000);
    }

    public void testUpgradeNameShort() throws Exception {
        List<Class<? extends Rush>> classes = new ArrayList<>();
        classes.add(TestBase1.class);
        classes.add(TestBase2.class);
        initializeUpgrade(getContext(), classes);

        TestBase1 test1 = new TestBase1();
        test1.shortField = 1;
        test1.save();
        String id = test1.getId();

        List<Class<? extends Rush>> classes2 = new ArrayList<>();
        classes2.add(TestUpgrade2.class);
        classes2.add(TestBase2.class);
        initializeUpgrade(getContext(), classes2);

        TestUpgrade2 test2 = new RushSearch().whereId(id).findSingle(TestUpgrade2.class);
        assertTrue(test2.shortFieldNamed == 1);
    }

    public void testUpgradeNameBoolean() throws Exception {
        List<Class<? extends Rush>> classes = new ArrayList<>();
        classes.add(TestBase1.class);
        classes.add(TestBase2.class);
        initializeUpgrade(getContext(), classes);

        TestBase1 test1 = new TestBase1();
        test1.booleanField = true;
        test1.save();
        String id = test1.getId();

        List<Class<? extends Rush>> classes2 = new ArrayList<>();
        classes2.add(TestUpgrade2.class);
        classes2.add(TestBase2.class);
        initializeUpgrade(getContext(), classes2);

        TestUpgrade2 test2 = new RushSearch().whereId(id).findSingle(TestUpgrade2.class);
        assertTrue(test2.booleanFieldNamed);
    }

    public void testUpgradeNameChild() throws Exception {
        List<Class<? extends Rush>> classes = new ArrayList<>();
        classes.add(TestBase1.class);
        classes.add(TestBase2.class);
        initializeUpgrade(getContext(), classes);

        TestBase1 test1 = new TestBase1();
        test1.testBase2 = new TestBase2();
        test1.save();
        String id = test1.getId();
        String testBase2Id = test1.testBase2.getId();

        List<Class<? extends Rush>> classes2 = new ArrayList<>();
        classes2.add(TestUpgrade2.class);
        classes2.add(TestBase2.class);
        initializeUpgrade(getContext(), classes2);

        TestUpgrade2 test2 = new RushSearch().whereId(id).findSingle(TestUpgrade2.class);
        assertTrue(test2.testBase2Names.getId().equals(testBase2Id));
    }

    public void testUpgradeNameChildren() throws Exception {
        List<Class<? extends Rush>> classes = new ArrayList<>();
        classes.add(TestBase1.class);
        classes.add(TestBase2.class);
        initializeUpgrade(getContext(), classes);

        TestBase1 test1 = new TestBase1();
        test1.testBase2List = new ArrayList<>();
        test1.testBase2List.add(new TestBase2());
        test1.testBase2List.add(new TestBase2());
        test1.save();
        String id = test1.getId();

        List<Class<? extends Rush>> classes2 = new ArrayList<>();
        classes2.add(TestUpgrade2.class);
        classes2.add(TestBase2.class);
        initializeUpgrade(getContext(), classes2);

        TestUpgrade2 test2 = new RushSearch().whereId(id).findSingle(TestUpgrade2.class);
        assertTrue(test2.testBase2ListNamed.size() == 2);
    }

    public void testUpgradeChildType() throws Exception {
        List<Class<? extends Rush>> classes = new ArrayList<>();
        classes.add(TestBase1.class);
        classes.add(TestBase2.class);
        initializeUpgrade(getContext(), classes);

        TestBase1 test1 = new TestBase1();
        test1.testBase2 = new TestBase2();
        test1.save();
        String id = test1.getId();
        String testBase2Id = test1.testBase2.getId();

        List<Class<? extends Rush>> classes2 = new ArrayList<>();
        classes2.add(TestUpgrade3.class);
        classes2.add(TestUpgrade5.class);
        initializeUpgrade(getContext(), classes2);

        TestUpgrade3 test2 = new RushSearch().whereId(id).findSingle(TestUpgrade3.class);
        assertTrue(test2.testBase2.getId().equals(testBase2Id));
    }

    public void testUpgradeChildrenType() throws Exception {
        List<Class<? extends Rush>> classes = new ArrayList<>();
        classes.add(TestBase1.class);
        classes.add(TestBase2.class);
        initializeUpgrade(getContext(), classes);

        TestBase1 test1 = new TestBase1();
        test1.testBase2List = new ArrayList<>();
        test1.testBase2List.add(new TestBase2());
        test1.testBase2List.add(new TestBase2());
        test1.save();
        String id = test1.getId();

        List<Class<? extends Rush>> classes2 = new ArrayList<>();
        classes2.add(TestUpgrade3.class);
        classes2.add(TestUpgrade5.class);
        initializeUpgrade(getContext(), classes2);

        TestUpgrade3 test2 = new RushSearch().whereId(id).findSingle(TestUpgrade3.class);
        assertTrue(test2.testBase2List.size() == 2);
    }

    public void testUpgradeChildTypeAndName() throws Exception {
        List<Class<? extends Rush>> classes = new ArrayList<>();
        classes.add(TestBase1.class);
        classes.add(TestBase2.class);
        initializeUpgrade(getContext(), classes);

        TestBase1 test1 = new TestBase1();
        test1.testBase2 = new TestBase2();
        test1.save();
        String id = test1.getId();
        String testBase2Id = test1.testBase2.getId();

        List<Class<? extends Rush>> classes2 = new ArrayList<>();
        classes2.add(TestUpgrade4.class);
        classes2.add(TestUpgrade5.class);
        initializeUpgrade(getContext(), classes2);

        TestUpgrade4 test2 = new RushSearch().whereId(id).findSingle(TestUpgrade4.class);
        assertTrue(test2.testUpgrade5.getId().equals(testBase2Id));
    }

    public void testUpgradeChildrenTypeAndName() throws Exception {
        List<Class<? extends Rush>> classes = new ArrayList<>();
        classes.add(TestBase1.class);
        classes.add(TestBase2.class);
        initializeUpgrade(getContext(), classes);

        TestBase1 test1 = new TestBase1();
        test1.testBase2List = new ArrayList<>();
        test1.testBase2List.add(new TestBase2());
        test1.testBase2List.add(new TestBase2());
        test1.save();
        String id = test1.getId();

        List<Class<? extends Rush>> classes2 = new ArrayList<>();
        classes2.add(TestUpgrade4.class);
        classes2.add(TestUpgrade5.class);
        initializeUpgrade(getContext(), classes2);

        TestUpgrade4 test2 = new RushSearch().whereId(id).findSingle(TestUpgrade4.class);
        assertTrue(test2.testUpgrade5List.size() == 2);
    }

    public static void initializeUpgrade(Context context, final List<Class<? extends Rush>> classes) throws InterruptedException {

        classes.add(SetupObject.class);
        final CountDownLatch latch = new CountDownLatch(1);

        AndroidInitializeConfig androidInitializeConfig = new AndroidInitializeConfig(context);
        androidInitializeConfig.setClasses(classes);
        androidInitializeConfig.setInitializeListener(new InitializeListener() {
            @Override
            public void initialized(boolean firstRun) {
                latch.countDown();
            }
        });

        RushCore.initialize(androidInitializeConfig);

        latch.await(10, TimeUnit.SECONDS);
        new SetupObject().save();
    }

    public void testCustomColumn() throws Exception {
        List<RushColumn> columns = new ArrayList<>();
        columns.add(new MyColumn());

        Utils.setUp(getContext().getApplicationContext(), columns);

        TestCustomColumn object = new TestCustomColumn();
        object.myClass = new MyClass();
        object.myClass.name = "Hello";
        object.save();
        String id = object.getId();

        TestCustomColumn loadedObject = new RushSearch().whereId(id).findSingle(TestCustomColumn.class);
        assertTrue(loadedObject.myClass.name.equalsIgnoreCase("Hello"));
    }

    private class MyColumn implements RushColumn<MyClass> {
        @Override
        public String sqlColumnType() {
            return "varchar(255)";
        }

        @Override
        public String serialize(MyClass object, RushStringSanitizer stringSanitizer) {
            return stringSanitizer.sanitize(object.name);
        }

        @Override
        public MyClass deserialize(String value) {
            MyClass myClass = new MyClass();
            myClass.name = value;
            return myClass;
        }

        @Override
        public Class[] classesColumnSupports() {
            return new Class[]{MyClass.class};
        }
    }
}
