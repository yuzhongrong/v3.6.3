package com.jinr.core.regist;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputType;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.jinr.core.R;
import com.jinr.core.base.BaseActivity;
import com.jinr.core.config.UrlConfig;
import com.jinr.core.utils.CommonUtil;
import com.jinr.core.utils.MyDES;
import com.jinr.core.utils.MyEditText;
import com.jinr.core.utils.MyhttpClient;
import com.jinr.core.utils.ToastUtil;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONObject;

import model.BaseModel;

public class ResetPasswordActivity extends BaseActivity implements View.OnClickListener {

    private ImageView image_back;
    private MyEditText et_newpsw;
    private EditText et_pswagain;
    private TextView tv_pswlook;
    private TextView tv_pswlookagain;
    private Button bt_sure;
    private Thread mUiThread;
    private Handler mHandler;
    private boolean one;
    private boolean two;
    private String phoneNum;
    private String phoneMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        Intent intent = getIntent();
        phoneNum = intent.getStringExtra("phoneNum");
        phoneMessage = intent.getStringExtra("phoneMessage");
        findViewById();
        setListener();
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            nextClickableListener();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void findViewById() {
        image_back = (ImageView) findViewById(R.id.image_back);
        et_newpsw = (MyEditText) findViewById(R.id.et_newpsw);
        et_pswagain = (EditText) findViewById(R.id.et_pswagain);
        tv_pswlook = (TextView) findViewById(R.id.tv_pswlook);
        tv_pswlookagain = (TextView) findViewById(R.id.tv_pswlookagain);
        bt_sure = (Button) findViewById(R.id.bt_sure);
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 1:
                        bt_sure.setOnClickListener(ResetPasswordActivity.this);
                        bt_sure.setBackgroundResource(R.drawable.btn_blue_bg);
                        break;
                    case 2:
                        bt_sure.setOnClickListener(null);
                        bt_sure.setBackgroundResource(R.drawable.btn_blue_light_bg);
                        break;
                }
                super.handleMessage(msg);
            }
        };
    }

    @Override
    protected void setListener() {
        image_back.setOnClickListener(this);
        tv_pswlook.setOnClickListener(this);
        tv_pswlookagain.setOnClickListener(this);
        bt_sure.setOnClickListener(this);
    }

    @Override
    protected void initUI() {

    }

    @Override
    protected void initData() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.image_back:
                finish();
                break;
            case R.id.tv_pswlook:
                visiblely(one, tv_pswlook, et_newpsw, 1);
                break;
            case R.id.tv_pswlookagain:
                visiblely(two, tv_pswlookagain, et_pswagain, 2);
                break;
            case R.id.bt_sure:
                prepaerReset();
                break;
            default:
                break;
        }
    }

    /**
     * 准备重置密码 @author Ysw created at 2017/3/9 17:47
     */
    private void prepaerReset() {
        String passWord = et_newpsw.getText().toString().trim();
        String passWordAgain = et_pswagain.getText().toString().trim();
        if (TextUtils.isEmpty(passWord)) {
            ToastUtil.show(ResetPasswordActivity.this, "请输入您的新密码");
        } else if (!CommonUtil.isPwdRegular(passWord)) {
            ToastUtil.show(ResetPasswordActivity.this, "您输入的密码格式不正确");
            et_newpsw.setText("");
        } else if (TextUtils.isEmpty(passWordAgain)) {
            ToastUtil.show(ResetPasswordActivity.this, "请再次输入您的新密码");
        } else if (!CommonUtil.isPwdRegular(passWordAgain)) {
            ToastUtil.show(ResetPasswordActivity.this, "您输入的密码格式不正确");
            et_pswagain.setText("");
        } else if (!passWord.equals(passWordAgain)) {
            ToastUtil.show(ResetPasswordActivity.this, "您输入的密码不一致");
            et_pswagain.setText("");
        } else {
            try {
                resetPassword(passWord);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 重置密码 @author Ysw created at 2017/3/9 17:58
     */
    private void resetPassword(String passWord) throws Exception {
        RequestParams params = new RequestParams();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("tel", phoneNum);
        jsonObject.put("checksms", phoneMessage);
        jsonObject.put("password", passWord);
        jsonObject.put("verification", UrlConfig.SMS_IDENTIFY);
        params.put("cipher", MyDES.encrypt(jsonObject.toString()));
        MyhttpClient.desPost(UrlConfig.FORGET_PASSWD, params, new AsyncHttpResponseHandler() {

            @Override
            public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
                super.onFailure(arg0, arg1, arg2, arg3);
                dismissWaittingDialog();
                ToastUtil.show(ResetPasswordActivity.this, R.string.default_error);
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
                        setResult(RESULT_OK);
                        finish();
                    } else {
                        ToastUtil.show(getApplication(), result.getMsg());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 控制新密码的可见性 @author Ysw created at 2017/3/9 17:29
     */
    public void visiblely(boolean isShow, TextView textView, EditText editText, int num) {
        if (num == 1) {
            one = !isShow;
        } else if (num == 2) {
            two = !isShow;
        }
        if (isShow) {
            textView.setText("隐藏");
            editText.setInputType(InputType.TYPE_CLASS_TEXT);
        } else {
            textView.setText("显示");
            editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);// 隐藏密码
        }
        CharSequence text = editText.getText();
        if (text != null) {
            Spannable spanText = (Spannable) text;
            Selection.setSelection(spanText, text.length());
        }
    }

    /**
     * 监听确定按钮是否可以点击 @author Ysw created at 2017/3/9 17:20
     */
    public void nextClickableListener() throws InterruptedException {
        mUiThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    int passWord = et_newpsw.getText().toString().trim().length();
                    int passWordAgain = et_pswagain.getText().toString().trim().length();
                    if (passWord > 0 && passWordAgain > 0) {
                        Message message = mHandler.obtainMessage();
                        message.what = 1;
                        mHandler.sendMessage(message);
                    } else {
                        Message message = mHandler.obtainMessage();
                        message.what = 2;
                        mHandler.sendMessage(message);
                    }
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        mUiThread.start();
    }

    /**
     * 界面销毁的时候记得将线程结束 @author Ysw created at 2017/3/9 15:17
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mUiThread != null) {
            mUiThread.interrupt();
            mUiThread = null;
        }
    }
}
