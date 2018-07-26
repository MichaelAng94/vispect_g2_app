package com.vispect.android.vispect_g2_app.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.MediaController;
import android.widget.VideoView;


import com.vispect.android.vispect_g2_app.R;
import com.vispect.android.vispect_g2_app.app.AppContext;
import com.vispect.android.vispect_g2_app.bean.ARG;
import com.vispect.android.vispect_g2_app.utils.XuString;
import com.vispect.android.vispect_g2_app.utils.XuToast;

import butterknife.Bind;



/**
 * 视频播放界面
 *
 * Created by xu on 2016/4/20.
 */
public class VideoPlayerActivity extends BaseActivity{

    @Bind(R.id.video_view)
    VideoView videoView;

    private int mPositionWhenPaused;

    String filePath;

    @Override
    public int getContentResource() {
        return R.layout.activity_video_player;
    }

    @Override
    protected void initData() {
        super.initData();
        Bundle args = getIntent().getExtras();
        if (null == args) {
            throw new IllegalArgumentException("播放页面：视频地址为空");
        }
       filePath = args.getString(ARG.CARCORDER_FILE_PATH);


    }

    @Override
    protected void initView(View view) {

        MediaController mc = new MediaController(this);
        videoView.setMediaController(mc);
        //videoView.setVideoURI(Uri.parse(""));
        if(XuString.isEmpty(filePath)){
            XuToast.show(AppContext.getInstance(), STR(R.string.update_not_find_file));
            return;
        }
        videoView.setVideoPath(filePath);
        videoView.requestFocus();
        videoView.start();

    }
}
