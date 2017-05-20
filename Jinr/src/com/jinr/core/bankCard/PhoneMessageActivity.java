package com.jinr.core.bankCard;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.jinr.core.JinrApp;
import com.jinr.core.R;
import com.jinr.core.bankCard.BankCardNoticeDialogFragment.BankCardNoticeDialogClick;
import com.jinr.core.bankCard.InputUserPhoneMessageDialogFragment.MessageOverListener;
import com.jinr.core.base.BaseActivity;
import com.jinr.core.config.EventBusKey;
import com.jinr.core.config.UrlConfig;
import com.jinr.core.security.tradepwd.SetTradePwdActivity;
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
import org.simple.eventbus.EventBus;

import java.io.UnsupportedEncodingException;

import model.BaseModel;
import model.DealPasswordMode;
import model.UidObj;
import model.UserBindinfo;

/**
 * Created by: Ysw on 2016/10/27.
 */

public class PhoneMessageActivity extends BaseActivity implements OnClickListener, MessageOverListener, BankCardNoticeDialogClick {

    private TextView title_center_text;
    private ImageView title_left_img;
    private TextView tv_phone;
    private String mPhoneNum;
    private String mToptext;
    private InputUserPhoneMessageDialogFragment mDialog;
    private PhoneMessageView phonemessageview;
    private LinearLayout ll_phonemessage;
    private RelativeLayout rl_next;
    private TextView tv_nogetmessage;
    private TextView tv_getagain;
    private String mRequestId;
    private String mZoneId;
    private String Uid;
    private String mOpenCity;
    private String userName;
    private String userNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_message);
        mPhoneNum = replaceAction(getIntent().getStringExtra("phone"));
        mRequestId = getIntent().getStringExtra("requestid");
        mZoneId = getIntent().getStringExtra("zone_id");
        mOpenCity = getIntent().getStringExtra("open_city");
        userName = getIntent().getStringExtra("userName");
        userNum = getIntent().getStringExtra("userNum");
        mToptext = "已向" + mPhoneNum + "发送短信";
        initUI();
        initData();
    }

    @Override
    protected void initUI() {
        Uid = PreferencesUtils.getValueFromSPMap(this, PreferencesUtils.Keys.UID);
        title_center_text = (TextView) findViewById(R.id.title_center_text);
        title_center_text.setText("添加银行卡");
        title_left_img = (ImageView) findViewById(R.id.title_left_img);
        title_left_img.setVisibility(View.INVISIBLE);
        //EventBus.getDefault().register(this);
        tv_phone = (TextView) findViewById(R.id.tv_phone);
        tv_getagain = (TextView) findViewById(R.id.tv_getagain);
        tv_nogetmessage = (TextView) findViewById(R.id.tv_nogetmessage);
        ll_phonemessage = (LinearLayout) findViewById(R.id.ll_phonemessage);
        phonemessageview = (PhoneMessageView) findViewById(R.id.phonemessageview);
        rl_next = (RelativeLayout) findViewById(R.id.rl_next);
        rl_next.setOnClickListener(this);
        tv_nogetmessage.setOnClickListener(this);
        rl_next.setClickable(false);
        ll_phonemessage.setOnClickListener(this);
        mDialog = InputUserPhoneMessageDialogFragment.getInstance(phonemessageview);
        mDialog.setOnMessageOverListener(this);
        SpannableStringBuilder builder = new SpannableStringBuilder(mToptext);
        ForegroundColorSpan blue = new ForegroundColorSpan(Color.parseColor("#0C72E3"));
        ForegroundColorSpan gray = new ForegroundColorSpan(Color.parseColor("#666666"));
        builder.setSpan(gray, 0, 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        builder.setSpan(blue, 2, 13, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        builder.setSpan(gray, 13, 17, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        tv_phone.setText(builder);
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(phonemessageview.getWindowToken(), 0);
        // 60秒倒计时定时器 @author Ysw created at 2017/1/9 10:01
        startTimers();
    }

    /**
     * 一进入界面就开始60秒倒计时 @author Ysw created at 2017/1/9 10:05
     */
    private void startTimers() {
        CountDownTimer cdt = new CountDownTimer(60000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                String one = "<font color='#1f7ee9'>" + millisUntilFinished / 1000 + "s" + "</font>" + "<font color='#b2b2b2'>后发送</font>";
                tv_getagain.setText(Html.fromHtml(one));
            }

            @Override
            public void onFinish() {
                tv_getagain.setText("重新发送");
                tv_getagain.setTextColor(PhoneMessageActivity.this.getResources().getColor(R.color.blue_color));
                tv_getagain.setOnClickListener(PhoneMessageActivity.this);
            }
        };
        cdt.start();
    }

    // 正则替换原则
    private static String replaceAction(String phoneNum) {
        return phoneNum.replaceAll("(?<=\\d{3})\\d(?=\\d{4})", "*");
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void findViewById() {

    }

    @Override
    protected void setListener() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_next:
                try {
                    checkMessageNetWork();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.ll_phonemessage:
                mDialog.show(getSupportFragmentManager(), "");
                break;
            case R.id.tv_nogetmessage:
                BankCardNoticeDialogFragment.getInstance(R.layout.phone_message_dialog, 3).setOnBankCardNoticeDialogClick(this).show(
                        getSupportFragmentManager(), "");
                break;
            case R.id.tv_getagain:
                tv_getagain.setOnClickListener(null);
                try {
                    getPhoneMessageAgain();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            default:
                break;
        }
    }

    /**
     * 点击重新获取验证码 @author Ysw created at 2017/1/9 9:49
     */
    private void getPhoneMessageAgain() throws Exception {
        RequestParams params = new RequestParams();
        JSONObject object = new JSONObject();
        object.put("requestid", mRequestId);
        object.put("uid", Uid);
        String desStr = MyDES.encrypt(object.toString());
        params.put("cipher", desStr);
        MyhttpClient.desPost(UrlConfig.YEEPAY_BANKCARD_SEND_SMS, params, new AsyncHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
            }

            @Override
            public void onFinish() {
                super.onFinish();
            }

            @Override
            public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
                super.onFailure(arg0, arg1, arg2, arg3);
                ToastUtil.show(PhoneMessageActivity.this, R.string.default_error);
            }

            @Override
            public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
                super.onSuccess(arg0, arg1, arg2);
                try {
                    String response = new String(arg2, "UTF-8");
                    response = CommonUtil.transResponse(response);
                    JSONObject jsb = new JSONObject(response);
                    String cipher = jsb.getString("cipher");
                    cipher = MyDES.decrypt(cipher);
                    BaseModel result = new Gson().fromJson(cipher, new TypeToken<BaseModel>() {
                    }.getType());
                    if (result.isSuccess()) {
                        startTimers();
                    } else {
                        ToastUtil.show(getApplication(), result.getMsg());
                    }
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (JsonSyntaxException e) {
                    MyLog.i("json解析错误", "解析错误");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 点击下一步检查验证码是否正确 @author Ysw created at 2017/1/9 9:27
     */
    private void checkMessageNetWork() throws Exception {
        RequestParams params = new RequestParams();
        JSONObject object = new JSONObject();
        object.put("requestid", mRequestId);
        object.put("zone_id", mZoneId);
        object.put("uid", Uid);
        object.put("code", phonemessageview.getText());
        String desStr = MyDES.encrypt(object.toString());
        params.put("cipher", desStr);
        MyhttpClient.desPost(UrlConfig.YEEPAY_BANKCARD_CHECK_SMS, params, new AsyncHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
            }

            @Override
            public void onFinish() {
                super.onFinish();
            }

            @Override
            public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
                super.onFailure(arg0, arg1, arg2, arg3);
                ToastUtil.show(PhoneMessageActivity.this, R.string.default_error);
            }

            @Override
            public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
                super.onSuccess(arg0, arg1, arg2);
                try {
                    String response = new String(arg2, "UTF-8");
                    response = CommonUtil.transResponse(response);
                    JSONObject jsb = new JSONObject(response);
                    String cipher = jsb.getString("cipher");
                    cipher = MyDES.decrypt(cipher);
                    BaseModel result = new Gson().fromJson(cipher, new TypeToken<BaseModel>() {
                    }.getType());
                    if (result.isSuccess()) {
                        getBindCardInfo();
                    } else {
                        ToastUtil.show(getApplication(), result.getMsg());
                    }
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (JsonSyntaxException e) {
                    MyLog.i("json解析错误", "解析错误");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @SuppressLint("NewApi")
    @Override
    public void onMessageOverListener(boolean isOver) {
        if (isOver) {
            rl_next.setClickable(true);
            rl_next.setOnClickListener(PhoneMessageActivity.this);
            rl_next.setBackground(getResources().getDrawable(R.drawable.blue_one));
        } else {
            rl_next.setClickable(false);
            rl_next.setOnClickListener(null);
            rl_next.setBackground(getResources().getDrawable(R.drawable.gray_one));
        }
    }

    @Override
    public void onCancelClick(int type) {
    }

    @Override
    public void onSureClick(int type) {
    }

    // 获取绑定卡的信息
    private void getBindCardInfo() throws Exception {
        RequestParams params = new RequestParams();
        UidObj obj = new UidObj(PreferencesUtils.getValueFromSPMap(PhoneMessageActivity.this, PreferencesUtils.Keys.UID));
        String desStr = MyDES.encrypt(new Gson().toJson(obj));
        params.put("cipher", desStr);
        MyhttpClient.desPost(UrlConfig.USER_BANKINFO, params, new AsyncHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                showWaittingDialog(PhoneMessageActivity.this);
            }

            @Override
            public void onFinish() {
                super.onFinish();
                dismissWaittingDialog();
            }

            @Override
            public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
                super.onFailure(arg0, arg1, arg2, arg3);
                ToastUtil.show(PhoneMessageActivity.this, R.string.default_error);
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
                        PreferencesUtils.putIntToSPMap(PhoneMessageActivity.this, PreferencesUtils.Keys.IS_BIND_CARD, Integer.valueOf(data.getState_bankBind()));
                        PreferencesUtils.putIntToSPMap(PhoneMessageActivity.this, PreferencesUtils.Keys.IS_SET_TRADE_PWD, Integer.valueOf(data.getState_tradePassword()));
                        PreferencesUtils.putValueToSPMap(PhoneMessageActivity.this, PreferencesUtils.Keys.BANK_BG_URL, data.getBgimg());
                        PreferencesUtils.putValueToSPMap(PhoneMessageActivity.this, PreferencesUtils.Keys.BANK_LOGO_URL, data.getImg());
                        PreferencesUtils.putValueToSPMap(PhoneMessageActivity.this, PreferencesUtils.Keys.BANK_CARD_ID, data.getId());
                        PreferencesUtils.putValueToSPMap(PhoneMessageActivity.this, PreferencesUtils.Keys.BANK_CARD_NO, data.getBank_no());
                        PreferencesUtils.putValueToSPMap(PhoneMessageActivity.this, PreferencesUtils.Keys.BANK_NAME, data.getBank_name());
                        PreferencesUtils.putValueToSPMap(PhoneMessageActivity.this, PreferencesUtils.Keys.USER_TEL, data.getUser_tel());
                        PreferencesUtils.putValueToSPMap(PhoneMessageActivity.this, PreferencesUtils.Keys.CARD_LAST, data.getCard_last());
                        EventBus.getDefault().post(true, EventBusKey.BANDCARD_SUCCESD);
                        ToastUtil.show(PhoneMessageActivity.this, "绑卡成功");
                        PreferencesUtils.putValueToSPMap(PhoneMessageActivity.this, PreferencesUtils.Keys.NAME, userName);
                        PreferencesUtils.putValueToSPMap(PhoneMessageActivity.this, PreferencesUtils.Keys.ID_CARD, userNum);
                        // 判断有无选择城市 @author Ysw created at 2017/1/16 17:45
                        if (mOpenCity != null && mOpenCity.equals("1")) {
                            // 判断是否设置交易密码 @author Ysw created at 2017/1/11 17:55
                            getDealPasswordNetwork();
                        } else {
                            Intent intent = new Intent();
                            intent.setClass(PhoneMessageActivity.this, ChooseCityActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    } else {
                        ToastUtil.show(PhoneMessageActivity.this, result.getMsg());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 查看用户是否设置交易密码 @author Ysw created at 2017/1/11 17:44
     */

    private void getDealPasswordNetwork() throws Exception {
        RequestParams params = new RequestParams();
        JSONObject object = new JSONObject();
        object.put("uid", Uid);
        String desStr = MyDES.encrypt(object.toString());
        params.put("cipher", desStr);
        MyhttpClient.desPost(UrlConfig.DEAL_PASSWORD, params, new AsyncHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
            }

            @Override
            public void onFinish() {
                super.onFinish();
            }

            @Override
            public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
                super.onFailure(arg0, arg1, arg2, arg3);
                finish();
            }

            @Override
            public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
                super.onSuccess(arg0, arg1, arg2);
                try {
                    String response = new String(arg2, "UTF-8");
                    response = CommonUtil.transResponse(response);
                    JSONObject jsb = new JSONObject(response);
                    String cipher = jsb.getString("cipher");
                    cipher = MyDES.decrypt(cipher);
                    Log.e("Ysw", "cipher = " + cipher);
                    BaseModel<DealPasswordMode> result = new Gson().fromJson(cipher, new TypeToken<BaseModel<DealPasswordMode>>() {
                    }.getType());
                    if (result.isSuccess()) {
                        String state = result.getData().getState_tradePassword();
                        if (state != null && state.equals("0")) {
                            Intent intent = new Intent();
                            intent.setClass(PhoneMessageActivity.this, SetTradePwdActivity.class);
                            startActivity(intent);
                        }
                        finish();
                    } else {
                        ToastUtil.show(getApplication(), result.getMsg());
                        finish();
                    }
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (JsonSyntaxException e) {
                    MyLog.i("json解析错误", "解析错误");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return false;
        } else if (keyCode == KeyEvent.KEYCODE_DEL) {
            return true;
        } else {
            return false;
        }
    }
}
