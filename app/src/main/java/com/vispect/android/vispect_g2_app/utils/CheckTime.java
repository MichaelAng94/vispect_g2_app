package com.vispect.android.vispect_g2_app.utils;

import android.net.wifi.WifiConfiguration;
import android.os.Handler;

import com.vispect.android.vispect_g2_app.app.AppConfig;
import com.vispect.android.vispect_g2_app.app.AppContext;


public class CheckTime {
	private static boolean issening = false;
	private static  boolean cansend = false;
	private static  boolean candisconnectWifi = false;

	private static Handler mHandler = new Handler();
	private static Runnable mRunnable = new Runnable() {
		@Override
		public void run() {
			//发送指令叫设备关闭WIFI

			AppContext.getInstance().getDeviceHelper().closeDeviceRealViewMode();
			XuLog.d("发送指令关闭WIFI");
		}
	};
	private static Runnable disconnectRunnable = new Runnable() {
		@Override
		public void run() {
			//断开WIFI连接
			XuLog.d("断开WIFI连接");
				WifiConfiguration wifiConfig = AppContext.getInstance().getWificontroller().CreateWifiInfo(AppConfig.getInstance(AppContext.getInstance()).getWifi_name(), AppConfig.getInstance(AppContext.getInstance()).getWifi_paw(), 3);
				if (wifiConfig != null) {
					AppContext.getInstance().getWificontroller().disconnectWifi(wifiConfig);
				}


		}
	};


	public static void setcansend(boolean send){
		cansend = send;
	}

	public static boolean isCandisconnectWifi() {
		return candisconnectWifi;
	}

	public static void setCandisconnectWifi(boolean candisconnectWifi) {
		CheckTime.candisconnectWifi = candisconnectWifi;
	}

	public static void removeHnadler(){
		if(mHandler != null){
			mHandler.removeCallbacksAndMessages(null);
		}
	}


	public static void toStop(){
		mHandler.removeCallbacksAndMessages(null);
	}

	public static void todisconnectWifi(){
		mHandler.postDelayed(disconnectRunnable, 1000);
	}

	
}
