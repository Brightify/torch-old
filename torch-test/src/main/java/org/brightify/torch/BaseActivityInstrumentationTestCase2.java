package org.brightify.torch;

import android.app.Activity;
import android.test.ActivityInstrumentationTestCase2;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public abstract class BaseActivityInstrumentationTestCase2<T extends Activity>
        extends ActivityInstrumentationTestCase2<T> {

    public BaseActivityInstrumentationTestCase2(Class<T> activityClass) {
        super(activityClass);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        Settings.enableDebugMode();
        Settings.enableQueryLogging();
        Settings.enableQueryArgumentsLogging();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        Settings.disableDebugMode();
        Settings.disableQueryLogging();
        Settings.disableQueryArgumentsLogging();
    }

}
