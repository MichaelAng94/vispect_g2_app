package com.vispect.android.vispect_g2_app.controller;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.vispect.android.vispect_g2_app.app.AppManager;

import butterknife.ButterKnife;


/**
 * ActivityHelper实现类
 * Created by xu on 2016/03/11.
 */
public class ActivityHelperImpl extends ActivityHelper {
	private Activity mActivity;

	ActivityHelperImpl(Activity activity) {
		this.mActivity = activity;
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		ButterKnife.bind(mActivity);
		AppManager.getInstance().addActivity(mActivity);
	}

	@Override
	public void onStart() {
	}

	@Override
	public void onResume() {
	}

	@Override
	public void onPause() {
	}

	@Override
	public void onStop() {
	}

	@Override
	public void onDestroy() {
		AppManager.getInstance().removeActivity(mActivity);
		ButterKnife.unbind(mActivity);
		mActivity = null;
	}

	@Override
	public void onRestart() {
	}

	@Override
	public void onBackPressed() {
		AppManager.getInstance().finishActivity(mActivity);
	}
}
