package co.uk.rushorm.android;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import co.uk.rushorm.core.AnnotationCache;
import co.uk.rushorm.core.Rush;
import co.uk.rushorm.core.RushColumns;
import co.uk.rushorm.core.RushConfig;
import co.uk.rushorm.core.RushMetaData;
import co.uk.rushorm.core.RushObjectDeserializer;
import co.uk.rushorm.core.implementation.ReflectionUtils;

/**
 * Created by Stuart on 18/02/15.
 */
public class AndroidJSONDeserializer implements RushObjectDeserializer {

    private final RushConfig rushConfig;

    public AndroidJSONDeserializer(RushConfig rushConfig) {
        this.rushConfig = rushConfig;
    }

    @Override
    public <T extends Rush> List<T> deserialize(String string, String idName, String versionName, RushColumns rushColumns, Map<Class<? extends Rush>, AnnotationCache> annotationCache, Class<T> clazz, Callback callback) {

        List<T> objects = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(string);
            Iterator<String> stringIterator = jsonObject.keys();

            while(stringIterator.hasNext()) {
                String className = stringIterator.next();
                Class<? extends Rush> objectClazz = classFromString(className, annotationCache);
                if(clazz != null) {
                    JSONArray jsonArray = jsonObject.getJSONArray(className);
                    List<? extends Rush> objectList = deserializeJSONArray(jsonArray, idName, versionName, objectClazz, ArrayList.class, rushColumns, annotationCache, callback);
                    objects.addAll((List<? extends T>) objectList);
                }
            }

        } catch (JSONException | InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }

        return objects;
    }

    private Class<? extends Rush> classFromString(String name, Map<Class<? extends Rush>, AnnotationCache> annotationCache) {
        for (Map.Entry<Class<? extends Rush>, AnnotationCache> entry : annotationCache.entrySet()) {
            if(entry.getValue().getSerializationName().equals(name)) {
                return entry.getKey();
            }          
        }
        return null;
    }
    
    private <T extends Rush> List<T> deserializeJSONArray(JSONArray jsonArray, String idName, String versionName, Class<T> clazz, Class<? extends List> listClazz, RushColumns rushColumns, Map<Class<? extends Rush>, AnnotationCache> annotationCache, Callback callback) throws JSONException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        if(jsonArray.length() < 1){
            return null;
        }

        List<T> objects;
        try {
            Constructor<? extends List> constructor = listClazz.getConstructor();
            objects = constructor.newInstance();
        } catch (InstantiationException e){
            objects = new ArrayList<>();
        } catch (InvocationTargetException e) {
            objects = new ArrayList<>();
        } catch (NoSuchMethodException e) {
            objects = new ArrayList<>();
        }

        for (int i = 0; i < jsonArray.length(); i ++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            objects.add(deserializeJSONObject(jsonObject, idName, versionName, clazz, rushColumns, annotationCache, callback));
        }
        return objects;
    }

    private <T extends Rush> T deserializeJSONObject(JSONObject object, String idName, String versionName, Class<T> clazz, RushColumns rushColumns, Map<Class<? extends Rush>, AnnotationCache> annotationCache, Callback callback) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException, JSONException {

        List<Field> fields = new ArrayList<>();
        ReflectionUtils.getAllFields(fields, clazz, rushConfig.orderColumnsAlphabetically());

        T rush = clazz.getConstructor().newInstance();

        for (Field field : fields) {
            if (!annotationCache.get(clazz).getFieldToIgnore().contains(field.getName()) && object.has(field.getName())) {
                field.setAccessible(true);
                if (Rush.class.isAssignableFrom(field.getType())) {
                    JSONObject childJSONObject = object.getJSONObject(field.getName());
                    Rush child = deserializeJSONObject(childJSONObject, idName, versionName, (Class<? extends Rush>) field.getType(), rushColumns, annotationCache, callback);
                    field.set(rush, child);
                } else if (annotationCache.get(clazz).getListsClasses().containsKey(field.getName())) {
                    Class childClazz = annotationCache.get(clazz).getListsClasses().get(field.getName());
                    Class listClazz = annotationCache.get(clazz).getListsTypes().get(field.getName());
                    JSONArray jsonArray = object.getJSONArray(field.getName());
                    List<? extends Rush> children = deserializeJSONArray(jsonArray, idName, versionName, childClazz, listClazz, rushColumns, annotationCache, callback);
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
