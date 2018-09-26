package com.vispect.android.vispect_g2_app.ui.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.PointF;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.vispect.android.vispect_g2_app.R;
import com.vispect.android.vispect_g2_app.app.AppConfig;
import com.vispect.android.vispect_g2_app.app.AppContext;
import com.vispect.android.vispect_g2_app.controller.DeviceHelper;
import com.vispect.android.vispect_g2_app.interf.Callback;
import com.vispect.android.vispect_g2_app.interf.GetListCallback;
import com.vispect.android.vispect_g2_app.interf.SurfaceViewWidthChangeCallback;
import com.vispect.android.vispect_g2_app.ui.widget.CustomView;
import com.vispect.android.vispect_g2_app.ui.widget.DialogHelp;
import com.vispect.android.vispect_g2_app.ui.widget.DrawAdas;
import com.vispect.android.vispect_g2_app.ui.widget.DrawDotView;
import com.vispect.android.vispect_g2_app.ui.widget.XuHCSurfaceView;
import com.vispect.android.vispect_g2_app.utils.XuLog;
import com.vispect.android.vispect_g2_app.utils.XuString;
import com.vispect.android.vispect_g2_app.utils.XuToast;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import bean.DrawShape;
import butterknife.Bind;
import butterknife.OnClick;
import interf.CorrectingCallback;
import interf.OnGetPicLisetener;
import interf.RealViewCallback;
import interf.ResultListner;
import interf.SideAlarmEvent;

import static android.view.View.GONE;
import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;


/**
 * 实时视频播放入口
 */
public class VMainActivity extends BaseActivity {

    private final static String TAG = "VMainActivity";
    public static boolean runing = false;
    public static boolean drawable = false;
    @Bind(R.id.tv_horizontal)
    TextView tvHorizontal;
    @Bind(R.id.tv_vertical)
    TextView tvVertical;
    @Bind(R.id.surfaceView)
    XuHCSurfaceView surfaceView;
    @Bind(R.id.drawAdas)
    DrawAdas drawAdas;
    @Bind(R.id.tv_speed)
    TextView tvSpeed;
    @Bind(R.id.tv_progress)
    TextView tvProgress;
    @Bind(R.id.ly_progress)
    LinearLayout lyProgress;
    @Bind(R.id.fl_top)
    FrameLayout flTop;
    @Bind(R.id.blue_center)
    TextView blueCenter;
    DrawShape sensor = null;
    ArrayList<DrawShape> adas = null;
    float c_x = 0;
    @Bind(R.id.customView)
    CustomView customView;
    int i = 1;
    private Handler mhandler = new Handler();
    Runnable check_network = new Runnable() {
        @Override
        public void run() {
            try {
                if (!runing) {
                    XuLog.e(TAG, "runing is false");
                    return;
                }
                if (!AppContext.getInstance().getWificontroller().isWifiEnabled()) {
                    XuLog.e(TAG, "检测到WIFI已关闭 退出实时路况");
                    finish();
                }
                if (!AppContext.getInstance().getWificontroller().getWifiInfo().getSSID().equals("\"" + AppConfig.getInstance(AppContext.getInstance()).getWifi_name() + "\"") && runing) {
                    XuLog.d(TAG, "检查到连接的WIFI发生变化，正在进行重连    getSSID():" + AppContext.getInstance().getWificontroller().getWifiInfo().getSSID() + "   getWifi_name():" + "\"" + AppConfig.getInstance(AppContext.getInstance()).getWifi_name() + "\"");
                    XuLog.e("Unnamed-0", "居然判断进来了：" + runing);
                    if (runing) {
//                        XuToast.show(VMainActivity.this, STR(R.string.reconnect_wifi) + "   SSID:" + "\"" + AppConfig.getInstance(AppContext.getInstance()).getWifi_name() + "\"");
                        AppContext.getInstance().getWificontroller().reconnect();
                    }
                }
                XuLog.d(TAG, "getSSID():" + AppContext.getInstance().getWificontroller().getWifiInfo().getSSID() + "   getWifi_name():" + "\"" + AppConfig.getInstance(AppContext.getInstance()).getWifi_name() + "\"");
            } catch (Exception e) {
                e.printStackTrace();
                XuLog.d(TAG, "check wifi connect has a exception" + e.getMessage());
            } finally {
                mhandler.postDelayed(check_network, 5000);
            }
        }
    };
    private volatile int progress = 0;
    private PowerManager.WakeLock sCpuWakeLock;
    private int horizontal = -1;
    private float xCenter = -1;
    private float yCenter = -1;
    private String speed = "";
    private int index = 0;

    private List<Point> cameras = new ArrayList<>();
    private Point _currentCamera = new Point();
    /**
     * 此处接收从画线/框信息
     */
    private boolean isNewSensor = true;
    //是否画ADAS信息
    private boolean canGetAdas = true;
    //    Runnable showSpeedRunnable = new Runnable() {
//        @Override
//        public void run() {
//            try {
//                if (runing) {
//                    XuLog.e(TAG, "设置车速：");
//                    if (!speed.isEmpty() && canGetAdas) {
//                        if (speed.equals("255KM/H") || speed.equals("255km/h")) {
//                            speed = "- -";
//                        }
//                        tvSpeed.setText(speed);
//                    }
//                    mhandler.postDelayed(showSpeedRunnable, 2000);
//                }
//
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//    };
    private Runnable changeProgress = new Runnable() {
        @Override
        public void run() {
            if (progress != 100) {
                if (lyProgress != null && lyProgress.getVisibility() == GONE) {
                    lyProgress.setVisibility(VISIBLE);
                }
                if (tvProgress != null) {
                    tvProgress.setText(progress + "");
                }
            } else if (progress == 100) {
                i = -1;
                lyProgress.setVisibility(GONE);
            }
        }
    };
    Runnable getCenterPoint = new Runnable() {
        @Override
        public void run() {
            AppContext.getInstance().getDeviceHelper().getCenterPoint(new CorrectingCallback() {
                @Override
                public void onGetProgress(int i) {
                    if (progress != i) {
                        progress = i;
                        mhandler.post(changeProgress);
                    } else {
                        mhandler.removeCallbacks(changeProgress);
                    }
                }

                @Override
                public void onGetOperationResult(boolean b) {
                    if (!b) {
                        tvProgress.setText(STR(R.string.calibration4_step9_context10));
                    }
                }

                @Override
                public void onGetCenterPoint(PointF center) {
                    xCenter = center.x;
                    yCenter = center.y;
                    XuLog.e(TAG, "中心点坐标：" + center.x + "   " + center.y);
                    if (xCenter != -1 && yCenter != -1) {
                        tvHorizontal.setTranslationX(drawAdas.getScaleWidth() * xCenter - tvHorizontal.getLeft());

                        blueCenter.setTranslationX(drawAdas.getScaleWidth() * xCenter - blueCenter.getLeft());
                        blueCenter.setTranslationY(drawAdas.getScaleHeight() * xCenter - blueCenter.getTop());
                    }

                }
            });
            mhandler.postDelayed(getCenterPoint, 2000);
        }
    };
    //是否已经初始化realView
    private boolean isShowConnecting = false;
    private RealViewCallback realViewCallback = new RealViewCallback() {
        @Override
        public void onGetPixData(byte[] buf, int size) {
//            if (isShowConnecting) {
//                isShowConnecting = false;
//                DialogHelp.getInstance().hideDialog();
//            }

            XuLog.e(TAG, "onGetPixData buf : " + buf + "size : " + size);
            if (surfaceView != null) {
                surfaceView.setOnePixData(buf, size);
            }
        }

        public void onGetADASRecInfo(ArrayList arrayList) {
            XuLog.e(TAG, "onGetADASRecInfo cameraType : " + _currentCamera.y);
            drawAdas.setDrawList(arrayList);
        }

        @Override
        public void onGetSideRecInfo(ArrayList arrayList) {
            if (arrayList == null || arrayList.size() < 0) {
                return;
            }
            ArrayList<SideAlarmEvent> sideAlarmEvents = arrayList;
            ArrayList<DrawShape> drawShapes = new ArrayList<>();
            for (SideAlarmEvent event : sideAlarmEvents) {
                DrawShape drawShape = new DrawShape();
                Point top = event.getLeftTop();
                Point btm = event.getRightBtm();
                drawShape.setType(1);
                drawShape.setColor(event.getColor());
                drawShape.setX0(top.x);
                drawShape.setY0(top.y);
                drawShape.setX1(btm.x);
                drawShape.setY1(btm.y);
                drawShapes.add(drawShape);
                XuLog.d(TAG, "getG2SideAlarm " + "top.x : " + top.x + " top.y : " + top.y + " btm.x : " + btm.x + " btm.y : " + btm.y);
            }
            drawAdas.setDrawList(drawShapes);
        }

        @Override
        public void onGetDSMAlarmInfo(final Map map) {
            XuLog.e(TAG, "onGetDSMAlarmInfo map.size() : " + map.size());
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    customView.setPointMap(map);
                }
            });
        }

        @Override
        public void onGetSensorData(float x, float y, float z) {
            c_x = x;
            x = x - AppConfig.getInstance(VMainActivity.this).get_CALIBRATION();
            if (x == 0) {
                XuLog.d(TAG, "加速度坐标系发现X轴等于零零");
            }
            XuLog.d(TAG, "角度X:" + x + "    角度y:" + y + "    角度z：" + z + "    x - y=" + (x - y));
            checksensor(x, y);
            float degreeX = 0;

            if (!isNewSensor) {
                degreeX = todegree(x, y);
            } else {
                degreeX = todegree(y, x);
            }

            float lasty = (float) ((640 / Math.sin((90 - degreeX) * Math.PI / 180)) * Math.sin(degreeX * Math.PI / 180));
            XuLog.e(TAG, "lasty:" + lasty + "       degreeX:" + degreeX + "    isNewSensor:" + isNewSensor);

            if (sensor == null) {
                sensor = new DrawShape();
            }

            sensor.setColor(Color.RED);
            sensor.setType(3);
            sensor.setDashed(false);
            sensor.setX0(0);
            sensor.setY0(360 + (lasty * -1));
            sensor.setX1(1280);
            sensor.setY1(360 + lasty);
            sensor.setTextStr("x:" + XuString.formatFloatNumber2(x) + "   y:" + XuString.formatFloatNumber2(y) + "   z:" + XuString.formatFloatNumber2(z));

            if (adas == null) {
                adas = new ArrayList<>();
            }
            XuLog.d(TAG, "加速度各值    X:" + formatFloatNumber(x) + "       y:" + formatFloatNumber(y) + "         z:" + formatFloatNumber(z));
        }

        @Override
        public void onGetSpeedData(String s) {
            speed = s;
            if (runing) {
                XuLog.e(TAG, "设置车速：");
                if (!speed.isEmpty() && canGetAdas) {
                    if (speed.equals("255KM/H") || speed.equals("255km/h")) {
                        speed = "- -";
                    }
                    tvSpeed.setText(speed);
                }
            }
            XuLog.e(TAG, "获得的车速：" + speed);
        }

        @Override
        public void onErro(int i) {
            XuLog.e(TAG, "realViewCallback onError" + i);
        }
    };

    private ResultListner resultListener = new ResultListner() {
        @Override
        public void onSuccess() {
            XuLog.e(TAG, "setUDPCamera Success");
        }

        @Override
        public void onFail(int i) {
            XuLog.e(TAG, "setUDPCamera Failed");
        }
    };

    /**
     * 当浮点型数据位数超过10位之后，数据变成科学计数法显示。用此方法可以使其正常显示。
     *
     * @param value
     * @return Sting
     */
    public static String formatFloatNumber(float value) {
        if (value != 0.00) {
            DecimalFormat df = new DecimalFormat("##0.000000000000");
            return df.format(value);
        } else {
            return "0.00";
        }

    }

    @Override
    public int getContentResource() {
        return R.layout.activity_real_view;
    }

    @SuppressLint("HandlerLeak")
    @Override
    protected void initView(View view) {
        cameras = AppConfig.getCameras();

        if (cameras == null || cameras.size() <= 0) {
            finish();
        }
        _currentCamera = cameras.get(0);

        AppContext.getInstance().getDeviceHelper().setUDPCamera(resultListener, _currentCamera.x, _currentCamera.y);

        AppContext.getInstance().getDeviceHelper().initRealView(realViewCallback);


        DialogHelp.getInstance().connectDialog(this, "Connecting");
        isShowConnecting = true;

        //查询中心点
        if (_currentCamera.y == 1) mhandler.post(getCenterPoint);
        //画标定框等信息
        drawAlarmInfo();

        DisplayMetrics displayMetrics = getDisplayMetrics();
        int screenWidth = displayMetrics.widthPixels;//得到宽度
        int screenHeight = displayMetrics.heightPixels;//得到高度

        XuLog.e("screenWidth=" + screenWidth + "         screenHeight=" + screenHeight);

        if (blueCenter != null) {
            LayoutParams params = blueCenter.getLayoutParams();
            params.width = screenWidth / 4;
            blueCenter.setLayoutParams(params);
        }

        drawAdas.setScreenWidth(screenWidth);
        drawAdas.setScreenHeight(screenHeight);

        //画面宽度发生变化时重新调整画ADAS的显示区域
        surfaceView.setWidthChangeCallback(new SurfaceViewWidthChangeCallback() {
            @Override
            public void onWidthChange() {
                resetDisplay();
            }
        });

        surfaceView.setListener(new OnGetPicLisetener() {
            @Override
            public void onGetPic() {
                //收到关键帧隐藏加载中
                try {
                    XuLog.e(TAG, "收到关键帧了隐藏加载中");
                    DialogHelp.getInstance().hideDialog();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        // 根据系统版本决定是否开启沉浸式
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // 透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//			// 透明导航栏
//            //  	getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
//            //隐藏导航栏  SYSTEM_UI_FLAG_HIDE_NAVIGATION : 点击屏幕时依然会弹出
//            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }

        if (AppContext.getInstance().getDeviceHelper().getCommunicationType() == 0) {
            //五秒后检查下WIFI是否继续保持连接
            XuLog.e(TAG, "进入去检测wifi是否连接正常：" + AppContext.getInstance().getDeviceHelper().getCommunicationType());
            mhandler.postDelayed(check_network, 5000);
        }
        if (AppContext.getInstance().getDeviceHelper().getDeviceType() == 3 && !AppContext.getInstance().getDeviceHelper().DrawADASable()) {
            canGetAdas = false;
        }
    }

    public void drawAlarmInfo() {
        XuLog.e(TAG, "改变了镜头,镜头ID:" + _currentCamera.x + "镜头类型:" + _currentCamera.y);
        hiddenViews();
        switch (_currentCamera.y) {
            case 1:
                showAdasInfo();
                break;
            case 4:
            case 5:
            case 6:
            case 7:
                drawSideInfo(_currentCamera.x);
                break;
        }
    }

    private void hiddenViews() {
        tvHorizontal.setVisibility(INVISIBLE);
        tvVertical.setVisibility(INVISIBLE);
        blueCenter.setVisibility(INVISIBLE);
//        customView.setVisibility(INVISIBLE);
        customView.clearView();
    }

    private void showAdasInfo() {
        tvHorizontal.setVisibility(VISIBLE);
        tvVertical.setVisibility(VISIBLE);
        blueCenter.setVisibility(VISIBLE);
    }

    private void drawSideInfo(final int cameraID) {
//        customView.setVisibility(VISIBLE);

        DeviceHelper.getPointOfArea(new GetListCallback() {
            @Override
            public void onGetListSuccess(final List list) {
                if (list != null && list.size() > 0) {
                    XuLog.e(TAG, "获取到四个点的范围 list.size : " + list.size());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            customView.setPointList(list);
                        }
                    });

                }
            }

            @Override
            public void onGetListFailed() {
                XuLog.e(TAG, "getPointOfArea Failed");
            }
        }, cameraID);
        if (horizontal == -1) {
            mhandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    DeviceHelper.getHorizontal(new Callback<Integer>() {
                        @Override
                        public void callback(final Integer value) {
                            XuLog.e(TAG, "获取到水平线 i :" + value + "Camera ID : " + cameraID);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    customView.addRedLine(value);
                                }
                            });
                        }
                    }, cameraID);
                }
            }, 500);
        }
    }

    private synchronized void checksensor(float x, float y) {
        XuLog.e(TAG, "获取的   x:" + x + "    y:" + y);
        isNewSensor = !(x > y);
    }

    /**
     * 计算倾斜角度
     */
    private float todegree(float ax, float ay) {
        double g = Math.sqrt(ax * ax + ay * ay);
        double cos = ay / g;
        if (cos > 1) {
            cos = 1;
        } else if (cos < -1) {
            cos = -1;
        }
        double rad = Math.acos(cos);
        if (ax < 0) {
            rad = 2 * Math.PI - rad;
        }
        float degree = (float) (rad * 180 / Math.PI);
        if (Float.isNaN(degree)) {
            return 0;
        } else {
            return degree;
        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        XuLog.e(TAG, "按下了一个键:" + keyCode);
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getRepeatCount() == 0) {
            XuLog.e(TAG, "返回键被按下了！" + keyCode);
            AppContext.getInstance().getDeviceHelper().closeDeviceRealViewMode();
            AppContext.getInstance().getDeviceHelper().closeDeviceCalibrationMode();
            AppContext.getInstance().getDeviceHelper().closeSenser();
            finish();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onPause() {
        super.onPause();
        drawAdas.stopVideo();
    }


    @Override
    protected void onStart() {
        super.onStart();
        //设置屏幕常亮
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        sCpuWakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK, "okTag");
        sCpuWakeLock.acquire();
        //表示正在看实时路况
        runing = true;
    }

    @Override
    protected void onStop() {
        super.onStop();

        XuLog.e("Unnamed-0", "老子设置了变量了");
        if (sCpuWakeLock != null) {
            sCpuWakeLock.release();
            sCpuWakeLock = null;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        drawAdas.PlayVideo();
    }

    @Override
    protected void onDestroy() {
        runing = false;
        mhandler.removeCallbacksAndMessages(null);

//        UIHelper.dismissLoading();
        AppContext.getInstance().getDeviceHelper().closeDeviceRealViewMode();
        AppContext.getInstance().getDeviceHelper().closeDeviceCalibrationMode();

        AppContext.getInstance().getDeviceHelper().closeADASEventListener();

        super.onDestroy();
    }

    private void resetDisplay() {
        int screenWidth = surfaceView.getWidth();//得到宽度
        int screenHeight = surfaceView.getHeight();//得到高度
        drawAdas.setScreenWidth(screenWidth);
        drawAdas.setScreenHeight(screenHeight);
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
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        SYSTEM_UI_FLAG_FULLSCREEN表示全屏的意思，也就是会将状态栏隐藏
        //设置系统UI元素的可见性
    }


    @OnClick({R.id.surfaceView, R.id.tv_speed, R.id.img_back_main, R.id.img_change})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.surfaceView:
                flTop.setVisibility(flTop.getVisibility() == VISIBLE ? GONE : VISIBLE);
                break;
            case R.id.tv_speed:
                AppContext.getInstance().getDeviceHelper().setADASInfoshowDetailsType((i % 2));
                i++;
                break;
            case R.id.img_back_main:
                finish();
                break;
            case R.id.img_change:
                changeCamera();
                break;
        }
    }

    //切换摄像头
    private void changeCamera() {
        if (cameras.size() < 2) {
            XuToast.show(this, "just have one or less camera");
            return;
        }

        ++index;
        _currentCamera = cameras.get(index % cameras.size());

        DialogHelp.getInstance().connectDialog(VMainActivity.this, "Switching");
        mhandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                DialogHelp.getInstance().hideDialog();
            }
        }, 2000);
        DeviceHelper.setUDPCamera(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        drawAlarmInfo();
                    }
                });
            }
        }, _currentCamera.x, _currentCamera.y);
    }
}
