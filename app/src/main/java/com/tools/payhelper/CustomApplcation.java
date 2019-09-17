package com.tools.payhelper;


import android.app.Application;
import android.content.Context;
import android.util.Log;

/**
 * 

* @ClassName: CustomApplcation

* @Description: TODO(这里用一句话描述这个类的作用)

* @author xinyu126

* @date 2018年6月23日 下午1:26:02

*
 */

public class CustomApplcation extends Application {

	public static CustomApplcation mInstance;
	private static Context context;

	@Override
	public void onCreate() {
		super.onCreate();
		context = getApplicationContext();
		mInstance = this;
	}

	public static CustomApplcation getInstance() {
		return mInstance;
	}

	public static Context getContext() {
		return context;
	}
}
