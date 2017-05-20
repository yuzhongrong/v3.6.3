package com.jinr.core.bankCard;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.jinr.core.R;
import com.jinr.core.base.BaseActivity;
import com.jinr.core.config.EventBusKey;
import com.jinr.core.config.UrlConfig;
import com.jinr.core.security.tradepwd.SetTradePwdActivity;
import com.jinr.core.utils.CommonUtil;
import com.jinr.core.utils.MyDES;
import com.jinr.core.utils.MyLog;
import com.jinr.core.utils.MyhttpClient;
import com.jinr.core.utils.PreferencesUtils;
import com.jinr.core.utils.ToastUtil;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.apache.http.Header;
import org.json.JSONObject;
import org.simple.eventbus.EventBus;

import java.io.UnsupportedEncodingException;

import model.BaseModel;
import model.DealPasswordMode;
import model.UserCardInfoMode;

public class ChooseCityActivity extends BaseActivity implements View.OnClickListener {

    private TextView title_center_text;
    private ImageView title_left_img;
    private ImageView image_icon;
    private TextView tv_bankname;
    private TextView tv_end;
    private TextView tv_city;
    private RelativeLayout rl_select;
    private Button bind_bank_submit;
    private String cityName;
    private String cityNum;
    private String Uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_city);
        initUI();
        initData();
    }

    @Override
    protected void initData() {
        try {
            getUserBandCardInfo();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void findViewById() {

    }

    @Override
    protected void setListener() {

    }

    @Override
    protected void initUI() {
        //EventBus.getDefault().register(this);
        Uid = PreferencesUtils.getValueFromSPMap(this, PreferencesUtils.Keys.UID);
        title_center_text = (TextView) findViewById(R.id.title_center_text);
        title_center_text.setText("选择开户城市");
        title_left_img = (ImageView) findViewById(R.id.title_left_img);
        title_left_img.setVisibility(View.INVISIBLE);
        title_left_img.setOnClickListener(this);
        image_icon = (ImageView) findViewById(R.id.image_icon);
        tv_bankname = (TextView) findViewById(R.id.tv_bankname);
        tv_end = (TextView) findViewById(R.id.tv_end);
        tv_city = (TextView) findViewById(R.id.tv_city);
        rl_select = (RelativeLayout) findViewById(R.id.rl_select);
        rl_select.setOnClickListener(this);
        bind_bank_submit = (Button) findViewById(R.id.bind_bank_submit);
        bind_bank_submit.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_left_img:

                break;
            case R.id.rl_select:
                Intent intentCityList = new Intent();
                intentCityList.setComponent(new ComponentName("com.jinr.core", "com.jinr.core.bankCard.citylist.main.CityList"));
                intentCityList.setAction(Intent.ACTION_VIEW);
                startActivityForResult(intentCityList, 0);
                break;
            case R.id.bind_bank_submit:
                if (!TextUtils.isEmpty(tv_city.getText().toString().trim())) {
                    try {
                        sendInsertCity();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
            if (data == null) return;
            if (data.getStringExtra("cityName") != null && !data.getStringExtra("cityName").equals("")) {
                cityName = data.getStringExtra("cityName");
                cityNum = data.getStringExtra("cityNum");
                tv_city.setText(cityName);
                bind_bank_submit.setBackgroundResource(R.drawable.btn_blue_bg);
                bind_bank_submit.setClickable(true);
            }
        }
    }

    /**
     * 手动添加用户银行卡开户城市 @author Ysw created at 2017/1/9 16:39
     */
    protected void sendInsertCity() throws Exception {
        JSONObject obj = new JSONObject();
        RequestParams params = new RequestParams();
        String uid = PreferencesUtils.getValueFromSPMap(this, PreferencesUtils.Keys.UID);
        obj.put(PreferencesUtils.Keys.UID, uid);
        obj.put("city", cityName);
        String desStr = MyDES.encrypt(obj.toString());
        params.put("cipher", desStr);
        MyhttpClient.desPost(UrlConfig.INSERT_CITY, params, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                super.onStart();
                showWaittingDialog(ChooseCityActivity.this);
            }

            @Override
            public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
                super.onFailure(arg0, arg1, arg2, arg3);
                dismissWaittingDialog();
                ToastUtil.show(ChooseCityActivity.this, R.string.default_error);
            }

            @Override
            public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
                super.onSuccess(arg0, arg1, arg2);
                dismissWaittingDialog();
                try {
                    String response = new String(arg2, "UTF-8");
                    response = CommonUtil.transResponse(response);
                    if (response == null || response.equals("")) return;
                    JSONObject jsonObject = new JSONObject(response);
                    String cipher = jsonObject.getString("cipher");
                    String desStr = MyDES.decrypt(cipher);
                    if (TextUtils.isEmpty(desStr)) return;
                    JSONObject obj = new JSONObject(desStr);
                    if (obj.getInt("code") == 1000) {
                        ToastUtil.show(ChooseCityActivity.this, "添加成功");
                        EventBus.getDefault().post(true, EventBusKey.ADD_CITY_SUCCESD);
                        // 判断用户是否设置交易密码 @author Ysw created at 2017/1/11 18:03
                        getDealPasswordNetwork();
                    } else {
                        finish();
                        ToastUtil.show(ChooseCityActivity.this, obj.getString("msg"));
                    }
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (JsonSyntaxException e) {
                    MyLog.i("json解析错误", "解析错误");
                } catch (Exception ignored) {
                }
            }
        });
    }

    /**
     * 获取用户绑卡的信息 @author Ysw created at 2017/1/9 16:41
     */
    public void getUserBandCardInfo() throws Exception {
        JSONObject obj = new JSONObject();
        RequestParams params = new RequestParams();
        String uid = PreferencesUtils.getValueFromSPMap(this, PreferencesUtils.Keys.UID);
        obj.put(PreferencesUtils.Keys.UID, uid);
        String desStr = MyDES.encrypt(obj.toString());
        params.put("cipher", desStr);
        MyhttpClient.desPost(UrlConfig.USER_BANKINFO, params, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                super.onStart();
                showWaittingDialog(ChooseCityActivity.this);
            }

            @Override
            public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
                super.onFailure(arg0, arg1, arg2, arg3);
                dismissWaittingDialog();
                ToastUtil.show(ChooseCityActivity.this, R.string.default_error);
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
                    BaseModel<UserCardInfoMode> result = new Gson().fromJson(cipher, new TypeToken<BaseModel<UserCardInfoMode>>() {
                    }.getType());
                    if (result.isSuccess()) {
                        UserCardInfoMode data = result.getData();
                        ImageLoader.getInstance().displayImage(data.getImg(), image_icon);
                        tv_bankname.setText(data.getBank_name());
                        tv_end.setText("(尾号" + data.getCard_last() + ")");
                    } else {
                        ToastUtil.show(getApplication(), result.getMsg());
                    }
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (JsonSyntaxException e) {
                    MyLog.i("json解析错误", "解析错误");
                } catch (Exception ignored) {
                }
            }
        });
    }


    /**
     * 查看用户是否设置交易密码 @author Ysw created at 2017/1/11 17:44
     */

    private void getDealPasswordNetwork() throws Exception {
        RequestParams params = new RequestParams();
        JSONObject object = new JSONObject();
        object.put("uid", Uid);
        String desStr = MyDES.encrypt(object.toString());
        params.put("cipher", desStr);
        MyhttpClient.desPost(UrlConfig.DEAL_PASSWORD, params, new AsyncHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
            }

            @Override
            public void onFinish() {
                super.onFinish();
            }

            @Override
            public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
                super.onFailure(arg0, arg1, arg2, arg3);
                finish();
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
                    BaseModel<DealPasswordMode> result = new Gson().fromJson(cipher, new TypeToken<BaseModel<DealPasswordMode>>() {
                    }.getType());
                    if (result.isSuccess()) {
                        String state = result.getData().getState_tradePassword();
                        if (state != null && state.equals("0")) {
                            Intent intent = new Intent();
                            intent.setClass(ChooseCityActivity.this, SetTradePwdActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            finish();
                        }
                    } else {
                        finish();
                    }
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (JsonSyntaxException e) {
                    MyLog.i("json解析错误", "解析错误");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
