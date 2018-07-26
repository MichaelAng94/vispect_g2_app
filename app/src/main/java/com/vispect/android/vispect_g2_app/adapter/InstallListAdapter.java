package com.vispect.android.vispect_g2_app.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.vispect.android.vispect_g2_app.R;

import java.util.List;

/**
 * Created by mo on 2018/3/26.
 */

public class InstallListAdapter extends BaseAdapter {

    private LayoutInflater mLayoutInflater;
    private Context mContext;
    private List<String> mData;

    public InstallListAdapter(Context mContext, List<String> mData) {
        mLayoutInflater = LayoutInflater.from(mContext);
        this.mContext = mContext;
        this.mData = mData;
    }

    @Override
    public int getCount() {
        return mData == null ? 0 : mData.size();
    }

    @Override
    public Object getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mLayoutInflater.inflate(R.layout.calibration_listitem, parent, false);
            holder.tv_item_description = (TextView) convertView.findViewById(R.id.tv_item_description);
            holder.iv_item_next = (ImageView) convertView.findViewById(R.id.iv_item_next);
            holder.iv_item_num = (ImageView) convertView.findViewById(R.id.iv_item_num);
            holder.tv_item_flag = (TextView) convertView.findViewById(R.id.tv_item_flag);
            holder.relativeLayout = convertView.findViewById(R.id.rela);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        if (position % 2 == 0) {
            //holder.relativeLayout.setBackground(new ColorDrawable(mContext.getResources().getColor(R.color.white)));
            StateListDrawable drawable = new StateListDrawable();
            Drawable selected = new ColorDrawable(mContext.getResources().getColor(R.color.textColor));
            Drawable unSelected = new ColorDrawable(mContext.getResources().getColor(R.color.background));
            drawable.addState(new int[]{android.R.attr.state_pressed},
                    selected);
            drawable.addState(new int[]{-android.R.attr.state_pressed},
                    unSelected);
            holder.relativeLayout.setBackground(drawable);
        }
        holder.tv_item_description.setText(mData.get(position));
        Resources res = mContext.getResources();
        Drawable drawable = ContextCompat.getDrawable(mContext, res.getIdentifier("install_menu_" + (position + 1), "drawable", mContext.getPackageName()));
        holder.iv_item_num.setImageDrawable(drawable);


        return convertView;
    }


    class ViewHolder {
        private TextView tv_item_description;
        private ImageView iv_item_num;
        private ImageView iv_item_next;
        private TextView tv_item_flag;
        private RelativeLayout relativeLayout;
    }

}
