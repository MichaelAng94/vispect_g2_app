package com.vispect.android.vispect_g2_app.ui.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by mo on 2018/5/29.
 */

public class SnagView extends View {

    Paint paint;
    private int DEVIATION = 30;

    public SnagView(Context context) {
        super(context);
    }

    public SnagView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public SnagView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setAntiAlias(true);
        setLayerType(LAYER_TYPE_SOFTWARE, null);
        paint.setShadowLayer(5 ,5,5, Color.GRAY);
        canvas.drawCircle(32,32,30,paint);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int height = 70;
        int width = 70;
        final int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        final int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
        final int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        final int heightSpecSize = MeasureSpec.getMode(heightMeasureSpec);
        if (widthSpecMode == MeasureSpec.AT_MOST && heightSpecMode == MeasureSpec.AT_MOST)
        {
            setMeasuredDimension(width,height);
        }
        else if (widthSpecMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(width,heightSpecSize);
        }else if (heightSpecMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(widthSpecSize,height); }
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int rawX = (int) event.getRawX();
        int rawY = (int) event.getRawY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                paint.setColor(Color.GRAY);
                break;
            case MotionEvent.ACTION_MOVE:
                setX(rawX-DEVIATION);
                setY(rawY-DEVIATION);
                break;
        }
        return true;
    }
}
