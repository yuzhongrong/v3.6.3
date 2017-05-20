package com.jinr.core;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;

import com.umeng.message.UTrack;
import com.umeng.message.UmengMessageService;
import com.umeng.message.entity.UMessage;

import org.android.agoo.common.AgooConstants;
import org.json.JSONObject;

import java.util.Map;

import model.UmMessageItem;

public class MyPushIntentService extends UmengMessageService {
    private static final String TAG = MyPushIntentService.class.getName();

    @Override
    public void onMessage(Context context, Intent intent) {
        try {
            // 可以通过MESSAGE_BODY取得消息体
            String message = intent.getStringExtra(AgooConstants.MESSAGE_BODY);
            UMessage msg = new UMessage(new JSONObject(message));
            // UmLog.d(TAG, "message=" + message); // 消息体
            // UmLog.d(TAG, "custom=" + msg.custom); // 自定义消息的内容
            // UmLog.d(TAG, "title=" + msg.title); // 通知标题
            // UmLog.d(TAG, "text=" + msg.text); // 通知内容
            // 消息处理
            Map<String, String> extra = msg.extra;
            long id = 0;
            if (extra.get("id") != null && !extra.get("id").equals("")) {
                id = Long.parseLong(extra.get("id"));// 展示情况WAP
            }
            String type = extra.get("type");
            // 和直接activity展示
            Intent intentAct = new Intent();
            intentAct.setClass(context, MainActivity.class);
            intentAct.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            Bundle bundle = new Bundle();
            UmMessageItem item = new UmMessageItem();// 自定义的消息bean
            item.setMsmType("PUSH");
            item.setMsmcontent(msg.text);// 获取推送的消息内容
            item.setTitle(msg.title);// 获取推送的消息标题
            item.setDisplayType(type);
            item.setId(id);
            // if (displayType.equals("DISPLAYONAPP")) {// 手机端展示
            // item.setDisplayType("DISPLAYONAPP");
            // } else if (displayType.equals("DISPLAYONWAP")) {// 打开指定网页
            // item.setDisplayType("DISPLAYONWAP");
            // item.setOtherParams(extra.get("otherParams"));//
            // 将整个自定义参数传出去，在需要的地方处理
            // } else {
            // System.out.println("推送消息类型错误！");
            // }
            bundle.putSerializable("message", item);// 传递一个序列化参数
            intentAct.putExtras(bundle);
            showNotification(context, msg, intentAct);// 必须要有，不然收不到推送的消息
            // 完全自定义消息的处理方式，点击或者忽略
            boolean isClickOrDismissed = true;
            if (isClickOrDismissed) {
                // 完全自定义消息的点击统计
                UTrack.getInstance(getApplicationContext()).trackMsgClick(msg);
            } else {
                // 完全自定义消息的忽略统计
                UTrack.getInstance(getApplicationContext()).trackMsgDismissed(msg);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showNotification(Context context, UMessage msg, Intent intent) {
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setAutoCancel(true);
        Notification mNotification = builder.build();
        mNotification.icon = R.drawable.ic_launcher;// notification通知栏图标
        mNotification.defaults |= Notification.DEFAULT_SOUND;
        mNotification.defaults |= Notification.DEFAULT_VIBRATE;
        mNotification.tickerText = msg.ticker;
        // 自定义布局
        RemoteViews contentView = new RemoteViews(getPackageName(), R.layout.notification_view);
        contentView.setImageViewResource(R.id.notification_large_icon, R.drawable.ic_launcher);
        contentView.setTextViewText(R.id.notification_title, msg.title);
        contentView.setTextViewText(R.id.notification_text, msg.text);
        mNotification.contentView = contentView;
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);// 不是Intent
        // mNotification.flags = Notification.FLAG_NO_CLEAR;// 永久在通知栏里
        mNotification.flags = Notification.FLAG_AUTO_CANCEL;
        // 使用自定义下拉视图时，不需要再调用setLatestEventInfo()方法，但是必须定义contentIntent
        mNotification.contentIntent = contentIntent;
        mNotificationManager.notify(1003, mNotification);
    }
}
