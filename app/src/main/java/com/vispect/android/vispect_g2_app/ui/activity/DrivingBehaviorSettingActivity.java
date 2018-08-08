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
import com.vispect.android.vispect_g2_app.interf.OnSoftKeyboardChangeListener;
import com.vispect.android.vispect_g2_app.utils.XuLog;
import com.vispect.android.vispect_g2_app.utils.XuString;
import com.vispect.android.vispect_g2_app.utils.XuToast;
import com.vispect.android.vispect_g2_app.utils.XuView;

import butterknife.Bind;
import butterknife.OnClick;
import interf.SetDeviceInfoCallback;
import interf.onGetCollisionParameterCallback;
import interf.onGetRapidAccelerationParameterCallback;
import interf.onGetRapidDecelerationParameterCallback;
import interf.onGetRapidTurnParameterCallback;
import interf.onGetRolloverParameterCallback;


/**
 * 设备驾驶参数设置
 *
 * Created by xu on 2017/9/12.
 */
public class DrivingBehaviorSettingActivity extends BaseActivity {
    private final static String TAG = "DrivingBehaviorSettingActivity";

    @Bind(R.id.iv_titlebar_title)
    TextView title;
//    @Bind(R.id.iv_titlebar_save_frame)
//    TextView iv_titlebar_save_frame;
    Button btn_save;

    @Bind(R.id.edit_acceleration_cache_time)
    EditText edit_acceleration_cache_time;
    @Bind(R.id.edit_acceleration_start_up_speed)
    EditText edit_acceleration_start_up_speed;
    @Bind(R.id.edit_acceleration_start_up_time)
    EditText edit_acceleration_start_up_time;
    @Bind(R.id.edit_acceleration_way_of_speed_got)
    EditText edit_acceleration_way_of_speed_got;

    @Bind(R.id.edit_deceleration_cache_time)
    EditText edit_deceleration_cache_time;
    @Bind(R.id.edit_deceleration_start_up_speed)
    EditText edit_deceleration_start_up_speed;
    @Bind(R.id.edit_deceleration_start_up_time)
    EditText edit_deceleration_start_up_time;
    @Bind(R.id.edit_deceleration_way_of_speed_got)
    EditText edit_deceleration_way_of_speed_got;

    @Bind(R.id.edit_turn_trigger_angular_velocity)
    EditText edit_turn_trigger_angular_velocity;
    @Bind(R.id.edit_turn_cache_time_of_before)
    EditText edit_turn_cache_time_of_before;
    @Bind(R.id.edit_turn_cache_time_of_last)
    EditText edit_turn_cache_time_of_last;
    @Bind(R.id.edit_turn_interval_time)
    EditText edit_turn_interval_time;

    @Bind(R.id.edit_rollover_trigger_angular_velocity)
    EditText edit_rollover_trigger_angular_velocity;
    @Bind(R.id.edit_rollover_cache_time_of_before)
    EditText edit_rollover_cache_time_of_before;
    @Bind(R.id.edit_rollover_cache_time_of_last)
    EditText edit_rollover_cache_time_of_last;
    @Bind(R.id.edit_rollover_interval_time)
    EditText edit_rollover_interval_time;

    @Bind(R.id.edit_collision__threshold_of_accelerationX)
    EditText edit_collision__threshold_of_accelerationX;
    @Bind(R.id.edit_collision__threshold_of_accelerationY)
    EditText edit_collision__threshold_of_accelerationY;
    @Bind(R.id.edit_collision__threshold_of_accelerationZ)
    EditText edit_collision__threshold_of_accelerationZ;
    @Bind(R.id.edit_collision_cache_time_before)
    EditText edit_collision_cache_time_before;
    @Bind(R.id.edit_collision_cache_time_last)
    EditText edit_collision_cache_time_last;
    @Bind(R.id.edit_collision_cache_upload_image)
    EditText edit_collision_cache_upload_image;
    @Bind(R.id.ll_buttom)
    LinearLayout ll_buttom;

    private int lastAccelerationcacheTime;
    private int lastAccelerationtriggerSpeed;
    private int lastAccelerationtriggerTime;
    private int lastAccelerationwayOfGetSpeed;

    private int lastDecelerationcacheTime;
    private int lastDecelerationtriggerSpeed;
    private int lastDecelerationtriggerTime;
    private int lastDecelerationwayOfGetSpeed;

    private int lastTurntriggerAngularVelocity;
    private int lastTurncacheTimeOfBefore;
    private int lastTurncacheTimeOfLast;
    private int lastTurnlapseTime;

    private int lastRollovertriggerAngularVelocity;
    private int lastRollovercacheTimeOfBefore;
    private int lastRollovercacheTimeOfLast;
    private int lastRolloverlapseTime;

    private int lastCollisionthresholdOFAccelerationX;
    private int lastCollisionthresholdOFAccelerationY;
    private int lastCollisionthresholdOFAccelerationZ;
    private int lastCollisioncacheTimeOfBefore;
    private int lastCollisioncacheTimeOfLast;
    private int lastCollisionuploadImage;


    Handler mHandler = new Handler();
    Runnable showprogress = new Runnable() {
        @Override
        public void run() {
            //showProgress();
        }
    };
    Runnable showToastByLegalValue = new Runnable() {
        @Override
        public void run() {
            XuToast.show(DrivingBehaviorSettingActivity.this,STR(R.string.edit_input_legal_value));
           // hideProgress();
        }
    };
    Runnable dissprogress = new Runnable() {
        @Override
        public void run() {
          //  hideProgress();
        }
    };
    Runnable setRapidAcceleration = new Runnable() {
        @Override
        public void run() {
            edit_acceleration_cache_time.setText(lastAccelerationcacheTime + "");
            edit_acceleration_start_up_speed.setText(lastAccelerationtriggerSpeed + "");
            edit_acceleration_start_up_time.setText(lastAccelerationtriggerTime + "");
            edit_acceleration_way_of_speed_got.setText(lastAccelerationwayOfGetSpeed + "");
        }
    };
    Runnable setRapidDeceleration = new Runnable() {
        @Override
        public void run() {
            edit_deceleration_cache_time.setText(lastDecelerationcacheTime + "");
            edit_deceleration_start_up_speed.setText(lastDecelerationtriggerSpeed + "");
            edit_deceleration_start_up_time.setText(lastDecelerationtriggerTime + "");
            edit_deceleration_way_of_speed_got.setText(lastDecelerationwayOfGetSpeed + "");
        }
    };
    Runnable setRapidTurn = new Runnable() {
        @Override
        public void run() {
            edit_turn_trigger_angular_velocity.setText(lastTurntriggerAngularVelocity + "");
            edit_turn_cache_time_of_before.setText(lastTurncacheTimeOfBefore + "");
            edit_turn_cache_time_of_last.setText(lastTurncacheTimeOfLast + "");
            edit_turn_interval_time.setText(lastTurnlapseTime + "");
        }
    };
    Runnable setRollover = new Runnable() {
        @Override
        public void run() {
            edit_rollover_trigger_angular_velocity.setText(lastRollovertriggerAngularVelocity + "");
            edit_rollover_cache_time_of_before.setText(lastRollovercacheTimeOfBefore + "");
            edit_rollover_cache_time_of_last.setText(lastRollovercacheTimeOfLast + "");
            edit_rollover_interval_time.setText(lastRolloverlapseTime + "");
        }
    };
    Runnable setCollision = new Runnable() {
        @Override
        public void run() {
            edit_collision__threshold_of_accelerationX.setText(lastCollisionthresholdOFAccelerationX + "");
            edit_collision__threshold_of_accelerationY.setText(lastCollisionthresholdOFAccelerationY + "");
            edit_collision__threshold_of_accelerationZ.setText(lastCollisionthresholdOFAccelerationZ + "");
            edit_collision_cache_time_before.setText(lastCollisioncacheTimeOfBefore + "");
            edit_collision_cache_time_last.setText(lastCollisioncacheTimeOfLast + "");
            edit_collision_cache_upload_image.setText(lastCollisionuploadImage + "");
        }
    };
    Runnable saveFail = new Runnable() {
        @Override
        public void run() {
            XuToast.show(DrivingBehaviorSettingActivity.this, STR(R.string.save_fail));
          //  hideProgress();
        }
    };
    Runnable saveSuccess = new Runnable() {
        @Override
        public void run() {
            XuToast.show(DrivingBehaviorSettingActivity.this, STR(R.string.save_success));
          //  hideProgress();
        }
    };

    private OnSoftKeyboardChangeListener onSoftKeyboardChangeListener = new OnSoftKeyboardChangeListener() {
        @Override
        public void onSoftKeyBoardChange(final int softKeybardHeight,final boolean visible) {
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
        return R.layout.activity_senser_set;
    }

    @Override
    protected void initView(View view) {
        title.setText(STR(R.string.driving_behavior_setting_title));
       // XuView.setViewVisible(iv_titlebar_save_frame, true);
        btn_save = findViewById(R.id.btn_save);
        btn_save.setVisibility(View.VISIBLE);

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
                    //获取急加速参数
                    AppContext.getInstance().getDeviceHelper().getRapidAccelerationParameter(new onGetRapidAccelerationParameterCallback() {
                        @Override
                        public void onSuccess(int cacheTime, int triggerSpeed, int triggerTime, int wayOfGetSpeed) {
                            XuLog.e(TAG, "获取急加速参数成功！");
                            lastAccelerationcacheTime = cacheTime;
                            lastAccelerationtriggerSpeed = triggerSpeed;
                            lastAccelerationtriggerTime = triggerTime;
                            lastAccelerationwayOfGetSpeed = wayOfGetSpeed;
                            if( mHandler != null) {
                                mHandler.post(setRapidAcceleration);
                            }
                        }

                        @Override
                        public void onFail(int i) {
                            XuLog.e(TAG, "获取急加速参数失败！  " + i);
                        }
                    });

                    Thread.sleep(300);

                    //获取急减速参数
                    AppContext.getInstance().getDeviceHelper().getRapidDecelerationParameter(new onGetRapidDecelerationParameterCallback() {
                        @Override
                        public void onSuccess(int cacheTime, int triggerSpeed, int triggerTime, int wayOfGetSpeed) {
                            XuLog.e(TAG, "获取急减速参数成功！");
                            lastDecelerationcacheTime = cacheTime;
                            lastDecelerationtriggerSpeed = triggerSpeed;
                            lastDecelerationtriggerTime = triggerTime;
                            lastDecelerationwayOfGetSpeed = wayOfGetSpeed;
                            if( mHandler != null) {
                                mHandler.post(setRapidDeceleration);
                            }
                        }

                        @Override
                        public void onFail(int i) {
                            XuLog.e(TAG, "获取急减速参数失败！  " + i);
                        }
                    });

                    Thread.sleep(300);

                    //获取急转弯参数
                    AppContext.getInstance().getDeviceHelper().getRapidTurnParameter(new onGetRapidTurnParameterCallback() {
                        @Override
                        public void onSuccess(int triggerAngularVelocity, int cacheTimeOfBefore, int cacheTimeOfLast, int lapseTime) {
                            XuLog.e(TAG, "获取急转弯参数成功！");
                            lastTurntriggerAngularVelocity = triggerAngularVelocity;
                            lastTurncacheTimeOfBefore = cacheTimeOfBefore;
                            lastTurncacheTimeOfLast = cacheTimeOfLast;
                            lastTurnlapseTime = lapseTime;
                            if( mHandler != null) {
                                mHandler.post(setRapidTurn);
                            }

                        }

                        @Override
                        public void onFail(int i) {
                            XuLog.e(TAG, "获取急转弯参数失败！  " + i);
                        }
                    });

                    Thread.sleep(300);

                    //获取侧翻参数
                    AppContext.getInstance().getDeviceHelper().getRolloverParameter(new onGetRolloverParameterCallback() {
                        @Override
                        public void onSuccess(int triggerAngularVelocity, int cacheTimeOfBefore, int cacheTimeOfLast, int lapseTime) {
                            XuLog.e(TAG, "获取侧翻参数成功！" + triggerAngularVelocity + "  " + cacheTimeOfBefore + "   " + cacheTimeOfLast + "   " + lapseTime);
                            lastRollovertriggerAngularVelocity = triggerAngularVelocity;
                            lastRollovercacheTimeOfBefore = cacheTimeOfBefore;
                            lastRollovercacheTimeOfLast = cacheTimeOfLast;
                            lastRolloverlapseTime = lapseTime;
                            if( mHandler != null){
                                mHandler.post(setRollover);
                            }

                        }

                        @Override
                        public void onFail(int i) {
                            XuLog.e(TAG, "获取侧翻参数失败！  " + i);
                        }
                    });

                    Thread.sleep(300);

                    //获取碰撞参数
                    AppContext.getInstance().getDeviceHelper().getCollisionParameter(new onGetCollisionParameterCallback() {
                        @Override
                        public void onSuccess(int thresholdOFAccelerationX, int thresholdOFAccelerationY, int thresholdOFAccelerationZ,
                                              int cacheTimeOfBefore, int cacheTimeOfLast, int uploadImage) {
                            XuLog.e(TAG, "获取碰撞参数成功！");
                            lastCollisionthresholdOFAccelerationX = thresholdOFAccelerationX;
                            lastCollisionthresholdOFAccelerationY = thresholdOFAccelerationY;
                            lastCollisionthresholdOFAccelerationZ = thresholdOFAccelerationZ;
                            lastCollisioncacheTimeOfBefore = cacheTimeOfBefore;
                            lastCollisioncacheTimeOfLast = cacheTimeOfLast;
                            lastCollisionuploadImage = uploadImage;
                            if( mHandler != null){
                            mHandler.post(setCollision);
                            }
                        }

                        @Override
                        public void onFail(int i) {
                            XuLog.e(TAG, "获取碰撞参数失败！  " + i);
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
    private void setEditTextViewFocus(EditText edit){
        edit.requestFocus();
        edit.requestFocusFromTouch();
    }

    @OnClick(R.id.btn_save)
    void saveData(View v) {
        try {
            mHandler.post(showprogress);
            AppContext.getInstance().getCachedThreadPool().execute(new Runnable() {
                @Override
                public void run() {

                    //先保存急加速的
                    final int rapidaccelerationcacheTime = Integer.parseInt(edit_acceleration_cache_time.getText().toString().trim().equals("- -") ? "-1" : edit_acceleration_cache_time.getText().toString().trim());
                    final int rapidaccelerationtriggerSpeed = Integer.parseInt(edit_acceleration_start_up_speed.getText().toString().trim().equals("- -") ? "-1" : edit_acceleration_start_up_speed.getText().toString().trim());
                    final int rapidaccelerationtriggerTime = Integer.parseInt(edit_acceleration_start_up_time.getText().toString().trim().equals("- -") ? "-1" : edit_acceleration_start_up_time.getText().toString().trim());
                    final int rapidaccelerationwayOfGetSpeed = Integer.parseInt(edit_acceleration_way_of_speed_got.getText().toString().trim().equals("- -") ? "-1" : edit_acceleration_way_of_speed_got.getText().toString().trim());

                    if (checkValue(lastAccelerationcacheTime, rapidaccelerationcacheTime) || checkValue(lastAccelerationtriggerSpeed, rapidaccelerationtriggerSpeed) || checkValue(lastAccelerationtriggerTime, rapidaccelerationtriggerTime) || checkValue(lastAccelerationtriggerTime, rapidaccelerationwayOfGetSpeed)) {
                        if(XuString.checkNumericalRange(rapidaccelerationcacheTime, 3, 15) && XuString.checkNumericalRange(rapidaccelerationtriggerSpeed, 1, 20) && XuString.checkNumericalRange(rapidaccelerationtriggerTime, 1, 10) && XuString.checkNumericalRange(rapidaccelerationwayOfGetSpeed, 0, 1)){
                            AppContext.getInstance().getDeviceHelper().rapidAccelerationSet(rapidaccelerationcacheTime, rapidaccelerationtriggerSpeed, rapidaccelerationtriggerTime, rapidaccelerationwayOfGetSpeed, new SetDeviceInfoCallback() {
                                @Override
                                public void onSuccess() {
                                    XuLog.e(TAG, "急加速的参数配置保存成功！");
                                    lastAccelerationcacheTime = rapidaccelerationcacheTime;
                                    lastAccelerationtriggerSpeed = rapidaccelerationtriggerSpeed;
                                    lastAccelerationtriggerTime = rapidaccelerationtriggerTime;
                                    lastAccelerationtriggerTime = rapidaccelerationwayOfGetSpeed;
                                    mHandler.post(saveSuccess);

                                }

                                @Override
                                public void onFail(int i) {
                                    XuLog.e(TAG, "急加速的参数配置保存失败！  " + i);
                                    mHandler.post(saveFail);
                                }
                            });

                            try {
                                Thread.sleep(500);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }else{
                            mHandler.post(showToastByLegalValue);
                            if(!XuString.checkNumericalRange(rapidaccelerationcacheTime, 3, 15)){
                                mHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        setEditTextViewFocus(edit_acceleration_cache_time);
                                    }
                                });
                                return;
                            }
                            if(!XuString.checkNumericalRange(rapidaccelerationtriggerSpeed, 1, 20)){
                                mHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        setEditTextViewFocus(edit_acceleration_start_up_speed);
                                    }
                                });
                                return;
                            }
                            if(!XuString.checkNumericalRange(rapidaccelerationtriggerTime, 1, 10)){
                                mHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        setEditTextViewFocus(edit_acceleration_start_up_time);
                                    }
                                });
                                return;
                            }
                            if(!XuString.checkNumericalRange(rapidaccelerationwayOfGetSpeed, 0, 1)){
                                mHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        setEditTextViewFocus(edit_acceleration_way_of_speed_got);
                                    }
                                });
                                return;
                            }
                            return;
                        }




                    }


                    //再保存急减速的
                    final int decelerationcacheTime = Integer.parseInt(edit_deceleration_cache_time.getText().toString().trim().equals("- -") ? "-1" : edit_deceleration_cache_time.getText().toString().trim());
                    final int decelerationtriggerSpeed = Integer.parseInt(edit_deceleration_start_up_speed.getText().toString().trim().equals("- -") ? "-1" : edit_deceleration_start_up_speed.getText().toString().trim());
                    final int decelerationtriggerTime = Integer.parseInt(edit_deceleration_start_up_time.getText().toString().trim().equals("- -") ? "-1" : edit_deceleration_start_up_time.getText().toString().trim());
                    final int decelerationwayOfGetSpeed = Integer.parseInt(edit_deceleration_way_of_speed_got.getText().toString().trim().equals("- -") ? "-1" : edit_deceleration_way_of_speed_got.getText().toString().trim());
                    if (checkValue(lastDecelerationcacheTime, decelerationcacheTime) || checkValue(lastDecelerationtriggerSpeed, decelerationtriggerSpeed) || checkValue(lastDecelerationtriggerTime, decelerationtriggerTime) || checkValue(lastDecelerationwayOfGetSpeed, decelerationwayOfGetSpeed)) {
                        if(XuString.checkNumericalRange(decelerationcacheTime, 3, 5) && XuString.checkNumericalRange(decelerationtriggerSpeed, 1, 20) && XuString.checkNumericalRange(decelerationtriggerTime, 1, 10) && XuString.checkNumericalRange(decelerationwayOfGetSpeed, 0, 1)){
                            AppContext.getInstance().getDeviceHelper().rapidDecelerationSet(decelerationcacheTime, decelerationtriggerSpeed, decelerationtriggerTime, decelerationwayOfGetSpeed, new SetDeviceInfoCallback() {
                                @Override
                                public void onSuccess() {
                                    XuLog.e(TAG, "急减速的参数配置保存成功！");
                                    lastDecelerationcacheTime = decelerationcacheTime;
                                    lastDecelerationtriggerSpeed = decelerationtriggerSpeed;
                                    lastDecelerationtriggerTime = decelerationtriggerTime;
                                    lastDecelerationwayOfGetSpeed = decelerationwayOfGetSpeed;
                                    mHandler.post(saveSuccess);

                                }

                                @Override
                                public void onFail(int i) {
                                    XuLog.e(TAG, "急减速的参数配置保存失败！  " + i);
                                    mHandler.post(saveFail);
                                }
                            });

                            try {
                                Thread.sleep(500);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }else{
                            mHandler.post(showToastByLegalValue);

                            if(!XuString.checkNumericalRange(decelerationcacheTime, 3, 5)){
                                mHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        setEditTextViewFocus(edit_deceleration_cache_time);
                                    }
                                });
                                return;
                            }
                            if(!XuString.checkNumericalRange(decelerationtriggerSpeed, 1, 20)){
                                mHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        setEditTextViewFocus(edit_deceleration_start_up_speed);
                                    }
                                });
                                return;
                            }
                            if(!XuString.checkNumericalRange(decelerationtriggerTime, 1, 10)){
                                mHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        setEditTextViewFocus(edit_deceleration_start_up_time);
                                    }
                                });
                                return;
                            }
                            if(!XuString.checkNumericalRange(decelerationwayOfGetSpeed, 0, 1)){
                                mHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        setEditTextViewFocus(edit_deceleration_way_of_speed_got);
                                    }
                                });
                                return;
                            }


                            return;
                        }

                    }


                    //再保存急转弯的
                    final int turntriggerAngularVelocity = Integer.parseInt(edit_turn_trigger_angular_velocity.getText().toString().trim().equals("- -") ? "-1" : edit_turn_trigger_angular_velocity.getText().toString().trim());
                    final int turncacheTimeOfBefore = Integer.parseInt(edit_turn_cache_time_of_before.getText().toString().trim().equals("- -") ? "-1" : edit_turn_cache_time_of_before.getText().toString().trim());
                    final int turncacheTimeOfLast = Integer.parseInt(edit_turn_cache_time_of_last.getText().toString().trim().equals("- -") ? "-1" : edit_turn_cache_time_of_last.getText().toString().trim());
                    final int turnlapseTime = Integer.parseInt(edit_turn_interval_time.getText().toString().trim().equals("- -") ? "-1" : edit_turn_interval_time.getText().toString().trim());

                    if (checkValue(lastTurntriggerAngularVelocity, turntriggerAngularVelocity) || checkValue(lastTurncacheTimeOfBefore, turncacheTimeOfBefore) || checkValue(lastTurncacheTimeOfLast, turncacheTimeOfLast) || checkValue(lastTurnlapseTime, turnlapseTime)) {
                        if(XuString.checkNumericalRange(turntriggerAngularVelocity, 100, 1000) && XuString.checkNumericalRange(turncacheTimeOfBefore, 0, 5) && XuString.checkNumericalRange(turncacheTimeOfLast, 0, 5) && XuString.checkNumericalRange(turnlapseTime, 2, 20)){
                            AppContext.getInstance().getDeviceHelper().rapidTurnSet(turntriggerAngularVelocity, turncacheTimeOfBefore, turncacheTimeOfLast, turnlapseTime, new SetDeviceInfoCallback() {
                                @Override
                                public void onSuccess() {
                                    XuLog.e(TAG, "急转弯的参数配置保存成功！");
                                    lastTurntriggerAngularVelocity = turntriggerAngularVelocity;
                                    lastTurncacheTimeOfBefore = turncacheTimeOfBefore;
                                    lastTurncacheTimeOfLast = turncacheTimeOfLast;
                                    lastTurnlapseTime = turnlapseTime;
                                    mHandler.post(saveSuccess);

                                }

                                @Override
                                public void onFail(int i) {
                                    XuLog.e(TAG, "急转弯的参数配置保存失败！  " + i);
                                    mHandler.post(saveFail);
                                }
                            });

                            try {
                                Thread.sleep(500);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }else{
                            mHandler.post(showToastByLegalValue);
                            if(!XuString.checkNumericalRange(turntriggerAngularVelocity, 100, 1000)){
                                mHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        setEditTextViewFocus(edit_turn_trigger_angular_velocity);
                                    }
                                });
                                return;
                            }
                            if(!XuString.checkNumericalRange(turncacheTimeOfBefore, 0, 5)){
                                mHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        setEditTextViewFocus(edit_turn_cache_time_of_before);
                                    }
                                });
                                return;
                            }
                            if(!XuString.checkNumericalRange(turncacheTimeOfLast, 0, 5)){
                                mHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        setEditTextViewFocus(edit_turn_cache_time_of_last);
                                    }
                                });
                                return;
                            }
                            if(!XuString.checkNumericalRange(turnlapseTime, 2, 20)){
                                mHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        setEditTextViewFocus(edit_turn_interval_time);
                                    }
                                });
                                return;
                            }

                            return;
                        }


                    }


                    //在保存侧翻的
                    final int rollovertriggerAngularVelocity = Integer.parseInt(edit_rollover_trigger_angular_velocity.getText().toString().trim().equals("- -") ? "-1" : edit_rollover_trigger_angular_velocity.getText().toString().trim());
                    final int rollovercacheTimeOfBefore = Integer.parseInt(edit_rollover_cache_time_of_before.getText().toString().trim().equals("- -") ? "-1" : edit_rollover_cache_time_of_before.getText().toString().trim());
                    final int rollovercacheTimeOfLast = Integer.parseInt(edit_rollover_cache_time_of_last.getText().toString().trim().equals("- -") ? "-1" : edit_rollover_cache_time_of_last.getText().toString().trim());
                    final int rolloverlapseTime = Integer.parseInt(edit_rollover_interval_time.getText().toString().trim().equals("- -") ? "-1" : edit_rollover_interval_time.getText().toString().trim());

                    if (checkValue(lastRollovertriggerAngularVelocity, rollovertriggerAngularVelocity) || checkValue(lastRollovercacheTimeOfBefore, rollovercacheTimeOfBefore) || checkValue(lastRollovercacheTimeOfLast, rollovercacheTimeOfLast) || checkValue(lastRolloverlapseTime, rolloverlapseTime)) {
                       if(XuString.checkNumericalRange(rollovertriggerAngularVelocity, 100, 1000) && XuString.checkNumericalRange(rollovercacheTimeOfBefore, 0, 5) && XuString.checkNumericalRange(rollovercacheTimeOfLast, 0, 5) && XuString.checkNumericalRange(rolloverlapseTime, 2, 20)){
                           AppContext.getInstance().getDeviceHelper().rolloverSet(rollovertriggerAngularVelocity, rollovercacheTimeOfBefore, rollovercacheTimeOfLast, rolloverlapseTime, new SetDeviceInfoCallback() {
                               @Override
                               public void onSuccess() {
                                   XuLog.e(TAG, "侧翻的参数配置保存成功！");
                                   lastRollovertriggerAngularVelocity = rollovertriggerAngularVelocity;
                                   lastRollovercacheTimeOfBefore = rollovercacheTimeOfBefore;
                                   lastRollovercacheTimeOfLast = rollovercacheTimeOfLast;
                                   lastRolloverlapseTime = rolloverlapseTime;
                                   mHandler.post(saveSuccess);

                               }

                               @Override
                               public void onFail(int i) {
                                   XuLog.e(TAG, "侧翻的参数配置保存失败！  " + i);
                                   mHandler.post(saveFail);
                               }
                           });

                           try {
                               Thread.sleep(500);
                           } catch (InterruptedException e) {
                               e.printStackTrace();
                           }
                       }else{
                        mHandler.post(showToastByLegalValue);

                           if(!XuString.checkNumericalRange(rollovertriggerAngularVelocity, 100, 1000)){
                               mHandler.post(new Runnable() {
                                   @Override
                                   public void run() {
                                       setEditTextViewFocus(edit_rollover_trigger_angular_velocity);
                                   }
                               });
                               return;
                           }

                           if(!XuString.checkNumericalRange(rollovercacheTimeOfBefore, 0, 5)){
                               mHandler.post(new Runnable() {
                                   @Override
                                   public void run() {
                                       setEditTextViewFocus(edit_rollover_cache_time_of_before);
                                   }
                               });
                               return;
                           }

                           if(!XuString.checkNumericalRange(rollovercacheTimeOfLast, 0, 5)){
                               mHandler.post(new Runnable() {
                                   @Override
                                   public void run() {
                                       setEditTextViewFocus(edit_rollover_cache_time_of_last);
                                   }
                               });
                               return;
                           }

                           if(!XuString.checkNumericalRange(rolloverlapseTime, 2, 20)){
                               mHandler.post(new Runnable() {
                                   @Override
                                   public void run() {
                                       setEditTextViewFocus(edit_rollover_interval_time);
                                   }
                               });
                               return;
                           }

                        return;
                    }

                    }


                    //在保存碰撞的

                    final int collisionthresholdOFAccelerationX = Integer.parseInt(edit_collision__threshold_of_accelerationX.getText().toString().trim().equals("- -") ? "-1" : edit_collision__threshold_of_accelerationX.getText().toString().trim());
                    final int collisionthresholdOFAccelerationY = Integer.parseInt(edit_collision__threshold_of_accelerationY.getText().toString().trim().equals("- -") ? "-1" : edit_collision__threshold_of_accelerationY.getText().toString().trim());
                    final int collisionthresholdOFAccelerationZ = Integer.parseInt(edit_collision__threshold_of_accelerationZ.getText().toString().trim().equals("- -") ? "-1" : edit_collision__threshold_of_accelerationZ.getText().toString().trim());
                    final int collisioncacheTimeOfBefore = Integer.parseInt(edit_collision_cache_time_before.getText().toString().trim().equals("- -") ? "-1" : edit_collision_cache_time_before.getText().toString().trim());
                    final int collisioncacheTimeOfLast = Integer.parseInt(edit_collision_cache_time_last.getText().toString().trim().equals("- -") ? "-1" : edit_collision_cache_time_last.getText().toString().trim());
                    final int collisionuploadImage = Integer.parseInt(edit_collision_cache_upload_image.getText().toString().trim().equals("- -") ? "-1" : edit_collision_cache_upload_image.getText().toString().trim());

                    if (checkValue(lastCollisionthresholdOFAccelerationX, collisionthresholdOFAccelerationX) || checkValue(lastCollisionthresholdOFAccelerationY, collisionthresholdOFAccelerationY) || checkValue(lastCollisionthresholdOFAccelerationZ, collisionthresholdOFAccelerationZ) || checkValue(lastCollisioncacheTimeOfBefore, collisioncacheTimeOfBefore) || checkValue(lastCollisioncacheTimeOfLast, collisioncacheTimeOfLast) || checkValue(lastCollisionuploadImage, collisionuploadImage)){

                        if(XuString.checkNumericalRange(collisionthresholdOFAccelerationX, 100, 3000) && XuString.checkNumericalRange(collisionthresholdOFAccelerationY, 100, 3000) && XuString.checkNumericalRange(collisionthresholdOFAccelerationZ, 100, 3000) && XuString.checkNumericalRange(collisioncacheTimeOfBefore, 1, 10) && XuString.checkNumericalRange(collisioncacheTimeOfLast, 1, 10) && XuString.checkNumericalRange(collisionuploadImage, 0,2)){
                            AppContext.getInstance().getDeviceHelper().collisionSet(collisionthresholdOFAccelerationX, collisionthresholdOFAccelerationY, collisionthresholdOFAccelerationZ, collisioncacheTimeOfBefore, collisioncacheTimeOfLast, collisionuploadImage, new SetDeviceInfoCallback() {
                                @Override
                                public void onSuccess() {
                                    XuLog.e(TAG, "碰撞的参数配置保存成功！");
                                    lastCollisionthresholdOFAccelerationX = collisionthresholdOFAccelerationX;
                                    lastCollisionthresholdOFAccelerationY = collisionthresholdOFAccelerationY;
                                    lastCollisionthresholdOFAccelerationZ = collisionthresholdOFAccelerationZ;
                                    lastCollisioncacheTimeOfBefore = collisioncacheTimeOfBefore;
                                    lastCollisioncacheTimeOfLast = collisioncacheTimeOfLast;
                                    mHandler.post(saveSuccess);

                                }

                                @Override
                                public void onFail(int i) {
                                    XuLog.e(TAG, "碰撞的参数配置保存失败！  " + i);
                                    mHandler.post(saveFail);
                                }
                            });
                        }else{
                            mHandler.post(showToastByLegalValue);
                            if(!XuString.checkNumericalRange(collisionthresholdOFAccelerationX, 100, 3000)){
                                mHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        setEditTextViewFocus(edit_collision__threshold_of_accelerationX);
                                    }
                                });
                                return;
                            }
                            if(!XuString.checkNumericalRange(collisionthresholdOFAccelerationY, 100, 3000)){
                                mHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        setEditTextViewFocus(edit_collision__threshold_of_accelerationY);
                                    }
                                });
                                return;
                            }
                            if(!XuString.checkNumericalRange(collisionthresholdOFAccelerationZ, 100, 3000)){
                                mHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        setEditTextViewFocus(edit_collision__threshold_of_accelerationZ);
                                    }
                                });
                                return;
                            }
                            if(!XuString.checkNumericalRange(collisioncacheTimeOfBefore, 1, 10)){
                                mHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        setEditTextViewFocus(edit_collision_cache_time_before);
                                    }
                                });
                                return;
                            }
                            if(!XuString.checkNumericalRange(collisioncacheTimeOfLast, 1, 10)){
                                mHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        setEditTextViewFocus(edit_collision_cache_time_last);
                                    }
                                });
                                return;
                            }
                            if(!XuString.checkNumericalRange(collisionuploadImage, 0,2)){
                                mHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        setEditTextViewFocus(edit_collision_cache_upload_image);
                                    }
                                });
                                return;
                            }


                            return;
                        }

                    }

                    mHandler.post(dissprogress);

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            mHandler.post(dissprogress);
        }
        finish();
    }

    @OnClick(R.id.iv_titlebar_back)
    void back() {

        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
            mHandler = null;
        }
    }
}
