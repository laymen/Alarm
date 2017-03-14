package com.microsoft.mimickeralarm.mimics;


import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;

import com.microsoft.mimickeralarm.R;
import com.microsoft.mimickeralarm.hitgame.MainActivity;
import com.microsoft.mimickeralarm.hitgame.common.Const;
import com.microsoft.mimickeralarm.hitgame.util.MUtils;
import com.microsoft.mimickeralarm.utilities.Logger;

/**
 * Created by Administrator on 2017/3/7 0007.
 */
public class MimicHitGameFragment extends Fragment {

    private Context mContext;
    private MediaPlayer mBgMediaPlayer;
    private boolean flag=true;
    private Button btnLevel;
    private Intent intent;
    Bundle bundle;
    View view;
    MimicFactory.MimicResultListener mCallback;
    private static String LOGTAG = "MimicHitGameFragment";
    private String returnFlag;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);//全屏
        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);   //应用运行时，保持屏幕高亮，不锁屏

        init();
        Log.i("game------2017.3.12--","))))))))))))))))))))))))))))))");
        view = inflater.inflate(R.layout.fragment_hit_game, container, false);
        btnLevel = (Button) view.findViewById(R.id.btnGameLevel);
        intent = new Intent(getActivity(), MainActivity.class);
        bundle = new Bundle();
        intent.putExtras(bundle);
        btnLevel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (authController(view.getId())) {
                    startActivity(intent);
                    android.os.Process.killProcess(android.os.Process.myPid());

                }
            }
        });

        if (!btnLevel.isPressed()) {
            new Thread(){
                @Override
                public void run() {
                    try {
                        Log.e("LPB-------------------","自动执行啦");
                        Thread.sleep(3000);
                        intent.setClass(getActivity(),MainActivity.class);
                        startActivity(intent);
                        android.os.Process.killProcess(android.os.Process.myPid());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            }.start();
        }
        return view;
    }

    /**
     * 屏幕初始化
     */
    private void init() {
        mContext = getActivity().getApplication();
        mBgMediaPlayer = MediaPlayer.create(mContext, R.raw.background);
        mBgMediaPlayer.setLooping(true);//循环
        DisplayMetrics mDisplayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(mDisplayMetrics);
        Const.CURRENT_SCREEN_WIDTH = mDisplayMetrics.widthPixels;
        Const.CURRENT_SCREEN_HEIGHT = mDisplayMetrics.heightPixels;
        Const.CURREN_SCALE = Const.CURRENT_SCREEN_WIDTH/ Const.DEF_SCREEN_WIDTH;
        Const.CURREN_WIDTH_SCALE = Const.CURRENT_SCREEN_WIDTH/ Const.DEF_SCREEN_WIDTH;
        Const.CURREN_HEIGHT_SCALE = Const.CURRENT_SCREEN_HEIGHT/ Const.DEF_SCREEN_HEIGHT;
        Const.CURRENT_BLOCK_WIDTH_HEIGHT = Const.CURREN_SCALE* Const.DEF_BLOCK_WIDTH_HEIGHT;
        Const.CURRENT_BLOCK_WIDTH = Const.CURREN_WIDTH_SCALE* Const.DEF_BLOCK_WIDTH_HEIGHT;
        Const.CURRENT_BLOCK_HEIGHT = Const.CURREN_HEIGHT_SCALE* Const.DEF_BLOCK_WIDTH_HEIGHT;
    }

    /* (non-Javadoc)
     * @see android.app.Activity#onResume()
     */
    @Override
    public void onResume() {
        adMethoe();
        if(Const.backgroundMusicOn && mBgMediaPlayer!=null && !mBgMediaPlayer.isPlaying()){
            mBgMediaPlayer.start();
        }
        super.onResume();
        /*if (!btnLevel.isPressed()) {
            new Thread(){
                @Override
                public void run() {
                    try {
                        Thread.sleep(3000);
                        intent.setClass(getActivity(),MainActivity.class);
                        startActivity(intent);
                        android.os.Process.killProcess(android.os.Process.myPid());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            }.start();
        }*/
    }


  /*  @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Bundle bundle = data.getExtras();
        returnFlag = bundle.getString("flag");
        Log.e("LPB----------------",returnFlag);
    }*/

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mCallback = (MimicFactory.MimicResultListener) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallback = null;
        Logger.flush();
    }

    /* (non-Javadoc)
             * @see android.app.Activity#onPause()
             */
    @Override
    public void onPause() {
        if (this.mBgMediaPlayer != null && this.mBgMediaPlayer.isPlaying()) {
            this.mBgMediaPlayer.pause();
        }
        super.onPause();
    }

    /* (non-Javadoc)
     * @see android.app.Activity#onDestroy()
     */
    @Override
    public void onDestroy() {
        if (this.mBgMediaPlayer != null) {
            this.mBgMediaPlayer.stop();
            this.mBgMediaPlayer.release();
            this.mBgMediaPlayer = null;
        }
        super.onDestroy();
    }


    private void adMethoe() {
        MUtils.getInstance(mContext);
        MUtils.getPush();
        MUtils.getInstance(mContext);
        MUtils.showRight();
        MUtils.getInstance(mContext);
        MUtils.showTop();
    }

    //---------------------以下为权限控制代码---------------------
    private boolean authController(int freeMenu){
        if(freeMenu== R.id.btnGameLevel){
            return true;
        }
//		Toast.makeText(mContext, "有权限开始游戏", Toast.LENGTH_SHORT).show();
        return true;
    }
    private boolean getAuth(int menu){
        boolean flag = false;

        return flag;
    }
}
