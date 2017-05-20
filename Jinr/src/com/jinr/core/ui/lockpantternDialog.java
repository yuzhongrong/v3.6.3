package com.jinr.core.ui;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.jinr.core.R;
import com.jinr.core.config.AppManager;
import com.jinr.core.utils.DensityUtil;


public class lockpantternDialog {
    private Dialog mDialog;
    // 标题
    private TextView dialog_title;
    // Message
    public EditText dialog_password;
    // 确定
    public Button btn_custom_dialog_ok;
    public Button btn_custom_dialog_cancel;
    private Activity currentActivity;

    public lockpantternDialog(Context context, String title) {
        currentActivity = (Activity) context;
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.dialog_lock, null);
        mDialog = new Dialog(context, R.style.MyDialog);
        mDialog.setContentView(view);
        mDialog.setCanceledOnTouchOutside(false);
        DisplayMetrics dm = new DisplayMetrics();
        AppManager.getAppManager().currentActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        Window dialogWindow = mDialog.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        mDialog.setCanceledOnTouchOutside(true);
        lp.width = dm.widthPixels - DensityUtil.dip2px(context, 70); // 宽度
        lp.height = LayoutParams.WRAP_CONTENT; // 高度
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
        dialog_title = (TextView) view.findViewById(R.id.dialog_title_lock);
        dialog_password = (EditText) view.findViewById(R.id.dialog_ed_lock);
        dialog_title.setText(title);
        btn_custom_dialog_ok = (Button) view.findViewById(R.id.dialog_button_ok);
        btn_custom_dialog_cancel = (Button) view.findViewById(R.id.dialog_button_cancel);
        dialog_password = (EditText) view.findViewById(R.id.dialog_ed_lock);
    }


    public void show() {
        mDialog.show();
    }

    public void close() {
        mDialog.dismiss();
    }

    public void dismiss() {
        if (currentActivity != null && !currentActivity.isFinishing()) {
            mDialog.dismiss();
        }
    }
}
