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
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;

import com.jinr.core.R;
import com.jinr.core.config.UrlConfig;
import com.jinr.core.utils.CommonUtil;
import com.jinr.core.utils.LoadingDialog;
import com.jinr.core.utils.MyDES;
import com.jinr.core.utils.MyLog;
import com.jinr.core.utils.MyhttpClient;
import com.jinr.core.utils.ToastUtil;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/**
 * 身份证证验证
 * 
 * @author 52
 * 
 */
public class FrgFindTradePwd_1 extends Fragment implements OnClickListener {

	private TextView txtCid;
	private Button btnNext;
	private LoadingDialog loadingdialog;
	private String cid;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.find_trade_pwd_1, container,
				false);

		initData();
		findViewById(view);
		initUI();
		setListener();
		return view;

	}

	private void setListener() {
		btnNext.setOnClickListener(this);
		btnNext.setClickable(false);
		txtCid.addTextChangedListener(new watcher());
	}

	private void initUI() {
		// txtCid.setInputType(InputType.TYPE_CLASS_NUMBER);
	}

	private void findViewById(View view) {
		txtCid = (TextView) view.findViewById(R.id.txt_cid);
		btnNext = (Button) view.findViewById(R.id.btn_next);
	}
	
	private void initData() {
		loadingdialog = new LoadingDialog(getActivity());
	}

	// =========================================
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
			String str = txtCid.getText().toString();
			if (str.length() == 15) {
				btnNext.setClickable(true);
				btnNext.setBackgroundResource(R.drawable.btn_bg_select);

				// InputMethodManager imm = (InputMethodManager) getActivity()
				// .getSystemService(Context.INPUT_METHOD_SERVICE);
				// imm.hideSoftInputFromWindow(txtCid.getWindowToken(), 0);
			} else if (str.length() == 14) {
				btnNext.setClickable(false);
				btnNext.setBackgroundResource(R.drawable.btn_blue_light_bg);
			}
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_next:
			if (txtCid.getText().toString().equals("")) {
				btnNext.setClickable(false);
				btnNext.setBackgroundResource(R.drawable.btn_blue_light_bg);
			} else {
				try {
					chkCid();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			break;

		default:
			break;
		}
	}


	/**
	 * 验证用户输入的身份证号
	 * @throws Exception 
	 */
	private void chkCid() throws Exception {
		cid = txtCid.getText().toString();
		RequestParams params = new RequestParams();
		//转JSON
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("uid", FindTradePwd.instance.user_id);
		jsonObject.put("card_id", cid);
		//加密
		params.put("cipher", MyDES.encrypt(jsonObject.toString()));
		loadingdialog.show();
		MyhttpClient.desPost(UrlConfig.YONGHU_CARD, params,
				new AsyncHttpResponseHandler() {
					@Override
					public void onFailure(int arg0, Header[] arg1, byte[] arg2,
							Throwable arg3) {
						super.onFailure(arg0, arg1, arg2, arg3);
						loadingdialog.dismiss();
						ToastUtil.show(FrgFindTradePwd_1.this.getActivity(), R.string.default_error);
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
							JSONObject job = new JSONObject(response);
							String cipher = job.getString("cipher");
							String desc = MyDES.decrypt(cipher);
							JSONObject obj = new JSONObject(desc);
							int isSuccess = obj.getInt("code");
							MyLog.d("isSuccess----", String.valueOf(isSuccess));
							if (isSuccess == SCONF.SUCCESS) {// 可以进入到下一步
								FindTradePwd.instance.cid = cid;
								FindTradePwd.instance.gotoStep2();
							} else {
								String msg = obj.getString("msg");
								ToastUtil.show(FrgFindTradePwd_1.this.getActivity(), msg);
							}
						} catch (Exception e) {
						}
					}
				});
	}
}
