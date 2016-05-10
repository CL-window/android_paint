package com.cl.slack.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import com.cl.slack.paint2d.R;

/** 自定义view,使用paint画图
 * Created by chenling on 2016/5/9.
 */
public class PaintView extends View {

    private Paint paint;//画笔
    int width;
    int height;
    // 刮刮乐
    private Canvas mCanvas;
    private Bitmap mFgBitmap;
    private Path mPath;
    private Paint mPaint;
    private float lastX,lastY;

    public PaintView(Context context) {
        super(context);
    }

    public PaintView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public PaintView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void init(Context context) {
        //获取屏幕的宽高
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);

        width = wm.getDefaultDisplay().getWidth();
        height = wm.getDefaultDisplay().getHeight();
        Log.i("slack",width+":"+height);

        paint = new Paint();
        //设置实心
        paint.setStyle(Paint.Style.FILL);
        // 设置红色
        paint.setColor(Color.RED);
        // 设置画笔的锯齿效果
        paint.setAntiAlias(true);

        // PorterDuffXfermode 刮刮乐 通过DST_IN.SRC_IN模式来实现将一个矩形变成圆角图片的效果
        mPaint = new Paint();
        mPaint.setAlpha(0);
        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeWidth(50);
        mPaint.setStrokeCap(Paint.Cap.ROUND);

        mPath = new Path();
        // createBitmap (int width, int height, Bitmap.Config config)
        mFgBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mFgBitmap);
        mCanvas.drawColor(Color.GRAY);

    }

    /* 重写draw
    *  Canvas.save() 将之前的图像保存起来，让后续的操作能像在新的画布一样操作
    *  Canvas.restore() 合并图层
    *  Canvas.translate() 平移 我们绘制的时候默认坐标点事左上角的起始点，那么我们调用translate（x,y）之后，
    则将原点（0,0）移动到（x,y）之后的所有绘图都是在这一点上执行的
    *  Canvas.roate() 旋转
    * */
    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);


        //矩形  drawRect (float left, float top, float right, float bottom, Paint paint)
        canvas.drawRect(50, 100, 200, 200, paint);

        //圆  drawCircle (float cx, float cy, float radius, Paint paint)
        canvas.drawCircle(width / 2, height / 2, 100, paint);

        //三角形（画线）
        //实例化路径
        Path path = new Path();
        path.moveTo(80, 300);// 此点为多边形的起点
        path.lineTo(120, 250);
        path.lineTo(80, 250);
        path.close(); // 使这些点构成封闭的多边形
        canvas.drawPath(path, paint);

        //扇形 RectF (float left, float top, float right, float bottom)
        RectF rectF = new RectF(160, 200, 400, 400);
        // drawArc (RectF oval, float startAngle 起始弧度, float sweepAngle 扫过的弧度, boolean useCenter, Paint paint)
        canvas.drawArc(rectF, 200, 130, true, paint);

        //椭圆
        rectF = new RectF(300, 300, 600, 600);
        //  set (float left, float top, float right, float bottom)
        rectF.set(210,100,450,200);
        // drawOval (RectF oval, Paint paint)
        canvas.drawOval(rectF, paint);

        //曲线
        //设置空心
        paint.setStyle(Paint.Style.STROKE);
        path = new Path();
        path.moveTo(500, 500);//设置Path的起点
        path.quadTo(550, 510, 670, 600);  //设置路径点和终点
        canvas.drawPath(path, paint);

        //文字+ 图片
        paint.setTextSize(30);
        //文本 drawText (String text, float x, float y, Paint paint)
        canvas.drawText("slack", 350, 330, paint);
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
        //图片 drawBitmap (Bitmap bitmap, float left, float top, Paint paint)
        canvas.drawBitmap(bitmap, 320, 360, paint);

        canvas.save();

        //画时钟
        // 画外圆
        Paint paintCircle = new Paint();
        paintCircle.setAntiAlias(true);
        paintCircle.setStyle(Paint.Style.STROKE);
        paintCircle.setStrokeWidth(5);
        canvas.drawCircle(width / 2, height / 2, width / 3, paintCircle);
        // 画刻度
        Paint paintDegree = new Paint();
        paintDegree.setStrokeWidth(3);
        canvas.rotate(30, width / 2, height / 2);
        for (int i = 1; i <= 12; i++) {
            // 区别整点和非整点
            if (i == 3 || i == 6 || i == 9 || i == 12 ) {
                paintDegree.setStrokeWidth(5);
                paintDegree.setTextSize(30);
                // drawLine (float startX, float startY, float stopX, float stopY, Paint paint)
                canvas.drawLine(width / 2, height / 2 - width / 3,
                        width / 2, height / 2 - width / 3 + 60, paintDegree);
                String degree = String.valueOf(i);
                canvas.drawText(degree,
                        width / 2 - paintDegree.measureText(degree) / 3,
                        height / 2 - width / 3 + 90, paintDegree);
            } else {
                paintDegree.setStrokeWidth(3);
                paintDegree.setTextSize(15);
                // 圆上的一点（x = 屏幕宽一半, y = 屏幕的一半 - 半径）
                canvas.drawLine(width / 2, height / 2 - width / 3,
                        width / 2, height / 2 - width / 3 + 30, paintDegree);
                String degree = String.valueOf(i);
                canvas.drawText(degree,
                        width / 2 - paintDegree.measureText(degree) / 3,
                        height / 2 - width / 3 + 60, paintDegree);
            }
            // 通过旋转画布——实际上是旋转了画图的坐标轴 简化坐标运算
            canvas.rotate(30, width / 2, height / 2);
        }

        canvas.save();
        // 画指针
        Paint paintHour = new Paint();
        paintHour.setStrokeWidth(15);
        Paint paintMinute = new Paint();
        paintMinute.setStrokeWidth(10);
        //绘制的时候默认坐标点事左上角的起始点，那么我们调用translate（x,y）之后，则将原点（0,0）移动到（x,y）之后的所有绘图都是在这一点上执行的
        canvas.translate(width / 2, height / 2);
        canvas.drawPoint(0, 0, paintHour);//圆点
        canvas.drawLine(0, 0, 100, 100, paintHour);
        canvas.drawLine(0, 0, 100, -200, paintMinute);
        canvas.restore();


        //刮刮乐
        canvas.drawBitmap(mFgBitmap, 0, 0, null);
    }

    /**
     * 触摸事件
     * 画一条透明的线  曲线
     * @param event
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mPath.reset();
                lastX = event.getX();
                lastY = event.getY();
                mPath.moveTo(lastX, lastY);//起点
                break;
            case MotionEvent.ACTION_UP:
                mPath.lineTo(event.getX(), event.getY());
                break;
            case MotionEvent.ACTION_MOVE:
                //移动时，记录这一次的点位置
                mPath.quadTo(lastX, lastY, event.getX(), event.getY());  //设置曲线路径点和终点
                lastX = event.getX();
                lastY = event.getY();
                break;
        }
        mCanvas.drawPath(mPath, mPaint);
        invalidate();
        return true;
    }

}
