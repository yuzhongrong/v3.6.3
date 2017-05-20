package com.jinr.core.regist;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jinr.core.R;
import com.jinr.core.base.BaseActivity;
import com.jinr.core.config.MessageType;
import com.jinr.core.config.UrlConfig;
import com.jinr.core.ui.CannotReceiveTextDialog;
import com.jinr.core.utils.CommonUtil;
import com.jinr.core.utils.DensityUtil;
import com.jinr.core.utils.MyDES;
import com.jinr.core.utils.MyhttpClient;
import com.jinr.core.utils.ToastUtil;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONObject;

import model.BaseModel;
import model.SCONF;

public class ForgetPasswordActivity extends BaseActivity implements View.OnClickListener {

    private ImageView image_back;
    private XEditText et_phoneNum;
    private MyRegistEditText et_message;
    private TextView tv_sendmsg;
    private TextView tv_nomassage;
    private Button bt_reset;
    private RoundProgressBar pb_round;
    private Thread mThread;
    private Thread mUiThread;
    private boolean isRunning = true;
    private int mSecond = 60;
    private Handler mHandler;
    private CannotReceiveTextDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);
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
        et_phoneNum = (XEditText) findViewById(R.id.et_phoneNum);
        et_phoneNum.setSeparator(" ");
        et_phoneNum.setPattern(new int[]{3, 4, 4});
        et_message = (MyRegistEditText) findViewById(R.id.et_message);
        tv_sendmsg = (TextView) findViewById(R.id.tv_sendmsg);
        tv_nomassage = (TextView) findViewById(R.id.tv_nomassage);
        pb_round = (RoundProgressBar) findViewById(R.id.pb_round);
        bt_reset = (Button) findViewById(R.id.bt_reset);
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 1:
                        bt_reset.setOnClickListener(ForgetPasswordActivity.this);
                        bt_reset.setBackgroundResource(R.drawable.btn_blue_bg);
                        break;
                    case 2:
                        bt_reset.setOnClickListener(null);
                        bt_reset.setBackgroundResource(R.drawable.btn_blue_light_bg);
                        break;
                    case 3:
                        tv_sendmsg.setText("重新发送");
                        tv_sendmsg.setVisibility(View.VISIBLE);
                        pb_round.setVisibility(View.GONE);
                        break;
                }
                super.handleMessage(msg);
            }
        };
    }

    @Override
    protected void setListener() {
        image_back.setOnClickListener(this);
        tv_sendmsg.setOnClickListener(this);
        tv_nomassage.setOnClickListener(this);
    }

    @Override
    protected void initUI() {

    }

    @Override
    protected void initData() {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            finish();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.image_back:
                finish();
                break;
            case R.id.tv_sendmsg:
                if (CommonUtil.isFastDoubleClick()) return;
                isRunning = true;
                sendMassage();
                break;
            case R.id.tv_nomassage:
                noMessage();
                break;
            case R.id.bt_reset:
                prepaerLogin();
                break;
            default:
                break;
        }
    }

    /**
     * 收不到短信 @author Ysw created at 2017/3/9 16:52
     */
    private void noMessage() {
        dialog = new CannotReceiveTextDialog(this);
        dialog.close_img.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.setCancelable();
        dialog.show();
    }

    /**
     * 准备验证短信 @author Ysw created at 2017/3/9 16:50
     */
    public void prepaerLogin() {
        String phoneNum = et_phoneNum.toString().trim();
        String phoneMessage = et_message.getText().toString().trim();
        if (TextUtils.isEmpty(phoneNum)) {
            ToastUtil.show(ForgetPasswordActivity.this, "请输入您的手机号码");
        } else if (!DensityUtil.isMobileNO(phoneNum)) {
            ToastUtil.show(ForgetPasswordActivity.this, "您输入的手机号码不正确");
        } else if (TextUtils.isEmpty(phoneMessage)) {
            ToastUtil.show(ForgetPasswordActivity.this, "请输入您的短信验证码");
        } else {
            try {
                verifyMessage(phoneNum, phoneMessage);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 验证手机短信是否正确 @author Ysw created at 2017/3/9 16:31
     */
    private void verifyMessage(final String phoneNum, final String phoneMessage) throws Exception {
        RequestParams params = new RequestParams();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("tel", phoneNum);
        jsonObject.put("code", phoneMessage);
        jsonObject.put("kw", MessageType.MESSAGE_MOBILE_XGDLMM);
        jsonObject.put("isDestroy", false);
        params.put("cipher", MyDES.encrypt(jsonObject.toString()));
        showWaittingDialog(this);
        MyhttpClient.desPost(UrlConfig.SMS_VERIFYUSER, params, new AsyncHttpResponseHandler() {
            @Override
            public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
                super.onFailure(arg0, arg1, arg2, arg3);
                dismissWaittingDialog();
                ToastUtil.show(ForgetPasswordActivity.this, R.string.default_error);
            }

            @Override
            public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
                super.onSuccess(arg0, arg1, arg2);
                dismissWaittingDialog();
                try {
                    String response = new String(arg2, "UTF-8");
                    response = CommonUtil.transResponse(response);
                    response = CommonUtil.transResponse(response);
                    JSONObject job = new JSONObject(response);
                    String cipher = job.getString("cipher");
                    String desc = MyDES.decrypt(cipher);
                    JSONObject obj = new JSONObject(desc);
                    int isSuccess = obj.getInt("code");
                    if (isSuccess == SCONF.SUCCESS) {
                        Intent intent = new Intent();
                        intent.putExtra("phoneNum", phoneNum);
                        intent.putExtra("phoneMessage", phoneMessage);
                        intent.setClass(ForgetPasswordActivity.this, ResetPasswordActivity.class);
                        startActivityForResult(intent, 1);
                    } else {
                        ToastUtil.show(ForgetPasswordActivity.this, obj.getString("msg"));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 准备获取手机短信验证码 @author Ysw created at 2017/3/9 16:20
     */
    private void sendMassage() {
        String phoneNum = et_phoneNum.toString().trim();
        if (TextUtils.isEmpty(phoneNum)) {
            ToastUtil.show(ForgetPasswordActivity.this, "请输入您的手机号码");
        } else if (!DensityUtil.isMobileNO(phoneNum)) {
            ToastUtil.show(ForgetPasswordActivity.this, "您输入的手机号码不正确");
        } else {
            try {
                getPhoneMessage(phoneNum);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 获取手机短信验证码 @author Ysw created at 2017/3/9 16:21
     */
    private void getPhoneMessage(String phoneNum) throws Exception {
        RequestParams params = new RequestParams();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("tel", phoneNum);
        jsonObject.put("kw", MessageType.MESSAGE_MOBILE_XGDLMM);
        params.put("cipher", MyDES.encrypt(jsonObject.toString()));
        MyhttpClient.desPost(UrlConfig.SMS_SENDSMS, params, new AsyncHttpResponseHandler() {

            @Override
            public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
                super.onFailure(arg0, arg1, arg2, arg3);
                dismissWaittingDialog();
                ToastUtil.show(ForgetPasswordActivity.this, R.string.default_error);
            }

            @Override
            public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
                super.onSuccess(arg0, arg1, arg2);
                dismissWaittingDialog();
                String response;
                try {
                    response = new String(arg2, "UTF-8");
                    response = response.substring(response.indexOf("{"));
                    JSONObject jsonObject = new JSONObject(response);
                    String cipher = jsonObject.getString("cipher");
                    String desc = MyDES.decrypt(cipher);
                    BaseModel<String> result = new Gson().fromJson(desc, new TypeToken<BaseModel<String>>() {
                    }.getType());
                    if (result.isSuccess()) {
                        tv_sendmsg.setVisibility(View.GONE);
                        pb_round.setVisibility(View.VISIBLE);
                        startTiming();
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
     * 开始一分钟的倒计时 @author Ysw created at 2017/3/9 15:17
     */
    private void startTiming() {
        mThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (isRunning) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (mSecond > 0) {
                        mSecond--;
                        pb_round.setProgress((60 - mSecond) * 6f, "" + mSecond);
                    } else {
                        pb_round.setProgress((60 - mSecond) * 6f, "");
                        isRunning = false;
                        mSecond = 60;
                        Message message = mHandler.obtainMessage();
                        message.what = 3;
                        mHandler.sendMessage(message);
                    }
                }
            }
        });
        mThread.start();
    }

    /**
     * 监听重置密码按钮是否可以点击 @author Ysw created at 2017/3/9 16:20
     */
    public void nextClickableListener() throws InterruptedException {
        mUiThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    int phone = et_phoneNum.toString().trim().length();
                    int phoneMessage = et_message.getText().toString().trim().length();
                    if (phone > 0 && phoneMessage > 0) {
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
        if (mThread != null) {
            mThread.interrupt();
            mThread = null;
        }
        if (mUiThread != null) {
            mUiThread.interrupt();
            mUiThread = null;
        }
    }
}
