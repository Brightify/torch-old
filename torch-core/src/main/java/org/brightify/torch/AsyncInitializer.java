package org.brightify.torch;

import android.content.Context;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public interface AsyncInitializer {

    /**
     * Prepare {@link AsyncFactoryBuilder} with {@link android.content.Context}.
     *
     * @param context Any context, {@link android.content.Context#getApplicationContext()} will be used.
     */
    AsyncEntityRegistrarSubmit with(Context context);

}
