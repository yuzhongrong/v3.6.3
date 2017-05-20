/*
 * RegisterActivity.java
 * @author Andrew Lee
 * 2014-10-20 上午11:18:19
 */
package com.jinr.core.regist;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
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
import android.widget.LinearLayout;
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
import com.jinr.core.ui.CannotReceiveTextDialog;
import com.jinr.core.ui.ErrorDialog;
import com.jinr.core.utils.CommonUtil;
import com.jinr.core.utils.MyDES;
import com.jinr.core.utils.MyLog;
import com.jinr.core.utils.MyhttpClient;
import com.jinr.core.utils.PreferencesUtils;
import com.jinr.core.utils.SendMobileCode;
import com.jinr.core.utils.SendMobileCode.Back;
import com.jinr.core.utils.ToastUtil;
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
import model.SCONF;
import model.UserInfo;

/**
 * 注册页面
 *
 * @author 757
 */
public class RegisterActivity extends BaseActivity implements OnClickListener {
    private Button send_code;// 验证码按钮
    private Button register;// 下一步
    private EditText code_et;// 填写验证码
    private EditText passwd_et;// 密码
    private EditText inviteCodeEd;// 密码
    private String mobile;
    private ImageView title_left_img;
    private TextView title_center_text;
    private Handler handlers = new Handler();
    private ImageView img_psw_dsp;// 是否显示密码
    private boolean ispsw = true;
    private int flag;
    private TextView no_message;// 收不到短信
    private CannotReceiveTextDialog dialog;
    private TextView tv_zc_num;
    private LinearLayout tv_zc_layout;
    private String event_key = "";
    private ErrorDialog errorDialog;
    private String user_id;
    LockPatternUtils lockp = new LockPatternUtils(this);

    public static RegisterActivity newInstance() {
        RegisterActivity f = new RegisterActivity();
        return f;
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);
        findViewById();
        initData();
        initUI();
        setListener();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void initData() {
        flag = getIntent().getIntExtra("flag", flag);
        title_center_text.setText("注册");
        mobile = getIntent().getStringExtra("mobile");
        event_key = getIntent().getStringExtra("event_key");
        try {
            sendInviteCode();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        sendCode();
    }

    @Override
    protected void findViewById() {
        title_center_text = (TextView) findViewById(R.id.title_center_text);
        title_left_img = (ImageView) findViewById(R.id.title_left_img);
        tv_zc_num = (TextView) findViewById(R.id.tv_zc_num);
        send_code = (Button) findViewById(R.id.register_send_code);
        register = (Button) findViewById(R.id.register_submit);
        code_et = (EditText) findViewById(R.id.register_et2);
        passwd_et = (EditText) findViewById(R.id.register_et3);
        inviteCodeEd = (EditText) findViewById(R.id.invite_code_ed);
        img_psw_dsp = (ImageView) findViewById(R.id.img_psw_dsp);
        no_message = (TextView) findViewById(R.id.no_message);// 收不到短信
        tv_zc_layout = (LinearLayout) findViewById(R.id.tv_zc_layout);
    }

    @Override
    protected void initUI() {
    }

    @Override
    protected void setListener() {
        send_code.setOnClickListener(this);
        register.setOnClickListener(this);
        img_psw_dsp.setOnClickListener(this);
        title_left_img.setOnClickListener(this);
        no_message.setOnClickListener(this);
        code_et.addTextChangedListener(new editTextListen());
        passwd_et.addTextChangedListener(new editTextListen());
        register.setClickable(false);
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.register_send_code:// 重新获取验证码
                sendCode();
                break;
            case R.id.register_submit:// 注册提交
                String password = passwd_et.getText().toString();
                String code = code_et.getText().toString();
                String inviteCode = inviteCodeEd.getText().toString();
                // 注册、忘记密码 只可以输入6-16 位混合(数字，字母)
                boolean b = CommonUtil.isPwdRegular(password);
                // 是否有中文
                boolean b2 = CommonUtil.isPwCh(password);
                if (password.contains(" ")) {
                    ToastUtil.show(this, R.string.space_alert);
                    return;
                }
                if ("".equals(code)) {
                    ToastUtil.show(this, R.string.code_checked);
                } else if (password.contains(" ")) {
                    ToastUtil.show(this, R.string.space_alert);
                    return;
                } else if (!b) {
                    // 提示相应信息
                    ToastUtil.show(this, R.string.pwd_alert);
                    return;
                } else if (b2) {
                    ToastUtil.show(this, R.string.pwd_alert);
                    return;
                } else if (password.length() < 6 || password.length() > 16) {
                    ToastUtil.show(this, R.string.pwd_alert);
                    return;
                } else {
                    showWaittingDialog(RegisterActivity.this);
                    try {
                        send(mobile, code, password, inviteCode);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                break;
            case R.id.img_psw_dsp:// 隐藏显示密码
                ispsw = !ispsw;
                if (!ispsw) {
                    img_psw_dsp.setImageResource(R.drawable.psw_disapper);
                    passwd_et.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);// 隐藏密码
                } else {
                    img_psw_dsp.setImageResource(R.drawable.psw_show);
                    passwd_et.setInputType(InputType.TYPE_CLASS_TEXT);
                }
                CharSequence text = passwd_et.getText();
                if (text instanceof Spannable) {
                    Spannable spanText = (Spannable) text;
                    Selection.setSelection(spanText, text.length());
                }
                break;
            case R.id.title_left_img:
                if (!PreferencesUtils.getValueFromSPMap(this, PreferencesUtils.Keys.UID).equals("")) {
                    EventBus.getDefault().post(user_id, EventBusKey.LOGIN_SUCCESS);
                }
                finish();
                break;
            case R.id.no_message:
                dialog = new CannotReceiveTextDialog(RegisterActivity.this);
                dialog.close_img.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                dialog.setCancelable();
                dialog.show();
                break;
            default:
                break;
        }
    }

    protected void sendInviteCode() throws Exception {
        RequestParams params = new RequestParams();
        showWaittingDialog(RegisterActivity.this);
        MyhttpClient.desPost(UrlConfig.USER_INVITE_CODE, params, new AsyncHttpResponseHandler() {
            @Override
            public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
                super.onFailure(arg0, arg1, arg2, arg3);
                dismissWaittingDialog();
                inviteCodeEd.setHint(getResources().getString(
                        R.string.invite_code));
            }

            @Override
            public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
                super.onSuccess(arg0, arg1, arg2);
                dismissWaittingDialog();
                try {
                    String response = new String(arg2, "UTF-8");
                    response = CommonUtil.transResponse(response);
                    JSONObject job = new JSONObject(response);
                    String cipher = job.getString("cipher");
                    String desc = MyDES.decrypt(cipher);
                    JSONObject obj = new JSONObject(desc);
                    int isSuccess = obj.getInt("code");
                    if (isSuccess == SCONF.SUCCESS) {// 可以进入到下一步
                        JSONObject data = obj.getJSONObject("data");
                        inviteCodeEd.setHint(data.getString("placeholder"));
                    } else {
                        inviteCodeEd.setHint(getResources().getString(
                                R.string.invite_code));
                    }
                } catch (Exception e) {
                }
            }
        });
    }

    protected void send(final String mobileNo, String new_code, final String password, String inviteCode) throws Exception {
        RequestParams params = new RequestParams();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("password", password);
        jsonObject.put("checksms", new_code);
        jsonObject.put("tel", mobileNo);
        jsonObject.put("invite_code", inviteCode);
        if (event_key != null && !event_key.equals("")) {
            jsonObject.put("event_key", event_key);
        }
        jsonObject.put("type", "4");
        jsonObject.put("platform", UrlConfig.PLATFORM);
        params.put("cipher", MyDES.encrypt(jsonObject.toString()));
        MyLog.i("UrlConfig.PLATFORM", UrlConfig.PLATFORM);
        MyhttpClient.desPost(UrlConfig.USER_REGISTER, params, new AsyncHttpResponseHandler() {

            @Override
            public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
                super.onFailure(arg0, arg1, arg2, arg3);
                dismissWaittingDialog();
                ToastUtil.show(RegisterActivity.this, R.string.default_error);

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
                    JSONObject job = new JSONObject(desc);
                    MyLog.i("注册返回", response);
                    int code = job.getInt("code");
                    if (code == 1000) {
                        //登录注册后直接请求登录接口
                        sendLoginRequest(mobileNo, password);
                    } else {
                        ToastUtil.show(getApplication(), job.getString("msg"));
                    }

                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    MyLog.i("json解析错误", "解析错误");
                }
            }
        });
    }


    protected void sendLoginRequest(String mobile, String passwd) throws Exception {
        RequestParams params = new RequestParams();
        String equipment_number = JinrApp.instance().g_imei;
        String password = passwd;
        String tel = mobile;
        String use_terminal = "android";
        // 转JSON
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("equipment_number", equipment_number);
        jsonObject.put("password", password);
        jsonObject.put("tel", tel);
        jsonObject.put("use_terminal", use_terminal);
        // 加密
        params.put("cipher", MyDES.encrypt(jsonObject.toString()));
        MyhttpClient.desPost(UrlConfig.USER_LOGIN, params, new AsyncHttpResponseHandler() {
            @Override
            public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
                super.onFailure(arg0, arg1, arg2, arg3);
                dismissWaittingDialog();
                ToastUtil.show(RegisterActivity.this, R.string.default_error);
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
                    MyLog.d("DES", "登录返回~~:" + desc);
                    if (TextUtils.isEmpty(response))
                        return;
                    BaseModel<UserInfo> result = new Gson().fromJson(desc, new TypeToken<BaseModel<UserInfo>>() {
                    }.getType());
                    if (result.isSuccess()) {
                        EventBus.getDefault().post(result.getData().getId(), EventBusKey.LOGIN_SUCCESS);
                        JinrApp.instance().state_bankBind = Integer.valueOf(result.getData().getBk());
                        PreferencesUtils.putIntToSPMap(RegisterActivity.this, PreferencesUtils.Keys.IS_BIND_CARD, Integer.valueOf(result.getData().getBk()));
                        PreferencesUtils.putValueToSPMap(RegisterActivity.this, PreferencesUtils.Keys.UID, result.getData().getId());
                        PreferencesUtils.putValueToSPMap(RegisterActivity.this, PreferencesUtils.Keys.NICKNAME, result.getData().getNikename());
                        PreferencesUtils.putValueToSPMap(RegisterActivity.this, PreferencesUtils.Keys.TEL, result.getData().getTel());
                        PreferencesUtils.putValueToSPMap(RegisterActivity.this, PreferencesUtils.Keys.NAME, result.getData().getName());
                        PreferencesUtils.putValueToSPMap(RegisterActivity.this, PreferencesUtils.Keys.BUSS_PWD, result.getData().getBuss_pwd());
                        PreferencesUtils.putValueToSPMap(RegisterActivity.this, PreferencesUtils.Keys.ID_CARD, result.getData().getCard_id());
                        PreferencesUtils.putBooleanToSPMap(RegisterActivity.this, PreferencesUtils.Keys.IS_LOGIN, true);
                        if (!result.getData().getId().equals(PreferencesUtils.getSValueFromSPMap(RegisterActivity.this, PreferencesUtils.Keys.UID))) {
                            PreferencesUtils.clearShPMap(RegisterActivity.this);
                            PreferencesUtils.putSValueToSPMap(RegisterActivity.this, PreferencesUtils.Keys.UID, result.getData().getId());
                        }
                        // 根据网络获取 判断 是否实名 有无支付密码
                        if (!"".equals(PreferencesUtils.getValueFromSPMap(RegisterActivity.this, PreferencesUtils.Keys.NAME))
                                && !"".equals(PreferencesUtils.getValueFromSPMap(RegisterActivity.this, PreferencesUtils.Keys.ID_CARD))) {// 实名
                            PreferencesUtils.putBooleanToSPMap(RegisterActivity.this, PreferencesUtils.Keys.IS_IDENTIFY, true);
                        }
                        if (!"".equals(PreferencesUtils.getValueFromSPMap(RegisterActivity.this, PreferencesUtils.Keys.BUSS_PWD))) {// 交易密码
                            PreferencesUtils.putBooleanToSPMap(RegisterActivity.this, PreferencesUtils.Keys.HAS_DEAL_PASSWD, true);
                        }
                        user_id = PreferencesUtils.getValueFromSPMap(RegisterActivity.this, PreferencesUtils.Keys.UID);
                        PushAgent mPushAgent = PushAgent.getInstance(RegisterActivity.this);
                        mPushAgent.addAlias(user_id, ALIAS_TYPE.SINA_WEIBO, new UTrack.ICallBack() {
                            @Override
                            public void onMessage(boolean isSuccess, String message) {

                            }
                        });
                        isSetLock();
                    } else {
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

    /**
     * 发送验证码
     */
    public void sendCode() {
        SendMobileCode.getInstance().send_code(send_code, RegisterActivity.this, mobile, MessageType.MESSAGE_MOBILE_ZCXX, null, new Back() {
            @Override
            public void sms(String result) {
                if (result != null && "".equals(result) != true) {
                    send_code.setClickable(true);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            // 使用postDelayed方式修改UI组件tvMessage的Text属性值
                            // 并且延迟3S执行
                            handlers.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    tv_zc_num.setText(mobile.toString().substring(0, 3) + "*****" + mobile.toString().substring(7, 11));
                                    tv_zc_layout.setVisibility(View.VISIBLE);
                                }
                            }, 2000);
                        }
                    }).start();
                }
            }
        });
    }

    /**
     * 监听输入框的状态，初始化ui按钮
     */
    private class editTextListen implements TextWatcher {

        @Override
        public void onTextChanged(CharSequence s, int arg1, int arg2, int arg3) {
            String b = code_et.getText().toString().trim();
            String c = passwd_et.getText().toString().trim();
            if (!b.equals("") && !c.equals("")) {
                register.setClickable(true);
                register.setBackgroundResource(R.drawable.btn_blue_bg);
            } else {
                if (register.isClickable() == true) {
                    register.setClickable(false);
                    register.setBackgroundResource(R.drawable.btn_blue_light_bg);
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


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {// TODO
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
                Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        }
        return true;
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
            errorDialog = new ErrorDialog(RegisterActivity.this, R.string.warning, R.string.dialog_set_hint, R.string.dialog_set);
            errorDialog.dialog_retry.setOnClickListener(new View.OnClickListener() {// 设置手势

                @Override
                public void onClick(View v) {
                    errorDialog.dismiss();
                    PreferencesUtils.putBooleanToSPMap(RegisterActivity.this, PreferencesUtils.Keys.IS_SETTING_CG, true);
                    Intent intent_CreateGesturePasswordActivity = new Intent(RegisterActivity.this, CreateGesturePasswordActivity.class);
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
                            intentMainActivity = new Intent(RegisterActivity.this, MainActivity.class);
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
                Intent intentMainActivity = new Intent(RegisterActivity.this, MainActivity.class);
                startActivity(intentMainActivity);
            }
            sendBroadcast(new Intent().setAction("action.refresh_entrance"));
            finish();
        }
    }
}
