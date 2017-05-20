package com.jinr.core.updata;

import model.UpDataMode;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.text.method.ScrollingMovementMethod;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jinr.core.R;
import com.jinr.core.config.AppManager;

@SuppressLint({"InflateParams", "ValidFragment"})
public class UpdataNoticeDialog extends DialogFragment implements
        OnClickListener, OnKeyListener {
    private Context mContext;
    private TextView tv_title;
    private RelativeLayout rl_cancel;
    private RelativeLayout rl_sure;
    private UpDataMode mData;
    private TextView tv_message;
    private TextView tv_cancel;
    private int mType;
    private int FromHomeActivity = 1;
    private int MustUpdaya = 2;

    public UpdataNoticeDialog(Context context, UpDataMode data, int type) {
        this.mContext = context;
        this.mData = data;
        this.mType = type;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.notice_dialog_updata, null);
        initUI(view);
        Dialog dialog = new Dialog(mContext, R.style.Dialog_Fullscreen);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setOnKeyListener(this);
        dialog.setContentView(view);
        return dialog;
    }

    private void initUI(View view) {
        tv_title = (TextView) view.findViewById(R.id.tv_title);
        tv_message = (TextView) view.findViewById(R.id.tv_message);
        tv_cancel = (TextView) view.findViewById(R.id.tv_cancel);
        if (mType == FromHomeActivity && mData.getConstraint() != MustUpdaya) {
            tv_cancel.setText("不再提醒");
        } else {
            tv_cancel.setText("取消");
        }
        rl_cancel = (RelativeLayout) view.findViewById(R.id.rl_cancel);
        rl_sure = (RelativeLayout) view.findViewById(R.id.rl_sure);
        tv_title.setText(mData.getEdition_name());
        // 设置可滚动
        tv_message.setMovementMethod(ScrollingMovementMethod.getInstance());
        // 设置超链接可以打开网页
        tv_message.setMovementMethod(LinkMovementMethod.getInstance());
        tv_message.setText(Html.fromHtml(mData.getMessage(), null, null));
        // tv_message.setText("\u3000\u3000" + mData.getMessage());
        rl_cancel.setOnClickListener(this);
        rl_sure.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_cancel:
                if (mData.getConstraint() == 2) {
                    AppManager.getAppManager().AppExit(mContext);
                } else {
                    this.dismiss();
                }
                break;
            case R.id.rl_sure:
                UpdataDownloadDialog dialog = UpdataDownloadDialog.getInstance(mData.getUrl(),mContext);
                dialog.show(getFragmentManager(), "");
                this.dismiss();
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && mData.getConstraint() == 2) {
            return true;
        } else {
            this.dismiss();
            return false;
        }
    }
}
