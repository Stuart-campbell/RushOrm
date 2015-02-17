package co.uk.rushorm.core;

import java.util.List;

/**
 * Created by Stuart on 10/12/14.
 */
public interface RushSaveStatementGenerator {

    public void generateSaveOrUpdate(List<? extends Rush> objects, RushSaveStatementGeneratorCallback saveCallback);

}
