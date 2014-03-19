package org.brightify.torch.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * One of the main annotations used in Torch. Adding an {@link Entity} annotation to your class and having
 * torch-compiler on classpath will result into generation of a class, named like the original class annotated with
 * {@link Entity}, but with dollar sign at the end. This class is called <strong>metadata class</strong>. Look at the
 * following example:
 * <p/>
 * <pre>
 * {@code
 * &#64;Entity
 *  class User {
 *      &#64;Id
 *      Long id;
 *  }}
 * </pre>
 * <p/>
 * The {@link Entity} annotation for class {@code User}, would produce a class named {@code User$} which contains all
 * the necessary data needed for torch to work with the entity. Also, this class has static field for each property of
 * the original class, which you would use for filtering and ordering.
 */
@Target({ ElementType.TYPE })
public @interface Entity {
    public static final String LOWEST_VERSION = "0.0.1";

    /**
     * Overrides the default table name.
     * <p/>
     * This is not recommended, because it might collide with another table. The default table name is entity name,
     * prepended by it's package name, with dots replaced by underscores. For example, {@code
     * org.brightify.torch.model.TorchEntityModel} will become {@code org_brightify_torch_model_TorchEntityModel}.
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
    String version() default LOWEST_VERSION;

    /**
     * Type of migration. Please refer to {@link Entity.MigrationType} for more details.
     *
     * @return Type of migration used for the entity.
     */
    MigrationType migration() default MigrationType.MIGRATE;

    /**
     * Delete entity table. Use this when you want to get rid of a deprecated entity. Entity with this flag set to true
     * doesn't have to contain any fields or methods. This is the only recommended way to delete entity table.
     *
     * @return True if all the data should be deleted next time the metadata are registered into {@link
     * org.brightify.torch.TorchFactory}.
     */
    boolean delete() default false; // TODO not implemented

    /**
     * When this flag is enabled, the package name is not included in the table name. It's not recommended to use this
     * unless you have very deep package structure.
     * <p/>
     * TODO provide maximal package length
     *
     * @return True if entity's table should only have class' simple name.
     */
    boolean useSimpleName() default false; // TODO check if implemented

    /**
     * Set this to true, to ignore this entity completely.
     */
    boolean ignore() default false; // TODO check if implemented

    public enum MigrationType {
        /**
         * Call all migration methods in order, if any of them fails (or there is no migration method available),
         * brightify will throw a {@link org.brightify.torch.util.MigrationException} and revert any change made to the
         * database. This is default behavior.
         * <p/>
         * If migration methods don't cover all version upgrades, compile-time error will be raised.
         */
        MIGRATE,

        /**
         * Drop the table and recreate it. Beware, this will erase all data in the table!
         */
        DROP_CREATE,

        /**
         * Try to call migration methods in order and if any of them fails, brightify will drop and recreate the table.
         * Same applies if migration methods don't cover all version upgrades.
         */
        TRY_MIGRATE
    }

}
