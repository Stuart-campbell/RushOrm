package co.uk.rushorm.core;

import java.util.List;

/**
 * Created by stuartc on 11/12/14.
 */
public interface RushTableStatementGenerator {

    public interface StatementCallback {
        public void StatementCreated(String statement);
    }

    public void generateStatements(List<Class> classes, StatementCallback statementCallback);

}
