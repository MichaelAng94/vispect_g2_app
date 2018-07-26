package com.vispect.android.vispect_g2_app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.vispect.android.vispect_g2_app.R;
import com.vispect.android.vispect_g2_app.utils.XuFileUtils;
import com.vispect.android.vispect_g2_app.utils.XuLog;
import com.vispect.android.vispect_g2_app.utils.XuView;


/**
 * 设置列表用到的adapter
 * Created by xu
 */
public class SettingMenuAdapter extends BaseAdapter {

    private String[] mune_name = {};
    private Context context;

    private boolean isupdataing = false;
    private int[] updatas;
    private int[] hasUpdates;
    private int progressValue;

    private boolean islastpage = false;

    boolean showIco = false;
    boolean showVersionName = false;
    boolean showDownorUpdate = false;
    String versionNmae = "";
    String btnText = "";



    public SettingMenuAdapter(Context context, String[] mune_name) {
        this.context = context;
        this.mune_name = mune_name;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return mune_name.length;
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        if (position < 0) {
            XuLog.e("连接设备的ViewPagerAdapter的getItem出现问题，position = " + position);
            return mune_name[0];
        }
        if (position > mune_name.length - 1) {
            XuLog.e("连接设备的ViewPagerAdapter的getItem出现问题，position = " + position);
            return mune_name[mune_name.length - 1];
        }
        return mune_name[position];
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
        if (convertView == null) {// 如果是第一次显示该页面(要记得保存到viewholder中供下次直接从缓存中调用)
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.item_connect_device2, null);
            holder.menu_name = (TextView) convertView.findViewById(R.id.tv_item_connect_device_name);
            holder.iv_arrow = (ImageView) convertView.findViewById(R.id.iv_arrow);
            holder.uptaprogressbar = (ProgressBar) convertView.findViewById(R.id.updata_progress);
            holder.iv_newversionname = (TextView) convertView.findViewById(R.id.iv_newversionname);
            holder.iv_new = (ImageView) convertView.findViewById(R.id.iv_new);
            holder.btn_downorupdate = (TextView) convertView.findViewById(R.id.btn_downorupdate);


            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if( mune_name[position].indexOf(";") != -1){
            String[] values = mune_name[position].split(";");
            switch (Integer.parseInt(values[1])){
                case 0:
                    showVersionName = false;
                    showIco = false;
                    showDownorUpdate = false;
                    break;
                case 1:
                    if(XuFileUtils.hasUpdateFileForMD5(values[3], values[4])){
                        showButtion(context.getResources().getString(R.string.update2));
                        mune_name[position] = values[0]+";"+"2"+";"+values[2]+";"+values[3]+";"+values[4];
                    }else{
                        showHasNewVersion(values[2]);
                        hideButtion();
                        mune_name[position] = values[0]+";"+"1"+";"+values[2]+";"+values[3]+";"+values[4];
                    }
                    break;
                case 2:
                    if(XuFileUtils.hasUpdateFileForMD5(values[3], values[4])) {
                        showButtion(context.getResources().getString(R.string.update2));
                        mune_name[position] = values[0]+";"+"2"+";"+values[2]+";"+values[3]+";"+values[4];
                    }else{
                        showHasNewVersion(values[2]);
                        hideButtion();
                        mune_name[position] = values[0]+";"+"1"+";"+values[2]+";"+values[3]+";"+values[4];
                    }
                    break;
            }
            XuView.setViewVisible(holder.iv_newversionname, showVersionName);
            XuView.setViewVisible(holder.iv_new,showIco);
            XuView.setViewVisible(holder.btn_downorupdate,showDownorUpdate);
            holder.iv_newversionname.setText(versionNmae);
            holder.btn_downorupdate.setText(btnText);


            if (isupdataing && updatas != null && updatas.length > 0) {
                for (int i : updatas) {
                    if (i == position) {
                        holder.menu_name.setText(values[0] + "    " + progressValue + "%");
                        holder.uptaprogressbar.setVisibility(View.VISIBLE);
                    }else{
                        holder.uptaprogressbar.setVisibility(View.GONE);
                    }
                }
            } else {
                holder.menu_name.setText(values[0]);
                holder.uptaprogressbar.setVisibility(View.GONE);
            }


        }else{
            XuView.setViewVisible(holder.iv_newversionname,false);
            XuView.setViewVisible(holder.iv_new,false);
            XuView.setViewVisible(holder.btn_downorupdate,false);


            if (isupdataing && updatas != null && updatas.length > 0) {
                for (int i : updatas) {
                    if (i == position) {
                        holder.menu_name.setText(mune_name[position] + "    " + progressValue + "%");
                        holder.uptaprogressbar.setVisibility(View.VISIBLE);
                    }else{
                        holder.uptaprogressbar.setVisibility(View.GONE);
                    }
                }
            } else {
                holder.menu_name.setText(mune_name[position]);
                holder.uptaprogressbar.setVisibility(View.GONE);
            }


        }

        XuView.setViewVisible(holder.iv_arrow, !islastpage);


        holder.uptaprogressbar.setProgress(progressValue);
//        XuView.setViewVisible(holder.btn_downorupdate, false);
        return convertView;
    }



    public void refreshData(String[] mune_name) {
        this.mune_name = mune_name;
        this.notifyDataSetChanged();
    }
    public void refreshData() {
        this.notifyDataSetChanged();
    }
    public void setIsupdataing(boolean isupdataing) {
        this.isupdataing = isupdataing;
    }

    public boolean getisupdataing() {
        return isupdataing;
    }

    public void setUpdatas(int[] updatas) {
        this.updatas = updatas;
    }

    public boolean islastpage() {
        return islastpage;
    }

    public void setIslastpage(boolean islastpage) {
        this.islastpage = islastpage;
    }

    public void setProgressValue(int progressValue) {
        this.progressValue = progressValue;
    }

    public String[] getMune_name() {
        return mune_name;
    }

    public void showHasNewVersion(String versionNmae){
        showIco = true;
        showVersionName = true;
        this.versionNmae = versionNmae;
    }
    public void hideHasNewVersion(){
        showIco = false;
        showVersionName = false;
    }
    public void showButtion(String btnText){
        showIco = true;
        showVersionName = false;
        showDownorUpdate = true;
        this.btnText = btnText;
    }
    public void hideButtion(){
        showDownorUpdate = false;
    }

    private class ViewHolder {

        TextView menu_name;
        ImageView iv_arrow;
        ProgressBar uptaprogressbar;
        TextView iv_newversionname;
        ImageView iv_new;
        TextView btn_downorupdate;

    }

}
