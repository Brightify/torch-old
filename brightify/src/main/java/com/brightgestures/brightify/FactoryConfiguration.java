package com.brightgestures.brightify;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.util.Log;

/**
 * @author <a href="mailto:tadeas.kriz@brainwashstudio.com">Tadeas Kriz</a>
 */
public class FactoryConfiguration {
    private static final String TAG = FactoryConfiguration.class.getSimpleName();

    private static final String DEFAULT_DATABASE_NAME = "brightify_main_database";
    private static final boolean DEFAULT_IMMEDIATE_DATABASE_CREATION = false;
    private static final boolean DEFAULT_ENABLE_QUERY_LOGGING = false;

    private String mDatabaseName = DEFAULT_DATABASE_NAME;
    private boolean mImmediateDatabaseCreation = DEFAULT_IMMEDIATE_DATABASE_CREATION;
    private boolean mEnableQueryLogging = DEFAULT_ENABLE_QUERY_LOGGING;

    private FactoryConfiguration() {
    }

    public static FactoryConfiguration create(Context context)  {
        FactoryConfiguration configuration = new FactoryConfiguration();

        try {
            ApplicationInfo info = context.getPackageManager().getApplicationInfo(context.getPackageName(),
                    PackageManager.GET_META_DATA);

            if(info == null || info.metaData == null) {
                return configuration;
            }

            String databaseNameProperty = context.getString(R.string.brightify__DATABASE_NAME);
            if(info.metaData.containsKey(databaseNameProperty)) {
                configuration.mDatabaseName = info.metaData.getString(databaseNameProperty);
            } else {
                Log.w(TAG, "Database name not set in AndroidManifest.xml! It's highly recommended that you set this meta-data!");
            }

            String immediateDatabaseCreationProperty = context.getString(R.string.brightify__IMMEDIATE_DATABASE_CREATION);
            if(info.metaData.containsKey(immediateDatabaseCreationProperty)) {
                configuration.mImmediateDatabaseCreation = info.metaData.getBoolean(immediateDatabaseCreationProperty);
            }

            String enableQueryLoggingProperty = context.getString(R.string.brightify__ENABLE_QUERY_LOGGING);
            if(info.metaData.containsKey(enableQueryLoggingProperty)) {
                configuration.mEnableQueryLogging = info.metaData.getBoolean(enableQueryLoggingProperty);
            }
        } catch (PackageManager.NameNotFoundException e) {
            throw new ConfigurationLoadException(e);
        }

        return configuration;
    }

    public String getDatabaseName() {
        return mDatabaseName;
    }

    public boolean isImmediateDatabaseCreation() {
        return mImmediateDatabaseCreation;
    }

    public boolean isEnableQueryLogging() {
        return mEnableQueryLogging;
    }

    public static final class ConfigurationLoadException extends RuntimeException {
        public ConfigurationLoadException(Throwable throwable) {
            super("Configuration couldn't be loaded!", throwable);
        }
    }

}
