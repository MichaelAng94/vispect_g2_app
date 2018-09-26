package com.vispect.android.vispect_g2_app.ui.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import com.vispect.android.vispect_g2_app.R;
import com.vispect.android.vispect_g2_app.ui.fragment.AutomaticallyFragment;
import com.vispect.android.vispect_g2_app.ui.fragment.CalibrateFragment;
import com.vispect.android.vispect_g2_app.ui.fragment.EnterSizeFragment;
import com.vispect.android.vispect_g2_app.ui.fragment.InstallListFragment;
import com.vispect.android.vispect_g2_app.ui.fragment.TalkWithCarFragment;
import com.vispect.android.vispect_g2_app.ui.fragment.VehicleInfoFragment;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 安装与标定
 */
public class InstallActivity extends BaseActivity {

    public final static int VEHICLE = 0;    //车辆信息
    public final static int CALIBRATE = 1;   //标定
    public final static int ENTER_SIZE = 2;  //尺寸
    public final static int TELK_WITH_CAR = 3; //与车通讯
    public final static int AUTOMATICALLY = 4; //自动校正
    public final static int INSTALLLIST = 5;  //Installation列表
    public final static int COUNTRY = 20;  //SelectCountry的回调
    public static Handler transHandler;
    @Bind(R.id.frame_install)
    FrameLayout frameInstall;
    @Bind(R.id.btn_save)
    Button btnSave;
    private CalibrateFragment calibrateFragment;
    private AutomaticallyFragment automaticallyFragment;
    private EnterSizeFragment enterSizeFragment;
    private TalkWithCarFragment talkWithCarFragment;
    private VehicleInfoFragment vehicleInfoFragment;
    private InstallListFragment installListFragment;
    private FragmentTransaction transaction;
    private int nowFragment = 5;

    @Override
    public int getContentResource() {
        return R.layout.activity_install;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
    }

    @Override
    protected void initView(View view) {
        setTitle(STR(R.string.title_step));
        switchFragment(5);
        transHandler = new myHandler();
    }

    @OnClick(R.id.img_back_main)
    public void onViewClicked() {
        onBackPressed();
    }

    public void switchFragment(int position) {
        nowFragment = position;
        transaction = getSupportFragmentManager().beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        hideAll();
        switch (position) {
            case VEHICLE:
                setTitle(STR(R.string.vehicle_information));
                if (vehicleInfoFragment == null) {
                    vehicleInfoFragment = new VehicleInfoFragment();
                    transaction.add(R.id.frame_install, vehicleInfoFragment);
                }
                transaction.show(vehicleInfoFragment);
                btnSave.setVisibility(View.VISIBLE);
                break;
            case CALIBRATE:
                setTitle(STR(R.string.calibrate));
                if (calibrateFragment == null) {
                    calibrateFragment = new CalibrateFragment();
                    transaction.add(R.id.frame_install, calibrateFragment);
                }
                transaction.show(calibrateFragment);
                btnSave.setVisibility(View.GONE);
                break;
            case ENTER_SIZE:
                setTitle(STR(R.string.enter_size));
                if (enterSizeFragment == null) {
                    enterSizeFragment = new EnterSizeFragment();
                    transaction.add(R.id.frame_install, enterSizeFragment);
                }
                transaction.show(enterSizeFragment);
                btnSave.setVisibility(View.VISIBLE);
                break;
            case TELK_WITH_CAR:
                setTitle(STR(R.string.tali_with_car));
                if (talkWithCarFragment != null) {
                    transaction.remove(talkWithCarFragment);
                }
                talkWithCarFragment = new TalkWithCarFragment();
                transaction.add(R.id.frame_install, talkWithCarFragment);
                transaction.show(talkWithCarFragment);
                btnSave.setVisibility(View.VISIBLE);
                break;
            case AUTOMATICALLY:
                setTitle(STR(R.string.adapt_automatically));
                if (automaticallyFragment == null) {
                    automaticallyFragment = new AutomaticallyFragment();
                    transaction.add(R.id.frame_install, automaticallyFragment);
                }
                transaction.show(automaticallyFragment);
                btnSave.setVisibility(View.GONE);
                break;
            case INSTALLLIST:
                setTitle(STR(R.string.title_step));
                if (installListFragment == null) {
                    installListFragment = new InstallListFragment();
                    transaction.add(R.id.frame_install, installListFragment);
                }
                transaction.show(installListFragment);
                btnSave.setVisibility(View.GONE);
                break;
        }
        transaction.commit();
    }

    @Override
    public void onBackPressed() {
        if (nowFragment == INSTALLLIST) {
            finish();
        } else {
            switchFragment(INSTALLLIST);
        }
    }

    public void hideAll() {
        if (vehicleInfoFragment != null) {
            transaction.hide(vehicleInfoFragment);
        }
        if (calibrateFragment != null) {
            transaction.hide(calibrateFragment);
        }
        if (automaticallyFragment != null) {
            transaction.hide(automaticallyFragment);
        }
        if (talkWithCarFragment != null) {
            transaction.hide(talkWithCarFragment);
        }
        if (enterSizeFragment != null) {
            transaction.hide(enterSizeFragment);
        }
        if (installListFragment != null) {
            transaction.hide(installListFragment);
        }
    }

    class myHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.arg2 > 5) {
                switch (msg.arg2) {
                    case COUNTRY:
                        vehicleInfoFragment.changeCountry(msg.obj.toString());
                        break;
                }
            } else {
                switchFragment(msg.arg2);
            }
        }
    }

}
