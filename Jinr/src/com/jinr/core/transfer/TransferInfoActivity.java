package com.jinr.core.transfer;

import android.app.ActionBar.LayoutParams;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jinr.core.JinrApp;
import com.jinr.core.MainActivity;
import com.jinr.core.R;
import com.jinr.core.bankCard.AddBankActivity;
import com.jinr.core.bankCard.SecondBandCardActivity;
import com.jinr.core.base.BaseActivity;
import com.jinr.core.config.Check;
import com.jinr.core.config.EventBusKey;
import com.jinr.core.config.UrlConfig;
import com.jinr.core.more.CommonWebActivity;
import com.jinr.core.regist.NewLoginActivity;
import com.jinr.core.security.tradepwd.FindTradePwd;
import com.jinr.core.security.tradepwd.SetTradePwdActivity;
import com.jinr.core.trade.PayListAdapter;
import com.jinr.core.trade.purchase.PurchaseLimitNoteActivity;
import com.jinr.core.trade.purchase.PurchaseResultActivity;
import com.jinr.core.ui.CustomDialog2;
import com.jinr.core.ui.CustomDialog2.OnDialogViewClickListener;
import com.jinr.core.ui.NewCustomDialogNoTitleFinish;
import com.jinr.core.utils.CommonUtil;
import com.jinr.core.utils.KeyboardUtil;
import com.jinr.core.utils.LoadingDialog;
import com.jinr.core.utils.MyDES;
import com.jinr.core.utils.MyLog;
import com.jinr.core.utils.MyhttpClient;
import com.jinr.core.utils.PreferencesUtils;
import com.jinr.core.utils.SendMobileCode;
import com.jinr.core.utils.SendMobileCode.Back;
import com.jinr.core.utils.ToastUtil;
import com.jinr.pulltorefresh.CustomJinrScrollView;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONObject;
import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.net.URLDecoder;
import java.text.DecimalFormat;
import java.util.ArrayList;

import model.BaseModel;
import model.PayInfo;
import model.PayInfoList;
import model.PaymentRegularResultObj;
import model.TransferInfoModel;
import model.UidObj;
import model.UserBindinfo;

public class TransferInfoActivity extends BaseActivity implements OnClickListener {
    public static final String TAG = "TransferInfoActivity";
    final static int REGULAR_MSG_KEYBOARD_HIDE = 2;
    final static int REGULAR_MSG_KEYBOARD_VALIDATE_SHOW = 3;
    final static int REGULAR_MSG_KEYBOARD_VALIDATE_HIDE = 4;
    final static int REGULAR_MSG_KEYBOARD_VALIDATE_ERROR_SHOW = 5;
    private ImageView title_left_img;
    private TextView title_center_text;
    private TextView title_right_tv;
    private String mTransferOrderId;
    private TransferInfoModel mTransferInfoModel;
    private RelativeLayout rl_protocol;
    private CustomJinrScrollView mRefreshableView;
    private Button send_code;
    private String mUid = "";
    private Double PURCHASE_MIN_MONEY = 100.00;
    private int code;
    private ArrayList<PayInfo> payInfoList = new ArrayList<>();
    private PayInfo payInfo;
    private ListView listView;
    private PayListAdapter adapter;
    private boolean isBalance = true;
    private PopupWindow window;
    private View view;
    private View cview;
    private PopupWindow popupWindow;
    private NewCustomDialogNoTitleFinish dialog_bind_card;
    private NewCustomDialogNoTitleFinish dialog_set_passwd;
    private LinearLayout keyboard_parts;
    private RelativeLayout loading_page;
    private ProgressBar loadingBar;
    private ImageView close_keyboard;
    private EditText pw;
    private KeyboardUtil keyboardUtil;
    private TextView forgot_passwordTextView;
    private Handler Pophandler;
    private String password;
    private String event_key = "";
    private PaymentRegularResultObj requesPay;
    private BaseModel<PaymentRegularResultObj> pModel;
    private CustomDialog2 customDialog;
    private TextView tv_name;
    private TextView tv_transferTime;
    private TextView tv_rate;
    private TextView tv_upRate;
    private TextView tv_laveDay;
    private TextView tv_number;
    private TextView tv_endDay;
    private TextView tv_value;
    private TextView tv_reduction;
    private TextView tv_laveIncome;
    private RelativeLayout rl_coupon;
    private TextView tv_actual;
    private TextView tv_offer;
    private RelativeLayout rl_turnin;
    private TextView tv_coupon;
    private String mCouponUrl;
    private String mCoupon_id = "0";
    private LoadingDialog mLoadingdialog;
    private RelativeLayout rl_value;
    private RelativeLayout rl_oldmoney;
    private RelativeLayout rl_oldincome;
    private TextView tv_oldmoney;
    private TextView tv_oldincome;
    private boolean isVisable;
    private ImageView image_more;
    private TextView tv_line;
    private Handler mRefreshHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 13:
                    try {
                        getTransFerInfo();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transfer_info_lay);
        findViewById();
        initData();
        initUI();
        setListener();
    }

    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()) {
            case R.id.title_left_img:
                finish();
                break;
            case R.id.rl_turnin:
                if (!Check.is_login(TransferInfoActivity.this)) {
                    intent = new Intent(TransferInfoActivity.this, NewLoginActivity.class);
                    startActivity(intent);
                    finish();
                    return;
                } else if (!CommonUtil.isFastDoubleClick()) {
                    showPopup();
                }
                break;
            case R.id.tv_protocol:
                intent = new Intent(TransferInfoActivity.this, CommonWebActivity.class);
                intent.putExtra("url", UrlConfig.IP_V + UrlConfig.DAILYGAIN_TRANSINFO);
                intent.putExtra("titleName", "转让说明");
                startActivity(intent);
                break;
            case R.id.rl_protocol:
                intent = new Intent(TransferInfoActivity.this, CommonWebActivity.class);
                intent.putExtra("url", UrlConfig.IP_R + UrlConfig.TRANSFER_AGREEMENT);
                intent.putExtra("titleName", "转让协议");
                startActivity(intent);
                break;
            case R.id.rl_coupon:
                jumpToCoupon();
                break;
            case R.id.rl_value:
                isVisable = !isVisable;
                if (isVisable) {
                    rl_oldmoney.setVisibility(View.VISIBLE);
                    rl_oldincome.setVisibility(View.VISIBLE);
                    image_more.setImageResource(R.drawable.down_transfer_ic);
                } else {
                    rl_oldmoney.setVisibility(View.GONE);
                    rl_oldincome.setVisibility(View.GONE);
                    image_more.setImageResource(R.drawable.goto_next);
                }
                break;
            default:
                break;
        }
    }

    @Override
    protected void initData() {
        //EventBus.getDefault().register(this);
        mTransferOrderId = getIntent().getStringExtra("transferOrderId");
        event_key = getIntent().getStringExtra("event_key");
        title_center_text.setText("转让信息");
        mUid = PreferencesUtils.getValueFromSPMap(this, PreferencesUtils.Keys.UID);
        initDialog();
        loadData(0);
        Pophandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                try {
                    switch (msg.what) {
                        case REGULAR_MSG_KEYBOARD_HIDE:// 隐藏键盘，开始验证密码
                            close_keyboard.setClickable(false);
                            keyboard_parts.setVisibility(View.INVISIBLE);
                            loading_page.setVisibility(View.VISIBLE);
                            loadingBar.setVisibility(View.VISIBLE);
                            showVerificationPopwindow();
                            break;
                        case REGULAR_MSG_KEYBOARD_VALIDATE_SHOW:
                            if (window != null && window.isShowing()) {
                                window.dismiss();
                            }
                            break;
                        case REGULAR_MSG_KEYBOARD_VALIDATE_HIDE:
                            close_keyboard.setClickable(false);
                            keyboard_parts.setVisibility(View.INVISIBLE);
                            loading_page.setVisibility(View.VISIBLE);
                            loadingBar.setVisibility(View.VISIBLE);
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
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        customDialog = new CustomDialog2(this, R.layout.dialog_common_confirm, new int[]{R.id.dialog_confirm});
        customDialog.setOnDialogViewClickListener(new OnDialogViewClickListener() {
            @Override
            public void OnDialogViewClick(CustomDialog2 dialog, View view) {
                dialog.dismiss();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!PreferencesUtils.getValueFromSPMap(this, PreferencesUtils.Keys.UID).equals("")) {
            showDialog();
        }
    }

    private void initDialog() {
        dialog_bind_card = new NewCustomDialogNoTitleFinish(this, this.getResources().getString(R.string.bind_card_notice));
        dialog_bind_card.btn_custom_dialog_sure.setText(this.getResources().getString(R.string.set_now_btn));
        dialog_bind_card.btn_custom_dialog_sure.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog_bind_card.dismiss();
                if (!PreferencesUtils.getValueFromSPMap(TransferInfoActivity.this,
                        PreferencesUtils.Keys.NAME).equals("") && !PreferencesUtils.getValueFromSPMap(TransferInfoActivity.this,
                        PreferencesUtils.Keys.ID_CARD).equals("")) {
                    Intent intent = new Intent(TransferInfoActivity.this, SecondBandCardActivity.class);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(TransferInfoActivity.this, AddBankActivity.class);
                    startActivity(intent);
                }
            }
        });
        dialog_set_passwd = new NewCustomDialogNoTitleFinish(this, this.getResources().getString(R.string.set_password_notice));
        dialog_set_passwd.btn_custom_dialog_sure.setText(this.getResources().getString(R.string.set_now_btn));
        dialog_set_passwd.btn_custom_dialog_sure.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog_set_passwd.dismiss();
                Intent intent = new Intent(TransferInfoActivity.this, SetTradePwdActivity.class);
                TransferInfoActivity.this.startActivity(intent);
            }
        });
    }

    /**
     * 绑定银行卡及交易密码设置弹窗
     */
    private void showDialog() {
        if (JinrApp.instance().state_bankBind == 0) {
            dialog_bind_card.show();
        } else {
            if (JinrApp.instance().state_tradePassword == 0) {
                dialog_set_passwd.show();
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void findViewById() {
        mLoadingdialog = new LoadingDialog(this);
        title_center_text = (TextView) findViewById(R.id.title_center_text);
        title_left_img = (ImageView) findViewById(R.id.title_left_img);
        title_right_tv = (TextView) findViewById(R.id.tv_protocol);
        title_right_tv.setText("转让说明");
        title_right_tv.setVisibility(View.VISIBLE);
        tv_name = (TextView) findViewById(R.id.tv_name);
        tv_number = (TextView) findViewById(R.id.tv_number);
        tv_transferTime = (TextView) findViewById(R.id.tv_transferTime);
        tv_rate = (TextView) findViewById(R.id.tv_rate);
        tv_upRate = (TextView) findViewById(R.id.tv_upRate);
        tv_laveDay = (TextView) findViewById(R.id.tv_laveDay);
        tv_endDay = (TextView) findViewById(R.id.tv_endDay);
        tv_value = (TextView) findViewById(R.id.tv_value);
        tv_oldmoney = (TextView) findViewById(R.id.tv_oldmoney);
        tv_oldincome = (TextView) findViewById(R.id.tv_oldincome);
        tv_reduction = (TextView) findViewById(R.id.tv_reduction);
        tv_laveIncome = (TextView) findViewById(R.id.tv_laveIncome);
        rl_protocol = (RelativeLayout) findViewById(R.id.rl_protocol);
        rl_coupon = (RelativeLayout) findViewById(R.id.rl_coupon);
        rl_coupon.setVisibility(Check.is_login(this) ? View.VISIBLE : View.GONE);
        tv_coupon = (TextView) findViewById(R.id.tv_coupon);
        tv_actual = (TextView) findViewById(R.id.tv_actual);
        tv_line = (TextView) findViewById(R.id.tv_line);
        tv_offer = (TextView) findViewById(R.id.tv_offer);
        rl_turnin = (RelativeLayout) findViewById(R.id.rl_turnin);
        rl_value = (RelativeLayout) findViewById(R.id.rl_value);
        rl_oldmoney = (RelativeLayout) findViewById(R.id.rl_oldmoney);
        rl_oldincome = (RelativeLayout) findViewById(R.id.rl_oldincome);
        image_more = (ImageView) findViewById(R.id.image_more);
        mRefreshableView = (CustomJinrScrollView) findViewById(R.id.scroll_view_redresable);
        mRefreshableView.setOnRefreshListener(new CustomJinrScrollView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadData(0);
            }
        });
        mRefreshableView.onLoadComplete();// 刷新完成，默认先完成
    }

    @Override
    protected void initUI() {

    }

    @Override
    protected void setListener() {
        title_left_img.setOnClickListener(this);
        title_right_tv.setOnClickListener(this);
        rl_protocol.setOnClickListener(this);
        rl_coupon.setOnClickListener(this);
        rl_value.setOnClickListener(this);
        rl_turnin.setOnClickListener(this);
    }

    public void loadData(final int type) {
        new Thread() {
            @Override
            public void run() {
                mRefreshHandler.sendEmptyMessage(13);
            }
        }.start();
    }

    /**
     * 选择支付方式 @author Ysw created at 2017/3/15 11:17
     */
    private void showPopup() {
        View view = LayoutInflater.from(TransferInfoActivity.this).inflate(R.layout.choose_payment_terms, null);
        listView = (ListView) view.findViewById(R.id.pay_list);
        listView.setOverScrollMode(View.OVER_SCROLL_NEVER);// 防止魅族手机出现HOLD悬停
        PURCHASE_MIN_MONEY = Double.valueOf(tv_actual.getText().toString().trim().replace("元", ""));
        adapter = new PayListAdapter(this, payInfoList, PURCHASE_MIN_MONEY);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                PayInfo minPayInfo = payInfoList.get(position);
                Double putMoney = 0.00;
                if (minPayInfo.getMoney() != null) {
                    putMoney = Double.valueOf(minPayInfo.getMoney());
                } else {
                    putMoney = 0.00;
                    ToastUtil.show(TransferInfoActivity.this, "网络不给力，请稍后再试");
                    return;
                }
                if (PURCHASE_MIN_MONEY > Double.valueOf(minPayInfo.getMoney())) {
                    return;
                }
                payInfo = payInfoList.get(position);
                if (payInfo.getGoods_type().equals("1")) {
                    isBalance = false;
                    popupWindow.dismiss();
                    if (PURCHASE_MIN_MONEY > 0 && PURCHASE_MIN_MONEY <= 50000) {
                        showPopwindow();
                    }
                    if (PURCHASE_MIN_MONEY > 50000) {
                        ToastUtil.show(TransferInfoActivity.this, "已超过银行卡限额,请更换支付方式");
                    }
                } else {
                    isBalance = true;
                    popupWindow.dismiss();
                    if (PURCHASE_MIN_MONEY <= putMoney) {
                        showPopwindow();
                    }
                }
            }
        });
        ImageView cancelBtn = (ImageView) view.findViewById(R.id.cancel_btn);
        popupWindow = new PopupWindow(view, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, true);
        popupWindow.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
        popupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        popupWindow.setBackgroundDrawable(new ColorDrawable());
        backgroundAlpha(0.7f);
        popupWindow.setAnimationStyle(R.style.popwin_anim_style);
        popupWindow.showAtLocation(view, Gravity.BOTTOM, 0, 0);
        cancelBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });

        popupWindow.setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss() {
                backgroundAlpha(1f);
            }
        });
    }

    /**
     * 自定义短信验证的popwindow @author Ysw created at 2017/3/15 11:18
     */
    private void showVerificationPopwindow() {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        cview = inflater.inflate(R.layout.layout_verification, null);
        pw = (EditText) cview.findViewById(R.id.register_et2);
        send_code = (Button) cview.findViewById(R.id.register_send_code);
        close_keyboard = (ImageView) cview.findViewById(R.id.close_keyboard);//
        keyboard_parts = (LinearLayout) cview.findViewById(R.id.keyboard_parts);//
        loading_page = (RelativeLayout) cview.findViewById(R.id.loading_page);//
        loadingBar = (ProgressBar) cview.findViewById(R.id.progressbar);// 模糊进度条
        window = new PopupWindow(cview, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT, true);
        ColorDrawable dw = new ColorDrawable(0xb0000000);
        window.setBackgroundDrawable(dw);
        window.setAnimationStyle(R.style.dialogWindowAnim);
        window.showAtLocation(cview, Gravity.BOTTOM, 0, 0);
        pw.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
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
        keyboardUtil = new KeyboardUtil(cview, window, this, pw);
        pw.setFocusable(true);
        pw.setFocusableInTouchMode(true);
        pw.requestFocus();
        // 隐藏系统键盘并显示光标
        if (android.os.Build.VERSION.SDK_INT <= 10) {// 4.0以下
            pw.setInputType(InputType.TYPE_NULL);
        } else {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            try {
                Class<EditText> cls = EditText.class;
                Method setShowSoftInputOnFocus;
                setShowSoftInputOnFocus = cls.getMethod("setShowSoftInputOnFocus", boolean.class);
                setShowSoftInputOnFocus.setAccessible(true);
                setShowSoftInputOnFocus.invoke(pw, false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
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
                    String validate = pw.getText().toString();
                    try {
                        payValidate(validate);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        sendCode(false);
        pw.addTextChangedListener(textWatcher);
        send_code.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                sendCode(true);
            }
        });
    }

    /**
     * 新需求 自定义密码盘的popwindow
     */
    private void showPopwindow() {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.layout_paypassword, null);
        pw = (EditText) view.findViewById(R.id.eta_pwd);
        close_keyboard = (ImageView) view.findViewById(R.id.close_keyboard);
        keyboard_parts = (LinearLayout) view.findViewById(R.id.keyboard_parts);
        loading_page = (RelativeLayout) view.findViewById(R.id.loading_page);
        loadingBar = (ProgressBar) view.findViewById(R.id.progressbar);// 模糊进度条
        forgot_passwordTextView = (TextView) view.findViewById(R.id.forgot_password);
        window = new PopupWindow(view, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT, true);
        ColorDrawable dw = new ColorDrawable(0xb0000000);
        window.setBackgroundDrawable(dw);
        window.setAnimationStyle(R.style.dialogWindowAnim);
        window.showAtLocation(view, Gravity.BOTTOM, 0, 0);
        pw.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
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
                    password = pw.getText().toString();
                    try {
                        CreateOrder();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        pw.addTextChangedListener(textWatcher);
        // 这三个控件是显示支付金额和购买产品名 @author Ysw created at 2017/3/31 17:14
        RelativeLayout rl_info = (RelativeLayout) view.findViewById(R.id.rl_info);
        TextView tv_payMoney = (TextView) view.findViewById(R.id.tv_payMoney);
        TextView tv_productName = (TextView) view.findViewById(R.id.tv_productName);
        rl_info.setVisibility(View.GONE);
        //购买产品信息
        rl_info.setVisibility(View.VISIBLE);
        tv_payMoney.setText("¥" + new DecimalFormat("#####0.00").format(Double.parseDouble(tv_actual.getText().toString().replace("元", ""))));
        tv_productName.setText(mTransferInfoModel.getProduct_name() + "转让");
    }

    /**
     * 设置添加屏幕的背景透明度
     *
     * @param bgAlpha
     */
    public void backgroundAlpha(float bgAlpha) {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = bgAlpha;
        getWindow().setAttributes(lp);
    }

    /**
     * 获取转让信息详情 @author Ysw created at 2017/4/5 15:55
     */
    private void getTransFerInfo() throws Exception {
        JSONObject obj = new JSONObject();
        obj.put("uid", mUid);
        obj.put("transfer_order_id", mTransferOrderId);
        RequestParams params = new RequestParams();
        String desStr = MyDES.encrypt(obj.toString());
        params.put("cipher", desStr);
        MyhttpClient.desPost(UrlConfig.TRANSFER_INFO, params, new AsyncHttpResponseHandler() {
            @Override
            public void onFinish() {
                super.onFinish();
                try {
                    if (code != 2600) {
                        if (!PreferencesUtils.getValueFromSPMap(TransferInfoActivity.this, PreferencesUtils.Keys.UID).equals("")) {
                            getBindCardInfo();
                        }
                    }
                } catch (Exception ignored) {
                }
            }

            @Override
            public void onStart() {
                super.onStart();
                mLoadingdialog.show();
            }

            @Override
            public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
                super.onFailure(arg0, arg1, arg2, arg3);
                mLoadingdialog.dismiss();
                mRefreshableView.onRefreshComplete(false);
                rl_turnin.setOnClickListener(null);
                rl_turnin.setBackgroundColor(getResources().getColor(R.color.gray_bg_btn));
                ToastUtil.show(MainActivity.instance, R.string.default_error);
            }

            @Override
            public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
                super.onSuccess(arg0, arg1, arg2);
                mLoadingdialog.dismiss();
                try {
                    String response = new String(arg2, "UTF-8");
                    response = CommonUtil.transResponse(response);
                    if (response == null || response.equals("")) {
                        mRefreshableView.onRefreshComplete(false);
                        return;
                    }
                    JSONObject jsonObject = new JSONObject(response);
                    String cipher = jsonObject.getString("cipher");
                    String desStr = MyDES.decrypt(cipher);
                    MyLog.e(TAG, "TransferInfoActivity.onSuccess：" + desStr);
                    if (TextUtils.isEmpty(desStr)) {
                        mRefreshableView.onRefreshComplete(false);
                        return;
                    }
                    JSONObject object = new JSONObject(desStr);
                    code = object.getInt("code");
                    if (code == 2600) {
                        mRefreshableView.onRefreshComplete(false);
                        ToastUtil.show(TransferInfoActivity.this, object.getString("msg"));
                        rl_turnin.setOnClickListener(null);
                        rl_turnin.setBackgroundColor(getResources().getColor(R.color.gray_bg_btn));
                        rl_turnin.postDelayed(new Runnable() {
                            public void run() {
                                TransferInfoActivity.this.finish();
                            }
                        }, 1000);
                    }
                    BaseModel<TransferInfoModel> result = new Gson().fromJson(desStr, new TypeToken<BaseModel<TransferInfoModel>>() {
                    }.getType());
                    mTransferInfoModel = result.getData();
                    if (result.isSuccess()) {
                        mRefreshableView.onRefreshComplete(true);
                        if (mTransferInfoModel.getTransfer_money() != null && !mTransferInfoModel.getTransfer_money().equals("")) {
                            PURCHASE_MIN_MONEY = Double.valueOf(mTransferInfoModel.getTransfer_money()) - Double.valueOf(mTransferInfoModel.getCoupon_money());
                        }
                        tv_name.setText(mTransferInfoModel.getProduct_name());
                        tv_number.setText(mTransferInfoModel.getContract_num());
                        tv_transferTime.setText(mTransferInfoModel.getCan_transfer_time());
                        tv_rate.setText(mTransferInfoModel.getToday_rate());
                        tv_upRate.setText(mTransferInfoModel.getDaily_gain_add_rate());
                        tv_laveDay.setText(mTransferInfoModel.getRemain_time());
                        tv_endDay.setText(mTransferInfoModel.getInvest_end_date());
                        // 转让价值=原始本金+已得收益 @author Ysw created at 2017/4/14 18:31
                        String invest_amount = mTransferInfoModel.getInvest_amount();
                        String gained_income = mTransferInfoModel.getGained_income();
                        double v = Double.valueOf(invest_amount) + Double.valueOf(gained_income);
                        tv_value.setText(new DecimalFormat("#####0.00").format(v) + "元");
                        tv_oldmoney.setText(invest_amount + "元");
                        tv_oldincome.setText(gained_income + "元");
                        tv_reduction.setText(mTransferInfoModel.getDiscount() + "元");
                        tv_laveIncome.setText(mTransferInfoModel.getExpect_reamin_yield() + "元");
                        tv_coupon.setText(mTransferInfoModel.getCoupon_selected());
                        tv_coupon.setTextColor(Color.parseColor("#f37f72"));
                        double actualMoney = Double.valueOf(mTransferInfoModel.getTransfer_money()) - Double.valueOf(mTransferInfoModel.getCoupon_money());
                        actualMoney = actualMoney > 0 ? actualMoney : 0;
                        tv_actual.setText(new DecimalFormat("#####0.00").format(actualMoney) + "元");
                        double offerMoney = Double.valueOf(mTransferInfoModel.getDiscount()) + Double.valueOf(mTransferInfoModel.getCoupon_money());
                        tv_offer.setText("已优惠" + new DecimalFormat("#####0.00").format(offerMoney) + "元");
                        if (offerMoney == 0) {
                            tv_line.setVisibility(View.GONE);
                            tv_offer.setVisibility(View.GONE);
                        } else {
                            tv_line.setVisibility(View.VISIBLE);
                            tv_offer.setVisibility(View.VISIBLE);
                        }
                        mCoupon_id = mTransferInfoModel.getCoupon_id();
                    } else {
                        mRefreshableView.onRefreshComplete(false);
                        rl_turnin.setOnClickListener(null);
                        rl_turnin.setBackgroundColor(getResources().getColor(R.color.gray_bg_btn));
                        ToastUtil.show(getApplication(), result.getMsg());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    rl_turnin.setOnClickListener(null);
                    rl_turnin.setBackgroundColor(getResources().getColor(R.color.gray_bg_btn));
                    mRefreshableView.onRefreshComplete(false);
                }
            }
        });
    }

    // 获取绑定卡的信息
    private void getBindCardInfo() throws Exception {
        RequestParams params = new RequestParams();
        UidObj obj = new UidObj(PreferencesUtils.getValueFromSPMap(TransferInfoActivity.this, PreferencesUtils.Keys.UID));
        String desStr = MyDES.encrypt(new Gson().toJson(obj));
        params.put("cipher", desStr);
        MyhttpClient.desPost(UrlConfig.USER_BANKINFO, params, new AsyncHttpResponseHandler() {
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
                ToastUtil.show(TransferInfoActivity.this, R.string.default_error);
            }

            @Override
            public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
                super.onSuccess(arg0, arg1, arg2);
                String response;
                try {
                    response = new String(arg2, "UTF-8");
                    JSONObject jsonObject = new JSONObject(response);
                    String cipher = jsonObject.getString("cipher");
                    String desc = MyDES.decrypt(cipher);
                    if (TextUtils.isEmpty(desc)) return;
                    BaseModel<UserBindinfo> result = new Gson().fromJson(desc, new TypeToken<BaseModel<UserBindinfo>>() {
                    }.getType());
                    if (result.isSuccess()) {
                        UserBindinfo data = result.getData();
                        if (data == null) return;
                        String id = PreferencesUtils.getValueFromSPMap(TransferInfoActivity.this, PreferencesUtils.Keys.BANK_CARD_ID);
                        String bank_no = PreferencesUtils.getValueFromSPMap(TransferInfoActivity.this, PreferencesUtils.Keys.BANK_CARD_NO);
                        getPayList();// 支付列表
                        if (data.getId().equals(id) && data.getBank_no().equals(bank_no)) return;
                        JinrApp.instance().state_bankBind = Integer.valueOf(data.getState_bankBind());
                        JinrApp.instance().state_tradePassword = Integer.valueOf(data.getState_tradePassword());
                        PreferencesUtils.putIntToSPMap(TransferInfoActivity.this, PreferencesUtils.Keys.IS_BIND_CARD, Integer.valueOf(data.getState_bankBind()));
                        PreferencesUtils.putIntToSPMap(TransferInfoActivity.this, PreferencesUtils.Keys.IS_SET_TRADE_PWD, Integer.valueOf(data.getState_tradePassword()));
                        PreferencesUtils.putValueToSPMap(TransferInfoActivity.this, PreferencesUtils.Keys.BANK_BG_URL, data.getBgimg());
                        PreferencesUtils.putValueToSPMap(TransferInfoActivity.this, PreferencesUtils.Keys.BANK_CARD_ID, data.getId());
                        PreferencesUtils.putValueToSPMap(TransferInfoActivity.this, PreferencesUtils.Keys.BANK_CARD_NO, data.getBank_no());
                        PreferencesUtils.putValueToSPMap(TransferInfoActivity.this, PreferencesUtils.Keys.BANK_NAME, data.getBank_name());
                        PreferencesUtils.putValueToSPMap(TransferInfoActivity.this, PreferencesUtils.Keys.USER_TEL, data.getUser_tel());
                        PreferencesUtils.putValueToSPMap(TransferInfoActivity.this, PreferencesUtils.Keys.CARD_LAST, data.getCard_last());
                    } else {
                        ToastUtil.show(TransferInfoActivity.this, result.getMsg());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void getPayList() throws Exception {
        JSONObject obj = new JSONObject();
        obj.put("uid", mUid);
        obj.put("goods_type", mTransferInfoModel.getGoods_type());
        obj.put("pay_list_type", mTransferInfoModel.getPay_list_type());
        RequestParams params = new RequestParams();
        String desStr = MyDES.encrypt(obj.toString());
        params.put("cipher", desStr);
        MyhttpClient.desPost(UrlConfig.PAY_LIST, params, new AsyncHttpResponseHandler() {
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
                ToastUtil.show(TransferInfoActivity.this, R.string.default_error);
            }

            @Override
            public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
                super.onSuccess(arg0, arg1, arg2);
                try {
                    String response = new String(arg2, "UTF-8");
                    response = CommonUtil.transResponse(response);
                    if (response == null || response.equals("")) return;
                    JSONObject obj = new JSONObject(response);
                    String cipher = obj.getString("cipher");
                    String desStr = MyDES.decrypt(cipher);
                    if (TextUtils.isEmpty(desStr)) return;
                    JSONObject jsonObject = new JSONObject(desStr);
                    if (jsonObject.getInt("code") == 1000) {
                        payInfoList.clear();
                        BaseModel<PayInfoList> result = new Gson().fromJson(desStr, new TypeToken<BaseModel<PayInfoList>>() {
                        }.getType());
                        if (result != null && result.getData().getList() != null) {
                            for (int i = 0; i < result.getData().getList().size(); i++) {
                                payInfoList.add(result.getData().getList().get(i));
                                rl_turnin.setOnClickListener(TransferInfoActivity.this);
                                rl_turnin.setBackgroundColor(getResources().getColor(R.color.blue_btn_bg));
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 购买转入，创建订单 @author Ysw created at 2017/4/5 16:15
     */
    protected void CreateOrder() throws Exception {
        Message msg = new Message();
        msg.what = REGULAR_MSG_KEYBOARD_VALIDATE_HIDE;
        Pophandler.sendMessage(msg);
        JSONObject obj = new JSONObject();
        obj.put("money", mTransferInfoModel.getTransfer_money());
        obj.put("buy_type", mTransferInfoModel.getGoods_type());
        obj.put("pay_type", payInfo.getGoods_type());
        obj.put("buy_order", mTransferOrderId);
        obj.put("product_code", mTransferInfoModel.getProduct_code());
        obj.put("buss_pwd", password);
        if (mCoupon_id.equals("-1")) mCoupon_id = "0";
        obj.put("coupon_id", mCoupon_id);
        obj.put("uid", mUid);
        obj.put("platform", "4");
        if (event_key != null && !event_key.equals("")) obj.put("event_key", event_key);
        RequestParams params = new RequestParams();
        String desStr = MyDES.encrypt(obj.toString());
        params.put("cipher", desStr);
        MyhttpClient.desPost(UrlConfig.ORDER_CREATE, params, new AsyncHttpResponseHandler() {
            @Override
            public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
                close_keyboard.setClickable(true);
                super.onFailure(arg0, arg1, arg2, arg3);
                ToastUtil.show(TransferInfoActivity.this, R.string.default_error);
                Message msg = new Message();
                msg.what = REGULAR_MSG_KEYBOARD_VALIDATE_SHOW;
                Pophandler.sendMessage(msg);
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
                    final String desStr = MyDES.decrypt(cipher);
                    if (TextUtils.isEmpty(desStr)) return;
                    final JSONObject obj = new JSONObject(desStr);
                    new Handler().postDelayed(new Runnable() {
                        public void run() {
                            try {
                                if (obj.getInt("code") == 3002) {
                                    Message msg = new Message();
                                    msg.what = REGULAR_MSG_KEYBOARD_VALIDATE_ERROR_SHOW;
                                    Pophandler.sendMessage(msg);
                                    pw.setText("");
                                    ToastUtil.show(TransferInfoActivity.this, obj.getString("msg"));
                                    return;
                                }
                                Message msg1 = new Message();
                                msg1.what = REGULAR_MSG_KEYBOARD_VALIDATE_SHOW;
                                Pophandler.sendMessage(msg1);
                                BaseModel<PaymentRegularResultObj> result = new Gson().fromJson(desStr, new TypeToken<BaseModel<PaymentRegularResultObj>>() {
                                }.getType());
                                if (result.isSuccess()) {
                                    requesPay = result.getData();
                                    if (isBalance) {
                                        Intent intent = new Intent(TransferInfoActivity.this, PurchaseResultActivity.class);
                                        intent.putExtra("payResult", requesPay);
                                        ToastUtil.show(getApplication(), "交易成功");
                                        sendBroadcast(new Intent().setAction("action.refresh_actdetail"));
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        Message msg = new Message();
                                        msg.what = REGULAR_MSG_KEYBOARD_HIDE;
                                        Pophandler.sendMessage(msg);
                                    }
                                } else {
                                    String msgStr = result.getMsg();
                                    ToastUtil.show(TransferInfoActivity.this, msgStr);
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }, 1000);

                } catch (Exception e) {
                    Message msg1 = new Message();
                    msg1.what = REGULAR_MSG_KEYBOARD_VALIDATE_SHOW;
                    Pophandler.sendMessage(msg1);
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 支付验证
     *
     * @throws Exception
     */
    private void payValidate(String validate) throws Exception {
        Message msg = new Message();
        msg.what = REGULAR_MSG_KEYBOARD_VALIDATE_HIDE;
        Pophandler.sendMessage(msg);
        JSONObject obj = new JSONObject();
        obj.put("order_num", requesPay.getOrder_num());
        obj.put("validate_code", validate);
        obj.put("uid", mUid);
        RequestParams params = new RequestParams();
        String desStr = MyDES.encrypt(obj.toString());
        params.put("cipher", desStr);
        MyhttpClient.desPost(UrlConfig.PAY_VALIDATE, params, new AsyncHttpResponseHandler() {

            @Override
            public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
                super.onFailure(arg0, arg1, arg2, arg3);
                Message msg = new Message();
                msg.what = REGULAR_MSG_KEYBOARD_VALIDATE_SHOW;
                Pophandler.sendMessage(msg);
                ToastUtil.show(TransferInfoActivity.this, R.string.default_error);

            }

            @Override
            public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
                super.onSuccess(arg0, arg1, arg2);
                try {
                    String response = new String(arg2, "UTF-8");
                    response = CommonUtil.transResponse(response);
                    if (response == null || response.equals("")) {
                        return;
                    }
                    JSONObject jsonObject = new JSONObject(response);
                    String cipher = jsonObject.getString("cipher");
                    String desStr = MyDES.decrypt(cipher);
                    if (TextUtils.isEmpty(desStr))
                        return;
                    JSONObject obj = new JSONObject(desStr);
                    if (obj.getInt("code") == 1000) {
                        pModel = new Gson().fromJson(desStr, new TypeToken<BaseModel<PaymentRegularResultObj>>() {
                        }.getType());
                        new Handler().postDelayed(new Runnable() {
                            public void run() {
                                try {
                                    Message msg = new Message();
                                    msg.what = REGULAR_MSG_KEYBOARD_VALIDATE_SHOW;
                                    Pophandler.sendMessage(msg);
                                    sendPaymentResult();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }, 3500);
                    } else if (obj.getInt("code") == 3001) {
                        Message msg = new Message();
                        msg.what = REGULAR_MSG_KEYBOARD_VALIDATE_ERROR_SHOW;
                        Pophandler.sendMessage(msg);
                        pw.setText("");
                        ToastUtil.show(getApplication(), obj.getString("msg"));
                    } else if (obj.getInt("code") == 2600) {
                        Message msg = new Message();
                        msg.what = REGULAR_MSG_KEYBOARD_VALIDATE_SHOW;
                        Pophandler.sendMessage(msg);
                        ToastUtil.show(TransferInfoActivity.this, obj.getString("msg"));
                        new Handler().postDelayed(new Runnable() {
                            public void run() {
                                try {
                                    TransferInfoActivity.this.finish();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }, 1000);
                    } else {
                        Message msg = new Message();
                        msg.what = REGULAR_MSG_KEYBOARD_VALIDATE_SHOW;
                        Pophandler.sendMessage(msg);
                        ToastUtil.show(getApplication(), obj.getString("msg"));
                    }

                } catch (Exception e) {
                    Message msg = new Message();
                    msg.what = REGULAR_MSG_KEYBOARD_VALIDATE_ERROR_SHOW;
                    Pophandler.sendMessage(msg);
                }
            }
        });
    }

    /**
     * 查询支付结果 @author Ysw created at 2017/3/15 11:25
     */
    protected void sendPaymentResult() throws Exception {
        showWaittingDialog(TransferInfoActivity.this);
        JSONObject obj = new JSONObject();
        obj.put("uid", mUid);
        obj.put("order_num", pModel.getData().getOrder_num());
        obj.put("pay_type", pModel.getData().getPay_type());
        obj.put("buy_type", pModel.getData().getBuy_type());
        RequestParams params = new RequestParams();
        String desStr = MyDES.encrypt(obj.toString());
        params.put("cipher", desStr);
        MyhttpClient.desPost(UrlConfig.ORDER_BAL_OUT_RESULT, params, new AsyncHttpResponseHandler() {

            @Override
            public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
                super.onFailure(arg0, arg1, arg2, arg3);
                dismissWaittingDialog();
                ToastUtil.show(TransferInfoActivity.this, R.string.default_error);
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
                    JSONObject obj = new JSONObject(response);
                    String cipher = obj.getString("cipher");
                    String desStr = MyDES.decrypt(cipher);
                    if (TextUtils.isEmpty(desStr))
                        return;
                    JSONObject jsonObject = new JSONObject(desStr);
                    if (jsonObject.getInt("code") == 1000) {
                        Intent intent = new Intent(TransferInfoActivity.this, PurchaseResultActivity.class);
                        intent.putExtra("payResult", pModel.getData());
                        sendBroadcast(new Intent().setAction("action.refresh_actdetail"));
                        startActivity(intent);
                        finish();
                    } else {
                        String msgStr = jsonObject.getString("msg");
                        if (msgStr == null || msgStr.equals("")) {
                            ToastUtil.show(TransferInfoActivity.this, "查询交易结果失败，请前往交易记录查询");
                            return;
                        }
                        ToastUtil.show(TransferInfoActivity.this, msgStr);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 发送验证码
     */
    public void sendCode(boolean isSend) {
        SendMobileCode.getInstance().sendValidateCode(send_code, TransferInfoActivity.this, mUid, requesPay.getOrder_num(), isSend, new Back() {
            @Override
            public void sms(String result) {
                if (result != null && !"".equals(result)) {
                    send_code.setClickable(true);
                }
            }
        });
    }

    /**
     * 跳转至卡卷界面 @author Ysw created at 2017/3/30 20:26
     */
    private void jumpToCoupon() {
        if (mTransferInfoModel != null) {
            String uid = "";
            try {
                uid = MyDES.encrypt(mUid);
            } catch (Exception e) {
                e.printStackTrace();
            }
            mCouponUrl = UrlConfig.IP_V + UrlConfig.USER_COUPON + uid + "&coupon_id=" + mCoupon_id + "&money="
                    + mTransferInfoModel.getTransfer_money() + "&product_num=" + mTransferInfoModel.getProduct_num() + "&randow=2561";
            Intent intent = new Intent(this, PurchaseLimitNoteActivity.class);
            intent.putExtra("type", "2");
            intent.putExtra("url", mCouponUrl);
            startActivityForResult(intent, 2);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == 2) {
            setResult(data.getStringExtra("url"));
        }
    }

    public void setResult(String url) {
        if (url != null && !"".equals(url)) {
            MyLog.e(TAG, "setResult: " + url);
            String[] split = url.split("&");
            String couponId = split[1];
            String couponMoney = split[2];
            String couponUse_mode = split[3];
            String couponSelected_name = split[4];
            mCoupon_id = couponId;
            try {
                couponSelected_name = URLDecoder.decode(couponSelected_name, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            if (null != couponId && !couponId.equals("-1")) {
                String couponMoney_type = split[5];
                tv_coupon.setText(couponSelected_name);
                tv_coupon.setTextColor(Color.parseColor("#f37f72"));
                if (couponMoney_type.equals("1")) {
                    Double money = Double.valueOf(mTransferInfoModel.getTransfer_money());
                    money = money - Double.valueOf(couponMoney) > 0 ? money - Double.valueOf(couponMoney) : 0;
                    tv_actual.setText(new DecimalFormat("#####0.00").format(money) + "元");
                    double offerMoney = Double.valueOf(mTransferInfoModel.getDiscount()) + Double.valueOf(couponMoney);
                    tv_offer.setText("已优惠" + new DecimalFormat("#####0.00").format(offerMoney) + "元");
                    if (offerMoney == 0) {
                        tv_line.setVisibility(View.GONE);
                        tv_offer.setVisibility(View.GONE);
                    } else {
                        tv_line.setVisibility(View.VISIBLE);
                        tv_offer.setVisibility(View.VISIBLE);
                    }
                }
            } else {
                tv_coupon.setText(couponSelected_name);
                tv_coupon.setTextColor(Color.parseColor("#f37f72"));
                tv_actual.setText(new DecimalFormat("#####0.00").format(Double.valueOf(mTransferInfoModel.getTransfer_money())) + "元");
                double offerMoney = Double.valueOf(mTransferInfoModel.getDiscount());
                tv_offer.setText("已优惠" + new DecimalFormat("#####0.00").format(offerMoney) + "元");
                if (offerMoney == 0) {
                    tv_line.setVisibility(View.GONE);
                    tv_offer.setVisibility(View.GONE);
                } else {
                    tv_line.setVisibility(View.VISIBLE);
                    tv_offer.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    // 绑卡成功后刷新页面
    @Subscriber(tag = EventBusKey.BANDCARD_SUCCESD)
    public void notityInfo(boolean isChoose) {
        if (isChoose)
            loadData(0);
    }
}
