package com.vispect.android.vispect_g2_app.ui.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.PointF;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.os.SystemClock;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.vispect.android.vispect_g2_app.R;
import com.vispect.android.vispect_g2_app.app.AppConfig;
import com.vispect.android.vispect_g2_app.app.AppContext;
import com.vispect.android.vispect_g2_app.bean.ARG;
import com.vispect.android.vispect_g2_app.interf.OnGetShortVideoCallback;
import com.vispect.android.vispect_g2_app.interf.SurfaceViewWidthChangeCallback;
import com.vispect.android.vispect_g2_app.ui.fragment.CalibrateFragment;
import com.vispect.android.vispect_g2_app.ui.fragment.CenterCalibrationFragment;
import com.vispect.android.vispect_g2_app.ui.widget.DialogHelp;
import com.vispect.android.vispect_g2_app.ui.widget.DrawAdas;
import com.vispect.android.vispect_g2_app.ui.widget.DrawDotView;
import com.vispect.android.vispect_g2_app.ui.widget.XuHCSurfaceView;
import com.vispect.android.vispect_g2_app.utils.CheckTime;
import com.vispect.android.vispect_g2_app.utils.CodeUtil;
import com.vispect.android.vispect_g2_app.utils.XuLog;
import com.vispect.android.vispect_g2_app.utils.XuString;
import com.vispect.android.vispect_g2_app.utils.XuToast;
import com.vispect.android.vispect_g2_app.utils.XuView;

import java.nio.ByteBuffer;
import java.text.DecimalFormat;
import java.util.ArrayList;

import bean.DrawShape;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import interf.CorrectingCallback;
import interf.GetG2CameraList;
import interf.GetHorizontalLine;
import interf.GetUDPcamera;
import interf.OnGetPicLisetener;
import interf.PointOfAreaListener;
import interf.RealViewCallback;
import interf.ResultListner;
import interf.SetPointOfArea;
import utils.Vispect_SDK_FileUtils;


//import com.coremedia.iso.boxes.Container;
//import com.googlecode.mp4parser.FileDataSourceImpl;
//import com.googlecode.mp4parser.authoring.Movie;
//import com.googlecode.mp4parser.authoring.builder.DefaultMp4Builder;
//import com.googlecode.mp4parser.authoring.tracks.h264.H264TrackImpl;

/**
 * 实时视频播放入口
 */
public class VMainActivity extends BaseActivity {

    private final static String TAG = "VMainActivity";

    @Bind(R.id.lin_click)
    LinearLayout linClick;
    @Bind(R.id.tv_readyClick)
    TextView tvReadyClick;
    @Bind(R.id.re_top)
    RelativeLayout reTop;
    @Bind(R.id.img_change)
    ImageView imgChange;
    private ArrayList<Point> cameras;

    @Bind(R.id.hsrufaceview)
    XuHCSurfaceView hsrufaceview;


    @Bind(R.id.drawadas)
    DrawAdas drawadas;


    @Bind(R.id.tv_speed)
    TextView tv_speed;


    @Bind(R.id.ll_center_x)
    LinearLayout ll_center_x;
    @Bind(R.id.iv_lin_center)
    LinearLayout iv_lin_center;


    @Bind(R.id.ll_lin_center_d)
    LinearLayout ll_lin_center_d;
    @Bind(R.id.ll_mask_tips)
    LinearLayout ll_mask_tips;


    ByteBuffer buffer = ByteBuffer.allocate(1024 * 1024);
    @Bind(R.id.tv_progress)
    TextView tvProgress;
    @Bind(R.id.lin_progress)
    LinearLayout linProgress;
    @Bind(R.id.drawdotView)
    DrawDotView drawdotView;
    private int count_i = 0;
    private boolean needGetShorVideo = false;
    private boolean onStart = false;
    private OnGetShortVideoCallback onGetShortVideoCallback;
    private volatile int progress = -1;
    private Boolean isCalibrate = false;
    public static boolean runing = false;

    public static boolean drawable = false;

    private int cameraID = 0;
    static boolean hasAdasDta = false;


    boolean toclear = false;
    DrawShape sensor = null;
    ArrayList<DrawShape> adas = null;
    float c_x = 0;


    private PowerManager.WakeLock sCpuWakeLock;
    private float centerpointX = -2;
    private float centerpointY = -2;


    private String speed = "";
    Handler mhandler = new Handler();
    Handler speedHandler = new Handler();

    private boolean canTranslation = false;


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
                        XuToast.show(VMainActivity.this, STR(R.string.reconnect_wifi) + "   SSID:" + "\"" + AppConfig.getInstance(AppContext.getInstance()).getWifi_name() + "\"");
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


    Runnable changeProgress = new Runnable() {
        @Override
        public void run() {
            if (progress != 100) {
                if (linProgress != null && linProgress.getVisibility() == View.GONE) {
                    linProgress.setVisibility(View.VISIBLE);
                }
                if (tvProgress != null) {
                    tvProgress.setText(progress + "");
                }

                mhandler.postDelayed(getProgress, 2000);
            } else {
                mhandler.post(getcenterponintag);
                i = -1;
                linProgress.setVisibility(View.GONE);
            }
        }
    };

    Runnable getcenterponint = new Runnable() {
        @Override
        public void run() {
            if (!canTranslation && centerpointX == -2 && centerpointY == -2) {
                AppContext.getInstance().getDeviceHelper().getCenterPoint(new CorrectingCallback() {
                    @Override
                    public void onGetProgress(int i) {

                    }

                    @Override
                    public void onGetOperationResult(boolean b) {

                    }

                    @Override
                    public void onGetCenterPoint(PointF pointF) {
                        centerpointX = pointF.x;
                        centerpointY = pointF.y;
                        XuLog.e(TAG, "中心点坐标：" + pointF.x + "   " + pointF.y);
                        canTranslation = true;
                    }


                });
                mhandler.postDelayed(getcenterponint, 5000);
            }
        }
    };

    Runnable getcenterponintag = new Runnable() {
        @Override
        public void run() {
            if (linProgress.getVisibility() == View.VISIBLE) {
                linProgress.setVisibility(View.GONE);
            }
            AppContext.getInstance().getDeviceHelper().getCenterPoint(new CorrectingCallback() {
                @Override
                public void onGetProgress(int i) {

                }

                @Override
                public void onGetOperationResult(boolean b) {

                }

                @Override
                public void onGetCenterPoint(final PointF pointF) {
                    centerpointX = pointF.x;
                    centerpointY = pointF.y;
                    XuLog.e(TAG, "中心点坐标：" + pointF.x + "   " + pointF.y);
                    canTranslation = true;
                }
            });
        }
    };

    Runnable showspeedRunnable = new Runnable() {
        @Override
        public void run() {
            try {
                if (runing) {
                    XuLog.e(TAG, "设置车速：");
                    if (!speed.isEmpty() && canGetAdas) {
                        if (speed.equals("255KM/H") || speed.equals("255km/h")) {
                            speed = "- -";
                        }
                        tv_speed.setText(speed);
                    }

                    if (canTranslation) {
                        //顺便显示出设备算好的中心点
                        XuLog.e(TAG, "获取到的中心点：" + centerpointX + "," + centerpointY + "原来的中心点：" + ll_center_x.getLeft() + "," + ll_lin_center_d.getTop());
                        canTranslation = false;
                        if (centerpointX != -1 && centerpointY != -1 && centerpointX != -2 && centerpointY != -2) {
                            if (!isShowBackgroud) {
                                ll_center_x.setTranslationX((float) (drawadas.getScaleWidth() * centerpointX) - ll_center_x.getLeft());
                            }
                            iv_lin_center.setTranslationX((float) (drawadas.getScaleWidth() * centerpointX) - ll_center_x.getLeft());
                            ll_lin_center_d.setTranslationY((float) (drawadas.getScaleHeight() * centerpointY) - ll_lin_center_d.getTop());
                            //       ll_lin_center_d.setTranslationX((float) (drawadas.getScaleWidth() * centerpointX) - ll_center_x.getLeft());
                            ll_lin_center_d.setTranslationX((float) (drawadas.getScaleWidth() * centerpointX) - ll_lin_center_d.getWidth() / 2);
                            XuLog.e(TAG, "ll_center_x的偏移：" + ll_center_x.getTranslationX() + "ll_lin_center_d的偏移：" + ll_lin_center_d.getTranslationY());
                        }

                    }
                    speedHandler.postDelayed(showspeedRunnable, 1000);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }


        }
    };


    Runnable getProgress = new Runnable() {
        @Override
        public void run() {

            try {
                AppContext.getInstance().getDeviceHelper().getCorrectingProgress(new CorrectingCallback() {
                    @Override
                    public void onGetProgress(int i) {

                        if (i == 100) {
                            progress = 100;
                        } else {
                            if (progress != i) {
                                progress = i;
                            }
                        }

                        mhandler.post(changeProgress);
                    }

                    @Override
                    public void onGetOperationResult(boolean b) {
                        if (!b) {
                            tvProgress.setText(STR(R.string.calibration4_step9_context10));
                        }
                    }

                    @Override
                    public void onGetCenterPoint(PointF pointF) {
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };


    public static boolean isShowBackgroud;
//    //----------------------------------------
//    BufferedOutputStream bo = null;
//    //----------------------------------------

    /**
     * 此处接收从画线/框信息
     */
    private boolean isNewSensor = true;
    private RealViewCallback realViewCallback = new RealViewCallback() {
        @Override
        public void onGetPixData(byte[] buf, int size) {
//            //----------------------------------------
//            try {
//                if(bo != null){
//                    bo.write(buf, 0, size);
//                }
//
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            //----------------------------------------
            if (hsrufaceview != null) {
                hsrufaceview.setOnePixData(buf, size);
            }

            if (needGetShorVideo) {
                //将拿到的H264数据和长度传为AsyncTask去硬解
                UpdateTask task = new UpdateTask();
                task.execute(buf, CodeUtil.intToByteArray(size));
            }


            ArrayList<ByteBuffer> bufflist = new ArrayList<ByteBuffer>();


        }


        public void onGetADASRecInfo(ArrayList arrayList) {
            hasAdasDta = true;
            if (!drawable || !canGetAdas) {
                return;
            }

            if (isShowBackgroud) {
                adas = arrayList;
            } else {
                drawadas.setDrawList(arrayList);
            }
//            //打印出adas信息
//            for (int i = 0; i < arrayList.size(); i++) {
//                DrawShape ds = arrayList.get(i);
//                int type = ds.getType();
//                float x0 = ds.getX0() * 1;
//                float y0 = ds.getY0() * 1;
//                float x1 = ds.getX1() * 1;
//                float y1 = ds.getY1() * 1;
//                float stroke_width = ds.getStroke_width();
//                int color = ds.getColor();
//                boolean isDashed = ds.isDashed();
//                String data = ds.getTextStr();
//                XuLog.e(TAG, "type:" + type + "  x0：" + x0+"   y0:"+y0+"  x1:"+x1+"  y1:"+y1+"  data:"+data);
//            }
            toclear = false;

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

            if (isShowBackgroud) {
                adas.add(sensor);
                drawadas.setDrawList(adas);
            }
            XuLog.d(TAG, "加速度各值    X:" + formatFloatNumber(x) + "       y:" + formatFloatNumber(y) + "         z:" + formatFloatNumber(z));
        }

        @Override
        public void onGetSpeedData(String s) {
            speed = s;
            XuLog.e(TAG, "获得的车速：" + speed);

        }

        @Override
        public void onErro(int i) {

        }
    };

    //是否画ADAS信息
    private boolean canGetAdas = true;

    @Override
    public int getContentResource() {
        return R.layout.activity_vmain;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

    }

    int i = 1;

    @OnClick(R.id.tv_speed)
    void changeShow() {
        AppContext.getInstance().getDeviceHelper().setADASInfoshowDetailsType((i % 2));
        i++;
    }


    @OnClick(R.id.tv_realview_got_it)
    void onclick(View v) {
        XuView.setViewVisible(ll_mask_tips, false);
    }

    @SuppressLint("HandlerLeak")
    @Override
    protected void initView(View view) {

        DialogHelp.getInstance().hideDialog();

        hsrufaceview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isCalibrate) {
                    if (reTop.getVisibility() == View.VISIBLE) {
                        reTop.setVisibility(View.GONE);
                    } else {
                        reTop.setVisibility(View.VISIBLE);
                    }
                }
            }
        });

        if (AppContext.getInstance().getCalibrateType() == 1) {
            tvReadyClick.setVisibility(View.VISIBLE);
            imgChange.setVisibility(View.GONE);
            ll_lin_center_d.setVisibility(View.GONE);
            ll_center_x.setVisibility(View.GONE);
            iv_lin_center.setVisibility(View.GONE);

            AppContext.getInstance().getDeviceHelper().getPointOfArea(new PointOfAreaListener() {
                @Override
                public void onSuccess(int i, ArrayList arrayList) {
                    XuLog.e(TAG, "获取到四个点的范围");
                    drawdotView.setVisibility(View.VISIBLE);
                    ArrayList<Point> points = arrayList;
                    drawdotView.addSnag(points, VMainActivity.this, false);
                }

                @Override
                public void onFail(int i) {

                }
            }, (byte) CalibrateFragment.selectCamera.x);

            AppContext.getInstance().getDeviceHelper().getHorizontal(new GetHorizontalLine() {
                @Override
                public void onSuccess(final int i) {
                    mhandler.post(new Runnable() {
                        @Override
                        public void run() {
                            XuLog.e(TAG, "获取到水平线");
                            drawdotView.addHorizontal(VMainActivity.this, i);
                        }
                    });
                }

                @Override
                public void onFail() {

                }
            }, (byte) CalibrateFragment.selectCamera.x);

        } else if (AppContext.getInstance().calibrateType != 0) {
            reTop.setVisibility(View.GONE);
        }

        AppContext.getInstance().getDeviceHelper().getG2CameraList(new GetG2CameraList() {
            @Override
            public void onSuccess(ArrayList arrayList) {
                cameras = arrayList;
            }

            @Override
            public void onFail() {

            }
        });

        AppContext.getInstance().getDeviceHelper().getUDPCamera(new GetUDPcamera() {
            @Override
            public void onSuccess(int i) {
                cameraID = i;
            }

            @Override
            public void onFail() {

            }
        });
        // AppContext.getInstance().getDeviceHelper().setADASInfoshowDetailsType(1);
        //显示加载中
//        UIHelper.showloading(this, R.string.loading, null);
        DialogHelp.getInstance().connectDialog(this, "Connecting");

        //视频播放参数设置
        isShowBackgroud = getIntent().getBooleanExtra(ARG.SHOW_BACKGROUND_CHECK, false);


        //查询中心点

        mhandler.postDelayed(getcenterponint, 1000);

        DisplayMetrics displayMetrics = getDisplayMetrics();
        final int screenWidth = displayMetrics.widthPixels;//得到宽度
        int screenHeight = displayMetrics.heightPixels;//得到高度

        XuLog.e("screenWidth=" + screenWidth + "         screenHeight=" + screenHeight);

        ll_lin_center_d.post(new Runnable() {
            @Override
            public void run() {
                ll_lin_center_d.setLayoutParams(new RelativeLayout.LayoutParams(screenWidth / 4, ll_lin_center_d.getHeight()));
            }
        });

        drawadas.setScreenWidth(screenWidth);
        drawadas.setScreenHeight(screenHeight);

        //画面宽度发生变化时重新调整画ADAS的显示区域
        hsrufaceview.setWidthChangeCallback(new SurfaceViewWidthChangeCallback() {
            @Override
            public void onWidthChange() {
                resetDisplay();
            }
        });
        if (AppContext.getInstance().calibrateType != 1) {
            XuView.setViewVisible(findViewById(R.id.iv_lin), isShowBackgroud);
            XuView.setViewVisible(findViewById(R.id.iv_lin_center), !isShowBackgroud);
            XuView.setViewVisible(findViewById(R.id.ll_lin_center_d), !isShowBackgroud);
        }

        hsrufaceview.setListener(new OnGetPicLisetener() {
            @Override
            public void onGetPic() {
                //收到关键帧隐藏加载中
                try {
//                    UIHelper.dismissLoading();
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
        initLive();


        XuView.setViewVisible(ll_mask_tips, CenterCalibrationFragment.isrunning);


        if (AppContext.getInstance().getDeviceHelper().getCommunicationType() == 0) {
            //五秒后检查下WIFI是否继续保持连接
            XuLog.e(TAG, "进入去检测wifi是否连接正常：" + AppContext.getInstance().getDeviceHelper().getCommunicationType());
            mhandler.postDelayed(check_network, 5000);
        }
        if (AppContext.getInstance().getDeviceHelper().getDeviceType() == 3 && !AppContext.getInstance().getDeviceHelper().DrawADASable()) {
            canGetAdas = false;
        }


//        mhandler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                getShortVideo(callback);
//            }
//        },20000);

        mhandler.post(getProgress);
    }

    @OnClick({R.id.tv_back, R.id.tv_removeall, R.id.tv_sure, R.id.tv_cancle})
    public void onViewClick(View view) {
        switch (view.getId()) {
            case R.id.tv_back:
                drawdotView.back();
                break;
            case R.id.tv_removeall:
                drawdotView.removeAll();
                break;
            case R.id.tv_sure:
                mhandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        DialogHelp.getInstance().hideDialog();
                    }
                }, 2000);
                AppContext.getInstance().getDeviceHelper().closeDeviceRealViewMode();
                DialogHelp.getInstance().connectDialog(VMainActivity.this, "");

                AppContext.getInstance().getDeviceHelper().setPointOfArea(new SetPointOfArea() {
                    @Override
                    public void onSuccess() {
                        DialogHelp.getInstance().hideDialog();
                        XuLog.e(TAG, "写入四点范围成功");
                    }

                    @Override
                    public void onFail(int i) {
                        XuLog.e("onFail");
                    }
                }, CalibrateFragment.selectCamera.x, drawdotView.getSnag(VMainActivity.this));

                AppContext.getInstance().getDeviceHelper().setHorizontal(new ResultListner() {
                    @Override
                    public void onSuccess() {
                        XuLog.e(TAG, "写入水平线高度成功");
                        XuToast.show(VMainActivity.this,"success");
                        finish();
                    }

                    @Override
                    public void onFail(int i) {

                    }
                }, CalibrateFragment.selectCamera.x, drawdotView.getLinY(VMainActivity.this));
                break;
            case R.id.tv_cancle:
                drawdotView.hideSnag();
                drawdotView.setIsCalibrate(false);
                findViewById(R.id.lin_click).setVisibility(View.GONE);
                findViewById(R.id.tv_readyClick).setVisibility(View.VISIBLE);
                break;
        }
    }

    private OnGetShortVideoCallback callback = new OnGetShortVideoCallback() {
        @Override
        public void onFail() {
            XuLog.e(TAG, "获取短视频失败");

        }

        @Override
        public void onGet(byte[] buff, int length) {
            XuLog.e(TAG, "获取短视频成功  length：" + length);
            Vispect_SDK_FileUtils.getFile(buff, AppContext.getInstance().getDeviceHelper().getDownloadDir(), "test.h264");
        }
    };

    private void setSimulateClick(View view, float x, float y) {
        long downTime = SystemClock.uptimeMillis();
        final MotionEvent downEvent = MotionEvent.obtain(downTime, downTime,
                MotionEvent.ACTION_DOWN, x, y, 0);
        downTime += 1000;
        final MotionEvent upEvent = MotionEvent.obtain(downTime, downTime,
                MotionEvent.ACTION_UP, x, y, 0);
        view.onTouchEvent(downEvent);
        view.onTouchEvent(upEvent);
        downEvent.recycle();
        upEvent.recycle();

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
            finish();
            AppContext.getInstance().getDeviceHelper().closeDeviceRealViewMode();
            AppContext.getInstance().getDeviceHelper().closeDeviceCalibrationMode();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

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
    protected void onPause() {
        super.onPause();
        drawadas.stopVideo();
    }


    private void initLive() {
        //表示正在看实时路况
        runing = true;
        //移除掉前面准备发送关闭WIFI的指令
        CheckTime.toStop();
//        try {
//            bo = new BufferedOutputStream(new FileOutputStream("/sdcard/video.264"));
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
        if (isShowBackgroud) {
            AppContext.getInstance().getDeviceHelper().initCalibration(realViewCallback);
        } else {
            AppContext.getInstance().getDeviceHelper().initRealView(realViewCallback);
        }

        speedHandler.post(showspeedRunnable);
    }

    @Override
    protected void onStart() {
        super.onStart();
        //设置屏幕常亮
        PowerManager pm = (PowerManager) this
                .getSystemService(Context.POWER_SERVICE);
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
        drawadas.PlayVideo();
    }

    @Override
    protected void onDestroy() {
        runing = false;
        drawable = false;
        mhandler.removeCallbacksAndMessages(null);
        speedHandler.removeCallbacksAndMessages(null);

//        UIHelper.dismissLoading();
        AppContext.getInstance().getDeviceHelper().closeDeviceRealViewMode();
        AppContext.getInstance().getDeviceHelper().closeDeviceCalibrationMode();

        super.onDestroy();
    }

    private void resetDisplay() {
        int screenWidth = hsrufaceview.getWidth();//得到宽度
        int screenHeight = hsrufaceview.getHeight();//得到高度
        drawadas.setScreenWidth(screenWidth);
        drawadas.setScreenHeight(screenHeight);
    }

    public DisplayMetrics getDisplayMetrics() {
        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        display.getMetrics(displayMetrics);
        return displayMetrics;
    }

    public void saveVideo(byte[] buf) {
        //判断是不是要保存这个流
        if (needGetShorVideo) {
            //先判断是不是关键帧
            if (buf[0] == 0x00 && buf[1] == 0x00 && buf[2] == 0x00
                    && buf[3] == 0x01 && buf[4] == 0x67) {
                count_i++;
                if (!onStart) {
                    onStart = true;
                    XuLog.e(TAG, "开始录制视频");
                }
            }
            //如果可以开始录就开始录
            if (onStart) {
                buffer.put(buf);

            }
            //判断是不是存够了
            if (count_i >= 3) {
                onStart = false;
                needGetShorVideo = false;
                count_i = 0;
                if (onGetShortVideoCallback != null) {
                    byte[] result = buffer.array();
                    onGetShortVideoCallback.onGet(result, result.length);
                    XuLog.e(TAG, "结束录制视频");
                    buffer.clear();
                }
            }
        }
    }

    private void getShortVideo(OnGetShortVideoCallback onGetShortVideoCallback) {
        needGetShorVideo = true;
        this.onGetShortVideoCallback = onGetShortVideoCallback;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        SYSTEM_UI_FLAG_FULLSCREEN表示全屏的意思，也就是会将状态栏隐藏
        //设置系统UI元素的可见性
        ButterKnife.bind(this);
    }

    @OnClick(R.id.tv_readyClick)
    public void onViewClicked() {
        if (findViewById(R.id.tv_readyClick) != null) {
            findViewById(R.id.tv_readyClick).setVisibility(View.GONE);
        }
        if (findViewById(R.id.lin_click) != null) {
            findViewById(R.id.lin_click).setVisibility(View.VISIBLE);
        }

        drawdotView.showSnag();
        drawdotView.setIsCalibrate(true);
    }

    public int getID() {
        if (cameras != null) {
            for (Point p : cameras) {
                if (p.x == cameraID + 1) {
                    return ++cameraID;
                }
            }
            cameraID = 0;
            return 0;
        }
        return 0;
    }


    @OnClick({R.id.img_back_main, R.id.img_change})
    public void onViewClickedd(View view) {
        switch (view.getId()) {
            case R.id.img_back_main:
                finish();
                AppContext.getInstance().getDeviceHelper().closeDeviceRealViewMode();
                break;
            case R.id.img_change:
                DialogHelp.getInstance().connectDialog(VMainActivity.this, "切换中");
                mhandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        DialogHelp.getInstance().hideDialog();
                    }
                }, 2000);
                AppContext.getInstance().getDeviceHelper().setUDPCamera(new ResultListner() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onFail(int i) {

                    }
                }, getID());
                break;
        }
    }

//    private void toMP4(File file){
//        FileOutputStream fos = null;
//        try {
//            //这里传入的file是指H264格式对应的文件
//            H264TrackImpl h264Track = new H264TrackImpl(new FileDataSourceImpl(file));
//            Movie m = new Movie();
//            m.addTrack(h264Track);
//            Container out = new DefaultMp4Builder().build(m);
//            //这里传入的就是要保存的mp4文件目录
//            fos = new FileOutputStream(AppContext.getInstance().getDeviceHelper().getDownloadDir()+ File.separator +"123.mp4");
//            FileChannel fc = fos.getChannel();
//            out.writeContainer(fc);
//        } catch (Exception e) {
//            XuLog.e(TAG,"出错了 "+e.getMessage());
//            e.printStackTrace();
//        } finally {
//            try {
//                if (null != fos)
//                    fos.close();
//                XuLog.e(TAG,"结束转换了");
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//    }

    private class UpdateTask extends AsyncTask<byte[], Void, String> {
        @Override
        protected void onPreExecute() {

            super.onPreExecute();
        }

        @Override
        protected String doInBackground(byte[]... params) {
            //存视频
            try {
                saveVideo(params[0]);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }
    }

    @OnClick(R.id.tv_readyClick)
    public void onViewClicke() {
        findViewById(R.id.tv_readyClick).setVisibility(View.GONE);
        findViewById(R.id.lin_click).setVisibility(View.VISIBLE);
        drawdotView.setVisibility(View.VISIBLE);
    }

}
