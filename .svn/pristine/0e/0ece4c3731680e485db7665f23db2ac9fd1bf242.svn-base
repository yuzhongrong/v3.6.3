package com.jinr.core.ui;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.jinr.core.R;
import com.jinr.core.config.AppManager;

import java.util.List;

public class BankChooseDialog {
    private Dialog mDialog;
    // 取消
    private Button btn_cancel;
    // 完成
    public Button btn_complete;
    private RadioGroup rgroup_bank; // 开户银行列表
    public int index = -1;
    private Activity currentActivity;
    private View view;

    public BankChooseDialog(Context context, List<String> data) {
        currentActivity = (Activity) context;
        LayoutInflater inflater = LayoutInflater.from(context);
        view = inflater.inflate(R.layout.dialog_bank_choose, null);
        mDialog = new Dialog(context, R.style.MyDialogWide);
        mDialog.setContentView(view);
        mDialog.setCanceledOnTouchOutside(true);
        DisplayMetrics dm = new DisplayMetrics();
        AppManager.getAppManager().currentActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        Window dialogWindow = mDialog.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        dialogWindow.setGravity(Gravity.BOTTOM);
        // lp.x = 100; // 新位置X坐标
        // lp.y = 100; // 新位置Y坐标
        lp.width = LayoutParams.MATCH_PARENT; // 宽度
//		lp.width = dm.widthPixels - DensityUtil.dip2px(context, 40); // 宽度
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

        // 动态生成开户银行列表
        rgroup_bank = (RadioGroup) view.findViewById(R.id.rgroup_bank);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        for (int i = 0; i < data.size(); i++) {
            RadioButton rbtn_bank = (RadioButton) LayoutInflater.from(currentActivity).inflate(R.layout.rbtn_bank, null);
            rbtn_bank.setText(data.get(i));
            rbtn_bank.setId(i);
            rgroup_bank.addView(rbtn_bank, params);
        }
        rgroup_bank.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                index = checkedId;
            }
        });

        // 取消
        btn_cancel = (Button) view.findViewById(R.id.btn_cancel);
        btn_cancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        // 完成
        btn_complete = (Button) view.findViewById(R.id.btn_complete);
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
