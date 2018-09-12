package com.vispect.android.vispect_g2_app.ui.fragment;

import android.view.View;
import android.widget.AdapterView;

import com.vispect.android.vispect_g2_app.R;
import com.vispect.android.vispect_g2_app.adapter.SettingsAdapter;
import com.vispect.android.vispect_g2_app.app.AppContext;
import com.vispect.android.vispect_g2_app.base.BaseFragment;
import com.vispect.android.vispect_g2_app.bean.Setting;
import com.vispect.android.vispect_g2_app.ui.widget.MoListView;
import com.vispect.android.vispect_g2_app.utils.XuString;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;

import static android.view.View.GONE;

/**
 * Created by mo on 2018/7/12.
 * <p>
 * 设置界面显示的Fragment
 */

public class SettingsFragment extends BaseFragment {
    @Bind(R.id.list_settings)
    MoListView settingsView;
    //    private CameraSettingsFragment cameraSettingsFragment; //镜头类型设置
//    private DSMSettings dsmSettings; //DSM相关设置
    private List<Setting> settings = new ArrayList<>();
    private boolean isZh = XuString.isZh(AppContext.getInstance());

    {
        settings.add(new Setting(R.string.set_lane_departrue, new WarSetUpFragment()));//参数设置
        settings.add(new Setting(R.string.set_driver_status, new DriverStatusFragment()));//设置疲劳驾驶监测
        settings.add(new Setting(R.string.set_side_cameras, new SideFragment()));//设置侧边摄像头
        settings.add(new Setting(R.string.set_password_wifi, new DeviceSetUpFragment()));//设置蓝牙和WiFi密码
        settings.add(new Setting(R.string.set_camera_socket, new CameraTypeFragment()));//设置Cam1接口和Cam2接口
        settings.add(new Setting(R.string.about_updates, new CheckUpdataFragment()));//检测升级
        settings.add(new Setting(R.string.about_this_app, new BleInfoFragment()));//检测当前版本
    }

    public static boolean isNumeric(String str) {
        for (int i = str.length(); --i >= 0; ) {
            if (!Character.isDigit(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int getContentResource() {
        return R.layout.fragment_settings;
    }

    @Override
    protected void initView() {

        if (isZh) settings.remove(2);//中文版隐藏侧边摄像头
        getActivity().findViewById(R.id.btn_save).setVisibility(GONE);

        SettingsAdapter adapter = new SettingsAdapter(getActivity());
        adapter.setData(settings);
        settingsView.setAdapter(adapter);

        settingsView.setOnItemClickListener(new itemClick());
    }

    @Override
    public void onResume() {
        super.onResume();
        if (AppContext.getInstance().isDeveloperMode()) {
//            if (isFrist) {
//                isFrist = false;
//                data.add(STR(R.string.adjust_camera_atlow));
//                data.add(STR(R.string.driving_behavior_setting));
//                data.add(STR(R.string.engineering_setting));
//                data.add(STR(R.string.DSM_settings));
//                data.add(STR(R.string.status_of_parts));
//                data.add(STR(R.string.restore_initial));
//            }
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            onResume();
        }
    }

    private class itemClick implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
//            if (isZh && i > 1) {
//                i += 1;
//            }
//            if (i == 12) {
//                if (AppContext.getInstance().getDeviceHelper().isConnectedDevice()) {
//                    UIHelper.showAsk(getActivity(), STR(R.string.ask_reset), true, new OnClickYesOrNoListener() {
//                        @Override
//                        public void isyes(boolean b, DialogInterface dialog) {
//                            if (b) {
//                                AppContext.getInstance().getDeviceHelper().resetDeviceData(new SetDeviceInfoCallback() {
//                                    @Override
//                                    public void onSuccess() {
//                                        XuToast.show(getActivity(), STR(R.string.success));
//                                    }
//
//                                    @Override
//                                    public void onFail(int i) {
//                                        XuToast.show(getActivity(), STR(R.string.fail) + ":" + i);
//                                    }
//                                });
//                            }
//                            dialog.dismiss();
//                        }
//                    });
//                } else {
//                    XuToast.show(getActivity(), STR(R.string.road_live_notconnect));
//                }
//            } else if (i == 11) {
//                UIHelper.startActivity(getActivity(), DeviceTestingActivity.class);
//            } else if (i == 9) {
//                UIHelper.startActivity(getActivity(), EngineeringModelActivity.class);
//            } else if (i == 8) {
//                UIHelper.startActivity(getActivity(), DrivingBehaviorSettingActivity.class);
//            } else if (i == 7) {
//                DialogHelp.getInstance().editDialog(getActivity(), STR(R.string.adjust_atlow_tips), new DialogClickListener() {
//                    @Override
//                    public void clickYes(String editText) {
//                        DialogHelp.getInstance().hideDialog();
//                        if (isNumeric(editText)) {
//                            int num = Integer.parseInt(editText);
//                            if (num < 0 || num > 255) {
//                                XuToast.show(getActivity(), STR(R.string.input_illegal));
//                            }
//                            AppContext.getInstance().getDeviceHelper().startCameraCorrectOnLow(Integer.parseInt(editText), new SetDeviceInfoCallback() {
//                                @Override
//                                public void onSuccess() {
//                                    XuToast.show(getActivity(), STR(R.string.success));
//                                }
//
//                                @Override
//                                public void onFail(int i) {
//                                    XuToast.show(getActivity(), STR(R.string.fail));
//                                }
//                            });
//                        } else {
//                            XuToast.show(getActivity(), STR(R.string.input_illegal));
//                        }
//                    }
//                });
//            }
            Setting setting = (Setting) adapterView.getAdapter().getItem(position);
            pushToFragment(setting.getFragment());
        }
    }
}
