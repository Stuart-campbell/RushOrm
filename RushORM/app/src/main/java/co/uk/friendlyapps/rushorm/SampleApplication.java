package co.uk.friendlyapps.rushorm;

import android.app.Application;

import co.uk.rushorm.android.RushAndroid;

/**
 * Created by stuartc on 11/12/14.
 */
public class SampleApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        RushAndroid.initialize(getApplicationContext());
    }

}
