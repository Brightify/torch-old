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
public class BrightifyServiceTest extends ActivityUnitTestCase<MainTestActivity> {

    private Context mContext;

    public BrightifyServiceTest() {
        super(MainTestActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        mContext = getInstrumentation().getContext();
    }

    @MediumTest
    public void testCreation() {
        assertFalse(BrightifyService.isLoaded());

        BrightifyService.load(mContext);

        assertTrue(BrightifyService.isLoaded());

        assertFalse(BrightifyService.isDatabaseCreated(mContext));

        BrightifyService.setDatabaseCreated(mContext);

        assertTrue(BrightifyService.isDatabaseCreated(mContext));

        BrightifyService.setDatabaseNotCreated(mContext);

        assertFalse(BrightifyService.isDatabaseCreated(mContext));
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }
}
