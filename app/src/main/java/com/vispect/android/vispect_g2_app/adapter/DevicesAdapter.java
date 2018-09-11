package com.vispect.android.vispect_g2_app.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import com.vispect.android.vispect_g2_app.R;
import com.vispect.android.vispect_g2_app.utils.XuLog;
import com.vispect.android.vispect_g2_app.utils.XuString;

import java.util.ArrayList;
import java.util.List;

import bean.BLEDevice;

import static android.view.View.GONE;


/**
 * 设备列表用到的adapter
 * Created by xu
 */
public class DevicesAdapter extends BaseAdapter {

    private List<BLEDevice> _devices = new ArrayList<>();
    private Context _context;

    public DevicesAdapter(@NonNull Context context) {
        _context = context;
    }

    public void setDevices(@Nullable List<BLEDevice> devices) {
        _devices.clear();
        if (devices != null && devices.size() > 0) _devices.addAll(devices);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return _devices.size();
    }

    @Override
    public BLEDevice getItem(int position) {
        if (position < 0) {
            XuLog.e("连接设备的ViewPagerAdapter的getItem出现问题，position = " + position);
            return _devices.get(0);
        }
        if (position > _devices.size() - 1) {
            XuLog.e("连接设备的ViewPagerAdapter的getItem出现问题，position = " + position);
            return _devices.get(_devices.size() - 1);
        }
        return _devices.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            // 如果是第一次显示该页面(要记得保存到viewholder中供下次直接从缓存中调用)
            holder = new ViewHolder();
            convertView = LayoutInflater.from(_context).inflate(R.layout.item_connect_device, null);
            holder.deviceName = convertView.findViewById(R.id.device_name);
            holder.signal = convertView.findViewById(R.id.signal);
            holder.divider = convertView.findViewById(R.id.divider);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        String deviceName = _devices.get(position).getBluetoothDevice().getName();
        int rssi = _devices.get(position).getRssi();
        if (rssi > -40) {
            holder.signal.setImageResource(R.mipmap.signal3);
        } else if (rssi > -70) {
            holder.signal.setImageResource(R.mipmap.signal2);
        } else if (rssi > -90) {
            holder.signal.setImageResource(R.mipmap.signal1);
        } else {
            holder.signal.setImageResource(R.mipmap.signal0);
        }
        holder.deviceName.setText(XuString.isEmpty(deviceName) ? "unKnown" : deviceName);
        holder.divider.setVisibility(position == getCount() - 1 ? GONE : View.VISIBLE);
        return convertView;
    }

    private class ViewHolder {
        ImageView signal;
        TextView deviceName;
        View divider;
    }

}
