package com.vispect.android.vispect_g2_app.app;

import android.content.Context;
import android.os.Environment;

import com.vispect.android.vispect_g2_app.utils.XuLog;
import com.vispect.android.vispect_g2_app.utils.XuSp;
import com.vispect.android.vispect_g2_app.utils.XuString;

import java.io.File;
import java.util.Locale;


/**
 * 配置信息类
 * Created by xu on 2016/03/11.
 */
public class AppConfig {


    public final static String CARPETSERVER_PACKAGENAME = "com.vispect.carpet";    //设备APP的包名
    public final static String CARPETWATCHDOG_PACKAGENAME = "com.vispect.watchdog";    //设备看门狗的包名
    public final static String CARPETLAUNCHER_PACKAGENAME = "com.vispect.launcher";    //设备launcher的包名
    //头像名称
    public static final String IMAGE_USER_AVATAR_NAME = "/user_avatar.jpg";
    //requestCode
    public static final int REQUEST_CODE_CAMERA_PERMISSION = 0;
    public static final int REQUEST_CODE_GLOBAL_PERMISSION = 1;
    public static final int REQUEST_CODE_CAMERA = 2;//拍照
    public static final int REQUEST_CODE_ALBUM = 3;//从相册中选择
    public static final int REQUEST_CODE_CROP_IMAGE = 4;//裁剪图片
    //resultCode
    public static final int RESULT_CODE_OK = 100;//连接设备成功
    //String Extra
    public static final String STRING_EXTRA = "stringExtra";
    public static final String EXTRA_TO_INSTALLATION = "toInstallation";
    public static final String EXTRA_TO_SETTING = "toSetting";
    /**
     * Socker的配置参数
     */
    public static final String SOCKET_STREAM_SERVICE_HOST = "192.168.43.1";
    public static final int SOCKET_STREAM_SERVICE_PORT = 9000;    //下载视频
    public static final int SOCKET_BLE_SERVICE_PORT = 9001;       //实时路况通讯
    public static final int SOCKET_UPDATA_PORT = 9002;       //上传文件
    public static final int SOCKET_ADAS_SERVICE_PORT = 1942;
    /**
     * 是否第一次运行
     */
    public final static String APP_FIRST_START = "APP_FIRST_START";
    /**
     * 默认存放图片的路径
     */
    public final static String DEFAULT_SAVE_IMAGE_PATH = Environment.getExternalStorageDirectory() + "/vispect/";
    public static final long TS_MAX = 253402271999000L;// 最大有限期
    /**
     * 行车记录
     */
    public static final String APP_NAME = "vispect_app";
    /**
     * 硬件升级
     */
    public static final String OTA_NAME = "ota.zip";
    public static final String OTA_SVALE_DIR = "OTA/" + OTA_NAME;
    /**
     * 第三方登录需要的配置
     */
    public static final String WX_APP_ID = "wxf1ca6af6d86907bb";
    /**
     * 安装视频的链接
     */
    public static final String INSTALL_VIDEO_URL = "http://v.youku.com/v_show/id_XMjcxODI1NTUzNg==.html?spm=a2h0k.8191407.0.0&from=s1.8-1-1.2";
    /**
     * SP对应的KEY
     */
    private final static String APP_CONFIG = "APP_CONFIG";
    private final static String USER_ID = "USER_ID";
    private final static String BLE_PWD = "BLE_PWD";
    private final static String BLE_TOKEN = "BLE_TOKEN";
    private final static String WIFI_PWD = "WIFI_PWD";
    private final static String WIFI_NAME = "WIFI_NAME";
    private final static String BLE_NAME = "BLE_NAME";
    private final static String INTERVAL_ADAS = "INTERVAL_ADAS";
    private final static String SEBSITIVITYLEVEL = "SEBSITIVITYLEVEL";
    private final static String STARTSPEED = "STARTSPEED";
    private final static String CAR_WIDTH = "CARWIDTH";
    private final static String CALIBRATION = "CALIBRATION";
    private final static String FIRSTSTARTCHECK = "FIRSTSTARTCHECK";
    private final static String USER = "USER";
    private final static String OBDVERSION = "OBD_VERSION";
    private final static String BUZZERVERSION = "BUZZER_VERSION";
    private final static String GPSVERSION = "GPS_VERSION";
    private final static String OPENCOUNT = "OPNE_COUNT";
    //    public static final int SOCKET_STREAM_SERVICE_PORT = 8821;
    private final static String CURRENT_DEVICE_NAME = "CURRENT_DEVICE_NAME";    //临时
    private final static String AUTO = "AUTO";    //临时
    private final static String LASTUID = "LASTUID";    //临时保存上次保存数据的用户的ID
    private final static String LASTBRAND = "LASTBRAND";    //临时保存上次保存的车的品牌
    private final static String LASTMODE = "LASTMODE";    //临时保存上次保存的车的型号
    private final static String LASTREGION = "LASTREGION";    //临时保存上次保存的地区
    private final static String LASTYEAR = "LASTYEAR";    //临时保存上次保存的车的年份
    private final static String LASTDATACAIRDID = "LASTDATACAIRDID";    //临时保存上次保存的数据卡ID
    public static Boolean showSSID = false;
    public static String DEVICE_NAME = "unkonwDevoce";
    public static String EXTERNAL_DIR = getExternalStoragePath() + File.separator + APP_NAME;
    private static AppConfig xInstance;
    private static XuSp xSharedPreferences;
    private static String CARCORDER_DIR = EXTERNAL_DIR + "/" + DEVICE_NAME + File.separator;
    /**
     * 是否已连接BLE设备
     */
    private boolean BLE_CONNECTED = false;


    private AppConfig() {

    }

    public static String getCarcorderDir() {
        return EXTERNAL_DIR + "/" + DEVICE_NAME + File.separator;
    }

    public static void setCarcorderDir(String carcorderDir) {
        CARCORDER_DIR = carcorderDir;
    }

    public static AppConfig getInstance(Context context) {
        if (null == xInstance) {
            xInstance = new AppConfig();
            xSharedPreferences = new XuSp(context, APP_CONFIG, Context.MODE_PRIVATE);
        }
        return xInstance;
    }

    public static String getWxAppId() {
        return WX_APP_ID;
    }

    public static boolean isZh(Context ct) {
        Locale locale = ct.getResources().getConfiguration().locale;
        String language = locale.getLanguage();
        if (language.endsWith("zh"))
            return true;
        else
            return false;
    }

    public static String getDeviceName() {
        return DEVICE_NAME;
    }

    public static void setDeviceName(String deviceName) {
        DEVICE_NAME = deviceName;
    }

    /**
     * get the external storage file path
     *
     * @return the file path
     */
    public static String getExternalStoragePath() {
        return getExternalStorageDir().getAbsolutePath();
    }

    public static File getExternalStorageDir() {
        return Environment.getExternalStorageDirectory();
    }

    public boolean islogin() {

        if (XuString.isEmpty(getUserId())) {
            return false;
        } else {
            return true;
        }


    }

    public String getUserId() {
        return xSharedPreferences.get(USER_ID, null);
    }

    public void setUserId(String uid) {
        xSharedPreferences.put(USER_ID, uid);
    }

    public boolean getFirstStart() {
        return xSharedPreferences.get(APP_FIRST_START, true);
    }

    public void setFirstStart(boolean firstStart) {
        xSharedPreferences.put(APP_FIRST_START, firstStart);
    }

    public String getBle_paw() {
        return xSharedPreferences.get(BLE_PWD, "6666");
    }

    public boolean setBle_paw(String value) {
        return xSharedPreferences.put(BLE_PWD, value);
    }

    public String getWifi_paw() {
        return xSharedPreferences.get(WIFI_PWD, "12345678");
    }

    public boolean setWifi_paw(String value) {
        return xSharedPreferences.put(WIFI_PWD, value);
    }

    public String getWifi_name() {
        return xSharedPreferences.get(WIFI_NAME, "CarPet_AndroidAp");
    }

    public boolean setWifi_name(String value) {
        return xSharedPreferences.put(WIFI_NAME, value);
    }

    public boolean setBle_name(String value) {
        return xSharedPreferences.put(BLE_NAME, value);
    }

    public String getBle_name() {
        return xSharedPreferences.get(BLE_NAME, "adas");
    }

    //临时
    public boolean setCurrentDeviceName(String value) {
        return xSharedPreferences.put(CURRENT_DEVICE_NAME, value);
    }

    public String getCurrentDeviceName() {
        return xSharedPreferences.get(CURRENT_DEVICE_NAME, "VISPECT_ADAS_V1");
    }

    public boolean setAUTOe(boolean auto) {
        return xSharedPreferences.putBoolean(AUTO, auto);
    }

    public boolean getAUTO() {
        return xSharedPreferences.getBoolean(AUTO, true);
    }

    public String getBle_Token() {
        return xSharedPreferences.get(BLE_TOKEN, "00000000");
    }

    public void setBle_Token(String token) {
        xSharedPreferences.put(BLE_TOKEN, token);
    }

    public int getOpenCount() {
        return xSharedPreferences.get(OPENCOUNT, 0);
    }

    public void settOpenCount(int count) {
        xSharedPreferences.put(OPENCOUNT, count);
    }

    public void setBleConnected(boolean bleConnected) {
        BLE_CONNECTED = bleConnected;
    }

    public boolean setInterval_ADAS(int value) {
        return xSharedPreferences.put(INTERVAL_ADAS, value);
    }

    public int getInterval_ADAS() {
        return xSharedPreferences.get(INTERVAL_ADAS, 150);
    }

    public boolean setCar_width(int value) {
        return xSharedPreferences.put(CAR_WIDTH, value);
    }

    public int getCar_width() {
        return xSharedPreferences.get(CAR_WIDTH, 1600);
    }

    public boolean set_CALIBRATION(float value) {
        return xSharedPreferences.put(CALIBRATION, value);
    }

    public float get_CALIBRATION() {
        return xSharedPreferences.getFloat(CALIBRATION, 0);
    }

    public String getSensitivityLecel() {
        return xSharedPreferences.get(SEBSITIVITYLEVEL, "99,99,99");
    }

    public boolean setSensitivityLecel(String vlaue) {
        return xSharedPreferences.put(SEBSITIVITYLEVEL, vlaue);
    }

    public boolean setFirstartCheck(String vlaue) {
        return xSharedPreferences.put(FIRSTSTARTCHECK, vlaue);
    }

    public String getFirstartCheck() {
        return xSharedPreferences.get(FIRSTSTARTCHECK, null);
    }

    public String getStartSpeed() {
        return xSharedPreferences.get(STARTSPEED, "50,50,20");
    }

    public boolean setStartSpeed(String vlaue) {
        return xSharedPreferences.put(STARTSPEED, vlaue);
    }

    public boolean setUser(String user) {
        return xSharedPreferences.put(USER, user);
    }

    public String getUser() {
        return xSharedPreferences.get(USER, "");
    }

    public boolean setOBDVersion(String version) {
        XuLog.e("setOBDVersion:" + version);
        return xSharedPreferences.put(OBDVERSION, version);
    }

    public String getOBDVersion() {
        return xSharedPreferences.get(OBDVERSION, "");
    }

    public boolean setBuzzerVersion(String version) {
        XuLog.e("setBuzzerVersion:" + version);
        return xSharedPreferences.put(BUZZERVERSION, version);
    }

    public String getBuzzerVersion() {
        return xSharedPreferences.get(BUZZERVERSION, "");
    }

    public boolean setGPSVersion(String version) {
        XuLog.e("setGPSVersion:" + version);
        return xSharedPreferences.put(GPSVERSION, version);
    }

    public String getGPSVersion() {
        return xSharedPreferences.get(GPSVERSION, "");
    }

    //临时保存本地的数据
    public String getLastuid() {
        return xSharedPreferences.get(LASTUID, "");
    }

    public boolean setLastuid(String uid) {
        return xSharedPreferences.put(LASTUID, uid);
    }

    public String getLastbrand() {
        return xSharedPreferences.get(LASTBRAND, "    ");
    }

    public boolean setLastbrand(String brand) {
        return xSharedPreferences.put(LASTBRAND, brand);
    }

    public String getLastmode() {
        return xSharedPreferences.get(LASTMODE, "    ");
    }

    public boolean setLastmode(String mode) {
        return xSharedPreferences.put(LASTMODE, mode);
    }

    public String getLasregion() {
        return xSharedPreferences.get(LASTREGION, "");
    }

    public boolean setLasregion(String region) {
        return xSharedPreferences.put(LASTREGION, region);
    }

    public String getLascaryear() {
        return xSharedPreferences.get(LASTYEAR, "");
    }

    public boolean setLascaryear(String year) {
        return xSharedPreferences.put(LASTYEAR, year);
    }

    public String getLasdatacarid() {
        return xSharedPreferences.get(LASTDATACAIRDID, "");
    }

    public boolean setLasdatacarid(String caidid) {
        return xSharedPreferences.put(LASTDATACAIRDID, caidid);
    }
}
