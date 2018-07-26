package com.vispect.android.vispect_g2_app.adapter;

import android.content.Context;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


import com.vispect.android.vispect_g2_app.R;
import com.vispect.android.vispect_g2_app.bean.ChangeOBDValue;
import com.vispect.android.vispect_g2_app.bean.OBDValue;
import com.vispect.android.vispect_g2_app.utils.XuLog;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * OBD破解数据用到的adapter
 * Created by xu
 */
public class OBDVualeAdapter extends BaseAdapter {
    private final static String TAG = "OBDVualeAdapter";
    private ArrayList<OBDValue> obdvalues = new ArrayList<OBDValue>();
    private Context context;
    private Boolean isfrist = true;

    ForegroundColorSpan redSpan;
    BackgroundColorSpan blackSpan;

    private HashMap<String, ViewHolder> viewHolderHashMap = new HashMap<>();

    public OBDVualeAdapter(Context context, ArrayList<OBDValue> values) {
        this.context = context;
        obdvalues = values;
    }

    public void setisfrist() {
        isfrist = true;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return obdvalues.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        if (position < 0) {
            XuLog.e("obdvalues getItem出现问题，position = " + position);
            return obdvalues.get(position);
        }
        if (position > obdvalues.size() - 1) {
            XuLog.e("obdvalues getItem出现问题，position = " + position);
            return obdvalues.get(obdvalues.size() - 1);
        }
        return obdvalues.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        ViewHolder holder = null;
        if (convertView == null) {// 如果是第一次显示该页面(要记得保存到viewholder中供下次直接从缓存中调用)
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.item_obd_value, null);
            holder.obd_value_id = (TextView) convertView.findViewById(R.id.obd_value_id);
            holder.obd_value = (TextView) convertView.findViewById(R.id.obd_value);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        OBDValue value = obdvalues.get(position);
        viewHolderHashMap.put(value.getId(), holder);

//		OBDValue last;
//
//		if(position >= lastvalues.size()){
//			last = new OBDValue();
//			last.setValue(value.getValue());
//		}else{
//			last = lastvalues.get(position);
//		}


        if (value != null) {
//			if(value.getValue().equals(last.getValue())){
//				holder.obd_value_id.setText(value.getId());
//				holder.obd_value.setText(value.getValue());
////				if(cantoBlack){
////					//没变化不改变颜色
////					holder.obd_value_id.setText(value.getId());
////					holder.obd_value.setText(value.getValue());
////				}else{
////					holder.obd_value_id.setText(value.getId());
////					holder.obd_value.setText(builder);
////				}
//			}else{
//				SpannableStringBuilder builder = new SpannableStringBuilder(value.getValue());
//				byte[] str_bytes_now = value.getValue().getBytes();
//				byte[] str_bytes_last = last.getValue().getBytes();
//				span_start.clear();
//				for (int i = 0 ; i < str_bytes_now.length; i++){
//					if(str_bytes_now[i] != str_bytes_last[i]){
//						ForegroundColorSpan redSpan = new ForegroundColorSpan(Color.RED);//ForegroundColorSpan 为文字前景色，BackgroundColorSpan为文字背景色
//						BackgroundColorSpan blackSpan =  new BackgroundColorSpan(Color.YELLOW);
//						builder.setSpan(redSpan, i, i + 1, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
//						builder.setSpan(blackSpan, i, i + 1, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
//						span_start.add(i);
////						XuLog.d(TAG,"发现一个变动的数字："+i);
//					}
//				}
//
//				holder.obd_value_id.setText(value.getId());
//				holder.obd_value.setText(builder);
//
//			}


            if (value.isChanged()) {
                SpannableStringBuilder builder = new SpannableStringBuilder(value.getValue());
                for (int temp_start : value.getStart()) {
                    int on = value.getBlue().indexOf(temp_start);
//					 XuLog.e(TAG,value.getBlue().size()+":"+on);
                    if (on != -1) {
//						 ForegroundColorSpan redSpan = new ForegroundColorSpan(Color.RED);//ForegroundColorSpan 为文字前景色，BackgroundColorSpan为文字背景色
//						 BackgroundColorSpan blackSpan =  new BackgroundColorSpan(Color.BLUE);
//						 builder.setSpan(redSpan, temp_start, temp_start + 1, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
//						 builder.setSpan(blackSpan, temp_start, temp_start + 1, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
                    } else {
                        redSpan = new ForegroundColorSpan(Color.RED);//ForegroundColorSpan 为文字前景色，BackgroundColorSpan为文字背景色
                        blackSpan = new BackgroundColorSpan(Color.YELLOW);
                        builder.setSpan(redSpan, temp_start, temp_start + 1, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
                        builder.setSpan(blackSpan, temp_start, temp_start + 1, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
                    }

                }
                holder.obd_value_id.setText(value.getId());
                holder.obd_value.setText(builder);
            } else {
                holder.obd_value_id.setText(value.getId());
                holder.obd_value.setText(value.getValue());
                ;
            }

        }

        return convertView;
    }


    public void refreshData(ArrayList<OBDValue> values) {
        this.notifyDataSetChanged();
    }


    /**
     * 数据变化时局部刷新
     * 获取viewholder的TEXTVIEW单独刷新,解决频繁notify卡顿
     *
     * @param values
     */
    public void refreshChaData(ArrayList<ChangeOBDValue> values, ArrayList<OBDValue> valuess, Boolean needchange) {
        ViewHolder viewHolder = null;
        SpannableStringBuilder builder = null;

        if (isfrist) {
            isfrist = false;
            refreshData(valuess);
        }
        if (needchange == true) {
            refreshData(valuess);
            return;
        }
        if (viewHolderHashMap.size() == 0) {
            return;
        }
//		obdvalues = values;
        for (int i = 0; i < values.size(); i++) {
            StringBuffer sb = new StringBuffer();
            viewHolder = viewHolderHashMap.get(values.get(i).getId());
            if (viewHolder == null) {
                return;
            }
            sb.append(viewHolder.obd_value.getText().toString());
            for (int j = 0; j < values.get(i).getChangebit().size(); j++) {
                if (sb.charAt((int) values.get(i).getChangebit().get(j)) == '1') {
                    sb.setCharAt((int) values.get(i).getChangebit().get(j), '0');
                } else {
                    sb.setCharAt((int) values.get(i).getChangebit().get(j), '1');
                }
            }
            builder = new SpannableStringBuilder(sb.toString());
            for (int k = 0; k < values.get(i).getChangebit().size(); k++) {
                redSpan = new ForegroundColorSpan(Color.RED);//ForegroundColorSpan 为文字前景色，BackgroundColorSpan为文字背景色
                blackSpan = new BackgroundColorSpan(Color.YELLOW);

                builder.setSpan(redSpan, Integer.parseInt(values.get(i).getChangebit().get(k).toString()), Integer.parseInt(values.get(i).getChangebit().get(k).toString()) + 1, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
                builder.setSpan(blackSpan, Integer.parseInt(values.get(i).getChangebit().get(k).toString()), Integer.parseInt(values.get(i).getChangebit().get(k).toString()) + 1, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
            }

            viewHolder.obd_value.setText(builder);
        }
    }

    public void flip(ArrayList<OBDValue> values) {
        obdvalues = values;
        this.notifyDataSetChanged();
    }

    private class ViewHolder {
        TextView obd_value_id;
        TextView obd_value;
    }


}
