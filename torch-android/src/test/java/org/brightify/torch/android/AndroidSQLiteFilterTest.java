package org.brightify.torch.android;

import org.brightify.torch.test.AbstractFilterTest;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class AndroidSQLiteFilterTest extends AbstractFilterTest<AndroidSQLiteEngine> {
    @Override
    protected AndroidSQLiteEngine prepareDatabaseEngine() {
        return new AndroidSQLiteEngine(Robolectric.application, "test_database", null);
    }
}
