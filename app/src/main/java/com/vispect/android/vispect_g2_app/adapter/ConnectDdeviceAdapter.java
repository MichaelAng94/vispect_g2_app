package com.vispect.android.vispect_g2_app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import com.vispect.android.vispect_g2_app.R;
import com.vispect.android.vispect_g2_app.app.AppConfig;
import com.vispect.android.vispect_g2_app.utils.XuLog;

import java.util.ArrayList;

import bean.BLEDevice;


/**
 * 设备列表用到的adapter
 * Created by xu
 */
public class ConnectDdeviceAdapter extends BaseAdapter {

    private ArrayList<BLEDevice> devices = new ArrayList<BLEDevice>();
    private Context context;

    public ConnectDdeviceAdapter(Context context, ArrayList<BLEDevice> devices) {
        this.context = context;
        this.devices = devices;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return devices.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        if (position < 0) {
            XuLog.e("连接设备的ViewPagerAdapter的getItem出现问题，position = " + position);
            return devices.get(0);
        }
        if (position > devices.size() - 1) {
            XuLog.e("连接设备的ViewPagerAdapter的getItem出现问题，position = " + position);
            return devices.get(devices.size() - 1);
        }
        return devices.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        ViewHolder holder = null;
        if (convertView == null) {
            // 如果是第一次显示该页面(要记得保存到viewholder中供下次直接从缓存中调用)
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.item_connect_device, null);
            holder.device_name = (TextView) convertView.findViewById(R.id.tv_item_connect_device_name);
            holder.device_ssid = (TextView) convertView.findViewById(R.id.tv_item_ssid);
            holder.signal = (ImageView) convertView.findViewById(R.id.signal);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final String deviceName = devices.get(position).getBluetoothDevice().getName();
        final String address = devices.get(position).getBluetoothDevice().getAddress();
        final int rssi = devices.get(position).getRssi();

        if (AppConfig.showSSID) {
            holder.device_ssid.setVisibility(View.VISIBLE);
        }else {
            holder.device_ssid.setVisibility(View.GONE);
        }
        if (deviceName != null && deviceName.length() > 0) {
//			holder.device_name.setText(deviceName+"         "+rssi+"\n"+""+address);
//			holder.device_name.setText(deviceName+"\n"+""+address);
            if (rssi > -40) {
                holder.signal.setImageResource(R.mipmap.signal3);
            } else if (rssi > -70) {
                holder.signal.setImageResource(R.mipmap.signal2);
            } else if (rssi > -90) {
                holder.signal.setImageResource(R.mipmap.signal1);
            } else {
                holder.signal.setImageResource(R.mipmap.signal0);
            }
            holder.device_name.setText(deviceName + "         ");
            holder.device_ssid.setText("   " + rssi);
        } else {
            holder.device_name.setText("unknow");
        }

        return convertView;
    }

    public void refreshData(ArrayList<BLEDevice> devices) {
        this.devices = devices;
        notifyDataSetChanged();
    }

    private class ViewHolder {
        ImageView signal;
        TextView device_name, device_ssid;

    }

}
