package org.brightify.torch.demo.todo;

import android.app.Application;
import org.brightify.torch.TorchService;
import org.brightify.torch.Settings;
import com.brightgestures.brightify.demo.todo.model.TaskMetadata;
import com.brightgestures.brightify.demo.todo.model.UserMetadata;
import com.googlecode.androidannotations.annotations.EApplication;

/**
 * @author <a href="mailto:tadeas.kriz@brainwashstudio.com">Tadeas Kriz</a>
 */
@EApplication
public class BaseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        initBrightify();
    }

    void initBrightify() {
        Settings.enableQueryLogging();
        Settings.enableDebugMode();
        TorchService
                .with(this)
                .register(UserMetadata.create())
                .register(TaskMetadata.create());
    }
}
