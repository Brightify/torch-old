package com.brightgestures.brightify.test;

import android.content.Context;
import android.test.ActivityUnitTestCase;
import android.test.AndroidTestCase;
import android.test.suitebuilder.annotation.MediumTest;
import com.brightgestures.brightify.BrightifyService;

/**
 * @author <a href="mailto:tadeas.kriz@brainwashstudio.com">Tadeas Kriz</a>
 */
public class BrightifyServiceTest extends AndroidTestCase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @MediumTest
    public void testService() {
        assertFalse(BrightifyService.isLoaded());

        BrightifyService.load(getContext());
        assertTrue(BrightifyService.isLoaded());

        assertFalse(BrightifyService.isDatabaseCreated(getContext()));

        BrightifyService.setDatabaseCreated(getContext());
        assertTrue(BrightifyService.isDatabaseCreated(getContext()));

        BrightifyService.setDatabaseNotCreated(getContext());
        assertFalse(BrightifyService.isDatabaseCreated(getContext()));

        BrightifyService.unload(getContext());
        assertFalse(BrightifyService.isLoaded());
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }
}
