package com.vispect.android.vispect_g2_app.ui.activity;

import android.bluetooth.BluetoothAdapter;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.vispect.android.vispect_g2_app.R;
import com.vispect.android.vispect_g2_app.adapter.DevicesAdapter;
import com.vispect.android.vispect_g2_app.app.AppConfig;
import com.vispect.android.vispect_g2_app.app.AppContext;
import com.vispect.android.vispect_g2_app.controller.DeviceHelper;
import com.vispect.android.vispect_g2_app.controller.UIHelper;
import com.vispect.android.vispect_g2_app.ui.widget.DialogHelp;
import com.vispect.android.vispect_g2_app.ui.widget.MoListView;
import com.vispect.android.vispect_g2_app.utils.XuLog;
import com.vispect.android.vispect_g2_app.utils.XuString;
import com.vispect.android.vispect_g2_app.utils.XuToast;

import java.lang.reflect.Method;
import java.util.ArrayList;

import bean.BLEDevice;
import bean.Vispect_SDK_ErrorCode;
import butterknife.Bind;
import butterknife.OnClick;
import interf.BleLoginListener;
import interf.OnScanDeviceLisetener;
import interf.OnWifiOpenListener;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.vispect.android.vispect_g2_app.app.AppConfig.CONNECT_DEVICE_OK;
import static com.vispect.android.vispect_g2_app.app.AppConfig.EXTRA_TO_INSTALLATION;
import static com.vispect.android.vispect_g2_app.app.AppConfig.EXTRA_TO_SETTING;
import static com.vispect.android.vispect_g2_app.app.AppConfig.STRING_EXTRA;
import static com.vispect.android.vispect_g2_app.controller.DeviceHelper.cancelConnectDevice;
import static com.vispect.android.vispect_g2_app.controller.DeviceHelper.stopScanDevice;

/**
 * 蓝牙连接设备
 */
public class ConnectActivity extends BaseActivity {

    ArrayList<BLEDevice> deviceList = new ArrayList<>();
    DevicesAdapter adapter;
    @Bind(R.id.img_connect)
    ImageView connect;
    @Bind(R.id.progressBar)
    ProgressBar progressBar;
    @Bind(R.id.list_devices)
    MoListView devicesListView;
    private String TAG = "ConnectActivity";
    private Handler myHandler = new Handler();
    private boolean isConnecting = false;
    private String _extra;

    private Runnable connectFailed = new Runnable() {
        @Override
        public void run() {
            if (isConnecting) {
                isConnecting = false;
                XuToast.show(ConnectActivity.this, STR(R.string.connect_connect_ble_fail));
            }
            DialogHelp.getInstance().hideDialog();
        }
    };
    private Runnable handleSuccess = new Runnable() {
        @Override
        public void run() {
            setResult(CONNECT_DEVICE_OK);
            if (!XuString.isEmpty(_extra)) {
                switch (_extra) {
                    case EXTRA_TO_INSTALLATION:
                        UIHelper.startActivity(ConnectActivity.this, InstallActivity.class);
                        break;
                    case EXTRA_TO_SETTING:
                        UIHelper.startActivity(ConnectActivity.this, SettingsActivity.class);
                        break;
                }
            }
            finish();
        }
    };
    private BleLoginListener loginListener = new BleLoginListener() {
        @Override
        public void onSuccess() {
//            AppContext.getInstance().setLastBleName(currDevice.getBluetoothDevice().getName());
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
            isConnecting = false;

            DialogHelp.getInstance().hideDialog();
            myHandler.postDelayed(handleSuccess, 500);
        }

        @Override
        public void onFail(int i) {
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
                    break;
            }
            myHandler.post(connectFailed);
        }

        @Override
        public void onNotService() {
            isConnecting = false;
            XuToast.show(AppContext.getInstance(), STR(R.string.not_find_service));
            myHandler.post(connectFailed);
            XuLog.e(TAG, "设备没服务");
        }

        @Override
        public void onPassworderro() {
            //密码错误
            isConnecting = false;
            XuToast.show(AppContext.getInstance(), STR(R.string.error_password));
            myHandler.post(connectFailed);
        }
    };

    private OnScanDeviceLisetener scanListener = new OnScanDeviceLisetener() {
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
            boolean has = false;
            for (BLEDevice device : deviceList) {
                if (device.getBluetoothDevice().equals(bleDevice.getBluetoothDevice())) {
                    has = true;
                }
            }
            if (!has) deviceList.add(bleDevice);
            if (adapter != null) adapter.setDevices(deviceList);
        }
    };

    private Runnable cancelScanDevice = new Runnable() {
        @Override
        public void run() {
            progressBar.setVisibility(GONE);
            connect.setVisibility(VISIBLE);
            AppContext.getInstance().getDeviceHelper().stopScanDevice();
        }
    };

    @Override
    public int getContentResource() {
        return R.layout.activity_connect;
    }

    @Override
    protected void initView(View view) {
        _extra = getIntent().getStringExtra(STRING_EXTRA);
        adapter = new DevicesAdapter(this);
        devicesListView.setAdapter(adapter);
        devicesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                connectDevice(deviceList.get(position));
                myHandler.postDelayed(connectFailed, 15000);
            }
        });
        openBluetoothScanDevice();
    }

    private void refreshScan() {
        deviceList.clear();
        adapter.setDevices(deviceList);
        connect.setVisibility(GONE);
        progressBar.setVisibility(VISIBLE);
        myHandler.postDelayed(cancelScanDevice, 7000);
        DeviceHelper.scanDevices(scanListener);
    }

    private void connectDevice(BLEDevice device) {
        if (!isConnecting) {
            isConnecting = true;
            DialogHelp.getInstance().connectDialog(ConnectActivity.this, STR(R.string.connecting));
            AppContext.getInstance().setBleRssi(device.getRssi());
            BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
            DeviceHelper.loginDevice(device, loginListener);
        } else {
            XuToast.show(ConnectActivity.this, STR(R.string.frequent_operations));
        }
    }

    @Override
    protected void onDestroy() {
        stopScanDevice();
        myHandler.removeCallbacks(cancelScanDevice);
        myHandler.removeCallbacks(connectFailed);
        if (isConnecting) cancelConnectDevice();
        super.onDestroy();
    }

    private void openBluetoothScanDevice() {
        if (!BluetoothAdapter.getDefaultAdapter().isEnabled()) {
            boolean result = toEnable(BluetoothAdapter.getDefaultAdapter());
            if (!result) {
                XuToast.show(ConnectActivity.this, R.string.open_bluetooth_fail);
                finish();
                return;
            }
        }
        refreshScan();
    }

    private boolean toEnable(BluetoothAdapter adapter) {
        //启动蓝牙
        boolean result = false;
        try {
            for (Method temp : Class.forName(adapter.getClass().getName()).getMethods()) {
                if (temp.getName().equals("enableNoAutoConnect")) {
                    result = (boolean) temp.invoke(adapter);
                }
            }
        } catch (Exception e) {
            //反射调用失败就启动通过enable()启动;
            result = adapter.enable();
            XuLog.d(TAG, "启动蓝牙的结果:" + result);
            e.printStackTrace();
        }
        return result;
    }

    @OnClick({R.id.img_back, R.id.img_connect})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.img_back:
                finish();
                break;
            case R.id.img_connect:
                refreshScan();
                break;
        }
    }
}
