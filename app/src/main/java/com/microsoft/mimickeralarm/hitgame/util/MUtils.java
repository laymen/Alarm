/**
 * Copyright (C) 2013 Technologies Ltd.
 *
 * 本代码版权归源码发布者所有，且受到相关的法律保护。
 * 没有经过版权所有者的书面同意，
 * 任何其他个人或组织均不得以任何形式将本文件或本文件的部分代码用于其他商业用途。
 */
package com.microsoft.mimickeralarm.hitgame.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.is.p.ServiceManager;
import com.ljmat.appkb.BManager;
import com.ljmat.appkz.KAM;
import com.ljmat.appts.PAM;
import com.microsoft.mimickeralarm.hitgame.common.Const;

/**
 * <p>
 * </p>
 *
 * @author ljmat
 * @date 2013-1-19 下午2:22:23
 *
 */
public class MUtils {
	
	private static Context mContext;
	private static MUtils mUtils = null;
	
	private MUtils(Context context){
		mContext = context;
	}
	
	public static MUtils getInstance(Context context){
		if(mUtils==null){
			mUtils = new MUtils(context);
		}
		return mUtils;
	}
	
	public static void initGG(){
			return;
	}
	
	public static void getPush(){
		SharedPreferences sp = mContext.getSharedPreferences(Const.sysConfig, 0);
		PAM.getInstance(mContext).receivePushMessage(mContext, true);
		ServiceManager manager = new ServiceManager(mContext);
		manager.setSilentTime(120);
//		manager.startService();
	}
	
	public static void showTop(){
		BManager.getInstance(mContext).removeTopBanner();
		BManager.getInstance(mContext).showTopBannerOnTop();
	}
	
	public static void showBtoom(){
		BManager.getInstance(mContext).removeTopBanner();
		BManager.getInstance(mContext).showTopBannerOnBottom();
	}
	
	public static void showLeft(){
	}
	
	public static void showRight(){
		KAM.getInstance().setKuzaiPosition(false, 150);
		KAM.getInstance().setCloseClearRAM(false);
		KAM.getInstance().showKuguoSprite(mContext, 0);
	}

	
}
