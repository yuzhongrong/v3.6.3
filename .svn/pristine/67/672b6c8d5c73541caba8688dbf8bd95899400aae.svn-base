package com.jinr.core.bankCard;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.jinr.core.R;
import com.jinr.core.bankCard.UseridKeyboardView.onPasswordOverListener;

/**
 * Created by: Ysw on 2016/10/26.
 */

public class InputUseridDialogfragment extends DialogFragment implements DialogInterface.OnKeyListener, onPasswordOverListener {
    private EditText mTextView;
    private ImageView mImageView;
    private UseridKeyboardView mPasswordView;
    private View mView;
    private String userId;
    private onUserIdOverListener mListener;

    public InputUseridDialogfragment() {
    }

    public static InputUseridDialogfragment getInstance(EditText textview, ImageView imageView) {
        InputUseridDialogfragment instance = new InputUseridDialogfragment();
        instance.setmTextView(textview);
        instance.setmImageView(imageView);
        return instance;
    }

    public interface onUserIdOverListener {
        void userIdOverListener(boolean isOver);
    }

    public void setOnUserIdOverListener(onUserIdOverListener listener) {
        this.mListener = listener;
    }

    @Override
    public void onStart() {
        super.onStart();
        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        getDialog().getWindow().setLayout(dm.widthPixels, getDialog().getWindow().getAttributes().height);
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        final WindowManager.LayoutParams layoutParams = getDialog().getWindow().getAttributes();
        layoutParams.width = dm.widthPixels;
        layoutParams.gravity = Gravity.BOTTOM;
        getDialog().getWindow().setAttributes(layoutParams);
        Window window = getDialog().getWindow();
        WindowManager.LayoutParams windowParams = window.getAttributes();
        windowParams.dimAmount = 0.0f;
        window.setAttributes(windowParams);
    }

    @SuppressLint("InflateParams")
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        mView = inflater.inflate(R.layout.inputuserid_dialog, null);
        userId = mTextView.getText().toString();
        initUI(mView);
        Dialog dialog = new Dialog(getActivity(), R.style.Dialog_Fullscreen);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setOnKeyListener(this);
        dialog.setContentView(mView);
        return dialog;
    }

    private void setmTextView(EditText textview) {
        this.mTextView = textview;
    }

    private void setmImageView(ImageView imageView) {
        this.mImageView = imageView;
    }

    private void initUI(View view) {
        mPasswordView = (UseridKeyboardView) view.findViewById(R.id.passwordView);
        mPasswordView.setOnPasswordOverListener(this);
    }

    @Override
    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            this.dismiss();
        }
        return false;
    }

    @Override
    public void onPasswordOverListener(String password) {
        if (password.equals("-1")) {
            if (userId.length() > 0 && !userId.equals("请输入身份证证件号")) {
                userId = userId.substring(0, userId.length() - 1);
                if (userId.length() == 0) {
                    userId = "请输入身份证证件号";
                    mTextView.setText("");
                    mTextView.setHint("请输入身份证证件号");
                    mTextView.setHintTextColor(Color.parseColor("#bbbbbb"));
                } else {
                    mTextView.setText(userId);
                    mTextView.setTextColor(Color.parseColor("#333333"));
                }
            }
        } else if (userId.length() < 18 && !userId.equals("请输入身份证证件号")) {
            userId = userId + password;
            mTextView.setText(userId);
            mTextView.setTextColor(Color.parseColor("#333333"));
        } else if (userId.length() < 18 && userId.equals("请输入身份证证件号")) {
            userId = password;
            mTextView.setText(userId);
            mTextView.setTextColor(Color.parseColor("#333333"));
        }
        if (userId.length() == 18 && !userId.equals("请输入身份证证件号")) {
            if (mListener != null) {
                mListener.userIdOverListener(true);
                this.dismiss();
            } else {
                mListener.userIdOverListener(false);
            }
        }
        if (userId.length() > 0 && !userId.equals("请输入身份证证件号")) {
            mImageView.setVisibility(View.VISIBLE);
        } else {
            mImageView.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onPasswordDismissListener() {
        this.dismiss();
    }
}