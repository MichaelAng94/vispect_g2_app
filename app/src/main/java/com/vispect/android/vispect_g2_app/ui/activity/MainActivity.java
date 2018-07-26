package com.vispect.android.vispect_g2_app.ui.activity;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;

import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.BoolRes;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.rey.material.app.BottomSheetDialog;
import com.vispect.android.vispect_g2_app.R;
import com.vispect.android.vispect_g2_app.app.AppConfig;
import com.vispect.android.vispect_g2_app.app.AppContext;
import com.vispect.android.vispect_g2_app.app.AppManager;
import com.vispect.android.vispect_g2_app.bean.ResultData;
import com.vispect.android.vispect_g2_app.bean.UserInfo;
import com.vispect.android.vispect_g2_app.controller.AppApi;
import com.vispect.android.vispect_g2_app.controller.UIHelper;
import com.vispect.android.vispect_g2_app.interf.DialogClickListener;
import com.vispect.android.vispect_g2_app.interf.OnClickYesOrNoListener;
import com.vispect.android.vispect_g2_app.interf.ResultCallback;
import com.vispect.android.vispect_g2_app.ui.fragment.CalibrateFragment;
import com.vispect.android.vispect_g2_app.ui.widget.DialogHelp;
import com.vispect.android.vispect_g2_app.utils.DoubleClickExit;
import com.vispect.android.vispect_g2_app.utils.SDcardTools;
import com.vispect.android.vispect_g2_app.utils.XuFileUtils;
import com.vispect.android.vispect_g2_app.utils.XuLog;
import com.vispect.android.vispect_g2_app.utils.XuNetWorkUtils;
import com.vispect.android.vispect_g2_app.utils.XuString;
import com.vispect.android.vispect_g2_app.utils.XuToast;

import java.io.File;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import bean.Vispect_SDK_ErrorCode;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import interf.G2DrivingVideoOperationListener;
import interf.GetDiatanceListener;
import interf.GetDsmAlarmSensitivity;
import interf.GetDsmSensitivityLevel;
import interf.GetDsmShieldingListener;
import interf.GetDsmStartSpeed;
import interf.GetG2CameraList;
import interf.GetG2CameraType;
import interf.GetHorizontalLine;
import interf.GetSPMSpeedSpace;
import interf.GetUDPcamera;
import interf.GetVideosTypeListener;
import interf.OnDeviceConnectionStateChange;
import interf.OnWifiOpenListener;
import interf.PointOfAreaListener;
import interf.ResultListner;
import interf.SetPointOfArea;
import okhttp3.Request;

import static java.security.AccessController.getContext;

public class MainActivity extends BaseActivity {

    @Bind(R.id.drawerlayout)
    DrawerLayout drawerlayout;
    @Bind(R.id.img_connect)
    ImageView imgConnect;
    @Bind(R.id.img_head)
    ImageView imgHead;
    @Bind(R.id.tv_username)
    TextView tvUsername;
    @Bind(R.id.tv_tel)
    TextView tvTel;
    @Bind(R.id.tv_email)
    TextView tvEmail;
    @Bind(R.id.tv_sex)
    TextView tvSex;
    @Bind(R.id.tv_appVersion)
    TextView tvAppVersion;
    private DoubleClickExit doubleclick = new DoubleClickExit(this);
    private String TAG = "MainActivity";
    private final int BLE_CONNECT = 406;
    private final int REQUESRST_CONNECT = 100;
    private final int REQUESRST_LIVE = 101;
    private static final int REQUEST_FINE_LOCATION = 0;
    private Handler myHandler = new Handler();
    /*头像名称*/
    private static final String IMAGE_FILE_NAME = "/user_face.jpg";
    /* 请求码*/
    private static final int IMAGE_REQUEST_CODE = 0;
    private static final int CAMERA_REQUEST_CODE = 1;
    private static final int RESULT_REQUEST_CODE = 2;
    private static final int RESULT_PHONE_CODE = 3;
    private static final int RESULT_EMAIL_CODE = 4;
    Thread openliveThread = null;
    private changeDialog changeDialog;
    private Boolean connectRealView = false;
    private int lastConnectionState = 0;
    private String curWIFISSID = "";
    private String curWIFIPassword = "";
    private BottomSheetDialog bottomInterPasswordDialog;

    @Override
    public int getContentResource() {
        return R.layout.activity_main;
    }


    @Override
    protected void initView(View view) {

        try {
            tvAppVersion.setText(getPackageManager().getPackageInfo(getPackageName(), 0).versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        myHandler.post(setuerinfo);
        mayRequestLocation();
        setTitle("G2 ADAS");
        AppContext.getInstance().setNeeedCloseBluetooth(!BluetoothAdapter.getDefaultAdapter().isEnabled());

        //监听连接变化
        AppContext.getInstance().getDeviceHelper().setDeviceConnectStateListener(deviceConnectionStateListener);
    }


    Runnable setuerinfo = new Runnable() {
        @Override
        public void run() {

            String uid = AppConfig.getInstance(MainActivity.this).getUserId();
            if (XuString.isEmpty(uid)) {
                return;
            }
            AppApi.getUserInfo(Integer.parseInt(uid), new ResultCallback<ResultData<UserInfo>>() {
                @Override
                public void onFailure(Request request, Exception e) {
                    XuLog.d(TAG, "获取用户信息发生错误：" + e.getMessage());
//					XuToast.show(AppContext.getInstance(), STR(R.string.mine_get_user_info_fail) + e.getMessage());
                }

                @Override
                public void onResponse(ResultData<UserInfo> response) {
                    if (response.getResult() == 0) {
                        AppContext.getInstance().setUser(response.getMsg());
                        ImageLoader.getInstance().displayImage(AppContext.getInstance().getUser().getAvatar(), imgHead);
                        tvEmail.setText(AppContext.getInstance().getUser().getEmail());
                        tvTel.setText(AppContext.getInstance().getUser().getPhone());
                        tvUsername.setText(AppContext.getInstance().getUser().getName());
                        if (AppContext.getInstance().getUser().getSex() == 1) {
                            tvSex.setText("Female");
                        } else {
                            tvSex.setText("Male");
                        }
                    }
                }
            });

        }
    };

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
    }

    private void mayRequestLocation() {
        //TODO 在6.0及以上的系统中动态请求一些敏感权限
        if (Build.VERSION.SDK_INT >= 18) {
            //请求蓝牙权限
            requestPermission(Manifest.permission.ACCESS_COARSE_LOCATION);
            //请求读取手机状态的权限
            requestPermission(Manifest.permission.READ_PHONE_STATE);
            //请求读取SD卡的权限
            requestPermission(Manifest.permission.READ_EXTERNAL_STORAGE);
            //请求写入SD卡的权限
            requestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }

    }

    synchronized private void requestPermission(String permission) {
        //TODO 向用户请求权限
        int checkCallPhonePermission = ContextCompat.checkSelfPermission(MainActivity.this, permission);
        if (checkCallPhonePermission != PackageManager.PERMISSION_GRANTED) {
            //判断是否需要 向用户解释，为什么要申请该权限
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission))
                XuToast.show(MainActivity.this, "request necessary permissions of app");
            ActivityCompat.requestPermissions(this, new String[]{permission}, REQUEST_FINE_LOCATION);
            return;
        } else {

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }

    OnDeviceConnectionStateChange deviceConnectionStateListener = new OnDeviceConnectionStateChange() {
        @Override
        public void onConnectionStateChange(int i) {
            if (lastConnectionState != i) {
                XuLog.e(TAG, "连接状态发生变化：" + i);
                lastConnectionState = i;
                if (i == 0) {
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

    public void cancelConnect() {
        myHandler.removeCallbacks(changeDialog);
        XuNetWorkUtils.cancelConnectWIFI();
        AppContext.getInstance().getDeviceHelper().closeDeviceRealViewMode();
    }

    class keyListener implements DialogInterface.OnKeyListener {

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
                }
                UIHelper.startActivity(MainActivity.this, ConnectActivity.class);
                break;
        }
    }

    @OnClick({R.id.menu_user_guide, R.id.menu_installation, R.id.menu_settings, R.id.menu_live, R.id.menu_video})

    public void onItemClicked(View view) {
        switch (view.getId()) {
            case R.id.menu_user_guide:
                UIHelper.showDoc(this);

//                AppContext.getInstance().getDeviceHelper().getG2RecordVideoList(1, 1, 2, new G2DrivingVideoOperationListener() {
//                    @Override
//                    public void onGetVideoList(ArrayList arrayList) {
//                        if (arrayList!=null){
//                            XuLog.e("islast"+arrayList.size());
//                        }
//                    }
//
//                    @Override
//                    public void onLockOrUnlockResult(boolean b) {
//
//                    }
//
//                    @Override
//                    public void onLast() {
//                        XuLog.e("islast");
//                    }
//                });
//
//                AppContext.getInstance().getDeviceHelper().getG2AlarmVideoList(1, 1, 2, new G2DrivingVideoOperationListener() {
//                    @Override
//                    public void onGetVideoList(ArrayList arrayList) {
//                        if (arrayList!=null){
//                            XuLog.e("islast"+arrayList.size());
//                        }
//                    }
//
//                    @Override
//                    public void onLockOrUnlockResult(boolean b) {
//
//                    }
//
//                    @Override
//                    public void onLast() {
//                        XuLog.e("islast");
//                    }
//                });
//                break;
            case R.id.menu_installation:
                if (isConnected()) {
                    UIHelper.startActivity(this, InstallActivity.class);
                }
                break;
            case R.id.menu_settings:
                if (isConnected()) {
                    UIHelper.startActivity(this, SettingsActivity.class);
                }
                break;
            case R.id.menu_live:
                if (isConnected()) {
                    openlivemode();
                }
                break;
            case R.id.menu_video:
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

    public boolean isConnected() {
        return true;
//        if (AppContext.getInstance().getDeviceHelper().isConnectedDevice()) {
//            if (isG2()) {
//                return true;
//            }
//        } else {
//            UIHelper.startActivity(MainActivity.this, ConnectActivity.class);
//        }
//        return false;
    }

    public boolean isG2() {
        if (AppContext.getInstance().getDeviceHelper().isG2()) {
            return true;
        }
        XuToast.show(this, STR(R.string.please_connect_G2));
        return false;
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
                    bottomInterPasswordDialog.show();
                    break;
                case R.id.driving_video:
                    bottomInterPasswordDialog.show();
                    break;
                case R.id.tv_localvideo:
                    break;
            }

            TextView tv_adas = dialog.findViewById(R.id.tv_ADAS);
            TextView tv_dsm = dialog.findViewById(R.id.tv_DSM);
            TextView tv_spml = dialog.findViewById(R.id.tv_SPML);
            TextView tv_spmr = dialog.findViewById(R.id.tv_SPMR);

            tv_adas.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    UIHelper.startActivity(MainActivity.this, ADASWarringVideosActivity.class);
                }
            });

        }
    }


    @OnClick(R.id.sign_out)
    public void onViewClicked() {
        UIHelper.showAsk(MainActivity.this, STR(R.string.ask_out_login), false, new OnClickYesOrNoListener() {
            @Override
            public void isyes(boolean var1, DialogInterface dialog) {
                if (var1) {
                    AppConfig.getInstance(MainActivity.this).setFirstStart(true);
                    UIHelper.startActivity(MainActivity.this, LoginActivity.class);
                }
                dialog.dismiss();
            }
        });
    }

    private Runnable savefail = new Runnable() {
        @Override
        public void run() {
            XuToast.show(MainActivity.this, "Save fail");
        }
    };
    private Runnable savesuccess = new Runnable() {
        @Override
        public void run() {
            myHandler.post(setuerinfo);
            DialogHelp.getInstance().hideDialog();
            DialogHelp.getInstance().hideDialog();
            XuToast.show(MainActivity.this, "Success");
        }
    };

    @OnClick({R.id.tv_tel, R.id.tv_email, R.id.tv_sex, R.id.img_head})
    public void oneditClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_tel:
                DialogHelp.getInstance().editDialog(this, "Phone", new DialogClickListener() {
                    @Override
                    public void clickYes(String editText) {
                        if (XuString.isEmpty(editText) || !isNumeric(editText)) {
                            myHandler.post(legalvalue);
                            return;
                        }
                        editUserInfo(AppContext.getInstance().getUser().getSex(), editText, AppContext.getInstance().getUser().getEmail());
                    }
                });
                break;
            case R.id.img_head:
                View dialog = getLayoutInflater().inflate(R.layout.select_avatar_dialog, null);
                final BottomSheetDialog bottomInterPasswordDialog = new BottomSheetDialog(MainActivity.this);
                bottomInterPasswordDialog
                        .contentView(dialog)
                        .heightParam(UIHelper.dp2px(MainActivity.this, 205))
                        .inDuration(100)
                        .outDuration(100)
                        .cancelable(true)
                        .show();
                TextView tvTakePhoto = bottomInterPasswordDialog.findViewById(R.id.tv_takePhoto);
                TextView tvChoosePhoto = bottomInterPasswordDialog.findViewById(R.id.tv_choosePhotos);
                TextView tvCancle = bottomInterPasswordDialog.findViewById(R.id.tv_cancel);
                tvCancle.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        bottomInterPasswordDialog.dismiss();
                    }
                });
                tvTakePhoto.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intentFromCapture = new Intent(
                                MediaStore.ACTION_IMAGE_CAPTURE);
                        // 判断存储卡是否可以用，可用则进行存储
                        if (SDcardTools.hasSdcard()) {
                            intentFromCapture.putExtra(
                                    MediaStore.EXTRA_OUTPUT,
                                    Uri.fromFile(new File(
                                            Environment
                                                    .getExternalStorageDirectory(),
                                            IMAGE_FILE_NAME)));
                        }
                        startActivityForResult(intentFromCapture,
                                CAMERA_REQUEST_CODE);
                        bottomInterPasswordDialog.dismiss();
                    }
                });
                tvChoosePhoto.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent galleryIntent = new Intent(
                                Intent.ACTION_GET_CONTENT);
                        galleryIntent
                                .addCategory(Intent.CATEGORY_OPENABLE);
                        galleryIntent.setType("image/*");
                        startActivityForResult(galleryIntent,
                                IMAGE_REQUEST_CODE);
                        bottomInterPasswordDialog.dismiss();
                    }
                });
                break;
            case R.id.tv_email:
                DialogHelp.getInstance().editDialog(this, "Email", new DialogClickListener() {
                    @Override
                    public void clickYes(String editText) {
                        if (XuString.isEmpty(editText)) {
                            myHandler.post(legalvalue);
                            return;
                        }
                        editUserInfo(AppContext.getInstance().getUser().getSex(), AppContext.getInstance().getUser().getPhone(), editText);
                    }
                });
                break;
            case R.id.tv_sex:
                DialogHelp.getInstance().sexDialog(this, new DialogClickListener() {
                    @Override
                    public void clickYes(String editText) {
                        int sex = Integer.parseInt(editText);
                        if (sex == 1 || sex == 0) {
                            editUserInfo(sex, AppContext.getInstance().getUser().getPhone(), AppContext.getInstance().getUser().getEmail());
                        }
                    }
                });
                break;
        }
    }

    private void editUserInfo(int sex, String tel, String email) {
        if ((sex != 0 && sex != 1) || (tel.length() < 7 || tel.length() > 20) || (email.length() < 3 || email.length() > 50)) {
            myHandler.post(legalvalue);
            return;
        }

        DialogHelp.getInstance().connectDialog(MainActivity.this, STR(R.string.updateing));

        AppApi.modifyUserInfo(Integer.parseInt(AppConfig.getInstance(AppContext.getInstance()).getUserId()), sex, tel, email, new ResultCallback<ResultData<String>>() {
            @Override
            public void onFailure(Request request, Exception e) {
                myHandler.post(savefail);
                DialogHelp.getInstance().hideDialog();
            }

            @Override
            public void onResponse(ResultData<String> response) {
                if (response.getResult() == 0) {
                    myHandler.post(savesuccess);
                } else {
                    myHandler.post(savefail);
                }
                DialogHelp.getInstance().hideDialog();
            }
        });
    }

    private void openRealViewNew() {
        AppContext.getInstance().getDeviceHelper().openDeviceRealViewMode(null);
        myHandler.postDelayed(toRealView, 1000);
    }

    private void openRealViewOld() {
        AppContext.getInstance().getDeviceHelper().openDeviceRealViewMode(new OnWifiOpenListener() {
            @Override
            public void onSuccess(String wifiName, String password) {
                XuLog.e(TAG, "打开设备wifi成功：" + wifiName + "   " + password);
                curWIFISSID = wifiName;
                curWIFIPassword = password;
                if (XuNetWorkUtils.connectWIFI(curWIFISSID, curWIFIPassword)) {
                    //成功连上WIFI  开始进入实时路况
                    if (myHandler != null) {
                        myHandler.postDelayed(toRealView, 1000);
                    }
                }
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

    int openlivemode() {
        //TODO 开启实时路况
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
        DialogHelp.getInstance().connectDialog(MainActivity.this, "连接中", "正在连接设备").setOnKeyListener(new keyListener());
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

    public class changeDialog implements Runnable {
        @Override
        public void run() {
            DialogHelp.getInstance().hideDialog();
            DialogHelp.getInstance().connectDialog(MainActivity.this, "连接中", "请手动接入WIFI 设备名称:" + AppConfig.getInstance(MainActivity.this).getWifi_name() + "密码:" + AppConfig.getInstance(MainActivity.this).getWifi_paw()).setOnKeyListener(new keyListener());
        }
    }


//    private void openRealViewNew(){
//        AppContext.getInstance().getDeviceHelper().openDeviceRealViewMode(null);
//        myHandler.postDelayed(toRealView, 1000);
//    }
//
//    private void openRealViewOld(){
//        //先打开设备的wifi
//        AppContext.getInstance().getDeviceHelper().openDeviceRealViewMode(new OnWifiOpenListener() {
//            @Override
//            public void onSuccess(final String name, final String password) {
//                //打开成功  开始连接特定wifi
//                AppContext.getInstance().getCachedThreadPool().execute(new Runnable() {
//                    @Override
//                    public void run() {
//                        //设置超时断开
//                        if (XuNetWorkUtils.connectWIFI(name, password)) {
//                            //成功连上WIFI  开始进入实时路况
//                            myHandler.postDelayed(toRealView, 1000);
//                        }else{
//                            XuLog.e(TAG,"连接设备的WIFI失败");
//                        }
//                    }
//                });
//
//
//            }
//
//            @Override
//            public void onFail(final int i) {
//                //1.5秒后获取开关的滑动
//                XuLog.e("open wifi fail：" + i);
//            }
//
//            @Override
//            public void onGetSanResult(String s) {
//
//            }
//        });
//    }

    private Runnable toRealView = new Runnable() {
        @Override
        public void run() {
//            hideProgress();
            if (AppContext.getInstance().getDeviceHelper().isConnectedDevice()) {
                DialogHelp.getInstance().hideDialog();
                UIHelper.showLiveForResult(MainActivity.this, REQUESRST_LIVE, false);
            }
//            AppContext.getInstance().setLocation(180000);
        }
    };

    private Runnable legalvalue = new Runnable() {
        @Override
        public void run() {
            XuToast.show(MainActivity.this, STR(R.string.edit_input_legal_value));
        }
    };

    private Runnable hideDialog = new Runnable() {
        @Override
        public void run() {
            DialogHelp.getInstance().hideDialog();
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case RESULT_REQUEST_CODE:
                    if (data != null) {
                        getImageToView(data);
                    }
                    break;
                case IMAGE_REQUEST_CODE:
                    Uri selectedImage = data.getData();
                    startPhotoZoom(Uri.parse(getPath(this, selectedImage)));
                    break;
                case CAMERA_REQUEST_CODE:
                    if (SDcardTools.hasSdcard()) {
                        File tempFile = new File(
                                Environment.getExternalStorageDirectory()
                                        + IMAGE_FILE_NAME);
                        startPhotoZoom(Uri.fromFile(tempFile));
                    } else {
                        XuToast.show(AppContext.getInstance(), "hava not sd card");
                    }

                    break;

            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    //利用正则判断是否为数字
    public boolean isNumeric(String str) {
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(str);
        if (!isNum.matches()) {
            return false;
        }
        return true;
    }

    /**
     * 裁剪图片方法实现
     *
     * @param uri
     */
    public void startPhotoZoom(Uri uri) {

        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        // 设置裁剪
        intent.putExtra("crop", "true");

           /* //裁剪区域设置为圆形
            intent.putExtra("circleCrop",true);*/

        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 150);
        intent.putExtra("outputY", 150);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, RESULT_REQUEST_CODE);
    }

    /**
     * 保存裁剪之后的图片数据
     *
     * @param data
     */
    private void getImageToView(Intent data) {
        Bundle extras = data.getExtras();
        if (extras != null) {
            Bitmap photo = extras.getParcelable("data");
            Drawable drawable = new BitmapDrawable(photo);
            imgHead.setImageDrawable(drawable);
            XuFileUtils.saveBitmap(photo, AppConfig.EXTERNAL_DIR + "/" + "/user_face.png", 100);
            AppApi.updateAvatar(AppContext.getInstance().getUser().getId(), AppConfig.EXTERNAL_DIR + "/" + "/user_face.png", new ResultCallback<ResultData<String>>() {
                @Override
                public void onFailure(Request request, Exception e) {
                    XuLog.d(TAG, "上传失败:" + e.getMessage());
                }

                @Override
                public void onResponse(ResultData<String> response) {
                    XuLog.d(TAG, "上传成功" + response.getMsg().toString());
                }
            });


        }
    }

    /**
     * 设配安卓4.4的图片地址，获取真实地址
     */
    public static String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // MediaProvider
            if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{
                        split[1]
                };
                return "file://" + getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        return uri.toString();
    }

    /**
     * 判断图片路径是否是安卓4.4版本的路径
     *
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {
        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

}
