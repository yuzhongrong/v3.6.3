/*
 * MoreActivity.java
 * @author Andrew Lee
 * 2014-10-18 上午11:27:21
 */
package com.jinr.core.more;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jinr.core.MainActivity;
import com.jinr.core.R;
import com.jinr.core.base.BaseActivity;
import com.jinr.core.config.UrlConfig;
import com.jinr.core.ui.CustomDialog;
import com.jinr.core.updata.UpdataUtils;
import com.jinr.core.utils.CommonUtil;
import com.jinr.core.utils.GetImsi;
import com.jinr.core.utils.MyDES;
import com.jinr.core.utils.MyLog;
import com.jinr.core.utils.MyhttpClient;
import com.jinr.core.utils.PreferencesUtils;
import com.jinr.core.utils.ToastUtil;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

public class MoreActivity extends BaseActivity implements OnClickListener {
    private ImageView title_left_img; // title左边图片
    private TextView title_center_text; // title标题
    private TextView more_version_text; // 版本号
    private RelativeLayout about_us; // 关于我们
    private RelativeLayout rl_updata; // 检查更新
    private String version = "";
    private TextView tv_popple;
    private TextView tv_money;
    private ImageView image_notice;
    private RelativeLayout rl_service;
    private CustomDialog dialog;
    private boolean isShowSys;
    private int mVersionCode;
    private ImageView image_updata;
    private TextView customer_service_phone;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.the_more);
        findViewById();
        initData();
        initUI();
        setListener();
        try {
            send();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        initData();
        initUI();
    }

    protected void initData() {
        version = GetImsi.getVersion(MoreActivity.this);
        mVersionCode = getVersionInfo(MoreActivity.this);
    }

    @Override
    protected void findViewById() {
        title_left_img = (ImageView) findViewById(R.id.title_left_img);
        title_center_text = (TextView) findViewById(R.id.title_center_text);
        more_version_text = (TextView) findViewById(R.id.more_version);
        about_us = (RelativeLayout) findViewById(R.id.the_more_rl1);
        rl_updata = (RelativeLayout) findViewById(R.id.rl_updata);
        tv_popple = (TextView) findViewById(R.id.tv_popple);// 投资人数
        tv_money = (TextView) findViewById(R.id.tv_money);// 投资金额
        image_notice = (ImageView) findViewById(R.id.image_notice);
        image_updata = (ImageView) findViewById(R.id.image_updata);
        customer_service_phone = (TextView) findViewById(R.id.customer_service_phone);
        if (!PreferencesUtils.getCValueFromSPMap(MoreActivity.this, PreferencesUtils.Keys.KEFU_PHONE).equals("")) {
            customer_service_phone.setVisibility(View.VISIBLE);
            customer_service_phone.setText("客服电话  " + PreferencesUtils.getCValueFromSPMap(MoreActivity.this, PreferencesUtils.Keys.KEFU_PHONE));
        }
        rl_service = (RelativeLayout) findViewById(R.id.rl_service);
        rl_service.setOnClickListener(this);
        findViewById(R.id.the_more_cqwenti).setOnClickListener(this);// 常见问题
        isShowSys = getIntent().getBooleanExtra("isShowSys", false);
    }

    protected void initUI() {
        title_center_text.setText(R.string.more);
        more_version_text.setText("v" + version);
        if (isShowSys) {
            image_notice.setImageDrawable(getResources().getDrawable(R.drawable.notice_red));
            isShowSys = false;
        }
        int versionCood = PreferencesUtils.getServiceVersionCood(this);
        if (versionCood > mVersionCode) {
            image_updata.setImageDrawable(getResources().getDrawable(R.drawable.more_update_red));
        } else {
            image_updata.setImageDrawable(getResources().getDrawable(R.drawable.more_update));
        }
    }

    protected void setListener() {
        about_us.setOnClickListener(this);
        title_left_img.setOnClickListener(this);
        rl_updata.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.the_more_rl1: // 关于鲸鱼
                Intent intent_rl1 = new Intent(MainActivity.instance, AboutActivity.class);
                startActivity(intent_rl1);
                break;
            case R.id.the_more_cqwenti:// 常见问题
                startActivity(new Intent(MoreActivity.this, CjWentiActivity.class));
                break;
            case R.id.rl_updata:
                try {
                    new UpdataUtils(MoreActivity.this, true).checkUpdata(2);
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                break;
            case R.id.title_left_img:// title左侧图标
                finish();
                break;
            case R.id.rl_service:
                if (!PreferencesUtils.getCValueFromSPMap(MoreActivity.this, PreferencesUtils.Keys.KEFU_PHONE).equals("")) {
                    dialog = new CustomDialog(MoreActivity.this, getString(R.string.warning), getString(R.string.dialog_call),
                            PreferencesUtils.getCValueFromSPMap(MoreActivity.this, PreferencesUtils.Keys.KEFU_PHONE));
                    dialog.dialog_sure.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(Intent.ACTION_CALL,
                                    Uri.parse("tel:" + PreferencesUtils.getCValueFromSPMap(MoreActivity.this, PreferencesUtils.Keys.KEFU_PHONE)));
                            startActivity(intent);
                            dialog.dismiss();
                        }
                    });
                    dialog.show();
                }
                break;
            default:
                break;
        }
    }

    /**
     * 获取交易额和投资人信息 @author Ysw created at 2017/3/14 17:36
     */
    protected void send() throws Exception {
        showWaittingDialog(MoreActivity.this);
        RequestParams params = new RequestParams();
        MyhttpClient.desPost(UrlConfig.SUM_MONEY_INVESTOR, params, new AsyncHttpResponseHandler() {
            @Override
            public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
                super.onFailure(arg0, arg1, arg2, arg3);
                dismissWaittingDialog();
                ToastUtil.show(MoreActivity.this, R.string.default_error);
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
                    cipher = MyDES.decrypt(cipher);
                    JSONObject object = new JSONObject(cipher);
                    int code = object.getInt("code");
                    if (code == 1000) {
                        JSONObject data = object.getJSONObject("data");
                        String money = data.getString("total_money");
                        String people = data.getString("number");
                        rl_service.setVisibility(View.VISIBLE);
                        tv_popple.setText(people);
                        tv_money.setText(money);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    // 获取版本信息 Ysw 2016.09.27
    public int getVersionInfo(Context context) {
        try {
            mVersionCode = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return mVersionCode;
    }
}
