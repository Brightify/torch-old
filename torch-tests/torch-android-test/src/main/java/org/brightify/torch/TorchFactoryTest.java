package org.brightify.torch;

import android.test.AndroidTestCase;
import org.brightify.torch.android.AndroidSQLiteEngine;
import org.brightify.torch.util.Constants;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public class TorchFactoryTest extends AndroidTestCase {

    private static final String TEST_DATABASE_NAME = "test_database";
    private static final int TEST_DATABASE_VERSION = 59;
    private static final boolean TEST_ENABLE_QUERY_LOGGING = true;

    private AndroidSQLiteEngine engine;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        engine = new AndroidSQLiteEngine(getContext().getApplicationContext(), Constants.DEFAULT_DATABASE_NAME, null);

        TorchService.with(engine);
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();

        engine.wipe();

        TorchService.forceUnload();
    }

}
