/**
 * Copyright (C) 2013 Guangzhou JHComn Technologies Ltd.
 * <p/>
 * 本代码版权归源码发布者所有，且受到相关的法律保护。
 * 没有经过版权所有者的书面同意，
 * 任何其他个人或组织均不得以任何形式将本文件或本文件的部分代码用于其他商业用途。
 */
package com.microsoft.mimickeralarm.hitgame.anima;
/**
 * Copyright (C) 2013 Guangzhou JHComn Technologies Ltd.
 *
 * 本代码版权归源码发布者所有，且受到相关的法律保护。
 * 没有经过版权所有者的书面同意，
 * 任何其他个人或组织均不得以任何形式将本文件或本文件的部分代码用于其他商业用途。

 */

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

/**
 * <p>
 * </p>
 *
 * @author ljmat
 * @date 2013-1-4 下午2:16:42
 *
 */
public class UpDownAnima {

    private Bitmap animaImg;//图片
    private int animaImgHeight;
    private int animaImgWidth;
    private long animaTime;//时间
    private int animaFrame;//帧数
    /**上下标识:true-up;false-down*/
    private boolean upDownFlag;//上下标识:true-up;false-down
    private Bitmap drawImg;
    private Bitmap imgDidong;
    private int curFrame;
    private int drawX;
    private int drawY;
    private long preDrawTime;
    private boolean isHit;
    private Bitmap hitImg;
    private Rect hitRect;
    private boolean hitable;
    private int row;
    private int col;
    private boolean addFlag;
    private int score;

    /**
     *
     * @param img 地鼠图片
     * @param time 动画间隔时间
     * @param frame 动画帧数
     * @param drawx 动画播放起点X坐标
     * @param drawy 动画播放起点Y坐标
     * @param addFlag 打中后加减分标识：true:加分；false:减分
     */
    public UpDownAnima(Bitmap img, Bitmap hitImg, Bitmap imgDidong, long time, int frame, int drawx, int drawy, boolean addFlag) {
        this.animaImg = img;
        this.animaTime = time;
        this.animaFrame = frame;
        /*this.upDownFlag = flag;*/
        this.upDownFlag = true;
        this.isHit = false;
        this.animaImgHeight = img.getHeight();
        this.animaImgWidth = img.getWidth();
        this.curFrame = 1;
        this.drawX = drawx;
        this.drawY = drawy;
        this.hitImg = hitImg;
        this.hitRect = new Rect(this.drawX - 20, this.drawY - 20, this.drawX + this.animaImgWidth + 20, this.drawY + this.animaImgHeight + 20);
        this.hitable = true;
        this.imgDidong = imgDidong;
        this.addFlag = addFlag;
        this.score = this.addFlag ? 10 : -10;
    }

    /**
     * 绘制动画中的其中一帧
     *
     * @param //Canvas
     * @param //paint
     * @param //x
     * @param //y
     * @param //frameID
     */
    public void drawFrame(Canvas canvas, Paint paint) {
        if (hitable) {
            if (preDrawTime == 0 || (System.currentTimeMillis() - preDrawTime) >= animaTime) {
                if (upDownFlag) {
                    curFrame++;
                    upDownFlag = curFrame == animaFrame ? false : true;
                } else {
                    curFrame--;
                    upDownFlag = curFrame == -1 ? true : false;
                }
                preDrawTime = System.currentTimeMillis();
            }
        }
        int hightNum = animaImgHeight / animaFrame * curFrame >= animaImgHeight ? animaImgHeight - 10 : (animaImgHeight / animaFrame * curFrame) <= 0 ? 2 : (animaImgHeight / animaFrame * curFrame);
		/*if(Const.gameMode!=Const.gameMode_Level){
			canvas.drawBitmap(imgDidong, drawX, drawY, null);
		}*/
        System.out.println("============ddw>" + imgDidong.getWidth());
        System.out.println("============ddh>" + imgDidong.getHeight());
        if (upDownFlag) {
            drawImg = Bitmap.createBitmap(animaImg, 0, 0, animaImgWidth, hightNum);
            canvas.drawBitmap(drawImg, drawX + (imgDidong.getWidth() - drawImg.getWidth()) / 2, drawY + imgDidong.getHeight() / 2 - hightNum, paint);
        } else {
            drawImg = Bitmap.createBitmap(animaImg, 0, 0, animaImgWidth, hightNum);
            canvas.drawBitmap(drawImg, drawX + (imgDidong.getWidth() - drawImg.getWidth()) / 2, drawY + imgDidong.getHeight() / 2 - hightNum, paint);
            if (hightNum == 2) {
                hitable = false;
            }
        }
        if (isHit) {
            canvas.drawBitmap(hitImg, drawX, drawY - hightNum, paint);
        }
    }

    public boolean isHit(int touchX, int touchY) {
        if (hitRect.contains(touchX, touchY)) {
            isHit = true;
        } else {
            isHit = false;
        }
        return isHit;
    }

    /**
     * @return the drawImg
     */
    public Bitmap getDrawImg() {
        int hightNum = animaImg.getHeight() / animaFrame;
        drawImg = Bitmap.createBitmap(animaImg, 0, 0, animaImgWidth, hightNum);
        return drawImg;
    }

    public int getCurFrame() {
        return curFrame;
    }

    public void setCurFrame(int curFrame) {
        this.curFrame = curFrame;
    }

    public boolean isUpDownFlag() {
        return upDownFlag;
    }

    public void setUpDownFlag(boolean upDownFlag) {
        this.upDownFlag = upDownFlag;
    }

    public long getAnimaTime() {
        return animaTime;
    }

    public void setAnimaTime(long animaTime) {
        this.animaTime = animaTime;
    }

    public int getDrawX() {
        return drawX;
    }

    public void setDrawX(int drawX) {
        this.drawX = drawX;
    }

    public int getDrawY() {
        return drawY;
    }

    public void setDrawY(int drawY) {
        this.drawY = drawY;
    }

    public boolean isHit() {
        return isHit;
    }

    public void setHit(boolean isHit) {
        this.isHit = isHit;
    }

    public Rect getHitRect() {
        return hitRect;
    }

    public boolean isHitable() {
        return hitable;
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getCol() {
        return col;
    }

    public void setCol(int col) {
        this.col = col;
    }

    public boolean isAddFlag() {
        return addFlag;
    }

    public void setAddFlag(boolean addFlag) {
        this.addFlag = addFlag;
    }

    public int getScore() {
        return score;
    }
}
