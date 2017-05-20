package com.jinr.core.regular;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jinr.core.JinrApp;
import com.jinr.core.MainActivity;
import com.jinr.core.R;
import com.jinr.core.base.BaseActivity;
import com.jinr.core.config.EventBusKey;
import com.jinr.core.config.UrlConfig;
import com.jinr.core.dayup.CommonProjectDetailActivity;
import com.jinr.core.more.CjWentiActivity;
import com.jinr.core.my_change.NewProfitAmountActivity;
import com.jinr.core.trade.purchase.CurrentPurchaseFirstActivity;
import com.jinr.core.trade.record.TradeRecordActivity;
import com.jinr.core.utils.CommonUtil;
import com.jinr.core.utils.MyDES;
import com.jinr.core.utils.MyLog;
import com.jinr.core.utils.MyhttpClient;
import com.jinr.core.utils.PreferencesUtils;
import com.jinr.core.utils.TimeUtil;
import com.jinr.core.utils.ToastUtil;
import com.jinr.graph_view.XListViewJinr;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONObject;
import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;

import java.util.ArrayList;
import java.util.List;

import model.BaseModel;
import model.DayUpLimitList;
import model.ProductCommonModel;
import model.RegularRecodeList;
import model.RegularRecord;
import model.UidObj;
import model.UserBindinfo;

public class RegularAssetsActivity extends BaseActivity implements OnClickListener, XListViewJinr.IXListViewListener {
    public static final String TAG = "RegularAssetsActivity";
    private XListViewJinr mListView;
    private LinearLayout ll_income;
    private RegularAssetsAdaper mAdapter;
    private View headView;
    private String uid;
    private Button turnInBtn;
    private RelativeLayout totalAssetLayout;
    private TextView tv_income, totalAssets, mTitle, tv_allIncome, tv_capital, tv_name;
    private ImageView backIv;
    private int PAGE_COUNT = 10;
    private List<RegularRecord> list;
    private ProductCommonModel productCommonModel;
    private RelativeLayout rlNoData;
    private boolean isSend, isSendMyAccount;
    private String goods_type;
    private String type;
    private BaseModel<DayUpLimitList> dayUpLimit;
    private TextView tv_protocol;
    private TextView tv_assets;
    private LinearLayout ll_allIncome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regular_asset);
        initData();
        findViewById();
        initUI();
        setListener();
    }

    @Override
    protected void initData() {
        uid = PreferencesUtils.getValueFromSPMap(this, PreferencesUtils.Keys.UID);
        list = new ArrayList<>();
        productCommonModel = (ProductCommonModel) getIntent().getExtras().getSerializable("regular"); // 取消注释
        registerReceiver();
    }

    private void registerReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("action.refresh_actdetail");
        registerReceiver(mRefreshBroadcastReceiver, intentFilter);
    }

    @SuppressLint("InflateParams")
    @Override
    protected void findViewById() {
        //EventBus.getDefault().register(this);
        mListView = (XListViewJinr) this.findViewById(R.id.asset_lv);
        turnInBtn = (Button) findViewById(R.id.product_turn_in);
        backIv = (ImageView) findViewById(R.id.title_left_img);
        mTitle = (TextView) findViewById(R.id.title_center_text);
        tv_protocol = (TextView) findViewById(R.id.tv_protocol);
        backIv.setOnClickListener(this);
        headView = LayoutInflater.from(RegularAssetsActivity.this).inflate(R.layout.head_regular_assets, null);
        tv_income = (TextView) headView.findViewById(R.id.tv_income);
        totalAssets = (TextView) headView.findViewById(R.id.total_assets);
        rlNoData = (RelativeLayout) headView.findViewById(R.id.rl_no_data);
        tv_allIncome = (TextView) headView.findViewById(R.id.tv_allIncome);
        tv_capital = (TextView) headView.findViewById(R.id.tv_capital);
        tv_name = (TextView) headView.findViewById(R.id.tv_name);
        tv_assets = (TextView) headView.findViewById(R.id.tv_assets);
        ll_income = (LinearLayout) headView.findViewById(R.id.ll_income);
        ll_allIncome = (LinearLayout) headView.findViewById(R.id.ll_allIncome);
        if (productCommonModel.getStatus().equals("1") || productCommonModel.getStatus().equals("2")) {
            ll_income.setVisibility(View.VISIBLE);
            ll_allIncome.setOnClickListener(this);
        } else {
            ll_income.setVisibility(View.GONE);
            ll_allIncome.setOnClickListener(null);
        }
        setLimit();
    }

    private void setLimit() {
        if (dayUpLimit == null) {
            turnInBtn.setText("立即转入");
            return;
        }
        turnInBtn.setTextColor(getResources().getColor(R.color.blue_pb));
        if (productCommonModel.getType() == null || productCommonModel.getType().equals("")) {
            turnInBtn.setText("立即转入");
        } else {
            for (int i = 0; i < dayUpLimit.getData().getList().size(); i++) {
                if (productCommonModel.getType().equals(dayUpLimit.getData().getList().get(i).getGoods_type())) {
                    if (dayUpLimit.getData().getList().get(i).getStart_time() == 0) {
                        turnInBtn.setText("立即转入");
                        return;
                    }
                    if (TimeUtil.getDiffTime(dayUpLimit.getData().getList().get(i).getStart_time(), dayUpLimit.getData().getCurrent_time()) >= 1) {
                        turnInBtn.setText("今天" + TimeUtil.formatHourDateTime(dayUpLimit.getData().getList().get(i).getStart_time()) + "开放转入");
                    } else {
                        if (dayUpLimit.getData().getList().get(i).getLimit() == 0) {
                            turnInBtn.setTextColor(getResources().getColor(R.color.gesture_text_gray));
                            turnInBtn.setText("已售罄，明天" + TimeUtil.formatHourDateTime(dayUpLimit.getData().getList().get(i).getNext_time()) + "开放转入");
                        } else {
                            turnInBtn.setText("立即转入");
                        }
                    }
                }
            }
        }
    }

    @Override
    protected void initUI() {
        mAdapter = new RegularAssetsAdaper(list, productCommonModel, RegularAssetsActivity.this);
        mListView.setAdapter(mAdapter);
        String assetsName = productCommonModel.getName() + "资产";
        tv_protocol.setText("交易记录");
        tv_protocol.setVisibility(View.VISIBLE);
        mTitle.setText(assetsName);
        tv_name.setText(assetsName + "(元)");
        mListView.addHeaderView(headView);
        mListView.setOverScrollMode(View.OVER_SCROLL_NEVER);// 防止魅族手机出现HOLD悬停
        mListView.setPullLoadEnable(false);
        mListView.setRefreshTime();
        mListView.setXListViewListener(this, 1);
        try {
            send(0);//资产列表
            sendMyAccount();//
            //Product_status为1时已下架
            if (!TextUtils.isEmpty(productCommonModel.getProduct_status()) && "1".equals(productCommonModel.getProduct_status())) {
                findViewById(R.id.ly_in_out).setVisibility(View.GONE);
            } else {
                getProductLimit();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private BroadcastReceiver mRefreshBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            showWaittingDialog(RegularAssetsActivity.this);
            if (action.equals("action.refresh_actdetail")) {
                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        try {
                            send(0);
                            sendMyAccount();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, 1000);
            }
        }
    };

    @Override
    protected void setListener() {
        tv_protocol.setOnClickListener(this);
        turnInBtn.setOnClickListener(this);
        headView.findViewById(R.id.iv_showing_ask).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.product_turn_in:
                if (!CommonUtil.isFastDoubleClick()) {
                    try {
                        if (productCommonModel.getType() == null || productCommonModel.getType().equals("") || dayUpLimit == null) {
                            sendVerifyuser(1);
                        } else {
                            for (int i = 0; i < dayUpLimit.getData().getList().size(); i++) {
                                if (productCommonModel.getType().equals(dayUpLimit.getData().getList().get(i).getGoods_type())) {
                                    if (dayUpLimit.getData().getList().get(i).getStart_time() == 0) {
                                        sendVerifyuser(1);
                                        return;
                                    }
                                    if (TimeUtil.getDiffTime(dayUpLimit.getData().getList().get(i).getStart_time(), dayUpLimit.getData().getCurrent_time()) >= 1) {
                                        ToastUtil.show(RegularAssetsActivity.this, "还未开启转入");
                                    } else {
                                        if (dayUpLimit.getData().getList().get(i).getLimit() == 0) {
                                            ToastUtil.show(RegularAssetsActivity.this, "今天已售罄");
                                        } else {
                                            sendVerifyuser(1);
                                        }
                                    }
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
            case R.id.iv_showing_ask:
                startActivity(new Intent(this, CjWentiActivity.class));
                break;
            case R.id.tv_protocol:// 跳转至定期、日增息、保险箱交易记录 @author Ysw created at 2017/2/6 18:00
                if (!TextUtils.isEmpty(goods_type)) {
                    Intent intent = new Intent(RegularAssetsActivity.this, TradeRecordActivity.class);
                    intent.putExtra("name", productCommonModel.getName());
                    intent.putExtra("goods_type", goods_type);
                    intent.putExtra("product_code", productCommonModel.getCode());
                    startActivity(intent);
                }
                break;
            case R.id.title_left_img:// 返回
                finish();
                break;
            case R.id.ll_allIncome:// 跳转累计收益
                if (!TextUtils.isEmpty(type)) {
                    Intent i = new Intent(this, NewProfitAmountActivity.class);
                    i.putExtra("name", productCommonModel.getName() + "收益");
                    i.putExtra("type", type);
                    startActivity(i);
                }
                break;
            default:
                break;
        }
    }

    private void turnInMethod() {
        if (list != null && list.size() > 0) {
            Intent intent = new Intent();
            intent.setClass(RegularAssetsActivity.this, CurrentPurchaseFirstActivity.class);
            intent.putExtra("regular", productCommonModel);
            startActivity(intent);
        } else {
            Intent intent = new Intent();
            intent.setClass(RegularAssetsActivity.this, CommonProjectDetailActivity.class);
            intent.putExtra("assetid", productCommonModel.getAssetid());
            intent.putExtra("event_key", productCommonModel.getEventKey());
            intent.putExtra("name", productCommonModel.getName());
            intent.putExtra("good_type", productCommonModel.getType());
            startActivity(intent);
        }
    }

    @Override
    public void onRefresh(int id) {
        try {
            send(0);
            sendMyAccount();
            getProductLimit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onLoadMore(int id) {
        try {
            send(1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void send(final int is_more) throws Exception {
        mListView.setRest(false);
        isSend = false;
        rlNoData.setVisibility(View.GONE);
        JSONObject obj = new JSONObject();
        if (productCommonModel.getStatus().equals("0")) {
            obj.put("type", "regular");
        } else if (productCommonModel.getStatus().equals("1")) {
            obj.put("type", "daily_gain");
        } else {
            obj.put("type", "safe");
        }
        if (is_more == 0) {
            obj.put("page", 1);
        } else {
            int page = (int) (Math.ceil((list.size()) * 1.0 / PAGE_COUNT + 1));
            obj.put("page", page);
        }
        obj.put("uid", uid);
        obj.put("page_size", PAGE_COUNT);
        RequestParams params = new RequestParams();
        String desStr = MyDES.encrypt(obj.toString());
        params.put("cipher", desStr);
        MyhttpClient.desPost(UrlConfig.CUNBEI_RECORDS, params, new AsyncHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                showWaittingDialog(RegularAssetsActivity.this);
            }

            @Override
            public void onFinish() {
                super.onFinish();
                dismissWaittingDialog();
            }

            @Override
            public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
                super.onFailure(arg0, arg1, arg2, arg3);
                mListView.stopLoadMore();
                mListView.stopRefresh();
                isSend = true;
                ToastUtil.show(RegularAssetsActivity.this, R.string.default_error);
            }

            @Override
            public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
                super.onSuccess(arg0, arg1, arg2);
                mListView.stopLoadMore();
                mListView.stopRefresh();
                if (is_more == 0) {
                    if (list != null) {
                        list.clear();
                    }
                }
                try {
                    String response = new String(arg2, "UTF-8");
                    response = CommonUtil.transResponse(response);
                    if (response == null || response.equals("")) {
                        return;
                    }
                    JSONObject jsonObject = new JSONObject(response); // 解析
                    String cipher = jsonObject.getString("cipher");
                    String desStr = MyDES.decrypt(cipher); // 解密
                    MyLog.e(TAG, "send.onSuccess：" + desStr);
                    if (TextUtils.isEmpty(desStr))
                        return;
                    BaseModel<RegularRecodeList> result = new Gson().fromJson(desStr, new TypeToken<BaseModel<RegularRecodeList>>() {
                    }.getType());
                    if (result.isSuccess()) {
                        list.addAll(result.getData().getList());
                        String count = result.getData().getTotalCount();
                        int count_in = Integer.parseInt(count) - (list.size()) + PAGE_COUNT;
                        int is_page = 0;
                        if (count_in / PAGE_COUNT > 1) {
                            is_page = count_in / PAGE_COUNT;
                        } else {
                            is_page = count_in % PAGE_COUNT;
                        }
                        if (is_page <= 0) {
                            mListView.setPullLoadEnable(false);
                            if (list != null && list.size() > 0) {
                                mListView.setRest(true);
                                mListView.loaded();
                                mAdapter.notifyDataSetChanged();
                            }
                        } else {
                            mListView.setPullLoadEnable(true);
                        }
                    } else {
                        mListView.setPullLoadEnable(false);
                        ToastUtil.show(RegularAssetsActivity.this, result.getMsg());
                    }
                    if (mAdapter != null) {
                        mAdapter.notifyDataSetChanged();
                    }
                    if (list.size() <= 0) {
                        rlNoData.setVisibility(View.VISIBLE);
                        mAdapter.notifyDataSetChanged();
                    } else {
                        rlNoData.setVisibility(View.GONE);
                        mAdapter.notifyDataSetChanged();
                    }
                    isSend = true;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void sendMyAccount() throws Exception {
        isSendMyAccount = false;
        JSONObject obj = new JSONObject();
        obj.put("uid", uid);
        if (productCommonModel.getStatus().equals("0")) {
            obj.put("type", "regular");
        } else if (productCommonModel.getStatus().equals("1")) {
            obj.put("type", "daily_gain");
        } else {
            obj.put("type", "safe");
        }
        RequestParams params = new RequestParams();
        String desStr = MyDES.encrypt(obj.toString());
        params.put("cipher", desStr);
        MyhttpClient.desPost(UrlConfig.USER_ACCOUNT, params, new AsyncHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
            }

            @Override
            public void onFinish() {
                super.onFinish();
                isSendMyAccount = true;
            }

            @Override
            public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
                super.onFailure(arg0, arg1, arg2, arg3);
                isSendMyAccount = true;
                ToastUtil.show(RegularAssetsActivity.this, R.string.default_error);
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
                    MyLog.e(TAG, "sendMyAccount.onSuccess：" + desStr);
                    if (TextUtils.isEmpty(desStr))
                        return;
                    JSONObject result = new JSONObject(desStr);
                    if (result.has("code")) {
                        int code = result.optInt("code");
                        if (code == 1000) {
                            JSONObject dataObj = result.getJSONObject("data");
                            if (dataObj.has("goods_type")) {
                                goods_type = dataObj.optString("goods_type");
                            }
                            if (dataObj.has("type")) {
                                type = dataObj.optString("type");
                            }
                            String earn = dataObj.optString("yesterday_amt");
                            tv_income.setText(earn);
                            if (dataObj.has("total_earnings")) {
                                tv_allIncome.setText(dataObj.optString("total_earnings"));
                            }
                            if (dataObj.has("total_yield")) {
                                tv_assets.setText(dataObj.optString("total_yield"));
                            }
                            if (dataObj.has("invest_capital")) {
                                tv_capital.setText(dataObj.optString("invest_capital"));
                            }
                        } else {
                            ToastUtil.show(RegularAssetsActivity.this, result.optString("msg"));
                        }
                    }
                    isSendMyAccount = true;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 限购接口调用 @author Ysw created at 2017/3/15 19:32
     */
    private void getProductLimit() throws Exception {
        RequestParams params = new RequestParams();
        String uid = PreferencesUtils.getValueFromSPMap(this, PreferencesUtils.Keys.UID);
        JSONObject cipher = new JSONObject();
        cipher.put(PreferencesUtils.Keys.UID, uid);
        params.put("cipher", MyDES.encrypt(cipher.toString()));
        MyhttpClient.desPost(UrlConfig.MAIN_PRODUCT_LIMIT, params, new AsyncHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                MyLog.e(TAG, "getProductLimit onStart");
            }

            @Override
            public void onFinish() {
                super.onFinish();
            }

            @Override
            public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
                super.onFailure(arg0, arg1, arg2, arg3);
                ToastUtil.show(RegularAssetsActivity.this, R.string.default_error);
            }

            @Override
            public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
                MyLog.e(TAG, "getProductLimit onSuccess");
                super.onSuccess(arg0, arg1, arg2);
                try {
                    String response = new String(arg2, "UTF-8");
                    response = CommonUtil.transResponse(response);
                    JSONObject obj = new JSONObject(response);
                    String cipher = obj.getString("cipher");
                    String desc = MyDES.decrypt(cipher);
                    MyLog.e(TAG, "getProductLimit.onSuccess：" + desc);
                    BaseModel<DayUpLimitList> model = new Gson().fromJson(desc, new TypeToken<BaseModel<DayUpLimitList>>() {
                    }.getType());
                    if (model.isSuccess()) {
                        dayUpLimit = model;
                        setLimit();
                    } else {
                        ToastUtil.show(RegularAssetsActivity.this, model.getMsg());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }


    private void isDismissWaiting() {
        if (isSend && isSendMyAccount) {
            dismissWaittingDialog();
        }
    }

    protected void sendVerifyuser(final int num) throws Exception {
        RequestParams params = new RequestParams();
        UidObj obj = new UidObj(uid);
        String desStr = MyDES.encrypt(new Gson().toJson(obj));
        params.put("cipher", desStr);
        MyhttpClient.desPost(UrlConfig.USER_BANKINFO, params, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                super.onStart();
                showWaittingDialog(RegularAssetsActivity.this);
            }

            @Override
            public void onFinish() {
                super.onFinish();
                dismissWaittingDialog();
            }

            @Override
            public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
                super.onFailure(arg0, arg1, arg2, arg3);
                ToastUtil.show(RegularAssetsActivity.this, R.string.default_error);
            }

            @Override
            public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
                super.onSuccess(arg0, arg1, arg2);
                String response;
                try {
                    response = new String(arg2, "UTF-8");
                    JSONObject jsonObject = new JSONObject(response);
                    String cipher = jsonObject.getString("cipher");
                    String desc = MyDES.decrypt(cipher);
                    if (TextUtils.isEmpty(desc))
                        return;
                    BaseModel<UserBindinfo> result = new Gson().fromJson(desc, new TypeToken<BaseModel<UserBindinfo>>() {
                    }.getType());
                    if (result.isSuccess()) {
                        UserBindinfo state = result.getData();
                        if (state == null) return;
                        JinrApp.instance().state_bankBind = Integer.valueOf(state.getState_bankBind());
                        JinrApp.instance().state_tradePassword = Integer.valueOf(state.getState_tradePassword());
                        PreferencesUtils.putIntToSPMap(RegularAssetsActivity.this, PreferencesUtils.Keys.IS_BIND_CARD, Integer.valueOf(state.getState_bankBind()));
                        PreferencesUtils.putIntToSPMap(RegularAssetsActivity.this, PreferencesUtils.Keys.IS_SET_TRADE_PWD, Integer.valueOf(state.getState_tradePassword()));
                        if (num == 1) {
                            turnInMethod();
                        }
                    } else {
                        ToastUtil.show(RegularAssetsActivity.this, result.getMsg());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mRefreshBroadcastReceiver);
    }

}
