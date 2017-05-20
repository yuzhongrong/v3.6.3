package com.jinr.core;

import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.util.Log;
import android.widget.Toast;

import com.android.jnet.Jnetapp;
import com.jinr.core.config.EventBusKey;
import com.jinr.core.utils.AuthImageDownloader;
import com.jinr.core.utils.GetImsi;
import com.jinr.core.utils.PreferencesUtils;
import com.lzy.okgo.OkGo;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.umeng.message.IUmengRegisterCallback;
import com.umeng.message.PushAgent;
import com.umeng.message.UTrack;
import com.umeng.message.UmengMessageHandler;
import com.umeng.message.UmengNotificationClickHandler;
import com.umeng.message.entity.UMessage;

import org.simple.eventbus.EventBus;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import model.UmMessageItem;

public class JinrApp extends Jnetapp {
    private static JinrApp instance;
    public int state_bankBind;
    public int state_tradePassword;
    public boolean is_fresh;
    public String my_ip;
    public String app_ver;
    public String g_imei;
    public PushAgent mPushAgent;
    public static final String UPDATE_STATUS_ACTION = "com.jinr.core.UPDATE_STATUS";
    public static long timer = 60000;
    public Vibrator mVibrator;

    @Override
    public void onCreate() {
        super.onCreate();
        EventBus.getDefault().register(this);
        instance = this;
        state_bankBind = -1;
        state_tradePassword = -1;
        // ImageLoader初始化
        DisplayImageOptions dispOptions = new DisplayImageOptions.Builder().cacheOnDisk(true)
                .imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                this)
                .threadPoolSize(3)
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory()
                .memoryCache(new WeakMemoryCache())
                .memoryCacheSizePercentage(13)
                .memoryCacheSize(2 * 1024 * 1024)
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .defaultDisplayImageOptions(dispOptions)
                .imageDownloader(new AuthImageDownloader(this))
                .build();// 开始构建
        ImageLoader.getInstance().init(config);// 全局初始化此配置

        app_ver = getVersion();
        g_imei = GetImsi.getImeiAll(getApplicationContext());
        mPushAgent = PushAgent.getInstance(this);
        // 注册推送服务，每次调用register方法都会回调该接口
        mPushAgent.register(new IUmengRegisterCallback() {

            @Override
            public void onSuccess(String deviceToken) {
                // 注册成功会返回device token
                // sendBroadcast(new Intent(UPDATE_STATUS_ACTION));
                Log.d("JinrApp", "deviceToken:" + deviceToken);
                PreferencesUtils.putStringToSPMap(PreferencesUtils.Keys.DEVICE_TOKEN,deviceToken);
            }

            @Override
            public void onFailure(String s, String s1) {

            }
        });

        UmengMessageHandler messageHandler = new UmengMessageHandler() {
            @Override
            public void dealWithCustomMessage(final Context context, final UMessage msg) {

                Log.d("JinrApp", "dealWithCustomMessage:");

                new Handler().post(new Runnable() {

                    @Override
                    public void run() {
                        // 对自定义消息的处理方式，点击或者忽略
                        boolean isClickOrDismissed = true;
                        if (isClickOrDismissed) {
                            // 统计自定义消息的打开
                            UTrack.getInstance(getApplicationContext()).trackMsgClick(msg);
                        } else {
                            // 统计自定义消息的忽略
                            UTrack.getInstance(getApplicationContext()).trackMsgDismissed(msg);
                        }
                        Toast.makeText(context, msg.custom, Toast.LENGTH_LONG).show();
                    }
                });

                //弹出被t下线对话框

                EventBus.getDefault().post(null,EventBusKey.T_LINE);



            }

            // 自定义通知样式
            @Override
            public Notification getNotification(Context context, UMessage msg) {
                Log.d("JinrApp", "getNotification:");
                return super.getNotification(context, msg);
            }
        };
        mPushAgent.setMessageHandler(messageHandler);
        UmengNotificationClickHandler notificationClickHandler = new UmengNotificationClickHandler() {
            // 点击通知的自定义行为
            @Override
            public void dealWithCustomAction(Context context, UMessage msg) {
                Intent intentAct = new Intent();
                intentAct.setClass(context, MainActivity.class);
                intentAct.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                Map<String, String> extra = msg.extra;
                long id = 0;
                if (extra.get("id") != null && !extra.get("id").equals("")) {
                    id = Long.parseLong(extra.get("id"));// 展示情况WAP
                }
                String type = extra.get("type");
                Bundle bundle = new Bundle();
                UmMessageItem item = new UmMessageItem();// 自定义的消息bean
                item.setMsmType("PUSH");
                item.setMsmcontent(msg.text);// 获取推送的消息内容
                item.setTitle(msg.title);// 获取推送的消息标题
                item.setDisplayType(type);
                item.setId(id);
                bundle.putSerializable("message", item);// 传递一个序列化参数
                intentAct.putExtras(bundle);
                startActivity(intentAct);
            }
        };
        mPushAgent.setNotificationClickHandler(notificationClickHandler);
    }

    public String getVersion() {
        try {
            PackageManager manager = this.getPackageManager();
            PackageInfo info = manager.getPackageInfo(this.getPackageName(), 0);
            String version = info.versionName;
            return version;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static  synchronized JinrApp instance() {
        return instance;
    }

    public String getPrivateKey() {
        try {
            AssetManager assetManager = getAssets();
            InputStream is = assetManager.open("nodes-testmerch.key");
            return readTextFile(is);
        } catch (Exception e) {
        }

        return null;
    }

    private String readTextFile(InputStream inputStream) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte buf[] = new byte[1024];
        int len;
        try {
            while ((len = inputStream.read(buf)) != -1) {
                outputStream.write(buf, 0, len);
            }
            outputStream.close();
            inputStream.close();
        } catch (IOException e) {
        }
        return outputStream.toString();
    }


    @Override
    public void initOkHttp() {
        OkGo.init(this);
        try {

            OkGo.getInstance()
                    .debug("OKGO")
                    .setConnectTimeout(OkGo.DEFAULT_MILLISECONDS)
                    .setReadTimeOut(OkGo.DEFAULT_MILLISECONDS)
                    .setWriteTimeOut(OkGo.DEFAULT_MILLISECONDS);
                   // .setCertificates(getAssets().open("jinr_api.bks"));//取消证书验证


        }catch (Exception e){
            e.printStackTrace();
            e.printStackTrace();
        }


    }



}
