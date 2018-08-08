package com.vispect.android.vispect_g2_app.ui.fragment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.rey.material.app.BottomSheetDialog;
import com.serchinastico.coolswitch.CoolSwitch;
import com.vispect.android.vispect_g2_app.R;
import com.vispect.android.vispect_g2_app.app.AppContext;
import com.vispect.android.vispect_g2_app.controller.UIHelper;
import com.vispect.android.vispect_g2_app.interf.OnClickYesOrNoListener;
import com.vispect.android.vispect_g2_app.ui.activity.MainActivity;
import com.vispect.android.vispect_g2_app.ui.activity.SettingsActivity;
import com.vispect.android.vispect_g2_app.ui.widget.DialogHelp;
import com.vispect.android.vispect_g2_app.utils.XuLog;
import com.vispect.android.vispect_g2_app.utils.XuToast;
import com.weigan.loopview.LoopView;

import java.io.IOException;
import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import interf.GetDsmAlarmSensitivity;
import interf.GetDsmStartSpeed;
import interf.ResultListner;

/**
 * Created by mo on 2018/7/18.
 *
 * DSM启动速度设置
 */

public class DriverStatusFragment extends BaseFragment {
    @Bind(R.id.speed_dis)
    TextView speedDis;
    @Bind(R.id.time_dis)
    TextView timeDis;
    @Bind(R.id.cool_switch_foo)
    CoolSwitch coolSwitchFoo;
    @Bind(R.id.speed_phone)
    TextView speedPhone;
    @Bind(R.id.time_phone)
    TextView timePhone;
    @Bind(R.id.cool_switch_foo1)
    CoolSwitch coolSwitchFoo1;
    @Bind(R.id.cool_switch_foo2)
    CoolSwitch coolSwitchFoo2;
    @Bind(R.id.speed_smoking)
    TextView speedSmoking;
    @Bind(R.id.time_smoking)
    TextView timeSmoking;
    @Bind(R.id.cool_switch_foo3)
    CoolSwitch coolSwitchFoo3;
    @Bind(R.id.speed_looking)
    TextView speedLooking;
    @Bind(R.id.time_looking)
    TextView timeLooking;
    @Bind(R.id.cool_switch_foo4)
    CoolSwitch coolSwitchFoo4;
    @Bind(R.id.speed_yawning)
    TextView speedYawning;
    @Bind(R.id.time_yawning)
    TextView timeYawning;
    @Bind(R.id.cool_switch_foo5)
    CoolSwitch coolSwitchFoo5;
    @Bind(R.id.speed_face)
    TextView speedFace;
    @Bind(R.id.time_face)
    TextView timeFace;
    @Bind(R.id.cool_switch_foo6)
    CoolSwitch coolSwitchFoo6;
    @Bind(R.id.speed_camera)
    TextView speedCamera;
    @Bind(R.id.time_camera)
    TextView timeCamera;
    @Bind(R.id.cool_switch_foo7)
    CoolSwitch coolSwitchFoo7;
    @Bind(R.id.speed_drinking)
    TextView speedDrinking;
    @Bind(R.id.time_drinking)
    TextView timeDrinking;
    @Bind(R.id.cool_switch_foo8)
    CoolSwitch coolSwitchFoo8;
    private ArrayList<String> list;
    private int timeDangerous;
    private int speedDangerous;

    @Override
    public int getContentResource() {
        return R.layout.fragment_driverstatus;
    }

    @Override
    protected void initView(View view) throws IOException {

    }

    @Override
    public void onResume() {
        super.onResume();


        AppContext.getInstance().getDeviceHelper().getDSMStartSpeed(new GetDsmStartSpeed() {
            @Override
            public void onSuccess(int i, int i1, int i2, int i3, int i4, int i5, int i6, int i7, int i8) {
                speedDangerous = i1;
                speedDis.setText(i + "");
                speedPhone.setText(i2 + "");
                speedSmoking.setText(i3 + "");
                speedLooking.setText(i4 + "");
                speedYawning.setText(i5 + "");
                speedFace.setText(i6 + "");
                speedCamera.setText(i7 + "");
                speedDrinking.setText(i8 + "");
            }

            @Override
            public void onFail() {

            }
        });

        AppContext.getInstance().getDeviceHelper().getDsmAlarmSensitivity(new GetDsmAlarmSensitivity() {
            @Override
            public void onSuccess(int i, int i1, int i2, int i3, int i4, int i5, int i6, int i7, int i8) {
                timeDangerous = i1;
                if (i != 0) {
                    coolSwitchFoo.setChecked(true);
                    timeDis.setText(((float) i) / 10 + "");
                }else{
                    timeDis.setText("1.0");
                }
                if (i1 != 0){
                    coolSwitchFoo1.setChecked(true);
                }
                if (i2 != 0) {
                    coolSwitchFoo2.setChecked(true);
                    timePhone.setText(((float) i2) / 10 + "");
                }else{
                    timePhone.setText("3.0");
                }
                if (i3 != 0) {
                    coolSwitchFoo3.setChecked(true);
                    timeSmoking.setText(((float) i3) / 10 + "");
                }else{
                    timeSmoking.setText("1.0");
                }
                if (i4 != 0) {
                    coolSwitchFoo4.setChecked(true);
                    timeLooking.setText(((float) i4) / 10 + "");
                }else {
                    timeLooking.setText("3.0");
                }
                if (i5 != 0) {
                    coolSwitchFoo5.setChecked(true);
                    timeYawning.setText(((float) i5) / 10 + "");
                }else {
                    timeYawning.setText("2.0");
                }
                if (i6 != 0) {
                    coolSwitchFoo6.setChecked(true);
                    timeFace.setText((i6) / 10 + "");
                }else {
                    timeFace.setText("10");
                }
                if (i7 != 0) {
                    coolSwitchFoo7.setChecked(true);
                    timeCamera.setText((i7) / 10 + "");
                }else{
                    timeCamera.setText("10");
                }
                if (i8 != 0) {
                    timeDrinking.setText(((float) i8) / 10 + "");
                    coolSwitchFoo8.setChecked(true);
                }else {
                    timeDrinking.setText("2.0");
                }
            }

            @Override
            public void onFail() {

            }
        });

            getActivity().findViewById(R.id.btn_save).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    UIHelper.showAsk(getActivity(), STR(R.string.ask_save_data), true, new OnClickYesOrNoListener() {
                        @Override
                        public void isyes(boolean b, DialogInterface dialog) {
                            if (b) {
                                saveData();
                                Message msg = new Message();
                                msg.arg2 = -1;
                                SettingsActivity.transHandler.sendMessage(msg);
                            }
                            dialog.dismiss();
                        }
                    });
                }
            });
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
       if (!hidden){
           onResume();
       }
    }

    public void saveData(){
        if (!AppContext.getInstance().getDeviceHelper().isConnectedDevice()){
            XuToast.show(getActivity(),"no connect");
        }
        int timeDis = 0,timePhone =0 ,timeSmoking=0,timeLooking=0,timeYawning=0,timeFace=0,timeCamera=0,timeDrink = 0;
        if (coolSwitchFoo.isChecked()){
            Float f = Float.parseFloat(this.timeDis.getText().toString());
            timeDis = (int) (f*10);
        }
        if (coolSwitchFoo2.isChecked()){
            Float f = Float.parseFloat(this.timePhone.getText().toString());
            timePhone = (int) (f*10);
        }
        if (coolSwitchFoo3.isChecked()){
            Float f = Float.parseFloat(this.timeSmoking.getText().toString());
            timeSmoking = (int) (f*10);
        }
        if (coolSwitchFoo4.isChecked()){
            Float f = Float.parseFloat(this.timeLooking.getText().toString());
            timeLooking = (int) (f*10);
        }
        if (coolSwitchFoo5.isChecked()){
            Float f = Float.parseFloat(this.timeYawning.getText().toString());
            timeYawning = (int) (f*10);
        }
        if (coolSwitchFoo6.isChecked()){
            Float f = Float.parseFloat(this.timeFace.getText().toString());
            timeFace = (int) (f*10);
        }
        if (coolSwitchFoo7.isChecked()){
            Float f = Float.parseFloat(this.timeCamera.getText().toString());
            timeCamera = (int) (f*10);
        }
        if (coolSwitchFoo8.isChecked()){
            Float f = Float.parseFloat(this.timeDrinking.getText().toString());
            timeDrink = (int) (f*10);
        }

        AppContext.getInstance().getDeviceHelper().setDSMStartSpeed(new ResultListner() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onFail(int i) {

            }
        },Integer.parseInt(speedDis.getText().toString()), speedDangerous,
          Integer.parseInt(speedPhone.getText().toString()),Integer.parseInt(speedSmoking.getText().toString()),
          Integer.parseInt(speedLooking.getText().toString()),Integer.parseInt(speedYawning.getText().toString()),
          Integer.parseInt(speedFace.getText().toString()),Integer.parseInt(speedCamera.getText().toString()),
          Integer.parseInt(speedDrinking.getText().toString()));

        XuLog.e("speed",speedDis.getText().toString());

        AppContext.getInstance().getDeviceHelper().setDsmAlarmSensitivity(new ResultListner() {
            @Override
            public void onSuccess() {
                XuToast.show(getActivity(),"success");
            }

            @Override
            public void onFail(int i) {

            }
        },timeDis,timeDangerous,timePhone,timeSmoking,timeLooking,timeYawning,timeFace,timeCamera,timeDrink);

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

    @OnClick({R.id.speed_dis, R.id.time_dis, R.id.speed_phone, R.id.time_phone, R.id.speed_smoking, R.id.time_smoking, R.id.speed_looking, R.id.time_looking, R.id.speed_yawning, R.id.time_yawning, R.id.speed_face, R.id.time_face, R.id.speed_camera, R.id.time_camera, R.id.speed_drinking, R.id.time_drinking})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.speed_dis:
                list = new ArrayList<>();
                for (int i = 10; i < 81; i++) {
                    list.add(i + "");
                }
                showLoopDialog((TextView) view, list);
                break;
            case R.id.time_dis:
                list = new ArrayList<>();
                for (int i = 10; i < 30; i++) {
                    list.add((float)i/10 + "");
                }
                showLoopDialog((TextView) view, list);
                break;
            case R.id.speed_phone:
                list = new ArrayList<>();
                for (int i = 10; i < 81; i++) {
                    list.add(i + "");
                }
                showLoopDialog((TextView) view, list);
                break;
            case R.id.time_phone:
                list = new ArrayList<>();
                for (int i = 30; i < 90; i++) {
                    list.add((float)i/10 + "");
                }
                showLoopDialog((TextView) view, list);
                break;
            case R.id.speed_smoking:
                list = new ArrayList<>();
                for (int i = 10; i < 81; i++) {
                    list.add(i + "");
                }
                showLoopDialog((TextView) view, list);
                break;
            case R.id.time_smoking:
                list = new ArrayList<>();
                for (int i = 10; i < 30; i++) {
                    list.add((float)i/10 + "");
                }
                showLoopDialog((TextView) view, list);
                break;
            case R.id.speed_looking:
                list = new ArrayList<>();
                for (int i = 10; i < 81; i++) {
                    list.add(i + "");
                }
                showLoopDialog((TextView) view, list);
                break;
            case R.id.time_looking:
                list = new ArrayList<>();
                for (int i = 30; i < 90; i++) {
                    list.add((float)i/10 + "");
                }
                showLoopDialog((TextView) view, list);
                break;
            case R.id.speed_yawning:
                list = new ArrayList<>();
                for (int i = 10; i < 81; i++) {
                    list.add(i + "");
                }
                showLoopDialog((TextView) view, list);
                break;
            case R.id.time_yawning:
                list = new ArrayList<>();
                for (int i = 20; i < 50; i++) {
                    list.add((float)i/10 + "");
                }
                showLoopDialog((TextView) view, list);
                break;
            case R.id.speed_face:
                list = new ArrayList<>();
                for (int i = 10; i < 81; i++) {
                    list.add(i + "");
                }
                showLoopDialog((TextView) view, list);
                break;
            case R.id.time_face:
                list = new ArrayList<>();
                for (int i = 10; i < 61; i++) {
                    list.add(i + "");
                }
                showLoopDialog((TextView) view, list);
                break;
            case R.id.speed_camera:
                list = new ArrayList<>();
                for (int i = 10; i < 81; i++) {
                    list.add(i + "");
                }
                showLoopDialog((TextView) view, list);
                break;
            case R.id.time_camera:
                list = new ArrayList<>();
                for (int i = 10; i < 61; i++) {
                    list.add(i + "");
                }
                showLoopDialog((TextView) view, list);
                break;
            case R.id.speed_drinking:
                list = new ArrayList<>();
                for (int i = 10; i < 81; i++) {
                    list.add(i + "");
                }
                showLoopDialog((TextView) view, list);
                break;
            case R.id.time_drinking:
                list = new ArrayList<>();
                for (int i = 20; i < 50; i++) {
                    list.add((float)i/10 + "");
                }
                showLoopDialog((TextView) view, list);
                break;
        }
    }

    public void showLoopDialog(final TextView textView, final ArrayList<String> stringArrayList) {
        final View dialog = getLayoutInflater().inflate(R.layout.dialog_loop, null);
        final BottomSheetDialog bottomInterPasswordDialog = new BottomSheetDialog(getActivity());
        bottomInterPasswordDialog
                .contentView(dialog)
                .heightParam(UIHelper.dp2px(getActivity(), 205))
                .inDuration(100)
                .outDuration(100)
                .cancelable(true)
                .show();

        final LoopView loopView = dialog.findViewById(R.id.loop);
        loopView.setItems(stringArrayList);
        loopView.setTextSize(20);
        loopView.setNotLoop();

        dialog.findViewById(R.id.tv_cancle).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomInterPasswordDialog.dismiss();
            }
        });

        dialog.findViewById(R.id.tv_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                textView.setText(stringArrayList.get(loopView.getSelectedItem()));
                bottomInterPasswordDialog.dismiss();
            }
        });
    }

}
