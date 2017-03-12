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

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.microsoft.mimickeralarm.R;
import com.microsoft.mimickeralarm.model.Alarm;
import com.microsoft.mimickeralarm.model.AlarmList;
import com.microsoft.mimickeralarm.utilities.DateTimeUtilities;
import com.microsoft.mimickeralarm.utilities.Loggable;
import com.microsoft.mimickeralarm.utilities.Logger;

import java.util.ArrayList;
import java.util.UUID;

/**
 * This is the main class that handles all the settings for an Alarm.  This class is a
 * PreferenceFragment which creates the list of settings based on the different preferences
 * listed in pref_alarm.xml.
 */
public class AlarmSettingsFragment extends PreferenceFragmentCompat {
    public static final String SETTINGS_FRAGMENT_TAG = "settings_fragment";
    private static final String ARGS_ALARM_ID = "alarm_id";
    private static final String ARGS_ENABLED_MIMICS = "enabled_mimics";
    private static final String ARGS_ENABLED_REPEATDAY="enabled_repeat_days";
    private static final String PREFERENCE_DIALOG_FRAGMENT_CLASS = "android.support.v7.preference.PreferenceFragment.DIALOG";

    AlarmSettingsListener mCallback;
    private Alarm mAlarm;
    private TimePreference mTimePreference;

    private RepeatDaysPreference mRepeatDaysPreference;

    private NamePreference mNamePreference;
    private MimicsPreference mMimicsPreference;
    private RingtonePreference mRingtonePreference;
    private VibratePreference mVibratePreference;
    private ButtonsPreference mButtonsPreference;

    public static AlarmSettingsFragment newInstance(String alarmId) {
        AlarmSettingsFragment fragment = new AlarmSettingsFragment();
        Bundle bundle = new Bundle(1);
        bundle.putString(ARGS_ALARM_ID, alarmId);
        fragment.setArguments(bundle);
        return fragment;
    }

    public static AlarmSettingsFragment newInstance(String alarmId, ArrayList<String> enabledMimics,ArrayList<String>enabledRepeatDays) {
        AlarmSettingsFragment fragment = new AlarmSettingsFragment();
        Bundle bundle = new Bundle(1);
        bundle.putString(ARGS_ALARM_ID, alarmId);
        bundle.putStringArrayList(ARGS_ENABLED_MIMICS, enabledMimics);
        bundle.putStringArrayList(ARGS_ENABLED_REPEATDAY, enabledRepeatDays);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mCallback = (AlarmSettingsListener) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallback = null;
        Logger.flush();
    }

    @Override
    public void onCreatePreferences(Bundle bundle, final String s) {
        addPreferencesFromResource(R.xml.pref_alarm);

        Bundle args = getArguments();
        UUID alarmId = UUID.fromString(args.getString(ARGS_ALARM_ID));
        mAlarm = AlarmList.get(getContext()).getAlarm(alarmId);//从数据库中获得的数据

        ArrayList<String> enabledMimics = args.getStringArrayList(ARGS_ENABLED_MIMICS);
        ArrayList<String>enabledRepeatDays=args.getStringArrayList(ARGS_ENABLED_REPEATDAY);
        // Initialize the preferences from the alarm object before populating the settings list
        initializeTimePreference();//当前的时间
        initializeRepeatDaysPreference(enabledRepeatDays);

        initializeNamePreference();//备注闹钟
        initializeMimicsPreference(enabledMimics);//怎么关闭闹钟
        initializeRingtonePreference();//闹钟的声音
        initializeVibratePreference();//是否震动
        initializeButtons();
    }

    @Override
    public RecyclerView onCreateRecyclerView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        LinearLayout rootLayout = (LinearLayout) parent.getParent();
        AppBarLayout appBarLayout = (AppBarLayout) LayoutInflater.from(getContext())
                .inflate(R.layout.settings_toolbar, rootLayout, false);
        rootLayout.addView(appBarLayout, 0); // insert at top
        Toolbar bar = (Toolbar) appBarLayout.findViewById(R.id.settings_toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(bar);
        bar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCancel();
            }
        });

        Loggable.UserAction userAction;
        if (mAlarm.isNew()) {
            userAction = new Loggable.UserAction(Loggable.Key.ACTION_ALARM_CREATE);
            bar.setTitle(R.string.pref_title_new);
        } else {
            userAction = new Loggable.UserAction(Loggable.Key.ACTION_ALARM_EDIT);
            bar.setTitle(R.string.pref_title_edit);
        }
        Logger.track(userAction);

        RecyclerView recyclerView = super.onCreateRecyclerView(inflater, parent, savedInstanceState);
        int timePreferenceOrder = mTimePreference.getOrder();
        int buttonsPreferenceOrder = mButtonsPreference.getOrder();
        int[] excludeDividerList = new int[]{timePreferenceOrder, buttonsPreferenceOrder};
        recyclerView.addItemDecoration(new SettingsDividerItemDecoration(getContext(), excludeDividerList));

        return recyclerView;
    }


    public void updateMimicsPreference(ArrayList<String> enabledMimics) {
        mMimicsPreference.setMimicValuesAndSummary(enabledMimics);
    }

    //2017/2/28mouse add
    public void updateRepeatDaysPreference(ArrayList<String> enabledMimics) {
        mRepeatDaysPreference.setRepeatDayValuesAndSummary(enabledMimics);
    }

    private void initializeTimePreference() {
        mTimePreference = (TimePreference) findPreference(getString(R.string.pref_time_key));
        mTimePreference.setTime(mAlarm.getTimeHour(), mAlarm.getTimeMinute());
    }

    //备注2017/2/28mouse添加
    private void initializeRepeatDaysPreference(ArrayList<String> enabledRepeatValues) {
        mRepeatDaysPreference=(RepeatDaysPreference)findPreference(getString(R.string.pref_repeating_days_key));
        for (int i = 0; i < 7; i++) {
            if (mAlarm.getRepeatingDay(0)){
                mAlarm.setmSuEnabled(true);
            }
            if (mAlarm.getRepeatingDay(1)){
                mAlarm.setmMoEnabled(true);
            }
            if (mAlarm.getRepeatingDay(2)){
                mAlarm.setmTuEnabled(true);
            }
            if (mAlarm.getRepeatingDay(3)){
                mAlarm.setmWeEnabled(true);
            }
            if (mAlarm.getRepeatingDay(4)){
                mAlarm.setmThEnabled(true);
            }
            if (mAlarm.getRepeatingDay(5)){
                mAlarm.setmFrEnabled(true);
            }
            if (mAlarm.getRepeatingDay(6)){
                mAlarm.setmSaEnabled(true);
            }
        }
        for(int i=0;i<7;i++){
            Log.i("AlarmSettingFragment星期"+i,mAlarm.getRepeatingDay(i)+"");

        }
        mRepeatDaysPreference.setRepeatDayInitialValues(mAlarm);
        if (enabledRepeatValues==null){
            mRepeatDaysPreference.setRepeatDayInitialSummary();
        }else {
            mRepeatDaysPreference.setRepeatDayValuesAndSummary(enabledRepeatValues);
        }
        mRepeatDaysPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                mCallback.onShowRepeatDaySettings(mRepeatDaysPreference.getEnabledRepeatDayValues());
                return true;
            }
        });
    }



    private void initializeNamePreference() {
        mNamePreference = (NamePreference) findPreference(getString(R.string.pref_name_key));
        mNamePreference.setAlarmName(mAlarm.getTitle());

    }

    private void initializeMimicsPreference(ArrayList<String> enabledValues) {
        mMimicsPreference = (MimicsPreference) findPreference(getString(R.string.pref_mimics_key));
        mMimicsPreference.setInitialValues(mAlarm);//这个mAlarm是来SQLite数据库
        if (enabledValues == null) {
            mMimicsPreference.setInitialSummary();
        } else {
            mMimicsPreference.setMimicValuesAndSummary(enabledValues);
        }
        mMimicsPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                mCallback.onShowMimicsSettings(mMimicsPreference.getEnabledMimicValues());//携带从数据库中取出的数据出去
                return true;
            }
        });
    }

    private void initializeRingtonePreference() {
        mRingtonePreference = (RingtonePreference) findPreference(getString(R.string.pref_ringtone_key));
        mRingtonePreference.setRingtone(mAlarm.getAlarmTone());
        mRingtonePreference.setParent(this);
    }

    private void initializeVibratePreference() {
        mVibratePreference = (VibratePreference) findPreference(getString(R.string.pref_vibrate_key));
        mVibratePreference.setInitialValue(mAlarm.shouldVibrate());
    }

    private void initializeButtons() {
        mButtonsPreference = (ButtonsPreference) findPreference(getString(R.string.pref_buttons_key));
        int resId = mAlarm.isNew() ? android.R.string.cancel : R.string.pref_button_delete;
        mButtonsPreference.setLeftButtonText(getResources().getString(resId));
        mButtonsPreference.setRightButtonText(getResources().getString(R.string.pref_button_save));//改写了button
        mButtonsPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {
                boolean rightButtonPressed = (boolean) o;
                if (rightButtonPressed) {
                    // Save button was pressed
                    saveSettingsAndExit();
                } else {
                    // Cancel (when new) or Delete button was pressed
                    deleteSettingsAndExit();
                }
                return true;
            }
        });
    }

    @Override
    public void onDisplayPreferenceDialog(Preference preference) {
        if (preference instanceof TimePreference) {
            DialogFragment dialogFragment = TimePreferenceDialogFragmentCompat.newInstance(preference);
            dialogFragment.setTargetFragment(this, 0);
            dialogFragment.show(getFragmentManager(), PREFERENCE_DIALOG_FRAGMENT_CLASS);
        } else if (preference instanceof NamePreference) {
            DialogFragment dialogFragment = NamePreferenceDialogFragmentCompat.newInstance(preference);
            dialogFragment.setTargetFragment(this, 0);
            dialogFragment.show(getFragmentManager(), PREFERENCE_DIALOG_FRAGMENT_CLASS);
        } else super.onDisplayPreferenceDialog(preference);
    }

    /**
     * 2017年mouse自定义dialog
     */
    public void onCancel() {
        if (haveSettingsChanged() || mAlarm.isNew()) {
            new AlertDialog.Builder(getContext())
                    .setMessage(R.string.pref_dialog_save_changes_message)
                    .setPositiveButton(R.string.pref_dialog_save_changes_positive_button, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            saveSettingsAndExit();
                        }
                    })
                    .setNegativeButton(R.string.pref_dialog_save_changes_negative_button, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (mAlarm.isNew()) {
                                deleteSettingsAndExit();
                            } else {
                                discardSettingsAndExit();
                            }
                        }
                    })
                    .show();
//            MyDialog myDialog=new MyDialog(getContext());
//            myDialog.setTitle("提示");
//            myDialog.setYesOnclickListener("保存", new MyDialog.onYesOnclickListener() {
//                @Override
//                public void onYesClick() {
//                    saveSettingsAndExit();
//                }
//            });
//            myDialog.setNoOnclickListener("放弃", new MyDialog.onNoOnclickListener() {
//                @Override
//                public void onNoClick() {
//                    if (mAlarm.isNew()){
//                        deleteSettingsAndExit();
//                    }else{
//                        discardSettingsAndExit();
//                    }
//                }
//            });
//            myDialog.show();
        } else {
            discardSettingsAndExit();
        }
    }

    private void saveSettingsAndExit() {
        Loggable.UserAction userAction = new Loggable.UserAction(Loggable.Key.ACTION_ALARM_SAVE);
        userAction.putJSON(mAlarm.toJSON());
        Logger.track(userAction);

        populateUpdatedSettings();

        long alarmTime = mAlarm.schedule();//用户修改完后闹钟应该响起的时间

        Toast.makeText(getActivity(),
                DateTimeUtilities.getTimeUntilAlarmDisplayString(getActivity(), alarmTime),
                Toast.LENGTH_LONG)
                .show();

        mCallback.onSettingsSaveOrIgnoreChanges();
    }

    private void deleteSettingsAndExit() {
        Loggable.UserAction userAction = new Loggable.UserAction(Loggable.Key.ACTION_ALARM_DELETE);
        userAction.putJSON(mAlarm.toJSON());
        Logger.track(userAction);

        mAlarm.delete();

        mCallback.onSettingsDeleteOrNewCancel();
    }

    private void discardSettingsAndExit() {
        Loggable.UserAction userAction = new Loggable.UserAction(Loggable.Key.ACTION_ALARM_SAVE_DISCARD);
        userAction.putJSON(mAlarm.toJSON());
        Logger.track(userAction);

        mCallback.onSettingsSaveOrIgnoreChanges();
    }

    private boolean haveSettingsChanged() {
        return mTimePreference.hasChanged() ||
                mRepeatDaysPreference.hasChanged() ||
                mNamePreference.hasChanged() ||
                mMimicsPreference.hasChanged() ||
                mRingtonePreference.hasChanged() ||
                mVibratePreference.hasChanged();
    }
    private void populateUpdatedSettings() {
        updateTimeSetting();
        updateRepeatDaysSetting();//mouse-2017/2/28 add
        updateNameSetting();
        updateMimicsSetting();
        updateRingtoneSetting();
        updateVibrateSetting();
    }

    private void updateVibrateSetting() {
        if (mVibratePreference.hasChanged()) {
            mAlarm.setVibrate(mVibratePreference.isChecked());
        }
    }

    private void updateRingtoneSetting() {
        if (mRingtonePreference.hasChanged()) {
            mAlarm.setAlarmTone(mRingtonePreference.getRingtone());
        }
    }

    private void updateMimicsSetting() {
        if (mMimicsPreference.hasChanged()) {
            mAlarm.setTongueTwisterEnabled(mMimicsPreference.isTongueTwisterEnabled());
            mAlarm.setColorCaptureEnabled(mMimicsPreference.isColorCaptureEnabled());
            mAlarm.setExpressYourselfEnabled(mMimicsPreference.isExpressYourselfEnabled());
            mAlarm.setShakeYourPhoneEnabled(mMimicsPreference.isShakeYourPhoneEnabled());//摇一摇
            mAlarm.setSolveMathProblemEnabled(mMimicsPreference.isSolveMathProblemEnabled());//解数学题
            mAlarm.setHitGameEnabled(mMimicsPreference.isHitGameEnabled());//打地鼠

        }
    }

    private void updateRepeatDaysSetting(){
        if (mRepeatDaysPreference.hasChanged()){
            mAlarm.setmMoEnabled(mRepeatDaysPreference.isRepeatMoEnabled());
            mAlarm.setmTuEnabled(mRepeatDaysPreference.isRepeatTuEnabled());
            mAlarm.setmWeEnabled(mRepeatDaysPreference.isRepeatWeEnabled());
            mAlarm.setmThEnabled(mRepeatDaysPreference.isRepeatThEnabled());
            mAlarm.setmFrEnabled(mRepeatDaysPreference.isRepeatFrEnabled());
            mAlarm.setmSaEnabled(mRepeatDaysPreference.isRepeatSaEnabled());
            mAlarm.setmSuEnabled(mRepeatDaysPreference.isRepeatSuEnabled());
            mAlarm.setRepeatingDay(0, mAlarm.ismSuEnabled());
            mAlarm.setRepeatingDay(1, mAlarm.ismMoEnabled());
            mAlarm.setRepeatingDay(2, mAlarm.ismTuEnabled());
            mAlarm.setRepeatingDay(3, mAlarm.ismWeEnabled());
            mAlarm.setRepeatingDay(4, mAlarm.ismThEnabled());
            mAlarm.setRepeatingDay(5, mAlarm.ismFrEnabled());
            mAlarm.setRepeatingDay(6, mAlarm.ismSaEnabled());

        }

    }

    private void updateNameSetting() {
        if (mNamePreference.hasChanged()) {
            mAlarm.setTitle(mNamePreference.getText());
        }
    }



    private void updateTimeSetting() {
        if (mTimePreference.hasChanged()) {
            mAlarm.setTimeHour(mTimePreference.getHour());
            mAlarm.setTimeMinute(mTimePreference.getMinute());
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == RingtonePreference.RINGTONE_PICKER_REQUEST) {
                mRingtonePreference.handleRingtonePickerResult(data);
            }
        }
    }

    public interface AlarmSettingsListener {
        void onShowMimicsSettings(ArrayList<String> enabledMimics);

        void onShowRepeatDaySettings(ArrayList<String>enableRepeatDay);//mouse 2017年2月28号添加

        void onSettingsSaveOrIgnoreChanges();

        void onSettingsDeleteOrNewCancel();
    }
}

