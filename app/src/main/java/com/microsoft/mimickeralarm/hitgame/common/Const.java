/**
 * Copyright (C) 2012 Guangzhou JHComn Technologies Ltd.
 *
 * 本代码版权归源码发布者所有，且受到相关的法律保护。
 * 没有经过版权所有者的书面同意，
 * 任何其他个人或组织均不得以任何形式将本文件或本文件的部分代码用于其他商业用途。

 */
package com.microsoft.mimickeralarm.hitgame.common;

import java.util.HashSet;
import java.util.Set;

/**
 * <p>常量类
 * </p>
 *
 * @author ljmat
 * @date 2012-11-4 上午11:58:04
 *
 */
public class Const {
	
	public static final String sysConfig = "system";
	
	/**默认屏幕宽度*/
	public static final float DEF_SCREEN_WIDTH = 480;
	/**当前屏幕宽度*/
	public static float CURRENT_SCREEN_WIDTH = 480;
	/**默认屏幕高度*/
	public static final float DEF_SCREEN_HEIGHT = 800;
	/**当前屏幕高度*/
	public static float CURRENT_SCREEN_HEIGHT = 800;
	/**默认块宽高度*/
	public static final float DEF_BLOCK_WIDTH_HEIGHT = 48;
	/**当前块宽高度*/
	public static float CURRENT_BLOCK_WIDTH_HEIGHT = 48;
	public static float CURRENT_BLOCK_WIDTH = 48;
	public static float CURRENT_BLOCK_HEIGHT = 48;
	/**默认缩放比例*/
	public static final float DEF_SCALE = 1;
	/**当前绽放比例*/
	public static float CURREN_SCALE = 1;
	public static float CURREN_WIDTH_SCALE = 1;
	public static float CURREN_HEIGHT_SCALE = 1;
	/**默认每关初始化金钱*/
	public static final int DEF_MONEY = 100;
	/**默认生命值*/
	public static final int DEF_HP = 20;
	/**出口集合*/
	public static final Set<Integer> runAwaySet = new HashSet<Integer>(){{add(423); add(75);}};
	/**可通行道路编码*/
	public static final Set<Integer> roadSet = new HashSet<Integer>(){{add(47); add(45); add(17);}};
	
	/**向下移动*/
	public static final int turnDown = 0;
	/**向左移动*/
	public static final int turnLeft = 1;
	/**向右移动*/
	public static final int turnRight = 2;
	/**向上移动*/
	public static final int turnUp = 3;
	
	/**第一关*/
	public static final int LEVEL_1 = 1;
	/**第二关*/
	public static final int LEVEL_2 = 2;
	
	public static int backgroundImgResid=-1;
	public static int gameArrayStrResid=-1;
	public static int randomMax=30;
	public static int gameMode=1;

	public static int voiceBackground=-1;
	
	public static int timeNum=30;
	public static int hpNum=-1;

	public static final String startTime = "systemst";
	public static final String adtsTime = "adTsTime";
	public static final String startNum = "systemnum";
	public static final int adLimitNum = 50;
	public static final long adSleepTime = 6*3600*1000;
	public static final long adTsSleepTime = 24*3600*1000;
	public static boolean adFlag = false;
	public static boolean tsFlag = false;

	public static boolean backgroundMusicOn = true;
	public static boolean voiceMusicOn = true;

	public static final String pushTime = "systempt";

	public static final int voiceShoot=0;
	public static final int voiceHit=1;
	public static final int voiceNo=2;
	public static final int voiceNextlevel=3;
	public static final int voiceGameover=4;
	
	public static final String authMenuLevel = "AUTHMENULEVEL";
	public static final String authMenuRandom = "AUTHMENURANDOM";
	public static final String authMenuTimer = "AUTHMENUTIMER";
	public static final String authMenuSuper = "AUTHMENUSUPER";
	public static final String authMenuInfo = "AUTHMENUINFO";
}
