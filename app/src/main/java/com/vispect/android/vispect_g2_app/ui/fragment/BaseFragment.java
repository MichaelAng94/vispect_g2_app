package com.vispect.android.vispect_g2_app.ui.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import com.vispect.android.vispect_g2_app.R;
import com.vispect.android.vispect_g2_app.controller.FragmentHelper;
import com.vispect.android.vispect_g2_app.interf.OnSoftKeyboardChangeListener;
import com.vispect.android.vispect_g2_app.interf.ProgressController;
import com.vispect.android.vispect_g2_app.utils.XuToast;

import java.io.IOException;

import butterknife.ButterKnife;


public abstract class BaseFragment extends Fragment implements ProgressController, View.OnLayoutChangeListener {

    private FragmentHelper mFragmentHelper;
    private boolean isFirstVisible = true;
    private boolean isFirstInvisible = true;

    //��Ļ�߶�
    private int screenHeight = 0;
    //����̵������ռ�߶ȷ�ֵ
    private int keyHeight = 0;
    private OnInputStatusChange listener;
    /**
     * 监听软键盘的弹出和高度
     */
    private boolean lastvisible = false;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        getFragmentHelper().onAttach(context);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentHelper().onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (savedInstanceState != null) {
            String FRAGMENTS_TAG = "Android:support:fragments";
            savedInstanceState.remove(FRAGMENTS_TAG);
        }
        View view = isCreatedContent() ? inflater.inflate(getContentResource(), container, false) : null;

        getFragmentHelper().onCreateView(view);

        //��ʼ��
        try {
            initView(view);
        } catch (IOException e) {
            e.printStackTrace();
        }
        ButterKnife.bind(this, view);
        initData();
        return view;
    }

    @Override
    public void onLayoutChange(View v, int left, int top, int right,
                               int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {

        //old�Ǹı�ǰ���������������ֵ��û��old���Ǹı����������������ֵ


        //������ΪֻҪ�ؼ���Activity�����Ƶĸ߶ȳ�����1/3��Ļ�ߣ�����Ϊ����̵���
        if (listener == null) {
            return;
        }
        if (oldBottom != 0 && bottom != 0 && (oldBottom - bottom > keyHeight)) {
            listener.InputStatusChange(true);
        } else if (oldBottom != 0 && bottom != 0 && (bottom - oldBottom > keyHeight)) {
            listener.InputStatusChange(false);
        }

    }

    /**
     * @return �Ƿ�ʵ���������ļ�
     */
    public boolean isCreatedContent() {
        return getContentResource() != 0;
    }

    /**
     * Ҫ��ʾ�Ĳ���id
     *
     * @return ����0���ʾ����ʾ����
     */
    @LayoutRes
    public abstract int getContentResource();

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getFragmentHelper().onActivityCreated(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        getFragmentHelper().onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        getFragmentHelper().onResume();
        if (getUserVisibleHint()) {
            onUserVisible(isFirstVisible);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        getFragmentHelper().onPause();
        if (getUserVisibleHint()) {
            onUserInVisible(isFirstInvisible);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        getFragmentHelper().onStop();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
        getFragmentHelper().onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getFragmentHelper().onDestroy();
//        AppContext.getInstance().getRefWatcher().watch(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        getFragmentHelper().onDetach();
        mFragmentHelper = null;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        onVisibleChange(hidden);
        if (hidden) {
            onUserVisible(isFirstVisible);
            if (isFirstVisible) {
                isFirstVisible = false;
            }
        } else {
            onUserInVisible(isFirstInvisible);
            if (isFirstInvisible) {
                isFirstInvisible = false;
            }
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        onVisibleChange(isVisibleToUser);
        if (isVisibleToUser) {
            onUserVisible(isFirstVisible);
            if (isFirstVisible) {
                isFirstVisible = false;
            }
        } else {
            onUserInVisible(isFirstInvisible);
            if (isFirstInvisible) {
                isFirstInvisible = false;
            }
        }

    }

    /**
     * fragment���ɼ�ʱ���ã��л�������onPause��
     */
    public void onUserInVisible(boolean isFirstInvisible) {
    }

    /**
     * fragment�ɼ�ʱ���ã��л���������onResume��
     */
    public void onUserVisible(boolean isFirstVisible) {
    }

    /**
     * fragment是否可见
     */
    public void onVisibleChange(boolean visible) {
    }

    public FragmentHelper getFragmentHelper() {
        if (null == mFragmentHelper) {
            mFragmentHelper = FragmentHelper.create(this);
        }
        return mFragmentHelper;
    }

    @Override
    public void hideProgress() {
        Activity activity = getActivity();
        if (activity instanceof ProgressController) {
            ((ProgressController) activity).hideProgress();
        }
    }


//    public ProgressDialog getProgressDialog() {
//        if (null == mProgressDialog) {
//            mProgressDialog = new ProgressDialog(getActivity());
//            mProgressDialog.setCanceledOnTouchOutside(false);
//        }
//        return mProgressDialog;
//    }
//    ProgressDialog mProgressDialog;
//
//    public ProgressDialog toshowProgress(CharSequence msg) {
//        ProgressDialog progressDialog = getProgressDialog();
//        progressDialog.setMessage(msg);
//        progressDialog.show();
//
//        return progressDialog;
//    }

    @Override
    public Dialog showProgress() {
        Activity activity = getActivity();
        return activity instanceof ProgressController ? ((ProgressController) activity).showProgress() : null;
    }

    public Dialog showProgress(boolean cancelable) {
        Activity activity = getActivity();
        if (activity instanceof ProgressController) {
            Dialog progressdialog = ((ProgressController) activity).showProgress();
            progressdialog.setCancelable(cancelable);
            return progressdialog;
        }
        return null;
    }

    public Dialog showProgress(boolean cancelable, String content) {
        Activity activity = getActivity();
        if (activity instanceof ProgressController) {
            ((ProgressController) activity).hideProgress();
            Dialog progressdialog = ((ProgressController) activity).showProgress(content);
            progressdialog.setCancelable(cancelable);
            return progressdialog;
        }
        return null;
    }

    @Override
    public Dialog showProgress(@StringRes int resId) {
        Activity activity = getActivity();
        boolean ttt = activity instanceof ProgressController;
        return activity instanceof ProgressController ? ((ProgressController) activity).showProgress(resId) : null;
    }

    @Override
    public Dialog showProgress(CharSequence text) {
        Activity activity = getActivity();
        return activity instanceof ProgressController ? ((ProgressController) activity).showProgress(text) : null;
    }

    public void showToast(CharSequence msg) {
        XuToast.show(getActivity(), msg);
    }

    public void showToast(@StringRes int resId) {
        XuToast.show(getActivity(), resId);
    }

    public String STR(int stringid) {
        String str = "null";
        try {
            str = getActivity().getResources().getString(stringid);
        } catch (Exception e) {
            // TODO: handle exception
            return "";
        }
        return str;
    }

    public String STR(@StringRes int stringRes, String value) {
        return String.format(getResources().getString(stringRes), value);
    }

    public String STR(@StringRes int stringRes, int value) {
        return String.format(getResources().getString(stringRes), value);
    }

    /**
     * ��ʼ��View,��initData()�����
     *
     * @param view {@link BaseFragment#getContentResource()}ʵ����������view
     */
    protected abstract void initView(View view) throws IOException;

    /**
     * ��ʼ������
     */
    protected void initData() {
    }

    public OnInputStatusChange getListener() {
        return listener;
    }

    public void setListener(View rootview, OnInputStatusChange listener) {
        this.listener = listener;
        //��ȡ��Ļ�߶�
        screenHeight = getActivity().getWindowManager().getDefaultDisplay().getHeight();
        //��ֵ����Ϊ��Ļ�߶ȵ�1/3
        keyHeight = screenHeight / 3;

        //���layout��С�����ı������
        rootview.addOnLayoutChangeListener(this);

    }

    public void observeSoftKeyboard(final OnSoftKeyboardChangeListener listener) {
        final View decorView = getActivity().getWindow().getDecorView();
        decorView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            int previousKeyboardHeight = -1;

            @Override
            public void onGlobalLayout() {
                Rect rect = new Rect();
                decorView.getWindowVisibleDisplayFrame(rect);
                int displayHeight = rect.bottom - rect.top;
                int height = decorView.getHeight();
                int keyboardHeight = height - displayHeight;
                if (previousKeyboardHeight != keyboardHeight) {
                    boolean hide = (double) displayHeight / height > 0.8;
                    if (lastvisible != !hide) {
                        lastvisible = !hide;
                        listener.onSoftKeyBoardChange(keyboardHeight, !hide);
                    }
                }
                previousKeyboardHeight = height;

            }
        });
    }


    protected void finish() {
        int entryCount = getFragmentManager().getBackStackEntryCount();
        if (entryCount > 1) {
            getFragmentManager().popBackStackImmediate();
        }
    }

    protected void pushToFragment(@NonNull Fragment fragment) {
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.content, fragment, BaseFragment.class.getSimpleName())
                .addToBackStack(BaseFragment.class.getSimpleName()).commit();
    }

    public interface OnInputStatusChange {
        public void InputStatusChange(boolean status);
    }
}