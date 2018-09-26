package com.vispect.android.vispect_g2_app.ui.fragment;

/**
 * Created by mo on 2018/3/27.
 */

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;


import com.vispect.android.vispect_g2_app.R;
import com.vispect.android.vispect_g2_app.app.AppContext;
import com.vispect.android.vispect_g2_app.bean.ARG;
import com.vispect.android.vispect_g2_app.controller.UIHelper;
import com.vispect.android.vispect_g2_app.interf.OnClickYesOrNoListener;
import com.vispect.android.vispect_g2_app.interf.XuTimeOutCallback;
import com.vispect.android.vispect_g2_app.ui.activity.InstallActivity;
import com.vispect.android.vispect_g2_app.utils.XuLog;
import com.vispect.android.vispect_g2_app.utils.XuString;
import com.vispect.android.vispect_g2_app.utils.XuTimeOutUtil;
import com.vispect.android.vispect_g2_app.utils.XuToast;

import butterknife.Bind;
import butterknife.ButterKnife;

import interf.OnGetDeviceCameraInfoListener;
import interf.OnGetDeviceCarInfoListener;
import interf.SetDeviceInfoCallback;


/**
 * 车辆信息
 */

public class EnterSizeFragment extends BaseFragment {

    @Bind(R.id.edit_width)
    EditText edit_width;
    @Bind(R.id.edit_deviation)
    EditText edit_deviation;
    @Bind(R.id.edit_height)
    EditText edit_height;
    @Bind(R.id.edit_depth)
    EditText edit_depth;


    private TextView title;
    private TextView titleFinish;

    private Handler mHandler = new Handler();

    private int lastdeviation = 0;
    private int lasdepth = 0;
    private int lastheight = 0;
    private int lastwidth = 0;
    private int lastlength = 0;
    private int lasthight = 0;

    private boolean canToSave = true;

    private Context mContext;
    private XuTimeOutUtil timeoutUtil = new XuTimeOutUtil(new XuTimeOutCallback() {
        @Override
        public void onTimeOut() {
            hideProgress();
        }
    });
    private Runnable successrunnable = new Runnable() {
        @Override
        public void run() {
            //发送完成
            XuToast.show(mContext, STR(R.string.save_success));
        }
    };
    private Runnable failrunnable = new Runnable() {
        @Override
        public void run() {
            //发送完成
            XuToast.show(getActivity(), STR(R.string.save_fail));
        }
    };
    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            //发送完成
            hideProgress();
            timeoutUtil.stopCheck();
            Message msg = new Message();
            msg.arg2 = 5;
            InstallActivity.transHandler.sendMessage(msg);
        }
    };

    @Override
    public int getContentResource() {
        return R.layout.fragment_calibration4step6;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden){
            onResume();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().findViewById(R.id.btn_save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UIHelper.showAsk(getActivity(), STR(R.string.ask_save_data), true, new OnClickYesOrNoListener() {
                    @Override
                    public void isyes(boolean b, DialogInterface dialog) {
                        if (b) {
                            save();
                        }
                        dialog.dismiss();
                    }
                });
            }
        });
    }

    @Override
    protected void initView(View view) {
        mContext = getActivity();
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
    public void onStart() {
        super.onStart();
        if (!AppContext.getInstance().getDeviceHelper().isConnectedDevice()) {
            return;
        }
        AppContext.getInstance().getCachedThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                timeoutUtil.startCheck(ARG.SET_VALUE_TIMEOUT);
                //获取车辆信息
                AppContext.getInstance().getDeviceHelper().getCarPara(new OnGetDeviceCarInfoListener() {
                    @Override
                    public void onSuccess(final int width, final int hight, final int length) {
                        timeoutUtil.stopCheck();
                        if (mHandler != null) {
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    if (edit_width == null) {
                                        return;
                                    }
                                    edit_width.setText(width + "");
                                    lastwidth = width;
                                    lastlength = length;
                                    lasthight = hight;
                                }
                            });
                        }


                    }

                    @Override
                    public void onFail(int i) {

                    }
                });
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //获取设备信息
                timeoutUtil.startCheck(ARG.SET_VALUE_TIMEOUT);
                AppContext.getInstance().getDeviceHelper().getCameraPara(new OnGetDeviceCameraInfoListener() {
                    @Override
                    public void onSuccess(final int center, final int hight, final int front) {
                        timeoutUtil.stopCheck();
                        if (mHandler != null) {
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    if (edit_height != null && edit_deviation != null && edit_depth != null) {
                                        edit_height.setText(hight + "");
                                        edit_deviation.setText(center + "");
                                        edit_depth.setText(front + "");
                                        lastdeviation = center;
                                        lasdepth = front;
                                        lastheight = hight;
                                    }
                                }
                            });
                        }
                    }
                    @Override
                    public void onFail(int i) {

                    }
                });


            }
        });
    }

    void save() {
        //TODO 保存数据
        if (!AppContext.getInstance().getDeviceHelper().isConnectedDevice()) {
            XuToast.show(AppContext.getInstance(), "not connect");
            return;
        }
        //保存车辆信息部分
        final String width = edit_width.getText().toString();
        if (XuString.isEmpty(width)) {
            XuToast.show(getActivity(), STR(R.string.value_not_null));
            return;
        }
        if (!XuString.checkNumberSection(width, 483, 4000)) {
            XuToast.show(getActivity(), STR(R.string.car_LWH_value_error));
            return;
        }
        //标定的部分
        final String center = edit_deviation.getText().toString();
        final String hights = edit_height.getText().toString();
        final String front = edit_depth.getText().toString();


        if (XuString.isEmpty(center) || XuString.isEmpty(hights) || XuString.isEmpty(front)) {
            XuToast.show(getActivity(), STR(R.string.value_not_null));
            return;
        }
        if (!checkStringLength(center) || !XuString.checkNumberSection(hights, 100, 3000) || !XuString.checkNumberSection(front, 100, 3000)) {
            XuToast.show(getActivity(), STR(R.string.camera_DHL_value_error));
            return;
        }
        if (canToSave) {
            canToSave = false;
            showProgress();
            AppContext.getInstance().getCachedThreadPool().execute(new Runnable() {
                @Override
                public void run() {
                    if (width.equals(lastwidth + "") && center.equals(lastdeviation + "") && hights.equals(lastheight + "") && front.equals(lasdepth + "")) {
                        XuLog.e("车辆信息数值 偏移数值 未发生改变 所以不保存");
                    } else {
                        setValueToBle_info(width, 100 + "", 100 + "");
                        try {
                            Thread.sleep(300);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        setValueToBle_adjust(center, hights, front);
                    }

                    if (mHandler != null) {
                        mHandler.post(mRunnable);
                        canToSave = true;
                    }
                }
            });

        }

    }

    public void setValueToBle_info(String width, String hight, String length) {
        //TODO 发送修改车辆信息的指令
        timeoutUtil.startCheck(ARG.SET_VALUE_TIMEOUT);
        AppContext.getInstance().getDeviceHelper().setCarPara(Integer.parseInt(width), Integer.parseInt(hight), Integer.parseInt(length), new SetDeviceInfoCallback() {
            @Override
            public void onSuccess() {
                timeoutUtil.stopCheck();
                canToSave = true;
            }

            @Override
            public void onFail(int i) {
                timeoutUtil.stopCheck();
                if (mHandler != null) {
                    mHandler.post(failrunnable);
                }
                canToSave = true;
            }
        });
    }

    public void setValueToBle_adjust(String center, String hight, String front) {
        //TODO 发送修改标定信息的指令
        AppContext.getInstance().getDeviceHelper().setCameraPara(Integer.parseInt(center), Integer.parseInt(hight), Integer.parseInt(front), new SetDeviceInfoCallback() {
            @Override
            public void onSuccess() {
                timeoutUtil.stopCheck();

                if (mHandler != null) {
                    mHandler.post(successrunnable);
                }
                canToSave = true;
            }

            @Override
            public void onFail(int i) {
                timeoutUtil.stopCheck();
                if (mHandler != null) {
                    mHandler.post(failrunnable);
                }
                canToSave = true;
            }
        });
    }

    private boolean checkStringLength(String str) {
        try {
            int num = Integer.parseInt(str);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return Integer.parseInt(str) >= -250 && Integer.parseInt(str) <= 250;
    }

}

