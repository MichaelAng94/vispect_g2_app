package com.vispect.android.vispect_g2_app.ui.fragment;

import android.content.DialogInterface;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.vispect.android.vispect_g2_app.R;
import com.vispect.android.vispect_g2_app.adapter.CalibrateAdapter;
import com.vispect.android.vispect_g2_app.app.AppConfig;
import com.vispect.android.vispect_g2_app.app.AppContext;
import com.vispect.android.vispect_g2_app.controller.UIHelper;
import com.vispect.android.vispect_g2_app.ui.activity.MainActivity;
import com.vispect.android.vispect_g2_app.ui.widget.DialogHelp;
import com.vispect.android.vispect_g2_app.ui.widget.MoListview;
import com.vispect.android.vispect_g2_app.utils.XuLog;
import com.vispect.android.vispect_g2_app.utils.XuNetWorkUtils;
import com.vispect.android.vispect_g2_app.utils.XuString;
import com.vispect.android.vispect_g2_app.utils.XuToast;

import java.io.IOException;
import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import interf.GetG2CameraList;
import interf.OnWifiOpenListener;

/**
 * Created by mo on 2018/7/12.
 *
 * 选择标定镜头的界面
 */

public class CalibrateFragment extends BaseFragment {
    private String TAG = "CalibrateFragment";

    @Bind(R.id.list_select_camera)
    MoListview listSelectCamera;
    private CalibrateAdapter calibrateAdapter;
    private ArrayList<String> data;
    private changeDialog changeDialog;
    private Handler myHandler;
    Thread openliveThread = null;
    private final int REQUESRST_LIVE = 101;
    private  ArrayList<Point> cameras = new ArrayList<>();
    public static Point selectCamera;

    @Override
    public int getContentResource() {
        return R.layout.fragment_calibrate;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden){
            DialogHelp.getInstance().hideDialog();
        }
    }

    @Override
    protected void initView(View view) throws IOException {
        myHandler = new Handler();
        AppContext.getInstance().getDeviceHelper().getG2CameraList(new GetG2CameraList() {
            @Override
            public void onSuccess(ArrayList arrayList) {
                cameras = arrayList;
                data = new ArrayList<>();
                for (Point p : cameras) {
                    switch (p.y) {
                        case -1:
                            data.add("Unavailable");
                            break;
                        case 0:
                            data.add("None");
                            break;
                        case 1:
                            data.add(STR(R.string.font_camera));
                            break;
                        case 2:
                            data.add("None");
                            break;
                        case 3:
                            data.add(STR(R.string.driver_status_monitoring));
                            break;
                        case 4:
                            data.add(STR(R.string.left_camera_forward));
                            break;
                        case 5:
                            data.add(STR(R.string.left_camera_back));
                            break;
                        case 6:
                            data.add(STR(R.string.right_camera_forward));
                            break;
                        case 7:
                            data.add(STR(R.string.right_camera_back));
                            break;
                    }
                }
                calibrateAdapter = new CalibrateAdapter(getContext(), data);
                listSelectCamera.setAdapter(calibrateAdapter);
            }

            @Override
            public void onFail() {

            }
        });

        listSelectCamera.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                selectCamera = cameras.get(i);
                AppContext.getInstance().setCalivrateID(cameras.get(i).x);
                if (cameras.get(i).y>=4){
                    AppContext.getInstance().setCalibrateType(1);
                }else if (cameras.get(i).y==3){
                    AppContext.getInstance().setCalibrateType(3);
                }else {
                    AppContext.getInstance().setCalibrateType(2);
                }
                //selectCamera = cameras.get(i);
                openlivemode();
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO: inflate a fragment view
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    int openlivemode() {

        //TODO 开启实时路况
        if (!AppContext.getInstance().getDeviceHelper().isConnectedDevice()) {
            XuToast.show(AppContext.getInstance(), STR(R.string.road_live_notconnect));
            return 1;
        }
        if (AppContext.getInstance().isERROR_CAMERA()) {
            XuToast.show(AppContext.getInstance(), STR(R.string.camera_erro));
            return 2;
        }
        changeDialog = new changeDialog();

        DialogHelp.getInstance().connectDialog(getActivity(), STR(R.string.dialog_tips_connecting), STR(R.string.dialog_tips_connecting2)).setOnKeyListener(new keyListener());
        myHandler.postDelayed(changeDialog, 10000);
        openliveThread = new Thread(new Runnable() {
            @Override
            public void run() {
                switch (AppContext.getInstance().getDeviceHelper().getCommunicationType()) {
                    case 0:
                        openRealViewOld();
                        break;
                    case 1:
                        openRealViewNew();
                        break;

                }
            }
        });
        AppContext.getInstance().getCachedThreadPool().execute(openliveThread);
        return 0;
    }

    private void openRealViewNew(){
        AppContext.getInstance().getDeviceHelper().openDeviceCalibrationMode(null);
        myHandler.postDelayed(toRealView, 1000);
    }

    private Runnable toRealView = new Runnable() {
        @Override
        public void run() {
            DialogHelp.getInstance().hideDialog();
            if(AppContext.getInstance().getDeviceHelper().isConnectedDevice()){
                DialogHelp.getInstance().hideDialog();
                UIHelper.showCalibrate(getActivity(), REQUESRST_LIVE, true);
            }
        }
    };


    private void toCancelRealTime() {
        XuLog.d(TAG, "取消连接WIFI");
        myHandler.removeCallbacksAndMessages(null);
        AppContext.getInstance().getDeviceHelper().closeDeviceCalibrationMode();
    }

    private void openRealViewOld(){
        //先打开设备的wifi
        AppContext.getInstance().getDeviceHelper().openDeviceCalibrationMode(new OnWifiOpenListener() {
            @Override
            public void onSuccess(final String name, final String password) {
                //打开成功  开始连接特定wifi
                AppContext.getInstance().getCachedThreadPool().execute(new Runnable() {
                    @Override
                    public void run() {
                        //设置超时断开
                        if (XuNetWorkUtils.connectWIFI(name, password)) {
                            DialogHelp.getInstance().hideDialog();
                            myHandler.removeCallbacks(changeDialog);
                            //成功连上WIFI  开始进入实时路况
                            myHandler.postDelayed(toRealView, 1000);
                        }else{
                            XuLog.e(TAG,"连接设备的WIFI失败");
                        }
                    }
                });


            }

            @Override
            public void onFail(final int i) {
                //1.5秒后获取开关的滑动
                XuLog.e("open wifi fail：" + i);
            }

            @Override
            public void onGetSanResult(String s) {

            }
        });
    }

    public class changeDialog implements Runnable {
        @Override
        public void run() {
            DialogHelp.getInstance().hideDialog();
            DialogHelp.getInstance().connectDialog(getActivity(), STR(R.string.dialog_tips_connecting), STR(R.string.wifi_waiting_too_long) + AppConfig.getInstance(getActivity()).getWifi_name() + STR(R.string.wifi_waiting_too_long2)+ AppConfig.getInstance(getActivity()).getWifi_paw()).setOnKeyListener(new keyListener());
        }
    }

    class keyListener implements DialogInterface.OnKeyListener {

        @Override
        public boolean onKey(DialogInterface dialogInterface, int i, KeyEvent keyEvent) {
            if (i == KeyEvent.KEYCODE_BACK) {
                cancelConnect();
                dialogInterface.dismiss();
                return true;
            }
            return false;
        }
    }

    public void cancelConnect() {
        myHandler.removeCallbacks(changeDialog);
        XuNetWorkUtils.cancelConnectWIFI();
        AppContext.getInstance().getDeviceHelper().closeDeviceRealViewMode();
    }

}