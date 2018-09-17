package com.vispect.android.vispect_g2_app.ui.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.vispect.android.vispect_g2_app.R;
import com.vispect.android.vispect_g2_app.adapter.SettingMenuAdapter;
import com.vispect.android.vispect_g2_app.app.AppContext;
import com.vispect.android.vispect_g2_app.bean.ARG;
import com.vispect.android.vispect_g2_app.interf.XuTimeOutCallback;
import com.vispect.android.vispect_g2_app.utils.XuLog;
import com.vispect.android.vispect_g2_app.utils.XuTimeOutUtil;
import com.vispect.android.vispect_g2_app.utils.XuToast;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import interf.GetBleInfoVersionCallback;


/**
 * 当前版本
 * <p>
 * Created by xu on 2015/12/26.
 */
public class BleInfoFragment extends BaseFragment {
    private static final String TAG = "BleInfoFragment";
    String version_s;
    String hardware_version;
    String application_software;
    String software_system;

    Handler mHandler = new Handler();
    @Bind(R.id.hardware_version)
    TextView hardwareVersion;
    @Bind(R.id.software_version)
    TextView softwareVersion;
    @Bind(R.id.application_version)
    TextView applicationVersion;
    @Bind(R.id.odb_version)
    TextView odbVersion;
    @Bind(R.id.buzzer_version)
    TextView buzzerVersion;
    String[] names;
    private XuTimeOutUtil timeoutUtil = new XuTimeOutUtil(new XuTimeOutCallback() {
        @Override
        public void onTimeOut() {

        }
    });
    private int clickCount = 0;
    private Runnable clearClickCount = new Runnable() {
        @Override
        public void run() {
            clickCount = 0;
        }
    };

    @Override
    public int getContentResource() {
        // TODO Auto-generated method stub
        return R.layout.fragment_checkversion;
    }

    @Override
    protected void initView(View view) {
//        // TODO Auto-generated method stub
//        if (!AppContext.getInstance().getDeviceHelper().isConnectedDevice()) {
//            return;
//        }

        AppContext.getInstance().getCachedThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                //获取软件版本
                timeoutUtil.startCheck(ARG.SET_VALUE_TIMEOUT);
                AppContext.getInstance().getDeviceHelper().getDeviceVersion(new GetBleInfoVersionCallback() {
                    @Override
                    public void onSuccess(String deviceHWver, String deviceOTAver, String deviceAPKver, String obdver, String buzzerver, String GPS) {
                        timeoutUtil.stopCheck();

                        hardwareVersion.setText(deviceHWver);
                        softwareVersion.setText(deviceAPKver);
                        applicationVersion.setText(deviceOTAver);
                        odbVersion.setText(obdver);
                        buzzerVersion.setText(buzzerver);
                    }

                    @Override
                    public void onFail(int i) {
                        timeoutUtil.stopCheck();
                    }
                });
            }
        });
        view.findViewById(R.id.img_back_main).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
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

    @OnClick(R.id.re_last)
    public void onViewClicked() {
        clickCount++;
        if (clickCount >= 5) {
            XuLog.d(TAG, "点够五次了，开启调试者模式      " + clickCount);
            XuToast.show(AppContext.getInstance(), STR(R.string.open_developer_mode));
            AppContext.getInstance().setDeveloperMode(true);
            return;
        }
    }
}
