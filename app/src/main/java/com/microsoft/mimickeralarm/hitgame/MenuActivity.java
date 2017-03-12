/**
 * Copyright (C) 2013 Technologies Ltd.
 *
 * 本代码版权归源码发布者所有，且受到相关的法律保护。
 * 没有经过版权所有者的书面同意，
 * 任何其他个人或组织均不得以任何形式将本文件或本文件的部分代码用于其他商业用途。
 */
package com.microsoft.mimickeralarm.hitgame;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.microsoft.mimickeralarm.R;
import com.microsoft.mimickeralarm.hitgame.common.Const;
import com.microsoft.mimickeralarm.hitgame.util.MUtils;

/**
 * <p>
 * </p>
 *
 * @author ljmat
 * @date 2013-1-10 上午1:43:44
 *
 */
public class MenuActivity extends Activity {

	private Context mContext;
	private MediaPlayer mBgMediaPlayer;
	private boolean flag=true;
	private Button btnLevel;
	Intent intent;
	Bundle bundle;
	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);//无标题
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);//全屏
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);   //应用运行时，保持屏幕高亮，不锁屏

		init();

		setContentView(R.layout.fragment_hit_game);
		 btnLevel = (Button) findViewById(R.id.btnGameLevel);
		 intent = new Intent(mContext, MainActivity.class);
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
	}

	/**
	 * 屏幕初始化
	 */
	private void init() {
		mContext = MenuActivity.this;
		mBgMediaPlayer = MediaPlayer.create(mContext, R.raw.background);
		mBgMediaPlayer.setLooping(true);//循环
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
	}
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onResume()
	 */
	@Override
	protected void onResume() {
		adMethoe();
		if(Const.backgroundMusicOn && mBgMediaPlayer!=null && !mBgMediaPlayer.isPlaying()){
			mBgMediaPlayer.start();
		}
		super.onResume();
		if (!btnLevel.isPressed()) {
			new Thread(){
				@Override
				public void run() {
					try {
						Thread.sleep(3000);
						startActivity(intent);
						android.os.Process.killProcess(android.os.Process.myPid());
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

			}.start();

		}
	}
	/* (non-Javadoc)
	 * @see android.app.Activity#onPause()
	 */
	@Override
	protected void onPause() {
		if (this.mBgMediaPlayer != null && this.mBgMediaPlayer.isPlaying()) {
			this.mBgMediaPlayer.pause();
        }
		super.onPause();
	}
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onDestroy()
	 */
	@Override
	protected void onDestroy() {
		if (this.mBgMediaPlayer != null) {
			this.mBgMediaPlayer.stop();
			this.mBgMediaPlayer.release();
			this.mBgMediaPlayer = null;
        }
		super.onDestroy();
	}
	
	/**
	 * 
	 */
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
