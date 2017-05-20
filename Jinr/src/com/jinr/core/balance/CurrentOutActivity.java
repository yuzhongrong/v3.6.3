package com.jinr.core.balance;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jinr.core.JinrApp;
import com.jinr.core.R;
import com.jinr.core.bankCard.AddBankActivity;
import com.jinr.core.bankCard.ChooseCityActivity;
import com.jinr.core.bankCard.SecondBandCardActivity;
import com.jinr.core.base.BaseActivity;
import com.jinr.core.config.EventBusKey;
import com.jinr.core.config.UrlConfig;
import com.jinr.core.more.CommonWebActivity;
import com.jinr.core.security.tradepwd.SetTradePwdActivity;
import com.jinr.core.ui.NewCustomDialogNoTitleFinish;
import com.jinr.core.utils.CommonUtil;
import com.jinr.core.utils.PreferencesUtils;

import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;

public class CurrentOutActivity extends BaseActivity implements OnClickListener {

    private TextView title_center_text;
    private TextView tv_protocol;
    private ImageView title_left_img;
    private TextView tv_out_balance;
    private TextView tv_out_bank;
    private TextView tv_bule_line;

    private int screenWidth;
    private int tv_balance_length;//点击余额下划线长度
    private int tv_bank_length;//点击银行卡下划线长度
    private ObjectAnimator animator_balance;//点击余额下划线动画
    private ObjectAnimator animator_bank;//点击银行卡下划线动画
    private LinearLayout.LayoutParams layoutParams_bank;//点击银行卡时设置
    private LinearLayout.LayoutParams layoutParam_bal;//点击银行卡下划线长度
    private FragmentManager fragmentManager;
    private CurOutBalFragment balFragment;
    private CurOutBankFragment bankFragment;
    private Fragment currentFragment;

    private NewCustomDialogNoTitleFinish dialog_bind_card;
    private NewCustomDialogNoTitleFinish dialog_set_passwd;
    private boolean has_city; // 城市是否为空
    private NewCustomDialogNoTitleFinish dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_currentout);
        findViewById();
        setListener();
        initData();
    }

    @Override
    protected void initData() {
        initBuleLine();
        initFragment();
    }

    private void initFragment() {
        fragmentManager = getSupportFragmentManager();
        balFragment = new CurOutBalFragment();
        bankFragment = new CurOutBankFragment();
        balFragment.setUserVisibleHint(true);
        showFragment(balFragment);
    }

    private void showFragment(Fragment fragment) {
        if (fragment != currentFragment) {
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            if (currentFragment != null) {
                transaction.hide(currentFragment);
            }
            currentFragment = fragment;
            if (!fragment.isAdded()) {
                transaction.add(R.id.frag_container, fragment).show(fragment).commit();
            } else {
                transaction.show(fragment).commit();
            }
        }
    }

    @Override
    protected void findViewById() {
        //EventBus.getDefault().register(this);
        title_center_text = (TextView) findViewById(R.id.title_center_text);
        tv_protocol = (TextView) findViewById(R.id.tv_protocol);
        title_left_img = (ImageView) findViewById(R.id.title_left_img);
        tv_out_balance = (TextView) findViewById(R.id.tv_out_balance);
        tv_out_bank = (TextView) findViewById(R.id.tv_out_bank);
        tv_bule_line = (TextView) findViewById(R.id.tv_bule_line);
        tv_protocol.setVisibility(View.VISIBLE);
        tv_protocol.setText("转出说明");
        title_center_text.setText("活期转出");
        has_city = getIntent().getBooleanExtra("has_city", true);
        initDialog();
    }

    @Override
    protected void initUI() {
    }

    @Override
    protected void setListener() {
        title_left_img.setOnClickListener(this);
        tv_protocol.setOnClickListener(this);
        tv_out_balance.setOnClickListener(this);
        tv_out_bank.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        showDialog();
    }

    /**
     * 绑定银行卡及交易密码设置弹窗
     */
    private void showDialog() {
        if (JinrApp.instance().state_bankBind == 0) {
            dialog_bind_card.show();
        } else {
            if (JinrApp.instance().state_tradePassword == 0) {
                dialog_set_passwd.show();
            }
        }
        if (!has_city) {
            showCityDialog();
        }
    }

    private void initDialog() {
        dialog_bind_card = new NewCustomDialogNoTitleFinish(this, this.getResources().getString(R.string.bind_card_notice));// 绑定银行卡提示
        dialog_bind_card.btn_custom_dialog_sure.setText(this.getResources().getString(R.string.set_now_btn));
        dialog_bind_card.btn_custom_dialog_sure.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog_bind_card.dismiss();
                if (!PreferencesUtils.getValueFromSPMap(CurrentOutActivity.this, PreferencesUtils.Keys.NAME).equals("")
                        && !PreferencesUtils.getValueFromSPMap(CurrentOutActivity.this, PreferencesUtils.Keys.ID_CARD).equals("")) {
                    Intent intent = new Intent(CurrentOutActivity.this, SecondBandCardActivity.class);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(CurrentOutActivity.this, AddBankActivity.class);
                    startActivity(intent);
                }
            }
        });
        dialog_set_passwd = new NewCustomDialogNoTitleFinish(this, this.getResources().getString(R.string.set_password_notice));
        dialog_set_passwd.btn_custom_dialog_sure.setText(this.getResources().getString(R.string.set_now_btn));
        dialog_set_passwd.btn_custom_dialog_sure.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog_set_passwd.dismiss();
                Intent intent = new Intent(CurrentOutActivity.this, SetTradePwdActivity.class);
                CurrentOutActivity.this.startActivity(intent);
            }
        });
    }

    private void initBuleLine() {
        Paint paint = new Paint();
        paint.setTextSize(tv_out_balance.getTextSize());
        tv_balance_length = (int) paint.measureText(tv_out_balance.getText().toString());
        paint.setTextSize(tv_out_bank.getTextSize());
        tv_bank_length = (int) paint.measureText(tv_out_bank.getText().toString());
        tv_bule_line.setWidth(tv_balance_length);
        screenWidth = CommonUtil.getScreenWidth(this) / 4;
        int length = tv_balance_length / 2;
        layoutParam_bal = new LinearLayout.LayoutParams(tv_balance_length, 5);
        layoutParams_bank = new LinearLayout.LayoutParams(tv_bank_length, 5);
        layoutParam_bal.setMargins(screenWidth - length, 0, 0, 0);
        tv_bule_line.setLayoutParams(layoutParam_bal);
        animator_balance = ObjectAnimator.ofFloat(tv_bule_line, "translationX", screenWidth - length);
        animator_bank = ObjectAnimator.ofFloat(tv_bule_line, "translationX", screenWidth - length, screenWidth * 3 - tv_bank_length / 2);
    }


    private void showCityDialog() {
        dialog = new NewCustomDialogNoTitleFinish(this, "到“选择开户城市”页面补充开户城市信息，将会加快提现到账速度");
        dialog.btn_custom_dialog_sure.setText("去补充");
        dialog.btn_custom_dialog_sure.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CurrentOutActivity.this, ChooseCityActivity.class);
                startActivity(intent);
            }
        });
        dialog.show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_left_img:
                CurrentOutActivity.this.finish();
                break;

            case R.id.tv_protocol:
                Intent intent_bal = new Intent(CurrentOutActivity.this, CommonWebActivity.class);
                intent_bal.putExtra("url", UrlConfig.IP_V+ UrlConfig.CURRENT_EXPLAIN);
                intent_bal.putExtra("titleName", "转出说明");
                startActivity(intent_bal);
                break;

            case R.id.tv_out_balance:
                resetTxtColor(0x001);
                break;

            case R.id.tv_out_bank:
                resetTxtColor(0x002);
                break;

            default:
                break;
        }
    }

    private void resetTxtColor(int index) {
        switch (index) {
            case 0x001:
                animator_balance.start();
                tv_bule_line.setLayoutParams(layoutParam_bal);
                tv_out_balance.setTextColor(getResources().getColor(R.color.main_blue_color));
                tv_out_bank.setTextColor(getResources().getColor(R.color.gray_txt_bg));
                balFragment.setUserVisibleHint(true);
                showFragment(balFragment);
                break;

            case 0x002:
                animator_bank.start();
                layoutParam_bal.setMargins(0, 0, 0, 0);
                tv_bule_line.setLayoutParams(layoutParams_bank);
                tv_out_bank.setTextColor(getResources().getColor(R.color.main_blue_color));
                tv_out_balance.setTextColor(getResources().getColor(R.color.gray_txt_bg));
                bankFragment.setUserVisibleHint(true);
                showFragment(bankFragment);
                break;

            default:
                break;
        }
    }

    // 添加城市成功后关闭页面
    @Subscriber(tag = EventBusKey.ADD_CITY_SUCCESD)
    public void dissmissDialog(boolean isChoose) {
        if (isChoose) {
            has_city = true;
            dialog.dismiss();
        }
    }
}
