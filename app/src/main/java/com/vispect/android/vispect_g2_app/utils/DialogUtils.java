package com.vispect.android.vispect_g2_app.utils;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.rey.material.app.BottomSheetDialog;
import com.vispect.android.vispect_g2_app.R;
import com.vispect.android.vispect_g2_app.controller.UIHelper;
import com.vispect.android.vispect_g2_app.interf.Callback;
import com.vispect.android.vispect_g2_app.interf.CarDialogClickListener;

public class DialogUtils {

    /**
     * 上传头像 选择图片打开方式
     *
     * @param runTakePhoto   点击拍照
     * @param runChoosePhoto 点击从相册中选择
     */
    public static void selectAvatar(@NonNull Activity activity, @NonNull final Runnable runTakePhoto, @NonNull final Runnable runChoosePhoto) {
        View view = activity.getLayoutInflater().inflate(R.layout.select_avatar_dialog, null);
        final BottomSheetDialog dialog = new BottomSheetDialog(activity);
        dialog.contentView(view)
                .heightParam(UIHelper.dp2px(activity, 205))
                .inDuration(100).outDuration(100)
                .cancelable(true).show();
        TextView takePhoto = dialog.findViewById(R.id.tv_take_photo);
        TextView choosePhoto = dialog.findViewById(R.id.tv_choose_photos);
        TextView cancel = dialog.findViewById(R.id.tv_cancel);

        takePhoto.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                runTakePhoto.run();
                dialog.dismiss();
            }
        });
        choosePhoto.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                runChoosePhoto.run();
                dialog.dismiss();
            }
        });

        cancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }

    /**
     * 选择视频类型
     *
     * @param activity
     * @param callback 选择类型后 回调
     */
    public static void selectVideoType(@NonNull Activity activity, @NonNull final Callback<Integer> callback) {
        View view = activity.getLayoutInflater().inflate(R.layout.select_video_dialog, null);
        final BottomSheetDialog dialog = new BottomSheetDialog(activity);
        dialog.contentView(view)
                .heightParam(UIHelper.dp2px(activity, 205))
                .inDuration(100).outDuration(100)
                .cancelable(true).show();
        TextView alarm = dialog.findViewById(R.id.alarm_video);
        TextView driving = dialog.findViewById(R.id.driving_video);
        TextView local = dialog.findViewById(R.id.local_video);
        TextView cancel = dialog.findViewById(R.id.tv_cancel);

        driving.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                callback.callback(0);
                dialog.dismiss();
            }
        });

        alarm.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                callback.callback(1);
                dialog.dismiss();
            }
        });

        local.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                callback.callback(2);
                dialog.dismiss();
            }
        });

        cancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }

    /**
     * 选择设备类型
     *
     * @param activity
     * @param callback 选择类型后 回调
     */
    public static void selectDeviceType(@NonNull Activity activity, @NonNull final Callback<Integer> callback) {
        View view = activity.getLayoutInflater().inflate(R.layout.select_type_dialog, null);
        final BottomSheetDialog dialog = new BottomSheetDialog(activity);
        dialog.contentView(view)
                .heightParam(UIHelper.dp2px(activity, 205))
                .inDuration(100).outDuration(100)
                .cancelable(true).show();
        TextView adas = dialog.findViewById(R.id.tv_ADAS);
        TextView dsm = dialog.findViewById(R.id.tv_DSM);
        TextView spml = dialog.findViewById(R.id.tv_SPML);
        TextView spmr = dialog.findViewById(R.id.tv_SPMR);
        TextView cancel = dialog.findViewById(R.id.tv_cancel);

        adas.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                callback.callback(0);
                dialog.dismiss();
            }
        });
        dsm.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                callback.callback(1);
                dialog.dismiss();
            }
        });

        spml.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                callback.callback(2);
                dialog.dismiss();
            }
        });

        spmr.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                callback.callback(3);
                dialog.dismiss();
            }
        });

        cancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }

    public static MaterialDialog confirmDialog(@NonNull Activity activity, @Nullable String title, @NonNull final Runnable confirm, @Nullable final Runnable cancel) {
        MaterialDialog.Builder builder = new MaterialDialog.Builder(activity);
        final MaterialDialog confirmDialog = builder
                .customView(R.layout.dialog_confirm, false)
                .show();
        TextView titleTV = (TextView) confirmDialog.findViewById(R.id.title);
        TextView confirmTV = (TextView) confirmDialog.findViewById(R.id.confirm);
        TextView cancelTV = (TextView) confirmDialog.findViewById(R.id.cancel);
        if (!TextUtils.isEmpty(title)) titleTV.setText(title);
        confirmTV.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                confirm.run();
                confirmDialog.dismiss();
            }
        });
        cancelTV.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (cancel != null) cancel.run();
                confirmDialog.dismiss();
            }
        });
        return confirmDialog;
    }

}
