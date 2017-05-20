package com.jinr.core.gift;

import android.content.Intent;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.SslErrorHandler;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.jinr.core.R;
import com.jinr.core.base.BaseActivity;
import com.jinr.core.config.EventBusKey;
import com.jinr.core.config.UrlConfig;
import com.jinr.core.news.NewsCenterActivity;
import com.jinr.core.regular.ProductInstructionsActivity;
import com.jinr.core.utils.CommonUtil;
import com.jinr.core.utils.DensityUtil;
import com.jinr.core.utils.MyDES;
import com.jinr.core.utils.MyLog;
import com.jinr.core.utils.PreferencesUtils;
import com.jinr.core.utils.ToastUtil;
import com.jinr.graph_view.ProgressWebView;

import org.simple.eventbus.EventBus;

/**
 * 福利界面 @author Ysw created at 2017/3/15 20:04
 */
public class BonusCenterActivity extends BaseActivity implements OnClickListener {
    public ProgressWebView mWebView = null;
    private String mUrl;
    private TextView tv_protocol;
    private String mWebViewUrl;
    private String mUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_gift_web_view);
        //EventBus.getDefault().register(this);
        initData();
        findViewById();
        initUI();
    }

    @Override
    protected void initData() {
        mUid = PreferencesUtils.getValueFromSPMap(this, PreferencesUtils.Keys.UID);
        try {
            mUid = MyDES.encrypt(mUid);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void findViewById() {
        tv_protocol = (TextView) findViewById(R.id.tv_protocol);
        tv_protocol.setVisibility(View.VISIBLE);
        tv_protocol.setText("使用规则");
        tv_protocol.setOnClickListener(this);
        findViewById(R.id.title_left_img).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                BonusCenterActivity.this.finish();
            }
        });
        mWebView = (ProgressWebView) findViewById(R.id.wv_gift);
        TextView title = (TextView) findViewById(R.id.title_center_text);
        title.setText(getResources().getString(R.string.gift));
    }

    @Override
    protected void initUI() {
        mWebView.setWebViewClient(mWebViewClient);
        mWebView.getSettings().setBuiltInZoomControls(true); // 放大缩放按钮
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setDomStorageEnabled(true);
        if (Build.VERSION.SDK_INT >= 21) {
            mWebView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        mUrl = UrlConfig.IP_V + UrlConfig.WELFARE_LIST + mUid + "&platform=android";
        mWebViewUrl = UrlConfig.IP_V + UrlConfig.WELFARE_RULES;
        mWebView.loadUrl(mUrl);
    }

    @Override
    protected void setListener() {
    }

    private WebViewClient mWebViewClient = new WebViewClient() {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            MyLog.e(TAG, "BonusCenterActivity.shouldOverrideUrlLoading：url = " + url);
            Intent intent = null;
            if (!CommonUtil.isFastDoubleClick()) {
                if (url != null) {
                    MyLog.e(TAG, "shouldOverrideUrlLoading: url = " + url);
                    if (url.contains("iosaction::Share:boon")) {
                        String[] split = url.split("&");
                        intent = new Intent(BonusCenterActivity.this, GiftDetailActivity.class);
                        intent.putExtra("id", split[1]);
                        intent.putExtra("type", split[2]);
                        BonusCenterActivity.this.startActivity(intent);
                    } else if (url.contains("iosaction::Share:goto_home_current")) {
                        String[] split = url.split("&");
                        EventBus.getDefault().postSticky(split[1], EventBusKey.MAIN_CURRENT);
                        finish();
                    } else if (url.contains("iosaction::Share:goto_activity")) {
                        intent = new Intent(BonusCenterActivity.this, NewsCenterActivity.class);
                        BonusCenterActivity.this.startActivity(intent);
                    } else if (url.contains("reload_jinr966")) {
                        if (DensityUtil.isNetworkAvailable(BonusCenterActivity.this)) {
                            mWebView.loadUrl(mUrl);
                        } else {
                            ToastUtil.show(BonusCenterActivity.this, "网络异常,请检查网络");
                        }
                    }
                    return true;
                }
            } else {
                return true;
            }
            return super.shouldOverrideUrlLoading(view, url);
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
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_protocol:
                Intent intent = new Intent();
                intent.putExtra("mWebViewUrl", mWebViewUrl);
                intent.putExtra("name", "使用规则");
                intent.setClass(BonusCenterActivity.this, ProductInstructionsActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        try {
            //EventBus.getDefault().unregister(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }
}
