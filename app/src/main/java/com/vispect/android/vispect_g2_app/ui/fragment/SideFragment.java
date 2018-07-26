package com.vispect.android.vispect_g2_app.ui.fragment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.rey.material.app.BottomSheetDialog;
import com.vispect.android.vispect_g2_app.R;
import com.vispect.android.vispect_g2_app.app.AppContext;
import com.vispect.android.vispect_g2_app.controller.UIHelper;
import com.vispect.android.vispect_g2_app.interf.OnClickYesOrNoListener;
import com.vispect.android.vispect_g2_app.ui.activity.SettingsActivity;
import com.vispect.android.vispect_g2_app.utils.XuLog;
import com.weigan.loopview.LoopView;

import java.io.IOException;
import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import interf.GetSPMSpeedSpace;
import interf.ResultListner;

/**
 * Created by mo on 2018/7/19.
 */

public class SideFragment extends BaseFragment {

    @Bind(R.id.tv_start_speed)
    TextView tvStartSpeed;
    @Bind(R.id.tv_end_speed)
    TextView tvEndSpeed;
    private ArrayList<String> list;

    @Override
    public int getContentResource() {
        return R.layout.fragment_side_cameras;
    }

    @Override
    protected void initView(View view) throws IOException {

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

    @Override
    public void onResume() {
        super.onResume();
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

        AppContext.getInstance().getDeviceHelper().getSPMSpeedSpace(new GetSPMSpeedSpace() {
            @Override
            public void onSuccess(int i, int i1) {
                XuLog.e("i" + i + "i1" + i1);
                tvStartSpeed.setText(i+"");
                tvEndSpeed.setText(i1+"");
            }

            @Override
            public void onFail(int i) {

            }
        }, 0);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            onResume();
        }
    }

    public void saveData(){
        AppContext.getInstance().getDeviceHelper().setSPMSpeedSpace(new ResultListner() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onFail(int i) {

            }
        },0,Integer.parseInt(tvStartSpeed.getText().toString()),Integer.parseInt(tvEndSpeed.getText().toString()));
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

    @OnClick({R.id.tv_start_speed, R.id.tv_end_speed})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_start_speed:
                list = new ArrayList<>();
                for (int i = 0; i < 35; i += 5) {
                    list.add("" + i);
                }
                showLoopDialog((TextView) view, list);
                break;
            case R.id.tv_end_speed:
                list = new ArrayList<>();
                for (int i = 40; i < 80; i += 10) {
                    list.add("" + i);
                }
                showLoopDialog((TextView) view, list);
                break;
        }
    }
}
