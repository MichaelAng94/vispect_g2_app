package com.vispect.android.vispect_g2_app.controller;
import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;

/**
 * ActivityHelper生命周期帮助类
 * Created by xu on 2016/03/11.
 */
public abstract class ActivityHelper {

    static final String TAG = ActivityHelper.class.getSimpleName();

    /**
     * 在 {@link Activity#onCreate(Bundle)}调用
     */
    public abstract void onCreate(@Nullable Bundle savedInstanceState);

    /**
     * 在 {@link Activity#onStart()}调用
     */
    public abstract void onStart();

    /**
     * 在 {@link Activity#onResume()}调用
     */
    public abstract void onResume();

    /**
     * 在 {@link Activity#onPause()}调用
     */
    public abstract void onPause();

    /**
     * 在 {@link Activity#onStop()}调用
     */
    public abstract void onStop();

    /**
     * 在 {@link Activity#onDestroy()}调用
     */
    public abstract void onDestroy();

    /**
     * 在 {@link Activity#onRestart()}调用
     */
    public abstract void onRestart();

    /**
     * 在 {@link Activity#onBackPressed()}调用
     */
    public void onBackPressed() {
    }

    public static ActivityHelper create(Activity activity) {
        return new ActivityHelperImpl(activity);
    }

    /** Private constructor */
    ActivityHelper() {
    }
}
