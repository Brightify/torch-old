package org.brightify.torch;

/**
 * @author <a href="mailto:tadeas.kriz@brainwashstudio.com">Tadeas Kriz</a>
 *
public class BrightifyPerformanceTest extends AndroidTestCase {

    @Override
    protected void runTest() throws Throwable {

        // Performance test ignored
        // super.runTest();

    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();

    }
*
    public void testOrmPerformance() {
        try {
            Debug.startMethodTracing("testOrmPerformance");

//            TorchService.load(getContext());

            TorchService.factory().register(TestObject.class);

            Torch torch = TorchService.torch();

            TestObject testObject = createTestObject();

            Key<TestObject> key = torch.save().entity(testObject).now();

            TestObject newTestObject = torch.load().key(key).now();

        } finally {
//            TorchService.factory().deleteDatabase(getContext());
//            TorchService.unload(getContext());
            Debug.stopMethodTracing();
        }
    }

    public void testRawPerformance() {
        Debug.startMethodTracing("testRawPerformance");

        SQLiteOpenHelper helper = new SQLiteOpenHelper(getContext(), "test_database", null, 1) {
            @Override
            public void onCreate(SQLiteDatabase db) {
                String query = "CREATE TABLE com_brightgestures_brightify_test_activity_test_object" +
                        "(" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "int_field INTEGER, " +
                        "string_field TEXT, " +
                        "long_field LONG" +
                        ")";
                db.execSQL(query);
            }

            @Override
            public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

            }
        };

        SQLiteDatabase db = helper.getWritableDatabase();


        TestObject testObject = createTestObject();

        ContentValues values = new ContentValues();
        values.put("id", testObject.id);
        values.put("int_field", testObject.intField);
        values.put("string_field", testObject.stringField);
        values.put("long_field", testObject.longField);

        long id = db.insert("com_brightgestures_brightify_test_activity_test_object", null, values);

        db.close();

        db = helper.getReadableDatabase();

        testObject = null;

        Cursor cursor = db.query("com_brightgestures_brightify_test_activity_test_object",
                new String[] { "id", "int_field", "string_field", "long_field" },
                "id=?",
                new String[] { String.valueOf(id)},
                null, null, null, null);

        cursor.moveToFirst();

        testObject = new TestObject();

        testObject.id = cursor.getLong(0);
        testObject.intField = cursor.getInt(1);
        testObject.stringField = cursor.getString(2);
        testObject.longField = cursor.getLong(3);

        db.close();

        getContext().deleteDatabase("test_database");

        Debug.stopMethodTracing();
    }

    private TestObject createTestObject() {
        TestObject testObject = new TestObject();

        testObject.id = null;
        testObject.stringField = "hello source sunny los angeles!";
        testObject.longField = 98765432123456789L;
        testObject.intField = 123456789;

        return testObject;
    }*
}
*/