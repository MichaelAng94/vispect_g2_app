package com.vispect.android.vispect_g2_app.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

/**
 * Created by mo on 2018/7/12.
 *
 * 控制长宽比
 */

public class MoRelative  extends RelativeLayout{
    public MoRelative(Context context) {
        super(context);
    }

    public MoRelative(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MoRelative(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);
    }
}
