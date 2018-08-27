package com.vispect.android.vispect_g2_app.ui.activity;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.vispect.android.vispect_g2_app.R;
import com.vispect.android.vispect_g2_app.adapter.DrivingVideosAdapter;
import com.vispect.android.vispect_g2_app.app.AppContext;
import com.vispect.android.vispect_g2_app.bean.ARG;
import com.vispect.android.vispect_g2_app.interf.XuTimeOutCallback;
import com.vispect.android.vispect_g2_app.ui.widget.DialogHelp;
import com.vispect.android.vispect_g2_app.ui.widget.RefreshableView;
import com.vispect.android.vispect_g2_app.utils.CheckTime;
import com.vispect.android.vispect_g2_app.utils.XuFileUtils;
import com.vispect.android.vispect_g2_app.utils.XuLog;
import com.vispect.android.vispect_g2_app.utils.XuNetWorkUtils;
import com.vispect.android.vispect_g2_app.utils.XuString;
import com.vispect.android.vispect_g2_app.utils.XuTimeOutUtil;
import com.vispect.android.vispect_g2_app.utils.XuToast;
import com.vispect.android.vispect_g2_app.utils.XuView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import bean.G2DVRInfo;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import interf.DrivingVideoOperationListener;
import interf.G2DrivingVideoOperationListener;
import interf.OnRefreshListener;
import interf.OnWifiOpenListener;
import interf.ProgressCallback;


/**
 * 报警视频
 * <p>
 * Created by xu on 2015/12/26.
 */
public class ADASWarringVideosActivity extends BaseActivity {

    @Bind(R.id.btn_save)
    ImageView btnSave;
    private List<Integer> checkList = new ArrayList<>();
    private final static String TAG = "ADASWarringVideosActivity";
    public ArrayList<String> checkable_list = new ArrayList<String>();//已选中的视频

    @Bind(R.id.list_videos)
    RefreshableView setting_menu;
    @Bind(R.id.iv_titlebar_title)
    TextView title;
    @Bind(R.id.ll_mask)
    LinearLayout ll_mask;

    private int videoType;
    private int algoType;
    DrivingVideosAdapter adapter;
    static int currIndex = 0;
    static int pageSize = 0x1d;
    ArrayList<G2DVRInfo> videos;
    private int isrefresh = -1;
    public static boolean needclose = false;
    private Handler mHandler = new Handler();
    private Runnable hideRunnable = new Runnable() {
        @Override
        public void run() {
            DialogHelp.getInstance().hideDialog();
        }
    };
    private Runnable notmoreRunnable = new Runnable() {
        @Override
        public void run() {
            XuToast.show(ADASWarringVideosActivity.this, STR(R.string.cube_views_load_not_date));
            switch (isrefresh) {
                case 0:
                    if (setting_menu != null) {
                        setting_menu.hideHeaderView();
                    }
                    isrefresh = -1;
                    break;
                case 1:
                    if (setting_menu != null) {
                        setting_menu.hideFooterView();
                    }
                    isrefresh = -1;
                    break;
            }
        }
    };
    private XuTimeOutUtil timeoututil = new XuTimeOutUtil(new XuTimeOutCallback() {
        @Override
        public void onTimeOut() {
            //超时后的对应操作
            DialogHelp.getInstance().hideDialog();
            mHandler.post(mdoneRunnable);
            switch (isrefresh) {
                case 0:
                    if (setting_menu != null) {
                        setting_menu.hideHeaderView();
                    }
                    isrefresh = -1;
                    break;
                case 1:
                    if (setting_menu != null) {
                        setting_menu.hideFooterView();
                    }
                    isrefresh = -1;
                    break;
            }
        }
    });
    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            //开始可以显示进度条了
            try {
                if (isrefresh == -1) {
                    //如果实在刷新的时候就不更新进度条
                    adapter.refreshData(videos);
                }
                DialogHelp.getInstance().hideDialog();
            } catch (Exception e) {

            }

        }
    };
    private boolean canCancelconnection = false;
    private Runnable mRunnable_cancel_connectwifi = new Runnable() {
        @Override
        public void run() {
            //时间过长的话就提示让用户手动连
            if (canCancelconnection) {
                XuLog.d(TAG, "超时取消wifi连接");
                canshowlongtime = false;
                XuNetWorkUtils.cancelConnectWIFI();
                XuToast.show(AppContext.getInstance(), STR(R.string.connect_timeout));
                DialogHelp.getInstance().hideDialog();
            }
        }
    };
    private Runnable mRunnable_toas = new Runnable() {
        @Override
        public void run() {
            XuToast.show(ADASWarringVideosActivity.this, STR(R.string.download_fail));

        }
    };
    private Runnable onFielError = new Runnable() {
        @Override
        public void run() {
            //文件损坏
            DialogHelp.getInstance().hideDialog();
            XuToast.show(ADASWarringVideosActivity.this, STR(R.string.file_corrupted));
        }
    };
    private boolean canshowlongtime = false;
    private Runnable mRunnable_toas_waiitingtoolong = new Runnable() {
        @Override
        public void run() {
            //时间过长的话就提示让用户手动连
            if (canshowlongtime) {
                DialogHelp.getInstance().connectDialog(ADASWarringVideosActivity.this, STR(R.string.wifi_waiting_too_long));
            }


        }
    };

    private void download(int position) {
        if (isdownloading || !XuString.isEmpty(AppContext.getInstance().getDownloading_name())) {
            XuToast.show(ADASWarringVideosActivity.this, STR(R.string.please_wai_last_file_down_done));
            return;
        }
        DialogHelp.getInstance().connectDialog(ADASWarringVideosActivity.this, STR(R.string.waitting));
        switch (AppContext.getInstance().getDeviceHelper().getCommunicationType()) {
            case 0:
                openDownloadOld(position);
                break;
            case 1:
                openDownloadNew(position);
                break;

        }
        popupWindow.dismiss();
    }

    private Runnable mdoneRunnable = new Runnable() {
        @Override
        public void run() {
            if (AppContext.getInstance().isNeedclosetcp()) {

                isdownloading = false;
                downloading_position = -1;

                AppContext.getInstance().getDeviceHelper().closeDeviceDownloadDVRMode();

                AppContext.getInstance().setNeedclosetcp(false);
                AppContext.getInstance().setDownloading_name("");
                if (adapter != null && videos != null) {
                    adapter.refreshData(videos);
                }


            }

        }
    };


    @Override
    public int getContentResource() {
        // TODO Auto-generated method stub
        return R.layout.activity_drivingvideos;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void initView(View view) {
        // TODO Auto-generated method stub
        Bundle b = getIntent().getExtras();
        videoType = b.getInt("videoType");
        algoType = b.getInt("type");

        if (videoType == 0) {
            title.setText(STR(R.string.videos_driving_record_videros));
        } else if (videoType == 1) {
            title.setText(STR(R.string.videos_adas_war_videros));
        }

        if (!AppContext.getInstance().getDeviceHelper().isConnectedDevice()) {
            return;
        }
        CheckTime.toStop();
        findViewById(R.id.btn_save).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupWindow(setting_menu, -1);
            }
        });
        XuView.setViewVisible(findViewById(R.id.btn_save), true);
        initlistview();
        //一开始从最新的开始取
        getnow();


    }


    private void getnow() {
        //TODO 刷新最新的数据
        videos.clear();
        adapter.saveCheckable();
        adapter.refreshData(videos);
        timeoututil.startCheck(ARG.SET_VALUE_TIMEOUT);
        if (videoType == 0) {
            AppContext.getInstance().getDeviceHelper().getG2RecordVideoList(1, pageSize, algoType, new G2DrivingVideoOperationListener() {
                @Override
                public void onGetVideoList(final ArrayList arrayList) {
                    timeoututil.stopCheck();
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            refreshDVRList(arrayList);
                        }
                    });
                }

                @Override
                public void onLockOrUnlockResult(boolean b) {

                }

                @Override
                public void onLast() {
                    XuToast.show(ADASWarringVideosActivity.this, "No More Files");
                }
            });
        } else if (videoType == 1) {
            AppContext.getInstance().getDeviceHelper().getG2AlarmVideoList(0, pageSize, algoType, new G2DrivingVideoOperationListener() {
                @Override
                public void onGetVideoList(final ArrayList arrayList) {
                    timeoututil.stopCheck();
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            refreshDVRList(arrayList);
                        }
                    });
                }

                @Override
                public void onLockOrUnlockResult(boolean b) {

                }

                @Override
                public void onLast() {
                    XuToast.show(ADASWarringVideosActivity.this, "No More Files");
                }
            });
        }
    }

    private void refreshDVRList(ArrayList<G2DVRInfo> carCorderInfoAdd) {
        //TODO 刷新数据
        videos.addAll(carCorderInfoAdd);
        currIndex = videos.size();
        //判断是否有正在下载的视频
        String filename = AppContext.getInstance().getDownloading_name();
        if (!XuString.isEmpty(filename)) {
            XuLog.d("DrivingVideosActivity", "filename:" + filename);
            for (int i = 0; i < videos.size(); i++) {
                if (videos.get(i).getName().equals(filename)) {
                    downloading_position = i;
                    if (downloading_position >= 0 && downloading_position < videos.size()) {
                        XuLog.d(TAG, " set it true");
                        videos.get(downloading_position).setIsDownloading(true);
                        isdownloading = true;
                        //在此重置下载进度
                        AppContext.getInstance().getDeviceHelper().resetDownloadCallback(filename, progressCallback);
                    }
                }
            }
        }


        switch (isrefresh) {
            case 0:
                if (setting_menu != null) {
                    setting_menu.hideHeaderView();
                }
                isrefresh = -1;
                break;
            case 1:
                if (setting_menu != null) {
                    setting_menu.hideFooterView();
                }
                isrefresh = -1;
                break;
        }
        mHandler.post(hideRunnable);
        mHandler.post(mRunnable);
    }

    private void initlistview() {
        videos = new ArrayList<G2DVRInfo>();
        adapter = new DrivingVideosAdapter(ADASWarringVideosActivity.this, videos);
        setting_menu.setAdapter(adapter);
        setting_menu.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onDownPullRefresh() {
                if (isrefresh == 1) {
                    //如果现在正在加载更多  那么就不让他刷新
                    if (setting_menu != null) {
                        setting_menu.hideHeaderView();
                    }
                    return;
                }
                isrefresh = 0;
                getnow();
            }

            @Override
            public void onLoadingMore() {
                isrefresh = 1;
                if (videoType == 0) {
                    getVideos(currIndex);
                } else if (videoType == 1) {
                    getVideos2(currIndex);
                }
            }
        });

        setting_menu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                showPopupWindow(setting_menu, position - 1);
            }
        });
    }


    void getVideos(int pageIndex) {
        timeoututil.startCheck(ARG.SET_VALUE_TIMEOUT);
        AppContext.getInstance().getDeviceHelper().getG2RecordVideoList(pageIndex, pageSize, algoType, new G2DrivingVideoOperationListener() {

            @Override
            public void onGetVideoList(final ArrayList arrayList) {
                timeoututil.stopCheck();
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        refreshDVRList(arrayList);
                    }
                });
            }

            @Override
            public void onLockOrUnlockResult(boolean b) {

            }

            @Override
            public void onLast() {
                timeoututil.stopCheck();
                XuToast.show(ADASWarringVideosActivity.this, STR(R.string.cube_views_load_more_loaded_empty));
                switch (isrefresh) {
                    case 0:
                        if (setting_menu != null) {
                            setting_menu.hideHeaderView();
                        }
                        isrefresh = -1;
                        break;
                    case 1:
                        if (setting_menu != null) {
                            setting_menu.hideFooterView();
                        }
                        isrefresh = -1;
                        break;
                }
            }
        });


    }

    void getVideos2(int pageIndex) {
        timeoututil.startCheck(ARG.SET_VALUE_TIMEOUT);
        AppContext.getInstance().getDeviceHelper().getG2AlarmVideoList(pageIndex, pageSize, algoType, new G2DrivingVideoOperationListener() {

            @Override
            public void onGetVideoList(final ArrayList arrayList) {
                timeoututil.stopCheck();
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        refreshDVRList(arrayList);
                    }
                });
            }

            @Override
            public void onLockOrUnlockResult(boolean b) {

            }

            @Override
            public void onLast() {
                timeoututil.stopCheck();
                XuToast.show(ADASWarringVideosActivity.this, STR(R.string.cube_views_load_more_loaded_empty));
                switch (isrefresh) {
                    case 0:
                        if (setting_menu != null) {
                            setting_menu.hideHeaderView();
                        }
                        isrefresh = -1;
                        break;
                    case 1:
                        if (setting_menu != null) {
                            setting_menu.hideFooterView();
                        }
                        isrefresh = -1;
                        break;
                }
            }
        });


    }

    private boolean toconnectWifi = true;
    PopupWindow popupWindow = null;

    void showPopupWindow(View parent, final int position) {
        View contentView;
        if (!adapter.isIsedit()) {
            contentView = LayoutInflater.from(ADASWarringVideosActivity.this).inflate(R.layout.popupwindow_drivingvideos_menu, null);
        } else {
            contentView = LayoutInflater.from(ADASWarringVideosActivity.this).inflate(R.layout.popupwindow_drivingvideos_menu2, null);
        }
        if (popupWindow != null) {
            popupWindow.dismiss();
        }
        popupWindow = new PopupWindow(contentView, LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT, true);

        popupWindow.setTouchable(true);
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                ll_mask.setVisibility(View.GONE);
            }
        });
        popupWindow.setTouchInterceptor(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;

            }
        });

//        contentView.findViewById(R.id.ll_popuwindow_choosedate).setOnClickListener(new OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                // TODO Auto-generated method stub
//                UIHelper.showCalendarForResult(DrivingVideosActivity.this, 100);
//                popupWindow.dismiss();
//            }
//        });
        contentView.findViewById(R.id.ll_popuwindow_batchmanagement).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (adapter.isIsedit()) {
                    adapter.saveCheckable();

                    for (String name : adapter.checkable_list) {
                        for (int i = 0; i < videos.size(); i++) {
                            if (name.equals(videos.get(i).getName())) {
                                if (!checkList.contains(i)) {
                                    checkList.add(i);
                                }
                            }
                        }
                    }
                    if (checkList.size() > 0) {
                        download(checkList.get(0));
                    }
                    adapter.setIsedit(false);
                } else {
                    adapter.setIsedit(true);
                }
                adapter.refreshData(videos);
                popupWindow.dismiss();
            }
        });


        contentView.findViewById(R.id.ll_popuwindow_lock).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                popupWindow.dismiss();
                DialogHelp.getInstance().connectDialog(ADASWarringVideosActivity.this, "");
                AppContext.getInstance().getCachedThreadPool().execute(new Runnable() {
                    @Override
                    public void run() {
                        adapter.setItemCheck(position);
                        adapter.setloclorunlock(new DrivingVideoOperationListener() {
                            @Override
                            public void onGetVideoList(ArrayList arrayList) {
                                //
                            }

                            @Override
                            public void onLockOrUnlockResult(boolean b) {
                                //操作成功

                                mHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        adapter.setIsedit(false);
                                        adapter.notifyDataSetChanged();
                                        DialogHelp.getInstance().hideDialog();
//                                        //隔个100毫秒去获取一下最新的数据
//                                        try {
//                                            Thread.sleep(100);
//                                        } catch (InterruptedException e) {
//                                            e.printStackTrace();
//                                        }
//                                        getnow();
                                    }
                                });

                            }

                            @Override
                            public void onLast() {
                                XuToast.show(ADASWarringVideosActivity.this, "No More Files");
                            }
                        });
                    }
                });


            }
        });

        XuView.setViewVisible(contentView.findViewById(R.id.ll_popuwindow_savetophone), position != -1 && !adapter.getItemVideo(position).isHasLocalFile());
        contentView.findViewById(R.id.ll_popuwindow_savetophone).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (isdownloading || !XuString.isEmpty(AppContext.getInstance().getDownloading_name())) {
                    XuToast.show(ADASWarringVideosActivity.this, STR(R.string.please_wai_last_file_down_done));
                    return;
                }
                DialogHelp.getInstance().connectDialog(ADASWarringVideosActivity.this, "");
                switch (AppContext.getInstance().getDeviceHelper().getCommunicationType()) {
                    case 0:
                        openDownloadOld(position);
                        break;
                    case 1:
                        openDownloadNew(position);
                        break;

                }
                popupWindow.dismiss();
            }
        });


        contentView.findViewById(R.id.ll_popuwindow_cancel).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                popupWindow.dismiss();
            }
        });


        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        ll_mask.setVisibility(View.VISIBLE);
        popupWindow.showAtLocation(ll_mask, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
    }


    @OnClick(R.id.iv_titlebar_back)
    void back() {
        finish();
    }

    private void openDownloadNew(final int position) {
        AppContext.getInstance().getCachedThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                AppContext.getInstance().getDeviceHelper().openDownloadProt();
                openUploadServiceAndDownload(position, videos.get(position).getName());
            }
        });
    }

    private void openDownloadOld(final int position) {
        canshowlongtime = true;
        timeoututil.startCheck(ARG.OPEN_WIFI_TIMEOUT);
        //先移除掉可能存在的关闭WIFI的指令
        CheckTime.removeHnadler();
        //开始下载
        AppContext.getInstance().getCachedThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    needclose = true;
                    //先打开下载模.
                    mHandler.postDelayed(mRunnable_toas_waiitingtoolong, 20000);
                    AppContext.getInstance().getDeviceHelper().openDeviceDownloadDVRMode(new OnWifiOpenListener() {
                        @Override
                        public void onSuccess(final String name, final String password) {
                            timeoututil.stopCheck();
                            //连接到指定WIFI
                            mHandler.postDelayed(mRunnable_cancel_connectwifi, 60000);
                            canCancelconnection = true;
                            AppContext.getInstance().getCachedThreadPool().execute(new Runnable() {
                                @Override
                                public void run() {
                                    if (XuNetWorkUtils.connectWIFI(name, password)) {
                                        canCancelconnection = false;
                                        openUploadServiceAndDownload(position, videos.get(position).getName());
                                    }
                                }
                            });


                        }

                        @Override
                        public void onFail(int i) {
                            timeoututil.stopCheck();
                        }

                        @Override
                        public void onGetSanResult(String s) {

                        }
                    });

                } catch (Exception e) {
                    canshowlongtime = false;
                    e.printStackTrace();
                }
            }
        });
    }

    int progress = 0;
    public static int downloading_position = -1;
    public static boolean isdownloading = false;
    Handler progressHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            HashMap<String, String> map = ((HashMap<String, String>) msg.obj);
            String fileName = map.get("name");
            if (map.get("progress").equals(ARG.DOWNLOAD_DONE) || map.get("progress").equals("100")) {
                progress = 0;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        if (downloading_position >= 0) {
                            AppContext.getInstance().setDownloading_name("");
                            isdownloading = false;
                            adapter.refreshData(videos);
                            adapter.check_loaddown();
                            XuLog.d(TAG, "the download is done");
                            if (downloading_position < videos.size()) {
                                videos.get(downloading_position).setIsDownloading(false);
                                videos.get(downloading_position).setHasLocalFile(true);
                            }
                            if (checkList.size() > 0) {
                                checkList.remove(0);
                            }
                            if (checkList != null && checkList.size() > 0) {
                                download(checkList.get(0));
                            } else {
                                Toast.makeText(ADASWarringVideosActivity.this, "完成", Toast.LENGTH_SHORT).show();
                                ;
                            }
                        }
                    }
                });
            } else {
                progress = Integer.valueOf(map.get("progress"));
                for (int i = 0; i < videos.size(); i++) {
                    if (videos.get(i).getName().equals(fileName)) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (downloading_position >= 0 && downloading_position < videos.size()) {
                                    videos.get(downloading_position).setIsDownloading(true);
                                    videos.get(downloading_position).setHasLocalFile(false);
                                    adapter.refreshData(videos);

                                }
                            }
                        });
                        break;
                    }
                }
            }
            refreshProgressHandler.sendEmptyMessage(0);
        }
    };
    Handler refreshProgressHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            publishProgress(downloading_position, progress);
        }
    };

    public void publishProgress(final int positionInAdapter, final int progress) {
        if (positionInAdapter < 0 || positionInAdapter > videos.size() - 1) {
            return;
        }
        videos.get(positionInAdapter).setProgressValue(progress);
        adapter.notifyDataSetChanged();
        // Attempt to invoke virtual method 'android.view.View android.view.View.findViewById(int)' on a null object reference
    }


    private ProgressCallback progressCallback = new ProgressCallback() {
        @Override
        public void onProgressChange(long l) {
            XuLog.e(TAG, "进度还在继续:" + l);
            Message message = new Message();
            message.what = ARG.DOWNLOAD_PROGRESS;
            HashMap<String, String> map = new HashMap<>();
            map.put("progress", l + "");
            map.put("name", curDowningFileName);
            message.obj = map;
            progressHandler.handleMessage(message);
        }

        @Override
        public void onErro(int i) {
            XuLog.d(TAG, " onFail():" + i);
            mHandler.removeCallbacks(mRunnable_toas_waiitingtoolong);
            if (videos != null && downloading_position < videos.size() && downloading_position > -1) {
                videos.get(downloading_position).setIsDownloading(false);
            }
            //删除未下载完成的文件
            XuFileUtils.delFile(AppContext.getInstance().getDeviceHelper().getDownloadDir() + AppContext.getInstance().getDownloading_name() + ".mp4");
            AppContext.getInstance().setDownloading_name("");
            mHandler.post(mRunnable);
            isdownloading = false;
            downloading_position = -1;
            mHandler.post(mRunnable_toas);
            mHandler.post(hideRunnable);
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    adapter.refreshData(videos);
                }
            });


        }

        @Override
        public void onDone(String fileName, String md5) {
            //完成
            XuLog.e(TAG, "下载完成");
            isdownloading = false;
        }
    };

    private String curDowningFileName;

    void openUploadServiceAndDownload(final int postion, final String value) {
        XuLog.d(TAG, "开始进入下载");
        canshowlongtime = false;
        videos.get(postion).setIsDownloading(true);
        downloading_position = postion;
        curDowningFileName = value;
        AppContext.getInstance().getDownThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                isdownloading = true;
                mHandler.removeCallbacks(mRunnable_toas_waiitingtoolong);
                mHandler.post(mRunnable);

                AppContext.getInstance().getDeviceHelper().downG2MultiMediaFile(videoType, algoType, value, progressCallback);
                //把下载未完成的存在起来，以便下次进来还有进度条
                if (isdownloading && videos != null && downloading_position < videos.size()) {
                    AppContext.getInstance().setDownloading_name(videos.get(downloading_position).getName());
                }

            }

        });

    }


    @Override
    protected void onDestroy() {
        AppContext.getInstance().setNeedclosetcp(isdownloading);
        if (needclose && !isdownloading) {
            //没有视频下载的时候关闭TCP
            CheckTime.todisconnectWifi();
            AppContext.getInstance().getDeviceHelper().closeDeviceDownloadDVRMode();
        }
        if (adapter != null) {
            adapter.clear_loaddown();
        }

        //退出的时候清空handler
        mHandler.removeCallbacksAndMessages(null);
        canshowlongtime = false;
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }
}
