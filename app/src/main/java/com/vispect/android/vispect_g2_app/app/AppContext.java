package com.vispect.android.vispect_g2_app.app;

import android.app.Application;
import android.graphics.Point;

import com.example.vispect_blesdk.DeviceHelper;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.vispect.android.vispect_g2_app.bean.UserInfo;
import com.vispect.android.vispect_g2_app.controller.WifiController;
import com.vispect.android.vispect_g2_app.utils.XuCrashHandler;
import com.vispect.android.vispect_g2_app.utils.XuLog;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import interf.Oninit;

/**
 * Created by mo on 2018/7/2.
 */

public class AppContext extends Application {

    private final static String TAG = "AppContext";
    /**
     * Application单例
     */
    private static AppContext sInstance;
    /**
     * 正在下载的视频的名字
     */
    public String downloading_name = "";
    public boolean app_error_s = false;
    /**
     * 是否正在升级S款设备
     */
    public boolean updateing_s = false;
    /**
     * 标定还是实景   0实景 1侧边标定
     */
    public int calibrateType = 0;
    /**
     * 标定的相机ID
     */
    public int calivrateID = 0;
    /**
     * 是否需要关闭下载的TCP端口
     */
    boolean needclosetcp = false;
    /**
     * 下载线程池
     * （单线程队列下载）
     */
    ExecutorService downThreadExecutor;
    /**
     * OBD操作线程池
     * （单线程队列下载）
     */
    ExecutorService obdOptThreadExecutor;
    /**
     * 其他作用的线程池
     * （如更新UI，联网等，方便管理）
     */
    ExecutorService cachedThreadPool;
    /**
     * 更新作用的线程池
     * （更新Sdk）
     */
    ExecutorService fixedThreadPool;
    /**
     * WIFI操作
     */
    WifiController wificontroller;
    /**
     * vispect的 SDK的操作类
     */
    DeviceHelper deviceHelper;
    /**
     * 是否可以进入实时路况
     */
    boolean canToRealView = true;
    /**
     * 是否处于开发者模式
     */
    private boolean DeveloperMode = false;
    private UserInfo user;
    private boolean neeedCloseBluetooth = false;
    private String lastBleName = "";
    /**
     * BLE的信号强度
     */
    private int bleRssi = 0;
    private Point _currentCamera = null;

    public static AppContext getInstance() {
        return sInstance;
    }

    public ExecutorService getDownThreadExecutor() {
        return downThreadExecutor;
    }

    public void setDownThreadExecutor(ExecutorService downThreadExecutor) {
        this.downThreadExecutor = downThreadExecutor;
    }

    public ExecutorService getObdOptThreadExecutor() {
        return obdOptThreadExecutor;
    }

    public void setObdOptThreadExecutor(ExecutorService obdOptThreadExecutor) {
        this.obdOptThreadExecutor = obdOptThreadExecutor;
    }

    public ExecutorService getFixedThreadPool() {
        return fixedThreadPool;
    }

    public void setFixedThreadPool(ExecutorService fixedThreadPool) {
        this.fixedThreadPool = fixedThreadPool;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
        //初始化SDK
        initVispectSDK();

        //初始化线程池
        initThreadPool();

        // 注册全局异常捕获者
        initCrashHandler();

        //配置图片加载
        initImageLoader();
        user = new UserInfo();

        wificontroller = new WifiController(this);
    }

    private void initVispectSDK() {
        //初始化vispect的SDK
        deviceHelper = new DeviceHelper();
        deviceHelper.initSDK(this, "7e3f545ec9c24303b0fd5fcfe5e91eab", new Oninit() {
            @Override
            public void onSuccess() {
                XuLog.e(TAG, "SDK Version :" + deviceHelper.getSDKVersionName());
            }

            @Override
            public void onFail(int i) {

            }
        });
    }

    public WifiController getWificontroller() {
        return wificontroller;
    }

    public void setNeeedCloseBluetooth(boolean neeedCloseBluetooth) {
        this.neeedCloseBluetooth = neeedCloseBluetooth;
    }

    public boolean isERROR_CAMERA() {
        return !getDeviceHelper().getCameraStatus();
    }

    public boolean isNeedclosetcp() {
        return needclosetcp;
    }

    public void setNeedclosetcp(boolean needclosetcp) {
        this.needclosetcp = needclosetcp;
    }

    private void initImageLoader() {
        //使用默认配置
        ImageLoaderConfiguration configuration = ImageLoaderConfiguration.createDefault(this);
        ImageLoader.getInstance().init(configuration);
    }

    private void initThreadPool() {
        //TODO  初始化APP需要用到的线程池
        //下载线程池
        downThreadExecutor = Executors.newSingleThreadExecutor();
        //其他作用的线程池
        cachedThreadPool = Executors.newCachedThreadPool();
        //updata
        fixedThreadPool = Executors.newSingleThreadExecutor();
        //OBD操作的线程池
        obdOptThreadExecutor = Executors.newSingleThreadExecutor();
    }

    private void initCrashHandler() {
        //TODO  初始化全局的异常捕获者
        XuCrashHandler crashHandler = XuCrashHandler.getInstance();
        crashHandler.init(this);
        // 发送以前没发送的报告(可选)
        //crashHandler.sendPreviousReportsToServer();
    }

    public DeviceHelper getDeviceHelper() {
        return deviceHelper;
    }

    public UserInfo getUser() {
        return user;
    }

    public void setUser(UserInfo user) {
        this.user = user;
    }

    public void setLastBleName(String lastBleName) {
        this.lastBleName = lastBleName;
    }

    public boolean isS() {
        return AppContext.getInstance().getDeviceHelper().getDeviceType() == 2;
    }

    public int isG2() {
        return AppContext.getInstance().getDeviceHelper().getDeviceType();
    }

    public String getNowModel() {
        return AppConfig.getInstance(this).getLastmode();
    }

    public void setNowModel(String nowModel) {
        AppConfig.getInstance(this).setLastmode(nowModel);
    }

    public String getNowBrand() {
        return AppConfig.getInstance(this).getLastbrand();
    }

    public void setNowBrand(String nowBrand) {
        AppConfig.getInstance(this).setLastbrand(nowBrand);
    }

    public ExecutorService getCachedThreadPool() {
        return cachedThreadPool;
    }

    public void setCachedThreadPool(ExecutorService cachedThreadPool) {
        this.cachedThreadPool = cachedThreadPool;
    }

    public boolean isCanToRealView() {
        return canToRealView;
    }

    public void setCanToRealView(boolean canToRealView) {
        this.canToRealView = canToRealView;
    }

    public String getDownloading_name() {
        return downloading_name;
    }

    public void setDownloading_name(String downloading_name) {
        XuLog.d("DrivingVideosActivity", "downloading_name:" + downloading_name);
        this.downloading_name = downloading_name;
    }

    public boolean isDeveloperMode() {
        return DeveloperMode;
    }

    public void setDeveloperMode(boolean developerMode) {
        DeveloperMode = developerMode;
    }

    public boolean isUpdateing_s() {
        return updateing_s;
    }

    public void setUpdateing_s(boolean updateing_s) {
        this.updateing_s = updateing_s;
    }

    public boolean isApp_error_s() {
        return app_error_s;
    }

    public void setApp_error_s(boolean app_error_s) {
        this.app_error_s = app_error_s;
    }

    public int getCalibrateType() {
        return calibrateType;
    }

    public void setCalibrateType(int calibrateType) {
        this.calibrateType = calibrateType;
    }

    public int getBleRssi() {
        return bleRssi;
    }

    public void setBleRssi(int bleRssi) {
        this.bleRssi = bleRssi;
    }

    public int getCalivrateID() {
        return calivrateID;
    }

    public void setCalivrateID(int calivrateID) {
        this.calivrateID = calivrateID;
    }

    public Point getCalibrateCamera() {
        return _currentCamera;
    }

    public void setCalibrateCamera(Point point) {
        _currentCamera = point;
    }
}
