package com.vispect.android.vispect_g2_app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.vispect.android.vispect_g2_app.R;
import com.vispect.android.vispect_g2_app.app.AppContext;
import com.vispect.android.vispect_g2_app.controller.UIHelper;
import com.vispect.android.vispect_g2_app.utils.XuLog;
import com.vispect.android.vispect_g2_app.utils.XuView;

import java.io.File;
import java.util.ArrayList;

import bean.DVRInfo;
import bean.G2DVRInfo;
import interf.DrivingVideoOperationListener;

/**
 * DVR列表用到的adapter
 * Created by xu
 */
public class DrivingVideosAdapter extends BaseAdapter {

	private ArrayList<G2DVRInfo> videos;
	private Context context;
	private boolean isedit = false; // 是否正处在编辑状态
	private ArrayList<String> list =  new ArrayList<String>();//已存在的视频

	public ArrayList<String> checkable_list =  new ArrayList<String>();//已选中的视频



	private DrivingVideoOperationListener drivingVideoOperationListener = new DrivingVideoOperationListener() {
		@Override
		public void onGetVideoList(ArrayList arrayList) {

		}

		@Override
		public void onLockOrUnlockResult(boolean b) {

		}

		@Override
		public void onLast() {

		}
	};


	public DrivingVideosAdapter(Context context, ArrayList<G2DVRInfo> videos) {
		this.context = context;
		this.videos = videos;
		check_loaddown();

	}

	public void check_loaddown(){
		list =  new ArrayList<String>();
		File[] files = new File(AppContext.getInstance().getDeviceHelper().getDownloadDir()).listFiles();
		if(files == null || files.length < 1){
			return;
		}
		for (File file : files) {
			if (file.getName().length() > 4 && file.getName().endsWith(".mp4")) {
				list.add(file.getName().substring(0, file.getName().length() - 4));

			}
		}
	}

	public void clear_loaddown(){
		if(list != null){
			list.clear();
		}
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return videos.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		if (position < 0) {
			XuLog.e("行车记录视频的getItem出现问题，position = " + position);
			return videos.get(position);
		}
		if (position > videos.size() - 1) {
			XuLog.e("行车记录的getItem出现问题，position = " + position);
			return videos.get(videos.size() - 1);
		}
		return videos.get(position);
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
		if (convertView == null) {
		// 如果是第一次显示该页面(要记得保存到viewholder中供下次直接从缓存中调用)
			holder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(R.layout.item_drivingvideos, null);
			holder.vide_time = (TextView) convertView.findViewById(R.id.tv_item_video_time);
			holder.vide_ischeck = (CheckBox) convertView.findViewById(R.id.checkischeck);
			holder.vide_islock = (ImageView) convertView.findViewById(R.id.iv_item_video_lock);
			holder.vide_play = (ImageView) convertView.findViewById(R.id.iv_video_play);
			holder.downprogressbar = (ProgressBar) convertView.findViewById(R.id.downloadr_progress);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}


		final G2DVRInfo videosinfo = videos.get(position);

		final String filename = videosinfo.getName().substring(0,4)+"-"+videosinfo.getName().substring(4,6)+"-"+videosinfo.getName().substring(6,8)+" "+videosinfo.getName().substring(8,10)+":"+videosinfo.getName().substring(10,12)+":"+videosinfo.getName().substring(12,14);

		if(videosinfo.isDownloading()){
			holder.vide_time.setText(filename + "       " + videosinfo.getProgressValue()+"%");
		}else{
			holder.vide_time.setText(filename);
		}


		XuView.setViewVisible(holder.vide_ischeck, isedit);
		final CheckBox check = holder.vide_ischeck;
		check.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				videosinfo.setCheck(check.isChecked());
				checkable_list.add(videos.get(position).getName());
				XuLog.d("DrivingVideosActivity", "选中状态发生变化    ：" + check.isChecked() + "          " + filename);
			}
		});

		if(checkable_list.indexOf(videos.get(position).getName()) != -1){
			videosinfo.setCheck(true);
		}

		holder.vide_ischeck.setChecked(videosinfo.isCheck());
//		XuLog.d("DrivingVideosActivity",filename+"       "+ videos.get(position).isCheck());


		holder.downprogressbar.setProgress(videosinfo.getProgressValue());

		XuView.setViewVisible(holder.vide_islock, videosinfo.getState().equals("1"));
		XuView.setViewVisible(holder.downprogressbar, videosinfo.isDownloading());
		XuView.setViewVisible(holder.vide_play, videosinfo.isHasLocalFile() && !videosinfo.isDownloading());



		holder.vide_play .setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				UIHelper.showVideoplayer(context, videosinfo.getName());
			}
		});

		return convertView;
	}

	public void refreshData(ArrayList<G2DVRInfo> videolist) {
		for (int i = 0;i<videolist.size();i++){
			String videoname = videolist.get(i).getName();
			for(String temp:list){
				if(temp.equals(videoname)){
					videolist.get(i).setHasLocalFile(true);
				}

			}
		}
		videos = videolist;
		this.notifyDataSetChanged();
	}

	public void setIsedit(boolean isedit) {
		this.isedit = isedit;
		clearCheckable();
		refreshData(videos);
	}

	public void saveCheckable() {
		for(int i=0;i<videos.size();i++){
			if(videos.get(i).isCheck()){
				checkable_list.add(videos.get(i).getName());
			}
		}
	}
	public void clearCheckable() {
		checkable_list.clear();
		if (videos != null && videos.size() > 0){
			for (G2DVRInfo temp : videos){
				if(temp.isCheck()){
					temp.setCheck(false);
				}
			}
		}
	}

	public boolean isIsedit() {
		return isedit;
	}



	public void setItemCheck(int position){
		if (position < 0) {
			XuLog.e("行车记录视频的getItem出现问题，position = " + position);
			return;
		}
		if (position > videos.size() - 1) {
			XuLog.e("行车记录的getItem出现问题，position = " + position);
			return;
		}
		videos.get(position).setCheck(true);
	}
	public void setloclorunlock(DrivingVideoOperationListener newdrivingVideoOperationListener) {
		// TODO Auto-generated method stub
		drivingVideoOperationListener = newdrivingVideoOperationListener;
		AppContext.getInstance().getCachedThreadPool().execute(new Runnable() {
			@Override
			public void run() {
				ArrayList<G2DVRInfo> temp_dvr = new ArrayList();
				for (G2DVRInfo tmep : videos) {
					if (tmep.isCheck()) {
						temp_dvr.add(tmep);
					}
				}
				if(temp_dvr.size() < 1){
					if(drivingVideoOperationListener != null){
						drivingVideoOperationListener.onLockOrUnlockResult(false);
					}
				}
				for(int i = 0 ;i < temp_dvr.size();i++){
					G2DVRInfo temp_ldvr = temp_dvr.get(i);
					if(i == temp_dvr.size() - 1){
						if (temp_ldvr.getState().equals("1")) {
							//发现已加锁
							temp_ldvr.setState("0");
//							AppContext.getInstance().getDeviceHelper()
//									.lockDVR(temp_ldvr, false, drivingVideoOperationListener);

						} else {
							//发现未加锁
							temp_ldvr.setState("1");
//							AppContext.getInstance().getDeviceHelper()
//									.lockDVR(temp_ldvr, true, drivingVideoOperationListener);

						}
					}else{
						if (temp_ldvr.getState().equals("1")) {
							//发现已加锁
//							AppContext.getInstance().getDeviceHelper()
//									.lockDVR(temp_ldvr, false, new DrivingVideoOperationListener() {
//										@Override
//										public void onGetVideoList(ArrayList arrayList) {
//
//										}
//
//										@Override
//										public void onLockOrUnlockResult(boolean b) {
//
//										}
//
//										@Override
//										public void onLast() {
//
//										}
//									});
//							temp_ldvr.setState("0");
						} else {
							//发现未加锁
//							AppContext.getInstance().getDeviceHelper()
//									.lockDVR(temp_ldvr, true, new DrivingVideoOperationListener() {
//										@Override
//										public void onGetVideoList(ArrayList arrayList) {
//
//										}
//
//										@Override
//										public void onLockOrUnlockResult(boolean b) {
//
//										}
//
//										@Override
//										public void onLast() {
//
//										}
//									});
//							temp_ldvr.setState("1");
						}
					}
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		});

//
//		for (DVRInfo tmep : videos) {
//			if (tmep.isCheck()) {
//
//				if (tmep.getState().equals("1")) {
//					//发现已加锁
//				AppContext.getInstance().getDeviceHelper()
//						.lockDVR(tmep, false, drivingVideoOperationListener);
//					videos.get(videos.indexOf(tmep)).setState("0");
//				} else {
//					//发现未加锁
//					AppContext.getInstance().getDeviceHelper()
//							.lockDVR(tmep, true,drivingVideoOperationListener);
//					videos.get(videos.indexOf(tmep)).setState("1");
//				}
//				if(videos.indexOf(tmep) != -1){
//					videos.get(videos.indexOf(tmep)).setCheck(false);
//				}
//
//			}
//
//		}

	}

	public G2DVRInfo getItemVideo(int index) {
		return (G2DVRInfo)getItem(index);
	}


	private class ViewHolder {

		TextView vide_time;
		CheckBox vide_ischeck;
		ImageView vide_islock;
		ImageView vide_play;
		ProgressBar downprogressbar;

	}


}
