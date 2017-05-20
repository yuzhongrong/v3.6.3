package com.jinr.core.trade.record;

import android.annotation.SuppressLint;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
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
import com.jinr.graph_view.XListView;
import com.jinr.graph_view.XListView.IXListViewListener;
import com.jinr.graph_view.XListViewJinr;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import model.BaseModel;
import model.Record;
import model.RecordList;

/**
 * TradeRecordActivity.java
 * <p>
 * 所有的交易记录
 *
 * @author Andrew Lee Ysw
 * @version 2014-10-25 上午10:36:41
 * @description:
 */
@SuppressLint("NewApi")
public class NewTradeRecordActivity extends BaseActivity implements XListViewJinr.IXListViewListener, OnClickListener {
    private View view;
    private String uid;
    private XListViewJinr view_list;
    private PopupWindow pop; // 标签弹出框
    private ImageView title_left_img, arrowImg; // title左边图片
    private TextView title_center_text; // title标题
    private TextView allRecord;//所有记录（标签）
    private List<Record> mRecordList = new ArrayList<>();
    private List<Record> list = new ArrayList<>();
    private List<String> popList = new ArrayList<>();
    public int PAGE_COUNT = 10;
    private String count = "";
    private TradeRecordAdapter adapter = null;
    private int count_in = 0, len = 0, mLen = 0;
    private int select = 1;// 1为所有记录
    private boolean flag = false;
    private String groupTime = " ";
    private int lastCnt = 0;
    private ListView listView;
    private AssetsListAdapert assetsListAdapert;
    private int selectPosition = -1;
    private View noDataLay;
    private String goods_type;
    private String name;
    private String product_code;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.trade_record);
        initData();
        findViewById();
        initUI();
        setListener();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return super.onKeyDown(keyCode, event);
    }

    protected void initData() {
        goods_type = getIntent().getStringExtra("goods_type");
        product_code = getIntent().getStringExtra("product_code");
        name = getIntent().getStringExtra("name");
        popList = new ArrayList<>();
        view = LayoutInflater.from(this).inflate(R.layout.pop_trade_record, null);
        uid = PreferencesUtils.getValueFromSPMap(this, PreferencesUtils.Keys.UID);
        pop = new PopupWindow(view, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, true);
        noDataLay = LayoutInflater.from(this).inflate(R.layout.no_data_layout, null);
    }

    protected void findViewById() {
        arrowImg = (ImageView) findViewById(R.id.nav_up);
        listView = (ListView) view.findViewById(R.id.trade_list);
        title_left_img = (ImageView) findViewById(R.id.title_left_img);
        title_center_text = (TextView) findViewById(R.id.title_center_text);
        view_list = (XListViewJinr) findViewById(R.id.view_list);
        allRecord = (TextView) view.findViewById(R.id.all_record);
        view_list.setOverScrollMode(View.OVER_SCROLL_NEVER);// 防止魅族手机出现HOLD悬停
        view_list.setPullLoadEnable(false);
        view_list.setRefreshTime();
        view_list.setXListViewListener(this, 1);
    }

    protected void initUI() {
        if (!TextUtils.isEmpty(name)) {
            title_center_text.setText(name + getResources()
                    .getString(R.string.deal_record));
        } else {
            title_center_text.setText(getResources()
                    .getString(R.string.deal_record));
        }
    }

    private void testData() {
        for (int i = 0; i < 3; i++) {
            popList.add("我的测试" + i);
        }
        assetsListAdapert = new AssetsListAdapert();
        listView.setAdapter(assetsListAdapert);
    }

    @Override
    protected void onResume() {
        if (list.size() == 0) {
            showWaittingDialog(NewTradeRecordActivity.this);
            try {
                send(0, select);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            int is_page = 0;
            if (adapter == null) {
                TradeRecordAdapter adapter = new TradeRecordAdapter(this, list);
                view_list.setAdapter(adapter);
            }
            count_in = Integer.parseInt(count) - (list.size() - mLen) + PAGE_COUNT;
            if (count_in / PAGE_COUNT > 1) {
                is_page = count_in / PAGE_COUNT;
            } else {
                is_page = count_in % PAGE_COUNT;
            }
            if (is_page <= 0) {
                view_list.setPullLoadEnable(false);
            } else {
                view_list.setPullLoadEnable(true);
            }
        }
        if (MainActivity.homekey) {// 如果在监听到home键的情况下进入，则启用图形锁
            MainActivity.instance.gotoLockPattern();
            MainActivity.homekey = false;
        }
        super.onResume();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_center_text://标题
                if (!flag) {
                    pop.setFocusable(false);
                    pop.setBackgroundDrawable(new ColorDrawable(0));
                    pop.setTouchInterceptor(new OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                                pop.dismiss();
                                arrowImg.setImageResource(R.drawable.down_icon);
                                flag = false;
                                return true;
                            }
                            return false;
                        }
                    });
                    pop.showAsDropDown(title_center_text);
                    arrowImg.setImageResource(R.drawable.up_icon);
                    flag = true;
                } else {
                    pop.dismiss();
                    arrowImg.setImageResource(R.drawable.down_icon);
                    flag = false;
                }
                break;
            case R.id.nav_up:
                if (!flag) {
                    pop.setFocusable(false);
                    pop.setBackgroundDrawable(new ColorDrawable(0));
                    pop.setTouchInterceptor(new OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                                pop.dismiss();
                                arrowImg.setImageResource(R.drawable.down_icon);
                                flag = false;
                                return true;
                            }
                            return false;
                        }
                    });
                    pop.showAsDropDown(title_center_text);
                    arrowImg.setImageResource(R.drawable.up_icon);
                    flag = true;
                } else {
                    pop.dismiss();
                    arrowImg.setImageResource(R.drawable.down_icon);
                    flag = false;
                }
                break;
            case R.id.title_left_img:// 左侧图标
                finish();
                break;
            default:
                break;
        }
    }

    protected void setListener() {
        //		arrowImg.setOnClickListener(this);
        //		title_center_text.setOnClickListener(this);
        title_left_img.setOnClickListener(this);
        listView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectPosition = position;
                assetsListAdapert.notifyDataSetInvalidated();
                pop.dismiss();
                arrowImg.setImageResource(R.drawable.down_icon);
                flag = false;
            }
        });
        view.findViewById(R.id.outsider).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                pop.dismiss();
                arrowImg.setImageResource(R.drawable.down_icon);
            }
        });
    }

    public void sendTo(final int is_more, int action_type) {
        try {
            send(is_more, action_type);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void send(final int is_more, int action_type) throws Exception {
        showWaittingDialog(NewTradeRecordActivity.this);
        view_list.setRest(false);
        JSONObject obj = new JSONObject();
        obj.put("uid", uid);//587320
        obj.put("goods_type", goods_type);
        obj.put("action_type", action_type);
        obj.put("product_code", product_code);
        if (is_more == 0) {
            obj.put("page", "1");
            obj.put("pageSize", PAGE_COUNT);
            mLen = 0;
            lastCnt = 0;
            if (list.size() > 0) {
                list.clear();
                adapter.notifyDataSetChanged();
            }
        } else {
            int page = (int) (Math.ceil((list.size() - mLen) * 1.0 / PAGE_COUNT) + 1);
            obj.put("page", page);
            obj.put("pageSize", PAGE_COUNT);
        }
        RequestParams params = new RequestParams();
        MyLog.d("DES", "json Str :" + obj.toString());
        String desStr = MyDES.encrypt(obj.toString());
        params.put("cipher", desStr);
        MyLog.d("DES", "加密json Str :" + desStr);
        MyLog.d("DES", "解密json Str :" + MyDES.decrypt(desStr));
        MyhttpClient.desPost(UrlConfig.GET_RECORD, params, new AsyncHttpResponseHandler() {

            @Override
            public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
                super.onFailure(arg0, arg1, arg2, arg3);
                ToastUtil.show(MainActivity.instance, R.string.default_error);
                dismissWaittingDialog();
                view_list.stopRefresh();
                view_list.stopLoadMore();
                if (list.size() == 0) {
                    view_list.removeHeaderView(noDataLay);
                    if (adapter == null) {
                        adapter = new TradeRecordAdapter(NewTradeRecordActivity.this, list);
                        view_list.setAdapter(adapter);
                    }
                    view_list.addHeaderView(noDataLay);
                } else {
                    view_list.removeHeaderView(noDataLay);
                }
            }

            int is_page = 0;

            @Override
            public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
                super.onSuccess(arg0, arg1, arg2);
                dismissWaittingDialog();
                view_list.stopRefresh();
                view_list.stopLoadMore();
                if (is_more == 0) {
                    list.clear();
                }
                try {
                    String response = new String(arg2, "UTF-8");
                    response = CommonUtil.transResponse(response);
                    MyLog.i("交易记录信息返回", response);
                    if (response == null || response.equals("")) {
                        return;
                    }
                    JSONObject jsonObject = new JSONObject(response); // 解析
                    String cipher = jsonObject.getString("cipher");
                    String desStr = MyDES.decrypt(cipher); // 解密
                    MyLog.e("DES", "解密请求返回：" + desStr);
                    if (TextUtils.isEmpty(desStr))
                        return;
                    BaseModel<RecordList> result = new Gson().fromJson(desStr,
                            new TypeToken<BaseModel<RecordList>>() {
                            }.getType());
                    if (result.isSuccess()) {
                        len = result.getData().getList().size();
                        MyLog.d("DES", "解密请求返回：" + mRecordList.size());

                        for (int i = 0; i < len; i++) {
                            Record mRecord = new Record();//作为月份使用
                            mRecord.setOrder_id("-1");
                            mRecord.setC_time(result.getData().getList().get(i).getGroup_time());
                            if (!(result.getData().getList().get(i).getGroup_time().equals(groupTime) && is_more == 1)) {
                                list.add(mRecord);
                                mLen++;
                                groupTime = result.getData().getList().get(i).getGroup_time();
                            }
                            mRecordList = (result.getData().getList().get(i).getList());
                            for (int j = 0; j < mRecordList.size(); j++) {
                                list.add(mRecordList.get(j));
                            }
                        }

                        if (adapter == null) {
                            adapter = new TradeRecordAdapter(NewTradeRecordActivity.this, list);
                            view_list.setAdapter(adapter);
                        } else {
                            adapter.notifyDataSetChanged();
                        }
                        count = result.getData().getTotal_count();
                        count_in = Integer.parseInt(count) - (list.size() - mLen) + PAGE_COUNT;
                        if (count_in / PAGE_COUNT > 1) {
                            is_page = count_in / PAGE_COUNT;
                        } else {
                            is_page = count_in % PAGE_COUNT;
                        }
                        if (is_page <= 0) {
                            view_list.setPullLoadEnable(false);
                            if (list != null && list.size() > 0) {
                                view_list.setRest(true);
                                view_list.loaded();
                            }
                        } else {
                            view_list.setPullLoadEnable(true);
                        }
                    } else {
                        if (adapter != null) {
                            adapter.notifyDataSetChanged();
                        }

                        view_list.setPullLoadEnable(false);
                        ToastUtil.show(NewTradeRecordActivity.this, result.getMsg());
                    }
                    if (list.size() == 0) {
                        view_list.removeHeaderView(noDataLay);
                        if (adapter == null) {
                            adapter = new TradeRecordAdapter(NewTradeRecordActivity.this, list);
                            view_list.setAdapter(adapter);
                        }
                        view_list.addHeaderView(noDataLay);
                        adapter.notifyDataSetInvalidated();
                    } else {
                        view_list.removeHeaderView(noDataLay);
                    }

                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (JsonSyntaxException e) {
                    MyLog.i("json解析错误", "解析错误");
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onRefresh(int id) {
        try {
            send(0, select);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onLoadMore(int id) {
        try {
            send(1, select);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    class AssetsListAdapert extends BaseAdapter {

        @Override
        public int getCount() {
            return popList.size();
        }

        @Override
        public Object getItem(int position) {
            return popList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup arg2) {
            ViewHolder viewHolder = null;
            if (convertView == null) {
                convertView = LayoutInflater.from(NewTradeRecordActivity.this).inflate(R.layout.item_pop_trade_record, null);
                viewHolder = new ViewHolder();
                viewHolder.myText = (TextView) convertView.findViewById(R.id.assets_type);
                viewHolder.icon = (ImageView) convertView.findViewById(R.id.icon);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            if (selectPosition == position) {
                viewHolder.icon.setVisibility(View.VISIBLE);
            } else {
                viewHolder.icon.setVisibility(View.GONE);
            }
            viewHolder.myText.setText(popList.get(position));
            return convertView;
        }
    }

    class ViewHolder {
        TextView myText;
        ImageView icon;
    }
}
