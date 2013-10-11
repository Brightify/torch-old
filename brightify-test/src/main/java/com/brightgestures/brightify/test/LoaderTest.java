package com.brightgestures.brightify.test;

import android.test.AndroidTestCase;
import android.test.suitebuilder.annotation.SmallTest;
import com.brightgestures.brightify.Brightify;
import com.brightgestures.brightify.BrightifyService;
import com.brightgestures.brightify.Key;
import com.brightgestures.brightify.action.load.ListLoader;

import java.util.List;
import java.util.Map;

import static com.brightgestures.brightify.BrightifyService.bfy;
import static com.brightgestures.brightify.BrightifyService.factory;

/**
 * @author <a href="mailto:tadeas.kriz@brainwashstudio.com">Tadeas Kriz</a>
 */
public class LoaderTest extends AndroidTestCase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        BrightifyService.load(getContext());

        assertFalse(BrightifyService.isDatabaseCreated(getContext()));

        factory().register(ActivityTestObject.class);

        factory().createDatabase(getContext());

        assertTrue(BrightifyService.isDatabaseCreated(getContext()));
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();

        assertTrue(BrightifyService.isDatabaseCreated(getContext()));

        factory().deleteDatabase(getContext());

        assertFalse(BrightifyService.isDatabaseCreated(getContext()));

        BrightifyService.unload(getContext());
    }

    @SmallTest
    public void testLoader() {


        ActivityTestObject testObject = createTestObject();
        testObject.intField = -10;
        ActivityTestObject testObject1 = createTestObject();
        testObject1.intField = 10;
        ActivityTestObject testObject2 = createTestObject();
        testObject2.intField = 100;
        ActivityTestObject testObject3 = createTestObject();
        testObject3.intField = 1000;

        Map<Key<ActivityTestObject>, ActivityTestObject> saved =
                bfy(getContext()).save().entities(testObject, testObject1, testObject2, testObject3).now();
        assertEquals(4, saved.size());

        List<ActivityTestObject> objects = bfy(getContext()).load().type(ActivityTestObject.class).list();
        assertEquals(4, objects.size());

        List<ActivityTestObject> objectsFiltered = bfy(getContext()).load().type(ActivityTestObject.class)
                .filter("intField>?", 10).list();
        assertEquals(2, objectsFiltered.size());

        List<ActivityTestObject> objectsFiltered1 = bfy(getContext()).load().type(ActivityTestObject.class)
                .filter("intField NOT IN (?, ?)", 10, 100).list();
        assertEquals(2, objectsFiltered1.size());
    }

    private ActivityTestObject createTestObject() {
        ActivityTestObject testObject = new ActivityTestObject();

        testObject.id = null;
        testObject.stringField = "hello from sunny los angeles!";
        testObject.longField = 98765432123456789L;
        testObject.intField = 123456789;

        return testObject;
    }

}
