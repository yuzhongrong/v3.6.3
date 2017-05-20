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
import android.widget.TextView;

import com.jinr.core.R;
import com.jinr.core.base.BaseActivity;
import com.jinr.core.config.Check;
import com.jinr.core.gift.BonusCenterActivity;
import com.jinr.core.regist.NewLoginActivity;
import com.jinr.core.regular.RegularAssetsActivity;
import com.jinr.core.utils.CommonUtil;
import com.jinr.core.utils.DensityUtil;
import com.jinr.core.utils.GetImsi;
import com.jinr.core.utils.MyDES;
import com.jinr.core.utils.PreferencesUtils;
import com.jinr.core.utils.ToastUtil;
import com.jinr.graph_view.ProgressWebView;

import java.net.URLDecoder;

import model.ProductCommonModel;

@SuppressLint("SetJavaScriptEnabled")
public class CommonWebActivity extends BaseActivity implements OnClickListener {

    private String path;
    private String titleName;
    private ProgressWebView mWebview;
    private String version = "";
    protected boolean flag = false;
    private TextView title_center_text;

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
    }

    @Override
    protected void findViewById() {
        path = getIntent().getStringExtra("url");
        titleName = getIntent().getStringExtra("titleName");
        title_center_text = (TextView) findViewById(R.id.title_center_text);
        title_center_text.setText(titleName);
        findViewById(R.id.title_left_img).setOnClickListener(this);
        mWebview = (ProgressWebView) findViewById(R.id.web_ask);
        mWebview.getSettings().setBuiltInZoomControls(true); // 放大缩放按钮
        mWebview.getSettings().setJavaScriptEnabled(true);
        if (Build.VERSION.SDK_INT >= 21) {
            mWebview.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        String uid = PreferencesUtils.getValueFromSPMap(CommonWebActivity.this, PreferencesUtils.Keys.UID);
        version = GetImsi.getVersion(CommonWebActivity.this);
        if (uid != null && !uid.equals("")) {
            try {
                uid = MyDES.encrypt(uid);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (titleName.equals("兑换码")) {
            path = path + uid + "&machine=an";
            path = path.toString().replace("\n", "");
            mWebview.loadUrl(path);
        } else {
            mWebview.loadUrl(path);
        }
        mWebview.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.startsWith("mailto:") || url.startsWith("geo:") || url.startsWith("tel:")) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(intent);
                    return true;
                }

                if (url.contains("reload_jinr966")) {
                    if (DensityUtil.isNetworkAvailable(CommonWebActivity.this)) {
                        mWebview.loadUrl(path);
                    } else {
                        ToastUtil.show(CommonWebActivity.this, "网络异常,请检查网络");
                    }
                    return true;
                }

                if (url.contains("iosaction::Share:")) {
                    String preStr = "iosaction::Share:";
                    url = url.replaceAll(preStr, "");
                    Intent intent = null;
                    if (url.contains("goto_regular_asset")) {
                        String parm[] = url.split("&");
                        String mversion = parm[1];
                        String id = parm[2];
                        String name = parm[3];
                        String code = parm[4];
                        String goods_type = parm[5];
                        try {
                            name = URLDecoder.decode(name, "utf-8");
                        } catch (Exception e1) {
                            e1.printStackTrace();
                        }
                        if (version.compareTo(mversion) >= 0) {
                            if (Check.is_login(CommonWebActivity.this)) {// 已登录
                                try {
                                    if (!CommonUtil.isFastDoubleClick()) {
                                        ProductCommonModel productCommonModel = new ProductCommonModel();
                                        Bundle bundle = new Bundle();
                                        intent = new Intent(CommonWebActivity.this, RegularAssetsActivity.class);
                                        productCommonModel.setCode(code);
                                        productCommonModel.setName(name);
                                        productCommonModel.setAssetid(id);
                                        productCommonModel.setStatus("0");
                                        productCommonModel.setType(goods_type);
                                        bundle.putSerializable("regular", productCommonModel);
                                        intent.putExtras(bundle);
                                        startActivity(intent);
                                        finish();
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } else {// 未登录
                                PreferencesUtils.Keys.BACK_TO_ACT = 1;
                                intent = new Intent(CommonWebActivity.this, NewLoginActivity.class);
                                startActivity(intent);
                            }
                        } else {
                            ToastUtil.show(CommonWebActivity.this, "请更新至最新包");
                        }
                    } else if (url.contains("goto_cash_bonus")) {
                        String parm[] = url.split("&");
                        String mversion = parm[1];
                        if (version.compareTo(mversion) >= 0) {
                            if (Check.is_login(CommonWebActivity.this)) {// 已登录
                                if (!CommonUtil.isFastDoubleClick()) {
                                    startActivity(new Intent(CommonWebActivity.this, BonusCenterActivity.class));
                                }
                            } else {// 未登录
                                PreferencesUtils.Keys.BACK_TO_ACT = 1;
                                intent = new Intent(CommonWebActivity.this, NewLoginActivity.class);
                                startActivity(intent);
                            }
                            finish();
                        } else {
                            ToastUtil.show(CommonWebActivity.this, "请更新至最新包");
                        }
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
                mWebview.loadUrl("file:///android_asset/load_error.html");
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
        if ((keyCode == KeyEvent.KEYCODE_BACK) && mWebview.canGoBack()) {
            if (titleName.equals("平台数据")) {
                finish();
                return true;
            }
            mWebview.goBack(); // goBack()表示返回WebView的上一页面
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
                if (flag) finish();
                if (mWebview.canGoBack() && !titleName.equals("平台数据")) {
                    mWebview.goBack();
                } else {
                    finish();
                }
                break;
            default:
                break;
        }
    }
}
