package com.jinr.core.base;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.jinr.core.MainActivity;
import com.jinr.core.config.AppManager;
import com.jinr.core.utils.MyLog;
import com.umeng.analytics.MobclickAgent;


public abstract class BaseFrgmtActivity extends FragmentActivity {
    public static final String TAG = "BaseActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppManager.getAppManager().addActivity(this);
        MyLog.d("BaseActivity", "BaseActivity");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppManager.getAppManager().finishActivity(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
        if (MainActivity.homekey) {// 如果在监听到home键的情况下进入，则启用图形锁
            MainActivity.instance.gotoLockPattern();
            MainActivity.homekey = false;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        MobclickAgent.onPause(this);
    }

    /**
     * 描述：数据初始化
     */
    protected abstract void initData();

    /**
     * 描述：渲染界面
     */
    protected abstract void findViewById();

    /**
     * 描述：界面初始化
     */
    protected abstract void initUI();

    /**
     * 描述：设置监听
     */
    protected abstract void setListener();

}
