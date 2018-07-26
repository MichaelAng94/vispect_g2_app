package com.vispect.android.vispect_g2_app.ui.activity;

import android.content.Intent;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

import com.vispect.android.vispect_g2_app.R;
import com.vispect.android.vispect_g2_app.app.AppConfig;
import com.vispect.android.vispect_g2_app.controller.UIHelper;
import com.vispect.android.vispect_g2_app.utils.DoubleClickExit;
import com.vispect.android.vispect_g2_app.utils.XuLog;

/**
 * APP启动页
 * Created by mo on 2018/7/2.
 */

public class SplashActivity extends BaseActivity{

    private android.os.Handler mHandler = new android.os.Handler();
    private int mill = 1500; //启动页显示时间
    private String TAG = "SplashActivity";

    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            if (AppConfig.getInstance(SplashActivity.this).getFirstStart()){
                Intent intent=new Intent(SplashActivity.this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }else {
                UIHelper.startActivity(SplashActivity.this, MainActivity.class);
                finish();
            }
        }
//            AppConfig config = AppConfig.getInstance(SplashActivity.this);
//            boolean firstStart = config.getFirstStart();
//            String userid = config.getUserId();
//            if (firstStart) {
////				UIHelper.showFirstStart(SplashActivity.this);
//                Intent i=new Intent(SplashActivity.this, FirstStartActivity.class);
//                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                startActivity(i);
//                finish();
//            } else {
//                if( !XuString.isEmpty(userid)){
////						UIHelper.showMain(SplashActivity.this);
//                    Intent intent=new Intent(SplashActivity.this, LoginActivity.class);
//                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    startActivity(intent);
//
//                }else{
//                    Intent intent=new Intent(SplashActivity.this, LoginActivity.class);
//                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    startActivity(intent);
//                }
//                finish();
//            }
//        }
    };

    @Override
    public int getContentResource() {
        return R.layout.activity_splash;
    }

    @Override
    protected void initView(View view) {
        mHandler.postDelayed(mRunnable,mill);
    }
}
