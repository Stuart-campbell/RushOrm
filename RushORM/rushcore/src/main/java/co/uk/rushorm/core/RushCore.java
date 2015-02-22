package co.uk.rushorm.core;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import co.uk.rushorm.core.exceptions.RushCoreNotInitializedException;
import co.uk.rushorm.core.exceptions.RushTableMissingEmptyConstructorException;
import co.uk.rushorm.core.implementation.ConflictSaveStatementGenerator;
import co.uk.rushorm.core.implementation.ReflectionClassLoader;
import co.uk.rushorm.core.implementation.ReflectionDeleteStatementGenerator;
import co.uk.rushorm.core.implementation.ReflectionSaveStatementGenerator;
import co.uk.rushorm.core.implementation.ReflectionTableStatementGenerator;
import co.uk.rushorm.core.implementation.ReflectionUpgradeManager;
import co.uk.rushorm.core.implementation.ReflectionUtils;
import co.uk.rushorm.core.implementation.RushColumnBoolean;
import co.uk.rushorm.core.implementation.RushColumnDate;
import co.uk.rushorm.core.implementation.RushColumnDouble;
import co.uk.rushorm.core.implementation.RushColumnFloat;
import co.uk.rushorm.core.implementation.RushColumnInt;
import co.uk.rushorm.core.implementation.RushColumnLong;
import co.uk.rushorm.core.implementation.RushColumnShort;
import co.uk.rushorm.core.implementation.RushColumnString;
import co.uk.rushorm.core.implementation.RushColumnsImplementation;

/**
 * Created by Stuart on 10/12/14.
 */
public class RushCore {

    public static void initialize(RushClassFinder rushClassFinder, RushStatementRunner statementRunner, RushQueProvider queProvider, RushConfig rushConfig, RushStringSanitizer rushStringSanitizer, Logger logger, List<RushColumn> columns, RushObjectSerializer rushObjectSerializer, RushObjectDeserializer rushObjectDeserializer) {

        columns.add(new RushColumnBoolean());
        columns.add(new RushColumnDate());
        columns.add(new RushColumnDouble());
        columns.add(new RushColumnInt());
        columns.add(new RushColumnLong());
        columns.add(new RushColumnShort());
        columns.add(new RushColumnFloat());
        columns.add(new RushColumnString());

        RushColumns rushColumns = new RushColumnsImplementation(columns);

        RushUpgradeManager rushUpgradeManager = new ReflectionUpgradeManager();
        Map<Class, AnnotationCache> annotationCache = new HashMap<>();
        RushSaveStatementGenerator saveStatementGenerator = new ReflectionSaveStatementGenerator();
        RushConflictSaveStatementGenerator conflictSaveStatementGenerator = new ConflictSaveStatementGenerator();
        RushDeleteStatementGenerator deleteStatementGenerator = new ReflectionDeleteStatementGenerator();
        RushTableStatementGenerator rushTableStatementGenerator = new ReflectionTableStatementGenerator();
        RushClassLoader rushClassLoader = new ReflectionClassLoader();

        initialize(rushUpgradeManager, saveStatementGenerator, conflictSaveStatementGenerator, deleteStatementGenerator, rushClassFinder, rushTableStatementGenerator, statementRunner, queProvider, rushConfig, rushClassLoader, rushStringSanitizer, logger, rushObjectSerializer, rushObjectDeserializer, rushColumns, annotationCache);
    }

    public static void initialize(RushUpgradeManager rushUpgradeManager,
                                  RushSaveStatementGenerator saveStatementGenerator,
                                  RushConflictSaveStatementGenerator rushConflictSaveStatementGenerator,
                                  RushDeleteStatementGenerator deleteStatementGenerator,
                                  RushClassFinder rushClassFinder,
                                  RushTableStatementGenerator rushTableStatementGenerator,
                                  RushStatementRunner statementRunner,
                                  RushQueProvider queProvider,
                                  RushConfig rushConfig,
                                  RushClassLoader rushClassLoader,
                                  RushStringSanitizer rushStringSanitizer,
                                  Logger logger,
                                  RushObjectSerializer rushObjectSerializer,
                                  RushObjectDeserializer rushObjectDeserializer,
                                  RushColumns rushColumns,
                                  Map<Class, AnnotationCache> annotationCache) {

        rushCore = new RushCore(saveStatementGenerator, rushConflictSaveStatementGenerator, deleteStatementGenerator, statementRunner, queProvider, rushConfig, rushTableStatementGenerator, rushClassLoader, rushStringSanitizer, logger, rushObjectSerializer, rushObjectDeserializer, rushColumns, annotationCache);
        rushCore.loadAnnotationCache(rushClassFinder);
        
        RushQue que = queProvider.blockForNextQue();
        if (rushConfig.firstRun()) {
            rushCore.createTables(new ArrayList<>(rushCore.annotationCache.keySet()), que);
        } else if(rushConfig.inDebug() || rushConfig.upgrade()){
            rushCore.upgrade(new ArrayList<>(rushCore.annotationCache.keySet()), rushUpgradeManager, que);
        } else {
            queProvider.queComplete(que);
        }
    }

    public static RushCore getInstance() {
        if (rushCore == null) {
            throw new RushCoreNotInitializedException();
        }
        return rushCore;
    }

    public RushMetaData getMetaData(Rush rush) {
        return idTable.get(rush);
    }
    
    public String getId(Rush rush) {
        RushMetaData rushMetaData = getMetaData(rush);
        if (rushMetaData == null) {
            return null;
        }
        return rushMetaData.getId();
    }

    public void save(Rush rush) {
        List<Rush> objects = new ArrayList<>();
        objects.add(rush);
        save(objects);
    }

    public void save(List<? extends Rush> objects) {
        RushQue que = queProvider.blockForNextQue();
        save(objects, que);
    }

    public void save(final Rush rush, final RushCallback callback) {
        List<Rush> objects = new ArrayList<>();
        objects.add(rush);
        save(objects, callback);
    }

    public void save(final List<? extends Rush> objects, final RushCallback callback) {
        queProvider.waitForNextQue(new RushQueProvider.RushQueCallback() {
            @Override
            public void callback(RushQue rushQue) {
                save(objects, rushQue);
                if(callback != null) {
                    callback.complete();
                }
            }
        });
    }

    public <T extends Rush> List<T> load(Class<T> clazz, String sql) {
        RushQue que = queProvider.blockForNextQue();
        return load(clazz, sql, que);
    }

    public <T extends Rush> void load(final Class<T> clazz, final String sql, final RushSearchCallback<T> callback) {
        queProvider.waitForNextQue(new RushQueProvider.RushQueCallback() {
            @Override
            public void callback(RushQue rushQue) {
                callback.complete(load(clazz, sql, rushQue));
            }
        });
    }

    public void delete(Rush rush) {
        List<Rush> objects = new ArrayList<>();
        objects.add(rush);
        delete(objects);
    }

    public void delete(List<? extends Rush> objects) {
        RushQue que = queProvider.blockForNextQue();
        delete(objects, que);
    }

    public void delete(final Rush rush, final RushCallback callback) {
        List<Rush> objects = new ArrayList<>();
        objects.add(rush);
        delete(objects, callback);
    }

    public void delete(final List<? extends Rush> objects, final RushCallback callback) {
        queProvider.waitForNextQue(new RushQueProvider.RushQueCallback() {
            @Override
            public void callback(RushQue rushQue) {
                delete(objects, rushQue);
                if(callback != null) {
                    callback.complete();
                }
            }
        });
    }

    public List<RushConflict> saveOnlyWithoutConflict(Rush rush) {
        List<Rush> objects = new ArrayList<>();
        objects.add(rush);
        return saveOnlyWithoutConflict(objects);
    }

    public List<RushConflict> saveOnlyWithoutConflict(List<? extends Rush> objects) {
        RushQue que = queProvider.blockForNextQue();
        return saveOnlyWithoutConflict(objects, que);
    }

    public void saveOnlyWithoutConflict(final Rush rush, final RushConflictCallback callback) {
        List<Rush> objects = new ArrayList<>();
        objects.add(rush);
        saveOnlyWithoutConflict(objects, callback);
    }

    public void saveOnlyWithoutConflict(final List<? extends Rush> objects, final RushConflictCallback callback) {
        queProvider.waitForNextQue(new RushQueProvider.RushQueCallback() {
            @Override
            public void callback(RushQue rushQue) {
                List<RushConflict> conflicts = saveOnlyWithoutConflict(objects, rushQue);
                if(callback != null) {
                    callback.complete(conflicts);
                }
            }
        });
    }

    public String serialize(List<? extends Rush> rush) {
        return serialize(rush, ReflectionUtils.RUSH_ID);
    }

    public String serialize(List<? extends Rush> rush, String idName) {
        return serialize(rush, idName, ReflectionUtils.RUSH_VERSION);
    }

    public String serialize(List<? extends Rush> rush, String idName, String versionName) {
        return rushObjectSerializer.serialize(rush, idName, versionName, rushColumns, annotationCache, new RushObjectSerializer.Callback() {
            @Override
            public RushMetaData getMetaData(Rush rush) {
                return idTable.get(rush);
            }
        });
    }

    public List<Rush> deserialize(String string) {
        return deserialize(string, ReflectionUtils.RUSH_ID);
    }

    public List<Rush> deserialize(String string, String idName) {
        return deserialize(string, idName, ReflectionUtils.RUSH_VERSION);
    }

    public List<Rush> deserialize(String string, String idName, String versionName) {
        return rushObjectDeserializer.deserialize(string, idName, versionName, rushColumns, annotationCache, new RushObjectDeserializer.Callback() {
            @Override
            public void addRush(Rush rush, RushMetaData rushMetaData) {
                idTable.put(rush, rushMetaData);
            }
        });
    }

    /* protected */
    protected String sanitize(String string) {
        return rushStringSanitizer.sanitize(string);
    }

    /* private */
    private static RushCore rushCore;
    private final Map<Rush, RushMetaData> idTable = new WeakHashMap<>();

    private final RushSaveStatementGenerator saveStatementGenerator;
    private final RushConflictSaveStatementGenerator rushConflictSaveStatementGenerator;
    private final RushDeleteStatementGenerator deleteStatementGenerator;
    private final RushStatementRunner statementRunner;
    private final RushQueProvider queProvider;
    private final RushConfig rushConfig;
    private final RushTableStatementGenerator rushTableStatementGenerator;
    private final RushClassLoader rushClassLoader;
    private final Logger logger;
    private final RushStringSanitizer rushStringSanitizer;
    private final RushObjectSerializer rushObjectSerializer;
    private final RushObjectDeserializer rushObjectDeserializer;
    private final RushColumns rushColumns;
    private final Map<Class, AnnotationCache> annotationCache;


    private RushCore(RushSaveStatementGenerator saveStatementGenerator,
                     RushConflictSaveStatementGenerator rushConflictSaveStatementGenerator,
                     RushDeleteStatementGenerator deleteStatementGenerator,
                     RushStatementRunner statementRunner,
                     RushQueProvider queProvider,
                     RushConfig rushConfig,
                     RushTableStatementGenerator rushTableStatementGenerator,
                     RushClassLoader rushClassLoader,
                     RushStringSanitizer rushStringSanitizer,
                     Logger logger,
                     RushObjectSerializer rushObjectSerializer,
                     RushObjectDeserializer rushObjectDeserializer,
                     RushColumns rushColumns,
                     Map<Class, AnnotationCache> annotationCache) {

        this.saveStatementGenerator = saveStatementGenerator;
        this.rushConflictSaveStatementGenerator = rushConflictSaveStatementGenerator;
        this.deleteStatementGenerator = deleteStatementGenerator;
        this.statementRunner = statementRunner;
        this.queProvider = queProvider;
        this.rushConfig = rushConfig;
        this.rushTableStatementGenerator = rushTableStatementGenerator;
        this.rushClassLoader = rushClassLoader;
        this.rushStringSanitizer = rushStringSanitizer;
        this.logger = logger;
        this.rushObjectSerializer = rushObjectSerializer;
        this.rushObjectDeserializer = rushObjectDeserializer;
        this.rushColumns = rushColumns;
        this.annotationCache = annotationCache;
    }
    
    private void loadAnnotationCache(RushClassFinder rushClassFinder) {
        for(Class clazz : rushClassFinder.findClasses(rushConfig)) {
            List<Field> fields = new ArrayList<>();
            ReflectionUtils.getAllFields(fields, clazz);
            annotationCache.put(clazz, new AnnotationCache(clazz, fields));
        }       
    }

    private void createTables(List<Class> classes, final RushQue que) {
        rushTableStatementGenerator.generateStatements(classes, rushColumns, new RushTableStatementGenerator.StatementCallback() {
            @Override
            public void statementCreated(String statement) {
                logger.logSql(statement);
                statementRunner.runRaw(statement, que);
            }
        });
        queProvider.queComplete(que);
    }

    private void upgrade(List<Class> classes, RushUpgradeManager rushUpgradeManager, final RushQue que) {
        rushUpgradeManager.upgrade(classes, new RushUpgradeManager.UpgradeCallback() {
            @Override
            public RushStatementRunner.ValuesCallback runStatement(String sql) {
                logger.logSql(sql);
                return statementRunner.runGet(sql, que);
            }

            @Override
            public void runRaw(String sql) {
                logger.logSql(sql);
                statementRunner.runRaw(sql, que);
            }

            @Override
            public void createClasses(List<Class> missingClasses) {
                createTables(missingClasses, que);
            }
        });
        queProvider.queComplete(que);
    }

    private void save(List<? extends Rush> objects, final RushQue que) {
        statementRunner.startTransition(que);
        saveStatementGenerator.generateSaveOrUpdate(objects, annotationCache, rushStringSanitizer, rushColumns, new RushSaveStatementGeneratorCallback() {
            @Override
            public void addRush(Rush rush, RushMetaData rushMetaData) {
                RushCore.this.addRush(rush, rushMetaData);
            }

            @Override
            public void createdOrUpdateStatement(String sql) {
                logger.logSql(sql);
                statementRunner.runRaw(sql, que);
            }

            @Override
            public void deleteStatement(String sql) {
                logger.logSql(sql);
                statementRunner.runRaw(sql, que);
            }

            @Override
            public RushMetaData getMetaData(Rush rush) {
                return idTable.get(rush);
            }
        });
        statementRunner.endTransition(que);
        queProvider.queComplete(que);
    }

    private List<RushConflict> saveOnlyWithoutConflict(List<? extends Rush> objects, final RushQue que) {
        final List<RushConflict> conflicts = new ArrayList<>();
        statementRunner.startTransition(que);
        rushConflictSaveStatementGenerator.conflictsFromGenerateSaveOrUpdate(objects, annotationCache, rushStringSanitizer, rushColumns, new RushConflictSaveStatementGenerator.Callback() {
            @Override
            public void conflictFound(RushConflict conflict) {
                conflicts.add(conflict);
            }

            @Override
            public <T extends Rush> T load(Class T, String sql) {
                List<T> objects = RushCore.this.load(T, sql, que);
                return objects.size() > 0 ? objects.get(0) : null;
            }

            @Override
            public void addRush(Rush rush, RushMetaData rushMetaData) {
                RushCore.this.addRush(rush, rushMetaData);
            }

            @Override
            public void createdOrUpdateStatement(String sql) {
                logger.logSql(sql);
                statementRunner.runRaw(sql, que);
            }

            @Override
            public void deleteStatement(String sql) {
                logger.logSql(sql);
                statementRunner.runRaw(sql, que);
            }

            @Override
            public RushMetaData getMetaData(Rush rush) {
                return idTable.get(rush);
            }
        });
        statementRunner.endTransition(que);
        queProvider.queComplete(que);
        return conflicts;
    }

    private void delete(List<? extends Rush> objects, final RushQue que) {
        statementRunner.startTransition(que);
        deleteStatementGenerator.generateDelete(objects, annotationCache, new RushDeleteStatementGenerator.Callback() {

            @Override
            public void removeRush(Rush rush) {
                RushCore.this.removeRush(rush);
            }

            @Override
            public void deleteStatement(String sql) {
                logger.logSql(sql);
                statementRunner.runRaw(sql, que);
            }

            @Override
            public RushMetaData getMetaData(Rush rush) {
                return idTable.get(rush);
            }
        });
        statementRunner.endTransition(que);
        queProvider.queComplete(que);
    }

    private <T extends Rush> List<T> load(Class<T> clazz, String sql, final RushQue que) {
        logger.logSql(sql);
        RushStatementRunner.ValuesCallback values = statementRunner.runGet(sql, que);
        List<T> objects = rushClassLoader.loadClasses(clazz, rushColumns, annotationCache, values, new RushClassLoader.LoadCallback() {
            @Override
            public RushStatementRunner.ValuesCallback runStatement(String string) {
                logger.logSql(string);
                return statementRunner.runGet(string, que);
            }

            @Override
            public void didLoadObject(Rush rush, RushMetaData rushMetaData) {
                addRush(rush, rushMetaData);
            }
        });
        values.close();
        queProvider.queComplete(que);
        if(objects == null) {
            throw new RushTableMissingEmptyConstructorException(clazz);
        }
        return objects;
    }

    private void addRush(Rush rush, RushMetaData rushMetaData) {
        idTable.put(rush, rushMetaData);
    }

    private void removeRush(Rush rush) {
        idTable.remove(rush);
    }
}
