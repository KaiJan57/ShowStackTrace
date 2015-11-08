package com.kai_jan_57.showstacktrace.xposed;

import android.app.Application;
import android.content.Intent;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class Hook implements IXposedHookLoadPackage {
    public static final String INTENT_ACTION = "com.kai_jan_57.ShowStackTrace.CRASHRECEIVER";
    public static final String INTENT_PACKAGE_NAME = "PACKAGE_NAME";
    public static final String INTENT_TIME = "TIME";
    public static final String INTENT_THROWABLE = "THROWABLE";
    public static final String INTENT_SHOWDIALOG = "ShowDialog";

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        if("android".equals(lpparam.packageName) && "android".equals(lpparam.processName)) {
            XposedBridge.hookAllConstructors(XposedHelpers.findClass("com.android.server.am.AppErrorDialog", lpparam.classLoader), new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    XposedHelpers.setObjectField(param.thisObject, "DISMISS_TIMEOUT", 0);
                }
            });
        }

        XposedHelpers.findAndHookMethod(Application.class, "onCreate", new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                hookUncaughtException((Application) param.thisObject);
            }
        });
    }

    private void hookUncaughtException(final Application application) {
        Class<?> c = Thread.getDefaultUncaughtExceptionHandler().getClass();
        XposedHelpers.findAndHookMethod(c, "uncaughtException", Thread.class, Throwable.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                Intent intent = new Intent(INTENT_ACTION);
                intent.putExtra(INTENT_PACKAGE_NAME, application.getPackageName());
                intent.putExtra(INTENT_TIME, System.currentTimeMillis());
                intent.putExtra(INTENT_THROWABLE, (Throwable) param.args[1]);
                application.sendBroadcast(intent);
            }
        });
    }
}
