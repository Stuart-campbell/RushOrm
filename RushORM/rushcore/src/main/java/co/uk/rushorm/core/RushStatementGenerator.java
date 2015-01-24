package co.uk.rushorm.core;

/**
 * Created by Stuart on 10/12/14.
 */
public interface RushStatementGenerator {

    public interface SaveCallback {
        public void statementCreatedForRush(String sql, Rush rush);
        public void deleteJoinStatementCreated(String sql);
        public void joinStatementCreated(String sql);
    }

    public interface DeleteCallback {
        public void deleteJoinStatementCreated(String sql);
        public void statementCreatedForRush(String sql, Rush rush);
        public void deleteChild(Rush rush);
    }

    public void generateSaveOrUpdate(Rush rush, SaveCallback saveCallback);
    public void generateDelete(Rush rush, DeleteCallback deleteCallback);

}
