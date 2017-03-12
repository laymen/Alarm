/*
 *
 * Copyright (c) Microsoft. All rights reserved.
 * Licensed under the MIT license.
 *
 * Project Oxford: http://ProjectOxford.ai
 *
 * Project Oxford Mimicker Alarm Github:
 * https://github.com/Microsoft/ProjectOxford-Apps-MimickerAlarm
 *
 * Copyright (c) Microsoft Corporation
 * All rights reserved.
 *
 * MIT License:
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED ""AS IS"", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */

package com.microsoft.mimickeralarm.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.microsoft.mimickeralarm.database.AlarmDbSchema.AlarmTable;

/**
 * This class implements the methods needed to create the SQLite alarm database.
 * <p/>
 * onCreate - is called to create a new database with a SQL query
 * onUpdate - is called in the cases where the database is already created.  Depending on the
 * database version, structural changes can be made to the database.
 */
public class AlarmDatabaseHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 6;
    private static final String DATABASE_NAME = "alarmDatabase.db";

    public AlarmDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + AlarmTable.NAME + "(" +
                        " _id integer primary key autoincrement, " +
                        AlarmTable.Columns.UUID + ", " +
                        AlarmTable.Columns.TITLE + ", " +
                        AlarmTable.Columns.ENABLED + ", " +
                        AlarmTable.Columns.DATE+", "+
                        AlarmTable.Columns.HOUR + ", " +
                        AlarmTable.Columns.MINUTE + ", " +
                        AlarmTable.Columns.DAYS + ", " +
                        AlarmTable.Columns.TONE + ", " +
                        AlarmTable.Columns.VIBRATE + ", " +
                        AlarmTable.Columns.TONGUE_TWISTER + ", " +
                        AlarmTable.Columns.COLOR_CAPTURE + ", " +
                        AlarmTable.Columns.EXPRESS_YOURSELF + ", " +
                        AlarmTable.Columns.SHAKE_YOUR_PHONE + ", " +
                        AlarmTable.Columns.SOLVE_MATH_PROBLEM+", " +
                        AlarmTable.Columns.HIT_GAME + ", " +
                        AlarmTable.Columns.NEW + ", " +
                        AlarmTable.Columns.SNOOZED + ", " +
                        AlarmTable.Columns.SNOOZED_HOUR + ", " +
                        AlarmTable.Columns.SNOOZED_MINUTE + ", " +
                        AlarmTable.Columns.SNOOZED_SECONDS +
                        ")"
        );


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(AlarmDatabaseHelper.class.getSimpleName(),
                "Upgrading database from version " + oldVersion + " to " + newVersion);
        db.execSQL("DROP TABLE IF EXISTS " + AlarmTable.NAME);
        onCreate(db);
    }
}
