package com.vispect.android.vispect_g2_app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.vispect.android.vispect_g2_app.R;
import com.vispect.android.vispect_g2_app.bean.Country;

import java.util.ArrayList;


/**
 * Created by mo on 2018/3/26.
 */

public class CountrySelectAdapter extends BaseAdapter {

    private LayoutInflater mLayoutInflater;
    private Context mContext;
    private ArrayList<Country> mData;
    private char fristcode;
    private ArrayList<FristList> fristList;


    public CountrySelectAdapter(Context mContext, ArrayList<Country> mData) {
        fristList = new ArrayList<>();
        mLayoutInflater = LayoutInflater.from(mContext);
        this.mContext = mContext;
        this.mData = mData;

        if (mData.size() > 0) {
            fristcode = mData.get(0).getPingyin().charAt(0);
        }
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
            convertView = mLayoutInflater.inflate(R.layout.country_item, parent, false);
            holder.tv_country_item = convertView.findViewById(R.id.tv_country_item);
            holder.lin_fristcode = convertView.findViewById(R.id.lin_frist);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.tv_country_item.setText(mData.get(position).getName());

        if(mData.get(position).getPingyin().charAt(0) == fristcode || (byte)mData.get(position).getPingyin().charAt(0) > (byte)fristcode){
            FristList frist = new FristList();
            frist.setPosition(position);
            frist.setFrist(mData.get(position).getPingyin().charAt(0));
            fristList.add(frist);

            if(mData.get(position).getPingyin().charAt(0) == fristcode) {
                fristcode = (char) ((byte) fristcode + 1);
            }else {
                fristcode = (char)((byte)mData.get(position).getPingyin().charAt(0)+1);
            }

        }

        holder.lin_fristcode.setVisibility(View.GONE);
        for (int i=0;i<fristList.size();i++){
            if(position == fristList.get(i).getPosition()){
                TextView tv = holder.lin_fristcode.findViewById(R.id.tv_frist);
                tv.setText((fristList.get(i).getFrist()+"").toUpperCase());
                holder.lin_fristcode.setVisibility(View.VISIBLE);
            }
        }


        return convertView;
    }


    class ViewHolder {
        private TextView tv_country_item;
        private LinearLayout lin_fristcode;
    }

    class FristList {
        public int getPosition() {
            return position;
        }

        public void setPosition(int position) {
            this.position = position;
        }

        public char getFrist() {
            return frist;
        }

        public void setFrist(char frist) {
            this.frist = frist;
        }

        private int position;
        private char frist;
    }

}
