package co.uk.rushorm.core;

import java.util.List;

/**
 * Created by Stuart on 14/12/14.
 */
public interface RushClassLoader {

    public interface LoadCallback {
        public RushStatementRunner.ValuesCallback runStatement(String string);
        public void didLoadObject(RushTable rushTable, long id);
    }

    public <T> List<T> loadClasses(Class<T> clazz, RushStatementRunner.ValuesCallback valuesCallback, LoadCallback callback);

}
