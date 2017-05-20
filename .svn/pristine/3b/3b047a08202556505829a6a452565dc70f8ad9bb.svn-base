package com.jinr.core.my_change;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.SslErrorHandler;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import com.jinr.core.R;
import com.jinr.core.utils.CommonUtil;
import com.jinr.core.utils.DensityUtil;
import com.jinr.core.utils.ToastUtil;
import com.jinr.graph_view.ProgressWebView;

/**
 * 鲸鱼服务中心网页版
 *
 * @author CQJ
 */
public class WebViewActivity extends Activity implements OnClickListener {
    private ProgressWebView mWebView;
    private TextView mTitle;
    private ImageView mBackImageView;
    private String mUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);
        initView();
        initUrl();
    }

    private void initUrl() {
        Bundle extras = getIntent().getExtras();
        mUrl = extras.getString("url");
        String title = extras.getString("title");
        String type = extras.getString("type");
        if (!TextUtils.isEmpty(title)) {
            mTitle.setText(title);
        }
        mWebView.loadUrl(mUrl);
        if (title.equals("公告详情")) {
            mBackImageView.setVisibility(View.VISIBLE);
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void initView() {
        mTitle = (TextView) findViewById(R.id.title_center_text);
        mBackImageView = (ImageView) findViewById(R.id.title_left_img);
        findViewById(R.id.title_left_img).setOnClickListener(this);
        mWebView = (ProgressWebView) findViewById(R.id.webview_webview);
        mWebView.setWebViewClient(mWebViewClient);
        mWebView.getSettings().setBuiltInZoomControls(true); // 放大缩放按钮
        // 如果访问的页面中有JavaScript，则WebView必须设置支持JavaScript
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setBlockNetworkImage(false);
        if (Build.VERSION.SDK_INT >= 21) {
            mWebView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        findViewById(R.id.tv_back_title).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_left_img:
                finish();
                break;
            case R.id.tv_back_title:
                finish();
                break;
        }
    }

    private WebViewClient mWebViewClient = new WebViewClient() {
        // 在加载页面资源时会调用，每一个资源（比如图片）的加载都会调用一次。
        @Override
        public void onLoadResource(WebView view, String url) {
            super.onLoadResource(view, url);
        }

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
                    if (DensityUtil.isNetworkAvailable(WebViewActivity.this)) {
                        mWebView.loadUrl(mUrl);
                    } else {
                        ToastUtil.show(WebViewActivity.this, "网络异常,请检查网络");
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
