package com.jinr.core.trade.purchase.product;


import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import com.jinr.core.gift.share.MyGiftActivity;
import com.jinr.core.regist.MyRotate3dAnimation;
import com.jinr.core.ui.DialogHeadNotice;
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

import java.util.ArrayList;

import model.AddRateModel;
import model.BaseModel;
import model.HeadNoticeObj;
import model.LiveProductList;
import model.LiveProductModel;
import model.MainAboutList;
import model.ProductCommonModel;
import model.UidObj;

public class LiveProductFragment extends BaseFragment implements View.OnClickListener, ChildScrollLayoutChangeListener {
    public static final String TAG = "LiveProductFragment";

    private View mLayout;
    private Typeface tf;
    private TextView tv_expect;
    private TextView tv_rate;
    private TextView tv_min;
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
    private boolean isShow = true;
    private MyChildVerticalScrollLayout mChildScrollLayout;

    private BaseModel<LiveProductList> mLiveProductMode = new BaseModel<>();
    private BaseModel<AddRateModel> mAddRateModel = new BaseModel<>();
    private ArrayList<LiveProductModel> mLiveProductDataList = new ArrayList<>();
    private String mProductAssetid;
    private ProductCommonModel mModel;
    private MediaPlayer mMediaPlayer;
    private MyRotate3dAnimation coinRotateAnimation;
    private Handler mHadler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    mChildScrollLayout.snapToOrigin();
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //EventBus.getDefault().register(this);
        if (mLayout == null) {
            mLayout = inflater.inflate(R.layout.fragment_live_product, container, false);
            tf = Typeface.createFromAsset(getActivity().getAssets(), "fonts/HYxyj.ttf");
            findViewById(mLayout);
            setListener();
            initData();
            UmUtils.customsEvent(getActivity(), UmUtils.MAINREGULAR_CLICK, UmUtils.MAINREGULAR_HUM);
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
        tv_rate = (TextView) mLayout.findViewById(R.id.tv_rate);
        tv_min = (TextView) mLayout.findViewById(R.id.tv_min);
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
        // 未登录情况下的滑动数据 @author Ysw created at 2017/3/21 19:10
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
        String liveData = PreferencesUtils.getStringToKey(PreferencesUtils.Keys.LIVEPRODUCT, "");
        if (!TextUtils.isEmpty(liveData)) {
            mLiveProductMode = new Gson().fromJson(liveData, new TypeToken<BaseModel<LiveProductList>>() {
            }.getType());
            mLiveProductDataList = mLiveProductMode.getData().getList();
            setProductCommonData(mLiveProductDataList.get(0));
            reFreshUI(mLiveProductDataList.get(0));
        }
        try {
            getCombinedID(false);
            getInportantNotice();
            getGuildDataNetWork();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void initUI() {
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (getUserVisibleHint() && mLayout != null) {
            MyLog.e(TAG, "LiveProductFragment.setUserVisibleHint：" + "我被执行了");
            checkLogin();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        MyLog.e(TAG, "onResume: " + "我被执行了");
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
                intent.putExtra("name", "活期");
                intent.putExtra("good_type", "2015");
                getActivity().startActivity(intent);
                break;
            default:
                break;
        }
    }

    private void reFreshUI(LiveProductModel mode) {
        mProductAssetid = mode.getAssetid();
        tv_expect.setText("七日年化收益率");
        String text_top = mode.getText_top();
        tv_min.setText(text_top);
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
        String rate = mode.getShouyulv();
        setTextStyle(rate);
    }

    private void setTextStyle(String rate) {
        SpannableString rateText = new SpannableString(rate + "%");
        rateText.setSpan(new TextAppearanceSpan(getActivity(), R.style.shouyilv_one), 0, rateText.length() - 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        rateText.setSpan(new TextAppearanceSpan(getActivity(), R.style.shouyilv_two), rateText.length() - 1, rateText.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tv_rate.setText(rateText, TextView.BufferType.SPANNABLE);
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
                tv_refresh.setText("正在赚钱");
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
                            getCombinedID(true);
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
     * 获取活期产品数据 @author Ysw created at 2017/3/23 9:13
     */
    protected void getCombinedID(final boolean refresh) throws Exception {
        MyLog.e(TAG, "getCombinedID: ");
        RequestParams params = new RequestParams();
        MyhttpClient.desPost(UrlConfig.GET_ZUHE, params, new AsyncHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
            }

            @Override
            public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
                super.onFailure(arg0, arg1, arg2, arg3);
                ToastUtil.show(getActivity(), R.string.default_error);
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
                    MyLog.e(TAG, "getCombinedID.onSuccess: ");
                    PreferencesUtils.putStringToSPMap(PreferencesUtils.Keys.LIVEPRODUCT, cipher);
                    mLiveProductMode = new Gson().fromJson(cipher, new TypeToken<BaseModel<LiveProductList>>() {
                    }.getType());
                    if (mLiveProductMode.isSuccess()) {
                        mLiveProductDataList = mLiveProductMode.getData().getList();
                        PreferencesUtils.putCValueToSPMap(getActivity(), PreferencesUtils.Keys.KEFU_PHONE, mLiveProductMode.getData().getService_phone());
                        reFreshUI(mLiveProductDataList.get(0));
                        setProductCommonData(mLiveProductDataList.get(0));
                        getAddRateNetWork();
                    } else {
                        ToastUtil.show(getActivity(), mLiveProductMode.getMsg());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFinish() {
                super.onFinish();
                if (refresh) new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(1000);
                            Message message = new Message();
                            message.what = 1;
                            mHadler.sendMessage(message);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        });
    }

    /**
     * 获取未登录时Tab页的显示数据 @author Ysw created at 2017/3/23 9:19
     */
    public void getGuildDataNetWork() throws Exception {
        MyLog.e(TAG, "getGuildDataNetWork: ");
        RequestParams params = new RequestParams();
        MyhttpClient.desPost(UrlConfig.ABOUNT_JINR, params, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                super.onStart();
                MyLog.e(TAG, "getGuildDataNetWork:onStart ");
            }

            @Override
            public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
                super.onFailure(arg0, arg1, arg2, arg3);
                MyLog.e(TAG, "getGuildDataNetWork:onFailure ");
                ToastUtil.show(getActivity(), R.string.default_error);
            }

            @Override
            public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
                super.onSuccess(arg0, arg1, arg2);
                MyLog.e(TAG, "getGuildDataNetWork:onSuccess ");
                try {
                    String response = new String(arg2, "UTF-8");
                    response = CommonUtil.transResponse(response);
                    JSONObject jsb = new JSONObject(response);
                    String cipher = jsb.getString("cipher");
                    String desc = MyDES.decrypt(cipher);
                    MyLog.e(TAG, "getGuildDataNetWork.onSuccess：" + desc);
                    PreferencesUtils.putStringToSPMap(PreferencesUtils.Keys.LIVEPRODUCT + "GUILDDATE", desc);
                    BaseModel<MainAboutList> result = new Gson().fromJson(desc, new TypeToken<BaseModel<MainAboutList>>() {
                    }.getType());
                    if (result.isSuccess()) {
                        EventBus.getDefault().postSticky(result.getData().getList(), EventBusKey.IMAGE_FRAGMENT);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }


    /**
     * 获取活期产品数据 @author Ysw created at 2017/3/23 9:13
     */
    protected void getAddRateNetWork() throws Exception {
        RequestParams params = new RequestParams();
        String uid = PreferencesUtils.getValueFromSPMap(getActivity(), PreferencesUtils.Keys.UID);
        if (uid.equals("")) return;
        UidObj obj = new UidObj(uid);
        String desStr = MyDES.encrypt(new Gson().toJson(obj));
        params.put("cipher", desStr);
        MyhttpClient.desPost(UrlConfig.NOW_PLUSYIELD, params, new AsyncHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
            }

            @Override
            public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
                super.onFailure(arg0, arg1, arg2, arg3);
                ToastUtil.show(getActivity(), R.string.default_error);
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
                    mAddRateModel = new Gson().fromJson(cipher, new TypeToken<BaseModel<AddRateModel>>() {
                    }.getType());
                    if (mAddRateModel.isSuccess()) {
                        String plus_shouyulv = mAddRateModel.getData().getPlus_shouyulv();
                        if (!TextUtils.isEmpty(plus_shouyulv)) {
                            setAddRate(plus_shouyulv);
                        }
                    } else {
                        ToastUtil.show(getActivity(), mLiveProductMode.getMsg());
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
     * 获取重要公告接口 @author Ysw created at 2017/3/16 16:01
     */
    private void getInportantNotice() throws Exception {
        RequestParams params = new RequestParams();
        MyhttpClient.desPost(UrlConfig.HEAD_NOTICE, params, new AsyncHttpResponseHandler() {
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
                    MyLog.e(TAG, "getInportantNotice.onSuccess：" + cipher);
                    JSONObject object = new JSONObject(cipher);
                    int code = object.getInt("code");
                    if (code == 1000) {
                        JSONObject data = object.getJSONObject("data");
                        HeadNoticeObj obj = new HeadNoticeObj(data.getString("edition"), data.getString("pop_content"), data.getString("url"));
                        if (isShow) {
                            isShow = false;
                            showDialogNotice(obj);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 显示重要的公告并设置相应的点击事件 @author Ysw created at 2017/3/16 15:48
     */
    private void showDialogNotice(final HeadNoticeObj obj) {
        final String edition = obj.edition;
        String old_edition = PreferencesUtils.getCValueFromSPMap(getActivity(), PreferencesUtils.Keys.HEAD_NOTICE_EDITION);
        if ((old_edition != null && !old_edition.equals("")) && edition.equals(old_edition))
            return;
        saveNoticeEdition(edition);
        final DialogHeadNotice dialog = new DialogHeadNotice(this.getActivity(), "重要公告", obj.content);
        dialog.btn_custom_dialog_sure.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Intent intent = new Intent(LiveProductFragment.this.getActivity(), MyGiftActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("url", obj.url);
                bundle.putString("title", "公告详情");
                intent.putExtras(bundle);
                LiveProductFragment.this.getActivity().startActivity(intent);
            }
        });
        dialog.iv_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    /**
     * 保存重要公告的版本信息用来之后的版本比对，以此来判断显示不显示重要公告 @author Ysw created at 2017/3/16 15:52
     */
    private void saveNoticeEdition(String edition) {
        PreferencesUtils.putCValueToSPMap(getActivity(), PreferencesUtils.Keys.HEAD_NOTICE_EDITION, edition);
    }

    /**
     * 首页点击转入，需要拿到活期的数据 @author Ysw created at 2017/3/24 8:36
     */
    public ProductCommonModel getCommonModel() {
        return mModel;
    }

    /**
     * 将活期数据放置到一个公共的Model中，转入转出需要这些数据支持 @author Ysw created at 2017/3/24 8:51
     */
    public void setProductCommonData(LiveProductModel model) {
        mModel = new ProductCommonModel();
        mModel.setAssetid(model.getAssetid());
        mModel.setCode(model.getCode_id());
        mModel.setName("活期");
        mModel.setType("2015");
        mModel.setRate(model.getShouyulv());
        mModel.setDescrible(model.getText_top());
    }


    /**
     * 福利界面post的活期加息 @author Ysw created at 2017/4/7 10:43
     */
    @Subscriber(tag = EventBusKey.MAIN_CURRENT)
    public void eventSetAddRate(String data) {
        setAddRate(data);
    }

    /**
     * 福利界面 EventBus post 首页进而调用此方法 @author Ysw created at 2017/4/7 13:09
     */
    public void setAddRate(String addRate) {
        double v = Double.parseDouble(addRate);
        if (v <= 0) return;
        String rate = mLiveProductDataList.get(0).getShouyulv() + "%";
        addRate = "+" + addRate + "%";
        SpannableString rateText = new SpannableString(rate + addRate);
        rateText.setSpan(new TextAppearanceSpan(getActivity(), R.style.shouyilv_one), 0, rate.length() - 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        rateText.setSpan(new TextAppearanceSpan(getActivity(), R.style.shouyilv_two), rate.length() - 1, rate.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        rateText.setSpan(new TextAppearanceSpan(getActivity(), R.style.shouyilv_addrate_one), rate.length(), rateText.length() - 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        rateText.setSpan(new TextAppearanceSpan(getActivity(), R.style.shouyilv_addrate_two), rateText.length() - 1, rateText.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tv_rate.setText(rateText, TextView.BufferType.SPANNABLE);
    }
}
