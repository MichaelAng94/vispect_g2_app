package com.vispect.android.vispect_g2_app.ui.fragment;

import android.graphics.Point;
import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.vispect.android.vispect_g2_app.R;
import com.vispect.android.vispect_g2_app.adapter.CalibrateAdapter;
import com.vispect.android.vispect_g2_app.app.AppContext;
import com.vispect.android.vispect_g2_app.ui.activity.SettingsActivity;
import com.vispect.android.vispect_g2_app.ui.widget.DialogHelp;
import com.vispect.android.vispect_g2_app.utils.DialogUtils;
import com.vispect.android.vispect_g2_app.utils.XuString;
import com.vispect.android.vispect_g2_app.utils.XuToast;

import java.io.IOException;
import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import interf.GetG2CameraList;
import interf.ResultListner;

/**
 * Created by mo on 2018/7/19.
 * <p>
 * 选择镜头类型
 */

public class CameraTypeFragment extends BaseFragment {

    @Bind(R.id.list_camera_type)
    ListView listCameraType;
    private ArrayList<Integer> data = new ArrayList<>();
    private CalibrateAdapter calibrateAdapter;

    @Override
    public int getContentResource() {
        return R.layout.fragment_camera_type;
    }

    @Override
    public void onResume() {
        super.onResume();
        for (Integer i = 1; i < 8; i++) {
            if (i == 2) {
                continue;
            }
            data.add(i);
        }

        AppContext.getInstance().getDeviceHelper().getG2CameraList(new GetG2CameraList() {
            @Override
            public void onSuccess(ArrayList arrayList) {
                boolean isNotZh = !XuString.isZh(AppContext.getInstance());
                ArrayList<Point> cameras = arrayList;
                for (Point p : cameras) {
                    for (int i = 0; i < data.size(); i++) {
                        if (p.y == data.get(i)) {
                            data.remove(i);
                            i--;
                        }
                    }
                }
                ArrayList<String> stringArrayList = new ArrayList<>();
                for (int i : data) {
                    switch (i) {
                        case 1:
                            stringArrayList.add(STR(R.string.font_camera));
                            break;
                        case 2:
                            break;
                        case 3:
                            stringArrayList.add(STR(R.string.driver_status_monitoring));
                            break;
                        case 4:
                            if (isNotZh) stringArrayList.add(STR(R.string.left_camera_forward));
                            break;
                        case 5:
                            if (isNotZh) stringArrayList.add(STR(R.string.left_camera_back));
                            break;
                        case 6:
                            if (isNotZh) stringArrayList.add(STR(R.string.right_camera_forward));
                            break;
                        case 7:
                            if (isNotZh) stringArrayList.add(STR(R.string.right_camera_back));
                            break;
                    }
                }
                calibrateAdapter.setData(stringArrayList);
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
    protected void initView(View view) throws IOException {

        view.findViewById(R.id.img_back_main).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().finish();
            }
        });

        calibrateAdapter = new CalibrateAdapter(getContext());
        listCameraType.setAdapter(calibrateAdapter);

        listCameraType.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Point p = new Point();
                p.x = CameraSettingsFragment.listitem;
                p.y = data.get(i);
                ArrayList<Point> points = new ArrayList<>();
                points.add(p);
                DialogHelp.getInstance().connectDialog(getActivity(), "Changing");
                AppContext.getInstance().getDeviceHelper().setG2CameraType(new ResultListner() {
                    @Override
                    public void onSuccess() {
                        XuToast.show(getActivity(), R.string.save_success);
                        DialogHelp.getInstance().hideDialog();
                        getActivity().finish();
                    }

                    @Override
                    public void onFail(int i) {
                        DialogHelp.getInstance().hideDialog();
                        XuToast.show(getActivity(), R.string.save_fail);
                    }
                }, points);

//                pushToFragment(new CameraSettingsFragment());
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

}
