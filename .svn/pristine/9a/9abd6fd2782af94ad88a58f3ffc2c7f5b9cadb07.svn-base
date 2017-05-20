package com.jinr.core;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.jinr.core.base.BaseData;
import com.jinr.core.utils.FileUtil;
import com.jinr.core.utils.PreferencesUtils;
import com.jinr.core.utils.SendMobileCode;
import com.jinr.core.utils.UmUtils;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.Timer;
import java.util.TimerTask;

import model.AdvertModel;
import model.UmMessageItem;

public class AdvertisementActivity extends Activity implements OnClickListener {
    private ImageView secondImage = null;
    private FrameLayout frameLayout;
    private TextView textView;

    final Handler handler = new Handler();

    private Boolean flag = false;// 是否点击广告
    private Boolean skip = false;// 是否点击跳过

    private Timer mTimer = null;
    private TimerTask mTimerTask = null;
    private long delay = 4000;
    private AdvertModel advert;
    private int width;
    private Thread newThread;
    private boolean isShow = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);
        findViewById();
        setListener();
        initData();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.second_img:
                flag = true;
                skip = true;
                stopTimer();
                startTimer();
                break;
            case R.id.time_down:
                flag = false;
                skip = true;
                stopTimer();
                startTimer();
                break;
            default:
                break;
        }
    }

    /**
     * 打开timer
     */
    private void startTimer() {
        if (mTimer == null) {
            mTimer = new Timer();
        }

        if (mTimerTask == null) {
            mTimerTask = new TimerTask() {
                @Override
                public void run() {
                    Intent intent = new Intent();
                    if (flag) {
                        UmUtils.customsEvent(AdvertisementActivity.this, UmUtils.AD_CLICK, UmUtils.AD_HUM);
                        UmMessageItem item = new UmMessageItem();// 自定义的消息bean
                        item.setDisplayType("3");
                        long id = 0;
                        if (advert.getLinkid() != null && !advert.getLinkid().equals("")) {
                            id = Long.parseLong(advert.getLinkid());// 展示情况WAP
                        }
                        item.setId(id);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("message", item);
                        intent.putExtras(bundle);
                    } else {
                        UmUtils.customsEvent(AdvertisementActivity.this, UmUtils.AD_SHUT_CLICK, UmUtils.AD_SHUT_HUM);
                    }
                    intent.setClass(AdvertisementActivity.this, MainActivity.class);
                    startActivity(intent);
                    stopTimer();
                    finish();
                }
            };
        }

        if (skip) {// 如果点击按钮了，则重设delay的时间，从而使立即执行该操作
            delay = 100;
        }

        if (mTimer != null && mTimerTask != null)
            mTimer.schedule(mTimerTask, delay);
    }

    /**
     * 关闭timer
     */
    private void stopTimer() {

        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }

        if (mTimerTask != null) {
            mTimerTask.cancel();
            mTimerTask = null;
        }
    }

    protected void initData() {
        isShow = getIntent().getBooleanExtra("isShow", false);
        advert = (AdvertModel) getIntent().getSerializableExtra("advert");
        SendMobileCode.getInstance().goToActivity(AdvertisementActivity.this, textView);
        try {
            if (!isShow) {
                Bitmap bitmap = FileUtil.readContentSDcardImg(advert.getId());
                if (bitmap != null) {
                    secondImage.setImageBitmap(bitmap);
                }
            } else {
                ImageLoader.getInstance().displayImage(advert.getImg(), secondImage);
                PreferencesUtils.putCValueToSPMap(AdvertisementActivity.this, "advId", advert.getId());
                PreferencesUtils.putCValueToSPMap(AdvertisementActivity.this, "img", advert.getImg());
                PreferencesUtils.putCValueToSPMap(AdvertisementActivity.this, "linkId", advert.getLinkid());
                FileUtil.checkDir(BaseData.SD_CARD_PATH);
                newThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Bitmap savebitmap;
                        try {
                            savebitmap = (Bitmap) (FileUtil.getData(advert.getImg(), Bitmap.class));
                            if (savebitmap != null) {
                                FileUtil.delAllFiles(BaseData.SD_CARD_PATH);
                                FileUtil.saveImageToSDcard(savebitmap, BaseData.SD_CARD_PATH + advert.getId() + BaseData.SAVE_PHOTO_TYPE);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
                newThread.start(); // 启动线程
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        startTimer();
    }

    protected void findViewById() {
        textView = (TextView) findViewById(R.id.time_down);
        secondImage = (ImageView) findViewById(R.id.second_img);// 渐入的第二张图
        frameLayout = (FrameLayout) findViewById(R.id.guide_frame);
        width = getWindowManager().getDefaultDisplay().getWidth();
    }

    protected void setListener() {
        secondImage.setOnClickListener(this);
        frameLayout.setOnClickListener(this);
        textView.setOnClickListener(this);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        return false;
    }
}
