package com.vispect.android.vispect_g2_app.ui.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.Button;

import com.vispect.android.vispect_g2_app.R;
import com.vispect.android.vispect_g2_app.controller.UIHelper;
import com.vispect.android.vispect_g2_app.ui.fragment.BleInfoFragment;
import com.vispect.android.vispect_g2_app.ui.fragment.CameraSettingsFragment;
import com.vispect.android.vispect_g2_app.ui.fragment.CameraTypeFragment;
import com.vispect.android.vispect_g2_app.ui.fragment.CheckUpdataFragment;
import com.vispect.android.vispect_g2_app.ui.fragment.DSMSettings;
import com.vispect.android.vispect_g2_app.ui.fragment.DeviceSetUpFragment;
import com.vispect.android.vispect_g2_app.ui.fragment.DriverStatusFragment;
import com.vispect.android.vispect_g2_app.ui.fragment.SettingsListFragment;
import com.vispect.android.vispect_g2_app.ui.fragment.SideFragment;
import com.vispect.android.vispect_g2_app.ui.fragment.WarSetUpFragment;

import java.util.Set;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by mo on 2018/7/12.
 */

public class SettingsActivity extends BaseActivity {

    @Bind(R.id.btn_save)
    Button btnSave;
    private FragmentTransaction transaction;
    public static Handler transHandler;

    private int nowFragment = -1;

    private SettingsListFragment settingsListFragment; //设置list
    private DeviceSetUpFragment deviceSetUpFragment; //设备设置
    private WarSetUpFragment warSetUpFragment;   //报警设置
    private DriverStatusFragment driverStatusFragment; //DSM设置
    private SideFragment sideFragment;  //侧边镜头报警速度
    private CameraSettingsFragment cameraSettingsFragment; //镜头类型设置
    private CameraTypeFragment cameraTypeFragment; //镜头类型选择
    private BleInfoFragment bleInfoFragment;   //获取设备版本
    private CheckUpdataFragment checkUpdataFragment; //获取更新
    private DSMSettings dsmSettings; //DSM相关设置

    public static final int LIST_SEETINGS = 0;
    public static final int SET_LANE_DEPARTURE = 1;
    public static final int SET_DRIVER_STATUS = 2;
    public static final int SET_SIDE_CAMERAS = 3;
    public static final int SET_PASSWORDS_WIFI = 4;
    public static final int SET_CAMERA_SOCKET = 5;
    public static final int SET_CHECK_DEVICE = 6;
    public static final int CHECK_CURRENT = 7;
    public static final int DSM_SETTING =  11;
    public static final int SET_CAMERA_TYPE = 51;


    @Override
    public int getContentResource() {
        return R.layout.activity_setting;
    }

    @Override
    protected void initView(View view) {
        setTitle("Settings");
        switchFragment(LIST_SEETINGS);
        transHandler = new myHandler();
    }

    @OnClick(R.id.img_back_main)
    public void onViewClicked() {
        if (nowFragment == LIST_SEETINGS){
            finish();
        }else if (nowFragment == SET_CAMERA_TYPE){
            switchFragment(SET_CAMERA_SOCKET);
        }else {
        switchFragment(0);
    }}


    private class myHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switchFragment(msg.arg2 + 1);
        }
    }

    @Override
    public void onBackPressed() {
        if (nowFragment == LIST_SEETINGS) {
            finish();
        } else if (nowFragment == SET_CAMERA_TYPE){
            switchFragment(SET_CAMERA_SOCKET);
        }else {
            switchFragment(LIST_SEETINGS);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }

    public void switchFragment(int position) {
        btnSave.setVisibility(View.GONE);
        transaction = getSupportFragmentManager().beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        hideAll();
        nowFragment = position;
        switch (position) {
            case LIST_SEETINGS:
                setTitle(STR(R.string.setting));
                if (settingsListFragment == null) {
                    settingsListFragment = new SettingsListFragment();
                    transaction.add(R.id.frame_settings, settingsListFragment);
                }
                transaction.show(settingsListFragment);
                break;
            case SET_LANE_DEPARTURE:
                setTitle(STR(R.string.warning_settings));
                btnSave.setVisibility(View.VISIBLE);
                if (warSetUpFragment == null) {
                    warSetUpFragment = new WarSetUpFragment();
                    transaction.add(R.id.frame_settings, warSetUpFragment);
                }
                transaction.show(warSetUpFragment);
                break;
            case SET_DRIVER_STATUS:
                setTitle(STR(R.string.dsm_settings));
                btnSave.setVisibility(View.VISIBLE);
                if (driverStatusFragment == null) {
                    driverStatusFragment = new DriverStatusFragment();
                    transaction.add(R.id.frame_settings, driverStatusFragment);
                }
                transaction.show(driverStatusFragment);
                break;
            case SET_SIDE_CAMERAS:
                setTitle(STR(R.string.side_camera));
                btnSave.setVisibility(View.VISIBLE);
                if (sideFragment == null) {
                    sideFragment = new SideFragment();
                    transaction.add(R.id.frame_settings, sideFragment);
                }
                transaction.show(sideFragment);
                break;
            case SET_PASSWORDS_WIFI:
                btnSave.setVisibility(View.VISIBLE);
                setTitle(STR(R.string.drive));
                if (deviceSetUpFragment == null) {
                    deviceSetUpFragment = new DeviceSetUpFragment();
                    transaction.add(R.id.frame_settings, deviceSetUpFragment);
                }
                transaction.show(deviceSetUpFragment);
                break;
            case SET_CAMERA_SOCKET:
                setTitle(STR(R.string.camera_settings));
                if (cameraSettingsFragment == null) {
                    cameraSettingsFragment = new CameraSettingsFragment();
                    transaction.add(R.id.frame_settings, cameraSettingsFragment);
                }
                transaction.show(cameraSettingsFragment);
                break;
            case SET_CHECK_DEVICE:
                setTitle(STR(R.string.check_devices));
                if (checkUpdataFragment == null) {
                    checkUpdataFragment = new CheckUpdataFragment();
                    transaction.add(R.id.frame_settings, checkUpdataFragment);
                }
                transaction.show(checkUpdataFragment);
                break;
            case CHECK_CURRENT:
                setTitle(STR(R.string.check_current));
                if (bleInfoFragment == null) {
                    bleInfoFragment = new BleInfoFragment();
                    transaction.add(R.id.frame_settings, bleInfoFragment);
                }
                transaction.show(bleInfoFragment);
                break;
            case SET_CAMERA_TYPE:
                if (cameraTypeFragment == null) {
                    cameraTypeFragment = new CameraTypeFragment();
                    transaction.add(R.id.frame_settings, cameraTypeFragment);
                }
                transaction.show(cameraTypeFragment);
                break;
            case DSM_SETTING:
                setTitle(STR(R.string.dsm_setings_title));
                btnSave.setVisibility(View.VISIBLE);
                if (dsmSettings == null) {
                    dsmSettings = new DSMSettings();
                    transaction.add(R.id.frame_settings, dsmSettings);
                }
                transaction.show(dsmSettings);
                break;
        }
        transaction.commit();
    }

    public void hideAll() {
        if (settingsListFragment != null) {
            transaction.hide(settingsListFragment);
        }
        if (deviceSetUpFragment != null) {
            transaction.hide(deviceSetUpFragment);
        }
        if (warSetUpFragment != null) {
            transaction.hide(warSetUpFragment);
        }
        if (driverStatusFragment != null) {
            transaction.hide(driverStatusFragment);
        }
        if (sideFragment != null) {
            transaction.hide(sideFragment);
        }
        if (cameraSettingsFragment!=null){
            transaction.hide(cameraSettingsFragment);
        }
        if (cameraTypeFragment!=null){
            transaction.hide(cameraTypeFragment);
        }
        if (bleInfoFragment!=null){
            transaction.hide(bleInfoFragment);
        }
        if (checkUpdataFragment!=null){
            transaction.hide(checkUpdataFragment);
        }
        if (dsmSettings!=null){
            transaction.hide(dsmSettings);
        }

    }

}
