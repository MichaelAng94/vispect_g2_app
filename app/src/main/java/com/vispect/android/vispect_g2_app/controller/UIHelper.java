package com.vispect.android.vispect_g2_app.controller;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Display;

import com.vispect.android.vispect_g2_app.R;
import com.vispect.android.vispect_g2_app.app.AppContext;
import com.vispect.android.vispect_g2_app.bean.ARG;
import com.vispect.android.vispect_g2_app.interf.OnClickYesOrNoListener;
import com.vispect.android.vispect_g2_app.ui.activity.DocActivity;
import com.vispect.android.vispect_g2_app.ui.activity.SelectCarModelActivity;
import com.vispect.android.vispect_g2_app.ui.activity.VMainActivity;
import com.vispect.android.vispect_g2_app.ui.activity.VideoPlayerActivity;
import com.vispect.android.vispect_g2_app.ui.widget.NotconnectDialog;
import com.vispect.android.vispect_g2_app.utils.XuLog;


/**
 * 控制显示页面
 * Created by xu on 2016/03/11.
 */
public class UIHelper {
    /**
     * 启动class对应的Activity
     *
     * @param activity
     * @param clazz
     */
    public static void startActivity(Activity activity, Class<? extends Activity> clazz) {
        Intent intent = new Intent(activity, clazz);
        activity.startActivity(intent);
    }



    /**
     * startActivityForResult启动class对应的Activity
     *
     * @param activity
     * @param clazz
     * @param requestCode
     */
    public static void startActivityForresult(Activity activity, Class<? extends Activity> clazz, int requestCode) {
        Intent intent = new Intent(activity, clazz);
        activity.startActivityForResult(intent, requestCode);
    }

    private UIHelper() {
    }

    public static void showDoc(Activity activity) {
        // TODO 跳到文档页面
        Intent intent = new Intent(activity, DocActivity.class);
        activity.startActivityForResult(intent, 999);
    }

    public static int dp2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    private static DialogInterface askDialog;

    public static DialogInterface showAsk(Context context, String msg,Boolean CanceledOnTouchOutside, final OnClickYesOrNoListener listener) {

        try {
            if (askDialog != null) {
                askDialog.dismiss();
            }
            NotconnectDialog.Builder builder = new NotconnectDialog.Builder(context);
            builder.setMessage(msg);
            builder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    askDialog = dialog;
                    listener.isyes(true, dialog);
                }
            });
            builder.setNegativeButton("no", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    askDialog = dialog;
                    listener.isyes(false, dialog);
                }
            });
            NotconnectDialog notconnectDialog=builder.create();
            notconnectDialog.setCanceledOnTouchOutside(CanceledOnTouchOutside);
            notconnectDialog.show();
            return askDialog;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    public static void showSelectCarModelForResult(Activity activity, int requestCode, String brand) {
        // TODO 选择OBD破解数据 页面
        Intent intent = new Intent(activity, SelectCarModelActivity.class);
        intent.putExtra(ARG.CARBRAND, brand);
        activity.startActivityForResult(intent, requestCode);
    }

    public static void showVideoplayer(Context activity, String name) {
        // TODO 跳到视频播放页面
        Bundle args = new Bundle();
        args.putString(ARG.CARCORDER_FILE_PATH, AppContext.getInstance().getDeviceHelper().getDownloadDir() + name + ".mp4");
        Intent intent = new Intent(activity, VideoPlayerActivity.class);
        intent.putExtras(args);
        activity.startActivity(intent);
    }

    public static void showLiveForResult(Activity activity, int requestCode, boolean isad) {
        // TODO 跳到实时路况页面
        if (VMainActivity.runing) {
            XuLog.e("实时路况界面还在");
            return;
        }

        if (activity != null) {
            Intent intent = new Intent(activity, VMainActivity.class);
            intent.putExtra(ARG.SHOW_BACKGROUND_CHECK, isad);
            activity.startActivityForResult(intent, requestCode);
        }
    }

}
