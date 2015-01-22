package co.uk.rushorm.android;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import co.uk.rushorm.core.Logger;
import co.uk.rushorm.core.RushClassFinder;
import co.uk.rushorm.core.RushColumn;
import co.uk.rushorm.core.RushCore;
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
        RushStringSanitizer rushStringSanitizer = new AndroidRushStringSanitizer();
        RushClassFinder rushClassFinder = new AndroidRushClassFinder(applicationContext);
        AndroidRushStatementRunner statementRunner = new AndroidRushStatementRunner(applicationContext, rushConfig.dbName(), rushConfig.dbVersion());
        rushConfig.setLastRunVersion(statementRunner.getLastRunVersion());
        RushQueProvider queProvider = new AndroidRushQueProvider();
        Logger logger = new AndroidLogger();

        RushCore.initialize(rushClassFinder, statementRunner, queProvider, rushConfig, rushStringSanitizer, logger, columns);
    }

}
