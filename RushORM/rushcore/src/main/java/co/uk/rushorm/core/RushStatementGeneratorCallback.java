package co.uk.rushorm.core;

/**
 * Created by Stuart on 16/02/15.
 */
public interface RushStatementGeneratorCallback {
    public void deleteStatement(String sql);
    public RushMetaData getMetaData(Rush rush);
}
