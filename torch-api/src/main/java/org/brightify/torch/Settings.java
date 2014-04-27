package org.brightify.torch;

import android.util.Log;

import java.util.HashMap;
import java.util.Map;

/**
 * Calling these methods, you can tweak Torch a little bit. Make sure you know what you're doing!
 *
 * @author <a href="mailto:tkriz@redhat.com">Tadeas Kriz</a>
 */
public class Settings {
    public static final String DEFAULT_DATABASE_NAME = "torch_main_database";
    private static final String TAG = Settings.class.getSimpleName();

    private static final Map<BooleanSetting, Integer> booleanSettings = new HashMap<BooleanSetting, Integer>();

    static {
        for (BooleanSetting setting : BooleanSetting.values()) {
            forceDisableBooleanSetting(setting);
        }
    }

    public static boolean isDebugModeEnabled() {
        return isBooleanSettingEnabled(BooleanSetting.DEBUG);
    }

    public static void enableDebugMode() {
        enableBooleanSetting(BooleanSetting.DEBUG);
    }

    public static void disableDebugMode() {
        disableBooleanSetting(BooleanSetting.DEBUG);
    }

    public static boolean isQueryLoggingEnabled() {
        return isBooleanSettingEnabled(BooleanSetting.QUERY_LOGGING);
    }

    public static void enableQueryLogging() {
        enableBooleanSetting(BooleanSetting.QUERY_LOGGING);
    }

    public static void disableQueryLogging() {
        disableBooleanSetting(BooleanSetting.QUERY_LOGGING);
    }

    public static boolean isQueryArgumentsLoggingEnabled() {
        return isBooleanSettingEnabled(BooleanSetting.QUERY_ARGUMENTS_LOGGING);
    }

    public static void enableQueryArgumentsLogging() {
        enableBooleanSetting(BooleanSetting.QUERY_ARGUMENTS_LOGGING);
    }

    public static void disableQueryArgumentsLogging() {
        disableBooleanSetting(BooleanSetting.QUERY_ARGUMENTS_LOGGING);
    }

    public static boolean isStackTraceQueryLoggingEnabled() {
        return isBooleanSettingEnabled(BooleanSetting.STACK_TRACE_QUERY_LOGGING);
    }
    public static void enableStackTraceQueryLogging() {
        enableBooleanSetting(BooleanSetting.STACK_TRACE_QUERY_LOGGING);
    }
    public static void disableStackTraceQueryLogging() {
        disableBooleanSetting(BooleanSetting.STACK_TRACE_QUERY_LOGGING);
    }

    private static void logStateChange(BooleanSetting setting, boolean oldState) {
        boolean newState = isBooleanSettingEnabled(setting);
        if (oldState == newState) {
            return;
        }
        Log.d(TAG, setting.getName() + " " + (newState ? "enabled" : "disabled"));
    }

    private static boolean isBooleanSettingEnabled(BooleanSetting setting) {
        Integer value = booleanSettings.get(setting);
        return value != null && value > 0;
    }

    private static void enableBooleanSetting(BooleanSetting setting) {
        boolean oldState = isBooleanSettingEnabled(setting);
        Integer value = booleanSettings.get(setting);
        if (value == null) {
            value = 1;
        } else if (value == Integer.MAX_VALUE) {
            // This should never happen, but we can never make assumptions.
            Log.w(TAG,
                  "Enable '" + setting.getName() + "' has been previously called " + Integer.MAX_VALUE +
                  " times! Make sure you're not calling it in a loop!"
            );
        } else {
            value++;
        }
        booleanSettings.put(setting, value);
        logStateChange(setting, oldState);
    }

    private static void disableBooleanSetting(BooleanSetting setting) {
        boolean oldState = isBooleanSettingEnabled(setting);
        Integer value = booleanSettings.get(setting);
        if (value == null) {
            value = 0;
        } else if (value > 0) {
            value--;
        }
        booleanSettings.put(setting, value);
        logStateChange(setting, oldState);
    }


    private static void forceDisableBooleanSetting(BooleanSetting setting) {
        boolean oldState = isBooleanSettingEnabled(setting);
        booleanSettings.put(setting, 0);
        logStateChange(setting, oldState);
    }

    private enum BooleanSetting {
        DEBUG("Debug mode"),
        QUERY_LOGGING("Query logging"),
        QUERY_ARGUMENTS_LOGGING("Query arguments logging"),
        STACK_TRACE_QUERY_LOGGING("Stack trace query logging");

        private final String name;

        private BooleanSetting(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }
}
