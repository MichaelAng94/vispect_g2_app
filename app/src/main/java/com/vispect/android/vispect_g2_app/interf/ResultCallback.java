package com.vispect.android.vispect_g2_app.interf;

import com.google.gson.internal.$Gson$Types;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import okhttp3.Request;

/**
 * 网络回调接口
 *
 * @param <T>
 */
public abstract class ResultCallback<T> {
    public final Type type;

    public ResultCallback() {
        type = getSuperclassTypeParameter(getClass());
    }

    static Type getSuperclassTypeParameter(Class<?> subclass) {
        Type superclass = subclass.getGenericSuperclass();
        if (superclass instanceof Class) {
            throw new RuntimeException("Missing type parameter.");
        }
        ParameterizedType parameterizedType = (ParameterizedType) superclass;
        return $Gson$Types.canonicalize(parameterizedType.getActualTypeArguments()[0]);
    }

    public void onPreExecute(Request request) {
    }

    public void onAfterExecute() {
    }

    /**
     * 请求的响应结果为失败时调用
     */
    public abstract void onFailure(Request request, Exception e);

    /**
     * 请求的响应结果为成功时调用
     *
     * @param response 返回的数据
     */
    public abstract void onResponse(T response);
}