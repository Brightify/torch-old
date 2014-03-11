package org.brightify.torch.demo.todo;

import android.app.Application;
import org.brightify.torch.TorchService;
import org.brightify.torch.Settings;
import org.brightify.torch.demo.todo.model.Task$;
import org.brightify.torch.demo.todo.model.User$;
import com.googlecode.androidannotations.annotations.EApplication;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
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
                .register(User$.create())
                .register(Task$.create());
    }
}
