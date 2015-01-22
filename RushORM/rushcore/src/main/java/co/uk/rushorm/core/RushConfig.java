package co.uk.rushorm.core;

/**
 * Created by stuartc on 11/12/14.
 */
public interface RushConfig {
    public String dbName();
    public int dbVersion();
    public boolean firstRun();
    public boolean upgrade();
    public boolean inDebug();
    public boolean requireTableAnnotation();
}
