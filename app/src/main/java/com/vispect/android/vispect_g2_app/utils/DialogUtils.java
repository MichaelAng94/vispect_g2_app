package com.vispect.android.vispect_g2_app.utils;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.rey.material.app.BottomSheetDialog;
import com.vispect.android.vispect_g2_app.R;
import com.vispect.android.vispect_g2_app.controller.UIHelper;

public class DialogUtils {

    /**
     * 上传头像 选择图片打开方式
     * @param runTakePhoto 点击拍照
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
}
