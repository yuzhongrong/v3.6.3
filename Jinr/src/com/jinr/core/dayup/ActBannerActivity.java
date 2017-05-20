package com.jinr.core.dayup;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.jinr.core.R;
import com.jinr.core.base.BaseActivity;
import com.jinr.core.config.UrlConfig;
import com.jinr.core.news.ActDetailActivity;
import com.jinr.core.news.NewsCenterActivity;
import com.jinr.core.utils.CommonUtil;
import com.jinr.core.utils.DensityUtil;
import com.jinr.core.utils.MyDES;
import com.jinr.core.utils.MyhttpClient;
import com.jinr.core.utils.ToastUtil;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import org.apache.http.Header;
import org.json.JSONObject;

import java.util.ArrayList;

import model.ActBannerObj;
import model.ActBannerObj.ActBannerData;
import model.ActBannerObj.ActBannerList;

/**
 * 活动
 *
 * @author 966
 */
public class ActBannerActivity extends BaseActivity {

    private ViewPager vp;
    private ImageView iv_no_select;
    private ImageView iv_finish;
    private LinearLayout linlayout;
    private RelativeLayout bannerlayout;

    boolean flag = true;
    private int index = 0;
    private ArrayList<ActBannerList> list_data;

    private ArrayList<ImageView> list_pager_iv = new ArrayList<>();
    private int screenHeight;
    private int screenWidth;
    private int px_100;
    private int px_60;
    private int mPosition = 0;
    private TextView tv_more;
    private String latitude;
    private String longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_banner_activity);
        findViewById();
        initData();
        setListener();
        try {
            getDataRequest();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void initData() {
        vp.setPageMargin(30);
        vp.setOffscreenPageLimit(3);
        screenHeight = CommonUtil.getScreenHeight(ActBannerActivity.this);
        screenWidth = CommonUtil.getScreenWidth(ActBannerActivity.this);
        px_60 = DensityUtil.dip2px(ActBannerActivity.this, 60);
        px_100 = DensityUtil.dip2px(ActBannerActivity.this, 100);
        latitude = getIntent().getStringExtra("lat");
        longitude = getIntent().getStringExtra("lng");
    }

    @Override
    protected void findViewById() {
        bannerlayout = (RelativeLayout) findViewById(R.id.banner_lay);
        linlayout = (LinearLayout) findViewById(R.id.lin);
        vp = (ViewPager) findViewById(R.id.vp);
        iv_finish = (ImageView) findViewById(R.id.iv_finish);
        tv_more = (TextView) findViewById(R.id.tv_more);
    }

    @Override
    protected void initUI() {

    }

    @Override
    protected void setListener() {
        vp.setOnPageChangeListener(new OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                mPosition = position;
                if (iv_no_select != null) {
                    iv_no_select.setBackgroundResource(R.drawable.point_gray);
                }
                ImageView iv_select = (ImageView) linlayout.getChildAt(position);
                iv_select.setBackgroundResource(R.drawable.point_white);
                iv_no_select = iv_select;
                flag = false;
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {

            }

            @Override
            public void onPageScrollStateChanged(int position) {
                if (flag) {
                    ImageView iv = (ImageView) linlayout.getChildAt(index);
                    iv.setBackgroundResource(R.drawable.point_gray);
                }
            }
        });

        iv_finish.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ActBannerActivity.this.finish();
            }
        });

        tv_more.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("lat", latitude);
                intent.putExtra("lng", longitude);
                intent.setClass(ActBannerActivity.this, NewsCenterActivity.class);
                startActivity(intent);
                finish();
            }
        });

        bannerlayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        bannerlayout.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int x = (int) event.getX();
                int y = (int) event.getY();
                switch (event.getAction()) {
                    case MotionEvent.ACTION_UP:
                        if (list_data == null || list_data.size() == 0) {
                            break;
                        }
                        if (x < px_60 && y > px_100 && y < (screenHeight - px_100)) {
                            if (mPosition > 0) {
                                vp.setCurrentItem(mPosition - 1);
                            } else {
                                ActBannerActivity.this.finish();
                            }
                        } else if (x > (screenWidth - px_60) && y > px_100 && y < (screenHeight - px_100)) {
                            if (mPosition < list_data.size() - 1) {
                                vp.setCurrentItem(mPosition + 1);
                            } else {
                                ActBannerActivity.this.finish();
                            }
                        } else {
                            ActBannerActivity.this.finish();
                        }
                        break;

                    default:
                        break;
                }
                return false;
            }
        });
    }

    /**
     * 由后台返回图片的个数动态生成指引点
     *
     * @param size
     */
    private void addPoints(int size) {
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(20, 0, 0, 0);
        for (int i = 0; i < size; i++) {
            ImageView iv = new ImageView(this);
            iv.setLayoutParams(lp);
            if (i == index) {
                iv.setBackgroundResource(R.drawable.point_white);
            } else {
                iv.setBackgroundResource(R.drawable.point_gray);
            }
            linlayout.addView(iv);
        }
    }

    /**
     * 请求后台数据
     *
     * @throws Exception
     */
    protected void getDataRequest() throws Exception {
        RequestParams params = new RequestParams();
        JSONObject cipher = new JSONObject();
        cipher.put("type", "3");
        latitude = latitude.equals("4.9E-324") ? "" : latitude;
        longitude = longitude.equals("4.9E-324") ? "" : longitude;
        cipher.put("lat", latitude);
        cipher.put("lng", longitude);
        params.put("cipher", MyDES.encrypt(cipher.toString()));
        MyhttpClient.desPost(UrlConfig.ACT_BANNER_LIST, params, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                super.onStart();
                showWaittingDialog(ActBannerActivity.this);
            }

            @Override
            public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
                super.onFailure(arg0, arg1, arg2, arg3);
                ToastUtil.show(ActBannerActivity.this, R.string.default_error);
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
                    JSONObject object = new JSONObject(cipher);
                    int code = object.getInt("code");
                    if (code == 1000) {
                        ActBannerObj bannerObj = new Gson().fromJson(cipher, ActBannerObj.class);
                        ActBannerData data = bannerObj.data;
                        list_data = data.list;
                        if (list_data != null) {
                            int size = list_data.size();
                            if (size == 0) {
                                ToastUtil.show(ActBannerActivity.this, "暂无数据");
                            }
                            addPoints(size);
                            // 生成相应个数的Image填充viewpager
                            for (int i = 0; i < list_data.size(); i++) {
                                ImageView iv = new ImageView(ActBannerActivity.this);
                                // iv.setBackgroundResource(R.drawable.ic_loading);
                                // iv.setScaleType(ScaleType.FIT_XY);
                                ActBannerList actBannerList = list_data.get(i);
                                iv.setTag(actBannerList.img);
                                list_pager_iv.add(iv);
                            }
                            vp.setAdapter(new ActBannerAdapter());
                            vp.setCurrentItem(index);// 默认显示中间一张
                            vp.setPageTransformer(true, new ScaleTransformer(0.85f));
                        }
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

    private class ActBannerAdapter extends PagerAdapter {

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            final ImageView iv = list_pager_iv.get(position);
            String url = (String) iv.getTag();
            if (iv.getParent() != null) {
                ((ViewGroup) iv.getParent()).removeView(iv);
            }
            ImageLoader.getInstance().displayImage(url, iv, new ImageLoadingListener() {

                @Override
                public void onLoadingStarted(String arg0, View arg1) {

                }

                @Override
                public void onLoadingFailed(String arg0, View arg1, FailReason arg2) {

                }

                @Override
                public void onLoadingComplete(String arg0, View arg1, Bitmap arg2) {
                    iv.setImageBitmap(arg2);
                }

                @Override
                public void onLoadingCancelled(String arg0, View arg1) {

                }
            });

            iv.setScaleType(ScaleType.FIT_XY);
            container.addView(iv);
            iv.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (!CommonUtil.isFastDoubleClick()) {
                        if (list_data == null || list_data.size() == 0) {
                            return;
                        }
                        Intent intent = new Intent(ActBannerActivity.this, ActDetailActivity.class);
                        intent.putExtra("id", list_data.get(position).linkid);
                        intent.putExtra("turn", "1");
                        startActivity(intent);
                    }
                }
            });
            return iv;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {

        }

        @Override
        public int getCount() {
            return list_pager_iv.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object o) {
            return view == o;
        }
    }

}
