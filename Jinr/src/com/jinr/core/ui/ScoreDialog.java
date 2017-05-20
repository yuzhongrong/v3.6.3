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
import android.widget.LinearLayout.LayoutParams;

import com.jinr.core.R;
import com.jinr.core.config.AppManager;
import com.jinr.core.utils.DensityUtil;

public class ScoreDialog {
    private Dialog mDialog;
    public Button btn_score;
    public Button btn_feed_back;
    public Button btn_cancel;
    private Activity currentActivity;

    public ScoreDialog(Context context) {
        currentActivity = (Activity) context;
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.dialog_score, null);
        mDialog = new Dialog(context, R.style.MyDialog);
        mDialog.setContentView(view);
        mDialog.setCanceledOnTouchOutside(false);
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

        btn_score = (Button) view.findViewById(R.id.btn_score);
        btn_feed_back = (Button) view.findViewById(R.id.btn_feedback);
        btn_cancel = (Button) view.findViewById(R.id.btn_cancel);
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
