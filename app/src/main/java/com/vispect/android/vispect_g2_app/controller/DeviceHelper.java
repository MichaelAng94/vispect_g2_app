package com.vispect.android.vispect_g2_app.controller;

import android.app.Activity;
import android.support.annotation.NonNull;

import com.vispect.android.vispect_g2_app.R;
import com.vispect.android.vispect_g2_app.app.AppContext;
import com.vispect.android.vispect_g2_app.interf.GetListCallback;
import com.vispect.android.vispect_g2_app.ui.activity.ConnectActivity;
import com.vispect.android.vispect_g2_app.ui.activity.InstallActivity;
import com.vispect.android.vispect_g2_app.utils.XuToast;

import java.util.ArrayList;

import bean.BLEDevice;
import interf.BleLoginListener;
import interf.GetG2CameraList;
import interf.OnScanDeviceLisetener;

public class DeviceHelper {
    public static boolean isConnected() {
        return AppContext.getInstance().getDeviceHelper().isConnectedDevice();
    }

    public static boolean isG2() {
        return AppContext.getInstance().getDeviceHelper().isG2();
    }

    public static boolean isG2Connected(@NonNull Activity activity, int requestCode) {
        if (isConnected() && isG2()) {
            return true;
        } else {
            XuToast.show(activity, R.string.please_connect_G2);
            UIHelper.startActivityForResult(activity, ConnectActivity.class, requestCode);
        }
        return false;
    }

    public static void getCameraList(@NonNull final GetListCallback callback) {
            AppContext.getInstance().getDeviceHelper().getG2CameraList(new GetG2CameraList() {
                @Override
                public void onSuccess(ArrayList arrayList) {
                    callback.onGetListSuccess(arrayList);
                }

                @Override
                public void onFail() {
                    callback.onGetListFailed();
                }
            });
        }

    public static void cancelConnect() {
        AppContext.getInstance().getDeviceHelper().canCelLoginDevice();
    }

    public static void stopScanDevice() {
        AppContext.getInstance().getDeviceHelper().stopScanDevice();
    }

    public static void scanDevices(OnScanDeviceLisetener scanListener) {
        if (isConnected()) cancelConnect();
        stopScanDevice();
        AppContext.getInstance().getDeviceHelper().startScanDevice(scanListener);
    }

    public static void loginDevice(@NonNull BLEDevice device, BleLoginListener loginListener) {
        AppContext.getInstance().getDeviceHelper().loginDevice(device, loginListener);
    }
}
