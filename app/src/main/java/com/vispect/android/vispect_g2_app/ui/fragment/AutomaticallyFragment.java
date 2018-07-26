package com.vispect.android.vispect_g2_app.ui.fragment;

/**
 * Created by mo on 2018/3/27.
 */

import android.graphics.PointF;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.vispect.android.vispect_g2_app.R;
import com.vispect.android.vispect_g2_app.app.AppContext;
import com.vispect.android.vispect_g2_app.utils.XuToast;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import interf.CorrectingCallback;
import interf.SetDeviceInfoCallback;



/**
 * Created by mo on 2018/3/27.
 */


public class AutomaticallyFragment extends BaseFragment {
    @Bind(R.id.speedlin)
    LinearLayout speedlin;
    @Bind(R.id.camera)
    TextView camera;
    private TextView title;
    private TextView titleFinish;
    private Handler step9Handler;
    @Bind(R.id.btn_angle)
    Button btnAngle;
    @Bind(R.id.btn_speed)
    Button btnSpeed;

    @Override
    public int getContentResource() {
        return R.layout.fragment_calibration4step9;
    }

    @Override
    protected void initView(View view) {
        
        step9Handler = new Handler();

        AppContext.getInstance().getDeviceHelper().getCorrectingProgress(new CorrectingCallback() {
            @Override
            public void onGetProgress(int i) {
                if (i != 100) {
                    btnAngle.setText(STR(R.string.calibration4_step9_context9));
                    btnAngle.setEnabled(false);
                    btnAngle.setBackgroundColor(getResources().getColor(R.color.table_check_white));
                }
            }

            @Override
            public void onGetOperationResult(boolean b) {

            }

            @Override
            public void onGetCenterPoint(PointF pointF) {

            }
        });



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

    @OnClick({R.id.btn_angle, R.id.btn_speed})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_angle:
                if (!AppContext.getInstance().getDeviceHelper().isConnectedDevice()) {
                    XuToast.show(getContext(), STR(R.string.road_live_notconnect));
                    return;
                }
//                AppContext.getInstance().getDeviceHelper().startCameraCorrectOnLow(10, new SetDeviceInfoCallback() {
//                    @Override
//                    public void onSuccess() {
//                        XuToast.show(getContext(),"校正角度成功");
//                    }
//
//                    @Override
//                    public void onFail(int i) {
//                        XuToast.show(getContext(),"校正角度失败");
//                    }
//                });
                AppContext.getInstance().getDeviceHelper().startCorrecting(new CorrectingCallback() {
                    @Override
                    public void onGetProgress(int i) {
                        Log.i("onGetProgress", "" + i);
                    }

                    @Override
                    public void onGetOperationResult(boolean b) {
                        if (b) {
                            btnAngle.setText(STR(R.string.calibration4_step9_context9));
                            btnAngle.setEnabled(false);
                            btnAngle.setBackgroundColor(getResources().getColor(R.color.table_check_white));
                            XuToast.show(getContext(), STR(R.string.calibration4_step9_context9));
                        } else {
                            if (AppContext.getInstance().getDeviceHelper().isConnectedDevice()) {
                                XuToast.show(getContext(), STR(R.string.calibration4_step9_context10));
                            } else {
                                XuToast.show(getContext(), STR(R.string.road_live_notconnect));
                            }

                        }
                    }

                    @Override
                    public void onGetCenterPoint(PointF pointF) {
                        Log.i("onGetProgress", "" + pointF.toString());
                    }
                });


                break;
            case R.id.btn_speed:
                if (!AppContext.getInstance().getDeviceHelper().isConnectedDevice()) {
                    XuToast.show(getContext(), STR(R.string.road_live_notconnect));
                    return;
                }


                AppContext.getInstance().getDeviceHelper().startSpeedCalibration(new SetDeviceInfoCallback() {
                    @Override
                    public void onSuccess() {
                        btnSpeed.setText(STR(R.string.calibration4_step9_context9));
                        btnSpeed.setEnabled(false);
                        btnSpeed.setBackgroundColor(getResources().getColor(R.color.table_check_white));
                    }

                    @Override
                    public void onFail(int i) {
                        XuToast.show(getContext(), STR(R.string.calibration4_step9_context10));
                    }
                });

                break;
        }
    }


//    public void switchContent(Fragment from, Fragment to, int position) {
//        if (mContent != to) {
//            mContent = to;
//            FragmentTransaction transaction = fm.beginTransaction();
//            if (!to.isAdded()) { // 先判断是否被add过
//                transaction.hide(from)
//                        .add(R.id.fra_counselor, to, tags[position]).commit(); // 隐藏当前的fragment，add下一个到Activity中
//            } else {
//                transaction.hide(from).show(to).commit(); // 隐藏当前的fragment，显示下一个
//            }
//        }
//    }

}

