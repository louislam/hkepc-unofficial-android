package net.louislam.hkepc.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabaseLockedException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;

public class DatabaseHelper extends SQLiteOpenHelper {
    static final String TAG = "DB";

    static final int DB_VERSION_NO = 1;
    static final String DB_NAME = "hkepc";
    static final String TABLE_FORUM_LAST_READ = "forum_last_read";
    static final String THREAD_ID = "thread_id";
    static final String PAGE_NO = "page_no";
    static final String READ_TIME = "read_time";

    SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


    public DatabaseHelper(Context context){
        super(context, DB_NAME, null, DB_VERSION_NO);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_FORUM_LAST_READ +
                " (thread_id integer  PRIMARY KEY, page_no integer, read_time text)" );

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public long newReadThread(int threadId, int pageNo){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(THREAD_ID, threadId);
        cv.put(PAGE_NO, pageNo);
        cv.put(READ_TIME, sd.format(GregorianCalendar.getInstance().getTime()));

        Log.d(TAG, "insert - thread: " + threadId + "page: " + pageNo);
        return db.insert(TABLE_FORUM_LAST_READ, null, cv);
    }

    public int updateReadThread(int threadId, int pageNo){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(THREAD_ID, threadId);
        cv.put(PAGE_NO, pageNo);
        cv.put(READ_TIME, sd.format(GregorianCalendar.getInstance().getTime()));

        Log.d(TAG, "update - thread: " + threadId + "page: " + pageNo);
        return db.update(TABLE_FORUM_LAST_READ, cv, THREAD_ID +"=? and " + PAGE_NO + " < ?",
                new String[]{String.valueOf(threadId), String.valueOf(pageNo)});
    }

    public void setReadThread(int threadId, int pageNo){
        if(pageNo > 1){
            updateReadThread(threadId, pageNo);
        }
        else{
            newReadThread(threadId, pageNo);
        }
    }

    public int getRead(int threadId){
        SQLiteDatabase db = this.getReadableDatabase();
        int pageNo = -1;

        String sql = "SELECT " + THREAD_ID +", " + PAGE_NO + ", " + READ_TIME + " from " + TABLE_FORUM_LAST_READ + " where " + THREAD_ID + "=?";
        Cursor cur = db.rawQuery(sql, new String[]{String.valueOf(threadId)});

        if(cur.moveToFirst()){
            pageNo = cur.getInt(cur.getColumnIndex(PAGE_NO));
        }


        Log.d(TAG, "read - thread: " + threadId + "page: " + pageNo);
        return pageNo;
    }
}
