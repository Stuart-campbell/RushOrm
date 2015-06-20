package co.uk.rushorm.android;

import android.content.Context;

import java.util.List;

import co.uk.rushorm.core.RushColumn;
import co.uk.rushorm.core.RushCore;
import co.uk.rushorm.core.RushInitializeConfig;

/**
 * Created by stuartc on 11/12/14.
 */
public class RushAndroid {

    public static void initialize(Context context) {
        initialize(new AndroidInitializeConfig(context));
    }

    public static void initialize(Context context, List<RushColumn> columns) {
        AndroidInitializeConfig androidInitializeConfig = new AndroidInitializeConfig(context);
        for(RushColumn rushColumn : columns) {
            androidInitializeConfig.addRushColumn(rushColumn);
        }
        initialize(androidInitializeConfig);
    }

    public static void initialize(RushInitializeConfig rushInitializeConfig){
        RushCore.initialize(rushInitializeConfig);
    }

}
