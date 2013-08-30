package com.brightgestures.brightify.util;

/**
* @author <a href="mailto:tadeas.kriz@brainwashstudio.com">Tadeas Kriz</a>
*/
public enum MigrationType {
    /**
     * Call all migration methods in order, if any of them fails (or there is no migration method available),
     * brightify will throw a {@link MigrationException} and revert any change
     * made to the database.
     */
    MIGRATE,

    /**
     * Drop the table and recreate it. Beware, this will erase all data in the table!
     */
    DROP_CREATE,

    /**
     * Try to call migration methods in order and if any of them fails, brightify will throw a
     * {@link MigrationException} and revert any change made to the database.
     * If there is no migration method available, brightify will drop and recreate the table.
     */
    TRY_MIGRATE
}
