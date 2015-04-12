package org.brightify.torch;

/**
 * Singleton for
 */
public class TorchService {
    private static final TorchFactoryRegistry defaultFactoryRegistry = new TorchFactoryRegistry();

    public static TorchFactory initialize(TorchConfiguration<?> configuration) {
        return defaultFactoryRegistry.initialize(configuration);
    }

    public static TorchFactory initialize(Class<? extends ConnectionIdentifier> identifier,
                                   TorchConfiguration<?> configuration) {
        return defaultFactoryRegistry.initialize(identifier, configuration);
    }

    public static Torch torch() {
        return defaultFactoryRegistry.torch();
    }

    public static boolean isLoaded() {
        return defaultFactoryRegistry.isLoaded();
    }

    public static boolean isLoaded(Class<? extends ConnectionIdentifier> identifier) {
        return defaultFactoryRegistry.isLoaded(identifier);
    }

    public static TorchFactory factory() {
        return defaultFactoryRegistry.factory();
    }

    public static TorchFactory factory(Class<? extends ConnectionIdentifier> identifier) {
        return defaultFactoryRegistry.factory(identifier);
    }

    public static void unload() {
        defaultFactoryRegistry.unload();
    }

    public static void unload(Class<? extends ConnectionIdentifier> identifier) {
        defaultFactoryRegistry.unload(identifier);
    }

    public static void unloadAll() {
        defaultFactoryRegistry.unloadAll();
    }

}
