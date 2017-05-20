package com.jinr.core.ui;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.jinr.core.R;
import com.jinr.core.config.AppManager;

public class CannotCodeDialog {
    private Dialog mDialog;
    private Activity currentActivity;
    public ImageView close_img;
    public TextView dialog_message;
    public TextView dialog_title;
    public LinearLayout btn_cancle;

    public CannotCodeDialog(Context context) {
        currentActivity = (Activity) context;
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.dialog_no_code, null);
        mDialog = new Dialog(context, R.style.MyDialog);
        mDialog.setCancelable(false);
        mDialog.setContentView(view);
        mDialog.setCanceledOnTouchOutside(false);
        DisplayMetrics dm = new DisplayMetrics();
        AppManager.getAppManager().currentActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        Window dialogWindow = mDialog.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        mDialog.setCanceledOnTouchOutside(true);
        // lp.x = 100; // 新位置X坐标
        // lp.y = 100; // 新位置Y坐标
        lp.width = LayoutParams.FILL_PARENT; // 宽度
        lp.height = LayoutParams.WRAP_CONTENT; // 高度
        // lp.alpha = 0.7f; // 透明度
        // 当Window的Attributes改变时系统会调用此函数,可以直接调用以应用上面对窗口参数的更改,也可以用setAttributes
        // dialog.onWindowAttributesChanged(lp);
        dialogWindow.setAttributes(lp);
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
        close_img = (ImageView) view.findViewById(R.id.close_img);
        dialog_title = (TextView) view.findViewById(R.id.dialog_title);
        dialog_message = (TextView) view.findViewById(R.id.dialog_content);
        btn_cancle = (LinearLayout) view.findViewById(R.id.btn_cancle);
        btn_cancle.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });
    }

    public void show() {
        mDialog.show();
    }

    public void setCancelable() {
        mDialog.setCancelable(false);
    }

    public void dismiss() {
        if (currentActivity != null && !currentActivity.isFinishing()) {
            mDialog.dismiss();
        }
    }
}
