package com.jinr.core.security.tradepwd;

import model.SCONF;

import org.apache.http.Header;
import org.json.JSONObject;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.view.ViewGroup;
import android.widget.Button;

import com.jinr.core.R;
import com.jinr.core.config.UrlConfig;
import com.jinr.core.utils.CommonUtil;
import com.jinr.core.utils.LoadingDialog;
import com.jinr.core.utils.MyDES;
import com.jinr.core.utils.MyLog;
import com.jinr.core.utils.MyhttpClient;
import com.jinr.core.utils.PasswordInputView2;
import com.jinr.core.utils.ToastUtil;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/**
 * 旧交易密码验证
 * 
 * @author 52
 * 
 */
public class FrgChgTradePwd_1 extends Fragment implements OnClickListener {

	private PasswordInputView2 txtPwd;
	private Button btnNext;
	private LoadingDialog loadingdialog;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater
				.inflate(R.layout.chg_trade_pwd_1, container, false);

		initData();
		findViewById(view);
		initUI();
		setListener();
		return view;

	}

	private void findViewById(View view) {
		txtPwd = (PasswordInputView2) view.findViewById(R.id.txt_pwd);
		btnNext = (Button) view.findViewById(R.id.btn_next);
	}

	private void setListener() {
		btnNext.setOnClickListener(this);
		btnNext.setClickable(false);
		txtPwd.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int arg1, int arg2,
					int arg3) {
				String str = txtPwd.getText().toString();
				if (str.length() == 6) {
					btnNext.setEnabled(true);
					btnNext.setClickable(true);
					btnNext.setBackgroundResource(R.drawable.btn_bg_select);

					InputMethodManager imm = (InputMethodManager) getActivity()
							.getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(txtPwd.getWindowToken(), 0);
					try {
						chkTradePwd();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else if (str.length() == 5) {
					btnNext.setClickable(false);
					btnNext.setBackgroundResource(R.drawable.btn_blue_light_bg);
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int arg1, int arg2,
					int arg3) {
			}

			@Override
			public void afterTextChanged(Editable arg0) {
			}
		});
	}

	private void initUI() {
		txtPwd.setInputType(InputType.TYPE_CLASS_NUMBER);
	}

	private void initData() {
		loadingdialog = new LoadingDialog(getActivity());
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_next:
			try {
				chkTradePwd();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;

		default:
			break;
		}
	}

	/**
	 * 验证用户输入的原始交易密码
	 * 
	 * @throws Exception
	 */
	private void chkTradePwd() throws Exception {
		String pwd = txtPwd.getText().toString();
		MyLog.d("chkTradePwd ", pwd);
		RequestParams params = new RequestParams();
		// 转JSON
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("uid", ChgTradePwd.instance.user_id);
		jsonObject.put("busspwd", pwd);
		// 加密
		params.put("cipher", MyDES.encrypt(jsonObject.toString()));
		loadingdialog.show();
		MyhttpClient.desPost(UrlConfig.USER_BUSSPWD, params,
				new AsyncHttpResponseHandler() {
					@Override
					public void onFailure(int arg0, Header[] arg1, byte[] arg2,
							Throwable arg3) {
						super.onFailure(arg0, arg1, arg2, arg3);
						loadingdialog.dismiss();
						ToastUtil.show(FrgChgTradePwd_1.this.getActivity(), R.string.default_error);
					}

					@Override
					public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
						super.onSuccess(arg0, arg1, arg2);
						loadingdialog.dismiss();
						try {
							String response = new String(arg2, "UTF-8");
							response = CommonUtil.transResponse(response);
							MyLog.i("serv response", response);
							response = CommonUtil.transResponse(response);
							JSONObject job = new JSONObject(response);
							String cipher = job.getString("cipher");
							String desc = MyDES.decrypt(cipher);
							JSONObject obj = new JSONObject(desc);
							int isSuccess = obj.getInt("code");
							MyLog.d("isSuccess----", String.valueOf(isSuccess));
							if (isSuccess == SCONF.SUCCESS) {// 可以进入到下一步
								ChgTradePwd.instance.gotoStep2();
							} else {
								String msg = obj.getString("msg");
								ToastUtil.show(FrgChgTradePwd_1.this.getActivity(), msg);
								txtPwd.setText("");
							}
						} catch (Exception e) {
						}
					}
				});
	}
}
