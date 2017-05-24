package com.jinr.core.base;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;

import com.jinr.core.utils.LoadingDialog;
import com.jinr.core.utils.MyLog;

import org.simple.eventbus.EventBus;

public abstract class BaseFragment extends Fragment {
    private LoadingDialog waittingDialog;


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        EventBus.getDefault().register(this);
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


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyLog.e("packageName", this.toString());
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


    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);

    }

    /**
     * 描述：数据初始化
     */
    protected abstract void initData();

    /**
     * 描述：渲染界面
     */
    protected abstract void findViewById(View view);

    /**
     * 描述：界面初始化
     *
     */
    protected abstract void initUI();

    /**
     * 描述：设置监听
     */
    protected abstract void setListener();
}
