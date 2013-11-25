package com.brightgestures.brightify.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

@Target({ ElementType.TYPE })
public @interface Entity {
    public static final int LOWEST_VERSION = 1;

    /**
     * Overrides the default table name.
     * <p/>
     * This is not recommended, because it might collide with another table. The default table name is entity name,
     * prepended by it's package name, with dots replaced by underscores. For example,
     * {@code com.brightgestures.brightify.model.BrightifySystemModel} will become
     * {@code com_brightgestures_brightify_model_BrightifySystemModel}
     *
     * @return
     */
    String name() default ""; // TODO check if implemented

    /**
     * Table version. The lowest possible version is the default, 1. You can only increment the version through new
     * versions of your applications.
     * <p/>
     * If you need to make changes to the table, increment this value and implement migration methods.
     *
     * @return
     */
    int version() default LOWEST_VERSION;

    /**
     * Type of migration. Please refer to {@link com.brightgestures.brightify.annotation.Entity.MigrationType} for
     * more details.
     *
     * @return
     */
    MigrationType migration() default MigrationType.MIGRATE;

    /**
     * Delete entity table. Use this when you want to get rid of a deprecated entity. Entity with this flag set to true
     * doesn't have to contain any fields or methods. This is the only recommended way to delete entity table.
     *
     * @return
     */
    boolean delete() default false; // TODO not implemented

    /**
     * When this flag is enabled, the package name is not included in the table name. It's not recommended to use this
     * unless you have very deep package structure.
     * <p/>
     * TODO provide maximal package length
     *
     * @return
     */
    boolean useSimpleName() default false; // TODO check if implemented

    /**
     * Set this to true, to ignore this entity completely.
     */
    boolean ignore() default false; // TODO check if implemented

    public enum MigrationType {
        /**
         * Call all migration methods in order, if any of them fails (or there is no migration method available),
         * brightify will throw a {@link com.brightgestures.brightify.util.MigrationException} and revert any change
         * made to the database. This is default behavior.
         * <p/>
         * If migration methods don't cover all version upgrades, compile-time error will be raised.
         */
        MIGRATE,

        /**
         * Drop the table and recreate it. Beware, this will erase all data in the table!
         */
        DROP_CREATE,

        /**
         * Try to call migration methods in order and if any of them fails, brightify will drop and recreate the
         * table. Same applies if migration methods don't cover all version upgrades.
         */
        TRY_MIGRATE
    }

}
