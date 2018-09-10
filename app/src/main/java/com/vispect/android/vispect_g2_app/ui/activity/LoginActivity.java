package com.vispect.android.vispect_g2_app.ui.activity;

import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;

import com.vispect.android.vispect_g2_app.R;
import com.vispect.android.vispect_g2_app.app.AppConfig;
import com.vispect.android.vispect_g2_app.app.AppContext;
import com.vispect.android.vispect_g2_app.bean.ResultData;
import com.vispect.android.vispect_g2_app.bean.UserInfo;
import com.vispect.android.vispect_g2_app.controller.AppApi;
import com.vispect.android.vispect_g2_app.controller.UIHelper;
import com.vispect.android.vispect_g2_app.interf.ResultCallback;
import com.vispect.android.vispect_g2_app.utils.DoubleClickExit;
import com.vispect.android.vispect_g2_app.utils.XuLog;
import com.vispect.android.vispect_g2_app.utils.XuString;
import com.vispect.android.vispect_g2_app.utils.XuToast;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Request;

public class LoginActivity extends BaseActivity {

    @Bind(R.id.et_login_userid)
    EditText etLoginUserid;
    @Bind(R.id.et_login_password)
    EditText etLoginPassword;
    private int backTime = 0; //点击返回键的次数
    private DoubleClickExit doubleclick = new DoubleClickExit(this);
    private String TAG = "LoginActivity";
    private Handler mhandler = new Handler();

    @Override
    public int getContentResource() {
        return R.layout.activity_login;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            XuLog.e(TAG, "捕获到一次按下返回按钮的事件:" + keyCode);
            if (doubleclick.onKeyDown(keyCode, event)) {
                return super.onKeyDown(keyCode, event);
            } else {
                return false;
            }
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    @Override
    protected void initView(View view) {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }

    @OnClick({R.id.btn_sign, R.id.iv_register})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_sign:
//                UIHelper.startActivity(LoginActivity.this, MainActivity.class);
//                AppManager.getInstance().finishActivity();
                String email = etLoginUserid.getText().toString();
                String password = etLoginPassword.getText().toString();
                if (XuString.isEmpty(email) || XuString.isEmpty(password)) {
                    XuToast.show(LoginActivity.this, STR(R.string.login_login_has_null));
                    return;
                }
                if (!XuString.checkPassword(password)) {
                    XuToast.show(LoginActivity.this, STR(R.string.login_login_password_format_error));
                    return;
                }
                if (email.length() < 3 || email.length() > 64) {
                    XuToast.show(LoginActivity.this, STR(R.string.login_login_name_format_error));
                    return;
                }
                login(email,password);
                break;
            case R.id.iv_register:
                UIHelper.startActivity(this, RegisterActivity.class);
                break;
        }
    }


    private void login(String email, String password) {
//        showProgress();
        AppApi.login(email, password, 0, "", "", new ResultCallback<ResultData<UserInfo>>() {
            @Override
            public void onFailure(Request request, Exception e) {
                XuToast.show(LoginActivity.this, STR(R.string.fail));
//                mhandler.post(hideProgressRunnable);
//                XuLog.d(TAG, "登录发生错误：" + e.getMessage());
//                XuToast.show(LoginActivity.this, STR(R.string.login_login_fail) + e.getMessage());
            }

            @Override
            public void onResponse(ResultData<UserInfo> response) {
//                mhandler.post(hideProgressRunnable);
                if (response.getResult() == 0) {
                    UserInfo user = response.getMsg();
                    XuLog.d(TAG, "用户ID：" + user.getId() + "用户名：" + user.getName());
                    AppConfig.getInstance(LoginActivity.this).setUserId(user.getId() + "");
                    AppContext.getInstance().setUser(user);
                    XuToast.show(LoginActivity.this, STR(R.string.success));
                    AppConfig.getInstance(LoginActivity.this).setFirstStart(false);
                    finish();
                    UIHelper.startActivity(LoginActivity.this, MainActivity.class);
                }
                else {
                    int resultCode = response.getResult();
                    XuLog.e(TAG,"res"+resultCode);
                    switch (resultCode) {
                        case 100:
                            mhandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    XuToast.show(LoginActivity.this,  STR(R.string.have_not_userid));
                                    //账号错误
                                }
                            });
                            break;
                        case 101:
                            mhandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    XuToast.show(LoginActivity.this,  STR(R.string.have_not_userid));
                                    //账号错误
                                }
                            });
                            break;
                        case 103:
                            mhandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    XuToast.show(LoginActivity.this,  STR(R.string.password_error));
                                    //密码错误
                                }
                            });
                            break;
                    }
                }
            }
        });
    }
}
