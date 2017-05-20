package com.jinr.core.bankCard;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.jinr.core.R;
import com.jinr.core.bankCard.UserMessageKeyoardView.onPasswordOverListener;

public class InputUserPhoneMessageDialogFragment extends DialogFragment
        implements OnKeyListener, onPasswordOverListener {
    private PhoneMessageView mPhoneMessageView;
    private View mView;
    private String mUserMessage;
    private UserMessageKeyoardView mPasswordView;
    private MessageOverListener mListener;

    public InputUserPhoneMessageDialogFragment() {
    }

    public static InputUserPhoneMessageDialogFragment getInstance(PhoneMessageView phonemessageview) {

        InputUserPhoneMessageDialogFragment instance = new InputUserPhoneMessageDialogFragment();
        instance.setView(phonemessageview);
        return instance;

    }

    private void setView(PhoneMessageView phonemessageview) {
        this.mPhoneMessageView = phonemessageview;
    }

    public interface MessageOverListener {
        void onMessageOverListener(boolean isOver);
    }

    public void setOnMessageOverListener(MessageOverListener listener) {
        this.mListener = listener;
    }

    @Override
    public void onStart() {
        super.onStart();
        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        getDialog().getWindow().setLayout(dm.widthPixels,
                getDialog().getWindow().getAttributes().height);
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        final WindowManager.LayoutParams layoutParams = getDialog().getWindow()
                .getAttributes();
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
        mView = inflater.inflate(R.layout.inputuserphonemessage_dialog, null);
        mUserMessage = mPhoneMessageView.getText().toString();
        initUI(mView);
        Dialog dialog = new Dialog(getActivity(), R.style.Dialog_Fullscreen);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setOnKeyListener(this);
        dialog.setContentView(mView);
        return dialog;
    }

    private void initUI(View view) {
        mPasswordView = (UserMessageKeyoardView) view
                .findViewById(R.id.passwordView);
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
            if (mUserMessage.length() > 0) {
                mUserMessage = mUserMessage.substring(0,
                        mUserMessage.length() - 1);
                mPhoneMessageView.setText(mUserMessage);
            }
        } else if (mUserMessage.length() < 6) {
            mUserMessage = mUserMessage + password;
            mPhoneMessageView.setText(mUserMessage);
        }
        if (mUserMessage.length() == 6) {
            mListener.onMessageOverListener(true);
        } else {
            mListener.onMessageOverListener(false);
        }
    }

    @Override
    public void onPasswordDismissListener() {
        this.dismiss();
    }
}
