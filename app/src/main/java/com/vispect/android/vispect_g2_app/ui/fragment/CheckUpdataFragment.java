package com.vispect.android.vispect_g2_app.ui.fragment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.vispect.android.vispect_g2_app.R;
import com.vispect.android.vispect_g2_app.adapter.SettingMenuAdapter;
import com.vispect.android.vispect_g2_app.app.AppConfig;
import com.vispect.android.vispect_g2_app.app.AppContext;
import com.vispect.android.vispect_g2_app.bean.ARG;
import com.vispect.android.vispect_g2_app.bean.ResultData;
import com.vispect.android.vispect_g2_app.bean.VispectUpdateFile;
import com.vispect.android.vispect_g2_app.controller.AppApi;
import com.vispect.android.vispect_g2_app.controller.UIHelper;
import com.vispect.android.vispect_g2_app.interf.ReqProgressCallBack;
import com.vispect.android.vispect_g2_app.interf.ResultCallback;
import com.vispect.android.vispect_g2_app.interf.XuTimeOutCallback;
import com.vispect.android.vispect_g2_app.ui.widget.DialogHelp;
import com.vispect.android.vispect_g2_app.ui.widget.NotconnectDialog;
import com.vispect.android.vispect_g2_app.utils.CheckTime;
import com.vispect.android.vispect_g2_app.utils.XuLog;
import com.vispect.android.vispect_g2_app.utils.XuNetWorkUtils;
import com.vispect.android.vispect_g2_app.utils.XuString;
import com.vispect.android.vispect_g2_app.utils.XuTimeOutUtil;
import com.vispect.android.vispect_g2_app.utils.XuToast;
import com.vispect.android.vispect_g2_app.utils.XuView;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import butterknife.Bind;
import butterknife.ButterKnife;
import interf.GetBleInfoVersionCallback;
import interf.OnWifiOpenListener;
import interf.ProgressCallback;
import interf.UpdateDeviceForSCallback;
import okhttp3.Request;

/**
 * Created by mo on 2018/7/20.
 */

public class CheckUpdataFragment extends BaseFragment {
    private static final long UPDATIMEOUT_S = 10000;
    private final static String TAG = "CheckUpdataFragment";
    public boolean canupdata = true;
    @Bind(R.id.list_updata)
    ListView listUpdata;

    private VispectUpdateFile newUpdaFile_firmware = null;
    private VispectUpdateFile newUpdaFile_system = null;
    private VispectUpdateFile newUpdaFile_software = null;
    private VispectUpdateFile newUpdaFile_app = null;
    private VispectUpdateFile newUpdaFile_s = null;
    private VispectUpdateFile newUpdaFile_obd = null;

    String device_software = null;
    String device_system = null;
    String device_software_s = null;
    int device_obd = -1;

    SettingMenuAdapter adapter;
    int updataposition = -1;
    int progress = 0;
    Handler mhandler = new Handler();
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            HashMap<String, String> map = ((HashMap<String, String>) msg.obj);
            String fileName = map.get("name");
            if (map.get("progress").equals(ARG.DOWNLOAD_DONE)) {
                //完成
                progress = 0;
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //  mhandler.removeCallbacks(check_timeout);
                        adapter.setIsupdataing(false);
                        adapter.setUpdatas(new int[0]);
                        adapter.setProgressValue(0);
                        adapter.refreshData();
                        canupdata = true;
                        AppContext.getInstance().setUpdateing_s(false);
                    }
                });
            } else {
                //下载中
                progress = Integer.valueOf(map.get("progress"));
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.setIsupdataing(true);
                        adapter.setUpdatas(new int[]{updataposition});
                        adapter.setProgressValue(progress);
                        adapter.refreshData();
                    }
                });
            }
        }
    };

    private boolean canshowlongtime = false;
    private Runnable mRunnable_toas_waiitingtoolong = new Runnable() {
        @Override
        public void run() {
            if (canshowlongtime) {
                   showProgress(true, STR(R.string.wifi_waiting_too_long) + AppConfig.getInstance(getActivity()).getWifi_name() + STR(R.string.wifi_waiting_too_long2) + AppConfig.getInstance(getActivity()).getWifi_paw());
            }
        }
    };

    Runnable nofile = new Runnable() {
        @Override
        public void run() {
            XuToast.show(getActivity(), STR(R.string.update_not_find_file));
        }
    };

    @Override
    public int getContentResource() {
        return R.layout.fragment_checkupdata;
    }

    @Override
    protected void initView(View view) throws IOException {
        String[] names = {STR(R.string.update_software_system), "OBD"};
        adapter = new SettingMenuAdapter(getActivity(), names);
        adapter.setIslastpage(true);
        listUpdata.setAdapter(adapter);
        listUpdata.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // TODO Auto-generated method stub
                tomenu(position);
            }
        });
        XuView.setViewVisible(getActivity().findViewById(R.id.btn_setting_login), false);
        XuView.setViewVisible(getActivity().findViewById(R.id.btn_setting_signout), false);
        AppContext.getInstance().getCachedThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                if (!XuNetWorkUtils.isNetworkAvailable() || !XuNetWorkUtils.ping()) {
                    mhandler.post(new Runnable() {
                        @Override
                        public void run() {
                            XuToast.show(getActivity(), STR(R.string.not_net_work));
                        }
                    });

                }
            }
        });

        checkupdate();
    }

    void tomenu(int position) {
        String value = adapter.getMune_name()[position];
        switch (position) {
            case 0:
                if (!AppContext.getInstance().getDeviceHelper().isConnectedDevice()) {
                    XuToast.show(getActivity(), STR(R.string.road_live_notconnect));
                    return;
                }

                    if (value.indexOf(";") != -1) {
                        if (adapter.getisupdataing() || !canupdata) {
                            XuToast.show(getActivity(), STR(R.string.update_has_update));
                            return;
                        }
                        updataposition = 0;
                        String[] values = value.split(";");
                        switch (Integer.parseInt(values[1])) {
                            case 0:
                                break;
                            case 1:
                                showDialogForUpdateFail(STR(R.string.to_download_updatefile), ARG.UPDATE_TYPE_SOFTWARE_V);
                                break;
                            case 2:
                                //先移除掉可能存在的关闭/断开WIFI的指令
                                CheckTime.removeHnadler();
                                DialogHelp.getInstance().connectDialog(getActivity(),"");
                                timeoutUtil.startCheck(ARG.OPEN_WIFI_TIMEOUT);
                                mhandler.postDelayed(mRunnable_toas_waiitingtoolong, 20000);
                                canshowlongtime = true;
                                AppContext.getInstance().getDownThreadExecutor().execute(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            final String path = Environment.getExternalStorageDirectory().getPath();
                                            final File f = new File(path + "/"+newUpdaFile_software.getFilename());
                                            if (f.exists()) {
                                                switch (AppContext.getInstance().getDeviceHelper().getCommunicationType()){
                                                    case 0:
                                                        updateDeviceAPKOld(path);
                                                        break;
                                                    case 1:
                                                        updateDeviceAPK(path);
                                                        break;
                                                }
                                            } else {
                                                mhandler.post(nowfilemRunnable);
                                                canupdata = true;
                                                canshowlongtime = false;
                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                            adapter.setIsupdataing(false);
                                            canupdata = true;
                                            mhandler.post(mRunnable);
                                            canshowlongtime = false;
                                        }

                                    }

                                });
                                break;


                        }

                    }else{
                        XuToast.show(getActivity(),STR(R.string.latest));
                        canshowlongtime = false;
                    }
                break;


            case 1:
                if (value.indexOf(";") != -1) {
                    if (adapter.getisupdataing() || !canupdata) {
                        XuToast.show(getActivity(), STR(R.string.update_has_update));
                        return;
                    }
                    updataposition = 1;
                    String[] values = value.split(";");
                    switch (Integer.parseInt(values[1])) {
                        case 0:
                            break;
                        case 1:
                            showDialogForUpdateFail(STR(R.string.to_download_updatefile), ARG.UPDATE_TYPE_OBD);
                            break;
                        case 2:
                            //先移除掉可能存在的关闭/断开WIFI的指令
                            CheckTime.removeHnadler();
                            DialogHelp.getInstance().connectDialog(getActivity(),"");
                            timeoutUtil.startCheck(ARG.OPEN_WIFI_TIMEOUT);
                            mhandler.postDelayed(mRunnable_toas_waiitingtoolong, 20000);
                            canshowlongtime = true;
                            AppContext.getInstance().getDownThreadExecutor().execute(new Runnable() {
                                @Override
                                public void run() {
                                    try {

                                        final File f = new File(Environment.getExternalStorageDirectory().getPath() + File.separator + newUpdaFile_obd.getFilename());
                                        if (f.exists()) {
                                            switch (AppContext.getInstance().getDeviceHelper().getCommunicationType()){
                                                case 0:
                                                    updateOBDOld(f.getPath());
                                                    break;
                                                case 1:
                                                    updateOBD(f.getPath());
                                                    break;
                                            }
                                        } else {
                                            mhandler.post(nowfilemRunnable);
                                            canupdata = true;
                                            canshowlongtime = false;
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        adapter.setIsupdataing(false);
                                        canupdata = true;
                                        mhandler.post(mRunnable);
                                        canshowlongtime = false;
                                    }

                                }

                            });

                            AppContext.getInstance().getCachedThreadPool().execute(new Runnable() {
                                @Override
                                public void run() {
                                    AppContext.getInstance().getDeviceHelper().uploadOBDUpdataFile(Environment.getExternalStorageDirectory().getPath() + File.separator + newUpdaFile_obd.getFilename(), updatePrigressCallback);
                                }
                            });


                            break;
                    }
                }else{
                    XuToast.show(getActivity(),STR(R.string.latest));
                }
                break;
            default:
                break;
        }
    }
    
    private void toCancelUpdate() {
        DialogHelp.getInstance().hideDialog();
        adapter.setIsupdataing(false);
        adapter.setUpdatas(new int[0]);
        adapter.setProgressValue(0);
        adapter.refreshData();
        AppContext.getInstance().setUpdateing_s(false);
        if (!AppContext.getInstance().isS()) {
            AppContext.getInstance().getDeviceHelper().closeDeviceUpdateMode();
        }

    }

    private void checkupdate() {
        if (AppContext.getInstance().getDeviceHelper().isConnectedDevice()) {
            //查询设备的最新版本

                AppContext.getInstance().getCachedThreadPool().execute(new Runnable() {
                    @Override
                    public void run() {
                        AppContext.getInstance().getDeviceHelper().getDeviceVersion(new GetBleInfoVersionCallback() {
                            @Override
                            public void onSuccess(String deviceHWver, String deviceAPKver, String deviceSystemver, String obdver, String buzzerver, String GPS) {
                                if (!XuString.isEmpty(deviceAPKver) && !XuString.isEmpty(deviceAPKver)) {
                                    //国强的软件
                                    device_software = deviceAPKver;
                                    device_system = deviceSystemver;
                                    int obdversion = -1;
                                    int buzzerversion = -1;
                                    String[] str_temp_arrays = null;
                                    String str_stemp = "";
                                    //获取OBD协议版本
                                    if (!obdver.isEmpty() && !obdver.equals("01.001.1")) {
                                        str_temp_arrays = obdver.split("\\.");
                                        for (String temp : str_temp_arrays) {
                                            str_stemp += temp;
                                        }
                                        obdversion = Integer.parseInt(str_stemp.substring(3, 6));
                                        if (obdver.contains("upv")) {
                                            device_obd = 1;
                                        } else {
                                            device_obd = Integer.parseInt(str_stemp.substring(0, 3));
                                        }
                                    } else {
                                        device_obd = -1;
                                    }
                                    //获取Buzzer协议版本
                                    if (!buzzerver.isEmpty()) {
                                        str_temp_arrays = buzzerver.split("\\.");
                                        str_stemp = "";
                                        for (String temp : str_temp_arrays) {
                                            str_stemp += temp;
                                        }
                                        buzzerversion = Integer.parseInt(str_stemp.substring(3, 6));
                                    }
                                    //判断需要符合哪些条件
                                    int hasLimitingCondition = 0;
                                    if ((obdver.isEmpty() && buzzerver.isEmpty()) || (obdver.equals("01.001.1") && buzzerver.equals("01.001.1"))) {
                                        hasLimitingCondition = 0;
                                    }
                                    if (!obdver.isEmpty() && !buzzerver.isEmpty() && obdver.equals("01.001.1") && buzzerver.equals("01.001.1")) {
                                        hasLimitingCondition = 1;
                                    }
                                    if (!obdver.isEmpty() && !obdver.equals("01.001.1") && (buzzerver.isEmpty() || buzzerver.equals("01.001.1"))) {
                                        hasLimitingCondition = 2;
                                    }
                                    if ((obdver.isEmpty() || obdver.equals("01.001.1")) && !buzzerver.isEmpty() && !buzzerver.equals("01.001.1")) {
                                        hasLimitingCondition = 3;
                                    }
                                    checkHasUpdate(1, hasLimitingCondition, obdversion, buzzerversion);
                                    checkHasUpdate(2, hasLimitingCondition, obdversion, buzzerversion);
                                    checkHasUpdate(4, 0, 0, 0);
                                    //查询OBD的更新
                                    checkHasUpdate(9, 0, 0, 0);

                                }


                            }

                            @Override
                            public void onFail(int i) {

                            }
                        });
                    }
                });
        } else {
            checkHasUpdate(4, 0, 0, 0);
        }

    }

    private void checkHasUpdate(final int updatetype, final int hasLimitingCondition, final int obdversion, final int buzzersion) {
        //TODO 获取最新版本
        AppContext.getInstance().getCachedThreadPool().execute(new Runnable() {
            @Override
            public void run() {

                AppApi.checkUpdate(updatetype, hasLimitingCondition, obdversion, buzzersion, new ResultCallback<ResultData<VispectUpdateFile>>() {
                    @Override
                    public void onFailure(Request request, Exception e) {
                        XuLog.e(TAG, "检查更新失败：" + e.getMessage());
                    }

                    @Override
                    public void onResponse(ResultData<VispectUpdateFile> response) {
                        if (response.getResult() == AppApi.ApiErrorCode.SUCCESS) {
                            XuLog.d(TAG, "检查更新成功：" + response.getResult() + "     msg:" + response.getMsg().toString());
                            VispectUpdateFile updaFileForNet = response.getMsg();
                            switch (updatetype) {
                                case ARG.UPDATE_TYPE_FIRMWARE:
//                                    if (response.getMsg().getVersionname().compareTo(device_firmware) > 0) {
//                                        XuLog.d(TAG, "有更新的设备固件版本");
//                                        newUpdaFile_firmware = updaFileForNet;
//                                    }
                                    break;
                                case ARG.UPDATE_TYPE_SOFTWARE_V:
                                    String[] d_version_strs = device_software.split("_")[0].split("\\.");
                                    StringBuffer sb = new StringBuffer();
                                    for (String temp_str : d_version_strs) {
                                        sb.append(temp_str);
                                    }
                                    long deviceVersion = Long.parseLong(sb.toString());

                                    String[] s_version_strs = response.getMsg().getVersionname().split("_")[0].split("\\.");
                                    sb.setLength(0);
                                    for (String temp_str : s_version_strs) {
                                        sb.append(temp_str);
                                    }
                                    long servicesVersion = Long.parseLong(sb.toString());
                                    if (servicesVersion > deviceVersion) {
                                        XuLog.d(TAG, "有更新的设备软件版本");
                                        newUpdaFile_software = updaFileForNet;
                                    }
                                    break;
                                case ARG.UPDATE_TYPE_SYSTEM:
                                    if (response.getMsg().getVersionname().compareTo(device_system) > 0) {
                                        XuLog.d(TAG, "有更新的设备操作系统版本");
                                        newUpdaFile_system = updaFileForNet;
                                    }
                                    break;
                                case ARG.UPDATE_TYPE_MYAPP:
//                                    if (response.getMsg().getVersionname().compareTo(myapp) > 0) {
//                                        XuLog.d(TAG, "有更新的APP版本");
//                                        newUpdaFile_app = updaFileForNet;
//                                    }
                                    break;
                                case ARG.UPDATE_TYPE_SOFTWARE_S:
                                    if (AppContext.getInstance().isApp_error_s()) {
                                        XuLog.d(TAG, "检测到应用已损坏 故使用最新版本更新");
                                        newUpdaFile_s = updaFileForNet;
                                    } else {
                                        if (response.getMsg().getVersionname().compareTo(device_software_s) > 0) {
                                            XuLog.d(TAG, "有更新的S款软件版本");
                                            newUpdaFile_s = updaFileForNet;
                                        }
                                    }

                                    break;
                                case ARG.UPDATE_TYPE_OBD:
                                    if ((device_obd < response.getMsg().getVersioncode()) && device_obd != -1) {
                                        XuLog.d(TAG, "有更新的OBD版本");
                                        newUpdaFile_obd = updaFileForNet;
                                    }
                                    break;


                            }
                            //显示最新版本
                            mhandler.post(toShowUpda);
                        }


                    }
                });


            }
        });

    }

    Runnable toShowUpda = new Runnable() {
        @Override
        public void run() {
                String[] newValue = {
                        newUpdaFile_software != null ? STR(R.string.update_software_system) + ";1;" + newUpdaFile_software.getVersionname() + ";" + newUpdaFile_software.getFilename() + ";" + newUpdaFile_software.getMd5() : STR(R.string.update_software_system), newUpdaFile_obd != null ? "OBD" + ";1;" + newUpdaFile_obd.getVersionname() + ";" + newUpdaFile_obd.getFilename() + ";" + newUpdaFile_obd.getMd5() : "OBD",
              };
                adapter.refreshData(newValue);
        }
    };

    private void showDialogForUpdateFail(final String title, final int type) {
        try {
            NotconnectDialog.Builder builder = new NotconnectDialog.Builder(getActivity());
            builder.setMessage(title);
            if (XuNetWorkUtils.getNetWorkType() != 1) {
                builder.setMessage(title + STR(R.string.current_network_type) + "mobile");
            }

            builder.setPositiveButton(STR(R.string.cancel), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            builder.setNegativeButton(STR(R.string.yes), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    //设置正在下载的标识
                    canupdata = false;
                    adapter.setIsupdataing(true);
                    mhandler.post(mRunnable);

                    //开启下载
                    String path = Environment.getExternalStorageDirectory().getPath();
                    switch (type) {
                        case ARG.UPDATE_TYPE_FIRMWARE:
                            updataposition = 0;
                            AppApi.download(newUpdaFile_firmware.getPath() + newUpdaFile_firmware.getFilename(), path + "/" + newUpdaFile_firmware.getFilename(), progressCallBack);
                            break;
                        case ARG.UPDATE_TYPE_SOFTWARE_V:
                            updataposition = 1;
                            AppApi.download(newUpdaFile_software.getPath() + newUpdaFile_software.getFilename(), path + "/" + newUpdaFile_software.getFilename(), progressCallBack);
                            break;
                        case ARG.UPDATE_TYPE_SYSTEM:
                            //操作系统暂时没有更新
                            break;
                        case ARG.UPDATE_TYPE_MYAPP:
                            updataposition = 3;
                            AppApi.download(newUpdaFile_app.getPath() + newUpdaFile_app.getFilename(), path + "/" + newUpdaFile_app.getFilename(), progressCallBack);
                            break;
                        case ARG.UPDATE_TYPE_SOFTWARE_S:
                            updataposition = 1;
                            AppApi.download(newUpdaFile_s.getPath() + newUpdaFile_s.getFilename(), path + "/" + newUpdaFile_s.getFilename(), progressCallBack);
                            break;
                        case ARG.UPDATE_TYPE_OBD:
                            updataposition = 2;
                            AppApi.download(newUpdaFile_obd.getPath() + newUpdaFile_obd.getFilename(), path + "/" + newUpdaFile_obd.getFilename(), progressCallBack);
                            break;
                    }


                    dialog.dismiss();
                }
            });

            builder.create().show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            //开始可以显示进度条了
            DialogHelp.getInstance().hideDialog();
        }
    };

    private Runnable onFielError = new Runnable() {
        @Override
        public void run() {
            //文件损坏
            DialogHelp.getInstance().hideDialog();
            XuToast.show(getActivity(), STR(R.string.file_corrupted));
        }
    };

    private Runnable nowfilemRunnable = new Runnable() {
        @Override
        public void run() {
            //没找到文件的提示
            DialogHelp.getInstance().hideDialog();
            XuToast.show(getActivity(), STR(R.string.update_not_find_file));
        }
    };

    private XuTimeOutUtil timeoutUtil = new XuTimeOutUtil(new XuTimeOutCallback() {
        @Override
        public void onTimeOut() {
            toCancelUpdate();
        }
    });


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO: inflate a fragment view
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    private void updateDeviceAPK(final String path){
        AppContext.getInstance()
                .getCachedThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                //连接完成之后开始升级
                updataposition = 0;
                AppContext.getInstance().getDeviceHelper().deviceAPKUpdate(path + "/" + newUpdaFile_software.getFilename(), updatePrigressCallback);
                mhandler.post(mRunnable);

            }
        });
    }
    private void updateDeviceAPKOld(final String path){
        //打开设备的升级模式
        AppContext.getInstance().getDeviceHelper().openDeviceUpdateMode(new OnWifiOpenListener() {
            @Override
            public void onSuccess(final String name, final String password) {
                //打开设备的升级模式成功
                timeoutUtil.stopCheck();
                //连接到指定wifi
                mhandler.postDelayed(mRunnable_cancel_connectwifi, 60000);
                canCancelconnection = true;
                AppContext.getInstance().getCachedThreadPool().execute(new Runnable() {
                    @Override
                    public void run() {
                        if (XuNetWorkUtils.connectWIFI(name, password)) {
                            canCancelconnection = false;
                            mhandler.post(mRunnable);
                            updateDeviceAPK(path);
                        }
                    }
                });


            }

            @Override
            public void onFail(int i) {
                timeoutUtil.stopCheck();
            }

            @Override
            public void onGetSanResult(String s) {
                timeoutUtil.stopCheck();
            }
        });
    }


    private void updateOBDOld(final String path){
        //打开设备的升级模式
        AppContext.getInstance().getDeviceHelper().openDeviceUpdateMode(new OnWifiOpenListener() {
            @Override
            public void onSuccess(final String name, final String password) {
                //打开设备的升级模式成功
                timeoutUtil.stopCheck();
                //连接到指定wifi
                mhandler.postDelayed(mRunnable_cancel_connectwifi, 60000);
                canCancelconnection = true;
                AppContext.getInstance().getCachedThreadPool().execute(new Runnable() {
                    @Override
                    public void run() {
                        if(XuNetWorkUtils.connectWIFI(name,password)){
                            canCancelconnection = false;
                            mhandler.post(mRunnable);
                            updateOBD(path);
                        }
                    }
                });


            }

            @Override
            public void onFail(int i) {
                timeoutUtil.stopCheck();
            }

            @Override
            public void onGetSanResult(String s) {
                timeoutUtil.stopCheck();
            }
        });
    }




    private void updateOBD(final String path){
        AppContext.getInstance()
                .getCachedThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                //连接完成之后开始升级
                updataposition = 2;
                AppContext.getInstance().getDeviceHelper().uploadOBDUpdataFile(path, updateOBDPrigressCallback);
                mhandler.post(mRunnable);

            }
        });
    }

    private void updateOTA(final File f){
        AppContext.getInstance().getCachedThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                updataposition = 0;
                mhandler.post(mRunnable);
                AppContext.getInstance().getDeviceHelper().deviceOTAUpdate(f.getPath(), updatePrigressCallback);

            }
        });
    }

    private void toUpdateOTAOld(final File f){
        AppContext.getInstance().getDeviceHelper().openDeviceUpdateMode(new OnWifiOpenListener() {
            @Override
            public void onSuccess(final String name, final String password) {
                //连接到指定WIFI
                mhandler.postDelayed(mRunnable_cancel_connectwifi, 60000);
                canCancelconnection = true;
                AppContext.getInstance().getCachedThreadPool().execute(new Runnable() {
                    @Override
                    public void run() {
                        if( XuNetWorkUtils.connectWIFI(name,password)){
                            canCancelconnection = false;
                            mhandler.post(mRunnable);
                            //连接成功  开始更新
                            updateOTA(f);

                        }else{
                            //连接失败

                        }
                    }
                });


            }

            @Override
            public void onFail(int i) {

            }

            @Override
            public void onGetSanResult(String s) {

            }
        });
    }

    private boolean canCancelconnection = false;
    private Runnable mRunnable_cancel_connectwifi = new Runnable() {
        @Override
        public void run() {
            //时间过长的话就提示让用户手动连
            if(canCancelconnection){
                XuLog.d(TAG,"超时取消wifi连接");
                canshowlongtime = false;
                XuNetWorkUtils.cancelConnectWIFI();
                XuToast.show(AppContext.getInstance(), STR(R.string.connect_timeout));
                DialogHelp.getInstance().hideDialog();
            }
        }
    };
    //下载进度callback
    ReqProgressCallBack progressCallBack = new ReqProgressCallBack() {
        @Override
        public void onProgress(long total, long current) {
            XuLog.d(TAG, "total:" + total + "     current:" + current);
            Message message = new Message();
            message.what = ARG.DOWNLOAD_PROGRESS;
            HashMap<String, String> map = new HashMap<>();
            map.put("progress", (100 * current / (total)) + "");
            message.obj = map;
            handler.handleMessage(message);
        }

        @Override
        public void onFile(String errorMessage) {
            Message message = new Message();
            message.what = ARG.DOWNLOAD_PROGRESS;
            HashMap<String, String> map = new HashMap<>();
            map.put("progress", ARG.DOWNLOAD_DONE);
            message.obj = map;
            handler.handleMessage(message);
        }

        @Override
        public void onDone() {
            XuLog.e(TAG,"完成了");
            Message message = new Message();
            message.what = ARG.DOWNLOAD_PROGRESS;
            HashMap<String, String> map = new HashMap<>();
            map.put("progress", ARG.DOWNLOAD_DONE);
            message.obj = map;
            handler.handleMessage(message);
        }
    };

    boolean isGetProgress = false;
    //OBD更新的callback
    ProgressCallback updateOBDPrigressCallback = new ProgressCallback() {
        @Override
        public void onProgressChange(long l) {
            XuLog.e(TAG, "进度：" + l);
            Message message = new Message();
            message.what = ARG.DOWNLOAD_PROGRESS;
            HashMap<String, String> map = new HashMap<>();
            map.put("progress", l + "");
            message.obj = map;
            handler.handleMessage(message);
        }

        @Override
        public void onErro(int i) {

        }

        @Override
        public void onDone(String s, String s1) {
            XuLog.e(TAG, "onDone");
            Message message = new Message();
            message.what = ARG.DOWNLOAD_PROGRESS;
            HashMap<String, String> map = new HashMap<>();
            map.put("progress", ARG.DOWNLOAD_DONE);
            message.obj = map;
            handler.handleMessage(message);
            //开始获取更新进度
            if(!isGetProgress){
                AppContext.getInstance().getDeviceHelper().startGetOBDUpdataProgress(updateOBDPrigressCallback,1000);
                isGetProgress = true;
            }else{
                AppContext.getInstance().getDeviceHelper().stopGetOBDUpdataProgress();
                isGetProgress = false;
//                checkupdate();
                mhandler.post(new Runnable() {
                    @Override
                    public void run() {
                        XuToast.show(getActivity(), STR(R.string.update_done));
                        mhandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                getActivity().finish();
                            }
                        }, 1000);
                    }
                });
            }
        }
    };


    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden){
            XuNetWorkUtils.cancelConnectWIFI();
        }
    }

    private ProgressCallback updatePrigressCallback  = new ProgressCallback() {
        @Override
        public void onProgressChange(long l) {
            XuLog.e(TAG,"进度："+l);
            Message message = new Message();
            message.what = ARG.DOWNLOAD_PROGRESS;
            HashMap<String, String> map = new HashMap<>();
            map.put("progress", l + "");
            message.obj = map;
            handler.handleMessage(message);
        }

        @Override
        public void onErro(int i) {

        }

        @Override
        public void onDone(String s, String s1) {
            XuLog.e(TAG,"onDone");
            Message message = new Message();
            message.what = ARG.DOWNLOAD_PROGRESS;
            HashMap<String, String> map = new HashMap<>();
            map.put("progress", ARG.DOWNLOAD_DONE);
            message.obj = map;
            handler.handleMessage(message);
        }

    };

}
