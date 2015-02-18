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

    private final String idName;
    private final String versionName;

    public AndroidJSONDeserializer(){
        idName = ReflectionUtils.RUSH_ID;
        versionName = ReflectionUtils.RUSH_VERSION;
    }

    public AndroidJSONDeserializer(String idName, String versionName){
        this.idName = idName;
        this.versionName = versionName;
    }

    @Override
    public List<Rush> deserialize(String string, RushColumns rushColumns, Map<Class, AnnotationCache> annotationCache, Callback callback) {

        try {
            JSONObject jsonObject = new JSONObject(string);
            Iterator<String> stringIterator = jsonObject.keys();

            List<Rush> objects = new ArrayList<>();
            while(stringIterator.hasNext()) {
                String className = stringIterator.next();
                Class clazz = Class.forName(className);
                JSONArray jsonArray = jsonObject.getJSONArray(className);
                objects.addAll(deserializeJSONArray(jsonArray, clazz, rushColumns, annotationCache, callback));
            }
            return objects;
        } catch (JSONException | ClassNotFoundException | InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }

        return null;
    }

    public List<Rush> deserializeJSONArray(JSONArray jsonArray, Class<? extends Rush> clazz, RushColumns rushColumns, Map<Class, AnnotationCache> annotationCache, Callback callback) throws JSONException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        if(jsonArray.length() < 1){
            return null;
        }

        List<Rush> objects = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i ++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            objects.add(deserializeJSONObject(jsonObject, clazz, rushColumns, annotationCache, callback));
        }
        return objects;
    }

    public Rush deserializeJSONObject(JSONObject object, Class<? extends Rush> clazz, RushColumns rushColumns, Map<Class, AnnotationCache> annotationCache, Callback callback) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException, JSONException {

        List<Field> fields = new ArrayList<>();
        ReflectionUtils.getAllFields(fields, clazz);

        if (!annotationCache.containsKey(clazz)) {
            annotationCache.put(clazz, new AnnotationCache(clazz, fields));
        }

        Rush rush = clazz.getConstructor().newInstance();

        for (Field field : fields) {
            if (!annotationCache.get(clazz).getFieldToIgnore().contains(field.getName()) && object.has(field.getName())) {
                field.setAccessible(true);
                if (Rush.class.isAssignableFrom(field.getType())) {
                    JSONObject childJSONObject = object.getJSONObject(field.getName());
                    Rush child = deserializeJSONObject(childJSONObject, (Class<? extends Rush>) field.getType(), rushColumns, annotationCache, callback);
                    field.set(rush, child);
                } else if (annotationCache.get(clazz).getListsFields().containsKey(field.getName())) {
                    Class childClazz = annotationCache.get(clazz).getListsFields().get(field.getName());
                    JSONArray jsonArray = object.getJSONArray(field.getName());
                    List<Rush> children = deserializeJSONArray(jsonArray, childClazz, rushColumns, annotationCache, callback);
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
