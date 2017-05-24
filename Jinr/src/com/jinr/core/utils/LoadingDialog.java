/*
 * LoadingDialog.java
 * @author Andrew Lee
 * 2014-10-20 下午4:05:04
 */
package com.jinr.core.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;

import com.jinr.core.R;

/**
 * LoadingDialog.java description:
 *
 * @author Andrew Lee version 2014-10-20 下午4:05:04
 */
public class LoadingDialog {
    private Dialog mDialog;
    private Activity currentActivity;

    public LoadingDialog(Context context) {
        currentActivity = (Activity) context;
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.loading, null);
        mDialog = new Dialog(context, R.style.MyDialog);
        mDialog.setContentView(view);
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    dismiss();
                    return true;
                }
                return false;
            }
        });

    }

    public void show() {
        try {
            if (currentActivity != null && !currentActivity.isFinishing()) {
                if(!mDialog.isShowing())mDialog.show();

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void dismiss() {
        try {
            if (currentActivity != null && !currentActivity.isFinishing() && mDialog.isShowing()) {
                if(mDialog.isShowing())mDialog.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
