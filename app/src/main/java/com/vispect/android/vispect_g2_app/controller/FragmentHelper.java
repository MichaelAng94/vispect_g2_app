package com.vispect.android.vispect_g2_app.controller;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;

/**
 * FragmentHelper生命周期帮助类
 * Created by xu on 2016/03/11.
 */
public abstract class FragmentHelper {
    static final String TAG = FragmentHelper.class.getSimpleName();

    /**
     * 在 {@link Fragment#onAttach(Context)}调用
     */
    public abstract void onAttach(Context context);

    /**
     * 在{@link Fragment#onCreate(Bundle)} 调用
     */
    public abstract void onCreate(Bundle savedInstanceState);


    public abstract void onCreateView(@Nullable View view);

    /**
     * 在{@link Fragment#onActivityCreated(Bundle)} 调用
     */
    public abstract void onActivityCreated(@Nullable Bundle savedInstanceState);

    /**
     * 在{@link Fragment#onStart()} 调用
     */
    public abstract void onStart();

    /**
     * 在{@link Fragment#onResume()} 调用
     */
    public abstract void onResume();

    /**
     * 在{@link Fragment#onPause()} 调用
     */
    public abstract void onPause();

    /**
     * 在{@link Fragment#onStop()} 调用
     */
    public abstract void onStop();

    /**
     * 在{@link Fragment#onDestroyView()} 调用
     */
    public abstract void onDestroyView();

    /**
     * 在{@link Fragment#onDestroy()} 调用
     */
    public abstract void onDestroy();

    /**
     * 在{@link Fragment#onDetach()} 调用
     */
    public abstract void onDetach();

    public static FragmentHelper create(Fragment fragment) {
        return new FragmentHelperImpl(fragment);
    }

    FragmentHelper() {
    }
}