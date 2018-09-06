package com.vispect.android.vispect_g2_app.ui.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.LayoutRes;
import android.support.annotation.StringRes;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.TextView;


import com.vispect.android.vispect_g2_app.R;
import com.vispect.android.vispect_g2_app.controller.ActivityHelper;
import com.vispect.android.vispect_g2_app.interf.OnSoftKeyboardChangeListener;
import com.vispect.android.vispect_g2_app.interf.ProgressController;

import java.util.ArrayList;
import java.util.List;

/**
 * Activity基类 Created by xu on 2016/03/11.
 */
public abstract class BaseActivity extends FragmentActivity implements ActivityCompat.OnRequestPermissionsResultCallback {
    /**
     * 6.0之后需要动态申请的权限
     * <p>
     * group:android.permission-group.CONTACTS
     * permission:android.permission.WRITE_CONTACTS
     * permission:android.permission.GET_ACCOUNTS
     * permission:android.permission.READ_CONTACTS
     * <p>
     * group:android.permission-group.PHONE
     * permission:android.permission.READ_CALL_LOG
     * permission:android.permission.READ_PHONE_STATE
     * permission:android.permission.CALL_PHONE
     * permission:android.permission.WRITE_CALL_LOG
     * permission:android.permission.USE_SIP
     * permission:android.permission.PROCESS_OUTGOING_CALLS
     * permission:com.android.voicemail.permission.ADD_VOICEMAIL
     * <p>
     * group:android.permission-group.CALENDAR
     * permission:android.permission.READ_CALENDAR
     * permission:android.permission.WRITE_CALENDAR
     * <p>
     * group:android.permission-group.CAMERA
     * permission:android.permission.CAMERA
     * <p>
     * group:android.permission-group.SENSORS
     * permission:android.permission.BODY_SENSORS
     * <p>
     * group:android.permission-group.LOCATION
     * permission:android.permission.ACCESS_FINE_LOCATION
     * permission:android.permission.ACCESS_COARSE_LOCATION
     * <p>
     * group:android.permission-group.STORAGE
     * permission:android.permission.READ_EXTERNAL_STORAGE
     * permission:android.permission.WRITE_EXTERNAL_STORAGE
     * <p>
     * group:android.permission-group.MICROPHONE
     * permission:android.permission.RECORD_AUDIO
     * <p>
     * group:android.permission-group.SMS
     * permission:android.permission.READ_SMS
     * permission:android.permission.RECEIVE_WAP_PUSH
     * permission:android.permission.RECEIVE_MMS
     * permission:android.permission.RECEIVE_SMS
     * permission:android.permission.SEND_SMS
     * permission:android.permission.READ_CELL_BROADCASTS
     */

    private static final int PERMISSON_REQUESTCODE = 0;
    /**
     * activity 生命周期管理
     */
    private ActivityHelper mActivityHelper;
    /**
     * 等待进度条
     */
    private Dialog mProgressDialog;
    private int a = 0;
    /**
     * 判断是否需要检测，防止不停的弹框
     */
    private boolean isNeedCheck = true;
    /**
     * 监听软键盘的弹出和高度
     */
    private boolean lastvisible = false;

    /**
     * 要显示的布局id
     *
     * @return 返回0则表示不显示布局
     */
    @LayoutRes
    public abstract int getContentResource();

    /**
     * @return 是否实例化布局文件
     */
    public boolean isCreatedContent() {
        return getContentResource() != 0;
    }

    public ActivityHelper getActivityHelper() {
        if (null == mActivityHelper) {
            mActivityHelper = ActivityHelper.create(this);
        }
        return mActivityHelper;
    }

    public String STR(@StringRes int id) {
        return getResources().getString(id);
    }

    @Override
    public void onBackPressed() {
        // 这里必须放在super.onBackPressed()方法之前
        getActivityHelper().onBackPressed();
        super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        onBeforeSetContent();

        View view = onCreateContentView();
        if (view != null) {
            setContentView(view);
        }

        getActivityHelper().onCreate(savedInstanceState);

        // 初始化
        initData();
        initView(view);
        // 根据系统版本决定是否开启沉浸式
//		if (VERSION.SDK_INT >= VERSION_CODES.KITKAT) {
//			// 透明状态栏
////			getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//			getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//					WindowManager.LayoutParams.FLAG_FULLSCREEN);
////			// 透明导航栏
////			getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
//		}
    }

    @Override
    protected void onStart() {
        super.onStart();
        getActivityHelper().onStart();
    }

    protected void setTitle(String title) {
        TextView tv = findViewById(R.id.tv_title);
        tv.setText(title);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getActivityHelper().onResume();
    }

    /**
     * 创建contentView
     */
    protected View onCreateContentView() {
        return isCreatedContent() ? View.inflate(this, getContentResource(), null) : null;
    }

    /**
     * 在{@link BaseActivity#setContentView(View)}前调用
     */
    protected void onBeforeSetContent() {
    }

    /**
     * 初始化数据
     */
    protected void initData() {
    }

    /**
     * 初始化View,在initData()后调用
     *
     * @param view {@link BaseActivity#getContentResource()}实例化出来的view
     */
    protected abstract void initView(View view);

    @Override
    protected void onPause() {
        super.onPause();
        getActivityHelper().onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        getActivityHelper().onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getActivityHelper().onDestroy();
        mProgressDialog = null;
        mActivityHelper = null;
//		AppContext.getInstance().getRefWatcher().watch(this);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        getActivityHelper().onRestart();
    }

    /**
     * 开始检测是否拥有权限
     */
    public void checkPermissions(String... permissions) {
        List<String> needRequestPermissonList = findDeniedPermissions(permissions);
        if (null != needRequestPermissonList
                && needRequestPermissonList.size() > 0) {
            ActivityCompat.requestPermissions(this,
                    needRequestPermissonList.toArray(
                            new String[needRequestPermissonList.size()]),
                    PERMISSON_REQUESTCODE);
        }
    }

    /**
     * 获取权限集中需要申请权限的列表
     *
     * @param permissions
     * @return
     */
    private List<String> findDeniedPermissions(String[] permissions) {
        List<String> needRequestPermissonList = new ArrayList<String>();
        for (String perm : permissions) {
            if (ContextCompat.checkSelfPermission(this,
                    perm) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.shouldShowRequestPermissionRationale(
                    this, perm)) {
                needRequestPermissonList.add(perm);
            }
        }
        return needRequestPermissonList;
    }

    /**
     * 检测是否说有的权限都已经授权
     *
     * @param grantResults
     * @return
     * @since 2.5.0
     */
    private boolean verifyPermissions(int[] grantResults) {
        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] paramArrayOfInt) {
        if (requestCode == PERMISSON_REQUESTCODE) {
            if (!verifyPermissions(paramArrayOfInt)) {
                //showMissingPermissionDialog();
                isNeedCheck = false;
            }
        }
    }

    /**
     * 启动应用的设置
     *
     * @since 2.5.0
     */
    private void startAppSettings() {
        Intent intent = new Intent(
                Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:" + getPackageName()));
        startActivity(intent);
        isNeedCheck = true;
    }

    public void observeSoftKeyboard(final OnSoftKeyboardChangeListener listener) {
        final View decorView = getWindow().getDecorView();
        decorView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            int previousKeyboardHeight = -1;

            @Override
            public void onGlobalLayout() {
                Rect rect = new Rect();
                decorView.getWindowVisibleDisplayFrame(rect);
                int displayHeight = rect.bottom - rect.top;
                int height = decorView.getHeight();
                int keyboardHeight = height - displayHeight;
                if (previousKeyboardHeight != keyboardHeight) {
                    boolean hide = (double) displayHeight / height > 0.8;
                    if (lastvisible != !hide) {
                        lastvisible = !hide;
                        listener.onSoftKeyBoardChange(keyboardHeight, !hide);
                    }
                }
                previousKeyboardHeight = height;

            }
        });
    }


}
