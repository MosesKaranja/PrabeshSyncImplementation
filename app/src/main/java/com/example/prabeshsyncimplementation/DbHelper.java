package com.example.prabeshsyncimplementation;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION;
    private static final String CREATE_TABLE="create table "+DbContract.TABLE_NAME+"(id integer primary key autoincrement,"+DbContract.NAME+" text,"+DbContract.SYNC_STATUS+" integer);";
    private static 

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
