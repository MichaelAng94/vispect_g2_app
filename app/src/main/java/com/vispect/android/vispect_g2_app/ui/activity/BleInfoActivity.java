package com.vispect.android.vispect_g2_app.ui.activity;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;

import com.vispect.android.vispect_g2_app.R;
import com.vispect.android.vispect_g2_app.ui.fragment.BleInfoFragment;
import com.vispect.android.vispect_g2_app.ui.fragment.CameraTypeFragment;

/**
 * 检查当前版本
 */

public class BleInfoActivity extends BaseActivity {

    private FragmentManager _fragmentManager;

    @Override
    public int getContentResource() {
        return R.layout.activity_setting;
    }

    @Override
    protected void initView(View view) {
        _fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = _fragmentManager.beginTransaction();
        transaction.add(R.id.content, new BleInfoFragment(), BleInfoFragment.class.getSimpleName()).commit();
    }
}
