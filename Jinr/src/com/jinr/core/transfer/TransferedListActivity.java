package com.jinr.core.transfer;

import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jinr.core.MainActivity;
import com.jinr.core.R;
import com.jinr.core.base.BaseActivity;
import com.jinr.core.config.UrlConfig;
import com.jinr.core.transfer.adapter.TransferSuccessAdapter;
import com.jinr.core.utils.CommonUtil;
import com.jinr.core.utils.MyDES;
import com.jinr.core.utils.MyhttpClient;
import com.jinr.core.utils.ToastUtil;
import com.jinr.graph_view.XListViewJinr;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import model.BaseModel;
import model.TransferSuccessInfo;
import model.TransferSuccessInfoList;

public class TransferedListActivity extends BaseActivity implements XListViewJinr.IXListViewListener {
    private XListViewJinr listView;
    private ImageView titleBack; // title左边图片
    private TextView title_center_text, no_more_tv, total_money_money; // title标题
    private View headView;
    private View footView;
    private TextView no_data_tv;
    private TransferSuccessAdapter mAdapter;
    private List<TransferSuccessInfo> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transfered_lay);
        findViewById();
        initUI();
        setListener();
        initData();
    }

    @Override
    protected void initData() {
        try {
            send();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    protected void findViewById() {
        headView = LayoutInflater.from(TransferedListActivity.this).inflate(R.layout.no_data_layout, null);
        footView = LayoutInflater.from(TransferedListActivity.this).inflate(R.layout.activity_transfered_food_lay, null);
        no_data_tv = (TextView) headView.findViewById(R.id.no_data_tv);
        no_data_tv.setText("暂无转让哦~");
        no_more_tv = (TextView) footView.findViewById(R.id.no_more_tv);
        no_more_tv.setVisibility(View.INVISIBLE);
        total_money_money = (TextView) findViewById(R.id.total_money_money);
        listView = (XListViewJinr) findViewById(R.id.view_list);
        listView.setOverScrollMode(View.OVER_SCROLL_NEVER);// 防止魅族手机出现HOLD悬停
        listView.setPullLoadEnable(false);
        listView.setPullRefreshEnable(true);
        listView.setRefreshTime();
        listView.setXListViewListener(this, 1);
        mAdapter = new TransferSuccessAdapter(TransferedListActivity.this, list);
        listView.setAdapter(mAdapter);
        titleBack = (ImageView) findViewById(R.id.title_left_img);
        titleBack.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        title_center_text = (TextView) findViewById(R.id.title_center_text);
        title_center_text.setText("已转让");
    }

    @Override
    protected void initUI() {

    }

    @Override
    protected void setListener() {

    }

    private void send() throws Exception {
        listView.setRest(false);
        listView.removeHeaderView(headView);
        RequestParams params = new RequestParams();
        MyhttpClient.desPost(UrlConfig.TRANSFER_SUCCESS, params, new AsyncHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
            }

            @Override
            public void onFinish() {
                super.onFinish();
                listView.stopRefresh();
                listView.stopLoadMore();
            }

            @Override
            public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
                super.onFailure(arg0, arg1, arg2, arg3);
                ToastUtil.show(MainActivity.instance, R.string.default_error);
                listView.stopRefresh();
                listView.stopLoadMore();
            }

            @Override
            public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
                super.onSuccess(arg0, arg1, arg2);
                try {
                    listView.stopRefresh();
                    listView.stopLoadMore();
                    String response = new String(arg2, "UTF-8");
                    response = CommonUtil.transResponse(response);
                    if (response == null || response.equals("")) return;
                    JSONObject jsonObject = new JSONObject(response); // 解析
                    String cipher = jsonObject.getString("cipher");
                    String desStr = MyDES.decrypt(cipher); // 解密
                    if (TextUtils.isEmpty(desStr)) return;
                    list.clear();
                    if (mAdapter != null) {
                        mAdapter.popList.clear();
                        mAdapter.notifyDataSetChanged();
                        listView.setSelection(0);
                    }
                    BaseModel<TransferSuccessInfoList> result = new Gson().fromJson(desStr, new TypeToken<BaseModel<TransferSuccessInfoList>>() {
                    }.getType());
                    if (result.isSuccess() && result.getData().getList() != null) {
                        for (int i = 0; i < result.getData().getList().size(); i++) {
                            list.add(result.getData().getList().get(i));
                        }
                    }
                    String totalMoney = "转让累计成交额: " + "<font color='#0c72e3'><b>" + result.getData().getTransfer_money() + "</b></font>" + "元";
                    total_money_money.setText(Html.fromHtml(totalMoney));
                    if (list.size() > 0) {
                        no_more_tv.setVisibility(View.VISIBLE);
                        listView.removeHeaderView(headView);
                        listView.addFooterView(footView);
                    } else {
                        no_more_tv.setVisibility(View.GONE);
                        listView.addHeaderView(headView);
                    }
                    mAdapter.notifyDataSetChanged();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onRefresh(int id) {
        listView.setRefreshTime();
        try {
            send();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onLoadMore(int id) {

    }
}
