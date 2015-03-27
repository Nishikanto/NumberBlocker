package com.shadow.numberblocker;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Comment;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;

public class DatabaseClass {
    public static final String KEY_NUM = "_num";
    public static final String KEY_MSG = "_msg";
    public static final String KEY_CALL = "_call";
    private static final String DATABASE_NAME = "numberDB";
    private static final String DATABASE_TABLE = "numberTable";
    private static final int DATABASE_VERSION = 1;

    private DBHelper Helper;
    private final Context BaseContext;
    private SQLiteDatabase mainDB;
    public ArrayList<Bundle> result;

    public static class DBHelper extends SQLiteOpenHelper {

        public DBHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);

        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL( "CREATE TABLE " + DATABASE_TABLE + " (" +
                            KEY_NUM + " TEXT, " +
                            KEY_MSG + " BOOLEAN, " +
                            KEY_CALL + " BOOLEAN);"
            );
            // TODO Auto-generated method stub

        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int OldVersion, int NewVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);
            onCreate(db);
            // TODO Auto-generated method stub

        }
    }

    public DatabaseClass(Context c) throws SQLiteException{
        BaseContext = c;
    }

    public DatabaseClass open() throws SQLiteException{
        Helper = new DBHelper(BaseContext);
        mainDB = Helper.getWritableDatabase();
        return this;
    }

    public void close(){
        Helper.close();
    }

    public void createEntry(String number, boolean msg, boolean call){
        System.out.println(call);
        ContentValues cv = new ContentValues();
        cv.put(KEY_NUM, number);
        cv.put(KEY_MSG, msg);
        cv.put(KEY_CALL, call);
        mainDB.insert(DATABASE_TABLE, null, cv);
    }

    public String getDbTableName(){
        return DATABASE_TABLE;
    }

    public String[] columnName(){
        return new String[]{KEY_NUM, KEY_MSG, KEY_CALL};
    }

    public Cursor getData() {
        String[] columns = new String[]{KEY_NUM, KEY_MSG, KEY_CALL};
        Cursor c = mainDB.query(DATABASE_TABLE, columns, null, null, null, null, null);
        return c;
    }

    public void deleteAll(){
        mainDB.delete(DATABASE_TABLE, null, null);
    }



    public void DeleteData(String num) {
        //System.out.println(num);
        mainDB.delete(DATABASE_TABLE, KEY_NUM + "=" + num, null);

    }

    public void EditMSG(String num, Boolean msg) {
        ContentValues cvUpdate = new ContentValues();
        cvUpdate.put(KEY_MSG, msg);
        mainDB.update(DATABASE_TABLE, cvUpdate, KEY_NUM + "=" + num, null);
    }

    public void EditCALL(String num, Boolean call) {
        ContentValues cvUpdate = new ContentValues();
        cvUpdate.put(KEY_CALL, call);
        mainDB.update(DATABASE_TABLE, cvUpdate, KEY_NUM + "=" + num, null);
    }

}

