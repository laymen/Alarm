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

import android.database.Cursor;
import android.database.CursorWrapper;
import android.net.Uri;

import com.microsoft.mimickeralarm.database.AlarmDbSchema.AlarmTable;
import com.microsoft.mimickeralarm.model.Alarm;
import com.microsoft.mimickeralarm.model.Weeks;

import java.util.UUID;

/**
 * This class implements a SQLite CursorWrapper for the alarm object data.
 */
public class AlarmCursorWrapper extends CursorWrapper {
    public AlarmCursorWrapper(Cursor cursor) { super(cursor); }

    public Weeks getWeeks(){
        String uuidString = getString(getColumnIndex(AlarmTable.Columns.UUID));
        String monthday = getString(getColumnIndex(AlarmTable.Columns.MONTHDAY));
        Weeks week = new Weeks();
        week.setmId(UUID.fromString(uuidString));
        week.setmMonthDay(monthday);
        return week;
    }

    public Alarm getAlarm() {
        String uuidString = getString(getColumnIndex(AlarmTable.Columns.UUID));
        String title = getString(getColumnIndex(AlarmTable.Columns.TITLE));
        boolean isEnabled = (getInt(getColumnIndex(AlarmTable.Columns.ENABLED)) != 0);
        String dateStr= getString(getColumnIndex(AlarmTable.Columns.DATE));//新增
        int timeHour = getInt(getColumnIndex(AlarmTable.Columns.HOUR));
        int timeMinute = getInt(getColumnIndex(AlarmTable.Columns.MINUTE));
        String alarmToneString = getString(getColumnIndex(AlarmTable.Columns.TONE));
        Uri alarmTone = null;
        if (!alarmToneString.isEmpty()) {
            alarmTone = Uri.parse(alarmToneString);
        }
        String[] repeatingDays = getString(getColumnIndex(AlarmTable.Columns.DAYS)).split(",");
        boolean vibrate = (getInt(getColumnIndex(AlarmTable.Columns.VIBRATE)) != 0);
        boolean tongueTwister = (getInt(getColumnIndex(AlarmTable.Columns.TONGUE_TWISTER)) != 0);
        boolean colorCapture = (getInt(getColumnIndex(AlarmTable.Columns.COLOR_CAPTURE)) != 0);
        boolean expressYourself = (getInt(getColumnIndex(AlarmTable.Columns.EXPRESS_YOURSELF)) != 0);
        boolean shakeYourPhone=(getInt(getColumnIndex(AlarmTable.Columns.SHAKE_YOUR_PHONE)) != 0);//摇一摇

        boolean solveMathProblem=(getInt(getColumnIndex(AlarmTable.Columns.SOLVE_MATH_PROBLEM)) != 0);//解数学题
        boolean hitGame = (getInt(getColumnIndex(AlarmTable.Columns.HIT_GAME)) != 0);//打地鼠
        boolean isNew = (getInt(getColumnIndex(AlarmTable.Columns.NEW)) != 0);
        boolean isSnoozed = (getInt(getColumnIndex(AlarmTable.Columns.SNOOZED)) != 0);
        int snoozedHour = getInt(getColumnIndex(AlarmTable.Columns.SNOOZED_HOUR));
        int snoozedMinute = getInt(getColumnIndex(AlarmTable.Columns.SNOOZED_MINUTE));
        int snoozedSeconds = getInt(getColumnIndex(AlarmTable.Columns.SNOOZED_SECONDS));

        Alarm alarm = new Alarm(UUID.fromString(uuidString));
        alarm.setTitle(title);
        alarm.setIsEnabled(isEnabled);
        alarm.setDateStr(dateStr);//新增
        alarm.setTimeHour(timeHour);
        alarm.setTimeMinute(timeMinute);
        alarm.setAlarmTone(alarmTone);
        for (int i = 0; i < repeatingDays.length; i++) {
            alarm.setRepeatingDay(i, !repeatingDays[i].equals("false"));
        }
        alarm.setVibrate(vibrate);
        alarm.setTongueTwisterEnabled(tongueTwister);
        alarm.setColorCaptureEnabled(colorCapture);
        alarm.setExpressYourselfEnabled(expressYourself);
        alarm.setShakeYourPhoneEnabled(shakeYourPhone);
        alarm.setSolveMathProblemEnabled(solveMathProblem);
        alarm.setHitGameEnabled(hitGame);
        alarm.setNew(isNew);
        alarm.setSnoozed(isSnoozed);
        alarm.setSnoozeHour(snoozedHour);
        alarm.setSnoozeMinute(snoozedMinute);
        alarm.setSnoozeSeconds(snoozedSeconds);

        return alarm;
    }


}
