package com.microsoft.mimickeralarm.mimics;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.microsoft.mimickeralarm.R;
import com.microsoft.mimickeralarm.hitgame.common.Const;
import com.microsoft.mimickeralarm.hitgame.util.GameSurface;
import com.microsoft.mimickeralarm.hitgame.util.MUtils;
import com.microsoft.mimickeralarm.mimics.MimicFactory.MimicResultListener;

/**
 * Created by mouse on 2017/3/12 0012.
 */
public class MimicHitMouseFragment extends Fragment {
    public static final String NO_NETWORK_FRAGMENT_TAG = "mimic_hit_mouse";
    MimicResultListener mCallback; //接口的提交出去
    private Context mContext;
    /**
     * 0:打;1:打中;2死机
     */
    private int[] soundIds = {-1, -1, -1, -1, -1};
    private MediaPlayer mBgMediaPlayer;
    private static SoundPool mSoundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
    private boolean isMusic = true;
    public boolean isPause;
    GameSurface game;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_hit_game, container, false);
        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);//全屏
        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);   //应用运行时，保持屏幕高亮，不锁屏
        init();
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, 1);
        game=new GameSurface(this);
        game.setLayoutParams(params);

        ((LinearLayout) view.findViewById(R.id.game_container)).addView(game);

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


    /**
     * 屏幕初始化
     */
    private void init() {
        mContext = getActivity().getApplication();
        initGameMode();
        DisplayMetrics mDisplayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(mDisplayMetrics);
        Const.CURRENT_SCREEN_WIDTH = mDisplayMetrics.widthPixels;
        Const.CURRENT_SCREEN_HEIGHT = mDisplayMetrics.heightPixels;
        Const.CURREN_SCALE = Const.CURRENT_SCREEN_WIDTH / Const.DEF_SCREEN_WIDTH;
        Const.CURREN_WIDTH_SCALE = Const.CURRENT_SCREEN_WIDTH / Const.DEF_SCREEN_WIDTH;
        Const.CURREN_HEIGHT_SCALE = Const.CURRENT_SCREEN_HEIGHT / Const.DEF_SCREEN_HEIGHT;
        Const.CURRENT_BLOCK_WIDTH_HEIGHT = Const.CURREN_SCALE * Const.DEF_BLOCK_WIDTH_HEIGHT;
        Const.CURRENT_BLOCK_WIDTH = Const.CURREN_WIDTH_SCALE * Const.DEF_BLOCK_WIDTH_HEIGHT;
        Const.CURRENT_BLOCK_HEIGHT = Const.CURREN_HEIGHT_SCALE * Const.DEF_BLOCK_WIDTH_HEIGHT;

        mBgMediaPlayer = MediaPlayer.create(mContext, Const.voiceBackground);
        mBgMediaPlayer.setLooping(true);//循环
        soundIds[Const.voiceShoot] = mSoundPool.load(mContext, R.raw.shoot, 1);
        soundIds[Const.voiceHit] = mSoundPool.load(mContext, R.raw.hit, 1);
        soundIds[Const.voiceNo] = mSoundPool.load(mContext, R.raw.no, 1);
        soundIds[Const.voiceNextlevel] = mSoundPool.load(mContext, R.raw.nextlevel, 1);
        soundIds[Const.voiceGameover] = mSoundPool.load(mContext, R.raw.gameover, 1);

        adMethod();
    }

    public void initGameMode() {

        Const.backgroundImgResid = R.drawable.mapds48_001;
        Const.gameArrayStrResid = R.string.didong_level;
        Const.voiceBackground = R.raw.level;
    }

    private void adMethod() {
        MUtils.getInstance(mContext);
        MUtils.showRight();
        MUtils.getInstance(mContext);
        MUtils.showBtoom();
    }

    public void gameOver() {
        playVoice(Const.voiceGameover);
        ImageView imgView = new ImageView(mContext);
        imgView.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
        imgView.setBackgroundResource(R.drawable.gameover);
        if (game.getGameView().killNum<10) {//我定的游戏需要打中10只老鼠
            mCallback.onMimicFailure();//同下
        } else {
            gameSuccess();

        }
    }

    public void gameSuccess() {
        playVoice(Const.voiceGameover);
        ImageView imgView = new ImageView(mContext);
        imgView.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
        imgView.setBackgroundResource(R.drawable.gameover);
        mCallback.onMimicSuccess(null);//给外面去实现
    }

    /**
     * 播放指定音效
     */
    public void playVoice(int idx) {
        if (Const.voiceMusicOn && soundIds[idx] != -1) {
            mSoundPool.play(soundIds[idx], 1.0f, 1.0f, 1, 0, 1.0f);
        }
    }

    /**
     * 播放背景音乐
     */
    public void playBackgroundMusic() {
        if (Const.backgroundMusicOn && mBgMediaPlayer != null) {
            mBgMediaPlayer.start();
        } else {
            if (mBgMediaPlayer.isPlaying()) {
                mBgMediaPlayer.pause();
            }
        }
    }

    @Override
    public void onDestroy() {
        if (this.mBgMediaPlayer != null) {
            if (this.isMusic) {
                this.mBgMediaPlayer.stop();
                this.mBgMediaPlayer.release();
                this.mBgMediaPlayer = null;
            }
        }
        super.onDestroy();
    }


}
