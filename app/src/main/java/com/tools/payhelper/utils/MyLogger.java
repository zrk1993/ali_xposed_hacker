package com.tools.payhelper.utils;

import android.util.Log;

/**
 * Created by efan on 2017/4/13.
 */

public class MyLogger {

    //设为false关闭日志
    private static final boolean LOG_ENABLE = true;
    private static final String TAG = "alipay";

    public static void i(String msg){
        if (LOG_ENABLE){
            Log.i(TAG, msg);
        }
    }
    public static void v(String msg){
        if (LOG_ENABLE){
            Log.v(TAG, msg);
        }
    }
    public static void d(String msg){
        if (LOG_ENABLE){
            Log.d(TAG, msg);
        }
    }
    public static void w(String msg){
        if (LOG_ENABLE){
            Log.w(TAG, msg);
        }
    }
    public static void e(String msg){
        if (LOG_ENABLE){
            Log.e(TAG, msg);
        }
    }

}
