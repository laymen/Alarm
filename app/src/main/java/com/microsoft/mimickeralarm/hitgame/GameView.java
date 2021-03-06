/**
 * Copyright (C) 2013
 * <p>
 * 本代码版权归源码发布者所有，且受到相关的法律保护。
 * 没有经过版权所有者的书面同意，
 * 任何其他个人或组织均不得以任何形式将本文件或本文件的部分代码用于其他商业用途。
 */
package com.microsoft.mimickeralarm.hitgame;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.view.View;

import com.microsoft.mimickeralarm.R;
import com.microsoft.mimickeralarm.hitgame.anima.UpDownAnima;
import com.microsoft.mimickeralarm.hitgame.common.Const;
import com.microsoft.mimickeralarm.hitgame.util.BitmapUtil;
import com.microsoft.mimickeralarm.hitgame.util.GameUtil;
import com.microsoft.mimickeralarm.mimics.MimicHitMouseFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * <p>
 * </p>
 *
 * @author ljmat
 * @date 2013-1-4 下午2:48:17
 *
 */
public class GameView extends View {

    public MimicHitMouseFragment mContext;
    public boolean running;
    public UpDownAnima uda;
    public List<UpDownAnima> udaList = new ArrayList<UpDownAnima>();
    public long sleepTime = 50;
    public long showDishuTime = 500;
    public int touchX;
    public int touchY;
    public Bitmap imgBackground;
    public Bitmap imgDishu;
    public Bitmap imgDishu2;
    public Bitmap imgHit;
    public Bitmap imgDidong;
    public int[][] didongArray;
    public int rowSize = 17;
    public int colSize = 10;
    public int count;
    public int dishuCount;
    public int curDishuCount;
    public int curLevel = 0;
    public int runAwayNum = 0;
    public int killNum = 0;
    public int totalDishuNum;
    public Bitmap imgLevel;
    public Bitmap imgNumber;
    public Bitmap imgSmallDishu40;
    public Bitmap imgX;
    public Bitmap imgMuchui;
    public boolean hitting;
    public Lock lock;
    public Bitmap imgMe;
    public int score;
    public boolean linkHit;
    public int linkHitScore = -5;
    public Bitmap imgLinkHit;
    public Bitmap imgTimer;
    public int timeCount;
    public int timeNum;
    public int hpNum = -1;
    public Bitmap imgHp;
    public Bitmap imgMusic;

    /**
     * @param context
     */
    public GameView(MimicHitMouseFragment context) {
        super(context.getContext());
        mContext = context;
        running = true;
        mContext.isPause = false;
        lock = new ReentrantLock();
    }

    /**
     *
     */
    public void playBackMusic() {
        mContext.playBackgroundMusic();
    }

    /**
     *
     */
    public void initGameInfo() {
        lock.lock();
        udaList.clear();
        lock.unlock();
        runAwayNum = 0;
        curLevel = 0;
        score = 0;
        linkHit = false;
        linkHitScore = -5;
        startGame();
        mContext.isPause = false;
    }

    public void gameOver() {
        mContext.isPause = true;
        mContext.gameOver();
    }

    public void gameSuccess() {
        mContext.isPause = true;
        mContext.gameSuccess();
    }

    /**
     * @param canvas
     */
    public void drawLinkHitInfo(Canvas canvas) {
        if (linkHit && linkHitScore >= 5) {
            linkHit = false;
            int marginLeft = touchX + imgLinkHit.getWidth();
            int marginTop = touchY - imgLinkHit.getHeight();
            canvas.drawBitmap(imgLinkHit, marginLeft, marginTop, null);
            marginLeft += imgLinkHit.getWidth();
            canvas.drawBitmap(imgX, marginLeft, marginTop + imgX.getHeight() / 4, null);
            marginLeft += imgX.getWidth();
            drawNum(canvas, linkHitScore / 5, marginLeft, marginTop + 2);
        }
    }

    /**
     * @param canvas
     */
    public void drawDisong(Canvas canvas) {
        for (int row = 0; row < rowSize; row++) {
            for (int col = 0; col < colSize; col++) {
                if (didongArray[row][col] != 0) {
                    canvas.drawBitmap(imgDidong, (int) (col * Const.CURRENT_BLOCK_WIDTH), (int) (row * Const.CURRENT_BLOCK_HEIGHT), null);
                }
            }
        }
    }

    /**
     * @param canvas
     */
    public void topMenu(Canvas canvas) {
        int marginLeft = 5;
        musicInfo(canvas, marginLeft);
        marginLeft = 130;//
        marginLeft = dishuInfo(canvas, 0);
        marginLeft = hpInfo(canvas, 210);
        if (hpNum <= 0) {//生命为0,就失败
            gameOver();
        }

    }

    /**
     * @param canvas
     * @param marginLeft
     */
    private void musicInfo(Canvas canvas, int marginLeft) {
        Bitmap tem = null;
        if (Const.backgroundMusicOn) {
            tem = Bitmap.createBitmap(imgMusic, 0, 0, imgMusic.getWidth() / 2, imgMusic.getHeight());
        } else {
            tem = Bitmap.createBitmap(imgMusic, imgMusic.getWidth() / 2, 0, imgMusic.getWidth() / 2, imgMusic.getHeight());
        }
        canvas.drawBitmap(tem, Const.CURRENT_SCREEN_WIDTH - imgMusic.getWidth() * 2 / 3, tem.getHeight(), null);
    }

    /**
     * 生命值
     * @param canvas
     * @param marginLeft
     * @return
     */
    private int hpInfo(Canvas canvas, int marginLeft) {
        marginLeft +=185;//mouse修改
        Bitmap hp = Bitmap.createBitmap(imgHp, 0, 0, imgHp.getWidth() / 3, imgHp.getHeight());//3
        canvas.drawBitmap(hp, marginLeft, 0, null);//5
        marginLeft += hp.getWidth() + 3;//20
        marginLeft = drawNum(canvas, hpNum, marginLeft, 7);//7
        return marginLeft;
    }

    /**
     * @param canvas
     * 画地鼠
     */
    private int dishuInfo(Canvas canvas, int marginLeft) {
        marginLeft += 10;
        canvas.drawBitmap(imgSmallDishu40, marginLeft, 5, null);
        marginLeft += imgSmallDishu40.getWidth() + 2;
        canvas.drawBitmap(imgX, marginLeft, 5, null);
        marginLeft += imgX.getWidth() + 2;
        marginLeft = drawNum(canvas, totalDishuNum - killNum, marginLeft, 7);
        return marginLeft;
    }

    /**画数字*/
    private int drawNum(Canvas canvas, int num, int marginLeft, int marginTop) {
        String numStr = String.valueOf(num < 0 ? 0 : num);
        Bitmap imgNum;
        for (int i = 0; i < numStr.length(); i++) {
            imgNum = getNumberImg(Integer.valueOf(numStr.substring(i, i + 1)));
            canvas.drawBitmap(imgNum, marginLeft, marginTop, null);
            marginLeft += imgNum.getWidth() + 2;
        }
        return marginLeft;
    }

    /**重要
     * hpNum:生命值
     * udaList：击中地鼠的list集合
     */
    public void playAnima(Canvas canvas) {
        lock.lock();
        for (UpDownAnima uda : udaList) {
            uda.drawFrame(canvas, null);
        }
        List<UpDownAnima> rm = new ArrayList<>();
        for (UpDownAnima uda : udaList) {
            if (uda.isHit()) {
                rm.add(uda);
            } else if (!uda.isHitable()) {
                rm.add(uda);
                if (uda.isAddFlag()) {
                    runAwayNum++;
                    totalDishuNum--;
                    hpNum--;
                }
            }
        }
        udaList.removeAll(rm);
        curDishuCount = udaList.size();
        lock.unlock();
    }

    //开始游戏
    public void startGame() {
        curLevel++;
        lock.lock();
        udaList.clear();
        lock.unlock();
        linkHitScore = -5;
        genDishu();
        killNum = 0;
        hpNum = 2 * curLevel > 10 ? 10 : 2 * curLevel;
        totalDishuNum = GameUtil.getInstansce(getContext()).getDishuNumByLevel(curLevel);
        mContext.isPause = false;
    }

    /**
     *
     */
    public void isHit() {
        if (touchX > Const.CURRENT_SCREEN_WIDTH - imgMusic.getWidth() * 2 / 3 - 5 && touchX < Const.CURRENT_SCREEN_WIDTH - imgMusic.getWidth() / 4 + 5 && touchY > imgMusic.getHeight() - 5 && touchY < imgMusic.getHeight() * 2 + 5) {
            if (Const.backgroundMusicOn) {
                Const.backgroundMusicOn = false;
                Const.voiceMusicOn = false;
            } else {
                Const.backgroundMusicOn = true;
                Const.voiceMusicOn = true;
            }
            mContext.playBackgroundMusic();
            return;
        }
        mContext.playVoice(Const.voiceShoot);
        lock.lock();
        for (UpDownAnima uda : udaList) {
            if (hitting && uda.isHit(touchX, touchY)) {
                hitting = false;
                score += uda.getScore();
                if (uda.isAddFlag()) {
                    linkHit = true;
                    linkHitScore += 5;
                    mContext.playVoice(Const.voiceHit);
                    killNum++;
                    if (linkHitScore / 5 > 65) {
                        score += 10;
                    } else if (linkHitScore / 5 > 45) {
                        score += 8;
                    } else if (linkHitScore / 5 > 30) {
                        score += 6;
                    } else if (linkHitScore / 5 > 15) {
                        score += 4;
                    } else if (linkHitScore / 5 > 5) {
                        score += 2;
                    } else if (linkHitScore > 0) {
                        score += 1;
                    }
                    if ((totalDishuNum - killNum) <= 0) {
                        gameSuccess();
                    }
                } else {
                    mContext.playVoice(Const.voiceNo);
                    linkHit = false;
                    linkHitScore = -5;
                    hpNum--;
                }
            }
        }
        lock.unlock();
    }

    private Bitmap getNumberImg(int num) {
        int numW = imgNumber.getWidth() / 10;
        return Bitmap.createBitmap(imgNumber, num * numW, 0, numW, imgNumber.getHeight());
    }

    private boolean randomShow(int row, int col) {
        double random = Math.random();
        boolean flag = false;
        if (0.9 > random) {
            flag = true;
        }
        lock.lock();
        for (UpDownAnima uda : udaList) {
            if (uda.getRow() == row && uda.getCol() == col) {
                flag = false;
            }
        }
        lock.unlock();
        return flag;
    }


    public void genDishu() {
        didongArray = GameUtil.getInstansce(getContext()).loadMapArrayByLevel(1, Const.gameArrayStrResid, rowSize, colSize);
        int rowSize = didongArray.length;
        int colSize = didongArray[0].length;
        int randomDishu = 0;
        Random random = new Random();
        randomDishu = random.nextInt(Const.randomMax);
        randomDishu = randomDishu == 0 ? 1 : randomDishu;
        Random rand = new Random();
        double randShow = 0.0;
        lock.lock();
        for (int row = 0; row < rowSize; row++) {
            for (int col = 0; col < colSize; col++) {
                if (randomDishu == didongArray[row][col]) {
                    if (randomShow(row, col)) {
                        randShow = rand.nextDouble();
                        if (randShow > 0.6) {
                            uda = new UpDownAnima(imgDishu, imgHit, imgDidong, 200, 8, (int) (col * Const.CURRENT_BLOCK_WIDTH), (int) (row * Const.CURRENT_BLOCK_HEIGHT), true);
                        } else if (randShow > 0.2) {
                            uda = new UpDownAnima(imgDishu2, imgHit, imgDidong, 200, 8, (int) (col * Const.CURRENT_BLOCK_WIDTH), (int) (row * Const.CURRENT_BLOCK_HEIGHT), true);
                        } else {
                            uda = new UpDownAnima(imgMe, imgHit, imgDidong, 200, 8, (int) (col * Const.CURRENT_BLOCK_WIDTH), (int) (row * Const.CURRENT_BLOCK_HEIGHT), false);
                        }
                        uda.setRow(row);
                        uda.setCol(col);
                        dishuCount++;
                        udaList.add(uda);
                    }
                }
            }
        }
        lock.unlock();
    }

    public void initBitmap() {
        imgBackground = BitmapUtil.getInstansce(getContext()).getImgByResId(Const.backgroundImgResid);
        imgDishu = BitmapUtil.getInstansce(getContext()).getImgByResId(R.drawable.dishu_40x60);
        imgDishu2 = BitmapUtil.getInstansce(getContext()).getImgByResId(R.drawable.dishu2_40x60);
        imgHit = BitmapUtil.getInstansce(getContext()).getImgByResId(R.drawable.xuanyun_64);
        imgDidong = BitmapUtil.getInstansce(getContext()).getImgByResId(R.drawable.dd_dc);
        imgLevel = BitmapUtil.getInstansce(getContext()).getImgByResId(R.drawable.level69x35);
        imgNumber = BitmapUtil.getInstansce(getContext()).getImgByResId(R.drawable.num);
        imgSmallDishu40 = BitmapUtil.getInstansce(getContext()).getImgByResId(R.drawable.smalldishu_40);
        imgX = BitmapUtil.getInstansce(getContext()).getImgByResId(R.drawable.x);
        imgMuchui = BitmapUtil.getInstansce(getContext()).getImgByResId(R.drawable.muchui_002);
        imgMe = BitmapUtil.getInstansce(getContext()).getImgByResId(R.drawable.me_48);
        imgLinkHit = BitmapUtil.getInstansce(getContext()).getImgByResId(R.drawable.linkhit_69x35);
        imgTimer = BitmapUtil.getInstansce(getContext()).getImgByResId(R.drawable.timer);
        imgHp = BitmapUtil.getInstansce(getContext()).getImgByResId(R.drawable.hp);
        imgMusic = BitmapUtil.getInstansce(getContext()).getImgByResId(R.drawable.music_96x48);

    }

}
