package com.vispect.android.vispect_g2_app.ui.fragment;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PointF;
import android.net.wifi.WifiConfiguration;
import android.os.Handler;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.vispect.android.vispect_g2_app.R;
import com.vispect.android.vispect_g2_app.app.AppConfig;
import com.vispect.android.vispect_g2_app.app.AppContext;
import com.vispect.android.vispect_g2_app.bean.ARG;
import com.vispect.android.vispect_g2_app.controller.UIHelper;
import com.vispect.android.vispect_g2_app.interf.OnNeedScroll;
import com.vispect.android.vispect_g2_app.interf.XuTimeOutCallback;
import com.vispect.android.vispect_g2_app.ui.activity.DrivingVideosActivity;
import com.vispect.android.vispect_g2_app.ui.activity.VMainActivity;
import com.vispect.android.vispect_g2_app.ui.widget.NotconnectDialog;
import com.vispect.android.vispect_g2_app.utils.CheckTime;
import com.vispect.android.vispect_g2_app.utils.XuLog;
import com.vispect.android.vispect_g2_app.utils.XuNetWorkUtils;
import com.vispect.android.vispect_g2_app.utils.XuString;
import com.vispect.android.vispect_g2_app.utils.XuTimeOutUtil;
import com.vispect.android.vispect_g2_app.utils.XuToast;
import com.vispect.android.vispect_g2_app.utils.XuView;

import butterknife.Bind;
import butterknife.OnClick;
import interf.CorrectingCallback;
import interf.OnWifiOpenListener;


/**
 * 中心点校正
 *
 * Created by xu on 2017/7/18.
 */
public class CenterCalibrationFragment extends BaseFragment {
    private static final String TAG = "CenterCalibrationFragment";
//    @Bind(R.id.iv_titlebar_save_frame)
//    TextView iv_titlebar_save_frame;
    @Bind(R.id.iv_titlebar_title)
    TextView iv_titlebar_title;
    @Bind(R.id.correcting_progress)
    ProgressBar correcting_progress;
    @Bind(R.id.tv_correcting_progress)
    TextView tv_correcting_progress;
    @Bind(R.id.tv_center_calibrationing_bigtips)
    TextView tv_center_calibrationing_bigtips;
    @Bind(R.id.tv_center_calibrationing_tips)
    TextView tv_center_calibrationing_tips;
    @Bind(R.id.rel_correcting_progress)
    RelativeLayout rel_correcting_progress;




    boolean cantoget = true;
    long newtime = 0;
    private boolean corrcting = false;
    private int lastprogress = -1;
    private String installviewurl = AppConfig.INSTALL_VIDEO_URL;
    private Thread openliveThread;
    private Handler mHandler;
    private Runnable hascorrecting = new Runnable() {
        @Override
        public void run() {
            XuToast.show(AppContext.getInstance(),STR(R.string.calibration_start_correcting_fail));
        }
    };
    private Runnable toastkRunnable = new Runnable() {
        @Override
        public void run() {
            hideProgress();
            XuToast.show(AppContext.getInstance(), STR(R.string.open_wifi_fail));
        }
    };
    private Runnable toRealView = new Runnable() {
        @Override
        public void run() {
            hideProgress();
            UIHelper.showLiveForResult(getActivity(), REQUESRST_LIVE, false);
//            AppContext.getInstance().setLocation(180000);
        }
    };
    private final int REQUESRST_LIVE = 101;
    private boolean cantoconnect = true;
    private XuTimeOutUtil timeoututil = new XuTimeOutUtil(new XuTimeOutCallback() {
        @Override
        public void onTimeOut() {
            hideProgress();
            toCancelRealTime();
        }
    });
    private boolean canshowlongtime = false;
    private Runnable mRunnable_toas_waiitingtoolong = new Runnable() {
        @Override
        public void run() {
            //时间过长的话就提示让用户手动连
            if(canshowlongtime){
                showProgress(true, STR(R.string.wifi_waiting_too_long) + AppConfig.getInstance(AppContext.getInstance()).getWifi_name() + STR(R.string.wifi_waiting_too_long2) + AppConfig.getInstance(AppContext.getInstance()).getWifi_paw());
            }
        }
    };
    private boolean canCancelconnection = false;
    private Runnable mRunnable_cancel_connectwifi = new Runnable() {
        @Override
        public void run() {
            //时间过长的话就提示让用户手动连
            if(canCancelconnection){
                XuLog.d(TAG,"超时取消wifi连接");
                canshowlongtime = false;
                XuNetWorkUtils.cancelConnectWIFI();
                toCancelRealTime();
                XuToast.show(AppContext.getInstance(), STR(R.string.connect_timeout));
                hideProgress();
            }
        }
    };

    public static boolean isrunning = false;
    private OnNeedScroll needScrollCallback;
    @Override
    public int getContentResource() {
        return R.layout.fragment_calibration;
    }

    @Override
    protected void initView(View view) {
//        XuView.setViewVisible(iv_titlebar_save_frame, true);
//        iv_titlebar_save_frame.setText(STR(R.string.video_of_install));
        iv_titlebar_title.setText(STR(R.string.center_calibrationing_title));
        isrunning = true;
        mHandler = new Handler();

        if(XuString.isZh(AppContext.getInstance())){
            //对重点语句进行变色 突出显示
            SpannableStringBuilder builder = new SpannableStringBuilder(tv_center_calibrationing_bigtips.getText().toString());
            ForegroundColorSpan redSpan = new ForegroundColorSpan(getActivity().getResources().getColor(R.color.table_check));//ForegroundColorSpan 为文字前景色，BackgroundColorSpan为文字背景色
            builder.setSpan(redSpan, 21, 46, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
            tv_center_calibrationing_bigtips.setText(builder);
        }

    }

    @OnClick(R.id.btn_start_correcting)
    void startCorrecting(View v){
        if (!AppContext.getInstance().getDeviceHelper().isConnectedDevice()) {
            XuToast.show(getActivity(), STR(R.string.road_live_notconnect));
            return;
        }

        if(corrcting){
            XuToast.show(getActivity(), STR(R.string.calibration_has_correcting));
            return;
        }

        //发送一次重新统计中心点的命令
        AppContext.getInstance().getDeviceHelper().startCorrecting(new CorrectingCallback() {
            @Override
            public void onGetProgress(int i) {
            }

            @Override
            public void onGetOperationResult(boolean b) {
                XuLog.e(TAG,"开始中心点校正的结果："+b);
                if (b) {
                    //开始校正成功
                    cantoget = true;
                    corrcting = true;
                    lastprogress = -1;
                    if(mHandler != null){
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                XuView.setViewVisible(correcting_progress, corrcting);
                                XuView.setViewVisible(tv_correcting_progress, corrcting);
                                XuView.setViewVisible(tv_center_calibrationing_tips, corrcting);
                                XuView.setViewVisible(rel_correcting_progress, corrcting);
                                tv_center_calibrationing_tips.setText(STR(R.string.center_calibrationing_tips));
                                correcting_progress.setProgress(0);
                                tv_correcting_progress.setText(0 + "%");
                            }
                        });

                    }

                    newtime = System.currentTimeMillis();
                    AppContext.getInstance().getCachedThreadPool().execute(new Runnable() {
                        @Override
                        public void run() {
                            long time = System.currentTimeMillis();
                            if (AppContext.getInstance().isS()) {
                                return;
                            }
                            while (cantoget && newtime - time <= 10 && AppContext.getInstance().getDeviceHelper().isConnectedDevice()) {
                                try {
                                    Thread.sleep(1000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                getCorrectingProgress();

                            }

                        }
                    });
                } else {
                    if (mHandler != null) {
                        mHandler.post(hascorrecting);
                    }
                }
            }

            @Override
            public void onGetCenterPoint(PointF pointF) {
            }
        });
    }
    private void getCorrectingProgress(){
        //TODO 获取校正进度
        XuLog.e(TAG,"开始获取一次进度");
        AppContext.getInstance().getDeviceHelper().getCorrectingProgress(new CorrectingCallback() {
            @Override
            public void onGetProgress(final int progress) {
                if (lastprogress != progress) {
                    lastprogress = progress;
                    if (mHandler != null) {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                correcting_progress.setProgress(progress);
                                tv_correcting_progress.setText(progress + "%");
                                if (progress >= 100) {
//                                    showDialog();
                                    cantoget = false;
                                    corrcting = false;
                                    tv_center_calibrationing_tips.setText(STR(R.string.calibration_correcting_done));
                                    XuView.setViewVisible(correcting_progress, corrcting);
                                    XuView.setViewVisible(tv_correcting_progress, corrcting);
                                    openlivemode();
                                }
                            }
                        });
                    }

                }
            }

            @Override
            public void onGetOperationResult(boolean b) {

            }

            @Override
            public void onGetCenterPoint(PointF pointF) {

            }
        });
    }
    private void showDialog() {
        try{
            NotconnectDialog.Builder builder = new NotconnectDialog.Builder(getActivity());
            builder.setMessage(STR(R.string.calibration_correcting_done));
            builder.setPositiveButton(STR(R.string.yes), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            builder.setNegativeButton(STR(R.string.cancel), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            builder.create().show();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    int openlivemode(){
        //TODO 开启实时路况
        if (!AppContext.getInstance().getDeviceHelper().isConnectedDevice()) {
            XuToast.show(AppContext.getInstance(), STR(R.string.road_live_notconnect));
            return 1;
        }
        if(AppContext.getInstance().isERROR_CAMERA()){
            XuToast.show(AppContext.getInstance(),  STR(R.string.camera_erro));
            return 2;
        }



        timeoututil.startCheck(ARG.OPEN_WIFI_TIMEOUT);
        cantoconnect = false;
        Dialog dialog_loading = showProgress(STR(R.string.to_rel_view_loading_title));

        //先移除掉可能存在的关闭/断开WIFI的指令
        CheckTime.removeHnadler();
        //设置时间过长的提示
        mHandler.postDelayed(mRunnable_toas_waiitingtoolong, 20000);
        canshowlongtime = true;
        openliveThread = new Thread(new Runnable() {
            @Override
            public void run() {
                //先打开设备的wifi
                AppContext.getInstance().getDeviceHelper().openDeviceRealViewMode(new OnWifiOpenListener() {
                    @Override
                    public void onSuccess(final String name, final String password) {
                        //打开成功  开始连接特定wifi
                        timeoututil.stopCheck();
                        AppContext.getInstance().getCachedThreadPool().execute(new Runnable() {
                            @Override
                            public void run() {
                                //设置超时断开
                                mHandler.postDelayed(mRunnable_cancel_connectwifi, 60000);
                                canCancelconnection = true;
                                if (XuNetWorkUtils.connectWIFI(name, password)) {
                                    //成功连上WIFI  开始进入实时路况
                                    canCancelconnection = false;
                                    canshowlongtime = false;
                                    mHandler.postDelayed(toRealView, 1000);
                                }
                            }
                        });



                    }

                    @Override
                    public void onFail(final int i) {
                        mHandler.post(toastkRunnable);
                        hideProgress();
                        canshowlongtime = false;
                        XuLog.e("open wifi fail：" + i);
                    }

                    @Override
                    public void onGetSanResult(String s) {

                    }
                });
            }
        });




        AppContext.getInstance().getCachedThreadPool().execute(openliveThread);
        if(dialog_loading != null){
            dialog_loading.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    XuLog.d(TAG, "点击取消了");
                    canshowlongtime = false;
                    XuNetWorkUtils.cancelConnectWIFI();
                    toCancelRealTime();
                }
            });
        }




        return 0;
    }



    private void toCancelRealTime() {
        XuLog.d(TAG, "取消连接WIFI");
        mHandler.removeCallbacksAndMessages(null);
        AppContext.getInstance().getDeviceHelper().closeDeviceRealViewMode();
    }


    @Override
    public void onActivityResult(final int requestCode, int resultCode, Intent data) {
        AppContext.getInstance().getCachedThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                if (requestCode == REQUESRST_LIVE) {

                    if (!DrivingVideosActivity.isdownloading) {
                        XuLog.d("AndroidBle", "关闭了WIFI");
                        //断开WIFI的同时忘记该WIFI
                        WifiConfiguration wifiConfig = AppContext.getInstance().getWificontroller().CreateWifiInfo(AppConfig.getInstance(AppContext.getInstance()).getWifi_name(), AppConfig.getInstance(AppContext.getInstance()).getWifi_paw(), 3);
                        if (wifiConfig != null) {
                            AppContext.getInstance().getWificontroller().disconnectWifi(wifiConfig);
                        }
                    }
                    //表示退出实时路况
                    VMainActivity.runing = false;
                    //向设备发送关闭视频流命令
                    AppContext.getInstance().getDeviceHelper().closeDeviceRealViewMode();
                    XuLog.e(TAG, "关闭设备的实景模式");
                }
            }
        });



    }


    public void setNeedScrollCallback(OnNeedScroll needScrollCallback) {
        this.needScrollCallback = needScrollCallback;
    }

    @OnClick(R.id.iv_titlebar_back)
    void back(){
        needScrollCallback.onNeedScroll(3);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if(mHandler != null){
            mHandler.removeCallbacksAndMessages(null);
            mHandler = null;
        }

        isrunning = false;
        cantoget = false;
        corrcting = false;

    }

//    @OnClick(R.id.iv_titlebar_save_frame)
//    void toSeeVideo(View v){
//        UIHelper.showURL(getActivity(), installviewurl);
//    }

    public String getInstallviewurl() {
        return installviewurl;
    }

    public void setInstallviewurl(String installviewurl) {
        this.installviewurl = installviewurl;
    }
}
