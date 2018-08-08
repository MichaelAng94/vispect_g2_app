package com.vispect.android.vispect_g2_app.ui.activity;

import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.vispect.android.vispect_g2_app.R;
import com.vispect.android.vispect_g2_app.controller.UIHelper;
import com.vispect.android.vispect_g2_app.utils.XuLog;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * 选择下载过的本地视频设备型号
 */
public class LocalVideoSeltActivity extends BaseActivity {

    @Bind(R.id.nothing_tip)
    TextView nothingTip;
    private List<String> filedata;
    private ArrayList<String> videodata;

    @Bind(R.id.iv_titlebar_title)
    TextView ivTitlebarTitle;

    @Bind(R.id.videolist)
    ListView videolist;

    @Override
    public int getContentResource() {
        return R.layout.activity_local_video;
    }

    @Override
    protected void initView(View view) {
        filedata = new ArrayList();
        ivTitlebarTitle.setText("选择设备");
        File file1 = new File(Environment.getExternalStorageDirectory().toString() + "/vispect_g2");
        getAllFiles(file1);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                LocalVideoSeltActivity.this, R.layout.listitem_video, filedata);
        videolist.setAdapter(adapter);
        if (filedata.size() == 0) {
            nothingTip.setVisibility(View.VISIBLE);
        }
        videolist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                File file1 = new File(Environment.getExternalStorageDirectory().toString() + "/vispect_g2/" + filedata.get(position));
                getAllFiles(file1);
                if (videodata.size() == 0) {
                    Toast.makeText(LocalVideoSeltActivity.this, "该设备没有本地视频", Toast.LENGTH_SHORT).show();
                } else {
                    UIHelper.showSelectVideoActivity(LocalVideoSeltActivity.this, videodata);
                }
            }
        });
    }

    void getAllFiles(File root) {
        videodata = new ArrayList();
        File files[] = root.listFiles();
        if (files != null) {
            for (File f : files) {
                XuLog.e("" + files.length);
                if (f.isDirectory()) {
                    File filess[] = f.listFiles();
                    XuLog.e("" + filess.length + f.getAbsolutePath());
                    for (File ff : filess) {
                        XuLog.e("" + ff.getAbsolutePath());
                        if (ff.getName().indexOf(".mp4") > 0) {
                            filedata.add(f.getName());
                            break;
                        }
                    }
                } else {
                    if (f.getName().indexOf(".mp4") > 0) {
                        videodata.add(f.getAbsolutePath());
                    }
                }
            }
        }
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
