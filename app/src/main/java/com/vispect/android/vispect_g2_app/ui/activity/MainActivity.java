package com.vispect.android.vispect_g2_app.ui.activity;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.vispect.android.vispect_g2_app.R;
import com.vispect.android.vispect_g2_app.app.AppContext;
import com.vispect.android.vispect_g2_app.app.AppManager;
import com.vispect.android.vispect_g2_app.controller.DeviceHelper;
import com.vispect.android.vispect_g2_app.controller.UIHelper;
import com.vispect.android.vispect_g2_app.ui.fragment.IndexFragment;
import com.vispect.android.vispect_g2_app.ui.fragment.UserInfoFragment;
import com.vispect.android.vispect_g2_app.utils.DoubleClickExit;
import com.vispect.android.vispect_g2_app.utils.PermissionUtils;
import com.vispect.android.vispect_g2_app.utils.SDcardTools;
import com.vispect.android.vispect_g2_app.utils.XuLog;
import com.vispect.android.vispect_g2_app.utils.XuToast;

import java.io.File;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import interf.OnDeviceConnectionStateChange;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.READ_PHONE_STATE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.provider.MediaStore.ACTION_IMAGE_CAPTURE;
import static com.vispect.android.vispect_g2_app.app.AppConfig.CONNECT_DEVICE_OK;
import static com.vispect.android.vispect_g2_app.app.AppConfig.IMAGE_USER_AVATAR_NAME;
import static com.vispect.android.vispect_g2_app.app.AppConfig.REQUEST_CODE_CAMERA;
import static com.vispect.android.vispect_g2_app.app.AppConfig.REQUEST_CODE_CAMERA_PERMISSION;
import static com.vispect.android.vispect_g2_app.app.AppConfig.REQUEST_CODE_CONNECT_DEVICE;
import static com.vispect.android.vispect_g2_app.app.AppConfig.REQUEST_CODE_GLOBAL_PERMISSION;
import static com.vispect.android.vispect_g2_app.controller.DeviceHelper.isConnected;
import static com.vispect.android.vispect_g2_app.controller.DeviceHelper.isG2;
import static com.vispect.android.vispect_g2_app.controller.DeviceHelper.isG2Connected;

public class MainActivity extends BaseActivity {

    @Bind(R.id.drawer_layout)
    DrawerLayout drawer;
    @Bind(R.id.img_left)
    ImageView imgLeft;
    @Bind(R.id.title)
    TextView title;
    @Bind(R.id.img_right)
    ImageView imgRight;
    private DoubleClickExit doubleClick = new DoubleClickExit(this);
    private String TAG = "MainActivity";
    private int lastConnectionState = 0;
    private boolean _deviceConnect = false;
    OnDeviceConnectionStateChange deviceConnectionStateListener = new OnDeviceConnectionStateChange() {
        //TODO 监听蓝牙的连接状态
        @Override
        public void onConnectionStateChange(int i) {
            if (lastConnectionState != i) {
                XuLog.e(TAG, "连接状态发生变化：" + i);
                lastConnectionState = i;
                if (i == 0) {
//                    imgConnect.setColorFilter(null);
                    XuToast.show(MainActivity.this, R.string.device_disconnected);
                    if (imgRight != null) {
                        imgRight.setImageResource(R.drawable.disconnect);
                        _deviceConnect = false;
                    }
                    XuLog.e(TAG, "需要退回到首页");
                    AppContext.getInstance().getDeviceHelper().closeWIFIMode();
                    AppContext.getInstance().getDeviceHelper().closeDeviceRealViewMode();
                    //关闭热点
                    if (AppContext.getInstance().getWificontroller().isWifiApEnabled()) {
                        XuLog.e(TAG, "断开连接关热点");
                        AppContext.getInstance().getWificontroller().setWifiApEnabled(false);
                    }
                    AppManager.getInstance().finishActivityToindex();
                }
            }
        }

        @Override
        public void onSocketStateChange(int i) {
        }
    };


    @Override
    public int getContentResource() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView(View view) {

        PermissionUtils.request(this, REQUEST_CODE_GLOBAL_PERMISSION, ACCESS_COARSE_LOCATION, READ_PHONE_STATE, READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE, CAMERA);

//        setTitle("G2-ADAS");
        AppContext.getInstance().setNeeedCloseBluetooth(!BluetoothAdapter.getDefaultAdapter().isEnabled());
        //监听连接变化
        AppContext.getInstance().getDeviceHelper().setDeviceConnectStateListener(deviceConnectionStateListener);

        addUserInfoFragment();
        addIndexFragment();
    }

    @Override
    protected void onResume() {
        super.onResume();
        XuLog.e(TAG, "onResume：isG2Connected() : " + isG2Connected());
        imgRight.setImageResource((_deviceConnect || isG2Connected()) ? R.drawable.connet_ble : R.drawable.disconnect);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
        initPhotoError();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
//            if (connectRealView){
//                myHandler.removeCallbacks(changeDialog);
//                cancelConnect();
//                return super.onKeyDown(keyCode, event);
//            }
            XuLog.e(TAG, "捕获到一次按下返回按钮的事件:" + keyCode);
            if (doubleClick.onKeyDown(keyCode, event)) {
                return super.onKeyDown(keyCode, event);
            } else {
                return false;
            }
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    private void initPhotoError() {
        // android 7.0系统解决拍照的问题
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
            builder.detectFileUriExposure();
        }
    }

    public void addUserInfoFragment() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.drawer, new UserInfoFragment(), UserInfoFragment.class.getSimpleName());
        transaction.addToBackStack(UserInfoFragment.class.getSimpleName()).commit();
    }

    public void addIndexFragment() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.container, new IndexFragment(), IndexFragment.class.getSimpleName());
        transaction.addToBackStack(IndexFragment.class.getSimpleName()).commit();
    }

    @OnClick({R.id.img_left, R.id.img_right})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.img_left:
                if (!drawer.isDrawerOpen(Gravity.LEFT)) {
                    drawer.openDrawer(Gravity.LEFT);
                }
                break;
            case R.id.img_right:
                if (isConnected()) {
                    DeviceHelper.cancelConnect();
                    _deviceConnect = false;
                    imgRight.setImageResource(R.drawable.disconnect);
                    imgRight.clearColorFilter();
                    XuToast.show(this, R.string.device_disconnected);
                    break;
                }
                UIHelper.startActivityForResult(MainActivity.this, ConnectActivity.class, REQUEST_CODE_CONNECT_DEVICE);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CODE_CONNECT_DEVICE:
                if (resultCode == CONNECT_DEVICE_OK) {
                    XuLog.e(TAG, "onActivityResult imgRight.setImageResource ");
                    _deviceConnect = true;
                    imgRight.setImageResource(R.drawable.connet_ble);
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
