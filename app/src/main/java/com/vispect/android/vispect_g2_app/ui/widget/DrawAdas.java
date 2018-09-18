package com.vispect.android.vispect_g2_app.ui.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.vispect.android.vispect_g2_app.interf.SurfaceViewWidthChangeCallback;
import com.vispect.android.vispect_g2_app.utils.DrawUtil;
import com.vispect.android.vispect_g2_app.utils.XuLog;
import com.vispect.android.vispect_g2_app.utils.XuString;

import java.util.ArrayList;
import java.util.concurrent.Executors;

import bean.DrawShape;


/**
 * 在实时路况话ADAS的surfaceview
 * <p>
 * Created by xu on 2016/8/18.
 */
public class DrawAdas extends SurfaceView implements SurfaceHolder.Callback, Runnable {


    public static final String RESETDISPALY = "RESETDISPALY";
    private final static String TAG = "DrawAdas";
    /**
     * 视频分辨率
     */
    public static int width = 1280;
    public static int height = 720;
    public static int screenHeight;
    private SurfaceViewWidthChangeCallback widthChangeCallback;
    private SurfaceHolder sfh;
    private Canvas canvas;
    private Paint paint;
    private Context context;
    private volatile Thread runner = null;
    // 画线和矩形的集合
    private ArrayList<DrawShape> drawList = new ArrayList<DrawShape>();
    private int screenWidth;
    private float scaleWidth = 1280;
    private float scaleHeight = 720;

    public DrawAdas(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        setFocusable(true);
        setZOrderOnTop(true);
        setKeepScreenOn(true);
        sfh = getHolder();
        sfh.addCallback(this);
        sfh.setFormat(PixelFormat.TRANSLUCENT);
        paint = new Paint();
        paint.setAntiAlias(true);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getRepeatCount() == 0) {
            XuLog.e("VMainActivity", "DrawAdas返回键被按下了！");
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        //TODO 可以开始画
        PlayVideo();
        XuLog.d(TAG, "surfaceCreated");
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        XuLog.d(TAG, "surfaceChanged");
        if (widthChangeCallback != null) {
            widthChangeCallback.onWidthChange();
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        XuLog.d(TAG, "surfaceDestroyed");
    }

    public void PlayVideo() {
        if (runner == null) {
            runner = new Thread(this);
            runner.start();
        }
    }

    public void stopVideo() {
        if (runner != null) {
            Thread moribund = runner;
            runner = null;
            moribund.interrupt();
        }


    }

    public void setDrawList(ArrayList<DrawShape> drawList) {
        this.drawList.clear();
        this.drawList.addAll(drawList);
        XuLog.e(TAG, "last drawList = drawList    drawList.size= " + drawList.size());
        drawTask task = new drawTask();
        task.execute("drawtask");
    }

    public void setScreenWidth(int screenWidths) {
        XuLog.d(TAG, "screenWidth被设定了：" + screenWidths);
        screenWidth = screenWidths;
        scaleWidth = (float) screenWidth / width;
        scaleHeight = (float) screenHeight / height;
    }

    public void setScreenHeight(int screenHeights) {
        XuLog.d(TAG, "screenHeight被设定了：" + screenHeights);
        screenHeight = screenHeights;
        scaleWidth = (float) screenWidth / width;
        scaleHeight = (float) screenHeight / height;
    }

    public float getScaleWidth() {
        return scaleWidth;
    }

    public float getScaleHeight() {
        return scaleHeight;
    }

    private void clear(Canvas mcanvas) {
        if (mcanvas == null) {
            return;
        }
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        canvas.drawPaint(paint);
        paint.reset();

    }

    synchronized private void draw() {
        canvas = sfh.lockCanvas();
        // 画线条和矩形
        if (drawList.size() > 0 && canvas != null) {
            try {
                int lin = 0;
                int car = 0;
                int sens = 0;
                scaleWidth = (float) screenWidth / width;
                scaleHeight = (float) screenHeight / height;
                clear(canvas);
                for (int i = 0; i < drawList.size(); i++) {
                    DrawShape ds = drawList.get(i);
                    int type = ds.getType();
                    if (type == 0) {
                        lin++;
                    }
                    if (type == 1) {
                        car++;
                    }
                    if (type == 3) {
                        sens++;
                    }
                    float x0 = ds.getX0() * scaleWidth;
                    float y0 = ds.getY0() * scaleHeight;
                    float x1 = ds.getX1() * scaleWidth;
                    float y1 = ds.getY1() * scaleHeight;
                    float stroke_width = ds.getStroke_width();
                    int color = ds.getColor();
                    boolean isDashed = ds.isDashed();
                    XuLog.d(TAG, "x0:" + x0 + "  y0:" + y0 + "  x1:" + x1 + "  y1:" + y1 + "  TextStr:" + ds.getTextStr());

                    DrawUtil.drawLineOrRect(canvas, type, x0, y0, x1, y1, stroke_width, color, isDashed, XuString.isEmpty(ds.getTextStr()) ? "" : ds.getTextStr() + ",unknow", paint);
                    paint.reset();
                }
                XuLog.e(TAG, "收到了 线：" + lin + "       车：" + car + "    sensor:" + sens);
            } catch (Exception e) {
                e.printStackTrace();
                XuLog.e(TAG, "收到错误的画框/线数据   e:" + e.getMessage());
            }
        }
        drawList.clear();
        if (canvas != null && sfh != null) {
            sfh.unlockCanvasAndPost(canvas);
        }
    }


    @Override
    public void run() {
        try {
//            android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_DISPLAY);
//            while (!Thread.currentThread().isInterrupted()){
//                draw();
//            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void setWidthChangeCallback(SurfaceViewWidthChangeCallback widthChangeCallback) {
        this.widthChangeCallback = widthChangeCallback;
    }


    private class drawTask extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                draw();
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }
    }


}
