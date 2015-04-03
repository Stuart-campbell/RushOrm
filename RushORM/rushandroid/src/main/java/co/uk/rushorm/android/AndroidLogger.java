package co.uk.rushorm.android;

import android.util.Log;

import co.uk.rushorm.core.Logger;
import co.uk.rushorm.core.RushConfig;

/**
 * Created by Stuart on 11/12/14.
 */
public class AndroidLogger implements Logger {

    private static final String TAG = "RushOrm";

    private final RushConfig rushConfig;

    public AndroidLogger(RushConfig rushConfig) {
        this.rushConfig = rushConfig;
    }

    @Override
    public void log(String message) {
        if(rushConfig.log() && message != null) {
            Log.i(TAG, message);
        }
    }

    @Override
    public void logSql(String sql) {
        if(rushConfig.log() && sql != null) {
            Log.d(TAG, sql);
        }
    }

    @Override
    public void logError(String message) {
        if(message != null) {
            Log.e(TAG, message);
        }
    }
}
