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

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.microsoft.mimickeralarm.R;
import com.microsoft.mimickeralarm.mimics.MimicFactory.MimicResultListener;
import com.microsoft.mimickeralarm.ringing.ShareFragment;
import com.microsoft.mimickeralarm.utilities.Logger;

/**
 * Base class for all camera based mimic games
 * it provides a capture button, countdown timer, game state banner, and a preview surface
 * Classes that inherits from this will only have to override the verify function.
 **/
@SuppressWarnings("deprecation")
abstract class MimicWithCameraFragment extends Fragment
    implements IMimicImplementation {

    private static final String LOGTAG = "MimicWithCameraFragment";
    private static final int TIMEOUT_MILLISECONDS = 30000;
    // Max width for sending to Project Oxford, reduce latency
    private static final int MAX_WIDTH = 500;
    private static final int LIGHT_THRESHOLD = 15;

    protected static int CameraFacing = Camera.CameraInfo.CAMERA_FACING_FRONT;
    MimicResultListener mCallback;//--------------------------------------------是给AlarmRingingActivty进行实现的
    private CameraPreview   mCameraPreview;
    private IMimicMediator mStateManager;//---是对接口IMimicMediator的具体实现，又是对接口IMimicImplementation的弱引用
    private Uri mSharableUri;
    private SensorManager mSensorManager;
    private Sensor mLightSensor;
    private SensorEventListener mLightSensorListener;
    private Toast mTooDarkToast;
    private ToggleButton mFlashButton;

    private Point mSize;
    private CameraPreview.CapturedImageCallbackAsync onCaptureCallback = new CameraPreview.CapturedImageCallbackAsync() {
        @Override
        public void execute(Bitmap bitmap) {
            new processOnProjectOxfordAsync().execute(bitmap);
        }
    };

    private CameraPreview.CameraInitializedCallback onCameraInitialized = new CameraPreview.CameraInitializedCallback() {
        @Override
        public void execute(boolean success) {
            if (success) {
                if (mCameraPreview.isFlashSupported()){
                    mFlashButton.setVisibility(View.VISIBLE);
                    mFlashButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton button, boolean isChecked) {
                            mCameraPreview.changeFlashState(isChecked);
                            button.setChecked(mCameraPreview.getFlashState());
                        }
                    });
                }
            }
        }
    };

    private CameraPreview.FlashStateCallback onFlashStateCallback = new CameraPreview.FlashStateCallback() {
        @Override
        public void execute(boolean state) {
            mFlashButton.setChecked(state);
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_camera_mimic, container, false);

        SurfaceView previewView = (SurfaceView) view.findViewById(R.id.camera_preview_view);

        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        mSize = size;
        double aspectRatio = size.y > size.x ?
                (double)size.y / (double)size.x : (double)size.x / (double)size.y;
        mCameraPreview = new CameraPreview(previewView,
                new CameraPreview.OnCameraPreviewException() {
                    @Override
                    public void execute() {
                        mStateManager.onMimicInternalError();//是对接口IMimicMediator的回调
                    }
                },
                onCameraInitialized,
                aspectRatio, CameraFacing);

        View overlay = view.findViewById(R.id.camera_preview_overlay);
        overlay.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    // Camera sensor ranges from -1000 to 1000 regardless of aspect ratio, sizes, resolution, ...
                    int deltaX = (int) (((float) mSize.x - event.getX()) / mSize.x * -2000) + 1000;
                    int deltaY = (int) (((float) mSize.y - event.getY()) / mSize.y * -2000) + 1000;
                    mCameraPreview.onFocus(deltaX, deltaY);
                }
                return true;
            }
        });

        mFlashButton = (ToggleButton) view.findViewById(R.id.camera_flash_toggle);

        ProgressButton progressButton = (ProgressButton) view.findViewById(R.id.capture_button);
        progressButton.setReadyState(ProgressButton.State.ReadyCamera);

        mStateManager = new MimicStateManager();
        mStateManager.registerCountDownTimer(
                (CountDownTimerView) view.findViewById(R.id.countdown_timer), TIMEOUT_MILLISECONDS);
        mStateManager.registerStateBanner((MimicStateBanner) view.findViewById(R.id.mimic_state));
        mStateManager.registerProgressButton(progressButton, MimicButtonBehavior.CAMERA);//
        mStateManager.registerMimic(this);

        mSensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        if (mSensorManager != null) {
            mLightSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        }
        mLightSensorListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                if (event.values[0] < LIGHT_THRESHOLD && CameraFacing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                    mTooDarkToast.show();//---------------------------------------------------------------------------------
                }
                else {
                    mTooDarkToast.cancel();
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {}
        };
        // This toast is only shown when there is not enough light
        mTooDarkToast = Toast.makeText(getActivity(), getString(R.string.mimic_camera_too_dark), Toast.LENGTH_SHORT);

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mCallback = (MimicResultListener) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallback = null;
    }

    @Override
    public void onStart() {
        super.onStart();

        if (mSensorManager != null && mLightSensorListener != null) {
            mSensorManager.registerListener(mLightSensorListener, mLightSensor, SensorManager.SENSOR_DELAY_UI);
        }

        mStateManager.start();
    }

    @Override
    public void onStop() {
        super.onStop();
        mStateManager.stop();

        mTooDarkToast.cancel();
        if (mSensorManager != null && mLightSensorListener != null) {
            mSensorManager.unregisterListener(mLightSensorListener);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Logger.flush();
    }

    /**
     * 来自IMimicImplementation接口的声明, X实现在MimicStateManager中
     */
    @Override
    public void initializeCapture() {//来自IMimicImplementation接口的声明,实现在这里
    }

    @Override
    public void startCapture() {//来自IMimicImplementation接口的声明,实现在这里
        mCameraPreview.onCapture(onCaptureCallback, onFlashStateCallback);
    }

    @Override
    public void stopCapture() {
        mCameraPreview.stop();
    }//来自IMimicImplementation接口的声明,实现在这里

    @Override
    public void onCountDownTimerExpired() {//来自IMimicImplementation接口的声明,实现在这里

        gameFailure(null, false);
    }

    /**
     * 来自MimicFactory中的接口声明,在此处进行回调
     */
    @Override
    public void onSucceeded() {//来自IMimicImplementation接口的声明
        if (mCallback != null) {
            mCallback.onMimicSuccess(mSharableUri.getPath());//来自MimicFactory中的接口声明,在此处进行回调，在AlarmRingActivity中进行具体实现
        }
    }

    @Override
    public void onFailed() {//来自IMimicImplementation接口的声明
        if (mCallback != null) {
            mCallback.onMimicFailure();//来自MimicFactory中的接口声明,在此处进行回调，在AlarmRingActivity中进行具体实现
        }
    }

    @Override
    public void onInternalError() {//来自IMimicImplementation接口的声明
        if (mCallback != null) {
            mCallback.onMimicError();//来自MimicFactory中的接口声明,在此处进行回调,在AlarmRingActivity中进行具体实现
        }
    }
//-----------------------------------------------------------------------------------------------------------
    protected void gameSuccess(final GameResult gameResult) {
        mSharableUri = gameResult.shareableUri;
        String successMessage = getString(R.string.mimic_success_message);
        if (gameResult.message != null) {
            successMessage = gameResult.message;
        }
        mStateManager.onMimicSuccess(successMessage);
    }

    protected void gameFailure(GameResult gameResult, boolean allowRetry) {
        if (allowRetry) {
            try {
                mCameraPreview.start();
            }
            catch (MimicException ex) {
                Logger.trackException(ex);
                mStateManager.onMimicInternalError();
            }

            String failureMessage = getString(R.string.mimic_failure_message);
            if (gameResult != null && gameResult.message != null) {
                failureMessage = gameResult.message;
            }
            mStateManager.onMimicFailureWithRetry(failureMessage);
        } else {
            mStateManager.onMimicFailure(getString(R.string.mimic_time_up_message));
        }
    }

    abstract protected GameResult verify(Bitmap bitmap);

    public class processOnProjectOxfordAsync extends AsyncTask<Bitmap, String, GameResult> {

        @Override
        protected GameResult doInBackground(Bitmap... bitmaps) {
            GameResult gameResult = null;
            try {
                if (bitmaps.length > 0) {
                    int width = bitmaps[0].getWidth();
                    int height = bitmaps[0].getHeight();
                    float ratio = (float) height / width;
                    width = Math.min(width, MAX_WIDTH);
                    height = (int) (width * ratio);
                    gameResult = verify(Bitmap.createScaledBitmap(bitmaps[0], width, height, true));
                    if (gameResult.success) {
                        gameResult.shareableUri = ShareFragment.saveShareableBitmap(getActivity(), bitmaps[0], gameResult.question);
                        bitmaps[0].recycle();
                    }
                }
            } catch (Exception ex) {
                Logger.trackException(ex);
            }
            return gameResult;
        }


        @Override
        protected void onPostExecute(GameResult gameResult) {
            super.onPostExecute(gameResult);
            if (mStateManager.isMimicRunning()) {
                if (gameResult.success) {
                    gameSuccess(gameResult);
                } else {
                    gameFailure(gameResult, true);
                }
            }
        }
    }

    protected class GameResult {
        boolean success = false;
        String message = null;
        Uri shareableUri = null;
        String question = null;
    }
}

