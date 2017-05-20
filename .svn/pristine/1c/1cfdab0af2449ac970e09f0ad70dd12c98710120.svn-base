package com.jinr.core.gift.share;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jinr.core.MainActivity;
import com.jinr.core.R;
import com.jinr.core.utils.PopupWindowUtil;
import com.jinr.core.utils.ToastUtil;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.SendMessageToWX;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.mm.sdk.openapi.WXMediaMessage;
import com.tencent.mm.sdk.openapi.WXWebpageObject;
import com.tencent.mm.sdk.platformtools.Util;

/**
 * 接口Web 交互Android
 * 
 * @author CQJ
 * 
 */
public class JavaScriptinterface {

	private Activity activity;
	private View v;

	private IWXAPI wxApi;// 微信api

	/** Instantiate the interface and set the context */
	public JavaScriptinterface(Activity activity, View view) {
		this.activity = activity;
		this.v = view;
		wxApi = WXAPIFactory.createWXAPI(activity, Constants.APP_ID);
		wxApi.registerApp(Constants.APP_ID);
	}

	/** Show a toast from the web page */
	public void showToast(String toast) {
		// share();
		showPop(v, activity);
	}

	/** 分享给好友 */
	private void share() {
		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setType("text/plain");
		intent.putExtra(Intent.EXTRA_SUBJECT, "分享");
		intent.putExtra(Intent.EXTRA_TEXT,
				"我正在使用鲤鱼宝理财，鲤鱼理财收益永高余额宝2%！；下载地址：http://www.jinr.com/Public/adt/jinr.apk");
		activity.startActivity(Intent.createChooser(intent, "分享..."));
	}

	private PopupWindow popupwindow;
	private TextView tv_start_grade;
	private RatingBar ratingBar;
	private Button btn_share_to;
	private RelativeLayout ly_miss_share, rel_no_miss;

	/** popupupWindow */
	private void showPop(View v, Activity activity) {
		if (popupwindow == null) {
//			View view = activity.getLayoutInflater().inflate(
//					R.layout.pop_share, null);
//			ratingBar = (RatingBar) view.findViewById(R.id.comment_ratingbar);
//			tv_start_grade = (TextView) view.findViewById(R.id.tv_start_grade);
//			btn_share_to = (Button) view.findViewById(R.id.btn_share_to);
//			ly_miss_share = (RelativeLayout) view
//					.findViewById(R.id.ly_miss_share);
//			rel_no_miss = (RelativeLayout) view.findViewById(R.id.rel_no_miss);
//			btn_share_to.setOnClickListener(new btnClick());
//			rel_no_miss.setOnClickListener(new btnClick());
//			ly_miss_share.setOnClickListener(new btnClick());
//			ratingBar.setOnRatingBarChangeListener(new RatingBarListener());
//			popupwindow = PopupWindowUtil.getPopupWindow(activity, view);
			popupwindow.setOnDismissListener(new OnDismissListener() {
				@Override
				public void onDismiss() {
				}
			});
			popupwindow.setTouchInterceptor(new OnTouchListener() {
				public boolean onTouch(View v, MotionEvent event) {
					if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
						popupwindow.dismiss();
						return true;
					}
					return false;
				}
			});
		}
		popupwindow.update();
		popupwindow.showAtLocation(v, Gravity.CENTER, 0, 0);

	}

	/**
	 * 点击监听
	 * 
	 * @author CQJ
	 * 
	 */
	private class btnClick implements OnClickListener {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
//			case R.id.rel_no_miss:
//				break;
//			case R.id.ly_miss_share:
//				if (popupwindow != null) {
//					popupwindow.dismiss();
//				}
//				break;
//			case R.id.btn_share_to:
//				popupwindow.dismiss();
//				wxApi = WXAPIFactory.createWXAPI(
//						JavaScriptinterface.this.activity, Constants.APP_ID);
//				// showShare(v, activity);//弹出选择分享方式
//				selectWX(true);
//				break;
			// case R.id.tv_share_1:
			// popShare.dismiss();
			// selectWX(false);
			// // 跳转到微信分享
			// break;
			// case R.id.tv_share_2:
			// popShare.dismiss();
			// selectWX(true);
			// // 跳转到朋友圈分享
			// break;

			default:
				break;
			}

		}

	}

	/** 微信分享朋友圈 */
	private void selectWX(boolean i) {
		Intent intent = activity.getPackageManager().getLaunchIntentForPackage(
				"com.tencent.mm");
		if (intent != null) {// 这里如果intent为空，就说明没有安装要跳转的应用嘛
			try {
				// wechatShare(i);
				wxShare(i);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			// 没有安装微信客户端，提醒一下
			ToastUtil.show(activity, "亲，您还没有微信客户端哦！！");
		}

	}

	/** 微信分享 (图片从drawable获取) */
	private void wechatShare(boolean flag) {
		// true分享给朋友圈
		WXWebpageObject webpage = new WXWebpageObject();
		webpage.webpageUrl = "http://www.baidu.com";// 从分享点击进去的链接
		WXMediaMessage msg = new WXMediaMessage(webpage);
		msg.title = activity.getResources().getString(R.string.wx_title);
		msg.description = activity.getResources().getString(
				R.string.description);
		Bitmap thumb = BitmapFactory.decodeResource(activity.getResources(),
				R.drawable.share_1);
		Bitmap thumbSize = Bitmap.createScaledBitmap(thumb, 150, 150, true);
		msg.setThumbImage(thumbSize);
		msg.thumbData = Util.bmpToByteArray(thumb, true);

		SendMessageToWX.Req req = new SendMessageToWX.Req();
		req.transaction = buildTransaction("webpage");
		req.message = msg;
		req.scene = flag ? SendMessageToWX.Req.WXSceneTimeline
				: SendMessageToWX.Req.WXSceneSession;
		wxApi.sendReq(req);
	}

	private void wxShare(boolean flag) {
		// 3.0以上必须要开一个子线程???
		// new Thread(new Runnable() {
		// @Override
		// public void run() {
		//
		// }
		// }).start();
		WXWebpageObject webpage = new WXWebpageObject();
		webpage.webpageUrl = Constants.webpageUrl;// 点击进去的链接
		//
		WXMediaMessage msg = new WXMediaMessage(webpage);
		msg.title = activity.getResources().getString(R.string.wx_title);
		msg.description = activity.getResources().getString(
				R.string.description);
		// msg.title = Constants.title;//分享的title
		// msg.description = Constants.description;//分享的描述
		// 下载图片并设置大小
		Bitmap bmp = null;
		try {
			// 重网络上下载图片返回Bitmap
			// bmp = BitmapFactory.decodeStream(new URL(Constants.img_url)
			// .openStream());
			bmp = getBitmap(Constants.img_url);
			if (bmp == null) {
				ToastUtil.show(activity,R.string.un_no_img);
				return;
			}
		} catch (Exception e) {
			e.printStackTrace();
			ToastUtil.show(activity,R.string.un_succeed);
		}
		Bitmap thumbBmp = Bitmap.createScaledBitmap(bmp, 50, 50, true);
		bmp.recycle();
		msg.thumbData = BitmapToByteUtil.bmpToByteArray(thumbBmp, true);
		//
		SendMessageToWX.Req req = new SendMessageToWX.Req();
		req.transaction = buildTransaction("webpage");
		req.message = msg;
		req.scene = flag ? SendMessageToWX.Req.WXSceneTimeline
				: SendMessageToWX.Req.WXSceneSession;
		wxApi.sendReq(req);
	}

	private TextView tv_share_1, tv_share_2;
	private PopupWindow popShare;

	/** popupupWindow (弹出选择朋友或者朋友圈) */
	private void showShare(View v, MainActivity activity) {
		if (popShare == null) {
			View view = activity.getLayoutInflater().inflate(
					R.layout.pop_share_gift, null);
			tv_share_1 = (TextView) view.findViewById(R.id.tv_share_1);
			tv_share_2 = (TextView) view.findViewById(R.id.tv_share_2);
			tv_share_1.setOnClickListener(new btnClick());
			tv_share_2.setOnClickListener(new btnClick());
			popShare = PopupWindowUtil.getPopupWindow(activity, view);
			popShare.setOnDismissListener(new OnDismissListener() {
				@Override
				public void onDismiss() {
				}
			});
			popShare.setTouchInterceptor(new OnTouchListener() {
				public boolean onTouch(View v, MotionEvent event) {
					if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
						popShare.dismiss();
						return true;
					}
					return false;
				}
			});
		}
		popShare.update();
		popShare.showAtLocation(v, Gravity.CENTER, 0, 0);

	}

	/**
	 * RatingBar监听
	 * 
	 * @author Administrator
	 * 
	 */
	private class RatingBarListener implements
			RatingBar.OnRatingBarChangeListener {
		@Override
		public void onRatingChanged(RatingBar ratingBar, float rating,
				boolean fromUser) {
			tv_start_grade.setText("" + String.valueOf(rating).substring(0, 1));
		}
	}

	/**
	 * wx transaction
	 * 
	 * @param type
	 * @return
	 */
	private String buildTransaction(final String type) {
		return (type == null) ? String.valueOf(System.currentTimeMillis())
				: type + System.currentTimeMillis();
	}

	/**
	 * 根据一个网络连接(String)获取bitmap图像
	 * 
	 * @param imageUri
	 * @return Bitmap
	 * @throws MalformedURLException
	 */
	public Bitmap getBitmap(String imageUri) {
		// 显示网络上的图片
		Bitmap bitmap = null;
		try {
			URL myFileUrl = new URL(imageUri);
			HttpURLConnection conn = (HttpURLConnection) myFileUrl
					.openConnection();
			conn.setDoInput(true);
			conn.connect();
			InputStream is = conn.getInputStream();
			bitmap = BitmapFactory.decodeStream(is);
			is.close();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		return bitmap;
	}

}
