package org.brightify.torch.android.action.load;

import android.test.suitebuilder.annotation.MediumTest;
import org.brightify.torch.BaseActivityInstrumentationTestCase2;
import org.brightify.torch.TorchService;
import org.brightify.torch.android.AndroidSQLiteEngine;
import org.brightify.torch.test.MainTestActivity;
import org.brightify.torch.test.TestObject;
import org.brightify.torch.test.TestObject$;
import org.brightify.torch.util.Constants;
import org.brightify.torch.util.async.Callback;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.brightify.torch.TorchService.torch;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public class LoaderTest extends BaseActivityInstrumentationTestCase2<MainTestActivity> {

    private TestObject[] savedData;

    private TestObject testObject;
    private TestObject testObject1;
    private TestObject testObject2;
    private TestObject testObject3;

    private AndroidSQLiteEngine engine;

    public LoaderTest() {
        super(MainTestActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        engine = new AndroidSQLiteEngine(getActivity(), Constants.DEFAULT_DATABASE_NAME, null);

        TorchService.with(engine).register(TestObject.class);

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

        Map<TestObject, Long> savedDataMap = torch().save().entities(savedData);
        assertEquals(4, savedDataMap.size());
    }

    @Override
    protected void tearDown() throws Exception {
        engine.wipe();
        engine = null;
        TorchService.forceUnload();
        super.tearDown();
    }

    private TestObject createTestObject() {
        TestObject testObject = new TestObject();

        testObject.stringField = UUID.randomUUID().toString();
        testObject.longField = 98765432123456789L;
        testObject.intField = 123456789;

        return testObject;
    }

    @MediumTest
    public void testLoadById() {
        TestObject object = torch().load().type(TestObject.class).id(testObject.id);
        assertEquals(testObject, object);

        List<TestObject> objects = torch().load().type(TestObject.class).ids(testObject1.id, testObject2.id);
        assertEquals(2, objects.size());
        assertEquals(testObject1, objects.get(0));
        assertEquals(testObject2, objects.get(1));

        objects = torch().load().type(TestObject.class).ids(Arrays.asList(testObject2.id, testObject3.id));
        assertEquals(2, objects.size());
        assertEquals(testObject2, objects.get(0));
        assertEquals(testObject3, objects.get(1));
    }

    @MediumTest
    public void testLoadAllEntities() {
        List<TestObject> objects = torch().load().type(TestObject.class).list();
        assertThat(objects, contains(testObject, testObject1, testObject2, testObject3));
    }

    @MediumTest
    public void testLoadAllEntitiesAsync() throws Exception {
        final CountDownLatch latch = new CountDownLatch(1);
        torch().load().type(TestObject.class).list(new Callback<List<TestObject>>() {
            @Override
            public void onSuccess(List<TestObject> data) {
                assertThat(data, contains(testObject, testObject1, testObject2, testObject3));
                latch.countDown();
            }

            @Override
            public void onFailure(Exception e) {
                fail("Failed to load data: " + e.getMessage());
            }
        });
        assertTrue(latch.await(5, TimeUnit.SECONDS));
    }

    @MediumTest
    public void testLoadFilteredEntities() {
        List<TestObject> objectsFiltered2 = torch().load()
                                                   .type(TestObject.class)
                                                   .filter(TestObject$.intField
                                                                   .greaterThan(10))
                                                   .list();
        assertEquals(2, objectsFiltered2.size());


        List<TestObject> objectsFiltered3 = torch().load()
                                                   .type(TestObject.class)
                                                   .filter(TestObject$.intField
                                                                   .notIn(10, 100))
                                                   .list();
        assertEquals(2, objectsFiltered3.size());

        List<TestObject> objectsFiltered4 = torch().load()
                                                   .type(TestObject.class)
                                                   .filter(TestObject$.intField
                                                                   .in(-10, 10)
                                                                   .and(TestObject$.booleanField.equalTo(true)))
                                                   .list();
        assertEquals(1, objectsFiltered4.size());
    }

    @MediumTest
    public void testLoadFilteredEntitiesAsync() throws Exception {
        final CountDownLatch firstLatch = new CountDownLatch(1);
        torch().load().type(TestObject.class).filter(TestObject$.intField.greaterThan(10)).list(
                new Callback<List<TestObject>>() {
                    @Override
                    public void onSuccess(List<TestObject> data) {
                        assertEquals(2, data.size());
                        firstLatch.countDown();
                    }

                    @Override
                    public void onFailure(Exception e) {
                        fail("Failed to load data: " + e.getMessage());
                    }
                });
        assertTrue(firstLatch.await(5, TimeUnit.SECONDS));

        final CountDownLatch secondLatch = new CountDownLatch(1);
        torch().load().type(TestObject.class).filter(TestObject$.intField.notIn(10, 100)).list(
                new Callback<List<TestObject>>() {
                    @Override
                    public void onSuccess(List<TestObject> data) {
                        assertEquals(2, data.size());
                        secondLatch.countDown();
                    }

                    @Override
                    public void onFailure(Exception e) {
                        fail("Failed to load data: " + e.getMessage());
                    }
                });
        assertTrue(secondLatch.await(5, TimeUnit.SECONDS));
    }

    @MediumTest
    public void testLoadOrderedDescending() {
        List<TestObject> objectsOrdered = torch().load().type(TestObject.class)
                                                 .orderBy(TestObject$.intField).desc().list();

        assertThat(objectsOrdered, contains(testObject3, testObject2, testObject1, testObject));
    }

    @MediumTest
    public void testLoadOrderedDescendingAsync() throws Exception {
        final CountDownLatch latch = new CountDownLatch(1);

        torch().load().type(TestObject.class).orderBy(TestObject$.intField).desc().list(
                new Callback<List<TestObject>>() {
                    @Override
                    public void onSuccess(List<TestObject> data) {
                        List<TestObject> saved = Arrays.asList(savedData);
                        Collections.reverse(saved);

                        assertEquals(saved, data);

                        latch.countDown();
                    }

                    @Override
                    public void onFailure(Exception e) {
                        fail("Failed to load data: " + e.getMessage());
                    }
                });

        assertTrue(latch.await(5, TimeUnit.SECONDS));
    }

    @MediumTest
    public void testLoadLimited() {
        List<TestObject> objectsLimited = torch().load().type(TestObject.class).limit(2).list();

        assertThat(objectsLimited, contains(testObject, testObject1));

        List<TestObject> objectsLimitedWithOffset = torch().load().type(TestObject.class).limit(2).offset(1).list();

        assertThat(objectsLimitedWithOffset, contains(testObject1, testObject2));
    }

    @MediumTest
    public void testLoadLimitedAsync() throws Exception {
        final CountDownLatch firstLatch = new CountDownLatch(1);

        torch().load().type(TestObject.class).limit(2).list(new Callback<List<TestObject>>() {
            @Override
            public void onSuccess(List<TestObject> data) {
                assertThat(data, contains(testObject, testObject1));

                firstLatch.countDown();
            }

            @Override
            public void onFailure(Exception e) {
                fail("Failed to load data: " + e.getMessage());
            }
        });

        assertTrue(firstLatch.await(5, TimeUnit.SECONDS));

        final CountDownLatch secondLatch = new CountDownLatch(1);

        torch().load().type(TestObject.class).limit(2).offset(1).list(new Callback<List<TestObject>>() {
            @Override
            public void onSuccess(List<TestObject> data) {
                assertThat(data, contains(testObject1, testObject2));

                secondLatch.countDown();
            }

            @Override
            public void onFailure(Exception e) {
                fail("Failed to load data: " + e.getMessage());
            }
        });

        assertTrue(secondLatch.await(5, TimeUnit.SECONDS));
    }
}
