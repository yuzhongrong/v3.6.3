package com.jinr.core.security.tradepwd;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.jinr.core.R;
import com.jinr.core.base.BaseActivity;
import com.jinr.core.utils.PreferencesUtils;

/**
 * 修改交易密码
 * @author 52
 *
 */
public class ChgTradePwd extends BaseActivity implements OnClickListener {
	public static ChgTradePwd instance = null;

	private FragmentManager fragmentManager;
	private FragmentTransaction transaction;
	private FrgChgTradePwd_1 frgChg_1;
	private FrgChgTradePwd_2 frgChg_2;
	private FrgChgTradePwd_3 frgChg_3;

	private ImageView title_left_img; // title左边图片
	private TextView title_center_text; // title标题

	public String user_id = "";

	/**
	 * 保存用户第一次输入的密码
	 */
	public String pwd_1 = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		instance = this;
		setContentView(R.layout.aty_chg_tel);

		initData();
		findViewById();
		initUI();
		setListener();
	}

	@Override
	public void setListener() {
		title_left_img.setOnClickListener(this);
	}

	@Override
	public void initUI() {
		transaction.replace(R.id.step_fragment, frgChg_1);
		transaction.commit();

		title_center_text.setText(R.string.set_deal_passwd);
	}

	@Override
	public void findViewById() {
		title_center_text = (TextView) findViewById(R.id.title_center_text);
		title_left_img = (ImageView) findViewById(R.id.title_left_img);
	}

	@Override
	public void initData() {
		user_id = PreferencesUtils.getValueFromSPMap(this,
				PreferencesUtils.Keys.UID);
		fragmentManager = getSupportFragmentManager();
		transaction = fragmentManager.beginTransaction();
		frgChg_1 = new FrgChgTradePwd_1();
		frgChg_2 = new FrgChgTradePwd_2();
		frgChg_3 = new FrgChgTradePwd_3();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.title_left_img:
			finish();
		}
	}

	public void gotoStep2() {
		transaction = fragmentManager.beginTransaction();
		transaction.replace(R.id.step_fragment, frgChg_2);
		transaction.commit();
	}

	public void gotoStep3() {
		transaction = fragmentManager.beginTransaction();
		transaction.replace(R.id.step_fragment, frgChg_3);
		transaction.commit();
	}
}
