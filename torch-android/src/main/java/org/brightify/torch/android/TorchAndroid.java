package org.brightify.torch.android;

import android.content.Context;
import org.brightify.torch.TorchFactoryImpl;
import org.brightify.torch.util.Constants;
import org.brightify.torch.util.async.AsyncRunner;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public class TorchAndroid {

    public static TorchFactoryImpl.BasicConfiguration with(Context context) {
        AndroidAsyncExecutor executor = new AndroidAsyncExecutor();
        AsyncRunner.setExecutor(executor);

        AndroidSQLiteEngine engine = new AndroidSQLiteEngine(context, Constants.DEFAULT_DATABASE_NAME, null);
        return new TorchFactoryImpl.BasicConfiguration(engine);
    }

}
