package co.uk.rushorm.core;

import java.util.List;

/**
 * Created by Stuart on 10/12/14.
 */
public interface RushSaveStatementGenerator {

    public interface Callback extends RushStatementGeneratorCallback{
        public void addRush(Rush rush, RushMetaData rushMetaData);
        public void createdOrUpdateStatement(String sql);
    }

    public void generateSaveOrUpdate(List<? extends Rush> objects, Callback saveCallback);

}
