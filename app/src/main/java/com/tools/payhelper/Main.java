package com.tools.payhelper;

import java.io.File;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Enumeration;

import com.tools.payhelper.utils.MyLogger;
import com.tools.payhelper.utils.PayHelperUtils;
import com.tools.payhelper.utils.PrintUtils;
import com.tools.payhelper.utils.QQDBManager;

import android.app.Activity;
import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONObject;

import dalvik.system.BaseDexClassLoader;
import dalvik.system.DexFile;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * 

* @ClassName: Main

* @Description: TODO(这里用一句话描述这个类的作用)

* @author xinyu126

* @date 2018年6月23日 下午1:26:26

*
 */
public class Main implements IXposedHookLoadPackage {
	public static String ALIPAY_PACKAGE = "com.eg.android.AlipayGphone";
	public static String QQ_PACKAGE = "com.tencent.mobileqq";
	public static boolean ALIPAY_PACKAGE_ISHOOK = false;
	
	
	public void handleLoadPackage(final XC_LoadPackage.LoadPackageParam lpparam)
			throws Throwable {
		MainActivity.sendmsg("xposed 启动0");
		if (lpparam.appInfo == null || (lpparam.appInfo.flags & (ApplicationInfo.FLAG_SYSTEM |
                ApplicationInfo.FLAG_UPDATED_SYSTEM_APP)) != 0) {
            return;
        }
		MainActivity.sendmsg("xposed 启动1");
		final String packageName = lpparam.packageName;
        final String processName = lpparam.processName;


        if(ALIPAY_PACKAGE.equals(packageName)){
    		try {
				MyLogger.i("handleLoadPackage->ALIPAY_PACKAGE: " + packageName);
                XposedHelpers.findAndHookMethod(Application.class, "attach", Context.class, new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(XC_MethodHook.MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);
                        Context context = (Context) param.args[0];
                        ClassLoader appClassLoader = context.getClassLoader();

                        if(ALIPAY_PACKAGE.equals(processName) && !ALIPAY_PACKAGE_ISHOOK){
                        	ALIPAY_PACKAGE_ISHOOK=true;
                        	PayHelperUtils.sendmsg(context, "支付宝Hook成功，当前支付宝版本:"+PayHelperUtils.getVerName(context));
                        	new AlipayHook().hook(appClassLoader,context);
                        }
                    }
                });

    		}catch (Throwable e) {
                XposedBridge.log(e);
            }
        }
	}


}
