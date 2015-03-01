package co.uk.rushexample;

import android.content.Context;

import co.uk.rushexample.testobjects.SetupObject;
import co.uk.rushorm.android.RushAndroid;

/**
 * Created by Stuart on 26/02/15.
 */
public class Utils {
    
    public static void setUp(Context context) throws InterruptedException {
        context.deleteDatabase("rush.db");
        RushAndroid.initialize(context);
        // Saving this object makes setUp wait until initialize finishes 
        // otherwise it seems that the thread initialize is done on gets killed
        new SetupObject().save();
    }
}
