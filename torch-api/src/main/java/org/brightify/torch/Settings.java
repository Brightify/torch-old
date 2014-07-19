package org.brightify.torch;

import org.brightify.torch.util.SerialExecutor;
import org.brightify.torch.util.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;

/**
 * Calling these methods, you can tweak Torch a little bit. Make sure you know what you're doing!
 *
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public class Settings {
    public static final String DEFAULT_DATABASE_NAME = "torch_main_database";
    public static final String METADATA_POSTFIX = "$";

    private static final Logger logger = LoggerFactory.getLogger(Settings.class);
    private static final Map<BooleanSetting, Integer> booleanSettings = new HashMap<BooleanSetting, Integer>();

    private static Executor asyncExecutor = SerialExecutor.INSTANCE;

    static {
        for (BooleanSetting setting : BooleanSetting.values()) {
            forceDisableBooleanSetting(setting);
        }
    }

    /**
     * Returns true if debug mode is enabled, false otherwise.
     */
    public static boolean isDebugModeEnabled() {
        return isBooleanSettingEnabled(BooleanSetting.DEBUG);
    }

    /**
     * Enables debug mode. Currently does not do anything useful.
     */
    public static void enableDebugMode() {
        enableBooleanSetting(BooleanSetting.DEBUG);
    }

    /**
     * Disables debug mode.
     */
    public static void disableDebugMode() {
        disableBooleanSetting(BooleanSetting.DEBUG);
    }

    /**
     * Returns true if query logging is enabled, false otherwise.
     *
     * @see Settings#enableQueryLogging()
     */
    public static boolean isQueryLoggingEnabled() {
        return isBooleanSettingEnabled(BooleanSetting.QUERY_LOGGING);
    }

    /**
     * Enables query logging. If query logging is enabled, Torch will log all SQL queries into the logcat. This however
     * does not include non-SQL commands like save or delete. This will be fixed in a future release.
     * <p/>
     * Enabling this will reduce the performance! Always be sure to disable the logging in a production package!
     */
    public static void enableQueryLogging() {
        enableBooleanSetting(BooleanSetting.QUERY_LOGGING);
    }

    /**
     * Disables query logging.
     *
     * @see Settings#enableQueryLogging()
     */
    public static void disableQueryLogging() {
        disableBooleanSetting(BooleanSetting.QUERY_LOGGING);
    }

    /**
     * Returns true if logging query arguments is enabled, false otherwise.
     *
     * @see Settings#enableQueryArgumentsLogging()
     */
    public static boolean isQueryArgumentsLoggingEnabled() {
        return isBooleanSettingEnabled(BooleanSetting.QUERY_ARGUMENTS_LOGGING);
    }

    /**
     * Enables logging of query arguments. This way you can directly see which arguments are used with the SQL query.
     * Enabling this option without {@link Settings#enableQueryLogging()} will result in nothing being shown in the log.
     * The query arguments logging is basically just an extension of the query logging itself.
     * <p/>
     * Enabling this will reduce the performance! Always be sure to disable the logging in a production package!
     */
    public static void enableQueryArgumentsLogging() {
        enableBooleanSetting(BooleanSetting.QUERY_ARGUMENTS_LOGGING);
    }

    /**
     * Disables logging of query arguments.
     *
     * @see Settings#enableQueryArgumentsLogging()
     */
    public static void disableQueryArgumentsLogging() {
        disableBooleanSetting(BooleanSetting.QUERY_ARGUMENTS_LOGGING);
    }

    /**
     * Returns true if stack trace query logging is enabled, false otherwise.
     *
     * @see Settings#enableStackTraceQueryLogging()
     */
    public static boolean isStackTraceQueryLoggingEnabled() {
        return isBooleanSettingEnabled(BooleanSetting.STACK_TRACE_QUERY_LOGGING);
    }

    /**
     * Enables logging of stacktraces on query. This way you can easily see which methods were invoked and which lines,
     * so you can debug just using the logcat, without actually attaching the debugger to your application. This is very
     * useful when you do many queries at once and just some of them might be wrong, or called from a wrong place, where
     * debugging step by step might not show the issue.
     * <p/>
     * This once again will have no effect, if you will not enable the query logging itself. This is similarly to the
     * arguments logging just an extension of the query logging.
     * <p/>
     * Enabling this will reduce the performance! Always be sure to disable the logging in a production package!
     */
    public static void enableStackTraceQueryLogging() {
        enableBooleanSetting(BooleanSetting.STACK_TRACE_QUERY_LOGGING);
    }

    /**
     * Disables logging of stacktraces on query.
     *
     * @see Settings#enableStackTraceQueryLogging()
     */
    public static void disableStackTraceQueryLogging() {
        disableBooleanSetting(BooleanSetting.STACK_TRACE_QUERY_LOGGING);
    }

    /**
     * Returns currently set executor for the {@link org.brightify.torch.util.AsyncRunner}.
     * <p/>
     * Default is {@link SerialExecutor#INSTANCE}.
     */
    public static Executor getAsyncExecutor() {
        return asyncExecutor;
    }

    /**
     * Sets a custom executor for the {@link org.brightify.torch.util.AsyncRunner}.
     *
     * @see Settings#getAsyncExecutor()
     */
    public static void setAsyncExecutor(Executor executor) {
        Validate.argumentNotNull(executor, "Specified executor cannot be null!");
        asyncExecutor = executor;
    }

    private static void logStateChange(BooleanSetting setting, boolean oldState) {
        boolean newState = isBooleanSettingEnabled(setting);
        if (oldState == newState) {
            return;
        }
        logger.debug(setting.getName() + " " + (newState ? "enabled" : "disabled"));
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
            logger.warn("Enable '" + setting.getName() + "' has been previously called " + Integer.MAX_VALUE +
                        " times! Make sure you're not calling it in a loop!");
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

        private BooleanSetting(final String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }
}
