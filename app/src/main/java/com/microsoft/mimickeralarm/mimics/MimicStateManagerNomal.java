package com.microsoft.mimickeralarm.mimics;

import android.util.Log;

import java.lang.ref.WeakReference;

/**
 * Created by Administrator on 2017/3/4 0004.
 */
public class MimicStateManagerNomal implements IMimicNormal {
    private static String TAG = "MimicStateManagerNomal";
    MimicStateBanner mMimicStateBanner;
    CountDownTimerView mCountDownTimer;
    boolean mMimicRunning;

    WeakReference<IMimicImplementation2> mMimicRef;//对接口IMimicImplementation的弱引用

    @Override
    public void startNormal() {
        Log.d(TAG, "Entered start!");
        mMimicRunning = true;
        mCountDownTimer.start();
        IMimicImplementation2 mimic = mMimicRef.get();
        if (mimic == null) {
            mimic.initializeNormal();
        }
    }

    @Override
    public void stopNormal() {
        Log.d(TAG, "Entered stop!");
        mMimicRunning = false;

    }


    @Override
    public boolean isMimicRunning() {
        return mMimicRunning;
    }

    @Override
    public void onMimicSuccessNormal(String successMessage) {
        Log.d(TAG, "Entered onMimicSuccess!");
        if (isMimicRunning()) {
            mCountDownTimer.stop();
            mMimicStateBanner.success(successMessage, new MimicStateBanner.Command() {
                @Override
                public void execute() {
                    Log.d(TAG, "Entered onMimicSuccess callback!");
                    if (isMimicRunning()) {
                        IMimicImplementation2 mimic = mMimicRef.get();
                        if (mimic != null) {
                            mimic.onSucceededNormal();//又是对接口IMimicImplementation的回调，实现在？
                        }
                    }
                }
            });
        }
    }

    @Override
    public void onMimicFailureWithRetryNormal(String failureMessage) {
        Log.d(TAG, "Entered onMimicFailureWithRetry!");
        // If the countdown timer has just expired and has already registered a failure command,
        // then we should avoid changing state
        if (isMimicRunning() && !mCountDownTimer.hasExpired()) {
            mCountDownTimer.pause();
            mMimicStateBanner.failure(failureMessage, new MimicStateBanner.Command() {
                @Override
                public void execute() {
                    Log.d(TAG, "Entered onMimicFailureWithRetry callback!");
                    if (isMimicRunning()) {
                        mCountDownTimer.resume();
                    }
                }
            });
        }
    }

    @Override
    public void onMimicFailureNormal(String failureMessage) {
        Log.d(TAG, "Entered onMimicFailure!");
        mCountDownTimer.stop();
        mMimicStateBanner.failure(failureMessage, new MimicStateBanner.Command() {
            @Override
            public void execute() {
                Log.d(TAG, "Entered onMimicFailure callback!");
                IMimicImplementation2 mimic = mMimicRef.get();
                if (mimic != null) {
                    mimic.onFailedNormal();//又是对接口IMimicImplementation的回调，实现在？
                }
            }
        });
    }

    @Override
    public void registerStateBannerNormal(MimicStateBanner mimicStateBanner) {
        mMimicStateBanner = mimicStateBanner;
    }

    @Override
    public void registerCountDownTimerNormal(CountDownTimerView countDownTimerView, int timeout) {
        mCountDownTimer = countDownTimerView;
        mCountDownTimer.init(timeout, new CountDownTimerView.Command() {
            @Override
            public void execute() {
                Log.d(TAG, "Countdown timer expired!");
                if (isMimicRunning()) {
                    IMimicImplementation2 mimic = mMimicRef.get();
                    if (mimic != null) {
                        mimic.onCountDownTimerExpiredNormal();//又是对接口IMimicImplementation的回调，实现在？
                    }
                }
            }
        });
    }

    @Override
    public void registerMimicNormal(IMimicImplementation2 mimic) {
        mMimicRef = new WeakReference<>(mimic);
    }
}
