package com.vispect.android.vispect_g2_app.ui.fragment;

import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.TextView;

import com.vispect.android.vispect_g2_app.R;
import com.vispect.android.vispect_g2_app.app.AppConfig;
import com.vispect.android.vispect_g2_app.app.AppContext;
import com.vispect.android.vispect_g2_app.bean.ARG;
import com.vispect.android.vispect_g2_app.controller.UIHelper;
import com.vispect.android.vispect_g2_app.interf.OnClickYesOrNoListener;
import com.vispect.android.vispect_g2_app.interf.XuTimeOutCallback;
import com.vispect.android.vispect_g2_app.ui.activity.SettingsActivity;
import com.vispect.android.vispect_g2_app.ui.widget.PickerView;
import com.vispect.android.vispect_g2_app.utils.CodeUtil;
import com.vispect.android.vispect_g2_app.utils.XuLog;
import com.vispect.android.vispect_g2_app.utils.XuTimeOutUtil;
import com.vispect.android.vispect_g2_app.utils.XuToast;
import com.vispect.android.vispect_g2_app.utils.XuView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import interf.GetADASInfoCallback;
import interf.SetDeviceInfoCallback;
import interf.onGetHMWTimeCallback;

/**
 * 报警参数设置
 *
 * Created by xu on 2017/7/20.
 */
public class WarSetUpFragment extends BaseFragment {


    @Bind(R.id.edt_starspeed_ldw)
    TextView edt_starspeed_ldw;
    @Bind(R.id.edt_sensitivity_ldw)
    TextView edt_sensitivity_ldw;
    @Bind(R.id.edt_starspeed_fcw)
    TextView edt_starspeed_fcw;
    @Bind(R.id.edt_sensitivity_fcw)
    TextView edt_sensitivity_fcw;
    @Bind(R.id.edt_starspeed_pcw)
    TextView edt_starspeed_pcw;
    @Bind(R.id.edt_sensitivity_pcw)
    TextView edt_sensitivity_pcw;
    @Bind(R.id.edt_hmw_fcw)
    TextView edt_hmw_fcw;

    @Bind(R.id.ll_fcw_hmw)
    LinearLayout ll_fcw_hmw;
    @Bind(R.id.ll_fcw_sensivitivity_level)
    LinearLayout ll_fcw_sensivitivity_level;


    @Bind(R.id.ll_mask)
    LinearLayout ll_mask;
    Handler mHandler = new Handler();
    String edit_sensitivityOfSolidModes = "2";
    String edit_sensitivityOfDashModes = "2";
    String edit_warningStartSpeedOfSolidModes = "50";
    String edit_warningStartSpeedOfDashModes = "50";
    String edit_warningStartSpeedOfWanderingAcrosss = "50";
    String edit_wanderingAcrossLaneTimeThreshs = "60";
    String sensitivityOfTTCS = "2";
    String sensitivityOfHeadwayS = "2";
    String sensitivityOfVirtualBumperS = "2";
    String warningStartSpeedOfTTCS = "20";
    String warningStartSpeedOfHeadwayS = "20";
    String frontVehicleMovingDistanceThreshS = "100";
    int ss_ldw = 50;
    int ss_fcw = 30;
    int ss_pcw = 70;
    int sl_ldw = 3;
    int sl_fcw = 3;
    int sl_pcw = 3;
    String nowsectle = "0";
    TextView seekbar_level;
    boolean isallsuccess = true;
    Runnable successRunnable = new Runnable() {
        @Override
        public void run() {
            hideProgress();
            if (!isallsuccess) {
                isallsuccess = true;
            } else {
                XuToast.show(getActivity(), STR(R.string.save_success));
            }

        }
    };
    Runnable failRunnable = new Runnable() {
        @Override
        public void run() {
            hideProgress();
            XuToast.show(getActivity(), STR(R.string.warning_setting_title) + STR(R.string.save_fail));

        }
    };
    Runnable getValueRunnableSS = new Runnable() {
        @Override
        public void run() {
            if(edt_starspeed_ldw != null && edt_starspeed_fcw != null && edt_starspeed_pcw != null){
                edt_starspeed_ldw.setText(ss_ldw + "");
                edt_starspeed_fcw.setText(ss_fcw + "");
                edt_starspeed_pcw.setText(ss_pcw + "");
            }

        }
    };
    Runnable getValueRunnableSL = new Runnable() {
        @Override
        public void run() {
            if(edt_sensitivity_ldw != null && edt_sensitivity_fcw != null && edt_sensitivity_pcw != null){
                edt_sensitivity_ldw.setText(changdata(sl_ldw) + "");
                edt_sensitivity_fcw.setText(changdata(sl_fcw) + "");
                edt_sensitivity_pcw.setText(changdata(sl_pcw) + "");
            }

        }
    };
    private boolean canToSave = true;
    private float HMWTime = -1;
    Runnable setHMWTime = new Runnable() {
        @Override
        public void run() {
            if (edt_hmw_fcw != null) {
                edt_hmw_fcw.setText(HMWTime + "");
            }

        }
    };
    private AppConfig appconfig;
    private int screenWidth;
    private XuTimeOutUtil timeoutUtil = new XuTimeOutUtil(new XuTimeOutCallback() {
        @Override
        public void onTimeOut() {
            hideProgress();

        }
    });
    private boolean canShowHMW = false;
    Runnable showHMW = new Runnable() {
        @Override
        public void run() {
            XuView.setViewVisible(ll_fcw_hmw, canShowHMW);
            XuView.setViewVisible(ll_fcw_sensivitivity_level, !canShowHMW);
        }
    };

    @OnClick({R.id.edt_starspeed_ldw, R.id.edt_starspeed_fcw, R.id.edt_starspeed_pcw ,R.id.edt_hmw_fcw})
    void showPopupWindowbytimeSS(View v) {
        final int viewId = v.getId();

        View contentView = LayoutInflater.from(getActivity()).inflate(R.layout.popupwindow_startspeed, null);
        final PopupWindow popupWindow = new PopupWindow(contentView, ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, true);
        PickerView hh = (PickerView) contentView.findViewById(R.id.pv_h);
        List<String> ht = new ArrayList<String>();

        switch (viewId) {
            case R.id.edt_starspeed_ldw:
                for (int a = 3; a <= 6; a++) {
                    int value = (a * 10);
                    ht.add(value + "");
                }
                hh.setData(ht);

                break;
            case R.id.edt_starspeed_fcw:
                for (int a = 2; a <= 6; a++) {
                    int value = (a * 10);
                    ht.add(value + "");
                }
                hh.setData(ht);

                break;
            case R.id.edt_starspeed_pcw:
                for (int a = 5; a <= 7; a++) {
                    int value = (a * 10);
                    ht.add(value + "");
                }
                hh.setData(ht);

                break;
            case R.id.edt_hmw_fcw:
                ht.add("0");
                for (int a = 6; a <= 25; a++) {
                    ht.add((a/10f) + "");
                }
                hh.setData(ht);
                break;
        }


        switch (viewId) {
            case R.id.edt_starspeed_ldw:
                for (int i = 0; i < ht.size(); i++) {
                    if (Integer.parseInt(ht.get(i)) == ss_ldw) {
                        if (i == 0) {
                            hh.moveTailToHead();
                            hh.setSelected(i + 1);
                        } else {
                            if (i == ht.size() - 1) {
                                hh.moveHeadToTail();
                                hh.setSelected(i - 1);
                            }
                        }
                    }
                }
                break;
            case R.id.edt_starspeed_fcw:
                for (int i = 0; i < ht.size(); i++) {
                    if (Integer.parseInt(ht.get(i)) == ss_fcw) {
                        if (i == 0) {
                            hh.moveTailToHead();
                            hh.setSelected(i + 1);
                        } else {
                            if (i == ht.size() - 1) {
                                hh.moveHeadToTail();
                                hh.setSelected(i - 1);
                            }
                        }
                    }
                }
                break;

            case R.id.edt_hmw_fcw:
                for (int i = 0; i < ht.size(); i++) {
                    if (Float.parseFloat(ht.get(i)) == HMWTime) {
                        if (i == 0) {
                            hh.moveTailToHead();
                            hh.setSelected(i + 1);
                        } else {
                            if (i == ht.size() - 1) {
                                hh.moveHeadToTail();
                                hh.setSelected(i - 1);
                            }
                        }
                    }
                }
                break;

            case R.id.edt_starspeed_pcw:
                for (int i = 0; i < ht.size(); i++) {
                    if (Integer.parseInt(ht.get(i)) == ss_pcw) {
                        if (i == 0) {
                            hh.moveTailToHead();
                            hh.setSelected(i + 1);
                        } else {
                            if (i == ht.size() - 1) {
                                hh.moveHeadToTail();
                                hh.setSelected(i - 1);
                            }
                        }
                    }
                }
                break;
        }

        hh.setOnSelectListener(new PickerView.onSelectListener() {
            @Override
            public void onSelect(String text) {
                nowsectle = text;
            }
        });
        nowsectle = hh.getSeclected();
        popupWindow.setTouchable(true);
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                ll_mask.setVisibility(View.GONE);
            }
        });

        popupWindow.setTouchInterceptor(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;

            }
        });

        contentView.findViewById(R.id.ll_popuwindow_cancel).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                popupWindow.dismiss();
            }
        });

        contentView.findViewById(R.id.ll_popuwindow_savetophone).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                switch (viewId) {
                    case R.id.edt_starspeed_ldw:
                        edt_starspeed_ldw.setText(nowsectle);
                        ss_ldw = Integer.parseInt(nowsectle);
                        break;
                    case R.id.edt_starspeed_fcw:
                        edt_starspeed_fcw.setText(nowsectle);
                        ss_fcw = Integer.parseInt(nowsectle);
                        break;
                    case R.id.edt_starspeed_pcw:
                        edt_starspeed_pcw.setText(nowsectle);
                        ss_pcw = Integer.parseInt(nowsectle);
                        break;
                    case R.id.edt_hmw_fcw:
                        edt_hmw_fcw.setText(nowsectle);
                        HMWTime = Float.parseFloat(nowsectle);
                        break;

                }
                popupWindow.dismiss();
            }
        });

        ll_mask.setVisibility(View.VISIBLE);
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popupWindow.showAtLocation(ll_mask, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
    }

    @OnClick({R.id.edt_sensitivity_ldw, R.id.edt_sensitivity_fcw, R.id.edt_sensitivity_pcw})
    void showPopupWindowSL(View v) {
        final int viewId = v.getId();
        screenWidth = getActivity().getWindowManager().getDefaultDisplay().getWidth();
        // 一个自定义的布局，作为显示的内容
        View contentView = LayoutInflater.from(getActivity()).inflate(R.layout.popupwindow_sensitivity, null);
        PopupWindow popupWindow = new PopupWindow(contentView, ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, true);

        final SeekBar seekbar_sensitivity = (SeekBar) contentView.findViewById(R.id.seekbar_sensitivity);
        seekbar_level = (TextView) contentView.findViewById(R.id.seekbar_level);
        seekbar_sensitivity.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//                seekbar_level.layout( (progress * seekbar_level.getWidth()), 20, screenWidth, 80);

                if (seekbar_level != null) {
                    seekbar_level.setText(progress / 25 + "");
                    seekbar_level.setTranslationX(progress * ((getResources().getDimension(R.dimen.x70)) / 11));
                    XuLog.e("我改变了一次：" + progress + "     width:" + getResources().getDimension(R.dimen.x70));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        switch (viewId) {
            case R.id.edt_sensitivity_ldw:
                seekbar_sensitivity.setProgress(sl_ldw);
                break;

            case R.id.edt_sensitivity_fcw:
                seekbar_sensitivity.setProgress(sl_fcw);
                break;
            case R.id.edt_sensitivity_pcw:
                seekbar_sensitivity.setProgress(sl_pcw);
                break;
        }
//        if(seekbar_level != null){
//            seekbar_level.setText(seekbar_sensitivity.getProgress()/25+"");
//            seekbar_level.setTranslationX(seekbar_sensitivity.getProgress() * (seekbar_level.getWidth()/11));
//        }


        popupWindow.setTouchable(true);
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                ll_mask.setVisibility(View.GONE);
                switch (viewId) {
                    case R.id.edt_sensitivity_ldw:
                        sl_ldw = seekbar_sensitivity.getProgress();
                        edt_sensitivity_ldw.setText(changdata(sl_ldw) + "");
                        break;

                    case R.id.edt_sensitivity_fcw:
                        sl_fcw = seekbar_sensitivity.getProgress();
                        edt_sensitivity_fcw.setText(changdata(sl_fcw) + "");
                        break;
                    case R.id.edt_sensitivity_pcw:
                        sl_pcw = seekbar_sensitivity.getProgress();
                        edt_sensitivity_pcw.setText(changdata(sl_pcw) + "");
                        break;
                }
            }
        });


        popupWindow.setTouchInterceptor(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // 这里如果返回true的话，touch事件将被拦截
                // 拦截后 PopupWindow的onTouchEvent不被调用，这样点击外部区域无法dismiss
                return false;

            }
        });
        // 如果不设置PopupWindow的背景，无论是点击外部区域还是Back键都无法dismiss弹框
        // 我觉得这里是API的一个bug
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        ll_mask.setVisibility(View.VISIBLE);
        // 设置好参数之后再show
        popupWindow.showAtLocation(ll_mask, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
    }

    @Override
    public int getContentResource() {
        return R.layout.fragment_setup_war;
    }

    @Override
    protected void initView(View view) {
        if (AppContext.getInstance().getDeviceHelper().isConnectedDevice()) {
            canShowHMW = AppContext.getInstance().getDeviceHelper().getDeviceType() == 3;
            mHandler.post(showHMW);
            AppContext.getInstance().getCachedThreadPool().execute(new Runnable() {
                @Override
                public void run() {
                    getValue();
                }
            });
        }
        view.findViewById(R.id.btn_save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UIHelper.showAsk(getActivity(), STR(R.string.ask_save_data), true, new OnClickYesOrNoListener() {
                    @Override
                    public void isyes(boolean b, DialogInterface dialog) {
                        if (b) {
                            saveData();
                            finish();
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


        timeoutUtil.startCheck(ARG.OPEN_WIFI_TIMEOUT);
        //获取灵敏
        AppContext.getInstance().getDeviceHelper().getADASSensitivityLevel(new GetADASInfoCallback() {

            @Override
            public void OnSuccess(int ldw, int fcw, int pcw) {
                timeoutUtil.stopCheck();
                sl_ldw = ldw * 25;
                sl_fcw = fcw * 25;
                sl_pcw = pcw * 25;
                mHandler.post(getValueRunnableSL);
            }

            @Override
            public void onFail(int i) {
                timeoutUtil.stopCheck();
                XuLog.d("获取灵敏度失败：" + i);
            }
        });

        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        timeoutUtil.startCheck(ARG.OPEN_WIFI_TIMEOUT);
        //获取启动车速
        AppContext.getInstance().getDeviceHelper().GetADASSpeedThreshold(new GetADASInfoCallback() {
            @Override
            public void OnSuccess(int ldw, int fcw, int pcw) {
                timeoutUtil.stopCheck();
                ss_ldw = ldw;
                ss_fcw = fcw;
                ss_pcw = pcw;
                mHandler.post(getValueRunnableSS);
            }

            @Override
            public void onFail(int i) {
                timeoutUtil.stopCheck();
                XuLog.d("获取启动车速失败：" + i);

            }
        });

        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //获取HMW提示时间
        timeoutUtil.startCheck(ARG.OPEN_WIFI_TIMEOUT);
       AppContext.getInstance().getDeviceHelper().getHMWTime(new onGetHMWTimeCallback() {
           @Override
           public void onSuccess(int i) {
               timeoutUtil.stopCheck();
               HMWTime = i/10f;
               mHandler.post(setHMWTime);
               XuLog.e("获取HMW提示时间成功：" + i);
           }

           @Override
           public void onFail(int i) {
               timeoutUtil.stopCheck();
               XuLog.e("获取HMW提示时间失败：" + i);
           }
       });

    }
   public void saveData() {
        //保存设备信息
        if (AppContext.getInstance().getDeviceHelper().isConnectedDevice()) {

            if(!canToSave){
                XuLog.e("保存完一次了");
                return;
            }

            AppContext.getInstance().getCachedThreadPool().execute(new Runnable() {
                @Override
                public void run() {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            showProgress();
                        }
                    });
                    timeoutUtil.startCheck(ARG.OPEN_WIFI_TIMEOUT);
                    AppContext.getInstance().getCachedThreadPool().execute(new Runnable() {
                        @Override
                        public void run() {
                            byte ldw_temp = changdata(sl_ldw);
                            byte fcw_temp = changdata(sl_fcw);
                            byte pcw_temp = changdata(sl_pcw);
                            byte[] temp = new byte[]{0x01, ldw_temp, 0x03, fcw_temp, 0x05, pcw_temp};
                            StringBuffer sb = new StringBuffer();
                            for (byte i : temp) {
                                sb.append(String.format("%02x", i));
                            }
                            XuLog.d("SL发送出去的内容：" + sb.toString());
                            AppContext.getInstance().getDeviceHelper().setADASSensitivityLevel(ldw_temp, fcw_temp, pcw_temp, new SetDeviceInfoCallback() {
                                @Override
                                public void onSuccess() {
                                    timeoutUtil.stopCheck();
                                    XuLog.e("设置设备灵敏度等级成功！");

                                }

                                @Override
                                public void onFail(int i) {
                                    timeoutUtil.stopCheck();
                                    XuLog.e("设置设备灵敏度等级失败！" + "    " + i);
                                }
                            });

                        }
                    });
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    timeoutUtil.startCheck(ARG.SET_VALUE_TIMEOUT);
                    AppContext.getInstance().getCachedThreadPool().execute(new Runnable() {
                        @Override
                        public void run() {
                            byte ldw_temp = CodeUtil.intToOneByte(ss_ldw);
                            byte fcw_temp = CodeUtil.intToOneByte(ss_fcw);
                            byte pcw_temp = CodeUtil.intToOneByte(ss_pcw);

                            byte[] temp = new byte[]{0x01, ldw_temp, 0x03, fcw_temp, 0x05, pcw_temp};
                            StringBuffer sb = new StringBuffer();
                            for (byte i : temp) {
                                sb.append(String.format("%02x", i));
                            }
                            XuLog.d("SS发送出去的内容：" + sb.toString());
                            AppContext.getInstance().getDeviceHelper().setADASSpeedThreshold(ldw_temp, fcw_temp, pcw_temp, new SetDeviceInfoCallback() {
                                @Override
                                public void onSuccess() {
                                    timeoutUtil.stopCheck();
                                    XuLog.e("设置设备启动车速成功！");
                                    canToSave = true;
                                    mHandler.postDelayed(successRunnable, 1000);
                                }

                                @Override
                                public void onFail(int i) {
                                    timeoutUtil.stopCheck();
                                    XuLog.e("设置设备启动车速失败！" + "    " + i);
                                    canToSave = true;
                                    mHandler.postDelayed(failRunnable, 1000);
                                }
                            });


                        }
                    });

                    if(canShowHMW){
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        AppContext.getInstance().getCachedThreadPool().execute(new Runnable() {
                            @Override
                            public void run() {
                                AppContext.getInstance().getDeviceHelper().setHMWTime((int)(Float.parseFloat(edt_hmw_fcw.getText().toString())*10f), new SetDeviceInfoCallback() {
                                    @Override
                                    public void onSuccess() {
                                        XuLog.e("设置HMW提示时间成功！");
                                    }

                                    @Override
                                    public void onFail(int i) {
                                        XuLog.e("设置HMW提示时间失败！" + "    " + i);
                                    }
                                });
                            }
                        });

                    }


                    canToSave = false;



                }
            });


        }


    }
    byte changdata(int data) {

        byte temp = 0x01;
        switch (data / 25) {
            case 0:
                temp = 0x00;
                break;
            case 1:
                temp = 0x01;
                break;
            case 2:
                temp = 0x02;
                break;
            case 3:
                temp = 0x03;
                break;

        }

        XuLog.d("sensitivity data:" + data + "       sensitivity leve:" + (int) temp);
        return temp;
    }
}
