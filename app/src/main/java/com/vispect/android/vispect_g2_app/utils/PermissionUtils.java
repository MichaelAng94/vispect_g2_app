package com.vispect.android.vispect_g2_app.utils;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;

public class PermissionUtils {

    /**
     * 申请权限
     */
    public static void request(Activity context, int requestCode, String... permissions) {

        if (Build.VERSION.SDK_INT > 22) {
            for (String permission : permissions) {
                int checkPermission = ContextCompat.checkSelfPermission(context, permission);
                if (checkPermission != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(context, new String[]{permission}, requestCode);
                }
            }
        }
    }

    /**
     * 检查是否已获得该项权限
     * 若无 弹出权限申请框
     */
    public static boolean request(@NonNull Activity context, int requestCode, String permission) {
        if (Build.VERSION.SDK_INT > 22) {
            int checkPermission = ContextCompat.checkSelfPermission(context, permission);
            if (checkPermission != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(context, new String[]{permission}, requestCode);
                return false;
            }
        }
        return true;
    }

    /**
     * 检查是否已获得该项权限
     * 若无 弹出权限申请框
     * fragment Fragment 中申请权限
     */
    public static boolean request(@NonNull Activity context, @NonNull Fragment fragment, int requestCode, String permission) {
        if (Build.VERSION.SDK_INT > 22) {
            int checkPermission = ActivityCompat.checkSelfPermission(context, permission);
            if (checkPermission != PackageManager.PERMISSION_GRANTED) {
                fragment.requestPermissions(new String[]{permission}, requestCode);
                return false;
            }
        }
        return true;
    }
}
