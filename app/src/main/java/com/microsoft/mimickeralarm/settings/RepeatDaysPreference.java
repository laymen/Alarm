package com.microsoft.mimickeralarm.settings;

import android.content.Context;
import android.support.v7.preference.Preference;
import android.util.AttributeSet;

import com.microsoft.mimickeralarm.R;
import com.microsoft.mimickeralarm.model.Alarm;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/2/28 0028.
 */
public class RepeatDaysPreference extends Preference {
    private String[] mRepeatDayLabels;
    private String[] mRepeatDayValues;
    ArrayList<String> mInitialRepeatValues;
    ArrayList<String> mEnabledRepeatValues;

    public RepeatDaysPreference(Context context, AttributeSet attrs) {
        super(context, attrs);

    }

    public static ArrayList<String> getEnabledReapteDay(Context context, Alarm alarm) {
        ArrayList<String> enabledRepateDay = new ArrayList<>();
        if (alarm.ismMoEnabled()) {
            enabledRepateDay.add(context.getString(R.string.pref_repeat_mo_id));
        }
        if (alarm.ismTuEnabled()) {
            enabledRepateDay.add(context.getString(R.string.pref_repeat_tu_id));
        }
        if (alarm.ismWeEnabled()) {
            enabledRepateDay.add(context.getString(R.string.pref_repeat_we_id));
        }
        if (alarm.ismThEnabled()) {
            enabledRepateDay.add(context.getString(R.string.pref_repeat_th_id));
        }
        if (alarm.ismFrEnabled()) {
            enabledRepateDay.add(context.getString(R.string.pref_repeat_fr_id));
        }
        if (alarm.ismSaEnabled()) {
            enabledRepateDay.add(context.getString(R.string.pref_repeat_sa_id));
        }
        if (alarm.ismSuEnabled()) {
            enabledRepateDay.add(context.getString(R.string.pref_repeat_su_id));
        }
        return enabledRepateDay;

    }

    public boolean hasChanged() {
        return !mInitialRepeatValues.equals(mEnabledRepeatValues);
    }

    public boolean isRepeatMoEnabled() {
        return mEnabledRepeatValues.contains(getContext().getString(R.string.pref_repeat_mo_id));
    }

    public boolean isRepeatTuEnabled() {
        return mEnabledRepeatValues.contains(getContext().getString(R.string.pref_repeat_tu_id));
    }

    public boolean isRepeatWeEnabled() {
        return mEnabledRepeatValues.contains(getContext().getString(R.string.pref_repeat_we_id));
    }

    public boolean isRepeatThEnabled() {
        return mEnabledRepeatValues.contains(getContext().getString(R.string.pref_repeat_th_id));
    }

    public boolean isRepeatFrEnabled() {
        return mEnabledRepeatValues.contains(getContext().getString(R.string.pref_repeat_fr_id));
    }

    public boolean isRepeatSaEnabled() {
        return mEnabledRepeatValues.contains(getContext().getString(R.string.pref_repeat_sa_id));
    }

    public boolean isRepeatSuEnabled() {
        return mEnabledRepeatValues.contains(getContext().getString(R.string.pref_repeat_su_id));
    }

    public void setRepeatDayValuesAndSummary(ArrayList<String> enabledRepeatDay) {
        this.mEnabledRepeatValues = enabledRepeatDay;
        setRepeatDaySummaryValues(mEnabledRepeatValues);
    }

    public void setRepeatDayInitialValues(Alarm alarm) {
        mRepeatDayValues = getContext().getResources().getStringArray(R.array.pref_repeat_values);
        mRepeatDayLabels = getContext().getResources().getStringArray(R.array.pref_repeat_labels);
        mEnabledRepeatValues = getEnabledReapteDay(getContext(), alarm);//传过来了但是里面的没有我要的值

        // Save the initial state so we can check for changes later
        this.mInitialRepeatValues = new ArrayList<>(mEnabledRepeatValues);
    }

    public void setRepeatDayInitialSummary() {
        setRepeatDaySummaryValues(mInitialRepeatValues);
    }

    public ArrayList<String> getEnabledRepeatDayValues() {
        return mEnabledRepeatValues;
    }

    private void setRepeatDaySummaryValues(ArrayList<String> values) {
        String summaryString = "";
        for (int i = 0; i < mRepeatDayValues.length; i++) {
            if (values.contains(mRepeatDayValues[i])) {
                String displayString = mRepeatDayValues[i];
                if (summaryString.isEmpty()) {
                    summaryString = displayString;
                } else {
                    summaryString += "," + displayString;
                }
            }
        }

        if (summaryString.isEmpty()) {
            summaryString = getContext().getString(R.string.pref_no_repeatday);
        }
        setSummary(summaryString);
    }


}
