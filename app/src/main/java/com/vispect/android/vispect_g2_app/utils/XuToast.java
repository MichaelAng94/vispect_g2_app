package com.vispect.android.vispect_g2_app.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * Toast工具类
 * Created by xu on 2016/03/11.
 */
public class XuToast {

    static Toast toast;
    static String oldMsg;
    static long oneTime;
    static long twoTime;
    public static void show(Context ctx, int resId) {
        show(ctx, Toast.LENGTH_SHORT, resId);
    }

    public static void show(Context ctx, CharSequence text) {
        show(ctx, Toast.LENGTH_SHORT, text);
    }

    public static void show(Context ctx, int duration, int resID) {
        show(ctx, duration, ctx.getString(resID));
    }

    public static void show(Context ctx, CharSequence text , int duration) {
        show(ctx, duration, text);
    }

    public static void show(Context ctx, int duration, CharSequence text) {
        if (toast == null) {
            toast = Toast.makeText(ctx, text, duration);
            oldMsg = text.toString();
            toast.show();
            oneTime = System.currentTimeMillis();
        } else {
            twoTime = System.currentTimeMillis();
            if (text.toString().equals(oldMsg)) {
                if (twoTime - oneTime > 3000) {
                    toast.show();
                    oneTime = twoTime;
                }
            } else {
                oldMsg = text.toString();
                toast.cancel();
//                toast.setText(text);
                toast = Toast.makeText(ctx, text, duration);
                toast.show();
            }
        }

    }

    private XuToast() {
    }


    /**
     * 弹出Toast消息
     *
     * @param msg
     */
    public static void ToastMessage(Context cont, String msg) {




    }

    }
