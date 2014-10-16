package org.brightify.torch.test;

import org.brightify.torch.DatabaseEngine;
import org.brightify.torch.Key;
import org.brightify.torch.TorchService;
import org.brightify.torch.filter.BaseFilter;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Map;

import static org.brightify.torch.TorchService.torch;
import static org.brightify.torch.test.TestUtils.createTestObject;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * This is for testing the DatabaseEngine's filtering. Subclass it and implement {@link #prepareDatabaseEngine()} to
 * return an instance of the tested database engine.
 */
public abstract class AbstractFilterTest {

    private DatabaseEngine databaseEngine;

    private TestObject[] savedData;

    private TestObject testObject;
    private TestObject testObject1;
    private TestObject testObject2;
    private TestObject testObject3;

    @Before
    public void setUp() throws Exception {
        databaseEngine = prepareDatabaseEngine();

        TorchService.with(databaseEngine).register(TestObject.class);

        testObject = createTestObject();
        testObject.intField = -10;
        testObject.booleanField = false;

        testObject1 = createTestObject();
        testObject1.intField = 10;
        testObject1.booleanField = true;

        testObject2 = createTestObject();
        testObject2.intField = 100;
        testObject2.booleanField = false;

        testObject3 = createTestObject();
        testObject3.intField = 1000;
        testObject3.booleanField = true;

        savedData = new TestObject[] { testObject, testObject1, testObject2, testObject3 };

        Map<Key<TestObject>, TestObject> savedDataMap = torch().save().entities(savedData);
        assertThat(savedDataMap.size(), is(4));
    }

    @After
    public void tearDown() throws Exception {
        databaseEngine.wipe();
        databaseEngine = null;
        TorchService.forceUnload();
    }

    @Test
    public void equalToFilter() {
        filteredContains(TestObject$.intField.equalTo(10), testObject1);

        filteredContains(TestObject$.booleanField.equalTo(true), testObject1, testObject3);

        filteredContains(TestObject$.stringField.equalTo(testObject.stringField), testObject);
    }

    @Test
    public void notEqualToFilter() {
        filteredContains(TestObject$.intField.notEqualTo(-10), testObject1, testObject2, testObject3);

        filteredContains(TestObject$.booleanField.notEqualTo(true), testObject, testObject2);
    }

    @Test
    public void greaterThanFilter() {
        filteredContains(TestObject$.intField.greaterThan(10), testObject2, testObject3);
    }

    @Test
    public void greaterThanOrEqualToFilter() {
        filteredContains(TestObject$.intField.greaterThanOrEqualTo(10), testObject1, testObject2, testObject3);
    }

    @Test
    public void lessThanFilter() {
        filteredContains(TestObject$.intField.lessThan(10), testObject);
    }

    @Test
    public void lessThanOrEqualToFilter() {
        filteredContains(TestObject$.intField.lessThanOrEqualTo(10), testObject, testObject1);
    }

    @Test
    public void inFilter() {
        filteredContains(TestObject$.intField.in(10, 100), testObject1, testObject2);
    }

    @Test
    public void notInFilter() {
        filteredContains(TestObject$.intField.notIn(10, 100), testObject, testObject3);
    }

    @Test
    public void containsStringFilter() {
        String contained = testObject.stringField.substring(
                testObject.stringField.indexOf("-"), testObject.stringField.lastIndexOf("-"));

        filteredContains(TestObject$.stringField.contains(contained), testObject);
    }

    @Test
    public void startsWithStringFilter() {
        String first8Chars = testObject1.stringField.substring(0, 8);

        filteredContains(TestObject$.stringField.startsWith(first8Chars), testObject1);
    }

    @Test
    public void endsWithStringFilter() {
        String last8Chars = testObject2.stringField.substring(testObject2.stringField.length() - 8);

        filteredContains(TestObject$.stringField.endsWith(last8Chars), testObject2);
    }

    private List<TestObject> filteredContains(BaseFilter<?, ?> filter, TestObject... objects) {
        List<TestObject> loadedObjects = torch()
                .load().type(TestObject.class)
                .filter(filter)
                .list();

        assertThat(loadedObjects, contains(objects));

        return loadedObjects;
    }

    protected abstract DatabaseEngine prepareDatabaseEngine();

}
