package com.jinr.core.guide;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import model.AdvertModel;
import model.BaseModel;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jinr.core.R;
import com.jinr.core.base.BaseData;
import com.jinr.core.config.UrlConfig;
import com.jinr.core.guide.adapter.ViewPagerAdapter;
import com.jinr.core.utils.CommonUtil;
import com.jinr.core.utils.FileUtil;
import com.jinr.core.utils.MyDES;
import com.jinr.core.utils.MyLog;
import com.jinr.core.utils.MyhttpClient;
import com.jinr.core.utils.PreferencesUtils;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;

/**
 * @author yangyu 功能描述：主程序入口类
 */
public class GuideActivity extends Activity implements OnClickListener, OnPageChangeListener {
    public static final String TAG = "GuideActivity";
    
    // 定义ViewPager对象
    private ViewPager viewPager;

    // 定义ViewPager适配器
    private ViewPagerAdapter vpAdapter;

    // 定义一个ArrayList来存放View
    private ArrayList<View> views;

    // 底部小点的图片
    private ImageView[] points;

    // 记录当前选中位置
    private int currentIndex;
    // private TextView tv_version;
    // private String version;
    private LinearLayout d_layout;
    private String img;
    private Thread newThread;
    private BaseModel<AdvertModel> result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.guide);
        d_layout = (LinearLayout) findViewById(R.id.d_layout);
        initView();
        initData();
    }

    /**
     * 初始化组件
     */
    private void initView() {
        // 实例化ArrayList对象
        views = new ArrayList<View>();
        // 实例化ViewPager
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        // 实例化ViewPager适配器
        vpAdapter = new ViewPagerAdapter(views, this);
    }

    /**
     * 初始化数据
     */
    private void initData() {
        LayoutInflater inflater = LayoutInflater.from(this);
        // 定义一个布局并设置参数
        try {
            PackageManager packageManager = getPackageManager();
            // getPackageName()是你当前类的包名，0代表是获取版本信息
            PackageInfo packInfo;

            packInfo = packageManager.getPackageInfo(
                    GuideActivity.this.getPackageName(), 0);
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }

        views.add(inflater.inflate(R.layout.what_new_one, null));
        views.add(inflater.inflate(R.layout.what_new_two, null));
        views.add(inflater.inflate(R.layout.what_new_three, null));
        views.add(inflater.inflate(R.layout.what_new_five, null));
        views.add(inflater.inflate(R.layout.what_new_four, null));
        // 设置数据
        viewPager.setAdapter(vpAdapter);
        // 设置监听
        viewPager.setOnPageChangeListener(this);

        // 初始化底部小点
        initPoint();
        try {
            PreferencesUtils.putMaskBooleanToSPMap(GuideActivity.this, "isShow", false);
            firstCharge();
            getImgAdd();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 初始化底部小点
     */
    private void initPoint() {
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.d_layout);

        points = new ImageView[5];

        // 循环取得小点图片
        for (int i = 0; i < 5; i++) {
            // 得到一个LinearLayout下面的每一个子元素
            points[i] = (ImageView) linearLayout.getChildAt(i);
            // 默认都设为灰色
            points[i].setEnabled(true);
            // 给每个小点设置监听
            points[i].setOnClickListener(this);
            // 设置位置tag，方便取出与当前位置对应
            points[i].setTag(i);

        }

        // 设置当面默认的位置
        currentIndex = 0;
        // 设置为白色，即选中状态
        points[currentIndex].setEnabled(false);
    }

    /**
     * 当滑动状态改变时调用
     */
    @Override
    public void onPageScrollStateChanged(int arg0) {

    }

    /**
     * 当当前页面被滑动时调用
     */

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {

    }

    /**
     * 当新的页面被选中时调用
     */

    @Override
    public void onPageSelected(int position) {
        // 设置底部小点选中状态
        setCurDot(position);
        if (position == 4) {

            d_layout.setVisibility(View.GONE);
        } else {
            d_layout.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 通过点击事件来切换当前的页面
     */
    @Override
    public void onClick(View v) {
//		int position = (Integer) v.getTag();
//		setCurView(position);
//		setCurDot(position);
    }

    /**
     * 设置当前的小点的位置
     */
    private void setCurDot(int positon) {
        if (positon < 0 || positon > 5 - 1 || currentIndex == positon) {
            return;
        }
        points[positon].setEnabled(false);
        points[currentIndex].setEnabled(true);

        currentIndex = positon;
    }

    protected void firstCharge() throws Exception {
        RequestParams params = new RequestParams();
        JSONObject cipher = new JSONObject();
        // cipher.put("money", 1000);
        cipher.put("type", "3");
        params.put("cipher", MyDES.encrypt(cipher.toString()));
        MyhttpClient.desPost(UrlConfig.FIRST_CHARGE, params, new AsyncHttpResponseHandler() {
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
                MyLog.e(TAG, "onSuccess: ");
                try {
                    String response = new String(arg2, "UTF-8");
                    response = CommonUtil.transResponse(response);
                    JSONObject jsb = new JSONObject(response);
                    String cipher = jsb.getString("cipher");
                    cipher = MyDES.decrypt(cipher);
                    JSONObject object = new JSONObject(cipher);
                    int result = object.getInt("code");
                    if (result == 1000) {
                        JSONObject listObject = object.getJSONObject("data");
                        img = listObject.getString("img");
                        PreferencesUtils.putMaskBooleanToSPMap(GuideActivity.this, "isShow", true);
                        FileUtil.checkDir(BaseData.SD_CARD_PATH);
                        newThread = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                Bitmap savebitmap;
                                try {
                                    savebitmap = (Bitmap) (FileUtil.getData(img, Bitmap.class));
                                    if (savebitmap != null) {
                                        FileUtil.saveImageToSDcard(savebitmap, BaseData.SD_CARD_PATH + BaseData.FIRST_PUSH + BaseData.SAVE_PHOTO_TYPE);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                        newThread.start(); // 启动线程
                    } else {
                        PreferencesUtils.putMaskBooleanToSPMap(GuideActivity.this, "isShow", false);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        });

    }

    // 获取广告页的图片地址
    private void getImgAdd() throws Exception {
        RequestParams params = new RequestParams();
        JSONObject cipher = new JSONObject();
        cipher.put("type", "3");
        params.put("cipher", MyDES.encrypt(cipher.toString()));
        MyhttpClient.desPost(UrlConfig.USER_NOTICE, params, new AsyncHttpResponseHandler() {
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
                    result = new Gson().fromJson(cipher, new TypeToken<BaseModel<AdvertModel>>() {
                    }.getType());
                    if (result.isSuccess()) {
                        PreferencesUtils.putCValueToSPMap(GuideActivity.this, "advId", result.getData().getId());
                        PreferencesUtils.putCValueToSPMap(GuideActivity.this, "img", result.getData().getImg());
                        PreferencesUtils.putCValueToSPMap(GuideActivity.this, "linkId", result.getData().getLinkid());
                        FileUtil.checkDir(BaseData.SD_CARD_PATH);
                        newThread = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                Bitmap savebitmap;
                                try {
                                    savebitmap = (Bitmap) (FileUtil.getData(result.getData().getImg(), Bitmap.class));
                                    if (savebitmap != null) {
                                        FileUtil.saveImageToSDcard(savebitmap, BaseData.SD_CARD_PATH + result.getData().getId() + BaseData.SAVE_PHOTO_TYPE);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                        newThread.start(); // 启动线程
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        });
    }

}
