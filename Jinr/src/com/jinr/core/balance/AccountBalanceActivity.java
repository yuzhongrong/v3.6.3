package com.jinr.core.balance;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.jinr.core.JinrApp;
import com.jinr.core.R;
import com.jinr.core.bankCard.AddBankActivity;
import com.jinr.core.bankCard.SecondBandCardActivity;
import com.jinr.core.base.BaseActivity;
import com.jinr.core.config.EventBusKey;
import com.jinr.core.config.UrlConfig;
import com.jinr.core.more.CommonWebActivity;
import com.jinr.core.trade.record.NewTradeRecordActivity;
import com.jinr.core.ui.NewCustomDialogNoTitleFinish;
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
import org.simple.eventbus.Subscriber;

/**
 * 余额
 *
 * @author 966
 */
public class AccountBalanceActivity extends BaseActivity implements OnClickListener {
    private TextView title_center_text;
    private ImageView title_left_img;
    private TextView tv_protocol;
    private Button btn_balance_in;
    private Button btn_balance_out;
    private TextView tv_balance_explain;
    private TextView tv_bal_account;//余额资产
    private String product_code;
    private NewCustomDialogNoTitleFinish dialog_bind_card;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //EventBus.getDefault().register(this);
        initUI();
        findViewById();
        setListener();
        initData();
    }

    @Override
    protected void initData() {
        String bal_money = getIntent().getStringExtra("money");//余额资产
        tv_bal_account.setText(bal_money + "");
    }

    @Override
    protected void findViewById() {
        title_center_text = (TextView) findViewById(R.id.title_center_text);
        tv_protocol = (TextView) findViewById(R.id.tv_protocol);
        tv_balance_explain = (TextView) findViewById(R.id.tv_balance_explain);
        tv_bal_account = (TextView) findViewById(R.id.tv_bal_account);
        title_left_img = (ImageView) findViewById(R.id.title_left_img);
        btn_balance_in = (Button) findViewById(R.id.btn_balance_in);
        btn_balance_out = (Button) findViewById(R.id.btn_balance_out);
        tv_protocol.setVisibility(View.VISIBLE);
        tv_protocol.setText("交易记录");
        title_center_text.setText("余额资产");
        tv_balance_explain.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
    }

    @Override
    protected void initUI() {
        setContentView(R.layout.activity_accountbalance);
        product_code = getIntent().getStringExtra("product_code");
    }

    @Override
    protected void setListener() {
        title_left_img.setOnClickListener(this);
        tv_protocol.setOnClickListener(this);
        tv_balance_explain.setOnClickListener(this);
        btn_balance_in.setOnClickListener(this);
        btn_balance_out.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_left_img:
                AccountBalanceActivity.this.finish();
                break;
            case R.id.tv_protocol:
                Intent intent = new Intent(AccountBalanceActivity.this, NewTradeRecordActivity.class);
                intent.putExtra("goods_type", "2");
                intent.putExtra("name", "余额");
                intent.putExtra("product_code", product_code);
                startActivity(intent);
                break;
            case R.id.tv_balance_explain:
                Intent intent_bal = new Intent(AccountBalanceActivity.this, CommonWebActivity.class);
                intent_bal.putExtra("url", UrlConfig.IP_R + UrlConfig.BALANCE_EXPLAIN);
                intent_bal.putExtra("titleName", "余额说明");
                startActivity(intent_bal);
                break;
            case R.id.btn_balance_in:
                startActivity(new Intent(AccountBalanceActivity.this, BalanceInActivity.class));
                break;
            case R.id.btn_balance_out:
                //未绑卡提示绑卡
                MyLog.e(TAG, "onClick: " + JinrApp.instance().state_bankBind);
                if (JinrApp.instance().state_bankBind == 0) {
                    dialog_bind_card = new NewCustomDialogNoTitleFinish(this, this.getResources().getString(R.string.bind_card_notice));
                    dialog_bind_card.btn_custom_dialog_sure.setText(this.getResources().getString(R.string.set_now_btn));
                    dialog_bind_card.btn_custom_dialog_sure.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog_bind_card.dismiss();
                            if (!PreferencesUtils.getValueFromSPMap(AccountBalanceActivity.this, PreferencesUtils.Keys.NAME).equals("")
                                    && !PreferencesUtils.getValueFromSPMap(AccountBalanceActivity.this, PreferencesUtils.Keys.ID_CARD).equals("")) {
                                Intent intent = new Intent(AccountBalanceActivity.this, SecondBandCardActivity.class);
                                startActivity(intent);
                            } else {
                                Intent intent = new Intent(AccountBalanceActivity.this, AddBankActivity.class);
                                startActivity(intent);
                            }

                        }
                    });
                    dialog_bind_card.show();
                } else {
                    try {
                        sendFormerInsertCity();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }


                break;
            default:
                break;
        }
    }

    @Subscriber(tag = EventBusKey.OUT_SUCCESS)
    public void killSelf(boolean flag) {
        if (flag) {
            AccountBalanceActivity.this.finish();
        }
    }

    /**
     * 获取用户城市是否为空
     */
    protected void sendFormerInsertCity() throws Exception {
        RequestParams params = new RequestParams();
        String uid = PreferencesUtils.getValueFromSPMap(AccountBalanceActivity.this, PreferencesUtils.Keys.UID);
        JSONObject cipher = new JSONObject();
        cipher.put(PreferencesUtils.Keys.UID, uid);
        params.put("cipher", MyDES.encrypt(cipher.toString()));
        MyhttpClient.desPost(UrlConfig.FORMER_INSERT_CITY, params, new AsyncHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                showWaittingDialog(AccountBalanceActivity.this);
            }

            @Override
            public void onFinish() {
                super.onFinish();
            }

            @Override
            public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
                super.onFailure(arg0, arg1, arg2, arg3);
                dismissWaittingDialog();
                ToastUtil.show(AccountBalanceActivity.this, R.string.default_error);
            }

            @Override
            public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
                super.onSuccess(arg0, arg1, arg2);
                dismissWaittingDialog();
                try {
                    String response = new String(arg2, "UTF-8");
                    response = CommonUtil.transResponse(response);
                    JSONObject obj = new JSONObject(response);
                    String cipher = obj.getString("cipher");
                    String desc = MyDES.decrypt(cipher);
                    JSONObject job = new JSONObject(desc);
                    if (job.getInt("code") == 1000) {
                        int data = job.getInt("data");
                        if (data == 1) {
                            Intent intent = new Intent(AccountBalanceActivity.this, BalanceOutActivity.class);
                            intent.putExtra("has_city", true);
                            AccountBalanceActivity.this.startActivity(intent);
                        } else if (data == 0) {
                            Intent intent = new Intent(AccountBalanceActivity.this, BalanceOutActivity.class);
                            intent.putExtra("has_city", false);
                            AccountBalanceActivity.this.startActivity(intent);
                        }
                    } else {
                        ToastUtil.show(AccountBalanceActivity.this, job.getString("msg"));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
