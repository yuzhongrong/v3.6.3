package com.jinr.core.security.tradepwd;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.view.ViewGroup;
import android.widget.Button;

import com.jinr.core.R;
import com.jinr.core.utils.PasswordInputView2;

/**
 * 第一次输入交易密码
 * @author 52
 *
 */
public class FrgFindTradePwd_3 extends Fragment implements OnClickListener {
	private PasswordInputView2 txtPwd;
	private Button btnNext;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.find_trade_pwd_3, container,
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

	}

	private void findViewById(View view) {
		txtPwd = (PasswordInputView2) view.findViewById(R.id.txt_pwd);
		btnNext = (Button) view.findViewById(R.id.btn_next);
		btnNext.setClickable(false);
		btnNext.setBackgroundResource(R.drawable.btn_blue_light_bg);
	}

	private void initData() {
		
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_next:
			gotoNext();
			break;

		default:
			break;
		}
	}

	private void gotoNext() {
		String pwd = txtPwd.getText().toString();
		FindTradePwd.instance.pwd_1 = pwd;
		FindTradePwd.instance.gotoStep4();
	}
}
