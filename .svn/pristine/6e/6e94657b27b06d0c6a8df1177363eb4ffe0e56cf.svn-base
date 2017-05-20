package com.jinr.core.security.tradepwd;

import model.SCONF;

import org.apache.http.Header;
import org.json.JSONObject;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.test.TouchUtils;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
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
 * 最后输入交易密码
 * @author 52
 *
 */
public class FrgFindTradePwd_4 extends Fragment implements OnClickListener {
	private PasswordInputView2 txtPwd;
	private Button btnSure;
	private LoadingDialog loadingdialog;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.find_trade_pwd_4, container,
				false);

		initData();
		findViewById(view);
		initUI();
		setListener();
		return view;

	}

	private void setListener() {
		btnSure.setOnClickListener(this);
		btnSure.setClickable(false);
		txtPwd.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int arg1, int arg2,
					int arg3) {
				String str = txtPwd.getText().toString();
				if (str.length() == 6) {
					btnSure.setEnabled(true);
					btnSure.setClickable(true);
					btnSure.setBackgroundResource(R.drawable.btn_bg_select);

					InputMethodManager imm = (InputMethodManager) getActivity()
							.getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(txtPwd.getWindowToken(), 0);
				} else if (str.length() == 5) {
					btnSure.setClickable(false);
					btnSure.setBackgroundResource(R.drawable.btn_blue_light_bg);
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

	}

	private void findViewById(View view) {
		txtPwd = (PasswordInputView2) view.findViewById(R.id.txt_pwd);
		btnSure = (Button) view.findViewById(R.id.btn_sure);
	}

	private void initData() {
		loadingdialog = new LoadingDialog(getActivity());
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_sure:
			try {
				chkAllInfo();
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
	 * 验证所有用户输入信息，并请求后端
	 */
	private void chkAllInfo() throws Exception {
		String pwd = txtPwd.getText().toString();
		if (!pwd.equals(FindTradePwd.instance.pwd_1)) {
			ToastUtil.show(getActivity(), R.string.passwd_equal_checked);
			return;
		}

		// 发送请求
		RequestParams params = new RequestParams();
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("card_id", FindTradePwd.instance.cid);
		jsonObject.put("trans_pwd", FindTradePwd.instance.pwd_1);
		jsonObject.put("uid", FindTradePwd.instance.user_id);
		
		//加密
		params.put("cipher", MyDES.encrypt(jsonObject.toString()));	
//		System.out.println("params------------------- "+params);
		loadingdialog.show();
		MyhttpClient.desPost(UrlConfig.YONGHU_FINDPWD, params,
				new AsyncHttpResponseHandler() {
					@Override
					public void onFailure(int arg0, Header[] arg1, byte[] arg2,
							Throwable arg3) {
						super.onFailure(arg0, arg1, arg2, arg3);
						loadingdialog.dismiss();
						ToastUtil.show(FrgFindTradePwd_4.this.getActivity(), R.string.default_error);
					}

					@Override
					public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
						super.onSuccess(arg0, arg1, arg2);
						loadingdialog.dismiss();
						try {
							String response = new String(arg2, "UTF-8");
							response = CommonUtil.transResponse(response);
							MyLog.i("response---", response);
							// {"code":1000,"data":1}
							response=CommonUtil.transResponse(response);
							JSONObject job = new JSONObject(response);
							String cipher = job.getString("cipher");
							String desc = MyDES.decrypt(cipher);
							JSONObject obj = new JSONObject(desc);
							int isSuccess = obj.getInt("code");
							MyLog.d("isSuccess----", String.valueOf(isSuccess));
							if (isSuccess == 1000) {
								FrgFindTradePwd_4.this.getActivity().finish();
								ToastUtil.show(FrgFindTradePwd_4.this.getActivity(), "修改成功");
							}else {
								String msg = obj.getString("msg");
								ToastUtil.show(FrgFindTradePwd_4.this.getActivity(), msg);
							}
						} catch (Exception e) {
						}
					}
				});

	}
}
