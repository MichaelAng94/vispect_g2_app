package com.vispect.android.vispect_g2_app.ui.activity;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.StrictMode;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.rey.material.app.BottomSheetDialog;
import com.vispect.android.vispect_g2_app.R;
import com.vispect.android.vispect_g2_app.app.AppConfig;
import com.vispect.android.vispect_g2_app.app.AppContext;
import com.vispect.android.vispect_g2_app.app.AppManager;
import com.vispect.android.vispect_g2_app.bean.ResultData;
import com.vispect.android.vispect_g2_app.controller.AppApi;
import com.vispect.android.vispect_g2_app.controller.UIHelper;
import com.vispect.android.vispect_g2_app.interf.ResultCallback;
import com.vispect.android.vispect_g2_app.ui.fragment.UserInfoFragment;
import com.vispect.android.vispect_g2_app.ui.widget.DialogHelp;
import com.vispect.android.vispect_g2_app.utils.DoubleClickExit;
import com.vispect.android.vispect_g2_app.utils.PermissionUtils;
import com.vispect.android.vispect_g2_app.utils.SDcardTools;
import com.vispect.android.vispect_g2_app.utils.XuFileUtils;
import com.vispect.android.vispect_g2_app.utils.XuLog;
import com.vispect.android.vispect_g2_app.utils.XuNetWorkUtils;
import com.vispect.android.vispect_g2_app.utils.XuToast;

import java.io.File;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import interf.GetG2CameraList;
import interf.GetUDPcamera;
import interf.OnDeviceConnectionStateChange;
import interf.OnWifiOpenListener;
import okhttp3.Request;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.READ_PHONE_STATE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.provider.MediaStore.ACTION_IMAGE_CAPTURE;
import static com.vispect.android.vispect_g2_app.app.AppConfig.IMAGE_USER_AVATAR_NAME;
import static com.vispect.android.vispect_g2_app.app.AppConfig.REQUEST_CODE_CAMERA;
import static com.vispect.android.vispect_g2_app.app.AppConfig.REQUEST_CODE_CAMERA_PERMISSION;
import static com.vispect.android.vispect_g2_app.app.AppConfig.REQUEST_CODE_GLOBAL_PERMISSION;

public class MainActivity extends BaseActivity {

    private final static int ADAS = 0;
    private final static int DSM = 1;
    private final static int SPML = 2;
    private final static int SPMR = 3;
    private static final int MY_CAMERA_REQUEST_CODE = 100;
    public static int udpCamera = -1;   //USP实景设定的镜头
    public static ArrayList<Point> cameras; //获取到的镜头列表
    public static int clickMeun = 0;
    private final int BLE_CONNECT = 406;
    private final int REQUESRST_CONNECT = 100;
    private final int REQUESRST_LIVE = 101;
    @Bind(R.id.drawerlayout)
    DrawerLayout drawerlayout;
    @Bind(R.id.img_connect)
    ImageView imgConnect;
    Thread openliveThread = null;
    private DoubleClickExit doubleclick = new DoubleClickExit(this);
    private String TAG = "MainActivity";
    private Handler myHandler = new Handler();
    private changeDialog changeDialog;
    private Boolean connectRealView = false;
    private int lastConnectionState = 0;
    OnDeviceConnectionStateChange deviceConnectionStateListener = new OnDeviceConnectionStateChange() {
        //TODO 监听蓝牙的连接状态
        @Override
        public void onConnectionStateChange(int i) {
            if (lastConnectionState != i) {
                XuLog.e(TAG, "连接状态发生变化：" + i);
                lastConnectionState = i;
                if (i == 0) {
                    imgConnect.setColorFilter(null);
                    XuToast.show(MainActivity.this, "Disconnect");
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
    private String curWIFISSID = "";
    private int checkTime = 0;
    private String curWIFIPassword = "";
    private BottomSheetDialog bottomInterPasswordDialog;
    private int videoType;   //选择的Video的类型
    private int algoType;    //选择的Video算法的类型

    private Runnable toRealView = new Runnable() {
        @Override
        public void run() {
//            hideProgress();
            DialogHelp.getInstance().hideDialog();
            myHandler.removeCallbacks(changeDialog);
            if (AppContext.getInstance().getDeviceHelper().isConnectedDevice()) {
                UIHelper.showLiveForResult(MainActivity.this, REQUESRST_LIVE, false);
            }
//            AppContext.getInstance().setLocation(180000);
        }
    };

    @Override
    public int getContentResource() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView(View view) {

        if (Build.VERSION.SDK_INT > 22) {
            PermissionUtils.request(this, REQUEST_CODE_GLOBAL_PERMISSION, ACCESS_COARSE_LOCATION, READ_PHONE_STATE, READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE, CAMERA);
        }
        setTitle("G2-ADAS");
        AppContext.getInstance().setNeeedCloseBluetooth(!BluetoothAdapter.getDefaultAdapter().isEnabled());
        //监听连接变化
        AppContext.getInstance().getDeviceHelper().setDeviceConnectStateListener(deviceConnectionStateListener);

        pushUserInfoFragment();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (AppContext.getInstance().getDeviceHelper().isConnectedDevice()) {
            imgConnect.setImageResource(R.mipmap.connet_ble);
            imgConnect.setColorFilter(Color.parseColor("#00CCCC"));
        } else {
            imgConnect.setImageResource(R.drawable.disconnect);
            imgConnect.clearColorFilter();
        }

        if (AppContext.getInstance().getDeviceHelper().isConnectedDevice()) {
            if (AppContext.getInstance().getDeviceHelper().isG2()) {
                AppContext.getInstance().getDeviceHelper().getG2CameraList(new GetG2CameraList() {
                    @Override
                    public void onSuccess(ArrayList arrayList) {
                        cameras = arrayList;
                    }

                    @Override
                    public void onFail() {

                    }
                });

                AppContext.getInstance().getDeviceHelper().getUDPCamera(new GetUDPcamera() {
                    @Override
                    public void onSuccess(int i) {
                        udpCamera = i;
                    }

                    @Override
                    public void onFail() {

                    }
                });
            }
        }

        switch (clickMeun) {
            case 1:
                UIHelper.startActivity(this, InstallActivity.class);
                break;
            case 2:
                UIHelper.startActivity(this, SettingsActivity.class);
                break;
            case 3:
                myHandler.post(new CheckCamera());
                break;
        }

        clickMeun = 0;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
        initPhotoError();
    }

    public void cancelConnect() {
        //断开连接
        myHandler.removeCallbacks(changeDialog);
        XuNetWorkUtils.cancelConnectWIFI();
        AppContext.getInstance().getDeviceHelper().closeDeviceRealViewMode();
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
            if (doubleclick.onKeyDown(keyCode, event)) {
                return super.onKeyDown(keyCode, event);
            } else {
                return false;
            }
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    @OnClick({R.id.img_useredit, R.id.img_connect})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.img_useredit:
                drawerlayout.openDrawer(Gravity.LEFT);
                break;
            case R.id.img_connect:
                if (AppContext.getInstance().getDeviceHelper().isConnectedDevice()) {
                    AppContext.getInstance().getDeviceHelper().canCelLoginDevice();
                    imgConnect.setImageResource(R.drawable.disconnect);
                    imgConnect.clearColorFilter();
                    XuToast.show(this, "Device Disconnected");
                    break;
                }
                UIHelper.startActivity(MainActivity.this, ConnectActivity.class);
                break;
        }
    }

    @OnClick({R.id.menu_user_guide, R.id.menu_installation, R.id.menu_settings, R.id.menu_live, R.id.menu_video})
    public void onItemClicked(View view) {
        switch (view.getId()) {
            case R.id.menu_user_guide:  //user guide的doc界面
                UIHelper.showDoc(this);
                break;
            case R.id.menu_installation: //安装界面
                if (isConnected()) {
                    UIHelper.startActivity(this, InstallActivity.class);
                } else {
                    clickMeun = 1;
                }
                break;
            case R.id.menu_settings: //设置界面
                if (isConnected()) {
                    UIHelper.startActivity(this, SettingsActivity.class);
                } else {
                    clickMeun = 2;
                }
                break;
            case R.id.menu_live:  //实景界面
                if (isConnected()) {
                    myHandler.post(new CheckCamera());
                } else {
                    clickMeun = 3;
                }
                break;
            case R.id.menu_video: //视频界面
                View dialog = getLayoutInflater().inflate(R.layout.select_video_dialog, null);
                bottomInterPasswordDialog = new BottomSheetDialog(MainActivity.this);
                bottomInterPasswordDialog
                        .contentView(dialog)
                        .heightParam(UIHelper.dp2px(MainActivity.this, 205))
                        .inDuration(100)
                        .outDuration(100)
                        .cancelable(true)
                        .show();
                ClickListener clickListener = new ClickListener();

                TextView tv_alarm = dialog.findViewById(R.id.alarm_video);
                TextView tv_driving = dialog.findViewById(R.id.driving_video);
                TextView tv_localvideo = dialog.findViewById(R.id.tv_localvideo);
                TextView tv_cancel = dialog.findViewById(R.id.tv_cancel);

                tv_alarm.setOnClickListener(clickListener);
                tv_driving.setOnClickListener(clickListener);
                tv_localvideo.setOnClickListener(clickListener);

                tv_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        bottomInterPasswordDialog.dismiss();
                    }
                });
                break;
        }
    }

    public boolean isConnected() {   //判断是否已经连接G2的设备
        //  return true;
        if (AppContext.getInstance().getDeviceHelper().isConnectedDevice() && isG2()) {
            return true;
        } else {
            UIHelper.startActivity(MainActivity.this, ConnectActivity.class);
        }
        return false;
    }

    public boolean isG2() {
        if (AppContext.getInstance().getDeviceHelper().isG2()) {
            return true;
        }
        XuToast.show(this, STR(R.string.please_connect_G2));
        return false;
    }

    private void initPhotoError() {
        // android 7.0系统解决拍照的问题
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
            builder.detectFileUriExposure();
        }
    }

    //设备连手机发出的热点
    private void openRealViewNew() {
        AppContext.getInstance().getDeviceHelper().openDeviceRealViewMode(null);
        myHandler.postDelayed(toRealView, 1000);
    }

    //手机连设备WiFi
    private void openRealViewOld() {
        System.out.println();
        AppContext.getInstance().getDeviceHelper().openDeviceRealViewMode(new OnWifiOpenListener() {
            @Override
            public void onSuccess(String wifiName, String password) {
                XuLog.e(TAG, "打开设备wifi成功：" + wifiName + "   " + password);
                curWIFISSID = wifiName;
                curWIFIPassword = password;
                AppContext.getInstance().getCachedThreadPool().execute(new Runnable() {
                    @Override
                    public void run() {
                        if (XuNetWorkUtils.connectWIFI(curWIFISSID, curWIFIPassword)) {
                            //成功连上WIFI  开始进入实时路况
                            if (myHandler != null) {
                                myHandler.postDelayed(toRealView, 1000);
                            }
                        }
                    }
                });
            }

            @Override
            public void onFail(int i) {
                XuLog.e(TAG, "打开设备wifi失败：" + i);
            }

            @Override
            public void onGetSanResult(String s) {

            }
        });
    }

    int openLiveMode() {
        //开启实时路况
        if (!AppContext.getInstance().getDeviceHelper().isConnectedDevice()) {
            XuToast.show(AppContext.getInstance(), STR(R.string.road_live_notconnect));
            return 1;
        }
        if (AppContext.getInstance().isERROR_CAMERA()) {
            XuToast.show(AppContext.getInstance(), STR(R.string.camera_erro));
            return 2;
        }
        changeDialog = new changeDialog();
        connectRealView = true;
        AppContext.getInstance().setCalibrateType(0);
        DialogHelp.getInstance().connectDialog(MainActivity.this, STR(R.string.dialog_tips_connecting), STR(R.string.dialog_tips_connecting2)).setOnKeyListener(new keyListener());
        myHandler.postDelayed(changeDialog, 10000);
        openliveThread = new Thread(new Runnable() {
            @Override
            public void run() {
                switch (AppContext.getInstance().getDeviceHelper().getCommunicationType()) {
                    case 0:
                        openRealViewOld();
                        break;
                    case 1:
                        openRealViewNew();
                        break;
                }
            }
        });

        AppContext.getInstance().getCachedThreadPool().execute(openliveThread);
        return 0;
    }


    public void pushUserInfoFragment() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.drawer, new UserInfoFragment(), UserInfoFragment.class.getSimpleName());
        transaction.addToBackStack(UserInfoFragment.class.getSimpleName()).commit();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_CAMERA_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Intent intentFromCapture = new Intent(ACTION_IMAGE_CAPTURE);
                    // 判断存储卡是否可以用，可用则进行存储
                    if (SDcardTools.hasSdcard()) {
                        intentFromCapture.putExtra(MediaStore.EXTRA_OUTPUT,
                                Uri.fromFile(new File(Environment.getExternalStorageDirectory(), IMAGE_USER_AVATAR_NAME)));
                    }
                    startActivityForResult(intentFromCapture, REQUEST_CODE_CAMERA);
                } else {
                    XuToast.show(this, STR(R.string.allow_camera_permission));
                }
                break;
        }
    }

    class keyListener implements DialogInterface.OnKeyListener {
        //Dialog的返回键监听
        @Override
        public boolean onKey(DialogInterface dialogInterface, int i, KeyEvent keyEvent) {
            if (i == KeyEvent.KEYCODE_BACK) {
                cancelConnect();
                dialogInterface.dismiss();
                return true;
            }
            return false;
        }
    }

    private class CheckCamera implements Runnable {    //获取G2设备的摄像头列表和类型
        @Override
        public void run() {
            if (cameras == null || udpCamera == -1) {
                if (checkTime == 5) {
                    checkTime = 0;
                    XuToast.show(MainActivity.this, "can't use camera");
                    return;
                }
                onResume();
                myHandler.postDelayed(new CheckCamera(), 500);
                checkTime++;
            } else {
                checkTime = 0;
                openLiveMode();
            }
        }
    }

    public class ClickListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            bottomInterPasswordDialog.dismiss();
            View dialog = getLayoutInflater().inflate(R.layout.select_type_dialog, null);
            final BottomSheetDialog bottomInterPasswordDialog = new BottomSheetDialog(MainActivity.this);

            bottomInterPasswordDialog
                    .contentView(dialog)
                    .heightParam(UIHelper.dp2px(MainActivity.this, 255))
                    .inDuration(100)
                    .outDuration(100)
                    .cancelable(true);

            switch (view.getId()) {
                case R.id.alarm_video:
                    if (isConnected()) {
                        videoType = 1;
                        bottomInterPasswordDialog.show();
                    }
                    break;
                case R.id.driving_video:
                    if (isConnected()) {
                        videoType = 0;
                        bottomInterPasswordDialog.show();
                    }
                    break;
                case R.id.tv_localvideo:
                    UIHelper.startActivity(MainActivity.this, LocalVideoSeltActivity.class);
                    break;
            }

            TextView tv_adas = dialog.findViewById(R.id.tv_ADAS);
            TextView tv_dsm = dialog.findViewById(R.id.tv_DSM);
            TextView tv_spml = dialog.findViewById(R.id.tv_SPML);
            TextView tv_spmr = dialog.findViewById(R.id.tv_SPMR);
            TextView tv_cancle = dialog.findViewById(R.id.tv_cancel);
            tv_cancle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    bottomInterPasswordDialog.dismiss();
                }
            });


            typeClick typeClick = new typeClick();

            tv_adas.setOnClickListener(typeClick);
            tv_dsm.setOnClickListener(typeClick);
            tv_spml.setOnClickListener(typeClick);
            tv_spmr.setOnClickListener(typeClick);
        }
    }

    class typeClick implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.tv_ADAS:
                    algoType = ADAS;
                    break;
                case R.id.tv_DSM:
                    algoType = DSM;
                    break;
                case R.id.tv_SPML:
                    algoType = SPML;
                    break;
                case R.id.tv_SPMR:
                    algoType = SPMR;
                    break;
            }
            if (isConnected()) {
                UIHelper.showVideosActivity(MainActivity.this, videoType, algoType);
            }
        }
    }

    public class changeDialog implements Runnable {
        @Override
        public void run() {
            DialogHelp.getInstance().hideDialog();
            DialogHelp.getInstance().connectDialog(MainActivity.this, STR(R.string.dialog_tips_connecting), STR(R.string.wifi_waiting_too_long) + AppConfig.getInstance(MainActivity.this).getWifi_name() + STR(R.string.wifi_waiting_too_long2) + AppConfig.getInstance(MainActivity.this).getWifi_paw()).setOnKeyListener(new keyListener());
        }
    }
}
