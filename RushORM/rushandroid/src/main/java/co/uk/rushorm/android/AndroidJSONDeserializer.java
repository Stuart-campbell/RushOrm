package co.uk.rushorm.android;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import co.uk.rushorm.core.AnnotationCache;
import co.uk.rushorm.core.Rush;
import co.uk.rushorm.core.RushColumns;
import co.uk.rushorm.core.RushMetaData;
import co.uk.rushorm.core.RushObjectDeserializer;
import co.uk.rushorm.core.implementation.ReflectionUtils;

/**
 * Created by Stuart on 18/02/15.
 */
public class AndroidJSONDeserializer implements RushObjectDeserializer {

    @Override
    public List<Rush> deserialize(String string, String idName, String versionName, RushColumns rushColumns, Map<Class, AnnotationCache> annotationCache, Callback callback) {

        try {
            JSONObject jsonObject = new JSONObject(string);
            Iterator<String> stringIterator = jsonObject.keys();

            List<Rush> objects = new ArrayList<>();
            while(stringIterator.hasNext()) {
                String className = stringIterator.next();
                Class clazz = classFromString(className, annotationCache);
                if(clazz != null) {
                    JSONArray jsonArray = jsonObject.getJSONArray(className);
                    objects.addAll(deserializeJSONArray(jsonArray, idName, versionName, clazz, rushColumns, annotationCache, callback));
                }
            }
            return objects;
        } catch (JSONException | InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }

        return null;
    }

    private Class classFromString(String name, Map<Class, AnnotationCache> annotationCache) {
        for (Map.Entry<Class, AnnotationCache> entry : annotationCache.entrySet()) {
            if(entry.getValue().getSerializationName().equals(name)) {
                return entry.getKey();
            }          
        }
        return null;
    }
    
    private List<Rush> deserializeJSONArray(JSONArray jsonArray, String idName, String versionName, Class<? extends Rush> clazz, RushColumns rushColumns, Map<Class, AnnotationCache> annotationCache, Callback callback) throws JSONException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        if(jsonArray.length() < 1){
            return null;
        }

        List<Rush> objects = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i ++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            objects.add(deserializeJSONObject(jsonObject, idName, versionName, clazz, rushColumns, annotationCache, callback));
        }
        return objects;
    }

    private Rush deserializeJSONObject(JSONObject object, String idName, String versionName, Class<? extends Rush> clazz, RushColumns rushColumns, Map<Class, AnnotationCache> annotationCache, Callback callback) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException, JSONException {

        List<Field> fields = new ArrayList<>();
        ReflectionUtils.getAllFields(fields, clazz);

        Rush rush = clazz.getConstructor().newInstance();

        for (Field field : fields) {
            if (!annotationCache.get(clazz).getFieldToIgnore().contains(field.getName()) && object.has(field.getName())) {
                field.setAccessible(true);
                if (Rush.class.isAssignableFrom(field.getType())) {
                    JSONObject childJSONObject = object.getJSONObject(field.getName());
                    Rush child = deserializeJSONObject(childJSONObject, idName, versionName, (Class<? extends Rush>) field.getType(), rushColumns, annotationCache, callback);
                    field.set(rush, child);
                } else if (annotationCache.get(clazz).getListsFields().containsKey(field.getName())) {
                    Class childClazz = annotationCache.get(clazz).getListsFields().get(field.getName());
                    JSONArray jsonArray = object.getJSONArray(field.getName());
                    List<Rush> children = deserializeJSONArray(jsonArray, idName, versionName, childClazz, rushColumns, annotationCache, callback);
                    field.set(rush, children);
                } else if (rushColumns.supportsField(field)) {
                    String value = object.getString(field.getName());
                    rushColumns.setField(rush, field, value);
                }
            }
        }
        if(object.has(idName)) {
            String id = object.getString(idName);
            long version = 1;
            if(object.has(versionName)) {
                version = object.getLong(versionName);
            }
            RushMetaData rushMetaData = new RushMetaData(id, version);
            callback.addRush(rush, rushMetaData);
        }
        return rush;
    }
}
