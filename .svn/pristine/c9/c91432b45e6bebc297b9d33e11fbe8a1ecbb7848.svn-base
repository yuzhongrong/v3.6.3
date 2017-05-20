package com.jinr.core.notice;

import android.annotation.SuppressLint;
import android.content.Intent;
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
import com.jinr.core.utils.ToastUtil;
import com.jinr.graph_view.ProgressWebView;

@SuppressLint("SetJavaScriptEnabled")
public class NoticeListActivity extends BaseActivity implements OnClickListener {
    private TextView title_center_text;
    private ProgressWebView mWebView;
    private ImageView title_left_img;
    private String mUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice);
        initUI();
        initData();
    }

    @Override
    protected void setListener() {
    }

    @Override
    protected void findViewById() {
    }

    @Override
    protected void initUI() {
        title_center_text = (TextView) findViewById(R.id.title_center_text);
        title_left_img = (ImageView) findViewById(R.id.title_left_img);
        mWebView = (ProgressWebView) findViewById(R.id.mWebView);
        mWebView.getSettings().setBuiltInZoomControls(true); // 放大缩放按钮
        // 如果访问的页面中有JavaScript，则WebView必须设置支持JavaScript
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setBlockNetworkImage(false);
        if (Build.VERSION.SDK_INT >= 21) {
            mWebView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        title_center_text.setText("平台公告");
        title_left_img.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        mUrl = UrlConfig.IP_R + UrlConfig.NEWS_NOTIOC;
        mWebView.loadUrl(mUrl);
        mWebView.setWebViewClient(new mWebViewClient());
    }

    public class mWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (!CommonUtil.isFastDoubleClick()) {
                if (url != null && url.contains("iosaction::Share:")) {
                    Intent intent = new Intent();
                    String Id = url.replace("iosaction::Share:", "");
                    intent.putExtra("Id", Id);
                    intent.setClass(NoticeListActivity.this, NoticeDetailsActivity.class);
                    startActivity(intent);
                } else if (url.contains("reload_jinr966")) {
                    if (DensityUtil.isNetworkAvailable(NoticeListActivity.this)) {
                        mWebView.loadUrl(mUrl);
                    } else {
                        ToastUtil.show(NoticeListActivity.this, "网络异常,请检查网络");
                    }
                    return true;
                }
            }
            return true;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
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
