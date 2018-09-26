package com.vispect.android.vispect_g2_app.ui.activity;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.View;

import com.vispect.android.vispect_g2_app.R;
import com.vispect.android.vispect_g2_app.ui.fragment.SettingsFragment;

import butterknife.OnClick;

/**
 * 设置
 */

public class SettingsActivity extends BaseActivity {

    private FragmentManager _fragmentManager;

    @Override
    public int getContentResource() {
        return R.layout.activity_setting;
    }

    @Override
    protected void initView(View view) {
        _fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = _fragmentManager.beginTransaction();
        transaction.add(R.id.content, new
                SettingsFragment(), SettingsFragment.class.getSimpleName()).commit();
    }

    private void handleBack() {
        int entryCount = _fragmentManager.getBackStackEntryCount();
        if (entryCount > 0) {
            _fragmentManager.popBackStackImmediate();
        } else {
            finish();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            handleBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
