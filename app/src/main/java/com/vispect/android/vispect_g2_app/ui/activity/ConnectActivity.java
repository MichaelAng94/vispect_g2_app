package com.vispect.android.vispect_g2_app.ui.activity;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.vispect.android.vispect_g2_app.R;
import com.vispect.android.vispect_g2_app.adapter.ConnectDdeviceAdapter;
import com.vispect.android.vispect_g2_app.app.AppConfig;
import com.vispect.android.vispect_g2_app.app.AppContext;
import com.vispect.android.vispect_g2_app.ui.widget.DialogHelp;
import com.vispect.android.vispect_g2_app.utils.XuLog;
import com.vispect.android.vispect_g2_app.utils.XuToast;

import java.lang.reflect.Method;
import java.util.ArrayList;

import bean.BLEDevice;
import bean.Vispect_SDK_ErrorCode;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import interf.BleLoginListener;
import interf.GetBleInfoVersionCallback;
import interf.OnScanDeviceLisetener;
import interf.OnWifiOpenListener;

import static com.vispect.android.vispect_g2_app.ui.activity.MainActivity.clickMeun;

public class ConnectActivity extends BaseActivity {

    @Bind(R.id.list_connect)
    ListView listConnect;
    @Bind(R.id.progressBar1)
    ProgressBar progressBar1;
    @Bind(R.id.img_connect)
    ImageView imgConnect;
    private String TAG = "ConnectActivity";
    ArrayList<BLEDevice> devicelist;
    ArrayList<BLEDevice> list;
    private Handler myHandler = new Handler();
    ConnectDdeviceAdapter adapter;
    private BLEDevice currDevice;
    private boolean isLogining = false;

    @Override
    public int getContentResource() {
        return R.layout.activity_connect;
    }

    @Override
    protected void initView(View view) {
        setTitle("Choose Your Device");
        devicelist = new ArrayList<>();
        list = new ArrayList<>();
        AppConfig.getInstance(ConnectActivity.this).setDeviceName("unkonwDevoce");
        adapter = new ConnectDdeviceAdapter(ConnectActivity.this, devicelist);
        listConnect.setAdapter(adapter);
        listConnect.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                logintToDevice(devicelist.get(position));
                myHandler.postDelayed(connectfail,15000);
            }
        });
        openBluetoothScanDevice();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        clickMeun = 0;
        finish();
    }

    private void refreshScan() {
        list.clear();
        devicelist.clear();
        adapter.notifyDataSetChanged();
        imgConnect.setVisibility(View.GONE);
        progressBar1.setVisibility(View.VISIBLE);
//        if (!AppContext.getInstance().getDeviceHelper().isConnectedDevice()) {
            AppContext.getInstance().getDeviceHelper().stopScanDevice();
            AppContext.getInstance().getDeviceHelper().startScanDevice(scanLister);
            myHandler.postDelayed(cancleScan,7000);
//        }
    }

    private void logintToDevice(final BLEDevice device) {
        if (!isLogining) {
            DialogHelp.getInstance().connectDialog(ConnectActivity.this,STR(R.string.connecting));
            currDevice = device;
            isLogining = true;
            AppContext.getInstance().setBleRssi(device.getRssi());
            BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
            AppContext.getInstance().getDeviceHelper().loginDevice(device, loginListener);
        } else {
            myHandler.post(frequentoperations);
        }
    }

    private Runnable frequentoperations = new Runnable() {
        @Override
        public void run() {
            XuToast.show(ConnectActivity.this, STR(R.string.frequent_operations));
        }
    };

    private BleLoginListener loginListener = new BleLoginListener() {
        @Override
        public void onSuccess() {
            AppContext.getInstance().setLastBleName(currDevice.getBluetoothDevice().getName());
            if (!AppContext.getInstance().isS()) {
                //获取设备的WIFI名称和密码
                AppContext.getInstance().getDeviceHelper().getDeviceWifiNameAndPassword(new OnWifiOpenListener() {
                    @Override
                    public void onSuccess(String name, String password) {
                        AppConfig.getInstance(AppContext.getInstance()).setWifi_name(name);
                        AppConfig.getInstance(AppContext.getInstance()).setWifi_paw(password);
                    }

                    @Override
                    public void onFail(int i) {

                    }

                    @Override
                    public void onGetSanResult(String s) {

                    }
                });
            }
            isLogining = false;

           DialogHelp.getInstance().hideDialog();
           myHandler.postDelayed(new Runnable() {
               @Override
               public void run() {
                   finish();
               }
           },500);
        }

        @Override
        public void onFail(int i) {
            isLogining = false;
            switch (i) {
                case Vispect_SDK_ErrorCode.BLE_DEVICE_UNWORK:
                    XuLog.d(TAG, "发现S款设备当前的生成正在处于IAP区且跳转失败，内置应该可能已损坏");
                    //  showDialogForUpdateFail(STR(R.string.s_app_erro));
                    break;
                case Vispect_SDK_ErrorCode.BLE_DEVICE_UPDATEING:
                    XuLog.d(TAG, "正处于升级步骤当中");
                    //    showDialogForUpdateFail(STR(R.string.update_to_update) + "?");
                    AppContext.getInstance().setApp_error_s(false);
                    break;
                case Vispect_SDK_ErrorCode.CONNECT_BLE_FAIL:
                    //连接失败
                    myHandler.post(connectfail);
                    break;
            }
        }

        @Override
        public void onNotService() {
            isLogining = false;
            myHandler.post(new Runnable() {
                @Override
                public void run() {
                    XuToast.show(AppContext.getInstance(), STR(R.string.not_find_service));
                    DialogHelp.getInstance().hideDialog();
                    myHandler.post(connectfail);
                }
            });
            XuLog.e(TAG, "设备没服务");
        }

        @Override
        public void onPassworderro() {
            //密码错误
            isLogining = false;
        }
    };

    private Runnable connectfail = new Runnable() {
        @Override
        public void run() {
            isLogining = false;
            DialogHelp.getInstance().hideDialog();
            XuToast.show(ConnectActivity.this, STR(R.string.connect_connect_ble_fail));
    }};

    private OnScanDeviceLisetener scanLister = new OnScanDeviceLisetener() {
        @Override
        public void onSuccess() {
            XuLog.d(TAG, "start ble scan success！");
        }

        @Override
        public void onFail(int i) {
            XuLog.d(TAG, "start ble scan fail！" + i);
        }

        @Override
        public void onFindDevice(BLEDevice bleDevice) {
            XuLog.e(TAG, "find a ADASDevice ：" + bleDevice.getBluetoothDevice().getName() + "     " + bleDevice.getBluetoothDevice().getAddress());
            addDevice(bleDevice);
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppContext.getInstance().getDeviceHelper().stopScanDevice();
        myHandler.removeCallbacks(cancleScan);
        myHandler.removeCallbacks(refreshRunnable);
        myHandler.removeCallbacks(connectfail);
        if (isLogining) {
            AppContext.getInstance().getDeviceHelper().disconnectDevice();
        }
    }

    void openBluetoothScanDevice() {
        if (!BluetoothAdapter.getDefaultAdapter().isEnabled()) {
            boolean openresult = toEnable(BluetoothAdapter.getDefaultAdapter());
            if (!openresult) {
                XuToast.show(ConnectActivity.this, "打开蓝牙失败1");
                finish();
                return;
            }

            SystemClock.sleep(500);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    int i = 0;
                    while (!BluetoothAdapter.getDefaultAdapter().isEnabled()) {
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        if (i >= 15) {
                            XuToast.show(ConnectActivity.this, "打开蓝牙失败");
                            finish();
                            break;
                        } else {
                            i++;
                        }
                    }
                    refreshScan();
                }
            });
        } else {
            refreshScan();
        }
    }

    private boolean toEnable(BluetoothAdapter bluertoothadapter) {
        //TODO 启动蓝牙
        boolean result = false;
        try {
            for (Method temp : Class.forName(bluertoothadapter.getClass().getName()).getMethods()) {
                if (temp.getName().equals("enableNoAutoConnect")) {
                    result = (boolean) temp.invoke(bluertoothadapter);
                }
            }
        } catch (Exception e) {
            //反射调用失败就启动通过enable()启动;
            result = bluertoothadapter.enable();
            XuLog.d(TAG, "启动蓝牙的结果:" + result);
            e.printStackTrace();

        }
        return result;

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK || requestCode == -1) {
            return;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }

    @OnClick(R.id.img_connect)
    public void onViewClicked() {
        AppContext.getInstance().getDeviceHelper().stopScanDevice();
        AppContext.getInstance().getDeviceHelper().startScanDevice(scanLister);
    }

    private void addDevice(final BLEDevice bleDevice) {
        // ADASDevice found
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                boolean isadd = true;
                for (BLEDevice temp : list) {
                    if (temp.getBluetoothDevice().equals(bleDevice.getBluetoothDevice())) {
                        isadd = false;
                    }
                }
                if (isadd) {
                    list.add(bleDevice);
                    myHandler.post(refreshRunnable);
                }
            }
        });
    }

    private Runnable refreshRunnable = new Runnable() {
        @Override
        public void run() {
            if (adapter == null || list == null) {
                return;
            }
            devicelist = (ArrayList<BLEDevice>) list.clone();
            adapter.refreshData(devicelist);
        }
    };

    private Runnable cancleScan = new Runnable() {
        @Override
        public void run() {
            progressBar1.setVisibility(View.GONE);
            imgConnect.setVisibility(View.VISIBLE);
            AppContext.getInstance().getDeviceHelper().stopScanDevice();
        }
    };

    @OnClick({R.id.img_connect,R.id.img_back_main})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.img_connect:
                refreshScan();
                break;
            case R.id.img_back_main:
                clickMeun = 0;
                finish();
                break;
        }
    }

}
