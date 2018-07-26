package com.vispect.android.vispect_g2_app.utils;

import android.os.Handler;

import com.vispect.android.vispect_g2_app.R;
import com.vispect.android.vispect_g2_app.app.AppContext;
import com.vispect.android.vispect_g2_app.interf.XuTimeOutCallback;


/**
 * 超时机制工具
 *
 * Created by xu on 2016/12/10.
 */
public class XuTimeOutUtil {

    XuTimeOutCallback timeOutCallback = null;
    Handler mHandler = new Handler();
    Runnable timeOutRunnable = new Runnable() {
        @Override
        public void run() {
            if(timeOutCallback != null){
                timeOutCallback.onTimeOut();
                XuToast.show(AppContext.getInstance(),"operation timeoout");
            }
        }
    };

    public XuTimeOutUtil(XuTimeOutCallback callback){
        timeOutCallback = callback;
    }

    public void startCheck(long time){
        stopCheck();
        mHandler.postDelayed(timeOutRunnable,time);
    }
    public void stopCheck(){
        mHandler.removeCallbacksAndMessages(null);
    }






}
