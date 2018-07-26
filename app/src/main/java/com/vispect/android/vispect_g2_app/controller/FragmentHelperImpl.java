package com.vispect.android.vispect_g2_app.controller;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;

import butterknife.ButterKnife;

/**
 * FragmentHelper实现类
 * Created by xu on 2016/03/11.
 */
class FragmentHelperImpl extends FragmentHelper {

	private Fragment mFragment;

	FragmentHelperImpl(Fragment fragment) {
		this.mFragment = fragment;
	}

	@Override
	public void onAttach(Context context) {

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {

	}

	@Override
	public void onCreateView(@Nullable View view) {
		ButterKnife.bind(mFragment, view);
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {

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
	public void onDestroyView() {
		//取消网络访问
	}

	@Override
	public void onDestroy() {
		ButterKnife.unbind(mFragment);
//	        RefWatcher refWatcher = AppContext.getInstance().getRefWatcher();
//	        refWatcher.watch(this);
	}

	@Override
	public void onDetach() {
		mFragment = null;
	}
}
