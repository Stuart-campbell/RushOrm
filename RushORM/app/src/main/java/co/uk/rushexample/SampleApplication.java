package co.uk.rushexample;

import android.app.Application;

import java.util.ArrayList;
import java.util.List;

import co.uk.rushexample.demo.Car;
import co.uk.rushexample.demo.Engine;
import co.uk.rushexample.demo.Wheel;
import co.uk.rushorm.android.AndroidInitializeConfig;
import co.uk.rushorm.android.RushAndroid;
import co.uk.rushorm.core.Rush;

/**
 * Created by stuartc on 11/12/14.
 */
public class SampleApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        List<Class<? extends Rush>> classes = new ArrayList<>();
        classes.add(Car.class);
        classes.add(Engine.class);
        classes.add(Wheel.class);

        AndroidInitializeConfig androidInitializeConfig = new AndroidInitializeConfig(getApplicationContext(), classes);

        RushAndroid.initialize(androidInitializeConfig);
    }

}
