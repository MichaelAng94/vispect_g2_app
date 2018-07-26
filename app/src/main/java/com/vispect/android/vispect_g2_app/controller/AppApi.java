package com.vispect.android.vispect_g2_app.controller;

import android.support.v4.util.SimpleArrayMap;


import com.vispect.android.vispect_g2_app.R;
import com.vispect.android.vispect_g2_app.app.AppContext;
import com.vispect.android.vispect_g2_app.bean.ADASDevice;
import com.vispect.android.vispect_g2_app.bean.CarInfo;
import com.vispect.android.vispect_g2_app.bean.InstallVideo;
import com.vispect.android.vispect_g2_app.bean.ResultData;
import com.vispect.android.vispect_g2_app.bean.SOBDCrackValue;
import com.vispect.android.vispect_g2_app.bean.UserInfo;
import com.vispect.android.vispect_g2_app.bean.VispectUpdateFile;
import com.vispect.android.vispect_g2_app.interf.ReqProgressCallBack;
import com.vispect.android.vispect_g2_app.interf.ResultCallback;
import com.vispect.android.vispect_g2_app.utils.XuLog;
import com.vispect.android.vispect_g2_app.utils.XuToast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;


/**
 * 联网操作类
 * Created by xu on 2016/03/11.
 */
public class AppApi {

    private static final String TAG = "AppApi";
    private static final String URL_BASE = "http://120.76.100.103:8080/Vispect_service_test/";    //URL前缀
//    private static final String URL_BASE = "http://192.168.8.251:8080/";    //URL前缀(本地服务器)


    /**
     * 服务器返回的错误码
     * */
    public static class ApiErrorCode{
        public static int SUCCESS = 0;                   //成功
        public static int PARAMETER_ERROR = 1;           //参数错误
        public static int USER_EXITED = 100;             //用户已存在
        public static int USER_UNFIND = 101;             //用户不存在
        public static int HASEXCEPTION = 102;            //服务端发生异常
        public static int PASSWORDERROR = 103;           //密码错误
        public static int NOTVALUE = 104;                //数值为空
        public static int OBDVALUE_EXITED = 105;         //破解数据已存在
        public static int OBDVALUE_FROMAT_ERROR = 106;   //破解数据格式错误
        public static int OBDVALUE_SAVE_FAIL = 107;      //保存破解数据失败
        public static int FEEDBACK_FROMAT_ERROR = 108;   //反馈信息格式错误
        public static int FEEDBACK_SAVE_ERROR = 109;     //反馈信息报错失败
        public static int CHECK_UPDATE_FAIL = 110;       //检查更新失败
        public static int ADD_UPDATE_FAIL = 111;         // 保存更新信息失败
        public static int GET_BRANDS_FAIL = 112;         //获取品牌信息失败
        public static int TYPE_INCORRET = 113;           //类型错误
        public static int GET_MODEL_FAIL = 114;          //获取型号失败
        public static int GET_OBD_CRASH_VALUE_FAIL = 115;//获取OBD破解数据失败
        public static int WECHATLOGINFAIL = 116;          //微信登录失败
        public static int FACEBOOKLOGINFAIL = 117;        //facebook登录失败
        public static int APPIDWRONGFUL = 118;            //APPID错误
        public static int APPIDCREATEERROR = 119;         //APPID创建失败
        public static int APPID_EXITED = 120;             //APPID已存在
        public static int COMPANY_EXITED = 121;           //公司信息已存在
        public static int COMPANYCREATEERROR = 122;       //公司信息创建失败
        public static int UPDATEAVATARFAIL = 123;         //更新头像失败
        public static int UPDATEAVATARENCTYPEERROR = 124; //头像图片文件类型错误
        public static int AVATARFEILSIZEEOORE = 125;      //图片文件长度错误
        public static int DEVICEID_UNFIND = 126;          //未找到设备
        public static int DATACOLLECTIONFAIL = -1;        //数据收集失败

    }
    /**
     * http://192.168.8.14:8080/Vispect_service_test/login
     * 登录
     *
     * @param type : 0是正常登录   1是微信登录    2是facebook登录
     */

    public static void login(String name, String password, int type, String code, String fbuserid, ResultCallback<ResultData<UserInfo>> callback) {
        SimpleArrayMap<String, String> params = new SimpleArrayMap<>();
        params.put("name", name);
        params.put("password", password);
        params.put("type", type+"");
        params.put("code", code);
        params.put("fbuserid", fbuserid);
        post(URL_BASE+"login", params, callback);
    }

    /**
     * http://192.168.8.14:8080/Vispect_service_test/register
     * 注册
     */
    public static void register(String name, String password, ResultCallback<ResultData<String>> callback) {
        SimpleArrayMap<String, String> params = new SimpleArrayMap<>();
        params.put("name", name);
        params.put("password", password);
        post(URL_BASE+"register", params, callback);
    }

    /**
     * http://192.168.8.14:8080/Vispect_service_test/war
     * 上传报警记录
     */
    public static void saveWar(int type, int did,int uid,ResultCallback<ResultData<String>> callback) {
        SimpleArrayMap<String, String> params = new SimpleArrayMap<>();
        SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        params.put("type", type+"");
        params.put("did", did+"");
        params.put("uid", uid+"");
        params.put("time", format.format(System.currentTimeMillis()));
        post(URL_BASE+"war", params, callback);
    }

    /**
     * http://192.168.8.14:8080/Vispect_service_test/crash
     * 上传崩溃记录
     */
    public static void saveCrash(String msg, int uid, ResultCallback<ResultData<String>> callback) {
        SimpleArrayMap<String, String> params = new SimpleArrayMap<>();
        SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        params.put("msg", msg);
        params.put("uid", uid+"");
        params.put("time", format.format(System.currentTimeMillis()));
        post(URL_BASE+"crash", params, callback);
    }

       /**
     * http://192.168.8.14:8080/Vispect_service_test/getUserInfo
     * 获取用户信息
     */
    public static void getUserInfo(int uid,ResultCallback<ResultData<UserInfo>> callback) {
        SimpleArrayMap<String, String> params = new SimpleArrayMap<>();
        params.put("uid", uid+"");
        post(URL_BASE+"getUserInfo", params, callback);
    }

    /**
     * http://120.76.100.103:8080/Vispect_service_test/modifyUserInfo
     * 修改用户信息
     */
    public static void modifyUserInfo(int uid, int sex, String phone, String email, ResultCallback<ResultData<String>> callback) {
        SimpleArrayMap<String, String> params = new SimpleArrayMap<>();
        params.put("uid", uid+"");
        params.put("sex", sex+"");
        params.put("phone", phone);
        params.put("email", email);
        post(URL_BASE+"modifyUserInfo", params, callback);
    }

    /**
     * http://120.76.100.103:8080/Vispect_service_test/obdvaluelist
     * 获取OBD数据列表
     */
    public static void getOBDValuesList(int start,int count,ResultCallback<ResultData<ArrayList<SOBDCrackValue>>> callback) {
        SimpleArrayMap<String, String> params = new SimpleArrayMap<>();
        params.put("start", start+"");
        params.put("count", count+"");
        post(URL_BASE+"obdvaluelist", params, callback);
    }

    /**
     * http://120.76.100.103:8080/Vispect_service_test/addobdvalue
     * 保存OBD破解数据
     */
    public static void saveOBDValues(int uid, int did, String brand, String year, String model, String value, ResultCallback<ResultData<String>> callback) {
        SimpleArrayMap<String, String> params = new SimpleArrayMap<>();
        params.put("uid", uid+"");
        params.put("did", did+"");
        params.put("brand", brand);
        params.put("year", year);
        params.put("model", model);
        params.put("value", value);
        post(URL_BASE + "addobdvalue", params, callback);
    }

    /**
     *  http://120.76.100.103:8080/Vispect_service_test/checkhasupdate
     * 检查是否有更新
     * @param type //1：V固件  2：V系统软件 3：操作系统 4：App 5：S系统软件 6 : IOS App  7:戬爱版的V系统软件 8：国脉版本 9  10
     * @param  callback   结果回调
     */
    public static void checkUpdate(int type,int hasLimitingCondition,int obdversion,int buzzersion,ResultCallback<ResultData<VispectUpdateFile>> callback) {
        SimpleArrayMap<String, String> params = new SimpleArrayMap<>();
        params.put("type", type+"");
        params.put("limitingcondition",hasLimitingCondition+"");
        params.put("obdversion", obdversion+"");
        params.put("buzzerversion", buzzersion+"");
        post(URL_BASE + "checkhasupdate", params, callback);
    }

    /**
     *  http://120.76.100.103:8080/Vispect_service_test/getbrandormodel
     * 获取车型
     * @param type //0：品牌   1：型号
     * @param brand //品牌 （只有在type = 1时必填）
     * @param  callback   结果回调
     */
    public static void getbrandormodel(int type, String brand, ResultCallback<ResultData<ArrayList<CarInfo>>> callback) {
        SimpleArrayMap<String, String> params = new SimpleArrayMap<>();
        params.put("type", type+"");
        params.put("brand", brand);
        post(URL_BASE + "getbrandormodel", params, callback);
    }

    /**
     *  http://120.76.100.103:8080/Vispect_service_test/getobdvalue
     * 获取OBD数据
     * @param brand //品牌
     * @param model //车型
     * @param  callback   结果回调
     */
    public static void getobdvalue(String brand, String model, ResultCallback<ResultData<SOBDCrackValue>> callback) {
        SimpleArrayMap<String, String> params = new SimpleArrayMap<>();
        params.put("brand", brand);
        params.put("model", model);
        post(URL_BASE + "getobdvalue", params, callback);
    }


    /**
     *  http://120.76.100.103:8080/Vispect_service_test/updateavatar
     * 更新用户头像
     * @param uid：用户ID
     * @param path：图片路径
     * @param  callback   结果回调
     */
    public static void updateAvatar(int uid, String path, ResultCallback<ResultData<String>> callback) {
        postFile(URL_BASE + "updateavatar", uid, path, callback);
    }

    /**
     *  http://120.76.100.103:8080/Vispect_service_test/getInstallVideoList
     * 获取视频列表
     * @param type：类型    0：V款安装视频
     * @param  callback   结果回调
     */
    public static void getInstallVideoList(int type,ResultCallback<ResultData<ArrayList<InstallVideo>>> callback) {
        SimpleArrayMap<String, String> params = new SimpleArrayMap<>();
        params.put("type", type+"");
        post(URL_BASE + "getInstallVideoList", params, callback);
    }

    /**
     *  http://120.76.100.103:8080/Vispect_service_test/CreateDeviceId
     * 获取一个UUID
     * @param  callback   结果回调
     */
    public static void getDeviceId(ResultCallback<ResultData<ADASDevice>> callback) {
        SimpleArrayMap<String, String> params = new SimpleArrayMap<>();
        post(URL_BASE + "CreateDeviceId", params, callback);
    }

    /**
     *  http://120.76.100.103:8080/Vispect_service_test/ConsumptionDeviceId
     * 消费一个UUID
     * @param  callback   结果回调
     */
    public static void consumptionDeviceId(int deviceId,ResultCallback<ResultData<String>> callback) {
        SimpleArrayMap<String, String> params = new SimpleArrayMap<>();
        params.put("deviceId", deviceId+"");
        post(URL_BASE + "ConsumptionDeviceId", params, callback);
    }
    /**
     *  http://120.76.100.103:8080/Vispect_service_test/ConsumptionDeviceIdList
     * 消费一堆UUID
     * @param  callback   结果回调
     */
    public static void consumptionDeviceIdList(String deviceIds, ResultCallback<ResultData<String>> callback) {
        SimpleArrayMap<String, String> params = new SimpleArrayMap<>();
        params.put("deviceId", deviceIds);
        post(URL_BASE + "ConsumptionDeviceIdList", params, callback);
    }

    /**
     *  http://120.76.100.103:8080/Vispect_service_test/savADASEvent
     * 保存一个ADAS事件到服务器
     * @param  callback   结果回调
     */
    public static void saveADASEvent(String adasEvent, ResultCallback<ResultData<String>> callback){
        SimpleArrayMap<String, String> params = new SimpleArrayMap<>();
        params.put("adasevent", adasEvent);
        params.put("deivceid", "12345678");
        post(URL_BASE + "savADASEvent", params, callback);
    }

    /**
     *  http://120.76.100.103:8080/Vispect_service_test/saveDriverEvent
     * 保存一个驾驶事件到服务器
     * @param  callback   结果回调
     */
    public static void saveDriverEvent(String DriverEvent, ResultCallback<ResultData<String>> callback){
        SimpleArrayMap<String, String> params = new SimpleArrayMap<>();
        params.put("driverevent", DriverEvent);
        post(URL_BASE + "saveDriverEvent", params, callback);
    }

    /**
     *  http://120.76.100.103:8080/Vispect_service_test/saveOBDData
     * 保存一个OBD数据到服务器
     * @param  callback   结果回调
     */
    public static void saveOBDData(String obdData, ResultCallback<ResultData<String>> callback){
        SimpleArrayMap<String, String> params = new SimpleArrayMap<>();
        params.put("obddata", obdData);
        post(URL_BASE + "saveOBDData", params, callback);
    }

    /**
     *  http://120.76.100.103:8080/Vispect_service_test/saveGPSSenser
     * 保存一个GOS和传感器数据到服务器
     * @param  callback   结果回调
     */
    public static void saveGPSSenser(String gpssenser, ResultCallback<ResultData<String>> callback){
        SimpleArrayMap<String, String> params = new SimpleArrayMap<>();
        params.put("gpssenser", gpssenser);
        post(URL_BASE + "saveGPSSenser", params, callback);
    }


    private static <T> void post(String url, ResultCallback<T> callback) {
        Request request = new Request.Builder().tag(TAG).url(url).build();
        OkHttpHelper.getInstance().enqueue(request, callback);
        XuLog.d(TAG, "request url: " + url);
    }

    private static <T> void post(String url, SimpleArrayMap<String, String> params, ResultCallback<T> callback) {
        FormBody.Builder formEncodingBuilder = new FormBody.Builder();

        for (int i = 0; i < params.size(); i++) {
            String key = params.keyAt(i);
            String value = params.valueAt(i);
            formEncodingBuilder.add(key, value);
        }
        RequestBody body = formEncodingBuilder.build();
        Request request = new Request.Builder().tag(TAG).url(url).post(body).build();
        OkHttpHelper.getInstance().enqueue(request, callback);
        XuLog.d(TAG, "request url: " + url);
        XuLog.d(TAG, "request params: " + params.toString());
    }

    /**
     * 文件下载
     *
     * @param fileUrl    下载文件的路径
     * @param destFileDir      存储地址
     * @param callback 回调接口
     */
    public static void download(String fileUrl, String destFileDir, ReqProgressCallBack callback) {
        OkHttpHelper.getInstance().downLoad(fileUrl, destFileDir, callback);
    }




    private static <T> void postFile(String url, int uid, String path, ResultCallback<T> callback) {
        File file = new File(path);
        MultipartBody.Builder requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM);
        if(file != null){
            // MediaType.parse() 里面是上传的文件类型。
            RequestBody body = RequestBody.create(MediaType.parse("image/*"), file);
            String filename = file.getName();
            // 参数分别为， 请求key ，文件名称 ， RequestBody
            requestBody.addFormDataPart("avatar", filename, body);
            requestBody.addFormDataPart("uid",uid+"");
            Request request = new Request.Builder()
                    .url(url).tag(TAG)
                    .post(requestBody.build())
                    .build();
            OkHttpHelper.getInstance().enqueue(request, callback);
            XuLog.e(TAG, "request url: " + url);
            XuLog.e(TAG, "request params: " + requestBody.toString());
        }else{
            XuToast.show(AppContext.getInstance(), AppContext.getInstance().getResources().getString(R.string.update_not_find_file));
        }
//        RequestBody requestBody = MultipartBody.create(MediaType.parse("application/octet-stream"),file);

    }






}
