package org.brightify.torch.android;

import android.app.Application;
import org.brightify.torch.EntityDescription;
import org.brightify.torch.TorchFactory;
import org.brightify.torch.TorchService;
import org.brightify.torch.util.Constants;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public class TorchApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        EntityDescription<?>[] metadataForRegistration = getMetadataForRegistration();

        AndroidSQLiteEngine engine = new AndroidSQLiteEngine(this, Constants.DEFAULT_DATABASE_NAME, null);
        TorchFactory factory = TorchService.with(engine);
        for (EntityDescription<?> metadata : metadataForRegistration) {
            factory.register(metadata);
        }
    }

    protected EntityDescription<?>[] getMetadataForRegistration() {
        return new EntityDescription<?>[0];
    }

}
