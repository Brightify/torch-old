package org.brightify.torch.android;

import android.content.Context;
import org.brightify.torch.TorchFactory;
import org.brightify.torch.TorchService;
import org.brightify.torch.util.Constants;
import org.brightify.torch.util.async.AsyncRunner;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public class TorchAndroid {

    public static TorchFactory with(Context context) {
        AndroidAsyncExecutor executor = new AndroidAsyncExecutor();
        AsyncRunner.setExecutor(executor);

        AndroidSQLiteEngine engine = new AndroidSQLiteEngine(context, Constants.DEFAULT_DATABASE_NAME, null);
        return TorchService.with(engine);
    }

}
