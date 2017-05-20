package com.jinr.core.bankCard;

import com.jinr.core.R;
import com.jinr.core.utils.PreferencesUtils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by Ysw on 2016/10/27.
 */

public class BankCardNoticeDialogFragment extends DialogFragment implements OnKeyListener, OnClickListener {
    private int mResId;
    // 根据Type来区分是有两个按钮还是一个按钮，1或2,有两个按钮
    private int mType;
    private View mView;
    private BankCardNoticeDialogClick mListener;

    public BankCardNoticeDialogFragment() {
    }

    public static BankCardNoticeDialogFragment getInstance(int resid, int type) {
        BankCardNoticeDialogFragment instance = new BankCardNoticeDialogFragment();
        Bundle args = new Bundle();
        args.putInt("resid", resid);
        args.putInt("type", type);
        instance.setArguments(args);
        return instance;
    }

    public interface BankCardNoticeDialogClick {
        void onCancelClick(int type);

        void onSureClick(int type);
    }

    public BankCardNoticeDialogFragment setOnBankCardNoticeDialogClick(BankCardNoticeDialogClick listener) {
        this.mListener = listener;
        return this;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        if (bundle != null) {
            mResId = bundle.getInt("resid");
            mType = bundle.getInt("type");
        }
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        mView = inflater.inflate(mResId, null);
        initUI(mView, mType);
        Dialog dialog = new Dialog(getActivity(), R.style.Dialog_Fullscreen);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setOnKeyListener(this);
        dialog.setContentView(mView);
        return dialog;
    }

    private void initUI(View view, int type) {
        if (type == 1) {
            RelativeLayout rl_cancel = (RelativeLayout) view.findViewById(R.id.rl_cancel);
            RelativeLayout rl_sure = (RelativeLayout) view.findViewById(R.id.rl_sure);
            rl_cancel.setOnClickListener(this);
            rl_sure.setOnClickListener(this);
        }else if(type == 2) {
            RelativeLayout rl_sure = (RelativeLayout) view.findViewById(R.id.rl_sure);
            rl_sure.setOnClickListener(this);
        } else if(type == 3) {
            TextView textView = (TextView) view.findViewById(R.id.textView1);
            textView.setText(getResources().getString(R.string.message_content) + PreferencesUtils.getCValueFromSPMap(getActivity(),
                    PreferencesUtils.Keys.KEFU_PHONE));
            RelativeLayout rl_sure = (RelativeLayout) view.findViewById(R.id.rl_sure);
            rl_sure.setOnClickListener(this);
        }
    }

    @Override
    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            this.dismiss();
        }
        return false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_cancel:
                if (mListener != null) {
                    mListener.onCancelClick(mType);
                    this.dismiss();
                }
                break;
            case R.id.rl_sure:
                if (mListener != null) {
                    mListener.onSureClick(mType);
                    this.dismiss();
                }
                break;
            default:
                break;
        }
    }
}
