package com.vispect.android.vispect_g2_app.interf;

/**
 * Created by xu on 2016/7/28.
 */
public interface OnWifiOpenLister {
    void onSuccess(String name, String password);
    void onFail();
    void onGetSanResult(String name);
}
