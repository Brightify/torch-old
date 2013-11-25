package com.brightgestures.brightify;

import android.util.Log;

/**
 * Calling these methods, you can tweak Brightify a little bit. Make sure you know what you're doing!
 *
 * @author <a href="mailto:tkriz@redhat.com">Tadeas Kriz</a>
 */
public class Settings {
    private static final String TAG = Settings.class.getSimpleName();

    private static int enableDebugRequests = 0;
    private static int enableQueryLoggingRequests = 0;

    public static void enableDebugMode() {
        if (enableDebugRequests == Integer.MAX_VALUE) {
            // This should never happen, but we can never make assumptions.
            Log.w(TAG, "Settings#enableDebug has been previously called " + Integer.MAX_VALUE + " times! Make sure " +
                    "you're not calling it in a loop!");
            return;
        }
        if (enableDebugRequests == 0) {
            Log.d(TAG, "Debug mode enabled");
        }
        enableDebugRequests++;
    }

    public static void disableDebugMode() {
        if (enableDebugRequests == 0) {
            return;
        }
        enableDebugRequests--;
        if (enableDebugRequests == 0) {
            Log.d(TAG, "Debug mode disabled");
        }
    }

    public static void enableQueryLogging() {
        if (enableQueryLoggingRequests == Integer.MAX_VALUE) {
            // This should never happen, but we can never make assumptions.
            Log.w(TAG, "Settings#enableQueryLogging has been previously called " + Integer.MAX_VALUE + " times! Make " +
                    "sure you're not calling it in a loop!");
            return;
        }
        if(enableQueryLoggingRequests == 0) {
            Log.d(TAG, "Query logging enabled");
        }
        enableQueryLoggingRequests++;
    }

    public static void disableQueryLogging() {
        if(enableQueryLoggingRequests == 0) {
            return;
        }
        enableQueryLoggingRequests--;
        if(enableQueryLoggingRequests == 0) {
            Log.d(TAG, "Query logging disabled");
        }
    }

    public static boolean isQueryLoggingEnabled() {
        return enableQueryLoggingRequests > 0;
    }
}
