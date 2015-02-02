package co.uk.rushorm.core;

import java.util.List;

/**
 * Created by Stuart on 10/12/14.
 */
public interface RushStatementGenerator {

    public interface Callback {
        public void addRush(Rush rush, long id);
        public void removeRush(Rush rush);
        public long lastIdInTable(String sql);
        public void createdOrUpdateStatement(String sql);
        public void deleteStatement(String sql);
    }

    public void generateSaveOrUpdate(List<? extends Rush> objects, Callback saveCallback);
    public void generateDelete(List<? extends Rush> objects, Callback deleteCallback);

}
