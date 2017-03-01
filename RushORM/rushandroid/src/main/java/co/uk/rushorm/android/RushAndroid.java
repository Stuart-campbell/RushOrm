package co.uk.rushorm.android;

import android.content.Context;

import java.util.List;

import co.uk.rushorm.core.Rush;
import co.uk.rushorm.core.RushColumn;
import co.uk.rushorm.core.RushCore;
import co.uk.rushorm.core.RushInitializeConfig;

/**
 * Created by stuartc on 11/12/14.
 */
public class RushAndroid {

    public static void initialize(Context context, List<Class<? extends Rush>> clazzes) {
        initialize(new AndroidInitializeConfig(context, clazzes));
    }

    public static void initialize(Context context, List<Class<? extends Rush>> clazzes, List<RushColumn> columns) {
        AndroidInitializeConfig androidInitializeConfig = new AndroidInitializeConfig(context, clazzes);
        for(RushColumn rushColumn : columns) {
            androidInitializeConfig.addRushColumn(rushColumn);
        }
        initialize(androidInitializeConfig);
    }

    public static void initialize(RushInitializeConfig rushInitializeConfig){
        RushCore.initialize(rushInitializeConfig);
    }

}
