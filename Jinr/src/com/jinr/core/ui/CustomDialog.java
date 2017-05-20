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
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.jinr.core.R;
import com.jinr.core.config.AppManager;
import com.jinr.core.utils.DensityUtil;

public class CustomDialog {
    private Dialog mDialog;
    // 标题
    private TextView dialog_title;
    // Message
    public TextView dialog_message;
    // 确定
    public TextView dialog_sure;
    //取消
    public TextView dialog_cancle;

    private Activity currentActivity;

    public CustomDialog(Context context, String title, String message, String content) {

        currentActivity = (Activity) context;
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.dialog_custom, null);

        mDialog = new Dialog(context, R.style.MyDialog);
        mDialog.setContentView(view);
        mDialog.setCanceledOnTouchOutside(false);

        DisplayMetrics dm = new DisplayMetrics();

        AppManager.getAppManager().currentActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);

        Window dialogWindow = mDialog.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        // dialogWindow.setGravity(Gravity.LEFT | Gravity.TOP);

        mDialog.setCanceledOnTouchOutside(true);
        // lp.x = 100; // 新位置X坐标
        // lp.y = 100; // 新位置Y坐标
        lp.width = dm.widthPixels - DensityUtil.dip2px(context, 70); // 宽度
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
        dialog_title = (TextView) view.findViewById(R.id.dialog_title);
        dialog_message = (TextView) view.findViewById(R.id.dialog_content);
        dialog_message.setText(content);

        dialog_sure = (TextView) view.findViewById(R.id.dialog_sure);
        dialog_cancle = (TextView) view.findViewById(R.id.dialog_cancle);
        dialog_cancle.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                mDialog.dismiss();
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
