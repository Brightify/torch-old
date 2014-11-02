package org.brightify.torch;

import org.brightify.torch.test.AbstractFilterTest;
import org.brightify.torch.test.MockDatabaseEngine;

/**
 * Test for the MockDatabaseEngine. It is needed to ensure it works as expected, so it won't hide any potential bugs
 * in Torch itself.
 */
public class MockDatabaseFilterTest extends AbstractFilterTest<MockDatabaseEngine> {

    @Override
    protected MockDatabaseEngine prepareDatabaseEngine() {
        return new MockDatabaseEngine();
    }

}
