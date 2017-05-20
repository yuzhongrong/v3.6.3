package com.jinr.core.gift;

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
import android.widget.TextView;

import com.jinr.core.R;
import com.jinr.core.base.BaseActivity;
import com.jinr.core.config.UrlConfig;
import com.jinr.core.news.ActDetailActivity;
import com.jinr.core.utils.CommonUtil;
import com.jinr.core.utils.DensityUtil;
import com.jinr.core.utils.MyDES;
import com.jinr.core.utils.MyLog;
import com.jinr.core.utils.PreferencesUtils;
import com.jinr.core.utils.ToastUtil;

@SuppressLint("SetJavaScriptEnabled")
public class NewActActivity extends BaseActivity implements OnClickListener {
	public WebView mWebView = null;
	private String user_id;
	private String mUrl;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_gift_web_view);
		initData();
		findViewById();
		initUI();
	}

	@Override
	protected void initData() {
		// TODO Auto-generated method stub
		user_id = PreferencesUtils.getValueFromSPMap(NewActActivity.this,
				PreferencesUtils.Keys.UID);
	}

	@Override
	protected void findViewById() {
		// TODO Auto-generated method stub
		TextView title_center_text = (TextView) findViewById(R.id.title_center_text);
		title_center_text.setText("活动");
		findViewById(R.id.title_left_img).setOnClickListener(this);
		mWebView = (WebView) findViewById(R.id.wv_gift);
	}

	@Override
	protected void initUI() {
		// TODO Auto-generated method stub
		mWebView.setWebViewClient(mWebViewClient);
		mWebView.getSettings().setBuiltInZoomControls(true); // 放大缩放按钮
		// 如果访问的页面中有JavaScript，则WebView必须设置支持JavaScript
		mWebView.getSettings().setJavaScriptEnabled(true);
		mWebView.getSettings().setBlockNetworkImage(false);
		if (Build.VERSION.SDK_INT >= 21) {
			mWebView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
		}
		// 正在生效
		try {
			user_id = MyDES.encrypt(user_id);
			MyLog.i("TAG", "user_id" + user_id);
		} catch (Exception e) {
			e.printStackTrace();
		}
		mUrl = UrlConfig.IP_M + UrlConfig.ACTIVITY_LIST + user_id;
		MyLog.d("TAG", "活动消息地址:" + mUrl);
		mWebView.loadUrl(mUrl);
	}

	@Override
	protected void setListener() {
		// TODO Auto-generated method stub

	}

	private WebViewClient mWebViewClient = new WebViewClient() {

		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {

			String preStr = "iosaction::Share:";
			if (!CommonUtil.isFastDoubleClick()) {
				if (null != url && url.contains(preStr)) {
					Intent intent = new Intent(NewActActivity.this,
							ActDetailActivity.class);
					String id = url.replace(preStr, "");
					intent.putExtra("id", id);
					MyLog.i("1", id);
					intent.putExtra("turn", "1");
					startActivity(intent);

					return true;
				} else if (url.contains("reload_jinr966")) {
					if (DensityUtil.isNetworkAvailable(NewActActivity.this)) {
						mWebView.loadUrl(mUrl);
					} else {
						ToastUtil.show(NewActActivity.this, "网络异常,请检查网络");
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
			dismissWaittingDialog();
		}

		// 在页面加载开始时调用。
		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			super.onPageStarted(view, url, favicon);
			showWaittingDialog(NewActActivity.this);
		}

		@Override
		public void onReceivedError(WebView view, int errorCode,
				String description, String failingUrl) {
			super.onReceivedError(view, errorCode, description, failingUrl);
			mWebView.loadUrl("file:///android_asset/load_error.html");
		}

		@Override
		public void onReceivedSslError(WebView view, SslErrorHandler handler,
				SslError error) {
			handler.proceed();
		}
	};

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.title_left_img:
			finish();
			break;

		default:
			break;
		}
	}

}
