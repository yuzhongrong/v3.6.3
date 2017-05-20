package com.jinr.core.guide.adapter;

import java.util.ArrayList;

import com.jinr.core.MainActivity;
import com.jinr.core.R;
import com.jinr.core.utils.PreferencesUtils;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * @author yangyu 功能描述：ViewPager适配器，用来绑定数据和view
 */
public class ViewPagerAdapter extends PagerAdapter {

	// 界面列表
	private ArrayList<View> views;
	private Activity activity;
	private String version = "";

	public ViewPagerAdapter(ArrayList<View> views, Activity activity) {
		this.views = views;
		this.activity = activity;
		// 获取packagemanager的实例
		PackageManager packageManager = activity.getPackageManager();
		// getPackageName()是你当前类的包名，0代表是获取版本信息
		PackageInfo packInfo;

		try {
			packInfo = packageManager.getPackageInfo(activity.getPackageName(),
					0);

			version = packInfo.versionName;
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 获得当前界面数
	 */
	@Override
	public int getCount() {
		if (views != null) {
			return views.size();
		}
		return 0;
	}

	/**
	 * 初始化position位置的界面
	 */
	@Override
	public Object instantiateItem(View view, int position) {
		((ViewPager) view).addView(views.get(position), 0);
		if (position == views.size() - 1) {
			ImageView mStartWeiboImageButton = (ImageView) view
					.findViewById(R.id.guide_btn);
			TextView version_txt = (TextView) view
					.findViewById(R.id.version_txt);
			version_txt.setText("V" + version);
			mStartWeiboImageButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// 设置已经引导
					setGuided();
					goHome();
				}

			});
		} 
		return views.get(position);
	}

	private void goHome() {
		// 跳转
		Intent intent = new Intent(activity, MainActivity.class);
		activity.startActivity(intent);
		activity.finish();
	}

	/**
	 * 
	 * method desc：设置已经引导过了，下次启动不用再次引导
	 */
	private void setGuided() {
		PreferencesUtils.putMaskBooleanToSPMap(activity,
				PreferencesUtils.Keys.IS_FIRST_PREF, false);
	}

	/**
	 * 判断是否由对象生成界面
	 */
	@Override
	public boolean isViewFromObject(View view, Object arg1) {
		return (view == arg1);
	}

	/**
	 * 销毁position位置的界面
	 */
	@Override
	public void destroyItem(View view, int position, Object arg2) {
		((ViewPager) view).removeView(views.get(position));
	}
}
