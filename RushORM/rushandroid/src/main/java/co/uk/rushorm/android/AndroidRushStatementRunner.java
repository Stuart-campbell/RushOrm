package co.uk.rushorm.android;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import co.uk.rushorm.core.RushQue;
import co.uk.rushorm.core.RushStatementRunner;

/**
 * Created by stuartc on 11/12/14.
 */
public class AndroidRushStatementRunner extends SQLiteOpenHelper implements RushStatementRunner {

    private static final String LAST_ID = "SELECT id FROM %s ORDER BY id DESC LIMIT 1";

    private int lastRunVersion = -1;

    public AndroidRushStatementRunner(Context context, String name, int version) {
        super(context, name, null, version);
        lastRunVersion = version;
    }

    @Override
    public void runRaw(String statement, RushQue que) {
        getWritableDatabase().execSQL(statement);
    }

    @Override
    public long runPut(String sql, RushQue que) {
        getWritableDatabase().execSQL(sql);
        Cursor cursor = getWritableDatabase().rawQuery("SELECT last_insert_rowid()", null);
        cursor.moveToFirst();
        long id = cursor.getLong(0);
        cursor.close();
        return id;
    }

    @Override
    public long runGetLastId(String table, RushQue que) {
        Cursor c = getWritableDatabase().rawQuery(String.format(LAST_ID, table), null);
        if (c != null && c.moveToFirst()) {
            return c.getLong(0); //The 0 is the column index, we only have 1 column, so the index is 0
        }
        return 0;
    }

    @Override
    public ValuesCallback runGet(String sql, RushQue que) {
        final Cursor cursor = getWritableDatabase().rawQuery(sql, null);
        cursor.moveToFirst();
        return new ValuesCallback() {
            @Override
            public boolean hasNext() {
                return !cursor.isAfterLast();
            }
            @Override
            public List<String> next() {

                List<String> row = new ArrayList();
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
