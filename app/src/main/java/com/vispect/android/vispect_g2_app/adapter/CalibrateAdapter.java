package com.vispect.android.vispect_g2_app.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.vispect.android.vispect_g2_app.R;
import java.util.ArrayList;
import bean.BLEDevice;

/**
 * Created by mo on 2018/7/12.
 */

public class CalibrateAdapter  extends BaseAdapter{
    private ArrayList<String> datas = new ArrayList<String>();
    private Context context;

    public CalibrateAdapter(Context context, ArrayList<String> datas) {
        this.context = context;
        this.datas = datas;
    }

    @Override
    public int getCount() {
        return datas.size();
    }

    @Override
    public Object getItem(int i) {
        return datas.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        CalibrateAdapter.ViewHolder holder = null;
        if (convertView == null) {
            // 如果是第一次显示该页面(要记得保存到viewholder中供下次直接从缓存中调用)
            holder = new CalibrateAdapter.ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item_dot, null);
            holder.tvContext = (TextView) convertView.findViewById(R.id.tv_choose_cammera);
            holder.dotView = (ImageView) convertView.findViewById(R.id.img_dot);
            convertView.setTag(holder);
        } else {
            holder = (CalibrateAdapter.ViewHolder) convertView.getTag();
        }
        if (position%2==0){
            holder.dotView.setColorFilter(Color.parseColor("#00CCCC"));
        }else {
            holder.dotView.setColorFilter(Color.parseColor("#0099cc"));
        }
        holder.tvContext.setText(datas.get(position));
        return convertView;
    }

    class ViewHolder {
        private TextView tvContext;
        private ImageView dotView;
    }

}
