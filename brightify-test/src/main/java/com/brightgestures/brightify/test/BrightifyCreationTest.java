package com.brightgestures.brightify.test;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.test.ActivityUnitTestCase;
import android.test.suitebuilder.annotation.MediumTest;
import com.brightgestures.brightify.Brightify;
import com.brightgestures.brightify.BrightifyService;

/**
 * @author <a href="mailto:tadeas.kriz@brainwashstudio.com">Tadeas Kriz</a>
 */
public class BrightifyCreationTest extends ActivityUnitTestCase<MainTestActivity> {

    private Context mContext;

    public BrightifyCreationTest() {
        super(MainTestActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        mContext = getInstrumentation().getContext();
    }

    @MediumTest
    public void testCreation() {
        BrightifyService.load(mContext);

        BrightifyService.factory().register(ActivityTestObject.class);

        Brightify db = BrightifyService.bfy(mContext);

        assertNotNull(db);

        SQLiteDatabase sqLiteDatabase = db.getWritableDatabase();

        assertNotNull(sqLiteDatabase);

        sqLiteDatabase.close();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }
}
