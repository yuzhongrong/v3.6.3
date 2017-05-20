package com.jinr.core.security.tradepwd;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.jinr.core.R;
import com.jinr.core.base.BaseActivity;
import com.jinr.core.security.tradepwd.ChgTradePwd;
import com.jinr.core.security.tradepwd.FrgChgTradePwd_1;
import com.jinr.core.security.tradepwd.FrgChgTradePwd_2;
import com.jinr.core.security.tradepwd.FrgChgTradePwd_3;
import com.jinr.core.utils.PreferencesUtils;

/**
 * 设置交易密码
 *
 * @author 640
 */
public class SetTradePwdActivity extends BaseActivity implements OnClickListener {
    public static SetTradePwdActivity instance = null;

    private FragmentManager fragmentManager;
    private FragmentTransaction transaction;
    private FrgSetTradePwd_1 frgSet_1;
    private FrgSetTradePwd_2 frgSet_2;
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
        setContentView(R.layout.activity_set_trade_pwd);
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
        transaction.replace(R.id.step_fragment, frgSet_1);
        transaction.commit();
        title_center_text.setText(R.string.set_deal_passwd);
    }

    @Override
    public void findViewById() {
        title_center_text = (TextView) findViewById(R.id.title_center_text);
        title_left_img = (ImageView) findViewById(R.id.title_left_img);
        title_left_img.setVisibility(View.GONE);
    }

    @Override
    public void initData() {
        user_id = PreferencesUtils.getValueFromSPMap(this, PreferencesUtils.Keys.UID);
        fragmentManager = getSupportFragmentManager();
        transaction = fragmentManager.beginTransaction();
        frgSet_1 = new FrgSetTradePwd_1();
        frgSet_2 = new FrgSetTradePwd_2();
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
        transaction.replace(R.id.step_fragment, frgSet_2);
        transaction.commit();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return false;
        } else if (keyCode == KeyEvent.KEYCODE_DEL) {
            return true;
        } else {
            return false;
        }
    }
}

