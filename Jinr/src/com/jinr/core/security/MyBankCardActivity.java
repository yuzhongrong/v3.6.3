package com.jinr.core.security;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
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
import android.widget.TextView;

import com.jinr.core.R;
import com.jinr.core.base.BaseActivity;
import com.jinr.core.config.UrlConfig;
import com.jinr.core.utils.CommonUtil;
import com.jinr.core.utils.DensityUtil;
import com.jinr.core.utils.PreferencesUtils;
import com.jinr.core.utils.ToastUtil;
import com.jinr.graph_view.ProgressWebView;

public class MyBankCardActivity extends BaseActivity implements OnClickListener {
    private ImageView title_left_img; // title左边图片
    private TextView title_center_text; // title标题
    private ImageView bank_logo_iv; //银行标志
    private TextView bank_name_tv, tv_kefu; //银行名称
    private TextView bank_card_no_tv; //银行卡尾号
    private String user_id = "";
    private ProgressWebView mWebView;
    private String mUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_bank_card);
        initData();
        findViewById();
        initUI();
        setListener();
    }

    @Override
    protected void initData() {
        user_id = PreferencesUtils.getValueFromSPMap(this, PreferencesUtils.Keys.UID);
    }

    @Override
    protected void findViewById() {
        title_left_img = (ImageView) findViewById(R.id.title_left_img);
        title_center_text = (TextView) findViewById(R.id.title_center_text);
        bank_name_tv = (TextView) findViewById(R.id.tv_bank_name);
        bank_card_no_tv = (TextView) findViewById(R.id.tv_bank_no);
        bank_logo_iv = (ImageView) findViewById(R.id.iv_bank_logo);
        mWebView = (ProgressWebView) findViewById(R.id.wv_my_bank_card);
        tv_kefu = (TextView) findViewById(R.id.tv_kefu);
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void initUI() {
        title_center_text.setText("解绑银行卡");
        mWebView.setWebViewClient(mWebViewClient);
        mWebView.getSettings().setBuiltInZoomControls(true); // 放大缩放按钮
        // 如果访问的页面中有JavaScript，则WebView必须设置支持JavaScript
        mWebView.getSettings().setJavaScriptEnabled(true);
        if (Build.VERSION.SDK_INT >= 21) {
            mWebView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        mUrl = UrlConfig.IP_M + UrlConfig.APPHELP_JYB;
        mWebView.loadUrl(mUrl);
        if (!PreferencesUtils.getCValueFromSPMap(MyBankCardActivity.this, PreferencesUtils.Keys.KEFU_PHONE).equals("")) {
            tv_kefu.setText(String.format(getResources().getString(R.string.my_bank_card_tip), PreferencesUtils.getCValueFromSPMap(MyBankCardActivity.this,
                    PreferencesUtils.Keys.KEFU_PHONE)));
        }
        //显示银行卡信息
        setBankInfo();
    }

    @Override
    protected void setListener() {
        title_left_img.setOnClickListener(this);
    }

    private void setBankInfo() {
        String bank_name = PreferencesUtils.getValueFromSPMap(this, PreferencesUtils.Keys.BANK_NAME);
        bank_name_tv.setText(bank_name);
        String bank_card_last = PreferencesUtils.getValueFromSPMap(this, PreferencesUtils.Keys.CARD_LAST);
        bank_card_no_tv.setText("尾号" + bank_card_last);
    }

    private WebViewClient mWebViewClient = new WebViewClient() {
        // 在加载页面资源时会调用，每一个资源（比如图片）的加载都会调用一次。
        @Override
        public void onLoadResource(WebView view, String url) {
            super.onLoadResource(view, url);
        }

        // 在页面加载结束时调用。
        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
        }

        // 在页面加载开始时调用。
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);
            mWebView.loadUrl("file:///android_asset/load_error.html");
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (!CommonUtil.isFastDoubleClick()) {
                if (url.contains("reload_jinr966")) {
                    if (DensityUtil.isNetworkAvailable(MyBankCardActivity.this)) {
                        mWebView.loadUrl(mUrl);
                    } else {
                        ToastUtil.show(MyBankCardActivity.this, "网络异常,请检查网络");
                    }
                    return true;
                }
            } else {
                return true;
            }
            return super.shouldOverrideUrlLoading(view, url);
        }

        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            handler.proceed();
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_left_img:// 左侧图标
                finish();
                break;
            default:
                break;
        }
    }
}
