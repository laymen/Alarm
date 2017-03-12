/**
 * Copyright (C) 2012 Guangzhou JHComn Technologies Ltd.
 *
 * 本代码版权归源码发布者所有，且受到相关的法律保护。
 * 没有经过版权所有者的书面同意，
 * 任何其他个人或组织均不得以任何形式将本文件或本文件的部分代码用于其他商业用途。

 */
package com.microsoft.mimickeralarm.hitgame.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;

import com.microsoft.mimickeralarm.hitgame.common.Const;

/**
 * <p>图片处理工具类
 * </p>
 *
 * @author ljmat
 * @date 2012-11-4 下午12:20:34
 *
 */
public class BitmapUtil {
	
	private Context mContext;
	private static BitmapUtil bitmapUtil=null;

	private BitmapUtil(Context context){
		this.mContext = context;
	}
	
	public static BitmapUtil getInstansce(Context context){
		if(null==bitmapUtil){
			bitmapUtil = new BitmapUtil(context);
		}
		return bitmapUtil;
	}
	
	/**
	 * 根据资源ID取图片
	 * @param resid
	 * @return
	 */
	public Bitmap getImgByResId(int resid){
		Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(), resid);
		if(Const.DEF_SCALE!= Const.CURREN_SCALE){
			bitmap = resizeBitmap(bitmap, Const.CURREN_SCALE);
		}
		return bitmap;
	}
	
	/**
	 * 缩放图片
	 * @param bitmap 要缩放的图片
	 * @param scale 要绽放的比例
	 * @return
	 */
	private Bitmap resizeBitmap(Bitmap bitmap, float scale){
		/*
		 * Bitmap BitmapOrg = bitmap;
		int width = BitmapOrg.getWidth();
		int height = BitmapOrg.getHeight();
		int newWidth = w;
		int newHeight = h;
		float scaleWidth = ((float) newWidth) / width;
		float scaleHeight = ((float) newHeight) / height;
		Matrix matrix = new Matrix();
		matrix.postScale(scaleWidth, scaleHeight);
		Bitmap resizedBitmap = Bitmap.createBitmap(BitmapOrg, 0, 0, width, height, matrix, true);
		return resizedBitmap;
		 */
		
		//如果要缩放的比例与默认缩放比例不同则进行图片缩放
		if(Const.DEF_SCALE!=scale){
//			int newWidth = (int) (bitmap.getWidth()*scale);//缩放后图片的宽度
//			int newHeight = (int) (bitmap.getHeight()*scale);//缩放后图片的高度
			int sImgWidth = bitmap.getWidth();
			int sImgHeight = bitmap.getHeight();
			Matrix matrix = new Matrix();
			matrix.postScale(Const.CURREN_WIDTH_SCALE, Const.CURREN_HEIGHT_SCALE);
			bitmap = Bitmap.createBitmap(bitmap, 0, 0, sImgWidth, sImgHeight, matrix, true);
//			bitmap = Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true);
		}
		
		return bitmap;
	}
	
	/**
	 * 根据游戏关数加载游戏地图
	 * @param level
	 * @return
	 */
	public Bitmap loadGameMapByLevel(int level, int resid){
		switch (level) {
		case Const.LEVEL_1:
			return getImgByResId(resid);
		case Const.LEVEL_2:
			return getImgByResId(resid);
		default:
			return getImgByResId(resid);
		}
	}
}
