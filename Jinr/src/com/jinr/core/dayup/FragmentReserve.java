package com.jinr.core.dayup;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.TextAppearanceSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jinr.core.MainActivity;
import com.jinr.core.R;
import com.jinr.core.base.BaseFragment;
import com.jinr.core.config.Check;
import com.jinr.core.config.UrlConfig;
import com.jinr.core.gift.share.MyGiftActivity;
import com.jinr.core.regular.FragmentRegular;
import com.jinr.core.utils.CommonUtil;
import com.jinr.core.utils.MyDES;
import com.jinr.core.utils.MyhttpClient;
import com.jinr.core.utils.PreferencesUtils;
import com.jinr.core.utils.ToastUtil;
import com.jinr.core.utils.UmUtils;
import com.jinr.pulltorefresh.CustomScrollView;
import com.jinr.pulltorefresh.CustomScrollView.OnRefreshListener;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONObject;

import model.ProductCommonModel;

public class FragmentReserve extends BaseFragment implements OnClickListener {
    public static final String TAG = "FragmentReserve";
    private RelativeLayout animLayout;
    private LinearLayout productRl;
    private TextView txtRate, tv_count, tv_count_per, tv_onehundred,
            tv_hundred_per, tv_twentyfour, tv_twentyfour_hour,
            text_top_content, textdown_left_top, textdown_left_middle,
            textdown_left_bottom, textdown_middle_top, textdown_middle_middle,
            textdown_middle_bottom, textdown_right_top, textdown_right_middle,
            textdown_right_bottom, min_money_tv, into_info_tv, improve_earn,
            tv_shouyi, goto_next_img;
    private Button notice_btn;
    private ImageView mRightBtn;
    private TextView img_sys;
    private FrameLayout news_system_fl;
    public static FragmentRegular intance = null;
    String urlString = "";
    private String url;
    private Typeface tf;
    private CustomScrollView mRefreshableView;// 自定义下拉
    private View rootView;
    private static ProductCommonModel productCommonModel = new ProductCommonModel();
    private boolean isOpen = false;// 四倍收益，后台控制
    private static String rate = "3.00";
    private static String rates = "3.0";
    private String strm[];

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.activity_main_regular, container, false);
        }
        ViewGroup parent = (ViewGroup) rootView.getParent();
        if (parent != null) {
            parent.removeView(rootView);
        }
        tf = Typeface.createFromAsset(getActivity().getAssets(), "fonts/HYxyj.ttf");
        findViewById(rootView);
        initUI(); // 过渡版修改
        setListener();
        UmUtils.customsEvent(getActivity(), UmUtils.MAINREGULAR_CLICK, UmUtils.MAINREGULAR_HUM);
        //EventBus.getDefault().register(this);
        initData();
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    public void initUI2(boolean inNum) {
        tv_count.setText(rates.subSequence(0, 1));
        if (inNum) {
            startAnim();
        } else {
            setTextStyle(rate);
        }
    }

    /**
     * ljs 4.01% 数字上飘
     */
    private void startAnim() {
        txtRate.setText("");
        improve_earn.setVisibility(View.GONE);
        Animation myAnimation1 = AnimationUtils.loadAnimation(MainActivity.instance, R.anim.anim_ljs);
        myAnimation1.setAnimationListener(new MyAnimListen());
        animLayout.startAnimation(myAnimation1);
    }

    /**
     * description:
     *
     * @author Andrew Lee version 2014-10-20 下午5:29:20
     */
    @Override
    protected void initData() {
    }

    /**
     * description:
     *
     * @param view
     * @author Andrew Lee version 2014-10-20 下午5:29:17
     */
    @Override
    protected void findViewById(View view) {
        //EventBus.getDefault().register(this);
        productRl = (LinearLayout) view.findViewById(R.id.product_rl);
        txtRate = (TextView) view.findViewById(R.id.product_txt_rate);
        animLayout = (RelativeLayout) view.findViewById(R.id.product_ly_anim);
        notice_btn = (Button) view.findViewById(R.id.btn_notice);
        tv_shouyi = (TextView) view.findViewById(R.id.tv_shouyi);
        tv_count = (TextView) view.findViewById(R.id.tv_count_product);
        tv_count_per = (TextView) view.findViewById(R.id.tv_count_per);
        tv_onehundred = (TextView) view.findViewById(R.id.tv_onehundred);
        tv_hundred_per = (TextView) view.findViewById(R.id.tv_hundred_per);
        tv_twentyfour = (TextView) view.findViewById(R.id.tv_twentyfour);
        text_top_content = (TextView) view.findViewById(R.id.text_top);
        into_info_tv = (TextView) view.findViewById(R.id.into_info_tv);
        goto_next_img = (TextView) view.findViewById(R.id.goto_next_img);
        if (!Check.is_login(getActivity())) {
            into_info_tv.setVisibility(View.GONE);
            goto_next_img.setVisibility(View.VISIBLE);
        } else {
            goto_next_img.setVisibility(View.GONE);
            into_info_tv.setVisibility(View.VISIBLE);
        }
        news_system_fl = (FrameLayout) view.findViewById(R.id.news_system_fl);
        textdown_left_top = (TextView) view.findViewById(R.id.textdown_left_top);
        textdown_left_middle = (TextView) view.findViewById(R.id.textdown_left_middle);
        textdown_left_bottom = (TextView) view.findViewById(R.id.textdown_left_bottom);
        textdown_middle_top = (TextView) view.findViewById(R.id.textdown_middle_top);
        textdown_middle_middle = (TextView) view.findViewById(R.id.textdown_middle_middle);
        textdown_middle_bottom = (TextView) view.findViewById(R.id.textdown_middle_bottom);
        textdown_right_top = (TextView) view.findViewById(R.id.textdown_right_top);
        textdown_right_middle = (TextView) view.findViewById(R.id.textdown_right_middle);
        textdown_right_bottom = (TextView) view.findViewById(R.id.textdown_right_bottom);
        min_money_tv = (TextView) view.findViewById(R.id.min_money_tv);
        improve_earn = (TextView) view.findViewById(R.id.improve_earnings);
        mRightBtn = (ImageView) view.findViewById(R.id.btn_menu_right);
        img_sys = (TextView) view.findViewById(R.id.img_sys);
        tv_twentyfour_hour = (TextView) view.findViewById(R.id.tv_twentyfour_hour);
        mRefreshableView = (CustomScrollView) view.findViewById(R.id.scroll_view_redresable);
        mRefreshableView.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadData(0);
            }
        });
        mRefreshableView.onLoadComplete();// 刷新完成，默认先完成
        tv_count.setTypeface(tf);
        tv_count_per.setTypeface(tf);
        tv_onehundred.setTypeface(tf);
        tv_hundred_per.setTypeface(tf);
        tv_twentyfour.setTypeface(tf);
        tv_twentyfour_hour.setTypeface(tf);
    }

    /**
     * description:
     *
     * @author Andrew Lee version 2014-10-20 下午5:29:15
     */
    @Override
    protected void initUI() {
        tv_onehundred.setText(R.string.hundred);
        tv_hundred_per.setText(R.string.per);
        tv_twentyfour_hour.setText(R.string.day);
    }

    /**
     * description:
     *
     * @author Andrew Lee version 2014-10-20 下午5:29:08
     */
    @Override
    protected void setListener() {
        notice_btn.setOnClickListener(this);
        mRightBtn.setOnClickListener(this);
        productRl.setOnClickListener(this);
        into_info_tv.setOnClickListener(this);
        goto_next_img.setOnClickListener(this);
        news_system_fl.setOnClickListener(this);
    }

    private double tot = 0;
    public Handler myHandler = new Handler() {// 用来更新UI线程中的控件
        public void handleMessage(Message msg) {
            try {
                if (msg.what == 9999) {
                    txtRate.setText("");
                    improve_earn.setVisibility(View.GONE);
                    tot = 0;
                    return;
                }
                tot += 0.05;
                if (tot > Double.parseDouble(rate)) {
                    tot = Double.parseDouble(rate);
                }
                SpannableString styledText = new SpannableString(String.format("%.2f", tot) + "%");
                styledText.setSpan(new TextAppearanceSpan(getActivity(), R.style.shouyilv_one), 0, 4, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                styledText.setSpan(new TextAppearanceSpan(getActivity(), R.style.shouyilv_two), 4, 5, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                txtRate.setText(styledText, TextView.BufferType.SPANNABLE);
                if (isOpen) {
                    improve_earn.setVisibility(View.VISIBLE);
                } else {
                    improve_earn.setVisibility(View.GONE);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    class MyAnimListen implements AnimationListener {
        @Override
        public void onAnimationStart(Animation arg0) {
        }

        @Override
        public void onAnimationRepeat(Animation arg0) {
        }

        @Override
        public void onAnimationEnd(Animation arg0) {
            dismissWaittingDialog();
            myHandler.sendEmptyMessage(9999);
            Thread t = new Thread() {
                private double step = 0.04;
                private double tot = 0;
                private int index = 0;

                public void run() {
                    try {
                        Thread.sleep(100);
                        myHandler.sendEmptyMessage(9999);
                        while (tot < Double.parseDouble(rate)) {
                            index++;
                            Thread.sleep(10);
                            myHandler.sendEmptyMessage(index);
                            tot += step;
                        }
                        currentThread().interrupt();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };
            t.start();
        }
    }


    protected void sendfoldfourfold() throws Exception {// 享四倍收益
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
                showWaittingDialog(getActivity());
            }

            @Override
            public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
                super.onFailure(arg0, arg1, arg2, arg3);
                dismissWaittingDialog();
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
                            improve_earn.setText(data.optString("content_four"));
                        }
                    } else {
                        isOpen = false;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFinish() {
                super.onFinish();
                dismissWaittingDialog();
            }
        });
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.btn_notice:
                intent = new Intent(getActivity(), MyGiftActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("url", url);
                bundle.putString("title", "公告详情");
                intent.putExtras(bundle);
                getActivity().startActivity(intent);
                break;
            case R.id.product_rl:
                if (Check.is_login(getActivity())) {
                    if (productCommonModel.getAssetid() == null || productCommonModel.getAssetid().equals("")) {
                        ToastUtil.show(getActivity(), R.string.default_error);
                        return;
                    }
                    intent = new Intent(getActivity(), CommonProjectDetailActivity.class);
                    intent.putExtra("assetid", productCommonModel.getAssetid());
                    intent.putExtra("name", productCommonModel.getName());
                    intent.putExtra("good_type", productCommonModel.getType());
                    getActivity().startActivity(intent);
                }
                break;
            case R.id.into_info_tv:
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
                    mRefreshHandler.sendEmptyMessage(13);
                }
            }
        }.start();
    }

    private Handler mRefreshHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 13:
                    try {
                        MainActivity.instance.getProduct();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                default:
                    break;
            }
        }
    };

    public static ProductCommonModel getProductCommonModel() {
        return productCommonModel;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    /**
     * @param rate 截取%缩小
     */
    private void setTextStyle(String rate) {
        strm = rate.split("~");
        int length = strm[0].length();
        SpannableString rateText = new SpannableString(rate + "%");
        rateText.setSpan(new TextAppearanceSpan(getActivity(), R.style.dayup_shouyilv_one), 0, length - 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        rateText.setSpan(new TextAppearanceSpan(getActivity(), R.style.dayup_shouyilv_two), length - 1, length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        rateText.setSpan(new TextAppearanceSpan(getActivity(), R.style.dayup_shouyilv_one), length, rateText.length() - 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        rateText.setSpan(new TextAppearanceSpan(getActivity(), R.style.dayup_shouyilv_two), rateText.length() - 1, rateText.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        txtRate.setText(rateText, TextView.BufferType.SPANNABLE);
    }
}
