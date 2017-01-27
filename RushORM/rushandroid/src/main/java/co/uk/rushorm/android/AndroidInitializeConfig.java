package co.uk.rushorm.android;

import android.content.Context;

import java.util.List;

import co.uk.rushorm.core.Logger;
import co.uk.rushorm.core.Rush;
import co.uk.rushorm.core.RushClassFinder;
import co.uk.rushorm.core.RushConfig;
import co.uk.rushorm.core.RushInitializeConfig;
import co.uk.rushorm.core.RushObjectDeserializer;
import co.uk.rushorm.core.RushObjectSerializer;
import co.uk.rushorm.core.RushQueProvider;
import co.uk.rushorm.core.RushStatementRunner;
import co.uk.rushorm.core.RushStringSanitizer;
import co.uk.rushorm.core.RushTextFile;

/**
 * Created by Stuart on 20/06/15.
 */
public class AndroidInitializeConfig extends RushInitializeConfig {

    private final Context context;

    private final List<Class<? extends Rush>> clazzes;

    public AndroidInitializeConfig(Context context, List<Class<? extends Rush>> clazzes) {
        if(clazzes == null || clazzes.size() == 0) {
            throw new RushNoClassesSetException();
        }

        this.context = context.getApplicationContext();
        this.clazzes = clazzes;
        this.clazzes.add(RushTextFile.class);
        this.clazzes.add(RushJSONFile.class);
        this.clazzes.add(RushBitmapFile.class);
    }

    @Override
    public RushClassFinder getRushClassFinder() {
        if(rushClassFinder == null) {
            rushClassFinder = new AndroidRushClassFinder(clazzes);
        }
        return rushClassFinder;
    }

    @Override
    public RushStatementRunner getRushStatementRunner() {
        if(rushStatementRunner == null) {
            rushStatementRunner = new AndroidRushStatementRunner(context, getRushConfig().dbName(), getRushConfig());
        }
        return rushStatementRunner;
    }

    @Override
    public RushQueProvider getRushQueProvider() {
        if(rushQueProvider == null) {
            rushQueProvider = new AndroidRushQueProvider();
        }
        return rushQueProvider;
    }

    @Override
    public RushConfig getRushConfig() {
        if(rushConfig == null) {
            rushConfig = new AndroidRushConfig(context);
        }
        return rushConfig;
    }

    @Override
    public Logger getRushLogger() {
        if(rushLogger == null) {
            rushLogger = new AndroidLogger(getRushConfig());
        }
        return rushLogger;
    }

    @Override
    public RushStringSanitizer getRushStringSanitizer() {
        if(rushStringSanitizer == null) {
            rushStringSanitizer = new AndroidRushStringSanitizer();
        }
        return rushStringSanitizer;
    }

    @Override
    public RushObjectSerializer getRushObjectSerializer() {
        if(rushObjectSerializer == null){
            rushObjectSerializer = new AndroidJSONSerializer(rushConfig);
        }
        return rushObjectSerializer;
    }

    @Override
    public RushObjectDeserializer getRushObjectDeserializer() {
        if(rushObjectDeserializer == null){
            rushObjectDeserializer = new AndroidJSONDeserializer(rushConfig);
        }
        return rushObjectDeserializer;
    }
}
