package org.brightify.torch.demo.todo;

import com.googlecode.androidannotations.annotations.EApplication;
import org.brightify.torch.EntityMetadata;
import org.brightify.torch.Settings;
import org.brightify.torch.android.TorchApplication;
import org.brightify.torch.demo.todo.model.Task$;
import org.brightify.torch.demo.todo.model.User$;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
@EApplication
public class BaseApplication extends TorchApplication {

    @Override
    public void onCreate() {
        Settings.enableQueryLogging();
        Settings.enableDebugMode();

        super.onCreate();
    }

    @Override
    protected EntityMetadata<?>[] getMetadataForRegistration() {
        return new EntityMetadata<?>[] {
                User$.create(),
                Task$.create()
        };
    }
}
