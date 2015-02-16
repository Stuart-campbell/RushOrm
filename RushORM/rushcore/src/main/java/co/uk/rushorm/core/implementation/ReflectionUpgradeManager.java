package co.uk.rushorm.core.implementation;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import co.uk.rushorm.core.Rush;
import co.uk.rushorm.core.RushStatementRunner;
import co.uk.rushorm.core.RushUpgradeManager;
import co.uk.rushorm.core.annotations.RushIgnore;
import co.uk.rushorm.core.annotations.RushList;
import co.uk.rushorm.core.annotations.RushRenamed;

/**
 * Created by stuartc on 11/12/14.
 */
public class ReflectionUpgradeManager implements RushUpgradeManager {

    private class Mapping {
        private String oldName;
        private String newName;
        private Mapping(String oldNames, String newName) {
            this.oldName = oldNames;
            this.newName = newName;
        }
    }

    private class TableMapping {
        private boolean isJoin;
        private Mapping name;
        private List<Mapping> fields = new ArrayList<>();
        private List<String> indexes = new ArrayList<>();
    }

    private class PotentialTableMapping {
        private PotentialMapping name;
        private List<PotentialMapping> fields = new ArrayList<>();
    }

    private class PotentialMapping {
        private String[] oldNames;
        private String newName;
        private PotentialMapping(String[] oldNames, String newName) {
            this.oldNames = oldNames;
            this.newName = newName;
        }
    }

    private static final String COLUMNS_INFO = "PRAGMA table_info(%s)";
    private static final String RENAME_TABLE = "ALTER TABLE %s RENAME TO %s";
    private static final String TABLE_INFO = "SELECT name FROM sqlite_master WHERE type='table';";
    private static final String DROP = "DROP TABLE %s";
    private static final String MOVE_ROWS = "INSERT INTO %s(" + ReflectionUtils.RUSH_ID + "," + ReflectionUtils.RUSH_CREATED + "," + ReflectionUtils.RUSH_UPDATED + "," + ReflectionUtils.RUSH_VERSION + "%s)\n" +
            "SELECT " + ReflectionUtils.RUSH_ID + "," + ReflectionUtils.RUSH_CREATED + "," + ReflectionUtils.RUSH_UPDATED + "," + ReflectionUtils.RUSH_VERSION  + "%s\n" +
            "FROM %s;";

    private static final String MOVE_JOIN_ROWS = "INSERT INTO %s(" + ReflectionUtils.RUSH_ID + "%s)\n" +
            "SELECT " + ReflectionUtils.RUSH_ID + "%s\n" +
            "FROM %s;";

    private static final String DELETE_INDEX = "DROP INDEX %s;";

    @Override
    public void upgrade(List<Class> classList, UpgradeCallback callback) {

        try {

            List<PotentialMapping> potentialJoinMappings = new ArrayList<>();
            List<String> currentTables = currentTables(callback);
            List<TableMapping> tableMappings = new ArrayList<>();

            for(Class clazz : classList) {
                PotentialTableMapping potentialTableMapping = potentialMapping(clazz, potentialJoinMappings);
                String tableName = nameExists(currentTables, potentialTableMapping.name.oldNames);
                if(tableName != null) {
                    currentTables.remove(tableName);
                    TableMapping tableMapping = new TableMapping();
                    tableMapping.name = new Mapping(tableName, potentialTableMapping.name.newName);
                    tableMapping.isJoin = false;
                    List<String> columns = tablesFields(tableName, callback);
                    for(PotentialMapping potentialMapping : potentialTableMapping.fields) {
                        String fieldName = nameExists(columns, potentialMapping.oldNames);
                        if(fieldName != null) {
                            columns.remove(fieldName);
                            Mapping fieldMapping = new Mapping(fieldName, potentialMapping.newName);
                            tableMapping.fields.add(fieldMapping);
                        }
                    }
                    tableMappings.add(tableMapping);
                }
            }

            for(PotentialMapping potentialJoinMapping : potentialJoinMappings) {
                String tableName = nameExists(currentTables, potentialJoinMapping.oldNames);
                if(tableName != null) {
                    currentTables.remove(tableName);
                    TableMapping joinMapping = new TableMapping();
                    joinMapping.name = new Mapping(tableName, potentialJoinMapping.newName);
                    joinMapping.fields.add(new Mapping("parent", "parent"));
                    joinMapping.fields.add(new Mapping("child", "child"));
                    joinMapping.indexes.add(tableName + "_idx");
                    joinMapping.isJoin = true;
                    tableMappings.add(joinMapping);

                }
            }

            for (TableMapping tableMapping : tableMappings) {
                if(tableMapping.name.oldName.equals(tableMapping.name.newName)) {
                    tableMapping.name.oldName = tableMapping.name.oldName + "_temp";
                    renameTable(tableMapping.name.oldName, tableMapping.name.newName, callback);
                }
                for(String index : tableMapping.indexes) {
                    callback.runRaw(String.format(DELETE_INDEX, index));
                }
            }

            callback.createClasses(classList);

            for (TableMapping tableMapping : tableMappings) {
                moveRows(tableMapping, callback);
                currentTables.add(tableMapping.name.oldName);
            }

            for(String table : currentTables) {
                dropTable(table, callback);
            }

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }

    private String nameExists(List<String> columns, String[] names) {
        for(String name : names){
            for(String column : columns) {
                if (column.equals(name)) {
                    return name;
                }
            }
        }
        return null;
    }

    private List<String> currentTables(UpgradeCallback callback) {
        RushStatementRunner.ValuesCallback values = callback.runStatement(TABLE_INFO);
        List<String> tables = new ArrayList<>();
        while(values.hasNext()) {
            String table = values.next().get(0);
            if(table.startsWith(ReflectionUtils.RUSH_TABLE_PREFIX)) {
                tables.add(table);
            }
        }
        values.close();
        return tables;
    }

    private List<String> tablesFields(String table, UpgradeCallback callback) {
        RushStatementRunner.ValuesCallback values = callback.runStatement(String.format(COLUMNS_INFO, table));
        List<String> columns = new ArrayList<>();
        while(values.hasNext()) {
            List<String> columnsInfo = values.next();
            String column = columnsInfo.get(1);
            if(!column.equals(ReflectionUtils.RUSH_ID)) {
                columns.add(columnsInfo.get(1));
            }
        }
        values.close();
        return columns;
    }

    private PotentialTableMapping potentialMapping(Class clazz,  List<PotentialMapping> joinMapping) throws ClassNotFoundException {

            PotentialTableMapping tableMapping = new PotentialTableMapping();

            if(clazz.isAnnotationPresent(RushRenamed.class)) {
                RushRenamed rushRenamed = (RushRenamed) clazz.getAnnotation(RushRenamed.class);
                String[] names = rushRenamed.names();
                for(int i = 0; i < names.length; i ++) {
                    names[i] = ReflectionUtils.tableNameForClass(names[i]);
                }
                tableMapping.name = new PotentialMapping(names, ReflectionUtils.tableNameForClass(clazz));
            } else {
                String name = ReflectionUtils.tableNameForClass(clazz);
                tableMapping.name = new PotentialMapping(new String[]{name}, name);
            }

            List<Field> fields = new ArrayList<>();
            ReflectionUtils.getAllFields(fields, clazz);
            for (Field field : fields) {
                field.setAccessible(true);
                if (!field.isAnnotationPresent(RushIgnore.class)) {
                    if(Rush.class.isAssignableFrom(field.getType())){
                        addJoinMappingIfRequired(joinMapping, clazz, field.getType(), field);
                    }else if(field.isAnnotationPresent(RushList.class)) {
                        RushList rushList = field.getAnnotation(RushList.class);
                        Class listClass = Class.forName(rushList.classname());
                        addJoinMappingIfRequired(joinMapping, clazz, listClass, field);
                    }else if(field.isAnnotationPresent(RushRenamed.class)){
                        RushRenamed rushRenamed = field.getAnnotation(RushRenamed.class);
                        tableMapping.fields.add(new PotentialMapping(rushRenamed.names(), field.getName()));
                    }else {
                        String[] names = new String[]{field.getName()};
                        tableMapping.fields.add(new PotentialMapping(names, field.getName()));
                    }
                }
            }
        return tableMapping;
    }

    private void addJoinMappingIfRequired(List<PotentialMapping> potentialMappings, Class parent, Class child, Field field) {

        String newName = ReflectionUtils.joinTableNameForClass(parent, child, field);
        List<String> possibleOldNames = new ArrayList<>();
        List<String> parentNames = oldNamesFromClass(parent);
        parentNames.add(ReflectionUtils.tableNameForClass(parent));

        List<String> childNames = oldNamesFromClass(child);
        childNames.add(ReflectionUtils.tableNameForClass(child));

        List<String> fieldNames = new ArrayList<>();
        if(field.isAnnotationPresent(RushRenamed.class)){
            RushRenamed rushRenamed = field.getAnnotation(RushRenamed.class);
            for(String name : rushRenamed.names()) {
                fieldNames.add(name);
            }
        }
        fieldNames.add(field.getName());

        for(String fieldName : fieldNames){
            for(String childName : childNames){
                for(String parentName : parentNames){
                    possibleOldNames.add(ReflectionUtils.joinTableNameForClass(parentName, childName, fieldName));
                }
            }
        }
        String[] oldNames = new String[possibleOldNames.size()];
        for (int i = 0; i < possibleOldNames.size(); i ++) {
            oldNames[i] = possibleOldNames.get(i);
        }
        potentialMappings.add(new PotentialMapping(oldNames, newName));
    }

    private List<String> oldNamesFromClass(Class clazz) {
        List<String> names = new ArrayList<>();
        if(clazz.isAnnotationPresent(RushRenamed.class)){
            RushRenamed rushRenamed = (RushRenamed) clazz.getAnnotation(RushRenamed.class);
            for(String name : rushRenamed.names()) {
                names.add(ReflectionUtils.tableNameForClass(name));
            }
        }
        return names;
    }


    private void renameTable(String newName, String oldName, UpgradeCallback upgradeCallback) {
        upgradeCallback.runRaw(String.format(RENAME_TABLE, oldName, newName));
    }

    private void dropTable(String name, UpgradeCallback upgradeCallback){
        upgradeCallback.runRaw(String.format(DROP, name));
    }

    private void moveRows(TableMapping tableMapping, UpgradeCallback upgradeCallback) {
        StringBuilder fromRows = new StringBuilder();
        StringBuilder toRows = new StringBuilder();
        for(Mapping mapping : tableMapping.fields) {
            fromRows.append(", ")
                    .append(mapping.oldName);
            toRows.append(", ")
                    .append(mapping.newName);
        }
        String sql = String.format(tableMapping.isJoin ? MOVE_JOIN_ROWS : MOVE_ROWS, tableMapping.name.newName, toRows.toString(), fromRows.toString(), tableMapping.name.oldName);
        upgradeCallback.runRaw(sql);
    }
}
