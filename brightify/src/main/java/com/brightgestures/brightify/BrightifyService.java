package com.brightgestures.brightify;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;
import com.brightgestures.brightify.model.TableMetadata;

import java.util.LinkedList;


public class BrightifyService {
    private static String DATABASE_NAME_PROPERTY = "com.brainwashstudio.brightify.DATABASE_NAME";
    private static String DATABASE_VERSION_PROPERTY = "com.brainwashstudio.brightify.DATABASE_VERSION";
    private static String ENABLE_QUERY_LOGGING_PROPERTY = "com.brainwashstudio.brightify.ENABLE_QUERY_LOGGING";

    private static String CURRENT_DATABASE_PREFERENCE = "com.brainwashstudio.brightify.CURRENT_DATABASE";

    private static BrightifyFactory sFactory;

    private static final ThreadLocal<LinkedList<Brightify>> STACK = new ThreadLocal<LinkedList<Brightify>>() {
        @Override
        protected LinkedList<Brightify> initialValue() {
            return new LinkedList<Brightify>();
        }
    };

    public static void load(Context context) {
        sFactory = new BrightifyFactory();

        sFactory.register(TableMetadata.class);

        try {
            ApplicationInfo info = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);

            if(info == null || info.metaData == null) {
                return;
            }

            // TODO log warning about missing database name property!
            if(info.metaData.containsKey(DATABASE_NAME_PROPERTY)) {
                sFactory.setDatabaseName(info.metaData.getString(DATABASE_NAME_PROPERTY));
            }

            // TODO log warning that it's not recommended to state database version!
            if(info.metaData.containsKey(DATABASE_VERSION_PROPERTY)) {
                sFactory.setDatabaseVersion(info.metaData.getInt(DATABASE_VERSION_PROPERTY));
            }

            if(info.metaData.containsKey(ENABLE_QUERY_LOGGING_PROPERTY)) {
                sFactory.setEnableQueryLogging(info.metaData.getBoolean(ENABLE_QUERY_LOGGING_PROPERTY));
            }

        } catch (PackageManager.NameNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Use this to force unload Brightify. Probably used in tests.
     *
     * This will NOT delete the database. It will only unload the factory.
     *
     * @param context
     */
    public static void unload(Context context) {
        // TODO go through all opened SQLite databases and close them

        sFactory = null;
    }

    public static Brightify bfy(Context context) {
        LinkedList<Brightify> stack = STACK.get();
        if(stack.isEmpty()) {
            stack.add(sFactory.begin(context));
        }

        return stack.getLast();
    }

    public static BrightifyFactory factory() {
        return sFactory;
    }

    public static boolean isLoaded() {
        return sFactory != null;
    }

    public static boolean isDatabaseCreated(Context context) {
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

    public static void setDatabaseNotCreated(Context context) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();

        editor.remove(CURRENT_DATABASE_PREFERENCE);

        editor.commit();
    }

    private static String getLoadedDatabaseIdentifier() {
        return factory().getDatabaseName() + "_" + factory().getDatabaseVersion();
    }

    public static void push(Brightify db) {
        STACK.get().add(db);
    }

    public static void pop() {
        STACK.get().removeLast();
    }

    public static void reset() {
        STACK.get().clear();
    }


}
