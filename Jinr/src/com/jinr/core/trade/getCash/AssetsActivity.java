package com.jinr.core.trade.getCash;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jinr.core.JinrApp;
import com.jinr.core.R;
import com.jinr.core.balance.CurrentOutActivity;
import com.jinr.core.base.BaseActivity;
import com.jinr.core.config.Check;
import com.jinr.core.config.EventBusKey;
import com.jinr.core.config.UrlConfig;
import com.jinr.core.gift.BonusCenterActivity;
import com.jinr.core.more.CjWentiActivity;
import com.jinr.core.my_change.ProfitAmountActivity;
import com.jinr.new_mvp.ui.activity.NewLoginActivity;
import com.jinr.core.trade.purchase.CurrentPurchaseFirstActivity;
import com.jinr.core.trade.record.TradeRecordActivity;
import com.jinr.core.utils.CommonUtil;
import com.jinr.core.utils.DensityUtil;
import com.jinr.core.utils.MyDES;
import com.jinr.core.utils.MyLog;
import com.jinr.core.utils.MyhttpClient;
import com.jinr.core.utils.PreferencesUtils;
import com.jinr.core.utils.ToastUtil;
import com.jinr.core.utils.UmUtils;
import com.jinr.graph_view.CustomLabelFormatter;
import com.jinr.graph_view.GraphView;
import com.jinr.graph_view.GraphView.GraphViewData;
import com.jinr.graph_view.GraphViewSeries;
import com.jinr.graph_view.GraphViewSeries.GraphViewSeriesStyle;
import com.jinr.graph_view.LineGraphView;
import com.jinr.pulltorefresh.CustomJinrScrollView;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;
import org.simple.eventbus.Subscriber;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import model.BaseModel;
import model.ProductCommonModel;
import model.RegularAccount;
import model.UidObj;
import model.UserBindinfo;

public class AssetsActivity extends BaseActivity implements OnClickListener {

    private ImageView title_left_img;// 返回
    private TextView title_center_text;// 标题
    private TextView total_assets_tv, total_revenue_tv, yesterdayEarn, // 总资产,总收益，昨日收益
            tv_million_money, freeze_money_tv,// 万份收益，点击次数，正转出
            tv_one_week, tv_one_month, total_money;// 近一周，近一月,红包金额
    private String user_id;
    private LinearLayout layoutChart;// 折线图
    private CustomJinrScrollView mRefreshableView;// 自定义下拉
    private boolean flag2 = true;
    private int count = 1;
    protected Button turn_in_tv, turn_out_tv;// 转出, 转入
    private RegularAccount regularAccount;
    private String product_code;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_assets);
        //EventBus.getDefault().register(this);
        findViewById();
        initData();
        initUI();
        setListener();
    }

    @Override
    public void onResume() {
        // 过渡版修改
        handler1.postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    showWaittingDialog(AssetsActivity.this);
                    send();
                    sendGetUrl(0);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, 1000);
        initUI();
        super.onResume();
    }

    @Override
    protected void initData() {
        regularAccount = (RegularAccount) getIntent().getExtras().get("account");
        product_code = getIntent().getStringExtra("product_code");
        user_id = PreferencesUtils.getValueFromSPMap(this, PreferencesUtils.Keys.UID);
    }

    @Override
    protected void findViewById() {
        yesterdayEarn = (TextView) findViewById(R.id.yesterday_earn);
        title_center_text = (TextView) findViewById(R.id.title_center_text);
        title_left_img = (ImageView) findViewById(R.id.title_left_img);
        mRefreshableView = (CustomJinrScrollView) findViewById(R.id.scroll_view_redresable);
        total_assets_tv = (TextView) findViewById(R.id.total_assets);// 总资产(总金额)
        total_revenue_tv = (TextView) findViewById(R.id.tv_total_revenue);// 总收益(累计收益)
        tv_million_money = (TextView) findViewById(R.id.tv_million_money);// 万份收益
        freeze_money_tv = (TextView) findViewById(R.id.tv_freeze_money);// 正转出
        tv_one_week = (TextView) findViewById(R.id.tv_one_week);// 近一周
        tv_one_month = (TextView) findViewById(R.id.tv_one_month);// 近一月
        total_money = (TextView) findViewById(R.id.total_money);// 红包金额
        turn_in_tv = (Button) findViewById(R.id.product_turn_in);// 转入
        turn_out_tv = (Button) findViewById(R.id.product_turn_out);// 转出
        layoutChart = (LinearLayout) findViewById(R.id.line_chart);// 折线图
    }

    @Override
    protected void initUI() {
        title_center_text.setText("活期资产");
    }

    @Override
    protected void setListener() {
        findViewById(R.id.ly_million_money).setOnClickListener(this);// 万份收益
        findViewById(R.id.ly_total_revenue).setOnClickListener(this);// 累计收益
        findViewById(R.id.ly_one_week).setOnClickListener(this);// 近一周收益
        findViewById(R.id.ly_one_month).setOnClickListener(this);// 近一月收益
        findViewById(R.id.ly_hongbao).setOnClickListener(this);// 红包金额
        findViewById(R.id.line_chart).setOnClickListener(this);// 七日年化收益
        findViewById(R.id.tv_qiri).setOnClickListener(this);//
        findViewById(R.id.iv_showing_ask).setOnClickListener(this);// 问号监听
        findViewById(R.id.details).setOnClickListener(this);// 明細
        // 转入转出
        turn_in_tv.setOnClickListener(this);
        turn_out_tv.setOnClickListener(this);
        title_left_img.setOnClickListener(this);
        mRefreshableView.setOnRefreshListener(new CustomJinrScrollView.OnRefreshListener() {

            @Override
            public void onRefresh() {
                loadData(0);
            }
        });
        mRefreshableView.onLoadComplete();// 刷新完成，默认先完成
        mRefreshableView.setOnTouchListener(new OnTouchListener() {
            private int lastY = 0;
            private int touchEventId = -9983761;
            @SuppressLint("HandlerLeak")
            Handler handler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    View scroller = (View) msg.obj;
                    if (msg.what == touchEventId) {
                        if (lastY == scroller.getScrollY()) {
                        } else {
                            handler.sendMessageDelayed(handler.obtainMessage(
                                    touchEventId, scroller), 1);
                            lastY = scroller.getScrollY();
                        }
                    }
                }
            };

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_UP:
                        try {
                            handler.sendMessageDelayed(handler.obtainMessage(touchEventId, v), 5);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    case MotionEvent.ACTION_MOVE:
                        break;
                    case MotionEvent.ACTION_DOWN:
                        break;
                    default:
                        break;
                }
                return false;
            }
        });
    }

    @Override
    public void onClick(View v) {// type=1表示每日收益金额，2表示每日收益率，3表示万份收益
        Intent intent;
        switch (v.getId()) {
            case R.id.title_left_img:
                finish();
                break;
            case R.id.ly_million_money:// 万份收益
                intent = new Intent(this, ProfitAmountActivity.class);
                intent.putExtra("type", "3");// 万份收益
                intent.putExtra("count", "30");// 万份收益
                intent.putExtra("typeStr", "万份收益");// 万份收益
                startActivity(intent);
                break;
            case R.id.rl_top:// 昨日收益
                UmUtils.customsEvent(AssetsActivity.this, UmUtils.CURRENTASSETS_PROFIT_CLICK, UmUtils.CURRENTASSETS_PROFIT_HUM);
                if (Check.is_login(this)) {
                    intent = new Intent(this, ProfitAmountActivity.class);
                    intent.putExtra("type", "1");
                    intent.putExtra("count", "30");
                    intent.putExtra("typeStr", "累计收益");
                    startActivity(intent);
                } else {
                    Intent intent_login = new Intent(this, NewLoginActivity.class);
                    intent_login.putExtra("flag", true);
                    startActivity(intent_login);
                }
                break;
            case R.id.ly_total_revenue:// 累计收益
                if (Check.is_login(this)) {
                    intent = new Intent(this, ProfitAmountActivity.class);
                    intent.putExtra("type", "1");
                    intent.putExtra("count", "30");
                    intent.putExtra("typeStr", "累计收益");
                    startActivity(intent);
                } else {
                    Intent intent_login = new Intent(this, NewLoginActivity.class);
                    intent_login.putExtra("flag", true);
                    startActivity(intent_login);
                }
                break;
            case R.id.ly_one_week:// 近一周收益
                if (Check.is_login(this)) {
                    intent = new Intent(this, ProfitAmountActivity.class);
                    intent.putExtra("type", "1");
                    intent.putExtra("count", "7");
                    intent.putExtra("typeStr", "近一周收益");
                    startActivity(intent);
                } else {
                    Intent intent_login = new Intent(this, NewLoginActivity.class);
                    intent_login.putExtra("flag", true);
                    startActivity(intent_login);
                }
                break;
            case R.id.ly_one_month:// 近一月收益
                if (Check.is_login(this)) {
                    intent = new Intent(this, ProfitAmountActivity.class);
                    intent.putExtra("type", "1");
                    intent.putExtra("count", "30");
                    intent.putExtra("typeStr", "近一月收益");
                    startActivity(intent);
                } else {
                    Intent intent_login = new Intent(this, NewLoginActivity.class);
                    intent_login.putExtra("flag", true);
                    startActivity(intent_login);
                }
                break;
            case R.id.ly_hongbao:// 红包金额
                if (!Check.is_login(this)) {// 判断是否登录状态
                    Intent intent_login = new Intent(this, NewLoginActivity.class);
                    startActivity(intent_login);
                    return;
                }
                Intent intent_login = new Intent(this, BonusCenterActivity.class);
                startActivity(intent_login);
                break;
            case R.id.tv_qiri:
            case R.id.line_chart:
                intent = new Intent(this, ProfitAmountActivity.class);
                intent.putExtra("type", "2");// 七日年化收益率
                intent.putExtra("count", "30");// 七日年化收益率
                intent.putExtra("typeStr", "七日年化收益率");// 七日年化收益率
                startActivity(intent);
                break;
            case R.id.details:// 跳转交易记录
                if (Check.is_login(this)) {
                    intent = new Intent(this, TradeRecordActivity.class);
                    intent.putExtra("goods_type", "3");
                    intent.putExtra("product_code", product_code);
                    startActivity(intent);
                    return;
                }
                intent = new Intent(this, NewLoginActivity.class);
                intent.putExtra("flag", true);
                startActivity(intent);
                break;
            case R.id.iv_showing_ask:
                // 跳转到问号要跳转的地方，支付宝是跳转到支付宝服务中心
                if (flag2) {
                    flag2 = false;
                    try {
                        sendGetUrl(1);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
            case R.id.product_turn_in:// 转入
                UmUtils.customsEvent(AssetsActivity.this, UmUtils.CURRENTASSETS_ROLLIN_CLICK, UmUtils.CURRENTASSETS_ROLLIN_HUM);
                if (!CommonUtil.isFastDoubleClick()) {
                    if (!Check.is_login(this)) {
                        intent = new Intent(AssetsActivity.this, NewLoginActivity.class);
                        startActivity(intent);
                        return;
                    } else {
                        try {
                            sendVerifyuser(1);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                break;
            case R.id.product_turn_out:// 转出
                UmUtils.customsEvent(AssetsActivity.this, UmUtils.CURRENTASSETS_ROLLOUT_CLICK, UmUtils.CURRENTASSETS_ROLLOUT_HUM);
                if (!CommonUtil.isFastDoubleClick()) {
                    if (!Check.is_login(this)) {
                        intent = new Intent(AssetsActivity.this, NewLoginActivity.class);
                        startActivity(intent);
                        return;
                    } else {
                        try {
                            sendVerifyuser(2);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                break;
            default:
                break;
        }
    }

    @Subscriber(tag = EventBusKey.OUT_SUCCESS)
    public void killSelf(boolean flag) {
        if (flag) {
            AssetsActivity.this.finish();
        }
    }


    String urlString = "";

    /**
     * 获取服务中心url
     */
    private void sendGetUrl(final int what) throws Exception {
        RequestParams params = new RequestParams();
        String uid = PreferencesUtils.getValueFromSPMap(this, PreferencesUtils.Keys.UID);
        UidObj obj = new UidObj(uid);
        String desStr = MyDES.encrypt(new Gson().toJson(obj));
        params.put("cipher", desStr);
        MyhttpClient.desPost(UrlConfig.SHOUYI_URL, params, new AsyncHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                showWaittingDialog(AssetsActivity.this);
            }

            @Override
            public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
                super.onFailure(arg0, arg1, arg2, arg3);
                dismissWaittingDialog();
                mRefreshableView.onRefreshComplete(true);
                ToastUtil.show(AssetsActivity.this, R.string.default_error);
            }

            @Override
            public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
                super.onSuccess(arg0, arg1, arg2);
                try {
                    dismissWaittingDialog();
                    String response = new String(arg2, "UTF-8");
                    response = CommonUtil.transResponse(response);
                    JSONObject obj = new JSONObject(response);
                    String cipher = obj.getString("cipher");
                    cipher = MyDES.decrypt(cipher);
                    MyLog.e(TAG, "onSuccess: " + cipher);
                    JSONObject object = new JSONObject(cipher);
                    if (object.getInt("code") == 1000) {
                        JSONObject data = object.getJSONObject("data");
                        urlString = data.getString("url");
                        tv_million_money.setText(data.getString("w_shouyu"));
                        JSONArray array = data.getJSONArray("shouyu");
                        initLineChartView(array);// 折线图
                        Message msg = Message.obtain();
                        msg.what = what;
                        msg.obj = urlString;
                        handler1.sendMessage(msg);
                        // // 成功刷新
                        mRefreshableView.onRefreshComplete(true);
                    } else {
                        mRefreshableView.onRefreshComplete(false);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void send() throws Exception {
        RequestParams params = new RequestParams();
        JSONObject cipher = new JSONObject();
        cipher.put("uid", user_id);
        params.put("cipher", MyDES.encrypt(cipher.toString()));
        MyhttpClient.desPost(UrlConfig.MONEY, params, new AsyncHttpResponseHandler() {
            @Override
            public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
                super.onFailure(arg0, arg1, arg2, arg3);
                dismissWaittingDialog();
                try {
                    count++;
                    if (count < 2) {
                        send();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                ToastUtil.show(AssetsActivity.this, getResources().getString(R.string.default_error));
            }

            @Override
            public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
                super.onSuccess(arg0, arg1, arg2);
                dismissWaittingDialog();
                try {
                    String response = new String(arg2, "UTF-8");
                    response = CommonUtil.transResponse(response);
                    JSONObject jsb = new JSONObject(response);
                    String cipher = jsb.getString("cipher");
                    cipher = MyDES.decrypt(cipher);
                    JSONObject result = new JSONObject(cipher);
                    int code = result.getInt("code");
                    if (code == 1000) {
                        JSONObject data = result.getJSONObject("data");
                        if (data.has("pre_yield_money")) {
                            if (data.getString("pre_yield_money").equals("暂无收益")) {
                                yesterdayEarn.setTextSize(60);
                            }
                            yesterdayEarn.setText(data.getString("pre_yield_money"));// 昨日收益
                        }
                        total_assets_tv.setText(data.getString("money"));// 总资产
                        total_revenue_tv.setText(data.getString("total_yield_money"));// 累计收益
                        tv_million_money.setText(data.getString("w_shouyu"));// 万份收益
                        tv_one_week.setText(data.getString("one_week"));// 周收益
                        tv_one_month.setText(data.getString("one_month"));
                        freeze_money_tv.setText(data.getString("freeze_money"));// 冻结金额（转出时）
                        total_money.setText(data.getString("hb_money"));// 红包金额
                    } else {
                        ToastUtil.show(AssetsActivity.this, result.getString("msg"));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }


    /**
     * 绘制折线
     */
    private void initLineChartView(JSONArray array) {
        String str1 = "0.00", str2 = "0.00", str3 = "0.00", str4 = "0.00", str5 = "0.00", str6 = "0.00", str7 = "0.00";
        long time1 = 0, time2 = 0, time3 = 0, time4 = 0, time5 = 0, time6 = 0, time7 = 0;
        try {
            str1 = array.getJSONObject(6).getString("shouyulv");
            str2 = array.getJSONObject(5).getString("shouyulv");
            str3 = array.getJSONObject(4).getString("shouyulv");
            str4 = array.getJSONObject(3).getString("shouyulv");
            str5 = array.getJSONObject(2).getString("shouyulv");
            str6 = array.getJSONObject(1).getString("shouyulv");
            str7 = array.getJSONObject(0).getString("shouyulv");

            time1 = new SimpleDateFormat("yyyy-MM-dd").parse("2017-" + array.getJSONObject(6).getString("c_time")).getTime();
            time2 = new SimpleDateFormat("yyyy-MM-dd").parse("2017-" + array.getJSONObject(5).getString("c_time")).getTime();
            time3 = new SimpleDateFormat("yyyy-MM-dd").parse("2017-" + array.getJSONObject(4).getString("c_time")).getTime();
            time4 = new SimpleDateFormat("yyyy-MM-dd").parse("2017-" + array.getJSONObject(3).getString("c_time")).getTime();
            time5 = new SimpleDateFormat("yyyy-MM-dd").parse("2017-" + array.getJSONObject(2).getString("c_time")).getTime();
            time6 = new SimpleDateFormat("yyyy-MM-dd").parse("2017-" + array.getJSONObject(1).getString("c_time")).getTime();
            time7 = new SimpleDateFormat("yyyy-MM-dd").parse("2017-" + array.getJSONObject(0).getString("c_time")).getTime();

        } catch (Exception e) {
            e.printStackTrace();
        }

//        long now = new Date().getTime();// 当前时间
//        long t = 86400000;
        GraphViewSeries exampleSeries = new GraphViewSeries("",
                new GraphViewSeriesStyle(Color.rgb(12, 114, 227), DensityUtil.dip2px(this, 3)),// 折线颜色(深蓝色)
                new GraphViewData[]{
                        new GraphViewData(time1, Double.parseDouble(str1)),
                        new GraphViewData(time2, Double.parseDouble(str2)),
                        new GraphViewData(time3, Double.parseDouble(str3)),
                        new GraphViewData(time4, Double.parseDouble(str4)),
                        new GraphViewData(time5, Double.parseDouble(str5)),
                        new GraphViewData(time6, Double.parseDouble(str6)),
                        new GraphViewData(time7, Double.parseDouble(str7))});
        GraphView graphView;
        graphView = new LineGraphView(this, "");
        ((LineGraphView) graphView).setDrawBackground(true);

        // ((LineGraphView) graphView)
        // .setBackgroundColor(Color.rgb(191, 230, 248));// 选择的背景颜色(淡蓝色)
        // 选择的背景颜色(淡蓝色)
        ((LineGraphView) graphView).setBackgroundColor(Color.parseColor("#200292D7"));
        ((LineGraphView) graphView).setDataPointsRadius(0);

        /** 字体色 */
        int fontColor = Color.parseColor("#9fa0a0");
        // 风格色//表格线颜色
        graphView.getGraphViewStyle().setGridColor(Color.parseColor("#D8DDE3"));
        graphView.getGraphViewStyle().setHorizontalLabelsColor(fontColor);
        graphView.getGraphViewStyle().setVerticalLabelsColor(fontColor);
        // x轴标签数
        graphView.getGraphViewStyle().setNumHorizontalLabels(7);
        // y轴标签数
        graphView.getGraphViewStyle().setNumVerticalLabels(7);
        // 隐藏y轴标签
        graphView.setShowVerticalLabels(false);
        // 字号 yx轴
        graphView.getGraphViewStyle().setTextSize(DensityUtil.dip2px(this, 12));
        // 图标利率数值字号
        graphView.getGraphViewStyle().setTextSizeDot(DensityUtil.dip2px(this, 15));
        // 虚拟字体 为了空间大一点
        // graphView.getGraphViewStyle().setTextSizeDots(
        // DensityUtil.dip2px(this, 16));
        graphView.getGraphViewStyle().setVerticalLabelsAlign(Align.RIGHT);
        // 设置宽度(两列的宽度)
        graphView.getGraphViewStyle().setVerticalLabelsWidth(DensityUtil.dip2px(this, 37));
        graphView.addSeries(exampleSeries);
        final SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd", Locale.CHINESE);
        graphView.setCustomLabelFormatter(new CustomLabelFormatter() {
            @Override
            public String formatLabel(double value, boolean isValueX) {
                if (isValueX) {
                    Date d = new Date((long) value);
                    return dateFormat.format(d);
                }
                return null;
            }
        });
        layoutChart.addView(graphView);
    }

    // 跳转到鲸鱼服务中心
    private Handler handler1 = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    flag2 = true;// 防止重复提交
                    urlString = (String) msg.obj;
                    Intent intent = new Intent(AssetsActivity.this, CjWentiActivity.class);
                    startActivity(intent);
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * 上下拉刷新加载数据方法
     */
    public void loadData(final int type) {
        new Thread() {
            @Override
            public void run() {
                switch (type) {
                    case 0:// 这里是下拉刷新
                        break;
                }
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (type == 0) {// 下拉刷新
                    // 通知Handler
                    myHandler.sendEmptyMessage(13);
                }
            }
        }.start();
    }

    private Handler myHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 13:
                    try {
                        send();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    try {
                        sendGetUrl(0);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * 获取用户是否绑定卡及是否设置交易密码
     *
     * @param action 1为转入，2为转出
     * @throws Exception
     */
    protected void sendVerifyuser(final int action) throws Exception {
        RequestParams params = new RequestParams();
        String uid = PreferencesUtils.getValueFromSPMap(this, PreferencesUtils.Keys.UID);
        UidObj obj = new UidObj(uid);
        String desStr = MyDES.encrypt(new Gson().toJson(obj));
        params.put("cipher", desStr);
        MyhttpClient.desPost(UrlConfig.USER_BANKINFO, params, new AsyncHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                showWaittingDialog(AssetsActivity.this);
            }

            @Override
            public void onFinish() {
                super.onFinish();
                dismissWaittingDialog();
            }

            @Override
            public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
                super.onFailure(arg0, arg1, arg2, arg3);
                try {
                    ToastUtil.show(AssetsActivity.this, R.string.default_error);
                } catch (Exception e) {
                    e.printStackTrace();
                }
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
                        if (state == null)
                            return;
                        JinrApp.instance().state_bankBind = Integer.valueOf(state.getState_bankBind());
                        JinrApp.instance().state_tradePassword = Integer.valueOf(state.getState_tradePassword());
                        PreferencesUtils.putIntToSPMap(AssetsActivity.this, PreferencesUtils.Keys.IS_BIND_CARD, Integer.valueOf(state.getState_bankBind()));
                        PreferencesUtils.putIntToSPMap(AssetsActivity.this, PreferencesUtils.Keys.IS_SET_TRADE_PWD, Integer.valueOf(state.getState_tradePassword()));
                        // 1为转入， 2为转出
                        if (action == 1) {
                            // 跳转至转入页面
                            Intent intent = new Intent(AssetsActivity.this, CurrentPurchaseFirstActivity.class);
                            ProductCommonModel productCommonModel = new ProductCommonModel();
                            productCommonModel.setAssetid(regularAccount.getAssetsid());
                            productCommonModel.setCode(regularAccount.getProductcode());
                            productCommonModel.setName(regularAccount.getNametext());
                            intent.putExtra("regular", productCommonModel);
                            startActivity(intent);
                        } else {
                            // 跳转至转出页面
                            if (JinrApp.instance().state_bankBind == 1 && JinrApp.instance().state_tradePassword == 1) {
                                sendFormerInsertCity();
                                return;
                            }
                            Intent intent = new Intent(AssetsActivity.this, CurrentOutActivity.class);
                            startActivity(intent);
                        }
                    } else {
                        ToastUtil.show(AssetsActivity.this, result.getMsg());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 获取用户城市是否为空
     */
    protected void sendFormerInsertCity() throws Exception {
        RequestParams params = new RequestParams();
        // params.put(UrlConfig.APP_KEY, UrlConfig.APP_VALUE);
        String uid = PreferencesUtils.getValueFromSPMap(AssetsActivity.this, PreferencesUtils.Keys.UID);
        JSONObject cipher = new JSONObject();
        cipher.put(PreferencesUtils.Keys.UID, uid);
        params.put("cipher", MyDES.encrypt(cipher.toString()));
        MyhttpClient.desPost(UrlConfig.FORMER_INSERT_CITY, params, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                super.onStart();
                showWaittingDialog(AssetsActivity.this);
            }

            @Override
            public void onFinish() {
                super.onFinish();
            }

            @Override
            public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
                super.onFailure(arg0, arg1, arg2, arg3);
                dismissWaittingDialog();
                try {
                    ToastUtil.show(AssetsActivity.this, R.string.default_error);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
                super.onSuccess(arg0, arg1, arg2);
                dismissWaittingDialog();
                try {
                    String response = new String(arg2, "UTF-8");
                    response = CommonUtil.transResponse(response);
                    JSONObject obj = new JSONObject(response);
                    String cipher = obj.getString("cipher");
                    String desc = MyDES.decrypt(cipher);
                    JSONObject job = new JSONObject(desc);
                    if (job.getInt("code") == 1000) {
                        int data = job.getInt("data");
                        if (data == 1) {
                            Intent intent = new Intent(AssetsActivity.this, CurrentOutActivity.class);
                            intent.putExtra("has_city", true);
                            startActivity(intent);
                        } else if (data == 0) {
                            Intent intent = new Intent(AssetsActivity.this, CurrentOutActivity.class);
                            intent.putExtra("has_city", false);
                            startActivity(intent);
                        }
                    } else {
                        ToastUtil.show(AssetsActivity.this, job.getString("msg"));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
