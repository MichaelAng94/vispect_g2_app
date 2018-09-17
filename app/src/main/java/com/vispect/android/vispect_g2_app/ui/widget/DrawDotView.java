package com.vispect.android.vispect_g2_app.ui.widget;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Build;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.vispect.android.vispect_g2_app.app.AppContext;
import com.vispect.android.vispect_g2_app.utils.XuLog;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mo on 2018/5/29.
 */

public class DrawDotView extends RelativeLayout {
    private Canvas canvas;
    private Paint paint;
    private SnagView snag1;
    private SnagView snag2;
    private SnagView snag3;
    private SnagView snag4;
    private int rawX1, rawY1, rawX2, rawY2, rawX3, rawY3, rawX4, rawY4;
    private MyLine line1, line2, line3, line4;
    private int viewnum = 0;
    private int DEVIATION = 30;
    private ViewGroup viewGroup;
    private View view1, view2, view3, view4;
    private Context ct;
    private LinearLayout lin;
    private Boolean isCalibrate = false;

    public DrawDotView(Context context) {
        super(context);
        setWillNotDraw(false);
        ct = context;
    }

    public DrawDotView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setWillNotDraw(false);
        ct = context;
    }

    public DrawDotView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setWillNotDraw(false);
        ct = context;
    }

    public static int px2dp(Context context, float pxValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    @SuppressLint("ResourceType")
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        this.canvas = canvas;

        snag1 = new SnagView(getContext());
        snag2 = new SnagView(getContext());
        snag3 = new SnagView(getContext());
        snag4 = new SnagView(getContext());
        snag1.setId(1);
        snag2.setId(2);
        snag3.setId(3);
        snag4.setId(4);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            snag1.setElevation(4);
            snag2.setElevation(4);
            snag3.setElevation(4);
            snag4.setElevation(4);
        }
        onTouch listen = new onTouch();

        snag1.setOnTouchListener(listen);
        snag2.setOnTouchListener(listen);
        snag3.setOnTouchListener(listen);
        snag4.setOnTouchListener(listen);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // 获取当前触摸的绝对坐标
        int rawX = (int) event.getRawX();
        int rawY = (int) event.getRawY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                switch (viewnum) {
                    case 0:
                        rawX1 = rawX;
                        rawY1 = rawY;
                        snag1.setX(rawX - DEVIATION);
                        snag1.setY(rawY - DEVIATION);
                        addView(snag1);
                        view1 = snag1;
                        break;
                    case 1:
                        line1 = new MyLine(getContext(), rawX1, rawY1, rawX, rawY);
                        rawX2 = rawX;
                        rawY2 = rawY;
                        addView(line1);
                        snag2.setX(rawX - DEVIATION);
                        snag2.setY(rawY - DEVIATION);
                        addView(snag2);
                        view2 = snag2;
                        break;
                    case 2:
                        line2 = new MyLine(getContext(), rawX2, rawY2, rawX, rawY);
                        rawX3 = rawX;
                        rawY3 = rawY;
                        addView(line2);
                        snag3.setX(rawX - DEVIATION);
                        snag3.setY(rawY - DEVIATION);
                        addView(snag3);
                        view3 = snag3;
                        break;
                    case 3:
                        line3 = new MyLine(getContext(), rawX3, rawY3, rawX, rawY);
                        addView(line3);
                        line4 = new MyLine(getContext(), rawX1, rawY1, rawX, rawY);
                        addView(line4);
                        rawX4 = rawX;
                        rawY4 = rawY;
                        snag4.setX(rawX - DEVIATION);
                        snag4.setY(rawY - DEVIATION);
                        addView(snag4);
                        view4 = snag4;
                        break;
                }
                if (viewnum < 4) {
                    viewnum++;
                }
                break;
        }
        return false;
    }

    public int getLinY(Activity ac) {
        Display display = ac.getWindowManager().getDefaultDisplay();
        float width = display.getHeight();
        return (int) (lin.getY() * 720 / width);
    }


    public ArrayList<Point> getSnag(Activity ac) {
        ArrayList<Point> points = new ArrayList<>();
        Point p1 = new Point();
        p1.set((int) view1.getX() + DEVIATION, (int) view1.getY() + DEVIATION);
        Point p2 = new Point();
        p2.set((int) view2.getX() + DEVIATION, (int) view2.getY() + DEVIATION);
        Point p3 = new Point();
        p3.set((int) view3.getX() + DEVIATION, (int) view3.getY() + DEVIATION);
        Point p4 = new Point();
        p4.set((int) view4.getX() + DEVIATION, (int) view4.getY() + DEVIATION);
        points.add(changePoint(p1, ac));
        points.add(changePoint(p2, ac));
        points.add(changePoint(p3, ac));
        points.add(changePoint(p4, ac));
        return points;
    }

    @SuppressLint("ResourceType")
    public void addSnag(List<Point> popints, Activity ac, Boolean withSnag) {
        if (popints == null || popints.size() < 4) {
            return;
        }

        Pointf p1 = changeSize(popints.get(0), ac);
        Pointf p2 = changeSize(popints.get(1), ac);
        Pointf p3 = changeSize(popints.get(2), ac);
        Pointf p4 = changeSize(popints.get(3), ac);

        snag1 = new SnagView(ac);
        snag2 = new SnagView(ac);
        snag3 = new SnagView(ac);
        snag4 = new SnagView(ac);

        onTouch listen = new onTouch();
        snag1.setOnTouchListener(listen);
        snag2.setOnTouchListener(listen);
        snag3.setOnTouchListener(listen);
        snag4.setOnTouchListener(listen);

        snag1.setId(1);
        snag2.setId(2);
        snag3.setId(3);
        snag4.setId(4);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            snag1.setElevation(4);
            snag2.setElevation(4);
            snag3.setElevation(4);
            snag4.setElevation(4);
        }

        snag1.setX(p1.x - DEVIATION);
        snag1.setY(p1.y - DEVIATION);
        addView(snag1);
        view1 = snag1;

        line1 = new MyLine(getContext(), p1.x, p1.y, p2.x, p2.y);
        addView(line1);
        snag2.setX(p2.x - DEVIATION);
        snag2.setY(p2.y - DEVIATION);
        addView(snag2);
        view2 = snag2;

        line2 = new MyLine(getContext(), p2.x, p2.y, p3.x, p3.y);
        addView(line2);
        snag3.setX(p3.x - DEVIATION);
        snag3.setY(p3.y - DEVIATION);
        addView(snag3);
        view3 = snag3;

        line3 = new MyLine(getContext(), p3.x, p3.y, p4.x, p4.y);
        addView(line3);
        line4 = new MyLine(getContext(), p4.x, p4.y, p1.x, p1.y);
        addView(line4);
        snag4.setX(p4.x - DEVIATION);
        snag4.setY(p4.y - DEVIATION);
        addView(snag4);
        view4 = snag4;

        rawX1 = (int) p1.x;
        rawY1 = (int) p1.y;
        rawX2 = (int) p2.x;
        rawY2 = (int) p2.y;
        rawX3 = (int) p3.x;
        rawY3 = (int) p3.y;
        rawX4 = (int) p4.x;
        rawY4 = (int) p4.y;

        if (!withSnag) {
            snag1.setVisibility(GONE);
            snag2.setVisibility(GONE);
            snag3.setVisibility(GONE);
            snag4.setVisibility(GONE);
        }
        viewnum = 4;
    }

    public void showSnag() {
        view1.setVisibility(VISIBLE);
        view2.setVisibility(VISIBLE);
        view3.setVisibility(VISIBLE);
        view4.setVisibility(VISIBLE);
    }

    public void addHorizontal(Activity ct, int y) {
        lin = new LinearLayout(ct);
        lin.setBackgroundColor(Color.RED);
        LinearLayout.LayoutParams linearParams = new LinearLayout.LayoutParams(px2dp(ct, 500), 25);
        lin.setLayoutParams(linearParams);
        lin.setY(changeSizeY(y, ct));
        Display display = ct.getWindowManager().getDefaultDisplay();
        float heigth = display.getWidth();
        lin.setX(heigth / 2 - px2dp(ct, 250));
        lin.setClickable(true);
        lin.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (isCalibrate) {
                    lin.setY(motionEvent.getRawY());
                }
                return false;
            }
        });
        addView(lin);
    }

    public void setIsCalibrate(Boolean b) {
        isCalibrate = b;
    }

    public void hideSnag() {
        view1.setVisibility(GONE);
        view2.setVisibility(GONE);
        view3.setVisibility(GONE);
        view4.setVisibility(GONE);
    }

    public Pointf changeSize(Point p, Activity ac) {
        Display display = ac.getWindowManager().getDefaultDisplay();
        float heigth = display.getWidth();
        float width = display.getHeight();
        Pointf changep = new Pointf();
        changep.x = (int) (p.x * heigth / 1280);
        changep.y = (int) (p.y * width / 720);
        return changep;
    }

    public Point changePoint(Point p, Activity ac) {
        Display display = ac.getWindowManager().getDefaultDisplay();
        float heigth = display.getWidth();
        float width = display.getHeight();
        Point changep = new Point();
        changep.x = (int) (p.x / heigth * 1280);
        changep.y = (int) (p.y / width * 720);
        return changep;
    }

    public float changeSizeY(int y, Activity ac) {
        Display display = ac.getWindowManager().getDefaultDisplay();
        float heigth = display.getWidth();
        float width = display.getHeight();
        return y * width / 720;
    }

    public void removeAll() {
        removeAllViews();
        line1 = null;
        line2 = null;
        line3 = null;
        line4 = null;
        viewnum = 0;
        addView(lin);
    }

    public void back() {
        switch (viewnum) {
            case 0:
                break;
            case 1:
                removeView(view1);
                viewnum--;
                break;
            case 2:
                removeView(view2);
                removeView(line1);
                viewnum--;
                break;
            case 3:
                removeView(view3);
                removeView(line2);
                viewnum--;
                break;
            case 4:
                removeView(view4);
                removeView(line3);
                removeView(line4);
                viewnum--;
                break;
        }
    }

    class onTouch implements OnTouchListener {

        @SuppressLint("ResourceType")
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            int rawX = (int) event.getRawX();
            int rawY = (int) event.getRawY();
            if (v.getId() == 1) {
                if (line1 != null) {
                    line1.setxy(rawX, rawY, rawX2, rawY2);
                }
                if (line4 != null) {
                    line4.setxy(rawX, rawY, rawX4, rawY4);
                }
                rawX1 = rawX;
                rawY1 = rawY;
            } else if (v.getId() == 2) {
                if (line1 != null) {
                    line1.setxy(rawX, rawY, rawX1, rawY1);
                }
                if (line2 != null) {
                    line2.setxy(rawX, rawY, rawX3, rawY3);
                }
                rawX2 = rawX;
                rawY2 = rawY;
            } else if (v.getId() == 3) {
                if (line2 != null) {
                    line2.setxy(rawX, rawY, rawX2, rawY2);
                }
                if (line3 != null) {
                    line3.setxy(rawX, rawY, rawX4, rawY4);
                }
                rawX3 = rawX;
                rawY3 = rawY;
            } else if (v.getId() == 4) {
                if (line3 != null) {
                    line3.setxy(rawX, rawY, rawX3, rawY3);
                }
                if (line4 != null) {
                    line4.setxy(rawX, rawY, rawX1, rawY1);
                }
                rawX4 = rawX;
                rawY4 = rawY;
            }
            return false;
        }
    }

    public class Pointf {
        float x;
        float y;
    }

}
