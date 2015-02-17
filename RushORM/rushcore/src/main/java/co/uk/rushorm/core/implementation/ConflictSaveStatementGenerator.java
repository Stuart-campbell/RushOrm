package co.uk.rushorm.core.implementation;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import co.uk.rushorm.core.AnnotationCache;
import co.uk.rushorm.core.Rush;
import co.uk.rushorm.core.RushColumns;
import co.uk.rushorm.core.RushConflict;
import co.uk.rushorm.core.RushConflictSaveStatementGenerator;
import co.uk.rushorm.core.RushDeleteStatementGenerator;
import co.uk.rushorm.core.RushMetaData;
import co.uk.rushorm.core.RushSaveStatementGeneratorCallback;
import co.uk.rushorm.core.RushStringSanitizer;

/**
 * Created by Stuart on 17/02/15.
 */
public class ConflictSaveStatementGenerator extends ReflectionSaveStatementGenerator implements RushConflictSaveStatementGenerator {

    private static final String SELECT_TEMPLATE = "SELECT * from %s" +
            "\nWHERE " + ReflectionUtils.RUSH_ID + "='%s';";

    public ConflictSaveStatementGenerator(RushStringSanitizer rushStringSanitizer, RushColumns rushColumns, Map<Class, AnnotationCache> annotationCache) {
        super(rushStringSanitizer, rushColumns, annotationCache);
    }

    @Override
    public void conflictsFromGenerateSaveOrUpdate(List<? extends Rush> objects, Callback saveCallback) {
        generateSaveOrUpdate(objects, saveCallback);
    }

    @Override
    protected void createOrUpdateObjects(Map<Class, List<BasicUpdate>> valuesMap, final Map<Class, List<String>> columnsMap, final RushSaveStatementGeneratorCallback saveCallback) {
        Callback callback = (Callback)saveCallback;

        List<Class> toRemove = new ArrayList<>();

        for (final Map.Entry<Class, List<BasicUpdate>> entry : valuesMap.entrySet()) {

            final List<BasicUpdate> creates = entry.getValue();
            Class clazz = entry.getKey();
            String sqlTemplate = String.format(SELECT_TEMPLATE, ReflectionUtils.tableNameForClass(clazz), "%s");

            Iterator<BasicUpdate> iterator = creates.iterator();
            checkForConflict(clazz, iterator, sqlTemplate, callback);

            if(creates.size() < 1) {
                toRemove.add(clazz);
            }
        }

        for(Class clazz : toRemove) {
            valuesMap.remove(clazz);
            columnsMap.remove(clazz);
        }

        if(valuesMap.size() > 0) {
            super.createOrUpdateObjects(valuesMap, columnsMap, saveCallback);
        }
    }

    private <T extends Rush> void checkForConflict(Class T, Iterator<BasicUpdate> iterator, String sqlTemplate, Callback callback) {

        BasicUpdate create = iterator.next();
        T inDatabase = callback.load(T, String.format(sqlTemplate, create.rushMetaData.getId()));
        if(inDatabase != null) {
            RushMetaData rushMetaData = callback.getMetaData(inDatabase);
            if(create.rushMetaData.getVersion() < rushMetaData.getVersion()) {
                iterator.remove();
                callback.conflictFound(new RushConflict<>(inDatabase, (T)create.object));
            }
        }
    }
}
