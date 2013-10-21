package com.brightgestures.brightify;

import android.content.Context;

/**
 * @author <a href="mailto:tadeas.kriz@brainwashstudio.com">Tadeas Kriz</a>
 */
public interface AsyncInitializer {

    /**
     * Prepare {@link com.brightgestures.brightify.AsyncFactoryBuilder} with {@link android.content.Context}.
     *
     * @param context Any context, {@link android.content.Context#getApplicationContext()} will be used.
     */
    AsyncFactoryBuilder with(Context context);

}
