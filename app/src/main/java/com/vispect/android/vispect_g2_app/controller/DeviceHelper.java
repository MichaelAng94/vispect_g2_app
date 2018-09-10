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

import interf.GetG2CameraList;

public class DeviceHelper {
    public static boolean isConnected() {
        return AppContext.getInstance().getDeviceHelper().isConnectedDevice();
    }

    public static boolean isG2() {
        return AppContext.getInstance().getDeviceHelper().isG2();
    }

    public static boolean isG2Connected(@NonNull Activity activity) {
        if (isConnected() && isG2()) {
            return true;
        } else {
            XuToast.show(activity, R.string.please_connect_G2);
            UIHelper.startActivity(activity, ConnectActivity.class);
        }
        return false;
    }

    public static void getCameraList(@NonNull Activity activity, @NonNull final GetListCallback callback) {
        if (isG2Connected(activity)) {
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
    }

    public static void cancelConnect() {
        AppContext.getInstance().getDeviceHelper().canCelLoginDevice();
    }
}
