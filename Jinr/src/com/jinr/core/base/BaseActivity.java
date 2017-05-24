package com.jinr.core.base;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.inputmethod.InputMethodManager;

import com.jinr.core.MainActivity;
import com.jinr.core.utils.LoadingDialog;
import com.lzy.okgo.OkGo;
import com.umeng.analytics.MobclickAgent;
import com.umeng.message.PushAgent;

import org.simple.eventbus.EventBus;

import static com.jinr.core.config.AppManager.getAppManager;

/**
 * Base2Activity.java description:activity 基类 主要是对activity的管理
 *
 * @author Andrew Lee version 2014-10-16 上午8:41:34
 */
public abstract class BaseActivity extends AppCompatActivity{
    public static final String TAG = "BaseActivity";

    private InputMethodManager manager;

    private LoadingDialog waittingDialog;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getAppManager().addActivity(this);
        PushAgent.getInstance(this).onAppStart();
        manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        EventBus.getDefault().register(this);




    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);


        dismissWaittingDialog();
        getAppManager().finishActivity(this);
        OkGo.getInstance().cancelTag(this);

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (getCurrentFocus() != null && getCurrentFocus().getWindowToken() != null) {
                manager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
        return super.onTouchEvent(event);
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
     * 显示等候对话框
     */
    public void showWaittingDialog(Context context) {
        try {
            if (waittingDialog == null) {
                waittingDialog = new LoadingDialog(context);
            }
            waittingDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 隐藏等候对话框
     */
    public void dismissWaittingDialog() {
        try {
            if (waittingDialog != null) {
                waittingDialog.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 自定义返回键的效果
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) { // 返回键
            finish();
        }
        return true;
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
