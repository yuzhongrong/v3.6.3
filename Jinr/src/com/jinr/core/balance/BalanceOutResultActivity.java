package com.jinr.core.balance;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.jinr.core.R;
import com.jinr.core.base.BaseActivity;
import com.jinr.core.config.EventBusKey;
import com.jinr.core.config.UrlConfig;
import com.jinr.core.utils.CommonUtil;
import com.jinr.core.utils.MyDES;
import com.jinr.core.utils.MyLog;
import com.jinr.core.utils.MyhttpClient;
import com.jinr.core.utils.ToastUtil;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONObject;
import org.simple.eventbus.EventBus;

import model.OrderResultObj;
import model.OrderResultObj.OrderResultData;

/**
 * 余额转出结果界面 @author Ysw created at 2017/3/14 13:08
 */
public class BalanceOutResultActivity extends BaseActivity implements OnClickListener {

    private TextView title_center_text;
    private ImageView title_left_img;
    private TextView tv_protocol;
    private TextView tv_create_time;
    private TextView tv_one_content;
    private TextView tv_two_content;
    private TextView tv_expect_time;
    private Button btn_complete;
    private ImageView iv_out_pro;
    private String entrance;
    private ImageView iv_success;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_balanceout_result);
        //EventBus.getDefault().register(this);
        findViewById();
        setListener();
        initData();
    }

    @Override
    protected void initData() {
        Intent intent = getIntent();
        String order_num = intent.getStringExtra("order_num");
        String buy_type = intent.getStringExtra("buy_type");
        String pay_type = intent.getStringExtra("pay_type");
        String uid = intent.getStringExtra("uid");
        entrance = intent.getStringExtra("entrance");
        if ("balance".equals(entrance)) {
            title_center_text.setText("余额转出详情");
        } else {
            title_center_text.setText("活期转出详情");
            btn_complete.setVisibility(View.GONE);
            iv_success.setVisibility(View.GONE);
        }
        try {
            getBalOutResult(uid, order_num, buy_type, pay_type);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void findViewById() {
        title_center_text = (TextView) findViewById(R.id.title_center_text);
        tv_protocol = (TextView) findViewById(R.id.tv_protocol);
        tv_create_time = (TextView) findViewById(R.id.tv_create_time);
        tv_one_content = (TextView) findViewById(R.id.tv_one_content);
        tv_two_content = (TextView) findViewById(R.id.tv_two_content);
        tv_expect_time = (TextView) findViewById(R.id.tv_expect_time);
        iv_success = (ImageView) findViewById(R.id.imageView1);
        title_left_img = (ImageView) findViewById(R.id.title_left_img);
        iv_out_pro = (ImageView) findViewById(R.id.iv_out_pro);
        btn_complete = (Button) findViewById(R.id.btn_complete);
    }

    @Override
    protected void initUI() {
    }

    @Override
    protected void setListener() {
        title_left_img.setOnClickListener(this);
        tv_protocol.setOnClickListener(this);
        btn_complete.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_left_img:
                BalanceOutResultActivity.this.finish();
                break;
            case R.id.btn_complete:
                BalanceOutResultActivity.this.finish();
                break;
            default:
                break;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        EventBus.getDefault().postSticky(true, EventBusKey.OUT_SUCCESS);
    }

    /**
     * 获取余额转状态接口请求 @author Ysw created at 2017/3/14 13:09
     */
    protected void getBalOutResult(String uid, String order_num, String buy_type, String pay_type) throws Exception {
        RequestParams params = new RequestParams();
        JSONObject cipher = new JSONObject();
        cipher.put("order_num", order_num);
        cipher.put("buy_type", buy_type);
        cipher.put("pay_type", pay_type);
        cipher.put("uid", uid);
        params.put("cipher", MyDES.encrypt(cipher.toString()));
        MyhttpClient.desPost(UrlConfig.ORDER_BAL_OUT_RESULT, params, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                super.onStart();
                showWaittingDialog(BalanceOutResultActivity.this);
            }

            @Override
            public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
                super.onFailure(arg0, arg1, arg2, arg3);
                ToastUtil.show(BalanceOutResultActivity.this, R.string.default_error);
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
                    JSONObject object = new JSONObject(cipher);
                    int code = object.getInt("code");
                    if (code == 1000) {
                        OrderResultObj orderObj = new Gson().fromJson(cipher, OrderResultObj.class);
                        if (orderObj != null) {
                            OrderResultData data = orderObj.data;
                            if (data != null) {
                                String status = data.status;
                                if ("1".equals(status)) {
                                    iv_out_pro.setImageDrawable(getResources().getDrawable(R.drawable.balance_out_pro1));
                                } else {
                                    iv_out_pro.setImageDrawable(getResources().getDrawable(R.drawable.balance_out_pro2));
                                    tv_two_content.setTextColor(getResources().getColor(R.color.blue_notice_bg));
                                    tv_expect_time.setTextColor(getResources().getColor(R.color.blue_notice_bg));
                                }
                                tv_create_time.setText(data.create_time);
                                tv_expect_time.setText(data.expect_time);
                                tv_one_content.setText(data.one_content);
                                tv_two_content.setText(data.two_content);
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFinish() {
                super.onFinish();
                dismissWaittingDialog();
            }
        });
    }
}
