package com.jinr.core.dayup;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.jinr.core.R;

/**
 * Created by： Ysw on 2016/8/22.16:57.
 */
public class DayupTurnoutDialog extends Dialog implements
		android.view.View.OnClickListener {

	private onTurnoutDialogClickLisenter mTurnoutDialogClickLisenter;
	private TextView tv_name;
	private TextView tv_money;
	private RelativeLayout rl_sure;

	public DayupTurnoutDialog(Context context) {
		super(context);
		initView(context);
	}
	
	  public DayupTurnoutDialog(Context context, int theme) {  
	        super(context, theme);  
	    }  

	public interface TurnoutDialogConstant {
		public final static int CANCEL = 0;
		public final static int SURE = 1;
	}

	public interface onTurnoutDialogClickLisenter {
		void onTurnoutDialogClick(int num);
	}

	public void setOnDayuoDialogClickLisenter(
			onTurnoutDialogClickLisenter lisenter) {
		this.mTurnoutDialogClickLisenter = lisenter;
	}

	@SuppressLint("InflateParams")
	private void initView(Context context) {
		super.setCancelable(true);
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.dialog_dayupturnout, null);
		tv_name = (TextView) layout.findViewById(R.id.tv_name);
		tv_money = (TextView) layout.findViewById(R.id.tv_money);
		rl_sure = (RelativeLayout) layout.findViewById(R.id.rl_sure);
		rl_sure.setOnClickListener(this);
		super.addContentView(layout, new ViewGroup.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.WRAP_CONTENT));
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		this.dismiss();
	}

	public void setName(String name) {
		tv_name.setText(name);
	}

	public void setMoney(String money) {
		tv_money.setText(money);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.rl_sure:
			mTurnoutDialogClickLisenter.onTurnoutDialogClick(0);
			DayupTurnoutDialog.this.dismiss();
			break;

		default:
			break;
		}
	}
}
