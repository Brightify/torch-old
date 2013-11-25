package com.brightgestures.brightify;

import com.brightgestures.brightify.core.R;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.util.Log;

/**
 * @author <a href="mailto:tadeas.kriz@brainwashstudio.com">Tadeas Kriz</a>
 */
public class FactoryConfigurationImpl implements FactoryConfiguration {
    private static final String TAG = FactoryConfigurationImpl.class.getSimpleName();

    private static final String DEFAULT_DATABASE_NAME = "brightify_main_database";
    private static final boolean DEFAULT_ENABLE_QUERY_LOGGING = false;

    private String mDatabaseName = DEFAULT_DATABASE_NAME;
    private boolean mEnableQueryLogging = DEFAULT_ENABLE_QUERY_LOGGING;

    private FactoryConfigurationImpl() {
    }

    public static FactoryConfiguration create(Context context)  {
        FactoryConfigurationImpl configuration = new FactoryConfigurationImpl();

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

            String enableQueryLoggingProperty = context.getString(R.string.brightify__ENABLE_QUERY_LOGGING);
            if(info.metaData.containsKey(enableQueryLoggingProperty)) {
                configuration.mEnableQueryLogging = info.metaData.getBoolean(enableQueryLoggingProperty);
            }
        } catch (PackageManager.NameNotFoundException e) {
            throw new ConfigurationLoadException(e);
        }

        return configuration;
    }

    @Override
    public String getDatabaseName() {
        return mDatabaseName;
    }

    @Override
    public boolean isEnableQueryLogging() {
        return mEnableQueryLogging;
    }

}
