package com.jinr.core;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jinr.core.base.BaseActivity;
import com.jinr.core.base.BaseData;
import com.jinr.core.config.AppManager;
import com.jinr.core.config.Check;
import com.jinr.core.config.EventBusKey;
import com.jinr.core.config.UrlConfig;
import com.jinr.core.dayup.ActBannerActivity;
import com.jinr.core.news.ActDetailActivity;
import com.jinr.core.regist.NewLoginActivity;
import com.jinr.core.regist.NewRegisterActivity;
import com.jinr.core.regular.MyAssetsActivity;
import com.jinr.core.security.lockpanttern.pattern.UnlockGesturePasswordActivity;
import com.jinr.core.security.lockpanttern.view.LockPatternUtils;
import com.jinr.core.trade.purchase.product.MainFragment;
import com.jinr.core.trade.purchase.product.MainImageFragment;
import com.jinr.core.ui.MenuRightView;
import com.jinr.core.ui.NewCustomDialog;
import com.jinr.core.updata.UpdataUtils;
import com.jinr.core.utils.BaiduUtils;
import com.jinr.core.utils.CommonUtil;
import com.jinr.core.utils.LoadingDialog;
import com.jinr.core.utils.MyDES;
import com.jinr.core.utils.MyLog;
import com.jinr.core.utils.MyhttpClient;
import com.jinr.core.utils.PreferencesUtils;
import com.jinr.core.utils.ScreenObserver;
import com.jinr.core.utils.ScreenObserver.ScreenStateListener;
import com.jinr.core.utils.ToastUtil;
import com.jinr.core.utils.UmUtils;
import com.jinr.graph_view.yviewpager.YFragmentPagerAdapter;
import com.jinr.graph_view.yviewpager.YViewPager;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.umeng.analytics.MobclickAgent;
import com.umeng.message.ALIAS_TYPE;
import com.umeng.message.PushAgent;
import com.umeng.message.UTrack;

import org.apache.http.Header;
import org.json.JSONObject;
import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Timer;
import java.util.TimerTask;

import model.BaseModel;
import model.ProductList;
import model.ProductModel;
import model.SystemNoticeModel;
import model.UmMessageItem;

import static anetwork.channel.http.NetworkSdkSetting.context;

public class MainActivity extends BaseActivity implements OnClickListener {
    public static final String TAG = "MainActivity";
    HomeKeyEventBroadCastReceiver receiver; // home键监听需要的广播对象
    public static MainActivity instance = null;
    public static boolean isForeground = false;
    private static Boolean isExit = false;// 双击退出
    public static boolean isLock = true;// 图形解锁开关 ，防止Home持续产生图形解锁界面
    public static boolean isLock_longin = true;
    public static boolean homekey = false;
    private ScreenObserver mScreenObserver; // 屏幕休眠监听对象
    public LockPatternUtils lockp = new LockPatternUtils(this);
    public MenuRightView mMenuLayout;

    private LoadingDialog mLoadingdialog;
    private PushAgent mPushAgent;
    private int sysNum, actNum;
    public HashSet<String> sysList;
    private String latitude;
    private String longitude;

    private boolean isFirst = true;
    private RelativeLayout rl_regist;//注册
    private RelativeLayout rl_dislogin;//未登录
    private RelativeLayout rl_menu;//左边菜单按钮
    private TextView tv_assest;//点击进入我的资产列表
    private ImageView image_notice;//首页活动按钮
    private TextView tv_noticeNnm;//有新的活动时显示红点
    private Button bt_login;//登录
    private ArrayList<Fragment> mPageItemList = new ArrayList<>();
    private BaseModel<ProductList> mProductMode = new BaseModel<>();
    private ArrayList<ProductModel> mProductDataList = new ArrayList<>();
    private YViewPager mYViewPager;
    private YVFragmentAdapter yvFragmentAdapter;



    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        MobclickAgent.setDebugMode(true);
        AppManager.getAppManager().addActivity(this);
        instance = MainActivity.this;
        initBaidu(false);
        screenSleepListener();
        getUmMessage();
        umAppStartPush();
        registerHomeKeyReceive();
        gotoLockPattern();
        findViewById();
        setListener();
        initUI();
        try {
            getSystemNews();
            new UpdataUtils(this, false).checkUpdata(1);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
        isForeground = true;
        if (homekey) { // 返回时，如果home键标识打开则打开图形解锁
            homekey = false;
            gotoLockPattern();
        }
        setBottomBtn();
        if (Check.is_login(this)) {
            scrollTop();
        }

        Log.d(TAG, "onResume:");
    }

    private void screenSleepListener() {
        mScreenObserver = new ScreenObserver(this);
        mScreenObserver.requestScreenStateUpdate(new ScreenStateListener() {
            @Override
            public void onScreenOn() {// 屏幕苏醒则执行
                sendBroadcast(new Intent().setAction("action.refresh_actdetail"));
            }

            @Override
            public void onScreenOff() { // 屏幕休眠则执行
                gotoLockPattern();
            }
        });
    }

    /**
     * 1、savedPatternExists()，图形解锁的密码数据文件需要存在，且图形解锁开关打开。
     * 2、Check.is_login(this)，用户的登录状态需要为已登录。 3、isLock，当前没有图形解锁页面打开，防止重复打开图形锁
     * 4、isLock_longin，使图形解锁一次无法打开，在如登录成功，和返回首页跳转，重新加载的情况。
     * 5、跳转到图形锁页面，内部跳转判断条件为 @author Ysw created at 2017/3/15 15:36
     */
    public void gotoLockPattern() {
        MyLog.i("主页跳转图形锁", lockp.savedPatternExists() + "+" + Check.is_login(this) + "+" + isLock + isLock_longin);
        // 判断是否进入图形锁，分别是“是否图形解锁文件存在图形解锁开关是否打开”，“是否在登录状态”，“是已有否图形锁”，“是否登录成功或因为返回首页按键重新加载首页”
        if (lockp.savedPatternExists() && Check.is_login(this) && isLock && isLock_longin) {
            isLock = false;
            startActivity(new Intent(this, UnlockGesturePasswordActivity.class));
        }
        isLock_longin = true;
    }

    protected void findViewById() {
        //EventBus.getDefault().register(this);
        mLoadingdialog = new LoadingDialog(this);
        tv_noticeNnm = (TextView) findViewById(R.id.tv_noticeNnm);//有新活动时显示红点
        mMenuLayout = (MenuRightView) findViewById(R.id.mMenuLayout);//侧滑菜单
        rl_menu = (RelativeLayout) findViewById(R.id.rl_menu);//侧滑菜单按钮
        bt_login = (Button) findViewById(R.id.bt_login);// 登录
        rl_regist = (RelativeLayout) findViewById(R.id.rl_regist);//注册
        rl_dislogin = (RelativeLayout) findViewById(R.id.rl_dislogin);//未登录
        tv_assest = (TextView) findViewById(R.id.tv_assest); //点击进入资产
        image_notice = (ImageView) findViewById(R.id.image_notice);//点击显示首页活动
        //第一次进入显示充值送5000
        if (PreferencesUtils.getMaskBooleanFromSPMap(this, PreferencesUtils.Keys.IS_FIRST_MAIN)) {
            PreferencesUtils.putMaskBooleanToSPMap(this, PreferencesUtils.Keys.IS_FIRST_MAIN, false);
            findViewById(R.id.rl_image_ad).setVisibility(View.VISIBLE);
            findViewById(R.id.image_ad).setOnClickListener(this);
            findViewById(R.id.image_ad_close).setOnClickListener(this);
            ((ImageView) findViewById(R.id.image_ad)).setImageBitmap(BitmapFactory.decodeFile(BaseData.SD_CARD_PATH + BaseData.FIRST_PUSH + BaseData.SAVE_PHOTO_TYPE));
        } else {
            findViewById(R.id.rl_image_ad).setVisibility(View.GONE);
        }
    }

    protected void setListener() {
        rl_menu.setOnClickListener(this);
        tv_assest.setOnClickListener(this);
        image_notice.setOnClickListener(this);
        bt_login.setOnClickListener(this);
        rl_regist.setOnClickListener(this);
    }

    protected void initUI() {
        setBottomBtn();
        //先创建MainFragment和5个未登录显示的ImageFragment
        mPageItemList.add(new MainFragment());
        if (!Check.is_login(this)) {
            for (int i = 0; i < 5; i++) {
                mPageItemList.add(new MainImageFragment(i));
            }
        }
        mYViewPager = (YViewPager) findViewById(R.id.mYViewPager);
        yvFragmentAdapter = new YVFragmentAdapter(getSupportFragmentManager());
        mYViewPager.setAdapter(yvFragmentAdapter);
        mYViewPager.setOffscreenPageLimit(5);
    }


    class YVFragmentAdapter extends YFragmentPagerAdapter {

        public YVFragmentAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return mPageItemList.get(position);
        }

        @Override
        public int getCount() {
            return mPageItemList.size();
        }
    }

    /**
     * 设置底部按钮显示状态
     */
    private void setBottomBtn() {
        if (Check.is_login(MainActivity.this)) {
            rl_dislogin.setVisibility(View.GONE);
        } else {
            rl_dislogin.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 设置ImageFragment的显示
     */
    private void setImageFragment() {
        if (!Check.is_login(this) && mPageItemList.size() != 6) {
            for (int i = 0; i < 5; i++) {
                mPageItemList.add(new MainImageFragment(i));
            }
        } else if (Check.is_login(this) && mPageItemList.size() == 1) {
            for (int i = mPageItemList.size(); i < 1; i--) {
                mPageItemList.remove(i);
            }
        }
        yvFragmentAdapter.notifyDataSetChanged();
    }

    private void umAppStartPush() {
        mPushAgent = PushAgent.getInstance(this);
        mPushAgent.onAppStart();
    }

    private void registerHomeKeyReceive() {
        receiver = new HomeKeyEventBroadCastReceiver();
        registerReceiver(receiver, new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS));
    }

    private void getUmMessage() {
        Intent intent = getIntent();
        if (intent != null && intent.getSerializableExtra("message") != null) {
            UmMessageItem item = (UmMessageItem) intent.getSerializableExtra("message");
            if (item != null) {
                int type = 0;
                if (item.getDisplayType() != null && !item.getDisplayType().equals("")) {
                    type = Integer.parseInt(item.getDisplayType());
                }
                if (type >= 0 && type < 4) {
                    isLock = true;
                    Intent intentNew = new Intent();
                    long id = item.getId();
                    if (type == 1) {
                        intentNew.putExtra("id", id + "");
                        intentNew.putExtra("turn", "1");
                        intentNew.setClass(this, ActDetailActivity.class);
                    }
                    if (type == 2) {
                        intentNew.putExtra("id", id + "");
                        intentNew.putExtra("turn", "2");
                        intentNew.setClass(this, ActDetailActivity.class);
                    }
                    if (type == 3) {
                        PreferencesUtils.putMaskBooleanToSPMap(MainActivity.this, EventBusKey.UNLOCKPASSWORD, false);
                        intentNew.putExtra("id", id + "");
                        intentNew.putExtra("turn", "1");
                        intentNew.setClass(this, ActDetailActivity.class);
                    }
                    startActivity(intentNew);
                }
            }
        }
    }


    /**
     * 自定义返回键的效果
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exitBy2Click();
        }
        return true;
    }

    @Override
    protected void initData() {

    }

    /**
     * 两次返回键退出APP @author Ysw created at 2017/3/15 18:35
     */
    private void exitBy2Click() {
        Timer tExit;
        if (!isExit) {
            isExit = true;
            ToastUtil.show(this, getResources().getString(R.string.back_to_exit));
            tExit = new Timer();
            tExit.schedule(new TimerTask() {
                @Override
                public void run() {
                    isExit = false;
                }
            }, 1000);
        } else {
            isLock_longin = true;
            MobclickAgent.onKillProcess(this);
            AppManager.getAppManager().finishAllActivity();
            AppManager.getAppManager().AppExit(this);
        }
    }

    @Override
    public void onClick(View v) {
        if (CommonUtil.isFastDoubleClick()) return;
        switch (v.getId()) {
            case R.id.image_notice:
                startLocation();
                break;
            case R.id.rl_menu:
                UmUtils.customsEvent(MainActivity.this, UmUtils.LOGO_CLICK, UmUtils.LOGO_NUM);
                showLeftMenu();
                break;
            case R.id.tv_assest:
                startActivity(new Intent(MainActivity.this, MyAssetsActivity.class));
                overridePendingTransition(R.anim.right_in, R.anim.left_out);
                break;
            case R.id.bt_login:
                startActivity(new Intent(MainActivity.this, NewLoginActivity.class));
                overridePendingTransition(R.anim.right_in, R.anim.left_out);
                break;
            case R.id.rl_regist:
                startActivity(new Intent(MainActivity.this, NewRegisterActivity.class));
                overridePendingTransition(R.anim.right_in, R.anim.left_out);
                break;
            case R.id.image_ad_close:
                findViewById(R.id.rl_image_ad).setVisibility(View.GONE);
                break;
            case R.id.image_ad:
                findViewById(R.id.rl_image_ad).setVisibility(View.GONE);
                startActivity(new Intent(MainActivity.this, NewRegisterActivity.class));
                overridePendingTransition(R.anim.right_in, R.anim.left_out);
                break;
            default:
                break;
        }
    }

    /**
     * 初始化经纬度
     *
     * @param jump 是否跳转页面
     */
    private void initBaidu(final boolean jump) {
        BaiduUtils.getInstance().initBaidu(new BaiduUtils.BaiduLocationListener() {
            @Override
            public void onSuccess(BDLocation location) {
                latitude = location.getLatitude() + "";
                longitude = location.getLongitude() + "";
                if (jump) {
                    startLocation();
                }
            }

            @Override
            public void onFail() {
                if (jump) {
                    tv_noticeNnm.setVisibility(View.GONE);
                    PreferencesUtils.UnReadActionToSPMap(MainActivity.this);
                    Intent intent = new Intent();
                    intent.putExtra("lat", latitude);
                    intent.putExtra("lng", longitude);
                    intent.setClass(MainActivity.this, ActBannerActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.right_in, R.anim.left_out);
                }
            }
        });
    }


    /**
     * 跳转活动列表
     */
    public void startLocation() {
        if (TextUtils.isEmpty(latitude) && TextUtils.isEmpty(longitude)) {
            initBaidu(true);
        } else {
            tv_noticeNnm.setVisibility(View.GONE);
            PreferencesUtils.UnReadActionToSPMap(MainActivity.this);
            Intent intent = new Intent();
            intent.putExtra("lat", latitude);
            intent.putExtra("lng", longitude);
            intent.setClass(MainActivity.this, ActBannerActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.right_in, R.anim.left_out);
        }
    }

    public void scrollTop() {
        mYViewPager.setCurrentItem(0);
    }

    /**
     * 对home广播监听，如果监听到则将标志homekey打开使在resume时打开图形锁，
     * 如果在图形锁打开的情况下监听到home键则结束所有Active
     *
     * @author fym
     */
    class HomeKeyEventBroadCastReceiver extends BroadcastReceiver {
        static final String SYSTEM_REASON = "reason";
        static final String SYSTEM_HOME_KEY = "homekey";// home key
        static final String SYSTEM_RECENT_APPS = "recentapps";// long home key

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {
                String reason = intent.getStringExtra(SYSTEM_REASON);
                if (reason != null) {
                    if (reason.equals(SYSTEM_HOME_KEY)) {
                        // home key处理点
                        if (!isLock) {// 如果存在图形解锁，则结束所有Active
                            AppManager.getAppManager().finishAllActivity();
                        }
                        homekey = true;
                    }
                }
            }
        }
    }

    public void showLeftMenu() {
        mMenuLayout.openDrawer(Gravity.LEFT);
    }

    public void closeLeftMenu() {
        mMenuLayout.closeDrawer(Gravity.LEFT);
    }

    public void setRedPoint() {
        if (sysNum > 0 || actNum > 0) {
            tv_noticeNnm.setVisibility(View.VISIBLE);
        } else {
            tv_noticeNnm.setVisibility(View.GONE);
        }
    }

    private boolean hasSysNews(String edition) {
        HashSet<String> oldDataStr = PreferencesUtils.getEditenFormSPMap(MainActivity.this, PreferencesUtils.Keys.IS_SYS_NEWS);
        if (oldDataStr.contains(edition)) {
            return false;
        } else {
            return true;
        }
    }

    private boolean hasActs(String edition) {
        HashSet<String> oldDataStr = PreferencesUtils.getReadAction(MainActivity.this, PreferencesUtils.Keys.READ_ACT);
        if (oldDataStr.contains(edition)) {
            return false;
        } else {
            return true;
        }
    }


    /**
     * 存呗相关产品
     *
     * @throws Exception
     */
    public void getProduct() throws Exception {
        RequestParams params = new RequestParams();
        String uid = PreferencesUtils.getValueFromSPMap(MainActivity.this, PreferencesUtils.Keys.UID);
        JSONObject cipher = new JSONObject();
        cipher.put(PreferencesUtils.Keys.UID, uid);
        params.put("cipher", MyDES.encrypt(cipher.toString()));
        MyhttpClient.desPost(UrlConfig.CUNBEI_LIST, params, new AsyncHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                if (isFirst) mLoadingdialog.show();
            }

            @Override
            public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
                super.onFailure(arg0, arg1, arg2, arg3);
                if (isFirst) mLoadingdialog.dismiss();
                isFirst = false;
            }

            @Override
            public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
                super.onSuccess(arg0, arg1, arg2);
                if (isFirst) mLoadingdialog.dismiss();
                isFirst = false;
                MyLog.e(TAG, "getProduct onSuccess");
                try {
                    String response = new String(arg2, "UTF-8");
                    response = CommonUtil.transResponse(response);
                    JSONObject jsb = new JSONObject(response);
                    String cipher = jsb.getString("cipher");
                    cipher = MyDES.decrypt(cipher);
                    MyLog.e(TAG, "MainActivity.onSuccess：" + cipher);
                    mProductMode = new Gson().fromJson(cipher, new TypeToken<BaseModel<ProductList>>() {
                    }.getType());
                    if (mProductMode.isSuccess()) {
                        mProductDataList.addAll(mProductMode.getData().getList());
                    } else {
                        ToastUtil.show(MainActivity.this, mProductMode.getMsg());
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
     * 系统消息
     *
     * @throws Exception
     */
    public void getSystemNews() throws Exception {
        sysNum = 0;
        actNum = 0;
        RequestParams params = new RequestParams();
        JSONObject cipher = new JSONObject();
        String uid = PreferencesUtils.getValueFromSPMap(MainActivity.this, PreferencesUtils.Keys.UID);
        cipher.put(PreferencesUtils.Keys.UID, uid);
        if (!PreferencesUtils.getCValueFromSPMap(MainActivity.this, PreferencesUtils.Keys.IS_ACT_NEWS_ID).equals("")) {
            cipher.put("goods_type", PreferencesUtils.getCValueFromSPMap(MainActivity.this, PreferencesUtils.Keys.IS_ACT_NEWS_ID));
        }
        params.put("cipher", MyDES.encrypt(cipher.toString()));
        MyhttpClient.desPost(UrlConfig.NEWS_CENTER_READ, params, new AsyncHttpResponseHandler() {
            @Override
            public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
                super.onFailure(arg0, arg1, arg2, arg3);
            }

            @Override
            public void onStart() {
                super.onStart();
            }

            @Override
            public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
                super.onSuccess(arg0, arg1, arg2);
                MyLog.e(TAG, "getSystemNews onSuccess");
                try {
                    sysList = new HashSet<>();
                    HashSet<String> actList = new HashSet<>();
                    String response = new String(arg2, "UTF-8");
                    response = CommonUtil.transResponse(response);
                    JSONObject jsb = new JSONObject(response);
                    String cipher = jsb.getString("cipher");
                    cipher = MyDES.decrypt(cipher);
                    JSONObject object = new JSONObject(cipher);
                    int code = object.getInt("code");
                    if (code == 1000) {
                        BaseModel<SystemNoticeModel> model = new Gson().fromJson(cipher, new TypeToken<BaseModel<SystemNoticeModel>>() {
                        }.getType());
                        String sysNewsEdition = model.getData().getGonggao();
                        String actEdition = model.getData().getHuodong();
                       // EventBus.getDefault().postSticky(model.getData().getBk(), EventBusKey.BING_BANK_CARD);
                        if (model.getData().getBk() != PreferencesUtils.getIntFromSPMap(MainActivity.this, PreferencesUtils.Keys.IS_BIND_CARD)) {
                            PreferencesUtils.putIntToSPMap(MainActivity.this, PreferencesUtils.Keys.IS_BIND_CARD, model.getData().getBk());
                            EventBus.getDefault().postSticky(model.getData().getBk(), EventBusKey.BING_BANK_CARD);
                        }
                        if (hasSysNews(sysNewsEdition)) {
                            sysNum++;
                            sysList.add(sysNewsEdition);
                            PreferencesUtils.putUnreadToSPMap(MainActivity.this, PreferencesUtils.Keys.UNREAD_SYS_NEWS, sysList);
                        }
                        if (hasActs(actEdition)) {
                            actNum++;
                            actList.add(actEdition);
                            PreferencesUtils.putUnReadAction(MainActivity.this, PreferencesUtils.Keys.UNREAD_ACT, actList);
                        }
                        setRedPoint();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }



    private NewCustomDialog dialog;//被t 弹出对话框
    @Subscriber(tag = EventBusKey.T_LINE)
    public void doT(String str) {
        dialog = new NewCustomDialog(this, getString(R.string.warning), getString(R.string.relogin));
        dialog.btn_custom_dialog_sure.setText(getString(R.string.dialog_call_bt_ok));
        dialog.btn_custom_dialog_cancel.setText(getString(R.string.dialog_call_bt_cancel));
        dialog.btn_custom_dialog_cancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {//取消退出app
                PreferencesUtils.clearSPMap(MainActivity.instance);
                dialog.dismiss();
                AppManager.getAppManager().AppExit(MainActivity.this);
            }
        });
        dialog.btn_custom_dialog_sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                PushAgent mPushAgent = PushAgent.getInstance(context);
                mPushAgent.removeAlias(PreferencesUtils.getValueFromSPMap(context, PreferencesUtils.Keys.UID),
                        ALIAS_TYPE.SINA_WEIBO, new UTrack.ICallBack() {
                            @Override
                            public void onMessage(boolean isSuccess, String message) {
                            }
                        });
                PreferencesUtils.putLastTelToSPMap(PreferencesUtils.Keys.TEL, PreferencesUtils.getValueFromSPMap(context, PreferencesUtils.Keys.TEL));
                PreferencesUtils.clearSPMap(MainActivity.instance);
                // AppManager.getAppManager().finishAllActivity();
                startActivity(new Intent(MainActivity.this, NewLoginActivity.class));
                overridePendingTransition(R.anim.right_in, R.anim.left_out);

            }
        });

        dialog.show();
    }





    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
        isForeground = false;
        Log.d(TAG, "onPause:");
    }

    @Override
    protected void onStop() {
        super.onStop();

        Log.d(TAG, "onStop:");
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(receiver);
        super.onDestroy();
        try {
            mScreenObserver.stopScreenStateUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
        AppManager.getAppManager().finishActivity(this);
    }
}