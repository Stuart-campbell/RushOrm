package co.uk.rushorm.android;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import co.uk.rushorm.core.Logger;
import co.uk.rushorm.core.RushClassFinder;
import co.uk.rushorm.core.RushColumn;
import co.uk.rushorm.core.RushCore;
import co.uk.rushorm.core.RushObjectDeserializer;
import co.uk.rushorm.core.RushObjectSerializer;
import co.uk.rushorm.core.RushQueProvider;
import co.uk.rushorm.core.RushStringSanitizer;

/**
 * Created by stuartc on 11/12/14.
 */
public class RushAndroid {

    public static void initialize(Context context) {
        initialize(context, new ArrayList<RushColumn>());
    }

    public static void initialize(Context context, List<RushColumn> columns) {
        Context applicationContext = context.getApplicationContext();

        AndroidRushConfig rushConfig = new AndroidRushConfig(applicationContext);
        Logger logger = new AndroidLogger(rushConfig);
        RushStringSanitizer rushStringSanitizer = new AndroidRushStringSanitizer();
        RushClassFinder rushClassFinder = new AndroidRushClassFinder(applicationContext, logger);
        AndroidRushStatementRunner statementRunner = new AndroidRushStatementRunner(applicationContext, rushConfig.dbName(), rushConfig);
        rushConfig.setLastRunVersion(statementRunner.getLastRunVersion());
        RushQueProvider queProvider = new AndroidRushQueProvider();
        RushObjectDeserializer rushObjectDeserializer = new AndroidJSONDeserializer();
        RushObjectSerializer rushObjectSerializer = new AndroidJSONSerializer();

        RushCore.initialize(rushClassFinder, statementRunner, queProvider, rushConfig, rushStringSanitizer, logger, columns, rushObjectSerializer, rushObjectDeserializer);
    }

}
