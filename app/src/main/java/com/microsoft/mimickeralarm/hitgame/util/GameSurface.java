package com.microsoft.mimickeralarm.hitgame.util;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import com.microsoft.mimickeralarm.hitgame.GameView;
import com.microsoft.mimickeralarm.hitgame.common.Const;
import com.microsoft.mimickeralarm.mimics.MimicHitMouseFragment;

/**
 * Created by mouse on 2017/3/13 0013.
 */
public class GameSurface extends SurfaceView implements View.OnLayoutChangeListener, SurfaceHolder.Callback {
    private MimicHitMouseFragment mParentFragment;
    private RenderThread renderThread;
    private SurfaceHolder holder;

    private GameView gameView;

    public GameView getGameView() {
        return gameView;
    }

    public GameSurface(MimicHitMouseFragment parent) {
        super(parent.getContext());
        mParentFragment = parent;
        holder = getHolder();
        holder.addCallback(this);
        renderThread = new RenderThread();
//        gameView.running = true;
        gameView = new GameView(mParentFragment);


    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case KeyEvent.KEYCODE_BACK:
                mParentFragment.getActivity().finish();
                return true;
            case MotionEvent.ACTION_DOWN://按下时
                gameView.touchX = (int) event.getX();
                gameView.touchY = (int) event.getY();
                gameView.hitting = true;
                gameView.isHit();
                break;
            case MotionEvent.ACTION_MOVE://移动时
//			hitting = false;
                break;
            case MotionEvent.ACTION_UP://抬起时
//			hitting = false;
                break;
        }
        return super.onTouchEvent(event);

    }


    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        //gameView.running = true;// 控制绘制的开关
        gameView.initBitmap();
        gameView.initGameInfo();
        gameView.playBackMusic();
        renderThread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        gameView.running = false;
    }

    @Override
    public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {

    }

    /**
     * 控制出地鼠的时机
     */
    private class RenderThread extends Thread {
        @Override
        public void run() {
            //不停绘制界面
            while (gameView.running) {
                drawUI();
                try {
                    Thread.sleep(gameView.sleepTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (!mParentFragment.isPause) {
                    postInvalidate();
                    gameView.count++;
                    if (gameView.count >= gameView.showDishuTime / gameView.sleepTime) {//60*50=3000毫秒;即每秒随机出一个地鼠
                        gameView.genDishu();
                        gameView.count = 0;
                    }
                    gameView.timeCount++;
                    if (gameView.timeCount * gameView.sleepTime == 800) {
                        gameView.timeCount = 0;
                        gameView.timeNum--;
                    }
                }
            }

            super.run();
        }
    }

    public void drawUI() {
        Canvas canvas = holder.lockCanvas();
        try {
            drawCanvas(canvas);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            holder.unlockCanvasAndPost(canvas);
        }
    }

    private void drawCanvas(Canvas canvas) {
        //在canvas上绘制需要的图形
        Bitmap bm = BitmapUtil.getInstansce(getContext()).getImgByResId(Const.backgroundImgResid);
        DisplayMetrics metric = new DisplayMetrics();
        mParentFragment.getActivity().getWindowManager().getDefaultDisplay().getMetrics(metric);
        int width = metric.widthPixels;     // 屏幕宽度（像素）
        int height = metric.heightPixels;   // 屏幕高度（像素）
        Bitmap bc = bm.createBitmap(gameView.imgBackground, 0, 0, width, height);
        canvas.drawBitmap(bc, 0, 0, null);
        gameView.topMenu(canvas);
        gameView.drawDisong(canvas);
        gameView.playAnima(canvas);
        gameView.playAnima(canvas);
        gameView.drawLinkHitInfo(canvas);
        gameView.drawDisong(canvas);
        gameView.playAnima(canvas);
    }


}
