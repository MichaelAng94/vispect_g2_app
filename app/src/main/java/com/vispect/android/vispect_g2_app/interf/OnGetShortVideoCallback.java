package com.vispect.android.vispect_g2_app.interf;

/**
 * Created by xu on 2017/11/23.
 */
public interface OnGetShortVideoCallback{
    void onFail();
    void onGet(byte[] buff, int length);
}
