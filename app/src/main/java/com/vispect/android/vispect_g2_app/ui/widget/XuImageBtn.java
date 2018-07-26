package com.vispect.android.vispect_g2_app.ui.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;

import com.vispect.android.vispect_g2_app.interf.OnProgressCallback;


/**
 * 自动破解的进度按钮
 *
 * Created by xu on 2017/10/30.
 */
@SuppressLint("AppCompatCustomView")
public class XuImageBtn extends ImageView implements Runnable {
    private static final String TAG = "XuImageBtn";

    private OnProgressCallback callback;
    private float startAngle = 0;
    private float sweepAngle = 0;
    private float tempAngle = 0;
    private long  interval = 50;
    private Canvas canvas = null;

    private volatile Thread runner = null;

    public XuImageBtn(Context context) {
        super(context);
    }

    public XuImageBtn(Context context, AttributeSet attrs) {
        super(context, attrs);

    }


    @Override
    protected void onDraw(Canvas canvas) {
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(12);
        canvas.rotate(270, canvas.getWidth()/2,canvas.getHeight()/2);//围绕中心顺时针旋转270度
        RectF rect = new RectF(12, 12, canvas.getWidth()-12, canvas.getHeight()-12);
//        paint.setColor(Color.RED);
//        canvas.drawRect(rect, paint);

        //先画好底色
        paint.setColor(0xFFE1E1E1);
        canvas.drawArc(rect, //弧线所使用的矩形区域大小
                0,  //开始角度
                360, //扫过的角度
                false, //是否使用中心
                paint);
        //再画进度颜色
        if(sweepAngle == 361){
            paint.setColor(0xFF00CCCC);
            canvas.drawArc(rect, //弧线所使用的矩形区域大小
                    startAngle,  //开始角度
                    tempAngle, //扫过的角度
                    false, //是否使用中心
                    paint);
        }else{
            if(tempAngle >= 0 && tempAngle <= 180){
                paint.setColor(0xFF00CCCC);
                canvas.drawArc(rect, //弧线所使用的矩形区域大小
                        startAngle,  //开始角度
                        tempAngle, //扫过的角度
                        false, //是否使用中心
                        paint);
            }else{
                paint.setColor(0xFF00CCCC);
                canvas.drawArc(rect, //弧线所使用的矩形区域大小
                        startAngle,  //开始角度
                        180, //扫过的角度
                        false, //是否使用中心
                        paint);
                paint.setColor(0xFF999999);
                canvas.drawArc(rect, //弧线所使用的矩形区域大小
                        180,  //开始角度
                        (tempAngle-180), //扫过的角度
                        false, //是否使用中心
                        paint);

            }
        }




//        canvas.save();
        super.onDraw(canvas);

    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {

        }
    };
    public boolean setAngle(float startAngle,float sweepAngle,int interval,OnProgressCallback callback){
        if(this.tempAngle < this.sweepAngle){
            return false;
        }
        this.startAngle = startAngle;
        this.sweepAngle = sweepAngle;
        tempAngle = startAngle;
        this.interval = interval;
        this.callback = callback;
        runner = new Thread(this);
        runner.start();
        return true;
    }

    public boolean isAlive(){
        if(runner == null){
            return false;
        }else{
            return  runner.isAlive();
        }
    }

    public void setInterval(long interval) {
        this.interval = interval;
    }

    @Override
    public void run() {
        while (tempAngle < sweepAngle){
            postInvalidate();
//            startAngle++;
            tempAngle ++;
            if(callback != null){
                callback.onProgress(tempAngle,sweepAngle);
            }
//            XuLog.e(TAG, "画一次:"+tempAngle);


            try {
                Thread.sleep(interval);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        startAngle = 0;
        sweepAngle = 0;
        tempAngle = 0;
        callback = null;
        postInvalidate();

    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:

                Drawable drawable = getBackground();
                drawable.setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);
                setBackground(drawable);
                postInvalidate();
                break;
            case MotionEvent.ACTION_UP:
                Drawable drawables = getBackground();
                drawables.clearColorFilter();
                setBackground(drawables);
                postInvalidate();
                break;
        }
        return super.onTouchEvent(event);
    }
}
