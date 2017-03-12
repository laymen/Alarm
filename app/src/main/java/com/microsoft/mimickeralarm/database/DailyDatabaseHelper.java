package com.microsoft.mimickeralarm.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by mouse on 2017/3/10 0010.
 */
public class DailyDatabaseHelper  extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 6;
    private static final String DATABASE_NAME = "dailyDatabase.db";

    public DailyDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + AlarmDbSchema.AlarmTable.DAILY + "(" +
                        " _id integer primary key autoincrement, " +
                        AlarmDbSchema.AlarmTable.Columns.UUID+ ", " +
                        AlarmDbSchema.AlarmTable.Columns.MONTHDAY +
                        ")"
        );


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(AlarmDatabaseHelper.class.getSimpleName(),
                "Upgrading database from version " + oldVersion + " to " + newVersion);
        db.execSQL("DROP TABLE IF EXISTS " + AlarmDbSchema.AlarmTable.DAILY);
        onCreate(db);
    }
}
