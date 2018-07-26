package com.vispect.android.vispect_g2_app.utils;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.PointF;
import android.graphics.Typeface;
import android.os.Build;
import android.view.WindowManager;

import com.vispect.android.vispect_g2_app.R;
import com.vispect.android.vispect_g2_app.app.AppContext;
import com.vispect.android.vispect_g2_app.ui.widget.DrawAdas;


/**
 * 图形绘制类
 *
 * @author puhuakuang
 */
public class DrawUtil {

    public static final int LINE = 0;
    public static final int RECT = 1;
    public static final int TEXT = 2;
    public static final int SLINE = 3;
    static WindowManager wm = null;

    /**
     * 画线条或者矩形
     *
     * @param canvas       画布
     * @param type         类型 （0，画线 1, 画矩形）
     * @param x0           起始点x坐标
     * @param y0           起始点y坐标
     * @param x1           终止点x坐标
     * @param y1           终止点y坐标
     * @param stroke_width 线的宽度
     * @param color        线的颜色
     * @param isDashed     是否虚线(true 是虚线 ， false 实线)
     */
    public static void drawLineOrRect(Canvas canvas, int type, float x0, float y0, float x1, float y1, float stroke_width, int color, boolean isDashed, String text, Paint paint) {

        switch (type) {
            // 画线
            case LINE:
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeWidth(AppContext.getInstance().getResources().getDimension(R.dimen.x12));
                paint.setColor(color);
                Path path = new Path();
                path.moveTo(x0, y0);
                path.lineTo(x1, y1);
                if (isDashed) {
                    PathEffect effects = new DashPathEffect(new float[]{20, 10}, 1);
                    paint.setPathEffect(effects);
                }
                canvas.drawPath(path, paint);


                Paint paintTextL = new Paint();
                paintTextL.setColor(color);// 设置颜色
                paintTextL.setTypeface(Typeface.create("宋体", Typeface.BOLD));
                paintTextL.setAntiAlias(true);//去除锯齿
                paintTextL.setFilterBitmap(true);//对位图进行滤波处理
                paintTextL.setTextSize(AppContext.getInstance().getResources().getDimension(R.dimen.x60));
                float tXL = x1;
                float tYL = y1 + 50;

                //计算斜率  安卓横屏的原点在左上角
                float k = getSlope(x0, y0, x1, y1);
                if (k >= 0) {
                    //右线
                    tXL = x0 - 300;
                    tYL = y0 - 50;
                } else {
                    //左线
                    tXL = x0 + 120;
                    tYL = y0 - 50;
                }
                if (!XuString.isEmpty(text) && text.indexOf("T:") != -1) {
                    String ttc = "T:" + text.split("T:")[1];
                    String content = text.split("T:")[0];
                    canvas.drawText(content, tXL, tYL, paintTextL);
                }

                break;
            // 画矩形
            case RECT:
                paint.setColor(color);// 设置颜色
                paint.setStyle(Paint.Style.FILL);//设置实心
                paint.setAlpha(80);

                paint.setStrokeWidth(stroke_width);
                if (isDashed) {
                    PathEffect effects = new DashPathEffect(new float[]{5, 5, 5, 5}, 1);
                    paint.setPathEffect(effects);
                }

                canvas.drawRect(x0, y0, x1, y1, paint);// 矩形

                //设置画框
                Paint paintB = new Paint();
                paintB.setColor(color);// 设置颜色
                paintB.setStyle(Paint.Style.STROKE);//设置空心
                paintB.setStrokeWidth(stroke_width);
                canvas.drawRect(x0, y0, x1, y1, paintB);// 矩形

                Paint paintText = new Paint();
                paintText.setColor(color);// 设置红色
                paintText.setTypeface(Typeface.create("宋体", Typeface.BOLD));
                paintText.setAntiAlias(true);//去除锯齿
                paintText.setFilterBitmap(true);//对位图进行滤波处理
                paintText.setTextSize(AppContext.getInstance().getResources().getDimension(R.dimen.x60));

                float tX = x0;
                float tY = y1 + 50;
                if (y1 >= DrawAdas.screenHeight - 50) {
                    if (y1 >= DrawAdas.screenHeight) {
                        tY = DrawAdas.screenHeight - 50;
                    } else {
                        tY = y1 - 50;
                    }
                    paintText.setColor(Color.WHITE);
                }
                if (!XuString.isEmpty(text) && text.indexOf(",") != -1) {
                    String id = text.split(",")[1];
                    String content = text.split(",")[0];
                    canvas.drawText(content, tX, tY, paintText);
//                    paintText.setColor(Color.WHITE);
//                    paintText.setTextSize(AppContext.getInstance().getResources().getDimension(R.dimen.x60));
//                    canvas.drawText(id,  x0, y0+((y1-y0)/2) ,paintText);


                }
                break;
            case SLINE:
                if (wm == null) {
                    wm = (WindowManager) AppContext.getInstance()
                            .getSystemService(Context.WINDOW_SERVICE);
                }

                int width = wm.getDefaultDisplay().getWidth();
                int height = wm.getDefaultDisplay().getHeight();
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeWidth(AppContext.getInstance().getResources().getDimension(R.dimen.x12));
                paint.setAntiAlias(true);//去除锯齿
                paint.setFilterBitmap(true);//对位图进行滤波处理
                paint.setColor(color);
                Path pathsLine = new Path();
                pathsLine.moveTo(x0, y0);
                pathsLine.lineTo(x1, y1);
                canvas.drawPath(pathsLine, paint);


                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    paint.setColor(AppContext.getInstance().getColor(R.color.color_warning));
                }


                float linelength = (float) (width * 0.125);
                float kkx = (float) (Math.sqrt(Math.pow(x0 - width/2, 2) + Math.pow(y0 - height/2, 2)));
                float kky = kkx / linelength;



                Path pathsLine1 = new Path();
                pathsLine1.moveTo(width / 2, height / 2);
                pathsLine1.lineTo(width/2 - (width / 2 - x0)/kky, height/2 - (height / 2 - y0)/kky);
                canvas.drawPath(pathsLine1, paint);

                pathsLine1 = new Path();
                pathsLine1.moveTo(width / 2, height / 2);
                pathsLine1.lineTo(width/2 + (width / 2 - x0)/kky, height/2 + (height / 2 - y0)/kky);
                canvas.drawPath(pathsLine1, paint);

                break;
            default:
                break;
        }

    }


    /**
     * 计算两点形成的直线方程的参数a,b,c
     *
     * @param x1 点1的x
     * @param y1 点1的y
     * @param x2 点2的x
     * @param y2 点2的y
     * @return 直线一般方程的a、b、c的值组成的数组
     */
    public static double[] getGeneralEquationABC(float x1, float y1, float x2, float y2) {
        double a, b, c;

        a = y2 - y1;
        b = x1 - x2;
        c = (x2 - x1) * y1 - (y2 - y1) * x1;
        if (b < 0) {
            a *= -1;
            b *= -1;
            c *= -1;
        } else if (b == 0 && a < 0) {
            a *= -1;
            c *= -1;
        }
        return new double[]{a, b, c};
    }

    /**
     * 计算两直线的交点
     *
     * @param point1 点1的直线方程的a、b、c组成的数组
     * @param point2 点2的直线方程的a、b、c组成的数组
     * @return 交点
     */
    public static PointF getIntersectPoint(double[] point1, double[] point2) {
        PointF p = null;
        double a1 = point1[0];
        double b1 = point1[1];
        double c1 = point1[2];
        double a2 = point2[0];
        double b2 = point2[1];
        double c2 = point2[2];
        double m = a1 * b2 - a2 * b1;
        if (m == 0) {
            return null;
        }
        double x = (c2 * b1 - c1 * b2) / m;
        double y = (c1 * a2 - c2 * a1) / m;
        p = new PointF((float) x, (float) y);
        return p;
    }

    /**
     * 计算直线的斜率
     *
     * @param x0 点1的X坐标
     * @param y0 点1的Y坐标
     * @param x1 点2的X坐标
     * @param y1 点2的Y坐标
     * @return 交点
     */
    private static float getSlope(float x0, float y0, float x1, float y1) {
        float k = (y1 - y0) / (x1 - x0);
        return k;

    }


}
