package com.microsoft.mimickeralarm.settings;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.CheckBoxPreference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceScreen;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.microsoft.mimickeralarm.R;
import com.microsoft.mimickeralarm.appcore.DividerItemDecoration;
import com.microsoft.mimickeralarm.utilities.Logger;
import com.microsoft.mimickeralarm.utilities.SettingsUtilities;

import java.util.ArrayList;

/**
 * Created by mouse on 2017/2/28 0028.
 */
public class RepeatDaySettingFragment extends PreferenceFragmentCompat {
    public static final String REPEATDAYS_SETTINGS_FRAGMENT_TAG = "repeat_days_settings_fragment";
    private static final String ARGS_ENABLED_REPEATDAYS = "enabled_repeat_days";
    RepeatDaySettingsListener mCallback;
    //添加反向选择的button
    private Button mInverstChoose;
    private boolean flag = false;

    public static RepeatDaySettingFragment newInstance(ArrayList<String> enabledRepeatDays) {
        RepeatDaySettingFragment fragment = new RepeatDaySettingFragment();
        Bundle bundle = new Bundle(1);
        bundle.putStringArrayList(ARGS_ENABLED_REPEATDAYS, enabledRepeatDays);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mCallback = (RepeatDaySettingsListener) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallback = null;//-----------------2017年3月6号改动
        Logger.flush();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (launchedFromAlarmSettings()) {
            onBack();
        }
    }


    public void onBack() {
        mCallback.onRepeatDaySettingsDismiss(getEnabledRepeatDays());
    }

    /**
     * 稳定后的情况
     *
     * @param savedInstanceState
     * @param rootKey
     */
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.pref_repeatdays);
        setDefaultEnabledState();

        Bundle args = getArguments();//获得数据

        ArrayList<String> enabledRepeatDays = args.getStringArrayList(ARGS_ENABLED_REPEATDAYS);

        if (flag) {//反向选择为true要求之前的选择为false
            for (String mId : enabledRepeatDays) {
                ((CheckBoxPreference) findPreference(mId)).setChecked(false);
            }
        } else {//反向选择为false,要求之前的选择是true
            for (String mId : enabledRepeatDays) {
                ((CheckBoxPreference) findPreference(mId)).setChecked(true);
            }
        }
        Log.i("ReDaySettingFg--mouse", enabledRepeatDays + "");//都是为true的日子
    }

    @Override
    public RecyclerView onCreateRecyclerView(LayoutInflater inflater, ViewGroup parent,
                                             Bundle savedInstanceState) {
        LinearLayout rootLayout = (LinearLayout) parent.getParent();//父布局
        AppBarLayout appBarLayout =
                (AppBarLayout) LayoutInflater.from(getContext()).inflate(R.layout.settings_toolbar,
                        rootLayout,//子布局
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
                    onBack();//--------------------2017年3月6号
                }

            }
        });
        bar.setTitle(R.string.pre_title_repeatdays);
        mInverstChoose = (Button) appBarLayout.findViewById(R.id.choose);
        mInverstChoose.setVisibility(View.VISIBLE);
        mInverstChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flag = true;

                ArrayList<String> enabledRepeatDays = new ArrayList<>();
                PreferenceScreen preferenceScreen = getPreferenceManager().getPreferenceScreen();
                for (int i = 0; i < preferenceScreen.getPreferenceCount(); i++) {
                    CheckBoxPreference preference = (CheckBoxPreference) preferenceScreen.getPreference(i);
                    if (!preference.isChecked()) {//之前没有选择
                        enabledRepeatDays.add(preference.getKey());
                        preference.setChecked(true);//要求当场就更新反向的选择即要求为true
                        flag = false;//因为你每次点击这个button是它会把它设置成true
                    } else {//如果没有选择反向选择
                        if (preference.isChecked()) {//也就是之前选择的
                            preference.setChecked(false);//要求当场更新为没有选择
                            flag = false;
                        }
                    }//end if

                }//end for
            }//for
        });


        RecyclerView recyclerView = super.onCreateRecyclerView(inflater, parent, savedInstanceState);
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(),
                DividerItemDecoration.VERTICAL_LIST));

        return recyclerView;
    }

    /**
     * 墨粉表示全都不选中
     */
    private void setDefaultEnabledState() {
        PreferenceScreen preferenceScreen = getPreferenceManager().getPreferenceScreen();
        for (int i = 0; i < preferenceScreen.getPreferenceCount(); i++) {
            ((CheckBoxPreference) preferenceScreen.getPreference(i)).setChecked(false);
        }
    }

    /**
     * 用户手动选择的哪些天将被记录下来
     *
     * @return
     */
    private ArrayList<String> getEnabledRepeatDays() {
        ArrayList<String> enabledRepeatDays = new ArrayList<>();
        PreferenceScreen preferenceScreen = getPreferenceManager().getPreferenceScreen();

        for (int i = 0; i < preferenceScreen.getPreferenceCount(); i++) {
            CheckBoxPreference preference = (CheckBoxPreference)preferenceScreen.getPreference(i);
            if (preference.isChecked()) {
                enabledRepeatDays.add(preference.getKey());
            }
        }
        Log.i("ReDaySettingFg--mouse2", enabledRepeatDays + "");//都是为true的日子
        return enabledRepeatDays;
    }

    private boolean launchedFromAlarmSettings() {
        return (SettingsUtilities.getAlarmSettingsFragment(getFragmentManager()) != null);
    }

    public interface RepeatDaySettingsListener {
        void onRepeatDaySettingsDismiss(ArrayList<String> enabledRepeatDay);
    }
}
