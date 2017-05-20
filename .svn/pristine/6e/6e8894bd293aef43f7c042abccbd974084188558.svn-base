package com.jinr.core.bankCard;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jinr.core.R;

/**
 * Created by: Ysw on 2016/11/17.
 */

public class PhoneMessageView extends LinearLayout {
	private Context mContext;
	private TextView tv_one;
	private TextView tv_two;
	private TextView tv_three;
	private TextView tv_four;
	private TextView tv_five;
	private TextView tv_six;
	private List<TextView> mTextViewList;

	public PhoneMessageView(Context context) {
		super(context);
		this.mContext = context;
		initView(mContext);
	}

	public PhoneMessageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.mContext = context;
		initView(context);
	}

	private void initView(Context mContext) {
		View view = LayoutInflater.from(mContext).inflate(
				R.layout.phone_message_layout, this);
		tv_one = (TextView) view.findViewById(R.id.et_one);
		tv_two = (TextView) view.findViewById(R.id.et_two);
		tv_three = (TextView) view.findViewById(R.id.et_three);
		tv_four = (TextView) view.findViewById(R.id.et_four);
		tv_five = (TextView) view.findViewById(R.id.et_five);
		tv_six = (TextView) view.findViewById(R.id.et_six);
		mTextViewList = new ArrayList<TextView>();
		mTextViewList.add(tv_one);
		mTextViewList.add(tv_two);
		mTextViewList.add(tv_three);
		mTextViewList.add(tv_four);
		mTextViewList.add(tv_five);
		mTextViewList.add(tv_six);
	}

	public void setText(String text) {
		int length = text.length();
		if (length < 6) {
			for (int i = 0; i < length; i++) {
				mTextViewList.get(i).setText(text.substring(i, i + 1));
			}
			for (int j = 5; j >= length; j--) {
				mTextViewList.get(j).setText("");
			}
		} else {
			for (int i = 0; i < 6; i++) {
				mTextViewList.get(i).setText(text.substring(i, i + 1));
			}
		}
	}

	public String getText() {
		String text = "";
		for (int i = 0; i < 6; i++) {
			text = text + mTextViewList.get(i).getText();
		}
		return text;
	}
}
