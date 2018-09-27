package com.vispect.android.vispect_g2_app.ui.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;

import com.vispect.android.vispect_g2_app.R;
import com.vispect.android.vispect_g2_app.app.AppConfig;
import com.vispect.android.vispect_g2_app.app.AppContext;
import com.vispect.android.vispect_g2_app.controller.DeviceHelper;
import com.vispect.android.vispect_g2_app.controller.UIHelper;
import com.vispect.android.vispect_g2_app.interf.Callback;
import com.vispect.android.vispect_g2_app.interf.GetListCallback;
import com.vispect.android.vispect_g2_app.ui.activity.DocActivity;
import com.vispect.android.vispect_g2_app.ui.activity.InstallActivity;
import com.vispect.android.vispect_g2_app.ui.activity.LocalVideoSeltActivity;
import com.vispect.android.vispect_g2_app.ui.activity.SettingsActivity;
import com.vispect.android.vispect_g2_app.ui.widget.DialogHelp;
import com.vispect.android.vispect_g2_app.utils.DialogUtils;
import com.vispect.android.vispect_g2_app.utils.XuLog;
import com.vispect.android.vispect_g2_app.utils.XuNetWorkUtils;
import com.vispect.android.vispect_g2_app.utils.XuToast;

import java.util.ArrayList;
import java.util.List;

import butterknife.OnClick;
import interf.OnWifiOpenListener;

import static android.view.KeyEvent.KEYCODE_BACK;
import static com.vispect.android.vispect_g2_app.app.AppConfig.EXTRA_TO_INSTALLATION;
import static com.vispect.android.vispect_g2_app.app.AppConfig.EXTRA_TO_SETTING;
import static com.vispect.android.vispect_g2_app.controller.DeviceHelper.isConnected;
import static com.vispect.android.vispect_g2_app.controller.DeviceHelper.isG2;

public class IndexFragment extends BaseFragment {
    private static final int MESSAGE_CODE_GET_CAMERA_LIST = 0;
    private static final int MESSAGE_CODE_CONNECT_WIFI = 1;
    private static final int MESSAGE_CODE_TO_REAL_VIEW = 2;
    public static List<Point> cameras = new ArrayList<>();
    public int _count = 0;
    public int _videoType = -1;
    public int _deviceType = -1;
    private KeyListener keyListener = new KeyListener();
    private String TAG = IndexFragment.class.getSimpleName();
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_CODE_GET_CAMERA_LIST:
                    getCameraList();
                    break;
                case MESSAGE_CODE_CONNECT_WIFI:
                    showConnectWifiDialog();
                    break;
                case MESSAGE_CODE_TO_REAL_VIEW:
                    if (DeviceHelper.isG2Connected()) {
                        DialogHelp.getInstance().hideDialog();
                        UIHelper.showLiveForResult(getActivity(), 100, false);
                    } else {
                        XuToast.show(getActivity(), R.string.device_disconnected);
                    }
                    break;
            }
            super.handleMessage(msg);
        }
    };

    @Override
    public int getContentResource() {
        return R.layout.fragment_index;
    }

    @Override
    protected void initView(View view) {
    }

    @OnClick({R.id.menu_user_guide, R.id.menu_installation, R.id.menu_settings, R.id.menu_live, R.id.menu_video})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.menu_user_guide:
                UIHelper.startActivity(getActivity(), DocActivity.class);
                break;
            case R.id.menu_installation:
                if (DeviceHelper.isG2Connected(getActivity(), EXTRA_TO_INSTALLATION)) {
                    UIHelper.startActivity(getActivity(), InstallActivity.class);
                }
                break;
            case R.id.menu_settings:
                if (DeviceHelper.isG2Connected(getActivity(), EXTRA_TO_SETTING)) {
                    UIHelper.startActivity(getActivity(), SettingsActivity.class);
                }
                break;
            case R.id.menu_live:
                if (DeviceHelper.isG2Connected(getActivity(), null)) {
                    getCameraList();
                }
                break;
            case R.id.menu_video:
                showBottomSheetDialog();
                break;
        }
    }

    //选择Video类型
    private void showBottomSheetDialog() {
        DialogUtils.selectVideoType(getActivity(), new Callback<Integer>() {
            @Override
            public void callback(Integer value) {
                switch (value) {
                    case 0:
                    case 1:
                        _videoType = value;
                        showSelectDeviceDialog();
                        break;
                    case 2:
                        UIHelper.startActivity(getActivity(), LocalVideoSeltActivity.class);
                        break;
                }
            }
        });
    }

    //选择设备类型
    private void showSelectDeviceDialog() {
        DialogUtils.selectDeviceType(getActivity(), new Callback<Integer>() {
            @Override
            public void callback(Integer value) {
                _deviceType = value;
                UIHelper.showVideosActivity(getActivity(), _videoType, _deviceType);
            }
        });
    }

    private void getCameraList() {
        DeviceHelper.getCameraList(new GetListCallback() {
            @Override
            public void onGetListSuccess(List list) {
                if (list != null && list.size() > 0) {
                    cameras.clear();
                    cameras.addAll(list);
                    AppConfig.setCameras(list);
                    handler.removeMessages(MESSAGE_CODE_GET_CAMERA_LIST);
                    openLiveMode();
                } else {
                    XuToast.show(getActivity(), R.string.get_camera_list_failed);
                }
            }

            @Override
            public void onGetListFailed() {
                if (_count < 5) {
                    _count++;
                    handler.sendEmptyMessage(MESSAGE_CODE_GET_CAMERA_LIST);
                } else {
                    _count = 0;
                    handler.removeMessages(MESSAGE_CODE_GET_CAMERA_LIST);
                    XuToast.show(getActivity(), R.string.get_camera_list_failed);
                }
            }
        });
    }

    @Override
    public void onPause() {
        if (handler != null) {
            handler.removeMessages(MESSAGE_CODE_GET_CAMERA_LIST);
            handler.removeMessages(MESSAGE_CODE_CONNECT_WIFI);
            handler.removeMessages(MESSAGE_CODE_TO_REAL_VIEW);
        }
        super.onPause();
    }

    //开启实时路况
    private void openLiveMode() {
        if (!(isConnected() && isG2())) {
            XuToast.show(AppContext.getInstance(), STR(R.string.road_live_notconnect));
            return;
        }
        if (AppContext.getInstance().isERROR_CAMERA()) {
            XuToast.show(AppContext.getInstance(), STR(R.string.camera_error));
            return;
        }

        DialogHelp.getInstance().connectDialog(getActivity(), STR(R.string.dialog_tips_connecting), STR(R.string.dialog_tips_connecting2)).setOnKeyListener(keyListener);
        handler.sendEmptyMessageDelayed(MESSAGE_CODE_CONNECT_WIFI, 10000);
        Thread openLiveMode = new Thread(new Runnable() {
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
        AppContext.getInstance().getCachedThreadPool().execute(openLiveMode);
    }

    //设备连手机发出的热点
    private void openRealViewNew() {
        AppContext.getInstance().getDeviceHelper().openDeviceRealViewMode(null);
        DialogHelp.getInstance().hideDialog();
        handler.sendEmptyMessage(MESSAGE_CODE_TO_REAL_VIEW);
    }

    //手机连设备WiFi
    private void openRealViewOld() {
        AppContext.getInstance().getDeviceHelper().openDeviceRealViewMode(new OnWifiOpenListener() {
            @Override
            public void onSuccess(final String wifiName, final String password) {
                XuLog.e(TAG, "打开设备wifi成功：" + wifiName + "   " + password);
                AppContext.getInstance().getCachedThreadPool().execute(new Runnable() {
                    @Override
                    public void run() {
                        if (XuNetWorkUtils.connectWIFI(wifiName, password)) {
                            handler.removeMessages(MESSAGE_CODE_CONNECT_WIFI);
                            DialogHelp.getInstance().hideDialog();
                            //成功连上WIFI  开始进入实时路况
                            handler.sendEmptyMessage(MESSAGE_CODE_TO_REAL_VIEW);
                        }
                    }
                });
            }

            @Override
            public void onFail(int i) {
                XuLog.e(TAG, "打开设备wifi失败：" + i);
            }

            @Override
            public void onGetSanResult(String s) {

            }
        });
    }

    public void cancelConnect() {
        //断开连接
        handler.removeMessages(MESSAGE_CODE_CONNECT_WIFI);
        DialogHelp.getInstance().hideDialog();
        XuNetWorkUtils.cancelConnectWIFI();
        AppContext.getInstance().getDeviceHelper().closeDeviceRealViewMode();
    }

    private void showConnectWifiDialog() {
        DialogHelp.getInstance().hideDialog();
        DialogHelp.getInstance().connectDialog(getActivity(), STR(R.string.dialog_tips_connecting),

                String.format(getResources().getString(R.string.connect_wifi),
                        AppConfig.getInstance(getActivity()).getWifi_name(),
                        AppConfig.getInstance(getActivity()).getWifi_paw()))
                .setOnKeyListener(keyListener);
    }

    class KeyListener implements DialogInterface.OnKeyListener {
        //Dialog的返回键监听
        @Override
        public boolean onKey(DialogInterface dialogInterface, int keyCode, KeyEvent keyEvent) {
            if (keyCode == KEYCODE_BACK) {
                cancelConnect();
                if (dialogInterface != null) dialogInterface.dismiss();
                return true;
            }
            return false;
        }
    }
}
