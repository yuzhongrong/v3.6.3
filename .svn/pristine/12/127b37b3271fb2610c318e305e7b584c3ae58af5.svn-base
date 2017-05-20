package com.jinr.core.security.tradepwd;

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
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
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

import org.apache.http.Header;
import org.json.JSONObject;

import model.SCONF;

/**
 * 验证用户新交易密码第二次
 * @author 52
 *
 */
public class FrgChgTradePwd_3 extends Fragment implements OnClickListener {
	private PasswordInputView2 txtPwd;
	private Button btnSure;
	private LoadingDialog loadingdialog;
	private Button btn_notice;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater
				.inflate(R.layout.chg_trade_pwd_3, container, false);

		initData();
		findViewById(view);
		initUI();
		setListener();
		return view;

	}

	private void findViewById(View view) {
		txtPwd = (PasswordInputView2) view.findViewById(R.id.txt_pwd);
		btnSure = (Button) view.findViewById(R.id.btn_sure);
		btn_notice = (Button) view.findViewById(R.id.btn_notice);
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
					sureChg();
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
		txtPwd.setInputType(InputType.TYPE_CLASS_NUMBER);

		InputMethodManager imm = (InputMethodManager) getActivity()
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(txtPwd.getWindowToken(), 0);
	}

	private void initData() {
		loadingdialog = new LoadingDialog(getActivity());
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_sure:
			sureChg();
			break;

		default:
			break;
		}
	}

	private void sureChg() {
		String pwd = txtPwd.getText().toString();
		if (ChgTradePwd.instance.pwd_1.equals(pwd)) {
			try {
				send2serv();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			//ToastUtil.show(getActivity(), R.string.passwd_equal_checked);
			btn_notice.setVisibility(View.VISIBLE);
			btn_notice.setText(R.string.passwd_equal_checked);
			Animation anim = AnimationUtils.loadAnimation(
					FrgChgTradePwd_3.this.getActivity(),
					R.anim.top_in);
			anim.setFillAfter(true);
			btn_notice.startAnimation(anim);
			anim.setAnimationListener(new AnimationListener() {
				@Override
				public void onAnimationStart(
						Animation animation) {

				}

				@Override
				public void onAnimationRepeat(
						Animation animation) {

				}

				@Override
				public void onAnimationEnd(
						Animation animation) {
					btn_notice.clearAnimation();
					Animation anim = AnimationUtils
							.loadAnimation(
									FrgChgTradePwd_3.this
											.getActivity(),
									R.anim.top_out);
					anim.setStartOffset(5000);
					btn_notice.startAnimation(anim);
					anim.setAnimationListener(new AnimationListener() {

						@Override
						public void onAnimationStart(
								Animation animation) {
						}

						@Override
						public void onAnimationRepeat(
								Animation animation) {
						}

						@Override
						public void onAnimationEnd(
								Animation animation) {
							btn_notice.clearAnimation();
							btn_notice
									.setVisibility(View.GONE);
						}
					});
				}
			});
		}
	}

	private void send2serv() throws Exception {
		String pwd = txtPwd.getText().toString();
		RequestParams params = new RequestParams();
		//转JSON
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("uid", ChgTradePwd.instance.user_id);
		jsonObject.put("jypwd", pwd);
		//加密
		params.put("cipher", MyDES.encrypt(jsonObject.toString()));
		loadingdialog.show();
		MyhttpClient.desPost(UrlConfig.YONHU_SETJYPWD, params,
				new AsyncHttpResponseHandler() {
					@Override
					public void onFailure(int arg0, Header[] arg1, byte[] arg2,
							Throwable arg3) {
						super.onFailure(arg0, arg1, arg2, arg3);
						loadingdialog.dismiss();
						ToastUtil.show(FrgChgTradePwd_3.this.getActivity(), R.string.default_error);
					}

					@Override
					public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
						super.onSuccess(arg0, arg1, arg2);
						loadingdialog.dismiss();
						try {
							String response = new String(arg2, "UTF-8");
							response = CommonUtil.transResponse(response);
							MyLog.i("response---", response);
							//{"code":1000,"data":1}
							response=CommonUtil.transResponse(response);
							JSONObject job = new JSONObject(response);
							String cipher = job.getString("cipher");
							String desc = MyDES.decrypt(cipher);
							JSONObject obj = new JSONObject(desc);
							int isSuccess = obj.getInt("code");
							MyLog.d("isSuccess----", String.valueOf(isSuccess));
							if (isSuccess == SCONF.SUCCESS) {
								FrgChgTradePwd_3.this.getActivity().finish();
								ToastUtil.show(FrgChgTradePwd_3.this.getActivity(),"修改成功");
							} else {
								String msg = job.getString("msg");
								//ToastUtil.show(getActivity(), msg);
								btn_notice.setVisibility(View.VISIBLE);
								btn_notice.setText(msg);
								Animation anim = AnimationUtils.loadAnimation(
										FrgChgTradePwd_3.this.getActivity(),
										R.anim.top_in);
								anim.setFillAfter(true);
								btn_notice.startAnimation(anim);
								anim.setAnimationListener(new AnimationListener() {
									@Override
									public void onAnimationStart(
											Animation animation) {

									}

									@Override
									public void onAnimationRepeat(
											Animation animation) {

									}

									@Override
									public void onAnimationEnd(
											Animation animation) {
										btn_notice.clearAnimation();
										Animation anim = AnimationUtils
												.loadAnimation(
														FrgChgTradePwd_3.this
																.getActivity(),
														R.anim.top_out);
										anim.setStartOffset(5000);
										btn_notice.startAnimation(anim);
										anim.setAnimationListener(new AnimationListener() {

											@Override
											public void onAnimationStart(
													Animation animation) {
											}

											@Override
											public void onAnimationRepeat(
													Animation animation) {
											}

											@Override
											public void onAnimationEnd(
													Animation animation) {
												btn_notice.clearAnimation();
												btn_notice
														.setVisibility(View.GONE);
											}
										});
									}
								});
							}
						} catch (Exception e) {
						}
					}
				});
	}

}
