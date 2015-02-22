package co.uk.rushorm.core;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import co.uk.rushorm.core.annotations.RushClassSerializationName;
import co.uk.rushorm.core.annotations.RushDisableAutodelete;
import co.uk.rushorm.core.annotations.RushIgnore;
import co.uk.rushorm.core.annotations.RushList;
import co.uk.rushorm.core.exceptions.RushListAnnotationDoesNotMatchClassException;

/**
 * Created by Stuart on 01/02/15.
 */
public class AnnotationCache {
    
    private final List<String> fieldToIgnore;
    private final List<String> disableAutoDelete;
    private final Map<String, Class> listsFields;
    private final String serializationName;
    
    public AnnotationCache(Class clazz, List<Field> fields) {
        
        if(clazz.isAnnotationPresent(RushClassSerializationName.class)) {
            RushClassSerializationName rushClassSerializationName = (RushClassSerializationName) clazz.getAnnotation(RushClassSerializationName.class);
            serializationName = rushClassSerializationName.name();
        } else {
            serializationName = clazz.getName();
        }
        
        listsFields = new HashMap<>();
        fieldToIgnore = new ArrayList<>();
        disableAutoDelete = new ArrayList<>();

        for(Field field : fields) {
            if(field.isAnnotationPresent(RushIgnore.class)) {
                fieldToIgnore.add(field.getName());
            } else {
                if (field.isAnnotationPresent(RushList.class)) {
                    try {
                        Class listClass = Class.forName(field.getAnnotation(RushList.class).classname());
                        listsFields.put(field.getName(), listClass);
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                        throw new RushListAnnotationDoesNotMatchClassException();
                    }
                }

                if (field.isAnnotationPresent(RushDisableAutodelete.class)) {
                    disableAutoDelete.add(field.getName());
                }
            }
        }
    }

    public List<String> getFieldToIgnore() {
        return fieldToIgnore;
    }

    public List<String> getDisableAutoDelete() {
        return disableAutoDelete;
    }

    public Map<String, Class> getListsFields() {
        return listsFields;
    }
    
    public String getSerializationName() {
        return serializationName;
    }
}
