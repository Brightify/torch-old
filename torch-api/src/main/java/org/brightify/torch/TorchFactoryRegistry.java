package org.brightify.torch;

import org.brightify.torch.util.Validate;

import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public class TorchFactoryRegistry {

    private final Map<Class<? extends ConnectionIdentifier>, TorchFactory> factoryRegistry =
            new HashMap<Class<? extends ConnectionIdentifier>, TorchFactory>();
    // We want the default factory field for faster access as the default will be used the most.
    private TorchFactory defaultFactory;

    public TorchFactory initialize(TorchConfiguration<?> configuration) {
        TorchFactory factory = initialize(DefaultConnection.class, configuration);

        defaultFactory = factory;

        return factory;
    }

    public TorchFactory initialize(Class<? extends ConnectionIdentifier> identifier,
                                          TorchConfiguration<?> configuration) {
        Validate.isNull(factory(identifier),
                        "Factory with identifier {0} is already registered!",
                        identifier.getSimpleName());


        TorchFactory factory = configuration.initializeFactory();

        factoryRegistry.put(identifier, factory);

        return factory;
    }

    public Torch torch() {
        Validate.isTrue(isLoaded(), "Default factory is not loaded!");

        return factory().begin();
    }

    public boolean isLoaded() {
        return factory() != null;
    }

    public boolean isLoaded(Class<? extends ConnectionIdentifier> identifier) {
        return factory(identifier) != null;
    }

    public TorchFactory factory() {
        return defaultFactory;
    }

    public TorchFactory factory(Class<? extends ConnectionIdentifier> identifier) {
        return factoryRegistry.get(identifier);
    }

    public void unload() {
        unload(DefaultConnection.class);
        defaultFactory = null;
    }

    public void unload(Class<? extends ConnectionIdentifier> identifier) {
        TorchFactory factory = factoryRegistry.remove(identifier);
        if(factory != null) {
            factory.unload();
        }
    }

    public void unloadAll() {
        for (TorchFactory factory : factoryRegistry.values()) {
            factory.unload();
        }
        factoryRegistry.clear();
        defaultFactory = null;
    }

    private interface DefaultConnection extends ReadWriteConnection {

    }

}
