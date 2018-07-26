package com.vispect.android.vispect_g2_app.interf;

import android.app.Dialog;
import android.support.annotation.StringRes;

/**
 * ProgressDialog的控制
 * Created by xu on 2016/03/11.
 */
public interface ProgressController {
    /**
     * 隐藏等待框
     */
    void hideProgress();

    /**
     * 显示等待框
     */
    Dialog showProgress();

    /**
     * 显示等待框
     *
     * @param resId R.string.xx progressDialog显示文字
     */
    Dialog showProgress(@StringRes int resId);

    /**
     * 显示等待框
     *
     * @param text progressDialog显示文字
     */
    Dialog showProgress(CharSequence text);
}
