package com.jinr.core.regist;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
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
import com.jinr.core.utils.DensityUtil;
import com.jinr.core.utils.MyDES;
import com.jinr.core.utils.MyEditText;
import com.jinr.core.utils.MyLog;
import com.jinr.core.utils.MyhttpClient;
import com.jinr.core.utils.PreferencesUtils;
import com.jinr.core.utils.ToastUtil;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.umeng.message.ALIAS_TYPE;
import com.umeng.message.PushAgent;
import com.umeng.message.UTrack;

import org.apache.http.Header;
import org.json.JSONObject;
import org.simple.eventbus.EventBus;

import java.io.UnsupportedEncodingException;

import model.BaseModel;
import model.UidObj;
import model.UserBindinfo;
import model.UserInfo;

public class SafeLoginActivity extends BaseActivity implements View.OnClickListener {

    private ImageView image_back;
    private RelativeLayout rl_regist;
    private XEditText et_phoneNum;
    private MyRegistEditText et_message;
    private Button bt_login;
    private TextView tv_nomassage;
    private TextView tv_sendmsg;
    private RoundProgressBar pb_round;
    private Thread mThread;
    private Thread mUiThread;
    private boolean isRunning = true;
    private int mSecond = 60;
    private Handler mHandler;
    private String mUid;
    private CannotReceiveTextDialog dialog;
    private ErrorDialog errorDialog;
    private LockPatternUtils lockp = new LockPatternUtils(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_safe_login);
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
        rl_regist = (RelativeLayout) findViewById(R.id.rl_regist);
        et_phoneNum = (XEditText) findViewById(R.id.et_phoneNum);
        et_phoneNum.setSeparator(" ");
        et_phoneNum.setPattern(new int[]{3, 4, 4});
        et_message = (MyRegistEditText) findViewById(R.id.et_message);
        tv_sendmsg = (TextView) findViewById(R.id.tv_sendmsg);
        bt_login = (Button) findViewById(R.id.bt_login);
        pb_round = (RoundProgressBar) findViewById(R.id.pb_round);
        tv_nomassage = (TextView) findViewById(R.id.tv_nomassage);
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 1:
                        bt_login.setOnClickListener(SafeLoginActivity.this);
                        bt_login.setBackgroundResource(R.drawable.btn_blue_bg);
                        break;
                    case 2:
                        bt_login.setOnClickListener(null);
                        bt_login.setBackgroundResource(R.drawable.btn_blue_light_bg);
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
        rl_regist.setOnClickListener(this);
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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.image_back:
                finish();
                break;
            case R.id.rl_regist:
                Intent intent = new Intent();
                intent.setClass(SafeLoginActivity.this, NewRegisterActivity.class);
                startActivity(intent);
                break;
            case R.id.tv_sendmsg:
                if (CommonUtil.isFastDoubleClick()) return;
                isRunning = true;
                sendMassage();
                break;
            case R.id.tv_nomassage:
                noMassage();
                break;
            case R.id.bt_login:
                prepaerLogin();
                break;
            default:
                break;
        }
    }

    /**
     * 收不到短信 @author Ysw created at 2017/3/9 15:36
     */
    private void noMassage() {
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
     * 准备获取手机短信验证码 @author Ysw created at 2017/3/9 15:21
     */
    private void sendMassage() {
        String phoneNum = et_phoneNum.toString().trim();
        if (TextUtils.isEmpty(phoneNum)) {
            ToastUtil.show(SafeLoginActivity.this, "请输入您的手机号码");
        } else if (!DensityUtil.isMobileNO(phoneNum)) {
            ToastUtil.show(SafeLoginActivity.this, "您输入的手机号码不正确");
        } else {
            try {
                getPhoneMessage(phoneNum);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 获取手机短信验证码 @author Ysw created at 2017/3/9 15:10
     */
    private void getPhoneMessage(String phoneNum) throws Exception {
        RequestParams params = new RequestParams();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("tel", phoneNum);
        jsonObject.put("kw", MessageType.MOBILE_LOGIN);
        params.put("cipher", MyDES.encrypt(jsonObject.toString()));
        MyhttpClient.desPost(UrlConfig.SMS_SENDSMS, params, new AsyncHttpResponseHandler() {
            @Override
            public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
                super.onFailure(arg0, arg1, arg2, arg3);
                dismissWaittingDialog();
                ToastUtil.show(SafeLoginActivity.this, R.string.default_error);
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
     * 准备登录 @author Ysw created at 2017/3/9 15:28
     */
    public void prepaerLogin() {
        String phoneNum = et_phoneNum.toString().trim();
        String phoneMessage = et_message.getText().toString().trim();
        if (TextUtils.isEmpty(phoneNum)) {
            ToastUtil.show(SafeLoginActivity.this, "请输入您的手机号码");
        } else if (!DensityUtil.isMobileNO(phoneNum)) {
            ToastUtil.show(SafeLoginActivity.this, "您输入的手机号码不正确");
        } else if (TextUtils.isEmpty(phoneMessage)) {
            ToastUtil.show(SafeLoginActivity.this, "请输入您的短信验证码");
        } else {
            try {
                login(phoneNum, phoneMessage);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 进行短信验证码登录 @author Ysw created at 2017/3/9 15:34
     */
    private void login(String phoneNum, String phoneMassage) throws Exception {
        RequestParams params = new RequestParams();
        String equipment_number = JinrApp.instance().g_imei;
        String use_terminal = "android";
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("equipment_number", equipment_number);
        jsonObject.put("checksms", phoneMassage);
        jsonObject.put("tel", phoneNum);
        jsonObject.put("use_terminal", use_terminal);
        params.put("cipher", MyDES.encrypt(jsonObject.toString()));
        MyhttpClient.desPost(UrlConfig.VERIFY_CODE_LOGIN, params, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                super.onStart();
                showWaittingDialog(SafeLoginActivity.this);
            }

            @Override
            public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
                super.onFailure(arg0, arg1, arg2, arg3);
                dismissWaittingDialog();
                ToastUtil.show(SafeLoginActivity.this, R.string.default_error);
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
                        EventBus.getDefault().post("", EventBusKey.SAFE_LOGIN_SUCCESS);
                        JinrApp.instance().state_bankBind = Integer.valueOf(result.getData().getBk());
                        PreferencesUtils.putIntToSPMap(SafeLoginActivity.this, PreferencesUtils.Keys.IS_BIND_CARD, Integer.valueOf(result.getData().getBk()));
                        PreferencesUtils.putValueToSPMap(SafeLoginActivity.this, PreferencesUtils.Keys.UID, result.getData().getId());
                        PreferencesUtils.putValueToSPMap(SafeLoginActivity.this, PreferencesUtils.Keys.NICKNAME, result.getData().getNikename());
                        PreferencesUtils.putValueToSPMap(SafeLoginActivity.this, PreferencesUtils.Keys.TEL, result.getData().getTel());
                        PreferencesUtils.putValueToSPMap(SafeLoginActivity.this, PreferencesUtils.Keys.NAME, result.getData().getName());
                        PreferencesUtils.putValueToSPMap(SafeLoginActivity.this, PreferencesUtils.Keys.BUSS_PWD, result.getData().getBuss_pwd());
                        PreferencesUtils.putValueToSPMap(SafeLoginActivity.this, PreferencesUtils.Keys.ID_CARD, result.getData().getCard_id());
                        PreferencesUtils.putBooleanToSPMap(SafeLoginActivity.this, PreferencesUtils.Keys.IS_LOGIN, true);
                        if (!result.getData().getId().equals(PreferencesUtils.getSValueFromSPMap(SafeLoginActivity.this, PreferencesUtils.Keys.UID))) {
                            PreferencesUtils.clearShPMap(SafeLoginActivity.this);
                            PreferencesUtils.putSValueToSPMap(SafeLoginActivity.this, PreferencesUtils.Keys.UID, result.getData().getId());
                        }
                        // 根据网络获取 判断 是否实名 有无支付密码
                        if (!"".equals(PreferencesUtils.getValueFromSPMap(SafeLoginActivity.this, PreferencesUtils.Keys.NAME)) &&
                                !"".equals(PreferencesUtils.getValueFromSPMap(SafeLoginActivity.this, PreferencesUtils.Keys.ID_CARD))) {// 实名
                            PreferencesUtils.putBooleanToSPMap(SafeLoginActivity.this, PreferencesUtils.Keys.IS_IDENTIFY, true);
                        }
                        if (!"".equals(PreferencesUtils.getValueFromSPMap(SafeLoginActivity.this, PreferencesUtils.Keys.BUSS_PWD))) {// 交易密码
                            PreferencesUtils.putBooleanToSPMap(SafeLoginActivity.this, PreferencesUtils.Keys.HAS_DEAL_PASSWD, true);
                        }
                        mUid = PreferencesUtils.getValueFromSPMap(SafeLoginActivity.this, PreferencesUtils.Keys.UID);
                        PushAgent mPushAgent = PushAgent.getInstance(SafeLoginActivity.this);
                        mPushAgent.addAlias(mUid, ALIAS_TYPE.SINA_WEIBO, new UTrack.ICallBack() {
                            @Override
                            public void onMessage(boolean isSuccess, String message) {
                            }
                        });
                        getBindCardInfo();
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
     * 监听登录按钮是否可以点击 @author Ysw created at 2017/3/9 16:20
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

    // 获取绑定卡的信息
    private void getBindCardInfo() throws Exception {
        RequestParams params = new RequestParams();
        UidObj obj = new UidObj(PreferencesUtils.getValueFromSPMap(SafeLoginActivity.this, PreferencesUtils.Keys.UID));
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
                ToastUtil.show(getApplication(), SafeLoginActivity.this.getResources().getString(
                        R.string.login_in_success));
                isSetLock();// 判断是否弹出图形解锁设置提示框
            }

            @Override
            public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
                super.onFailure(arg0, arg1, arg2, arg3);
                ToastUtil.show(SafeLoginActivity.this, R.string.default_error);
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
                    if (TextUtils.isEmpty(desc))
                        return;
                    BaseModel<UserBindinfo> result = new Gson().fromJson(desc, new TypeToken<BaseModel<UserBindinfo>>() {
                    }.getType());
                    if (result.isSuccess()) {
                        UserBindinfo data = result.getData();
                        if (data == null)
                            return;
                        JinrApp.instance().state_bankBind = Integer.valueOf(data.getState_bankBind());
                        JinrApp.instance().state_tradePassword = Integer.valueOf(data.getState_tradePassword());
                        PreferencesUtils.putIntToSPMap(SafeLoginActivity.this, PreferencesUtils.Keys.IS_BIND_CARD, Integer.valueOf(data.getState_bankBind()));
                        PreferencesUtils.putIntToSPMap(SafeLoginActivity.this, PreferencesUtils.Keys.IS_SET_TRADE_PWD, Integer.valueOf(data.getState_tradePassword()));
                        PreferencesUtils.putValueToSPMap(SafeLoginActivity.this, PreferencesUtils.Keys.BANK_BG_URL, data.getBgimg());
                        PreferencesUtils.putValueToSPMap(SafeLoginActivity.this, PreferencesUtils.Keys.BANK_LOGO_URL, data.getImg());
                        PreferencesUtils.putValueToSPMap(SafeLoginActivity.this, PreferencesUtils.Keys.BANK_CARD_ID, data.getId());
                        PreferencesUtils.putValueToSPMap(SafeLoginActivity.this, PreferencesUtils.Keys.BANK_CARD_NO, data.getBank_no());
                        PreferencesUtils.putValueToSPMap(SafeLoginActivity.this, PreferencesUtils.Keys.BANK_NAME, data.getBank_name());
                        PreferencesUtils.putValueToSPMap(SafeLoginActivity.this, PreferencesUtils.Keys.USER_TEL, data.getUser_tel());
                        PreferencesUtils.putValueToSPMap(SafeLoginActivity.this, PreferencesUtils.Keys.CARD_LAST, data.getCard_last());
                    } else {
                        ToastUtil.show(SafeLoginActivity.this, result.getMsg());
                    }
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 在用户登录进入主页面前，先弹出弹窗询问是否需要设置密码，所以将sendBank()放在了这个函数中。 by fym
     */
    public void isSetLock() {
        mUid = PreferencesUtils.getValueFromSPMap(this, PreferencesUtils.Keys.UID);
        MainActivity.isLock = true;
        MyLog.i("isSetLock", mUid);
        if (!mUid.equals(lockp.userID()) || !lockp.savedPatternExists()) {
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
            errorDialog = new ErrorDialog(SafeLoginActivity.this, R.string.warning, R.string.dialog_set_hint, R.string.dialog_set);
            errorDialog.dialog_retry.setOnClickListener(new View.OnClickListener() {// 设置手势

                @Override
                public void onClick(View v) {
                    errorDialog.dismiss();
                    PreferencesUtils.putBooleanToSPMap(SafeLoginActivity.this, PreferencesUtils.Keys.IS_SETTING_CG, true);
                    Intent intent_CreateGesturePasswordActivity = new Intent(SafeLoginActivity.this, CreateGesturePasswordActivity.class);
                    // //如果不是在忘记密码跳转到登录界面的情况下
                    if (PreferencesUtils.Keys.BACK_TO_ACT != 0 || PreferencesUtils.Keys.BACK_TO_GIFT || PreferencesUtils.Keys.BACK_TO_ABOUT) {
                        // PreferencesUtils.Keys.BACK_TO_ACT = 0;
                        // PreferencesUtils.Keys.BACK_TO_GIFT = false;
                        EventBus.getDefault().post(mUid, EventBusKey.LOGIN_SUCCESS);
                    } else {
                        intent_CreateGesturePasswordActivity.putExtra("gotoMainActive", "gotoMainActive");
                        MainActivity.isLock_longin = false;
                    }
                    startActivity(intent_CreateGesturePasswordActivity);
                    sendBroadcast(new Intent().setAction("action.refresh_entrance"));
                    finish();
                }
            });
            errorDialog.dialog_cancle.setTextColor(getResources().getColor(R.color.color_text_gr));
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
                    lockp.lockPattern_off(mUid);
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
                                EventBus.getDefault().post(mUid, EventBusKey.LOGIN_SUCCESS);
                            }
                        } else {
                            intentMainActivity = new Intent(SafeLoginActivity.this, MainActivity.class);
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
                EventBus.getDefault().post(mUid, EventBusKey.LOGIN_SUCCESS);
            } else {
                Intent intentMainActivity = new Intent(SafeLoginActivity.this, MainActivity.class);
                startActivity(intentMainActivity);
            }
            sendBroadcast(new Intent().setAction("action.refresh_entrance"));
            finish();
        }
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
