package com.vispect.android.vispect_g2_app.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiConfiguration;

import com.vispect.android.vispect_g2_app.app.AppContext;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;



/**
 * 网络操作工具类
 * Created by xu on 2017/3/21.
 */
public class XuNetWorkUtils {
    private static final String TAG = "XuNetWorkUtil";





    private static boolean canConnectWIFI = true;
    /**
     * 取消WIFI连接
     */
    public  static void cancelConnectWIFI(){
        canConnectWIFI = false;
        AppContext.getInstance().getWificontroller().setCanScan();
    }
    /**
     * 连接到指定WIFI
     * @param wifiName          wifi名称
     *  @param wifiPassword     wifi密码
     * @return true:连接成功;否则返回false
     */

    public static boolean connectWIFI(String wifiName, String wifiPassword){
        canConnectWIFI = true;
        //先检查wifi是否有打开  没有则打开
        while (!AppContext.getInstance().getWificontroller().openWifi()) {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        if(!canConnectWIFI){
            return false;
        }
        //判断是否已经连接上指定WIFI
        if (AppContext.getInstance().getWificontroller().getWifiInfo().getSSID().equals("\"" + wifiName + "\"")) {
            if(!canConnectWIFI){
                return false;
            }
            //已经连接上指定WIFI则检测网络是否可用
            XuLog.d("判断到已经连接到WIFI了");
            while (!XuNetWorkUtils.isWIFINetworkAvailable() && canConnectWIFI) {
                try {
                    XuLog.d("网络还不可以用");
                    Thread.sleep(1000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if(!canConnectWIFI){
                return false;
            }
            //网络可用则准备开始更新
            return true;

        }else{
            if(!canConnectWIFI){
                return false;
            }
            //未连接到指定WIFI
            //生成指定WIFI的配置文件
            final WifiConfiguration wifiConfig = AppContext.getInstance().getWificontroller().CreateWifiInfo(wifiName, wifiPassword, 3);
            if (wifiConfig == null) {
                return false;
            }
            if(!canConnectWIFI){
                return false;
            }
            //连接到指定WIFI
            while (!AppContext.getInstance().getWificontroller().getWifiInfo().getSSID().equals(wifiConfig.SSID) && canConnectWIFI) {
                XuLog.d("正在等待WIFI连接成功");
                AppContext.getInstance().getWificontroller().toconnectwifi(wifiConfig);
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            if(!canConnectWIFI){
                return false;
            }
            //连接上指定WIFI则检测网络是否可用
            XuLog.d("判断到已经连接到WIFI了");
            while (!XuNetWorkUtils.isWIFINetworkAvailable() && canConnectWIFI) {
                try {
                    XuLog.d("网络还不可以用");
                    Thread.sleep(1000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if(!canConnectWIFI){
                return false;
            }
            //网络可用则准备开始更新
            return true;
        }
    }
    /**
     * 检测WIFI网络是否可用
     */
    public static boolean isWIFINetworkAvailable() {

        ConnectivityManager connectivity = (ConnectivityManager) AppContext.getInstance().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity == null) {
            return false;
        } else {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();

            if (info != null) {
                for (int i = 0; i < info.length; i++) {
                    if (info[i].getState() == NetworkInfo.State.CONNECTED && info[i].getTypeName().equals("WIFI")) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * 检测网络是否可用
     */
    public static boolean isNetworkAvailable() {
        ConnectivityManager connectivity = (ConnectivityManager) AppContext.getInstance().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity == null) {
            return false;
        } else {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null) {
                for (int i = 0; i < info.length; i++) {
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * 检测当前的网络类型
     * return  1:WIFI  2:MOBILE
     */
    public static int getNetWorkType() {
        ConnectivityManager connectivity = (ConnectivityManager) AppContext.getInstance().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity == null) {
            return -1;
        } else {

            //先判断是否WIFI
            NetworkInfo wifiInfo = connectivity.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            XuLog.e(TAG,"当前是WIFI");
            if (wifiInfo != null) {
                NetworkInfo.State state = wifiInfo.getState();
                if (null != state) {
                    if (state == NetworkInfo.State.CONNECTED || state == NetworkInfo.State.CONNECTING) {
                        return 1;
                    }
                }
            }


            NetworkInfo networkInfo = connectivity.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            if (null != networkInfo) {
                NetworkInfo.State state = networkInfo.getState();
                String strSubTypeName = networkInfo.getSubtypeName();
                XuLog.e(TAG,"不是WIFIF,当前的移动网络类型:"+strSubTypeName);
                if (null != state) {
                    if (state == NetworkInfo.State.CONNECTED || state == NetworkInfo.State.CONNECTING) {
                        return 2;
                    }
                }
            }


        }


        return -1;
    }

    /***
     * 打开热点
     * @param ap_ssid ：热点名称
     * @param ap_password ：热点密码
     * */
    public static boolean openWifiAp(String ap_ssid, String ap_password){
        //判断WIFI是否打开着  如果是则关闭
        AppContext.getInstance().getWificontroller().closeWifi();
        //判断热点是否开启着  如果是则关闭
        if(AppContext.getInstance().getWificontroller().isWifiApEnabled()){
            XuLog.e(TAG,"方法里关热点");
            AppContext.getInstance().getWificontroller().setWifiApEnabled(false);
        }
        //设置wifi热点的名称和密码
        AppContext.getInstance().getWificontroller().setWifiHostSsidAndWifiHostPresharedKey(ap_ssid,ap_password);
        //打开wifi热点
        return AppContext.getInstance().getWificontroller().setWifiApEnabled(true);
    }


    /**
     * 判断当前的网络连接状态是否能用
     * return ture  可用   flase不可用
     */
    public static final boolean ping() {

        String result = null;
        try {
            String ip = "120.76.100.103";// ping 的地址，可以换成任何一种可靠的外网
            Process p = Runtime.getRuntime().exec("ping -c 3 -w 100 " + ip);// ping网址3次
            // 读取ping的内容，可以不加
            InputStream input = p.getInputStream();
            BufferedReader in = new BufferedReader(new InputStreamReader(input));
            StringBuffer stringBuffer = new StringBuffer();
            String content = "";
            while ((content = in.readLine()) != null) {
                stringBuffer.append(content);
            }
            XuLog.d("------ping-----", "result content : " + stringBuffer.toString());
            // ping的状态
            int status = p.waitFor();
            if (status == 0) {
                result = "success";
                return true;
            } else {
                result = "failed";
            }
        } catch (IOException e) {
            result = "IOException";
        } catch (InterruptedException e) {
            result = "InterruptedException";
        } finally {
            XuLog.d("----result---", "result = " + result);
        }
        return false;

    }


}
