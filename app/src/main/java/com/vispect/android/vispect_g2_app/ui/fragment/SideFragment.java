package com.vispect.android.vispect_g2_app.ui.fragment;

import android.content.DialogInterface;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.vispect.android.vispect_g2_app.R;
import com.vispect.android.vispect_g2_app.app.AppContext;
import com.vispect.android.vispect_g2_app.controller.DeviceHelper;
import com.vispect.android.vispect_g2_app.controller.UIHelper;
import com.vispect.android.vispect_g2_app.interf.Callback;
import com.vispect.android.vispect_g2_app.interf.OnClickYesOrNoListener;
import com.vispect.android.vispect_g2_app.utils.DialogUtils;
import com.vispect.android.vispect_g2_app.utils.XuLog;
import com.vispect.android.vispect_g2_app.utils.XuToast;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import interf.GetSPMSpeedSpace;
import interf.ResultListner;

/**
 * Created by mo on 2018/7/19.
 * <p>
 * 侧边镜头启动速度设置
 */

public class SideFragment extends BaseFragment {

    @Bind(R.id.tv_start_speed)
    TextView startSpeed;
    @Bind(R.id.tv_end_speed)
    TextView endSpeed;
    private List<String> startList = new ArrayList<>();
    private List<String> endList = new ArrayList<>();
    private int _startSpeed;
    private int _endSpeed;

    {
        startList.add("0");
        startList.add("5");
        startList.add("10");
        startList.add("15");
        startList.add("20");
        startList.add("25");
        startList.add("30");

        endList.add("40");
        endList.add("50");
        endList.add("60");
        endList.add("70");
        endList.add("80");
    }

    @Override
    public int getContentResource() {
        return R.layout.fragment_side_cameras;
    }

    @Override
    protected void initView(View view) {
        AppContext.getInstance().getDeviceHelper().getSPMSpeedSpace(new GetSPMSpeedSpace() {
            @Override
            public void onSuccess(int start, int end) {
                XuLog.e("start" + start + "end" + end);
                startSpeed.setText(start + "");
                endSpeed.setText(end + "");
                _startSpeed = start;
                _endSpeed = end;
            }

            @Override
            public void onFail(int i) {
            }
        }, 0);

        Button save = view.findViewById(R.id.btn_save);

        save.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                UIHelper.showAsk(getActivity(), STR(R.string.ask_save_data), true, new OnClickYesOrNoListener() {
                    @Override
                    public void isyes(boolean b, DialogInterface dialog) {
                        if (b) {
                            saveData();
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

    public void saveData() {
        int start = Integer.parseInt(startSpeed.getText().toString());
        int end = Integer.parseInt(endSpeed.getText().toString());
        if (start == _startSpeed && end == _endSpeed) {
            XuToast.show(getActivity(), R.string.no_change);
            return;
        }
        if (!DeviceHelper.isG2Connected()) {
            XuToast.show(getActivity(), R.string.device_disconnected);
            return;
        }
        DeviceHelper.setSPMSpeedSpace(new ResultListner() {
            @Override
            public void onSuccess() {
                XuToast.show(getActivity(), R.string.save_success);
                finish();
            }

            @Override
            public void onFail(int i) {
                XuToast.show(getActivity(), R.string.save_fail);
            }
        }, start, end);

    }

    @OnClick({R.id.fl_start_speed, R.id.fl_end_speed})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.fl_start_speed:
                DialogUtils.showLooperDialog(getActivity(), startList, new Callback<String>() {
                    @Override
                    public void callback(String value) {
                        startSpeed.setText(value);
                    }
                });
                break;
            case R.id.fl_end_speed:
                DialogUtils.showLooperDialog(getActivity(), endList, new Callback<String>() {
                    @Override
                    public void callback(String value) {
                        endSpeed.setText(value);
                    }
                });
                break;
        }
    }
}
