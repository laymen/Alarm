package com.microsoft.mimickeralarm.mimics;

/**
 * 主要控制不是联网的游戏
 * Created by Administrator on 2017/3/4 0004.
 */
public interface IMimicNormal {
    void startNormal();
    void stopNormal();
    boolean isMimicRunning();

    void onMimicSuccessNormal(String successMessage);
    void onMimicFailureWithRetryNormal(String failureMessage);
    void onMimicFailureNormal(String failureMessage);

    void registerStateBannerNormal(MimicStateBanner mimicStateBanner);//显示游戏进行的情况
    void registerCountDownTimerNormal(CountDownTimerView countDownTimerView, int timeout);//显示进度条进行的情况
    void registerMimicNormal(IMimicImplementation2 mimic);
}
