package co.uk.rushorm.android;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import co.uk.rushorm.core.RushQue;
import co.uk.rushorm.core.RushStatementRunner;
import co.uk.rushorm.core.exceptions.RushSqlException;
import co.uk.rushorm.core.implementation.ReflectionUtils;

/**
 * Created by stuartc on 11/12/14.
 */
public class AndroidRushStatementRunner extends SQLiteOpenHelper implements RushStatementRunner {

    private static final String LAST_ID = "SELECT " + ReflectionUtils.RUSH_ID + " FROM %s ORDER BY " + ReflectionUtils.RUSH_ID + " DESC LIMIT 1";

    private int lastRunVersion = -1;

    public AndroidRushStatementRunner(Context context, String name, int version) {
        super(context, name, null, version);
        lastRunVersion = version;
    }

    @Override
    public void runRaw(String statement, RushQue que) {
        try {
            getWritableDatabase().execSQL(statement);
        } catch (SQLiteException exception) {
            throw new RushSqlException();
        }
    }

    @Override
    public long runGetLastId(String table, RushQue que) {
        Cursor cursor;
        try {
            cursor = getWritableDatabase().rawQuery(String.format(LAST_ID, table), null);
        } catch (SQLiteException exception) {
            throw new RushSqlException();
        }
        long id = 0;
        if (cursor != null ) {
            if(cursor.moveToFirst()){
                id = cursor.getLong(0); //The 0 is the column index, we only have 1 column, so the index is 0
            }
            cursor.close();
        }
        return id;
    }

    @Override
    public ValuesCallback runGet(String sql, RushQue que) {
        final Cursor cursor;
        try {
            cursor = getWritableDatabase().rawQuery(sql, null);
        } catch (SQLiteException exception) {
            throw new RushSqlException();
        }
        cursor.moveToFirst();
        return new ValuesCallback() {
            @Override
            public boolean hasNext() {
                return !cursor.isAfterLast();
            }
            @Override
            public List<String> next() {

                List<String> row = new ArrayList<>();
                for(int i = 0; i < cursor.getColumnCount(); i++){
                    row.add(cursor.getString(i));
                }
                cursor.moveToNext();
                return row;
            }
            @Override
            public void close() {
                cursor.close();
            }
        };
    }

    @Override
    public void startTransition(RushQue que) {
        getWritableDatabase().beginTransaction();
    }

    @Override
    public void endTransition(RushQue que) {
        getWritableDatabase().setTransactionSuccessful();
        getWritableDatabase().endTransaction();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {}

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        lastRunVersion = oldVersion;
    }

    public int getLastRunVersion(){
        if(lastRunVersion < 0) {
            getReadableDatabase();
        }
        return lastRunVersion;
    }

}
