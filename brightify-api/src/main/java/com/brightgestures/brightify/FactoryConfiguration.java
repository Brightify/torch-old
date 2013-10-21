package com.brightgestures.brightify;

/**
 * @author <a href="mailto:tadeas.kriz@brainwashstudio.com">Tadeas Kriz</a>
 */
public interface FactoryConfiguration {
    String getDatabaseName();

    boolean isImmediateDatabaseCreation();

    boolean isEnableQueryLogging();

    public static final class ConfigurationLoadException extends RuntimeException {
        public ConfigurationLoadException(Throwable throwable) {
            super("Configuration couldn't be loaded!", throwable);
        }
    }
}
