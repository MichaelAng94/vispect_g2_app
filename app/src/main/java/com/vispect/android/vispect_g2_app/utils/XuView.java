package com.vispect.android.vispect_g2_app.utils;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.vispect.android.vispect_g2_app.ui.widget.NotconnectDialog;


/**
 * View工具类 
 * Created by xu on 2016/03/14.
 */
public class XuView {

	/**
	 * 设置view可见性(GONE)
	 *
	 * @param view
	 * @param visible
	 */
	public static void setViewVisible(View view, boolean visible) {
		if (null == view) {
			return;
		}
		view.setVisibility(visible ? View.VISIBLE : View.GONE);
	}


	/**
	 * 设置view可见性 (INVISIBLE)
	 *
	 * @param view
	 * @param visible
	 */
	public static void setViewVisibleByInvsisibleE(View view, boolean visible) {
		if (null == view) {
			return;
		}
		view.setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
	}

	/**
	 * 重设View的LayoutParams
	 *
	 * @param view
	 * @param width
	 * @param height
	 */
	public static void updateLayoutParams(View view, int width, int height) {
		if (null == view) {
			return;
		}

		ViewGroup.LayoutParams params = view.getLayoutParams();
		params.height = height;
		params.width = width;
		view.setLayoutParams(params);
	}

	/**
	 * 获取textView的文字
	 *
	 * @param textView
	 * @return
	 */
	public static String getText(TextView textView) {
		return textView == null ? null : textView.getText().toString();
	}

	/**
	 * 设置textView的文字
	 *
	 * @param view
	 * @return
	 */
	public static void setText(View view, String content) {
		TextView textview = (TextView)view;
		textview.setText(content);
	}


	/**
	 * @param textView
	 * @return textView里的文字是否为null，""，或者"            "
	 */
	public static boolean isEmpty(TextView textView) {
		String text = getText(textView);
		return TextUtils.isEmpty(text) || TextUtils.isEmpty(text.trim());
	}

	public static void showErroDialog(Context context, String title){
		try{
			NotconnectDialog.Builder builder = new NotconnectDialog.Builder(context);
			builder.setMessage(title);
			builder.create().show();
		}catch (Exception e){
			e.printStackTrace();
		}
	}

	/** private constructor */
	private XuView() {
	}

}
