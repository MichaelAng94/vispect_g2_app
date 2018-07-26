package com.vispect.android.vispect_g2_app.interf;

/**
 * Created by xu on 2017/1/23.
 */
public interface ReqProgressCallBack<T>  {
    /**
     * 响应进度更新
     */
    void onProgress(long total, long current);
    /**
     * 下载失败
     */
    void onFile(String errorMessage);
    /**
     * 下载完成
     */
    void onDone();
}
