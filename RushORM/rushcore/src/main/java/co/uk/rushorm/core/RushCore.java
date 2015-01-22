package co.uk.rushorm.core;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import co.uk.rushorm.core.exceptions.RushCoreNotInitializedException;
import co.uk.rushorm.core.implementation.ReflectionClassLoader;
import co.uk.rushorm.core.implementation.ReflectionStatementGenerator;
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
    private static Map<RushTable, Long> idTable = new WeakHashMap<>();

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
        RushStatementGenerator statementGenerator = new ReflectionStatementGenerator(rushStringSanitizer, rushColumns);
        RushTableStatementGenerator rushTableStatementGenerator = new ReflectionTableStatementGenerator(rushColumns);
        RushClassLoader rushClassLoader = new ReflectionClassLoader(rushColumns);

        initialize(rushUpgradeManager, statementGenerator, rushClassFinder, rushTableStatementGenerator, statementRunner, queProvider, rushConfig, rushClassLoader, rushStringSanitizer, logger);
    }

    public static void initialize(RushUpgradeManager rushUpgradeManager, RushStatementGenerator statementGenerator, RushClassFinder rushClassFinder, RushTableStatementGenerator rushTableStatementGenerator, RushStatementRunner statementRunner, RushQueProvider queProvider, RushConfig rushConfig, RushClassLoader rushClassLoader, RushStringSanitizer rushStringSanitizer, Logger logger) {
        rushCore = new RushCore(statementGenerator, statementRunner, queProvider, rushConfig, rushTableStatementGenerator, rushClassLoader, rushStringSanitizer, logger);
        RushQue que = queProvider.blockForNextQue();
        if (rushConfig.firstRun()) {
            rushCore.createTables(rushClassFinder, que);
        } else if(rushConfig.inDebug() || rushConfig.upgrade()){
            rushCore.upgrade(rushClassFinder, rushUpgradeManager, que);
        }
    }

    public static RushCore getInstance() {
        if (rushCore == null) {
            throw new RushCoreNotInitializedException();
        }
        return rushCore;
    }

    public void save(final List<? extends RushTable> objects, final RushCallback callback) {
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

    public void save(List<? extends RushTable> objects) {
        RushQue que = queProvider.blockForNextQue();
        save(objects, que);
    }

    public void delete(final List<? extends RushTable> objects, final RushCallback callback) {
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

    public void delete(List<? extends RushTable> objects) {
        RushQue que = queProvider.blockForNextQue();
        delete(objects, que);
    }

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
            public void StatementCreated(String statement) {
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

    protected long getId(RushTable rushTable) {
        Long id = idTable.get(rushTable);
        if (id == null) {
            return -1;
        }
        return id;
    }

    protected void save(RushTable rushTable) {
        RushQue que = queProvider.blockForNextQue();
        save(rushTable, que);
    }

    protected void save(final RushTable rushTable, final RushCallback callback) {
        queProvider.waitForNextQue(new RushQueProvider.RushQueCallback() {
            @Override
            public void callback(RushQue rushQue) {
                save(rushTable, rushQue);
                if(callback != null) {
                    callback.complete();
                }
            }
        });
    }

    private void save(RushTable rushTable, final RushQue que) {
        statementRunner.startTransition(que);
        statementGenerator.generateSaveOrUpdate(rushTable, new RushStatementGenerator.SaveCallback() {
            @Override
            public void statementCreatedForRush(String sql, RushTable rushTable) {
                logger.logSql(sql);
                long id = statementRunner.runPut(sql, que);
                if(rushTable.getId() < 0) {
                    idTable.put(rushTable, id);
                }
            }
            @Override
            public void deleteJoinStatementCreated(String sql) {
                logger.logSql(sql);
                statementRunner.runRaw(sql, que);
            }

            @Override
            public void joinStatementCreated(String sql) {
                logger.logSql(sql);
                statementRunner.runPut(sql, que);
            }
        });
        statementRunner.endTransition(que);
        queProvider.queComplete(que);
    }

    private void save(List<? extends RushTable> objects, RushQue que) {
        statementRunner.startTransition(que);
        for (RushTable rushTable : objects) {
            save(rushTable, que);
        }
        statementRunner.endTransition(que);
        queProvider.queComplete(que);
    }

    protected void delete(RushTable rushTable) {
        RushQue que = queProvider.blockForNextQue();
        statementRunner.startTransition(que);
        delete(rushTable, que);
        statementRunner.endTransition(que);
    }

    protected void delete(final RushTable rushTable, final RushCallback callback) {
        queProvider.waitForNextQue(new RushQueProvider.RushQueCallback() {
            @Override
            public void callback(RushQue rushQue) {
                statementRunner.startTransition(rushQue);
                delete(rushTable, rushQue);
                statementRunner.endTransition(rushQue);
                if(callback != null) {
                    callback.complete();
                }
            }
        });
    }

    private void delete(RushTable rushTable, final RushQue que) {
        statementGenerator.generateDelete(rushTable, new RushStatementGenerator.DeleteCallback() {
            @Override
            public void deleteJoinStatementCreated(String sql) {
                logger.logSql(sql);
                statementRunner.runRaw(sql, que);
            }

            @Override
            public void statementCreatedForRush(String sql, RushTable rushTable) {
                logger.logSql(sql);
                statementRunner.runRaw(sql, que);
                removeId(rushTable.getClass(), rushTable.getId());
            }

            @Override
            public void deleteChild(RushTable rushTable) {
                delete(rushTable, que);
            }
        });
        queProvider.queComplete(que);
    }

    private void delete(List<? extends RushTable> objects, RushQue que) {
        statementRunner.startTransition(que);
        for (RushTable rushTable : objects) {
            delete(rushTable, que);
        }
        statementRunner.endTransition(que);
        queProvider.queComplete(que);
    }

    protected <T> List<T> load(Class<T> clazz, String sql) {
        RushQue que = queProvider.blockForNextQue();
        return load(clazz, sql, que);
    }

    private <T> List<T> load(Class<T> clazz, String sql, final RushQue que) {
        logger.logSql(sql);
        RushStatementRunner.ValuesCallback values = statementRunner.runGet(sql, que);
        List<T> objects = rushClassLoader.loadClasses(clazz, values, new RushClassLoader.LoadCallback() {
            @Override
            public RushStatementRunner.ValuesCallback runStatement(String string) {
                logger.logSql(string);
                return statementRunner.runGet(string, que);
            }

            @Override
            public void didLoadObject(RushTable rush, long id) {
                idTable.put(rush, id);
            }
        });
        queProvider.queComplete(que);
        return objects;
    }

    private void removeId(Class clazz, long id) {
        Iterator<Map.Entry<RushTable, Long>> iterator = idTable.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<RushTable, Long> entry = iterator.next();
            if (id == entry.getValue() && clazz.isInstance(entry.getKey())) {
                iterator.remove();
            }
        }
    }

    protected String sanitize(String string) {
        return rushStringSanitizer.sanitize(string);
    }

}
