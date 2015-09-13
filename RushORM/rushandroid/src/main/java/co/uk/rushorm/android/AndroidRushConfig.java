package co.uk.rushorm.android;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import co.uk.rushorm.core.RushConfig;

/**
 * Created by stuartc on 11/12/14.
 */
public class AndroidRushConfig implements RushConfig {

    private static final String VERSION_KEY = "Rush_db_version";
    private static final String NAME_KEY = "Rush_db_name";
    private static final String DEBUG_KEY = "Rush_debug";
    private static final String LOG_KEY = "Rush_log";
    private static final String ORDER_ALPHABETICALLY = "Rush_order_alphabetically";
    private static final String REQUIRE_TABLE_ANNOTATION_KEY = "Rush_requires_table_annotation";
    private static final String DEFAULT_NAME = "rush.db";

    private String dbName;
    private int dbVersion;
    private boolean debug;
    private boolean log;
    private boolean orderColumnsAlphabetically;
    private boolean requiresTableAnnotation;

    public AndroidRushConfig(Context context) {
        try {
            ApplicationInfo ai = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            Bundle bundle = ai.metaData;
            dbVersion = bundle != null && bundle.containsKey(VERSION_KEY) ? bundle.getInt(VERSION_KEY) : 1;
            dbName = bundle != null && bundle.containsKey(NAME_KEY) ? bundle.getString(NAME_KEY) : DEFAULT_NAME;
            debug = bundle != null && bundle.containsKey(DEBUG_KEY) && bundle.getBoolean(DEBUG_KEY);
            log = bundle != null && bundle.containsKey(LOG_KEY) && bundle.getBoolean(LOG_KEY);
            orderColumnsAlphabetically = bundle != null && bundle.containsKey(ORDER_ALPHABETICALLY) && bundle.getBoolean(ORDER_ALPHABETICALLY);
            requiresTableAnnotation = bundle != null && bundle.containsKey(REQUIRE_TABLE_ANNOTATION_KEY) && bundle.getBoolean(REQUIRE_TABLE_ANNOTATION_KEY);
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
    public boolean requireTableAnnotation() {
        return requiresTableAnnotation;
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
