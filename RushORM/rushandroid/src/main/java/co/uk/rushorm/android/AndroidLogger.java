package co.uk.rushorm.android;

import android.util.Log;

import co.uk.rushorm.core.Logger;

/**
 * Created by Stuart on 11/12/14.
 */
public class AndroidLogger implements Logger {

    private static final String TAG = "RushOrm";

    @Override
    public void log(String message) {
        Log.i(TAG, message);
    }

    @Override
    public void logSql(String sql) {
        Log.d(TAG, sql);
    }

    @Override
    public void logError(String message) {
        Log.e(TAG, message);
    }
}
