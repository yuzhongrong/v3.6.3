package com.jinr.core.trade.record;

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
import com.jinr.core.utils.CommonUtil;
import com.jinr.core.utils.DensityUtil;
import com.jinr.core.utils.ToastUtil;

public class WebRecordGetCash extends BaseActivity{
	private String url;
	private WebView webView;
	private String mUrl;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_gift);
		initData();
		findViewById();
		setListener();
		initUI();
	}
	@Override
	protected void initData() {
		mUrl=getIntent().getStringExtra("url");
		
	}

	@Override
	protected void findViewById() {
		webView=(WebView) findViewById(R.id.webview);
	}

	@Override
	protected void initUI() {
		((TextView)findViewById(R.id.title_center_text)).setText("转入详情");
		webView.setWebViewClient(mWebViewClient);
		webView.getSettings().setBuiltInZoomControls(true); // 放大缩放按钮
		webView.getSettings().setJavaScriptEnabled(true);
		webView.getSettings().setDomStorageEnabled(true);
		webView.getSettings().setBlockNetworkImage(false);
		if (Build.VERSION.SDK_INT >= 21) {
			webView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
		}
		webView.loadUrl(mUrl);
	}
	private WebViewClient mWebViewClient = new WebViewClient() {

		@Override
		public void onReceivedError(WebView view, int errorCode,
				String description, String failingUrl) {
			super.onReceivedError(view, errorCode, description, failingUrl);
			webView.loadUrl("file:///android_asset/load_error.html");
		}
		
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			
			if (!CommonUtil.isFastDoubleClick()) {
				if(url.contains("reload_jinr966")){
					if(DensityUtil.isNetworkAvailable(WebRecordGetCash.this)){
						webView.loadUrl(mUrl);
					}else{
						ToastUtil.show(WebRecordGetCash.this, "网络异常,请检查网络");
					}
					return true;
				} 
			}else{
				return true;
			}
			
			return super.shouldOverrideUrlLoading(view, url);
		}
		@Override
		public void onLoadResource(WebView view, String url) {
			super.onLoadResource(view, url);
		}
		@Override
		public void onPageFinished(WebView view, String url) {
			super.onPageFinished(view, url);
			dismissWaittingDialog();
		}
		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			super.onPageStarted(view, url, favicon);
			showWaittingDialog(WebRecordGetCash.this);
		}
		@Override
		public void onReceivedSslError(WebView view, SslErrorHandler handler,
									   SslError error) {
			handler.proceed();
		}
	};
	@Override
	protected void setListener() {
		findViewById(R.id.title_left_img).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});;
	}
	
}
