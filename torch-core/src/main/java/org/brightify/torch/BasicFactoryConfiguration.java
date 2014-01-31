package org.brightify.torch;


import org.brightify.torch.FactoryConfiguration;
import org.brightify.torch.Settings;
import org.brightify.torch.TorchFactory;

/**
 * @author <a href="mailto:tadeas.kriz@brainwashstudio.com">Tadeas Kriz</a>
 */
public class BasicFactoryConfiguration implements FactoryConfiguration {
    private String databaseName;

    public BasicFactoryConfiguration() {
        this(Settings.DEFAULT_DATABASE_NAME);
    }

    public BasicFactoryConfiguration(String databaseName) {
        setDatabaseName(databaseName);
    }

    @Override
    public void configureFactory(TorchFactory factory) {
        factory.setDatabaseName(databaseName);
    }

    public String getDatabaseName() {
        return databaseName;
    }

    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }
}
