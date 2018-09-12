package com.vispect.android.vispect_g2_app.base;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.vispect.android.vispect_g2_app.R;
import com.vispect.android.vispect_g2_app.ui.fragment.IndexFragment;

import butterknife.ButterKnife;

public abstract class BaseFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = getContentResource() != 0 ? inflater.inflate(getContentResource(), container, false) : null;
        ButterKnife.bind(this, view);

        initView();
        return view;
    }

    protected void initView() {

    }

    protected void setTitle(@StringRes int title) {
        TextView titleView = getActivity().findViewById(R.id.tv_title);
        if (titleView != null) titleView.setText(title);
    }

    /**
     * 布局资源文件
     *
     * @return
     */
    @LayoutRes
    public abstract int getContentResource();

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    protected void replaceFragment(@NonNull Fragment fragment) {
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.content, fragment, BaseFragment.class.getSimpleName()).commit();
    }

    protected void pushToFragment(@NonNull Fragment fragment) {
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.content, fragment, BaseFragment.class.getSimpleName())
                .addToBackStack(BaseFragment.class.getSimpleName()).commit();
    }

    protected String STR(@StringRes int res) {
        return getActivity().getString(res);
    }

    protected void finish() {
        int entryCount = getFragmentManager().getBackStackEntryCount();
        if (entryCount > 0) {
            getFragmentManager().popBackStackImmediate();
        }
    }
}
