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

package com.microsoft.mimickeralarm.mimics;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.widget.Toast;

import com.microsoft.mimickeralarm.model.Alarm;
import com.microsoft.mimickeralarm.model.AlarmList;
import com.microsoft.mimickeralarm.model.DailyList;
import com.microsoft.mimickeralarm.model.Weeks;
import com.microsoft.mimickeralarm.scheduling.AlarmScheduler;
import com.microsoft.mimickeralarm.utilities.Logger;
import com.microsoft.mimickeralarm.utilities.SharePreferencesUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.UUID;

/**
 * simple class that spawns a random mimic game while respect that mimics enabled in settings
 * <p/>
 * if no internet access is detected, spawns the NoNetwork mimic.
 */
public final class MimicFactory {

    public static final String MIMIC_FRAGMENT_TAG = "mimic_fragment";
    private static final String TAG = "MimicFactory";

    public static Fragment getMimicFragment(Activity caller, UUID alarmId) {
        Alarm alarm = AlarmList.get(caller).getAlarm(alarmId);
        //记录关闭闹钟的时间
        Calendar calenderNow = Calendar.getInstance();
        long time = AlarmScheduler.getStartTimeMillis(calenderNow, alarm);//获得关闭闹钟的时间

        //处理
        Calendar calendarAlarm = Calendar.getInstance();
        calendarAlarm.setTimeInMillis(time);//是按秒来计算的
        Date alarmTime = calendarAlarm.getTime();
        // Log.i("================>", "==========+" + alarmTime.getDay());
        int alarmDate = alarmTime.getDate();
        //系统的时间
        int day = calenderNow.get(Calendar.DATE);
        int month = calenderNow.get(Calendar.MONTH) + 1;
//        int year = calenderNow.get(Calendar.YEAR);
//        int dow = calenderNow.get(Calendar.DAY_OF_WEEK);
//        int dom = calenderNow.get(Calendar.DAY_OF_MONTH);

        Log.i("look------look", day + "---" + alarmDate);
        Log.i("see------see", (day == alarmDate) + "" + (alarmTime.getMonth() + 1 == month));
        Log.i("watch-----watch", SharePreferencesUtils.getBoolean(caller, "flag", false) + "");
        Long endMillis = AlarmScheduler.getEndTimeMillis();//今天的截止时间
        if(time>SharePreferencesUtils.getLong(caller, "endmillis", Long.valueOf("0"))){//当前的时间大于保存在数据库里面的时间
            SharePreferencesUtils.putBoolean(caller, "flag", false);//目的是让插入SQlite数据库中
        }

        if (day == alarmDate && alarmTime.getMonth() + 1 == month) {//判断是否是当天
            if (!SharePreferencesUtils.getBoolean(caller, "flag", false)) {
                // SharePreferencesUtils.putLong(caller, "tempTime", time);//闹钟第一次响起时我存进数据库
               // Toast.makeText(caller, "=======>", Toast.LENGTH_SHORT).show();
                SharePreferencesUtils.putLong(caller, "tempTime", time);//把闹钟关闭的时间存入数据库中
                SharePreferencesUtils.putLong(caller, "endmillis", endMillis);//目的是不让插入SQlite数据库中
                Weeks weeks = new Weeks();
                weeks.setmId(alarmId);
                weeks.setmMonthDay(Long.toString(time));
                DailyList.get(caller).addDaily(weeks);
            }
        }


        if (time <= SharePreferencesUtils.getLong(caller, "endmillis", endMillis) && time >= SharePreferencesUtils.getLong(caller, "tempTime", Long.valueOf("0"))) {
            Log.i("结束时间--》", SharePreferencesUtils.getLong(caller, "endmillis", endMillis) + "");
            Log.i("中间时间--->", time + "");
            Log.i("开始时间--->", SharePreferencesUtils.getLong(caller, "tempTime", Long.valueOf("0")) + "");

            SharePreferencesUtils.putBoolean(caller, "flag", true);//目的是不让插入SQlite数据库中

        } else {
            SharePreferencesUtils.putBoolean(caller, "flag", false);//目的是让插入SQlite数据库中
        }

        List<Class> mimics = new ArrayList<>();

        if (alarm.isTongueTwisterEnabled()) {
            mimics.add(MimicTongueTwisterFragment.class);
        }
        if (alarm.isColorCaptureEnabled()) {
            mimics.add(MimicColorCaptureFragment.class);
        }
        if (alarm.isExpressYourselfEnabled()) {
            mimics.add(MimicExpressYourselfFragment.class);
        }

        if (alarm.isShakeYourPhoneEnabled()) {//摇一摇
            mimics.add(MimicShakeYourPhoneFragment.class);
        }

        if (alarm.isSolveMathProblemEnabled()) {//解数学题
            mimics.add(MimicSolveMathFragment.class);
        }
        if (alarm.isHitGameEnabled()) {
            mimics.add(MimicHitMouseFragment.class);
        }

        Class mimic = null;
        if (mimics.size() > 0) {
            if (isNetworkAvailable(caller)) {
                int rand = new Random().nextInt(mimics.size());
                mimic = mimics.get(rand);
            } else {
                mimic = MimicNoNetworkFragment.class;
            }
        }

        Fragment fragment = null;
        if (mimic != null) {
            try {
                fragment = (Fragment) mimic.newInstance();
            } catch (Exception e) {
                Log.e(TAG, "Couldn't create fragment:", e);
                Logger.trackException(e);
            }
        }
        return fragment;
    }

    private static boolean isNetworkAvailable(Activity caller) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) caller.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static Fragment getNoNetworkMimic(Activity caller) {
        Fragment fragment = null;
        try {
            fragment = MimicNoNetworkFragment.class.newInstance();
        } catch (Exception e) {
            Log.e(TAG, "Couldn't create fragment:", e);
            Logger.trackException(e);
        }
        return fragment;
    }

    public interface MimicResultListener {
        void onMimicSuccess(String shareable);

        void onMimicFailure();

        void onMimicError();
    }
}
