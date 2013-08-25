package com.brightgestures.droidorm;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;

import java.util.LinkedList;


public class DatabaseService {
    private static String DATABASE_NAME_PROPERTY = "com.brainwashstudio.orm.DATABASE_NAME";
    private static String DATABASE_VERSION_PROPERTY = "com.brainwashstudio.orm.DATABASE_VERSION";
    private static String ENABLE_QUERY_LOGGING_PROPERTY = "com.brainwashstudio.orm.ENABLE_QUERY_LOGGING";

    private static String CURRENT_DATABASE_PREFERENCE = "com.brainwashstudio.orm.CURRENT_DATABASE";

    private static String DEFAULT_DATABASE_NAME = "my_database";
    private static int DEFAULT_DATABASE_VERSION = 1;
    private static boolean DEFAULT_ENABLE_QUERY_LOGGING = false;

    private static DatabaseFactory sFactory;

    private static final ThreadLocal<LinkedList<Database>> STACK = new ThreadLocal<LinkedList<Database>>() {
        @Override
        protected LinkedList<Database> initialValue() {
            return new LinkedList<Database>();
        }
    };

    public static void load(Context context) {
        try {
            ApplicationInfo info = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            String databaseName;
            if(info.metaData.containsKey(DATABASE_NAME_PROPERTY)) {
                databaseName = info.metaData.getString(DATABASE_NAME_PROPERTY);
            } else {
                // TODO log warning about missing database name property!
                databaseName = DEFAULT_DATABASE_NAME;
            }

            int databaseVersion;
            if(info.metaData.containsKey(DATABASE_VERSION_PROPERTY)) {
                databaseVersion = info.metaData.getInt(DATABASE_VERSION_PROPERTY);
            } else {
                // TODO log warning about missing database version property!
                databaseVersion = DEFAULT_DATABASE_VERSION;
            }

            boolean enableQueryLogging;
            if(info.metaData.containsKey(ENABLE_QUERY_LOGGING_PROPERTY)) {
                enableQueryLogging = info.metaData.getBoolean(ENABLE_QUERY_LOGGING_PROPERTY);
            } else {
                enableQueryLogging = DEFAULT_ENABLE_QUERY_LOGGING;
            }

            sFactory = new DatabaseFactory(databaseName, databaseVersion, enableQueryLogging);
        } catch (PackageManager.NameNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static Database db(Context context) {
        LinkedList<Database> stack = STACK.get();
        if(stack.isEmpty()) {
            stack.add(sFactory.begin(context));
        }

        return stack.getLast();
    }

    public static DatabaseFactory factory() {
        return sFactory;
    }

    public static boolean isLoaded() {
        return sFactory != null;
    }

    protected static boolean isDatabaseCreated(Context context) {
        if(!isLoaded()) {
            load(context);
        }

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        String currentDatabase = prefs.getString(CURRENT_DATABASE_PREFERENCE, null);
        String loadedDatabase = getLoadedDatabaseIdentifier();

        return loadedDatabase.equals(currentDatabase);
    }

    public static void setDatabaseCreated(Context context) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();

        editor.putString(CURRENT_DATABASE_PREFERENCE, getLoadedDatabaseIdentifier());

        editor.commit();
    }

    private static String getLoadedDatabaseIdentifier() {
        return factory().getDatabaseName() + "_" + factory().getDatabaseVersion();
    }

    public static void push(Database db) {
        STACK.get().add(db);
    }

    public static void pop() {
        STACK.get().removeLast();
    }

    public static void reset() {
        STACK.get().clear();
    }


}
