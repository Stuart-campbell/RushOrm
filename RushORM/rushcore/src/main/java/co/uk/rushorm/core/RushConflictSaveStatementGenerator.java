package co.uk.rushorm.core;

import java.util.List;

/**
 * Created by Stuart on 17/02/15.
 */
public interface RushConflictSaveStatementGenerator {

    public interface Callback extends RushSaveStatementGeneratorCallback {
        public void conflictFound(RushConflict conflict);
        public <T extends Rush> T load(Class T, String sql);
    }

    public void conflictsFromGenerateSaveOrUpdate(List<? extends Rush> objects, Callback saveCallback);

}
