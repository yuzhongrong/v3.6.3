package com.jinr.core.trade.purchase.product;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.TextAppearanceSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jinr.core.R;
import com.jinr.core.base.BaseFragment;
import com.jinr.core.config.Check;
import com.jinr.core.config.EventBusKey;
import com.jinr.core.config.UrlConfig;
import com.jinr.core.dayup.CommonProjectDetailActivity;
import com.jinr.core.regist.MyRotate3dAnimation;
import com.jinr.core.utils.CommonUtil;
import com.jinr.core.utils.MyDES;
import com.jinr.core.utils.MyLog;
import com.jinr.core.utils.MyhttpClient;
import com.jinr.core.utils.PreferencesUtils;
import com.jinr.core.utils.ToastUtil;
import com.jinr.core.utils.UmUtils;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONObject;
import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;

import model.BaseModel;
import model.ProductList;
import model.ProductModel;

public class ProductFragment extends BaseFragment implements View.OnClickListener, ChildScrollLayoutChangeListener {
    public static final String TAG = "MainProductFragment";


    private View mLayout;
    private Typeface tf;
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
    private TextView tv_refresh;
    private View image_refresh;

    private MyChildVerticalScrollLayout mChildScrollLayout;

    private String mProductCode;
    private String mProductName;
    private String mProductAssetid;
    private String mProductGoodType;
    private ProductModel mModel;
    private MediaPlayer mMediaPlayer;
    private MyRotate3dAnimation coinRotateAnimation;
    private int mPosition;

    @SuppressLint("ValidFragment")
    public ProductFragment() {
    }

    @SuppressLint("ValidFragment")
    public ProductFragment(ProductModel model, int position) {
        this.mModel = model;
        this.mPosition = position;


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (mLayout == null) {
            mLayout = inflater.inflate(R.layout.fragment_product2, container, false);
            tf = Typeface.createFromAsset(getActivity().getAssets(), "fonts/HYxyj.ttf");
            findViewById(mLayout);
            setListener();
            UmUtils.customsEvent(getActivity(), UmUtils.MAINREGULAR_CLICK, UmUtils.MAINREGULAR_HUM);
            setData(mModel);
        }
        //EventBus.getDefault().register(this);
        return mLayout;
    }

    @Override
    protected void findViewById(View view) {
        mChildScrollLayout = (MyChildVerticalScrollLayout) mLayout.findViewById(R.id.mChildScrollLayout);
        image_refresh = mLayout.findViewById(R.id.image_refresh);
        tv_refresh = (TextView) mLayout.findViewById(R.id.tv_refresh);
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
        mMediaPlayer = MediaPlayer.create(getActivity(), R.raw.coin);

        tv_one.setTypeface(tf);
        tv_two.setTypeface(tf);
        tv_three.setTypeface(tf);

        //创建硬币动画
        image_refresh.post(new Runnable() {
            @Override
            public void run() {
                float mCenterX = image_refresh.getWidth() / 2.0f;
                float mCenterY = image_refresh.getHeight() / 2.0f;
                coinRotateAnimation = new MyRotate3dAnimation(0.0f, 360.0f, mCenterX, mCenterY, 100f, true, 1);
                coinRotateAnimation.setDuration(500);
                coinRotateAnimation.setFillAfter(true);
                coinRotateAnimation.setInterpolator(new DecelerateInterpolator());
            }
        });
    }

    @Override
    protected void setListener() {
        tv_details.setOnClickListener(this);
        tv_next.setOnClickListener(this);
        mChildScrollLayout.addChangeListener(this);
    }

    @Override
    protected void initData() {
        //读取本地数据
        String productList = PreferencesUtils.getValueFromSPMap(getActivity(), PreferencesUtils.Keys.PRODUCTLIST, "");
        if (!TextUtils.isEmpty(productList)) {
            BaseModel<ProductList> mModel = new Gson().fromJson(productList, new TypeToken<BaseModel<ProductList>>() {
            }.getType());
            setData(mModel.getData().getList().get(mPosition));
        }
    }


    @Override
    protected void initUI() {
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (getUserVisibleHint() && mLayout != null) {
            checkLogin();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        checkLogin();
    }

    @Override
    public void onDestroy() {
        try {
            //EventBus.getDefault().unregister(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }

    private void checkLogin() {
        if (Check.is_login(getActivity())) {
            tv_details.setText("点击查看详情");
            tv_details.setVisibility(View.VISIBLE);
            tv_next.setVisibility(View.INVISIBLE);
        } else {
            tv_details.setVisibility(View.INVISIBLE);
            tv_next.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.tv_details:
                if (mProductAssetid == null || mProductAssetid.equals("")) {
                    ToastUtil.show(getActivity(), R.string.default_error);
                    return;
                }
                intent = new Intent(getActivity(), CommonProjectDetailActivity.class);
                intent.putExtra("assetid", mProductAssetid);
                intent.putExtra("name", mProductName);
                intent.putExtra("good_type", mProductGoodType);
                getActivity().startActivity(intent);
                break;
            default:
                break;
        }
    }

    /**
     * 设置各个产品的显示数据 @author Ysw created at 2017/3/21 19:25
     */
    public void setData(ProductModel model) {
        mProductName = model.getName();
        mProductCode = model.getCode();
        mProductAssetid = model.getAssetid();
        mProductGoodType = model.getGoods_type();
        reFreshUI(model);
    }

    private void reFreshUI(ProductModel model) {
        tv_expect.setText(model.getDesc());
        String text_top = model.getText_top();
        String[] split = text_top.split(";");
        tv_min.setText(split[0]);
        tv_depict.setText(split[1]);
        String one = model.getTextdown_left();
        String leftStr[] = one.split(";");
        tv_one.setText(leftStr[0]);
        tv_one_first.setText(leftStr[1]);
        tv_one_second.setText(leftStr[2]);
        String two = model.getTextdown_middle();
        String middleStr[] = two.split(";");
        tv_two.setText(middleStr[0]);
        tv_two_first.setText(middleStr[1]);
        tv_two_second.setText(middleStr[2]);
        String three = model.getTextdown_right();
        String rightStr[] = three.split(";");
        tv_three.setText(rightStr[0]);
        tv_three_first.setText(rightStr[1]);
        tv_three_second.setText(rightStr[2]);
        String rate = model.getInvestRateFinish();
        setTextStyle(rate);
    }

    private void setTextStyle(String rate) {
        switch (mProductName) {
            case "日增息":
            case "保险箱": {
                String strm[] = rate.split("~");
                int length = strm[0].length();
                SpannableString rateText = new SpannableString(rate + "%");
                rateText.setSpan(new TextAppearanceSpan(getActivity(), R.style.dayup_shouyilv_one), 0, length - 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                rateText.setSpan(new TextAppearanceSpan(getActivity(), R.style.dayup_shouyilv_two), length - 1, length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                rateText.setSpan(new TextAppearanceSpan(getActivity(), R.style.dayup_shouyilv_one), length, rateText.length() - 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                rateText.setSpan(new TextAppearanceSpan(getActivity(), R.style.dayup_shouyilv_two), rateText.length() - 1, rateText.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                tv_rate.setText(rateText, TextView.BufferType.SPANNABLE);
                break;
            }
            case "定期": {
                SpannableString rateText = new SpannableString(rate + "%");
                rateText.setSpan(new TextAppearanceSpan(getActivity(), R.style.shouyilv_one), 0, rateText.length() - 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                rateText.setSpan(new TextAppearanceSpan(getActivity(), R.style.shouyilv_two), rateText.length() - 1, rateText.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                tv_rate.setText(rateText, TextView.BufferType.SPANNABLE);
                break;
            }
            default:
                break;
        }
        try {
            getMultipleNetWork();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 获取是否显示4倍收益接口 @author Ysw created at 2017/3/21 19:21
     */
    protected void getMultipleNetWork() throws Exception {

       MyLog.e(TAG,"------->getMultipleNetWork()");
        RequestParams params = new RequestParams();
        String uid = PreferencesUtils.getValueFromSPMap(getActivity(), PreferencesUtils.Keys.UID);
        JSONObject cipher = new JSONObject();
        cipher.put(PreferencesUtils.Keys.UID, uid);
        cipher.put("productcode", mProductCode);
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
                    MyLog.e(TAG, "getMultipleNetWork onSuccess: ");
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
    public void doChange(int lastIndex, int currentIndex) {

    }

    @Override
    public void onReadyRrfresh() {
        image_refresh.setBackgroundResource(R.drawable.one);
        tv_refresh.setText("下拉赚钱");
    }

    @Override
    public void onReady() {
        image_refresh.setBackgroundResource(R.drawable.one);
        tv_refresh.setText("释放赚钱");
    }

    @Override
    public void doRefresh() {
        tv_refresh.setText("正在赚钱");
        startCoinAnima();
    }

    @Override
    public void overRefresh() {
        image_refresh.clearAnimation();
        image_refresh.setBackgroundResource(R.drawable.four);
        tv_refresh.setText("刷新成功");
    }


    /**
     * 硬币旋转动画，动画结束调接口
     */
    public void startCoinAnima() {
        image_refresh.clearAnimation();
        image_refresh.startAnimation(coinRotateAnimation);
        coinRotateAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(final Animation animation) {
                mMediaPlayer.start();
                image_refresh.setBackgroundResource(R.drawable.coin_refresh);
                AnimationDrawable drawable = (AnimationDrawable) image_refresh.getBackground();
                drawable.start();
                image_refresh.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if (mModel == null) {
                                getProduct();
                            } else {
                                mChildScrollLayout.snapToOrigin();
                            }
                            getMultipleNetWork();
                            EventBus.getDefault().post(true, EventBusKey.REFRESH_LIMIT);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, 450);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
    }

    /**
     * 存呗相关产品
     *
     * @throws Exception
     */
    public void getProduct() throws Exception {
        RequestParams params = new RequestParams();
        String uid = PreferencesUtils.getValueFromSPMap(getActivity(), PreferencesUtils.Keys.UID);
        JSONObject cipher = new JSONObject();
        cipher.put(PreferencesUtils.Keys.UID, uid);
        params.put("cipher", MyDES.encrypt(cipher.toString()));
        MyhttpClient.desPost(UrlConfig.CUNBEI_LIST, params, new AsyncHttpResponseHandler() {
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
                    String response = new String(arg2, "UTF-8");
                    response = CommonUtil.transResponse(response);
                    JSONObject jsb = new JSONObject(response);
                    String cipher = jsb.getString("cipher");
                    cipher = MyDES.decrypt(cipher);
                    MyLog.e(TAG, "onSuccess: " + cipher);
                    PreferencesUtils.putStringToSPMap(PreferencesUtils.Keys.PRODUCTLIST, cipher);
                    BaseModel<ProductList> mProductMode = new Gson().fromJson(cipher, new TypeToken<BaseModel<ProductList>>() {
                    }.getType());
                    if (mProductMode.isSuccess()) {
                        for (int i = 0; i < mProductMode.getData().getList().size(); i++) {
                            ProductModel productModel = mProductMode.getData().getList().get(i);
                            if (productModel.getName().equals(mModel.getName())) {
                                mModel = productModel;
                                setData(mModel);
                            }
                        }
                    } else {
                        ToastUtil.show(getActivity(), mProductMode.getMsg());
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

    /**
     * @author jzs created 2017/4/20
     */
    @Subscriber(tag = EventBusKey.REFRESH_MULTIPLE)
    public void getData(boolean refresh) {
        if (refresh) {
            try {
                getMultipleNetWork();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
