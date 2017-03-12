/**
 * Copyright (C) 2013 Technologies Ltd.
 *
 * 本代码版权归源码发布者所有，且受到相关的法律保护。
 * 没有经过版权所有者的书面同意，
 * 任何其他个人或组织均不得以任何形式将本文件或本文件的部分代码用于其他商业用途。
 */
package com.microsoft.mimickeralarm.hitgame.anima;

import android.graphics.Bitmap;
import android.graphics.Canvas;

/**
 * <p>
 * </p>
 *
 * @author ljmat
 * @date 2013-1-5 下午5:16:54
 *
 */
public class HammerAnima {

	private int drawX,drawY;
	private Bitmap imgHammer;
	private long animaTime;
	private long preDrawTime;
	private Bitmap imgHit;
	private int temp;
	
	public HammerAnima(Bitmap imgHammer, Bitmap imgHit, int drawx, int drawy){
		this.drawX = drawx;
		this.drawY = drawy;
		this.imgHammer = imgHammer;
		this.imgHit = imgHit;
		this.preDrawTime = 0;
		this.animaTime = 50;
	}
	
	public void drawHammer(Canvas canvas, int touchX, int touchY){
		//canvas.drawBitmap(imgHammer, touchX-imgHammer.getWidth()/5, touchY-imgHammer.getHeight()/5, null);
		/*if(preDrawTime==0 || (System.currentTimeMillis()-preDrawTime)>animaTime){
		}*/
	}
	
	public int getTemp() {
		return temp;
	}

	public void setTemp(int temp) {
		this.temp = temp;
	}
}
