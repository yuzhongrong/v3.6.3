package com.jinr.core.trade.purchase;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.SslErrorHandler;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.jinr.core.R;
import com.jinr.core.base.BaseActivity;
import com.jinr.core.utils.CommonUtil;
import com.jinr.core.utils.DensityUtil;
import com.jinr.core.utils.ToastUtil;
import com.jinr.graph_view.ProgressWebView;

public class PurchaseLimitNoteActivity extends BaseActivity implements OnClickListener {
    private ProgressWebView mWebView;
    private String type;
    private String mUrl;
    private TextView title_center_text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchase_limit_note);
        initUI();
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void findViewById() {

    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void initUI() {
        ((TextView) findViewById(R.id.title_center_text)).setText(R.string.trade_introduce_a);
        // type == 2 的时候目前已被卡卷占用 @author Ysw created at 2017/3/30 18:03
        type = getIntent().getStringExtra("type");
        mUrl = getIntent().getStringExtra("url");
        findViewById(R.id.title_left_img).setOnClickListener(this);
        title_center_text = (TextView) findViewById(R.id.title_center_text);
        mWebView = (ProgressWebView) findViewById(R.id.wv_limit_note);
        mWebView.setWebViewClient(mWebViewClient);
        mWebView.getSettings().setBuiltInZoomControls(true); // 放大缩放按钮
        // 如果访问的页面中有JavaScript，则WebView必须设置支持JavaScript
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setBlockNetworkImage(false);
        if (Build.VERSION.SDK_INT >= 21) {
            mWebView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        mWebView.loadUrl(mUrl);
    }

    @Override
    protected void setListener() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_left_img:
                finish();
                break;

            default:
                break;
        }
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
            if (type != null && type.equals("2")) {
                title_center_text.setText(mWebView.getTitle());
            }
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
                String str = "iosaction::Share:goto_coupon";
                if (url.contains("reload_jinr966")) {
                    if (DensityUtil.isNetworkAvailable(PurchaseLimitNoteActivity.this)) {
                        mWebView.loadUrl(mUrl);
                    } else {
                        ToastUtil.show(PurchaseLimitNoteActivity.this, "网络异常,请检查网络");
                    }
                    return true;
                } else if (url.contains(str)) {
                    Intent intent = new Intent();
                    intent.putExtra("url", url);
                    setResult(2, intent);
                    finish();
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
    public void onBackPressed() {
        if (mWebView.canGoBack()) {
            mWebView.goBack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            mWebView.stopLoading();
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }
}
