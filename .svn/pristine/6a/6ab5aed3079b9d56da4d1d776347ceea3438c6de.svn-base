package com.jinr.core.dayup;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.webkit.SslErrorHandler;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jinr.core.MainActivity;
import com.jinr.core.R;
import com.jinr.core.base.BaseActivity;
import com.jinr.core.config.UrlConfig;
import com.jinr.core.more.CommonWebActivity;
import com.jinr.core.security.tradepwd.FindTradePwd;
import com.jinr.core.strongbox.StrongBoxPactActivity;
import com.jinr.core.trade.purchase.RegularPurchaseFirstActivity;
import com.jinr.core.transfer.TransferActivity;
import com.jinr.core.utils.CommonUtil;
import com.jinr.core.utils.DensityUtil;
import com.jinr.core.utils.KeyboardUtil;
import com.jinr.core.utils.LoadingDialog;
import com.jinr.core.utils.MyDES;
import com.jinr.core.utils.MyLog;
import com.jinr.core.utils.MyhttpClient;
import com.jinr.core.utils.PreferencesUtils;
import com.jinr.core.utils.ToastUtil;
import com.jinr.graph_view.ProgressWebView;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONObject;

import model.ProductCommonModel;
import model.RegularRecord;

/**
 * 我的日增息
 *
 * @author 1154
 */
@SuppressLint("HandlerLeak")
public class MyDayUpActivity extends BaseActivity implements OnClickListener {
    private ImageView title_left_img; // title左边
    private TextView title_center_text; // title标题
    private TextView title_right_tv;//转让说明
    private RelativeLayout rl_transfer;
    private LinearLayout layout_bottom;
    private String assetid;
    private RelativeLayout rl_turnin;
    private TextView tv_transfer;
    private EditText pw;
    private View view;
    private TextView forgot_passwordTextView;
    private ProgressWebView mWebView;
    final static int MSG_KEYBOARD_SHOW = 3;
    final static int MSG_KEYBOARD_HIDE = 4;
    final static int REGULAR_MSG_KEYBOARD_VALIDATE_ERROR_SHOW = 5;
    private LinearLayout keyboard_parts;
    private RelativeLayout loading_page;
    private ProgressBar loadingBar;
    private ImageView close_keyboard;
    private PopupWindow window;
    @SuppressWarnings("unused")
    private KeyboardUtil keyboardUtil;
    private String uid;
    private String code;
    private String name;
    private LoadingDialog loadingdialog;
    private String status;
    private AlertDialog mAlertDialog;
    private RegularRecord mRegular;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_KEYBOARD_HIDE:// 隐藏键盘，开始验证密码
                    close_keyboard.setClickable(false);
                    keyboard_parts.setVisibility(View.INVISIBLE);
                    loading_page.setVisibility(View.VISIBLE);
                    loadingBar.setVisibility(View.VISIBLE);
                    loadingBar.requestLayout();
                    break;
                case MSG_KEYBOARD_SHOW:
                    handler.removeMessages(MSG_KEYBOARD_HIDE);
                    if (window != null && window.isShowing()) {
                        window.dismiss();
                    }
                    break;
                case REGULAR_MSG_KEYBOARD_VALIDATE_ERROR_SHOW:
                    close_keyboard.setClickable(true);
                    keyboard_parts.setVisibility(View.VISIBLE);
                    loading_page.setVisibility(View.INVISIBLE);
                    loadingBar.setVisibility(View.INVISIBLE);
                    break;
                default:
                    break;
            }
        }
    };
    private TextView tv_turnin;
    private String mProduct_num;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mydayup);
        loadingdialog = new LoadingDialog(this);
        assetid = getIntent().getStringExtra("assetid");
        name = getIntent().getStringExtra("name");
        status = getIntent().getStringExtra("status");
        mProduct_num = getIntent().getStringExtra("product_num");
        mRegular = (RegularRecord) getIntent().getExtras().get("regular");
        code = getIntent().getStringExtra("code");
        findViewById();
        initUI();
        setListener();
        registerReceiver();
        initData();
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        switch (v.getId()) {
            case R.id.title_left_img:
                finish();
                break;
            case R.id.rl_transfer:
                if (CommonUtil.isFastDoubleClick()) {
                    return;
                } else {
                    if (!TextUtils.isEmpty(mRegular.getTransferStatus())) {
                        switch (Integer.parseInt(mRegular.getTransferStatus())) {
                            case 1://转让跳转
                                Intent pIntent = new Intent(this, TransferActivity.class);
                                pIntent.putExtra("regular", mRegular);
                                startActivity(pIntent);
                                break;
                            case 2://撤销转让的接口
                                try {
                                    sendCancelCount();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                break;
                            default:
                                break;
                        }
                    }
                }
                break;
            case R.id.rl_turnin:
                if (CommonUtil.isFastDoubleClick()) {
                    return;
                } else {
                    ProductCommonModel productCommonModel = new ProductCommonModel();
                    productCommonModel.setCode(code);
                    productCommonModel.setAssetid(assetid);
                    productCommonModel.setName(name);
                    productCommonModel.setStatus(status);
                    intent.putExtra("regular", productCommonModel);
                    intent.setClass(MyDayUpActivity.this, RegularPurchaseFirstActivity.class);
                    startActivity(intent);
                    finish();
                }
                break;
            case R.id.cancel_transfer:
                mAlertDialog.dismiss();
                break;
            case R.id.confirm_transfer:
                if (CommonUtil.isFastDoubleClick()) {
                    return;
                } else {
                    mAlertDialog.dismiss();
                    showPopwindow();
                }
                break;
            case R.id.tv_protocol:
                Intent intent2 = new Intent(this, CommonWebActivity.class);
                intent2.putExtra("titleName", "转让说明");
                String url = UrlConfig.IP_V + UrlConfig.DAILYGAIN_TRANSINFO;
                intent2.putExtra("url", url);
                startActivity(intent2);
                break;
            default:
                break;
        }
    }

    /**
     * 获取撤销转让次数 @author Ysw created at 2017/3/14 15:56
     */
    private void sendCancelCount() throws Exception {
        showWaittingDialog(MyDayUpActivity.this);
        JSONObject obj = new JSONObject();
        obj.put("uid", uid);
        RequestParams params = new RequestParams();
        String desStr = MyDES.encrypt(obj.toString());
        params.put("cipher", desStr);
        MyhttpClient.desPost(UrlConfig.TRANSFER_REVOKE_COUNT, params, new AsyncHttpResponseHandler() {

            @Override
            public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
                super.onFailure(arg0, arg1, arg2, arg3);
                dismissWaittingDialog();
                ToastUtil.show(MyDayUpActivity.this, R.string.default_error);
            }

            @Override
            public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
                super.onSuccess(arg0, arg1, arg2);
                dismissWaittingDialog();
                try {
                    String response = new String(arg2, "UTF-8");
                    response = CommonUtil.transResponse(response);
                    if (response == null || response.equals(""))
                        return;
                    JSONObject jsonObject = new JSONObject(response);
                    String cipher = jsonObject.getString("cipher");
                    String desStr = MyDES.decrypt(cipher);
                    if (TextUtils.isEmpty(desStr))
                        return;
                    JSONObject obj = new JSONObject(desStr);
                    if (obj.getInt("code") == 1000) {
                        JSONObject data = obj.optJSONObject("data");
                        int times = data.optInt("times");
                        if (times <= 0) {
                            ToastUtil.show(MyDayUpActivity.this, "今日撤销转让机会已用完");
                        } else {
                            showMyDialog(times);
                        }
                    } else {
                        ToastUtil.show(MyDayUpActivity.this, obj.getString("msg"));
                    }
                    Message message = new Message();
                    message.what = MSG_KEYBOARD_SHOW;
                    handler.sendMessage(message);
                } catch (Exception e) {
                }
            }
        });
    }

    private void sendCancelTransfer(String pwd) throws Exception {
        JSONObject obj = new JSONObject();
        obj.put("uid", uid);
        obj.put("buss_pwd", pwd);
        obj.put("transfer_order_id", mRegular.getTransferOrderId());
        RequestParams params = new RequestParams();
        String desStr = MyDES.encrypt(obj.toString());
        params.put("cipher", desStr);
        MyhttpClient.desPost(UrlConfig.TRANSFER_REVOKE, params, new AsyncHttpResponseHandler() {

            @Override
            public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
                close_keyboard.setClickable(true);
                super.onFailure(arg0, arg1, arg2, arg3);
                ToastUtil.show(MyDayUpActivity.this, R.string.default_error);
            }

            @Override
            public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
                super.onSuccess(arg0, arg1, arg2);
                dismissWaittingDialog();
                try {
                    String response = new String(arg2, "UTF-8");
                    response = CommonUtil.transResponse(response);
                    if (response == null || response.equals(""))
                        return;
                    JSONObject jsonObject = new JSONObject(response);
                    String cipher = jsonObject.getString("cipher");
                    String desStr = MyDES.decrypt(cipher);
                    if (TextUtils.isEmpty(desStr))
                        return;
                    JSONObject obj = new JSONObject(desStr);
                    if (obj.getInt("code") == 1000) {
                        Message message = new Message();
                        message.what = MSG_KEYBOARD_SHOW;
                        handler.sendMessage(message);
                        sendBroadcast(new Intent().setAction("action.refresh_actdetail"));
                    } else if (obj.getInt("code") == 3002) {
                        pw.setText("");
                        Message msg = new Message();
                        msg.what = REGULAR_MSG_KEYBOARD_VALIDATE_ERROR_SHOW;
                        handler.sendMessage(msg);
                        ToastUtil.show(getApplication(), obj.getString("msg"));
                    } else {
                        Message message = new Message();
                        message.what = MSG_KEYBOARD_SHOW;
                        handler.sendMessage(message);
                        ToastUtil.show(MyDayUpActivity.this, obj.getString("msg"));
                    }
                } catch (Exception e) {
                }
            }
        });
    }

    private void showMyDialog(int times) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        mAlertDialog = builder.create();
        mAlertDialog.show();
        mAlertDialog.setCanceledOnTouchOutside(false);
        mAlertDialog.getWindow().setContentView(R.layout.transfer_dialog);
        mAlertDialog.getWindow().setLayout(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        mAlertDialog.getWindow().findViewById(R.id.cancel_transfer).setOnClickListener(this);
        mAlertDialog.getWindow().findViewById(R.id.confirm_transfer).setOnClickListener(this);
        ((TextView) mAlertDialog.getWindow().findViewById(R.id.times)).setText("您今日还有" + times + "次撤销转让机会");
    }

    @Override
    protected void initData() {
        uid = PreferencesUtils.getValueFromSPMap(this, PreferencesUtils.Keys.UID);
        if (!TextUtils.isEmpty(mRegular.getUrl()))
            initWeb(mRegular.getUrl());
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void findViewById() {
        title_right_tv = (TextView) findViewById(R.id.tv_protocol);
        title_center_text = (TextView) findViewById(R.id.title_center_text);
        title_left_img = (ImageView) findViewById(R.id.title_left_img);
        rl_transfer = (RelativeLayout) findViewById(R.id.rl_transfer);
        rl_turnin = (RelativeLayout) findViewById(R.id.rl_turnin);
        tv_turnin = (TextView) findViewById(R.id.turn_in_tv);
        tv_transfer = (TextView) findViewById(R.id.tv_transfer);
        mWebView = (ProgressWebView) findViewById(R.id.mWebview);
        layout_bottom = (LinearLayout) findViewById(R.id.rl_bottom);
        rl_transfer.setOnClickListener(this);
        rl_turnin.setOnClickListener(this);
    }

    @Override
    protected void initUI() {
        title_center_text.setText("我的" + name);
        /**
         * 目前有日增息和保险箱可以转让需要根据转让状态来判断按钮的状态，定期没有转让功能不需要判断 @author Ysw created at 2017/2/7 10:49
         */
        if (name.equals("日增息") || name.equals("保险箱")) {
            if (!TextUtils.isEmpty(mRegular.getTransferStatus())) {
                MyLog.e(TAG, "initUI: " + mRegular.toString());
                title_right_tv.setText("转让说明");
                title_right_tv.setVisibility(View.VISIBLE);
                layout_bottom.setVisibility(View.VISIBLE);
                switch (Integer.parseInt(mRegular.getTransferStatus())) {
                    case 1://可以转让
                        rl_transfer.setVisibility(View.VISIBLE);
                        tv_transfer.setText("转让");
                        rl_turnin.setVisibility(View.GONE);
                        ((TextView) findViewById(R.id.turn_in_tv)).setText("转入");
                        break;
                    case 2://撤销转让
                        rl_transfer.setVisibility(View.VISIBLE);
                        rl_turnin.setVisibility(View.GONE);
                        tv_transfer.setText("撤销转让");
                        break;
                    case 3://转让审核中
                        rl_transfer.setVisibility(View.VISIBLE);
                        rl_turnin.setVisibility(View.GONE);
                        tv_transfer.setText("交易审核中");
                        rl_transfer.setOnClickListener(null);
                        break;
                    case 4:// 转让完成,转让置灰不可点击 @author Ysw created at 2017/1/17 11:14
                        rl_transfer.setVisibility(View.VISIBLE);
                        rl_turnin.setVisibility(View.GONE);
                        tv_transfer.setText("转让");
                        tv_transfer.setTextColor(getResources().getColor(R.color.gray_txt_bg));
                        tv_transfer.setClickable(false);
                        rl_transfer.setOnClickListener(null);

                        break;
                    case 0://转让置灰
                        rl_turnin.setVisibility(View.GONE);
                        rl_transfer.setVisibility(View.VISIBLE);
                        tv_transfer.setText(mRegular.getRemain_tip());
                        tv_transfer.setTextColor(getResources().getColor(R.color.gray_txt_bg));
                        tv_transfer.setClickable(false);
                        break;
                    default:
                        break;
                }
            }
        } else {
            rl_transfer.setVisibility(View.GONE);
            rl_turnin.setVisibility(View.GONE);
            layout_bottom.setVisibility(View.GONE);
        }
    }

    @Override
    protected void setListener() {
        title_left_img.setOnClickListener(this);
        title_right_tv.setOnClickListener(this);
    }

    private BroadcastReceiver mRefreshBroadcastReceiver = new BroadcastReceiver() {
        // 刷新重新进入本页，如8.88活动页登陆成功和转入成功返回
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals("action.refresh_actdetail")) {
                finish();
            }
        }
    };

    // 注册广播
    private void registerReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("action.refresh_actdetail");
        registerReceiver(mRefreshBroadcastReceiver, intentFilter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mRefreshBroadcastReceiver);
    }

    /**
     * 新需求 自定义密码盘的popwindow
     */
    @SuppressLint("InflateParams")
    private void showPopwindow() {
        // 利用layoutInflater获得View
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.layout_paypassword, null);
        pw = (EditText) view.findViewById(R.id.eta_pwd);
        close_keyboard = (ImageView) view.findViewById(R.id.close_keyboard);//
        // 关闭按钮
        keyboard_parts = (LinearLayout) view.findViewById(R.id.keyboard_parts);//
        // 密码盘区域
        loading_page = (RelativeLayout) view.findViewById(R.id.loading_page);//
        // loading区域
        loadingBar = (ProgressBar) view.findViewById(R.id.progressbar);// 模糊进度条
        forgot_passwordTextView = (TextView) view.findViewById(R.id.forgot_password);
        window = new PopupWindow(view, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT, true);
        // 实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(0xb0000000);
        window.setBackgroundDrawable(dw);
        // 设置popWindow的显示和消失动画
        window.setAnimationStyle(R.style.dialogWindowAnim);
        // 在底部显示
        window.showAtLocation(view, Gravity.BOTTOM, 0, 0);
        pw.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                }
            }
        });
        /**
         * 忘记密码？
         */
        forgot_passwordTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_real_info = new Intent(MainActivity.instance, FindTradePwd.class);
                startActivity(intent_real_info);
            }
        });
        /**
         * 关闭输入密码
         */
        close_keyboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                window.dismiss();
            }
        });
        keyboardUtil = new KeyboardUtil(view, window, this, pw);
        pw.setFocusable(true);
        pw.setFocusableInTouchMode(true);
        pw.requestFocus();
        pw.setInputType(InputType.TYPE_NULL);
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = 0.7f;
        getWindow().setAttributes(lp);
        window.setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss() {
                WindowManager.LayoutParams lp = getWindow().getAttributes();
                lp.alpha = 1f;
                getWindow().setAttributes(lp);
            }
        });

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            /**
             * 输入密码6位之后handler发送关闭
             */
            @Override
            public void afterTextChanged(Editable s) {
                s = pw.getText();
                if (s.length() >= 6) {
                    String passwd = pw.getText().toString();
                    try {
                        sendCancelTransfer(passwd);
                        Message message = new Message();
                        message.what = MSG_KEYBOARD_HIDE;
                        handler.sendMessage(message);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        pw.addTextChangedListener(textWatcher);
    }

    // 加载web界面
    public void initWeb(final String mUrl) {
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setBuiltInZoomControls(true);
        mWebView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        if (Build.VERSION.SDK_INT >= 21) {
            mWebView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (!CommonUtil.isFastDoubleClick()) {
                    if (url.contains("reload_jinr966")) {
                        if (DensityUtil.isNetworkAvailable(MyDayUpActivity.this)) {
                            mWebView.loadUrl(mUrl);
                        } else {
                            ToastUtil.show(MyDayUpActivity.this, "网络异常,请检查网络");
                        }
                        return true;
                    } else if (url.contains("iosaction::Service:")) {
                        url = url.replace("iosaction::Service:", "");
                        Intent intent = new Intent();
                        intent.setClass(MyDayUpActivity.this, StrongBoxPactActivity.class);
                        intent.putExtra("url", url);
                        startActivity(intent);
                        return true;
                    }
                } else {
                    return true;
                }
                return super.shouldOverrideUrlLoading(view, url);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                mWebView.loadUrl("file:///android_asset/load_error.html");
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                handler.proceed();
            }
        });
        mWebView.loadUrl(mUrl);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!DensityUtil.isNetworkAvailable(MyDayUpActivity.this)) {
            layout_bottom.setVisibility(View.GONE);
        }
    }
}
