package com.jinr.core.security;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.jinr.core.R;
import com.jinr.core.base.BaseActivity;
import com.jinr.core.config.UrlConfig;
import com.jinr.core.utils.CommonUtil;
import com.jinr.core.utils.LoadingDialog;
import com.jinr.core.utils.MyDES;
import com.jinr.core.utils.MyhttpClient;
import com.jinr.core.utils.ToastUtil;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONObject;

/**
 * 忘记密码第一步 提交手机号获取验证码
 *
 * @author 604
 */
public class ForgotPwd1Activity extends BaseActivity implements OnClickListener {

    private ImageView title_left_img; // title左边图片
    private TextView title_center_text; // title标题
    private EditText mobile_et; // 手机号
    private Button submit_bt;// 确认提交
    private LoadingDialog loadingdialog;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forgot_pwd1);
        initData();
        findViewById();
        initUI();
        setListener();
    }

    @Override
    protected void initData() {
        loadingdialog = new LoadingDialog(ForgotPwd1Activity.this);
    }

    @Override
    protected void findViewById() {
        title_left_img = (ImageView) findViewById(R.id.title_left_img);
        title_center_text = (TextView) findViewById(R.id.title_center_text);
        mobile_et = (EditText) findViewById(R.id.forgot_passwd_tel);
        submit_bt = (Button) findViewById(R.id.btn_commit_fp);
    }

    @Override
    protected void initUI() {
        title_center_text.setText(R.string.forgot_the_passwd);
    }

    @Override
    protected void setListener() {
        title_left_img.setOnClickListener(this);
        submit_bt.setOnClickListener(this);
        mobile_et.addTextChangedListener(new editTextListen());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_left_img:
                finish();
                break;
            case R.id.btn_commit_fp:// 提交确认
                if (mobile_et.getText().toString().trim().length() == 0) {
                    return;
                }
                if (mobile_et.getText().toString().trim().length() != 11) {
                    ToastUtil.show(this, R.string.mobile_no_checked);
                } else {
                    try {
                        SendNumber(mobile_et.getText().toString().trim());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
            default:
                break;
        }
    }

    /**
     * 监听输入框的状态，初始化ui按钮
     */
    private class editTextListen implements TextWatcher {

        @Override
        public void onTextChanged(CharSequence s, int arg1, int arg2, int arg3) {
            String a = mobile_et.getText().toString().trim();
            if (!a.equals("")) {
                submit_bt.setClickable(true);
                submit_bt.setBackgroundResource(R.drawable.btn_blue_bg);
            } else {
                if (submit_bt.isClickable()) {
                    submit_bt.setClickable(false);
                    submit_bt.setBackgroundResource(R.drawable.btn_blue_light_bg);
                }
            }
        }

        @Override
        public void beforeTextChanged(CharSequence s, int arg1, int arg2, int arg3) {
        }

        @Override
        public void afterTextChanged(Editable arg0) {
        }
    }

    public void SendNumber(String tellphone) throws Exception {
        RequestParams params = new RequestParams();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("tel", tellphone);
        params.put("cipher", MyDES.encrypt(jsonObject.toString()));
        loadingdialog.show();
        MyhttpClient.desPost(UrlConfig.USER_JUDGE_TEL, params, new AsyncHttpResponseHandler() {
            @Override
            public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
                super.onFailure(arg0, arg1, arg2, arg3);
                loadingdialog.dismiss();
                ToastUtil.show(ForgotPwd1Activity.this, R.string.default_error);
            }

            @Override
            public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
                super.onSuccess(arg0, arg1, arg2);
                loadingdialog.dismiss();
                try {
                    String response = new String(arg2, "UTF-8");
                    response = CommonUtil.transResponse(response);
                    JSONObject job = new JSONObject(response);
                    String cipher = job.getString("cipher");
                    String desc = MyDES.decrypt(cipher);
                    JSONObject obj = new JSONObject(desc);
                    int isSuccess = obj.getInt("code");
                    if (isSuccess == 1000) {// 可以进入到下一步
                        Intent intentForgot = new Intent(ForgotPwd1Activity.this, ForgotPasswdActivity.class);
                        intentForgot.putExtra("phone", mobile_et.getText().toString().trim());
                        startActivity(intentForgot);
                    } else {
                        ToastUtil.show(ForgotPwd1Activity.this, "该手机号尚未注册或不存在");
                    }
                } catch (Exception e) {
                }
            }
        });
    }
}
