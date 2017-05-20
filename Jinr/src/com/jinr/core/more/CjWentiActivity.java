package com.jinr.core.more;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.jinr.core.R;
import com.jinr.core.base.BaseActivity;
import com.jinr.core.config.UrlConfig;
import com.jinr.core.utils.DensityUtil;
import com.jinr.core.utils.ToastUtil;
import com.jinr.graph_view.ProgressWebView;

import java.util.ArrayList;
import java.util.List;

public class CjWentiActivity extends BaseActivity implements OnClickListener {

    private String path;
    private ProgressWebView web_ask;
    private TextView title_center_text;
    List<String> titles = new ArrayList<>();
    protected boolean flag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cj_wenti);
        initUI();
        initData();
        findViewById();
        setListener();
    }

    @Override
    protected void initData() {
        path = UrlConfig.IP_V + UrlConfig.DAILYGAIN_FAQ;
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void findViewById() {
        title_center_text = (TextView) findViewById(R.id.title_center_text);
        title_center_text.setText("常见问题");
        findViewById(R.id.title_left_img).setOnClickListener(this);
        web_ask = (ProgressWebView) findViewById(R.id.web_ask);
        web_ask.getSettings().setBuiltInZoomControls(true); // 放大缩放按钮
        web_ask.getSettings().setJavaScriptEnabled(true);
        if (Build.VERSION.SDK_INT >= 21) {
            web_ask.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        web_ask.loadUrl(path);
        WebChromeClient wvcc = new WebChromeClient() {
            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                if (DensityUtil.isNetworkAvailable(CjWentiActivity.this)) {
                    title_center_text.setText(title);
                    titles.add(title);
                } else {
                    title_center_text.setText("常见问题");
                }
            }
        };
        // 设置setWebChromeClient对象
        web_ask.setWebChromeClient(wvcc);
        web_ask.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.startsWith("mailto:") || url.startsWith("geo:") || url.startsWith("tel:")) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(intent);
                    return true;
                }

                if (url.contains("reload_jinr966")) {
                    if (DensityUtil.isNetworkAvailable(CjWentiActivity.this)) {
                        web_ask.loadUrl(path);
                    } else {
                        ToastUtil.show(CjWentiActivity.this, "网络异常,请检查网络");
                    }
                    return true;
                }
                return super.shouldOverrideUrlLoading(view, url);
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
                web_ask.loadUrl("file:///android_asset/load_error.html");
                flag = true;
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                handler.proceed();
            }
        });
    }

    @Override
    // 设置回退
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (flag) {//只要加载过load_error页面，按返回键一律finish，防止在先加载异常后正常加载情况下按返回键出现此页面
            finish();
            return true;
        }
        if ((keyCode == KeyEvent.KEYCODE_BACK) && web_ask.canGoBack()) {
            web_ask.goBack(); // goBack()表示返回WebView的上一页面
            if (titles.size() > 1) {
                titles.remove(titles.size() - 1);
                title_center_text.setText(titles.get(titles.size() - 1));
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void initUI() {

    }

    @Override
    protected void setListener() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_left_img:
                if (flag) {//只要加载过load_error页面，按返回键一律finish，防止在先加载异常后正常加载情况下按返回键出现此页面
                    finish();
                }
                if (web_ask.canGoBack()) {
                    web_ask.goBack();
                    if (titles.size() > 1) {
                        titles.remove(titles.size() - 1);
                        title_center_text.setText(titles.get(titles.size() - 1));
                    }
                } else {
                    finish();
                }
                break;

            default:
                break;
        }
    }
}
