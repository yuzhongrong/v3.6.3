package com.jinr.core.ui;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
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
import com.jinr.core.utils.DensityUtil;

import java.util.List;

import model.AppInfo;

public class MarketDialog {
    private Dialog mDialog;
    // 标题
    private TextView dialog_title;
    private List<AppInfo> info_list;
    // Message
    private Activity currentActivity;

    public MarketDialog(Context context, String title, List<AppInfo> list) {
        currentActivity = (Activity) context;
        info_list = list;
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.dialog_market, null);
        mDialog = new Dialog(context, R.style.MyDialog);
        mDialog.setContentView(view);
        mDialog.setCanceledOnTouchOutside(false);
        DisplayMetrics dm = new DisplayMetrics();
        AppManager.getAppManager().currentActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);

        Window dialogWindow = mDialog.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width = dm.widthPixels - DensityUtil.dip2px(context, 80); // 宽度
        lp.height = LayoutParams.WRAP_CONTENT; // 高度
        dialogWindow.setAttributes(lp);
        mDialog.setCanceledOnTouchOutside(true);
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
        LinearLayout marketLayout = (LinearLayout) view.findViewById(R.id.layout_market);
        dialog_title = (TextView) view.findViewById(R.id.dialog_title);
        dialog_title.setText(title);
        initMarkets(context, marketLayout, info_list);
    }

    private void initMarkets(Context context, LinearLayout layout, List<AppInfo> list) {
        int size = list.size();
        for (int i = 0; i < size; i++) {
            AppInfo info = list.get(i);
            View view = LayoutInflater.from(context).inflate(R.layout.item_market, null);
            ImageView imageView = (ImageView) view.findViewById(R.id.iv_app_icon);
            TextView textView = (TextView) view.findViewById(R.id.tv_app_name);
            LinearLayout itemLayout = (LinearLayout) view.findViewById(R.id.layout_market_item);
            itemLayout.setOnClickListener(new OnClickImageListener(i));
            imageView.setImageDrawable(info.appIcon);
            textView.setText(info.appName);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
            layout.addView(view, params);
        }
    }

    class OnClickImageListener implements OnClickListener {
        private int index;
        public OnClickImageListener(int i) {
            this.index = i;
        }

        @Override
        public void onClick(View v) {
            dismiss();
            Intent intent = new Intent(Intent.ACTION_VIEW);
            Uri uri = Uri.parse("market://details?id=" + "com.jinr.core");
            intent.setData(uri);
            intent.setPackage(info_list.get(index).appPackageName);
            currentActivity.startActivity(intent);
        }

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
