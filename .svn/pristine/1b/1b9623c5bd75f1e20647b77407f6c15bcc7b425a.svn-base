package com.jinr.core.ui;

import android.annotation.SuppressLint;
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
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jinr.core.R;
import com.jinr.core.config.AppManager;
import com.jinr.core.utils.DensityUtil;

public class DialogHeadNotice {
    private Dialog mDialog;
    private TextView dialog_title;
    private TextView dialog_message;
    public RelativeLayout btn_custom_dialog_sure;
    public ImageView iv_close;
    public ImageView iv_notice_close;
    private Activity currentActivity;

    @SuppressLint("InflateParams")
    public DialogHeadNotice(Context context, String title, String message) {
        currentActivity = (Activity) context;
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.dialog_head_notice, null);
        mDialog = new Dialog(context, R.style.MyDialog);
        mDialog.setContentView(view);
        // 设置点击空白处Dialog消失 Ysw 2019.09.22
        mDialog.setCanceledOnTouchOutside(true);
        DisplayMetrics dm = new DisplayMetrics();
        AppManager.getAppManager().currentActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        Window dialogWindow = mDialog.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width = dm.widthPixels - DensityUtil.dip2px(context, 40); // 宽度
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
        dialog_title = (TextView) view.findViewById(R.id.dialog_title);
        dialog_message = (TextView) view.findViewById(R.id.dialog_content);
        dialog_title.setText(title);
        dialog_message.setText(message);
        btn_custom_dialog_sure = (RelativeLayout) view.findViewById(R.id.dialog_button);
        iv_close = (ImageView) view.findViewById(R.id.iv_close);
        iv_notice_close = (ImageView) view.findViewById(R.id.iv_notice_close);
        iv_notice_close.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    public void show() {
        mDialog.show();
    }

    public void dismiss() {
        if (currentActivity != null && !currentActivity.isFinishing()) {
            mDialog.dismiss();
        }
    }
}
