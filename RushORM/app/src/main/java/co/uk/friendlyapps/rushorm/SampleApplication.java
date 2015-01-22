package co.uk.friendlyapps.rushorm;

import android.app.Application;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import co.uk.rushorm.android.RushAndroid;
import co.uk.rushorm.core.RushColumn;
import co.uk.rushorm.core.RushStringSanitizer;
import co.uk.rushorm.core.RushTable;

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
