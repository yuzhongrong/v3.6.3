package com.jinr.core.security.address;

import android.content.Intent;
import android.os.Bundle;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jinr.core.R;
import com.jinr.core.base.BaseActivity;
import com.jinr.core.config.UrlConfig;
import com.jinr.core.utils.CommonUtil;
import com.jinr.core.utils.DensityUtil;
import com.jinr.core.utils.MyDES;
import com.jinr.core.utils.MyLog;
import com.jinr.core.utils.MyhttpClient;
import com.jinr.core.utils.PreferencesUtils;
import com.jinr.core.utils.ToastUtil;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.tencent.mm.sdk.platformtools.Log;

import org.apache.http.Header;
import org.json.JSONObject;

import model.AddrBean;
import model.AddressList;


public class EditAddressActivity extends BaseActivity implements OnClickListener, AddressDialogFragment.OnAreaChoosedListener, NoticeDialogFragment.OnNoticeDialogChooseListener {

    private ImageView title_left_img; // title左边图片
    private TextView title_center_text; // title标题
    private TextView tv_save;
    private RelativeLayout rl_area;
    private TextView tv_area;
    private EditText et_name;
    private EditText et_phone;
    private EditText et_address;
    private AddressDialogFragment mDialog;
    private DBManager mDbManager;
    private RelativeLayout rl_default;
    private ImageView image_default;
    private AddrBean addrBean;
    private String consignee_name;
    private String consignee_tel;
    private String detailed_address;
    private boolean isDfAddress = false;
    private int isDefault = 1;
    private AddressList addressData;
    private String addr_id = "";
    private NoticeDialogFragment dialog;
    private boolean mFlag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);
        findViewById();
        initUI();
        setListener();
        initData();
    }

    @Override
    protected void initData() {
        mDbManager = new DBManager(this);
        mDbManager.openDateBase();
        mDbManager.closeDatabase();
        Intent intent = getIntent();
        // 此处的flag用来判断是否是编辑地址还是新增地址 @author Ysw created at 2016/12/21 10:47
        mFlag = intent.getBooleanExtra("flag", false);
        if (mFlag) {
            addressData = (AddressList) intent.getSerializableExtra("data");
            if (addressData != null) {
                et_name.setText(addressData.getConsignee_name());
                et_phone.setText(addressData.getConsignee_tel());
                tv_area.setText(addressData.getRegion_name());
                et_address.setText(addressData.getDetailed_address());
                addr_id = addressData.getAddressid() + "";
                isDefault = addressData.getIs_default();
                if (2 == addressData.getIs_default()) {
                    image_default.setImageDrawable(getResources().getDrawable(R.drawable.selected));
                    rl_default.setVisibility(View.GONE);
                } else {
                    image_default.setImageDrawable(getResources().getDrawable(R.drawable.choice));
                    rl_default.setVisibility(View.VISIBLE);
                }
            }
            setCursorLast(et_name);
        }

    }


    @Override
    protected void findViewById() {
        title_left_img = (ImageView) findViewById(R.id.title_left_img);
        title_center_text = (TextView) findViewById(R.id.title_center_text);
        tv_save = (TextView) findViewById(R.id.tv_save);
        rl_area = (RelativeLayout) findViewById(R.id.rl_area);
        tv_area = (TextView) findViewById(R.id.tv_area);
        et_name = (EditText) findViewById(R.id.et_name);
        et_phone = (EditText) findViewById(R.id.et_phone);
        et_address = (EditText) findViewById(R.id.et_address);
        rl_default = (RelativeLayout) findViewById(R.id.rl_default);
        image_default = (ImageView) findViewById(R.id.image_default);

    }

    @Override
    protected void initUI() {
        title_center_text.setText("收货地址");
        tv_save.setVisibility(View.VISIBLE);
        dialog = NoticeDialogFragment.getInstance("修改了信息还未保存，确认现在返回吗？");
        dialog.setOnNoticeDialogChooseListener(this);
    }

    @Override
    protected void setListener() {
        title_left_img.setOnClickListener(this);
        rl_area.setOnClickListener(this);
        tv_save.setOnClickListener(this);
        rl_default.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_left_img:
                dialog.show(getSupportFragmentManager(), "");
                break;
            case R.id.rl_area:
                mDialog = new AddressDialogFragment();
                mDialog.setOnAreaChoosedListener(EditAddressActivity.this);
                mDialog.show(getSupportFragmentManager(), null);
                break;
            case R.id.tv_save:
                saveAddress();
                break;
            case R.id.rl_default:
                setDefault();
                break;
            default:
                break;
        }
    }

    // 保存用户地址，需要请求接口 @author Ysw created at 2016/12/19 18:53
    private void saveAddress() {
        consignee_name = et_name.getText().toString().trim();
        consignee_tel = et_phone.getText().toString().trim();
        detailed_address = et_address.getText().toString().trim();

        if (TextUtils.isEmpty(consignee_name)) {
            ToastUtil.show(EditAddressActivity.this, "请输入收货人姓名");
            return;
        } else if (!(consignee_name.length() >= 2 && consignee_name.length() <= 15)) {
            ToastUtil.show(EditAddressActivity.this, "请输入正确的收货人姓名");
            return;
        } else if (!DensityUtil.isMobileNO(consignee_tel)) {
            ToastUtil.show(EditAddressActivity.this, "请输入正确的手机号码");
            return;
        } else if (TextUtils.isEmpty(tv_area.getText().toString().trim())) {
            ToastUtil.show(EditAddressActivity.this, "请选择正确的地区");
            return;
        } else if (TextUtils.isEmpty(detailed_address)) {
            ToastUtil.show(EditAddressActivity.this, "请输入详细的地址");
            return;
        }

        try {
            saveAddressToService();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 向服务保存新地址 @author Ysw created at 2016/12/19 23:19
    private void saveAddressToService() throws Exception {
        RequestParams params = new RequestParams();
        String uid = PreferencesUtils.getValueFromSPMap(EditAddressActivity.this, PreferencesUtils.Keys.UID);
        JSONObject cipher = new JSONObject();
        cipher.put(PreferencesUtils.Keys.UID, uid);
        // 保存地址的时候从数据库中获取地区编码或者从上个界面传递的实体中获取 @author Ysw created at 2016/12/21 11:01
        if (addrBean != null) {
            cipher.put("address_code", addrBean.address_code);
            cipher.put("region_name", addrBean.region_name);
            cipher.put("province", addrBean.province);
            cipher.put("city", addrBean.city);
            cipher.put("area", addrBean.area);
            cipher.put("county", addrBean.county);
        } else {
            cipher.put("address_code", addressData.getAddress_code());
            cipher.put("region_name", addressData.getRegion_name());
            cipher.put("province", addressData.getProvince());
            cipher.put("city", addressData.getCity());
            cipher.put("area", addressData.getArea());
            cipher.put("county", addressData.getCounty());
        }
        cipher.put("id", addr_id);
        cipher.put("detailed_address", detailed_address);
        cipher.put("consignee_name", consignee_name);
        cipher.put("consignee_tel", consignee_tel);
        cipher.put("default", isDefault);

        params.put("cipher", MyDES.encrypt(cipher.toString()));
        MyhttpClient.desPost(UrlConfig.CREATE_ADDRESS, params,
                new AsyncHttpResponseHandler() {

                    @Override
                    public void onStart() {
                        super.onStart();
                        showWaittingDialog(EditAddressActivity.this);
                    }

                    @Override
                    public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
                        super.onFailure(arg0, arg1, arg2, arg3);
                        ToastUtil.show(EditAddressActivity.this, R.string.default_error);
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
                            Log.e("=====", cipher);
                            int code = object.getInt("code");
                            if (code == 1000) {
                                finish();
                            } else {
                                ToastUtil.show(EditAddressActivity.this, object.getString("msg"));
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

    // 设置默认地址时控制图标显示 @author Ysw created at 2016/12/19 18:53
    private void setDefault() {
        isDfAddress = !isDfAddress;
        if (isDfAddress) {
            isDefault = 2;
            image_default.setImageDrawable(getResources().getDrawable(R.drawable.selected));
        } else {
            isDefault = 1;
            image_default.setImageDrawable(getResources().getDrawable(R.drawable.choice));
        }
    }

    // 地区选择完毕的回调，需要回调的参数有挺多的，省、市、县、镇等等的Id之类的参数，需要参考Rap文档 @author Ysw created at 2016/12/20 0:16 
    @Override
    public void OnChoosedListener(AddrBean addrBean) {
        this.addrBean = addrBean;
        tv_area.setText(addrBean.region_name);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && mFlag) {
            dialog.show(getSupportFragmentManager(), "");
            return false;
        } else if(keyCode == KeyEvent.KEYCODE_DEL ){
            return true;
        }else {
            finish();
            return false;
        }
    }

    @Override
    public void onChooseClick(boolean isTrue) {
        if (isTrue) {
            finish();
        }
    }
    //光标移到最后
    private void setCursorLast(EditText editText){
        CharSequence text = editText.getText();
        if (text instanceof Spannable) {
            Spannable spanText = (Spannable) text;
            Selection.setSelection(spanText, text.length());
        }
    }
}
