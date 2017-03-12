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

package com.microsoft.mimickeralarm.utilities;

import android.content.Context;
import android.text.format.DateUtils;
import android.util.Log;

import com.ibm.icu.text.SimpleDateFormat;
import com.ibm.icu.util.Calendar;
import com.ibm.icu.text.MessageFormat;
import com.microsoft.mimickeralarm.R;

import java.text.Format;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * This utility class centralizes all the Date and Time formatting functionality for the app
 */
public final class DateTimeUtilities {

    // As per http://icu-project.org/apiref/icu4j/com/ibm/icu/text/SimpleDateFormat.html, we
    // need the format 'EEEEEE' to get a short weekday name
    private final static String TWO_CHARACTER_SHORT_DAY_PATTERN = "EEEEEE";

    private DateTimeUtilities() {
    }


    public static String getUserTimeString(Context context, int hour, int minute) {
        Format formatter = android.text.format.DateFormat.getTimeFormat(context);
        Calendar calendar = Calendar.getInstance();

        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);

        return formatter.format(calendar.getTime());
    }

    public static String getFullDateStringForNow() {
        Format formatter = java.text.DateFormat.getDateInstance(java.text.DateFormat.FULL);
        return formatter.format(Calendar.getInstance().getTime());
    }

    public static String[] getShortDayNames() {
        String[] dayNames = new String[7];
        Format formatter = new SimpleDateFormat(TWO_CHARACTER_SHORT_DAY_PATTERN, Locale.getDefault());
        Calendar calendar = Calendar.getInstance();
        for (int d = Calendar.SUNDAY, i = 0; d <= Calendar.SATURDAY; d++, i++) {
            calendar.set(Calendar.DAY_OF_WEEK, d);
            dayNames[i] = formatter.format(calendar.getTime()).toUpperCase(Locale.getDefault());
        }
        String[] chineseNames = new String[7];
        for (int i = 0; i < dayNames.length; i++) {
            if ("SU".equalsIgnoreCase(dayNames[i])) {
                chineseNames[i] = "周日";
            } else if ("Mo".equalsIgnoreCase(dayNames[i])) {
                chineseNames[i] = "周一";
            } else if ("Tu".equalsIgnoreCase(dayNames[i])) {
                chineseNames[i] = "周二";
            } else if ("We".equalsIgnoreCase(dayNames[i])) {
                chineseNames[i] = "周三";
            } else if ("Th".equalsIgnoreCase(dayNames[i])) {
                chineseNames[i] = "周四";
            } else if ("Fr".equalsIgnoreCase(dayNames[i])) {
                chineseNames[i] = "周五";
            } else if ("Sa".equalsIgnoreCase(dayNames[i])) {
                chineseNames[i] = "周六";
            }
        }
        return chineseNames;
    }

    private static String switchString(String day) {
        if ("SU".equalsIgnoreCase(day)) {
            return "周日";
        } else if ("MO".equalsIgnoreCase(day)) {
            return "周一";
        } else if ("TU".equalsIgnoreCase(day)) {
            return "周二";
        } else if ("WE".equalsIgnoreCase(day)) {
            return "周三";
        } else if ("TH".equalsIgnoreCase(day)) {
            return "周四";
        } else if ("FR".equalsIgnoreCase(day)) {
            return "周五";
        } else{
            return "周六";
        }
    }

    /**
     * @param daysOfWeek
     * @return
     */
    public static String getShortDayNamesString(int[] daysOfWeek) {
        String dayNames = null;
        Format formatter = new SimpleDateFormat(TWO_CHARACTER_SHORT_DAY_PATTERN, Locale.getDefault());
        Calendar calendar = Calendar.getInstance();
        for (int day = 0; day < daysOfWeek.length; day++) {
            calendar.set(Calendar.DAY_OF_WEEK, daysOfWeek[day]);
            if (day == 0) {
                String dayy=formatter.format(calendar.getTime()).toUpperCase(Locale.getDefault());
                dayNames=dayy;
            } else {
                String dayys=formatter.format(calendar.getTime()).toUpperCase(Locale.getDefault());
                dayNames += " " + dayys;
            }
        }
        Log.i("getShortDa--->",dayNames);
        return dayNames;
    }

    public static String getDayPeriodSummaryString(Context context, int[] daysOfWeek) {
        int[] weekdays = {Calendar.MONDAY, Calendar.TUESDAY, Calendar.WEDNESDAY, Calendar.THURSDAY,
                Calendar.FRIDAY};
        int[] weekend = {Calendar.SUNDAY, Calendar.SATURDAY};
        int[] everyday = {Calendar.SUNDAY, Calendar.MONDAY, Calendar.TUESDAY, Calendar.WEDNESDAY,
                Calendar.THURSDAY, Calendar.FRIDAY, Calendar.SATURDAY};
        if (Arrays.equals(daysOfWeek, weekend)) {
            return context.getString(R.string.alarm_list_weekend);
        } else if (Arrays.equals(daysOfWeek, weekdays)) {
            return context.getString(R.string.alarm_list_weekdays);
        } else if (Arrays.equals(daysOfWeek, everyday)) {
            return context.getString(R.string.alarm_list_every_day);
        } else {
            return getShortDayNamesString(daysOfWeek);
        }
    }

    /**
     *  闹钟新建或编辑后的时间
     * @param context
     * @param timeUntilAlarm
     * @return
     */
    public static String getTimeUntilAlarmDisplayString(Context context, long timeUntilAlarm) {
        Calendar calendarNow = Calendar.getInstance();
        Calendar calendarAlarm = Calendar.getInstance();
        calendarAlarm.setTimeInMillis(timeUntilAlarm);//是按秒来计算的
        Date alarmTime = calendarAlarm.getTime();
        Log.i("day-----day--->",alarmTime.toString());//Sat Mar 11 09:33:00 GMT+08:00 2017

        // It's very important we make the fieldDifference calls in this order.  Each time
        // calendarNow moves closer to alarmTime by the difference units it returns. This implies
        // that you start with the largest calendar unit and move to smaller ones if you want
        // accurate results for different units between the two times.
        int days = Math.max(0, calendarNow.fieldDifference(alarmTime, Calendar.DATE));
        int hours = Math.max(0, calendarNow.fieldDifference(alarmTime, Calendar.HOUR_OF_DAY));
        int minutes = Math.max(0, calendarNow.fieldDifference(alarmTime, Calendar.MINUTE));

        Map<String, Integer> args = new HashMap<>();
        try {
            args.put("days", days);
            args.put("hours", hours);
            args.put("minutes", minutes);
        } catch (Exception e) {
            Logger.trackException(e);
        }

        int resourceIdForDisplayString;
        if (days > 0) {
            if (hours > 0 && minutes > 0) {
                resourceIdForDisplayString = R.string.alarm_set_day_hour_minute;
            } else if (hours > 0) {
                resourceIdForDisplayString = R.string.alarm_set_day_hour;
            } else if (minutes > 0) {
                resourceIdForDisplayString = R.string.alarm_set_day_minute;
            } else {
                resourceIdForDisplayString = R.string.alarm_set_day;
            }
        } else if (hours > 0) {
            if (minutes > 0) {
                resourceIdForDisplayString = R.string.alarm_set_hour_minute;
            } else {
                resourceIdForDisplayString = R.string.alarm_set_hour;
            }
        } else if (minutes > 0) {
            resourceIdForDisplayString = R.string.alarm_set_minute;
        } else {
            resourceIdForDisplayString = R.string.alarm_set_less_than_minute;
        }
        return new MessageFormat(context.getString(resourceIdForDisplayString)).format(args);
    }

    public static String getDayAndTimeAlarmDisplayString(Context context, long timeUntilAlarm) {
        return DateUtils.formatDateTime(context, timeUntilAlarm, DateUtils.FORMAT_SHOW_TIME |
                DateUtils.FORMAT_SHOW_WEEKDAY);
    }


    //获取当前日期和这之前七天的日期并返回一个String数组
    public static List<String> getSevenDayDate(){
        List<String> sevenDayDate=new ArrayList<>();
        Calendar c=Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat("dd");
        SimpleDateFormat format1 = new SimpleDateFormat("MM月dd日");
        //过去6天:3.3
        c.setTime(new Date());
        c.add(Calendar.DATE, -6);
        Date d6 = c.getTime();
        Log.i("ddd6---->",d6+"");
        String sixDayAgo = format1.format(d6);
        Log.i("ddd6--age-->",sixDayAgo);
        sevenDayDate.add(sixDayAgo);
        //过去5天:3.4
        c.setTime(new Date());
        c.add(Calendar.DATE, -5);
        Date d5 = c.getTime();
        String fiveDayAgo = format.format(d5);
        sevenDayDate.add(fiveDayAgo);
        //过去4天:3.5
        c.setTime(new Date());
        c.add(Calendar.DATE, -4);
        Date d4 = c.getTime();
        String fourDayAgo = format.format(d4);
        sevenDayDate.add(fourDayAgo);
        //过去3天:3.6
        c.setTime(new Date());
        c.add(Calendar.DATE, -3);
        Date d3 = c.getTime();
        String threeDayAgo = format.format(d3);
        sevenDayDate.add(threeDayAgo);
        //过去2天:3.7
        c.setTime(new Date());
        c.add(Calendar.DATE, -2);
        Date d2 = c.getTime();
        String twoDayAgo = format.format(d2);
        sevenDayDate.add(twoDayAgo);
        //过去1天:3.8
        c.setTime(new Date());
        c.add(Calendar.DATE, -1);
        Date d1 = c.getTime();
        String oneDayAgo = format.format(d1);
        sevenDayDate.add(oneDayAgo);
        //获取当前的日子，并将其存储到sevenDayDate中
        //3.9
        String now=format.format(c.getTime());
        sevenDayDate.add(now);
        return sevenDayDate;
    }

}
