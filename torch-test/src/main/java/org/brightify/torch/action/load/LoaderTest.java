package org.brightify.torch.action.load;

import android.test.suitebuilder.annotation.MediumTest;
import org.brightify.torch.BaseActivityInstrumentationTestCase2;
import org.brightify.torch.Key;
import org.brightify.torch.TorchService;
import org.brightify.torch.test.MainTestActivity;
import org.brightify.torch.test.TestObject;
import org.brightify.torch.test.TestObject$;
import org.brightify.torch.util.Callback;

import java.util.ArrayList;
import java.util.Arrays;
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
public class LoaderTest extends BaseActivityInstrumentationTestCase2<MainTestActivity> {

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

        Map<Key<TestObject>, TestObject> savedDataMap = torch().save().entities(savedData);
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
        assertEquals(savedData, objects);
    }

    @MediumTest
    public void testLoadAllEntitiesAsync() throws Exception {
        final CountDownLatch latch = new CountDownLatch(1);
        torch().load().async().type(TestObject.class).list(new Callback<List<TestObject>>() {
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
        torch().load().async().type(TestObject.class).filter(TestObject$.intField.greaterThan(10)).list(
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
        torch().load().async().type(TestObject.class).filter(TestObject$.intField.notIn(10, 100)).list(
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

        ArrayList<TestObject> saved = new ArrayList<TestObject>(savedData);

        Collections.reverse(saved);

        assertEquals(saved, objectsOrdered);
    }

    @MediumTest
    public void testLoadOrderedDescendingAsync() throws Exception {
        final CountDownLatch latch = new CountDownLatch(1);

        torch().load().async().type(TestObject.class).orderBy(TestObject$.intField).desc().list(new Callback<List<TestObject>>() {
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

        torch().load().async().type(TestObject.class).limit(2).list(new Callback<List<TestObject>>() {
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

        torch().load().async().type(TestObject.class).limit(2).offset(1).list(new Callback<List<TestObject>>() {
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
