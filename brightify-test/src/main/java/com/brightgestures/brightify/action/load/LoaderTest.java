package com.brightgestures.brightify.action.load;

import android.test.AndroidTestCase;
import android.test.suitebuilder.annotation.MediumTest;
import android.util.Log;
import com.brightgestures.brightify.BrightifyService;
import com.brightgestures.brightify.Key;
import com.brightgestures.brightify.test.TestObject;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.brightgestures.brightify.BrightifyService.bfy;

/**
 * @author <a href="mailto:tadeas.kriz@brainwashstudio.com">Tadeas Kriz</a>
 */
public class LoaderTest extends AndroidTestCase {

    private Map<Key<TestObject>, TestObject> savedDataMap;
    private ArrayList<TestObject> savedData;

    private TestObject testObject;
    private TestObject testObject1;
    private TestObject testObject2;
    private TestObject testObject3;


    @Override
    protected void setUp() throws Exception {
        super.setUp();

        BrightifyService.with(getContext())
                .register(TestObject.class);

        BrightifyService.factory().forceOpenOrCreateDatabase();

        testObject = createTestObject();
        testObject.intField = -10;

        testObject1 = createTestObject();
        testObject1.intField = 10;

        testObject2 = createTestObject();
        testObject2.intField = 100;

        testObject3 = createTestObject();
        testObject3.intField = 1000;

        savedData = new ArrayList<TestObject>();
        savedData.add(testObject);
        savedData.add(testObject1);
        savedData.add(testObject2);
        savedData.add(testObject3);

        savedDataMap = bfy().save().entities(savedData).now();
        assertEquals(4, savedDataMap.size());
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();

        assertTrue(BrightifyService.factory().deleteDatabase());

        BrightifyService.forceUnload();
    }

    @MediumTest
    public void testLoadAllEntities() {
        List<TestObject> objects = bfy().load().type(TestObject.class).list();
        assertEquals(4, objects.size());
        assertEquals(savedData, objects);
    }

    @MediumTest
    public void testLoadFilteredEntities() {
        List<TestObject> objectsFiltered = bfy().load().type(TestObject.class)
                .filter("intField > ?", 10).list();
        assertEquals(2, objectsFiltered.size());

        List<TestObject> objectsFiltered1 = bfy().load().type(TestObject.class)
                .filter("intField NOT IN (?, ?)", 10, 100).list();
        assertEquals(2, objectsFiltered1.size());
    }

    @MediumTest
    public void testLoadOrderedDescending() {
        List<TestObject> objectsOrdered = bfy().load().type(TestObject.class)
                .orderBy("intField").desc().list();

        ArrayList<TestObject> saved = new ArrayList<TestObject>(savedData);

        Collections.reverse(saved);

        assertEquals(4, objectsOrdered.size());
        assertEquals(saved, objectsOrdered);
    }

    @MediumTest
    public void testLoadLimited() {
        List<TestObject> objectsLimited = bfy().load().type(TestObject.class)
                .limit(2).list();

        ArrayList<TestObject> saved = new ArrayList<TestObject>();
        saved.add(testObject);
        saved.add(testObject1);

        assertEquals(2, objectsLimited.size());
        assertEquals(saved, objectsLimited);

        List<TestObject> objectsLimitedWithOffset = bfy().load().type(TestObject.class)
                .limit(2).offset(1).list();

        saved.clear();
        saved.add(testObject1);
        saved.add(testObject2);

        assertEquals(2, objectsLimitedWithOffset.size());
        assertEquals(saved, objectsLimitedWithOffset);
    }

    private TestObject createTestObject() {
        TestObject testObject = new TestObject();

        testObject.id = null;
        testObject.stringField = UUID.randomUUID().toString();
        testObject.longField = 98765432123456789L;
        testObject.intField = 123456789;

        return testObject;
    }
}
