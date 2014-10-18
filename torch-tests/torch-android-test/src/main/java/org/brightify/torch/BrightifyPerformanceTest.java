package org.brightify.torch;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Debug;
import android.test.AndroidTestCase;
import android.util.Log;
import org.brightify.torch.android.AndroidSQLiteEngine;
import org.brightify.torch.test.TestObject;
import org.brightify.torch.test.TestObject$;
import org.brightify.torch.util.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.brightify.torch.TorchService.torch;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public class BrightifyPerformanceTest extends AndroidTestCase {

    private static final String TAG = "PerfTest";
    private static final int COUNT = 1;


    @Override
    protected void runTest() throws Throwable {

        // Comment next line to disable perf tests
        super.runTest();

    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        Settings.enableQueryLogging();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        Settings.disableQueryLogging();
    }

    public void testOrmPerformance() throws Exception {
        AndroidSQLiteEngine engine = null;
        try {
            Debug.startMethodTracing("testOrmPerformance");
            long start = System.currentTimeMillis();
            long time = start;

            engine = new AndroidSQLiteEngine(getContext(), Constants.DEFAULT_DATABASE_NAME, null);

            TorchService.with(engine).register(TestObject$.create());

            Log.d(TAG, "ORM - Init: " + (System.currentTimeMillis() - time) + "ms");
            time = System.currentTimeMillis();

            List<TestObject> testObjects = new ArrayList<TestObject>();

            for (int i = 0; i < COUNT; i++) {
                testObjects.add(createTestObject());
            }
            Log.d(TAG, "ORM - Create objects: " + (System.currentTimeMillis() - time) + "ms");
            time = System.currentTimeMillis();

            Set<TestObject> entities = torch().save().entities(testObjects).keySet();

            Log.d(TAG, "ORM - Save: " + (System.currentTimeMillis() - time) + "ms");
            time = System.currentTimeMillis();

            assertEquals(testObjects.size(), entities.size());

            List<TestObject> testObjects1 = torch().load().type(TestObject.class).list();

            Log.d(TAG, "ORM - Load: " + (System.currentTimeMillis() - time) + "ms");

            assertEquals(testObjects.size(), testObjects1.size());

            Log.d(TAG, "ORM - Complete: " + (System.currentTimeMillis() - start) + "ms");
        } finally {
            engine.wipe();
            TorchService.forceUnload();
            Debug.stopMethodTracing();
        }
    }

    public void testRawPerformance() {
        try {
            Debug.startMethodTracing("testRawPerformance");

            long start = System.currentTimeMillis();
            long time = start;

            SQLiteOpenHelper helper = new SQLiteOpenHelper(getContext(), "test_database", null, 1) {
                @Override
                public void onCreate(SQLiteDatabase db) {
                    String query = "CREATE TABLE IF NOT EXISTS org_brightify_torch_test_TestObject" +
                                   "(" +
                                   "booleanPrimitiveField INTEGER, " +
                                   "id INTEGER CONSTRAINT id_primary PRIMARY KEY AUTOINCREMENT, " +
                                   "testName INTEGER, " +
                                   "intField INTEGER, " +
                                   "protectedTest TEXT, " +
                                   "longPrimitiveField INTEGER, " +
                                   "longField INTEGER, " +
                                   "intPrimitiveField INTEGER, " +
                                   "booleanField INTEGER, " +
                                   "defaultTest TEXT, " +
                                   "stringField TEXT, " +
                                   "testMethod INTEGER" +
                                   ")";

                    db.execSQL(query);
                }

                @Override
                public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

                }
            };

            SQLiteDatabase db = helper.getWritableDatabase();

            Log.d(TAG, "RAW - Init: " + (System.currentTimeMillis() - time) + "ms");
            time = System.currentTimeMillis();

            List<TestObject> testObjects = new ArrayList<TestObject>();

            for (int i = 0; i < COUNT; i++) {
                testObjects.add(createTestObject());
            }

            Log.d(TAG, "RAW - Create objects: " + (System.currentTimeMillis() - time) + "ms");
            time = System.currentTimeMillis();

            db.beginTransaction();

            for (TestObject testObject : testObjects) {
                ContentValues values = new ContentValues();
                values.put("id", testObject.id);
                values.put("booleanPrimitiveField", testObject.booleanPrimitiveField);
                values.put("intField", testObject.intField);
                values.put("stringField", testObject.stringField);
                values.put("longField", testObject.longField);
                values.put("testName", testObject.a());
                values.put("protectedTest", testObject.getProtectedTest());
                values.put("longPrimitiveField", testObject.longPrimitiveField);
                values.put("intPrimitiveField", testObject.intPrimitiveField);
                values.put("booleanField", testObject.booleanField);
                //values.put("defaultTest", testObject);
                values.put("testMethod", testObject.testMethod());

                testObject.id = db.insert("org_brightify_torch_test_TestObject", null, values);
            }

            db.setTransactionSuccessful();
            db.endTransaction();

            Log.d(TAG, "RAW - Save: " + (System.currentTimeMillis() - time) + "ms");
            time = System.currentTimeMillis();

            db.close();

            db = helper.getReadableDatabase();

            Cursor cursor = db.rawQuery(
                    "SELECT " +
                    "booleanPrimitiveField, " +
                    "id, " +
                    "testName, " +
                    "intField, " +
                    "protectedTest, " +
                    "longPrimitiveField, " +
                    "longField, " +
                    "intPrimitiveField, " +
                    "booleanField, " +
                    "defaultTest, " +
                    "stringField, " +
                    "testMethod " +
                    "FROM org_brightify_torch_test_TestObject",
                    new String[0]
            );

            cursor.moveToFirst();

            List<TestObject> testObjects1 = new ArrayList<TestObject>();

            do {

                TestObject testObject = new TestObject();

                testObject.id = cursor.getLong(0);
                testObject.intField = cursor.getInt(1);
                testObject.stringField = cursor.getString(2);
                testObject.longField = cursor.getLong(3);

                testObject.booleanPrimitiveField = cursor.getInt(0) == 1;
                testObject.id = cursor.getLong(1);
                testObject.setTestName(cursor.getInt(2));
                testObject.intField = cursor.getInt(3);
                testObject.setProtectedTest(cursor.getString(4));
                testObject.longPrimitiveField = cursor.getLong(5);
                testObject.longField = cursor.getLong(6);
                testObject.intPrimitiveField = cursor.getInt(7);
                testObject.booleanField = cursor.getInt(8) == 1;
//                testObject.halelujah();
                testObject.stringField = cursor.getString(10);
                testObject.halelujah(cursor.getLong(11));

                testObjects1.add(testObject);

                cursor.moveToNext();
            } while (!cursor.isAfterLast());

            cursor.close();

            Log.d(TAG, "RAW - Load: " + (System.currentTimeMillis() - time) + "ms");

            db.close();

            assertEquals(testObjects.size(), testObjects1.size());

            Log.d(TAG, "RAW - Complete: " + (System.currentTimeMillis() - start) + "ms");
        } finally {
            getContext().deleteDatabase("test_database");

            Debug.stopMethodTracing();
        }
    }

    private TestObject createTestObject() {
        TestObject testObject = new TestObject();

        testObject.id = null;
        testObject.stringField = "hello source sunny los angeles!";
        testObject.longField = 98765432123456789L;
        testObject.intField = 123456789;

        return testObject;
    }
}
