package com.vispect.android.vispect_g2_app.ui.activity;

import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.widget.EditText;

import com.vispect.android.vispect_g2_app.R;
import com.vispect.android.vispect_g2_app.app.AppManager;
import com.vispect.android.vispect_g2_app.bean.ResultData;
import com.vispect.android.vispect_g2_app.controller.AppApi;
import com.vispect.android.vispect_g2_app.controller.UIHelper;
import com.vispect.android.vispect_g2_app.interf.ResultCallback;
import com.vispect.android.vispect_g2_app.utils.XuLog;
import com.vispect.android.vispect_g2_app.utils.XuString;
import com.vispect.android.vispect_g2_app.utils.XuToast;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Request;

/**
 * 注册
 */
public class RegisterActivity extends BaseActivity {

    private final static String TAG = "RegisterActivity";

    Handler mhandler = new Handler();
    @Bind(R.id.tv_email)
    EditText tvEmail;
    @Bind(R.id.tv_password)
    EditText tvPassword;

    @Override
    public int getContentResource() {
        return R.layout.activity_register;
    }

    @Override
    protected void initView(View view) {

    }

    private void toRegister(String name, String password) {
        if (XuString.isEmpty(name) || XuString.isEmpty(password)) {
            XuToast.show(RegisterActivity.this, STR(R.string.login_login_has_null));
            return;
        }
        if (!XuString.checkPassword(password)) {
            XuToast.show(RegisterActivity.this, STR(R.string.login_login_password_format_error));
            return;
        }
        if (name.length() < 3 || name.length() > 64) {
            mhandler.post(new Runnable() {
                @Override
                public void run() {
                    //ErrorDialog(1);
                }
            });
            XuToast.show(RegisterActivity.this, STR(R.string.login_login_name_format_error));
            return;
        }
        //showProgress();
        AppApi.register(name, password, new ResultCallback<ResultData<String>>() {
            @Override
            public void onFailure(Request request, Exception e) {
                //    mhandler.post(hideProgressRunnable);
                XuLog.d(TAG, "注册发生错误：" + e.getMessage());
                XuToast.show(RegisterActivity.this, STR(R.string.register_register_fail) + e.getMessage());
            }

            @Override
            public void onResponse(ResultData<String> response) {
                //  mhandler.post(hideProgressRunnable);
                if (response.getResult() == 0) {
                    XuToast.show(RegisterActivity.this, STR(R.string.register_register_success));
                    //    UIHelper.showLogin(RegisterActivity.this);
                    UIHelper.startActivity(RegisterActivity.this, LoginActivity.class);
                    AppManager.getInstance().finishActivity();
                } else {
                    int resultCode = response.getResult();
                    switch (resultCode) {
                        case 100:
                            mhandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    //  ErrorDialog(0);
                                    XuToast.show(RegisterActivity.this, STR(R.string.email_exited) );
                                }
                            });
                            break;
                        case 103:
                            mhandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    //   ErrorDialog(3);
                                    XuToast.show(RegisterActivity.this, STR(R.string.password_error) );
                                }
                            });
                            break;

                    }
                }
            }
        });

    }


    public DisplayMetrics getDisplayMetrics() {
        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        display.getMetrics(displayMetrics);
        return displayMetrics;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }

    @OnClick({R.id.btn_register, R.id.tv_tologin})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_register:
                toRegister(tvEmail.getText().toString().trim(),tvPassword.getText().toString().trim());
                break;
            case R.id.tv_tologin:
                UIHelper.startActivity(RegisterActivity.this, LoginActivity.class);
                break;
        }
    }
}
