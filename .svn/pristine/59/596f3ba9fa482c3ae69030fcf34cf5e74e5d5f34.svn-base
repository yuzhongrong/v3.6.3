package com.jinr.core.security.mobile;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.jinr.core.R;
import com.jinr.core.base.BaseActivity;

/**
 * 修改手机号, 确认登录密码
 *
 * @author 52
 */
public class ChangeMobileStep1 extends BaseActivity implements OnClickListener {

    public static ChangeMobileStep1 instance = null;

    private FragmentManager fragmentManager;
    private FragmentTransaction transaction;
    private FrgChgTel_1 frgChgTel_1;
    private FrgChgTel_2 frgChgTel_2;
    private FrgChgTel_3 frgChgTel_3;

    private ImageView title_left_img; // title左边图片
    private TextView title_center_text; // title标题

    public String logPwd;

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
    protected void setListener() {
        title_left_img.setOnClickListener(this);
    }

    @Override
    protected void findViewById() {
        title_center_text = (TextView) findViewById(R.id.title_center_text);
        title_left_img = (ImageView) findViewById(R.id.title_left_img);
    }

    @Override
    protected void initUI() {
        transaction.replace(R.id.step_fragment, frgChgTel_1);
        transaction.commit();
        title_center_text.setText(R.string.change_bind_mobile);
    }

    @Override
    protected void initData() {
        fragmentManager = getSupportFragmentManager();
        transaction = fragmentManager.beginTransaction();
        frgChgTel_1 = new FrgChgTel_1();
        frgChgTel_2 = new FrgChgTel_2();
        frgChgTel_3 = new FrgChgTel_3();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_left_img:
                finish();
        }
    }

    // =======================================================
    public void getCaptcha(String tel) {
        transaction = fragmentManager.beginTransaction();
        Bundle bundle = new Bundle();
        bundle.putString("tel", tel);
        frgChgTel_3.setArguments(bundle);
        transaction.replace(R.id.step_fragment, frgChgTel_3);
        transaction.commit();
    }

    public void writeNewTel() {
        transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.step_fragment, frgChgTel_2);
        transaction.commit();
    }
}
