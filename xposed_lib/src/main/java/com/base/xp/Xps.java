package com.base.xp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import java.lang.reflect.Field;

import de.robv.android.xposed.XC_MethodHook;

/**
 * 本源码只限于学习交流使用，其用途于与原作者无关\nCreated by york on 10/06/2017.
 */

public class Xps {
    public static final String intentExtra2Str(Intent intent) {
        Bundle extras = intent.getExtras();
        if (extras == null) {
            return "has no extra";
        } else {
            StringBuilder sb = new StringBuilder();
            for (String s : extras.keySet()) {
                sb.append(s).append(" -> ").append(extras.get(s)).append(",").append("\t");
            }
            return sb.toString();
        }
    }

    public static void printStackTrace(String tag) {
        Log.i(tag, Log.getStackTraceString(new Throwable()));
    }

    public static void printMethod(XC_MethodHook.MethodHookParam param, String tag) {
        StringBuilder sb = new StringBuilder();
        sb.append(param.method.getName());
        sb.append("(");
        if (param.args != null) {
            for (int i = 0; i < param.args.length; i++) {
                if (i != 0) {
                    sb.append(", ");
                }

                Object arg = param.args[i];
                if (param.args[i] instanceof Object[]) {
                    Object[] vars = (Object[]) param.args[i];
                    sb.append("<").append(vars.length).append(">");
                    sb.append("[");
                    for (int i1 = 0; i1 < vars.length; i1++) {
                        if (i1 != 0) {
                            sb.append(", ");
                        }
                        sb.append(vars[i1]);
                    }
                    sb.append("]");
                } else {
                    sb.append(arg);
                }

            }
        }
        sb.append(")");
        if (param.getResult() != null) {
            sb.append(" rst: ").append(param.getResult());
        }
        sb.append("\n").append("Thread name: ").append(Thread.currentThread().getName());
        Log.i(tag, sb.toString());
    }

    public static void printIntent(String tag, Intent intent) {
        if (intent == null) {
            Log.i(tag, "intent in null");
        } else {
            Bundle extras = intent.getExtras();
            if (extras == null) {
                Log.i(tag, "extras is null");
            } else {
                for (String key : extras.keySet()) {
                    Log.i(tag, key + "->" + (extras.get(key) == null ? "null" : extras.get(key)));
                }
            }
        }
    }

    public static String toJson(Object obj) {
        StringBuilder sb = new StringBuilder();
        sb.append(obj.getClass().getName());
        sb.append("{");
        Field[] declaredFields = obj.getClass().getDeclaredFields();
        for (Field declaredField : declaredFields) {
            Class<?> type = declaredField.getType();
            if (isPrimary(type)) {
                declaredField.setAccessible(true);
                try {
                    sb.append(declaredField.getName()).append(" : ").append(declaredField.get(obj)).append(",");
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        sb.append("}");
        return sb.toString();
    }

    private static boolean isPrimary(Class type) {
        return type == int.class || type == Integer.class ||
                type == long.class || type == Long.class ||
                type == float.class || type == Float.class ||
                type == String.class;
    }
}
