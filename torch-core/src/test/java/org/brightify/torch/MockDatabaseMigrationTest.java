package org.brightify.torch;

import org.brightify.torch.test.AbstractMigrationTest;
import org.brightify.torch.test.MockDatabaseEngine;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public class MockDatabaseMigrationTest extends AbstractMigrationTest<MockDatabaseEngine> {
    @Override
    protected MockDatabaseEngine prepareDatabaseEngine() {
        return new MockDatabaseEngine();
    }
}
