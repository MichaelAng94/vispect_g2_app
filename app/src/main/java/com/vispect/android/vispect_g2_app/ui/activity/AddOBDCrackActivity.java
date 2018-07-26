package com.vispect.android.vispect_g2_app.ui.activity;

import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;


import com.vispect.android.vispect_g2_app.R;
import com.vispect.android.vispect_g2_app.app.AppConfig;
import com.vispect.android.vispect_g2_app.app.AppContext;
import com.vispect.android.vispect_g2_app.bean.OBDCrackValue;
import com.vispect.android.vispect_g2_app.bean.ResultData;
import com.vispect.android.vispect_g2_app.controller.AppApi;
import com.vispect.android.vispect_g2_app.controller.UIHelper;
import com.vispect.android.vispect_g2_app.interf.ResultCallback;
import com.vispect.android.vispect_g2_app.utils.CodeUtil;
import com.vispect.android.vispect_g2_app.utils.XuLog;
import com.vispect.android.vispect_g2_app.utils.XuString;
import com.vispect.android.vispect_g2_app.utils.XuToast;
import com.vispect.android.vispect_g2_app.utils.XuView;

import butterknife.Bind;
import butterknife.OnClick;
import interf.SetOBDCrackDataCallback;
import okhttp3.Request;


/**
 * 添加OBD破解数据
 * <p>
 * Created by xu on 2015/12/26.
 */
public class AddOBDCrackActivity extends BaseActivity {
    private final static String TAG = "AddOBDCrackActivity";

    @Bind(R.id.iv_titlebar_title)
    TextView title;
    @Bind(R.id.edt_light_id)
    EditText edt_light_id;
    @Bind(R.id.edt_light_left)
    EditText edt_light_left;
    @Bind(R.id.edt_light_right)
    EditText edt_light_right;
    @Bind(R.id.edt_light_big)
    EditText edt_light_big;
    @Bind(R.id.edt_light_driving)
    EditText edt_light_driving;
    @Bind(R.id.edt_wipper_id)
    EditText edt_wipper_id;
    @Bind(R.id.edt_wipper)
    EditText edt_wipper;
    @Bind(R.id.edt_reverse_id)
    EditText edt_reverse_id;
    @Bind(R.id.edt_reverse)
    EditText edt_reverse;
    @Bind(R.id.edt_brake_id)
    EditText edt_brake_id;
    @Bind(R.id.edt_brake)
    EditText edt_brake;
    @Bind(R.id.edt_brand)
    EditText edt_brand;
    @Bind(R.id.edt_model)
    EditText edt_model;
//    @Bind(R.id.iv_titlebar_save)
//    TextView save;


    Handler mHandler = new Handler();

    @Override
    public int getContentResource() {
        // TODO Auto-generated method stub
        return R.layout.activity_add_obd_crack;
    }

    @Override
    protected void initView(View view) {
        // TODO Auto-generated method stub
        title.setText(STR(R.string.add_obd_crack_title));
        findViewById(R.id.tv_title_save).setVisibility(View.VISIBLE);
        findViewById(R.id.tv_title_save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UIHelper.startActivity(AddOBDCrackActivity.this, OBDDeBugerActivity.class);
            }
        });
    }


    private String checkoutValue() {
        String light_id = setFFForId(edt_light_id.getText().toString().trim());
        String light_left = setFF(edt_light_left.getText().toString().trim());
        String light_right = setFF(edt_light_right.getText().toString().trim());
        String light_big = setFF(edt_light_big.getText().toString().trim());
        String light_driving = setFF(edt_light_driving.getText().toString().trim());
        String wipper_id = setFFForId(edt_wipper_id.getText().toString().trim());
        String wipper = setFF(edt_wipper.getText().toString().trim());
        String reverse_id = setFFForId(edt_reverse_id.getText().toString().trim());
        String reverse = setFF(edt_reverse.getText().toString().trim());
        String brake_id = setFFForId(edt_brake_id.getText().toString().trim());
        String brake = setFF(edt_brake.getText().toString().trim());


        if (!checkoutId(light_id) || !checkoutId(wipper_id) || !checkoutId(reverse_id) || !checkoutId(brake_id)) {
            return null;
        }


        if (checkoutIndex(light_left) && checkoutIndex(light_right) && checkoutIndex(light_big) && checkoutIndex(light_driving) && checkoutIndex(wipper) && checkoutIndex(reverse) && checkoutIndex(brake)) {
            OBDCrackValue crackValue = new OBDCrackValue();
            crackValue.setM_LightID(CodeUtil.stringToByte(light_id));
            crackValue.setM_LLight(str2byte(light_left));
            crackValue.setM_RLight(str2byte(light_right));
            crackValue.setM_BigLight(str2byte(light_big));
            crackValue.setM_Light(str2byte(light_driving));
            crackValue.setM_WipperID(CodeUtil.stringToByte(wipper_id));
            crackValue.setM_Wipper(str2byte(wipper));
            crackValue.setM_ReverseID(CodeUtil.stringToByte(reverse_id));
            crackValue.setM_Reverse(str2byte(reverse));
            crackValue.setM_BrakeID(CodeUtil.stringToByte(brake_id));
            crackValue.setM_Brake(str2byte(brake));
            return crackValue.toHexStr();
        } else {
            XuLog.e("填写的序号不合法");
            return null;
        }

    }

    private byte str2byte(String str) {
        if (str.equals("FF")) {
            return (byte) 0xFF;
        } else {
            return (byte) Integer.parseInt(str);
        }

    }

    private String setFF(String str) {
        if (str.equals("")) {
            return "FF";
        }
        return str;
    }

    private String setFFForId(String str) {
        if (str.equals("")) {
            return "FFFFFFFF";
        }
        return str;
    }

    private boolean checkoutId(String id) {

        if (id.length() != 8) {
            return false;
        }
        try {

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    private boolean checkoutIndex(String str_index) {
        try {
            if (str_index.equals("FF")) {
                return true;
            }
            int index = Integer.parseInt(str_index);
            return index >= 0 && index <= 63;
        } catch (Exception e) {
            e.printStackTrace();
            XuLog.e("检查OBD输入的值有误：" + e.getMessage());
            return false;
        }

    }


    @OnClick(R.id.btn_save)
    void saveDate(View v) {
        if (XuString.isEmpty(edt_brand.getText().toString().trim()) || XuString.isEmpty(edt_model.getText().toString().trim())) {
            XuToast.show(AddOBDCrackActivity.this, STR(R.string.add_obd_not_brand_model));
            return;
        }

        // showProgress();

        AppContext.getInstance().getCachedThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                final String value = checkoutValue();
                if (value != null) {
                    isresult = false;
                    if (!AppContext.getInstance().getDeviceHelper().isConnectedDevice()) {
                        return;
                    }
                    AppContext.getInstance().getCachedThreadPool().execute(new Runnable() {
                        @Override
                        public void run() {
                            Looper.prepare();
                            //发送到设备上
                            AppContext.getInstance().getDeviceHelper().setOBDCrackData(value, new SetOBDCrackDataCallback() {
                                @Override
                                public void onSuccess() {
                                    mHandler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            XuToast.show(AddOBDCrackActivity.this, STR(R.string.save_success));
                                            //    hideProgress();
                                        }
                                    });

                                }

                                @Override
                                public void onFail(final int i) {

                                    mHandler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            XuToast.show(AddOBDCrackActivity.this, STR(R.string.save_fail) + "    " + i);
                                            //  hideProgress();
                                        }
                                    });
                                }
                            });

                            Looper.loop();
                        }
                    });


                    AppContext.getInstance().getCachedThreadPool().execute(new Runnable() {
                        @Override
                        public void run() {
                            //保存到服务器
                            AppApi.saveOBDValues(Integer.parseInt(AppConfig.getInstance(AddOBDCrackActivity.this).getUserId()), 1, edt_brand.getText().toString().trim(), "", edt_model.getText().toString().trim(), value, new ResultCallback<ResultData<String>>() {
                                @Override
                                public void onFailure(Request request, Exception e) {
                                    XuLog.e("保存OBD破解数据失败：" + e.getMessage());
                                }

                                @Override
                                public void onResponse(ResultData<String> response) {
                                    if (response.getResult() == 0) {
                                        XuLog.e("保存OBD破解数据成功：" + response.toString());
                                    } else {
                                        XuLog.e("保存OBD破解数据失败：" + response.toString());
                                    }

                                }
                            });
                        }
                    });


                } else {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            XuToast.show(AddOBDCrackActivity.this, STR(R.string.edit_input_legal_value));
                            // hideProgress();
                        }
                    });
                }
            }
        });


    }

    @OnClick(R.id.iv_titlebar_back)
    void back() {
        finish();
    }

    boolean isresult = false;


    @Override
    protected void onStart() {
        super.onStart();


    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
