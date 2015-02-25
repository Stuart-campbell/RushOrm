package co.uk.rushorm.core;

/**
 * Created by Stuart on 11/12/14.
 */
public interface Logger {

    public void log(String message);
    public void logSql(String sql);
    public void logError(String error);
}
