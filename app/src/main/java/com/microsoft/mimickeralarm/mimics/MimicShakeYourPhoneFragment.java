package com.microsoft.mimickeralarm.mimics;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.microsoft.mimickeralarm.R;
import com.microsoft.mimickeralarm.mimics.MimicFactory.MimicResultListener;
import com.microsoft.mimickeralarm.utilities.Logger;

import java.lang.ref.WeakReference;

/**
 * 添加摇一摇的方式关闭闹钟
 * Created by Mouse on 2017/3/2 0002.
 */
public class MimicShakeYourPhoneFragment extends Fragment implements SensorEventListener, IMimicImplementation2 {
    private static final int TIMEOUT_MILLISECONDS = 30000;
    MimicResultListener mCallback;
    private static final String TAG = "ShakeYourPhoneFragment";
    private static final int START_SHAKE = 0x1;
    private static final int AGAIN_SHAKE = 0x2;
    private static final int END_SHAKE = 0x3;
    static final int UPDATE_INTERVAL = 100;//检测的时间间隔
    long mLastUpdateTime = 0;//上一次检测的时间
    float mLastX = 0;
    float mLastY = 0;
    float mLastZ = 0;//上一次检测时，加速度在x、y、z方向上的分量，用于和当前加速度比较求差。
    private SensorManager mSensorManager;
    private Sensor mAccelerometerSensor;//加速
    private Vibrator mVibrator;//手机震动
    private SoundPool mSoundPool;//摇一摇音效

    //记录摇动状态
    private boolean isShake = false;
    //摇晃值
    private int shakeValue = 0;

    private LinearLayout mTopLayout;
    private LinearLayout mBottomLayout;
    private ImageView mTopLine;
    private ImageView mBottomLine;

    private MyHandler mHandler;
    private int mWeiChatAudio;
    private View view;
    private Context mContext;

    private IMimicNormal mStateManagerNormal;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_shake_your_phone, container, false);
        initView(view);
        mContext = getContext();

        mHandler = new MyHandler(this);
        //初始化SoundPool
        mSoundPool = new SoundPool(1, AudioManager.STREAM_SYSTEM, 5);
        mWeiChatAudio = mSoundPool.load(mContext, R.raw.weichat_audio, 1);
        //获取Vibrator震动服务
        mVibrator = (Vibrator) mContext.getSystemService(Context.VIBRATOR_SERVICE);

        mStateManagerNormal = new MimicStateManagerNomal();//这个是对IMimicMediator接口进行具体的实现。
        mStateManagerNormal.registerCountDownTimerNormal((CountDownTimerView) view.findViewById(R.id.countdown_timer), TIMEOUT_MILLISECONDS);
        mStateManagerNormal.registerStateBannerNormal((MimicStateBanner) view.findViewById(R.id.mimic_state));
        TextView instruction = (TextView) view.findViewById(R.id.instruction_text);
        instruction.setText("摇一摇你的手机");
        mStateManagerNormal.registerMimicNormal(this);
        return view;
    }

    private void initView(View view) {

        mTopLayout = (LinearLayout) view.findViewById(R.id.main_linear_top);
        mBottomLayout = ((LinearLayout) view.findViewById(R.id.main_linear_bottom));
        mTopLine = (ImageView) view.findViewById(R.id.main_shake_top_line);
        mBottomLine = (ImageView) view.findViewById(R.id.main_shake_bottom_line);

        //默认
        mTopLine.setVisibility(View.GONE);
        mBottomLine.setVisibility(View.GONE);
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
        Logger.flush();
    }

    @Override
    public void onStart() {
        super.onStart();
        mStateManagerNormal.startNormal();//启动进度条和时间
        //获取 SensorManager 负责管理传感器
        mSensorManager = ((SensorManager) mContext.getSystemService(Context.SENSOR_SERVICE));
        if (mSensorManager != null) {
            //获取加速度传感器
            mAccelerometerSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            if (mAccelerometerSensor != null) {
                mSensorManager.registerListener(this, mAccelerometerSensor, SensorManager.SENSOR_DELAY_UI);
            }
        }
    }

    @Override
    public void onPause() {
        // 务必要在pause中注销 mSensorManager
        // 否则会造成界面退出后摇一摇依旧生效的bug
        if (mSensorManager != null) {
            mSensorManager.unregisterListener(this);
        }
        super.onPause();

    }

    @Override
    public void onStop() {
        super.onStop();
        mStateManagerNormal.stopNormal();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Logger.flush();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        long currentTime = System.currentTimeMillis();
        long diffTime = currentTime - mLastUpdateTime;
        if (diffTime < UPDATE_INTERVAL)
            return;
        mLastUpdateTime = currentTime;
        //获取三个方向值
        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];
        float deltaX = x - mLastX;
        float deltaY = y - mLastY;
        float deltaZ = z - mLastZ;
        mLastX = x;
        mLastY = y;
        mLastZ = z;
        float delta = (float) Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ
                * deltaZ)
                / diffTime * 10000;
        shakeValue = (int) delta;

        int type = event.sensor.getType();
        final GameResultNormal gameResultNormal = new GameResultNormal();
        Log.i("mouse is laymen", "first----->" + shakeValue);
        if (type == Sensor.TYPE_ACCELEROMETER) {//加速度感应检测
            if (shakeValue - 5000 > 0 && !isShake) {

                gameResultNormal.success = true;
                gameResultNormal.message = "闯关成功";
                gameSuccess(gameResultNormal);//闯关成功

                isShake = true;
                // TODO: 2016/10/19 实现摇动逻辑, 摇动后进行震动
                Thread thread = new Thread() {
                    @Override
                    public void run() {
                        super.run();
                        try {
                            Log.d(TAG, "onSensorChanged: 摇动");
                            //开始震动 发出提示音 展示动画效果
                            mHandler.obtainMessage(START_SHAKE).sendToTarget();
                            Thread.sleep(500);
                            //再来一次震动提示
                            mHandler.obtainMessage(AGAIN_SHAKE).sendToTarget();
                            Thread.sleep(500);
                            mHandler.obtainMessage(END_SHAKE).sendToTarget();

                            if (shakeValue - 5000 > 0) {//摇一摇的力度大小
                                Log.i("mouse is laymen", "second----->" + shakeValue);

                                if (mStateManagerNormal.isMimicRunning()) {//时间没有完
                                    Log.i("mouse is laymen", "three----->" + shakeValue);
                                    gameResultNormal.success = true;
                                    gameResultNormal.message = "闯关成功";
                                    gameSuccess(gameResultNormal);//闯关成功

                                } else {//时间完了
                                    Log.i("mouse is laymen", "four----->" + shakeValue);
                                    gameFailure(null, false);//时间完了
                                }

                            } else {//摇摆的力度不够时
                                Toast.makeText(mContext, "mouse===>" + shakeValue, Toast.LENGTH_SHORT).show();
                                if (mStateManagerNormal.isMimicRunning()) {//进度条还是有的
                                    Log.i("mouse is laymen", "five----->" + shakeValue);
                                    gameResultNormal.success = false;//挑战游戏失败
                                    gameResultNormal.message = "你摇手机的力度不够大呀~加油";
                                    gameFailure(gameResultNormal, true);
                                } else {//进度条消耗完了
                                    Log.i("mouse is laymen", "six----->" + shakeValue);
                                    gameFailure(null, false);//直接说时间完了就行了
                                }
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                };
                thread.start();
            }

        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    //IMimicImplementation2接口的实现，间接使用MimicFactory中的接口
    @Override
    public void initializeNormal() {

    }

    @Override
    public void onCountDownTimerExpiredNormal() {
        gameFailure(null, false);
    }

    @Override
    public void onSucceededNormal() {
        if (mCallback != null) {
            mCallback.onMimicSuccess(null);//来自MimicFactory中的接口声明,在此处进行回调，在AlarmRingActivity中进行具体实现
        }
    }

    @Override
    public void onFailedNormal() {
        if (mCallback != null) {
            mCallback.onMimicFailure();//来自MimicFactory中的接口声明,在此处进行回调，在AlarmRingActivity中进行具体实现
        }
    }

    protected void gameFailure(GameResultNormal gameResultNormal, boolean allowRetry) {
        if (allowRetry) {//不是因为时间完了
            //进入到铃声中
            String failureMessage = getString(R.string.mimic_failure_message);
            if (gameResultNormal != null && gameResultNormal.message != null) {
                failureMessage = gameResultNormal.message;
            }
            mStateManagerNormal.onMimicFailureWithRetryNormal(failureMessage);//不是因为时间用完了，而是你的任务完成失败，故需要再来一场，
        } else {//时间完了
            String failureMessage = getString(R.string.mimic_time_up_message);
            mStateManagerNormal.onMimicFailureNormal(failureMessage);
        }
        mCallback.onMimicFailure();//2017.3.15
    }

    protected void gameSuccess(GameResultNormal gameResult) {
        String successMessage = getString(R.string.mimic_success_message);
        if (gameResult.message != null) {
            successMessage = gameResult.message;
        }
        mStateManagerNormal.onMimicSuccessNormal(successMessage);
        mCallback.onMimicSuccess("");
    }

    private static class MyHandler extends Handler {
        private WeakReference<MimicShakeYourPhoneFragment> mReference;
        private MimicShakeYourPhoneFragment mActivity;

        public MyHandler(MimicShakeYourPhoneFragment activity) {
            mReference = new WeakReference<>(activity);
            if (mReference != null) {
                mActivity = mReference.get();
            }
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case START_SHAKE:
                    //This method requires the caller to hold the permission VIBRATE.
                    mActivity.mVibrator.vibrate(300);
                    //发出提示音
                    mActivity.mSoundPool.play(mActivity.mWeiChatAudio, 1, 1, 0, 0, 1);
                    mActivity.mTopLine.setVisibility(View.VISIBLE);
                    mActivity.mBottomLine.setVisibility(View.VISIBLE);
                    mActivity.startAnimation(false);//参数含义: (不是回来) 也就是说两张图片分散开的动画
                    break;
                case AGAIN_SHAKE:
                    mActivity.mVibrator.vibrate(300);
                    break;
                case END_SHAKE:
                    //整体效果结束, 将震动设置为false
                    mActivity.isShake = false;
                    // 展示上下两种图片回来的效果
                    mActivity.startAnimation(true);
                    break;
            }
        }
    }

    /**
     * 开启 摇一摇动画
     *
     * @param isBack 是否是返回初识状态
     */
    private void startAnimation(boolean isBack) {
        //动画坐标移动的位置的类型是相对自己的
        int type = Animation.RELATIVE_TO_SELF;

        float topFromY;
        float topToY;
        float bottomFromY;
        float bottomToY;
        if (isBack) {
            topFromY = -0.5f;
            topToY = 0;
            bottomFromY = 0.5f;
            bottomToY = 0;
        } else {
            topFromY = 0;
            topToY = -0.5f;
            bottomFromY = 0;
            bottomToY = 0.5f;
        }

        //上面图片的动画效果
        TranslateAnimation topAnim = new TranslateAnimation(
                type, 0, type, 0, type, topFromY, type, topToY
        );
        topAnim.setDuration(200);
        //动画终止时停留在最后一帧~不然会回到没有执行之前的状态
        topAnim.setFillAfter(true);

        //底部的动画效果
        TranslateAnimation bottomAnim = new TranslateAnimation(
                type, 0, type, 0, type, bottomFromY, type, bottomToY
        );
        bottomAnim.setDuration(200);
        bottomAnim.setFillAfter(true);

        //大家一定不要忘记, 当要回来时, 我们中间的两根线需要GONE掉
        if (isBack) {
            bottomAnim.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    //当动画结束后 , 将中间两条线GONE掉, 不让其占位
                    mTopLine.setVisibility(View.GONE);
                    mBottomLine.setVisibility(View.GONE);
                }
            });
        }
        //设置动画
        mTopLayout.startAnimation(topAnim);
        mBottomLayout.startAnimation(bottomAnim);

    }

    protected class GameResultNormal {
        boolean success = false;
        String message = null;
    }
}
