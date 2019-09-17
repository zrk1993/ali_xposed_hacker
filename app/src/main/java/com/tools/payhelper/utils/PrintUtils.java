package com.tools.payhelper.utils;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class PrintUtils {
    public static void printBundle(Bundle bundle) {
        for (String key : bundle.keySet()) {
            MyLogger.d("bundle.key: " + key + ", value: " + bundle.get(key));
        }
    }
    public static void printTreeView(Activity activity) {
        View rootView = activity.getWindow().getDecorView();
        printTreeView(rootView);
    }
    public static void printTreeView(View rootView) {
        if (rootView instanceof ViewGroup) {
            ViewGroup parentView = (ViewGroup) rootView;
            for (int i = 0; i < parentView.getChildCount(); i++) {
                printTreeView(parentView.getChildAt(i));
            }
        } else {
            MyLogger.d("view: " + rootView.getId() + ", class: " + rootView.getClass());
            // any view if you want something different
            if (rootView instanceof EditText) {
                MyLogger.d("edit:" + rootView.getTag()
                        + "， hint: " + ((EditText) rootView).getHint());
            } else if (rootView instanceof TextView) {
                MyLogger.d("text:" + ((TextView) rootView).getText().toString());
            }
        }
    }
    public static void printMethods(Class clazz) {
        for (Method method : clazz.getDeclaredMethods()) {
            MyLogger.d("" + method);
        }
    }
    public static void printFields(Class clazz) {
        for (Field field : clazz.getFields()) {
            MyLogger.d("" + field);
        }
    }


    private void dumpClass(Class actions) {
        Log.i("kun","Dump class " + actions.getName());

        Log.i("kun","Methods");
        Method[] m = actions.getDeclaredMethods();
        for (int i = 0; i < m.length; i++) {
            Log.i("kun",m[i].toString());
        }
        Log.i("kun","Fields");
        Field[] f = actions.getDeclaredFields();
        for (int j = 0; j < f.length; j++) {
            Log.i("kun",f[j].toString());
        }
        Log.i("kun","Classes");
        Class[] c = actions.getDeclaredClasses();
        for (int k = 0; k < c.length; k++) {
            Log.i("kun",c[k].toString());
        }
    }
}
