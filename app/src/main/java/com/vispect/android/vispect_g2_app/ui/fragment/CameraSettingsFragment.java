package com.vispect.android.vispect_g2_app.ui.fragment;

import android.graphics.Point;
import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.vispect.android.vispect_g2_app.R;
import com.vispect.android.vispect_g2_app.adapter.CalibrateAdapter;
import com.vispect.android.vispect_g2_app.app.AppContext;
import com.vispect.android.vispect_g2_app.controller.UIHelper;
import com.vispect.android.vispect_g2_app.ui.activity.CameraTypeActivity;
import com.vispect.android.vispect_g2_app.ui.widget.MoListView;
import com.vispect.android.vispect_g2_app.utils.XuLog;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import interf.GetG2CameraList;

/**
 * Created by mo on 2018/7/12.
 * <p>
 * 选择G2镜头,改变镜头类型
 */

public class CameraSettingsFragment extends BaseFragment {
    public static int listitem;
    @Bind(R.id.list_select_camera)
    MoListView listSelectCamera;
    private CalibrateAdapter calibrateAdapter;
    private List<String> data = new ArrayList<>();
    private List<Point> cameras = new ArrayList<>();

    @Override
    public int getContentResource() {
        return R.layout.fragment_calibrate;
    }

    @Override
    protected void initView(View view) throws IOException {
        listSelectCamera.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                listitem = cameras.get(i).x;
                UIHelper.startActivity(getActivity(), CameraTypeActivity.class);
            }
        });

        view.findViewById(R.id.img_back_main).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        calibrateAdapter = new CalibrateAdapter(getContext());
        listSelectCamera.setAdapter(calibrateAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        AppContext.getInstance().getDeviceHelper().getG2CameraList(new GetG2CameraList() {
            @Override
            public void onSuccess(ArrayList arrayList) {
                XuLog.e("success" + arrayList.size());
                cameras.clear();
                data.clear();
                cameras.addAll(arrayList);
                for (Point p : cameras) {
                    switch (p.y) {
                        case -1:
                            data.add(STR(R.string.camera_unavailable, p.x));
                            break;
                        case 0:
                            data.add(STR(R.string.camera_none, p.x));
                            break;
                        case 1:
                            data.add(STR(R.string.camera_font, p.x));
                            break;
                        case 2:
                            data.add(STR(R.string.camera_none, p.x));
                            break;
                        case 3:
                            data.add(STR(R.string.camera_DSM, p.x));
                            break;
                        case 4:
                            data.add(STR(R.string.camera_LF, p.x));
                            break;
                        case 5:
                            data.add(STR(R.string.camera_LB, p.x));
                            break;
                        case 6:
                            data.add(STR(R.string.camera_RF, p.x));
                            break;
                        case 7:
                            data.add(STR(R.string.camera_RB, p.x));
                            break;
                    }
                }
                calibrateAdapter.setData(data);
            }

            @Override
            public void onFail() {

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
}
