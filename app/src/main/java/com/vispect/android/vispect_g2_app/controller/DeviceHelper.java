package com.vispect.android.vispect_g2_app.controller;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.vispect.android.vispect_g2_app.R;
import com.vispect.android.vispect_g2_app.app.AppConfig;
import com.vispect.android.vispect_g2_app.app.AppContext;
import com.vispect.android.vispect_g2_app.interf.Callback;
import com.vispect.android.vispect_g2_app.interf.GetListCallback;
import com.vispect.android.vispect_g2_app.ui.activity.ConnectActivity;
import com.vispect.android.vispect_g2_app.utils.XuLog;
import com.vispect.android.vispect_g2_app.utils.XuToast;

import java.util.ArrayList;

import bean.BLEDevice;
import interf.BleLoginListener;
import interf.GetG2CameraList;
import interf.GetHorizontalLine;
import interf.OnScanDeviceLisetener;
import interf.PointOfAreaListener;
import interf.ResultListner;

public class DeviceHelper {
    public static final String TAG = "DeviceHelper";

    public static boolean isConnected() {
        return AppContext.getInstance().getDeviceHelper().isConnectedDevice();
    }

    public static boolean isG2() {
        return AppContext.getInstance().getDeviceHelper().isG2();
    }

    public static boolean isG2Connected() {
        return isConnected() && isG2();
    }

    public static boolean isG2Connected(@NonNull Activity activity, @Nullable String extra) {
        if (isG2Connected()) {
            return true;
        } else {
            XuToast.show(activity, R.string.please_connect_G2);
            UIHelper.startActivity(activity, ConnectActivity.class, extra);
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

    public static void cancelConnectDevice() {
        AppContext.getInstance().getDeviceHelper().disconnectDevice();
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

    public static void setSPMSpeedSpace(ResultListner resultListner, int start, int end) {
        AppContext.getInstance().getDeviceHelper().setSPMSpeedSpace(resultListner, 0, start, end);
    }

    //获取水平线
    public static void getHorizontal(@NonNull final Callback<Integer> callback, int cameraId) {
        AppContext.getInstance().getDeviceHelper().getHorizontal(new GetHorizontalLine() {

            @Override
            public void onSuccess(int i) {
                callback.callback(i);
            }

            @Override
            public void onFail() {

            }
        }, (byte) cameraId);
    }

    public static void setUDPCamera(@NonNull final Runnable callback, int cameraId, int cameraType) {
        AppContext.getInstance().getDeviceHelper().setUDPCamera(new ResultListner() {
            @Override
            public void onSuccess() {
                callback.run();
            }

            @Override
            public void onFail(int i) {
                XuLog.e(TAG, "setUDPCamera onFail");
            }
        }, cameraId, cameraType);
    }

    public static void getPointOfArea(@NonNull final GetListCallback callback, int cameraId) {
        AppContext.getInstance().getDeviceHelper().getPointOfArea(new PointOfAreaListener() {

            @Override
            public void onSuccess(int i, ArrayList arrayList) {
                callback.onGetListSuccess(arrayList);
            }

            @Override
            public void onFail(int i) {
                callback.onGetListFailed();
            }
        }, (byte) cameraId);
    }
}
