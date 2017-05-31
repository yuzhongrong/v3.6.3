package com.jinr.new_mvp.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jinr.core.JinrApp;
import com.jinr.core.MainActivity;
import com.jinr.core.R;
import com.jinr.core.bankCard.AddBankActivity;
import com.jinr.core.bankCard.LeftBankCardActivity;
import com.jinr.core.bankCard.SecondBandCardActivity;
import com.jinr.core.config.AppManager;
import com.jinr.core.config.Check;
import com.jinr.core.security.SecurityCenterActivity;
import com.jinr.core.security.address.AddressListActivity;
import com.jinr.core.security.mobile.ChangeMobileStep1;
import com.jinr.core.ui.NewCustomDialog;
import com.jinr.core.utils.PreferencesUtils;
import com.jinr.core.utils.TextAdjustUtil;
import com.jinr.new_mvp.presenter.SetingPresenter;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.umeng.message.ALIAS_TYPE;
import com.umeng.message.PushAgent;
import com.umeng.message.UTrack;

import org.json.JSONException;


public class SettingActivity extends BaseActivity<SetingPresenter> implements OnClickListener {

    private ImageView title_left_img; // title左边图片
    private TextView title_center_text; // title标题
    private LinearLayout layoutBankCardInfo, layoutNoBankCardInfo, layoutMoble, layoutPassWord;
    private TextView tvBankName, tvBankNo, tvNoBankName, mobleTv;
    private Button btnLogout;
    private ImageView ivBankLogo;
    private String tel;
    private String login_text = "";
    private NewCustomDialog dialog2;
    private RelativeLayout rl_phone;
    private LinearLayout layout_address;

    protected void findViewById() {
        title_left_img = (ImageView) findViewById(R.id.title_left_img);
        title_center_text = (TextView) findViewById(R.id.title_center_text);
        layoutBankCardInfo = (LinearLayout) findViewById(R.id.layout_bank_card_info);
        layoutNoBankCardInfo = (LinearLayout) findViewById(R.id.layout_no_bank_card_info);
        layoutMoble = (LinearLayout) findViewById(R.id.layout_moble);
        layoutPassWord = (LinearLayout) findViewById(R.id.layout_passWord);
        layout_address = (LinearLayout) findViewById(R.id.layout_address);
        tvBankName = (TextView) findViewById(R.id.tv_bank_name);
        rl_phone = (RelativeLayout) findViewById(R.id.rl_phone);
        tvBankNo = (TextView) findViewById(R.id.tv_bank_no);
        tvNoBankName = (TextView) findViewById(R.id.tv_no_bank_name);
        mobleTv = (TextView) findViewById(R.id.moble_tv);
        btnLogout = (Button) findViewById(R.id.btn_logout);
        ivBankLogo = (ImageView) findViewById(R.id.iv_bank_logo);
    }


    protected void initUI() {

        tel = PreferencesUtils.getValueFromSPMap(SettingActivity.this, PreferencesUtils.Keys.TEL);
        if (Check.is_login(MainActivity.instance)) {
            login_text = getResources().getString(R.string.log_out);
        } else {
            login_text = getResources().getString(R.string.log_in);
        }

        btnLogout.setText(login_text);
        title_center_text.setText("设置");
    }

    private void setTitle() {
        if (PreferencesUtils.getIntFromSPMap(this, PreferencesUtils.Keys.IS_BIND_CARD) == 1) {
            layoutBankCardInfo.setVisibility(View.VISIBLE);
            layoutNoBankCardInfo.setVisibility(View.GONE);
            String bank_name = PreferencesUtils.getValueFromSPMap(this, PreferencesUtils.Keys.BANK_NAME);
            tvBankName.setText(bank_name);
            String bank_card_last = PreferencesUtils.getValueFromSPMap(this, PreferencesUtils.Keys.CARD_LAST);
            tvBankNo.setText("(尾号" + bank_card_last + ")");
            String logourl = PreferencesUtils.getValueFromSPMap(this, PreferencesUtils.Keys.BANK_LOGO_URL);
            ImageLoader.getInstance().displayImage(logourl, ivBankLogo);
        } else {
            layoutBankCardInfo.setVisibility(View.GONE);
            layoutNoBankCardInfo.setVisibility(View.VISIBLE);
            tvNoBankName.setText(getResources().getString(R.string.nobankcard));
        }
        mobleTv.setText(TextAdjustUtil.getInstance().mobileAdjust(tel));
    }

    @Override
    protected void onResume() {
        super.onResume();
        setTitle();
    }


    protected void setListener() {
        btnLogout.setOnClickListener(this);
        layoutBankCardInfo.setOnClickListener(this);
        layoutNoBankCardInfo.setOnClickListener(this);
        layoutMoble.setOnClickListener(this);
        layoutPassWord.setOnClickListener(this);
        layout_address.setOnClickListener(this);
        title_left_img.setOnClickListener(this);
        rl_phone.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layout_passWord:
                startActivity(new Intent(SettingActivity.this, SecurityCenterActivity.class));
                break;
            case R.id.layout_address:
                startActivity(new Intent(SettingActivity.this, AddressListActivity.class));
                break;
            case R.id.title_left_img:// title左侧图标
                finish();
                break;
            case R.id.layout_bank_card_info:
                startActivity(new Intent(SettingActivity.this, LeftBankCardActivity.class));
                break;
            case R.id.layout_no_bank_card_info:
                if (!PreferencesUtils.getValueFromSPMap(SettingActivity.this, PreferencesUtils.Keys.NAME).equals("")
                        && !PreferencesUtils.getValueFromSPMap(SettingActivity.this, PreferencesUtils.Keys.ID_CARD).equals("")) {
                    Intent intent = new Intent(SettingActivity.this, SecondBandCardActivity.class);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(SettingActivity.this, AddBankActivity.class);
                    startActivity(intent);
                }
                break;
            case R.id.btn_logout:// 退出登录
                doLogout();
                break;
            case R.id.rl_phone:
                clickChgTel();
                break;
            default:
                break;
        }
    }

    // 跳转到修改手机号码页面 Ysw 2016.09.29
    private void clickChgTel() {
        Intent intent_mobile = new Intent(MainActivity.instance, ChangeMobileStep1.class);
        startActivity(intent_mobile);
    }

    private void doLogout() {
        dialog2 = new NewCustomDialog(SettingActivity.this, getString(R.string.warning), getString(R.string.Are_you_sure));
        dialog2.btn_custom_dialog_sure.setText(getString(R.string.dialog_call_bt_ok));
        dialog2.btn_custom_dialog_cancel.setText(getString(R.string.dialog_call_bt_cancel));
        dialog2.btn_custom_dialog_cancel.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                dialog2.dismiss();
            }
        });
        dialog2.btn_custom_dialog_sure.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog2.dismiss();
                PushAgent mPushAgent = PushAgent.getInstance(SettingActivity.this);
                mPushAgent.removeAlias(PreferencesUtils.getValueFromSPMap(SettingActivity.this, PreferencesUtils.Keys.UID),
                        ALIAS_TYPE.SINA_WEIBO, new UTrack.ICallBack() {
                            @Override
                            public void onMessage(boolean isSuccess, String message) {

                            }
                        });

//                try {
//                    getP().RequestLogout();
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }


                PreferencesUtils.putLastTelToSPMap(PreferencesUtils.Keys.TEL, PreferencesUtils.getValueFromSPMap(SettingActivity.this, PreferencesUtils.Keys.TEL));
                PreferencesUtils.clearSPMap(JinrApp.getInstance().getApplicationContext());
                AppManager.getAppManager().finishAllActivity();

                startActivity(new Intent(SettingActivity.this, MainActivity.class));

            }
        });

        dialog2.show();
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        findViewById();
        initUI();
        setListener();

    }

    @Override
    public int getLayoutId() {
        return  R.layout.activity_left_setting_lay;
    }

    @Override
    public SetingPresenter newP() {
        return new SetingPresenter();
    }
}
