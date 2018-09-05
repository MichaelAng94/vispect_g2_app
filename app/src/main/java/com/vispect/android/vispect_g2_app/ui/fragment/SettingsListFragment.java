package com.vispect.android.vispect_g2_app.ui.fragment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.vispect.android.vispect_g2_app.R;
import com.vispect.android.vispect_g2_app.adapter.CalibrateAdapter;
import com.vispect.android.vispect_g2_app.app.AppContext;
import com.vispect.android.vispect_g2_app.controller.UIHelper;
import com.vispect.android.vispect_g2_app.interf.DialogClickListener;
import com.vispect.android.vispect_g2_app.interf.OnClickYesOrNoListener;
import com.vispect.android.vispect_g2_app.ui.activity.DeviceTestingActivity;
import com.vispect.android.vispect_g2_app.ui.activity.DrivingBehaviorSettingActivity;
import com.vispect.android.vispect_g2_app.ui.activity.EngineeringModelActivity;
import com.vispect.android.vispect_g2_app.ui.activity.SettingsActivity;
import com.vispect.android.vispect_g2_app.ui.widget.DialogHelp;
import com.vispect.android.vispect_g2_app.ui.widget.MoListview;
import com.vispect.android.vispect_g2_app.utils.XuString;
import com.vispect.android.vispect_g2_app.utils.XuToast;

import java.io.IOException;
import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import interf.SetDeviceInfoCallback;

/**
 * Created by mo on 2018/7/12.
 * <p>
 * 设置界面显示的Fragment
 */

public class SettingsListFragment extends BaseFragment {

    @Bind(R.id.list_settings)
    MoListview listSettings;
    Boolean isFrist = true;
    private ArrayList<String> data;
    private boolean isZh = XuString.isZh(AppContext.getInstance());

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
    protected void initView(View view) throws IOException {
        data = new ArrayList<>();
        data.add(STR(R.string.set_lane_departrue));
        data.add(STR(R.string.set_driver_status));
        if (!isZh) data.add(STR(R.string.set_side_cameras));
        data.add(STR(R.string.set_password_wifi));
        data.add(STR(R.string.set_camera_socket));
        data.add(STR(R.string.check_device));
        data.add(STR(R.string.check_version));
        CalibrateAdapter cvl = new CalibrateAdapter(getContext());
        cvl.setData(data);
        listSettings.setAdapter(cvl);
        listSettings.setOnItemClickListener(new itemClick());
    }

    @Override
    public void onResume() {
        super.onResume();
        if (AppContext.getInstance().isDeveloperMode()) {
            if (isFrist) {
                isFrist = false;
                data.add(STR(R.string.adjust_camera_atlow));
                data.add(STR(R.string.driving_behavior_setting));
                data.add(STR(R.string.engineering_setting));
                data.add(STR(R.string.DSM_settings));
                data.add(STR(R.string.status_of_parts));
                data.add(STR(R.string.restore_initial));
            }
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            onResume();
        }
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

    private class itemClick implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            if (isZh && i > 1) {
                i += 1;
            }
            if (i == 12) {
                if (AppContext.getInstance().getDeviceHelper().isConnectedDevice()) {
                    UIHelper.showAsk(getActivity(), STR(R.string.ask_reset), true, new OnClickYesOrNoListener() {
                        @Override
                        public void isyes(boolean b, DialogInterface dialog) {
                            if (b) {
                                AppContext.getInstance().getDeviceHelper().resetDeviceData(new SetDeviceInfoCallback() {
                                    @Override
                                    public void onSuccess() {
                                        XuToast.show(getActivity(), STR(R.string.success));
                                    }

                                    @Override
                                    public void onFail(int i) {
                                        XuToast.show(getActivity(), STR(R.string.fail) + ":" + i);
                                    }
                                });
                            }
                            dialog.dismiss();
                        }
                    });
                } else {
                    XuToast.show(getActivity(), STR(R.string.road_live_notconnect));
                }
            } else if (i == 11) {
                UIHelper.startActivity(getActivity(), DeviceTestingActivity.class);
            } else if (i == 9) {
                UIHelper.startActivity(getActivity(), EngineeringModelActivity.class);
            } else if (i == 8) {
                UIHelper.startActivity(getActivity(), DrivingBehaviorSettingActivity.class);
            } else if (i == 7) {
                DialogHelp.getInstance().editDialog(getActivity(), STR(R.string.adjust_atlow_tips), new DialogClickListener() {
                    @Override
                    public void clickYes(String editText) {
                        DialogHelp.getInstance().hideDialog();
                        if (isNumeric(editText)) {
                            int num = Integer.parseInt(editText);
                            if (num < 0 || num > 255) {
                                XuToast.show(getActivity(), STR(R.string.input_illegal));
                            }
                            AppContext.getInstance().getDeviceHelper().startCameraCorrectOnLow(Integer.parseInt(editText), new SetDeviceInfoCallback() {
                                @Override
                                public void onSuccess() {
                                    XuToast.show(getActivity(), STR(R.string.success));
                                }

                                @Override
                                public void onFail(int i) {
                                    XuToast.show(getActivity(), STR(R.string.fail));
                                }
                            });
                        } else {
                            XuToast.show(getActivity(), STR(R.string.input_illegal));
                        }
                    }
                });
            } else {
                Message msg = new Message();
                msg.arg2 = i;
                SettingsActivity.transHandler.sendMessage(msg);
            }
        }
    }
}
