package com.jinr.core;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.SslErrorHandler;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import com.jinr.core.base.BaseActivity;
import com.jinr.core.gift.share.JavaScriptinterface;
import com.jinr.core.utils.CommonUtil;
import com.jinr.core.utils.DensityUtil;
import com.jinr.core.utils.ToastUtil;
import com.jinr.graph_view.ProgressWebView;

@SuppressLint("JavascriptInterface")
public class MyWebViewActivity extends BaseActivity implements OnClickListener {
    private ProgressWebView mWebView;
    private TextView mTitle;
    private ImageView title_left_img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);
        findViewById();
        initData();
        initUI();
        setListener();
    }

    @Override
    protected void initData() {
        mWebView.setWebViewClient(mWebViewClient);
        mWebView.getSettings().setBuiltInZoomControls(true); // 放大缩放按钮
        // 如果访问的页面中有JavaScript，则WebView必须设置支持JavaScript
        mWebView.getSettings().setJavaScriptEnabled(true);
        if (Build.VERSION.SDK_INT >= 21) {
            mWebView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        mWebView.setWebViewClient(mWebViewClient);
        mWebView.getSettings().setBuiltInZoomControls(true); // 放大缩放按钮
        // 如果访问的页面中有JavaScript，则WebView必须设置支持JavaScript
        mWebView.addJavascriptInterface(new JavaScriptinterface(MyWebViewActivity.this, mWebView), "android");
    }

    @Override
    protected void findViewById() {
        mTitle = (TextView) findViewById(R.id.title_center_text);
        title_left_img = (ImageView) findViewById(R.id.title_left_img);
        mWebView = (ProgressWebView) findViewById(R.id.webview_webview);
    }

    @Override
    protected void initUI() {
        Intent intent = getIntent();
        if (intent == null) {
            mTitle.setText(R.string.gift);
            return;
        }
        Bundle extras = intent.getExtras();
        if (null != extras) {
            titles = extras.getString("title");
            urls = extras.getString("url");
        }
        if (!TextUtils.isEmpty(titles)) {
            mTitle.setText(titles);
        }
        mWebView.loadUrl(urls);
    }

    @Override
    protected void setListener() {
        findViewById(R.id.title_left_img).setOnClickListener(this);
        title_left_img.setOnClickListener(this);
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
                    if (DensityUtil.isNetworkAvailable(MyWebViewActivity.this)) {
                        mWebView.loadUrl(urls);
                    } else {
                        ToastUtil.show(MyWebViewActivity.this, "网络异常,请检查网络");
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
    private String titles;
    private String urls;

}
