package com.jinr.core.my_change;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jinr.core.MainActivity;
import com.jinr.core.R;
import com.jinr.core.base.BaseActivity;
import com.jinr.core.config.UrlConfig;
import com.jinr.core.utils.CommonUtil;
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

import java.util.ArrayList;

import model.BaseModel;
import model.Profit;
import model.ProfitList;

public class NewProfitAmountActivity extends BaseActivity implements XListViewJinr.IXListViewListener {
    private XListViewJinr listView;
    private TextView title;
    private NewProfitAmountAdapter adapter;
    private ArrayList<Profit> list;
    private View headerView;
    private TextView totalMoney, describle;
    private View noDataView;
    private String nameTitle = "累计收益";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profit);
        initData();
        findViewById();
        initUI();
        setListener();
    }

    @Override
    protected void initData() {
        if (getIntent().getExtras() != null) {
            nameTitle = getIntent().getExtras().getString("name");
        }
        list = new ArrayList<>();
        headerView = LayoutInflater.from(this).inflate(R.layout.header_profit, null);
        noDataView = LayoutInflater.from(this).inflate(R.layout.no_data_layout, null);
    }

    @Override
    protected void findViewById() {
        listView = (XListViewJinr) findViewById(R.id.profit_list);
        title = (TextView) findViewById(R.id.title_center_text);
        totalMoney = (TextView) headerView.findViewById(R.id.total_money);
        describle = (TextView) headerView.findViewById(R.id.tv_describle);
        ((TextView) noDataView.findViewById(R.id.no_data_tv)).setText(getResources().getString(R.string.no_profit_record));
    }

    @Override
    protected void initUI() {
        title.setText(nameTitle);
        listView.setOverScrollMode(View.OVER_SCROLL_NEVER);// 防止魅族手机出现HOLD悬停
        listView.setPullLoadEnable(false);
        listView.setPullRefreshEnable(true);
        listView.setRefreshTime();
        adapter = new NewProfitAmountAdapter(this, list, nameTitle);
        listView.addHeaderView(headerView);
        listView.setAdapter(adapter);
        startPost();
    }

    private void startPost() {
        try {
            if (nameTitle.equals("昨日收益")) {
                describle.setText(getResources().getString(R.string.yesterday_earn));
                sendYesterday();
            } else if (nameTitle.equals("累计收益")) {
                describle.setText(getResources().getString(R.string.all_earn));
                sendAllProfit();
            } else {//具体收益列表
                describle.setText(nameTitle + "(元)");
                String type = getIntent().getStringExtra("type");
                getProductProfit(type);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void setListener() {
        listView.setXListViewListener(this, 1);
        findViewById(R.id.title_left_img).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void onLoadMore(int id) {

    }

    @Override
    public void onRefresh(int id) {
        try {
            if (list != null) {
                list.clear();
            }
            startPost();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 所有产品的昨日收益列表
     *
     * @throws Exception
     */
    private void sendYesterday() throws Exception {
        JSONObject obj = new JSONObject();
        obj.put("uid", PreferencesUtils.getValueFromSPMap(this, PreferencesUtils.Keys.UID));
        RequestParams params = new RequestParams();
        String desStr = MyDES.encrypt(obj.toString());
        params.put("cipher", desStr);
        MyhttpClient.desPost(UrlConfig.USER_YESERDAY_PROFIT, params, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                super.onStart();
                showWaittingDialog(NewProfitAmountActivity.this);
            }

            @Override
            public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
                super.onFailure(arg0, arg1, arg2, arg3);
                dismissWaittingDialog();
                ToastUtil.show(MainActivity.instance, R.string.default_error);
                isNoData();
            }

            @Override
            public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
                super.onSuccess(arg0, arg1, arg2);
                dismissWaittingDialog();
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
                    BaseModel<ProfitList> result = new Gson().fromJson(desStr, new TypeToken<BaseModel<ProfitList>>() {
                    }.getType());
                    if (result.isSuccess()) {
                        if (list != null) {
                            list.clear();
                        }
                        list.addAll(result.getData().getList());
                        adapter.notifyDataSetChanged();
                        if (!TextUtils.isEmpty(result.getData().getTotal())) {
                            totalMoney.setText(result.getData().getTotal());
                        } else {
                            totalMoney.setText(getResources().getString(R.string.zero_zero));
                        }
                    }
                    isNoData();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 所有产品的总收益列表
     *
     * @throws Exception
     */
    private void sendAllProfit() throws Exception {
        if (list != null) {
            list.clear();
            adapter.notifyDataSetChanged();
        }
        JSONObject obj = new JSONObject();
        obj.put("uid", PreferencesUtils.getValueFromSPMap(this, PreferencesUtils.Keys.UID));
        RequestParams params = new RequestParams();
        String desStr = MyDES.encrypt(obj.toString());
        params.put("cipher", desStr);
        MyhttpClient.desPost(UrlConfig.USER_ALL_PROFIT, params, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                super.onStart();
                showWaittingDialog(NewProfitAmountActivity.this);
            }

            @Override
            public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
                super.onFailure(arg0, arg1, arg2, arg3);
                dismissWaittingDialog();
                ToastUtil.show(MainActivity.instance, R.string.default_error);
                isNoData();
            }

            @Override
            public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
                super.onSuccess(arg0, arg1, arg2);
                dismissWaittingDialog();
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
                    MyLog.e(TAG, "onSuccess: " + desStr);
                    BaseModel<ProfitList> result = new Gson().fromJson(desStr, new TypeToken<BaseModel<ProfitList>>() {
                    }.getType());
                    if (result.isSuccess() && result.getData().getList() != null) {
                        if (list != null) {
                            list.clear();
                        }
                        for (int i = 0; i < result.getData().getList().size(); i++) {
                            if (result.getData().getList().get(i) != null) {
                                list.add(result.getData().getList().get(i));
                            }
                        }
                        adapter.notifyDataSetChanged();
                        if (!TextUtils.isEmpty(result.getData().getTotal())) {
                            totalMoney.setText(result.getData().getTotal());
                        } else {
                            totalMoney.setText(getResources().getString(R.string.zero_zero));
                        }
                    }
                    if (list.size() > 0) {
                        setOnItemListener();
                    } else {
                        listView.setOnItemClickListener(null);
                    }
                    isNoData();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 活期/定期/日增息/红包/保险箱 具体收益列表
     *
     * @param type
     * @throws Exception
     */
    private void getProductProfit(String type) throws Exception {
        JSONObject obj = new JSONObject();
        obj.put("uid", PreferencesUtils.getValueFromSPMap(this, PreferencesUtils.Keys.UID));
        obj.put("type", type);
        RequestParams params = new RequestParams();
        String desStr = MyDES.encrypt(obj.toString());
        params.put("cipher", desStr);
        MyhttpClient.desPost(UrlConfig.USER_PRODUCT_PROFIT, params, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                super.onStart();
                showWaittingDialog(NewProfitAmountActivity.this);
            }

            @Override
            public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
                super.onFailure(arg0, arg1, arg2, arg3);
                dismissWaittingDialog();
                ToastUtil.show(MainActivity.instance, R.string.default_error);
                isNoData();
            }

            @Override
            public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
                super.onSuccess(arg0, arg1, arg2);
                dismissWaittingDialog();
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
                    MyLog.e(TAG, "onSuccess: " + desStr);
                    BaseModel<ProfitList> result = new Gson().fromJson(desStr, new TypeToken<BaseModel<ProfitList>>() {
                    }.getType());
                    if (result.isSuccess()) {
                        if (list != null) {
                            list.clear();
                        }
                        listView.setOnItemClickListener(null);
                        if (result.getData().getList() != null) {
                            ArrayList<Profit> plist = result.getData().getList();
                            for (int i = 0; i < plist.size(); i++) {
                                plist.get(i).setName(nameTitle);
                            }
                            list.addAll(result.getData().getList());
                            adapter.notifyDataSetChanged();
                        }
                        if (!TextUtils.isEmpty(result.getData().getTotal())) {
                            totalMoney.setText(result.getData().getTotal());
                        } else {
                            totalMoney.setText(getResources().getString(R.string.zero_zero));
                        }
                    }
                    isNoData();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void isNoData() {//是否为暂无记录
        listView.stopRefresh();
        listView.removeHeaderView(noDataView);
        if (list.size() == 0) {
            listView.addHeaderView(noDataView);
            listView.setOnItemClickListener(null);
        }
    }

    private void setOnItemListener() {
        listView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                position = position - 2;
                if (position >= 0) {
                    Intent intent = new Intent(NewProfitAmountActivity.this, NewProfitAmountActivity.class);
                    intent.putExtra("type", list.get(position).getType());
                    intent.putExtra("name", list.get(position).getName());
                    startActivity(intent);
                }
            }
        });
    }
}
