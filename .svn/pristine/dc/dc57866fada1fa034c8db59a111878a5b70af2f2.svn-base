package com.jinr.core.gift;

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
import android.widget.ImageView;
import android.widget.TextView;

import com.jinr.core.R;
import com.jinr.core.base.BaseActivity;
import com.jinr.core.config.UrlConfig;
import com.jinr.core.utils.CommonUtil;
import com.jinr.core.utils.DensityUtil;
import com.jinr.core.utils.MyDES;
import com.jinr.core.utils.ToastUtil;
import com.jinr.graph_view.ProgressWebView;

public class GiftDetailActivity extends BaseActivity implements OnClickListener {
    private ImageView title_left_img; // title左边图片
    private TextView title_center_text; // title标题
    private ProgressWebView mWebView;
    private String mType;
    private String mUrl;
    private String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gift_detail);
        initData();
        findViewById();
        initUI();
    }

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

    @Override
    protected void initData() {
        id = getIntent().getStringExtra("id");
        mType = getIntent().getStringExtra("type");
    }

    @Override
    protected void findViewById() {
        title_left_img = (ImageView) findViewById(R.id.title_left_img);
        title_center_text = (TextView) findViewById(R.id.title_center_text);
        mWebView = (ProgressWebView) findViewById(R.id.wv_event);
        mWebView.setWebViewClient(mWebViewClient);
        mWebView.getSettings().setBuiltInZoomControls(true); // 放大缩放按钮
        // 如果访问的页面中有JavaScript，则WebView必须设置支持JavaScript
        mWebView.getSettings().setJavaScriptEnabled(true);
        if (Build.VERSION.SDK_INT >= 21) {
            mWebView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        try {
            id = MyDES.encrypt(id);
            mUrl = UrlConfig.IP_V + UrlConfig.BOON_DETAIL + id + "&type=" + mType;
            mWebView.loadUrl(mUrl);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void initUI() {
        title_left_img.setOnClickListener(this);
    }

    @Override
    protected void setListener() {
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
            title_center_text.setText(mWebView.getTitle());
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
                    if (DensityUtil.isNetworkAvailable(GiftDetailActivity.this)) {
                        mWebView.loadUrl(mUrl);
                    } else {
                        ToastUtil.show(GiftDetailActivity.this, "网络异常,请检查网络");
                    }
                    return true;
                }
                return super.shouldOverrideUrlLoading(view, url);
            } else {
                return true;
            }
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
