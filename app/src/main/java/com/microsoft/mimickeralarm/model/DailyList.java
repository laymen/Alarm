package com.microsoft.mimickeralarm.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.microsoft.mimickeralarm.database.AlarmCursorWrapper;
import com.microsoft.mimickeralarm.database.AlarmDbSchema;
import com.microsoft.mimickeralarm.database.DailyDatabaseHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/3/10 0010.
 */
public class DailyList {
    private static DailyList sAlarmList;
    private Context mContext;
    private SQLiteDatabase mDatabase;

    private DailyList(Context context) {
        mContext = context.getApplicationContext();
        mDatabase = new DailyDatabaseHelper(mContext)
                .getWritableDatabase();
    }

    public static DailyList get(Context context) {
        if (sAlarmList == null) {
            sAlarmList = new DailyList(context);
        }
        return sAlarmList;
    }

    private static ContentValues populateContentValues(Weeks weeks) {
        ContentValues values = new ContentValues();
        values.put(AlarmDbSchema.AlarmTable.Columns.UUID, weeks.getmId().toString());
        values.put(AlarmDbSchema.AlarmTable.Columns.MONTHDAY, weeks.getmMonthDay().toString());
        return values;
    }

    public void addDaily(Weeks weeks) {
        ContentValues values = populateContentValues(weeks);
        mDatabase.insert(AlarmDbSchema.AlarmTable.DAILY, null, values);
    }

    private AlarmCursorWrapper queryAlarms(String queryClause, String[] queryArgs, String orderBy) {
        Cursor cursor = mDatabase.query(
                AlarmDbSchema.AlarmTable.DAILY,
                null, // gets all columns
                queryClause,
                queryArgs,
                null,
                null,
                orderBy
        );

        return new AlarmCursorWrapper(cursor);
    }

    /**
     * 保持一7天的数据，里面记录着用户每天早上首次关闭闹钟并起床的数据
     * @return
     */
    public List<Weeks> getDaily() {
        List<Weeks> dailys = new ArrayList<>();
        AlarmCursorWrapper cursor = queryAlarms(null, null, null);
        cursor.moveToFirst();
        int count = 0;
        while (!cursor.isAfterLast()) {
            dailys.add(cursor.getWeeks());
            count++;
            cursor.moveToNext();
        }
        cursor.close();
        if (count > 7) { //>7条数据
            //删除前面的数据
            for (int i=0;i<count-7;i++){
                deleteDaily(dailys.get(i));//从数据库中删除
                dailys.remove(i);//从List集合中删除
            }
        }
        return dailys;
    }

    public void deleteDaily(Weeks weeks) {
        mDatabase.delete(AlarmDbSchema.AlarmTable.DAILY,
                AlarmDbSchema.AlarmTable.Columns.UUID + " = ?",
                new String[]{weeks.getmId().toString()});
    }
}
