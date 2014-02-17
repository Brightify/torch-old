package org.brightify.torch.action.load;

import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.MediumTest;
import org.brightify.torch.Key;
import org.brightify.torch.TorchService;
import org.brightify.torch.test.MainTestActivity;
import org.brightify.torch.test.TestObject;
import org.brightify.torch.test.TestObjectMetadata;
import org.brightify.torch.util.Callback;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.brightify.torch.TorchService.torch;

/**
 * @author <a href="mailto:tadeas.kriz@brainwashstudio.com">Tadeas Kriz</a>
 */
public class LoaderTest extends ActivityInstrumentationTestCase2<MainTestActivity> {

    private Map<Key<TestObject>, TestObject> savedDataMap;
    private ArrayList<TestObject> savedData;

    private TestObject testObject;
    private TestObject testObject1;
    private TestObject testObject2;
    private TestObject testObject3;

    public LoaderTest() {
        super(MainTestActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        TorchService.with(getActivity()).register(TestObject.class);

        TorchService.factory().forceOpenOrCreateDatabase();

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

        savedData = new ArrayList<TestObject>();
        savedData.add(testObject);
        savedData.add(testObject1);
        savedData.add(testObject2);
        savedData.add(testObject3);

        savedDataMap = torch().save().entities(savedData).now();
        assertEquals(4, savedDataMap.size());
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();

        assertTrue(TorchService.factory().deleteDatabase());

        TorchService.forceUnload();
    }

    private TestObject createTestObject() {
        TestObject testObject = new TestObject();

        testObject.id = null;
        testObject.stringField = UUID.randomUUID().toString();
        testObject.longField = 98765432123456789L;
        testObject.intField = 123456789;

        return testObject;
    }

    @MediumTest
    public void testLoadAllEntities() {
        List<TestObject> objects = torch().load().type(TestObject.class).list();
        assertEquals(savedData, objects);
    }

    @MediumTest
    public void testLoadAllEntitiesAsync() throws Exception {
        final CountDownLatch latch = new CountDownLatch(1);
        torch().load().type(TestObject.class).async(new Callback<List<TestObject>>() {
            @Override
            public void onSuccess(List<TestObject> data) {
                assertEquals(savedData, data);
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
        List<TestObject> objectsFiltered = torch().load().type(TestObject.class)
                                                  .filter("intField > ?", 10).list();
        assertEquals(2, objectsFiltered.size());

        List<TestObject> objectsFiltered2 = torch().load()
                                                   .type(TestObject.class)
                                                   .filter(TestObjectMetadata.intField
                                                                   .greaterThan(10))
                                                   .list();
        assertEquals(2, objectsFiltered2.size());


        List<TestObject> objectsFiltered1 = torch().load().type(TestObject.class)
                                                   .filter("intField NOT IN (?, ?)", 10, 100).list();
        assertEquals(2, objectsFiltered1.size());

        List<TestObject> objectsFiltered3 = torch().load()
                                                   .type(TestObject.class)
                                                   .filter(TestObjectMetadata.intField
                                                                   .notIn(10, 100))
                                                   .list();
        assertEquals(2, objectsFiltered3.size());

        List<TestObject> objectsFiltered4 = torch().load()
                                                   .type(TestObject.class)
                                                   .filter(TestObjectMetadata.intField
                                                                   .in(-10, 10)
                                                                   .and(TestObjectMetadata.booleanField.equalTo(true)))
                                                   .list();
        assertEquals(1, objectsFiltered4.size());
    }

    @MediumTest
    public void testLoadFilteredEntitiesAsync() throws Exception {
        final CountDownLatch firstLatch = new CountDownLatch(1);
        torch().load().type(TestObject.class).filter("intField > ?", 10).async(
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
        torch().load().type(TestObject.class).filter("intField NOT IN (?, ?)", 10, 100).async(
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
                                                 .orderBy("intField").desc().list();

        ArrayList<TestObject> saved = new ArrayList<TestObject>(savedData);

        Collections.reverse(saved);

        assertEquals(saved, objectsOrdered);
    }

    @MediumTest
    public void testLoadOrderedDescendingAsync() throws Exception {
        final CountDownLatch latch = new CountDownLatch(1);

        torch().load().type(TestObject.class).orderBy("intField").desc().async(new Callback<List<TestObject>>() {
            @Override
            public void onSuccess(List<TestObject> data) {
                ArrayList<TestObject> saved = new ArrayList<TestObject>(savedData);
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
        List<TestObject> objectsLimited = torch().load().type(TestObject.class)
                                                 .limit(2).list();

        ArrayList<TestObject> saved = new ArrayList<TestObject>();
        saved.add(testObject);
        saved.add(testObject1);

        assertEquals(saved, objectsLimited);

        List<TestObject> objectsLimitedWithOffset = torch().load().type(TestObject.class)
                                                           .limit(2).offset(1).list();

        saved.clear();
        saved.add(testObject1);
        saved.add(testObject2);

        assertEquals(saved, objectsLimitedWithOffset);
    }

    @MediumTest
    public void testLoadLimitedAsync() throws Exception {
        final CountDownLatch firstLatch = new CountDownLatch(1);

        torch().load().type(TestObject.class).limit(2).async(new Callback<List<TestObject>>() {
            @Override
            public void onSuccess(List<TestObject> data) {
                ArrayList<TestObject> saved = new ArrayList<TestObject>();
                saved.add(testObject);
                saved.add(testObject1);

                assertEquals(saved, data);

                firstLatch.countDown();
            }

            @Override
            public void onFailure(Exception e) {
                fail("Failed to load data: " + e.getMessage());
            }
        });

        assertTrue(firstLatch.await(5, TimeUnit.SECONDS));

        final CountDownLatch secondLatch = new CountDownLatch(1);

        torch().load().type(TestObject.class).limit(2).offset(1).async(new Callback<List<TestObject>>() {
            @Override
            public void onSuccess(List<TestObject> data) {
                ArrayList<TestObject> saved = new ArrayList<TestObject>();
                saved.add(testObject1);
                saved.add(testObject2);

                assertEquals(saved, data);
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
