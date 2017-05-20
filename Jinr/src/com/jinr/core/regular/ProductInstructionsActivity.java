package com.jinr.core.regular;

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
import com.jinr.core.utils.CommonUtil;
import com.jinr.core.utils.DensityUtil;
import com.jinr.core.utils.ToastUtil;
import com.jinr.graph_view.ProgressWebView;

/**
 * 鲸鱼定期服务合同（范本）
 *
 * @author 1048
 */
public class ProductInstructionsActivity extends BaseActivity implements OnClickListener {
    private ImageView title_left_img; // title左边图片
    private TextView title_center_text; // title标题
    private ProgressWebView mWebView;
    private String mUrl;
    private String mName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_instructions);
        mName = getIntent().getStringExtra("name");
        mUrl = getIntent().getStringExtra("mWebViewUrl");
        findViewById();
        initUI();
        setListener();
        initData();
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void findViewById() {
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
    }

    @Override
    protected void initUI() {
        if (mName != null && !mName.equals("")) {
            title_center_text.setText(mName);
        } else {
            title_center_text.setText("产品说明书");
        }
    }

    @Override
    protected void setListener() {
        title_left_img.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        mWebView.loadUrl(mUrl);
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                title_center_text.setText(mWebView.getTitle());
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
                        if (DensityUtil.isNetworkAvailable(ProductInstructionsActivity.this)) {
                            mWebView.loadUrl(mUrl);
                        } else {
                            ToastUtil.show(ProductInstructionsActivity.this, "网络异常,请检查网络");
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
