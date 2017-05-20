package com.jinr.core.trade.my_getCash;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jinr.core.R;
import com.jinr.core.base.BaseActivity;
import com.jinr.core.config.IntentCode;
import com.jinr.core.config.UrlConfig;
import com.jinr.core.utils.CommonUtil;
import com.jinr.core.utils.MyDES;
import com.jinr.core.utils.MyhttpClient;
import com.jinr.core.utils.PreferencesUtils;
import com.jinr.core.utils.ToastUtil;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONObject;

/**
 * 转出添加城市信息
 *
 * @author 640
 */
public class CityAddActivity extends BaseActivity implements OnClickListener {
    private ImageView title_left_img; // title左边图片
    private TextView title_center_text; // title标题
    private RelativeLayout city_select_rl; // 选择城市
    public String cityName;// 存放城市名
    public String cityNum;// 存放城市编号
    private TextView city_select_tv; // 城市文本
    private String user_id = "";
    private Button submit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city_add);
        initData();
        findViewById();
        initUI();
        setListener();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
            if (data == null) return;
            if (data.getStringExtra("cityName") != null && !data.getStringExtra("cityName").equals("")) {
                cityName = data.getStringExtra("cityName");
                cityNum = data.getStringExtra("cityNum");
                city_select_tv.setText(cityName);
                submit.setBackgroundResource(R.drawable.btn_blue_bg);
                submit.setClickable(true);
            }
        }
    }

    @Override
    protected void initData() {
        user_id = PreferencesUtils.getValueFromSPMap(this, PreferencesUtils.Keys.UID);
    }

    @Override
    protected void findViewById() {
        title_left_img = (ImageView) findViewById(R.id.title_left_img);
        title_center_text = (TextView) findViewById(R.id.title_center_text);
        // 开户城市
        city_select_rl = (RelativeLayout) findViewById(R.id.bind_bank_rl2);
        city_select_tv = (TextView) findViewById(R.id.tv_city);
        submit = (Button) findViewById(R.id.bind_bank_submit);
    }

    @Override
    protected void initUI() {
        title_center_text.setText("城市添加");
    }

    @Override
    protected void setListener() {
        city_select_rl.setOnClickListener(this);
        submit.setOnClickListener(this);
        submit.setClickable(false);
        title_left_img.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_left_img:// 左侧图标
                CityAddActivity.this.setResult(IntentCode.CITY_ADD);
                finish();
                break;

            case R.id.bind_bank_submit:
                try {
                    sendInsertCity();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.bind_bank_rl2:// 城市选择
                Intent intentCityList = new Intent();
                intentCityList.setComponent(new ComponentName("com.jinr.core", "com.jinr.core.bankCard.citylist.main.CityList"));
                intentCityList.setAction(Intent.ACTION_VIEW);
                startActivityForResult(intentCityList, 0);
                break;

            default:
                break;
        }
    }

    /**
     * 获取用户城市是否为空
     *
     * @throws Exception
     */
    protected void sendInsertCity() throws Exception {
        //转JSON
        JSONObject obj = new JSONObject();
        String uid = PreferencesUtils.getValueFromSPMap(this, PreferencesUtils.Keys.UID);
        obj.put(PreferencesUtils.Keys.UID, uid);
        obj.put("city", cityName);
        RequestParams params = new RequestParams();
        String desStr = MyDES.encrypt(obj.toString());
        params.put("cipher", desStr);
        MyhttpClient.desPost(UrlConfig.INSERT_CITY, params, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                super.onStart();
                showWaittingDialog(CityAddActivity.this);
            }

            @Override
            public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
                super.onFailure(arg0, arg1, arg2, arg3);
                dismissWaittingDialog();
                ToastUtil.show(CityAddActivity.this, R.string.default_error);
            }

            @Override
            public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
                super.onSuccess(arg0, arg1, arg2);
                dismissWaittingDialog();
                try {
                    String response = new String(arg2, "UTF-8");
                    response = CommonUtil.transResponse(response);
                    if (response == null || response.equals("")) {
                        return;
                    }
                    JSONObject jsonObject = new JSONObject(response); // 解析
                    String cipher = jsonObject.getString("cipher");
                    String desStr = MyDES.decrypt(cipher); // 解密
                    if (TextUtils.isEmpty(desStr)) return;
                    JSONObject obj = new JSONObject(desStr);
                    if (obj.getInt("code") == 1000) {
                        ToastUtil.show(CityAddActivity.this, "添加成功");
                        CityAddActivity.this.setResult(IntentCode.CITY_ADD);
                        finish();
                    } else {
                        ToastUtil.show(CityAddActivity.this, obj.getString("msg"));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
