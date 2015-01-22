package co.uk.rushorm.core;

/**
 * Created by Stuart on 10/12/14.
 */
public interface RushStatementGenerator {

    public interface SaveCallback {
        public void statementCreatedForRush(String sql, RushTable rushTable);
        public void deleteJoinStatementCreated(String sql);
        public void joinStatementCreated(String sql);
    }

    public interface DeleteCallback {
        public void deleteJoinStatementCreated(String sql);
        public void statementCreatedForRush(String sql, RushTable rushTable);
        public void deleteChild(RushTable rushTable);
    }

    public void generateSaveOrUpdate(RushTable rushTable, SaveCallback saveCallback);
    public void generateDelete(RushTable rushTable, DeleteCallback deleteCallback);

}
