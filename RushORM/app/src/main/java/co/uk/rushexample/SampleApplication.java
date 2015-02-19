package co.uk.rushexample;

import android.app.Application;

import co.uk.rushorm.android.RushAndroid;

/**
 * Created by stuartc on 11/12/14.
 */
public class SampleApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        // Commented out because it was interfering with unit tests
        //RushAndroid.initialize(getApplicationContext());
    }

}
