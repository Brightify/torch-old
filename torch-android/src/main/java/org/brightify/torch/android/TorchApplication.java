package org.brightify.torch.android;

import android.app.Application;
import org.brightify.torch.EntityDescription;
import org.brightify.torch.TorchFactory;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public class TorchApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        EntityDescription<?>[] metadataForRegistration = getMetadataForRegistration();

        TorchFactory factory = TorchAndroid.with(this);
        for (EntityDescription<?> metadata : metadataForRegistration) {
            factory.register(metadata);
        }
    }

    protected EntityDescription<?>[] getMetadataForRegistration() {
        return new EntityDescription<?>[0];
    }

}
