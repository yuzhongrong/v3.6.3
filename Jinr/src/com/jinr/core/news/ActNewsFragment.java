package com.jinr.core.news;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.SslErrorHandler;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.jinr.core.R;
import com.jinr.core.base.BaseFragment;
import com.jinr.core.config.UrlConfig;
import com.jinr.core.utils.CommonUtil;
import com.jinr.core.utils.DensityUtil;
import com.jinr.core.utils.MyDES;
import com.jinr.core.utils.MyLog;
import com.jinr.core.utils.PreferencesUtils;
import com.jinr.core.utils.ToastUtil;
import com.jinr.graph_view.ProgressWebView;

/**
 * 活动消息页面-web页
 *
 * @author 640
 */

public class ActNewsFragment extends BaseFragment {
    public static final String TAG = "ActNewsFragment";
    public ProgressWebView mWebView = null;
    private String user_id;
    private String mUrl;
    private String latitude;
    private String longitude;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.act_news_web_activity, null);
        initData();
        findViewById(view);
        initUI();
        return view;
    }

    @Override
    protected void initData() {
        user_id = PreferencesUtils.getValueFromSPMap(getActivity(), PreferencesUtils.Keys.UID);
        Bundle args = getArguments();
        latitude = args.getString("lat");
        longitude = args.getString("lng");
    }

    @Override
    protected void findViewById(View view) {
        mWebView = (ProgressWebView) view.findViewById(R.id.wv_gift);
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void initUI() {
        mWebView.setWebViewClient(mWebViewClient);
        mWebView.getSettings().setBuiltInZoomControls(true); // 放大缩放按钮
        // 如果访问的页面中有JavaScript，则WebView必须设置支持JavaScript
        mWebView.getSettings().setJavaScriptEnabled(true);
        if (Build.VERSION.SDK_INT >= 21) {
            mWebView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        // 正在生效
        try {
            user_id = MyDES.encrypt(user_id);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (null == latitude || "4.9E-324".equals(latitude)) latitude = "";
        if (null == longitude || "4.9E-324".equals(longitude)) longitude = "";
        mUrl = UrlConfig.IP_M + UrlConfig.ACTIVITY_LIST + user_id + "&lat=" + latitude + "&lng=" + longitude;
        MyLog.e(TAG, "ActNewsFragment.initUI：" + mUrl);
        mWebView.loadUrl(mUrl);
    }

    @Override
    protected void setListener() {
    }

    private WebViewClient mWebViewClient = new WebViewClient() {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            String preStr = "iosaction::Share:";
            if (!CommonUtil.isFastDoubleClick()) {
                if (null != url && url.contains(preStr)) {
                    Intent intent = new Intent(ActNewsFragment.this.getActivity(), ActDetailActivity.class);
                    String id = url.replace(preStr, "");
                    intent.putExtra("id", id);
                    intent.putExtra("turn", "1");
                    ActNewsFragment.this.getActivity().startActivity(intent);
                    return true;
                }
                if (url.contains("reload_jinr966")) {
                    if (DensityUtil.isNetworkAvailable(getActivity())) {
                        mWebView.loadUrl(mUrl);
                    } else {
                        ToastUtil.show(getActivity(), "网络异常,请检查网络");
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
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);
            mWebView.loadUrl("file:///android_asset/load_error.html");
        }

        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            handler.proceed();
        }
    };
}
