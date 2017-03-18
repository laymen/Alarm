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

package com.microsoft.mimickeralarm.appcore;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.view.menu.MenuBuilder;
import android.text.format.Time;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import com.microsoft.mimickeralarm.R;
import com.microsoft.mimickeralarm.model.Alarm;
import com.microsoft.mimickeralarm.onboarding.OnboardingToSFragment;
import com.microsoft.mimickeralarm.onboarding.OnboardingTutorialFragment;
import com.microsoft.mimickeralarm.scheduling.AlarmNotificationManager;
import com.microsoft.mimickeralarm.scheduling.AlarmScheduler;
import com.microsoft.mimickeralarm.settings.AlarmSettingsFragment;
import com.microsoft.mimickeralarm.settings.MimicsSettingsFragment;
import com.microsoft.mimickeralarm.settings.RepeatDaySettingFragment;
import com.microsoft.mimickeralarm.utilities.GeneralUtilities;
import com.microsoft.mimickeralarm.utilities.Loggable;
import com.microsoft.mimickeralarm.utilities.Logger;
import com.microsoft.mimickeralarm.utilities.SettingsUtilities;
import com.microsoft.mimickeralarm.utilities.SharePreferencesUtils;

import net.hockeyapp.android.FeedbackManager;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.UUID;

/**
 * The AlarmMainActivity is the launch activity for the application.  It has the following
 * features:
 * <p/>
 * On launch it is determined whether the onboarding/tutorial experience has been completed. If
 * not, the user is presented with the tutorial. On completion the user does not see the
 * tutorial again unless it is accessed from the options menu.
 * <p/>
 * After the tutorial is completed, the user is presented with the consent ux with the terms of
 * service.  The user will not be able to use the application until the terms of service have
 * been accepted.
 * <p/>
 * Once the terms of service have been accepted, the alarm list will be shown (AlarmListFragment)
 * on first run and subsequent launches of the application.
 * <p/>
 * Once the user adds or selects an alarm, the activity will transition from the alarm list to
 * the alarm settings page (AlarmSettingsFragment).  From there, the user can transition
 * further to the Mimics settings page (MimicsSettingsFragment).
 * <p/>
 * If the user selects any options on the Options menu - Settings (AlarmGlobalSettingsActivity)
 * , Tutorial, Learn more (LearnMoreActivity) this activity will schedule the transitions to
 * those screens.
 * <p/>
 * This activity can be started (onCreate) or restarted (onIntent) with an alarm id argument,
 * to enable launches to a specific alarm settings page.
 * <p/>
 * This activity overrides the back button to better handle the specific transitions
 * between the different settings pages etc.
 * <p/>
 * This activity listens for volume key presses and updates the alarm volume state while
 * displaying the system volume ui.
 * <p/>
 * The different fragments that are launched from this activity communicate their status back
 * to the activity via listener interfaces.
 */
public class AlarmMainActivity extends AppCompatActivity
        implements AlarmListFragment.AlarmListListener,
        OnboardingTutorialFragment.OnOnboardingTutorialListener,
        OnboardingToSFragment.OnOnboardingToSListener,
        AlarmSettingsFragment.AlarmSettingsListener,
        MimicsSettingsFragment.MimicsSettingsListener,
        RepeatDaySettingFragment.RepeatDaySettingsListener {
    private static String TAG = "AlarmMainActivity";

    public final static String SHOULD_ONBOARD = "onboarding";//引导页
    public final static String SHOULD_TOS = "show-tos";//规则说明
    private SharedPreferences mPreferences = null;
    private AudioManager mAudioManager;//音频管理器
    //记录按返回键的时间
    //private long downTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment);

        String packageName = getApplicationContext().getPackageName();//创建SharePreference数据库
        mPreferences = getSharedPreferences(packageName, MODE_PRIVATE);

        PreferenceManager.setDefaultValues(this, R.xml.pref_global, false);//使用偏好文件xml

        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);//实例化音频

        AlarmNotificationManager.get(this).handleNextAlarmNotificationStatus();//闹钟状态栏提示

        UUID alarmId = (UUID) getIntent().getSerializableExtra(AlarmScheduler.ARGS_ALARM_ID);

        if (alarmId != null) {
            showAlarmSettingsFragment(alarmId.toString());//如果该闹钟存在，那么它可以跳转到SettingFragment中
        }


        Logger.init(this);
    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        UUID alarmId = (UUID) intent.getSerializableExtra(AlarmScheduler.ARGS_ALARM_ID);
        if (alarmId != null) {
            showAlarmSettingsFragment(alarmId.toString());
        }
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        return super.onKeyUp(keyCode, event);
    }

    @Override
    protected void onResume() {
        super.onResume();

        //背景图变化
        FrameLayout frameLayout = (FrameLayout) findViewById(R.id.fragment_container);
        if (SharePreferencesUtils.getInt(this,"ThemeId",0)==0) {
            frameLayout.setBackgroundResource(R.color.dark);
        } else if (SharePreferencesUtils.getInt(this,"ThemeId",0)==1) {
            frameLayout.setBackgroundResource(R.color.green1);

        }

        GeneralUtilities.registerCrashReport(this);//广播接收器
        if (mPreferences.getBoolean(SHOULD_ONBOARD, true)) {//来自SharedPreferences数据库
            if (!hasOnboardingStarted()) {
                Loggable.UserAction userAction = new Loggable.UserAction(Loggable.Key.ACTION_ONBOARDING);
                Logger.track(userAction);

                showTutorial(null);
            }
        } else if (mPreferences.getBoolean(SHOULD_TOS, true)) {
            showToS();
        } else if (!SettingsUtilities.areEditingSettings(getSupportFragmentManager())) {
            GeneralUtilities.showFragment(getSupportFragmentManager(),
                    new AlarmListFragment(),
                    AlarmListFragment.ALARM_LIST_FRAGMENT_TAG);
        }

      //  onCreate(null);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        FeedbackManager.unregister();
        Logger.flush();
    }

    /**
     * 引导页
     *
     * @param item
     */
    public void showTutorial(MenuItem item) {
        if (item != null) {
            GeneralUtilities.showFragmentFromLeft(getSupportFragmentManager(),
                    new OnboardingTutorialFragment(),
                    OnboardingTutorialFragment.ONBOARDING_FRAGMENT_TAG);
        } else {
            GeneralUtilities.showFragment(getSupportFragmentManager(),
                    new OnboardingTutorialFragment(),
                    OnboardingTutorialFragment.ONBOARDING_FRAGMENT_TAG);
        }
    }

    @Override
    public void onSkip() {
        if (mPreferences.getBoolean(SHOULD_TOS, true)) {
            Loggable.UserAction userAction = new Loggable.UserAction(Loggable.Key.ACTION_ONBOARDING_SKIP);
            Logger.track(userAction);
            showToS();
        } else {
            GeneralUtilities.showFragmentFromRight(getSupportFragmentManager(),
                    new AlarmListFragment(),
                    AlarmListFragment.ALARM_LIST_FRAGMENT_TAG);
        }
    }

    @Override
    public void onAccept() {
        GeneralUtilities.showFragmentFromRight(getSupportFragmentManager(),
                new AlarmListFragment(),
                AlarmListFragment.ALARM_LIST_FRAGMENT_TAG);
    }

    @Override
    public void onBackPressed() {
        if (SettingsUtilities.areEditingAlarmSettingsExclusive(getSupportFragmentManager())) {
            SettingsUtilities.getAlarmSettingsFragment(getSupportFragmentManager()).onCancel();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            mAudioManager.adjustStreamVolume(AudioManager.STREAM_ALARM,
                    AudioManager.ADJUST_LOWER,
                    AudioManager.FLAG_SHOW_UI | AudioManager.FLAG_PLAY_SOUND);

        } else if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            mAudioManager.adjustStreamVolume(AudioManager.STREAM_ALARM,
                    AudioManager.ADJUST_RAISE,
                    AudioManager.FLAG_SHOW_UI | AudioManager.FLAG_PLAY_SOUND);

        } else {
            return super.onKeyDown(keyCode, event);
        }

        //2017年3月6号mouse add
//        if (keyCode == KeyEvent.KEYCODE_BACK) {
//            if (System.currentTimeMillis() - downTime > 2000) {
//                Toast.makeText(AlarmMainActivity.this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
//                downTime = System.currentTimeMillis();
//            } else {
//                finish();
//                System.exit(0);
//            }
//            return true;
//        }
        return true;
    }

    /**
     * 关于用户的隐私问题的说明
     */
    public void showToS() {
        mPreferences.edit().putBoolean(SHOULD_ONBOARD, false).apply();
        GeneralUtilities.showFragment(getSupportFragmentManager(),
                new OnboardingToSFragment(),
                OnboardingToSFragment.TOS_FRAGMENT_TAG);
    }

    private boolean hasOnboardingStarted() {
        return (getSupportFragmentManager()
                .findFragmentByTag(OnboardingTutorialFragment.ONBOARDING_FRAGMENT_TAG) != null);
    }

    /**
     * 来自AlarmSettingsFragment中接口的申明+回调，在这里进行具体操作
     */
    @Override
    public void onSettingsSaveOrIgnoreChanges() {
        GeneralUtilities.showFragmentFromLeft(getSupportFragmentManager(),
                new AlarmListFragment(),
                AlarmListFragment.ALARM_LIST_FRAGMENT_TAG);
        onAlarmChanged();
    }

    @Override
    public void onSettingsDeleteOrNewCancel() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(android.R.anim.fade_in, R.anim.slide_down);
        transaction.replace(R.id.fragment_container, new AlarmListFragment());
        transaction.commit();
        onAlarmChanged();
    }

    /**
     * mouse add
     *
     * @param enabledRepeatDay
     */
    @Override
    public void onRepeatDaySettingsDismiss(ArrayList<String> enabledRepeatDay) {
        AlarmSettingsFragment settingsFragment = SettingsUtilities.
                getAlarmSettingsFragment(getSupportFragmentManager());
        if (settingsFragment != null) {
            settingsFragment.updateRepeatDaysPreference(enabledRepeatDay);
        }
//        for (int i = 0; i < enabledRepeatDay.size(); i++) {
//            Log.i("AMainActivityRepday->", enabledRepeatDay.get(i));
//
//        }
    }

    @Override
    public void onShowMimicsSettings(ArrayList<String> enabledMimics) {//携带有来自AlarmSettingsFragment中的Minics初始化后的数据
        SettingsUtilities.transitionFromAlarmToMimicsSettings(getSupportFragmentManager(), enabledMimics);
    }

    /**
     * 把RepeatDay里面选择中天提交出去
     *
     * @param enableRepeatDay
     */
    @Override
    public void onShowRepeatDaySettings(ArrayList<String> enableRepeatDay) {
        SettingsUtilities.transitionFromAlarmToRepeatDaySettings(getSupportFragmentManager(), enableRepeatDay);
    }

    /**
     * 来自MimicsSettingsFragment中接口的申明+回调 ，此处为该接口要做的事
     *
     * @param enabledMimics
     */
    @Override
    public void onMimicsSettingsDismiss(ArrayList<String> enabledMimics) {
        AlarmSettingsFragment settingsFragment = SettingsUtilities.
                getAlarmSettingsFragment(getSupportFragmentManager());
        if (settingsFragment != null) {
            settingsFragment.updateMimicsPreference(enabledMimics);
        }
    }

    /**
     * 来自AlarmListFragment接口
     *
     * @param alarm
     */
    @Override
    public void onAlarmSelected(Alarm alarm) {
        showAlarmSettingsFragment(alarm.getId().toString());
    }

    /**
     * 用到了AlarmNotificationManager
     * 来自AlarmListFragmen提供的接口
     */
    @Override
    public void onAlarmChanged() {
        AlarmNotificationManager.get(this).handleNextAlarmNotificationStatus();
    }

    private void showAlarmSettingsFragment(String alarmId) {
        SettingsUtilities.transitionFromAlarmListToSettings(getSupportFragmentManager(), alarmId);
    }

    /**
     * 通过反射，设置menu显示icon
     *
     * @param view
     * @param menu
     * @return
     */
    @Override
    protected boolean onPrepareOptionsPanel(View view, Menu menu) {
        if (menu != null) {
            if (menu.getClass() == MenuBuilder.class) {
                try {
                    Method m = menu.getClass().getDeclaredMethod("setOptionalIconsVisible", Boolean.TYPE);
                    m.setAccessible(true);
                    m.invoke(menu, true);
                } catch (Exception e) {
                }
            }
        }
        return super.onPrepareOptionsPanel(view, menu);
    }

}
