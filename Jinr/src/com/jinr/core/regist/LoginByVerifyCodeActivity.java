package com.jinr.core.regist;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.jinr.core.JinrApp;
import com.jinr.core.MainActivity;
import com.jinr.core.R;
import com.jinr.core.base.BaseActivity;
import com.jinr.core.config.EventBusKey;
import com.jinr.core.config.MessageType;
import com.jinr.core.config.UrlConfig;
import com.jinr.core.security.lockpanttern.pattern.CreateGesturePasswordActivity;
import com.jinr.core.security.lockpanttern.view.LockPatternUtils;
import com.jinr.core.ui.ErrorDialog;
import com.jinr.core.utils.CommonUtil;
import com.jinr.core.utils.MyDES;
import com.jinr.core.utils.MyLog;
import com.jinr.core.utils.MyhttpClient;
import com.jinr.core.utils.PreferencesUtils;
import com.jinr.core.utils.ToastUtil;
import com.jinr.graph_view.TimeButton;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.umeng.message.ALIAS_TYPE;
import com.umeng.message.PushAgent;
import com.umeng.message.UTrack;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;
import org.simple.eventbus.EventBus;

import java.io.UnsupportedEncodingException;

import model.BaseModel;
import model.UidObj;
import model.UserBindinfo;
import model.UserInfo;

/**
 * Created by: 966 on 2017/1/3.
 */
public class LoginByVerifyCodeActivity extends BaseActivity implements View.OnClickListener {

    private TextView tv_center_title;
    private EditText ed_login_verify;
    private TimeButton btn_msg_again;
    private Button btn_login_verify;

    private String telPhone;
    private String user_id = "";
    private ErrorDialog errorDialog;

    LockPatternUtils lockp = new LockPatternUtils(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_verifycode);
        findViewById();
        initData();
        setListener();
    }

    @Override
    protected void findViewById() {
        tv_center_title = (TextView) findViewById(R.id.title_center_text);
        ed_login_verify = (EditText) findViewById(R.id.ed_login_verify);
        btn_msg_again = (TimeButton) findViewById(R.id.btn_msg_again);
        btn_login_verify = (Button) findViewById(R.id.btn_login_verify);
        findViewById(R.id.title_left_img).setOnClickListener(this);
    }

    @Override
    protected void initData() {
        tv_center_title.setText("验证码登录");
        telPhone = getIntent().getStringExtra("mobile");
        user_id = PreferencesUtils.getValueFromSPMap(this, PreferencesUtils.Keys.UID);
    }

    @Override
    protected void initUI() {

    }

    @Override
    protected void setListener() {
        btn_login_verify.setOnClickListener(this);
        btn_msg_again.setOnClickListener(this);
        ed_login_verify.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String inviteCode = ed_login_verify.getText().toString().trim();
                if (!TextUtils.isEmpty(inviteCode) && inviteCode.length() == 6) {
                    btn_login_verify.setClickable(true);
                    btn_login_verify.setBackgroundResource(R.drawable.btn_blue_bg);
                } else {
                    btn_login_verify.setClickable(false);
                    btn_login_verify.setBackgroundResource(R.drawable.btn_blue_light_bg);
                }
            }
        });
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.title_left_img:
                if (!PreferencesUtils.getValueFromSPMap(this, PreferencesUtils.Keys.UID).equals("")) {
                    EventBus.getDefault().post(user_id, EventBusKey.LOGIN_SUCCESS);
                }
                String str = getIntent().getStringExtra("unlock_forget");
                if (PreferencesUtils.Keys.BACK_TO_ACT != 0 || PreferencesUtils.Keys.BACK_TO_GIFT) {
                    PreferencesUtils.Keys.BACK_TO_ACT = 0;
                    PreferencesUtils.Keys.BACK_TO_GIFT = false;
                }
                if (str != null && str.equals("unlock_forget")) { // 如果在忘记密码跳转到登录界面的情况下
                    Intent intent_back = new Intent(this, MainActivity.class);
                    startActivity(intent_back);
                    finish();
                } else {
                    finish();
                }
                break;

            case R.id.btn_msg_again:
                try {
                    getVerifyCodeRequest();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            case R.id.btn_login_verify:
                String verufyCode = ed_login_verify.getText().toString().trim();
                try {
                    sendLoginRequest(verufyCode);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (!PreferencesUtils.getValueFromSPMap(this, PreferencesUtils.Keys.UID).equals("")) {
                EventBus.getDefault().post(user_id, EventBusKey.LOGIN_SUCCESS);
            }
            if ((!(PreferencesUtils.Keys.BACK_TO_ACT == 0)) || (PreferencesUtils.Keys.BACK_TO_GIFT)) {
                PreferencesUtils.Keys.BACK_TO_ACT = 0;
                PreferencesUtils.Keys.BACK_TO_GIFT = false;
                sendBroadcast(new Intent().setAction("action.refresh_actdetail"));
                finish();
            } else {
                MainActivity.isLock = true;
                Intent intent = new Intent(LoginByVerifyCodeActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        }
        return true;
    }

    protected void getVerifyCodeRequest() throws Exception {
        RequestParams params = new RequestParams();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("tel", telPhone);
        jsonObject.put("kw", MessageType.MOBILE_LOGIN);
        params.put("cipher", MyDES.encrypt(jsonObject.toString()));
        MyhttpClient.desPost(UrlConfig.SMS_SENDSMS, params, new AsyncHttpResponseHandler() {

            @Override
            public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
                super.onFailure(arg0, arg1, arg2, arg3);
                dismissWaittingDialog();
                ToastUtil.show(LoginByVerifyCodeActivity.this, R.string.default_error);
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
                    MyLog.i("======", desc);
                    BaseModel<String> result = new Gson().fromJson(desc, new TypeToken<BaseModel<String>>() {
                    }.getType());
                    if (result.isSuccess()) {

                    } else {
                        ToastUtil.show(getApplication(), result.getMsg());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    protected void sendLoginRequest(String verifyCode) throws Exception {
        RequestParams params = new RequestParams();
        String equipment_number = JinrApp.instance().g_imei;
        String use_terminal = "android";
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("equipment_number", equipment_number);
        jsonObject.put("checksms", verifyCode);
        jsonObject.put("tel", telPhone);
        jsonObject.put("use_terminal", use_terminal);
        params.put("cipher", MyDES.encrypt(jsonObject.toString()));
        MyhttpClient.desPost(UrlConfig.VERIFY_CODE_LOGIN, params, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                super.onStart();
                showWaittingDialog(LoginByVerifyCodeActivity.this);
            }

            @Override
            public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
                super.onFailure(arg0, arg1, arg2, arg3);
                dismissWaittingDialog();
                ToastUtil.show(LoginByVerifyCodeActivity.this, R.string.default_error);
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
                    MyLog.i("======1", desc);
                    if (TextUtils.isEmpty(response)) return;
                    BaseModel<UserInfo> result = new Gson().fromJson(desc, new TypeToken<BaseModel<UserInfo>>() {
                    }.getType());
                    if (result.isSuccess()) {
                        // 图形解锁忘记密码跳转到重设图形解锁密码界面
                        Intent intent = getIntent();
                        String str = intent.getStringExtra("unlock_forget");
                        if (str != null && str.equals("unlock_forget")) { // 如果在忘记密码跳转到登录界面的情况下
                            lockp.clear_lock_off_on();
                            new Thread() {
                                public void run() {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            lockp.clearLock();
                                        }
                                    });
                                }
                            }.start();
                            PreferencesUtils.clearSPMap(getApplicationContext());
                            Intent intent_return = new Intent();
                            intent_return.putExtra("return", "T");
                            setResult(RESULT_OK, intent_return);
                        }

                        EventBus.getDefault().post(result.getData().getId(), EventBusKey.LOGIN_SUCCESS);
                        JinrApp.instance().state_bankBind = Integer.valueOf(result.getData().getBk());
                        PreferencesUtils.putIntToSPMap(LoginByVerifyCodeActivity.this, PreferencesUtils.Keys.IS_BIND_CARD, Integer.valueOf(result.getData().getBk()));
                        PreferencesUtils.putValueToSPMap(LoginByVerifyCodeActivity.this, PreferencesUtils.Keys.UID, result.getData().getId());
                        PreferencesUtils.putValueToSPMap(LoginByVerifyCodeActivity.this, PreferencesUtils.Keys.NICKNAME, result.getData().getNikename());
                        PreferencesUtils.putValueToSPMap(LoginByVerifyCodeActivity.this, PreferencesUtils.Keys.TEL, result.getData().getTel());
                        PreferencesUtils.putValueToSPMap(LoginByVerifyCodeActivity.this, PreferencesUtils.Keys.NAME, result.getData().getName());
                        PreferencesUtils.putValueToSPMap(LoginByVerifyCodeActivity.this, PreferencesUtils.Keys.BUSS_PWD, result.getData().getBuss_pwd());
                        PreferencesUtils.putValueToSPMap(LoginByVerifyCodeActivity.this, PreferencesUtils.Keys.ID_CARD, result.getData().getCard_id());
                        PreferencesUtils.putBooleanToSPMap(LoginByVerifyCodeActivity.this, PreferencesUtils.Keys.IS_LOGIN, true);
                        if (!result.getData().getId().equals(PreferencesUtils.getSValueFromSPMap(LoginByVerifyCodeActivity.this, PreferencesUtils.Keys.UID))) {
                            PreferencesUtils.clearShPMap(LoginByVerifyCodeActivity.this);
                            PreferencesUtils.putSValueToSPMap(LoginByVerifyCodeActivity.this, PreferencesUtils.Keys.UID, result.getData().getId());
                        }
                        // 根据网络获取 判断 是否实名 有无支付密码
                        if (!"".equals(PreferencesUtils.getValueFromSPMap(LoginByVerifyCodeActivity.this, PreferencesUtils.Keys.NAME))
                                && !"".equals(PreferencesUtils.getValueFromSPMap(LoginByVerifyCodeActivity.this, PreferencesUtils.Keys.ID_CARD))) {// 实名
                            PreferencesUtils.putBooleanToSPMap(LoginByVerifyCodeActivity.this, PreferencesUtils.Keys.IS_IDENTIFY, true);
                        }
                        if (!"".equals(PreferencesUtils.getValueFromSPMap(LoginByVerifyCodeActivity.this, PreferencesUtils.Keys.BUSS_PWD))) {// 交易密码
                            PreferencesUtils.putBooleanToSPMap(LoginByVerifyCodeActivity.this, PreferencesUtils.Keys.HAS_DEAL_PASSWD, true);
                        }
                        user_id = PreferencesUtils.getValueFromSPMap(LoginByVerifyCodeActivity.this, PreferencesUtils.Keys.UID);
                        PushAgent mPushAgent = PushAgent.getInstance(LoginByVerifyCodeActivity.this);
                        mPushAgent.addAlias(user_id, ALIAS_TYPE.SINA_WEIBO, new UTrack.ICallBack() {
                            @Override
                            public void onMessage(boolean isSuccess, String message) {

                            }
                        });
                        getBindCardInfo();
                    } else {
                        dismissWaittingDialog();
                        ToastUtil.show(getApplication(), result.getMsg());
                    }
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                    MyLog.i("错误", "解析错误");
                } catch (JsonSyntaxException e) {
                    MyLog.i("json解析错误", "解析错误");
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    // 获取绑定卡的信息
    private void getBindCardInfo() throws Exception {
        RequestParams params = new RequestParams();
        UidObj obj = new UidObj(PreferencesUtils.getValueFromSPMap(LoginByVerifyCodeActivity.this, PreferencesUtils.Keys.UID));
        String desStr = MyDES.encrypt(new Gson().toJson(obj));
        params.put("cipher", desStr);
        MyhttpClient.desPost(UrlConfig.USER_BANKINFO, params, new AsyncHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
            }

            @Override
            public void onFinish() {
                super.onFinish();
                dismissWaittingDialog();
                ToastUtil.show(getApplication(), LoginByVerifyCodeActivity.this.getResources().getString(R.string.login_in_success));
                isSetLock();// 判断是否弹出图形解锁设置提示框
            }

            @Override
            public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
                super.onFailure(arg0, arg1, arg2, arg3);
                ToastUtil.show(LoginByVerifyCodeActivity.this, R.string.default_error);
                dismissWaittingDialog();
            }

            @Override
            public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
                super.onSuccess(arg0, arg1, arg2);
                String response;
                try {
                    response = new String(arg2, "UTF-8");
                    JSONObject jsonObject = new JSONObject(response);
                    String cipher = jsonObject.getString("cipher");
                    String desc = MyDES.decrypt(cipher);
                    MyLog.i("======2", desc);
                    if (TextUtils.isEmpty(desc)) return;
                    BaseModel<UserBindinfo> result = new Gson().fromJson(desc, new TypeToken<BaseModel<UserBindinfo>>() {
                    }.getType());
                    if (result.isSuccess()) {
                        UserBindinfo data = result.getData();
                        if (data == null)
                            return;
                        JinrApp.instance().state_bankBind = Integer.valueOf(data.getState_bankBind());
                        JinrApp.instance().state_tradePassword = Integer.valueOf(data.getState_tradePassword());
                        PreferencesUtils.putIntToSPMap(LoginByVerifyCodeActivity.this, PreferencesUtils.Keys.IS_BIND_CARD, Integer.valueOf(data.getState_bankBind()));
                        PreferencesUtils.putIntToSPMap(LoginByVerifyCodeActivity.this, PreferencesUtils.Keys.IS_SET_TRADE_PWD, Integer.valueOf(data.getState_tradePassword()));
                        PreferencesUtils.putValueToSPMap(LoginByVerifyCodeActivity.this, PreferencesUtils.Keys.BANK_BG_URL, data.getBgimg());
                        PreferencesUtils.putValueToSPMap(LoginByVerifyCodeActivity.this, PreferencesUtils.Keys.BANK_LOGO_URL, data.getImg());
                        PreferencesUtils.putValueToSPMap(LoginByVerifyCodeActivity.this, PreferencesUtils.Keys.BANK_CARD_ID, data.getId());
                        PreferencesUtils.putValueToSPMap(LoginByVerifyCodeActivity.this, PreferencesUtils.Keys.BANK_CARD_NO, data.getBank_no());
                        PreferencesUtils.putValueToSPMap(LoginByVerifyCodeActivity.this, PreferencesUtils.Keys.BANK_NAME, data.getBank_name());
                        PreferencesUtils.putValueToSPMap(LoginByVerifyCodeActivity.this, PreferencesUtils.Keys.USER_TEL, data.getUser_tel());
                        PreferencesUtils.putValueToSPMap(LoginByVerifyCodeActivity.this, PreferencesUtils.Keys.CARD_LAST, data.getCard_last());
                    } else {
                        ToastUtil.show(LoginByVerifyCodeActivity.this, result.getMsg());
                    }
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }


    public void isSetLock() {
        user_id = PreferencesUtils.getValueFromSPMap(this, PreferencesUtils.Keys.UID);
        MainActivity.isLock = true;
        MyLog.i("isSetLock", user_id);
        if (!user_id.equals(lockp.userID()) || !lockp.savedPatternExists()) {
            lockp.clear_lock_off_on();
            new Thread() {
                public void run() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            lockp.clearLock();
                        }
                    });
                }
            }.start();

            errorDialog = new ErrorDialog(LoginByVerifyCodeActivity.this, R.string.warning, R.string.dialog_set_hint, R.string.dialog_set);
            errorDialog.dialog_retry.setOnClickListener(new View.OnClickListener() {// 设置手势

                @Override
                public void onClick(View v) {
                    errorDialog.dismiss();
                    PreferencesUtils.putBooleanToSPMap(LoginByVerifyCodeActivity.this, PreferencesUtils.Keys.IS_SETTING_CG, true);
                    Intent intent_CreateGesturePasswordActivity = new Intent(LoginByVerifyCodeActivity.this, CreateGesturePasswordActivity.class);
                    Intent intent = getIntent();
                    String str = intent.getStringExtra("unlock_forget");
                    // //如果不是在忘记密码跳转到登录界面的情况下
                    if (PreferencesUtils.Keys.BACK_TO_ACT != 0 || PreferencesUtils.Keys.BACK_TO_GIFT || PreferencesUtils.Keys.BACK_TO_ABOUT) {
                        EventBus.getDefault().post(user_id, EventBusKey.LOGIN_SUCCESS);
                    } else {
                        intent_CreateGesturePasswordActivity.putExtra("gotoMainActive", "gotoMainActive");
                        MainActivity.isLock_longin = false;
                    }
                    startActivity(intent_CreateGesturePasswordActivity);
                    sendBroadcast(new Intent().setAction("action.refresh_entrance"));
                    finish();
                }
            });
            errorDialog.dialog_cancle.setOnClickListener(new View.OnClickListener() {// 不设置
                @Override
                public void onClick(View v) {
                    errorDialog.dismiss();
                    new Thread() {
                        public void run() {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    lockp.clearLock();
                                }
                            });
                        }
                    }.start();
                    lockp.lockPattern_off(user_id);
                    Intent intent = getIntent();
                    String str = intent.getStringExtra("unlock_forget");
                    Intent intentMainActivity = null;
                    if (!(str != null && str.equals("unlock_forget"))) { // 如果不是在忘记密码跳转到登录界面的情况下
                        if (PreferencesUtils.Keys.BACK_TO_ACT != 0 || PreferencesUtils.Keys.BACK_TO_ABOUT) {
                            PreferencesUtils.Keys.BACK_TO_ABOUT = false;
                            if (PreferencesUtils.Keys.BACK_TO_ACT != 0 || PreferencesUtils.Keys.BACK_TO_GIFT) {
                                PreferencesUtils.Keys.BACK_TO_ACT = 0;
                                PreferencesUtils.Keys.BACK_TO_GIFT = false;
                                sendBroadcast(new Intent().setAction("action.refresh_actdetail"));
                                EventBus.getDefault().post(user_id, EventBusKey.LOGIN_SUCCESS);
                            }
                        } else {
                            intentMainActivity = new Intent(LoginByVerifyCodeActivity.this, MainActivity.class);
                            intentMainActivity.putExtra("gotoMainActive", "gotoMainActive");
                            MainActivity.isLock_longin = false;
                            startActivity(intentMainActivity);
                        }
                    }
                    sendBroadcast(new Intent().setAction("action.refresh_entrance"));
                    finish();
                }
            });
            errorDialog.show();
        } else {
            Intent intent = getIntent();
            String str = intent.getStringExtra("unlock_forget");
            if (!(str != null && str.equals("unlock_forget"))) { // 如果不是在忘记密码跳转到登录界面的情况下
                MainActivity.isLock_longin = false;
            }
            if (PreferencesUtils.Keys.BACK_TO_ACT != 0 || PreferencesUtils.Keys.BACK_TO_GIFT || PreferencesUtils.Keys.BACK_TO_ABOUT) {
                PreferencesUtils.Keys.BACK_TO_ACT = 0;
                PreferencesUtils.Keys.BACK_TO_GIFT = false;
                PreferencesUtils.Keys.BACK_TO_ABOUT = false;
                sendBroadcast(new Intent().setAction("action.refresh_actdetail"));
                EventBus.getDefault().post(user_id, EventBusKey.LOGIN_SUCCESS);
            } else {
                Intent intentMainActivity = new Intent(LoginByVerifyCodeActivity.this, MainActivity.class);
                startActivity(intentMainActivity);
            }
            sendBroadcast(new Intent().setAction("action.refresh_entrance"));
            finish();
        }
    }
}
