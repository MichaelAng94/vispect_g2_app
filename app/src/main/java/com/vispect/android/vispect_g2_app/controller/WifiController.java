package com.vispect.android.vispect_g2_app.controller;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.DhcpInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.WifiLock;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Handler;

import com.vispect.android.vispect_g2_app.app.AppContext;
import com.vispect.android.vispect_g2_app.interf.OnWifiOpenLister;
import com.vispect.android.vispect_g2_app.utils.XuLog;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.util.List;



/**
 * WIFI的操作类
 * Created by xu on 2016/4/21.
 */
public class WifiController {
    private final static String TAG = "WifiController";

    private Context context;
    private WifiManager mWifiManager;
    private WifiInfo mWifiInfo;
    private List<ScanResult> mWifiList = null;
    private List<WifiConfiguration> mWifiConfiguration;
    private WifiLock mWifiLock;
    private DhcpInfo dhcpInfo;
    private WifiConfiguration lastnework;

    private String WIFI_HOST_SSID = "vispectAP";
    private String WIFI_HOST_PRESHARED_KEY = "vispectAP";


    public WifiController(Context context) {
        this.context = context;
        mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        mWifiInfo = mWifiManager.getConnectionInfo();
        // registerWIFI();
    }

    private void registerWIFI() {
        IntentFilter mWifiFilter = new IntentFilter();
        mWifiFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        context.registerReceiver(mWifiConnectReceiver, mWifiFilter);
    }


    public boolean isWifiEnabled(){
        return mWifiManager.isWifiEnabled();
    }


    public boolean openWifi() {
        //打开wifi
        if (!mWifiManager.isWifiEnabled()) {
            XuLog.i("setWifiEnabled.....");
            mWifiManager.setWifiEnabled(true);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            XuLog.i("setWifiEnabled.....end");
        }
        return mWifiManager.isWifiEnabled();
    }

    public void closeWifi() {
        if (mWifiManager.isWifiEnabled()) {
            mWifiManager.setWifiEnabled(false);
        }
    }

    public int checkState() {
        return mWifiManager.getWifiState();
    }

    public void acquireWifiLock() {
        //锁定wifiLock
        mWifiLock.acquire();
    }

    public void releaseWifiLock() {
        //解锁wifiLock
        if (mWifiLock.isHeld()) {
            mWifiLock.acquire();
        }
    }

    public void creatWifiLock() {
        mWifiLock = mWifiManager.createWifiLock("Test");
    }

    public List<WifiConfiguration> getConfiguration() {
        return mWifiConfiguration;
    }

    public boolean reconnect(){
        if(lastnework == null){
            return  false;
        }
        return addNetwork(lastnework);
    }

    public boolean connectConfiguration(int index) {
        //指定配置好的网络进行连接
        if (index > mWifiConfiguration.size()) {
            return false;
        }
        return  mWifiManager.enableNetwork(mWifiConfiguration.get(index).networkId, true);
    }
    OnWifiOpenLister lister;
    public void setLister(OnWifiOpenLister lister){
       this.lister = lister;
    }


   public boolean haswifi(WifiConfiguration wifiConfig){
       mWifiList = mWifiManager.getScanResults();
       if (mWifiList != null) {
           XuLog.i(TAG, "getScan result:" + mWifiList.size());
           for (int i = 0; i < mWifiList.size(); i++) {
               ScanResult result = mWifiList.get(i);
               XuLog.i(TAG, "Scan result[" + i + "]" + result.SSID + "--" + result.BSSID);
               if (wifiConfig.SSID.equals("\"" + result.SSID + "\"")){
                   XuLog.i(TAG, "has wifi " + wifiConfig.SSID);
                   return true;
               }
           }
           XuLog.i(TAG, "search wifi " + wifiConfig.SSID + " is end");
           return false;
       } else {
           XuLog.i(TAG, "startScan result is null.");
           return false;
       }
   }

    //获取指定wifi的信号强度
    public int getWIFIRssi(String ssid){
        mWifiList = mWifiManager.getScanResults();
        if (mWifiList != null) {
            XuLog.i(TAG, "getScan result:" + mWifiList.size());
            for (int i = 0; i < mWifiList.size(); i++) {
                ScanResult result = mWifiList.get(i);
                XuLog.i(TAG, "Scan result[" + i + "]" + result.SSID + "--" + result.BSSID);
                if (ssid.equals("\"" + result.SSID + "\"")){

                    return result.level;
                }
            }
            XuLog.i(TAG, "search wifi " + ssid + " is end");
            return 0;
        } else {
            XuLog.i(TAG, "startScan result is null.");
            return 0;
        }
    }

    public boolean startScan() {
        //wifi扫描
        boolean scan = mWifiManager.startScan();
        XuLog.e(TAG, "startScan result:" + scan);
        return  scan;
    }

    public List<ScanResult> getWifiList() {
        return mWifiList;
    }

    public StringBuilder lookUpScan() {
        // 查看扫描结果
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < mWifiList.size(); i++) {
            stringBuilder.append("Index_" + new Integer(i + 1).toString() + ":");
            stringBuilder.append((mWifiList.get(i)).toString());
            stringBuilder.append("/n");
        }
        return stringBuilder;
    }

    public String getMacAddress() {
        return (mWifiInfo == null) ? "NULL" : mWifiInfo.getMacAddress();
    }

    public String getBSSID() {
        return (mWifiInfo == null) ? "NULL" : mWifiInfo.getBSSID();
    }

    public DhcpInfo getDhcpInfo() {
        return dhcpInfo = mWifiManager.getDhcpInfo();
    }

    public int getIPAddress() {
        return (mWifiInfo == null) ? 0 : mWifiInfo.getIpAddress();
    }

    public int getNetworkId() {
        return (mWifiInfo == null) ? 0 : mWifiInfo.getNetworkId();
    }

    public WifiInfo getWifiInfo() {
        mWifiInfo = mWifiManager.getConnectionInfo();
        return mWifiInfo;
    }

    public boolean connect(WifiConfiguration config, WifiP2pManager.ActionListener listener){
        try{
            Class cls = mWifiManager.getClass();
            for(Method temp:cls.getMethods()){
                if(temp.getName().equals("connect")){
                    temp.setAccessible(true);
                    temp.invoke(mWifiManager,config,listener);
                    return true;
                }
            }
        }catch (Exception e){
            XuLog.e(TAG, "反射调用connect()方法失败，现在使用addNetwork()");
            XuLog.e(TAG, "错误信息:" + e.toString());

            return addNetwork(config);
        }
        // boolean b = mWifiManager.reconnect();
        return false;

    }

    Handler mHandler = new Handler();
    public static boolean canScan = true;
    public static void setCanScan(){
        canScan = false;
    }
    public boolean toconnectwifi(final WifiConfiguration wcg){
        canScan = true;
        AppContext.getInstance().getCachedThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                //查询是否有扫描到此WIFI
                if(!haswifi(wcg) && canScan){
                    //开始一次扫描
                    startScan();
                }else{
                    addNetwork(wcg);
                }
            }
        });



        return false;
    }


    public boolean addNetwork(WifiConfiguration wcg) {
        //TODO    添加一个网络配置并连接
//        //先断开一次WIFI
//        disconnectWifi();
//        SystemClock.sleep(2000);
        //存在就先删除再添加
        WifiConfiguration tempConfig = IsExsits(wcg.SSID);
        boolean r = false;
        if(tempConfig != null){
            mWifiManager.removeNetwork(tempConfig.networkId);
            r =  mWifiManager.saveConfiguration();
        }

        int wcgID = mWifiManager.addNetwork(wcg);
         boolean s = mWifiManager.saveConfiguration();
        boolean b = false;
        if(wcgID != -1){
            lastnework = wcg;
            b = mWifiManager.enableNetwork(wcgID, true);
            b =  mWifiManager.saveConfiguration();

        }

        if(!b || wcgID ==-1){
            if(tempConfig != null){
                XuLog.d("添加或者连接指定失败，但是有找到对应的config，所以尝试连接对应的config");
                b = mWifiManager.enableNetwork(tempConfig.networkId, true);
                lastnework = null;
            }
        }


      // boolean b = mWifiManager.reconnect();
        System.out.println("removeNetwork--" + r);
        System.out.println("addNetwork--" + wcgID);
        System.out.println("enableNetwork--" + b);
        System.out.println("enableNetwork--" + s);
        return b;
    }

    public void disconnectWifi(int netId) {
        mWifiManager.disableNetwork(netId);
        mWifiManager.disconnect();
    }
    public void disconnectWifi() {
        mWifiManager.disconnect();
    }

    public WifiConfiguration CreateWifiInfo(String SSID, String Password, int Type) {
        XuLog.i("SSID:" + SSID + ",password:" + Password);
        WifiConfiguration config = new WifiConfiguration();
        config.allowedAuthAlgorithms.clear();
        config.allowedGroupCiphers.clear();
        config.allowedKeyManagement.clear();
        config.allowedPairwiseCiphers.clear();
        config.allowedProtocols.clear();
        config.SSID = "\"" + SSID + "\"";

        WifiConfiguration tempConfig = this.IsExsits(SSID);

        if (tempConfig != null) {
            mWifiManager.removeNetwork(tempConfig.networkId);
        } else {
            XuLog.i("IsExsits is null.");
        }

        if (Type == 1) // WIFICIPHER_NOPASS
        {
            XuLog.i("Type =1.");
            config.wepKeys[0] = "";
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            config.wepTxKeyIndex = 0;
        }
        if (Type == 2) // WIFICIPHER_WEP
        {
            XuLog.i("Type =2.");
            config.hiddenSSID = true;
            config.wepKeys[0] = "\"" + Password + "\"";
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            config.wepTxKeyIndex = 0;
        }
        if (Type == 3) // WIFICIPHER_WPA
        {

            XuLog.i("Type =3.");
            config.preSharedKey = "\"" + Password + "\"";
            config.hiddenSSID = true;
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
            // config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
            config.status = WifiConfiguration.Status.ENABLED;
        }


        if (Type == 4) // WIFICIPHER_WPA
        {
            XuLog.i("Type =4.");
            config.preSharedKey = "\"" + Password + "\"";
            config.hiddenSSID = true;
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedKeyManagement.set(4);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
            // config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
            config.status = WifiConfiguration.Status.ENABLED;
        }

        return config;
    }

    private WifiConfiguration IsExsits(String SSID) {
        // 查看以前是否已经配置过该SSID
        List<WifiConfiguration> existingConfigs = mWifiManager.getConfiguredNetworks();
        if (existingConfigs == null || existingConfigs.isEmpty()) {
            return null;
        }
        for (WifiConfiguration existingConfig : existingConfigs) {
            if (existingConfig.SSID.equals("\"" + SSID + "\"")) {
                return existingConfig;
            }
        }
        return null;
    }

    public void destroy() {
        closeWifi();
        // context.unregisterReceiver(mWifiConnectReceiver);
    }


    /**
     * 判断热点开启状态
     * */
    public boolean isWifiApEnabled() {
        return getWifiApState() == WIFI_AP_STATE.WIFI_AP_STATE_ENABLED;
    }
    /**
     * 获取WIFI的状态
     * */
    private WIFI_AP_STATE getWifiApState(){
        int tmp;
        try {
            Method method = mWifiManager.getClass().getMethod("getWifiApState");
            tmp = ((Integer) method.invoke(mWifiManager));
            // Fix for Android 4
            if (tmp > 10) {
                tmp = tmp - 10;
            }
            return WIFI_AP_STATE.class.getEnumConstants()[tmp];
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return WIFI_AP_STATE.WIFI_AP_STATE_FAILED;
        }
    }
    /**
     * wifi热点开关
     * @param enabled   true：打开  false：关闭
     * @return  true：成功  false：失败
     */
    public boolean setWifiApEnabled(boolean enabled) {

        if (enabled) { // disable WiFi in any case
            //wifi和热点不能同时打开，所以打开热点的时候需要关闭wifi
            mWifiManager.setWifiEnabled(false);
            System.out.println(TAG + ":开启热点");

        }else{
            mWifiManager.setWifiEnabled(true);
            System.out.println(TAG + ":关闭热点");
        }
        try {
            //热点的配置类
            WifiConfiguration apConfig = new WifiConfiguration();
            //配置热点的名称(可以在名字后面加点随机数什么的)
            apConfig.SSID = WIFI_HOST_SSID ;
            //配置热点的密码
            apConfig.preSharedKey = WIFI_HOST_PRESHARED_KEY;
            //安全：WPA2_PSK
            apConfig.allowedKeyManagement.set(4);
//            apConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
            //通过反射调用设置热点
            Method method = mWifiManager.getClass().getMethod(
                    "setWifiApEnabled", WifiConfiguration.class, Boolean.TYPE);
            //返回热点打开状态
            return (Boolean) method.invoke(mWifiManager, apConfig, enabled);
        } catch (Exception e) {
            return false;
        }
    }
    public enum WIFI_AP_STATE {
        WIFI_AP_STATE_DISABLING, WIFI_AP_STATE_DISABLED, WIFI_AP_STATE_ENABLING,  WIFI_AP_STATE_ENABLED, WIFI_AP_STATE_FAILED
    }



    private BroadcastReceiver mWifiConnectReceiver = new BroadcastReceiver() {


        @Override
        public void onReceive(Context context, Intent intent) {
            XuLog.d("Wifi onReceive action = " + intent.getAction());
            if (intent.getAction().equals(WifiManager.WIFI_STATE_CHANGED_ACTION)) {
                int message = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, -1);
                XuLog.d("liusl wifi onReceive msg=" + message);
                 /*
        WifiManager.WIFI_STATE_DISABLED: //wifi不可用

        WifiManager.WIFI_STATE_DISABLING://wifi 正在关闭或者断开

        WifiManager.WIFI_STATE_ENABLED://wifi可用

        WifiManager.WIFI_STATE_ENABLING://wifi正在打开或者连接

        WifiManager.WIFI_STATE_UNKNOWN://未知消息
        */
                switch (message) {
                    case WifiManager.WIFI_STATE_DISABLED:
                        XuLog.d("WIFI_STATE_DISABLED");

                        break;
                    case WifiManager.WIFI_STATE_DISABLING:
                        XuLog.d("WIFI_STATE_DISABLING");
                        break;
                    case WifiManager.WIFI_STATE_ENABLED:
                        XuLog.d("WIFI_STATE_ENABLED");
                        break;
                    case WifiManager.WIFI_STATE_ENABLING:
                        XuLog.d("WIFI_STATE_ENABLING");
                        break;
                    case WifiManager.WIFI_STATE_UNKNOWN:
                        XuLog.d("WIFI_STATE_UNKNOWN");
                        break;
                    default:
                        break;
                }
            }
        }
    };


    public void disconnectWifi(WifiConfiguration wifiConfig) {
        //TODO 断开WIFI连接并忘记WIFI
        WifiConfiguration tempConfig = IsExsits(wifiConfig.SSID);
        if(tempConfig != null){
           boolean re =  mWifiManager.removeNetwork(tempConfig.networkId);
            re =  mWifiManager.saveConfiguration();
            XuLog.d("remove WIFIconfig result：" + re);
        }

    }

    public static final boolean ping() {

        String result = null;
        try {
            String ip = "www.baidu.com";// ping 的地址，可以换成任何一种可靠的外网
            Process p = Runtime.getRuntime().exec("ping -c 3 -w 100 " + ip);// ping网址3次
            // 读取ping的内容，可以不加
            InputStream input = p.getInputStream();
            BufferedReader in = new BufferedReader(new InputStreamReader(input));
            StringBuffer stringBuffer = new StringBuffer();
            String content = "";
            while ((content = in.readLine()) != null) {
                stringBuffer.append(content);
            }
            XuLog.d(TAG, "ping  " + ip + "  result content : " + stringBuffer.toString());
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
            XuLog.d(TAG, "ping  result = " + result);
        }
        return false;

    }

    public String getWIFI_HOST_SSID() {
        return WIFI_HOST_SSID;
    }

    public String getWIFI_HOST_PRESHARED_KEY() {
        return WIFI_HOST_PRESHARED_KEY;
    }

    public  void setWifiHostSsidAndWifiHostPresharedKey(String wifiHostSsid, String wifiHostPresharedKey) {
        WIFI_HOST_SSID = wifiHostSsid;
        WIFI_HOST_PRESHARED_KEY = wifiHostPresharedKey;
    }


}
