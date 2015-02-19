package co.uk.rushorm.android;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import co.uk.rushorm.core.AnnotationCache;
import co.uk.rushorm.core.Rush;
import co.uk.rushorm.core.RushColumns;
import co.uk.rushorm.core.RushMetaData;
import co.uk.rushorm.core.RushObjectSerializer;
import co.uk.rushorm.core.RushStringSanitizer;
import co.uk.rushorm.core.implementation.ReflectionUtils;

/**
 * Created by Stuart on 18/02/15.
 */
public class AndroidJSONSerializer implements RushObjectSerializer {

    private static final RushStringSanitizer rushStringSanitizer = new RushStringSanitizer() {
        @Override
        public String sanitize(String string) {
            return string;
        }
    };

    @Override
    public String serialize(List<? extends Rush> objects, String idName, String versionName, RushColumns rushColumns, Map<Class, AnnotationCache> annotationCache, Callback callback) {

        Map<Class, JSONArray> arraysMap = new HashMap<>();

        for(Rush rush : objects) {
            try {
                if(!arraysMap.containsKey(rush.getClass())) {
                    arraysMap.put(rush.getClass(), new JSONArray());
                }
                arraysMap.get(rush.getClass()).put(serializeToJSONObject(rush, idName, versionName, rushColumns, annotationCache, rushStringSanitizer, callback));
            } catch (JSONException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        JSONObject jsonObject = new JSONObject();
        for (Map.Entry<Class, JSONArray> entry : arraysMap.entrySet())
        {
            try {
                jsonObject.put(entry.getKey().getName(), entry.getValue());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return jsonObject.toString();
    }

    public JSONArray serializeToJSONArray(List<? extends Rush> objects, String idName, String versionName, RushColumns rushColumns, Map<Class, AnnotationCache> annotationCache, RushStringSanitizer rushStringSanitizer, Callback callback) throws JSONException, IllegalAccessException {

        JSONArray jsonArray = new JSONArray();
        if(objects != null) {
            for (Rush rush : objects) {
                jsonArray.put(serializeToJSONObject(rush, idName, versionName, rushColumns, annotationCache, rushStringSanitizer, callback));
            }
        }
        return jsonArray;
    }

    public JSONObject serializeToJSONObject(Rush rush, String idName, String versionName, RushColumns rushColumns, Map<Class, AnnotationCache> annotationCache, RushStringSanitizer rushStringSanitizer, Callback callback) throws IllegalAccessException, JSONException {

        if(rush == null) {
            return null;
        }

        JSONObject jsonObject = new JSONObject();
        RushMetaData rushMetaData = callback.getMetaData(rush);
        if (rushMetaData != null) {
            jsonObject.put(idName, rushMetaData.getId());
            jsonObject.put(versionName, rushMetaData.getVersion());
        }

        List<Field> fields = new ArrayList<>();
        ReflectionUtils.getAllFields(fields, rush.getClass());

        if (!annotationCache.containsKey(rush.getClass())) {
            annotationCache.put(rush.getClass(), new AnnotationCache(rush.getClass(), fields));
        }

        for (Field field : fields) {
            if (!annotationCache.get(rush.getClass()).getFieldToIgnore().contains(field.getName())) {
                field.setAccessible(true);
                if (Rush.class.isAssignableFrom(field.getType())) {
                    JSONObject object = serializeToJSONObject((Rush) field.get(rush), idName, versionName, rushColumns, annotationCache, rushStringSanitizer, callback);
                    jsonObject.put(field.getName(), object);
                } else if (annotationCache.get(rush.getClass()).getListsFields().containsKey(field.getName())) {
                    JSONArray array = serializeToJSONArray((List<Rush>) field.get(rush), idName, versionName, rushColumns, annotationCache, rushStringSanitizer, callback);
                    jsonObject.put(field.getName(), array);
                } else if (rushColumns.supportsField(field)) {
                    String value = rushColumns.valueFromField(rush, field, rushStringSanitizer);
                    jsonObject.put(field.getName(), value);
                }
            }
        }
        return jsonObject;
    }
}
