package com.vispect.android.vispect_g2_app.ui.activity;

import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.vispect.android.vispect_g2_app.R;
import com.vispect.android.vispect_g2_app.app.AppContext;
import com.vispect.android.vispect_g2_app.bean.ADASDevice;
import com.vispect.android.vispect_g2_app.bean.ResultData;
import com.vispect.android.vispect_g2_app.controller.AppApi;
import com.vispect.android.vispect_g2_app.interf.OnSoftKeyboardChangeListener;
import com.vispect.android.vispect_g2_app.interf.ResultCallback;
import com.vispect.android.vispect_g2_app.utils.XuLog;
import com.vispect.android.vispect_g2_app.utils.XuString;
import com.vispect.android.vispect_g2_app.utils.XuToast;
import com.vispect.android.vispect_g2_app.utils.XuView;

import butterknife.Bind;
import butterknife.OnClick;
import interf.OnGetTTCWarValveCallbakc;
import interf.SetDeviceInfoCallback;
import interf.onGetADASAlarmConfigurationCallback;
import interf.onGetDVRConfigurationCallback;
import interf.onGetDrivingBehaviorConfigurtionCallback;
import interf.onGetGPSSensorConfigurationCallback;
import interf.onGetUUIDCallback;
import okhttp3.Request;


/**
 * 工程配置
 * <p>
 * Created by xu on 2017/9/12.
 */
public class EngineeringModelActivity extends BaseActivity {
    private final static String TAG = "EngineeringModelActivity";

    @Bind(R.id.iv_titlebar_title)
    TextView title;
    @Bind(R.id.btn_save)
    Button iv_titlebar_save_frame;

    @Bind(R.id.edit_dvr_configuration_draw_adas)
    EditText edit_dvr_configuration_draw_adas;
    @Bind(R.id.edit_dvr_configuration_condense_level)
    EditText edit_dvr_configuration_condense_level;
    @Bind(R.id.edit_dvr_configuration_recording_ldw)
    EditText edit_dvr_configuration_recording_ldw;
    @Bind(R.id.edit_dvr_configuration_recording_hmw)
    EditText edit_dvr_configuration_recording_hmw;
    @Bind(R.id.edit_dvr_configuration_recording_fcw)
    EditText edit_dvr_configuration_recording_fcw;
    @Bind(R.id.edit_dvr_configuration_recording_pdw)
    EditText edit_dvr_configuration_recording_pdw;
    @Bind(R.id.edit_gps_and_senser_configuration_sensor_sampling_frequency)
    EditText edit_gps_and_senser_configuration_sensor_sampling_frequency;
    @Bind(R.id.edit_gps_and_senser_configuration_gps_sampling_frequency)
    EditText edit_gps_and_senser_configuration_gps_sampling_frequency;
    @Bind(R.id.edit_gps_and_senser_configuration_gps_upload_interva)
    EditText edit_gps_and_senser_configuration_gps_upload_interva;
    @Bind(R.id.edit_gps_and_senser_configuration_senser_upload_interva)
    EditText edit_gps_and_senser_configuration_senser_upload_interva;
    @Bind(R.id.edit_adas_alarm_ttc_war_time_valve)
    EditText edit_adas_alarm_ttc_war_time_valve;
    @Bind(R.id.edit_adas_alarm_car_slide_back_level)
    EditText edit_adas_alarm_car_slide_back_level;
    @Bind(R.id.edit_adas_alarm_before_car_start_up_level)
    EditText edit_adas_alarm_before_car_start_up_level;
    @Bind(R.id.edit_adas_alarm_hmw_model)
    EditText edit_adas_alarm_hmw_model;
    @Bind(R.id.edit_adas_alarm_left_car_model)
    EditText edit_adas_alarm_left_car_model;
    @Bind(R.id.edit_driving_behavior_event_input_model)
    EditText edit_driving_behavior_event_input_model;
    @Bind(R.id.edit_driving_behavior_senser_input_model)
    EditText edit_driving_behavior_senser_input_model;
    @Bind(R.id.edit_uuid)
    EditText edit_uuid;
    @Bind(R.id.ll_buttom)
    LinearLayout ll_buttom;

    private boolean lastdrawADAS;
    private int lastcondenseLevel;
    private boolean lastldw;
    private boolean lasthmw;
    private boolean lastfcw;
    private boolean lastpdw;

    private int lastsensorSamplingFrequency;
    private int lastgpsSamplingFrequency;
    private int lastgpsUploadInterva;
    private int lastsensorUploadInterva;

    private int lastTTCWarvalve;
    private int lastCarSlideBackLevel;
    private int lastbeforeCarStartUpLevel;
    private int lastHMWModel;
    private int lastleftCarModel;

    private int lasteventInputModel;
    private int lastsensorInputModel;

    private String lastUUID;


    Handler mHandler = new Handler();
    Runnable showprogress = new Runnable() {
        @Override
        public void run() {
            //  showProgress();
        }
    };
    Runnable dissprogress = new Runnable() {
        @Override
        public void run() {
            //   hideProgress();
        }
    };
    Runnable showToastByLegalValue = new Runnable() {
        @Override
        public void run() {
            XuToast.show(EngineeringModelActivity.this, STR(R.string.edit_input_legal_value));
            //    hideProgress();
        }
    };

    Runnable saveFail = new Runnable() {
        @Override
        public void run() {
            XuToast.show(EngineeringModelActivity.this, STR(R.string.save_fail));
            //    hideProgress();
        }
    };
    Runnable saveSuccess = new Runnable() {
        @Override
        public void run() {
            XuToast.show(EngineeringModelActivity.this, STR(R.string.save_success));
            //  hideProgress();
        }
    };

    Runnable setDVRConfig = new Runnable() {
        @Override
        public void run() {
            try {
                edit_dvr_configuration_draw_adas.setText(lastdrawADAS ? 1 + "" : 0 + "");
                edit_dvr_configuration_condense_level.setText(lastcondenseLevel + "");
                edit_dvr_configuration_recording_ldw.setText(lastldw ? 1 + "" : 0 + "");
                edit_dvr_configuration_recording_hmw.setText(lasthmw ? 1 + "" : 0 + "");
                edit_dvr_configuration_recording_fcw.setText(lastfcw ? 1 + "" : 0 + "");
                edit_dvr_configuration_recording_pdw.setText(lastpdw ? 1 + "" : 0 + "");
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    };

    Runnable setGPSSenserConfig = new Runnable() {
        @Override
        public void run() {
            try {
                edit_gps_and_senser_configuration_sensor_sampling_frequency.setText(lastsensorSamplingFrequency + "");
                edit_gps_and_senser_configuration_gps_sampling_frequency.setText(lastgpsSamplingFrequency + "");
                edit_gps_and_senser_configuration_gps_upload_interva.setText(lastgpsUploadInterva + "");
                edit_gps_and_senser_configuration_senser_upload_interva.setText(lastsensorUploadInterva + "");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    Runnable setADASAlarmConfig = new Runnable() {
        @Override
        public void run() {
            try {
                edit_adas_alarm_car_slide_back_level.setText(lastCarSlideBackLevel + "");
                edit_adas_alarm_before_car_start_up_level.setText(lastbeforeCarStartUpLevel + "");
                edit_adas_alarm_hmw_model.setText(lastHMWModel + "");
                edit_adas_alarm_left_car_model.setText(lastleftCarModel + "");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    Runnable setDrivingBehaviorConfig = new Runnable() {
        @Override
        public void run() {
            try {
                edit_driving_behavior_event_input_model.setText(lasteventInputModel + "");
                edit_driving_behavior_senser_input_model.setText(lastsensorInputModel + "");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    private boolean canToConsumption = false;
    Runnable setUUID = new Runnable() {
        @Override
        public void run() {
            try {
                edit_uuid.setText(XuString.fixed_length(Integer.parseInt(lastUUID), 10, '0') + "");
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    };


    private int newuuid = -1;
    private Runnable shownewUUID = new Runnable() {
        @Override
        public void run() {
            edit_uuid.setText(XuString.fixed_length(newuuid, 10, '0') + "");
        }
    };


    private void getanewuuid() {
        AppApi.getDeviceId(new ResultCallback<ResultData<ADASDevice>>() {
            @Override
            public void onFailure(Request request, Exception e) {
                XuLog.e(TAG, "获取一个UUID失败：" + e.getMessage());
            }

            @Override
            public void onResponse(ResultData<ADASDevice> response) {
                XuLog.e(TAG, "获取一个UUID成功：" + response.getMsg().toString());
                newuuid = response.getMsg().getDeviceId();
                canToConsumption = true;
                mHandler.post(shownewUUID);
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        saveUUID();
                    }
                });
            }
        });
    }

    private OnSoftKeyboardChangeListener onSoftKeyboardChangeListener = new OnSoftKeyboardChangeListener() {
        @Override
        public void onSoftKeyBoardChange(final int softKeybardHeight, final boolean visible) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    ViewGroup.LayoutParams tmep = null;
                    if (visible) {
                        tmep = ll_buttom.getLayoutParams();
                        tmep.height = softKeybardHeight;
                        ll_buttom.setLayoutParams(tmep);

                    } else {
                        tmep = ll_buttom.getLayoutParams();
                        tmep.height = 1;
                        ll_buttom.setLayoutParams(tmep);
                    }
                }
            });

        }
    };

    @Override
    public int getContentResource() {
        return R.layout.activity_engineering_model;
    }

    @Override
    protected void initView(View view) {
        title.setText(STR(R.string.engineering_model_title));
        XuView.setViewVisible(iv_titlebar_save_frame, true);
        if (AppContext.getInstance().getDeviceHelper().isConnectedDevice() && !AppContext.getInstance().isS()) {
            AppContext.getInstance().getDeviceHelper().openEngineeringModel();
            getValue();
        }
        observeSoftKeyboard(onSoftKeyboardChangeListener);

    }


    private void getValue() {
        AppContext.getInstance().getCachedThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    //先获取行车记录配置
                    AppContext.getInstance().getDeviceHelper().getDVRConfiguration(new onGetDVRConfigurationCallback() {
                        @Override
                        public void onSuccess(boolean drawADAS, int condenseLevel, boolean ldw, boolean hmw, boolean fcw, boolean pdw) {
                            XuLog.e(TAG, "获取DVR的参数配置成功");
                            lastdrawADAS = drawADAS;
                            lastcondenseLevel = condenseLevel;
                            lastldw = ldw;
                            lasthmw = hmw;
                            lastfcw = fcw;
                            lastpdw = pdw;
                            mHandler.post(setDVRConfig);
                        }

                        @Override
                        public void onFail(int i) {
                            XuLog.e(TAG, "获取DVR的参数配置失败：" + i);
                        }
                    });

                    Thread.sleep(300);

                    //获取GOS&传感器参数配置
                    AppContext.getInstance().getDeviceHelper().getGPSSensorConfiguration(new onGetGPSSensorConfigurationCallback() {
                        @Override
                        public void onSuccess(int sensorSamplingFrequency, int gpsSamplingFrequency, int gpsUploadInterva, int sensorUploadInterva) {
                            XuLog.e(TAG, "获取GOS&传感器参数配置成功");
                            lastsensorSamplingFrequency = sensorSamplingFrequency;
                            lastgpsSamplingFrequency = gpsSamplingFrequency;
                            lastgpsUploadInterva = gpsUploadInterva;
                            lastsensorUploadInterva = sensorUploadInterva;
                            mHandler.post(setGPSSenserConfig);
                        }

                        @Override
                        public void onFail(int i) {
                            XuLog.e(TAG, "获取GOS&传感器参数配置失败：" + i);
                        }
                    });

                    Thread.sleep(300);

                    //获取ADAS报警参数设置
                    AppContext.getInstance().getDeviceHelper().getADASAlarmConfiguration(new onGetADASAlarmConfigurationCallback() {
                        @Override
                        public void onSuccess(int CarSlideBackLevel, int beforeCarStartUpLevel, int HMWModel, int leftCarModel) {
                            XuLog.e(TAG, "获取ADAS报警参数设置成功");
                            lastCarSlideBackLevel = CarSlideBackLevel;
                            lastbeforeCarStartUpLevel = beforeCarStartUpLevel;
                            lastHMWModel = HMWModel;
                            lastleftCarModel = leftCarModel;
                            mHandler.post(setADASAlarmConfig);


                        }

                        @Override
                        public void onFail(int i) {
                            XuLog.e(TAG, "获取ADAS报警参数设置失败：" + i);
                        }
                    });

                    Thread.sleep(300);

                    //获取TTC报警时间
                    AppContext.getInstance().getDeviceHelper().getTTCValve(new OnGetTTCWarValveCallbakc() {
                        @Override
                        public void onSuccess(float v) {
                            XuLog.e(TAG, "获取TTC报警时间成功");
                            edit_adas_alarm_ttc_war_time_valve.setText((int) (v * 10f) + "");
                            lastTTCWarvalve = (int) (v * 10);
                        }

                        @Override
                        public void onFail(int i) {
                            XuLog.e(TAG, "获取TTC报警时间失败：" + i);
                        }
                    });

                    Thread.sleep(300);

                    //获取驾驶行为参数配置
                    AppContext.getInstance().getDeviceHelper().getDrivingBehaviorConfigurtion(new onGetDrivingBehaviorConfigurtionCallback() {
                        @Override
                        public void onSuccess(int eventInputModel, int sensorInputModel) {
                            XuLog.e(TAG, "获取驾驶行为参数配置成功");
                            lasteventInputModel = eventInputModel;
                            lastsensorInputModel = sensorInputModel;
                            mHandler.post(setDrivingBehaviorConfig);

                        }

                        @Override
                        public void onFail(int i) {
                            XuLog.e(TAG, "获取驾驶行为参数配置失败：" + i);
                        }
                    });

                    Thread.sleep(300);

                    //获取UUI
                    AppContext.getInstance().getDeviceHelper().getUUID(new onGetUUIDCallback() {
                        @Override
                        public void onSuccess(String uuid) {
                            XuLog.e(TAG, "获取UUI成功");
                            lastUUID = uuid;
                            if (lastUUID.isEmpty() || lastUUID.equals("-1")) {
                                getanewuuid();
                            } else {
                                mHandler.post(setUUID);
                            }

                        }

                        @Override
                        public void onFail(int i) {
                            XuLog.e(TAG, "获取UUI失败：" + i);
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });


    }


    private boolean checkValue(int last, int now) {
        //TODO 检查输入值是否满足要求
        if (last != now && now != -1) {
            return true;
        } else {
            return false;
        }

    }


    @OnClick(R.id.tv_engineering_model_dvr_configuration_title)
    void saveDVRConfiguration() {
        final int drawADAS = Integer.parseInt(edit_dvr_configuration_draw_adas.getText().toString().trim().equals("- -") ? "-1" : edit_dvr_configuration_draw_adas.getText().toString().trim());
        final int condenseLevel = Integer.parseInt(edit_dvr_configuration_condense_level.getText().toString().trim().equals("- -") ? "-1" : edit_dvr_configuration_condense_level.getText().toString().trim());
        final int ldw = Integer.parseInt(edit_dvr_configuration_recording_ldw.getText().toString().trim().equals("- -") ? "-1" : edit_dvr_configuration_recording_ldw.getText().toString().trim());
        final int hmw = Integer.parseInt(edit_dvr_configuration_recording_hmw.getText().toString().trim().equals("- -") ? "-1" : edit_dvr_configuration_recording_hmw.getText().toString().trim());
        final int fcw = Integer.parseInt(edit_dvr_configuration_recording_fcw.getText().toString().trim().equals("- -") ? "-1" : edit_dvr_configuration_recording_fcw.getText().toString().trim());
        final int pdw = Integer.parseInt(edit_dvr_configuration_recording_pdw.getText().toString().trim().equals("- -") ? "-1" : edit_dvr_configuration_recording_pdw.getText().toString().trim());
        if (checkValue(lastdrawADAS ? 1 : 0, drawADAS) || checkValue(lastcondenseLevel, condenseLevel) || checkValue(lastldw ? 1 : 0, ldw) || checkValue(lasthmw ? 1 : 0, hmw) || checkValue(lastfcw ? 1 : 0, fcw) || checkValue(lastpdw ? 1 : 0, pdw)) {

            if (XuString.checkNumericalRange(drawADAS, 0, 1) && XuString.checkNumericalRange(condenseLevel, 1, 5) && XuString.checkNumericalRange(ldw, 0, 1) && XuString.checkNumericalRange(hmw, 0, 1) && XuString.checkNumericalRange(fcw, 0, 1) && XuString.checkNumericalRange(pdw, 0, 1)) {
                AppContext.getInstance().getDeviceHelper().DVRConfiguration(drawADAS == 1 ? true : false, condenseLevel, ldw, hmw, fcw, pdw, new SetDeviceInfoCallback() {
                    @Override
                    public void onSuccess() {
                        XuLog.e(TAG, "保存行车记录配置成功！");
                        lastdrawADAS = drawADAS == 1;
                        lastcondenseLevel = condenseLevel;
                        lastldw = ldw == 1;
                        lasthmw = hmw == 1;
                        lastfcw = fcw == 1;
                        lastpdw = pdw == 1;
                        mHandler.post(saveSuccess);

                    }

                    @Override
                    public void onFail(int i) {
                        XuLog.e(TAG, "保存行车记录配置失败！   " + i);
                        mHandler.post(saveFail);
                    }
                });
            } else {
                mHandler.post(showToastByLegalValue);
                if (!XuString.checkNumericalRange(drawADAS, 0, 1)) {
                    setEditTextViewFocus(edit_dvr_configuration_draw_adas);
                    return;
                }
                if (!XuString.checkNumericalRange(condenseLevel, 1, 5)) {
                    setEditTextViewFocus(edit_dvr_configuration_condense_level);
                    return;
                }
                if (!XuString.checkNumericalRange(ldw, 0, 1)) {
                    setEditTextViewFocus(edit_dvr_configuration_recording_ldw);
                    return;
                }
                if (!XuString.checkNumericalRange(hmw, 0, 1)) {
                    setEditTextViewFocus(edit_dvr_configuration_recording_hmw);
                    return;
                }
                if (!XuString.checkNumericalRange(fcw, 0, 1)) {
                    setEditTextViewFocus(edit_dvr_configuration_recording_fcw);
                    return;
                }
                if (!XuString.checkNumericalRange(pdw, 0, 1)) {
                    setEditTextViewFocus(edit_dvr_configuration_recording_pdw);
                    return;
                }

                return;
            }

        }

    }

    @OnClick(R.id.tv_engineering_model_gps_and_senser_configuration_title)
    void saveGPSSensorConfiguration() {
        final int sensorSamplingFrequency = Integer.parseInt(edit_gps_and_senser_configuration_sensor_sampling_frequency.getText().toString().trim().equals("- -") ? "-1" : edit_gps_and_senser_configuration_sensor_sampling_frequency.getText().toString().trim());
        final int gpsSamplingFrequency = Integer.parseInt(edit_gps_and_senser_configuration_gps_sampling_frequency.getText().toString().trim().equals("- -") ? "-1" : edit_gps_and_senser_configuration_gps_sampling_frequency.getText().toString().trim());
        final int gpsUploadInterva = Integer.parseInt(edit_gps_and_senser_configuration_gps_upload_interva.getText().toString().trim().equals("- -") ? "-1" : edit_gps_and_senser_configuration_gps_upload_interva.getText().toString().trim());
        final int sensorUploadInterva = Integer.parseInt(edit_gps_and_senser_configuration_senser_upload_interva.getText().toString().trim().equals("- -") ? "-1" : edit_gps_and_senser_configuration_senser_upload_interva.getText().toString().trim());

        if (checkValue(lastsensorSamplingFrequency, sensorSamplingFrequency) || checkValue(lastgpsSamplingFrequency, gpsSamplingFrequency) || checkValue(lastgpsUploadInterva, gpsUploadInterva) || checkValue(lastsensorUploadInterva, sensorUploadInterva)) {

            if (XuString.checkNumericalRange(sensorSamplingFrequency, 0, 100) && XuString.checkNumericalRange(gpsSamplingFrequency, 0, 100) && XuString.checkNumericalRange(gpsUploadInterva, 0, 65535) && XuString.checkNumericalRange(sensorUploadInterva, 0, 65535)) {
                AppContext.getInstance().getDeviceHelper().GPSSensorConfiguration(sensorSamplingFrequency, gpsSamplingFrequency, gpsUploadInterva, sensorUploadInterva, new SetDeviceInfoCallback() {
                    @Override
                    public void onSuccess() {
                        XuLog.e(TAG, "保存GPS&senser参数配置成功！");
                        lastsensorSamplingFrequency = sensorSamplingFrequency;
                        lastgpsSamplingFrequency = gpsSamplingFrequency;
                        lastgpsUploadInterva = gpsUploadInterva;
                        lastsensorUploadInterva = sensorUploadInterva;
                        mHandler.post(saveSuccess);

                    }

                    @Override
                    public void onFail(int i) {
                        XuLog.e(TAG, "保存GPS&senser参数配置失败！   " + i);
                        mHandler.post(saveFail);
                    }
                });
            } else {
                mHandler.post(showToastByLegalValue);
                if (!XuString.checkNumericalRange(sensorSamplingFrequency, 0, 100)) {
                    setEditTextViewFocus(edit_gps_and_senser_configuration_sensor_sampling_frequency);
                    return;
                }
                if (!XuString.checkNumericalRange(gpsSamplingFrequency, 0, 100)) {
                    setEditTextViewFocus(edit_gps_and_senser_configuration_gps_sampling_frequency);
                    return;
                }
                if (!XuString.checkNumericalRange(gpsUploadInterva, 0, 65535)) {
                    setEditTextViewFocus(edit_gps_and_senser_configuration_gps_upload_interva);
                    return;
                }
                if (!XuString.checkNumericalRange(sensorUploadInterva, 0, 65535)) {
                    setEditTextViewFocus(edit_gps_and_senser_configuration_senser_upload_interva);
                    return;
                }
                return;
            }

        }


    }

    @OnClick(R.id.tv_engineering_model_adas_alarm_configuration_title)
    void saveADASAlarmConfiguration() {
        final int CarSlideBackLevel = Integer.parseInt(edit_adas_alarm_car_slide_back_level.getText().toString().trim().equals("- -") ? "-1" : edit_adas_alarm_car_slide_back_level.getText().toString().trim());
        final int beforeCarStartUpLevel = Integer.parseInt(edit_adas_alarm_before_car_start_up_level.getText().toString().trim().equals("- -") ? "-1" : edit_adas_alarm_before_car_start_up_level.getText().toString().trim());
        final int HMWModel = Integer.parseInt(edit_adas_alarm_hmw_model.getText().toString().trim().equals("- -") ? "-1" : edit_adas_alarm_hmw_model.getText().toString().trim());
        final int leftCarModel = Integer.parseInt(edit_adas_alarm_left_car_model.getText().toString().trim().equals("- -") ? "-1" : edit_adas_alarm_left_car_model.getText().toString().trim());
        final int ttcwarvalve = Integer.parseInt(edit_adas_alarm_ttc_war_time_valve.getText().toString().trim().equals("- -") ? "-1" : edit_adas_alarm_ttc_war_time_valve.getText().toString().trim());

        if (checkValue(lastCarSlideBackLevel, CarSlideBackLevel) || checkValue(lastbeforeCarStartUpLevel, beforeCarStartUpLevel) || checkValue(lastHMWModel, HMWModel) || checkValue(lastleftCarModel, leftCarModel) || checkValue(lastTTCWarvalve, ttcwarvalve)) {
            if (XuString.checkNumericalRange(CarSlideBackLevel, 0, 5) && XuString.checkNumericalRange(beforeCarStartUpLevel, 0, 5) && XuString.checkNumericalRange(HMWModel, 0, 1) && XuString.checkNumericalRange(leftCarModel, 0, 1) && XuString.checkNumericalRange(ttcwarvalve, 10, 50)) {
                AppContext.getInstance().getDeviceHelper().ADASAlarmConfiguration(CarSlideBackLevel, beforeCarStartUpLevel, HMWModel, leftCarModel, new SetDeviceInfoCallback() {
                    @Override
                    public void onSuccess() {
                        XuLog.e(TAG, "保存ADAS报警参数配置成功！");
                        lastCarSlideBackLevel = CarSlideBackLevel;
                        lastbeforeCarStartUpLevel = beforeCarStartUpLevel;
                        lastHMWModel = HMWModel;
                        lastleftCarModel = leftCarModel;
                        mHandler.post(saveSuccess);
                    }

                    @Override
                    public void onFail(int i) {
                        XuLog.e(TAG, "保存ADAS报警参数配置失败！   " + i);
                        mHandler.post(saveFail);
                    }
                });

                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                AppContext.getInstance().getDeviceHelper().setTTCValve((float) ttcwarvalve / 10f, new SetDeviceInfoCallback() {
                    @Override
                    public void onSuccess() {
                        XuLog.e(TAG, "设置TTC报警时间阈值成功！");
                    }

                    @Override
                    public void onFail(int i) {
                        XuLog.e(TAG, "设置TTC报警时间阈值失败：" + i);
                    }
                });


            } else {
                mHandler.post(showToastByLegalValue);

                if (!XuString.checkNumericalRange(CarSlideBackLevel, 0, 5)) {
                    setEditTextViewFocus(edit_adas_alarm_car_slide_back_level);
                    return;
                }
                if (!XuString.checkNumericalRange(beforeCarStartUpLevel, 0, 5)) {
                    setEditTextViewFocus(edit_adas_alarm_before_car_start_up_level);
                    return;
                }
                if (!XuString.checkNumericalRange(HMWModel, 0, 1)) {
                    setEditTextViewFocus(edit_adas_alarm_hmw_model);
                    return;
                }
                if (!XuString.checkNumericalRange(leftCarModel, 0, 1)) {
                    setEditTextViewFocus(edit_adas_alarm_left_car_model);
                    return;
                }

                if (!XuString.checkNumericalRange(ttcwarvalve, 10, 50)) {
                    setEditTextViewFocus(edit_adas_alarm_ttc_war_time_valve);
                    return;
                }

                return;
            }


        }


    }

    @OnClick(R.id.tv_engineering_model_driving_behavior_configuration_title)
    void saveDrivingBehaviorConfigurtion() {
        int eventInputModel = Integer.parseInt(edit_driving_behavior_event_input_model.getText().toString().trim().equals("- -") ? "-1" : edit_driving_behavior_event_input_model.getText().toString().trim());
        int sensorInputModel = Integer.parseInt(edit_driving_behavior_senser_input_model.getText().toString().trim().equals("- -") ? "-1" : edit_driving_behavior_senser_input_model.getText().toString().trim());
        if (checkValue(lasteventInputModel, eventInputModel) || checkValue(lastsensorInputModel, sensorInputModel)) {
            if (XuString.checkNumericalRange(eventInputModel, 0, 3) && XuString.checkNumericalRange(sensorInputModel, 0, 3)) {
                AppContext.getInstance().getDeviceHelper().drivingBehaviorConfigurtion(eventInputModel, sensorInputModel, new SetDeviceInfoCallback() {
                    @Override
                    public void onSuccess() {
                        XuLog.e(TAG, "保存ADAS报警参数配置成功！");
                        mHandler.post(saveSuccess);
                    }

                    @Override
                    public void onFail(int i) {
                        XuLog.e(TAG, "保存ADAS报警参数配置失败！   " + i);
                        mHandler.post(saveFail);
                    }
                });
            } else {
                mHandler.post(showToastByLegalValue);

                if (!XuString.checkNumericalRange(eventInputModel, 0, 3)) {
                    setEditTextViewFocus(edit_driving_behavior_event_input_model);
                    return;
                }
                if (!XuString.checkNumericalRange(sensorInputModel, 0, 3)) {
                    setEditTextViewFocus(edit_driving_behavior_senser_input_model);
                    return;
                }


                return;
            }
        }


    }

    @OnClick(R.id.tv_engineering_model_uuid_title)
    void saveUUID() {
        final int uuid = Integer.parseInt(edit_uuid.getText().toString().trim().equals("- -") ? "-1" : edit_uuid.getText().toString().trim());
        if (checkValue(lastUUID.isEmpty() ? -1 : Integer.valueOf(lastUUID), uuid)) {
            long max = 4294967295l;
            if (uuid >= 0 && uuid <= max) {
                AppContext.getInstance().getDeviceHelper().setUUID(uuid, new SetDeviceInfoCallback() {
                    @Override
                    public void onSuccess() {
                        XuLog.e(TAG, "保存UUID成功！");
                        lastUUID = uuid + "";
                        mHandler.post(dissprogress);
                        mHandler.post(saveSuccess);
                        if (canToConsumption) {
                            AppApi.consumptionDeviceId(Integer.parseInt(lastUUID), new ResultCallback<ResultData<String>>() {
                                @Override
                                public void onFailure(Request request, Exception e) {
                                    XuLog.e(TAG, "消耗掉一个UUID失败：" + e.getMessage());

                                }

                                @Override
                                public void onResponse(ResultData<String> response) {
                                    XuLog.e(TAG, "消耗掉一个UUID成功！");
                                    canToConsumption = false;
                                }
                            });
                        }
                    }

                    @Override
                    public void onFail(int i) {
                        XuLog.e(TAG, "保存UUID失败！   " + i);
                        mHandler.post(dissprogress);
                        mHandler.post(saveFail);
                    }
                });
            } else {
                mHandler.post(showToastByLegalValue);
                mHandler.post(saveFail);
                setEditTextViewFocus(edit_uuid);
                return;
            }

        }

    }

    private void setEditTextViewFocus(final EditText edit) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                edit.requestFocus();
                edit.requestFocusFromTouch();
            }
        });

    }

    @OnClick(R.id.btn_save)
    void saveData(View v) {
        mHandler.post(showprogress);
        AppContext.getInstance().getCachedThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    //先保存行车记录配置
                    saveDVRConfiguration();

                    Thread.sleep(500);

                    //再保存GPS&senser参数配置
                    saveGPSSensorConfiguration();

                    Thread.sleep(500);

                    //再保存ADAS报警参数配置
                    saveADASAlarmConfiguration();

                    Thread.sleep(500);

                    //保存驾驶行为参数配置
                    saveDrivingBehaviorConfigurtion();

                    Thread.sleep(800);

                    //保存UUID
                    saveUUID();

                    mHandler.post(dissprogress);
                } catch (Exception e) {
                    e.printStackTrace();
                    mHandler.post(dissprogress);
                }

            }
        });

        finish();
    }


    @OnClick(R.id.iv_titlebar_back)
    void back() {
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppContext.getInstance().getDeviceHelper().closeEngineeringModel();
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
    }
}
