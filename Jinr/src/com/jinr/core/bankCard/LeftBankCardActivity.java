/*
 * SecuCenterActivity.java
 * @author Andrew Lee
 * 2014-10-23 上午10:23:45
 */
package com.jinr.core.bankCard;

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
import com.jinr.core.base.BaseActivity;
import com.jinr.core.config.Check;
import com.jinr.core.config.UrlConfig;
import com.jinr.new_mvp.ui.activity.NewLoginActivity;
import com.jinr.core.security.MyBankCardActivity;
import com.jinr.core.security.lockpanttern.view.LockPatternUtils;
import com.jinr.core.trade.record.NewTradeRecordActivity;
import com.jinr.core.utils.CommonUtil;
import com.jinr.core.utils.MyDES;
import com.jinr.core.utils.MyLog;
import com.jinr.core.utils.MyhttpClient;
import com.jinr.core.utils.PreferencesUtils;
import com.jinr.core.utils.ToastUtil;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.apache.http.Header;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import model.BaseModel;
import model.UserBindinfo;

/**
 * 侧滑栏 银行卡
 *
 * @author 757
 */
public class LeftBankCardActivity extends BaseActivity implements OnClickListener {

    private ImageView title_left_img; // title左边图片
    private TextView title_center_text;// 标题
    private RelativeLayout bank_card_rl;
    private RelativeLayout jc_layout;
    private int is_bind_card = -1; // 是否绑卡
    private int is_set_trade_pwd = -1; // 是否设置交易密码
    private TextView bank_note_tv; // 是否绑卡提示
    // 银行卡信息
    private TextView the_bank_tv;
    private TextView card_no_tv;
    private View bank_info_layout;
    private String bind_mobile = "";
    private String real_name = "";
    private String deal_passwd = "";
    public static String user_id;
    private TextView tv_set;
    private TextView tv_name;
    private TextView tv_hide;
    private TextView trade_record;
    private ImageView bank_bg_img;
    LockPatternUtils lockp = new LockPatternUtils(MainActivity.instance);

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.left_band_card);
        initData();
        findViewById();
        initUI();
        setListener();
    }

    public void onResume() {
        super.onResume();
        initData();
        // 绑完卡刷新数据
        if (JinrApp.instance().is_fresh) {
            JinrApp.instance().is_fresh = false;
            is_bind_card = PreferencesUtils.getIsBindCardFromSPMap(LeftBankCardActivity.this, PreferencesUtils.Keys.IS_BIND_CARD);
            setBankCard();
        }
    }

    protected void initData() {
        bind_mobile = PreferencesUtils.getValueFromSPMap(MainActivity.instance, PreferencesUtils.Keys.TEL);
        if ("".equals(bind_mobile) != true) {
            Pattern p = Pattern.compile("(\\d{3})(\\d{4})(\\d{4})");
            Matcher m = p.matcher(bind_mobile);
            bind_mobile = m.replaceAll("$1****$3");
            MyLog.i("正则", bind_mobile);
        } else {

        }

        real_name = PreferencesUtils.getValueFromSPMap(MainActivity.instance, PreferencesUtils.Keys.NAME);
        if ("".equals(real_name) == false) {
            if (real_name.length() == 2 || real_name.length() == 1) {
                real_name = real_name.substring(0, 1) + "*";
            } else {
                int length = real_name.length();
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
        // ((TextView) view.findViewById(R.id.tv_back_title)).setText("");
        title_left_img = (ImageView) findViewById(R.id.title_left_img);
        title_center_text = (TextView) findViewById(R.id.title_center_text);
        bank_info_layout = findViewById(R.id.layout_bank_info);
        bank_note_tv = (TextView) findViewById(R.id.tv_bank_note);
        tv_set = (TextView) findViewById(R.id.tv_set);
        bank_card_rl = (RelativeLayout) findViewById(R.id.secu_center_rl_bank);
        jc_layout = (RelativeLayout) findViewById(R.id.jc_layout);
        bank_card_rl.setOnClickListener(this);
        bank_bg_img = (ImageView) findViewById(R.id.bank_bg_img);
        // 银行卡信息
        the_bank_tv = (TextView) findViewById(R.id.tv_bank_name);
        card_no_tv = (TextView) findViewById(R.id.tv_bank_no);
        tv_name = (TextView) findViewById(R.id.tv_name);
        tv_hide = (TextView) findViewById(R.id.tv_hide);
        trade_record = (TextView) findViewById(R.id.trade_record);
    }

    protected void initUI() {
        title_center_text.setText("银行卡");
        String backgroundurl = PreferencesUtils.getValueFromSPMap(LeftBankCardActivity.this, PreferencesUtils.Keys.BANK_BG_URL);
        // 内存中有地址，尝试下载
        ImageLoader.getInstance().displayImage(backgroundurl, bank_bg_img);
        try {
            sendVerifyuser();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void setListener() {
        title_left_img.setOnClickListener(this);
        bank_info_layout.setOnClickListener(this);
        jc_layout.setOnClickListener(this);
        trade_record.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_left_img:
                finish();
                break;
            // 我的银行卡
            case R.id.layout_bank_info:

                break;

            case R.id.jc_layout:
                if (is_bind_card == 0) {
                    if (!PreferencesUtils.getValueFromSPMap(LeftBankCardActivity.this, PreferencesUtils.Keys.NAME).equals("")
                            && !PreferencesUtils.getValueFromSPMap(LeftBankCardActivity.this, PreferencesUtils.Keys.ID_CARD).equals("")) {
                        Intent intent = new Intent(LeftBankCardActivity.this, SecondBandCardActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Intent intent = new Intent(LeftBankCardActivity.this, AddBankActivity.class);
                        startActivity(intent);
                        finish();
                    }
                } else if (is_bind_card == 1) {
                    Intent intent = new Intent(LeftBankCardActivity.this, MyBankCardActivity.class);
                    startActivity(intent);
                }
                break;
            case R.id.trade_record:
                if (Check.is_login(LeftBankCardActivity.this)) {
                    startActivity(new Intent(LeftBankCardActivity.this, NewTradeRecordActivity.class));
                } else {
                    startActivity(new Intent(LeftBankCardActivity.this, NewLoginActivity.class));
                }
                break;
            default:
                break;
        }
    }

    /**
     * 我的银行卡
     */
    private void setBankCard() {
        switch (is_bind_card) {
            // 无是否绑卡数据
            case -1:
                bank_note_tv.setText("获取银行卡信息失败");
                bank_card_rl.setVisibility(View.INVISIBLE);
                jc_layout.setVisibility(View.VISIBLE);
                bank_card_rl.setClickable(false);
                tv_set.setText("获取银行卡信息失败");
                jc_layout.setClickable(false);
                break;
            // 未绑卡
            case 0:
                bank_note_tv.setText("您尚未绑定银行卡");
                bank_card_rl.setVisibility(View.GONE);
                // bank_card_rl.setClickable(true);
                bank_info_layout.setVisibility(View.GONE);
                jc_layout.setVisibility(View.VISIBLE);
                tv_set.setText("添加银行卡");
                break;
            // 已绑卡
            case 1:
                bank_info_layout.setVisibility(View.VISIBLE);
                bank_note_tv.setVisibility(View.GONE);
                bank_card_rl.setVisibility(View.VISIBLE);
                bank_card_rl.setClickable(false);
                tv_set.setText("解绑银行卡");
                // 显示修改密码和交易密码
                // 银行卡信息栏
                setBankInfo();
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
        JSONObject jsonObject = new JSONObject();
        String uid = PreferencesUtils.getValueFromSPMap(LeftBankCardActivity.this, PreferencesUtils.Keys.UID);
        jsonObject.put(PreferencesUtils.Keys.UID, uid);
        params.put("cipher", MyDES.encrypt(jsonObject.toString()));
        MyhttpClient.desPost(UrlConfig.USER_BANKINFO, params, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                super.onStart();
                showWaittingDialog(LeftBankCardActivity.this);
            }

            @Override
            public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
                super.onFailure(arg0, arg1, arg2, arg3);
                dismissWaittingDialog();
                ToastUtil.show(LeftBankCardActivity.this, R.string.default_error);
                is_bind_card = PreferencesUtils.getIsBindCardFromSPMap(LeftBankCardActivity.this, PreferencesUtils.Keys.IS_BIND_CARD);
                is_set_trade_pwd = PreferencesUtils.getIsBindCardFromSPMap(LeftBankCardActivity.this, PreferencesUtils.Keys.IS_SET_TRADE_PWD);
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
                            is_bind_card = PreferencesUtils.getIsBindCardFromSPMap(LeftBankCardActivity.this, PreferencesUtils.Keys.IS_BIND_CARD);
                            is_set_trade_pwd = PreferencesUtils.getIsBindCardFromSPMap(LeftBankCardActivity.this, PreferencesUtils.Keys.IS_SET_TRADE_PWD);
                            setBankCard(); // 我的银行卡
                            return;
                        }
                        is_bind_card = Integer.valueOf(state.getState_bankBind());
                        is_set_trade_pwd = Integer.valueOf(state.getState_tradePassword());
                        JinrApp.instance().state_bankBind = Integer.valueOf(state.getState_bankBind());
                        JinrApp.instance().state_tradePassword = Integer.valueOf(state.getState_tradePassword());
                        PreferencesUtils.putIntToSPMap(LeftBankCardActivity.this, PreferencesUtils.Keys.IS_BIND_CARD, Integer.valueOf(state.getState_bankBind()));
                        PreferencesUtils.putIntToSPMap(LeftBankCardActivity.this, PreferencesUtils.Keys.IS_BIND_CARD, Integer.valueOf(state.getState_tradePassword()));
                        setBankCard(); // 我的银行卡
                    } else {
                        ToastUtil.show(LeftBankCardActivity.this, result.getMsg());
                        is_bind_card = PreferencesUtils.getIsBindCardFromSPMap(LeftBankCardActivity.this, PreferencesUtils.Keys.IS_BIND_CARD);
                        is_set_trade_pwd = PreferencesUtils.getIsBindCardFromSPMap(LeftBankCardActivity.this, PreferencesUtils.Keys.IS_SET_TRADE_PWD);
                        setBankCard(); // 我的银行卡
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

    private void setBankInfo() {
        bank_info_layout.setVisibility(View.VISIBLE);
        jc_layout.setVisibility(View.VISIBLE);
        tv_name.setVisibility(View.VISIBLE);
        tv_hide.setVisibility(View.VISIBLE);
        String bank_name = PreferencesUtils.getValueFromSPMap(LeftBankCardActivity.this, PreferencesUtils.Keys.BANK_NAME);
        the_bank_tv.setText(bank_name);
        String bank_card_last = PreferencesUtils.getValueFromSPMap(LeftBankCardActivity.this, PreferencesUtils.Keys.CARD_LAST);
        card_no_tv.setText("" + bank_card_last);
    }
}
