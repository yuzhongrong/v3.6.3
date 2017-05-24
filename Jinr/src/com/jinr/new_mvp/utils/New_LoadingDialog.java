/*
 * LoadingDialog.java
 * @author Andrew Lee
 * 2014-10-20 下午4:05:04
 */
package com.jinr.new_mvp.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;

import com.jinr.core.R;

/**
 * LoadingDialog.java description:
 *
 * @author Andrew Lee version 2014-10-20 下午4:05:04
 */
public class New_LoadingDialog {
    private Dialog mDialog;
    private Activity currentActivity;
    private static New_LoadingDialog instance;


    public  synchronized  static New_LoadingDialog getInstance(Context context){

        if(instance==null){

            instance= new New_LoadingDialog(context);

        }
        return instance;


    }


    public New_LoadingDialog(Context context) {
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

        if(mDialog.isShowing()) return;


        try {
         //   if (currentActivity != null && !currentActivity.isFinishing()) {
                Log.d("New_LoadingDialog", "mDialog.show():");
                mDialog.show();
        //    }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void dismiss() {
        try {
       //     if (currentActivity != null && !currentActivity.isFinishing() && mDialog.isShowing()) {
                Log.d("New_LoadingDialog", "mDialog.dismiss()");
                mDialog.dismiss();
      //      }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
