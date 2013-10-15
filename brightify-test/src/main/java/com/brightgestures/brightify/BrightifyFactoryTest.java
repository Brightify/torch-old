package com.brightgestures.brightify;

import android.test.AndroidTestCase;
import android.test.suitebuilder.annotation.MediumTest;
import android.test.suitebuilder.annotation.SmallTest;
import com.brightgestures.brightify.BrightifyFactory;
import com.brightgestures.brightify.BrightifyService;
import com.brightgestures.brightify.Entities;

import static com.brightgestures.brightify.BrightifyService.factory;

/**
 * @author <a href="mailto:tadeas.kriz@brainwashstudio.com">Tadeas Kriz</a>
 */
public class BrightifyFactoryTest extends AndroidTestCase {

    private static final String TEST_DATABASE_NAME = "test_database";
    private static final int TEST_DATABASE_VERSION = 59;
    private static final boolean TEST_ENABLE_QUERY_LOGGING = true;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        BrightifyService.with(getContext());

        assertNotNull(factory().forceOpenOrCreateDatabase());
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();

        factory().deleteDatabase();

        BrightifyService.forceUnload();
    }

}
