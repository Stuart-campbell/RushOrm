package co.uk.rushorm.android;

import android.app.Application;
import android.test.ApplicationTestCase;

import java.util.ArrayList;
import java.util.List;

import co.uk.rushorm.core.RushCore;
import co.uk.rushorm.core.RushSearch;
import co.uk.rushorm.android.testobjects.TestChildObject;
import co.uk.rushorm.android.testobjects.TestObject;

/**
 * Created by Stuart on 15/12/14.
 */
public class SearchTests extends ApplicationTestCase<Application> {

    public SearchTests() {
        super(Application.class);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        Utils.setUp(getContext());
    }

    public void testFindByString() throws Exception {

        TestObject testObject = new TestObject();
        testObject.stringField = "string";
        testObject.save();

        List<TestObject> results = new RushSearch().whereEqual("stringField", "string").find(TestObject.class);

        assertTrue(results.size() == 1);
    }

    public void testFindByStringTwo() throws Exception {

        TestObject testObject = new TestObject();
        testObject.stringField = "string";
        testObject.save();

        TestObject testObject2 = new TestObject();
        testObject2.stringField = "string";
        testObject2.save();

        List<TestObject> results = new RushSearch().whereEqual("stringField", "string").find(TestObject.class);

        assertTrue(results.size() == 2);
    }

    public void testFindByInt() throws Exception {

        TestObject testObject = new TestObject();
        testObject.intField = 5;
        testObject.save();

        List<TestObject> results = new RushSearch().whereEqual("intField", 5).find(TestObject.class);

        assertTrue(results.size() == 1);
    }
    public void testFindByIntTwo() throws Exception {

        TestObject testObject = new TestObject();
        testObject.intField = 5;
        testObject.save();

        TestObject testObject2 = new TestObject();
        testObject2.intField = 5;
        testObject2.save();

        List<TestObject> results = new RushSearch().whereEqual("intField", 5).find(TestObject.class);

        assertTrue(results.size() == 2);
    }

    public void testFindByDouble() throws Exception {

        TestObject testObject = new TestObject();
        testObject.doubleField = 5.1234;
        testObject.save();

        List<TestObject> results = new RushSearch().whereEqual("doubleField", 5.1234).find(TestObject.class);

        assertTrue(results.size() == 1);
    }

    public void testFindByDoubleTwo() throws Exception {

        TestObject testObject = new TestObject();
        testObject.doubleField = 5.1234;
        testObject.save();

        TestObject testObject2 = new TestObject();
        testObject2.doubleField = 5.1234;
        testObject2.save();

        List<TestObject> results = new RushSearch().whereEqual("doubleField", 5.1234).find(TestObject.class);

        assertTrue(results.size() == 2);
    }

    public void testFindByShort() throws Exception {

        TestObject testObject = new TestObject();
        testObject.shortField = 3;
        testObject.save();

        List<TestObject> results = new RushSearch().whereEqual("shortField", (short)3).find(TestObject.class);

        assertTrue(results.size() == 1);
    }

    public void testFindByChild() throws Exception {

        TestChildObject testChildObject = new TestChildObject();

        TestObject testObject = new TestObject();
        testObject.childObject = testChildObject;
        testObject.save();

        List<TestObject> results = new RushSearch().whereEqual("childObject", testChildObject).find(TestObject.class);

        assertTrue(results.size() == 1);
    }

    public void testFindByChildTwo() throws Exception {

        TestChildObject testChildObject = new TestChildObject();

        TestObject testObject = new TestObject();
        testObject.childObject = testChildObject;
        testObject.save();

        TestObject testObject2 = new TestObject();
        testObject2.childObject = testChildObject;
        testObject2.save();

        List<TestObject> results = new RushSearch().whereEqual("childObject", testChildObject).find(TestObject.class);

        assertTrue(results.size() == 2);
    }

    public void testFindByChildInList() throws Exception {

        TestChildObject testChildObject = new TestChildObject();

        TestObject testObject = new TestObject();
        testObject.children = new ArrayList<>();
        testObject.children.add(testChildObject);
        testObject.save();

        List<TestObject> results = new RushSearch().whereEqual("children", testChildObject).find(TestObject.class);

        assertTrue(results.size() == 1);
    }

    public void testFindByStringAndInt() throws Exception {

        TestObject testObject = new TestObject();
        testObject.stringField = "string";
        testObject.intField = 5;
        testObject.save();

        List<TestObject> results = new RushSearch()
                .whereEqual("stringField", "string")
                .and()
                .whereEqual("intField", 5)
                .find(TestObject.class);

        assertTrue(results.size() == 1);
    }

    public void testFindByStringAndIntWrongInt() throws Exception {

        TestObject testObject = new TestObject();
        testObject.stringField = "string";
        testObject.save();

        List<TestObject> results = new RushSearch()
                .whereEqual("stringField", "string")
                .and()
                .whereEqual("intField", 5)
                .find(TestObject.class);

        assertTrue(results.isEmpty());
    }

    public void testFindByStringAndIntWrongString() throws Exception {

        TestObject testObject = new TestObject();
        testObject.intField = 5;
        testObject.save();

        List<TestObject> results = new RushSearch()
                .whereEqual("stringField", "string")
                .and()
                .whereEqual("intField", 5)
                .find(TestObject.class);

        assertTrue(results.isEmpty());
    }

    public void testFindByStringOrIntWrongInt() throws Exception {

        TestObject testObject = new TestObject();
        testObject.stringField = "string";
        testObject.save();

        List<TestObject> results = new RushSearch()
                .whereEqual("stringField", "string")
                .or()
                .whereEqual("intField", 5)
                .find(TestObject.class);

        assertTrue(results.size() == 1);
    }

    public void testFindByStringOrIntWrongString() throws Exception {

        TestObject testObject = new TestObject();
        testObject.intField = 5;
        testObject.save();

        List<TestObject> results = new RushSearch()
                .whereEqual("stringField", "string")
                .or()
                .whereEqual("intField", 5)
                .find(TestObject.class);

        assertTrue(results.size() == 1);
    }

    public void testFindByIntAndDoubleOrStringWrongString() throws Exception {

        TestObject testObject = new TestObject();
        testObject.intField = 5;
        testObject.booleanField = true;
        testObject.stringField = "string";
        testObject.save();

        List<TestObject> results = new RushSearch()
                .whereEqual("stringField", "string")
                .and()
                .startGroup()
                .whereEqual("intField", 4)
                .or()
                .whereEqual("booleanField", true)
                .endGroup()
                .find(TestObject.class);

        assertTrue(results.size() == 1);
    }

    public void testOrderDescByInt() throws Exception {

        TestObject testObject = new TestObject();
        testObject.intField = 1;
        testObject.save();

        TestObject testObject1 = new TestObject();
        testObject1.intField = 2;
        testObject1.save();

        List<TestObject> results = new RushSearch()
                .orderDesc("intField")
                .find(TestObject.class);


        TestObject firstChild = results.get(0);
        assertTrue(firstChild.intField == 2);
    }

    public void testOrderAscByInt() throws Exception {

        TestObject testObject = new TestObject();
        testObject.intField = 1;
        testObject.save();

        TestObject testObject1 = new TestObject();
        testObject1.intField = 2;
        testObject1.save();

        List<TestObject> results = new RushSearch()
                .orderAsc("intField")
                .find(TestObject.class);


        TestObject firstChild = results.get(0);
        assertTrue(firstChild.intField == 1);
    }

    public void testExample1() throws Exception {

        TestObject testObject = new TestObject();
        testObject.intField = 1;
        testObject.save();

        TestObject testObject1 = new TestObject();
        testObject1.intField = 2;
        testObject1.stringField = "Hello world";
        testObject1.save();

        TestObject testObject2 = new TestObject();
        testObject2.intField = 6;
        testObject2.stringField = "Hello world";
        testObject2.save();

        TestObject testObject3 = new TestObject();
        testObject3.intField = 8;
        testObject3.save();

        String id = testObject.getId();

        // Get all objects with id 1 or stringField "Hello world" and intField greater than 5 order ascending by intField
        List<TestObject> objects = new RushSearch()
                .whereId(id)
                .or()
                .startGroup()
                .whereEqual("stringField", "Hello world")
                .and()
                .whereGreaterThan("intField", 5)
                .endGroup()
                .orderDesc("intField")
                .find(TestObject.class);

        TestObject firstChild = objects.get(0);
        assertTrue(firstChild.intField == 6);
    }

    public void testExample2() throws Exception {

        List<TestObject> objects = new ArrayList<>();

        TestObject testObject = new TestObject();
        testObject.intField = 1;
        objects.add(testObject);

        TestObject testObject1 = new TestObject();
        testObject1.intField = 2;
        testObject1.stringField = "Hello world";
        objects.add(testObject1);

        TestObject testObject2 = new TestObject();
        testObject2.intField = 6;
        testObject2.stringField = "Hello world";
        objects.add(testObject2);

        TestObject testObject3 = new TestObject();
        testObject3.intField = 8;
        objects.add(testObject3);

        RushCore.getInstance().save(objects);

        String id = testObject.getId();

        // Get all objects with id 1 or stringField "Hello world" and intField greater than 5 order ascending by intField
        List<TestObject> loadedObjects = new RushSearch()
                .whereId(id)
                .or()
                .startGroup()
                .whereEqual("stringField", "Hello world")
                .and()
                .whereGreaterThan("intField", 5)
                .endGroup()
                .orderDesc("intField")
                .find(TestObject.class);

        TestObject firstChild = loadedObjects.get(0);
        assertTrue(firstChild.intField == 6);
    }

    public void testWhereChildOf() throws Exception {

        TestObject testObject = new TestObject();
        testObject.children = new ArrayList<>();
        testObject.children.add(new TestChildObject());
        testObject.save();

        List<TestChildObject> testChildObject = new RushSearch().whereChildOf(testObject, "children").find(TestChildObject.class);
        assertTrue(testChildObject.size() == 1);

    }

    public void testWhereChildOf2() throws Exception {

        TestObject testObject = new TestObject();
        testObject.children = new ArrayList<>();
        testObject.children.add(new TestChildObject());
        testObject.save();

        new TestChildObject().save();

        List<TestChildObject> testChildObject = new RushSearch().whereChildOf(testObject, "children").find(TestChildObject.class);
        assertTrue(testChildObject.size() == 1);

    }

    public void testLimit() throws Exception {

        List<TestObject> list = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            TestObject testObject = new TestObject();
            testObject.intField = i;
            list.add(testObject);
        }

        RushCore.getInstance().save(list);

        List<TestObject> testObjects = new RushSearch().limit(100).find(TestObject.class);
        assertTrue(testObjects.size() == 100);
    }

    public void testOffsetLimit() throws Exception {

        List<TestObject> list = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            TestObject testObject = new TestObject();
            testObject.intField = i;
            list.add(testObject);
        }

        RushCore.getInstance().save(list);

        List<TestObject> testObjects = new RushSearch().limit(100).offset(100).find(TestObject.class);
        assertTrue(testObjects.get(0).intField == 100 && testObjects.size() == 100);
    }

    public void testWhereIn() throws Exception {

        List<TestObject> list = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            TestObject testObject = new TestObject();
            testObject.intField = i;
            list.add(testObject);
        }

        RushCore.getInstance().save(list);

        List<String> ints = new ArrayList<>();
        for (int i = 0; i < 10; i += 2) {
            ints.add(Integer.toString(i));

        }

        List<TestObject> testObjects = new RushSearch().whereIN("intField", ints).find(TestObject.class);
        assertTrue(testObjects.size() == 5);
    }

    public void testWhereInEmpty() throws Exception {

        List<TestObject> list = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            TestObject testObject = new TestObject();
            list.add(testObject);
        }

        RushCore.getInstance().save(list);

        List<String> strings = new ArrayList<>();
        List<TestObject> testObjects = new RushSearch().whereIN("stringField", strings).find(TestObject.class);
        assertTrue(testObjects.size() == 0);
    }


    public void testGroupBy() throws Exception {

        List<TestObject> list = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            TestObject testObject = new TestObject();
            testObject.intField = 0;
            list.add(testObject);
        }

        RushCore.getInstance().save(list);

        List<TestObject> testObjects = new RushSearch().groupBy("intField").find(TestObject.class);
        assertTrue(testObjects.size() == 1);
    }

    public void testGroupByTest2() throws Exception {

        List<TestObject> list = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            TestObject testObject = new TestObject();
            testObject.intField = 0;
            if(i % 2 == 0) {
                testObject.stringField = "even";
            } else {
                testObject.stringField = "odd";
            }
            list.add(testObject);
        }

        RushCore.getInstance().save(list);

        List<TestObject> testObjects = new RushSearch().groupBy("intField").groupBy("stringField").find(TestObject.class);
        assertTrue(testObjects.size() == 2);
    }

    public void testWhereIsNull() throws Exception {

        TestObject testObject = new TestObject();
        testObject.save();

        List<TestObject> results = new RushSearch().whereIsNull("stringField").find(TestObject.class);

        assertTrue(results.size() == 1);
    }

    public void testWhereIsNull2() throws Exception {

        TestObject testObject = new TestObject();
        testObject.stringField = "not null string";
        testObject.save();

        List<TestObject> results = new RushSearch().whereIsNull("stringField").find(TestObject.class);

        assertTrue(results.isEmpty());
    }

    public void testWhereIsNotNull() throws Exception {

        TestObject testObject = new TestObject();
        testObject.save();

        List<TestObject> results = new RushSearch().whereIsNotNull("stringField").find(TestObject.class);

        assertTrue(results.isEmpty());
    }

    public void testWhereIsNotNull2() throws Exception {

        TestObject testObject = new TestObject();
        testObject.stringField = "not null string";
        testObject.save();

        List<TestObject> results = new RushSearch().whereIsNotNull("stringField").find(TestObject.class);

        assertTrue(results.size() == 1);
    }

    public void testWhereNotEqualString() throws Exception {

        TestObject testObject = new TestObject();
        testObject.stringField = "string1";
        testObject.save();

        List<TestObject> results = new RushSearch().whereNotEqual("stringField", "string1").find(TestObject.class);

        assertTrue(results.isEmpty());
    }

    public void testWhereNotEqualString2() throws Exception {

        TestObject testObject = new TestObject();
        testObject.stringField = "string1";
        testObject.save();

        List<TestObject> results = new RushSearch().whereNotEqual("stringField", "string2").find(TestObject.class);

        assertTrue(results.size() == 1);
    }

    public void testWhereNotEqualInt() throws Exception {

        TestObject testObject = new TestObject();
        testObject.intField = 1;
        testObject.save();

        List<TestObject> results = new RushSearch().whereNotEqual("intField", 1).find(TestObject.class);

        assertTrue(results.isEmpty());
    }

    public void testWhereNotEqualInt2() throws Exception {

        TestObject testObject = new TestObject();
        testObject.intField = 1;
        testObject.save();

        List<TestObject> results = new RushSearch().whereNotEqual("intField", 2).find(TestObject.class);

        assertTrue(results.size() == 1);
    }

    public void testWhereNotEqualDouble() throws Exception {

        TestObject testObject = new TestObject();
        testObject.doubleField = 1.001;
        testObject.save();

        List<TestObject> results = new RushSearch().whereNotEqual("doubleField", 1.001).find(TestObject.class);

        assertTrue(results.isEmpty());
    }

    public void testWhereNotEqualDouble2() throws Exception {

        TestObject testObject = new TestObject();
        testObject.doubleField = 1.001;
        testObject.save();

        List<TestObject> results = new RushSearch().whereNotEqual("doubleField", 1.002).find(TestObject.class);

        assertTrue(results.size() == 1);
    }

    public void testWhereNotEqualShort() throws Exception {

        TestObject testObject = new TestObject();
        testObject.shortField = 2;
        testObject.save();

        List<TestObject> results = new RushSearch().whereNotEqual("shortField", (short)2).find(TestObject.class);

        assertTrue(results.isEmpty());
    }

    public void testWhereNotEqualShort2() throws Exception {

        TestObject testObject = new TestObject();
        testObject.shortField = 2;
        testObject.save();

        List<TestObject> results = new RushSearch().whereNotEqual("shortField", (short)3).find(TestObject.class);

        assertTrue(results.size() == 1);
    }

    public void testLessThanInt() throws Exception {

        TestObject testObject = new TestObject();
        testObject.intField = 2;
        testObject.save();

        List<TestObject> results = new RushSearch().whereLessThan("intField", 2).find(TestObject.class);

        assertTrue(results.isEmpty());
    }

    public void testLessThanInt2() throws Exception {

        TestObject testObject = new TestObject();
        testObject.intField = 2;
        testObject.save();

        List<TestObject> results = new RushSearch().whereLessThan("intField", 3).find(TestObject.class);

        assertTrue(results.size() == 1);
    }

    public void testGreaterThanInt() throws Exception {

        TestObject testObject = new TestObject();
        testObject.intField = 2;
        testObject.save();

        List<TestObject> results = new RushSearch().whereGreaterThan("intField", 2).find(TestObject.class);

        assertTrue(results.isEmpty());
    }

    public void testGreaterThanInt2() throws Exception {

        TestObject testObject = new TestObject();
        testObject.intField = 2;
        testObject.save();

        List<TestObject> results = new RushSearch().whereGreaterThan("intField", 1).find(TestObject.class);

        assertTrue(results.size() == 1);
    }

    public void testLessThanDouble() throws Exception {

        TestObject testObject = new TestObject();
        testObject.doubleField = 2.5;
        testObject.save();

        List<TestObject> results = new RushSearch().whereLessThan("doubleField", 2.5).find(TestObject.class);

        assertTrue(results.isEmpty());
    }

    public void testLessThanDouble2() throws Exception {

        TestObject testObject = new TestObject();
        testObject.doubleField = 2.5;
        testObject.save();

        List<TestObject> results = new RushSearch().whereLessThan("doubleField", 2.6).find(TestObject.class);

        assertTrue(results.size() == 1);
    }

    public void testGreaterThanDouble() throws Exception {

        TestObject testObject = new TestObject();
        testObject.doubleField = 2.5;
        testObject.save();

        List<TestObject> results = new RushSearch().whereGreaterThan("doubleField", 2.5).find(TestObject.class);

        assertTrue(results.isEmpty());
    }

    public void testGreaterThanDouble2() throws Exception {

        TestObject testObject = new TestObject();
        testObject.doubleField = 2.5;
        testObject.save();

        List<TestObject> results = new RushSearch().whereGreaterThan("doubleField", 2.4).find(TestObject.class);

        assertTrue(results.size() == 1);
    }

    public void testLessThanShort() throws Exception {

        TestObject testObject = new TestObject();
        testObject.shortField = 2;
        testObject.save();

        List<TestObject> results = new RushSearch().whereLessThan("shortField", (short)2).find(TestObject.class);

        assertTrue(results.isEmpty());
    }

    public void testLessThanShort2() throws Exception {

        TestObject testObject = new TestObject();
        testObject.shortField = 2;
        testObject.save();

        List<TestObject> results = new RushSearch().whereLessThan("shortField", (short)3).find(TestObject.class);

        assertTrue(results.size() == 1);
    }

    public void testGreaterThanShort() throws Exception {

        TestObject testObject = new TestObject();
        testObject.shortField = 2;
        testObject.save();

        List<TestObject> results = new RushSearch().whereGreaterThan("shortField", (short)2).find(TestObject.class);

        assertTrue(results.isEmpty());
    }

    public void testGreaterThanShort2() throws Exception {

        TestObject testObject = new TestObject();
        testObject.shortField = 2;
        testObject.save();

        List<TestObject> results = new RushSearch().whereGreaterThan("shortField", (short)1).find(TestObject.class);

        assertTrue(results.size() == 1);
    }

    // This is a bit of a stupid test, if it breaks no big worry,
    // it should only emphasise that toString should produce valid json
    public void testToString() throws Exception {

        RushSearch rushSearch = new RushSearch()
                .whereId("testId")
                .or()
                .startGroup()
                .whereEqual("stringField", "Hello world")
                .and()
                .whereGreaterThan("intField", 5)
                .endGroup()
                .orderDesc("intField");

        String json = rushSearch.toString();
        assertEquals(json, "{\"limit\":null,\"offset\":null,\"order\":[{\"field\":\"intField\",\"order\":\"DESC\"}],\"where\":[{\"field\":\"rush_id\",\"modifier\":\"=\",\"value\":\"'testId'\",\"type\":\"whereStatement\"},{\"element\":\" OR \",\"type\":\"where\"},{\"element\":\"(\",\"type\":\"where\"},{\"field\":\"stringField\",\"modifier\":\"=\",\"value\":\"'Hello world'\",\"type\":\"whereStatement\"},{\"element\":\" AND \",\"type\":\"where\"},{\"field\":\"intField\",\"modifier\":\">\",\"value\":\"5\",\"type\":\"whereStatement\"},{\"element\":\")\",\"type\":\"where\"}]}");

    }

}
