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
import com.vispect.android.vispect_g2_app.ui.activity.SettingsActivity;
import com.vispect.android.vispect_g2_app.ui.widget.MoListview;
import com.vispect.android.vispect_g2_app.utils.XuLog;

import java.io.IOException;
import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import interf.GetG2CameraList;

/**
 * Created by mo on 2018/7/12.
 *
 * 选择G2镜头,改变镜头类型
 */

public class CameraSettingsFragment extends BaseFragment {
    @Bind(R.id.list_select_camera)
    MoListview listSelectCamera;
    private CalibrateAdapter calibrateAdapter;
    private ArrayList<String> data;
    public static int listitem;
    private ArrayList<Point> cameras;

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
                Message msg = new Message();
                msg.arg2 = 50;
                SettingsActivity.transHandler.sendMessage(msg);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        AppContext.getInstance().getDeviceHelper().getG2CameraList(new GetG2CameraList() {
            @Override
            public void onSuccess(ArrayList arrayList) {
                XuLog.e("sucess" + arrayList.size());
                cameras = arrayList;
                data = new ArrayList<>();
                for (Point p : cameras){
                    switch (p.y){
                        case -1:data.add("Camera "+p.x+" socket: "+"Unavailable");break;
                        case 0:data.add("Camera "+p.x+" socket: "+"None");break;
                        case 1:data.add("Camera "+p.x+" socket: "+"Font Camera");break;
                        case 2:data.add("Camera "+p.x+" socket: "+"None");break;
                        case 3:data.add("Camera "+p.x+" socket: "+"Driver Status Monitoring");break;
                        case 4:data.add("Camera "+p.x+" socket: "+"Left side(forwards)");break;
                        case 5:data.add("Camera "+p.x+" socket: "+"Left side(backwards)");break;
                        case 6:data.add("Camera "+p.x+" socket: "+"Right side(forwards)");break;
                        case 7:data.add("Camera "+p.x+" socket: "+"Right side(backwards)");break;
                    }
                }
                calibrateAdapter = new CalibrateAdapter(getContext(),data);
                listSelectCamera.setAdapter(calibrateAdapter);
            }

            @Override
            public void onFail() {

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
}
