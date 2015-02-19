package co.uk.rushorm.core;

import java.util.List;
import java.util.Map;

/**
 * Created by Stuart on 14/12/14.
 */
public interface RushClassLoader {

    public interface LoadCallback {
        public RushStatementRunner.ValuesCallback runStatement(String string);
        public void didLoadObject(Rush rush, RushMetaData rushMetaData);
    }

    public <T extends Rush> List<T> loadClasses(Class<T> clazz, RushColumns rushColumns, Map<Class, AnnotationCache> annotationCache, RushStatementRunner.ValuesCallback valuesCallback, LoadCallback callback);

}
