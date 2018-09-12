package com.vispect.android.vispect_g2_app.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.vispect.android.vispect_g2_app.R;
import com.vispect.android.vispect_g2_app.bean.Setting;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mo on 2018/7/12.
 */

public class CalibrateAdapter extends BaseAdapter {
    private List<String> _data = new ArrayList<>();
    private Context _context;

    public CalibrateAdapter(Context context) {
        _context = context;
    }

    public void setData(List<String> data) {
        _data.clear();
        if (data != null && data.size() > 0) _data.addAll(data);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return _data.size();
    }

    @Override
    public Object getItem(int i) {
        return _data.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            // 如果是第一次显示该页面(要记得保存到viewholder中供下次直接从缓存中调用)
            holder = new ViewHolder();
            convertView = LayoutInflater.from(_context).inflate(R.layout.list_item_dot, null);
            holder.tvContext = convertView.findViewById(R.id.tv_choose_cammera);
            holder.dotView = convertView.findViewById(R.id.img_dot);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.dotView.setColorFilter(Color.parseColor(position % 2 == 0 ? "#00CCCC" : "#0099CC"));
        holder.tvContext.setText(_data.get(position));
        return convertView;
    }

    class ViewHolder {
        private TextView tvContext;
        private ImageView dotView;
    }

}
