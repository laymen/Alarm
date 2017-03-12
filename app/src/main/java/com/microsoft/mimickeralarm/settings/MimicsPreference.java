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

package com.microsoft.mimickeralarm.settings;

import android.content.Context;
import android.support.v7.preference.Preference;
import android.util.AttributeSet;

import com.microsoft.mimickeralarm.R;
import com.microsoft.mimickeralarm.model.Alarm;

import java.util.ArrayList;

/**
 * A custom preferences class that encapsulates the Mimics settings for an alarm.
 */
public class MimicsPreference extends Preference {
    private String[] mMimicLabels;
    private String[] mMimicValues;
    ArrayList<String> mInitialValues;
    ArrayList<String> mEnabledValues;
    public static ArrayList<String> getEnabledMimics(Context context, Alarm alarm) {
        ArrayList<String> enabledMimics = new ArrayList<>();
        if (alarm.isColorCaptureEnabled()) {
            enabledMimics.add(context.getString(R.string.pref_mimic_color_capture_id));
        }
        if (alarm.isExpressYourselfEnabled()) {
            enabledMimics.add(context.getString(R.string.pref_mimic_express_yourself_id));
        }
        if (alarm.isTongueTwisterEnabled()) {
            enabledMimics.add(context.getString(R.string.pref_mimic_tongue_twister_id));
        }
        if (alarm.isShakeYourPhoneEnabled()){
            enabledMimics.add(context.getString(R.string.pref_mimic_shake_your_phone_id));
        }
        if (alarm.isSolveMathProblemEnabled()){
            enabledMimics.add(context.getString(R.string.pref_mimic_solve_mathematics_id));
        }
        if (alarm.isHitGameEnabled()) {
            enabledMimics.add(context.getString(R.string.pref_mimic_hit_game_id));
        }
        return enabledMimics;
    }

    public MimicsPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public boolean hasChanged() {
        return !mInitialValues.equals(mEnabledValues);
    }

    public boolean isTongueTwisterEnabled() {
        return mEnabledValues.contains(getContext().getString(R.string.pref_mimic_tongue_twister_id));
    }

    public boolean isColorCaptureEnabled() {
        return mEnabledValues.contains(getContext().getString(R.string.pref_mimic_color_capture_id));
    }

    public boolean isExpressYourselfEnabled() {
        return mEnabledValues.contains(getContext().getString(R.string.pref_mimic_express_yourself_id));
    }

    public boolean isShakeYourPhoneEnabled(){
        return mEnabledValues.contains(getContext().getString(R.string.pref_mimic_shake_your_phone_id));
    }

    public boolean isSolveMathProblemEnabled(){
        return mEnabledValues.contains(getContext().getString(R.string.pref_mimic_solve_mathematics_id));
    }

    public boolean isHitGameEnabled(){
        return mEnabledValues.contains(getContext().getString(R.string.pref_mimic_hit_game_id));
    }


    public void setMimicValuesAndSummary(ArrayList<String> enabledMimics) {
        mEnabledValues = enabledMimics;
        setSummaryValues(mEnabledValues);
    }

    public void setInitialValues(Alarm alarm) {
        mMimicValues = getContext().getResources().getStringArray(R.array.pref_mimic_values);
        mMimicLabels = getContext().getResources().getStringArray(R.array.pref_mimic_labels);
        mEnabledValues = getEnabledMimics(getContext(), alarm);//获得初始化状态下的mimics并初始化原布局文件

        // Save the initial state so we can check for changes later
        mInitialValues = new ArrayList<>(mEnabledValues);
    }

    public void setInitialSummary() {
        setSummaryValues(mInitialValues);
    }

    //接收MimicsPreference，携带数据库中数据传回的数据
    public ArrayList<String> getEnabledMimicValues() {
        return mEnabledValues;
    }

    private void setSummaryValues(ArrayList<String> values) {
        String summaryString = "";
        for (int i = 0; i < mMimicValues.length; i++) {
            if (values.contains(mMimicValues[i])) {
                String displayString = mMimicLabels[i];
                if (summaryString.isEmpty()) {
                    summaryString = displayString;
                } else {
                    summaryString += "," + displayString;
                }
            }
        }
        if (summaryString.isEmpty()) {
            summaryString = getContext().getString(R.string.pref_no_mimics);
        }
        setSummary(summaryString);
    }
}
