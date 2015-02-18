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
import co.uk.rushorm.core.implementation.ReflectionStatementGenerator;
import co.uk.rushorm.core.implementation.ReflectionTableStatementGenerator;
import co.uk.rushorm.core.implementation.ReflectionUpgradeManager;
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

    private static RushCore rushCore;
    private static Map<Rush, Long> idTable = new WeakHashMap<>();

    /* Public */
    public static void initialize(RushClassFinder rushClassFinder, RushStatementRunner statementRunner, RushQueProvider queProvider, RushConfig rushConfig, RushStringSanitizer rushStringSanitizer, Logger logger, List<RushColumn> columns) {

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

        RushStatementGenerator statementGenerator = new ReflectionStatementGenerator(rushStringSanitizer, rushColumns, annotationCache);
        RushTableStatementGenerator rushTableStatementGenerator = new ReflectionTableStatementGenerator(rushColumns);
        RushClassLoader rushClassLoader = new ReflectionClassLoader(rushColumns, annotationCache);

        initialize(rushUpgradeManager, statementGenerator, rushClassFinder, rushTableStatementGenerator, statementRunner, queProvider, rushConfig, rushClassLoader, rushStringSanitizer, logger);
    }

    public static void initialize(RushUpgradeManager rushUpgradeManager, RushStatementGenerator statementGenerator, RushClassFinder rushClassFinder, RushTableStatementGenerator rushTableStatementGenerator, RushStatementRunner statementRunner, RushQueProvider queProvider, RushConfig rushConfig, RushClassLoader rushClassLoader, RushStringSanitizer rushStringSanitizer, Logger logger) {
        rushCore = new RushCore(statementGenerator, statementRunner, queProvider, rushConfig, rushTableStatementGenerator, rushClassLoader, rushStringSanitizer, logger);
        RushQue que = queProvider.blockForNextQue();
        if (rushConfig.firstRun()) {
            /* Turn off automatic_index to stop logging in Android L */
            //statementRunner.runRaw("PRAGMA automatic_index=off;", que);
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

    public long getId(Rush rush) {
        Long id = idTable.get(rush);
        if (id == null) {
            return -1;
        }
        return id;
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
    private final RushStatementGenerator statementGenerator;
    private final RushStatementRunner statementRunner;
    private final RushQueProvider queProvider;
    private final RushConfig rushConfig;
    private final RushTableStatementGenerator rushTableStatementGenerator;
    private final RushClassLoader rushClassLoader;
    private final Logger logger;
    private final RushStringSanitizer rushStringSanitizer;

    private RushCore(RushStatementGenerator statementGenerator, RushStatementRunner statementRunner, RushQueProvider queProvider, RushConfig rushConfig, RushTableStatementGenerator rushTableStatementGenerator, RushClassLoader rushClassLoader, RushStringSanitizer rushStringSanitizer, Logger logger) {
        this.statementGenerator = statementGenerator;
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
        statementGenerator.generateSaveOrUpdate(objects, new RushStatementGenerator.Callback() {
            @Override
            public void addRush(Rush rush, long id) {
                idTable.put(rush, id);
            }
            @Override
            public void removeRush(Rush rush) {}
            @Override
            public long lastIdInTable(String sql) {
                return statementRunner.runGetLastId(sql, que);
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
        });
        statementRunner.endTransition(que);
        queProvider.queComplete(que);
    }

    private void delete(List<? extends Rush> objects, final RushQue que) {
        statementRunner.startTransition(que);
        statementGenerator.generateDelete(objects, new RushStatementGenerator.Callback() {
            @Override
            public void addRush(Rush rush, long id) {}
            @Override
            public void removeRush(Rush rush) {
                removeId(rush.getClass(), rush.getId());
            }
            @Override
            public long lastIdInTable(String sql) { return 0; }
            @Override
            public void createdOrUpdateStatement(String sql) {}
            @Override
            public void deleteStatement(String sql) {
                logger.logSql(sql);
                statementRunner.runRaw(sql, que);
            }
        });
        statementRunner.endTransition(que);
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
            public void didLoadObject(Rush rush, long id) {
                idTable.put(rush, id);
            }
        });
        values.close();
        queProvider.queComplete(que);
        if(objects == null) {
            throw new RushTableMissingEmptyConstructor(clazz);
        }
        return objects;
    }

    private void removeId(Class clazz, long id) {
        Iterator<Map.Entry<Rush, Long>> iterator = idTable.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Rush, Long> entry = iterator.next();
            if (id == entry.getValue() && clazz.isInstance(entry.getKey())) {
                iterator.remove();
            }
        }
    }
}
