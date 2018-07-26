package com.vispect.android.vispect_g2_app.ui.fragment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.vispect.android.vispect_g2_app.R;
import com.vispect.android.vispect_g2_app.app.AppContext;
import com.vispect.android.vispect_g2_app.controller.UIHelper;
import com.vispect.android.vispect_g2_app.interf.OnClickYesOrNoListener;
import com.vispect.android.vispect_g2_app.ui.activity.SettingsActivity;
import com.vispect.android.vispect_g2_app.utils.XuToast;

import java.io.IOException;

import butterknife.Bind;
import butterknife.ButterKnife;
import interf.GetDsmSensitivityLevel;
import interf.GetDsmShieldingListener;
import interf.ResultListner;

/**
 * Created by mo on 2018/7/24.
 */

public class DSMSettings extends BaseFragment {
    @Bind(R.id.et_distracting)
    EditText etDistracting;
    @Bind(R.id.et_dangerous)
    EditText etDangerous;
    @Bind(R.id.et_phone)
    EditText etPhone;
    @Bind(R.id.et_smoking)
    EditText etSmoking;
    @Bind(R.id.et_looking)
    EditText etLooking;
    @Bind(R.id.et_yawning)
    EditText etYawning;
    @Bind(R.id.et_face)
    EditText etFace;
    @Bind(R.id.et_camera)
    EditText etCamera;
    @Bind(R.id.et_drinking)
    EditText etDrinking;
    @Bind(R.id.et_distracting_sen)
    EditText etDistractingSen;
    @Bind(R.id.et_dangerous_sen)
    EditText etDangerousSen;
    @Bind(R.id.et_yawning_sen)
    EditText etYawningSen;
    @Bind(R.id.et_looking_sen)
    EditText etLookingSen;

    @Override
    public int getContentResource() {
        return R.layout.fragment_dsmsettings;
    }

    @Override
    protected void initView(View view) throws IOException {

    }

    @Override
    public void onResume() {
        super.onResume();
        if (!AppContext.getInstance().getDeviceHelper().isConnectedDevice()){
            XuToast.show(getActivity(),STR(R.string.road_live_notconnect));
        }

        AppContext.getInstance().getDeviceHelper().getDSMShieldingTime(new GetDsmShieldingListener() {
            @Override
            public void onSuccess(int i, int i1, int i2, int i3, int i4, int i5, int i6, int i7, int i8) {
                etDistracting.setText(""+i);
                etDangerous.setText(""+i1);
                etPhone.setText(""+i2);
                etSmoking.setText(""+i3);
                etLooking.setText(""+i4);
                etYawning.setText(""+i5);
                etFace.setText(""+i6);
                etCamera.setText(""+i7);
                etDrinking.setText(""+i8);
            }

            @Override
            public void onFail() {

            }
        });

        AppContext.getInstance().getDeviceHelper().getDSMSensitivityLevel(new GetDsmSensitivityLevel() {
            @Override
            public void onSuccess(int i, int i1, int i2, int i3) {
                etDistractingSen.setText(""+i);
                etDangerousSen.setText(""+i1);
                etYawningSen.setText(""+i2);
                etLookingSen.setText(""+i3);
            }

            @Override
            public void onFail() {

            }
        });

        getActivity().findViewById(R.id.btn_save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UIHelper.showAsk(getContext(), STR(R.string.ask_save_data), false, new OnClickYesOrNoListener() {
                    @Override
                    public void isyes(boolean var1, DialogInterface dialog) {
                        if (var1){
                            if (isNumeric(etDistracting.getText().toString())&&isNumeric(etDangerous.getText().toString())&&isNumeric(etPhone.getText().toString())&&isNumeric(etSmoking.getText().toString())&&isNumeric(etLooking.getText().toString())&&isNumeric(etYawning.getText().toString())&&isNumeric(etFace.getText().toString())&&isNumeric(etCamera.getText().toString())&&isNumeric(etDrinking.getText().toString())&&isNumeric(etDistractingSen.getText().toString())&&isNumeric(etDangerousSen.getText().toString())&&isNumeric(etYawningSen.getText().toString())&&isNumeric(etLookingSen.getText().toString())) {
                                int distracting = Integer.parseInt(etDistracting.getText().toString());
                                int dangerous = Integer.parseInt(etDangerous.getText().toString());
                                int phone = Integer.parseInt(etPhone.getText().toString());
                                int smoking = Integer.parseInt(etSmoking.getText().toString());
                                int looking = Integer.parseInt(etLooking.getText().toString());
                                int yawning = Integer.parseInt(etYawning.getText().toString());
                                int face = Integer.parseInt(etFace.getText().toString());
                                int camera = Integer.parseInt(etCamera.getText().toString());
                                int driking = Integer.parseInt(etDrinking.getText().toString());
                                int distracting_sen = Integer.parseInt(etDistractingSen.getText().toString());
                                int dangerous_sen = Integer.parseInt(etDangerousSen.getText().toString());
                                int yawn_sen = Integer.parseInt(etYawningSen.getText().toString());
                                int look_sen = Integer.parseInt(etLookingSen.getText().toString());
                                if (distracting<4||distracting>60||dangerous<4||dangerous>60||phone<4||phone>60||smoking<4||smoking>60||looking<4||looking>60||yawning<4||yawning>60||face<4||face>60||camera<4||camera>60||driking<4||driking>60||distracting_sen<1||distracting_sen>3||dangerous_sen<1||dangerous_sen>3||yawn_sen<1||yawn_sen>3||look_sen<1||look_sen>3){
                                    XuToast.show(getActivity(),STR(R.string.input_illegal));
                                }else {
                                    AppContext.getInstance().getDeviceHelper().setDSMShieldingTime(new ResultListner() {
                                        @Override
                                        public void onSuccess() {

                                        }

                                        @Override
                                        public void onFail(int i) {

                                        }
                                    },distracting,dangerous,phone,smoking,looking,yawning,face,camera,driking);

                                    AppContext.getInstance().getDeviceHelper().setDSMSensitivityLevel(distracting_sen, dangerous_sen, yawn_sen, look_sen, new ResultListner() {
                                        @Override
                                        public void onSuccess() {
                                            XuToast.show(getActivity(),"success");
                                        }

                                        @Override
                                        public void onFail(int i) {

                                        }
                                    });
                                    Message msg = new Message();
                                    msg.arg2 = -1;
                                    SettingsActivity.transHandler.sendMessage(msg);
                                }
                            }else {
                                XuToast.show(getActivity(),STR(R.string.input_illegal));
                            }

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

    public static boolean isNumeric(String str){
        for (int i = str.length();--i>=0;){
            if (!Character.isDigit(str.charAt(i))){
                return false;
            }
        }
        return true;
    }

}
