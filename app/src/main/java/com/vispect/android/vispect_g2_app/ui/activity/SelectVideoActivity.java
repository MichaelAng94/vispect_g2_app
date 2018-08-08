package com.vispect.android.vispect_g2_app.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;


import com.vispect.android.vispect_g2_app.R;
import com.vispect.android.vispect_g2_app.adapter.VideoAdapter;
import com.vispect.android.vispect_g2_app.bean.ARG;
import com.vispect.android.vispect_g2_app.controller.UIHelper;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 *  选择本地视频的界面
 */

public class SelectVideoActivity extends BaseActivity {

    @Bind(R.id.select_video)
    ListView selectVideo;

    ArrayList<String> mDatas;
    @Bind(R.id.iv_titlebar_title)
    TextView ivTitlebarTitle;

    @Override
    public int getContentResource() {
        return R.layout.activity_select_video;
    }

    @Override
    protected void initView(View view) {
        Intent i = getIntent();
        mDatas = (ArrayList<String>) i.getBundleExtra(ARG.VIDEO_LIST).get(ARG.VIDEO_LIST);
        selectVideo.setAdapter(new VideoAdapter(this, mDatas));
        ivTitlebarTitle.setText("选择本地视频");
        selectVideo.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                UIHelper.showVideoplayerbyPath(SelectVideoActivity.this,mDatas.get(position));
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }

    @OnClick(R.id.iv_titlebar_back)
    public void onViewClicked() {
        finish();
    }
}
