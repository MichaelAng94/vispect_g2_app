package com.vispect.android.vispect_g2_app.ui.activity;

import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import com.vispect.android.vispect_g2_app.R;
import com.vispect.android.vispect_g2_app.adapter.VehicleAdapter;
import com.vispect.android.vispect_g2_app.app.AppContext;
import com.vispect.android.vispect_g2_app.ui.widget.MoListView;
import com.vispect.android.vispect_g2_app.utils.XuLog;

import java.util.ArrayList;

import bean.VehicleInfo;
import bean.Vispect_SDK_CarInfo;
import butterknife.Bind;
import interf.GetCarInfoListener;

/**
 * Created on 2018/12/04.
 */
public class VehicleStatusActivity extends BaseActivity {
    private final static String TAG = "VehicleStatusActivity";
    private ArrayList<VehicleInfo> vehicleInfos;
    VehicleAdapter vehicleadapter;
    @Bind(R.id.iv_titlebar_title)
    TextView titie;
    @Bind(R.id.list_vehicle_status)
    MoListView list_vehicle_status;

    Handler m_handler = new Handler();
    @Override
    public int getContentResource() {
        return R.layout.vehicle_status;
    }

    @Override
    protected void initView(View view) {
        titie.setText(R.string.vehicle_status);
        vehicleInfos = new ArrayList<>();
        if(AppContext.getInstance().getDeviceHelper().isConnectedDevice()){
            for (int i = 1; i < 9; i++) {
                VehicleInfo car = new VehicleInfo();
                car.setType(i);
                vehicleInfos.add(car);
            }
            vehicleadapter = new VehicleAdapter(this, vehicleInfos);
            list_vehicle_status.setAdapter(vehicleadapter);
            resetItem(list_vehicle_status);
            list_vehicle_status.setSelection(vehicleadapter.getCount()/2);
        }
    }

    private void resetItem(MoListView listview) {
        if(listview == null){
            XuLog.e("reset item with no list");
            return;
        }
        ArrayList<VehicleInfo> vehicletemplist = new ArrayList<>();
        vehicletemplist.addAll(vehicleInfos);
        vehicleadapter.refreshData(vehicletemplist);
    }

    GetCarInfoListener car_info_listener =new GetCarInfoListener() {
        @Override
        public void onGetCarInfoData(Vispect_SDK_CarInfo carInfo) {
            XuLog.e(TAG,"车况信息:"+carInfo.toString());
            ArrayList<VehicleInfo> vehicleInfos_temp = new ArrayList<>();
            for (VehicleInfo temp :vehicleInfos ){
                switch (temp.getType()){
                    case 1:
                        temp.setValue((carInfo.getCoolantTemperature().equals("-1℃") || carInfo.getCoolantTemperature().equals("255℃")) ? "- -" : carInfo.getCoolantTemperature());
                        break;
                    case 2:
                        temp.setValue((carInfo.getEngineSpeed().equals("-1rpm") || carInfo.getEngineSpeed().equals("65535rpm")) ? "- -" : carInfo.getEngineSpeed());
                        break;
                    case 3:
                        temp.setValue((carInfo.getInstaneousFuel().equals("-1L") || carInfo.getInstaneousFuel().equals("65535L")) ? "- -" : carInfo.getInstaneousFuel());
                        break;
                    case 4:
                        temp.setValue((carInfo.getODO().equals("-1KM") || carInfo.getODO().equals("4294967295KM")) ? "- -" : carInfo.getODO());
                        break;
                    case 5:
                        temp.setValue((carInfo.getVehicleSpeed().equals("-1KM/H") || carInfo.getVehicleSpeed().equals("255KM/H") ) ? "- -" : carInfo.getVehicleSpeed());
                        break;
                    case 6:
                        temp.setValue((carInfo.getRemainingFuel().equals("-1%") || carInfo.getRemainingFuel().equals("255%")) ? "- -" : carInfo.getRemainingFuel());
                        break;
                    case 7:
                        temp.setValue((carInfo.getBatteryVoltage().equals("-1V") || carInfo.getBatteryVoltage().equals("65535V")) ? "- -" : carInfo.getBatteryVoltage());
                        break;
                    case 8:
                        temp.setValue((carInfo.getFuelConnsumption().equals("-1L") || carInfo.getFuelConnsumption().equals("4294967295L")) ? "- -" : carInfo.getFuelConnsumption());
                        break;
                    case 9:
                        temp.setValue(carInfo.getLight_L_R());
                        XuLog.d("设置左右转向");
                        break;
                    default:
                        break;
                }
                vehicleInfos_temp.add(temp);
            }
            vehicleInfos = vehicleInfos_temp;
            m_handler.post(new Runnable() {
                @Override
                public void run() {
                    vehicleadapter.refreshData(vehicleInfos);
                    resetItem(list_vehicle_status);
                }
            });
        }

        @Override
        public void onErro(int i) {

        }
    };

    @Override
    public void onResume() {
        super.onResume();
        AppContext.getInstance().getDeviceHelper().getCarInfoData(car_info_listener);
    }
}
