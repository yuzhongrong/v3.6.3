package com.jinr.core.trade;

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
import com.jinr.core.utils.ToastUtil;
import com.jinr.graph_view.ProgressWebView;

/**
 * 营运月报
 *
 * @author 757
 */
public class OperationsAcitivity extends BaseActivity implements OnClickListener {
    private String path;
    private ImageView title_left_img;
    private ProgressWebView web_ask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cj_wenti);
        findViewById();
        initData();
        initUI();
        setListener();
    }

    @Override
    protected void initData() {
        path = UrlConfig.IP_M + UrlConfig.APPHELP_YYBG;
    }

    @Override
    protected void findViewById() {
        TextView title_center_text = (TextView) findViewById(R.id.title_center_text);
        title_center_text.setText("运营报告");
        findViewById(R.id.title_left_img).setOnClickListener(this);
        title_left_img = (ImageView) findViewById(R.id.title_left_img);
        title_left_img.setOnClickListener(this);
        web_ask = (ProgressWebView) findViewById(R.id.web_ask);
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void initUI() {
        web_ask.setWebViewClient(mWebViewClient);
        web_ask.getSettings().setBuiltInZoomControls(true); // 放大缩放按钮
        // 如果访问的页面中有JavaScript，则WebView必须设置支持JavaScript
        web_ask.getSettings().setJavaScriptEnabled(true);
        web_ask.getSettings().setBlockNetworkImage(false);
        if (Build.VERSION.SDK_INT >= 21) {
            web_ask.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        web_ask.loadUrl(path);
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

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);
            web_ask.loadUrl("file:///android_asset/load_error.html");
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (!CommonUtil.isFastDoubleClick()) {
                if (url.contains("reload_jinr966")) {
                    if (DensityUtil.isNetworkAvailable(OperationsAcitivity.this)) {
                        web_ask.loadUrl(path);
                    } else {
                        ToastUtil.show(OperationsAcitivity.this, "网络异常,请检查网络");
                    }
                    return true;
                }
            } else {
                return true;
            }
            return super.shouldOverrideUrlLoading(view, url);
        }

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
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            handler.proceed();
        }
    };
}
