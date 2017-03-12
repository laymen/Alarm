package com.microsoft.mimickeralarm.hitgame;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.microsoft.mimickeralarm.R;
import com.microsoft.mimickeralarm.hitgame.common.Const;
import com.microsoft.mimickeralarm.hitgame.util.MUtils;

@SuppressLint("ResourceAsColor")
public class MainActivity extends Activity{

	private MediaPlayer mBgMediaPlayer;
	private boolean isMusic = true;
	private Context mContext;
	private static SoundPool mSoundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
	/**0:打;1:打中;2死机*/
	private int[] soundIds = {-1,-1,-1,-1,-1};
	public boolean isPause;
	public GameView gameView;
	public static int count = 0;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);//无标题
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);//全屏
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);   //应用运行时，保持屏幕高亮，不锁屏
		init();
		gameView = new GameView(MainActivity.this);
		setContentView(gameView);
	}

	/**
	 *
	 */
	private void init() {
		count++;
		mContext = MainActivity.this;
		initGameMode();
		DisplayMetrics mDisplayMetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(mDisplayMetrics);
		Const.CURRENT_SCREEN_WIDTH = mDisplayMetrics.widthPixels;
		Const.CURRENT_SCREEN_HEIGHT = mDisplayMetrics.heightPixels;
		Const.CURREN_SCALE = Const.CURRENT_SCREEN_WIDTH/ Const.DEF_SCREEN_WIDTH;
		Const.CURREN_WIDTH_SCALE = Const.CURRENT_SCREEN_WIDTH/ Const.DEF_SCREEN_WIDTH;
		Const.CURREN_HEIGHT_SCALE = Const.CURRENT_SCREEN_HEIGHT/ Const.DEF_SCREEN_HEIGHT;
		Const.CURRENT_BLOCK_WIDTH_HEIGHT = Const.CURREN_SCALE* Const.DEF_BLOCK_WIDTH_HEIGHT;
		Const.CURRENT_BLOCK_WIDTH = Const.CURREN_WIDTH_SCALE* Const.DEF_BLOCK_WIDTH_HEIGHT;
		Const.CURRENT_BLOCK_HEIGHT = Const.CURREN_HEIGHT_SCALE* Const.DEF_BLOCK_WIDTH_HEIGHT;

		mBgMediaPlayer = MediaPlayer.create(mContext, Const.voiceBackground);
		mBgMediaPlayer.setLooping(true);//循环
		soundIds[Const.voiceShoot] = mSoundPool.load(mContext, R.raw.shoot, 1);
		soundIds[Const.voiceHit] = mSoundPool.load(mContext, R.raw.hit, 1);
		soundIds[Const.voiceNo] = mSoundPool.load(mContext, R.raw.no, 1);
		soundIds[Const.voiceNextlevel] = mSoundPool.load(mContext, R.raw.nextlevel, 1);
		soundIds[Const.voiceGameover] = mSoundPool.load(mContext, R.raw.gameover, 1);

		adMethod();
	}


	/**
	 *
	 */
	private void adMethod() {
		MUtils.getInstance(mContext);
		MUtils.showRight();
		MUtils.getInstance(mContext);
		MUtils.showBtoom();
	}

	/* (non-Javadoc)
     * @see android.app.Activity#onKeyDown(int, android.view.KeyEvent)
     */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (event.getKeyCode()) {
			case KeyEvent.KEYCODE_BACK:
				finish();
				return true;
		}
		return false;
	}



	public void gameOver(){
		playVoice(Const.voiceGameover);
		ImageView imgView = new ImageView(mContext);
		imgView.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
		imgView.setBackgroundResource(R.drawable.gameover);
		count++;
		if(count < 3){
			Intent intent = getIntent();
			finish();
			startActivity(intent);
		}else {
			gameSuccess();
		}
	}


	public void gameSuccess(){
		playVoice(Const.voiceGameover);
		ImageView imgView = new ImageView(mContext);
		imgView.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
		imgView.setBackgroundResource(R.drawable.gameover);
		/*Intent intent = new Intent();
		intent.setClass(MainActivity.this, AlarmMainActivity.class);
		startActivity(intent);
		System.out.print("------------finish");*/
		/*String flag="ok";
		Intent intent=new Intent();
		Bundle bundle=new Bundle();
		bundle.putString("flag", flag);
		intent.putExtras(bundle);
		// 1是返回的requestCode
		MainActivity.this.setResult(1, intent);
		Log.e("LPB------------",flag);*/
		finish();
	}

	public void initGameMode(){

		Const.backgroundImgResid = R.drawable.mapds48_001;
		Const.gameArrayStrResid = R.string.didong_level;
		Const.voiceBackground = R.raw.level;
	}

	/**播放背景音乐*/
	public void playBackgroundMusic(){
		if(Const.backgroundMusicOn && mBgMediaPlayer!=null){
			mBgMediaPlayer.start();
		}else{
			if(mBgMediaPlayer.isPlaying()){
				mBgMediaPlayer.pause();
			}
		}
	}

	/**播放指定音效*/
	public void playVoice(int idx){
		if(Const.voiceMusicOn && soundIds[idx]!=-1){
			mSoundPool.play(soundIds[idx], 1.0f, 1.0f, 1, 0, 1.0f);
		}
	}

	/* (non-Javadoc)
     * @see android.app.Activity#onDestroy()
     */
	@Override
	protected void onDestroy() {
		if (this.mBgMediaPlayer != null) {
			if (this.isMusic){
				this.mBgMediaPlayer.stop();
				this.mBgMediaPlayer.release();
				this.mBgMediaPlayer = null;
			}
		}
		super.onDestroy();
	}

}
