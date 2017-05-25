package com.jinr.core.regular;

import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.TextAppearanceSpan;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jinr.core.MainActivity;
import com.jinr.core.R;
import com.jinr.core.balance.AccountBalanceActivity;
import com.jinr.core.base.BaseActivity;
import com.jinr.core.config.Check;
import com.jinr.core.config.UrlConfig;
import com.jinr.core.gift.BonusCenterActivity;
import com.jinr.core.my_change.NewProfitAmountActivity;
import com.jinr.core.regist.MyRotate3dAnimation;
import com.jinr.new_mvp.ui.activity.NewLoginActivity;
import com.jinr.core.trade.getCash.AssetsActivity;
import com.jinr.core.trade.record.NewTradeRecordActivity;
import com.jinr.core.utils.CommonUtil;
import com.jinr.core.utils.MyDES;
import com.jinr.core.utils.MyhttpClient;
import com.jinr.core.utils.PreferencesUtils;
import com.jinr.core.utils.ToastUtil;
import com.jinr.core.utils.UmUtils;
import com.jinr.graph_view.XListViewJinr;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONObject;

import java.util.ArrayList;

import model.BaseModel;
import model.ProductCommonModel;
import model.RegularAccount;
import model.RegularAccountList;

public class MyAssetsActivity extends BaseActivity implements OnClickListener, XListViewJinr.IXListViewListener {
    private XListViewJinr listView;
    private TextView totalAssets, title, yesterdayEarn, totalEarn;
    private ArrayList<RegularAccount> list;
    private TotalAssetsAdapter adapter;
    private View headerView;
    private boolean isSend, isSendIncome;
    private ImageView image_back;
    // 注册红包相关变量 @author Ysw created at 2017/3/13 18:31
    private ImageView image_open;
    private View mStartAnimView;
    private float mOpenCenterX = 0.0f;
    private float mOpenCenterY = 0.0f;
    private float mCenterX = 0.0f;
    private float mCenterY = 0.0f;
    private int mDuration = 500;
    private View mContainer;
    private FrameLayout fl_registerOne;
    private FrameLayout fl_registerTwo;
    private TextView tv_money;
    private TextView tv_renpackage;
    private ImageView image_down;
    private FrameLayout fl_redpackage;
    private TextView details;
    private boolean isRegister;
    private boolean isRedpackage;
    // 注册红包相关变量 @author Ysw created at 2017/3/13 18:31

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_assets);
        Intent intent = getIntent();
        isRegister = intent.getBooleanExtra("isRegister", false);
        findViewById();
        initUI();
        setListener();
    }

    private void testData() {
        if (list != null) {
            list.clear();
        }
        RegularAccount r = new RegularAccount();
        r.setMoney("0.00");
        r.setName(getResources().getString(R.string.rest_assets));
        list.add(r);
        r = new RegularAccount();
        r.setMoney("0.00");
        r.setName(getResources().getString(R.string.current_assets));
        list.add(r);
        r = new RegularAccount();
        r.setMoney("0.00");
        r.setName(getResources().getString(R.string.strongbox_assets));
        list.add(r);
        r = new RegularAccount();
        r.setMoney("0.00");
        r.setName(getResources().getString(R.string.regular_assets));
        list.add(r);
        r = new RegularAccount();
        r.setMoney("0.00");
        r.setName(getResources().getString(R.string.dayup_assets));
        list.add(r);

        if (adapter == null) {
            adapter = new TotalAssetsAdapter(this, list);
            listView.setAdapter(adapter);
        } else {
            adapter.notifyDataSetChanged();
        }
        if (Check.is_login(this)) {
            listView.setOnItemClickListener(null);
        } else {
            listView.setOnItemClickListener(new OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    startActivity(new Intent(MyAssetsActivity.this, NewLoginActivity.class));
                }
            });
        }
    }

    @Override
    protected void initData() {
    }

    @Override
    protected void findViewById() {
        list = new ArrayList<>();
        adapter = new TotalAssetsAdapter(this, list);
        headerView = LayoutInflater.from(MyAssetsActivity.this).inflate(R.layout.header_my_assets, null);
        title = (TextView) findViewById(R.id.title_center_text);
        image_back = (ImageView) findViewById(R.id.title_left_img);
        listView = (XListViewJinr) findViewById(R.id.assets_list);
        // 交易记录 @author Ysw created at 2017/3/13 20:40
        details = (TextView) findViewById(R.id.details);
        totalAssets = (TextView) headerView.findViewById(R.id.total_assets);
        yesterdayEarn = (TextView) headerView.findViewById(R.id.yesterday_earn);
        totalEarn = (TextView) headerView.findViewById(R.id.all_earn);
        // 注册红包相关控件 @author Ysw created at 2017/3/13 20:36
        fl_redpackage = (FrameLayout) findViewById(R.id.fl_redpackage);
        fl_registerOne = (FrameLayout) findViewById(R.id.fl_registerOne);
        fl_registerTwo = (FrameLayout) findViewById(R.id.fl_registerTwo);
        image_open = (ImageView) findViewById(R.id.image_open);
        tv_money = (TextView) findViewById(R.id.tv_money);
        SpannableString rateText = new SpannableString("5,000.00" + "元");
        rateText.setSpan(new TextAppearanceSpan(this, R.style.register_redpackage_one), 0, rateText.length() - 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        rateText.setSpan(new TextAppearanceSpan(this, R.style.register_redpackage_two), rateText.length() - 1, rateText.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tv_money.setText(rateText, TextView.BufferType.SPANNABLE);
        image_open.setOnClickListener(this);
        image_down = (ImageView) findViewById(R.id.image_down);
        tv_renpackage = (TextView) findViewById(R.id.tv_renpackage);
        // 注册红包相关控件 @author Ysw created at 2017/3/13 20:36
    }

    @Override
    protected void initUI() {
        title.setText(getResources().getString(R.string.assets));
        listView.setPullLoadEnable(false);
        listView.setOverScrollMode(View.OVER_SCROLL_NEVER);// 防止魅族手机出现HOLD悬停
        listView.setRefreshTime();
        listView.setXListViewListener(this, 1);
        listView.addHeaderView(headerView);
        listView.setAdapter(adapter);
        if (isRegister) {
            fl_redpackage.setVisibility(View.VISIBLE);
        } else {
            fl_redpackage.setVisibility(View.GONE);
        }
    }

    @Override
    protected void setListener() {
        image_back.setOnClickListener(this);
        details.setOnClickListener(this);
        headerView.findViewById(R.id.yesterday_earn_lay).setOnClickListener(this);
        headerView.findViewById(R.id.all_earn_lay).setOnClickListener(this);
    }

    private void setOnItemListener() {
        listView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                if (!Check.is_login(MyAssetsActivity.this)) {
                    startActivity(new Intent(MyAssetsActivity.this, NewLoginActivity.class));
                    return;
                }
                if (list != null && list.size() > 0) {
                    if (!Check.is_login(MyAssetsActivity.this)) {
                        Intent intent_login = new Intent(MyAssetsActivity.this, NewLoginActivity.class);
                        startActivity(intent_login);
                    }
                    int size = position - 2;
                    if (size >= 0) {
                        RegularAccount regularAccount = list.get(size);
                        Intent intent = new Intent();
                        ProductCommonModel productCommonModel = new ProductCommonModel();
                        Bundle bundle = new Bundle();
                        if (regularAccount.getGoback().equals("101")) {//活期资产，未和定期日增息保险箱公用界面
                            intent.setClass(MyAssetsActivity.this, AssetsActivity.class);
                            intent.putExtra("account", regularAccount);
                            intent.putExtra("product_code", regularAccount.getProductcode());
                            startActivity(intent);
                            UmUtils.customsEvent(MyAssetsActivity.this, UmUtils.CURRENTASSETS_CLICK, UmUtils.CURRENTASSETS_HUM);
                        } else if (regularAccount.getGoback().equals("102")) {//定期资产
                            if (null != regularAccount.getAssetsid() && !regularAccount.getAssetsid().equals("")) {
                                UmUtils.customsEvent(MyAssetsActivity.this, UmUtils.REGULARASSETS_CLICK, UmUtils.REGULARASSETS_HUM);
                                productCommonModel.setCode(regularAccount.getProductcode());
                                productCommonModel.setName(regularAccount.getNametext());
                                productCommonModel.setAssetid(regularAccount.getAssetsid());
                                productCommonModel.setType(regularAccount.getGoods_type());
                                productCommonModel.setProduct_status(regularAccount.getProduct_status());
                                productCommonModel.setStatus("0");
                                bundle.putSerializable("regular", productCommonModel);
                                intent.putExtras(bundle);
                                intent.setClass(MyAssetsActivity.this, RegularAssetsActivity.class);
                                startActivity(intent);
                            } else {
                                ToastUtil.show(MyAssetsActivity.this, "网络不给力，请刷新重试");
                            }
                        } else if (regularAccount.getGoback().equals("103")) {//日增息资产
                            UmUtils.customsEvent(MyAssetsActivity.this, UmUtils.DAYPRODUCTASSETS_CLICK, UmUtils.DAYPRODUCTASSETS_HUM);
                            if (null != regularAccount.getAssetsid() && !regularAccount.getAssetsid().equals("")) {
                                productCommonModel.setCode(regularAccount.getProductcode());
                                productCommonModel.setName(regularAccount.getNametext());
                                productCommonModel.setAssetid(regularAccount.getAssetsid());
                                productCommonModel.setType(regularAccount.getGoods_type());
                                productCommonModel.setProduct_status(regularAccount.getProduct_status());
                                productCommonModel.setStatus("1");
                                bundle.putSerializable("regular", productCommonModel);
                                intent.putExtras(bundle);
                                intent.setClass(MyAssetsActivity.this, RegularAssetsActivity.class);
                                startActivity(intent);
                            } else {
                                ToastUtil.show(MyAssetsActivity.this, "网络不给力，请刷新重试");
                            }
                        } else if (regularAccount.getGoback().equals("104")) {//余额
                            intent.putExtra("money", regularAccount.getMoney());
                            intent.putExtra("product_code", regularAccount.getProductcode());
                            intent.setClass(MyAssetsActivity.this, AccountBalanceActivity.class);
                            startActivity(intent);
                        } else if (regularAccount.getGoback().equals("105")) {//保险箱资产
                            if (null != regularAccount.getAssetsid() && !regularAccount.getAssetsid().equals("")) {
                                UmUtils.customsEvent(MyAssetsActivity.this, UmUtils.REGULARASSETS_CLICK, UmUtils.REGULARASSETS_HUM);
                                productCommonModel.setCode(regularAccount.getProductcode());
                                productCommonModel.setName(regularAccount.getNametext());
                                productCommonModel.setAssetid(regularAccount.getAssetsid());
                                productCommonModel.setType(regularAccount.getGoods_type());
                                productCommonModel.setProduct_status(regularAccount.getProduct_status());
                                productCommonModel.setStatus("2");
                                bundle.putSerializable("regular", productCommonModel);
                                intent.putExtras(bundle);
                                intent.setClass(MyAssetsActivity.this, RegularAssetsActivity.class);
                                startActivity(intent);
                            } else {
                                ToastUtil.show(MyAssetsActivity.this, "网络不给力，请刷新重试");
                            }
                        } else if (regularAccount.getGoback().equals("106")) {//我的红包
                            startActivity(new Intent(MyAssetsActivity.this, BonusCenterActivity.class));
                        }
                    }
                } else {
                    ToastUtil.show(MyAssetsActivity.this, "网络不给力，请刷新重试");
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.title_left_img) {
            if (isRedpackage) {
                startActivity(new Intent(MyAssetsActivity.this, MainActivity.class));
            }
            finish();
            return;
        }
        if (!Check.is_login(this)) {
            startActivity(new Intent(this, NewLoginActivity.class));
            return;
        }
        switch (view.getId()) {
            case R.id.fl_redpackage://点击空白区域消失
                fl_redpackage.setVisibility(View.GONE);
                isRegister = !isRegister;
                isRedpackage = true;
                break;
            case R.id.details://所有的交易记录
                startActivity(new Intent(this, NewTradeRecordActivity.class));
                break;
            case R.id.yesterday_earn_lay://跳转昨日收益
                Intent intent = new Intent(this, NewProfitAmountActivity.class);
                intent.putExtra("name", "昨日收益");
                startActivity(intent);
                break;
            case R.id.all_earn_lay://跳转累计收益
                Intent pIntent = new Intent(this, NewProfitAmountActivity.class);
                pIntent.putExtra("name", "累计收益");
                startActivity(pIntent);
                break;
            case R.id.image_open:
                openRotation();
                break;
            default:
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            if (Check.is_login(this)) {
                send();
                sendIncome();
            } else {
                testData();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void send() throws Exception {
        isSend = false;
        JSONObject obj = new JSONObject();
        obj.put("uid", PreferencesUtils.getValueFromSPMap(this, PreferencesUtils.Keys.UID));
        RequestParams params = new RequestParams();
        String desStr = MyDES.encrypt(obj.toString());
        params.put("cipher", desStr);
        MyhttpClient.desPost(UrlConfig.TOTALA_CCOUNT_DETAILS, params, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                super.onStart();
                showWaittingDialog(MyAssetsActivity.this);
            }

            @Override
            public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
                super.onFailure(arg0, arg1, arg2, arg3);
                listView.stopRefresh();
                ToastUtil.show(MainActivity.instance, R.string.default_error);
                if (list != null && list.size() == 0) {
                    testData();
                    listView.setOnItemClickListener(null);
                }
                isSend = true;
                isDismiss();
            }

            @Override
            public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
                super.onSuccess(arg0, arg1, arg2);
                listView.stopRefresh();
                try {
                    String response = new String(arg2, "UTF-8");
                    response = CommonUtil.transResponse(response);
                    if (response == null || response.equals("")) {
                        return;
                    }
                    JSONObject jsonObject = new JSONObject(response); // 解析
                    String cipher = jsonObject.getString("cipher");
                    String desStr = MyDES.decrypt(cipher); // 解密
                    if (TextUtils.isEmpty(desStr))
                        return;
                    BaseModel<RegularAccountList> result = new Gson().fromJson(desStr, new TypeToken<BaseModel<RegularAccountList>>() {
                    }.getType());
                    if (result.isSuccess() && result.getData().getList() != null) {
                        if (list != null) {
                            list.clear();
                        }
                        totalAssets.setText(result.getData().getTotal_money());
                        list.addAll(result.getData().getList());
                        if (adapter == null) {
                            adapter = new TotalAssetsAdapter(MyAssetsActivity.this, list);
                            listView.setAdapter(adapter);
                        } else {
                            adapter.list = list;
                            adapter.notifyDataSetChanged();
                        }
                        setOnItemListener();
                    } else {
                        if (list != null && list.size() == 0) {
                            testData();
                            listView.setOnItemClickListener(null);
                        }
                    }
                    isSend = true;
                    isDismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void sendIncome() throws Exception {
        isSendIncome = false;
        JSONObject obj = new JSONObject();
        obj.put("uid", PreferencesUtils.getValueFromSPMap(this, PreferencesUtils.Keys.UID));
        RequestParams params = new RequestParams();
        String desStr = MyDES.encrypt(obj.toString());
        params.put("cipher", desStr);
        MyhttpClient.desPost(UrlConfig.TOTAL_ASSETS_INCOME, params, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                super.onStart();
                showWaittingDialog(MyAssetsActivity.this);
            }

            @Override
            public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
                super.onFailure(arg0, arg1, arg2, arg3);
                isSendIncome = true;
                isDismiss();
            }

            @Override
            public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
                super.onSuccess(arg0, arg1, arg2);
                try {
                    String response = new String(arg2, "UTF-8");
                    response = CommonUtil.transResponse(response);
                    if (response == null || response.equals("")) {
                        return;
                    }
                    JSONObject jsonObject = new JSONObject(response); // 解析
                    String cipher = jsonObject.getString("cipher");
                    String desStr = MyDES.decrypt(cipher); // 解密
                    if (TextUtils.isEmpty(desStr))
                        return;
                    JSONObject result = new JSONObject(desStr);
                    int code = result.optInt("code");
                    if (code == 1000) {
                        JSONObject data = result.optJSONObject("data");
                        yesterdayEarn.setText(data.optString("yesterday_total"));
                        totalEarn.setText(data.optString("income_total"));
                    } else {
                        ToastUtil.show(MyAssetsActivity.this, result.optString("msg"));
                    }
                    isSendIncome = true;
                    isDismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void isDismiss() {
        if (isSend && isSendIncome) {
            dismissWaittingDialog();
        }
    }

    @Override
    public void onRefresh(int id) {
        try {
            if (Check.is_login(this)) {
                send();
                sendIncome();
            } else {
                listView.stopRefresh();
                listView.stopLoadMore();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onLoadMore(int id) {

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) { // 返回键
            if (isRegister) {
                fl_redpackage.setVisibility(View.GONE);
                isRegister = !isRegister;
                isRedpackage = true;
            } else {
                finish();
            }
        }
        return true;
    }


    /**
     * 红包动画相关 @author Ysw created at 2017/3/13 18:30
     */

    public void openRotation() {
        mOpenCenterX = image_open.getWidth() / 2.0f;
        mOpenCenterY = image_open.getHeight() / 2.0f;
        MyRotate3dAnimation animation = new MyRotate3dAnimation(0.0f, -270.0f, mOpenCenterX, mOpenCenterY, 0, true, 1);
        animation.setDuration(mDuration);
        animation.setFillAfter(true);
        animation.setInterpolator(new DecelerateInterpolator());
        animation.setAnimationListener(new DisplayNextView(1));
        image_open.startAnimation(animation);
    }

    private final class DisplayNextView implements Animation.AnimationListener {
        private int mNum;

        public DisplayNextView(int num) {
            mNum = num;
        }

        public void onAnimationStart(Animation animation) {
        }

        public void onAnimationEnd(Animation animation) {
            if (mNum == 1) {
                fl_redpackage.post(new Runnable() {
                    @Override
                    public void run() {
                        mCenterX = fl_registerOne.getWidth() / 2.0f;
                        mCenterY = fl_registerOne.getHeight() / 2.0f;
                        MyRotate3dAnimation animation = new MyRotate3dAnimation(0.0f, -90.0f, mCenterX, mCenterY, 100f, true, 2);
                        animation.setDuration(300);
                        animation.setFillAfter(true);
                        animation.setInterpolator(new DecelerateInterpolator());
                        animation.setAnimationListener(new DisplayNextView(2));
                        fl_registerOne.startAnimation(animation);
                    }
                });
            } else if (mNum == 2) {
                fl_redpackage.post(new SwapViews());
            }
            //设置不可点击
            image_open.setClickable(false);
            fl_redpackage.setOnClickListener(MyAssetsActivity.this);
        }

        public void onAnimationRepeat(Animation animation) {
        }
    }

    private final class SwapViews implements Runnable {
        @Override
        public void run() {
            fl_registerOne.setVisibility(View.GONE);
            fl_registerTwo.setVisibility(View.GONE);
            mStartAnimView = fl_registerTwo;
            mStartAnimView.setVisibility(View.VISIBLE);
            mStartAnimView.requestFocus();
            MyRotate3dAnimation rotation = new MyRotate3dAnimation(90, 0, mCenterX, mCenterY, 100f, false, 2);
            rotation.setDuration(300);
            rotation.setFillAfter(true);
            rotation.setInterpolator(new DecelerateInterpolator());
            mStartAnimView.startAnimation(rotation);
            tv_renpackage.setVisibility(View.VISIBLE);
            image_down.setVisibility(View.VISIBLE);
        }
    }

}
