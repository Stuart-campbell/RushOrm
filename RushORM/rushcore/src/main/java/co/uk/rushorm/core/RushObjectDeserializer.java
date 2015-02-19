package co.uk.rushorm.core;

import java.util.List;
import java.util.Map;

/**
 * Created by Stuart on 18/02/15.
 */
public interface RushObjectDeserializer {

    public interface Callback {
        public void addRush(Rush rush, RushMetaData rushMetaData);
    }
    public List<Rush> deserialize(String string, String idName, String versionName, RushColumns rushColumns, Map<Class, AnnotationCache> annotationCache, Callback callback);

}
