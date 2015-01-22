package co.uk.rushorm.core;

import java.util.List;

/**
 * Created by Stuart on 10/12/14.
 */
public interface RushUpgradeManager {

    public interface UpgradeCallback {
        public RushStatementRunner.ValuesCallback runStatement(String sql);
        public void runRaw(String sql);
        public void createClasses(List<Class> missingClasses);
    }

    public void upgrade(List<Class> classList, UpgradeCallback callback);

}
