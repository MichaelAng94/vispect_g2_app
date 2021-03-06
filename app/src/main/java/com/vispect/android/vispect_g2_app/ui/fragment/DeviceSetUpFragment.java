package com.vispect.android.vispect_g2_app.ui.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.vispect.android.vispect_g2_app.R;

import butterknife.Bind;
import interf.GetDeviceBleInfoCallback;
import interf.SetDeviceInfoCallback;

import com.vispect.android.vispect_g2_app.controller.UIHelper;
import com.vispect.android.vispect_g2_app.interf.OnClickYesOrNoListener;
import com.vispect.android.vispect_g2_app.ui.activity.SettingsActivity;
import com.vispect.android.vispect_g2_app.ui.fragment.BaseFragment;
import com.vispect.android.vispect_g2_app.utils.XuLog;
import com.vispect.android.vispect_g2_app.utils.XuString;
import com.vispect.android.vispect_g2_app.utils.XuToast;
import com.vispect.android.vispect_g2_app.app.AppConfig;
import com.vispect.android.vispect_g2_app.app.AppContext;
import com.vispect.android.vispect_g2_app.interf.OnSoftKeyboardChangeListener;

/**
 * 设备参数设置
 * <p>
 * Created by xu on 2017/7/20.
 */
public class DeviceSetUpFragment extends BaseFragment {


    @Bind(R.id.edit_parameters_ble_name)
    EditText edit_parameters_ble_name;
    @Bind(R.id.edit_parameters_ble_pas)
    EditText edit_parameters_ble_pas;
    @Bind(R.id.edit_parameters_wifi_name)
    EditText edit_parameters_wifi_name;
    @Bind(R.id.edit_parameters_wifi_pas)
    EditText edit_parameters_wifi_pas;
    //    @Bind(R.id.ll_buttom)
//    LinearLayout ll_buttom;
    boolean isallsuccess = true;
    Handler mHandler = new Handler();
    Runnable show = new Runnable() {
        @Override
        public void run() {
            showProgress();
        }
    };
    Runnable hide = new Runnable() {
        @Override
        public void run() {
            hideProgress();
        }
    };
    Runnable successRunnable = new Runnable() {
        @Override
        public void run() {
            hideProgress();
            if (!isallsuccess) {
                isallsuccess = true;
            } else {
                XuToast.show(getActivity(), STR(R.string.save_success));
                finish();
            }

        }
    };
    private AppConfig appconfig;
    private String ble_pas, ble_name, wifi_pas, wifi_name;
    private boolean canToSave = true;

    @Override
    public int getContentResource() {
        return R.layout.fragment_setup_device;
    }

    @Override
    protected void initView(View view) {
        if (AppContext.getInstance().getDeviceHelper().isConnectedDevice()) {
            AppContext.getInstance().getCachedThreadPool().execute(new Runnable() {
                @Override
                public void run() {
                    getValue();
                }
            });

        } else {
            XuToast.show(getActivity(), STR(R.string.road_live_notconnect));
        }

        view.findViewById(R.id.btn_save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UIHelper.showAsk(getActivity(), STR(R.string.ask_save_data), true, new OnClickYesOrNoListener() {
                    @Override
                    public void isyes(boolean b, DialogInterface dialog) {
                        if (b) {
                            saveData();
                        }
                        dialog.dismiss();
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
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            onResume();
        }
    }

    private void getValue() {
        //获取账号名密码等数据
        appconfig = AppConfig.getInstance(getActivity());
        AppContext.getInstance().getDeviceHelper().getDeviceBleNameAndPassword(new GetDeviceBleInfoCallback() {
            @Override
            public void onGetNameAndPassword(String name, String password) {
                ble_pas = password;
                ble_name = name;
                wifi_name = appconfig.getWifi_name();
                wifi_pas = appconfig.getWifi_paw();
                if (mHandler != null) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
//                            //临时
//                            AppConfig.getInstance(AppContext.getInstance()).setCurrentDeviceName(ble_name);
                            edit_parameters_ble_name.setText(ble_name);
                            edit_parameters_ble_pas.setText(ble_pas);
                            edit_parameters_wifi_name.setText(wifi_name);
                            edit_parameters_wifi_pas.setText(wifi_pas);
                        }
                    });
                }
            }
        });


    }

    public void saveData() {
        //保存设备信息
        if (AppContext.getInstance().getDeviceHelper().isConnectedDevice()) {

            if (!canToSave) {
                XuLog.e("保存完一次了");
                return;
            }
            ble_pas = edit_parameters_ble_pas.getText().toString().trim();
            ble_name = edit_parameters_ble_name.getText().toString().trim();
            wifi_name = edit_parameters_wifi_name.getText().toString().trim();
            wifi_pas = edit_parameters_wifi_pas.getText().toString().trim();
            if (XuString.isEmpty(ble_pas) || XuString.isEmpty(ble_name) || XuString.isEmpty(wifi_name) || XuString.isEmpty(wifi_pas)) {
                XuToast.show(AppContext.getInstance(), STR(R.string.value_not_null));
                return;
            }

            if (XuString.isConSpeCharacters(ble_pas, 4, 4) || XuString.isConSpeCharacters(ble_name, 2, 15) || XuString.isConSpeCharacters(wifi_name, 3, 32) || XuString.isConSpeCharacters(wifi_pas, 8, 8)) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        XuToast.show(AppContext.getInstance(), STR(R.string.edit_input_legal_value));
                    }
                });

                return;
            }
            if (!XuString.checkNumber(ble_pas, 4)) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        XuToast.show(AppContext.getInstance(), STR(R.string.edit_input_4_number_password));
                    }
                });

                return;
            }
            if (!XuString.checkNumber(wifi_pas, 8)) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        XuToast.show(AppContext.getInstance(), STR(R.string.edit_input_8_number_password));
                    }
                });

                return;
            }

            if (appconfig.getBle_paw().equals(ble_pas) && appconfig.getBle_name().equals(ble_name) && appconfig.getWifi_name().equals(wifi_name) && appconfig.getWifi_paw().equals(wifi_pas)) {
                return;
            }

            AppContext.getInstance().getCachedThreadPool().execute(new Runnable() {
                @Override
                public void run() {
                    if (!appconfig.getBle_paw().equals(ble_pas)) {
                        AppContext.getInstance().getDeviceHelper().setDevicePassword(ble_pas, new SetDeviceInfoCallback() {
                            @Override
                            public void onSuccess() {
                                XuLog.e("设备BLE密码成功！");
                                mHandler.post(hide);
                                mHandler.post(successRunnable);
                            }

                            @Override
                            public void onFail(int i) {
                                XuLog.e("设备BLE密码失败！" + "    " + i);
                                mHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        XuToast.show(getActivity(), STR(R.string.setting_parmater_edit_ble_pas) + STR(R.string.save_fail));
                                    }
                                });
                                mHandler.post(hide);
                            }
                        });
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    if (!appconfig.getBle_name().equals(ble_name)) {
                        AppContext.getInstance().getDeviceHelper().setDeviceName(ble_name, new SetDeviceInfoCallback() {
                            @Override
                            public void onSuccess() {
                                XuLog.e("设备BLE名称成功！");
                                mHandler.post(hide);
                                mHandler.post(successRunnable);
////                                //临时
//                                AppConfig.getInstance(AppContext.getInstance()).setCurrentDeviceName(ble_name);


                            }

                            @Override
                            public void onFail(int i) {
                                XuLog.e("设备BLE名称失败！" + "    " + i);
                                isallsuccess = false;
                                mHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        XuToast.show(getActivity(), STR(R.string.setting_parmater_edit_ble_name) + STR(R.string.save_fail));
                                    }
                                });
                                mHandler.post(hide);
                            }
                        });
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                    if (!appconfig.getWifi_name().equals(wifi_name)) {
                        AppContext.getInstance().getDeviceHelper().setDeviceWifiName(wifi_name, new SetDeviceInfoCallback() {
                            @Override
                            public void onSuccess() {
                                XuLog.e("设备WIFI名称成功！");
                                mHandler.post(hide);
                                mHandler.post(successRunnable);
                            }

                            @Override
                            public void onFail(int i) {
                                XuLog.e("设备WIFI名称失败！" + "    " + i);
                                isallsuccess = false;
                                mHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        XuToast.show(getActivity(), STR(R.string.setting_parmater_edit_wifi_name) + STR(R.string.save_fail));
                                    }
                                });
                                mHandler.post(hide);
                            }
                        });
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    if (!appconfig.getWifi_paw().equals(wifi_pas)) {
                        AppContext.getInstance().getDeviceHelper().setDeviceWifiPassword(wifi_pas, new SetDeviceInfoCallback() {
                            @Override
                            public void onSuccess() {
                                XuLog.e("设备WIFI密码成功！");
                                mHandler.post(hide);
                                mHandler.post(successRunnable);


                            }

                            @Override
                            public void onFail(int i) {
                                XuLog.e("设备WIFI密码失败！" + "    " + i);
                                isallsuccess = false;
                                mHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        XuToast.show(getActivity(), STR(R.string.setting_parmater_edit_wifi_pas) + STR(R.string.save_fail));
                                    }
                                });
                                mHandler.post(hide);
                            }
                        });
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                }
            });


        }
    }

    @Override
    public void onDestroy() {

        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null && imm.isActive()) {
            imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
        super.onDestroy();
    }
}
