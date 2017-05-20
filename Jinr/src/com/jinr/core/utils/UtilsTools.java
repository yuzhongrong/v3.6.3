package com.jinr.core.utils;

import java.io.InputStream;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.LocationManager;
import android.provider.Settings;

public class UtilsTools {
    /**
     * 单位转换 sp dp 与 px 之间的转换 @author Ysw created at 2016/4/15 11:35
     */

    // 将 dp 转换成为 px @author Ysw created at 2016/4/12 11:49
    public static int Dp2Px(Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    // 将 px 转换为dp @author Ysw created at 2016/4/12 11:49
    public static int Px2Dp(Context context, float px) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (px / scale + 0.5f);
    }

    // 将 px 转换成 sp @author Ysw created at 2016/4/12 17:54
    public static int px2sp(Context context, float pxValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }

    // 将 sp 转换成 px @author Ysw created at 2016/4/12 17:55
    public static int Sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    /**
     * 获取 Bitmap 对象的方法，通过资源 ID Drawable 等 @author Ysw created at 2016/4/12 17:58
     */
    // 获取图片资源方法一 @author Ysw created at 2016/4/12 14:46
    public static Bitmap getBitmap(Context context, int id, int i) {
        Resources rec = context.getResources();
        InputStream in = rec.openRawResource(id);
        Bitmap bitmap = BitmapFactory.decodeStream(in);
        return bitmap;
    }

    // 获取图片资源方法二 @author Ysw created at 2016/4/12 15:35
    public static Bitmap getBitmap(Context context, int id, String str) {
        Resources rec = context.getResources();
        BitmapDrawable bitmapDrawable = (BitmapDrawable) rec.getDrawable(id);
        Bitmap bitmap = bitmapDrawable.getBitmap();
        return bitmap;
    }

    // 获取图片资源方法三 @author Ysw created at 2016/4/12 15:36
    public static Bitmap getBitmap(Context context, int id) {
        Resources rec = context.getResources();
        InputStream in = rec.openRawResource(id);
        BitmapDrawable bitmapDrawable = new BitmapDrawable(in);
        Bitmap bitmap = bitmapDrawable.getBitmap();
        return bitmap;
    }

    // 将 Drawable 转换成 Bitmap @author Ysw created at 2016/4/12 17:49
    public static Bitmap getBitmap(Drawable drawable) {
        BitmapDrawable bd = (BitmapDrawable) drawable;
        Bitmap bitmap = bd.getBitmap();
        return bitmap;
    }

    /**
     * 监听GPS
     */
    public static boolean isOpenGPS(Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        // 判断GPS模块是否开启，如果没有则开启
        if (!locationManager
                .isProviderEnabled(android.location.LocationManager.GPS_PROVIDER)) {
            return true;
        } else {
            return false;
        }
    }
}
