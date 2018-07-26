package com.vispect.android.vispect_g2_app.utils;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.view.KeyEvent;
import android.widget.Toast;

import com.vispect.android.vispect_g2_app.R;
import com.vispect.android.vispect_g2_app.app.AppContext;


/**
 * 双击退出的工具类
 *  Created by xu on 2016/03/11.
 */
public class DoubleClickExit {
    private final Activity mActivity;
    private boolean mIsOnKeyBacking;
    private Handler mHandler;
    private Toast mBackToast;

    public DoubleClickExit(Activity activity) {
        mActivity = activity;
        mHandler = new Handler(Looper.getMainLooper());
    }


    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode != KeyEvent.KEYCODE_BACK) {
            return false;
        }

        if (mIsOnKeyBacking) {
            mHandler.removeCallbacks(mOnBackTimeRunnable);
            if (mBackToast != null) {
                mBackToast.cancel();
            }
            return true;
        } else {
            mIsOnKeyBacking = true;
            if (mBackToast == null) {
                mBackToast = Toast.makeText(mActivity, AppContext.getInstance().getResources().getString(R.string.click_again_to_exit_app), Toast.LENGTH_LONG);
            }

            mBackToast.show();
            mHandler.postDelayed(mOnBackTimeRunnable, 2000);
            return false;
        }

    }

    private Runnable mOnBackTimeRunnable = new Runnable() {

        @Override
        public void run() {
            mIsOnKeyBacking = false;
            if (mBackToast != null) {
                mBackToast.cancel();
            }
        }
    };
}
