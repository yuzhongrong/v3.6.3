package com.jinr.core.security.address;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jinr.core.R;
import com.jinr.core.base.BaseActivity;
import com.jinr.core.config.UrlConfig;
import com.jinr.core.utils.CommonUtil;
import com.jinr.core.utils.MyDES;
import com.jinr.core.utils.MyhttpClient;
import com.jinr.core.utils.PreferencesUtils;
import com.jinr.core.utils.ToastUtil;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.tencent.mm.sdk.platformtools.Log;

import org.apache.http.Header;
import org.json.JSONObject;

import java.util.ArrayList;

import model.AddressList;
import model.AddressListModel;
import model.BaseModel;

public class AddressListActivity extends BaseActivity implements View.OnClickListener, AddressListAdapter.OnEditAddressListener,
        AddressListAdapter.OnDeleteAddressListener, AddressListAdapter.OnSetDefaultAddressListener, NoticeDialogFragment.OnNoticeDialogChooseListener {
    private ImageView title_left_img; // title左边图片
    private TextView title_center_text; // title标题
    private RelativeLayout rl_add;
    private ArrayList<AddressList> mAddressList;
    private AddressListAdapter mAdapter;
    private boolean isOvre;
    private ListView mListView;
    private String uid;
    private int mDeletePosition;
    private TextView tv_no_address;
    private ImageView image_no_address;
    private NoticeDialogFragment dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_list);
        findViewById();
        initUI();
        setListener();
        initData();
    }

    @Override
    protected void initData() {
        uid = PreferencesUtils.getValueFromSPMap(AddressListActivity.this, PreferencesUtils.Keys.UID);
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            getAddressListData();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void findViewById() {
        title_left_img = (ImageView) findViewById(R.id.title_left_img);
        title_center_text = (TextView) findViewById(R.id.title_center_text);
        mListView = (ListView) findViewById(R.id.mListView);
        tv_no_address = (TextView) findViewById(R.id.tv_no_address);
        image_no_address = (ImageView) findViewById(R.id.image_no_address);
        rl_add = (RelativeLayout) findViewById(R.id.rl_add);
    }

    @Override
    protected void initUI() {
        mAdapter = new AddressListAdapter(this);
        title_center_text.setText("收货地址");
        mListView.setAdapter(mAdapter);
        dialog = NoticeDialogFragment.getInstance("删除地址后，将影响您收取奖品的时间。");
        dialog.setOnNoticeDialogChooseListener(this);
    }

    @Override
    protected void setListener() {
        title_left_img.setOnClickListener(this);
        rl_add.setOnClickListener(this);
        mAdapter.setOnEditAddressListener(AddressListActivity.this);
        mAdapter.setOnDeleteAddressListener(AddressListActivity.this);
        mAdapter.onSetDefaultAddrListener(AddressListActivity.this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_left_img:
                finish();
                break;
            case R.id.rl_add:
                if (isOvre) {
                    Intent intent = new Intent();
                    // 此处的flag用来判断是新增地址还是编辑地址 @author Ysw created at 2016/12/20 0:50
                    intent.putExtra("flag", false);
                    intent.setClass(AddressListActivity.this, EditAddressActivity.class);
                    startActivity(intent);
                }
                break;
            default:
                break;
        }
    }

    public void getAddressListData() throws Exception {
        RequestParams params = new RequestParams();
        JSONObject cipher = new JSONObject();
        cipher.put(PreferencesUtils.Keys.UID, uid);
        params.put("cipher", MyDES.encrypt(cipher.toString()));
        MyhttpClient.desPost(UrlConfig.USER_ADDRESSINFO_LIST, params, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                super.onStart();
                showWaittingDialog(AddressListActivity.this);
            }

            @Override
            public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
                super.onFailure(arg0, arg1, arg2, arg3);
                ToastUtil.show(AddressListActivity.this, R.string.default_error);
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
                    Log.e("Ysw", "cipher = " + cipher);
                    BaseModel<AddressListModel> result = new Gson().fromJson(cipher, new TypeToken<BaseModel<AddressListModel>>() {
                    }.getType());
                    if (result.isSuccess()) {
                        AddressListModel data = result.getData();
                        String null_value = data.getNull_value();
                        if (null != null_value) {
                            mListView.setVisibility(View.GONE);
                            tv_no_address.setVisibility(View.VISIBLE);
                            image_no_address.setVisibility(View.VISIBLE);
                        } else {
                            mAddressList = data.getList();
                            mListView.setVisibility(View.VISIBLE);
                            tv_no_address.setVisibility(View.GONE);
                            image_no_address.setVisibility(View.GONE);
                            mAdapter.setParent(mAddressList);
                            mAdapter.notifyDataSetChanged();
                            if (mAddressList.size() == 5) {
                                rl_add.setVisibility(View.GONE);
                            } else {
                                rl_add.setVisibility(View.VISIBLE);
                            }
                        }
                    } else {
                        ToastUtil.show(AddressListActivity.this, R.string.default_error);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFinish() {
                super.onFinish();
                isOvre = true;
                dismissWaittingDialog();
            }
        });
    }

    /**
     * 删除收货地址
     *
     * @throws Exception
     */
    public void deleteAddress(final int position) throws Exception {
        AddressList addrModel = mAddressList.get(position);
        RequestParams params = new RequestParams();
        JSONObject cipher = new JSONObject();
        cipher.put(PreferencesUtils.Keys.UID, uid);
        cipher.put("default", addrModel.getIs_default());
        cipher.put("id", addrModel.getAddressid());
        params.put("cipher", MyDES.encrypt(cipher.toString()));
        MyhttpClient.desPost(UrlConfig.DELETE_ADDRESS, params, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                super.onStart();
                showWaittingDialog(AddressListActivity.this);
            }

            @Override
            public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
                super.onFailure(arg0, arg1, arg2, arg3);
                ToastUtil.show(AddressListActivity.this, R.string.default_error);
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
                        getAddressListData();
                    } else {
                        ToastUtil.show(AddressListActivity.this, object.getString("msg"));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFinish() {
                super.onFinish();
                isOvre = true;
                dismissWaittingDialog();
            }
        });
    }

    /**
     * 设置默认收货地址
     *
     * @throws Exception
     */
    public void setDefaultAddress(final int position) throws Exception {
        final AddressList addrModel = mAddressList.get(position);
        RequestParams params = new RequestParams();
        JSONObject cipher = new JSONObject();
        cipher.put(PreferencesUtils.Keys.UID, uid);
        cipher.put("id", addrModel.getAddressid());
        params.put("cipher", MyDES.encrypt(cipher.toString()));
        MyhttpClient.desPost(UrlConfig.ISDEFAULT_ADDRESS, params, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                super.onStart();
                showWaittingDialog(AddressListActivity.this);
            }

            @Override
            public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
                super.onFailure(arg0, arg1, arg2, arg3);
                ToastUtil.show(AddressListActivity.this, R.string.default_error);
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
                        for (AddressList data : mAddressList) {
                            if (data.getIs_default() == 2) {
                                data.setIs_default(1);
                            }
                        }
                        addrModel.setIs_default(2);
                        mAdapter.notifyDataSetChanged();
                    } else {
                        ToastUtil.show(AddressListActivity.this, object.getString("msg"));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFinish() {
                super.onFinish();
                isOvre = true;
                dismissWaittingDialog();
            }
        });
    }

    // 点击Item中编辑按钮的回调 @author Ysw created at 2016/12/20 0:58
    @Override
    public void EditAddressListener(int position) {
        AddressList addressData = mAddressList.get(position);
        Intent intent = new Intent();
        intent.putExtra("flag", true);
        intent.putExtra("data", addressData);
        intent.setClass(AddressListActivity.this, EditAddressActivity.class);
        startActivity(intent);
    }

    // 点击Item中删除按钮的回调 @author Ysw created at 2016/12/20 0:58
    @Override
    public void DeleteAddressListener(int position) {
        this.mDeletePosition = position;
        dialog.show(getSupportFragmentManager(), "");
    }

    @Override
    public void onSetDefaultAddr(int position) {
        try {
            setDefaultAddress(position);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onChooseClick(boolean isTrue) {
        if (isTrue) {
            try {
                deleteAddress(mDeletePosition);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
