package com.jinr.core.strongbox;

import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import com.jinr.core.R;
import com.jinr.core.base.BaseActivity;
import com.jinr.graph_view.ProgressWebView;

public class StrongBoxPactActivity extends BaseActivity implements View.OnClickListener {

    private TextView title_center_text;
    private ImageView title_left_img;
    private ProgressWebView mWebView;
    private String mUrl;
    private TextView title_right_tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_strong_box_pact);
        mUrl = getIntent().getStringExtra("url");
        findViewById();
        initUI();
        setListener();
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void findViewById() {
        title_center_text = (TextView) findViewById(R.id.title_center_text);
        title_right_tv = (TextView) findViewById(R.id.tv_protocol);
        title_left_img = (ImageView) findViewById(R.id.title_left_img);
        mWebView = (ProgressWebView) findViewById(R.id.mWebview);
    }

    @Override
    protected void initUI() {
        title_right_tv.setVisibility(View.INVISIBLE);
        initWeb(mUrl);
    }

    @Override
    protected void setListener() {
        title_left_img.setOnClickListener(this);
    }

    // 加载web界面
    public void initWeb(final String url) {
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setBuiltInZoomControls(true);
        mWebView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        if (Build.VERSION.SDK_INT >= 21) {
            mWebView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return super.shouldOverrideUrlLoading(view, url);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                String title = view.getTitle();
                title_center_text.setText(title);
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                mWebView.loadUrl("file:///android_asset/load_error.html");
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                handler.proceed();
            }
        });
        mWebView.loadUrl(url);
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
}
