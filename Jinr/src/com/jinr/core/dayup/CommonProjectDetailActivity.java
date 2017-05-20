package com.jinr.core.dayup;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.SslErrorHandler;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jinr.core.JinrApp;
import com.jinr.core.MainActivity;
import com.jinr.core.R;
import com.jinr.core.base.BaseActivity;
import com.jinr.core.config.Check;
import com.jinr.core.config.UrlConfig;
import com.jinr.core.regist.NewLoginActivity;
import com.jinr.core.regular.ProductInstructionsActivity;
import com.jinr.core.trade.purchase.CurrentPurchaseFirstActivity;
import com.jinr.core.utils.CommonUtil;
import com.jinr.core.utils.LoadingDialog;
import com.jinr.core.utils.MyDES;
import com.jinr.core.utils.MyLog;
import com.jinr.core.utils.MyhttpClient;
import com.jinr.core.utils.PreferencesUtils;
import com.jinr.core.utils.TimeUtil;
import com.jinr.core.utils.ToastUtil;
import com.jinr.graph_view.ProgressWebView;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONObject;
import org.simple.eventbus.EventBus;

import java.util.ArrayList;

import model.BaseModel;
import model.DayUpLimitList;
import model.ProductCommonModel;
import model.ProductLimit;
import model.UidObj;
import model.UserBindinfo;

/**
 * 活期/定期/日增息详情界面
 *
 * @author 1154
 */
public class CommonProjectDetailActivity extends BaseActivity implements OnClickListener {
    private ImageView title_left_img; // title左边
    private TextView title_center_text; // title标题
    private RelativeLayout rl_transfer;
    private String mUid;
    private String assetid;
    private String eventKey;
    private String mTitleName = "";
    private String type;
    private String arrow;
    private String goodType;
    private String mWebViewUrl;
    private String mUrl;
    private LoadingDialog loadingdialog;
    public BaseModel<DayUpLimitList> dayUpLimit;
    private TextView tv_turnIn;
    private ProgressWebView mWebView;
    private TextView tv_protocol;
    private String mProduct_code;
    private boolean isArrive;
    private int mTimes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_common_product_detail);
        //EventBus.getDefault().register(this);
        loadingdialog = new LoadingDialog(this);
        assetid = getIntent().getStringExtra("assetid");
        eventKey = getIntent().getStringExtra("event_key");
        mTitleName = getIntent().getStringExtra("name");
        goodType = getIntent().getStringExtra("good_type");
        MyLog.e(TAG, "onCreate: goodType=" + goodType);
        initData();
        findViewById();
        setListener();
        initUI();
        registerReceiver();
    }

    private void registerReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("action.refresh_actdetail");
        registerReceiver(mRefreshBroadcastReceiver, intentFilter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_left_img:
                finish();
                break;
            case R.id.tv_protocol:
                Intent intent = new Intent();
                intent.putExtra("mWebViewUrl", mWebViewUrl);
                intent.setClass(CommonProjectDetailActivity.this, ProductInstructionsActivity.class);
                startActivity(intent);
                break;
            case R.id.rl_transfer:
                if (CommonUtil.isFastDoubleClick()) {
                    return;
                } else {
                    if (goodType == null || goodType.equals("") || goodType.equals("2015") || dayUpLimit == null) {
                        if (!Check.is_login(CommonProjectDetailActivity.this)) {
                            Intent intent_login = new Intent(CommonProjectDetailActivity.this, NewLoginActivity.class);
                            startActivity(intent_login);
                            return;
                        } else {
                            try {
                                getBindCardInfo();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        return;
                    }
                    for (int i = 0; i < dayUpLimit.getData().getList().size(); i++) {
                        if (goodType.equals(dayUpLimit.getData().getList().get(i).getGoods_type())) {
                            if (dayUpLimit.getData().getList().get(i).getStart_time() == 0) {
                                if (!Check.is_login(CommonProjectDetailActivity.this)) {
                                    Intent intent_login = new Intent(CommonProjectDetailActivity.this, NewLoginActivity.class);
                                    startActivity(intent_login);
                                    return;
                                } else {
                                    try {
                                        getBindCardInfo();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                                return;
                            }
                            if (TimeUtil.getDiffTime(dayUpLimit.getData().getList().get(i).getStart_time(), dayUpLimit.getData().getCurrent_time()) >= 1) {
                                ToastUtil.show(CommonProjectDetailActivity.this, "还未开启转入");
                            } else {
                                if (dayUpLimit.getData().getList().get(i).getLimit() == 0) {
                                    ToastUtil.show(CommonProjectDetailActivity.this, "今天已售罄");
                                } else {
                                    if (!Check.is_login(CommonProjectDetailActivity.this)) {
                                        Intent intent_login = new Intent(CommonProjectDetailActivity.this, NewLoginActivity.class);
                                        startActivity(intent_login);
                                        return;
                                    } else {
                                        try {
                                            getBindCardInfo();
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                break;
            default:
                break;
        }
    }

    protected void initData() {
        mUid = PreferencesUtils.getValueFromSPMap(this, PreferencesUtils.Keys.UID);
        try {
            mUid = MyDES.encrypt(mUid);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void findViewById() {
        mWebView = (ProgressWebView) findViewById(R.id.mWebView);
        title_center_text = (TextView) findViewById(R.id.title_center_text);
        title_left_img = (ImageView) findViewById(R.id.title_left_img);
        tv_protocol = (TextView) findViewById(R.id.tv_protocol);
        rl_transfer = (RelativeLayout) findViewById(R.id.rl_transfer);
        tv_turnIn = (TextView) findViewById(R.id.tv_turnIn);
        try {
            getProductLimit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置转入按钮的状态 @author Ysw created at 2017/3/24 15:13
     */
    private void initTransfer() {
        if (goodType != null && !goodType.equals("") && (goodType.equals("4") || goodType.equals("5") || goodType.equals("8"))) {
            if (dayUpLimit == null) {
                tv_turnIn.setText("立即转入");
                tv_turnIn.setTextColor(getResources().getColor(R.color.checked));
                rl_transfer.setOnClickListener(CommonProjectDetailActivity.this);
                return;
            }
            ArrayList<ProductLimit> limits = dayUpLimit.getData().getList();
            for (int i = 0; i < limits.size(); i++) {
                String type = limits.get(i).getGoods_type();
                if (type != null && type.equals(goodType)) {
                    setProductLimit(limits, i);
                }
            }
        } else {
            tv_turnIn.setText("立即转入");
            tv_turnIn.setTextColor(getResources().getColor(R.color.checked));
            rl_transfer.setOnClickListener(CommonProjectDetailActivity.this);
        }
    }

    /**
     * 设置产品线的限购 @author Ysw created at 2017/4/11 18:52
     */
    private void setProductLimit(ArrayList<ProductLimit> limits, int i) {
        ProductLimit data = limits.get(i);
        long start_time = data.getStart_time();
        long next_time = data.getNext_time();
        long current_time = dayUpLimit.getData().getCurrent_time();
        if (start_time != 0) {
            if (TimeUtil.IsToday(start_time, current_time)) {
                if (TimeUtil.compareTime(start_time, current_time) <= 0) {
                    if (data.getLimit() == 0) {
                        rl_transfer.setOnClickListener(null);
                        tv_turnIn.setText("已售罄，明天" + TimeUtil.formatHourDateTime(next_time) + "开放转入");
                        tv_turnIn.setTextColor(getResources().getColor(R.color.gray_bg_btn));
                    } else if (data.getLimit() == 1) {
                        rl_transfer.setOnClickListener(CommonProjectDetailActivity.this);
                        tv_turnIn.setTextColor(getResources().getColor(R.color.checked));
                        tv_turnIn.setText("立即转入");
                    }
                } else {
                    rl_transfer.setOnClickListener(null);
                    tv_turnIn.setText("今天" + TimeUtil.formatHourDateTime(start_time) + "开放转入");
                    tv_turnIn.setTextColor(getResources().getColor(R.color.blue_btn_bg));
                    startTimers(start_time - current_time);
                }
            } else if (TimeUtil.IsYesterday(start_time, current_time)) {
                rl_transfer.setOnClickListener(CommonProjectDetailActivity.this);
                tv_turnIn.setText("立即转入");
                tv_turnIn.setTextColor(getResources().getColor(R.color.checked));
            } else if (TimeUtil.IsTomorrow(start_time, current_time)) {
                rl_transfer.setOnClickListener(CommonProjectDetailActivity.this);
                tv_turnIn.setText("明天" + TimeUtil.formatHourDateTime(next_time) + "开放转入");
                tv_turnIn.setTextColor(getResources().getColor(R.color.blue_btn_bg));
            }
        } else {
            tv_turnIn.setText("立即转入");
            tv_turnIn.setTextColor(getResources().getColor(R.color.checked));
            rl_transfer.setOnClickListener(CommonProjectDetailActivity.this);
        }
    }

    /**
     * 开启定时器 @author Ysw created at 2017/4/8 17:05
     *
     * @param times
     */
    public void startTimers(final long times) {
        new Runnable() {
            @Override
            public void run() {
                while (!isArrive) {
                    try {
                        Thread.sleep(1000);
                        mTimes++;
                        if (mTimes >= times) {
                            isArrive = true;
                            tv_turnIn.setText("立即转入");
                            rl_transfer.setOnClickListener(CommonProjectDetailActivity.this);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        };
    }

    protected void initUI() {
        if (mTitleName != null && mTitleName.equals("活期")) {
            tv_protocol.setVisibility(View.GONE);
        } else {
            tv_protocol.setVisibility(View.VISIBLE);
        }
        tv_protocol.setText("协议");
        mTitleName = mTitleName == null ? "详情" : (mTitleName + "详情");
        mWebView.setWebViewClient(mWebViewClient);
        mWebView.getSettings().setBuiltInZoomControls(false); // 放大缩放按钮
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setDomStorageEnabled(true);
        if (Build.VERSION.SDK_INT >= 21)
            mWebView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        mUrl = UrlConfig.IP_V + UrlConfig.PRODUCT_DETAIL + mUid + "&" + "id=" + assetid;
        mWebView.loadUrl(mUrl);
        try {
            mWebViewUrl = UrlConfig.IP_V + UrlConfig.PRODUCT_PROTOCOL + MyDES.encrypt(assetid);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void setListener() {
        title_left_img.setOnClickListener(this);
        tv_protocol.setOnClickListener(this);
    }

    private BroadcastReceiver mRefreshBroadcastReceiver = new BroadcastReceiver() {
        // 刷新重新进入本页，如8.88活动页登陆成功和转入成功返回
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals("action.refresh_actdetail")) {
                finish();
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mRefreshBroadcastReceiver);
    }

    /**
     * 限购接口调用 @author Ysw created at 2017/3/15 19:32
     */
    private void getProductLimit() throws Exception {
        RequestParams params = new RequestParams();
        String uid = PreferencesUtils.getValueFromSPMap(this, PreferencesUtils.Keys.UID);
        JSONObject cipher = new JSONObject();
        cipher.put(PreferencesUtils.Keys.UID, uid);
        params.put("cipher", MyDES.encrypt(cipher.toString()));
        MyhttpClient.desPost(UrlConfig.MAIN_PRODUCT_LIMIT, params, new AsyncHttpResponseHandler() {
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
                ToastUtil.show(CommonProjectDetailActivity.this, R.string.default_error);
            }

            @Override
            public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
                MyLog.e(TAG, "getProductLimit onSuccess");
                super.onSuccess(arg0, arg1, arg2);
                try {
                    String response = new String(arg2, "UTF-8");
                    response = CommonUtil.transResponse(response);
                    JSONObject obj = new JSONObject(response);
                    String cipher = obj.getString("cipher");
                    String desc = MyDES.decrypt(cipher);
                    MyLog.e(TAG, "getProductLimit.onSuccess：" + desc);
                    BaseModel<DayUpLimitList> model = new Gson().fromJson(desc, new TypeToken<BaseModel<DayUpLimitList>>() {
                    }.getType());
                    if (model.isSuccess()) {
                        dayUpLimit = model;
                        initTransfer();
                    } else {
                        ToastUtil.show(CommonProjectDetailActivity.this, model.getMsg());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void getBindCardInfo() throws Exception {
        RequestParams params = new RequestParams();
        String uid = PreferencesUtils.getValueFromSPMap(CommonProjectDetailActivity.this, PreferencesUtils.Keys.UID);
        UidObj obj = new UidObj(uid);
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
            }

            @Override
            public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
                super.onFailure(arg0, arg1, arg2, arg3);
                ToastUtil.show(CommonProjectDetailActivity.this, R.string.default_error);
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
                    BaseModel<UserBindinfo> result = new Gson().fromJson(desc, new TypeToken<BaseModel<UserBindinfo>>() {
                    }.getType());
                    if (result.isSuccess()) {
                        UserBindinfo data = result.getData();
                        JinrApp.instance().state_bankBind = Integer.valueOf(data.getState_bankBind());
                        JinrApp.instance().state_tradePassword = Integer.valueOf(data.getState_tradePassword());
                        PreferencesUtils.putIntToSPMap(CommonProjectDetailActivity.this, PreferencesUtils.Keys.IS_BIND_CARD,
                                Integer.valueOf(data.getState_bankBind()));
                        PreferencesUtils.putIntToSPMap(CommonProjectDetailActivity.this, PreferencesUtils.Keys.IS_SET_TRADE_PWD,
                                Integer.valueOf(data.getState_tradePassword()));
                        jump();
                    } else {
                        ToastUtil.show(CommonProjectDetailActivity.this, result.getMsg());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void jump() {
        Intent intent = new Intent();
        ProductCommonModel productCommonModel = new ProductCommonModel();
        productCommonModel.setCode(mProduct_code);
        productCommonModel.setAssetid(assetid);
        productCommonModel.setName(mTitleName);
        productCommonModel.setEventKey(eventKey);
        productCommonModel.setStatus(arrow);
        intent.putExtra("regular", productCommonModel);
        if (mProduct_code != null && !mProduct_code.equals("") && !mTitleName.equals("")) {
            intent.setClass(CommonProjectDetailActivity.this, CurrentPurchaseFirstActivity.class);
            startActivity(intent);
        } else {
            ToastUtil.show(CommonProjectDetailActivity.this, R.string.default_error);
        }
    }

    private WebViewClient mWebViewClient = new WebViewClient() {
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            MyLog.e(TAG, "CommonProjectDetailActivity.onPageFinished：" + url);
            title_center_text.setText(mWebView.getTitle());
            mTitleName = mWebView.getTitle();
        }

        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            super.onLoadResource(view, url);
            String preStr = "iosaction::Share:";
            if (url != null && url.contains(preStr)) {
                String[] split = url.split("&");
                mProduct_code = split[1];
            } else if (url.contains("reload_jinr966")) {
                return true;
            }
            return true;
        }

        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);
            mWebView.loadUrl("file:///android_asset/load_error.html");
        }

        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            handler.proceed();
        }
    };
}
