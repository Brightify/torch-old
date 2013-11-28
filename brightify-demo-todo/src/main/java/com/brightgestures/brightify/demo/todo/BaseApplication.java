package com.brightgestures.brightify.demo.todo;

import android.app.Application;
import com.brightgestures.brightify.BrightifyService;
import com.brightgestures.brightify.demo.todo.model.UserMetadata;
import com.googlecode.androidannotations.annotations.Background;
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

    @Background
    void initBrightify() {
        BrightifyService.with(this).register(UserMetadata.create());
    }
}
