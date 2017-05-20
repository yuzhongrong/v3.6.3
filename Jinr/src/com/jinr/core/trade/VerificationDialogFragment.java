package com.jinr.core.trade;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.jinr.core.R;
import com.jinr.core.utils.KeyboardUtil;
import com.jinr.core.utils.PreferencesUtils;
import com.jinr.core.utils.SendMobileCode;
import com.jinr.core.utils.SendMobileCode.Back;

import java.lang.reflect.Method;

@SuppressLint("ValidFragment")
public class VerificationDialogFragment extends DialogFragment {
    private View mview;
    private LinearLayout keyboard_parts;
    private RelativeLayout loading_page;
    private ImageView close_keyboard;
    private ProgressBar loadingBar;
    private EditText pw;// 密码输入框
    private KeyboardUtil keyboardUtil;
    private Button send_code;
    private Context mContext;
    private String user_id = "";
    private String mOrderNum;
    private OnVerificatCompleted mOnVerificatCompleted;
    final static int REGULAR_MSG_KEYBOARD_HIDE = 2;
    final static int REGULAR_MSG_KEYBOARD_VALIDATE_SHOW = 3;
    final static int REGULAR_MSG_KEYBOARD_VALIDATE_HIDE = 4;
    final static int REGULAR_MSG_KEYBOARD_VALIDATE_ERROR_SHOW = 5;

    private Handler Pophandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            try {
                switch (msg.what) {
                    case REGULAR_MSG_KEYBOARD_HIDE:// 隐藏键盘，开始验证密码
                        close_keyboard.setClickable(false);
                        keyboard_parts.setVisibility(View.INVISIBLE);
                        loading_page.setVisibility(View.VISIBLE);
                        loadingBar.setVisibility(View.VISIBLE);
                        break;
                    case REGULAR_MSG_KEYBOARD_VALIDATE_SHOW:
                        dismiss();
                        break;
                    case REGULAR_MSG_KEYBOARD_VALIDATE_ERROR_SHOW:
                        close_keyboard.setClickable(true);
                        keyboard_parts.setVisibility(View.VISIBLE);
                        loading_page.setVisibility(View.INVISIBLE);
                        loadingBar.setVisibility(View.INVISIBLE);
                        break;
                    default:
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    public void setOnVerificatCompleted(OnVerificatCompleted onVerificatCompleted) {
        mOnVerificatCompleted = onVerificatCompleted;
    }

    public VerificationDialogFragment() {
        super();
    }

    public VerificationDialogFragment(Context context, String orderNum) {
        mContext = context;
        mOrderNum = orderNum;
        user_id = PreferencesUtils.getValueFromSPMap(mContext, PreferencesUtils.Keys.UID);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        mview = inflater.inflate(R.layout.layout_verification, null);
        initData();
        return mview;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().getAttributes().windowAnimations = R.style.dialogWindowAnim;
        return dialog;
    }

    public void onStart() {
        super.onStart();
        Window win = getDialog().getWindow();
        // 一定要设置Background，如果不设置，window属性设置无效
        win.setBackgroundDrawable(new ColorDrawable());
        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        LayoutParams params = win.getAttributes();
        params.gravity = Gravity.BOTTOM;
        // 使用ViewGroup.LayoutParams，以便Dialog 宽度充满整个屏幕
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        win.setAttributes(params);
    }

    public void initData() {
        pw = (EditText) mview.findViewById(R.id.register_et2);
        send_code = (Button) mview.findViewById(R.id.register_send_code);
        close_keyboard = (ImageView) mview.findViewById(R.id.close_keyboard);//
        // 关闭按钮
        keyboard_parts = (LinearLayout) mview.findViewById(R.id.keyboard_parts);//
        // 密码盘区域
        loading_page = (RelativeLayout) mview.findViewById(R.id.loading_page);//
        // loading区域
        loadingBar = (ProgressBar) mview.findViewById(R.id.progressbar);// 模糊进度条
        pw.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                }
            }
        });
        /**
         * 关闭输入密码
         */
        close_keyboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                mOnVerificatCompleted.dissWindow();
            }
        });
        PopupWindow window = new PopupWindow();
        keyboardUtil = new KeyboardUtil(mview, window, mContext, pw);
        pw.setFocusable(true);
        pw.setFocusableInTouchMode(true);
        pw.requestFocus();
        // 隐藏系统键盘并显示光标
        if (android.os.Build.VERSION.SDK_INT <= 10) {// 4.0以下
            pw.setInputType(InputType.TYPE_NULL);
        } else {
            try {
                Class<EditText> cls = EditText.class;
                Method setShowSoftInputOnFocus;
                setShowSoftInputOnFocus = cls.getMethod("setShowSoftInputOnFocus", boolean.class);
                setShowSoftInputOnFocus.setAccessible(true);
                setShowSoftInputOnFocus.invoke(pw, false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            /**
             * 输入密码6位之后handler发送关闭
             */
            @Override
            public void afterTextChanged(Editable s) {
                s = pw.getText();
                if (s.length() >= 6) {
                    String validate = pw.getText().toString();
                    mOnVerificatCompleted.inVerificatCompleted(validate);
                }
            }
        };
        sendCode(false);
        pw.addTextChangedListener(textWatcher);
        send_code.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                sendCode(true);
            }
        });

    }

    /**
     * 发送验证码
     */
    public void sendCode(boolean isSend) {
        SendMobileCode.getInstance().sendValidateCode(send_code, mContext, user_id, mOrderNum, isSend, new Back() {
            @Override
            public void sms(String result) {
                if (result != null && !"".equals(result)) {
                    send_code.setClickable(true);
                }
            }
        });
    }

    public void setVerificat(int status) {
        Message msg = new Message();
        msg.what = status;
        Pophandler.sendMessage(msg);
    }

    public interface OnVerificatCompleted {
        void inVerificatCompleted(String validate);
        void dissWindow();
    }
}
