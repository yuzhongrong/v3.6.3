/*
 * SecuCenterActivity.java
 * @author Andrew Lee
 * 2014-10-23 上午10:23:45
 */
package com.jinr.core.security;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.jinr.core.JinrApp;
import com.jinr.core.MainActivity;
import com.jinr.core.R;
import com.jinr.core.bankCard.AddBankActivity;
import com.jinr.core.bankCard.SecondBandCardActivity;
import com.jinr.core.base.BaseActivity;
import com.jinr.core.config.Check;
import com.jinr.core.config.UrlConfig;
import com.jinr.core.security.lockpanttern.pattern.CreateGesturePasswordActivity;
import com.jinr.core.security.lockpanttern.view.LockPatternUtils;
import com.jinr.core.security.loginpwd.ChangeLoginPwd;
import com.jinr.core.security.tradepwd.ChgTradePwd;
import com.jinr.core.security.tradepwd.FindTradePwd;
import com.jinr.core.security.tradepwd.SetTradePwdActivity;
import com.jinr.core.ui.lockpantternDialog;
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

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import model.BaseModel;
import model.UserBindinfo;
import model.UserInfo;

/**
 * 安全中心
 *
 * @author CQJ
 * @description: 设置密码修改重置等，登陆交易
 */
public class SecurityCenterActivity extends BaseActivity implements OnClickListener {

    private ImageView title_left_img; // title左边图片
    private TextView title_center_text;// 标题
    private RelativeLayout login_passwd_rl;
    private RelativeLayout deal_passwd_rl;
    private RelativeLayout modify_lock_rl;
    private RelativeLayout find_trade_pwd_rl;
    private RelativeLayout bank_card_rl;
    private View trade_passwd_layout; // 修改、找回交易密码
    private int is_bind_card = -1; // 是否绑卡
    private int is_set_trade_pwd = -1; // 是否设置交易密码
    private TextView bank_note_tv; // 是否绑卡提示
    // 银行卡信息
    private ImageView iv_bank;// 银行卡图
    private TextView the_bank_tv;
    private TextView card_no_tv;
    private View bank_info_layout;

    private TextView bind_mobile_tv;
    private static ImageView lock_panttern_tv;

    private String bind_mobile = "";
    private String real_name = "";
    private String deal_passwd = "";
    private lockpantternDialog dialog;
    public static String user_id;
    private boolean lock_statue = false;
    private boolean send_statue = true;
    private LockPatternUtils lockp = new LockPatternUtils(MainActivity.instance);

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.secu_center);
        initData();
        findViewById();
        initUI();
        setListener();
    }

    public void onResume() {
        super.onResume();
        try {
            sendVerifyuser();
        } catch (Exception e) {
            e.printStackTrace();
        }
        initData();
        bind_mobile_tv.setText(bind_mobile);
        if (lockp.savedPatternExists()) {
            lock_panttern_tv.setImageResource(R.drawable.an_on);
            modify_lock_rl.setVisibility(View.VISIBLE);
        } else {
            lock_panttern_tv.setImageResource(R.drawable.an_off);
            modify_lock_rl.setVisibility(View.GONE);
        }
        setBankCard();
    }

    protected void initData() {
        bind_mobile = PreferencesUtils.getValueFromSPMap(MainActivity.instance, PreferencesUtils.Keys.TEL);
        if ("".equals(bind_mobile) != true) {
            Pattern p = Pattern.compile("(\\d{3})(\\d{4})(\\d{4})");
            Matcher m = p.matcher(bind_mobile);
            bind_mobile = m.replaceAll("$1****$3");
            MyLog.i("正则", bind_mobile);
        }
        real_name = PreferencesUtils.getValueFromSPMap(MainActivity.instance, PreferencesUtils.Keys.NAME);
        if ("".equals(real_name) == false) {
            if (real_name.length() == 2 || real_name.length() == 1) {
                real_name = real_name.substring(0, 1) + "*";
            } else {
                real_name = real_name.substring(0, 1) + "**";
            }
        } else {
            // 留空
            real_name = getResources().getString(R.string.un_name);
        }

        if (Check.has_deal_passwd(MainActivity.instance)) {
            deal_passwd = getResources().getString(R.string.change);
        } else {
            deal_passwd = getResources().getString(R.string.un_set);
        }
        user_id = PreferencesUtils.getValueFromSPMap(MainActivity.instance, PreferencesUtils.Keys.UID);
    }

    protected void findViewById() {
        title_left_img = (ImageView) findViewById(R.id.title_left_img);
        title_center_text = (TextView) findViewById(R.id.title_center_text);
        bind_mobile_tv = (TextView) findViewById(R.id.secu_center_tv1);
        lock_panttern_tv = (ImageView) findViewById(R.id.an_off_on);
        login_passwd_rl = (RelativeLayout) findViewById(R.id.secu_center_rl3);
        deal_passwd_rl = (RelativeLayout) findViewById(R.id.secu_center_rl4);
        modify_lock_rl = (RelativeLayout) findViewById(R.id.secu_center_rl6);
        trade_passwd_layout = findViewById(R.id.layout_trade_passwd);
        bank_info_layout = findViewById(R.id.layout_bank_info);
        bank_note_tv = (TextView) findViewById(R.id.tv_bank_note);
        find_trade_pwd_rl = (RelativeLayout) findViewById(R.id.layout_find_trade_pwd);
        bank_card_rl = (RelativeLayout) findViewById(R.id.secu_center_rl_bank);
        bank_card_rl.setOnClickListener(this);
        // 银行卡信息
        iv_bank = (ImageView) findViewById(R.id.iv_bank);
        the_bank_tv = (TextView) findViewById(R.id.tv_bank_name);
        card_no_tv = (TextView) findViewById(R.id.tv_bank_no);
    }

    protected void initUI() {
        title_center_text.setText("密码管理");
    }

    protected void setListener() {
        title_left_img.setOnClickListener(this);
        login_passwd_rl.setOnClickListener(this);
        deal_passwd_rl.setOnClickListener(this);
        lock_panttern_tv.setOnClickListener(this);
        modify_lock_rl.setOnClickListener(this);
        find_trade_pwd_rl.setOnClickListener(this);
        bank_info_layout.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_left_img:
                finish();
                break;

            case R.id.secu_center_rl3:
                clickChgLogingPwd();
                break;

            case R.id.secu_center_rl4:
                clickChgTrade();
                break;
            // 我的银行卡
            case R.id.layout_bank_info:
            case R.id.secu_center_rl_bank:
                if (is_bind_card == 0) {
                    if (!PreferencesUtils.getValueFromSPMap(SecurityCenterActivity.this, PreferencesUtils.Keys.NAME).equals("")
                            && !PreferencesUtils.getValueFromSPMap(SecurityCenterActivity.this, PreferencesUtils.Keys.ID_CARD).equals("")) {
                        Intent intent = new Intent(SecurityCenterActivity.this, SecondBandCardActivity.class);
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent(SecurityCenterActivity.this, AddBankActivity.class);
                        startActivity(intent);
                    }
                } else if (is_bind_card == 1) {
                    Intent intent = new Intent(SecurityCenterActivity.this, MyBankCardActivity.class);
                    startActivity(intent);
                }
                break;
            case R.id.an_off_on:
                lock_statue = true;
                changeLock();
                break;
            case R.id.secu_center_rl6:
                lock_statue = false;
                changeLock();
                break;

            case R.id.layout_find_trade_pwd:
                findTradePwd();
                break;

            default:
                break;
        }
    }

    private void changeLock() {
        dialog = new lockpantternDialog(SecurityCenterActivity.this, getString(R.string.lockmessage));
        dialog.btn_custom_dialog_ok.setText(getString(R.string.dialog_call_bt_ok));
        dialog.btn_custom_dialog_cancel.setText(getString(R.string.dialog_call_bt_cancel));
        dialog.btn_custom_dialog_ok.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (send_statue) {// send控制
                    send_statue = false;
                    try {
                        showWaittingDialog(SecurityCenterActivity.this);
                        send(PreferencesUtils.getValueFromSPMap(MainActivity.instance, PreferencesUtils.Keys.TEL),
                                dialog.dialog_password.getText().toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        dialog.btn_custom_dialog_cancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    protected void send(String mobile, String passwd) throws Exception {
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
                dismissWaittingDialog();
                super.onFailure(arg0, arg1, arg2, arg3);
                ToastUtil.show(MainActivity.instance, R.string.default_error);
                send_statue = true;
            }

            @SuppressWarnings("static-access")
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
                    MyLog.i("登陆返回", response);
                    BaseModel<UserInfo> result = new Gson().fromJson(desc, new TypeToken<BaseModel<UserInfo>>() {
                    }.getType());
                    if (result.isSuccess()) {
                        dialog.dismiss();
                        if (lockp.savedPatternExists() && lock_statue) {
                            lock_panttern_tv.setImageResource(R.drawable.an_off);
                            modify_lock_rl.setVisibility(View.GONE);
                            lockp.lockPattern_off(user_id);
                            lockp.clearLock();
                        } else {
                            lock_panttern_tv.setImageResource(R.drawable.an_on);
                            modify_lock_rl.setVisibility(View.VISIBLE);
                            lockp.lockPattern_on(user_id);
                            if (!lockp.savedLockPatternExists() || !lock_statue)
                                startActivity(new Intent(MainActivity.instance, CreateGesturePasswordActivity.class));// 跳转到设置手势密码
                        }
                    } else {
                        ToastUtil.show(SecurityCenterActivity.this, result.getMsg());
                    }
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (JsonSyntaxException e) {
                    MyLog.i("json解析错误", "解析错误");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                send_statue = true;
            }
        });
    }

    /**
     * 修改登录密码
     */
    private void clickChgLogingPwd() {
        Intent intent_login_passwd = new Intent(MainActivity.instance, ChangeLoginPwd.class);
        startActivity(intent_login_passwd);
    }

    /**
     * 修改交易密码
     */
    private void clickChgTrade() {
        if (is_set_trade_pwd == 0) {
            Intent intent = new Intent(MainActivity.instance, SetTradePwdActivity.class);
            startActivity(intent);
        } else if (is_set_trade_pwd == 1) {
            Intent intent_real_info = new Intent(MainActivity.instance, ChgTradePwd.class);
            startActivity(intent_real_info);
        }
    }

    /**
     * 找回交易密码 已设置交易密码跳转至找回交易密码，未设置交易密码跳转至设置交易密码
     */
    private void findTradePwd() {
        if (is_set_trade_pwd == 0) {
            Intent intent = new Intent(MainActivity.instance, SetTradePwdActivity.class);
            startActivity(intent);
        } else if (is_set_trade_pwd == 1) {
            Intent intent_real_info = new Intent(MainActivity.instance, FindTradePwd.class);
            startActivity(intent_real_info);
        }
    }

    /**
     * 我的银行卡
     */
    private void setBankCard() {
        switch (is_bind_card) {
            // 已绑卡
            case 1:
                // 显示修改密码和交易密码
                trade_passwd_layout.setVisibility(View.VISIBLE);
                break;
            default:
                break;
        }
    }

    /**
     * 获取用户是否绑定卡及是否设置交易密码
     *
     * @throws Exception
     */
    protected void sendVerifyuser() throws Exception {
        RequestParams params = new RequestParams();
        // 转JSON
        JSONObject jsonObject = new JSONObject();
        String uid = PreferencesUtils.getValueFromSPMap(SecurityCenterActivity.this, PreferencesUtils.Keys.UID);
        jsonObject.put(PreferencesUtils.Keys.UID, uid);
        // 加密
        params.put("cipher", MyDES.encrypt(jsonObject.toString()));
        MyhttpClient.desPost(UrlConfig.USER_BANKINFO, params, new AsyncHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                showWaittingDialog(SecurityCenterActivity.this);
            }

            @Override
            public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
                super.onFailure(arg0, arg1, arg2, arg3);
                dismissWaittingDialog();
                ToastUtil.show(SecurityCenterActivity.this, R.string.default_error);
                is_bind_card = PreferencesUtils.getIsBindCardFromSPMap(SecurityCenterActivity.this, PreferencesUtils.Keys.IS_BIND_CARD);
                is_set_trade_pwd = PreferencesUtils.getIsBindCardFromSPMap(SecurityCenterActivity.this, PreferencesUtils.Keys.IS_SET_TRADE_PWD);
                setBankCard(); // 我的银行卡
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
                    MyLog.i("用户绑卡、密码返回", response);
                    BaseModel<UserBindinfo> result = new Gson().fromJson(desc, new TypeToken<BaseModel<UserBindinfo>>() {
                    }.getType());
                    if (result.isSuccess()) {
                        UserBindinfo state = result.getData();
                        if (state == null) {
                            is_bind_card = PreferencesUtils.getIsBindCardFromSPMap(SecurityCenterActivity.this, PreferencesUtils.Keys.IS_BIND_CARD);
                            is_set_trade_pwd = PreferencesUtils.getIsBindCardFromSPMap(SecurityCenterActivity.this, PreferencesUtils.Keys.IS_SET_TRADE_PWD);
                            setBankCard(); // 我的银行卡
                            return;
                        }
                        is_bind_card = Integer.valueOf(state.getState_bankBind());
                        is_set_trade_pwd = Integer.valueOf(state.getState_tradePassword());
                        JinrApp.instance().state_bankBind = Integer.valueOf(state.getState_bankBind());
                        JinrApp.instance().state_tradePassword = Integer.valueOf(state.getState_tradePassword());
                        PreferencesUtils.putIntToSPMap(SecurityCenterActivity.this, PreferencesUtils.Keys.IS_BIND_CARD, Integer.valueOf(state.getState_bankBind()));
                        PreferencesUtils.putIntToSPMap(SecurityCenterActivity.this, PreferencesUtils.Keys.IS_SET_TRADE_PWD, Integer.valueOf(state.getState_tradePassword()));
                        setBankCard(); // 我的银行卡
                    } else {
                        ToastUtil.show(SecurityCenterActivity.this, result.getMsg());
                        is_bind_card = PreferencesUtils.getIsBindCardFromSPMap(SecurityCenterActivity.this, PreferencesUtils.Keys.IS_BIND_CARD);
                        is_set_trade_pwd = PreferencesUtils.getIsBindCardFromSPMap(SecurityCenterActivity.this, PreferencesUtils.Keys.IS_SET_TRADE_PWD);
                        setBankCard(); // 我的银行卡
                    }
                } catch (JsonSyntaxException e) {
                    MyLog.i("json解析错误", "解析错误");
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
