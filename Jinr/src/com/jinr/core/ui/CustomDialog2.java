package com.jinr.core.ui;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.jinr.core.R;
import com.jinr.core.utils.CommonUtil;

/**
 * Created by: 966 on 2016/11/8.
 */
public class CustomDialog2 extends Dialog implements View.OnClickListener {

    private Context mContext;
    private int layoutId;
    private int[] viewIds;// 要监听的控件的id
    private OnDialogViewClickListener listener;

    public CustomDialog2(Context context, int layoutId, int[] viewId) {
        super(context, R.style.MyDialog);
        this.mContext = context;
        this.layoutId = layoutId;
        this.viewIds = viewId;
        setCanceledOnTouchOutside(true);
    }

    public CustomDialog2(Context context, int layoutId, int[] viewId, boolean flag) {
        super(context, R.style.MyDialog);
        this.mContext = context;
        this.layoutId = layoutId;
        this.viewIds = viewId;

        if (flag) {
            setCanceledOnTouchOutside(false);
            this.setOnKeyListener(new DialogInterface.OnKeyListener() {
                @Override
                public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        dismiss();
                        ((Activity) mContext).finish();
                        return true;
                    }
                    return false;
                }
            });
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        window.setGravity(Gravity.CENTER);
        setContentView(layoutId);
        int screenWidth = CommonUtil.getScreenWidth(mContext);
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.width = screenWidth * 4 / 5;
        getWindow().setAttributes(lp);
        for (int id : viewIds) {
            findViewById(id).setOnClickListener(this);
        }
    }

    public interface OnDialogViewClickListener {
        void OnDialogViewClick(CustomDialog2 dialog, View view);
    }

    public void setOnDialogViewClickListener(OnDialogViewClickListener listener) {
        this.listener = listener;
    }

    @Override
    public void onClick(View view) {
        listener.OnDialogViewClick(this, view);
    }

    public void setContent(int id, String content) {
        ((TextView) findViewById(id)).setText(content + "");
    }

    public void setContent(int id, String content, int type) {
        ((TextView) findViewById(id)).setText(content);
        if (type == 0) {
            ((TextView) findViewById(id)).setGravity(Gravity.LEFT);
        } else {
            ((TextView) findViewById(id)).setGravity(Gravity.CENTER);
        }
    }
}
