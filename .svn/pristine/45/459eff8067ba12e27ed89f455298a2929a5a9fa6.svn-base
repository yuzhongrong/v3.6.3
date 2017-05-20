package com.jinr.core.gift.share;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.DownloadListener;
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

/**
 * 我的资产进入的红包列表(6期)
 * 
 * @author CQJ
 * 
 */
public class MyGiftActivity extends BaseActivity implements OnClickListener,DownloadListener {
	private WebView mWebView;
	private TextView mTitle;
	private ImageView title_left_img;
	private String mUrl;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_gift);
		findViewById();
		initData();
		initUI();
		setListener();
	}

	@SuppressLint("JavascriptInterface")
	@Override
	protected void initData() {
		mWebView.setWebViewClient(mWebViewClient);
		mWebView.getSettings().setBuiltInZoomControls(true); // 放大缩放按钮
		// 如果访问的页面中有JavaScript，则WebView必须设置支持JavaScript
		mWebView.getSettings().setJavaScriptEnabled(true);
		if (Build.VERSION.SDK_INT >= 21) {
			mWebView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
		}
		mWebView.setWebViewClient(mWebViewClient);
		mWebView.getSettings().setBuiltInZoomControls(true); // 放大缩放按钮
		// 如果访问的页面中有JavaScript，则WebView必须设置支持JavaScript
		mWebView.addJavascriptInterface(new JavaScriptinterface(
				MyGiftActivity.this, mWebView), "android");

		initUI();

	}

	@Override
	protected void findViewById() {
		mTitle = (TextView) findViewById(R.id.title_center_text);
		title_left_img = (ImageView) findViewById(R.id.title_left_img);
		mWebView = (WebView) findViewById(R.id.webview);

	}

	@Override
	protected void initUI() {
		Intent intent = getIntent();
		if (intent == null) {
			mTitle.setText(R.string.gift);
			return;
		}
		mUrl = intent.getStringExtra("url");
		String title = intent.getStringExtra("title");
		if (!TextUtils.isEmpty(title)) {
			mTitle.setText(title);
		}
		// 红包页面
		// mWebView.loadUrl(url);
		// Android交互Web
		// 本地test.html 文件，记得在assest删除
		// mWebView.loadUrl("file:///android_asset/test.html");
		// 测试网站
		mWebView.loadUrl(mUrl);
	}

	@Override
	protected void setListener() {
		findViewById(R.id.title_left_img).setOnClickListener(this);
		title_left_img.setOnClickListener(this);
		mWebView.setDownloadListener(this);
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
		public void onReceivedError(WebView view, int errorCode,
				String description, String failingUrl) {
			super.onReceivedError(view, errorCode, description, failingUrl);
			mWebView.loadUrl("file:///android_asset/load_error.html");
		}
		
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			
			if (!CommonUtil.isFastDoubleClick()) {
				if(url.contains("reload_jinr966")){
					if(DensityUtil.isNetworkAvailable(MyGiftActivity.this)){
						mWebView.loadUrl(mUrl);
					}else{
						ToastUtil.show(MyGiftActivity.this, "网络异常,请检查网络");
					}
					return true;
				} 
			}else{
				return true;
			}
			
			return super.shouldOverrideUrlLoading(view, url);
		}
		@Override
		public void onReceivedSslError(WebView view, SslErrorHandler handler,
									   SslError error) {
			handler.proceed();
		}
	};

	@Override
	public void onDownloadStart(String url, String userAgent,
			String contentDisposition, String mimetype, long contentLength) {
		// TODO Auto-generated method stub
		Uri uri = Uri.parse(url);
		Intent intent = new Intent(Intent.ACTION_VIEW, uri);
		startActivity(intent);
	}

}
