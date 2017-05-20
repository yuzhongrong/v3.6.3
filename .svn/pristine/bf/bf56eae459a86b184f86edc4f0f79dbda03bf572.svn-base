package com.jinr.core.my_change;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.TextView;

import com.jinr.core.R;
import com.jinr.core.base.BaseActivity;
import com.jinr.core.config.UrlConfig;
import com.jinr.core.utils.CommonUtil;
import com.jinr.core.utils.MyDES;
import com.jinr.core.utils.MyLog;
import com.jinr.core.utils.MyhttpClient;
import com.jinr.core.utils.PopupWindowUtil;
import com.jinr.core.utils.PreferencesUtils;
import com.jinr.core.utils.ToastUtil;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import model.Profit;

/**
 * 累计收益(七日年化收益率)每日收益金额
 * <p/>
 * w_shouyu 万份收益 y_week 每日收益率
 *
 * @author Administrator
 */
public class ProfitAmountActivity extends BaseActivity implements
        OnClickListener {
    private String user_id = "";
    private ArrayList<Profit> listProfits;
    private String avg_shuju = "";// 累计收益金额,平均收益率，，万份收益，周，月收益
    private ListView lv_profit_data;
    private TextView tv_avg_shuju, tv_avg_;
    private TextView title_center_text; // title标题
    private String type = "";// 标志是什么类型收益收益率(1-2-3-4-5)
    private String count = ""; // 条数
    private String typeStr = "";// 收益类型
    private String productCode = "";//日增息使用
    ProfitAmountAdapter adapter;
    ImageView nav_up;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_profit_amount);
        type = getIntent().getStringExtra("type");
        count = getIntent().getStringExtra("count");
        typeStr = getIntent().getStringExtra("typeStr");
        productCode = getIntent().getStringExtra("productCode");
        initData();
        findViewById();
        initUI();
        setListener();
    }

    @Override
    protected void initData() {
        user_id = PreferencesUtils.getValueFromSPMap(this, PreferencesUtils.Keys.UID);
    }

    @Override
    protected void findViewById() {
        title_center_text = (TextView) findViewById(R.id.title_center_text);
        lv_profit_data = (ListView) findViewById(R.id.lv_profit_data);
        tv_avg_shuju = (TextView) findViewById(R.id.tv_y_total);
        tv_avg_ = (TextView) findViewById(R.id.tv_avg_);
        nav_up = (ImageView) findViewById(R.id.nav_up);
        if (!TextUtils.isEmpty(productCode)) {
            nav_up.setVisibility(View.GONE);
        }
    }

    @Override
    protected void initUI() {
        title_center_text.setText(typeStr + "  ");
        downIcon();
        try {
            send();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void setListener() {
        findViewById(R.id.title_left_img).setOnClickListener(this);
        title_center_text.setOnClickListener(this);
        nav_up.setOnClickListener(this);
    }

    protected void send() throws Exception {
        showWaittingDialog(ProfitAmountActivity.this);
        if (!TextUtils.isEmpty(productCode)) {
            sendRegualr();
            return;
        }
        showWaittingDialog(ProfitAmountActivity.this);
        RequestParams params = new RequestParams();

        JSONObject cipher = new JSONObject();
        cipher.put("uid", user_id);
        cipher.put("type", type);
        cipher.put("count", count);

        params.put("cipher", MyDES.encrypt(cipher.toString()));
        MyhttpClient.desPost(UrlConfig.PROFIT_AMOUNT, params, new AsyncHttpResponseHandler() {

            @Override
            public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
                super.onFailure(arg0, arg1, arg2, arg3);
                dismissWaittingDialog();
                ToastUtil.show(ProfitAmountActivity.this, getResources()
                        .getString(R.string.default_error));
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
                    String re = MyDES.decrypt(cipher);
                    JSONObject data = new JSONObject(re);
                    int code = (Integer) data.get("code");
                    if (code == 1000) {
                        data = data.getJSONObject("data");
                        String y_total;
                        if (!data.has("y_total")) {
                            y_total = "";
                        } else {
                            y_total = data.getString("y_total");
                        }
                        String avg_shuju = data.getString("avg_shuju");
                        if (typeStr.equals("七日年化收益率")) {
                            tv_avg_.setText("近一月平均收益率");
                            tv_avg_shuju.setText(avg_shuju + "%");
                        } else if (typeStr.equals("万份收益")) {
                            tv_avg_.setText("近一月平均万份收益（元）");
                            tv_avg_shuju.setText(avg_shuju);
                        } else if (typeStr.equals("累计收益")) {
                            tv_avg_.setText(typeStr + "（元）");
                            tv_avg_shuju.setText(y_total);
                        } else {
                            tv_avg_.setText(typeStr + "（元）");
                            tv_avg_shuju.setText(avg_shuju);
                        }
                        JSONArray array;
                        if (data.has("childs")) {
                            array = data.getJSONArray("childs");
                            for (int i = 0; i < array.length(); i++) {
                                if (listProfits == null) {
                                    listProfits = new ArrayList<Profit>();
                                }
                                JSONObject object = array.getJSONObject(i);
                                Profit profit = new Profit();
                                profit.setY_shouyu(object.getString("y_shouyu"));
                                String date = object.getString("y_date");
                                date = date.replaceAll("/", "-");
                                profit.setY_date(date);
                                profit.setJdt(object.getString("jdt"));
                                profit.setTag(i);
                                listProfits.add(profit);
                                MyLog.i("syse", profit.toString());
                            }
                            adapter = new ProfitAmountAdapter(getApplicationContext(), listProfits);
                            lv_profit_data.setAdapter(adapter);
                        }
                    } else {
                        tv_avg_shuju.setText("");
                        tv_avg_.setText((String) data.get("msg"));
                    }
                } catch (Exception e) {
                    MyLog.i("json解析错误", "解析错误");
                }
            }
        });
    }


    public void sendRegualr() throws Exception {//日增息 累计收益
        RequestParams params = new RequestParams();
        JSONObject cipher = new JSONObject();
        cipher.put("uid", user_id);
        cipher.put("pageSize", 30);
        cipher.put("page", 0);
        cipher.put("productcode", productCode);
        params.put("cipher", MyDES.encrypt(cipher.toString()));
        MyhttpClient.desPost(UrlConfig.DAY_PRODUCT_PROFIT, params, new AsyncHttpResponseHandler() {

            @Override
            public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
                super.onFailure(arg0, arg1, arg2, arg3);
                dismissWaittingDialog();
                ToastUtil.show(ProfitAmountActivity.this, getResources()
                        .getString(R.string.default_error));

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
                    String re = MyDES.decrypt(cipher);
                    JSONObject data = new JSONObject(re);
                    int code = (Integer) data.get("code");
                    if (code == 1000) {
                        data = data.getJSONObject("data");
                        String y_total;
                        if (!data.has("totalYield")) {
                            y_total = "";
                        } else {
                            y_total = data.getString("totalYield");
                        }
                        if (!data.has("avg_shuju")) {
                            avg_shuju = "";
                        } else {
                            avg_shuju = data.getString("avg_shuju");
                        }
                        if (typeStr.equals("累计收益")) {
                            tv_avg_.setText(typeStr + "（元）");
                            tv_avg_shuju.setText(y_total);
                        } else {
                            tv_avg_.setText(typeStr + "（元）");
                            tv_avg_shuju.setText(avg_shuju);
                        }
                        JSONArray array;
                        if (data.has("list")) {
                            MyLog.e("Test", "RUN3");
                            array = data.getJSONArray("list");
                            for (int i = 0; i < array.length(); i++) {
                                if (listProfits == null) {
                                    listProfits = new ArrayList<Profit>();
                                }
                                JSONObject object = array.getJSONObject(i);
                                Profit profit = new Profit();
                                profit.setY_shouyu(object.getString("y_shouyu"));
                                String date = object.getString("y_date");
                                date = date.replaceAll("/", "-");
                                profit.setY_date(date);
                                profit.setJdt(object.getString("jdt"));
                                profit.setTag(i);
                                listProfits.add(profit);
                            }
                            adapter = new ProfitAmountAdapter(getApplicationContext(), listProfits);
                            lv_profit_data.setAdapter(adapter);
                        }
                    } else {
                        tv_avg_shuju.setText("");
                        tv_avg_.setText((String) data.get("msg"));
                    }
                } catch (Exception e) {
                    MyLog.i("json解析错误", "解析错误");
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_left_img:
                finish();
                break;
            case R.id.title_center_text://
                // 显示popupWindow
                showSelectWindow(v);
                if (typeStr.equals("七日年化收益率")) {
                    switchSelect(1);
                } else if (typeStr.equals("万份收益")) {
                    switchSelect(2);
                } else if (typeStr.equals("累计收益")) {
                    switchSelect(3);
                } else if (typeStr.equals("近一周收益")) {
                    switchSelect(4);
                } else if (typeStr.equals("近一月收益")) {
                    switchSelect(5);
                }
                break;
            case R.id.nav_up://
                // 显示popupWindow
                showSelectWindow(v);
                if (typeStr.equals("七日年化收益率")) {
                    switchSelect(1);
                } else if (typeStr.equals("万份收益")) {
                    switchSelect(2);
                } else if (typeStr.equals("累计收益")) {
                    switchSelect(3);
                } else if (typeStr.equals("近一周收益")) {
                    switchSelect(4);
                } else if (typeStr.equals("近一月收益")) {
                    switchSelect(5);
                }
                break;
            case R.id.ly1:
                type = "2";
                count = "30";
                typeStr = "七日年化收益率";
                title_center_text.setText("七日年化收益率" + "  ");
                initSelected(1);
                break;
            case R.id.ly2:
                type = "3";
                count = "30";
                typeStr = "万份收益";
                title_center_text.setText("万份收益" + "  ");
                initSelected(2);
                break;
            case R.id.ly3:
                type = "1";
                count = "30";
                typeStr = "累计收益";
                title_center_text.setText("累计收益" + "  ");
                initSelected(3);
                break;
            case R.id.ly4:
                type = "1";
                count = "7";
                typeStr = "近一周收益";
                title_center_text.setText("近一周收益" + "  ");
                initSelected(4);
                break;
            case R.id.ly5:
                type = "1";
                count = "30";
                typeStr = "近一月收益";
                title_center_text.setText("近一月收益" + "  ");
                initSelected(5);
                break;
            case R.id.ly_all:
                popupwindow.dismiss();
                break;
            default:
                break;
        }
    }

    /**
     * 初始化选中的item
     */
    private void initSelected(int tag) {
        if (listProfits != null) {
            listProfits.clear();// 记得清除，不然数据会多出来
        }
        if (adapter != null) {// 判断该适配器是否为空
            adapter.notifyDataSetChanged();
        }
        switchSelect(tag);
        try {
            send();
        } catch (Exception e) {
            e.printStackTrace();
        }
        popupwindow.dismiss();

    }

    private void switchSelect(int tag) {
        initView();
        switch (tag) {
            case 1:
                iv_1.setVisibility(View.VISIBLE);
                break;
            case 2:
                iv_2.setVisibility(View.VISIBLE);
                break;
            case 3:
                iv_3.setVisibility(View.VISIBLE);
                break;
            case 4:
                iv_4.setVisibility(View.VISIBLE);
                break;
            case 5:
                iv_5.setVisibility(View.VISIBLE);
                break;

        }
    }

    private PopupWindow popupwindow;
    private ImageView iv_1, iv_2, iv_3, iv_4, iv_5;

    /**
     * 选择收益类型
     */
    private void showSelectWindow(View v) {
        upIcon();
        if (popupwindow == null) {
            View view = getLayoutInflater().inflate(R.layout.pop_profit, null);
            view.findViewById(R.id.ly_all).setOnClickListener(this);
            view.findViewById(R.id.ly1).setOnClickListener(this);
            view.findViewById(R.id.ly2).setOnClickListener(this);
            view.findViewById(R.id.ly3).setOnClickListener(this);
            view.findViewById(R.id.ly4).setOnClickListener(this);
            view.findViewById(R.id.ly5).setOnClickListener(this);
            iv_1 = (ImageView) view.findViewById(R.id.iv_1);
            iv_2 = (ImageView) view.findViewById(R.id.iv_2);
            iv_3 = (ImageView) view.findViewById(R.id.iv_3);
            iv_4 = (ImageView) view.findViewById(R.id.iv_4);
            iv_5 = (ImageView) view.findViewById(R.id.iv_5);

            popupwindow = PopupWindowUtil.getPopupWindow(getApplicationContext(), view);
            popupwindow.setOnDismissListener(new OnDismissListener() {
                @Override
                public void onDismiss() {
                    downIcon();
                }
            });
            popupwindow.setTouchInterceptor(new OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                        popupwindow.dismiss();
                        return true;
                    }
                    return false;
                }
            });
        }
        popupwindow.update();
        popupwindow.showAsDropDown(v);
    }

    /**
     * 设置标题右边图标向下
     */
    public void downIcon() {
        nav_up.setImageResource(R.drawable.down_icon);
    }

    /**
     * 设置标题右边图标向上
     */
    public void upIcon() {
        nav_up.setImageResource(R.drawable.up_icon);
    }

    /**
     * 隐藏打钩
     */
    private void initView() {
        iv_1.setVisibility(View.INVISIBLE);
        iv_2.setVisibility(View.INVISIBLE);
        iv_3.setVisibility(View.INVISIBLE);
        iv_4.setVisibility(View.INVISIBLE);
        iv_5.setVisibility(View.INVISIBLE);
    }
}
