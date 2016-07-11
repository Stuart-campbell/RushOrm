package co.uk.rushexample;

import android.app.Application;

import co.uk.rushorm.android.AndroidInitializeConfig;
import co.uk.rushorm.android.RushAndroid;
import co.uk.rushorm.core.RushColumn;

/**
 * Created by stuartc on 11/12/14.
 */
public class SampleApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        AndroidInitializeConfig androidInitializeConfig = new AndroidInitializeConfig(getApplicationContext());
        androidInitializeConfig.addPackage("co.uk.rushexample.demo");
        RushAndroid.initialize(androidInitializeConfig);
    }

}
