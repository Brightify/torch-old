package com.brightgestures.brightify.test;

import android.content.Context;
import android.test.ActivityUnitTestCase;
import android.test.suitebuilder.annotation.MediumTest;
import com.brightgestures.brightify.BrightifyService;
import com.brightgestures.brightify.Entities;

import static com.brightgestures.brightify.BrightifyService.factory;

/**
 * @author <a href="mailto:tadeas.kriz@brainwashstudio.com">Tadeas Kriz</a>
 */
public class BrightifyFactoryTest extends ActivityUnitTestCase<MainTestActivity> {

    private Context mContext;

    public BrightifyFactoryTest() {
        super(MainTestActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        mContext = getInstrumentation().getContext();

        BrightifyService.load(mContext);

        assertFalse(BrightifyService.isDatabaseCreated(mContext));

        factory().createDatabase(mContext);

        assertTrue(BrightifyService.isDatabaseCreated(mContext));
    }

    @MediumTest
    public void testEntityRegistration() {

        factory().register(ActivityTestObject.class);

        assertNotNull(Entities.getMetadata(ActivityTestObject.class));

        assertNotNull(factory().unregister(ActivityTestObject.class));

        assertNull(Entities.getMetadata(ActivityTestObject.class));
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();

        assertTrue(BrightifyService.isDatabaseCreated(mContext));

        factory().deleteDatabase(mContext);

        assertFalse(BrightifyService.isDatabaseCreated(mContext));

        BrightifyService.unload(mContext);
    }
}
