package com.vispect.android.vispect_g2_app.ui.activity;

import android.content.Intent;
import android.net.wifi.WifiConfiguration;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;


import com.vispect.android.vispect_g2_app.R;
import com.vispect.android.vispect_g2_app.app.AppConfig;
import com.vispect.android.vispect_g2_app.app.AppContext;
import com.vispect.android.vispect_g2_app.controller.UIHelper;
import com.vispect.android.vispect_g2_app.utils.CheckTime;
import com.vispect.android.vispect_g2_app.utils.XuLog;
import com.vispect.android.vispect_g2_app.utils.XuNetWorkUtils;
import com.vispect.android.vispect_g2_app.utils.XuToast;

import bean.GPSData_V;
import butterknife.Bind;
import butterknife.OnClick;
import interf.OnWifiOpenListener;
import interf.onGet485StatusCallback;
import interf.onGetCameraStatusCallback;
import interf.onGetCellVoltageCallback;
import interf.onGetGPS_VCallback;
import interf.onGetSenserCallback;
import interf.onGetTCardStatusCallback;
import interf.onGetTemperatureCallback;


/**
 * 设备硬件检测
 *
 * Created by xu on 2017/9/13.
 */
public class DeviceTestingActivity extends BaseActivity {
    private final static String TAG = "DeviceTestingActivity";


    @Bind(R.id.iv_titlebar_title)
    TextView title;

    @Bind(R.id.tv_device_testing_model_tf_card_status)
    TextView tv_device_testing_model_tf_card_status;
    @Bind(R.id.tv_device_testing_model_battery)
    TextView tv_device_testing_model_battery;
    @Bind(R.id.tv_device_testing_model_camera_status)
    TextView tv_device_testing_model_camera_status;
    @Bind(R.id.tv_device_testing_model_gps)
    TextView tv_device_testing_model_gps;
    @Bind(R.id.tv_device_testing_model_senser)
    TextView tv_device_testing_model_senser;
    @Bind(R.id.tv_device_testing_model_485_status)
    TextView tv_device_testing_model_458_status;
    @Bind(R.id.tv_device_testing_model_cpu_temperature)
    TextView tv_device_testing_model_cpu_temperature;
    @Bind(R.id.tv_device_testing_model_battery_temperature)
    TextView tv_device_testing_model_battery_temperature;
    @Bind(R.id.tv_device_testing_model_wifi_rssi)
    TextView tv_device_testing_model_wifi_rssi;
    @Bind(R.id.tv_device_testing_model_ble_rssi)
    TextView tv_device_testing_model_ble_rssi;


    Handler mHandler = new Handler();

    private String TCardStatus;
    Runnable setTCardStatus = new Runnable() {
        @Override
        public void run() {
            tv_device_testing_model_tf_card_status.setText(TCardStatus);
        }
    };

    private float battery = 0;
    Runnable setBattery = new Runnable() {
        @Override
        public void run() {
            if(battery < 0){
                tv_device_testing_model_battery.setText(STR(R.string.engineering6));
            }else{
                tv_device_testing_model_battery.setText(battery + "v");
            }

        }
    };
    private String statu_485;
    Runnable set485Statu = new Runnable() {
        @Override
        public void run() {
            tv_device_testing_model_458_status.setText(statu_485);
        }
    };
    private String cameraStatus;
    Runnable setCameraStatus = new Runnable() {
        @Override
        public void run() {
            tv_device_testing_model_camera_status.setText(cameraStatus);
        }
    };
    private float cpuTemperature =  -1f;
    Runnable setCPUTemperature = new Runnable() {
        @Override
        public void run() {
            tv_device_testing_model_cpu_temperature.setText(cpuTemperature + "℃");
        }
    };
    private float batteryTemperature = -1f;
    Runnable setBatteryTemperature = new Runnable() {
        @Override
        public void run() {
            if(batteryTemperature < 0){
                tv_device_testing_model_battery_temperature.setText(STR(R.string.engineering6));
            }else{
                tv_device_testing_model_battery_temperature.setText(batteryTemperature + "℃");
            }

        }
    };
    private String gpsStatus;
    Runnable setgpsStatus = new Runnable() {
        @Override
        public void run() {
            tv_device_testing_model_gps.setText(gpsStatus);
        }
    };

    private String senserStatus;
    Runnable setSenserStatus = new Runnable() {
        @Override
        public void run() {
            tv_device_testing_model_senser.setText(senserStatus);
        }
    };

    private String curWIFISSID = "";
    private String curWIFIPassword = "";
    private int wifiRssi;
    Runnable setWifiRssi = new Runnable() {
        @Override
        public void run() {
            tv_device_testing_model_wifi_rssi.setText(wifiRssi + "");
        }
    };

    Runnable setBleRssi = new Runnable() {
        @Override
        public void run() {
            tv_device_testing_model_ble_rssi.setText(AppContext.getInstance().getBleRssi() + "");
        }
    };

    private boolean canGetData = false;
    @Override
    public int getContentResource() {
        return R.layout.activity_device_testing;
    }


    @Override
    protected void initView(View view) {
        title.setText(STR(R.string.device_testing_model_title));
        canGetData = true;
        if (AppContext.getInstance().getDeviceHelper().isConnectedDevice()) {
            AppContext.getInstance().getCachedThreadPool().execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        AppContext.getInstance().getDeviceHelper().openTestModel();
                        AppContext.getInstance().getDeviceHelper().openSenser();

                        Thread.sleep(500);

                        if(AppContext.getInstance().getDeviceHelper().getCommunicationType() == 1){
                            curWIFISSID = AppContext.getInstance().getWificontroller().getWIFI_HOST_SSID();
                            curWIFIPassword = AppContext.getInstance().getWificontroller().getWIFI_HOST_PRESHARED_KEY();

                        }else{
                            AppContext.getInstance().getDeviceHelper().openDeviceRealViewMode(new OnWifiOpenListener() {
                                @Override
                                public void onSuccess(String wifiName, String password) {
                                    XuLog.e(TAG, "打开设备wifi成功：" + wifiName + "   " + password);
                                    curWIFISSID = wifiName;
                                    curWIFIPassword = password;
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


                        AppContext.getInstance().getWificontroller().startScan();
                        while (canGetData){
                            getAllData();
                            Thread.sleep(5*1000);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            findViewById(R.id.rl_device_testing_model_gps).setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                   // UIHelper.showGPSInfo(DeviceTestingActivity.this);
                    return false;
                }
            });


        }

    }


    private void getAllData() throws InterruptedException {
        getCPUTemperature();
        Thread.sleep(300);
        getTCardStatus();
        Thread.sleep(300);
        getBattery();
        Thread.sleep(300);
        get485Status();
        Thread.sleep(300);
        getCameraStatus();
        Thread.sleep(300);
        getBLERssi();
        Thread.sleep(300);
        getWIFIRssi();
        Thread.sleep(300);
        getSenserData();
        Thread.sleep(300);
        getGPSStatus();
        Thread.sleep(300);
        getBatteryTemperature();

    }


    @OnClick({R.id.rl_device_testing_model_tf_card_status, R.id.rl_device_testing_model_battery, R.id.rl_device_testing_model_485_status, R.id.rl_device_testing_model_camera_status, R.id.rl_device_testing_model_cpu_temperature,R.id.rl_device_testing_model_battery_temperature, R.id.rl_device_testing_model_gps, R.id.rl_device_testing_model_senser, R.id.rl_device_testing_model_wifi_rssi, R.id.rl_device_testing_model_ble_rssi, R.id.rl_device_testing_model_to_real_view})
    void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_device_testing_model_tf_card_status:
                getTCardStatus();
                break;
            case R.id.rl_device_testing_model_battery:
                getBattery();
                break;
            case R.id.rl_device_testing_model_485_status:
                get485Status();
                break;
            case R.id.rl_device_testing_model_camera_status:
                getCameraStatus();
                break;
            case R.id.rl_device_testing_model_cpu_temperature:
                getCPUTemperature();
                break;
            case R.id.rl_device_testing_model_battery_temperature:
                getBatteryTemperature();
                break;
            case R.id.rl_device_testing_model_gps:
                getGPSStatus();
                break;
            case R.id.rl_device_testing_model_senser:
                getSenserData();
                break;
            case R.id.rl_device_testing_model_wifi_rssi:
                getWIFIRssi();
                break;
            case R.id.rl_device_testing_model_ble_rssi:
                getBLERssi();
                break;
            case R.id.rl_device_testing_model_to_real_view:
                openlivemode();
                break;

        }

    }


    Thread openliveThread = null;
    private final int REQUESRST_LIVE = 101;
    private boolean canshowlongtime = false;
    private Runnable mRunnable_toas_waiitingtoolong = new Runnable() {
        @Override
        public void run() {
            //时间过长的话就提示让用户手动连
            if (canshowlongtime) {
                //showProgress(true, STR(R.string.wifi_waiting_too_long) + AppConfig.getInstance(AppContext.getInstance()).getWifi_name() + STR(R.string.wifi_waiting_too_long2) + AppConfig.getInstance(AppContext.getInstance()).getWifi_paw());
            }
        }
    };
    private Runnable toRealView = new Runnable() {
        @Override
        public void run() {
          //  hideProgress();
            UIHelper.showLiveForResult(DeviceTestingActivity.this, REQUESRST_LIVE, false);
//            AppContext.getInstance().setLocation(180000);
        }
    };

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
       // showProgress(R.string.to_rel_view_loading_title);
        //先移除掉可能存在的关闭/断开WIFI的指令
        CheckTime.removeHnadler();
        //设置时间过长的提示
        if(mHandler != null) {
            mHandler.postDelayed(mRunnable_toas_waiitingtoolong, 20000);
        }
        canshowlongtime = true;
        openliveThread = new Thread(new Runnable() {
            @Override
            public void run() {

                AppContext.getInstance().getCachedThreadPool().execute(new Runnable() {
                    @Override
                    public void run() {
                        switch (AppContext.getInstance().getDeviceHelper().getCommunicationType()){
                            case 0:
                                openRealViewOld();
                                break;
                            case 1:
                                openRealViewNew();
                                break;

                        }

                    }
                });
            }
        });


        AppContext.getInstance().getCachedThreadPool().execute(openliveThread);


        return 0;
    }
    private void openRealViewNew(){
        AppContext.getInstance().getDeviceHelper().openDeviceRealViewMode(null);
        mHandler.postDelayed(toRealView, 1000);
    }

    private void openRealViewOld(){
        if (XuNetWorkUtils.connectWIFI(curWIFISSID, curWIFIPassword)) {
            //成功连上WIFI  开始进入实时路况
            canshowlongtime = false;
            if(mHandler != null) {
                mHandler.postDelayed(toRealView, 1000);
            }
        }
    }


    private void toCancelRealTime() {
        XuLog.d(TAG, "取消连接WIFI");
        mHandler.removeCallbacksAndMessages(null);
    }


    private void getBLERssi() {
        if(mHandler != null) {
            mHandler.post(setBleRssi);
        }
    }

    private void getWIFIRssi() {
        if (!curWIFISSID.isEmpty()) {
            wifiRssi = AppContext.getInstance().getWificontroller().getWIFIRssi("\"" + curWIFISSID + "\"");
            if(mHandler != null){
                if(mHandler != null){
                mHandler.post(setWifiRssi);
                }
            }

            AppContext.getInstance().getWificontroller().startScan();
        }

    }

    private void getSenserData() {

        AppContext.getInstance().getCachedThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    AppContext.getInstance().getDeviceHelper().getSenserData(new onGetSenserCallback() {
                        @Override
                        public void onSuccess(float v, float v1, float v2) {
                            XuLog.e(TAG, "获取Senser数据成功：" + v + "   " + v1 + "   " + v2);
                            if (Math.abs(v-0) > 0 || Math.abs(v1-0) > 0 || Math.abs(v2-0) > 0) {
                                senserStatus = STR(R.string.device_testing_model_normal)+ ": " + v + "  " + "  " + v1 + "  " + v2;
                            } else {
                                senserStatus = STR(R.string.device_testing_model_error) + ": " + v + "  " + "  " + v1 + "  " + v2;
                            }
                            if (mHandler != null) {
                                mHandler.post(setSenserStatus);
                            }
                        }

                        @Override
                        public void onFail(int i) {
                            XuLog.e(TAG, "获取Senser数据失败：" + i);
                            senserStatus = STR(R.string.device_testing_model_error) + ": " + i;
                            if (mHandler != null) {
                                mHandler.post(setSenserStatus);
                            }
                        }
                    });


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });


    }

    private void getGPSStatus() {
        AppContext.getInstance().getDeviceHelper().getGPSData(new onGetGPS_VCallback() {
            @Override
            public void onSuccess(GPSData_V gpsData_v) {
                XuLog.e(TAG, "获取GPS数据成功：" + gpsData_v.toString());
                if (gpsData_v != null && gpsData_v.getLoaded() == 1) {
                    gpsStatus = "lon:" + gpsData_v.getLongitude() + "  lat:" + gpsData_v.getLatitude();
                } else {
                    gpsStatus = STR(R.string.device_testing_model_error);
                }
                if (mHandler != null) {
                    mHandler.post(setgpsStatus);
                }
            }

            @Override
            public void onFail(int i) {
                XuLog.e(TAG, "获取GPS数据失败：" + i);
                gpsStatus = STR(R.string.device_testing_model_error) + ": " + i;
                if (mHandler != null) {
                    mHandler.post(setgpsStatus);
                }
            }
        });
    }

    private void getCPUTemperature() {
        AppContext.getInstance().getDeviceHelper().getCPUTemperature(new onGetTemperatureCallback() {
            @Override
            public void onSuccess(float v) {
                XuLog.e(TAG, "获取CPU温度成功：" + v);
                cpuTemperature = v;
                if (mHandler != null) {
                    mHandler.post(setCPUTemperature);
                }
            }

            @Override
            public void onFail(int i) {
                XuLog.e(TAG, "获取CPU温度失败：" + i);
            }
        });
    }

    private void getBatteryTemperature() {
        AppContext.getInstance().getDeviceHelper().getBatteryTemperature(new onGetTemperatureCallback() {
            @Override
            public void onSuccess(float v) {
                XuLog.e(TAG, "获取电池温度成功：" + v);
                batteryTemperature = v;
                if (mHandler != null) {
                    mHandler.post(setBatteryTemperature);
                }
            }

            @Override
            public void onFail(int i) {
                XuLog.e(TAG, "获取电池温度失败：" + i);
            }
        });
    }

    private void getCameraStatus() {
        AppContext.getInstance().getDeviceHelper().getCameraStatus(new onGetCameraStatusCallback() {
            @Override
            public void onSuccess(int i) {
                XuLog.e(TAG, "获取相机状态成功：" + i);
                if (i == 0) {
                    cameraStatus = STR(R.string.device_testing_model_normal);
                } else {
                    cameraStatus = STR(R.string.device_testing_model_error) + ": " + i;
                }
                if (mHandler != null) {
                    mHandler.post(setCameraStatus);
                }
            }

            @Override
            public void onFail(int i) {
                XuLog.e(TAG, "获取相机状态失败：" + i);
            }
        });

    }

    private void get485Status() {
        AppContext.getInstance().getDeviceHelper().get485Status(new onGet485StatusCallback() {
            @Override
            public void onSuccess(int i) {
                XuLog.e(TAG, "获取485通讯状态成功：" + i);
                switch (i) {
                    case 0:
                        statu_485 = "null";
                        break;
                    case 1:
                        statu_485 = "Buzzer";
                        break;
                    case 2:
                        statu_485 = "OBD";
                        break;
                    case 3:
                        statu_485 = "Buzzer OBD";
                        break;
                    default:
                        statu_485 = i + "";
                        break;
                }

                if (mHandler != null) {
                    mHandler.post(set485Statu);
                }
            }

            @Override
            public void onFail(int i) {
                XuLog.e(TAG, "获取485通讯状态失败：" + i);
            }
        });
    }

    private void getBattery() {
        AppContext.getInstance().getDeviceHelper().getCellVoltage(new onGetCellVoltageCallback() {
            @Override
            public void onSuccess(int i) {
                XuLog.e(TAG, "获取电池电压成功：" + i);
                battery = (float) i / 1000f;
                if(mHandler != null){
                mHandler.post(setBattery);
                }

            }

            @Override
            public void onFail(int i) {
                XuLog.e(TAG, "获取电池电压失败：" + i);
            }
        });
    }

    private void getTCardStatus() {
        AppContext.getInstance().getDeviceHelper().getTCardStatus(new onGetTCardStatusCallback() {
            @Override
            public void onSuccess(int i) {
                XuLog.e(TAG, "获取T卡状态成功：" + i);
                switch (i) {
                    case 0:
                        TCardStatus = "unknow";
                        break;
                    case 1:
                        TCardStatus = STR(R.string.device_testing_model_normal);
                        break;
                    case 2:
                        TCardStatus = STR(R.string.device_testing_model_tf_card_inserted);
                        break;
                    case 3:
                        TCardStatus = STR(R.string.device_testing_model_tf_card_not_loaded);
                        break;
                    case 4:
                        TCardStatus = STR(R.string.device_testing_model_tf_card_block);
                        break;

                    default:
                        TCardStatus = i + "";
                        break;
                }
                if(mHandler != null){
                mHandler.post(setTCardStatus);
                }
            }

            @Override
            public void onFail(int i) {
                XuLog.e(TAG, "获取T卡状态失败：" + i);
            }
        });
    }


    @Override
    public void onActivityResult(final int requestCode, int resultCode, Intent data) {

        AppContext.getInstance().getCachedThreadPool().execute(new Runnable() {
            @Override
            public void run() {

                if (requestCode == REQUESRST_LIVE) {

                    AppContext.getInstance().setCanToRealView(false);
                    if(mHandler != null) {
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                AppContext.getInstance().setCanToRealView(true);
                            }
                        }, 5000);
                    }
                  //  AppContext.getInstance().setLocation(60000);

                    if (!DrivingVideosActivity.isdownloading) {
                        XuLog.d("AndroidBle", "关闭了WIFI");
                        //断开WIFI的同时忘记该WIFI
                        WifiConfiguration wifiConfig = AppContext.getInstance().getWificontroller().CreateWifiInfo(AppConfig.getInstance(AppContext.getInstance()).getWifi_name(), AppConfig.getInstance(AppContext.getInstance()).getWifi_paw(), 3);
                        if (wifiConfig != null) {
                            AppContext.getInstance().getWificontroller().disconnectWifi(wifiConfig);
                        }
                    }
                    //表示退出实时路况
                    VMainActivity.runing = false;
                    XuLog.e(TAG, "关闭设备的实景模式");
                }
            }
        });


    }

    @OnClick(R.id.iv_titlebar_back)
    void back() {

        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        canGetData = false;
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
            mHandler = null;
            AppContext.getInstance().setCanToRealView(true);
        }
        AppContext.getInstance().getCachedThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                AppContext.getInstance().getDeviceHelper().closeTestModel();
                AppContext.getInstance().getDeviceHelper().closeSenser();
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                AppContext.getInstance().getDeviceHelper().closeDeviceRealViewMode();
            }
        });

    }
}
