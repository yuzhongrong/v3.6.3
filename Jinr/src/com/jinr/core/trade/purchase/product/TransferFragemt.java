package com.jinr.core.trade.purchase.product;


import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jinr.core.MainActivity;
import com.jinr.core.R;
import com.jinr.core.base.BaseFragment;
import com.jinr.core.config.EventBusKey;
import com.jinr.core.config.UrlConfig;
import com.jinr.core.transfer.TransferInfoActivity;
import com.jinr.core.transfer.TransferedListActivity;
import com.jinr.core.transfer.adapter.TransferAdapter;
import com.jinr.core.utils.CommonUtil;
import com.jinr.core.utils.DensityUtil;
import com.jinr.core.utils.MyDES;
import com.jinr.core.utils.MyLog;
import com.jinr.core.utils.MyhttpClient;
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
import model.TransferInfo;
import model.TransferInfoList;

/**
 * 转让市场Fragment界面 @author Ysw created at 2017/3/15 17:06
 */
public class TransferFragemt extends BaseFragment implements OnClickListener, XListViewJinr.IXListViewListener {
    private LinearLayout annualRateLay, transferAmountLay, allowanceAmountLay, residualMaturityLay;
    private ImageView annualRateImg, transferAmountImg, allowanceAmountImg, residualMaturityImg, goToTopimg, goToTransferImg;
    private TextView annual_rate_tv, transfer_amount_tv, allowance_amount_tv, residual_maturity_tv;
    private XListViewJinr listView;
    private View headView;
    private TextView no_data_tv;
    private TransferAdapter mAdapter;
    private List<TransferInfo> list = new ArrayList<>();
    public int PAGE_COUNT = 10;
    private int isAnnual = 0;
    private int isTransfer = 0;
    private int isAllowance = 0;
    private int isResidual = 0;
    private int tabState = 0;
    private int count = 0;
    private int count_in = 0;
    private String orderBy = "";
    private String sort = "";
    private View mLayout;
    private MediaPlayer mMediaPlayer;
    public static final String TAG = "TransferFragemt";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (mLayout == null) {
            mLayout = inflater.inflate(R.layout.activity_transfer_lay, container, false);
            findViewById(mLayout);
            initUI();
            setListener();
            initData();
        }
        //EventBus.getDefault().register(this);
        return mLayout;
    }

    @Override
    protected void initData() {
        if (DensityUtil.isNetworkAvailable(getActivity())) {
            try {
                send(0);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void findViewById(View view) {
        annualRateLay = (LinearLayout) view.findViewById(R.id.annual_rate_lay);
        transferAmountLay = (LinearLayout) view.findViewById(R.id.transfer_amount_lay);
        allowanceAmountLay = (LinearLayout) view.findViewById(R.id.allowance_amount_lay);
        residualMaturityLay = (LinearLayout) view.findViewById(R.id.residual_maturity_lay);
        annualRateImg = (ImageView) view.findViewById(R.id.annual_rate_img);
        transferAmountImg = (ImageView) view.findViewById(R.id.transfer_amount_img);
        allowanceAmountImg = (ImageView) view.findViewById(R.id.allowance_amount_img);
        residualMaturityImg = (ImageView) view.findViewById(R.id.residual_maturity_img);
        annual_rate_tv = (TextView) view.findViewById(R.id.annual_rate_tv);
        transfer_amount_tv = (TextView) view.findViewById(R.id.transfer_amount_tv);
        allowance_amount_tv = (TextView) view.findViewById(R.id.allowance_amount_tv);
        residual_maturity_tv = (TextView) view.findViewById(R.id.residual_maturity_tv);
        goToTopimg = (ImageView) view.findViewById(R.id.goto_top_img);
        goToTransferImg = (ImageView) view.findViewById(R.id.goto_transfer_img);
        headView = LayoutInflater.from(getActivity()).inflate(R.layout.no_data_layout, null);
        no_data_tv = (TextView) headView.findViewById(R.id.no_data_tv);
        no_data_tv.setText("暂无转让哦~");
        listView = (XListViewJinr) view.findViewById(R.id.view_list);
        listView.setOverScrollMode(View.OVER_SCROLL_NEVER);// 防止魅族手机出现HOLD悬停
        listView.setPullLoadEnable(false);
        listView.setPullRefreshEnable(true);
        listView.setRefreshTime();
        listView.setXListViewListener(this, 1);
        listView.setOnScrollListener(new OnScrollListenerImple());
        listView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (list != null && list.size() > 0) {
                    TransferInfo transferInfo = list.get(position - 1);
                    if (transferInfo.getTransfer_order_id() != null && !transferInfo.getTransfer_order_id().equals("")) {
                        Intent intent = new Intent();
                        intent.putExtra("transferOrderId", transferInfo.getTransfer_order_id());
                        intent.setClass(getActivity(), TransferInfoActivity.class);
                        startActivity(intent);
                    }
                }
            }
        });
        mAdapter = new TransferAdapter(getActivity(), list);
        listView.setAdapter(mAdapter);

        mMediaPlayer = MediaPlayer.create(getActivity(), R.raw.coin);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.annual_rate_lay:
                if (isAnnual == 2) {
                    isAnnual = 0;
                } else {
                    isAnnual = isAnnual + 1;
                }
                tabState = 1;
                setTabState(isAnnual, tabState);
                break;
            case R.id.transfer_amount_lay:
                if (isTransfer == 2) {
                    isTransfer = 0;
                } else {
                    isTransfer = isTransfer + 1;
                }
                tabState = 2;
                setTabState(isTransfer, tabState);
                break;

            case R.id.allowance_amount_lay:
                if (isAllowance == 2) {
                    isAllowance = 0;
                } else {
                    isAllowance = isAllowance + 1;
                }
                tabState = 3;
                setTabState(isAllowance, tabState);
                break;

            case R.id.residual_maturity_lay:
                if (isResidual == 2) {
                    isResidual = 0;
                } else {
                    isResidual = isResidual + 1;
                }
                tabState = 4;
                setTabState(isResidual, tabState);
                break;
            case R.id.goto_top_img:
                listView.setSelection(0);
                goToTopimg.setVisibility(View.INVISIBLE);
                break;
            case R.id.goto_transfer_img:
                startActivity(new Intent(getActivity(), TransferedListActivity.class));
                break;
            default:
                break;
        }
    }

    private void setTabState(int isState, int tabState) {
        switch (tabState) {
            case 1:
                transfer_amount_tv.setTextColor(getResources().getColor(R.color.gray_txt_bg));
                allowance_amount_tv.setTextColor(getResources().getColor(R.color.gray_txt_bg));
                residual_maturity_tv.setTextColor(getResources().getColor(R.color.gray_txt_bg));
                transferAmountImg.setBackgroundResource(R.drawable.ic_transfer_un);
                allowanceAmountImg.setBackgroundResource(R.drawable.ic_transfer_un);
                residualMaturityImg.setBackgroundResource(R.drawable.ic_transfer_un);
                switch (isState) {
                    case 0:
                        annual_rate_tv.setTextColor(getResources().getColor(R.color.gray_txt_bg));
                        annualRateImg.setBackgroundResource(R.drawable.ic_transfer_un);
                        orderBy = "";
                        sort = "";
                        break;
                    case 1:
                        annual_rate_tv.setTextColor(getResources().getColor(R.color.blue_btn_bg));
                        annualRateImg.setBackgroundResource(R.drawable.ic_transfer_asc);
                        orderBy = TransferInfo.ORDER_BY_TYPE_RATE;
                        sort = TransferInfo.STOR_TYPE_ASC;
                        break;
                    case 2:
                        annual_rate_tv.setTextColor(getResources().getColor(R.color.blue_btn_bg));
                        annualRateImg.setBackgroundResource(R.drawable.ic_transfer_dec);
                        orderBy = TransferInfo.ORDER_BY_TYPE_RATE;
                        sort = TransferInfo.STOR_TYPE_DESC;
                        break;
                    default:
                        break;
                }
                break;
            case 2:
                annual_rate_tv.setTextColor(getResources().getColor(R.color.gray_txt_bg));
                allowance_amount_tv.setTextColor(getResources().getColor(R.color.gray_txt_bg));
                residual_maturity_tv.setTextColor(getResources().getColor(R.color.gray_txt_bg));
                annualRateImg.setBackgroundResource(R.drawable.ic_transfer_un);
                allowanceAmountImg.setBackgroundResource(R.drawable.ic_transfer_un);
                residualMaturityImg.setBackgroundResource(R.drawable.ic_transfer_un);
                switch (isState) {
                    case 0:
                        transfer_amount_tv.setTextColor(getResources().getColor(R.color.gray_txt_bg));
                        transferAmountImg.setBackgroundResource(R.drawable.ic_transfer_un);
                        orderBy = "";
                        sort = "";
                        break;
                    case 1:
                        transfer_amount_tv.setTextColor(getResources().getColor(R.color.blue_btn_bg));
                        transferAmountImg.setBackgroundResource(R.drawable.ic_transfer_asc);
                        orderBy = TransferInfo.ORDER_BY_TYPE_MONEY;
                        sort = TransferInfo.STOR_TYPE_ASC;
                        break;
                    case 2:
                        transfer_amount_tv.setTextColor(getResources().getColor(R.color.blue_btn_bg));
                        transferAmountImg.setBackgroundResource(R.drawable.ic_transfer_dec);
                        orderBy = TransferInfo.ORDER_BY_TYPE_MONEY;
                        sort = TransferInfo.STOR_TYPE_DESC;
                        break;
                    default:
                        break;
                }
                break;
            case 3:
                annual_rate_tv.setTextColor(getResources().getColor(R.color.gray_txt_bg));
                transfer_amount_tv.setTextColor(getResources().getColor(R.color.gray_txt_bg));
                residual_maturity_tv.setTextColor(getResources().getColor(R.color.gray_txt_bg));
                annualRateImg.setBackgroundResource(R.drawable.ic_transfer_un);
                transferAmountImg.setBackgroundResource(R.drawable.ic_transfer_un);
                residualMaturityImg.setBackgroundResource(R.drawable.ic_transfer_un);
                switch (isState) {
                    case 0:
                        allowance_amount_tv.setTextColor(getResources().getColor(R.color.gray_txt_bg));
                        allowanceAmountImg.setBackgroundResource(R.drawable.ic_transfer_un);
                        orderBy = "";
                        sort = "";
                        break;
                    case 1:
                        allowance_amount_tv.setTextColor(getResources().getColor(R.color.blue_btn_bg));
                        allowanceAmountImg.setBackgroundResource(R.drawable.ic_transfer_asc);
                        orderBy = TransferInfo.ORDER_BY_TYPE_DIS;
                        sort = TransferInfo.STOR_TYPE_ASC;
                        break;
                    case 2:
                        allowance_amount_tv.setTextColor(getResources().getColor(R.color.blue_btn_bg));
                        allowanceAmountImg.setBackgroundResource(R.drawable.ic_transfer_dec);
                        orderBy = TransferInfo.ORDER_BY_TYPE_DIS;
                        sort = TransferInfo.STOR_TYPE_DESC;
                        break;
                    default:
                        break;
                }
                break;
            case 4:
                annual_rate_tv.setTextColor(getResources().getColor(R.color.gray_txt_bg));
                transfer_amount_tv.setTextColor(getResources().getColor(R.color.gray_txt_bg));
                allowance_amount_tv.setTextColor(getResources().getColor(R.color.gray_txt_bg));
                annualRateImg.setBackgroundResource(R.drawable.ic_transfer_un);
                transferAmountImg.setBackgroundResource(R.drawable.ic_transfer_un);
                allowanceAmountImg.setBackgroundResource(R.drawable.ic_transfer_un);
                switch (isState) {
                    case 0:
                        residual_maturity_tv.setTextColor(getResources().getColor(R.color.gray_txt_bg));
                        residualMaturityImg.setBackgroundResource(R.drawable.ic_transfer_un);
                        orderBy = "";
                        sort = "";
                        break;
                    case 1:
                        residual_maturity_tv.setTextColor(getResources().getColor(R.color.blue_btn_bg));
                        residualMaturityImg.setBackgroundResource(R.drawable.ic_transfer_asc);
                        orderBy = TransferInfo.ORDER_BY_TYPE_TIME;
                        sort = TransferInfo.STOR_TYPE_ASC;
                        break;
                    case 2:
                        residual_maturity_tv.setTextColor(getResources().getColor(R.color.blue_btn_bg));
                        residualMaturityImg.setBackgroundResource(R.drawable.ic_transfer_dec);
                        orderBy = TransferInfo.ORDER_BY_TYPE_TIME;
                        sort = TransferInfo.STOR_TYPE_DESC;
                        break;
                    default:
                        break;
                }
                break;
            default:
                break;
        }
        goToTransferImg.setVisibility(View.GONE);
        try {
            send(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void initUI() {
    }

    @Override
    protected void setListener() {
        annualRateLay.setOnClickListener(this);
        transferAmountLay.setOnClickListener(this);
        allowanceAmountLay.setOnClickListener(this);
        residualMaturityLay.setOnClickListener(this);
        goToTopimg.setOnClickListener(this);
        goToTransferImg.setOnClickListener(this);
    }

    @Override
    public void onRefresh(int id) {
        try {
            mMediaPlayer.start();
            listView.setRefreshTime();
            goToTransferImg.setVisibility(View.GONE);
            send(0);
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

    private void send(final int Loadpage) throws Exception {
        MyLog.e(TAG, "send: ");
        listView.setRest(false);
        listView.removeHeaderView(headView);
        JSONObject obj = new JSONObject();
        obj.put("order_by", orderBy);
        obj.put("sort", sort);
        if (Loadpage == 0) {
            obj.put("page", "1");
        } else {
            int page = (int) (Math.ceil(list.size() * 1.0 / PAGE_COUNT));
            obj.put("page", page + 1);
        }
        RequestParams params = new RequestParams();
        String desStr = MyDES.encrypt(obj.toString());
        params.put("cipher", desStr);
        MyhttpClient.desPost(UrlConfig.TRANSFER_MARKET, params, new AsyncHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
            }

            @Override
            public void onFinish() {
                super.onFinish();
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
                    int is_page = 0;
                    listView.stopRefresh();
                    listView.stopLoadMore();
                    String response = new String(arg2, "UTF-8");
                    response = CommonUtil.transResponse(response);
                    JSONObject jsonObject = new JSONObject(response); // 解析
                    String cipher = jsonObject.getString("cipher");
                    String desStr = MyDES.decrypt(cipher); // 解密
                    MyLog.e(TAG, "onSuccess: " + desStr);
                    if (Loadpage == 0) {
                        list.clear();
                        if (mAdapter != null) {
                            mAdapter.popList.clear();
                            mAdapter.notifyDataSetChanged();
                            listView.setSelection(0);
                        }
                    }
                    BaseModel<TransferInfoList> result = new Gson().fromJson(desStr, new TypeToken<BaseModel<TransferInfoList>>() {
                    }.getType());
                    if (result.isSuccess() && result.getData().getList() != null) {
                        for (int i = 0; i < result.getData().getList().size(); i++) {
                            list.add(result.getData().getList().get(i));
                        }
                        goToTransferImg.setVisibility(View.VISIBLE);
                        String totalMoney = "转让累计成交额:" + "<font color='#0c72e3'><b>" + result.getData().getTransfer_money() + "</b></font>" + "元";
                        listView.setRefreshTotalMoney(totalMoney);
                        mAdapter.notifyDataSetChanged();
                        count = result.getData().getCount();
                        count_in = count - list.size() + PAGE_COUNT;
                        if (count_in / PAGE_COUNT > 1) {
                            is_page = count_in % PAGE_COUNT + 1;
                        } else {
                            is_page = count_in % PAGE_COUNT;
                        }
                        if (is_page <= 0) {
                            listView.setPullLoadEnable(false);
                            if (list != null && list.size() > 0) {
                                listView.setRest(true);
                                listView.loaded();
                            }
                        } else {
                            listView.setPullLoadEnable(true);
                        }
                    } else {
                        if (mAdapter != null) {
                            mAdapter.notifyDataSetChanged();
                        }
                        listView.setPullLoadEnable(false);
                    }
                    if (list.size() > 0) {
                        listView.removeHeaderView(headView);
                        mAdapter.notifyDataSetChanged();
                    } else {
                        listView.addHeaderView(headView);
                        mAdapter.notifyDataSetChanged();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private class OnScrollListenerImple implements OnScrollListener {
        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        }

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            switch (scrollState) {
                case OnScrollListener.SCROLL_STATE_IDLE:
                    // 闲置状态 , 当最后一个显示的item大于10时 , 显示指定图标
                    if (view.getLastVisiblePosition() > 10) {
                        goToTopimg.setVisibility(View.VISIBLE);
                    } else {
                        goToTopimg.setVisibility(View.INVISIBLE);
                    }
                    break;
                case OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
                    // 手指滑动状态
                    break;
                case OnScrollListener.SCROLL_STATE_FLING:
                    // 惯性滑动状态
                    break;
                default:
                    break;
            }
        }
    }


    @Subscriber(tag = EventBusKey.TRANSFER_SUCCESS)
    public void notityChangeViewPage(String transferOrderId) {
        if (transferOrderId != null && !transferOrderId.equals("")) {
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).getTransfer_order_id().equals(transferOrderId)) {
                    list.remove(i);
                    mAdapter.popList = list;
                    mAdapter.notifyDataSetChanged();
                }
            }
        }
    }
}
