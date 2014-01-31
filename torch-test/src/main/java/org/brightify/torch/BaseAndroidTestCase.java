package org.brightify.torch;

import android.test.AndroidTestCase;
import org.brightify.torch.Settings;

/**
 * @author <a href="mailto:tadeas.kriz@brainwashstudio.com">Tadeas Kriz</a>
 */
public abstract class BaseAndroidTestCase extends AndroidTestCase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        Settings.enableDebugMode();
        Settings.enableQueryLogging();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        Settings.disableDebugMode();
        Settings.disableQueryLogging();
    }
}
