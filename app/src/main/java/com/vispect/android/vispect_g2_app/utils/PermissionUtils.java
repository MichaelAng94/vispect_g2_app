package com.vispect.android.vispect_g2_app.utils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

public class PermissionUtils {

    /**
     * 申请权限
     */
    public static void request(Activity context, int requestCode, String... permissions) {
        for (String permission : permissions) {
            int checkPermission = ContextCompat.checkSelfPermission(context, permission);
            if (checkPermission != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(context, new String[]{permission}, requestCode);
            }
        }
    }

    /**
     * 检查是否已获得该项权限
     * 若无 弹出权限申请框
     */
    public static boolean request(Activity context, int requestCode, String permission) {
        int checkPermission = ContextCompat.checkSelfPermission(context, permission);
        if (checkPermission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(context, new String[]{permission}, requestCode);
            return false;
        }
        return true;
    }
}
