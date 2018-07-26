package com.vispect.android.vispect_g2_app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.vispect.android.vispect_g2_app.R;
import com.vispect.android.vispect_g2_app.bean.CarInfo;
import com.vispect.android.vispect_g2_app.utils.XuView;

import java.util.List;

/**
 * 选择车的品牌和型号列表用到的adapter
 * Created by xu
 */
public class SelectCarBrandOrModelAdapter extends BaseAdapter implements SectionIndexer {
	private List<CarInfo> list = null;
    private Context mContext;
    private boolean isgetBrand = true;
    public SelectCarBrandOrModelAdapter(Context mContext, List<CarInfo> list) {
        this.mContext = mContext;  
        this.list = list;  
    }  
      
    /** 
     * 当ListView数据发生变化时,调用此方法来更新ListView 
     * @param list 
     */  
    public void updateListView(List<CarInfo> list){
        this.list = list;  
        notifyDataSetChanged();  
    }  
    @Override
    public int getCount() {  
        return this.list.size();  
    }  
    @Override
    public Object getItem(int position) {
        return list.get(position);  
    }  
    @Override
    public long getItemId(int position) {  
        return position;  
    }  
    @Override
    public View getView(final int position, View view, ViewGroup arg2) {
        ViewHolder viewHolder = null;  

        if (view == null) {  
            viewHolder = new ViewHolder();  
            view = LayoutInflater.from(mContext).inflate(R.layout.item_carlist, null);
            viewHolder.cars_item_head= (TextView) view.findViewById(R.id.cars_item_head);
//            viewHolder.cars_item_carsimg=  (RoundImageView) view.findViewById(R.id.cars_item_carsimg);
            viewHolder.cars_item_name=(TextView) view.findViewById(R.id.cars_item_name);
            viewHolder.cars_item_content=(TextView) view.findViewById(R.id.cars_item_content);
            viewHolder.cars_item_lin=(LinearLayout) view.findViewById(R.id.cars_item_lin);
            viewHolder.cars_item_reviewed=(TextView) view.findViewById(R.id.cars_item_reviewed);

            view.setTag(viewHolder);  
        } else {  
            viewHolder = (ViewHolder) view.getTag();  
        }  
          
        if(viewHolder!=null){
        	try {
                CarInfo mContent = list.get(position);
        		 //根据position获取分类的首字母的char ascii值  
                int section = getSectionForPosition(position);
                //如果当前位置等于该分类首字母的Char的位置 ，则认为是第一次出现  
                if(position == getPositionForSection(section)){  
                	viewHolder.cars_item_head.setVisibility(View.VISIBLE);
                	viewHolder.cars_item_head.setText(mContent.getSortLetters());  
                }else{  
                	viewHolder.cars_item_head.setVisibility(View.GONE);
                }  

                if(isgetBrand){
                    viewHolder.cars_item_name.setText(mContent.getBrand());
                    XuView.setViewVisible(viewHolder.cars_item_reviewed, false);
                }else{
                    viewHolder.cars_item_name.setText(mContent.getModel());
                    XuView.setViewVisible(viewHolder.cars_item_reviewed, mContent.getIsreviewed().equals("1"));
                }


			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
        }
       return view;  
  
    }  
      
  
  
    final static class ViewHolder {  
       private TextView cars_item_head;
       private TextView cars_item_name;
       private TextView cars_item_content;
       private LinearLayout cars_item_lin;
        private TextView cars_item_reviewed;

       
    }  
  
  
    /** 
     * 根据ListView的当前位置获取分类的首字母的char ascii值 
     */  
    public int getSectionForPosition(int position) {  
        return list.get(position).getSortLetters().charAt(0);  
    }  
  
    /** 
     * 根据分类的首字母的Char ascii值获取其第一次出现该首字母的位置 
     */  
    public int getPositionForSection(int section) {  
        for (int i = 0; i < getCount(); i++) {  
            String sortStr = list.get(i).getSortLetters();
            char firstChar = sortStr.toUpperCase().charAt(0);  
            if (firstChar == section) {  
                return i;  
            }  
        }  
          
        return -1;  
    }

    public boolean isgetBrand() {
        return isgetBrand;
    }

    public void setIsgetBrand(boolean isgetBrand) {
        this.isgetBrand = isgetBrand;
    }

    @Override
    public Object[] getSections() {
        return null;  
    }

}
