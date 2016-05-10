package com.cl.slack.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * SurfaceView的使用
 * Created by chenling on 2016/5/9.
 * 1.View主要用于自动更新的情况下,而surfaceVicw主要适用于被动更新,例如频繁刷新
 * 2.View在主线程中刷新，而surfaceView通常会通过一 个子线程来进行页面刷新。
 * 3.View在绘制的时候没有双缓冲机制,而surfaceVicw在底层实现机制中就已经实现了双缓冲机制；
 */
public class SurfaView extends SurfaceView implements SurfaceHolder.Callback, Runnable {

    //SurfaceHolder
    private SurfaceHolder mHolder;
    //用于绘制的Canvas
    private Canvas mCanvas;
    //子线程标志位
    private boolean mIsDrawing;
    private float lastX,lastY;
    private Path mPath;
    private Paint mPaint;

    /**
     * 构造方法
     *
     * @param context
     * @param attrs
     */
    public SurfaView(Context context, AttributeSet attrs) {
        super(context, attrs);

        mPath = new Path();
        mPaint = new Paint();
        mPaint.setStrokeWidth(20);
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mHolder = getHolder();
        mHolder.addCallback(this);
        setFocusable(true);
        setFocusableInTouchMode(true);
        setKeepScreenOn(true);
    }
    // 创建
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mIsDrawing = true;
        new Thread(this).start();
    }
    // 改变
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }
    //销毁
    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        mIsDrawing = false;
    }

    @Override
    public void run() {
        while (mIsDrawing) {
            draw();
//            lastX += 1;
//            lastY = (int) (100 * Math.sin(lastX * 2 * Math.PI / 180) + 400);
//            mPath.lineTo(lastX, lastY);
        }
    }

    // 负责绘画，频繁刷新
    private void draw() {
        try {
            mCanvas = mHolder.lockCanvas();
            mCanvas.drawColor(Color.WHITE);
            mCanvas.drawPath(mPath, mPaint);
        } catch (Exception e) {

        } finally {
            if (mCanvas != null) {
                //提交
                mHolder.unlockCanvasAndPost(mCanvas);
            }
        }
    }

    /**
     * 触摸事件
     *
     * @param event
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        lastX =  event.getX();
        lastY =  event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mPath.moveTo(lastX, lastY);
                break;
            case MotionEvent.ACTION_MOVE:
                mPath.lineTo(lastX,lastY);
                break;
            case MotionEvent.ACTION_UP:
                break;
        }

        return true;
    }
}
