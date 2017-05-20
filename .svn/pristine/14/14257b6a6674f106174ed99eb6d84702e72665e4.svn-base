package com.jinr.core.security.tradepwd;

import model.SCONF;

import org.apache.http.Header;
import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.telephony.SmsMessage;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;

import com.jinr.core.R;
import com.jinr.core.config.MessageType;
import com.jinr.core.config.UrlConfig;
import com.jinr.core.security.mobile.FrgChgTel_3;
import com.jinr.core.ui.CannotCodeDialog;
import com.jinr.core.ui.CannotReceiveTextDialog;
import com.jinr.core.utils.AutoSmsUtil;
import com.jinr.core.utils.CommonUtil;
import com.jinr.core.utils.LoadingDialog;
import com.jinr.core.utils.MyDES;
import com.jinr.core.utils.MyLog;
import com.jinr.core.utils.MyhttpClient;
import com.jinr.core.utils.PreferencesUtils;
import com.jinr.core.utils.SendMobileCode;
import com.jinr.core.utils.TextAdjustUtil;
import com.jinr.core.utils.SendMobileCode.Back;
import com.jinr.core.utils.ToastUtil;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/**
 * 短信验证
 * 
 * @author 52
 * 
 */
public class FrgFindTradePwd_2 extends Fragment implements OnClickListener {

	private TextView txtTelInfo;
	private TextView txtCaptcha;
	private Button btnNext;
	private Button btnSend;
	private LoadingDialog loadingdialog;
	private String telNo;// 用户手机号
	private String captcha;
	
	// 自动填写验证码
	private BroadcastReceiver smsReceiver;
	private IntentFilter filter;
	private Handler handler;
	private String strContent;
	private TextView no_message;
	private CannotReceiveTextDialog dialog;
	@Override	
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.find_trade_pwd_2, container,
				false);

		initData();
		findViewById(view);
		initUI();
		setListener();

		// 获取短信密码先
		getCaptcha();
		setTelInfo();
		return view;
	}

	private void setTelInfo() {
		txtTelInfo.setText(TextAdjustUtil.getInstance().mobileAdjust(telNo));
	}

	private void setListener() {
		btnNext.setOnClickListener(this);
		btnNext.setClickable(false);
		btnSend.setOnClickListener(this);
		no_message.setOnClickListener(this);
		txtCaptcha.addTextChangedListener(new watcher());
	}

	private void initUI() {
		txtCaptcha.setInputType(InputType.TYPE_CLASS_NUMBER);
		
		// 获取手机验证码自动填写
		handler = new Handler() {
			public void handleMessage(android.os.Message msg) {
				txtCaptcha.setText(strContent);
				if(!btnNext.isClickable()) {
					btnNext.setClickable(true);
				}
			};
		};
		filter = new IntentFilter();
		filter.addAction("android.provider.Telephony.SMS_RECEIVED");
		filter.setPriority(Integer.MAX_VALUE);
		smsReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				Object[] objs = (Object[]) intent.getExtras().get("pdus");
				for (Object obj : objs) {
					byte[] pdu = (byte[]) obj;
					SmsMessage sms = SmsMessage.createFromPdu(pdu);
					// 短信的内容
					String message = sms.getMessageBody();
					MyLog.d("logo", "message     " + message);
					// 短息的手机号。。+86开头？
					String from = sms.getOriginatingAddress();
					MyLog.d("logo", "from     " + from);
					if (!TextUtils.isEmpty(from)) {
						String code = AutoSmsUtil.getInstance().patternCode(
								message);
						if (!TextUtils.isEmpty(code)) {
							strContent = code;
							handler.sendEmptyMessage(1);
						}
					}
				}
			}
		};
		getActivity().registerReceiver(smsReceiver, filter);
	}
	
	@Override
	public void onDestroyView() {
		super.onDestroyView();
		getActivity().unregisterReceiver(smsReceiver);
	}

	private void findViewById(View view) {
		txtTelInfo = (TextView) view.findViewById(R.id.txt_telno);
		txtCaptcha = (TextView) view.findViewById(R.id.txt_captcha);

		btnNext = (Button) view.findViewById(R.id.btn_next);
		btnSend = (Button) view.findViewById(R.id.btn_send);
		
		no_message = (TextView) view.findViewById(R.id.no_message);
	}

	private void initData() {
		telNo = PreferencesUtils.getValueFromSPMap(getActivity(),
				PreferencesUtils.Keys.TEL);
		FindTradePwd.instance.tel = telNo;
		loadingdialog = new LoadingDialog(getActivity());
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_send:
			getCaptcha();
			break;
		case R.id.btn_next:
			try {
				chkCaptcha();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		case R.id.no_message:
			dialog = new CannotReceiveTextDialog(FrgFindTradePwd_2.this.getActivity());
			//dialog.dialog_message.setText(getResources().getString(R.string.message_content));
			dialog.close_img.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					dialog.dismiss();
				}
			});
			dialog.setCancelable();
			dialog.show();
			break;
		default:
			break;
		}
	}

	/**
	 * 验证短信是否正确
	 */
	private void chkCaptcha() throws Exception {
		MyLog.d("chkCaptcha--------", "-----###");
		captcha = txtCaptcha.getText().toString();
		RequestParams params = new RequestParams();
		//转JSON
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("tel", telNo);
		jsonObject.put("code", captcha);
		//加密
		params.put("cipher", MyDES.encrypt(jsonObject.toString()));
		loadingdialog.show();
		MyhttpClient.desPost(UrlConfig.SMS_VERIFYUSER, params,
				new AsyncHttpResponseHandler() {
					@Override
					public void onFailure(int arg0, Header[] arg1, byte[] arg2,
							Throwable arg3) {
						super.onFailure(arg0, arg1, arg2, arg3);
						loadingdialog.dismiss();
						ToastUtil.show(FrgFindTradePwd_2.this.getActivity(), R.string.default_error);
					}

					@Override
					public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
						super.onSuccess(arg0, arg1, arg2);
						loadingdialog.dismiss();
						try {
							String response = new String(arg2, "UTF-8");
							response = CommonUtil.transResponse(response);
							MyLog.i("serv response", response);
							response=CommonUtil.transResponse(response);
							MyLog.e("response", response);
							JSONObject job = new JSONObject(response);
							String cipher = job.getString("cipher");
							String desc = MyDES.decrypt(cipher);
							JSONObject obj = new JSONObject(desc);
							int isSuccess = obj.getInt("code");
							MyLog.d("isSuccess----", String.valueOf(isSuccess));
							if (isSuccess == SCONF.SUCCESS) {// 可以进入到下一步
								FindTradePwd.instance.captcha = captcha;
								FindTradePwd.instance.gotoStep3();
							} else {
								String msg = obj.getString("msg");
								ToastUtil.show(FrgFindTradePwd_2.this.getActivity(), msg);
							}
						} catch (Exception e) {
						}
					}
				});
	}


	/**
	 * 获取短信码
	 */
	private void getCaptcha() {
		SendMobileCode.getInstance().send_code(btnSend, getActivity(), telNo,
				MessageType.MESSAGE_MOBILE_ZHJYMM, null, new Back() {
					@Override
					public void sms(String result) {
						if (result != null && "".equals(result) != true) {
							MyLog.i("sys", result + "aaaaaaaaaaaaaaaa");
						}
					}
				});
	}

	private class watcher implements TextWatcher {
		@Override
		public void afterTextChanged(Editable s) {

		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {

		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			String str = txtCaptcha.getText().toString();
			if (str.length() == 6) {
				btnNext.setEnabled(true);
				btnNext.setClickable(true);
				btnNext.setBackgroundResource(R.drawable.btn_bg_select);

				InputMethodManager imm = (InputMethodManager) getActivity()
						.getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(txtCaptcha.getWindowToken(), 0);
			} else {
				btnNext.setEnabled(true);
				btnNext.setClickable(false);
				btnNext.setBackgroundResource(R.drawable.btn_blue_light_bg);
			}
		}
	}
}
