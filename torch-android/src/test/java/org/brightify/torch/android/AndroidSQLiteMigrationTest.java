package org.brightify.torch.android;

import org.brightify.torch.test.AbstractMigrationTest;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
@RunWith(RobolectricTestRunner.class)
public class AndroidSQLiteMigrationTest extends AbstractMigrationTest<AndroidSQLiteEngine> {
    @Override
    protected AndroidSQLiteEngine prepareDatabaseEngine() {
        return new AndroidSQLiteEngine(Robolectric.application, "test_database", null);
    }
}
