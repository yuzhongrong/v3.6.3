package com.jinr.core.strongbox;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.TextAppearanceSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jinr.core.R;
import com.jinr.core.base.BaseFragment;
import com.jinr.core.config.UrlConfig;
import com.jinr.core.dayup.CommonProjectDetailActivity;
import com.jinr.core.utils.CommonUtil;
import com.jinr.core.utils.MyDES;
import com.jinr.core.utils.MyhttpClient;
import com.jinr.core.utils.PreferencesUtils;
import com.jinr.core.utils.ToastUtil;
import com.jinr.core.utils.UmUtils;
import com.jinr.pulltorefresh.CustomScrollView;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONObject;

import java.util.ArrayList;

import model.ProductCommonModel;
import model.ProductModel;

/**
 * 保险箱 Fragment 界面
 * Created by: Ysw on 2016/12/28.
 */
public class FragmentStrongBox extends BaseFragment implements View.OnClickListener {
    public static final String TAG = "FragmentStrongBox";
    private String url;
    private Typeface tf;
    private View mLayout;
    private TextView tv_expect;
    private TextView tv_improve;
    private TextView tv_rate;
    private TextView tv_min;
    private TextView tv_depict;
    private TextView tv_one;
    private TextView tv_one_first;
    private TextView tv_one_second;
    private TextView tv_two;
    private TextView tv_two_first;
    private TextView tv_two_second;
    private TextView tv_three;
    private TextView tv_three_first;
    private TextView tv_three_second;
    private TextView tv_details;
    private TextView tv_next;
    private CustomScrollView mRefreshScrollView;
    private ArrayList<ProductModel> mDataList;
    private static ProductCommonModel productCommonModel = new ProductCommonModel();
    private boolean isOpen = false;
    private LinearLayout ll_content;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (mLayout == null) {
            mLayout = inflater.inflate(R.layout.fragment_common_product, container, false);
            tf = Typeface.createFromAsset(getActivity().getAssets(), "fonts/HYxyj.ttf");
            findViewById(mLayout);
            setListener();
            UmUtils.customsEvent(getActivity(), UmUtils.MAINREGULAR_CLICK, UmUtils.MAINREGULAR_HUM);
        }
        //EventBus.getDefault().register(this);
        return mLayout;
    }

    @Override
    protected void findViewById(View view) {
        ll_content = (LinearLayout) mLayout.findViewById(R.id.ll_content);
        mRefreshScrollView = (CustomScrollView) mLayout.findViewById(R.id.mRefreshScrollView);
        tv_expect = (TextView) mLayout.findViewById(R.id.tv_expect);
        tv_improve = (TextView) mLayout.findViewById(R.id.tv_improve);
        tv_rate = (TextView) mLayout.findViewById(R.id.tv_rate);
        tv_min = (TextView) mLayout.findViewById(R.id.tv_min);
        tv_depict = (TextView) mLayout.findViewById(R.id.tv_depict);
        tv_one = (TextView) mLayout.findViewById(R.id.tv_one);
        tv_one_first = (TextView) mLayout.findViewById(R.id.tv_one_first);
        tv_one_second = (TextView) mLayout.findViewById(R.id.tv_one_second);
        tv_two = (TextView) mLayout.findViewById(R.id.tv_two);
        tv_two_first = (TextView) mLayout.findViewById(R.id.tv_two_first);
        tv_two_second = (TextView) mLayout.findViewById(R.id.tv_two_second);
        tv_three = (TextView) mLayout.findViewById(R.id.tv_three);
        tv_three_first = (TextView) mLayout.findViewById(R.id.tv_three_first);
        tv_three_second = (TextView) mLayout.findViewById(R.id.tv_three_second);
        tv_details = (TextView) mLayout.findViewById(R.id.tv_details);
        tv_next = (TextView) mLayout.findViewById(R.id.tv_next);
        mRefreshScrollView.setOnRefreshListener(new CustomScrollView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadData(0);
            }
        });
        mRefreshScrollView.onLoadComplete();
        tv_one.setTypeface(tf);
        tv_two.setTypeface(tf);
        tv_three.setTypeface(tf);
    }


    @Override
    protected void setListener() {
        tv_details.setOnClickListener(this);
        tv_next.setOnClickListener(this);
        ll_content.setOnClickListener(this);
    }

    @Override
    protected void initUI() {
    }

    @Override
    protected void initData() {
    }

    public void setData(ArrayList<ProductModel> list) {
        this.mDataList = list;
        if (mDataList != null && mDataList.size() > 0) {
            for (int i = 0; i < mDataList.size(); i++) {
                String name = mDataList.get(i).getName();
                if (name.equals("保险箱")) {
                    ProductModel mode = mDataList.get(i);
                    reFreshUI(mode);
                }
            }
        }
    }

    private void reFreshUI(ProductModel mode) {
        tv_expect.setText(mode.getDesc());
        String text_top = mode.getText_top();
        String[] split = text_top.split(";");
        tv_min.setText(split[0]);
        tv_depict.setText(split[1]);
        String one = mode.getTextdown_left();
        String leftStr[] = one.split(";");
        tv_one.setText(leftStr[0]);
        tv_one_first.setText(leftStr[1]);
        tv_one_second.setText(leftStr[2]);
        String two = mode.getTextdown_middle();
        String middleStr[] = two.split(";");
        tv_two.setText(middleStr[0]);
        tv_two_first.setText(middleStr[1]);
        tv_two_second.setText(middleStr[2]);
        String three = mode.getTextdown_right();
        String rightStr[] = three.split(";");
        tv_three.setText(rightStr[0]);
        tv_three_first.setText(rightStr[1]);
        tv_three_second.setText(rightStr[2]);
        String rate = mode.getInvestRateFinish();
        setTextStyle(rate);
    }

    private void setTextStyle(String rate) {
        String strm[] = rate.split("~");
        int length = strm[0].length();
        SpannableString rateText = new SpannableString(rate + "%");
        rateText.setSpan(new TextAppearanceSpan(getActivity(), R.style.dayup_shouyilv_one), 0, length - 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        rateText.setSpan(new TextAppearanceSpan(getActivity(), R.style.dayup_shouyilv_two), length - 1, length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        rateText.setSpan(new TextAppearanceSpan(getActivity(), R.style.dayup_shouyilv_one), length, rateText.length() - 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        rateText.setSpan(new TextAppearanceSpan(getActivity(), R.style.dayup_shouyilv_two), rateText.length() - 1, rateText.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tv_rate.setText(rateText, TextView.BufferType.SPANNABLE);
    }

    protected void sendfoldfourfold() throws Exception {
        RequestParams params = new RequestParams();
        String uid = PreferencesUtils.getValueFromSPMap(getActivity(), PreferencesUtils.Keys.UID);
        JSONObject cipher = new JSONObject();
        cipher.put(PreferencesUtils.Keys.UID, uid);
        cipher.put("productcode", productCommonModel.getCode());
        params.put("cipher", MyDES.encrypt(cipher.toString()));
        MyhttpClient.desPost(UrlConfig.USER_REGULARALLMONEY, params, new AsyncHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
            }

            @Override
            public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
                super.onFailure(arg0, arg1, arg2, arg3);
            }

            @Override
            public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
                super.onSuccess(arg0, arg1, arg2);
                try {
                    dismissWaittingDialog();
                    String response = new String(arg2, "UTF-8");
                    response = CommonUtil.transResponse(response);
                    JSONObject jsb = new JSONObject(response);
                    String cipher = jsb.getString("cipher");
                    cipher = MyDES.decrypt(cipher);
                    JSONObject object = new JSONObject(cipher);
                    int code = object.getInt("code");
                    if (code == 1000) {
                        if (object.has("data")) {
                            JSONObject data = object.getJSONObject("data");
                            if (data.getInt("isopen") == 1) {
                                isOpen = true;
                            } else {
                                isOpen = false;
                            }
                            if (data.getInt("isopen") == 1) {
                                tv_improve.setVisibility(View.VISIBLE);
                            } else {
                                tv_improve.setVisibility(View.GONE);
                            }
                            tv_improve.setText(data.optString("content_four"));
                        }
                    } else {
                        tv_improve.setVisibility(View.GONE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFinish() {
                super.onFinish();
            }
        });
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.ll_content:
            case R.id.tv_details:
                if (productCommonModel.getAssetid() == null || productCommonModel.getAssetid().equals("")) {
                    ToastUtil.show(getActivity(), R.string.default_error);
                    return;
                }
                intent = new Intent(getActivity(), CommonProjectDetailActivity.class);
                intent.putExtra("assetid", productCommonModel.getAssetid());
                intent.putExtra("name", productCommonModel.getName());
                intent.putExtra("good_type", productCommonModel.getType());
                getActivity().startActivity(intent);
                break;
            default:
                break;
        }
    }

    /**
     * 上下拉刷新加载数据方法
     */
    public void loadData(final int type) {
        new Thread() {
            @Override
            public void run() {

            }
        }.start();
    }


    public static ProductCommonModel getProductCommonModel() {
        return productCommonModel;
    }
}
