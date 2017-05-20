package com.jinr.core.security.loginpwd;

import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jinr.core.R;
import com.jinr.core.base.BaseActivity;
import com.jinr.core.config.UrlConfig;
import com.jinr.core.utils.CommonUtil;
import com.jinr.core.utils.MyDES;
import com.jinr.core.utils.MyLog;
import com.jinr.core.utils.MyhttpClient;
import com.jinr.core.utils.PreferencesUtils;
import com.jinr.core.utils.ToastUtil;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import model.BaseModel;
import model.ChangeMobile;

public class ChangeLoginPwd extends BaseActivity implements OnClickListener {

    private ImageView title_left_img; // title左边图片
    private TextView title_center_text; // title标题

    private ImageView btnEyeOld;
    private ImageView btnEyeNew1;
    private ImageView btnEyeNew2;
    private Button btnSure;

    private TextView txtPwdOld;
    private TextView txtPwdNew1;
    private TextView txtPwdNew2;

    private Boolean isEyeOld = false;
    private Boolean isEyeNew1 = false;
    private Boolean isEyeNew2 = false;


    private String user_id = "";
    private String tel_no = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aty_chg_login_pwd);
        initData();
        findViewById();
        initUI();
        setListener();
    }

    @Override
    protected void initData() {
        user_id = PreferencesUtils.getValueFromSPMap(this, PreferencesUtils.Keys.UID);
        tel_no = PreferencesUtils.getValueFromSPMap(this, PreferencesUtils.Keys.TEL);
    }

    @Override
    protected void findViewById() {
        title_center_text = (TextView) findViewById(R.id.title_center_text);
        title_left_img = (ImageView) findViewById(R.id.title_left_img);

        btnEyeOld = (ImageView) findViewById(R.id.btn_eye_old);
        btnEyeNew1 = (ImageView) findViewById(R.id.btn_eye_new1);
        btnEyeNew2 = (ImageView) findViewById(R.id.btn_eye_new2);

        txtPwdOld = (TextView) findViewById(R.id.txt_pwd_old);
        txtPwdNew1 = (TextView) findViewById(R.id.txt_pwd_new1);
        txtPwdNew2 = (TextView) findViewById(R.id.txt_pwd_new2);

        btnSure = (Button) findViewById(R.id.btn_sure);
    }

    @Override
    protected void initUI() {
        title_center_text.setText(R.string.change_login_passwd);
        txtPwdOld.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD | InputType.TYPE_CLASS_TEXT);
        txtPwdNew1.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD | InputType.TYPE_CLASS_TEXT);
        txtPwdNew2.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD | InputType.TYPE_CLASS_TEXT);
    }

    @Override
    protected void setListener() {
        title_left_img.setOnClickListener(this);
        btnEyeOld.setOnClickListener(this);
        btnEyeNew1.setOnClickListener(this);
        btnEyeNew2.setOnClickListener(this);
        txtPwdOld.addTextChangedListener(new watcher());
        txtPwdNew1.addTextChangedListener(new watcher());
        txtPwdNew2.addTextChangedListener(new watcher());
        btnSure.setOnClickListener(this);
        btnSure.setClickable(false);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_left_img:
                finish();
            case R.id.btn_eye_old:
                isEyeOld = !isEyeOld;
                setEye(btnEyeOld, txtPwdOld, isEyeOld);
                break;
            case R.id.btn_eye_new1:
                isEyeNew1 = !isEyeNew1;
                setEye(btnEyeNew1, txtPwdNew1, isEyeNew1);
                break;
            case R.id.btn_eye_new2:
                isEyeNew2 = !isEyeNew2;
                setEye(btnEyeNew2, txtPwdNew2, isEyeNew2);
                break;
            case R.id.btn_sure:
                clickSure();
                break;
        }
    }

    // ---------------------------------------------------------------------
    private void clickSure() {
        String old_passwd = txtPwdOld.getText().toString();
        String new_passwd = txtPwdNew1.getText().toString();
        String submit_new_passwd = txtPwdNew2.getText().toString();
        // 注册、忘记密码 只可以输入6-16 位混合(数字，字母)
        boolean b = CommonUtil.isPwdRegular(new_passwd);
        boolean b2 = CommonUtil.isPwCh(new_passwd);
        if (!b) {
            // 提示相应信息
            ToastUtil.show(this, R.string.pwd_alert);
            return;
        } else if (b2) {
            ToastUtil.show(this, R.string.pwd_alert);
            return;
        }
        if ("".equals(old_passwd)) {
            ToastUtil.show(this, getResources().getString(R.string.please_input) + getResources().getString(R.string.old_login_passwd));
        } else {
            if ("".equals(new_passwd) || new_passwd.length() < 6) {
                ToastUtil.show(this, R.string.passwd_limited);
            } else {
                if (("".equals(submit_new_passwd))) {
                    ToastUtil.show(this, getResources().getString(R.string.please_input)
                            + getResources().getString(R.string.register_submit_passwd));
                } else {
                    if (!new_passwd.equals(submit_new_passwd)) {
                        ToastUtil.show(this, R.string.passwd_equal_checked);
                    } else {
                        showWaittingDialog(ChangeLoginPwd.this);
                        try {
                            send(old_passwd, new_passwd, submit_new_passwd);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    private void send(String old_passwd, String new_passwd, String submit_passwd) throws Exception {
        RequestParams params = new RequestParams();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("user_password", old_passwd);
        jsonObject.put("user_lpassword", new_passwd);
        jsonObject.put("tel", tel_no);
        jsonObject.put("uid", user_id);
        params.put("cipher", MyDES.encrypt(jsonObject.toString()));
        MyhttpClient.desPost(UrlConfig.YONHU_PASSWORD, params, new AsyncHttpResponseHandler() {

            @Override
            public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
                super.onFailure(arg0, arg1, arg2, arg3);
                dismissWaittingDialog();
                ToastUtil.show(ChangeLoginPwd.this, R.string.default_error);

            }

            @Override
            public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
                super.onSuccess(arg0, arg1, arg2);
                dismissWaittingDialog();

                try {
                    String response = new String(arg2, "UTF-8");
                    MyLog.i("修改登录密码返回", response);
                    response = CommonUtil.transResponse(response);
                    JSONObject jsonObject = new JSONObject(response);
                    String cipher = jsonObject.getString("cipher");
                    String desc = MyDES.decrypt(cipher);
                    BaseModel<ChangeMobile> result = new Gson().fromJson(desc, new TypeToken<BaseModel<ChangeMobile>>() {
                    }.getType());
                    if (result.isSuccess()) {
                        ToastUtil.show(getApplicationContext(), R.string.change_passwd_success);
                        finish();
                    } else {
                        ToastUtil.show(getApplication(), result.getMsg());
                    }

                } catch (Exception e) {
                    MyLog.i("json解析错误", "解析错误");
                }
            }
        });
    }

    private class watcher implements TextWatcher {
        @Override
        public void afterTextChanged(Editable s) {

        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String a = txtPwdOld.getText().toString().trim();
            String b = txtPwdNew1.getText().toString().trim();
            String c = txtPwdNew2.getText().toString().trim();
            if (!a.equals("") && !b.equals("") && !c.equals("")) {
                btnSure.setClickable(true);
                btnSure.setBackgroundResource(R.drawable.btn_bg_select);
            } else {
                btnSure.setClickable(false);
                btnSure.setBackgroundResource(R.drawable.btn_blue_light_bg);
            }
        }
    }

    private void setEye(ImageView btn, TextView txt, Boolean bool) {
        if (bool) {
            btn.setImageResource(R.drawable.psw_show);
            txt.setInputType(InputType.TYPE_TEXT_VARIATION_NORMAL
                    | InputType.TYPE_CLASS_TEXT);
        } else {
            btn.setImageResource(R.drawable.psw_disapper);
            txt.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD
                    | InputType.TYPE_CLASS_TEXT);
        }
        // 设置输入框光标位置
        CharSequence text = txt.getText();
        if (text instanceof Spannable) {
            Spannable spanText = (Spannable) text;
            Selection.setSelection(spanText, text.length());
        }
    }
}
