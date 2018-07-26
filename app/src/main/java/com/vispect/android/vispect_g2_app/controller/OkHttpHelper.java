package com.vispect.android.vispect_g2_app.controller;

import android.os.Handler;
import android.os.Looper;


import com.google.gson.Gson;
import com.vispect.android.vispect_g2_app.interf.ReqProgressCallBack;
import com.vispect.android.vispect_g2_app.interf.ResultCallback;
import com.vispect.android.vispect_g2_app.utils.XuLog;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


/**
 * OkHttp帮助类
 * Created by benio on 2015/11/16.
 */
public class OkHttpHelper {
    private static final String TAG = "OkHttpHelper";
    /**
     * 连接超时时间，30秒
     */
    public static final long TIME_OUT = 30;
    private static final String CHARSET_NAME = "UTF-8";

    private static OkHttpHelper sInstance;
    private final OkHttpClient mOkHttpClient;
    private final Handler mDelivery;
    private final Gson mGson;

    private static final int DELTA = 256 * 1024;
    private int mTotal;
    private int mCurrentDownload;
    private OutputStream mDstOutputStream;

    private OkHttpHelper() {
        mOkHttpClient = new OkHttpClient.Builder().connectTimeout(TIME_OUT, TimeUnit.SECONDS).readTimeout(TIME_OUT, TimeUnit.SECONDS).writeTimeout(TIME_OUT, TimeUnit.SECONDS).cookieJar(new CookieJar() {
            private final HashMap<HttpUrl, List<Cookie>> cookieStore = new HashMap<>();

            @Override
            public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
                cookieStore.put(url, cookies);
            }

            @Override
            public List<Cookie> loadForRequest(HttpUrl url) {
                List<Cookie> cookies = cookieStore.get(url);
                return cookies != null ? cookies : new ArrayList<Cookie>();
            }
        }).build();
        mDelivery = new Handler(Looper.getMainLooper());
        mGson = new Gson();
    }

    public OkHttpClient getOkHttpClient() {
        return mOkHttpClient;
    }

    public static OkHttpHelper getInstance() {
        if (sInstance == null) {
            synchronized (OkHttpHelper.class) {
                if (sInstance == null) {
                    sInstance = new OkHttpHelper();
                }
            }
        }
        return sInstance;
    }

    public void cancel() {
        mOkHttpClient.dispatcher().cancelAll();

    }

    /**
     * 开启异步线程。
     *
     * @param request
     * @return
     * @throws IOException
     */
    public Response execute(Request request) throws IOException {
        return mOkHttpClient.newCall(request).execute();
    }

    public <T> T execute(Request request, Class<T> clazz) throws IOException {
        Response execute = execute(request);
        String respStr = execute.body().string();
        return mGson.fromJson(respStr, clazz);
    }

    /**
     * 开启异步线程访问网络
     *
     * @param request
     * @param callback
     */
    public <T> void enqueue(final Request request, final ResultCallback<T> callback) {
        if (null == callback) {
            return;
        }
        callback.onPreExecute(request);
        mOkHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                sendFailedCallback(request, e, callback);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    try {
                        sendFailedCallback(request, new RuntimeException(response.body().string()), callback);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return;
                }

                try {
                    final String string = response.body().string();
                    XuLog.d(TAG, "response data: " + string);
                    if (callback.type == String.class) {
                        sendSuccessCallback((T) string, callback);
                    } else {
                        T data = mGson.fromJson(string, callback.type);
                        sendSuccessCallback(data, callback);
                    }
                } catch (IOException e) {
                    //response.body().string()异常
                    sendFailedCallback(response.request(), e, callback);
                } catch (com.google.gson.JsonSyntaxException e) {
                    //Json解析的错误
                    sendFailedCallback(response.request(), e, callback);
                }
            }
        });

    }

    /**
     * 分发成功回调
     *
     * @param response
     * @param callback
     * @param <T>
     */
    private <T> void sendSuccessCallback(final T response, final ResultCallback<T> callback) {
        mDelivery.post(new Runnable() {
            @Override
            public void run() {
                if (callback != null) {
                    callback.onResponse(response);
                    callback.onAfterExecute();
                }
            }
        });
    }

    /**
     * 分发失败回调
     *
     * @param request
     * @param e
     * @param callback
     * @param <T>
     */
    private <T> void sendFailedCallback(final Request request, final Exception e, final ResultCallback<T> callback) {
        mDelivery.post(new Runnable() {
            @Override
            public void run() {
                if (callback != null) {
                    callback.onFailure(request, e);
                    callback.onAfterExecute();
                }
            }
        });
    }



    /**
     * 下载文件
     * @param fileUrl 文件url
     * @param destFileDir 存储目标目录
     * @param callBack 进度和结果回调
     */
    public  void downLoad(final String fileUrl, final String destFileDir, final ReqProgressCallBack callBack){
        getFileLength(fileUrl,new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    //获取下载文件长度
                    mCurrentDownload = 0;
                    mTotal = Integer.valueOf(response.header("Content-Length"));
                    mDstOutputStream = new FileOutputStream(destFileDir);
                    downloadRange(fileUrl,0,callBack);
                }
            }
        });
    }

    //获取文件大小
    private void getFileLength(String fileUrl, Callback callback) {
        Request request = new Request.Builder()
                .url(fileUrl)
                .method("HEAD", null).build();
        mOkHttpClient.newCall(request).enqueue(callback);
    }

    private void downloadRange(final String fileUrl, int start, final ReqProgressCallBack callBack) {
        //分段下载，range参数将文件进行分片
//        XuLog.d(TAG,"下载的参数：  fileUrl："+fileUrl+"    start:"+start+"   mTotal:"+mTotal);
        Request request = new Request.Builder()
                .url(fileUrl)
                .addHeader("range", "bytes=" + start + "-" + Math.min(start + DELTA, mTotal)).build();


        mOkHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                mCurrentDownload = 0;
                mTotal = 0;
                callBack.onFile(e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    final byte[] bytes = response.body().bytes();
                    mDstOutputStream.write(bytes);
                    mCurrentDownload += bytes.length;
                    callBack.onProgress(mTotal, mCurrentDownload);
                    int progress = (int)(mCurrentDownload * 1.0 / mTotal * 100);
                    if (100 == progress) {
                        mCurrentDownload = 0;
                        mTotal = 0;
                        callBack.onDone();
                        return;
                    }
                    downloadRange(fileUrl, mCurrentDownload, callBack);
                }
            }
        });
    }


}
