package org.brightify.torch.action.load;

import org.brightify.torch.Key;
import org.brightify.torch.test.MockDatabaseEngine;
import org.brightify.torch.TorchService;
import org.brightify.torch.test.TestObject;
import org.brightify.torch.test.TestObject$;
import org.brightify.torch.util.async.Callback;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.brightify.torch.TorchService.torch;
import static org.brightify.torch.test.TestUtils.createTestObject;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public class LoaderTest {

    private TestObject[] savedData;

    private TestObject testObject;
    private TestObject testObject1;
    private TestObject testObject2;
    private TestObject testObject3;

    private MockDatabaseEngine engine;

    @Before
    public void setUp() throws Exception {
        engine = new MockDatabaseEngine();

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

        Map<Key<TestObject>, TestObject> savedDataMap = torch().save().entities(savedData);
        assertThat(savedDataMap.size(), is(4));
    }

    @After
    public void tearDown() throws Exception {
        engine.wipe();
        engine = null;
        TorchService.forceUnload();
    }

    @Test
    public void testLoadById() {
        TestObject object = torch().load().type(TestObject.class).id(testObject.id);
        assertThat(object, is(testObject));

        List<TestObject> objects = torch().load().type(TestObject.class).ids(testObject1.id, testObject2.id);
        assertThat(objects.size(), is(2));
        assertThat(objects.get(0), is(testObject1));
        assertThat(objects.get(1), is(testObject2));

        objects = torch().load().type(TestObject.class).ids(Arrays.asList(testObject2.id, testObject3.id));
        assertThat(objects.size(), is(2));
        assertThat(objects.get(0), is(testObject2));
        assertThat(objects.get(1), is(testObject3));
    }

    @Test
    public void testLoadAllEntities() {
        List<TestObject> objects = torch().load().type(TestObject.class).list();
        assertThat(objects, contains(testObject, testObject1, testObject2, testObject3));
    }

    @Ignore
    @Test
    public void testLoadAllEntitiesAsync() throws Exception {
        final CountDownLatch latch = new CountDownLatch(1);
        torch().load().async().type(TestObject.class).list(new Callback<List<TestObject>>() {
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
        assertThat(latch.await(5, TimeUnit.SECONDS), is(true));
    }

    @Test
    public void testLoadFilteredEntities() {
        List<TestObject> objectsFiltered2 = torch().load()
                .type(TestObject.class)
                .filter(TestObject$.intField
                        .greaterThan(10))
                .list();
        assertThat(objectsFiltered2.size(), is(2));


        List<TestObject> objectsFiltered3 = torch().load()
                .type(TestObject.class)
                .filter(TestObject$.intField
                        .notIn(10, 100))
                .list();
        assertThat(objectsFiltered3.size(), is(2));

        List<TestObject> objectsFiltered4 = torch().load()
                .type(TestObject.class)
                .filter(TestObject$.intField
                        .in(-10, 10)
                        .and(TestObject$.booleanField.equalTo(true)))
                .list();
        assertThat(objectsFiltered4.size(), is(1));
    }

    @Ignore
    @Test
    public void testLoadFilteredEntitiesAsync() throws Exception {
        final CountDownLatch firstLatch = new CountDownLatch(1);
        torch().load().async().type(TestObject.class).filter(TestObject$.intField.greaterThan(10)).list(
                new Callback<List<TestObject>>() {
                    @Override
                    public void onSuccess(List<TestObject> data) {
                        assertThat(data.size(), is(2));
                        firstLatch.countDown();
                    }

                    @Override
                    public void onFailure(Exception e) {
                        fail("Failed to load data: " + e.getMessage());
                    }
                });
        assertThat(firstLatch.await(5, TimeUnit.SECONDS), is(true));

        final CountDownLatch secondLatch = new CountDownLatch(1);
        torch().load().async().type(TestObject.class).filter(TestObject$.intField.notIn(10, 100)).list(
                new Callback<List<TestObject>>() {
                    @Override
                    public void onSuccess(List<TestObject> data) {
                        assertThat(data.size(), is(2));
                        secondLatch.countDown();
                    }

                    @Override
                    public void onFailure(Exception e) {
                        fail("Failed to load data: " + e.getMessage());
                    }
                });
        assertThat(secondLatch.await(5, TimeUnit.SECONDS), is(true));
    }

    @Test
    public void testLoadOrderedDescending() {
        List<TestObject> objectsOrdered = torch().load().type(TestObject.class)
                .orderBy(TestObject$.intField).desc().list();

        assertThat(objectsOrdered, contains(testObject3, testObject2, testObject1, testObject));
    }

    @Ignore
    @Test
    public void testLoadOrderedDescendingAsync() throws Exception {
        final CountDownLatch latch = new CountDownLatch(1);

        torch().load().async().type(TestObject.class).orderBy(TestObject$.intField).desc().list(
                new Callback<List<TestObject>>() {
                    @Override
                    public void onSuccess(List<TestObject> data) {
                        List<TestObject> saved = Arrays.asList(savedData);
                        Collections.reverse(saved);

                        assertThat(data, is(saved));

                        latch.countDown();
                    }

                    @Override
                    public void onFailure(Exception e) {
                        fail("Failed to load data: " + e.getMessage());
                    }
                });

        assertThat(latch.await(5, TimeUnit.SECONDS), is(true));
    }

    @Test
    public void testLoadLimited() {
        List<TestObject> objectsLimited = torch().load().type(TestObject.class).limit(2).list();

        assertThat(objectsLimited, contains(testObject, testObject1));

        List<TestObject> objectsLimitedWithOffset = torch().load().type(TestObject.class).limit(2).offset(1).list();

        assertThat(objectsLimitedWithOffset, contains(testObject1, testObject2));
    }

    @Ignore
    @Test
    public void testLoadLimitedAsync() throws Exception {
        final CountDownLatch firstLatch = new CountDownLatch(1);

        torch().load().async().type(TestObject.class).limit(2).list(new Callback<List<TestObject>>() {
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

        assertThat(firstLatch.await(5, TimeUnit.SECONDS), is(true));

        final CountDownLatch secondLatch = new CountDownLatch(1);

        torch().load().async().type(TestObject.class).limit(2).offset(1).list(new Callback<List<TestObject>>() {
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

        assertThat(secondLatch.await(5, TimeUnit.SECONDS), is(true));
    }
}
