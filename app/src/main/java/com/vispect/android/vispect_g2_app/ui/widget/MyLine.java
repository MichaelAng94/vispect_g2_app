package com.vispect.android.vispect_g2_app.ui.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by mo on 2018/5/29.
 */

public class MyLine extends View {
    float x1,y1,x2,y2;


    public MyLine(Context context, float x1, float y1, float x2, float y2) {
        super(context);
        this.x1 = x1;
        this.x2 = x2;
        this.y1 = y1;
        this.y2 = y2;
    }

    public MyLine(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public MyLine(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Paint paint = new Paint();
        paint.setColor(Color.BLUE);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(3);
        paint.setAntiAlias(true);
        canvas.drawLine(x1,y1,x2,y2,paint);
    }

    public void setxy(float x1,float y1,int x2,int y2){
        this.x1 = x1;
        this.x2 = x2;
        this.y1 = y1;
        this.y2 = y2;
        invalidate();
    }

}
