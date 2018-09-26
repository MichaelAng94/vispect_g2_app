package com.vispect.android.vispect_g2_app.ui.activity;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.View;

import com.vispect.android.vispect_g2_app.R;
import com.vispect.android.vispect_g2_app.ui.fragment.CameraTypeFragment;
import com.vispect.android.vispect_g2_app.ui.fragment.SettingsFragment;

import butterknife.OnClick;

/**
 * 选择镜头类型
 */

public class CameraTypeActivity extends BaseActivity {

    private FragmentManager _fragmentManager;

    @Override
    public int getContentResource() {
        return R.layout.activity_setting;
    }

    @Override
    protected void initView(View view) {
        _fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = _fragmentManager.beginTransaction();
        transaction.add(R.id.content, new CameraTypeFragment(), CameraTypeFragment.class.getSimpleName()).commit();
    }

}
