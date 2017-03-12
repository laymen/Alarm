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

package com.microsoft.mimickeralarm.mimics;

/**
 * This interface is implemented by the MimicStateManager class to control the state of common
 * Mimic UI controls
 */

enum MimicButtonBehavior {
    AUDIO,
    CAMERA
}

public interface IMimicMediator {
    void start();
    void stop();
    boolean isMimicRunning();

    void onMimicSuccess(String successMessage);
    void onMimicFailureWithRetry(String failureMessage);
    void onMimicFailure(String failureMessage);
    void onMimicInternalError();

    void registerStateBanner(MimicStateBanner mimicStateBanner);//显示游戏进行的情况
    void registerCountDownTimer(CountDownTimerView countDownTimerView, int timeout);//显示进度条进行的情况
    void registerProgressButton(ProgressButton progressButton, MimicButtonBehavior buttonBehavior);
    void registerMimic(IMimicImplementation mimic);
}