package com.jinr.core.config;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.text.TextUtils;

import com.jinr.core.utils.MyLog;

import java.util.ArrayList;
import java.util.List;


/**
 * AppManager.java
 * description:Activity管理类，用于管理Activity的退出
 *
 * @author Andrew Lee
 *         version
 *         2014-10-16 上午8:43:03
 */
public class AppManager {
    public static final String TAG = "AppManager";

    private static List<Activity> activityStack;
    private static AppManager instance;

    private AppManager() {
    }

    /**
     * 单一实例
     */
    public static AppManager getAppManager() {
        if (instance == null) {
            instance = new AppManager();
        }
        return instance;
    }

    public static void ActivityPackageName() {
        for (int i = 0; i < activityStack.size(); i++) {
            MyLog.e(TAG, "PackageName: i = " + i + "," + activityStack.get(i).toString());
        }
    }

    /**
     * 添加Activity到堆栈
     */
    public void addActivity(Activity activity) {
        MyLog.e("packageName", activity.toString());
        if (activityStack == null) {
            activityStack = new ArrayList<>();
        }
        activityStack.add(activity);
    }

    /**
     * 获取当前Activity（堆栈中最后一个压入的）
     */
    public Activity currentActivity() {
        Activity activity = activityStack.get(activityStack.size() - 1);
        return activity;
    }

    /**
     * 结束当前Activity（堆栈中最后一个压入的）
     */
    public void finishActivity() {
        Activity activity = activityStack.get(activityStack.size() - 1);
        finishActivity(activity);
    }

    /**
     * 结束指定的Activity
     */
    public void finishActivity(Activity activity) {
        if (activity != null) {
            activityStack.remove(activity);
            activity.finish();
            activity = null;
        }
    }

    /**
     * 结束指定类名的Activity
     */
    public void finishActivity(Class<?> cls) {
        List<Activity> activityStacks = activityStack;
        for (Activity activity : activityStacks) {
            if (activity.getClass().equals(cls)) {
                finishActivity(activity);
            }
        }
        //新建一个stack，临时记录要删除的，然后再删除, 两个for遍历，或者用list
    }

    /**
     * 结束所有Activity
     */
    public void finishAllActivity() {
        for (int i = 0, size = activityStack.size(); i < size; i++) {
            if (null != activityStack.get(i)) {
                activityStack.get(i).finish();
            }
        }
        activityStack.clear();
    }

    /**
     * 退出应用程序
     */
    public void AppExit(Context context) {
        try {
            finishAllActivity();
            ActivityManager activityMgr = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            activityMgr.killBackgroundProcesses(context.getPackageName());
            System.exit(0);
        } catch (Exception e) {
        }
    }

    public static boolean isServiceRunning(Context context, String className) {
        boolean isRunning = false;
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> serviceList = activityManager.getRunningServices(Integer.MAX_VALUE);
        if (serviceList == null || serviceList.isEmpty())
            return false;
        for (int i = 0; i < serviceList.size(); i++) {
            if (serviceList.get(i).service.getClassName().equals(className) && TextUtils.equals(
                    serviceList.get(i).service.getPackageName(), context.getPackageName())) {
                isRunning = true;
                break;
            }
        }
        return isRunning;
    }
}