package com.jinr.core.news;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.jinr.core.R;
import com.jinr.core.base.BaseFrgmtActivity;
import com.jinr.core.config.EventBusKey;
import com.jinr.core.utils.PreferencesUtils;

import org.simple.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * 活动页面，跳转是经过定位后的回调再跳转到这个页面的 @author Ysw created at 2017/3/14 16:37
 */
public class NewsCenterActivity extends BaseFrgmtActivity implements OnClickListener {
    private TextView mTitle;
    private ImageView title_left_back;
    private List<Fragment> mPagerList;
    private ActNewsFragment mActNewsFragment;
    private SysNewsFragment mSysNewsFragment;
    private FragmentManager fragmentManager;
    private FragmentTransaction transaction;
    public ArrayList<String> ActList;
    public ArrayList<String> SystemList;
    private TextView act_title,notice_title;
    private String latitude;
    private String longitude;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_news_center);
        initData();
        findViewById();
        setListener();
        initUI();
    }
    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, R.anim.right_out);
    }
    protected void initData() {
        latitude = getIntent().getStringExtra("lat");
        longitude = getIntent().getStringExtra("lng");
        mPagerList = new ArrayList<>();
        fragmentManager = getSupportFragmentManager();
        transaction = fragmentManager.beginTransaction();
        mActNewsFragment = new ActNewsFragment();
        Bundle bundle=new Bundle();
        bundle.putString("lat", latitude);
        bundle.putString("lng", longitude);
        mActNewsFragment.setArguments(bundle);
        mSysNewsFragment = new SysNewsFragment();
        ActList = new ArrayList<>();
        SystemList = new ArrayList<>();
        mPagerList.add(mActNewsFragment);
        mPagerList.add(mSysNewsFragment);
        //EventBus.getDefault().register(this);
    }

    protected void setListener() {
        act_title.setOnClickListener(this);
        notice_title.setOnClickListener(this);
    }

    private void doCheck(int i) {
        if (i==0){
            act_title.setTextColor(getResources().getColor(R.color.blue_btn_bg));
            notice_title.setTextColor(getResources().getColor(R.color.title_security));
        }else if (i==1){
            act_title.setTextColor(getResources().getColor(R.color.title_security));
            notice_title.setTextColor(getResources().getColor(R.color.blue_btn_bg));
            HashSet hashSet=PreferencesUtils.getUnReadFormSPMap(NewsCenterActivity.this,PreferencesUtils.Keys.UNREAD_SYS_NEWS);
            if (!hashSet.isEmpty()) {
                PreferencesUtils.UnReadChangeToSPMap(NewsCenterActivity.this);
                EventBus.getDefault().post(false, EventBusKey.LOGO_SYS_REDPOINT);
            }
        }
        transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.container, mPagerList.get(i));
        transaction.commit();
    }

    protected void findViewById() {
        title_left_back = (ImageView) findViewById(R.id.title_left_img);
        title_left_back.setOnClickListener(this);
        act_title= (TextView) findViewById(R.id.act_title);
        notice_title = (TextView) findViewById(R.id.notice_title);
    }

    @Override
    protected void initUI() {
       doCheck(0);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.act_title:
                doCheck(0);
                break;
            case R.id.notice_title:
                doCheck(1);
                break;
            case R.id.title_left_img:
                finish();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}