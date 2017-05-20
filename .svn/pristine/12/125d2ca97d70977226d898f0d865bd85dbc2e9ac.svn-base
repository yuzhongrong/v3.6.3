/*
foff * ForgotPasswdActivity.java
 * @author Andrew Lee
 * 2014-10-21 上午10:31:29
 */
package com.jinr.core.security;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.SmsMessage;
import android.text.Editable;
import android.text.InputType;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.jinr.core.R;
import com.jinr.core.base.BaseActivity;
import com.jinr.core.config.MessageType;
import com.jinr.core.config.UrlConfig;
import com.jinr.core.regist.NewLoginActivity;
import com.jinr.core.utils.AutoSmsUtil;
import com.jinr.core.utils.CommonUtil;
import com.jinr.core.utils.MyDES;
import com.jinr.core.utils.MyhttpClient;
import com.jinr.core.utils.PreferencesUtils;
import com.jinr.core.utils.SendMobileCode;
import com.jinr.core.utils.SendMobileCode.Back;
import com.jinr.core.utils.ToastUtil;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONObject;

import model.BaseModel;

/**
 * ForgotPasswdActivity.java description:
 *
 * @author CQJ
 */
public class ForgotPasswdActivity extends BaseActivity implements OnClickListener {

    private ImageView title_left_img; // title左边图片
    private TextView title_center_text; // title标题
    private EditText code_et, passwd_et, submit_passwd_et; // 验证码/密码/确认密码
    private Button code_bt;// 发送验证码
    private Button submit_bt;// 确认提交
    private String tel_no, tel_no_source, mobileNoSms = "", sms = "";

    // 自动填写验证码
    private BroadcastReceiver smsReceiver;
    private IntentFilter filter;
    private Handler handler;
    private String strContent;
    private ImageView img_password;
    private ImageView img_confirm_passw;
    private boolean ispsw;
    private boolean isconfirm;

    @SuppressLint("HandlerLeak")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forgot_passwd);
        initData();
        findViewById();
        initUI();
        setListener();
        // 获取手机验证码自动填写
        handler = new Handler() {
            public void handleMessage(android.os.Message msg) {
                code_et.setText(strContent);
            }
        };
        filter = new IntentFilter();
        filter.addAction("android.provider.Telephony.SMS_RECEIVED");
        filter.setPriority(Integer.MAX_VALUE);
        smsReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Object[] objs = (Object[]) intent.getExtras().get("pdus");
                for (Object obj : objs) {
                    byte[] pdu = (byte[]) obj;
                    SmsMessage sms = SmsMessage.createFromPdu(pdu);
                    // 短信的内容
                    String message = sms.getMessageBody();
                    // 短息的手机号。。+86开头？
                    String from = sms.getOriginatingAddress();
                    if (!TextUtils.isEmpty(from)) {
                        String code = AutoSmsUtil.getInstance().patternCode(message);
                        if (!TextUtils.isEmpty(code)) {
                            strContent = code;
                            handler.sendEmptyMessage(1);
                        }
                    }
                }
            }
        };
        registerReceiver(smsReceiver, filter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(smsReceiver);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void initData() {
        ToastUtil.show(ForgotPasswdActivity.this, R.string.go_reset);
        mobileNoSms = getIntent().getStringExtra("phone");
    }

    @Override
    protected void findViewById() {
        title_left_img = (ImageView) findViewById(R.id.title_left_img);
        title_center_text = (TextView) findViewById(R.id.title_center_text);
        code_et = (EditText) findViewById(R.id.forgot_passwd_code);
        passwd_et = (EditText) findViewById(R.id.forgot_passwd_passwd);
        submit_passwd_et = (EditText) findViewById(R.id.forgot_passwd_submit_passwd);
        code_bt = (Button) findViewById(R.id.forgot_passwd_send_code);
        submit_bt = (Button) findViewById(R.id.forgot_passwd_submit);
        img_password = (ImageView) findViewById(R.id.img_password);
        img_password.setOnClickListener(this);
        img_confirm_passw = (ImageView) findViewById(R.id.img_confirm_passw);
        img_confirm_passw.setOnClickListener(this);
    }

    @Override
    protected void initUI() {
        title_center_text.setText(R.string.set_pwd);
        sendCode();
    }

    @Override
    protected void setListener() {
        title_left_img.setOnClickListener(this);
        code_bt.setOnClickListener(this);
        submit_bt.setOnClickListener(this);
        code_et.addTextChangedListener(new watcher());
        passwd_et.addTextChangedListener(new watcher());
        submit_passwd_et.addTextChangedListener(new watcher());
    }

    /**
     * 确定键监听
     *
     * @author Administrator
     */
    private class watcher implements TextWatcher {

        @Override
        public void onTextChanged(CharSequence s, int arg1, int arg2, int arg3) {
            String a = submit_passwd_et.getText().toString().trim();
            String b = code_et.getText().toString().trim();
            String c = passwd_et.getText().toString().trim();
            if (!a.equals("") && !b.equals("") && !c.equals("")) {
                submit_bt.setClickable(true);
                submit_bt.setBackgroundResource(R.drawable.btn_bg_select);
            } else {
                submit_bt.setClickable(false);
                submit_bt.setBackgroundResource(R.drawable.btn_blue_light_bg);
            }
        }

        @Override
        public void beforeTextChanged(CharSequence s, int arg1, int arg2, int arg3) {
        }

        @Override
        public void afterTextChanged(Editable arg0) {
        }
    }

    protected void submit() {
        String mobile = mobileNoSms.toString().trim();
        String code = code_et.getText().toString();
        String passwd = passwd_et.getText().toString();
        String submit_passwd = submit_passwd_et.getText().toString();
        if (mobile.equals("") || code.equals("") || passwd.equals("") || submit_passwd.equals("")) {
            return;
        }
        // 注册、忘记密码 只可以输入8-16 位混合(数字，字母)
        boolean b = CommonUtil.isPwdRegular(passwd);
        boolean b2 = CommonUtil.isPwCh(passwd);
        if (!b) {
            // 提示相应信息
            ToastUtil.show(this, R.string.pwd_alert);
            return;
        } else if (b2) {
            ToastUtil.show(this, R.string.pwd_alert);
            return;
        }

        if (mobile.length() != 11 || !mobileNoSms.equals(mobile)) {
            ToastUtil.show(this, R.string.mobile_no_checked);
        } else {
            if ("".equals(code)) {
                ToastUtil.show(this, R.string.code_checked);
            } else {
                if ("".equals(passwd)) {
                    ToastUtil.show(this, R.string.passwd_checked);
                } else {
                    if ("".equals(submit_passwd)) {
                        ToastUtil.show(this, R.string.submit_passwd_checked);
                    } else {
                        if (!submit_passwd.equals(passwd)) {
                            ToastUtil.show(this, R.string.passwd_equal_checked);
                        } else {
                            submit_bt.setText(R.string.sub_mit);
                            try {
                                send(mobile, code, passwd, submit_passwd);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        }
    }

    protected void send(String tel, String code, String passwd, String submit_passwd) throws Exception {
        showWaittingDialog(ForgotPasswdActivity.this);
        RequestParams params = new RequestParams();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("tel", tel);
        jsonObject.put("checksms", code);
        jsonObject.put("password", passwd);
        jsonObject.put("verification", UrlConfig.SMS_IDENTIFY);
        params.put("cipher", MyDES.encrypt(jsonObject.toString()));
        MyhttpClient.desPost(UrlConfig.FORGET_PASSWD, params, new AsyncHttpResponseHandler() {

            @Override
            public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
                super.onFailure(arg0, arg1, arg2, arg3);
                dismissWaittingDialog();
                ToastUtil.show(ForgotPasswdActivity.this, R.string.default_error);
            }

            @Override
            public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
                super.onSuccess(arg0, arg1, arg2);
                dismissWaittingDialog();

                try {
                    String response = new String(arg2, "UTF-8");
                    response = CommonUtil.transResponse(response);
                    JSONObject jsonObject = new JSONObject(response);
                    String cipher = jsonObject.getString("cipher");
                    String desc = MyDES.decrypt(cipher);
                    BaseModel<?> result = new Gson().fromJson(desc, BaseModel.class);
                    if (result.isSuccess()) {
                        ToastUtil.show(getApplication(), R.string.forgot_passwd_success);
                        PreferencesUtils.putBooleanToSPMap(ForgotPasswdActivity.this, PreferencesUtils.Keys.IS_LOGIN, true);
                        PreferencesUtils.clearSPMap(ForgotPasswdActivity.this);
                        Intent intent = new Intent(ForgotPasswdActivity.this, NewLoginActivity.class);
                        intent.putExtra("mobile", mobileNoSms);
                        startActivity(intent);
                        finish();
                    } else {
                        submit_bt.setText(R.string.lockpattern_confirm_button_text);
                        ToastUtil.show(getApplication(), result.getMsg());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_left_img:
                finish();
                break;
            case R.id.forgot_passwd_send_code:// 发送验证码
                sendCode();
                break;
            case R.id.forgot_passwd_submit:// 提交确认
                submit();
                break;
            case R.id.img_password:
                ispsw = !ispsw;
                if (ispsw) {
                    img_password.setImageResource(R.drawable.psw_show);
                    Isvisible(passwd_et, true);

                } else {
                    img_password.setImageResource(R.drawable.psw_disapper);
                    Isvisible(passwd_et, false);
                }

                break;
            case R.id.img_confirm_passw:
                isconfirm = !isconfirm;
                if (isconfirm) {
                    img_confirm_passw.setImageResource(R.drawable.psw_show);
                    Isvisible(submit_passwd_et, true);
                } else {
                    img_confirm_passw.setImageResource(R.drawable.psw_disapper);
                    Isvisible(submit_passwd_et, false);
                }

                break;
            default:
                break;
        }
    }

    /*
     * 设置密码可见并设置光标
     */
    public void Isvisible(EditText edt, boolean isvisible) {
        if (isvisible) {
            edt.setInputType(InputType.TYPE_CLASS_TEXT);
        } else {
            edt.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        }
        CharSequence text = edt.getText();
        if (text instanceof Spannable) {
            Spannable spanText = (Spannable) text;
            Selection.setSelection(spanText, text.length());
        }
    }

    /**
     * 发送验证码
     */
    private void sendCode() {
        SendMobileCode.getInstance().send_code(code_bt, ForgotPasswdActivity.this, mobileNoSms,
                MessageType.MESSAGE_MOBILE_XGDLMM, null, new Back() {
                    @Override
                    public void sms(String result) {
                        if (result != null && !"".equals(result)) {
                            ForgotPasswdActivity.this.sms = result;
                            if (result.trim().equals("0")) {
                                ForgotPasswdActivity.this.finish();
                            }
                        }
                    }
                });
    }
}
