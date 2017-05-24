package com.jinr.new_mvp.network;

import android.content.Context;
import android.support.annotation.Nullable;

import com.jinr.core.utils.LoadingDialog;
import com.lzy.okgo.request.BaseRequest;

import org.json.JSONObject;

/**
 * Created by yuzhongrong on 2017/5/24.
 */

public abstract class DialogMd5JsonCallBack<T> extends Md5JsonCallBack<T> {

    private LoadingDialog waittingDialog;

    public DialogMd5JsonCallBack(JSONObject baseJson) {
        super(baseJson);
    }


    @Override
    public void onBefore(BaseRequest request) {
        super.onBefore(request);

        showWaittingDialog((Context) request.getTag());
    }


    @Override
    public void onAfter(@Nullable T t, @Nullable Exception e) {
        super.onAfter(t, e);
        dismissWaittingDialog();
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


}
