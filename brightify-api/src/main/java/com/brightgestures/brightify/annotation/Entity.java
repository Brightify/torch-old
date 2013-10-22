package com.brightgestures.brightify.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

// @Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface Entity {
    /**
     * Overrides the default table name.
     *
     * This is not recommended, because it might collide with another table. The default table name is entity name,
     * prepended by it's package name, with dots replaced by underscores. For example,
     * {@code com.brightgestures.brightify.model.BrightifySystemModel} will become
     * {@code com_brightgestures_brightify_model_BrightifySystemModel}
     * @return
     */
    String name() default "";

    /**
     * Table version.
     *
     * If you need to make changes to the table, increment this value and implement migration methods.
     * @return
     */
    int version() default 1;

    /**
     * Type of migration. Please refer to {@link com.brightgestures.brightify.annotation.Entity.MigrationType} for more details.
     * @return
     */
    MigrationType migration() default MigrationType.TRY_MIGRATE;

    /**
     * Delete entity table. Use this when you want to get rid of a deprecated entity. Entity with this flag set to true
     * doesn't have to contain any fields or methods. This is the only recommended way to delete entity table.
     * @return
     */
    boolean delete() default false;

    /**
     * When this flag is enabled, the package name is not included in the table name. It's not recommended to use this
     * unless you have very deep package structure.
     *
     * TODO provide maximal package length
     *
     * @return
     */
    boolean useSimpleName() default false;

    /**
     * Set this to true, to ignore this entity completely.
     */
    boolean ignore() default false;

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

}