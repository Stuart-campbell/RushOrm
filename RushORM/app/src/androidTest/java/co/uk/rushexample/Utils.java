package co.uk.rushexample;

import android.content.Context;

import co.uk.rushorm.android.RushAndroid;

/**
 * Created by Stuart on 26/02/15.
 */
public class Utils {
    
    public static void setUp(Context context) throws InterruptedException {
        context.deleteDatabase("rush.db");
        RushAndroid.initialize(context);
        Thread.sleep(500);
    }
}
