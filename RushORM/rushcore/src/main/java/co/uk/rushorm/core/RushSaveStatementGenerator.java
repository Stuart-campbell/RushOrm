package co.uk.rushorm.core;

import java.util.List;
import java.util.Map;

/**
 * Created by Stuart on 10/12/14.
 */
public interface RushSaveStatementGenerator {

    public void generateSaveOrUpdate(List<? extends Rush> objects, Map<Class, AnnotationCache> annotationCache, RushStringSanitizer rushStringSanitizer, RushColumns rushColumns, RushSaveStatementGeneratorCallback saveCallback);

}
