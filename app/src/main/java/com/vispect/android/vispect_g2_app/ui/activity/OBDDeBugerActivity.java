package com.vispect.android.vispect_g2_app.ui.activity;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Environment;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;


import com.vispect.android.vispect_g2_app.R;
import com.vispect.android.vispect_g2_app.adapter.OBDVualeAdapter;
import com.vispect.android.vispect_g2_app.app.AppContext;
import com.vispect.android.vispect_g2_app.bean.ChangeOBDValue;
import com.vispect.android.vispect_g2_app.bean.OBDValue;
import com.vispect.android.vispect_g2_app.ui.widget.RefreshableView;
import com.vispect.android.vispect_g2_app.utils.CodeUtil;
import com.vispect.android.vispect_g2_app.utils.XuLog;
import com.vispect.android.vispect_g2_app.utils.XuToast;
import com.vispect.android.vispect_g2_app.utils.XuView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import interf.OBDDebugCallback;
import interf.OnRefreshListener;

/**
 * OBD Debug （手动破解）
 * <p>
 * Created by xu on 2017/12/20.
 */
public class OBDDeBugerActivity extends BaseActivity {
    private final static String TAG = "OBDDeBugerActivity";


    @Bind(R.id.list_obdvalue)
    RefreshableView list_obdvalue;
    @Bind(R.id.iv_titlebar_title)
    TextView title;

    @Bind(R.id.tv_page)
    TextView tv_page;


    @Bind(R.id.ll_mask)
    LinearLayout ll_mask;
    TextView iv_titlebar_mune;

    ArrayList<OBDValue> obdvalues = new ArrayList<OBDValue>();
    ArrayList<OBDValue> obdValueBuff = new ArrayList<OBDValue>();
    volatile ArrayList<ChangeOBDValue> changeValueBuff = new ArrayList<ChangeOBDValue>();

    private int STARTINDEX = 0;
    private int COUNT = 12;
    private int ENDINDEX = 5;
    private int refreshType = -1;
    private Boolean NEEDCHANGE = false;

    OBDVualeAdapter adapter;
    Handler mHandler = new Handler();

    Handler displayHandler = new Handler();

    private boolean showHexForOBDValue = false;


    Runnable refreshDisplay = new Runnable() {
        @Override
        public void run() {
            AppContext.getInstance().getCachedThreadPool().execute(new Runnable() {
                @Override
                public void run() {
                    ENDINDEX = COUNT + STARTINDEX;

                    if (ENDINDEX > obdValueBuff.size()) {
                        ENDINDEX = obdValueBuff.size();
                    }
                    if (STARTINDEX < 0) {
                        STARTINDEX = 0;
                    }
                    if (ENDINDEX < 1 || STARTINDEX >= ENDINDEX) {
                        XuLog.d(TAG, "没有更多数据了");
                        if (list_obdvalue != null) {
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    switch (refreshType) {
                                        case 0:
                                            list_obdvalue.hideHeaderView();
                                            XuToast.show(OBDDeBugerActivity.this, STR(R.string.cube_views_load_not_date));
                                            break;
                                        case 1:
                                            list_obdvalue.hideFooterView();
                                            XuToast.show(OBDDeBugerActivity.this, STR(R.string.cube_views_load_more_loaded_empty));
                                            STARTINDEX = STARTINDEX - COUNT;
                                            break;
                                    }
                                }
                            });


                        }
                    } else {
                        obdvalues.clear();
                        ArrayList<OBDValue> list_temp = (ArrayList<OBDValue>) obdValueBuff.clone();
//						obdvalues.addAll(obdvalues.subList(STARTINDEX, ENDINDEX));
                        for (int i = STARTINDEX; i < ENDINDEX; i++) {
                            OBDValue temp = new OBDValue();
                            temp.setId(list_temp.get(i).getId());
                            temp.setValue(list_temp.get(i).getValue());
                            temp.setChanged(list_temp.get(i).isChanged());
                            temp.setStart(list_temp.get(i).getStart());
                            temp.setBlue(list_temp.get(i).getBlue());
                            obdvalues.add(temp);
                        }
                        list_temp.clear();
//				XuLog.e(TAG,"STARTINDEX:"+STARTINDEX+"    ENDINDEX:"+ENDINDEX);
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                switch (refreshType) {
                                    case 0:
                                        list_obdvalue.hideHeaderView();
                                        break;
                                    case 1:
                                        list_obdvalue.hideFooterView();
                                        break;
                                }
                            }
                        });


                    }
                    displayHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            //adapter.refreshData(obdvalues);

                            adapter.refreshChaData(changeValueBuff, obdvalues, NEEDCHANGE);
                            NEEDCHANGE = false;
                            changeValueBuff.clear();
                            changeValueBuff = new ArrayList<>();
                        }
                    });

                    if (obdValueBuff != null && obdValueBuff.size() > 0) {
                        int sum_page = obdValueBuff.size() / COUNT;
                        if ((obdValueBuff.size() % COUNT) > 0) {
                            sum_page++;
                        }
                        int nowpage = 1;
                        if (obdValueBuff.size() < COUNT) {
                            nowpage = 1;
                            sum_page = 1;
                        } else {
                            nowpage = ENDINDEX / COUNT;
                            if ((ENDINDEX % COUNT) > 0) {
                                nowpage++;
                            }
                        }
                        final int n = nowpage;
                        final int s = sum_page;
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                tv_page.setText(n + "/" + s);
                            }
                        });

                    }
                    mHandler.postDelayed(this, 300);

                }
            });


        }
    };

    Runnable re = new Runnable() {
        @Override
        public void run() {
            obdvalues.clear();
            for (int i = 0; i < 14; i++) {
                OBDValue temp = new OBDValue();
                temp.setId("FFFFFFFF");
                StringBuffer sb = new StringBuffer();
                for (int ai = 0; ai < 8; ai++) {
                    if (ai == 8 - 1) {
                        sb.append(CodeUtil.toBitStr((int) (Math.random() * 100) + 1));
                    } else {
                        sb.append(CodeUtil.toBitStr((int) (Math.random() * 100) + 1) + " ");
                    }
                }
                temp.setValue(sb.toString());
                obdvalues.add(temp);
            }

            adapter.refreshData(obdvalues);
            mHandler.postDelayed(this, 100);

        }
    };

    Runnable refreshdata = new Runnable() {
        @Override
        public void run() {
            adapter.refreshData(obdvalues);
        }
    };


    /**
     * 获取byte 转化为二进制
     */
    private OBDDebugCallback obddebugCallback = new OBDDebugCallback() {
        @Override
        public void onGetCANData(final byte[] id, final byte[] value) {
            AppContext.getInstance()
                    .getCachedThreadPool().execute(new Runnable() {
                @Override
                public void run() {
                    OBDValue temp = new OBDValue();
                    temp.setId(CodeUtil.bytesToString(id));
                    StringBuffer sb = new StringBuffer();
                    if (!showHexForOBDValue) {
                        for (int ai = 0; ai < 8; ai++) {
                            if (ai == 8 - 1) {
                                sb.append(CodeUtil.toBitStr(CodeUtil.oneByteToInt(value[ai])));
                            } else {
                                sb.append(CodeUtil.toBitStr(CodeUtil.oneByteToInt(value[ai])) + " ");
                            }
                        }
                    } else {
                        for (int ai = 0; ai < 8; ai++) {
                            if (ai == 8 - 1) {
                                sb.append(String.format("%02x", value[ai]));
                            } else {
                                sb.append(String.format("%02x", value[ai]) + " ");
                            }
                        }
                    }
                    temp.setValue(sb.toString());
                    try {
                        refreshbuff(temp);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            });
        }

        @Override
        public void onGetResult(final boolean b) {
            mHandler.post(new Runnable() {

                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    XuToast.show(AppContext.getInstance(), System.currentTimeMillis() + "  收到OBD操作的结果" + b);
                }
            });
        }
    };

    @Override
    public int getContentResource() {
        // TODO Auto-generated method stub
        return R.layout.activity_obd_debuger;
    }

    boolean toSetBlue = false;

    @Override
    protected void initView(View view) {
        // TODO Auto-generated method stub
        title.setText("OBD DeBuger");
        title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshType = 0;
                STARTINDEX = 0;
                obdValueBuff.clear();
            }
        });
        iv_titlebar_mune = findViewById(R.id.tv_title_save);
        iv_titlebar_mune.setText("set");
        XuView.setViewVisible(iv_titlebar_mune, true);
        iv_titlebar_mune.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupWindow();
            }
        });
        initListView();

        AppContext.getInstance().getCachedThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                AppContext.getInstance().getDeviceHelper().openCANListener(obddebugCallback);
            }
        });

      // AppContext.getInstance().getCachedThreadPool().execute(setOBDvalue);


    }


    Runnable setOBDvalue = new Runnable() {
        @Override
        public void run() {
            String path = Environment.getExternalStorageDirectory() + File.separator + "autoCarack2018-04-09-16-37-32.txt";
            File file = new File(path);
            if (file.exists()) {
                try {
                    FileReader fr = new FileReader(file.getPath());
                    BufferedReader br = new BufferedReader(fr);
                    String line = "";
                    String[] arrs = null;
                    while ((line = br.readLine()) != null) {
                        XuLog.e(TAG, "读出来的一行数据：" + line);
                        //检查读出一行数据里面是否是分割符
                        String temp_str = new String(line);
                        if (temp_str.equals("get ALL finish") || temp_str.equals("light changge finish") || temp_str.equals("get not change finish")) {
                            XuLog.e(TAG, "读出来的是分割符：" + temp_str);
                            continue;
                        }

                        byte[] data = CodeUtil.stringToByte(line);
                        obddebugCallback.onGetCANData(CodeUtil.subBytes(data, 0, 4), CodeUtil.subBytes(data, 4, 8));
                        Thread.sleep(20);
                    }
                    br.close();
                    fr.close();
//					mHandler.postDelayed(new Runnable() {
//						@Override
//						public void run() {
//							XuToast.show(AppContext.getInstance(),"数据放完了，重新放一次");
//							AppContext.getInstance().getCachedThreadPool().execute(setOBDvalue);
//						}
//					}, 5000);


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }
    };

    PopupWindow popupWindow = null;

    void showPopupWindow() {
        View contentView = LayoutInflater.from(OBDDeBugerActivity.this).inflate(R.layout.popupwindow_drivingvideos_menu, null);
        if (popupWindow != null) {
            popupWindow.dismiss();
        }
        popupWindow = new PopupWindow(contentView, ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, true);

        popupWindow.setTouchable(true);
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                ll_mask.setVisibility(View.GONE);
            }
        });
        popupWindow.setTouchInterceptor(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;

            }
        });

        TextView tv_popuwindow_batchmanagement = (TextView) contentView.findViewById(R.id.tv_popuwindow_batchmanagement);
        tv_popuwindow_batchmanagement.setText(STR(R.string.obd_debug_hex_exchange));
        contentView.findViewById(R.id.ll_popuwindow_batchmanagement).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (showHexForOBDValue) {
                    showHexForOBDValue = false;
                    COUNT = 12;
                } else {
                    showHexForOBDValue = true;
                    COUNT = 22;
                }
                refreshType = 0;
                STARTINDEX = 0;
                obdValueBuff.clear();
                popupWindow.dismiss();
            }
        });


        TextView tv_popuwindow_lock = (TextView) contentView.findViewById(R.id.tv_popuwindow_lock);
        tv_popuwindow_lock.setText(STR(R.string.obd_debug_start_filter));
        contentView.findViewById(R.id.ll_popuwindow_lock).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                toSetBlue = true;
                XuLog.e(TAG, "toSetBlue = true");
                popupWindow.dismiss();
            }
        });

        TextView tv_popuwindow_download = (TextView) contentView.findViewById(R.id.tv_popuwindow_download);
        tv_popuwindow_download.setText(STR(R.string.obd_debug_stop_filter));
        contentView.findViewById(R.id.ll_popuwindow_savetophone).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                toSetBlue = false;
                XuLog.e(TAG, "toSetBlue = false");
                popupWindow.dismiss();
            }
        });


        contentView.findViewById(R.id.ll_popuwindow_cancel).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                popupWindow.dismiss();
            }
        });


        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        ll_mask.setVisibility(View.VISIBLE);
        popupWindow.showAtLocation(ll_mask, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
    }


    private void initListView() {
        adapter = new OBDVualeAdapter(this, obdvalues);
        list_obdvalue.setAdapter(adapter);

        list_obdvalue.setOnRefreshListener(new OnRefreshListener() {


            @Override
            public void onDownPullRefresh() {
                NEEDCHANGE = true;
                refreshType = 0;
                STARTINDEX = STARTINDEX - COUNT;
            }

            @Override
            public void onLoadingMore() {
                NEEDCHANGE = true;
                refreshType = 1;
                STARTINDEX = STARTINDEX + COUNT;
            }
        });
        displayHandler.postDelayed(refreshDisplay, 1500);
    }


    @OnClick(R.id.iv_titlebar_back)
    void back() {
        adapter.setisfrist();
        finish();
    }

    /**
     * 更新数据，存在
     */
    private synchronized void refreshbuff(final OBDValue newvalue) {
        AppContext.getInstance().getCachedThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                //更新obd数据的缓存  存在则修改  不存在则添加
                boolean exists = false;
                int index = -1;

                for (OBDValue temp : (ArrayList<OBDValue>) obdValueBuff.clone()) {
                    if (temp.getId().equals(newvalue.getId())) {
                        exists = true;
                        index = obdValueBuff.indexOf(temp);
                    }
                }
                if (exists) {
                    if (!obdValueBuff.get(index).getValue().equals(newvalue.getValue())) {
                        char[] oldobdchar = obdValueBuff.get(index).getValue().toCharArray();
                        char[] newobdchar = newvalue.getValue().toCharArray();

                        List l = new ArrayList();

                        for (int i = 0; i < oldobdchar.length; i++) {
                            if (oldobdchar[i] != newobdchar[i]) {
                                l.add(i);
                            }
                        }

                        ChangeOBDValue changeOBDValue = new ChangeOBDValue();
                        changeOBDValue.setId(obdValueBuff.get(index).getId());
                        changeOBDValue.setChangebit(l);

                        changeValueBuff.add(changeOBDValue);

                        ArrayList<Integer> span_start = new ArrayList<Integer>();
                        byte[] str_bytes_now = newvalue.getValue().getBytes();
                        byte[] str_bytes_last = obdValueBuff.get(index).getValue().getBytes();

                        for (int i = 0; i < str_bytes_now.length; i++) {
                            if (str_bytes_now[i] != str_bytes_last[i]) {
                                span_start.add(i);
                            }
                        }


                        if (toSetBlue) {
                            obdValueBuff.get(index).setValue(newvalue.getValue());
                            obdValueBuff.get(index).setChanged(true);
                            obdValueBuff.get(index).setStart(span_start);
                            obdValueBuff.get(index).setBlue((ArrayList<Integer>) span_start.clone());
                        } else {
                            obdValueBuff.get(index).setValue(newvalue.getValue());
                            obdValueBuff.get(index).setChanged(true);
                            obdValueBuff.get(index).setStart(span_start);
                        }
                    }
//					else{
//						obdValueBuff.get(index).setValue(newvalue.getValue());
//					}

                } else {
                    obdValueBuff.add(newvalue);
                }

//		mHandler.post(new Runnable() {
//			@Override
//			public void run() {
//				if(obdValueBuff != null && obdValueBuff.size() > 0){
//					int sum_page = obdValueBuff.size()/COUNT;
//					if( (obdValueBuff.size() % COUNT) > 0){
//						sum_page ++;
//					}
//					int nowpage = 1;
//					if(obdValueBuff.size() < COUNT){
//						nowpage = 1;
//						sum_page = 1;
//					}else{
//						nowpage = ENDINDEX/COUNT;
//						if((ENDINDEX%COUNT)> 0){
//							nowpage = ENDINDEX/COUNT++;
//						}
//					}
//					tv_page.setText(nowpage + "/" + sum_page);
//				}
//			}
//		});
            }
        });

    }


    @Override
    protected void onStart() {
        super.onStart();
        XuLog.d(TAG, "onStart()");
    }

    @Override
    protected void onStop() {
        super.onStop();
        XuLog.d(TAG, "onStop()");
    }

    @Override
    protected void onDestroy() {
        displayHandler.removeCallbacksAndMessages(null);
        mHandler.removeCallbacksAndMessages(null);
        AppContext.getInstance().getDeviceHelper().closeCANListener(new OBDDebugCallback() {
            @Override
            public void onGetCANData(byte[] bytes, byte[] bytes1) {

            }

            @Override
            public void onGetResult(boolean b) {

            }
        });
        super.onDestroy();
    }
}
