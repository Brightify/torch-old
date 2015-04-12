package org.brightify.torch.android;

import android.app.Application;
import org.brightify.torch.EntityDescription;
import org.brightify.torch.TorchConfiguration;
import org.brightify.torch.TorchService;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public class TorchApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        TorchConfiguration<?> configuration = TorchAndroid.with(this);
        configuration.register(getMetadataForRegistration());
        configuration.register(getDescriptionsToRegister());
        TorchService.initialize(configuration);
    }

    @Deprecated
    protected EntityDescription<?>[] getMetadataForRegistration() {
        return new EntityDescription<?>[0];
    }

    protected EntityDescription<?>[] getDescriptionsToRegister() {
        return new EntityDescription<?>[0];
    }

}
