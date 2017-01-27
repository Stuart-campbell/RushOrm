package co.uk.rushorm.android;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

import co.uk.rushorm.core.RushConfig;
import co.uk.rushorm.core.exceptions.RushClassNotFoundException;

/**
 * Created by stuartc on 11/12/14.
 */
public class AndroidRushConfig implements RushConfig {

    private static final String VERSION_KEY = "Rush_db_version";
    private static final String NAME_KEY = "Rush_db_name";
    private static final String DEBUG_KEY = "Rush_debug";
    private static final String LOG_KEY = "Rush_log";
    private static final String ORDER_ALPHABETICALLY = "Rush_order_alphabetically";
    private static final String RUSH_CLASSES_PACKAGE = "Rush_classes_package";
    private static final String DEFAULT_NAME = "rush.db";

    private String dbName;
    private int dbVersion;
    private boolean debug;
    private boolean log;
    private boolean orderColumnsAlphabetically;

    public AndroidRushConfig(Context context) {

        try {
            ApplicationInfo ai = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            Bundle bundle = ai.metaData;
            dbVersion = bundle != null && bundle.containsKey(VERSION_KEY) ? bundle.getInt(VERSION_KEY) : 1;
            dbName = bundle != null && bundle.containsKey(NAME_KEY) ? bundle.getString(NAME_KEY) : DEFAULT_NAME;
            debug = bundle != null && bundle.containsKey(DEBUG_KEY) && bundle.getBoolean(DEBUG_KEY);
            log = bundle != null && bundle.containsKey(LOG_KEY) && bundle.getBoolean(LOG_KEY);
            orderColumnsAlphabetically = bundle != null && bundle.containsKey(ORDER_ALPHABETICALLY) && bundle.getBoolean(ORDER_ALPHABETICALLY);

            if (bundle != null && bundle.containsKey(RUSH_CLASSES_PACKAGE)) {
                throw new RushDeprecatedException("Class searching no longer supported please remove this tag <meta-data android:name=\"Rush_classes_package\" android:value=\"co.uk.rushorm\" /> from the manifest and instead add your Rush classes directly to the AndroidInitializeConfig. See www.rushorm.com setup for more details.");

            }

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

    }

    @Override
    public String dbName() {
        return dbName;
    }

    @Override
    public int dbVersion() {
        return dbVersion;
    }

    @Override
    public boolean inDebug() {
        return debug;
    }

    @Override
    public boolean log() {
        return log;
    }

    @Override
    public boolean usingMySql() {
        return false;
    }

    @Override
    public boolean userBulkInsert() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN;
    }

    @Override
    public boolean orderColumnsAlphabetically() {
        return orderColumnsAlphabetically;
    }

}
