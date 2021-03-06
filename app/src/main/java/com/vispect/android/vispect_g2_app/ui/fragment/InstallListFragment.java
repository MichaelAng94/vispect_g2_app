package com.vispect.android.vispect_g2_app.ui.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.vispect.android.vispect_g2_app.R;
import com.vispect.android.vispect_g2_app.adapter.InstallListAdapter;
import com.vispect.android.vispect_g2_app.ui.activity.InstallActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;


/**
 * Created by mo on 2018/3/27.
 *
 * 安装界面
 */


@SuppressLint("ValidFragment")
public class InstallListFragment extends BaseFragment {

    @SuppressLint("HandlerLeak")
    public static Handler listHandler;
    @Bind(R.id.list_install)
    ListView listInstall;
    private List<String> data;


    public InstallListFragment() {

    }

    @Override
    public int getContentResource() {
        return R.layout.fragment_installlist;
    }


    @Override
    protected void initView(View view) {
        data = new ArrayList<>();
        data.add(STR(R.string.vehicle_information));
        data.add(STR(R.string.calibrate));
        data.add(STR(R.string.enter_size));
        data.add(STR(R.string.tali_with_car));
        data.add(STR(R.string.adapt_automatically));

        InstallListAdapter cvl = new InstallListAdapter(getContext(), data);
        listInstall.setAdapter(cvl);
        listInstall.setOnItemClickListener(new listClick());

        getActivity().findViewById(R.id.btn_save).setVisibility(View.GONE);
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

    public class listClick implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            Message msg = new Message();
            msg.arg2 = i;
            InstallActivity.transHandler.sendMessage(msg);
        }
    }

}
