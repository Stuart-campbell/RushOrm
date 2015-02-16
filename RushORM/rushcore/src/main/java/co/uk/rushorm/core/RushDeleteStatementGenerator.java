package co.uk.rushorm.core;

import java.util.List;

/**
 * Created by Stuart on 16/02/15.
 */
public interface RushDeleteStatementGenerator {

    public interface Callback extends RushStatementGeneratorCallback{
        public void removeRush(Rush rush);
        public void deleteStatement(String sql);
    }

    public void generateDelete(List<? extends Rush> objects, Callback deleteCallback);

}
