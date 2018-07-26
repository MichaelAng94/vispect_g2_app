package com.vispect.android.vispect_g2_app.ui.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.vispect.android.vispect_g2_app.R;
import com.vispect.android.vispect_g2_app.app.AppConfig;
import com.vispect.android.vispect_g2_app.app.AppContext;
import com.vispect.android.vispect_g2_app.bean.OBDAutoCrackElement;
import com.vispect.android.vispect_g2_app.bean.OBDCrackValue;
import com.vispect.android.vispect_g2_app.bean.ResultData;
import com.vispect.android.vispect_g2_app.controller.AppApi;
import com.vispect.android.vispect_g2_app.controller.UIHelper;
import com.vispect.android.vispect_g2_app.interf.OnClickYesOrNoListener;
import com.vispect.android.vispect_g2_app.interf.OnProgressCallback;
import com.vispect.android.vispect_g2_app.interf.ResultCallback;
import com.vispect.android.vispect_g2_app.ui.widget.InputCarModeDialog;
import com.vispect.android.vispect_g2_app.ui.widget.XuImageBtn;
import com.vispect.android.vispect_g2_app.utils.CodeUtil;
import com.vispect.android.vispect_g2_app.utils.XuFileUtils;
import com.vispect.android.vispect_g2_app.utils.XuLog;
import com.vispect.android.vispect_g2_app.utils.XuString;
import com.vispect.android.vispect_g2_app.utils.XuToast;
import com.vispect.android.vispect_g2_app.utils.XuView;

import java.io.IOException;
import java.util.ArrayList;

import bean.Vispect_SDK_ARG;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import interf.OBDDebugCallback;
import interf.OBDVerificationCallback;
import interf.SetOBDCrackDataCallback;
import okhttp3.Request;

/**
 * OBD自动破解
 * <p>
 * Created by xu on 2017/10/30.
 */
public class OBDAutoCrackActivity extends BaseActivity {
    private static final String TAG = "OBDAutoCrackActivity";


    @Bind(R.id.iv_titlebar_title)
    TextView title;

    @Bind(R.id.iv_center_btn)
    XuImageBtn iv_center_btn;
    @Bind(R.id.tv_obd_auto_crack_tips)
    TextView tv_obd_auto_crack_tips;
    @Bind(R.id.tv_btn_center)
    TextView tv_btn_center;
    @Bind(R.id.tv_on)
    TextView tv_on;
    @Bind(R.id.tv_off)
    TextView tv_off;
    @Bind(R.id.ll_crack)
    LinearLayout ll_crack;


    //OBD验证部分
    @Bind(R.id.iv_left_turn_light)
    ImageView iv__left_turn_light;
    @Bind(R.id.iv_right_turn_light)
    ImageView iv__right_turn_light;
    @Bind(R.id.ll_obd_verification)
    LinearLayout ll_obd_verification;
    @Bind(R.id.img_center_left)
    ImageView imgCenterLeft;
    @Bind(R.id.img_center_right)
    ImageView imgCenterRight;
    @Bind(R.id.iv_titlebar_back)
    ImageView ivTitlebarBack;
    TextView ivTitlebarFinish;
    //    @Bind(R.id.iv_left_turn_light)
//    ImageView ivLeftTurnLight;
    @Bind(R.id.btn_sumit)
    Button btnSumit;

    private boolean isFristReuslt = true;
    private volatile boolean isFrist = true;
    private boolean canUpdate = false;
    private Handler mHandler = new Handler();
    private int option = 0;
    private int crackCount = 0;
    private DialogInterface dialog;
    private Boolean closeCan;

    ArrayList<OBDAutoCrackElement> fristList = new ArrayList<OBDAutoCrackElement>();     //总表
    ArrayList<OBDAutoCrackElement> beFilteredList = new ArrayList<OBDAutoCrackElement>();//进行过滤的表
    //    ArrayList<OBDAutoCrackElement> temp_left_list; //过滤剩下的左转向灯的疑似目标
//    ArrayList<OBDAutoCrackElement> temp_right_list; //过滤剩下的右转向的疑似目标
    private OBDAutoCrackElement review_left;
    private OBDAutoCrackElement review_right;
    private boolean isReviewing = false;
    private String mofileName = "";
    private Boolean isDebug = false;

    private OBDDebugCallback obddebugCallback = new OBDDebugCallback() {
        @Override
        public void onGetCANData(final byte[] id, final byte[] value) {
            AppContext.getInstance().getFixedThreadPool().execute(new Runnable() {
                @Override
                public void run() {
                    String str_id = CodeUtil.bytesToString(id);
                    XuLog.e(value.toString()+"what?");
                    boolean isMissing = true;
                    //判断是不是第一次获取列表
                    if (isFrist) {
                        for (OBDAutoCrackElement temp : fristList) {
                            if (temp.getId().equals(str_id)) {
                                isFrist = false;
                                //开始出现重复的了 把总表复制到筛选表
                                beFilteredList = (ArrayList<OBDAutoCrackElement>) fristList.clone();
                                //BUG in this
                                isMissing = false;
                                break;
                            }
                        }
                    } else {
                        //判断是否已存在总表里
                        for (OBDAutoCrackElement element : fristList) {
                            if (element.getId().equals(str_id)) {
                                isMissing = false;
                                break;
                            }
                        }
                    }

                    for (int i = 0; i < value.length; i++) {
                        String str_bit = CodeUtil.toBitStr(value[i]);


                        for (int j = 0; j < str_bit.length(); j++) {
                            int temp_bit = Integer.parseInt(str_bit.substring(j, j + 1));
                            OBDAutoCrackElement element = new OBDAutoCrackElement();
                            element.setId(str_id);
                            element.setValue(temp_bit);
                            element.setIndex(j + (i * 8));
                            if (isReviewing) {
                                if (review_left == null || review_right == null) {
//                                    XuToast.show(AppContext.getInstance(), "验证对象为空");
                                }
                                //到了校验阶段则不更新数据
                                if (element.getId().equals(review_left.getId()) && element.getIndex() == review_left.getIndex() && element.getValue() == 1) {
                                    //打了左转
                                    mHandler.post(leftTurnLightOn);
                                }
                                if (element.getId().equals(review_right.getId()) && element.getIndex() == review_right.getIndex() && element.getValue() == 1) {
                                    //打了右转
                                    mHandler.post(rightTurnLightOn);
                                }
                            } else {
                                if (isFrist) {
                                    fristList.add(element);
                                } else {
                                    //如果是遗漏的数据  则加到总表和过滤表里
                                    if (isMissing) {
                                        fristList.add(element);
                                        beFilteredList.add(element);
                                    } else {
                                        //更新数据
                                        if (canUpdate) {
                                            screen(0, element);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            });
        }

        @Override
        public void onGetResult(final boolean b) {
            mHandler.post(new Runnable() {

                @Override
                public void run() {
                    // TODO Auto-generated method stub
//                    ToastUtil.show(AppContext.getInstance(), System.currentTimeMillis() + "  收到OBD操作的结果" + b);
                    if (isFristReuslt) {
                        isFristReuslt = false;
                        if (b) {
                            tv_obd_auto_crack_tips.setText(STR(R.string.obd_auto_crack_tips_get_all_data));
                            tv_btn_center.setText(STR(R.string.wait));
                            iv_center_btn.setAngle(0, 361, 35, progressCallback);
                        }
                    }
                }
            });
        }
    };

    Runnable leftTurnLightOn = new Runnable() {
        @Override
        public void run() {
//            XuToast.show(AppContext.getInstance(),"左转灯发生变化");
            iv__left_turn_light.setImageResource(R.mipmap.ico_left_turn_light_on);
            //900毫秒后恢复
            mHandler.postDelayed(leftTurnLightOff, 900);
        }
    };
    Runnable leftTurnLightOff = new Runnable() {
        @Override
        public void run() {
            iv__left_turn_light.setImageResource(R.mipmap.ico_left_turn_light_off);
        }
    };

    Runnable rightTurnLightOn = new Runnable() {
        @Override
        public void run() {
//            XuToast.show(AppContext.getInstance(),"右转灯发生变化");
            iv__right_turn_light.setImageResource(R.mipmap.ico_right_turn_light_on);
            XuLog.d("左转了");
            //900毫秒后恢复
            mHandler.postDelayed(rightTurnLightOff, 900);
        }
    };
    Runnable rightTurnLightOff = new Runnable() {
        @Override
        public void run() {
            iv__right_turn_light.setImageResource(R.mipmap.ico_right_turn_light_off);
        }
    };

    Runnable del_fixed = new Runnable() {
        @Override
        public void run() {
            screen(2, null);

        }
    };

    Runnable del_changed = new Runnable() {
        @Override
        public void run() {
            screen(3, null);
            tv_btn_center.setText("正在过滤，此时请不要做任何操作，包括车上开关转向灯");
        }
    };

    Runnable crackFail = new Runnable() {
        @Override
        public void run() {
            if (isDebug) {
                XuToast.show(OBDAutoCrackActivity.this, "破解失败！");
                try {
                    XuFileUtils.writeFileSdcardFile(mofileName, "option " + option + " " + " Carckcount " + crackCount + "  fitercount " + beFilteredList.size() + " fail " + (review_left == null));
                    if (beFilteredList.size() > 0) {
                        for (int i = 0; i < beFilteredList.size(); i++) {
                            XuFileUtils.writeFileSdcardFile(mofileName, "  id: " + beFilteredList.get(i).getId() + "  value: " + beFilteredList.get(i).getValue() + "  index: " + beFilteredList.get(i).getIndex() + "\n");
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            UIHelper.showAsk(OBDAutoCrackActivity.this, STR(R.string.obd_auto_crack_tips_auto_crack_fail), false, new OnClickYesOrNoListener() {
                @Override
                public void isyes(boolean var1, DialogInterface dialog) {
                    if (var1) {
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                screen(8, null);
                                canUpdate = true;
                                //重走进度条
                                boolean b = iv_center_btn.setAngle(0, 361, 35, progressCallback);
                                //修改提示
                                tv_obd_auto_crack_tips.setText(STR(R.string.obd_auto_crack_tips_get_all_data));
                                tv_on.setVisibility(View.GONE);
                                tv_off.setVisibility(View.GONE);
                                //更新标识
                                option = 0;
                            }
                        }, 100);
                        crackCount = 0;
                        dialog.dismiss();
                    } else {
                        dialog.dismiss();
                        finish();
                    }
                }
            });

        }
    };

    Runnable craackSuccess = new Runnable() {
        @Override
        public void run() {

            if (review_left == null) {
                review_left = beFilteredList.get(0);
                XuLog.e(TAG, "左转破解成功：" + review_left.getId() + "    " + review_left.getIndex());
                UIHelper.showAsk(OBDAutoCrackActivity.this, STR(R.string.to_crack_right_tips), false, new OnClickYesOrNoListener() {
                    @Override
                    public void isyes(boolean var1, DialogInterface dialog) {
                        if (var1) {
                            mHandler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    screen(8, null);
                                    canUpdate = true;
                                    //重走进度条
                                    boolean b = iv_center_btn.setAngle(0, 361, 35, progressCallback);
                                    //修改提示
                                    tv_obd_auto_crack_tips.setText(STR(R.string.obd_auto_crack_tips_get_all_data));
                                    tv_on.setVisibility(View.GONE);
                                    tv_off.setVisibility(View.GONE);
                                    //更新标识
                                    option = 0;
                                    crackCount = 0;
                                }
                            }, 100);
                            dialog.dismiss();
                        } else {
                            dialog.dismiss();
                            finish();
                        }
                    }
                });
            } else {
                if (review_right == null) {
                    review_right = beFilteredList.get(0);
                }
                XuLog.e(TAG, "右转破解成功：" + review_right.getId() + "    " + review_right.getIndex());
                UIHelper.showAsk(OBDAutoCrackActivity.this, STR(R.string.to_verification_tips), false, new OnClickYesOrNoListener() {
                    @Override
                    public void isyes(boolean var1, DialogInterface dialog) {
                        if (var1) {
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    screen(8, null);
                                    XuView.setViewVisible(ll_obd_verification, true);
                                    isReviewing = true;
                                    tv_on.setVisibility(View.GONE);
                                    tv_off.setVisibility(View.GONE);
                                    ll_crack.setVisibility(View.GONE);

                                    option = 0;
                                    crackCount = 0;
                                }
                            });

                        }
                        dialog.dismiss();
                    }
                });
            }
            //破解成功就删除记录的文件
        }
    };


    @Override
    public int getContentResource() {
        return R.layout.activity_obd_auto_crack;
    }

    private OnProgressCallback progressCallback = new OnProgressCallback() {
        @Override
        public void onProgress(float current, float max) {

            if (current < max) {
                return;
            }
            if (!started) {
                return;
            }
            switch (option) {
                case 0:
                    //完成了总表的收集 下一步开始打灯
                    canUpdate = false; //先把更新给停了
                    if (isDebug) {
                        if (!mofileName.isEmpty()) {
                            try {
                                XuFileUtils.writeFileSdcardFile(mofileName, "总表收集  case0 " + "\n");
                                if (beFilteredList.size() > 0) {
                                    for (int i = 0; i < beFilteredList.size(); i++) {
                                        XuFileUtils.writeFileSdcardFile(mofileName, "  id: " + beFilteredList.get(i).getId() + "  value: " + beFilteredList.get(i).getValue() + "  index: " + beFilteredList.get(i).getIndex() + "    count:" + beFilteredList.get(i).getCount() + "\n");
                                    }
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                                XuLog.e(TAG, "保存抛了异常：" + e.getMessage());
                            }
                        }
                    }

                    screen(3, null);    //再清楚掉发生了变化的数据；
                    screen(1, null);    //重置变化次数


                    canUpdate = true; //再次把更新开起来
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            tv_btn_center.setVisibility(View.GONE);
                            imgCenterLeft.setVisibility(View.VISIBLE);
                            if (review_left == null) {
                                for (int i = 0; i < 6; i++) {
                                    mHandler.postDelayed(img_leftTurnLightOn, i * 1000);
                                }
                            } else {
                                for (int i = 0; i < 6; i++) {
                                    mHandler.postDelayed(img_rightTurnLightOn, i * 1000);
                                }
                            }

                            //重走进度条
                            boolean b = iv_center_btn.setAngle(0, 360, 32, progressCallback);
                            //修改提示
                            tv_obd_auto_crack_tips.setText(STR(R.string.obd_auto_crack_tips_off_or_on_light) + "\n" + (review_left != null ? STR(R.string.right_turn_signal) : STR(R.string.left_turn_signal)));
                            tv_on.setVisibility(View.VISIBLE);
                            tv_off.setVisibility(View.VISIBLE);
                            //更新标识
                            option++;
                        }
                    }, 100);
                    break;
                case 1:
                    //打完灯了，准备过滤没有发生变化的
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            imgCenterLeft.setVisibility(View.GONE);
                            tv_btn_center.setVisibility(View.VISIBLE);
                            tv_btn_center.setText(STR(R.string.OBDAutoCrack_context7));
                        }
                    });
                    //先看看是第几次 是不是要显示失败
                    if (crackCount > 10) {
                        mHandler.post(crackFail);
                        return;
                    }
                    crackCount++;
                    canUpdate = false; //先把更新给停了

                    if (isDebug) {
                        if (!mofileName.isEmpty()) {
                            try {
                                XuFileUtils.writeFileSdcardFile(mofileName, "清除变化的 case 1 " + "\n");
                                if (beFilteredList.size() > 0) {
                                    for (int i = 0; i < beFilteredList.size(); i++) {
                                        XuFileUtils.writeFileSdcardFile(mofileName, "  id: " + beFilteredList.get(i).getId() + "  value: " + beFilteredList.get(i).getValue() + "  index: " + beFilteredList.get(i).getIndex() + "    count:" + beFilteredList.get(i).getCount() + "\n");
                                    }
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                                XuLog.e(TAG, "保存抛了异常：" + e.getMessage());
                            }
                        }
                    }

                    screen(2, null);    //再清除掉发生了没有变化的数据；


                    if (review_left != null && crackCount == 10) {
                        for (OBDAutoCrackElement temp : beFilteredList) {
                            if (temp.getId().equals(review_left.getId())) {
                                XuLog.e(TAG, "智能过滤出来的目标：  id:" + temp.getId() + "    index:" + temp.getIndex() + "    count:" + temp.getCount());
                            }
                        }
                    }


                    screen(1, null);    //重置变化次数

                    if (beFilteredList.size() < 1) {
                        mHandler.post(crackFail);
                        return;
                    }
                    if (beFilteredList.size() == 1) {
                        mHandler.post(craackSuccess);
                        return;
                    } else {
                        if (review_left != null && beFilteredList.size() <= 6) {
                            int num = 0;
                            int index = 0;
                            for (int i = 0; i < beFilteredList.size(); i++) {
                                if (review_left.getId().equals(beFilteredList.get(i).getId())) {
                                    num++;
                                    index = i;
                                }
                            }
                            if (num == 1) {
                                review_right = beFilteredList.get(index);
                            }
                            mHandler.post(craackSuccess);
                            return;
                        }

                        canUpdate = true; //再次把更新开起来
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                //重走进度条
                                boolean b = iv_center_btn.setAngle(0, 361, 20, progressCallback);
                                //修改提示
                                tv_obd_auto_crack_tips.setText(STR(R.string.obd_auto_crack_tips_get_all_data));
                                tv_on.setVisibility(View.GONE);
                                tv_off.setVisibility(View.GONE);
                                //更新标识
                                option++;
                            }
                        }, 100);

                    }

                    break;

                case 2:
                    //现在准备过滤掉发生过变化的
                    canUpdate = false; //先把更新给停了

                    if (isDebug) {
                        if (!mofileName.isEmpty()) {
                            try {
                                XuFileUtils.writeFileSdcardFile(mofileName, "清除掉没变化的 case 2 " + "\n");
                                if (beFilteredList.size() > 0) {
                                    for (int i = 0; i < beFilteredList.size(); i++) {
                                        XuFileUtils.writeFileSdcardFile(mofileName, "  id: " + beFilteredList.get(i).getId() + "  value: " + beFilteredList.get(i).getValue() + "  index: " + beFilteredList.get(i).getIndex() + "    count:" + beFilteredList.get(i).getCount() + "\n");
                                    }
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                                XuLog.e(TAG, "保存抛了异常：" + e.getMessage());
                            }
                        }
                    }

                    screen(3, null);    //再清楚掉发生了变化的数据；

                    screen(1, null);    //重置变化次数
                    if (beFilteredList.size() < 1) {
                        mHandler.post(crackFail);
                        return;

                    }
                    if (beFilteredList.size() == 1) {
                        mHandler.post(craackSuccess);

                        return;
                    } else {
                        if (review_left != null && beFilteredList.size() <= 6) {
                            int num = 0;
                            int index = 0;
                            for (int i = 0; i < beFilteredList.size(); i++) {
                                if (review_left.getId().equals(beFilteredList.get(i).getId())) {
                                    num++;
                                    index = i;
                                }
                            }
                            if (num == 1) {
                                review_right = beFilteredList.get(index);
                            }
                            mHandler.post(craackSuccess);
                            return;
                        }
                        canUpdate = true; //再次把更新开起来
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                //重走进度条

                                tv_btn_center.setVisibility(View.GONE);
                                imgCenterLeft.setVisibility(View.VISIBLE);
                                if (review_left == null) {
                                    for (int i = 0; i < 4; i++) {
                                        mHandler.postDelayed(img_leftTurnLightOn, i * 1000);
                                    }
                                } else {
                                    for (int i = 0; i < 4; i++) {
                                        mHandler.postDelayed(img_rightTurnLightOn, i * 1000);
                                    }
                                }


                                boolean b = iv_center_btn.setAngle(0, 360, 20, progressCallback);
                                //修改提示
                                tv_obd_auto_crack_tips.setText(STR(R.string.obd_auto_crack_tips_off_or_on_light) + "\n" + (review_left != null ? STR(R.string.right_turn_signal) : STR(R.string.left_turn_signal)));
                                //更新标识
                                option = 1;
                                tv_on.setVisibility(View.VISIBLE);
                                tv_off.setVisibility(View.VISIBLE);

                            }
                        }, 100);
                    }
                    break;
            }
        }
    };


    @Override
    protected void initView(View view) {
        title.setText("OBD Debug");
        title.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                UIHelper.startActivity(OBDAutoCrackActivity.this,AddOBDCrackActivity.class);
                return false;
            }
        });

    }


    @OnClick(R.id.btn_sumit)
    void save(View v) {
        showDialogToInputBrand(OBDAutoCrackActivity.this, STR(R.string.obd_auto_crack_tips_save), new OnClickYesOrNoListener() {
            @Override
            public void isyes(boolean var1, DialogInterface dialog) {
                if (var1) {
                    savteData(builder.getBrand(), builder.getMode());
                }
                dialog.dismiss();
            }
        });

    }

    private void savteData(final String brand, final String model) {
       // showProgress();
        OBDCrackValue crackValue = new OBDCrackValue();
        crackValue.setM_LightID(CodeUtil.stringToByte(review_left.getId()));
        crackValue.setM_LLight((byte) review_left.getIndex());
        crackValue.setM_RLight((byte) review_right.getIndex());
        crackValue.setM_BigLight((byte) 0xff);
        crackValue.setM_Light((byte) 0xff);
        crackValue.setM_WipperID(CodeUtil.stringToByte("FFFFFFFF"));
        crackValue.setM_Wipper((byte) 0xff);
        crackValue.setM_ReverseID(CodeUtil.stringToByte("FFFFFFFF"));
        crackValue.setM_Reverse((byte) 0xff);
        crackValue.setM_BrakeID(CodeUtil.stringToByte("FFFFFFFF"));
        crackValue.setM_Brake((byte) 0xff);
        final String value = crackValue.toHexStr();
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
                                XuToast.show(OBDAutoCrackActivity.this, STR(R.string.save_success));
                                AppContext.getInstance().setNowBrand(brand);
                                AppContext.getInstance().setNowModel(model);
                               // hideProgress();
                                finish();
                            }
                        });

                    }

                    @Override
                    public void onFail(final int i) {

                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                XuToast.show(OBDAutoCrackActivity.this, STR(R.string.save_fail) + "    " + i);
                            //    hideProgress();
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
                AppApi.saveOBDValues(Integer.parseInt(AppConfig.getInstance(OBDAutoCrackActivity.this).getUserId()), 1, brand.trim(), "", model.trim(), value, new ResultCallback<ResultData<String>>() {
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

    }

    private void startVerification() {
        if (!AppContext.getInstance().getDeviceHelper().isConnectedDevice()) {
            return;
        }
        //开启OBD验证
        AppContext.getInstance().getCachedThreadPool().execute(new Runnable() {
            @Override
            public void run() {
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
                    }
                });
            }
        });
    }

    private boolean started = false;

    @OnClick(R.id.iv_center_btn)
    void click(View v) {
        if (!started) {
            started = true;
            if (AppContext.getInstance().getDeviceHelper().isConnectedDevice() && !iv_center_btn.isAlive()) {
                iv_center_btn.setEnabled(false);
                if (isDebug) {
                    resetFileName();
                }
                tv_btn_center.setText(STR(R.string.OBDAutoCrack_context7));
                AppContext.getInstance().getDeviceHelper().openCANListener(obddebugCallback);
            } else {
                started = false;
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        closeCan = false;
    }

    @Override
    protected void onStop() {
        super.onStop();

        closeCan = true;

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (closeCan) {
                    AppContext.getInstance().getDeviceHelper().closeCANListener(null);
                }
            }
        }, 5000);
    }


    private void resetFileName() {
        mofileName = AppConfig.getCarcorderDir() + "moautoCarack" + XuString.getNowTime("yyyy-MM-dd-HH-mm-ss") + ".txt";
    }

    private synchronized void screen(int type, OBDAutoCrackElement temp) {
        switch (type) {
            case 0:  //更新数据
                for (int i = 0; i < beFilteredList.size(); i++) {
                    if (beFilteredList.get(i).getId().equals(temp.getId()) && beFilteredList.get(i).getIndex() == temp.getIndex()) {
                        if (beFilteredList.get(i).getValue() != temp.getValue()) {
                            beFilteredList.get(i).setValue(temp.getValue());
                            beFilteredList.get(i).setCount(beFilteredList.get(i).getCount() + 1);
                        }
                    }
                }
                break;

            case 1:  //重置变化次数；
                for (int i = 0; i < beFilteredList.size(); i++) {
                    beFilteredList.get(i).setCount(0);
                }


                break;

            case 2:  //去除掉没有发生过变化的
                ArrayList<OBDAutoCrackElement> temp_list_fixed = (ArrayList<OBDAutoCrackElement>) beFilteredList.clone();
                for (int i = 0; i < temp_list_fixed.size(); i++) {
                    OBDAutoCrackElement temp_element = temp_list_fixed.get(i);

                    if (temp_element.getCount() < 2 || temp_element.getCount() > 3) {
                        beFilteredList.remove(temp_element);
                    }
                }
                temp_list_fixed.clear();
                break;

            case 3:  //去除掉发生过变化的
                ArrayList<OBDAutoCrackElement> temp_list_changed = (ArrayList<OBDAutoCrackElement>) beFilteredList.clone();
                for (int i = 0; i < temp_list_changed.size(); i++) {
                    OBDAutoCrackElement temp_element = temp_list_changed.get(i);
                    if (temp_element.getCount() != 0) {
                        beFilteredList.remove(temp_element);
                    }
                }
                temp_list_changed.clear();
                break;

            case 4:  //打印出当前剩下的内容
                if (beFilteredList.isEmpty()) {
                    XuLog.e(TAG, "列表为空");
                }
                for (OBDAutoCrackElement temp_element : beFilteredList) {
                    XuLog.e(TAG, temp_element.toString());
                }
                break;

//            case 5:  //把过滤剩下的左转向灯存起来
//                if(beFilteredList.isEmpty()){
//                    XuLog.e(TAG,"列表为空");
//                    return;
//                }
//
//                temp_left_list = (ArrayList<OBDAutoCrackElement>)beFilteredList.clone();
//                break;
//            case 6:  //把过滤剩下的右转向灯存起来
//                if(beFilteredList.isEmpty()){
//                    XuLog.e(TAG,"列表为空");
//                    return;
//                }
//                temp_right_list = (ArrayList<OBDAutoCrackElement>)beFilteredList.clone();
//                break;
//            case 7:  //开始尝试识别出目标
//                if(temp_right_list.isEmpty() || temp_left_list.isEmpty()){
//                    XuLog.e(TAG,"列表为空,破解失败");
//                    return;
//                }
//                if(temp_left_list.size() == 1 && temp_right_list.size() == 1){
//                    XuLog.e(TAG,"两次都是唯一值 破解成功");
//                    XuLog.e(TAG,"左转："+temp_left_list.get(0).toString());
//                    XuLog.e(TAG,"右转："+temp_right_list.get(0).toString());
//                    return;
//                }
//
//
//                if(temp_left_list.size() > 1){
//                    //如果破解左转的时候没有破解出唯一值
//
//                    //判断右转是一个还是多个
//                    if(temp_right_list.size() > 1){
//                        //多个的时候比较是否有相同ID的
//                        ArrayList<OBDAutoCrackElement> temp_left = new ArrayList<OBDAutoCrackElement>();
//                        for(OBDAutoCrackElement temp_left_element : temp_left_list){
//                            for(OBDAutoCrackElement temp_right_element : temp_right_list){
//                                if(temp_left_element.getId().equals(temp_right_element.getId())){
//                                    //ID相等 加入目标列表 跳出遍历
//                                    temp_left.add(temp_left_element);
//                                    break;
//                                }
//
//                            }
//                        }
//
//                        //比较完了之后如果只剩一下 那么这条就是左转向  如果剩下多条 那么做取舍  取舍条件待定  此时使用哪条应该都能达到目的
//                        if(temp_left.size() <= 1 && temp_left.size() > 0){
//                            //比较完了之后如果只剩一下 那么这条就是左转向
//                            temp_left_list.clear();
//                            temp_left_list = (ArrayList<OBDAutoCrackElement>)temp_left.clone();
//                            XuLog.e(TAG,"过滤出唯一的左转，破解成功");
//                            XuLog.e(TAG,"左转："+temp_left_list.get(0).toString());
//                            XuLog.e(TAG,"右转："+temp_right_list.get(0).toString());
//                            return;
//                        }
//
//                        //剩下多条 那么做取舍  取舍条件待定  此时使用哪条应该都能达到目的
//                        if(temp_left.size() > 1){
//                            temp_left_list.clear();
//                            temp_left_list = (ArrayList<OBDAutoCrackElement>)temp_left.clone();
//                            XuLog.e(TAG,"无法过滤出唯一的右转，开始进行取舍");
//                            XuLog.e(TAG,"左转：");
//                            for(OBDAutoCrackElement finalized_temp : temp_left_list){
//                                XuLog.e(TAG,"finalized:"+finalized_temp.toString());
//                            }
//                            XuLog.e(TAG,"右转：");
//                            for(OBDAutoCrackElement finalized_temp : temp_right_list){
//                                XuLog.e(TAG,"finalized:"+finalized_temp.toString());
//                            }
//                            return;
//                        }
//
//                        //如果没有相同的  开始做取舍
//                        if(temp_left.size() < 1){
//                            XuLog.e(TAG, "没有相同ID的右转数据");
//                            XuLog.e(TAG,"左转：");
//                            for(OBDAutoCrackElement finalized_temp : temp_left_list){
//                                XuLog.e(TAG,"finalized:"+finalized_temp.toString());
//                            }
//                            XuLog.e(TAG,"右转：");
//                            for(OBDAutoCrackElement finalized_temp : temp_right_list){
//                                XuLog.e(TAG,"finalized:"+finalized_temp.toString());
//                            }
//                        }
//
//
//                    }else{
//                        //如果右转只有一个的话 那么判断是否与左转相同
//                        ArrayList<OBDAutoCrackElement> temp_left = new ArrayList<OBDAutoCrackElement>();
//                        for(OBDAutoCrackElement temp_left_element : temp_left_list){
//                            if(temp_left_element.getId().equals(temp_right_list.get(0).getId())){
//                                temp_left.add(temp_left_element);
//                            }
//                        }
//
//
//                        //比较完了之后如果只剩一下 那么这条就是左转向  如果剩下多条 那么做取舍  取舍条件待定  此时使用哪条应该都能达到目的
//                        if(temp_left.size() <= 1 && temp_left.size() > 0){
//                            //比较完了之后如果只剩一下 那么这条就是左转向
//                            temp_left_list.clear();
//                            temp_left_list = (ArrayList<OBDAutoCrackElement>)temp_left.clone();
//                            XuLog.e(TAG,"过滤出唯一的左转，破解成功");
//                            XuLog.e(TAG,"左转："+temp_left_list.get(0).toString());
//                            XuLog.e(TAG,"右转："+temp_right_list.get(0).toString());
//                            return;
//                        }
//
//                        //剩下多条 那么做取舍  取舍条件待定  此时使用哪条应该都能达到目的
//                        if(temp_left.size() > 1){
//                            temp_left_list.clear();
//                            temp_left_list = (ArrayList<OBDAutoCrackElement>)temp_left.clone();
//                            XuLog.e(TAG,"无法过滤出唯一的右转，开始进行取舍");
//                            XuLog.e(TAG,"左转：");
//                            for(OBDAutoCrackElement finalized_temp : temp_left_list){
//                                XuLog.e(TAG,"finalized:"+finalized_temp.toString());
//                            }
//                            XuLog.e(TAG,"右转："+temp_right_list.get(0).toString());
//                            return;
//                        }
//
//                        //如果没有相同的  开始做取舍
//                        if(temp_left.size() < 1){
//                            XuLog.e(TAG, "没有相同ID的右转数据");
//                            XuLog.e(TAG,"左转：");
//                            for(OBDAutoCrackElement finalized_temp : temp_left_list){
//                                XuLog.e(TAG,"finalized:"+finalized_temp.toString());
//                            }
//                            XuLog.e(TAG,"右转："+temp_right_list.get(0).toString());
//                        }
//
//
//
//                    }
//
//
//
//                }else{
//                    //如果破解左转的时候破解出了唯一值 这时右转是多个的
//
//                    //首先判断右转里面是否有跟左转相同ID的数据 有则取相同ID的
//                    ArrayList<OBDAutoCrackElement> temp_reight = new ArrayList<OBDAutoCrackElement>();
//                    for(OBDAutoCrackElement temp_right_element : temp_right_list){
//                        if(temp_right_element.getId().equals(temp_left_list.get(0).getId())){
//                            temp_reight.add(temp_right_element);
//                        }
//                    }
//
//                    //比较完了之后如果只剩一下 那么这条就是右转向  如果剩下多条 那么做取舍  取舍条件待定  此时使用哪条应该都能达到目的
//                    if(temp_reight.size() <= 1 && temp_reight.size() > 0){
//                        //比较完了之后如果只剩一下 那么这条就是右转向
//                        temp_right_list.clear();
//                        temp_right_list = (ArrayList<OBDAutoCrackElement>)temp_reight.clone();
//                        XuLog.e(TAG,"过滤出唯一的右转，破解成功");
//                        XuLog.e(TAG,"左转："+temp_left_list.get(0).toString());
//                        XuLog.e(TAG,"右转："+temp_right_list.get(0).toString());
//                        return;
//                    }
//                    //剩下多条 那么做取舍  取舍条件待定  此时使用哪条应该都能达到目的
//                    if(temp_reight.size() > 1){
//                        temp_right_list.clear();
//                        temp_right_list = (ArrayList<OBDAutoCrackElement>)temp_reight.clone();
//                        XuLog.e(TAG,"无法过滤出唯一的右转，开始进行取舍");
//                        XuLog.e(TAG,"左转："+temp_left_list.get(0).toString());
//                        XuLog.e(TAG,"右转：");
//                        for(OBDAutoCrackElement finalized_temp : temp_right_list){
//                            XuLog.e(TAG,"finalized:"+finalized_temp.toString());
//                        }
//                        return;
//                    }
//                    //如果没有相同的  开始做取舍
//                    if(temp_reight.size() < 1){
//                        XuLog.e(TAG,"没有相同ID的右转数据");
//                        XuLog.e(TAG,"左转："+temp_left_list.get(0).toString());
//                        XuLog.e(TAG,"右转：");
//                        for(OBDAutoCrackElement finalized_temp : temp_right_list){
//                            XuLog.e(TAG,"finalized:"+finalized_temp.toString());
//                        }
//                    }
//                }
//
//                break;

            case 8:  //重置过滤列表 准备破解下一个
                if (fristList.isEmpty()) {
                    XuLog.e(TAG, "总表是空的");
                    return;
                }
                beFilteredList.clear();
                beFilteredList = (ArrayList<OBDAutoCrackElement>) fristList.clone();
                //重新保存文件
                if (isDebug) {
                    resetFileName();
                }
                break;


//            case 9:  //保存打了五次的左转
//                if(beFilteredList.isEmpty()){
//                    XuLog.e(TAG,"列表为空");
//                    return;
//                }
//                temp_left_list.clear();
//                ArrayList<OBDAutoCrackElement> temp_left_5_list = new ArrayList<OBDAutoCrackElement>();
//                for(OBDAutoCrackElement temp_left_5 : beFilteredList){
//                    if(temp_left_5.getCount() == 5 ){
//                        temp_left_5_list.add(temp_left_5);
//                    }
//                }
//                if(temp_left_5_list.isEmpty()){
//                    for(OBDAutoCrackElement temp_left_5 : beFilteredList){
//                        if(temp_left_5.getCount() >= 5 ){
//                            temp_left_5_list.add(temp_left_5);
//                        }
//                    }
//                }
//                temp_left_list = (ArrayList<OBDAutoCrackElement>)temp_left_5_list.clone();
//
//                break;
//
//            case 10:  //保存打了五次的右转
//                if(beFilteredList.isEmpty()){
//                    XuLog.e(TAG,"列表为空");
//                    return;
//                }
//                temp_right_list.clear();
//                ArrayList<OBDAutoCrackElement> temp_right_5_list = new ArrayList<OBDAutoCrackElement>();
//                for(OBDAutoCrackElement temp_left_5 : beFilteredList){
//                    if(temp_left_5.getCount() == 5 ){
//                        temp_right_5_list.add(temp_left_5);
//                    }
//                }
//                if(temp_right_5_list.isEmpty()){
//                    for(OBDAutoCrackElement temp_left_5 : beFilteredList){
//                        if(temp_left_5.getCount() >= 5 ){
//                            temp_right_5_list.add(temp_left_5);
//                        }
//                    }
//                }
//                temp_right_list = (ArrayList<OBDAutoCrackElement>)temp_right_5_list.clone();
//
//                break;


        }


    }


    private InputCarModeDialog.Builder builder;
    private static DialogInterface askDialog;

    public DialogInterface showDialogToInputBrand(Context context, String msg, final OnClickYesOrNoListener listener) {


        try {
            if (askDialog != null) {
                askDialog.dismiss();
            }
            builder = new InputCarModeDialog.Builder(context);
            builder.setMessage(msg);
            builder.setPositiveButton(AppContext.getInstance().getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    askDialog = dialog;
                    listener.isyes(true, dialog);
                }
            });

            builder.setNegativeButton(AppContext.getInstance().getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    askDialog = dialog;
                    listener.isyes(false, dialog);
                }
            });

            builder.create().show();
            return askDialog;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    Runnable img_leftTurnLightOn = new Runnable() {
        @Override
        public void run() {
//            XuToast.show(AppContext.getInstance(),"左转灯发生变化");
            if (imgCenterLeft != null && mHandler != null) {
                imgCenterLeft.setImageResource(R.mipmap.ico_left_turn_light_on);
                //900毫秒后恢复
                mHandler.postDelayed(img_leftTurnLightOff, 500);
            }
        }
    };
    Runnable img_leftTurnLightOff = new Runnable() {
        @Override
        public void run() {
            if (imgCenterLeft != null) {
                imgCenterLeft.setImageResource(R.mipmap.ico_left_turn_light_off);
            }
        }
    };

    Runnable img_rightTurnLightOn = new Runnable() {
        @Override
        public void run() {
            if (imgCenterLeft != null && mHandler != null) {
                imgCenterLeft.setImageResource(R.mipmap.ico_right_turn_light_on);
                mHandler.postDelayed(img_rightTurnLightOff, 500);
            }
        }
    };
    Runnable img_rightTurnLightOff = new Runnable() {
        @Override
        public void run() {
            if (imgCenterLeft != null) {
                imgCenterLeft.setImageResource(R.mipmap.ico_right_turn_light_off);
            }
        }
    };

    @OnClick(R.id.iv_titlebar_back)
    void back() {
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacks(rightTurnLightOn);
        mHandler.removeCallbacks(rightTurnLightOff);
        mHandler.removeCallbacks(leftTurnLightOn);
        mHandler.removeCallbacks(leftTurnLightOff);
        if (askDialog != null) {
            askDialog.dismiss();
        }
        AppContext.getInstance().getDeviceHelper().closeCANListener(null);
        isFristReuslt = true;
        started = false;
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
        isReviewing = false;
        option = 0;
        crackCount = 0;
        fristList = new ArrayList<OBDAutoCrackElement>();     //总表
        beFilteredList = new ArrayList<OBDAutoCrackElement>();//进行过滤的表

        review_left = null;
        review_right = null;
    }


    @OnClick(R.id.tv_obd_auto_crack_tips)
    public void onViewClicked() {
        //UIHelper.showtestAutoCrack(this);
    }


}
