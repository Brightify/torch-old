package com.brightgestures.brightify.test;

import android.test.AndroidTestCase;
import android.test.suitebuilder.annotation.MediumTest;
import android.test.suitebuilder.annotation.SmallTest;
import com.brightgestures.brightify.BrightifyService;
import com.brightgestures.brightify.Entities;

import java.net.URL;
import java.net.URLClassLoader;

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

        BrightifyService.load(getContext());

        assertFalse(BrightifyService.isDatabaseCreated(getContext()));

        factory().createDatabase(getContext());

        assertTrue(BrightifyService.isDatabaseCreated(getContext()));
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();

        assertTrue(BrightifyService.isDatabaseCreated(getContext()));

        factory().deleteDatabase(getContext());

        assertFalse(BrightifyService.isDatabaseCreated(getContext()));

        BrightifyService.unload(getContext());
    }

    @SmallTest
    public void testPropertiesSetting() {
        String previousDatabaseName = factory().getDatabaseName();
        int previousDatabaseVersion = factory().getDatabaseVersion();
        boolean previousEnableQueryLogging = factory().isEnableQueryLogging();

        factory().setDatabaseName(TEST_DATABASE_NAME);
        factory().setDatabaseVersion(TEST_DATABASE_VERSION);
        factory().setEnableQueryLogging(TEST_ENABLE_QUERY_LOGGING);

        assertEquals(TEST_DATABASE_NAME, factory().getDatabaseName());
        assertEquals(TEST_DATABASE_VERSION, factory().getDatabaseVersion());
        assertEquals(TEST_ENABLE_QUERY_LOGGING, factory().isEnableQueryLogging());

        factory().setDatabaseName(previousDatabaseName);
        factory().setDatabaseVersion(previousDatabaseVersion);
        factory().setEnableQueryLogging(previousEnableQueryLogging);

        assertEquals(previousDatabaseName, factory().getDatabaseName());
        assertEquals(previousDatabaseVersion, factory().getDatabaseVersion());
        assertEquals(previousEnableQueryLogging, factory().isEnableQueryLogging());
    }

    @MediumTest
    public void testEntityRegistration() {
        factory().register(ActivityTestObject.class);

        assertNotNull(Entities.getMetadata(ActivityTestObject.class));

        assertNotNull(factory().unregister(ActivityTestObject.class));

        assertNull(Entities.getMetadata(ActivityTestObject.class));
    }


}
