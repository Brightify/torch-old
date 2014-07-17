package org.brightify.torch.android;

import android.app.Application;
import org.brightify.torch.EntityMetadata;
import org.brightify.torch.TorchFactory;
import org.brightify.torch.TorchService;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public class TorchApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        EntityMetadata<?>[] metadataForRegistration = getMetadataForRegistration();
        TorchFactory factory = TorchService.with(this);
        for (EntityMetadata<?> metadata : metadataForRegistration) {
            factory.register(metadata);
        }
    }

    protected EntityMetadata<?>[] getMetadataForRegistration() {
        return new EntityMetadata<?>[0];
    }

}
