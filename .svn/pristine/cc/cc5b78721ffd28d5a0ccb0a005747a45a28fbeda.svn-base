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
import android.widget.LinearLayout.LayoutParams;

import com.jinr.core.R;
import com.jinr.core.config.AppManager;

public class DialogGuideMask {
    private Dialog mDialog;
    // 遮罩
    private ImageView iv_mask;
    // 确定
    public ImageView iv_custom_dialog_sure;
    private Activity currentActivity;

    public DialogGuideMask(Context context, int resid) {
        currentActivity = (Activity) context;
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.dialog_guide_mask, null);
        mDialog = new Dialog(context, R.style.MyDialog);
        mDialog.setContentView(view);
        mDialog.setCanceledOnTouchOutside(false);
        DisplayMetrics dm = new DisplayMetrics();
        AppManager.getAppManager().currentActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        Window dialogWindow = mDialog.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        // lp.x = 100; // 新位置X坐标
        // lp.y = 100; // 新位置Y坐标
        lp.width = LayoutParams.MATCH_PARENT; // 宽度
        lp.height = LayoutParams.MATCH_PARENT; // 高度
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
        iv_mask = (ImageView) view.findViewById(R.id.iv_mask);
        iv_mask.setBackgroundResource(resid);
        iv_custom_dialog_sure = (ImageView) view.findViewById(R.id.iv_sure);
        iv_custom_dialog_sure.setOnClickListener(new OnClickListener() {
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
