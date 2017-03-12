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
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.CheckBoxPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceScreen;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.microsoft.mimickeralarm.R;
import com.microsoft.mimickeralarm.appcore.DividerItemDecoration;
import com.microsoft.mimickeralarm.utilities.GeneralUtilities;
import com.microsoft.mimickeralarm.utilities.Logger;
import com.microsoft.mimickeralarm.utilities.SettingsUtilities;

import java.util.ArrayList;

/**
 * This is a special PreferenceFragment class that lists the different Mimic settings for an
 * alarm.  The list of Mimics is populated from pref_mimics.xml.
 */
public class MimicsSettingsFragment extends PreferenceFragmentCompat {
    private static String TAG="MimicsSettingsFragment------201731";
    public static final String MIMICS_SETTINGS_FRAGMENT_TAG = "mimics_settings_fragment";
    private static final String ARGS_ENABLED_MIMICS = "enabled_mimics";
    MimicsSettingsListener mCallback;

    public static MimicsSettingsFragment newInstance(ArrayList<String> enabledMimics) {
        MimicsSettingsFragment fragment = new MimicsSettingsFragment();
        Bundle bundle = new Bundle(1);
        bundle.putStringArrayList(ARGS_ENABLED_MIMICS, enabledMimics);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mCallback = (MimicsSettingsListener) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallback = null;
        Logger.flush();
    }

    @Override
    public void onPause() {
        super.onPause();
        // We need to pass the enabled Mimics to the Alarm Settings if we are dismissed
        // using the back button with Alarm Settings already on the backstack
        if (launchedFromAlarmSettings()) {
            onBack();
        }
    }

    @Override
    public void onCreatePreferences(Bundle bundle, final String s) {
        addPreferencesFromResource(R.xml.pref_mimics);
        setDefaultEnabledState();

        Bundle args = getArguments();
        Log.i("----------mimic----", args.toString());
        ArrayList<String> enabledMimics = args.getStringArrayList(ARGS_ENABLED_MIMICS);

        for (String mimicId : enabledMimics) {
            ((CheckBoxPreference) findPreference(mimicId)).setChecked(true);
        }
    }

    @Override
    public RecyclerView onCreateRecyclerView(LayoutInflater inflater, ViewGroup parent,
                                             Bundle savedInstanceState) {
        LinearLayout rootLayout = (LinearLayout) parent.getParent();
        AppBarLayout appBarLayout =
                (AppBarLayout) LayoutInflater.from(getContext()).inflate(R.layout.settings_toolbar,
                        rootLayout,
                        false);
        rootLayout.addView(appBarLayout, 0); // insert at top
        Toolbar bar = (Toolbar) appBarLayout.findViewById(R.id.settings_toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(bar);
        bar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // If Alarm settings is already in the backstack just pop, otherwise callback
                if (launchedFromAlarmSettings()) {
                    getFragmentManager().popBackStack();
                } else {
                    onBack();
                }

            }
        });
        bar.setTitle(R.string.pref_title_mimics);

        RecyclerView recyclerView = super.onCreateRecyclerView(inflater, parent, savedInstanceState);
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(),
                DividerItemDecoration.VERTICAL_LIST));
        return recyclerView;
    }

    public void onBack() {
        mCallback.onMimicsSettingsDismiss(getEnabledMimics());
    }

    private void setDefaultEnabledState() {
        PreferenceScreen preferenceScreen = getPreferenceManager().getPreferenceScreen();
        for (int i = 0; i < preferenceScreen.getPreferenceCount(); i++) {
            ((CheckBoxPreference) preferenceScreen.getPreference(i)).setChecked(false);
        }
        //进一步实现功能
        if (!GeneralUtilities.deviceHasFrontFacingCamera()) {//如果前置摄像头不能打开则说明它的设备不支持
            Preference preference = findPreference(getString(R.string.pref_mimic_express_yourself_id));
            preference.setEnabled(false);
            preference.setSummary(R.string.pref_mimic_not_supported);
        }
        if (!GeneralUtilities.deviceHasRearFacingCamera()) {
            Preference preference = findPreference(getString(R.string.pref_mimic_color_capture_id));
            preference.setEnabled(false);
            preference.setSummary(R.string.pref_mimic_not_supported);
        }
    }

    private ArrayList<String> getEnabledMimics() {
        ArrayList<String> enabledMimics = new ArrayList<>();
        PreferenceScreen preferenceScreen = getPreferenceManager().getPreferenceScreen();
        for (int i = 0; i < preferenceScreen.getPreferenceCount(); i++) {
            CheckBoxPreference preference = (CheckBoxPreference) preferenceScreen.getPreference(i);
            if (preference.isChecked()) {
                enabledMimics.add(preference.getKey());
            }
        }
        return enabledMimics;
    }

    private boolean launchedFromAlarmSettings() {
        return (SettingsUtilities.getAlarmSettingsFragment(getFragmentManager()) != null);
    }

    public interface MimicsSettingsListener {
        void onMimicsSettingsDismiss(ArrayList<String> enabledMimics);
    }
}

