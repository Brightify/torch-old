package org.brightify.torch;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public interface AsyncInitializer {

    /**
     * Prepare an {@link AsyncEntityRegistrarSubmit} with given {@link DatabaseEngine}.
     *
     * @param databaseEngine DatabaseEngine which will be used by {@link Torch} instances created by this factory, never
     *                       null.
     *
     * @return An instance of {@link AsyncEntityRegistrarSubmit}.
     */
    AsyncEntityRegistrarSubmit with(DatabaseEngine databaseEngine);

}
