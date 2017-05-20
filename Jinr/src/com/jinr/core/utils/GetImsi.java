package com.jinr.core.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.telephony.TelephonyManager;

public class GetImsi {
    /**
     * 无卡或者获取失败返回000000
     *
     * @param context
     * @return
     */
    @SuppressLint("UseValueOf")
    public static String getImsiAll(Context context) {
        String imsi = "";
        try {
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            //普通方法获取
            imsi = tm.getSubscriberId();
            return imsi;
        } catch (Exception e) {
            return "000000";
        }
    }

    @SuppressLint("UseValueOf")
    public static String getImeiAll(Context context) {
        String imsi = "";
        try {
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            imsi = tm.getDeviceId();
            return imsi;
        } catch (Exception e) {
            return "000000";
        }
    }

    public static String getLineNumber(Context context) {
        String line = "";
        try {
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            line = tm.getSimSerialNumber();
            return line;
        } catch (Exception e) {
            return "000000";
        }
    }

    public static String getVersion(Context context) {
        String version = "";
        PackageManager packageManager = context.getPackageManager();
        PackageInfo packInfo = null;
        try {
            packInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            version = packInfo.versionName;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return version;
    }
}
