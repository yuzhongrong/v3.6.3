/**
 *
 */
package com.jinr.core;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.jinr.core.bankCard.AddBankActivity;
import com.jinr.core.bankCard.SecondBandCardActivity;
import com.jinr.core.base.BaseFragment;
import com.jinr.core.config.Check;
import com.jinr.core.config.EventBusKey;
import com.jinr.core.config.UrlConfig;
import com.jinr.core.gift.BonusCenterActivity;
import com.jinr.core.more.CommonWebActivity;
import com.jinr.core.more.MoreActivity;
import com.jinr.core.news.NewsCenterActivity;
import com.jinr.core.regist.NewLoginActivity;
import com.jinr.core.regular.MyAssetsActivity;
import com.jinr.core.security.SettingActivity;
import com.jinr.core.utils.BaiduUtils;
import com.jinr.core.utils.MyLog;
import com.jinr.core.utils.PreferencesUtils;
import com.jinr.core.utils.TextAdjustUtil;
import com.jinr.core.utils.TimeUtil;
import com.jinr.core.utils.UmUtils;

import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;

/**
 * 侧滑页面 为一个fragment
 *
 * @author CQJ
 */
public class LeftSlidingMenuFragment extends BaseFragment implements OnClickListener {
    public static final String TAG = "LeftSlidingMenuFragment";
    private View toolBtnSec;// 安全中心
    private View toolBtnMore;// 更多
    public View toolBtnGift;// 我的红包
    public View jinrBtnBankCard, jinrBtnAsset, jinrBtnWelfare;// 银行卡
    private TextView titleTextView;
    private TextView titleTextViewTime;
    private TextView bankTextView, redeemCodeTv, platformTv, guaranteeTv;
    public static LeftSlidingMenuFragment leftinstance = null;
    private ImageView img_slid_redgift, img_slid_more;
    private String tel;
    private String name;
    private int state_bankBind;
    private boolean isShowSys = false;
    private String latitude;
    private String longitude;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void findViewById(View view) {

    }

    @Override
    protected void initUI() {

    }

    @Override
    protected void setListener() {

    }

    @Override
    public void onResume() {
        super.onResume();
        setTitle();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.left_menu, container, false);
        EventBus.getDefault().register(this);

        toolBtnSec = view.findViewById(R.id.jinrBtnSec);
        toolBtnSec.setOnClickListener(this);

        toolBtnMore = view.findViewById(R.id.jinrBtnMore);
        toolBtnMore.setOnClickListener(this);

        toolBtnGift = view.findViewById(R.id.jinrBtnGift);
        toolBtnGift.setOnClickListener(this);
        jinrBtnAsset = view.findViewById(R.id.jinrBtnAsset);
        jinrBtnAsset.setOnClickListener(this);

        jinrBtnWelfare = view.findViewById(R.id.jinrBtnWelfare);
        jinrBtnWelfare.setOnClickListener(this);

        jinrBtnBankCard = view.findViewById(R.id.jinrBtnBankCard);
        jinrBtnBankCard.setOnClickListener(this);

        bankTextView = (TextView) jinrBtnBankCard.findViewById(R.id.bank_name_title);
        redeemCodeTv = (TextView) view.findViewById(R.id.left_redeem_code_tv);
        platformTv = (TextView) view.findViewById(R.id.left_platform_tv);
        guaranteeTv = (TextView) view.findViewById(R.id.left_guarantee_tv);
        img_slid_redgift = (ImageView) toolBtnGift.findViewById(R.id.img_slid_redgift);
        img_slid_more = (ImageView) toolBtnMore.findViewById(R.id.img_slid_more);
        titleTextView = (TextView) view.findViewById(R.id.tv_title_left_menu);
        titleTextViewTime = (TextView) view.findViewById(R.id.tv_title_left_menu_time);
        titleTextView.setOnClickListener(this);
        redeemCodeTv.setOnClickListener(this);
        platformTv.setOnClickListener(this);
        guaranteeTv.setOnClickListener(this);
        leftinstance = this;
        return view;
    }

    public void setTitle() {
        if (Check.is_login(getActivity())) {
            name = PreferencesUtils.getValueFromSPMap(getActivity(), PreferencesUtils.Keys.NAME);
            tel = PreferencesUtils.getValueFromSPMap(getActivity(), PreferencesUtils.Keys.TEL);
            state_bankBind = PreferencesUtils.getIntFromSPMap(getActivity(), PreferencesUtils.Keys.IS_BIND_CARD);
            if (state_bankBind == 1) {
                jinrBtnBankCard.setVisibility(View.GONE);
                if (!name.equals("")) {
                    titleTextViewTime.setText(TimeUtil.getGreetings());
                    titleTextView.setText(name.charAt(0) + "财主");
                } else {
                    titleTextViewTime.setText(TimeUtil.getGreetings());
                    titleTextView.setText("财主");
                }
            } else {
                bankTextView.setText(getResources().getString(R.string.nobankcard));
                jinrBtnBankCard.setVisibility(View.VISIBLE);
                titleTextView.setText(TextAdjustUtil.getInstance().mobileAdjust(tel));
            }
            titleTextView.setClickable(false);
        } else {
            bankTextView.setText("银行卡");
            jinrBtnBankCard.setVisibility(View.VISIBLE);
            titleTextViewTime.setText(TimeUtil.getGreetings());
            titleTextView.setText(getResources().getString(R.string.login_left_menu));
            titleTextView.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
            titleTextView.setClickable(true);
        }
    }

    @Override
    public void onClick(View v) {
        MainActivity.instance.closeLeftMenu();
        Intent intent = null;
        switch (v.getId()) {
            case R.id.tv_title_left_menu:
                intent = new Intent(getActivity(), NewLoginActivity.class);
                startActivity(intent);
                break;
            case R.id.jinrBtnAsset:
                //资产
                UmUtils.customsEvent(getActivity(), UmUtils.ASSETS_CLICK, UmUtils.ASSETS_HUM);
                startActivity(new Intent(getActivity(), MyAssetsActivity.class));
                break;
            case R.id.jinrBtnSec:
                // 点击效果修改
                UmUtils.customsEvent(getActivity(), UmUtils.SECURITY_CLICK, UmUtils.SECURITY_NUM);
                if (!Check.is_login(getActivity())) {
                    Intent intent_login = new Intent(getActivity(), NewLoginActivity.class);
                    startActivity(intent_login);
                    return;
                }
                startActivity(new Intent(getActivity(), SettingActivity.class));
                break;
            case R.id.jinrBtnMore:
                // 点击效果修改
                UmUtils.customsEvent(getActivity(), UmUtils.MORE_CLICK, UmUtils.MORE_HUM);
                Intent intentSys = new Intent(getActivity(), MoreActivity.class);
                intentSys.putExtra("isShowSys", isShowSys);
                startActivity(intentSys);
                break;
            case R.id.jinrBtnGift:
                //活动
                startLocation();
                break;
            case R.id.jinrBtnBankCard:
                //绑卡
                UmUtils.customsEvent(getActivity(), UmUtils.BANKCARD_CLICK, UmUtils.BANKCARD_HUM);
                if (!Check.is_login(getActivity())) {// 判断是否登录状态
                    Intent intent_login = new Intent(getActivity(), NewLoginActivity.class);
                    startActivity(intent_login);
                    return;
                }
                if (!PreferencesUtils.getValueFromSPMap(getActivity(), PreferencesUtils.Keys.NAME).equals("") &&
                        !PreferencesUtils.getValueFromSPMap(getActivity(), PreferencesUtils.Keys.ID_CARD).equals("")) {
                    intent = new Intent(getActivity(), SecondBandCardActivity.class);
                    startActivity(intent);
                } else {
                    intent = new Intent(getActivity(), AddBankActivity.class);
                    startActivity(intent);
                }
                break;

            /**
             * 福利按钮点击 @author Ysw created at 2017/3/15 20:06
             */
            case R.id.jinrBtnWelfare:
                if (!Check.is_login(getActivity())) {
                    Intent intent_login = new Intent(getActivity(), NewLoginActivity.class);
                    startActivity(intent_login);
                    return;
                }
                startActivity(new Intent(getActivity(), BonusCenterActivity.class));
                break;
            case R.id.left_guarantee_tv:
                intent = new Intent(getActivity(), CommonWebActivity.class);
                intent.putExtra("url", UrlConfig.IP_R + UrlConfig.DAILY_SAFE);
                intent.putExtra("titleName", "安全保障");
                startActivity(intent);
                break;
            case R.id.left_platform_tv:
                intent = new Intent(getActivity(), CommonWebActivity.class);
                intent.putExtra("url", UrlConfig.IP_V + UrlConfig.PLATFORM_DATA);
                intent.putExtra("titleName", "营运数据");
                startActivity(intent);
                break;
            case R.id.left_redeem_code_tv:
                if (!Check.is_login(getActivity())) {
                    Intent intent_login = new Intent(getActivity(), NewLoginActivity.class);
                    startActivity(intent_login);
                    return;
                }
                intent = new Intent(getActivity(), CommonWebActivity.class);
                intent.putExtra("url", UrlConfig.IP_R + UrlConfig.DAILY_REDEEM_CODE);
                intent.putExtra("titleName", "兑换码");
                startActivity(intent);
                break;
        }
    }

    @Subscriber(tag = EventBusKey.BING_BANK_CARD)
    public void bingBankCard(int data) {

        MyLog.e(TAG,"LeftSlidingMenuFragment.bingBankCard：");
        setTitle();
    }

    @Subscriber(tag = EventBusKey.LOGO_SYS_REDPOINT)
    public void LogoSYsRed(boolean data) {
        isShowSys = data;
        if (data) {
            img_slid_more.setVisibility(View.VISIBLE);
        } else {
            img_slid_more.setVisibility(View.GONE);
        }
    }

    /**
     * 初始化经纬度
     */
    private void initBaidu() {
        BaiduUtils.getInstance().initBaidu(new BaiduUtils.BaiduLocationListener() {
            @Override
            public void onSuccess(BDLocation location) {
                latitude = location.getLatitude() + "";
                longitude = location.getLongitude() + "";
                startLocation();
            }

            @Override
            public void onFail() {
                PreferencesUtils.UnReadActionToSPMap(getActivity());
                img_slid_redgift.setVisibility(View.GONE);
                Intent intent = new Intent();
                intent.setClass(getActivity(), NewsCenterActivity.class);
                startActivity(intent);
            }
        });
    }

    public void startLocation() {
        if (TextUtils.isEmpty(latitude) && TextUtils.isEmpty(longitude)) {
            initBaidu();
        } else {
            PreferencesUtils.UnReadActionToSPMap(getActivity());
            img_slid_redgift.setVisibility(View.GONE);
            Intent intent = new Intent();
            intent.putExtra("lat", latitude);
            intent.putExtra("lng", longitude);
            intent.setClass(getActivity(), NewsCenterActivity.class);
            startActivity(intent);
        }
    }

}
