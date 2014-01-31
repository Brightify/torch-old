package org.brightify.torch;

import android.test.AndroidTestCase;

import static org.brightify.torch.TorchService.factory;

/**
 * @author <a href="mailto:tadeas.kriz@brainwashstudio.com">Tadeas Kriz</a>
 */
public class TorchFactoryTest extends AndroidTestCase {

    private static final String TEST_DATABASE_NAME = "test_database";
    private static final int TEST_DATABASE_VERSION = 59;
    private static final boolean TEST_ENABLE_QUERY_LOGGING = true;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        TorchService.with(getContext());

        assertNotNull(factory().forceOpenOrCreateDatabase());
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();

        factory().deleteDatabase();

        TorchService.forceUnload();
    }

}
