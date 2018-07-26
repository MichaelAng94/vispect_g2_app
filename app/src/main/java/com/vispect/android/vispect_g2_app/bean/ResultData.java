package com.vispect.android.vispect_g2_app.bean;

import com.google.gson.annotations.SerializedName;

/**
 * 结果返回
 *
 * Created by xu on 2015/12/26.
 */
public class ResultData<T> {
    @SerializedName("result")
    private int result;
    @SerializedName("msg")
    private T msg;

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public T getMsg() {
        return msg;
    }

    public void setMsg(T msg) {
        this.msg = msg;
    }

    @Override
    public String toString() {
        return "ResultData{" +
                "result=" + result +
                ", msg=" + msg +
                '}';
    }
}