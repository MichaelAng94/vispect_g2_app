package com.vispect.android.vispect_g2_app.ui.fragment;

/**
 * Created by mo on 2018/3/27.
 */

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.vispect.android.vispect_g2_app.R;
import com.vispect.android.vispect_g2_app.app.AppContext;
import com.vispect.android.vispect_g2_app.bean.ResultData;
import com.vispect.android.vispect_g2_app.bean.SOBDCrackValue;
import com.vispect.android.vispect_g2_app.controller.AppApi;
import com.vispect.android.vispect_g2_app.controller.UIHelper;
import com.vispect.android.vispect_g2_app.interf.OnClickYesOrNoListener;
import com.vispect.android.vispect_g2_app.interf.ResultCallback;
import com.vispect.android.vispect_g2_app.ui.activity.InstallActivity;
import com.vispect.android.vispect_g2_app.ui.activity.OBDAutoCrackActivity;
import com.vispect.android.vispect_g2_app.utils.XuLog;
import com.vispect.android.vispect_g2_app.utils.XuString;
import com.vispect.android.vispect_g2_app.utils.XuToast;

import bean.Vispect_SDK_ARG;
import bean.Vispect_SDK_CarInfo;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import interf.GetCarInfoListener;
import interf.OBDVerificationCallback;
import interf.SetOBDCrackDataCallback;
import okhttp3.Request;


/**
 * Created by mo on 2018/3/27.
 * 与车交流
 */


public class TalkWithCarFragment extends BaseFragment {

    private final int BLE_CONNECT = 406;
    private final int REQUST_CONNECT = 306;
    Handler mHandler = new Handler();
    SpannableStringBuilder builder;
    ForegroundColorSpan redSpan;
    boolean cantoget = false;
    @Bind(R.id.tv_rataionrate_number_1)
    TextView tv_rataionrate_number_1;
    @Bind(R.id.tv_rataionrate_number_2)
    TextView tv_rataionrate_number_2;
    @Bind(R.id.tv_rataionrate_number_3)
    TextView tv_rataionrate_number_3;
    @Bind(R.id.tv_rataionrate_number_4)
    TextView tv_rataionrate_number_4;
    @Bind(R.id.iv_left_turn_light)
    ImageView iv_left_turn_light;
    @Bind(R.id.iv_right_turn_light)
    ImageView iv_right_turn_light;
    Runnable leftTurnLightOff = new Runnable() {
        @Override
        public void run() {
            iv_left_turn_light.setImageResource(R.mipmap.ico_left_turn_light_off);
        }
    };
    Runnable leftTurnLightOn = new Runnable() {
        @Override
        public void run() {
            iv_left_turn_light.setImageResource(R.mipmap.ico_left_turn_light_on);
            //900毫秒后恢复
            mHandler.postDelayed(leftTurnLightOff, 900);
        }
    };
    Runnable rightTurnLightOff = new Runnable() {
        @Override
        public void run() {
            iv_right_turn_light.setImageResource(R.mipmap.ico_right_turn_light_off);
        }
    };
    Runnable rightTurnLightOn = new Runnable() {
        @Override
        public void run() {
            iv_right_turn_light.setImageResource(R.mipmap.ico_right_turn_light_on);
            XuLog.d("左转了");
            //900毫秒后恢复
            mHandler.postDelayed(rightTurnLightOff, 900);
        }
    };
    Runnable brakeOn = new Runnable() {
        @Override
        public void run() {
            iv_right_turn_light.setImageResource(R.mipmap.ico_brake_on);
            XuLog.d("左转了");
            //900毫秒后恢复
            mHandler.postDelayed(rightTurnLightOff, 900);
        }
    };
    Runnable brakeOff = new Runnable() {
        @Override
        public void run() {
            iv_right_turn_light.setImageResource(R.mipmap.ico_brake_off);
        }
    };
    Runnable wiperOn = new Runnable() {
        @Override
        public void run() {
            iv_right_turn_light.setImageResource(R.mipmap.ico_wiper_on);
            XuLog.d("左转了");
            //900毫秒后恢复
            mHandler.postDelayed(rightTurnLightOff, 900);
        }
    };
    Runnable wiperOff = new Runnable() {
        @Override
        public void run() {
            iv_right_turn_light.setImageResource(R.mipmap.ico_wiper_off);
        }
    };
    private String nowOBDValue;
    private TextView title;
    private TextView titleFinish;
    private String rotationRate = "0000";
    private boolean canGetRotationRate = false;
    Runnable setNumber = new Runnable() {
        @Override
        public void run() {
            if (canGetRotationRate && tv_rataionrate_number_1 != null && tv_rataionrate_number_2 != null && tv_rataionrate_number_3 != null && tv_rataionrate_number_4 != null) {
                tv_rataionrate_number_1.setText(rotationRate.substring(0, 1));
                tv_rataionrate_number_2.setText(rotationRate.substring(1, 2));
                tv_rataionrate_number_3.setText(rotationRate.substring(2, 3));
                tv_rataionrate_number_4.setText(rotationRate.substring(3, 4));
            }

        }
    };

    @Override
    public int getContentResource() {
        return R.layout.fragment_calibration4step7;
    }

    @Override
    protected void initView(View view) {

    }

    @OnClick(R.id.btn_show_AutoCrack)
    void showAutoCrack() {
        if (AppContext.getInstance().getNowBrand() != null && AppContext.getInstance().getNowModel() != null) {
            UIHelper.showAsk(getActivity(), STR(R.string.ask_save_OBD), true, new OnClickYesOrNoListener() {
                @Override
                public void isyes(boolean b, DialogInterface dialog) {
                    if (b) {
                        AppContext.getInstance().getCachedThreadPool().execute(new Runnable() {
                            @Override
                            public void run() {
                                AppApi.getobdvalue(AppContext.getInstance().getNowBrand(), AppContext.getInstance().getNowModel(), new ResultCallback<ResultData<SOBDCrackValue>>() {
                                    @Override
                                    public void onFailure(Request request, Exception e) {
                                        XuToast.show(getContext(), STR(R.string.cannot_getOBD));
                                    }

                                    @Override
                                    public void onResponse(ResultData<SOBDCrackValue> response) {
                                        nowOBDValue = response.getMsg().getValue();
                                        if (!XuString.isEmpty(nowOBDValue) && nowOBDValue.length() == 46) {
                                            AppContext.getInstance().getDeviceHelper().setOBDCrackData(nowOBDValue, new SetOBDCrackDataCallback() {
                                                @Override
                                                public void onSuccess() {
                                                    //设置成功
                                                    XuToast.show(getContext(), STR(R.string.save_OBD_success));
                                                }

                                                @Override
                                                public void onFail(int i) {
                                                    XuToast.show(getContext(), STR(R.string.cannot_getOBD));
                                                }
                                            });

                                        } else {
                                            //获取到的数据格式不对
                                            if (mHandler != null) {
                                                mHandler.post(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        XuToast.show(getContext(), STR(R.string.obd_value_not_format));
                                                    }
                                                });
                                            }

                                        }
                                    }
                                });


                            }
                        });
                        dialog.dismiss();
                        return;
                    }
                    dialog.dismiss();
                    showOBDCrack();
                }
            });
            return;
        }
        showOBDCrack();
    }

    public void showOBDCrack() {
        if (AppContext.getInstance().getDeviceHelper().isConnectedDevice()) {
            cantoget = false;
            AppContext.getInstance().getDeviceHelper().stopVerification();
            canGetRotationRate = false;
            mHandler.removeCallbacksAndMessages(null);
            UIHelper.startActivity(getActivity(),OBDAutoCrackActivity.class);
        } else {
            XuToast.show(getContext(), STR(R.string.calibration4_step7_vcontext6));
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        initConnet();
        getActivity().findViewById(R.id.btn_save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Message msg = new Message();
                msg.arg2 = 5;
                InstallActivity.transHandler.sendMessage(msg);
            }
        });
    }

    @Override
    public void onStop() {
        cantoget = false;
        canGetRotationRate = false;
        AppContext.getInstance().getDeviceHelper().stopVerification();
        mHandler.removeCallbacksAndMessages(null);
        super.onStop();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        cantoget = false;
        AppContext.getInstance().getDeviceHelper().stopVerification();
        canGetRotationRate = false;
        mHandler.removeCallbacksAndMessages(null);

        if (!hidden){
            getActivity().findViewById(R.id.btn_save).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Message msg = new Message();
                    msg.arg2 = 5;
                    InstallActivity.transHandler.sendMessage(msg);
                }
            });
        }
    }

    void initConnet() {
        if (AppContext.getInstance().getDeviceHelper().isConnectedDevice()) {

            AppContext.getInstance().getDeviceHelper().startVerification(1000, new OBDVerificationCallback() {
                @Override
                public void onHasOperation(String s) {
                    if (s.equals(Vispect_SDK_ARG.LIGHT_RIGHT)) {
                        //打了右转
                        mHandler.post(rightTurnLightOn);
                        return;
                    }
                    if (s.equals(Vispect_SDK_ARG.LIGHT_LEFT)) {
                        //打了左转
                        mHandler.post(leftTurnLightOn);
                        return;
                    }

                    if (s.equals(Vispect_SDK_ARG.WIPER)) {
                        //打了雨刮
                        mHandler.post(wiperOn);
                        return;
                    }
                    if (s.equals(Vispect_SDK_ARG.BRAKE)) {
                        //打了刹车
                        mHandler.post(brakeOn);
                        return;
                    }


                }
            });
        }

        if (canGetRotationRate) {
            return;
        }
        if (AppContext.getInstance().getDeviceHelper().isConnectedDevice()) {
            canGetRotationRate = true;
            AppContext.getInstance().getCachedThreadPool().execute(new Runnable() {
                @Override
                public void run() {
                    while (canGetRotationRate) {
                        AppContext.getInstance().getDeviceHelper().getCarInfoData(new GetCarInfoListener() {
                            @Override
                            public void onGetCarInfoData(Vispect_SDK_CarInfo vispect_sdk_carInfo) {
                                String value = vispect_sdk_carInfo.getEngineSpeed().replace("rpm", "");
                                formatRotationRate(value.trim());
                                mHandler.post(setNumber);
                            }

                            @Override
                            public void onErro(int i) {

                            }
                        });

                        try {
                            Thread.sleep(1000);
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
        super.onDestroy();
    }

    private void formatRotationRate(String value) {
        if (value.length() == 4) {
            rotationRate = value;
            return;
        }

        if (value.length() > 4) {
            rotationRate = value.substring(0, 4);
        } else {
            int count = 4 - value.length();
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < count; i++) {
                sb.append("0");
            }
            sb.append(value);
            rotationRate = sb.toString();
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        initConnet();
    }


    private void changeTextColor(TextView textView, int start, int end) {
        builder = new SpannableStringBuilder(textView.getText().toString());
        redSpan = new ForegroundColorSpan(getResources().getColor(R.color.color_title_bar));//ForegroundColorSpan 为文字前景色，BackgroundColorSpan为文字背景色
        builder.setSpan(redSpan, start, end, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        textView.setText(builder);
    }
}

