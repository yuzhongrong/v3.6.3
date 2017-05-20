/*
 * AboutUsActivity.java
 * @author Andrew Lee
 * 2014-10-18 下午12:20:07
 */
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
 * 关于我们的界面 @author Ysw created at 2017/3/14 17:51
 */
@SuppressLint("SetJavaScriptEnabled")
public class AboutUsActivity extends BaseActivity implements OnClickListener {
    private ImageView title_left_img; // title左边图片
    private TextView title_center_text; // title标题
    private ProgressWebView web_about;
    private String path;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about_us);
        initData();
        findViewById();
        initUI();
        setListener();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void initData() {
        path = UrlConfig.IP_M + UrlConfig.APPHELP_ABOUT;
    }

    @Override
    protected void findViewById() {
        title_left_img = (ImageView) findViewById(R.id.title_left_img);
        title_center_text = (TextView) findViewById(R.id.title_center_text);
        web_about = (ProgressWebView) findViewById(R.id.web_about);
        web_about.getSettings().setBuiltInZoomControls(true); // 放大缩放按钮
        web_about.getSettings().setJavaScriptEnabled(true);
        web_about.getSettings().setBlockNetworkImage(false);
        if (Build.VERSION.SDK_INT >= 21) {
            web_about.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        web_about.loadUrl(path);
        web_about.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                showWaittingDialog(AboutUsActivity.this);
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                dismissWaittingDialog();
                super.onPageFinished(view, url);
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                web_about.loadUrl("file:///android_asset/load_error.html");
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (!CommonUtil.isFastDoubleClick()) {
                    if (url.startsWith("mailto:") || url.startsWith("geo:") || url.startsWith("tel:")) {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        startActivity(intent);
                        return true;
                    }
                    if (url.contains("reload_jinr966")) {
                        if (DensityUtil.isNetworkAvailable(AboutUsActivity.this)) {
                            web_about.loadUrl(path);
                        } else {
                            ToastUtil.show(AboutUsActivity.this, "网络异常,请检查网络");
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
        });
    }

    @Override
    protected void initUI() {
        title_center_text.setText(R.string.about_us_title);
    }

    @Override
    protected void setListener() {
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
}
