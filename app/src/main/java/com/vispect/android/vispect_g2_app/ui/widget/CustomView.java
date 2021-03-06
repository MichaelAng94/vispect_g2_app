package com.vispect.android.vispect_g2_app.ui.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.vispect.android.vispect_g2_app.controller.UIHelper.dp2px;

public class CustomView extends View {

    private List<Point> _points = new ArrayList<>();
    private Map<Integer, List<Point>> _map = new HashMap<>();
    private Paint paint = new Paint();
    private Paint redPaint = new Paint();
    private Paint facePaint = new Paint();
    private Context _context;
    private int _screenWidth;
    private int _screenHeight;
    private int _startY = 0;
    private int _startX = 0;

    {
        paint.setColor(Color.BLUE);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(3);
        paint.setAntiAlias(true);
        redPaint.setColor(Color.RED);
        redPaint.setStyle(Paint.Style.STROKE);
        redPaint.setStrokeWidth(20);
        redPaint.setAntiAlias(true);
        facePaint.setColor(Color.GREEN);
        facePaint.setStyle(Paint.Style.STROKE);
        facePaint.setStrokeWidth(3);
        facePaint.setAntiAlias(true);
    }

    public CustomView(Context context) {
        super(context);
        _context = context;
        init();
    }

    public CustomView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        _context = context;
        init();
    }

    public CustomView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        _context = context;
        init();
    }

    private void init() {
        _screenWidth = getScreenWidth();
        _screenHeight = getScreenHeight();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (_points != null && _points.size() == 4) {
            Path path = new Path();
            Point point0 = changeSize(_points.get(0));
            Point point1 = changeSize(_points.get(1));
            Point point2 = changeSize(_points.get(2));
            Point point3 = changeSize(_points.get(3));
            path.moveTo(point0.x, point0.y);
            path.lineTo(point1.x, point1.y);
            path.lineTo(point2.x, point2.y);
            path.lineTo(point3.x, point3.y);
            path.lineTo(point0.x, point0.y);
            canvas.drawPath(path, paint);
        }
        if (_startX != 0 && _startY != 0) {
            canvas.drawLine(_startX, _startY, _startX + dp2px(_context, 100), _startY, redPaint);
        }
        if (_map != null && _map.size() > 0) {
            for (int i = 0; i < _map.size(); i++) {
                if (_map.containsKey(i)) {
                    List<Point> points = _map.get(i);
                    for (Point point : points) {
                        Point pointCS = changeSize(point);
                        canvas.drawPoint(pointCS.x, pointCS.y, facePaint);
                    }
                }
            }
        }
    }

    public void setPointList(@Nullable List<Point> points) {
        _points.clear();
        if (points != null && points.size() == 4) {
            _points.addAll(points);
        }
        invalidate();
    }

    public Point changeSize(Point p) {
        Point point = new Point();
        point.x = p.x * _screenWidth / 1280;
        point.y = p.y * _screenHeight / 720;
        return point;
    }

    private int getScreenWidth() {
        WindowManager windowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.widthPixels;
    }

    private int getScreenHeight() {
        WindowManager windowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.heightPixels;
    }

    public void addRedLine(int y) {
        _startY = y * _screenHeight / 720;
        _startX = _screenWidth / 2 - dp2px(_context, 50);
        invalidate();
    }

    public void clearView() {
        _points.clear();
        _map.clear();
        _startY = 0;
        _startX = 0;
        invalidate();
    }

    public void setPointMap(@Nullable Map map) {
        _map.clear();
        if (map != null && map.size() > 0) {
            _map.putAll(map);
        }
        invalidate();
    }
}
