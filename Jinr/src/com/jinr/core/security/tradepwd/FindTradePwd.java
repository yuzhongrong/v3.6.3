package com.jinr.core.security.tradepwd;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.jinr.core.R;
import com.jinr.core.base.BaseActivity;
import com.jinr.core.utils.PreferencesUtils;

/**
 * 找回交易密码
 *
 * @author 52
 */
public class FindTradePwd extends BaseActivity implements OnClickListener {
    public static FindTradePwd instance = null;

    private FrgFindTradePwd_1 find_1;
    private FrgFindTradePwd_2 find_2;
    private FrgFindTradePwd_3 find_3;
    private FrgFindTradePwd_4 find_4;

    private FragmentManager fragmentManager;
    private FragmentTransaction transaction;

    private ImageView title_left_img; // title左边图片
    private TextView title_center_text; // title标题

    public String user_id = "";

    public String tel = ""; //用户手机号
    public String cid = ""; //用户身份证号
    public String pwd_1 = "";//保存用户第一次输入的密码
    public String captcha = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instance = this;
        setContentView(R.layout.aty_find_trade_pwd);

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
        transaction.replace(R.id.step_fragment, find_1);
        transaction.commit();

        title_center_text.setText(R.string.find_deal_passwd);
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
        find_1 = new FrgFindTradePwd_1();
        find_2 = new FrgFindTradePwd_2();
        find_3 = new FrgFindTradePwd_3();
        find_4 = new FrgFindTradePwd_4();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_left_img:
                finish();
        }
    }

    // ================================================

    /**
     * 短信验证
     */
    public void gotoStep2() {
        transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.step_fragment, find_2);
        transaction.commit();
    }

    /**
     * 用户第一次输入交易密码
     */
    public void gotoStep3() {
        transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.step_fragment, find_3);
        transaction.commit();
    }

    public void gotoStep4() {
        transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.step_fragment, find_4);
        transaction.commit();
    }

    @Override
    public void onPause() {
        super.onPause();
        final View v = getWindow().peekDecorView();
        if (v != null && v.getWindowToken() != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
    }
}
