package co.uk.rushorm.core;

import java.util.List;
import java.util.Map;

/**
 * Created by Stuart on 18/02/15.
 */
public interface RushObjectSerializer {

    public interface Callback {
        public RushMetaData getMetaData(Rush rush);
    }

    public String serialize(List<? extends Rush> objects, RushColumns rushColumns, Map<Class, AnnotationCache> annotationCache, Callback callback);

}
