package com.jinr.new_mvp.ui.activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.text.InputType;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jinr.core.JinrApp;
import com.jinr.core.MainActivity;
import com.jinr.core.R;
import com.jinr.core.config.EventBusKey;
import com.jinr.core.config.UrlConfig;
import com.jinr.core.regist.ForgetPasswordActivity;
import com.jinr.core.regist.MyRegistEditText;
import com.jinr.new_mvp.ui.activity.NewRegisterActivity;
import com.jinr.core.regist.SafeLoginActivity;
import com.jinr.core.regist.XEditText;
import com.jinr.core.security.lockpanttern.pattern.CreateGesturePasswordActivity;
import com.jinr.core.security.lockpanttern.view.LockPatternUtils;
import com.jinr.core.ui.ErrorDialog;
import com.jinr.core.ui.NewCustomDialogNoTitle;
import com.jinr.core.utils.DensityUtil;
import com.jinr.core.utils.MyDES;
import com.jinr.core.utils.MyLog;
import com.jinr.core.utils.MyhttpClient;
import com.jinr.core.utils.PreferencesUtils;
import com.jinr.core.utils.ToastUtil;
import com.jinr.new_mvp.presenter.LoginPresenter;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import org.apache.http.Header;
import org.json.JSONObject;
import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;
import java.io.UnsupportedEncodingException;
import model.BaseModel;
import model.UidObj;
import model.UserBindinfo;
public class NewLoginActivity extends BaseActivity<LoginPresenter> implements View.OnClickListener {
    private ImageView image_back;
    private RelativeLayout rl_regist;
    private XEditText et_phoneNum;
    private MyRegistEditText et_password;
    private TextView tv_look;
    private Button bt_login;
    private TextView tv_safe;
    private TextView tv_forget;
    // 判断密码是否可见 @author Ysw created at 2017/3/9 9:55
    private boolean isShow = true;
    // 检测登录按钮是否可点击的监听线程 @author Ysw created at 2017/3/9 11:14
    private Thread mThread;
    private Handler mHandler;
    private String user_id = "";
    private ErrorDialog errorDialog;
    private LockPatternUtils lockp = new LockPatternUtils(this);
    @Override
    protected void onResume() {
        super.onResume();
        try {
            nextClickableListener();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    protected void findViewById() {
        image_back = (ImageView) findViewById(R.id.image_back);
        rl_regist = (RelativeLayout) findViewById(R.id.rl_regist);
        et_phoneNum = (XEditText) findViewById(R.id.et_phoneNum);
        et_phoneNum.setSeparator(" ");
        et_phoneNum.setPattern(new int[]{3, 4, 4});
        et_password = (MyRegistEditText) findViewById(R.id.et_password);
        tv_look = (TextView) findViewById(R.id.tv_look);
        bt_login = (Button) findViewById(R.id.bt_login);
        tv_safe = (TextView) findViewById(R.id.tv_safe);
        tv_forget = (TextView) findViewById(R.id.tv_forget);
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 1:
                        bt_login.setOnClickListener(NewLoginActivity.this);
                        bt_login.setBackgroundResource(R.drawable.btn_blue_bg);
                        break;
                    case 2:
                        bt_login.setOnClickListener(null);
                        bt_login.setBackgroundResource(R.drawable.btn_blue_light_bg);
                        break;
                }
                super.handleMessage(msg);
            }
        };
        et_phoneNum.setTextToSeparate(PreferencesUtils.getLastTel(PreferencesUtils.Keys.TEL, ""));
        //EventBus.getDefault().register(this);
    }
    protected void setListener() {
        image_back.setOnClickListener(this);
        rl_regist.setOnClickListener(this);
        tv_look.setOnClickListener(this);
        tv_safe.setOnClickListener(this);
        tv_forget.setOnClickListener(this);
    }
    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        switch (v.getId()) {
            case R.id.image_back:
                finish();
                break;
            case R.id.rl_regist:
                intent.setClass(NewLoginActivity.this, NewRegisterActivity.class);
                startActivity(intent);
                break;
            case R.id.tv_look:
                isShow = !isShow;
                if (isShow) {
                    tv_look.setText("隐藏");
                    et_password.setInputType(InputType.TYPE_CLASS_TEXT);
                } else {
                    tv_look.setText("显示");
                    et_password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);// 隐藏密码
                }
                CharSequence text = et_password.getText();
                if (text != null) {
                    Spannable spanText = (Spannable) text;
                    Selection.setSelection(spanText, text.length());
                }
                break;
            case R.id.bt_login:
                prepaerLogin();
                break;
            case R.id.tv_safe:
                intent.setClass(NewLoginActivity.this, SafeLoginActivity.class);
                startActivity(intent);
                break;
            case R.id.tv_forget:
                intent.setClass(NewLoginActivity.this, ForgetPasswordActivity.class);
                startActivity(intent);
                break;
            default:
                break;

        }
    }
    /**
     * 准备登录前的判断 @author Ysw created at 2017/3/9 15:13
     */
    public void prepaerLogin() {
        if (!DensityUtil.isMobileNO(et_phoneNum.toString().trim())) {
            ToastUtil.show(this, "您输入的电话号码不正确");
            return;
        } else if (et_password.getText().toString().trim().length() < 6) {
            ToastUtil.show(this, "您输入的密码太短");
            return;
        } else if (et_password.getText().toString().trim().contains(" ")) {
            ToastUtil.show(this, "您输入的密码不能包含空格");
            return;
        } else {
            try {
                getP().login(et_phoneNum.toString().trim(),et_password.getText().toString().trim());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    // 获取绑定卡的信息
    private void getBindCardInfo() throws Exception {
        RequestParams params = new RequestParams();
        UidObj obj = new UidObj(PreferencesUtils.getValueFromSPMap(NewLoginActivity.this, PreferencesUtils.Keys.UID));
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
                ToastUtil.show(getApplication(), NewLoginActivity.this.getResources().getString(R.string.login_in_success));
                isSetLock();// 判断是否弹出图形解锁设置提示框
            }

            @Override
            public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
                super.onFailure(arg0, arg1, arg2, arg3);
                ToastUtil.show(NewLoginActivity.this, R.string.default_error);
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
                        PreferencesUtils.putIntToSPMap(NewLoginActivity.this, PreferencesUtils.Keys.IS_BIND_CARD, Integer.valueOf(data.getState_bankBind()));
                        PreferencesUtils.putIntToSPMap(NewLoginActivity.this, PreferencesUtils.Keys.IS_SET_TRADE_PWD, Integer.valueOf(data.getState_tradePassword()));
                        PreferencesUtils.putValueToSPMap(NewLoginActivity.this, PreferencesUtils.Keys.BANK_BG_URL, data.getBgimg());
                        PreferencesUtils.putValueToSPMap(NewLoginActivity.this, PreferencesUtils.Keys.BANK_LOGO_URL, data.getImg());
                        PreferencesUtils.putValueToSPMap(NewLoginActivity.this, PreferencesUtils.Keys.BANK_CARD_ID, data.getId());
                        PreferencesUtils.putValueToSPMap(NewLoginActivity.this, PreferencesUtils.Keys.BANK_CARD_NO, data.getBank_no());
                        PreferencesUtils.putValueToSPMap(NewLoginActivity.this, PreferencesUtils.Keys.BANK_NAME, data.getBank_name());
                        PreferencesUtils.putValueToSPMap(NewLoginActivity.this, PreferencesUtils.Keys.USER_TEL, data.getUser_tel());
                        PreferencesUtils.putValueToSPMap(NewLoginActivity.this, PreferencesUtils.Keys.CARD_LAST, data.getCard_last());
                    } else {
                        ToastUtil.show(NewLoginActivity.this, result.getMsg());
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
            errorDialog = new ErrorDialog(NewLoginActivity.this, R.string.warning, R.string.dialog_set_hint, R.string.dialog_set);
            errorDialog.dialog_cancle.setTextColor(getResources().getColor(R.color.color_text_gr));
            errorDialog.dialog_retry.setOnClickListener(new View.OnClickListener() {// 设置手势

                @Override
                public void onClick(View v) {
                    errorDialog.dismiss();
                    PreferencesUtils.putBooleanToSPMap(NewLoginActivity.this, PreferencesUtils.Keys.IS_SETTING_CG, true);
                    Intent intent_CreateGesturePasswordActivity = new Intent(NewLoginActivity.this, CreateGesturePasswordActivity.class);
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
                    Intent intentMainActivity;
                    if (!(str != null && str.equals("unlock_forget"))) { // 如果不是在忘记密码跳转到登录界面的情况下
                        if (PreferencesUtils.Keys.BACK_TO_ACT != 0 || PreferencesUtils.Keys.BACK_TO_ABOUT) {
                            PreferencesUtils.Keys.BACK_TO_ABOUT = false;
                            if (PreferencesUtils.Keys.BACK_TO_ACT != 0 || PreferencesUtils.Keys.BACK_TO_GIFT) {
                                PreferencesUtils.Keys.BACK_TO_ACT = 0;
                                PreferencesUtils.Keys.BACK_TO_GIFT = false;
                                sendBroadcast(new Intent().setAction("action.refresh_actdetail"));
                                EventBus.getDefault().post(user_id, EventBusKey.LOGIN_SUCCESS);
                            }
//                            if ("about".equals(getIntent().getStringExtra("about"))) {
//                                AppManager.getAppManager().finishAllActivity();
//                                intentMainActivity = new Intent(NewLoginActivity.this, MainActivity.class);
//                                MainActivity.isLock_longin = false;
//                                startActivity(intentMainActivity);
//                            }
                        } else {
//                            AppManager.getAppManager().finishAllActivity();
//                            intentMainActivity = new Intent(NewLoginActivity.this, MainActivity.class);
//                            intentMainActivity.putExtra("gotoMainActive", "gotoMainActive");
//                            MainActivity.isLock_longin = false;
//                            startActivity(intentMainActivity);
                        }
                    }
                    // FIXME
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
                Intent intentMainActivity = new Intent(NewLoginActivity.this, MainActivity.class);
                startActivity(intentMainActivity);
            }
            sendBroadcast(new Intent().setAction("action.refresh_entrance"));
            finish();
        }
    }
    /**
     * 监听登录按钮是否可点击 @author Ysw created at 2017/3/9 15:13
     */
    public void nextClickableListener() throws InterruptedException {
        mThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    int phone = et_phoneNum.toString().trim().length();
                    int password = et_password.getText().toString().trim().length();
                    if (phone > 0 && password > 5) {
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
        mThread.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mThread != null) {
            mThread.interrupt();
            mThread = null;
        }

    }

    //跳转到注册页面

    public void ToRegist(final String mobilephone){
        //跳转到注册界面
        NewCustomDialogNoTitle dialog = new NewCustomDialogNoTitle(this, "");
        dialog.dialog_message.setText(Html.fromHtml("该手机号未注册<br>" + "<font color='#0c72e3'>" + "现在注册送5000体验金" + "</font>"));
        dialog.btn_custom_dialog_sure.setText("马上注册");
        dialog.btn_custom_dialog_sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NewLoginActivity.this, NewRegisterActivity.class);
                intent.putExtra("mobile", mobilephone);
                startActivity(intent);
                finish();
            }
        });
        dialog.show();

    }

    /**点击忘记手势密码 到登录页面 登录成功后 清除手势
     * **
     * @param
     */
    public void ClearLock(){


        // 图形解锁忘记密码跳转到重设图形解锁密码界面
        Intent intent = getIntent();
        String str = intent.getStringExtra("unlock_forget");
        if (str != null && str.equals("unlock_forget")) { // 如果在忘记密码跳转到登录界面的情况下
            lockp.clear_lock_off_on();//清理手势开关
            new Thread() {
                public void run() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            lockp.clearLock();//删除手势文件
                        }
                    });
                }
            }.start();
            PreferencesUtils.clearSPMap(getApplicationContext());//清理所有Preferences保存数据
            Intent intent_return = new Intent();
            intent_return.putExtra("return", "T");
            setResult(RESULT_OK, intent_return);
        }




    }


    // 安全登录成功关闭页面
    @Subscriber(tag = EventBusKey.SAFE_LOGIN_SUCCESS)
    public void closeActivity(String data) {
        finish();
    }

    @Override
    public void initData(Bundle savedInstanceState) {

        findViewById();
        setListener();

    }

    @Override
    public int getLayoutId() {
        return  R.layout.activity_new_login;
    }

    @Override
    public LoginPresenter newP() {
        return new LoginPresenter();
    }
}
