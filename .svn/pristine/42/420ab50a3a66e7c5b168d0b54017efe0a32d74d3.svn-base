package com.jinr.core.trade.purchase;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.jinr.core.MainActivity;
import com.jinr.core.R;
import com.jinr.core.base.BaseActivity;
import com.jinr.core.config.UrlConfig;
import com.jinr.core.utils.CommonUtil;
import com.jinr.core.utils.MyDES;
import com.jinr.core.utils.MyhttpClient;
import com.jinr.core.utils.PreferencesUtils;
import com.jinr.core.utils.ToastUtil;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONObject;

import model.PaymentRegularResultObj;

public class PurchaseResultActivity extends BaseActivity implements OnClickListener {
    private ImageView title_left_img; // title左边图片
    private ImageView iv_state;//
    private TextView title_center_text; // title标题
    private TextView amount_tv; // 转入金额
    private TextView jy_time_tv; // 付款时间
    private TextView date_start_tv; // 开始计算收益时间
    private TextView tv_start;// 开始计算收益
    private TextView date_arrive_tv; // 收益到账
    private TextView tv_arrive;
    private Button finish_btn; // 完成
    private String uid;
    private int color_blue;
    private PaymentRegularResultObj payResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchase_result);
        initData();
        findViewById();
        initUI();
        setListener();
    }

    @Override
    protected void initData() {
        Intent intent = getIntent();
        if (intent != null) {
            payResult = (PaymentRegularResultObj) intent.getExtras().get("payResult");
        }
        color_blue = PurchaseResultActivity.this.getResources().getColor(R.color.blue_color);
    }

    @Override
    protected void findViewById() {
        title_left_img = (ImageView) findViewById(R.id.title_left_img);
        title_center_text = (TextView) findViewById(R.id.title_center_text);
        finish_btn = (Button) findViewById(R.id.btn_finish);
        amount_tv = (TextView) findViewById(R.id.tv_amount);
        jy_time_tv = (TextView) findViewById(R.id.tv_jy_time);
        date_start_tv = (TextView) findViewById(R.id.tv_date_start);
        date_arrive_tv = (TextView) findViewById(R.id.tv_date_arrive);
        tv_arrive = (TextView) findViewById(R.id.tv_arrive);
        iv_state = (ImageView) findViewById(R.id.iv_state);
        tv_start = (TextView) findViewById(R.id.tv_start);
    }

    @Override
    protected void initUI() {
        title_center_text.setText(R.string.turn_into_finally);
        title_left_img.setVisibility(View.GONE);
        uid = PreferencesUtils.getValueFromSPMap(this, PreferencesUtils.Keys.UID);
        try {
            if (null != (payResult)) {
                send();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void setListener() {
        finish_btn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_finish:
                Intent intent = new Intent();
                intent.setAction("action.refresh_data");
                sendBroadcast(intent);
                PreferencesUtils.Keys.BACK_TO_ACT = 0;
                PreferencesUtils.Keys.BACK_TO_GIFT = false;
                sendBroadcast(new Intent().setAction("action.refresh_actdetail"));
                finish();
                break;
            default:
                break;
        }
    }

    /**
     * 转入结果详情
     */
    private void send() throws Exception {
        showWaittingDialog(PurchaseResultActivity.this);
        JSONObject obj = new JSONObject();
        obj.put("uid", uid);
        obj.put("order_num", payResult.getOrder_num());
        obj.put("pay_type", payResult.getPay_type());
        obj.put("buy_type", payResult.getBuy_type());
        RequestParams params = new RequestParams();
        String desStr = MyDES.encrypt(obj.toString());
        params.put("cipher", desStr);
        MyhttpClient.desPost(UrlConfig.ORDER_BAL_OUT_RESULT, params, new AsyncHttpResponseHandler() {

            @Override
            public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
                super.onFailure(arg0, arg1, arg2, arg3);
                dismissWaittingDialog();
                ToastUtil.show(MainActivity.instance, R.string.default_error);

            }

            @Override
            public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
                super.onSuccess(arg0, arg1, arg2);
                dismissWaittingDialog();
                try {
                    String response = new String(arg2, "UTF-8");
                    response = CommonUtil.transResponse(response);
                    if (response == null || response.equals("")) {
                        return;
                    }
                    JSONObject obj = new JSONObject(response); // 解析
                    String cipher = obj.getString("cipher");
                    String desStr = MyDES.decrypt(cipher); // 解密
                    if (TextUtils.isEmpty(desStr))
                        return;
                    JSONObject jsonObject = new JSONObject(desStr);
                    if (jsonObject.getInt("code") == 1000) {
                        JSONObject jsonObj = jsonObject.getJSONObject("data");
                        String status = jsonObj.optString("status");
                        amount_tv.setText(jsonObj.optString("one_content"));
                        tv_start.setText(jsonObj.optString("two_content"));
                        tv_arrive.setText(jsonObj.optString("three_content"));
                        jy_time_tv.setText(jsonObj.optString("create_time"));
                        date_start_tv.setText(jsonObj.optString("expect_time"));
                        date_arrive_tv.setText(jsonObj.optString("toAccount_time"));
                        if (jsonObj.optString("line_num").equals("3")) {
                            if ("1".equals(status)) {
                                iv_state.setImageResource(R.drawable.ic_purchase_result);
                            } else if ("2".equals(status)) {
                                iv_state.setImageResource(R.drawable.ic_purchase_result2);
                                tv_start.setTextColor(color_blue);
                                date_start_tv.setTextColor(color_blue);
                            } else if ("3".equals(status)) {
                                iv_state.setImageResource(R.drawable.ic_purchase_result3);
                                tv_start.setTextColor(color_blue);
                                date_start_tv.setTextColor(color_blue);
                                date_arrive_tv.setTextColor(color_blue);
                                tv_arrive.setTextColor(color_blue);
                            }
                        } else if (jsonObj.optString("line_num").equals("2")) {
                            if ("2".equals(status)) {
                                iv_state.setImageResource(R.drawable.get_cash2);
                                tv_start.setTextColor(color_blue);
                                date_start_tv.setTextColor(color_blue);
                            } else if ("1".equals(status)) {
                                iv_state.setImageResource(R.drawable.get_cash);
                            }
                        }
                    } else {
                        ToastUtil.show(MainActivity.instance, R.string.default_error);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent intents = new Intent();
            intents.setAction("action.refresh_data");
            sendBroadcast(intents);
            PreferencesUtils.Keys.BACK_TO_ACT = 0;
            PreferencesUtils.Keys.BACK_TO_GIFT = false;
            sendBroadcast(new Intent().setAction("action.refresh_actdetail"));
            finish();
        }
        return false;
    }
}
