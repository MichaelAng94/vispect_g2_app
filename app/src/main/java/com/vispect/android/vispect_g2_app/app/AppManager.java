package com.vispect.android.vispect_g2_app.app;

import android.app.Activity;

import com.vispect.android.vispect_g2_app.utils.XuLog;

import java.util.Stack;



/**
 * activity堆栈式管理
 *
 * @author xu
 * @created 2016年03月11日
 */
public class AppManager {
	private final static String TAG = AppManager.class.getSimpleName();

	private static Stack<Activity> activityStack;
	private static AppManager instance;

	private AppManager() {
	}

	public static AppManager getInstance() {
		if (instance == null) {
			instance = new AppManager();
			activityStack = new Stack<Activity>();
		}
		return instance;
	}

	/**
	 * 结束当前Activity（堆栈中最后一个压入的）
	 */
	public void finishActivity() {
		Activity activity = currentActivity();
		finishActivity(activity);
	}


	public void removeActivity(Activity activity){
		activityStack.remove(activity);
	}

	/**
	 * 退出指定的Activity
	 */
	public void finishActivity(Activity activity) {
		if(activity == null || activity.isFinishing()){
			XuLog.i(TAG, "the activity waht want to finish  is null or finishing     " + activity.toString());
		}
		if (activity != null && !activity.isFinishing()) {
			XuLog.i(TAG, "finish activity : " + activity.toString());
			//在从自定义集合中取出当前Activity时，也进行了Activity的finish操作
			activity.finish();
			activityStack.remove(activity);
		}
	}

	/**
	 * 退出指定类名的Activity
	 */
	public void finishActivity(Class<?> cls) {
		for (Activity activity : activityStack) {
			if (activity.getClass().equals(cls)) {
				finishActivity(activity);
				break;
			}
		}
	}


	/**
	 * 退出重复的指定类名的Activity 保留最靠近栈顶的一个
	 */
	public void finishRepeatableActivity(Class<?> cls) {
		//统计堆栈里指定类名的Activity的数量
		int count = 0;
		for (Activity activity : activityStack) {
			if (activity.getClass().equals(cls)) {
				count ++;
			}
		}
		//如果大于一个 则说明有重复的
		if(count > 1){
			for (Activity activity : activityStack) {
				if (activity.getClass().equals(cls)) {
					if(count == 1){
						break;
					}else{
						finishActivity(activity);
						count -- ;
					}

				}
			}
		}
	}

	/**
	 * 退回到首页
	 */
	public void finishActivityToindex() {
		for (int i = 1, size = activityStack.size(); i < size; i++) {
			XuLog.e(TAG, "退回到首页");
			finishActivity(currentActivity());
		}
	}

	/**
	 * 结束之前所有的Activity
	 */
	public void finishActivityToLast() {
		XuLog.e(TAG, "准备结束之前所有的Activity，保留栈顶的");
		for (int i = 1, size = activityStack.size(); i < size; i++) {
			finishActivity(currentActivity());
		}
	}

	/**
	 * 结束除了指定名称之外的所有的Activity
	 */
	public void finishActivityOutSideClass(Class<?> cls) {
		for (Activity activity : activityStack) {
			if (!activity.getClass().equals(cls)) {
				finishActivity(activity);
			}
		}
	}

	/**
	 * 结束所有Activity
	 */
	public void finishAllActivity() {
		for (int i = 0, size = activityStack.size(); i < size; i++) {
			finishActivity(beforeActivity());
		}
		activityStack.clear();
	}
	//
//	    /**
//	     * 退出栈中所有Activity
//	     */
//	    public void finishAllActivityExceptOne(Class cls) {
//	        for (; ; ) {
//	            Activity activity = currentActivity();
//	            if (activity == null) {
//	                break;
//	            }
//	            if (activity.getClass().equals(cls)) {
//	                break;
//	            }
//	            finishActivity(activity);
//	        }
//	    }

	/**
	 * 获取当前Activity（堆栈中最后一个压入的）
	 */
	public Activity currentActivity() {
		return activityStack.empty() ? null : activityStack.lastElement();
	}

	/**
	 * 获取第一个Activity（堆栈中第一个压入的）
	 */
	public Activity beforeActivity() {
		return activityStack.empty() ? null : activityStack.firstElement();
	}

	/**
	 * 添加Activity到堆栈
	 */
	public void addActivity(Activity activity) {
		activityStack.add(activity);
		XuLog.i(TAG, "add activity : " + activity.toString());
		logStack();
	}

	/**
	 * 获取指定的Activity
	 */
	public Activity getActivity(Class<?> cls) {
		for (Activity activity : activityStack) {
			if (activity.getClass().equals(cls)) {
				return activity;
			}
		}
		return null;
	}

	/**
	 * 退出应用程序
	 */
	public void appExit() {
		try {
			finishAllActivity();
			// 杀死该应用进程
			android.os.Process.killProcess(android.os.Process.myPid());
			System.exit(0);
		} catch (Exception e) {
			android.os.Process.killProcess(android.os.Process.myPid());
			System.exit(0);
		}
	}

	private void logStack() {
		XuLog.d(TAG, "==================stack begin=====================");
		for (int i = 0; i < activityStack.size(); i++) {
			XuLog.i(TAG, " activity" + i + " " + (activityStack.get(i) == null ? " activity is null" : activityStack.get(i).toString()));
		}
		XuLog.d(TAG, "==================stack end=======================");
	}
}
