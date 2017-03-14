/**
 * Copyright (C) 2012 Guangzhou JHComn Technologies Ltd.
 *
 * 本代码版权归源码发布者所有，且受到相关的法律保护。
 * 没有经过版权所有者的书面同意，
 * 任何其他个人或组织均不得以任何形式将本文件或本文件的部分代码用于其他商业用途。

 */
package com.microsoft.mimickeralarm.hitgame.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

import com.microsoft.mimickeralarm.hitgame.common.Const;

/**
 * <p>
 * </p>
 *
 * @author ljmat
 * @date 2012-11-4 下午1:45:45
 *
 */
@SuppressLint("ParserError")
public class GameUtil {

	private static GameUtil gameUtil=null;
	private static Context mContext;
	private static int[][] mapArray = null;
	private static int RESID = 0;
	private static int num = 10;
	
	private GameUtil(Context context){mContext = context;}
	
	public static GameUtil getInstansce(Context context){
		if(null == gameUtil){
			gameUtil = new GameUtil(context);
		}
		return gameUtil;
	}
	
	private static int[][] getMapArray(int resid, int rowSize, int colSize){
		String str = "";
		Log.i("打LogCat--》",resid+""+"===="+rowSize+"---"+colSize);
		if(RESID==0 || RESID!=resid){
			RESID = resid;
			str = mContext.getResources().getString(resid);
			mapArray = getMapArrayByStr(str, rowSize, colSize);
		}
		return mapArray;
	}
	
	/**
	 * 根据游戏关数加载地图数组
	 * @param level
	 * @return
	 */
	public int[][] loadMapArrayByLevel(int level, int strResid, int rowSize, int colSize){
		return getMapArray(strResid, rowSize, colSize);
	}
	
	/**
	 * 根据字符串生成地图数组
	 * @param str
	 * @return
	 */
	private static int[][] getMapArrayByStr(String str, int rowSize, int colSize){
		mapArray = new int[rowSize][colSize];
		str = str.replace(" ", "");
		String[] arr = str.split(",");
		int temp = 0;
		int max = 0;
		for(int row=0; row<rowSize; row++){
			for(int col=0; col<colSize; col++){
				temp = row*colSize + col;
				mapArray[row][col] = Short.valueOf(arr[temp]);
				max = mapArray[row][col]>max?mapArray[row][col]:max;
			}
		}
		Const.randomMax = max;
		return mapArray;
	}

	public int getDishuNumByLevel(int level){
		int num = 10;
		/*if(level<=5){
			num = level*10;
		}else if(level<=10){
			num = level*10+5;
		}else if(level<=15){
			num = level*10+10;
		}else if(level<=20){
			num = level*15+15;
		}else {
			num = level*20;
		}*/
		return num;
	}
}
