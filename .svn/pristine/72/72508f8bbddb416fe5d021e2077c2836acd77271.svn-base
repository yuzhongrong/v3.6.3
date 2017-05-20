package com.jinr.core.trade.record;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.jinr.core.MainActivity;
import com.jinr.core.R;
import com.jinr.core.base.BaseActivity;
import com.jinr.core.config.UrlConfig;
import com.jinr.core.utils.CommonUtil;
import com.jinr.core.utils.DensityUtil;
import com.jinr.core.utils.MyDES;
import com.jinr.core.utils.MyLog;
import com.jinr.core.utils.MyhttpClient;
import com.jinr.core.utils.PreferencesUtils;
import com.jinr.core.utils.ToastUtil;
import com.jinr.graph_view.XListViewJinr;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import model.BaseModel;
import model.Record;
import model.RecordList;

public class TradeRecordActivity extends BaseActivity implements XListViewJinr.IXListViewListener {
    private String uid;
    private XListViewJinr listView;
    private ImageView titleBack; // title左边图片
    private TextView title_center_text; // title标题
    private List<Record> mRecordList = new ArrayList<>();
    private List<Record> list = new ArrayList<>();
    private LinearLayout ll;
    private RadioGroup radioGroup;
    public int PAGE_COUNT = 10;
    private TradeRecordAdapter adapter = null;
    private String count;
    private int count_in;
    private String goods_type, name;
    private View headView;
    private LinearLayout llNoData;
    private String product_code;
    private RadioButton turn_out;
    private RadioButton turn_in;
    private RadioButton transfer_btn;
    // 转入,转让，到期都为"1" @author Ysw created at 2017/2/10 17:17
    private int TURN_IN = 1;
    // 转出 @author Ysw created at 2017/2/10 17:18
    private int TURN_OUT = 2;
    // 交易的类型,初始化类型为转入 @author Ysw created at 2017/2/10 17:20
    private int turnType = TURN_IN;
    // 判断是转让还是转出 @author Ysw created at 2017/2/13 10:25
    private boolean isTransfer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.trade_record);
        initData();
        findViewById();
        initUI();
        setListener();
    }

    /**
     * goods_type类型的定义:"3"是活期;"4"是定期;"5"是日增息;"8"是保险箱
     *
     * @author Ysw created at 2017/2/10 17:29
     */
    @Override
    protected void initData() {
        goods_type = getIntent().getStringExtra("goods_type");
        product_code = getIntent().getStringExtra("product_code");
        name = getIntent().getExtras().getString("name");
        adapter = new TradeRecordAdapter(this, list, goods_type);
        uid = PreferencesUtils.getValueFromSPMap(this, PreferencesUtils.Keys.UID);
    }

    @Override
    protected void findViewById() {
        titleBack = (ImageView) findViewById(R.id.title_left_img);
        ll = (LinearLayout) findViewById(R.id.ll_turn_in_out);
        title_center_text = (TextView) findViewById(R.id.title_center_text);
        radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        // RadioBution第一个按钮 转入 @author Ysw created at 2017/2/10 16:32
        turn_in = (RadioButton) findViewById(R.id.turn_in);
        // RadioBution第二个按钮 转出 @author Ysw created at 2017/2/10 16:32
        turn_out = (RadioButton) findViewById(R.id.turn_out);
        // RadioBution第三个按钮 转让 @author Ysw created at 2017/2/10 16:32
        transfer_btn = (RadioButton) findViewById(R.id.transfer_btn);
        listView = (XListViewJinr) findViewById(R.id.view_list);
        listView.setOverScrollMode(View.OVER_SCROLL_NEVER);// 防止魅族手机出现HOLD悬停
        listView.setPullLoadEnable(false);
        listView.setRefreshTime();
        listView.setXListViewListener(this, 1);
        listView.setAdapter(adapter);
    }

    @Override
    protected void initUI() {
        RadioGroup.LayoutParams params = new RadioGroup.LayoutParams(DensityUtil.dip2px(this, 87), DensityUtil.dip2px(this, 30), 1);
        int distance = DensityUtil.dip2px(this, 45);
        params.setMargins(distance, 0, distance, 0);
        if ("3".equals(goods_type)) {//活期
            turn_in.setText(getResources().getString(R.string.record_trun_in));
            turn_out.setText(getResources().getString(R.string.record_trun_out));
            transfer_btn.setVisibility(View.GONE);
            turn_in.setLayoutParams(params);
            turn_out.setLayoutParams(params);
        } else if ("5".equals(goods_type)) {//日增息
            transfer_btn.setVisibility(View.VISIBLE);
            turn_in.setText(getResources().getString(R.string.record_trun_in));
            turn_out.setText(getResources().getString(R.string.record_transfer));
            transfer_btn.setText(getResources().getString(R.string.record_return));
        } else if ("4".equals(goods_type)) {//定期
            turn_in.setText(getResources().getString(R.string.record_trun_in));
            turn_out.setText(getResources().getString(R.string.record_return));
            transfer_btn.setVisibility(View.GONE);
            turn_in.setLayoutParams(params);
            turn_out.setLayoutParams(params);
        } else if ("8".equals(goods_type)) {//保险箱
            turn_in.setText(getResources().getString(R.string.record_trun_in));
            turn_out.setText(getResources().getString(R.string.record_transfer));
            transfer_btn.setVisibility(View.GONE);
            turn_in.setLayoutParams(params);
            turn_out.setLayoutParams(params);
        } else {
            turn_in.setText(getResources().getString(R.string.record_trun_in));
            turn_out.setText(getResources().getString(R.string.record_trun_out));
            transfer_btn.setVisibility(View.GONE);
            turn_in.setLayoutParams(params);
            turn_out.setLayoutParams(params);
        }
        //无数据记录布局
        headView = LayoutInflater.from(TradeRecordActivity.this).inflate(R.layout.no_data_layout, null);
        llNoData = (LinearLayout) headView.findViewById(R.id.ll_no_data);
        if (TextUtils.isEmpty(name)) {
            title_center_text.setText(getResources().getString(R.string.currnet_trade));
        } else {
            title_center_text.setText(name + "交易记录");
        }
        ll.setVisibility(View.VISIBLE);
        findViewById(R.id.view).setVisibility(View.VISIBLE);
        try {
            send(0);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void setListener() {
        titleBack.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        radioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                listView.setPullLoadEnable(false);
                switch (checkedId) {
                    // 转入按钮 @author Ysw created at 2017/2/13 10:30
                    case R.id.turn_in:
                        try {
                            adapter.setDescrible(goods_type);
                            adapter.notifyDataSetChanged();
                            turnType = TURN_IN;
                            isTransfer = false;
                            send(0);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    // 转让或者转出按钮 @author Ysw created at 2017/2/13 10:29
                    case R.id.turn_out:
                        try {
                            if ("5".equals(goods_type) || "8".equals(goods_type)) {
                                adapter.setDescrible("6");
                                turnType = TURN_OUT;
                                isTransfer = true;
                            } else {
                                adapter.setDescrible(goods_type);
                                turnType = TURN_OUT;
                                isTransfer = false;
                            }
                            adapter.notifyDataSetChanged();
                            send(0);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    // 到期按钮 @author Ysw created at 2017/2/13 10:29
                    case R.id.transfer_btn:
                        try {
                            isTransfer = false;
                            turnType = TURN_OUT;
                            adapter.setDescrible(goods_type);
                            adapter.notifyDataSetChanged();
                            send(0);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    default:
                        break;
                }
            }
        });
    }

    /**
     * 获取交易记录列表 @author Ysw created at 2017/2/6 15:52
     */
    private void send(final int is_more) throws Exception {
        listView.setRest(false);
        listView.removeHeaderView(headView);
        showWaittingDialog(TradeRecordActivity.this);
        JSONObject obj = new JSONObject();
        obj.put("uid", uid);
        obj.put("product_code", product_code);
        if (isTransfer) {
            obj.put("goods_type", "6");
            obj.put("type", "0");
        } else {
            obj.put("goods_type", goods_type);
            obj.put("type", "" + turnType);
        }
        if (is_more == 0) {
            obj.put("page", "1");
            obj.put("pageSize", PAGE_COUNT);
            if (list.size() > 0) {
                list.clear();
                adapter.notifyDataSetChanged();
            }
        } else {
            int page = (int) (Math.ceil((list.size()) * 1.0 / PAGE_COUNT) + 1);
            obj.put("page", page);
            obj.put("pageSize", PAGE_COUNT);
        }
        RequestParams params = new RequestParams();
        String desStr = MyDES.encrypt(obj.toString());
        params.put("cipher", desStr);
        MyhttpClient.desPost(UrlConfig.GET_RECORD, params, new AsyncHttpResponseHandler() {
            @Override
            public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
                super.onFailure(arg0, arg1, arg2, arg3);
                ToastUtil.show(MainActivity.instance, R.string.default_error);
                dismissWaittingDialog();
                listView.stopRefresh();
                listView.stopLoadMore();
            }

            int is_page = 0;

            @Override
            public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
                super.onSuccess(arg0, arg1, arg2);
                dismissWaittingDialog();
                listView.stopRefresh();
                listView.stopLoadMore();
                if (is_more == 0) {
                    list.clear();
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
                    if (TextUtils.isEmpty(desStr))
                        return;
                    BaseModel<RecordList> result = new Gson().fromJson(desStr, new TypeToken<BaseModel<RecordList>>() {
                    }.getType());
                    if (result.isSuccess()) {
                        int len = result.getData().getList().size();
                        for (int i = 0; i < len; i++) {
                            mRecordList = (result.getData().getList().get(i).getList());
                            for (int j = 0; j < mRecordList.size(); j++) {
                                list.add(mRecordList.get(j));
                            }
                        }

                        if (adapter == null) {
                            adapter = new TradeRecordAdapter(TradeRecordActivity.this, list, goods_type);
                            listView.setAdapter(adapter);
                        } else {
                            adapter.notifyDataSetChanged();
                        }
                        count = result.getData().getTotal_count();
                        count_in = Integer.parseInt(count) - (list.size()) + PAGE_COUNT;
                        if (count_in / PAGE_COUNT > 1) {
                            is_page = count_in / PAGE_COUNT;
                        } else {
                            is_page = count_in % PAGE_COUNT;
                        }
                        if (is_page <= 0) {
                            listView.setPullLoadEnable(false);
                            if (list.size() > 0) {
                                listView.setRest(true);
                                listView.loaded();
                            }
                        } else {
                            listView.setPullLoadEnable(true);
                        }
                    } else {
                        if (adapter != null) {
                            adapter.notifyDataSetChanged();
                        }
                        listView.setPullLoadEnable(false);
                        ToastUtil.show(TradeRecordActivity.this, result.getMsg());
                    }
                    System.out.println(list.size());
                    if (list.size() > 0) {
                        listView.removeHeaderView(headView);
                        adapter.notifyDataSetChanged();
                    } else {
                        listView.addHeaderView(headView);
                        adapter.notifyDataSetChanged();
                    }
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (JsonSyntaxException e) {
                    MyLog.i("json解析错误", "解析错误");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Listview 的下拉刷新的回调接口 @author Ysw created at 2017/2/6 15:53
     */
    @Override
    public void onRefresh(int id) {
        try {
            send(0);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Listview 的加载更多回调接口 @author Ysw created at 2017/2/6 15:54
     */
    @Override
    public void onLoadMore(int id) {
        try {
            send(1);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
