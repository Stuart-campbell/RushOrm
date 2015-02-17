package co.uk.rushorm.core;

/**
 * Created by Stuart on 17/02/15.
 */
public interface RushSaveStatementGeneratorCallback extends RushStatementGeneratorCallback {

    public void addRush(Rush rush, RushMetaData rushMetaData);
    public void createdOrUpdateStatement(String sql);

}
