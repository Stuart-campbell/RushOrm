package co.uk.rushorm.android;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import co.uk.rushorm.android.testobjects.Bug29A;
import co.uk.rushorm.android.testobjects.Bug29B;
import co.uk.rushorm.android.testobjects.Bug29C;
import co.uk.rushorm.android.testobjects.Bug34;
import co.uk.rushorm.android.testobjects.Bug6;
import co.uk.rushorm.android.testobjects.Bug61Child;
import co.uk.rushorm.android.testobjects.Bug61Parent;
import co.uk.rushorm.android.testobjects.Bug78;
import co.uk.rushorm.android.testobjects.Bug7Child;
import co.uk.rushorm.android.testobjects.Bug7Parent;
import co.uk.rushorm.android.testobjects.SetupObject;
import co.uk.rushorm.android.testobjects.TestBase1;
import co.uk.rushorm.android.testobjects.TestBase2;
import co.uk.rushorm.android.testobjects.TestChildObject;
import co.uk.rushorm.android.testobjects.TestCustomColumn;
import co.uk.rushorm.android.testobjects.TestCustomName;
import co.uk.rushorm.android.testobjects.TestModifiers;
import co.uk.rushorm.android.testobjects.TestObject;
import co.uk.rushorm.android.testobjects.TestPageListObject;
import co.uk.rushorm.android.testobjects.TestRootObject;
import co.uk.rushorm.android.testobjects.TestSelfReference;
import co.uk.rushorm.android.testobjects.TestUpgrade1;
import co.uk.rushorm.android.testobjects.TestUpgrade2;
import co.uk.rushorm.android.testobjects.TestUpgrade3;
import co.uk.rushorm.android.testobjects.TestUpgrade4;
import co.uk.rushorm.android.testobjects.TestUpgrade5;
import co.uk.rushorm.core.Rush;
import co.uk.rushorm.core.RushColumn;
import co.uk.rushorm.core.RushInitializeConfig;
import co.uk.rushorm.core.RushTextFile;

/**
 * Created by Stuart on 26/02/15.
 */
public class Utils {

    public static void setUp(Context context) throws InterruptedException {
        setUp(context, null);
    }

    public static void setUp(Context context, List<RushColumn> columns) throws InterruptedException {
        context.deleteDatabase("rush.db");

        RushInitializeConfig rushInitializeConfig = new AndroidInitializeConfig(context);
        final List<Class<? extends Rush>> classes = new ArrayList<>();

        classes.add(RushTextFile.class);
        classes.add(RushBitmapFile.class);
        classes.add(RushJSONFile.class);

        classes.add(Bug6.class);
        classes.add(Bug7Child.class);
        classes.add(Bug7Parent.class);
        classes.add(Bug29A.class);
        classes.add(Bug29B.class);
        classes.add(Bug29C.class);
        classes.add(Bug34.class);
        classes.add(Bug61Child.class);
        classes.add(Bug61Parent.class);
        classes.add(Bug78.class);

        classes.add(SetupObject.class);
        classes.add(TestBase1.class);
        classes.add(TestBase2.class);
        classes.add(TestChildObject.class);
        classes.add(TestCustomColumn.class);
        classes.add(TestCustomName.class);
        classes.add(TestModifiers.class);
        classes.add(TestObject.class);
        classes.add(TestPageListObject.class);
        classes.add(TestRootObject.class);
        classes.add(TestSelfReference.class);
        classes.add(TestUpgrade1.class);
        classes.add(TestUpgrade2.class);
        classes.add(TestUpgrade3.class);
        classes.add(TestUpgrade4.class);
        classes.add(TestUpgrade5.class);

        rushInitializeConfig.setClasses(classes);

        if(columns != null) {
            for (RushColumn rushColumn : columns) {
                rushInitializeConfig.addRushColumn(rushColumn);
            }
        }

        RushAndroid.initialize(rushInitializeConfig);

        // Saving this object makes setUp wait until initialize finishes
        // otherwise it seems that the thread initialize is done on gets killed
        new SetupObject().save();
    }
}
