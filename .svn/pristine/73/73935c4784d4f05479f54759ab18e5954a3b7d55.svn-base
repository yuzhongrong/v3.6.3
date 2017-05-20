package com.jinr.graph_view;


import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jinr.core.R;
import com.jinr.core.security.ForgotPasswdActivity;
import com.jinr.core.security.ForgotPwd1Activity;
import com.jinr.core.utils.ToastUtil;

import java.lang.reflect.Method;

/**
 * Created by: 966 on 2016/11/11.
 */
public class DealPwdDialog extends Dialog implements View.OnClickListener{

    private Context context;

    private EditText ed_pwd;//密码输入框
    private TextView tv_forget_pwd;
    private TextView tv_pay_money;  //支付金额
    private TextView tv_pay_product;//支付的产品种类
    private ImageView iv_close;
    private KeyboardView keyboardView;

    private Editable editable;
    private OnDealPwdDialogCallBack listener;
    private LinearLayout keyboard_parts;
    private RelativeLayout loading_page;
    private ProgressBar loadingBar;

    private Handler pwdhandler;
    private static final int KEYBOARD_HIDE_LOADING_SHOW = 0x001;
    private static final int KEYBOARD_SHOW_LOADING_HIDE = 0x002;


    public DealPwdDialog(Context context) {
        super(context, R.style.DialogStylePwd);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_dealpwd);

        Window window = getWindow();
        window.setWindowAnimations(R.style.dialogWindowAnim);
        window.setGravity(Gravity.BOTTOM);

        Keyboard k1 = new Keyboard(context, R.xml.symbols);
        ed_pwd = (EditText) findViewById(R.id.eta_pwd);
        iv_close = (ImageView) findViewById(R.id.close_keyboard);
        tv_forget_pwd = (TextView) findViewById(R.id.forgot_password);
        tv_pay_money = (TextView) findViewById(R.id.tv_pay_money);
        tv_pay_product = (TextView) findViewById(R.id.tv_pay_product);
        keyboard_parts = (LinearLayout)findViewById(R.id.keyboard_parts);// 关闭按钮
        loading_page = (RelativeLayout) findViewById(R.id.loading_page);//密码盘区域
        loadingBar = (ProgressBar) findViewById(R.id.progressbar);//loading区域
        keyboardView = (KeyboardView) findViewById(R.id.keyboard_view);
        keyboardView.setKeyboard(k1);
        keyboardView.setEnabled(true);
        keyboardView.setPreviewEnabled(false);

        setCanceledOnTouchOutside(false);
        setListener();

        pwdhandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                try {
                    switch (msg.what) {
                        case KEYBOARD_HIDE_LOADING_SHOW:

                            iv_close.setClickable(false);
                            keyboard_parts.setVisibility(View.INVISIBLE);
                            loading_page.setVisibility(View.VISIBLE);
                            loadingBar.setVisibility(View.VISIBLE);
                            break;
                        case KEYBOARD_SHOW_LOADING_HIDE:

                            iv_close.setClickable(true);
                            keyboard_parts.setVisibility(View.VISIBLE);
                            loading_page.setVisibility(View.INVISIBLE);
                            loadingBar.setVisibility(View.INVISIBLE);
                            break;
                        default:
                            dismiss();
                            break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.close_keyboard:
                dismiss();
                break;

            case R.id.forgot_password:

                ToastUtil.show(context,"跳转到忘记密码页");
                context.startActivity(new Intent(context, ForgotPwd1Activity.class));

                break;

        }
    }

    private void setListener() {

        tv_forget_pwd.setOnClickListener(this);
        iv_close.setOnClickListener(this);
        keyboardView.setOnKeyboardActionListener(key_listener);
        ed_pwd.addTextChangedListener(new TextWatcher(){
            @Override
            public void onTextChanged(CharSequence s, int start, int before,int count) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                s = ed_pwd.getText();
                if (s.length() >= 6) {
                   String str_pwd = ed_pwd.getText().toString();
                    try {

                        listener.getPwd2Request(str_pwd);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        this.setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                ed_pwd.setText("");
            }
        });

        // 隐藏系统键盘并显示光标
        ed_pwd.setFocusable(true);
        ed_pwd.setFocusableInTouchMode(true);
        ed_pwd.requestFocus();
        if (android.os.Build.VERSION.SDK_INT <= 10) {// 4.0以下
            ed_pwd.setInputType(InputType.TYPE_NULL);
        } else {
            getWindow().setSoftInputMode(
                    WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            try {
                Class<EditText> cls = EditText.class;
                Method setShowSoftInputOnFocus;
                setShowSoftInputOnFocus = cls.getMethod("setShowSoftInputOnFocus", boolean.class);
                setShowSoftInputOnFocus.setAccessible(true);
                setShowSoftInputOnFocus.invoke(ed_pwd, false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private KeyboardView.OnKeyboardActionListener key_listener = new KeyboardView.OnKeyboardActionListener() {
        @Override
        public void swipeUp() {
        }

        @Override
        public void swipeRight() {
        }

        @Override
        public void swipeLeft() {
        }

        @Override
        public void swipeDown() {
        }

        @Override
        public void onText(CharSequence text) {
        }

        @Override
        public void onRelease(int primaryCode) {
        }

        @Override
        public void onPress(int primaryCode) {
        }

        @Override
        public void onKey(int primaryCode, int[] keyCodes) {
            if (ed_pwd != null) {
                editable = ed_pwd.getText();
                int start = ed_pwd.getSelectionStart();
                if (primaryCode == Keyboard.KEYCODE_CANCEL) {// // 左下角限制

                }else if (primaryCode == Keyboard.KEYCODE_DELETE) {// 回退
                    if (editable != null && editable.length() > 0) {
                        if (start > 0) {
                            editable.delete(start - 1, start);
                        }
                    }
                } else {

                    editable.insert(start,Character.toString((char) primaryCode));
                }

            }
        }
    };

    /**
     * 设置键盘显示 loading隐藏
     *
     */
    public void setKeyboardShowLoadingHide(){
        Message msg = new Message();
        msg.what = KEYBOARD_SHOW_LOADING_HIDE;
        pwdhandler.sendMessage(msg);
    }

    /**
     * 设置键盘隐藏 loading显示
     *
     */
    public void setKeyboardHideLoadingShow(){
        Message msg = new Message();
        msg.what = KEYBOARD_HIDE_LOADING_SHOW;
        pwdhandler.sendMessage(msg);
    }

    public interface OnDealPwdDialogCallBack {

        /**
         * 当交易密码输入六位的时候回调
         * @param pwd
         */
        void getPwd2Request(String pwd);
    }
    public void setOnDealPwdDialogCallBack(OnDealPwdDialogCallBack listener) {

        this.listener = listener;
    }

    /**
     * 设置密码输入框为空
     *
     */
    public void  setEditEmpty(){
        ed_pwd.setText("");
    }

    /**
     * 设置支付金额
     * @param payMoney
     */
    public void setPayMoney(String payMoney){
         if(!TextUtils.isEmpty(payMoney)){
             tv_pay_money.setText("¥"+payMoney);
         }
    }

    /**
     * 设置支付产品种类
     * @param strProduct
     */
    public void setPayProduct(String strProduct){
        if(!TextUtils.isEmpty(strProduct)){
            tv_pay_money.setText(strProduct+"");
        }
    }
}

