package co.uk.rushorm.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import co.uk.rushorm.core.exceptions.RushCoreNotInitializedException;
import co.uk.rushorm.core.exceptions.RushTableMissingEmptyConstructor;
import co.uk.rushorm.core.implementation.ReflectionClassLoader;
import co.uk.rushorm.core.implementation.ReflectionDeleteStatementGenerator;
import co.uk.rushorm.core.implementation.ReflectionSaveStatementGenerator;
import co.uk.rushorm.core.implementation.ReflectionTableStatementGenerator;
import co.uk.rushorm.core.implementation.ReflectionUpgradeManager;
import co.uk.rushorm.core.implementation.RushColumnBoolean;
import co.uk.rushorm.core.implementation.RushColumnDate;
import co.uk.rushorm.core.implementation.RushColumnDouble;
import co.uk.rushorm.core.implementation.RushColumnInt;
import co.uk.rushorm.core.implementation.RushColumnLong;
import co.uk.rushorm.core.implementation.RushColumnShort;
import co.uk.rushorm.core.implementation.RushColumnString;
import co.uk.rushorm.core.implementation.RushColumnsImplementation;

/**
 * Created by Stuart on 10/12/14.
 */
public class RushCore {

    private static RushCore rushCore;
    private final Map<Rush, RushMetaData> idTable = new WeakHashMap<>();
    private final List<RushMetaData> deletedIds = new ArrayList<>();
    /* Public */
    public static void initialize(RushClassFinder rushClassFinder, RushStatementRunner statementRunner, RushQueProvider queProvider, RushConfig rushConfig, RushStringSanitizer rushStringSanitizer, Logger logger, List<RushColumn> columns) {

        columns.add(new RushColumnBoolean());
        columns.add(new RushColumnDate());
        columns.add(new RushColumnDouble());
        columns.add(new RushColumnInt());
        columns.add(new RushColumnLong());
        columns.add(new RushColumnShort());
        columns.add(new RushColumnString());

        RushColumns rushColumns = new RushColumnsImplementation(columns);
        RushUpgradeManager rushUpgradeManager = new ReflectionUpgradeManager();

        Map<Class, AnnotationCache> annotationCache = new HashMap<>();

        RushSaveStatementGenerator saveStatementGenerator = new ReflectionSaveStatementGenerator(rushStringSanitizer, rushColumns, annotationCache);
        RushDeleteStatementGenerator deleteStatementGenerator = new ReflectionDeleteStatementGenerator(annotationCache);

        RushTableStatementGenerator rushTableStatementGenerator = new ReflectionTableStatementGenerator(rushColumns);
        RushClassLoader rushClassLoader = new ReflectionClassLoader(rushColumns, annotationCache);

        initialize(rushUpgradeManager, saveStatementGenerator, deleteStatementGenerator, rushClassFinder, rushTableStatementGenerator, statementRunner, queProvider, rushConfig, rushClassLoader, rushStringSanitizer, logger);
    }

    public static void initialize(RushUpgradeManager rushUpgradeManager, RushSaveStatementGenerator saveStatementGenerator, RushDeleteStatementGenerator deleteStatementGenerator, RushClassFinder rushClassFinder, RushTableStatementGenerator rushTableStatementGenerator, RushStatementRunner statementRunner, RushQueProvider queProvider, RushConfig rushConfig, RushClassLoader rushClassLoader, RushStringSanitizer rushStringSanitizer, Logger logger) {
        rushCore = new RushCore(saveStatementGenerator, deleteStatementGenerator, statementRunner, queProvider, rushConfig, rushTableStatementGenerator, rushClassLoader, rushStringSanitizer, logger);
        RushQue que = queProvider.blockForNextQue();
        if (rushConfig.firstRun()) {
            rushCore.createTables(rushClassFinder, que);
        } else if(rushConfig.inDebug() || rushConfig.upgrade()){
            rushCore.upgrade(rushClassFinder, rushUpgradeManager, que);
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

    public String getId(Rush rush) {
        RushMetaData rushMetaData = idTable.get(rush);
        if (rushMetaData == null
                || !rushMetaData.isSaved()
                || deletedIds.contains(rushMetaData)) {
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

    /* protected */
    protected String sanitize(String string) {
        return rushStringSanitizer.sanitize(string);
    }

    /* private */
    private final RushSaveStatementGenerator saveStatementGenerator;
    private final RushDeleteStatementGenerator deleteStatementGenerator;
    private final RushStatementRunner statementRunner;
    private final RushQueProvider queProvider;
    private final RushConfig rushConfig;
    private final RushTableStatementGenerator rushTableStatementGenerator;
    private final RushClassLoader rushClassLoader;
    private final Logger logger;
    private final RushStringSanitizer rushStringSanitizer;

    private RushCore(RushSaveStatementGenerator saveStatementGenerator, RushDeleteStatementGenerator deleteStatementGenerator, RushStatementRunner statementRunner, RushQueProvider queProvider, RushConfig rushConfig, RushTableStatementGenerator rushTableStatementGenerator, RushClassLoader rushClassLoader, RushStringSanitizer rushStringSanitizer, Logger logger) {
        this.saveStatementGenerator = saveStatementGenerator;
        this.deleteStatementGenerator = deleteStatementGenerator;
        this.statementRunner = statementRunner;
        this.queProvider = queProvider;
        this.rushConfig = rushConfig;
        this.rushTableStatementGenerator = rushTableStatementGenerator;
        this.rushClassLoader = rushClassLoader;
        this.rushStringSanitizer = rushStringSanitizer;
        this.logger = logger;
    }

    private void createTables(RushClassFinder rushClassFinder, RushQue que) {
        createTables(rushClassFinder.findClasses(rushConfig), que);
    }

    private void createTables(List<Class> classes, final RushQue que) {
        rushTableStatementGenerator.generateStatements(classes, new RushTableStatementGenerator.StatementCallback() {
            @Override
            public void statementCreated(String statement) {
                logger.logSql(statement);
                statementRunner.runRaw(statement, que);
            }
        });
        queProvider.queComplete(que);
    }

    private void upgrade(RushClassFinder rushClassFinder, RushUpgradeManager rushUpgradeManager, final RushQue que) {
        rushUpgradeManager.upgrade(rushClassFinder.findClasses(rushConfig), new RushUpgradeManager.UpgradeCallback() {
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
        saveStatementGenerator.generateSaveOrUpdate(objects, new RushSaveStatementGenerator.Callback() {
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

    private void delete(List<? extends Rush> objects, final RushQue que) {
        statementRunner.startTransition(que);
        deleteStatementGenerator.generateDelete(objects, new RushDeleteStatementGenerator.Callback() {

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
        cleanIdMap();
        queProvider.queComplete(que);
    }

    private <T extends Rush> List<T> load(Class<T> clazz, String sql, final RushQue que) {
        logger.logSql(sql);
        RushStatementRunner.ValuesCallback values = statementRunner.runGet(sql, que);
        List<T> objects = rushClassLoader.loadClasses(clazz, values, new RushClassLoader.LoadCallback() {
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
            throw new RushTableMissingEmptyConstructor(clazz);
        }
        return objects;
    }

    private void addRush(Rush rush, RushMetaData rushMetaData) {
        idTable.put(rush, rushMetaData);
    }

    private void removeRush(Rush rush) {
        RushMetaData rushMetaData = idTable.remove(rush);
        deletedIds.add(rushMetaData);
    }

    private void cleanIdMap() {
        Iterator<Map.Entry<Rush, RushMetaData>> iterator = idTable.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Rush, RushMetaData> entry = iterator.next();
            if (deletedIds.contains(entry.getValue())) {
                iterator.remove();
            }
        }
        deletedIds.clear();
    }
}
