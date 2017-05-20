package com.jinr.core.trade.purchase;

import android.annotation.SuppressLint;
import android.app.ActionBar.LayoutParams;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.Html;
import android.text.InputType;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.text.style.TextAppearanceSpan;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
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
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jinr.core.JinrApp;
import com.jinr.core.MainActivity;
import com.jinr.core.R;
import com.jinr.core.bankCard.AddBankActivity;
import com.jinr.core.bankCard.SecondBandCardActivity;
import com.jinr.core.base.BaseActivity;
import com.jinr.core.config.MessageWhat;
import com.jinr.core.config.UrlConfig;
import com.jinr.core.security.tradepwd.FindTradePwd;
import com.jinr.core.security.tradepwd.SetTradePwdActivity;
import com.jinr.core.trade.PayListAdapter;
import com.jinr.core.ui.NewCustomDialogNoTitleFinish;
import com.jinr.core.utils.CommonUtil;
import com.jinr.core.utils.KeyboardUtil;
import com.jinr.core.utils.MyDES;
import com.jinr.core.utils.MyLog;
import com.jinr.core.utils.MyhttpClient;
import com.jinr.core.utils.PreferencesUtils;
import com.jinr.core.utils.SendMobileCode;
import com.jinr.core.utils.SendMobileCode.Back;
import com.jinr.core.utils.ToastUtil;
import com.jinr.core.utils.UmUtils;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.util.ArrayList;

import model.BaseModel;
import model.CommonProjectMode;
import model.PayInfo;
import model.PayInfoList;
import model.PaymentRegularResultObj;
import model.ProductCommonModel;
import model.UidObj;
import model.UserBindinfo;

/**
 * 定期专用转入页面
 *
 * @author Administrator
 */
public class RegularPurchaseFirstActivity extends BaseActivity implements OnClickListener {

    private ImageView title_left_img; // title左边图片
    private TextView title_center_text; // title标题
    private TextView puchase_tips_tv;// 50000限额字符串
    private TextView tv_money_yield;
    private TextView tv_yestday_money;
    private TextView pre_money_yield;
    private TextView tv_content, bottomTv;
    private EditText purchase_et;
    private Button pay_bt;
    private Button send_code;
    private TextView switch_account_hint, limit_money_tv;
    private TextView tv_about_yesterday;
    private Button limit_note_btn; // 限额说明
    private ScrollView sView;
    private String purchase = "0";
    private String user_id = "";
    private Double putMoney = 0.00;
    private Double minMoney = 0.00;
    private ProductCommonModel productCommonModel;

    private NewCustomDialogNoTitleFinish dialog_bind_card;
    private NewCustomDialogNoTitleFinish dialog_set_passwd;
    public static RegularPurchaseFirstActivity instance = null;
    private TextView tv_time_in;// 预计转入时间
    private PopupWindow window;
    private TextView switchAccountTv;
    private View view;
    private View cview;
    private TextView forgot_passwordTextView;
    private Handler Pophandler;
    // final static int REGULAR_MSG_CREATE_ORDER = 1;
    final static int REGULAR_MSG_KEYBOARD_HIDE = 2;
    final static int REGULAR_MSG_KEYBOARD_VALIDATE_SHOW = 3;
    final static int REGULAR_MSG_KEYBOARD_VALIDATE_HIDE = 4;
    final static int REGULAR_MSG_KEYBOARD_VALIDATE_ERROR_SHOW = 5;

    private LinearLayout keyboard_parts;
    private LinearLayout switch_account_lay;
    private RelativeLayout loading_page;
    private ProgressBar loadingBar;
    private ImageView close_keyboard;
    private EditText pw;// 密码输入框
    private TextView limitTv;
    private KeyboardUtil keyboardUtil;
    private PopupWindow popupWindow;
    private boolean isBalance = true;
    private String bank_name;
    private String bank_card_last;

    private PaymentRegularResultObj requesPay;
    private BaseModel<PaymentRegularResultObj> pModel;
    private String strContent;
    private String event_key = "";
    private long limit_money;
    private String url;
    private Double PURCHASE_MIN_MONEY = 100.00;
    private ArrayList<PayInfo> payInfoList = new ArrayList<>();
    private ListView listView;
    private PayListAdapter adapter;
    private CommonProjectMode cProjectMode;
    private PayInfo payInfo;
    private String password;
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case MessageWhat.LIMIT_MESS:
                    String rev = (String) msg.obj;
                    puchase_tips_tv.setText(Html.fromHtml(rev));
                    break;
            }
        }
    };

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE | WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        setContentView(R.layout.regular_purchase_first);
        instance = RegularPurchaseFirstActivity.this;
        findViewById();
        initData();
        initUI();
        setListener();
    }

    private void initDialog() {
        dialog_bind_card = new NewCustomDialogNoTitleFinish(this, this.getResources().getString(R.string.bind_card_notice));
        dialog_bind_card.btn_custom_dialog_sure.setText(this.getResources()
                .getString(R.string.set_now_btn));
        dialog_bind_card.btn_custom_dialog_sure.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog_bind_card.dismiss();
                if (!PreferencesUtils.getValueFromSPMap(RegularPurchaseFirstActivity.this, PreferencesUtils.Keys.NAME).equals("")
                        && !PreferencesUtils.getValueFromSPMap(RegularPurchaseFirstActivity.this, PreferencesUtils.Keys.ID_CARD).equals("")) {
                    Intent intent = new Intent(RegularPurchaseFirstActivity.this, SecondBandCardActivity.class);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(RegularPurchaseFirstActivity.this, AddBankActivity.class);
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
                Intent intent = new Intent(RegularPurchaseFirstActivity.this, SetTradePwdActivity.class);
                RegularPurchaseFirstActivity.this.startActivity(intent);
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
    protected void onResume() {
        super.onResume();
        // 显示银行卡信息
        user_id = PreferencesUtils.getValueFromSPMap(this, PreferencesUtils.Keys.UID);
        if (!isBalance) {
            bank_name = PreferencesUtils.getValueFromSPMap(this, PreferencesUtils.Keys.BANK_NAME);
            bank_card_last = PreferencesUtils.getValueFromSPMap(this, PreferencesUtils.Keys.CARD_LAST);
            limit_note_btn.setVisibility(View.VISIBLE);
            switch_account_hint.setText("使用" + bank_name + " (尾号" + bank_card_last + ") 付款，");
            limitTv.setVisibility(View.VISIBLE);
            setColorTV(limitTv, 8, 16, instance.getResources().getColor(R.color.event_dialog_button));
        }
        // 未绑定银行卡或未设置交易密码之后dialog重新生成
        showDialog();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return super.onKeyDown(keyCode, event);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.jinr.core.base.Base2Activity#initData()
     */
    @Override
    protected void initData() {
        productCommonModel = (ProductCommonModel) getIntent().getSerializableExtra("regular");
        if (null != productCommonModel && null != productCommonModel.getEventKey()) {
            event_key = productCommonModel.getEventKey();
        }
        bank_name = PreferencesUtils.getValueFromSPMap(this, PreferencesUtils.Keys.BANK_NAME);
        bank_card_last = PreferencesUtils.getValueFromSPMap(this, PreferencesUtils.Keys.CARD_LAST);
        user_id = PreferencesUtils.getValueFromSPMap(this, PreferencesUtils.Keys.UID);
        try {
            sendCunbeiDetail();
        } catch (Exception e1) {
            e1.printStackTrace();
        }
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
                                scollFull(0);
                                window.dismiss();
                            }
                            break;
                        case REGULAR_MSG_KEYBOARD_VALIDATE_HIDE:
                            pay_bt.setClickable(true);
                            close_keyboard.setClickable(false);
                            keyboard_parts.setVisibility(View.INVISIBLE);
                            loading_page.setVisibility(View.VISIBLE);
                            loadingBar.setVisibility(View.VISIBLE);
                            break;
                        case REGULAR_MSG_KEYBOARD_VALIDATE_ERROR_SHOW:
                            pay_bt.setClickable(true);
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
    }

    @Override
    protected void findViewById() {
        title_left_img = (ImageView) findViewById(R.id.title_left_img);
        title_center_text = (TextView) findViewById(R.id.title_center_text);
        limit_note_btn = (Button) findViewById(R.id.btn_limit_note);
        purchase_et = (EditText) findViewById(R.id.puchase_et1);
        pay_bt = (Button) findViewById(R.id.purchase_submit_bt);
        tv_time_in = (TextView) findViewById(R.id.tv_time_in);
        switch_account_lay = (LinearLayout) findViewById(R.id.switch_account_lay);
        switch_account_lay.setVisibility(View.VISIBLE);
        switchAccountTv = (TextView) findViewById(R.id.switch_account_tv);
        switch_account_hint = (TextView) findViewById(R.id.switch_account_hint);
        limitTv = (TextView) findViewById(R.id.limit_tv);
        limit_money_tv = (TextView) findViewById(R.id.tv_limit_money);
        tv_money_yield = (TextView) findViewById(R.id.tv_money_yield);
        pre_money_yield = (TextView) findViewById(R.id.pre_money_yield);
        tv_yestday_money = (TextView) findViewById(R.id.tv_yestday_money);
        tv_about_yesterday = (TextView) findViewById(R.id.tv_about_yesterday);
        tv_content = (TextView) findViewById(R.id.tv_content);
        bottomTv = (TextView) findViewById(R.id.bottom_tv);
        sView = (ScrollView) findViewById(R.id.scroll_view_redresable);
    }

    // 滑动到底部或顶部
    private void scollFull(final int type) {
        switch (type) {
            case 0:
                bottomTv.setVisibility(View.GONE);
                break;
            case 1:
                bottomTv.setVisibility(View.INVISIBLE);
                break;
            default:
                break;
        }
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (type == 0) {
                    sView.fullScroll(ScrollView.FOCUS_UP);
                } else {
                    sView.fullScroll(ScrollView.FOCUS_DOWN);
                }
            }
        });
    }

    protected void setColorTV(TextView tips_tv, int star, int end, int color) {
        SpannableString ss = new SpannableString(tips_tv.getText());
        ss.setSpan(new ForegroundColorSpan(color), star, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tips_tv.setText(ss);
    }

    @Override
    protected void initUI() {
        title_center_text.setText("转入" + productCommonModel.getName());
        limit_money_tv.setText("您的账户最多可转入");
        initDialog();
    }

    @Override
    protected void setListener() {
        title_left_img.setOnClickListener(this);
        limit_note_btn.setOnClickListener(this);
        switchAccountTv.setOnClickListener(this);
        purchase_et.setOnClickListener(this);
        pay_bt.setOnClickListener(this);
        pay_bt.setClickable(false);
        purchase_et.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int arg1, int arg2, int arg3) {
                if (s.length() > 0) {
                    if (s.toString().startsWith("0")) {
                        purchase_et.setText("");
                    }
                    if (Double.parseDouble(s.toString()) >= PURCHASE_MIN_MONEY) {
                        pay_bt.setClickable(true);
                        pay_bt.setBackgroundResource(R.drawable.btn_bg_select);
                    } else {
                        pay_bt.setClickable(false);
                        pay_bt.setBackgroundResource(R.drawable.btn_gray_bg);
                    }
                    long input_money = Long.parseLong(s.toString());
                    if (limit_money != 0) {
                        if (limit_money < input_money) {
                            purchase_et.setText(String.valueOf(limit_money));
                            purchase_et.setSelection(s.length() - 1);
                        } else {
                            pay_bt.setClickable(true);
                        }
                    } else {
                        ToastUtil.show(RegularPurchaseFirstActivity.this, "已超过限额");
                        purchase_et.setText("");
                        pay_bt.setClickable(false);
                    }
                    if (isBalance) {
                        if (Double.parseDouble(s.toString()) > 10000000) {
                            purchase_et.setText("10000000");
                        }
                    } else {
                        if (Double.parseDouble(s.toString()) > 50000) {
                            purchase_et.setText("50000");
                        }
                    }
                    purchase_et.setSelection(purchase_et.getText().length());
                } else {
                    pay_bt.setClickable(false);
                    pay_bt.setBackgroundResource(R.drawable.btn_gray_bg);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int arg1, int arg2, int arg3) {

            }

            @Override
            public void afterTextChanged(Editable arg0) {

            }
        });
        purchase_et.setOnEditorActionListener(new EditText.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    return true;
                }
                return false;
            }
        });
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_left_img:// 左侧图标
                PreferencesUtils.Keys.BACK_TO_ACT = 0;
                PreferencesUtils.Keys.BACK_TO_GIFT = false;
                finish();
                break;
            case R.id.btn_limit_note:// 限额说明
                gotoIntro();
                break;
            case R.id.puchase_et1:
                if (productCommonModel.getName().equals("定期")) {
                    UmUtils.customsEvent(RegularPurchaseFirstActivity.this, UmUtils.REGULARIN_MONEY_CLICK, UmUtils.REGULARIN_MONEY_HUM);
                } else {
                    UmUtils.customsEvent(RegularPurchaseFirstActivity.this, UmUtils.DAYPRODUCTIN_MONEY_CLICK, UmUtils.DAYPRODUCTIN_MONEY_HUM);
                }

                break;
            case R.id.switch_account_tv:// 更换支付方式
                if (payInfoList == null || payInfoList.size() <= 0) {
                    ToastUtil.show(RegularPurchaseFirstActivity.this, "请稍后再试");
                    return;
                }
                showPopup();
                break;
            case R.id.purchase_submit_bt:
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(pay_bt, InputMethodManager.SHOW_FORCED);
                imm.hideSoftInputFromWindow(pay_bt.getWindowToken(), 0); // 强制隐藏键盘
                if (!CommonUtil.isFastDoubleClick()) {
                    if (productCommonModel.getName().equals("定期")) {
                        UmUtils.customsEvent(RegularPurchaseFirstActivity.this, UmUtils.REGULARIN_CHECK_CLICK, UmUtils.REGULARIN_CHECK_HUM);
                    } else {
                        UmUtils.customsEvent(RegularPurchaseFirstActivity.this, UmUtils.DAYPRODUCTIN_CHECK_CLICK, UmUtils.DAYPRODUCTIN_CHECK_HUM);
                    }

                    pay_bt.setClickable(false);
                    purchase = purchase_et.getText().toString();
                    if (purchase.equals("")) {
                        ToastUtil.show(this, getResources().getString(R.string.please_input) + getResources().getString(R.string.purchase_no));
                    } else {
                        if (Double.parseDouble(purchase) >= PURCHASE_MIN_MONEY) {
                            try {
                                if (isBalance) {
                                    if (Double.parseDouble(purchase) > putMoney) {
                                        if (payInfo.getGoods_type().equals("3")) {
                                            ToastUtil.show(this, "活期账户金额不足");
                                        } else if (payInfo.getGoods_type().equals(
                                                "2")) {
                                            ToastUtil.show(this, "余额账户金额不足");
                                        } else {
                                            ToastUtil.show(this, "当前账户金额不足");
                                        }
                                        showPopup();
                                        return;
                                    }
                                }
                                showPopwindow();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            ToastUtil.show(this, "建议转入" + PURCHASE_MIN_MONEY + "元以上金额");
                        }
                    }
                }
                break;
            default:
                break;
        }
    }

    private Handler myHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    try {
                        showPopwindow();
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * 先判断网络是否可用，可用跳转转入说明
     */
    private void gotoIntro() {
        if (null != url && !url.equals("")) {
            Intent intent = new Intent(this, PurchaseLimitNoteActivity.class);
            intent.putExtra("type", "1");
            intent.putExtra("url", url);
            startActivity(intent);
        } else {
            ToastUtil.show(RegularPurchaseFirstActivity.this, "请稍后再试");
        }
    }

    /**
     * 自定义密码盘的popwindow @author Ysw created at 2017/3/15 10:56
     */
    private void showPopwindow() {
        scollFull(1);
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.layout_paypassword, null);
        pw = (EditText) view.findViewById(R.id.eta_pwd);
        close_keyboard = (ImageView) view.findViewById(R.id.close_keyboard);//
        keyboard_parts = (LinearLayout) view.findViewById(R.id.keyboard_parts);//
        loading_page = (RelativeLayout) view.findViewById(R.id.loading_page);//
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
                if (productCommonModel.getName().equals("定期")) {
                    UmUtils.customsEvent(RegularPurchaseFirstActivity.this, UmUtils.REGULARIN_PASSWORDCLOSE_CLICK,
                            UmUtils.REGULARIN_PASSWORDCLOSE_NUM);
                } else {
                    UmUtils.customsEvent(RegularPurchaseFirstActivity.this, UmUtils.DAYPRODUCTIN_PASSWORDCLOSE_CLICK,
                            UmUtils.DAYPRODUCTIN_PASSWORDCLOSE_NUM);
                }
                scollFull(0);
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
                scollFull(0);
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
                        purchase = purchase_et.getText().toString();
                        sendNewCurrentOut();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        pay_bt.setClickable(true);
        pw.addTextChangedListener(textWatcher);
    }

    /**
     * 自定义短信验证的popwindow @author Ysw created at 2017/3/15 10:57
     */
    private void showVerificationPopwindow() {
        scollFull(1);
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
                if (productCommonModel.getName().equals("定期")) {
                    UmUtils.customsEvent(RegularPurchaseFirstActivity.this, UmUtils.REGULARIN_MESSAGE_CLICK, UmUtils.REGULARIN_MESSAGE_NUM);
                } else {
                    UmUtils.customsEvent(RegularPurchaseFirstActivity.this, UmUtils.DAYPRODUCTIN_MESSAGE_CLICK, UmUtils.DAYPRODUCTIN_MESSAGE_NUM);
                }
                scollFull(0);
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
                scollFull(0);
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
        handler = new Handler() {
            public void handleMessage(android.os.Message msg) {
                pw.setText(strContent);
            }
        };
        send_code.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                sendCode(true);
            }
        });
    }

    /**
     * 选择支付方式 @author Ysw created at 2017/3/15 10:58
     */
    private void showPopup() {
        final String pushMoney = purchase_et.getText().toString();
        if (pushMoney.equals("") || Double.valueOf(pushMoney) < PURCHASE_MIN_MONEY) {
            minMoney = PURCHASE_MIN_MONEY;
        } else {
            minMoney = Double.valueOf(pushMoney);
        }
        View view = LayoutInflater.from(RegularPurchaseFirstActivity.this).inflate(R.layout.choose_payment_terms, null);
        listView = (ListView) view.findViewById(R.id.pay_list);
        listView.setOverScrollMode(View.OVER_SCROLL_NEVER);// 防止魅族手机出现HOLD悬停
        adapter = new PayListAdapter(this, payInfoList, minMoney);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                PayInfo minPayInfo = payInfoList.get(position);
                if (minPayInfo.getMoney() == null) {
                    ToastUtil.show(RegularPurchaseFirstActivity.this, "网络不给力，请稍后再试");
                    return;
                }
                if (minMoney > Double.valueOf(minPayInfo.getMoney())) {
                    return;
                }
                if (pushMoney.equals("")) {
                    ToastUtil.show(RegularPurchaseFirstActivity.this, "请输入金额");
                    return;
                }
                if (Double.valueOf(pushMoney) < minMoney) {
                    ToastUtil.show(RegularPurchaseFirstActivity.this, "输入金额不能小于最低起投");
                    return;
                }
                payInfo = payInfoList.get(position);
                putMoney = Double.valueOf(payInfo.getMoney());
                tv_time_in.setText(payInfo.getExpected_time());
                switch_account_hint.setText(payInfo.getUse_description());
                if (payInfo.getGoods_type().equals("1")) {
                    isBalance = false;
                    popupWindow.dismiss();
                    limit_note_btn.setVisibility(View.VISIBLE);
                    limitTv.setVisibility(View.VISIBLE);
                    setColorTV(limitTv, 8, 16, instance.getResources().getColor(R.color.event_dialog_button));
                    if (!pushMoney.equals("") && Double.parseDouble(pushMoney) > 0 && Double.parseDouble(pushMoney) <= 50000) {
                        showPopwindow();
                    }
                    if (!pushMoney.equals("")) {
                        if (Double.parseDouble(pushMoney) > 50000) {
                            ToastUtil.show(RegularPurchaseFirstActivity.this, "已超过限额");
                            purchase_et.setText("50000");
                        }
                    }
                } else {
                    isBalance = true;
                    popupWindow.dismiss();
                    limitTv.setVisibility(View.GONE);
                    limit_note_btn.setVisibility(View.GONE);
                    if (!purchase_et.getText().toString().equals("") && Double.parseDouble(purchase_et.getText().toString()) > 0) {
                        if (Double.valueOf(purchase_et.getText().toString()) <= putMoney) {
                            showPopwindow();
                        }
                    }
                }
            }
        });
        ImageView cancelBtn = (ImageView) view.findViewById(R.id.cancel_btn);
        popupWindow = new PopupWindow(view, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, true);
        // 在PopupWindow里面就加上下面代码，让键盘弹出时，不会挡住pop窗口。
        popupWindow.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
        popupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        // 实例化一个ColorDrawable（不起作用）
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

        // 添加pop窗口关闭事件，以便pop关闭时恢复背景为全透明
        popupWindow.setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss() {
                backgroundAlpha(1f);
            }
        });

        pay_bt.setClickable(true);
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
        obj.put("uid", user_id);
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
                ToastUtil.show(RegularPurchaseFirstActivity.this, R.string.default_error);
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
                        }, 4500);
                    } else if (obj.getInt("code") == 3001) {
                        Message msg = new Message();
                        msg.what = REGULAR_MSG_KEYBOARD_VALIDATE_ERROR_SHOW;
                        Pophandler.sendMessage(msg);
                        pw.setText("");
                        ToastUtil.show(getApplication(), obj.getString("msg"));
                    } else {
                        Message msg = new Message();
                        msg.what = REGULAR_MSG_KEYBOARD_VALIDATE_SHOW;
                        Pophandler.sendMessage(msg);
                        ToastUtil.show(getApplication(), obj.getString("msg"));
                    }

                } catch (Exception e) {
                    Message msg = new Message();
                    msg.what = REGULAR_MSG_KEYBOARD_VALIDATE_SHOW;
                    Pophandler.sendMessage(msg);
                }
            }
        });
    }

    private void getPayList() throws Exception {
        JSONObject obj = new JSONObject();
        obj.put("uid", user_id);
        obj.put("goods_type", cProjectMode.getGoods_type());
        obj.put("pay_list_type", cProjectMode.getPay_list_type());
        RequestParams params = new RequestParams();
        String desStr = MyDES.encrypt(obj.toString());
        params.put("cipher", desStr);
        MyhttpClient.desPost(UrlConfig.PAY_LIST, params, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                showWaittingDialog(RegularPurchaseFirstActivity.this);
                super.onStart();
            }

            @Override
            public void onFinish() {
                super.onFinish();
                dismissWaittingDialog();
            }

            @Override
            public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
                super.onFailure(arg0, arg1, arg2, arg3);
                ToastUtil.show(RegularPurchaseFirstActivity.this, R.string.default_error);

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
                    JSONObject obj = new JSONObject(response);
                    String cipher = obj.getString("cipher");
                    String desStr = MyDES.decrypt(cipher);
                    if (TextUtils.isEmpty(desStr))
                        return;
                    JSONObject jsonObject = new JSONObject(desStr);
                    if (jsonObject.getInt("code") == 1000) {
                        BaseModel<PayInfoList> result = new Gson().fromJson(desStr, new TypeToken<BaseModel<PayInfoList>>() {
                        }.getType());
                        if (result != null && result.getData().getList() != null) {
                            boolean isMin = true;
                            for (int i = 0; i < result.getData().getList().size(); i++) {
                                payInfoList.add(result.getData().getList().get(i));
                                if (Double.parseDouble(result.getData().getList().get(i).getMoney()) > PURCHASE_MIN_MONEY) {
                                    if (isMin) {
                                        payInfo = result.getData().getList().get(i);
                                        putMoney = Double.valueOf(payInfo.getMoney());
                                        isMin = false;
                                        isBalance = true;
                                        tv_time_in.setText(payInfo.getExpected_time());
                                        switch_account_hint.setText(payInfo.getUse_description());
                                        if (payInfo.getGoods_type().equals("1")) {
                                            isBalance = false;
                                            limit_note_btn.setVisibility(View.VISIBLE);
                                            limitTv.setVisibility(View.VISIBLE);
                                            setColorTV(limitTv, 8, 16, instance.getResources().getColor(R.color.event_dialog_button));
                                        }
                                    }
                                }
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
     * @throws Exception 限额接口
     */
    private void sendXzMoney() throws Exception {
        showWaittingDialog(RegularPurchaseFirstActivity.this);
        JSONObject obj = new JSONObject();
        obj.put("uid", user_id);
        obj.put("goods_type", cProjectMode.getGoods_type());
        RequestParams params = new RequestParams();
        String desStr = MyDES.encrypt(obj.toString());
        params.put("cipher", desStr);
        MyhttpClient.desPost(UrlConfig.BUY_LIMIT, params, new AsyncHttpResponseHandler() {

            @Override
            public void onFinish() {
                super.onFinish();
                dismissWaittingDialog();
            }

            @Override
            public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
                super.onFailure(arg0, arg1, arg2, arg3);
                ToastUtil.show(RegularPurchaseFirstActivity.this, R.string.default_error);

            }

            @Override
            public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
                super.onSuccess(arg0, arg1, arg2);
                String msg = "";
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
                        JSONObject dataObj = new JSONObject(obj.getString("data"));
                        limit_money = dataObj.getLong("money");
                        url = dataObj.getString("url");
                    } else {
                        msg = obj.getString("msg");
                        MyLog.i("msg", msg);
                        limit_money = 0;
                    }
                    limit_money_tv.setText("您的账户最多可转入" + limit_money + "元");

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * @throws Exception 产品详情页
     */
    private void sendCunbeiDetail() throws Exception {
        JSONObject obj = new JSONObject();
        obj.put("uid", PreferencesUtils.getValueFromSPMap(this, PreferencesUtils.Keys.UID));
        obj.put("assetid", productCommonModel.getAssetid());
        RequestParams params = new RequestParams();
        String desStr = MyDES.encrypt(obj.toString());
        params.put("cipher", desStr);
        MyhttpClient.desPost(UrlConfig.USER_DETAIL, params, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                showWaittingDialog(RegularPurchaseFirstActivity.this);
                super.onStart();
            }

            @Override
            public void onFinish() {
                super.onFinish();
                dismissWaittingDialog();
                try {
                    sendXzMoney();
                    getBindCardInfo();
                    sendAllMoney();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
                super.onFailure(arg0, arg1, arg2, arg3);
                ToastUtil.show(RegularPurchaseFirstActivity.this, R.string.default_error);
            }

            @Override
            public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
                super.onSuccess(arg0, arg1, arg2);
                String msg = "";
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
                        BaseModel<CommonProjectMode> result = new Gson().fromJson(desStr, new TypeToken<BaseModel<CommonProjectMode>>() {
                        }.getType());
                        if (result.isSuccess()) {
                            cProjectMode = result.getData();
                            if (cProjectMode == null)
                                return;
                            setTextStyle(cProjectMode.getInvestRateFinish());
                            tv_about_yesterday.setText(cProjectMode.getContext());
                            tv_content.setText(cProjectMode.getContent());
                            if (cProjectMode.getMin_amount() != null && !cProjectMode.getMin_amount().equals("")) {
                                PURCHASE_MIN_MONEY = Double.parseDouble(cProjectMode.getMin_amount());
                            } else {
                                PURCHASE_MIN_MONEY = 0.00;
                            }
                            purchase_et.setHint(PURCHASE_MIN_MONEY + "元起投");
                            pre_money_yield.setVisibility(View.VISIBLE);
                        }
                    } else {
                        msg = obj.getString("msg");
                        PURCHASE_MIN_MONEY = 0.00;
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void setTextStyle(String rate) {
        String strm[] = rate.split("~");
        if (strm.length <= 1) {
            tv_money_yield.setText(rate);
        } else {
            int length = strm[0].length();
            SpannableString rateText = new SpannableString(rate);
            rateText.setSpan(new TextAppearanceSpan(this, R.style.dayup_push_one), 0, length - 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            rateText.setSpan(new TextAppearanceSpan(this, R.style.dayup_push_two), length - 1, length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            rateText.setSpan(new TextAppearanceSpan(this, R.style.dayup_push_one), length, rateText.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            tv_money_yield.setText(rateText, TextView.BufferType.SPANNABLE);
        }
    }

    // 获取绑定卡的信息
    private void getBindCardInfo() throws Exception {
        RequestParams params = new RequestParams();
        UidObj obj = new UidObj(PreferencesUtils.getValueFromSPMap(RegularPurchaseFirstActivity.this, PreferencesUtils.Keys.UID));
        String desStr = MyDES.encrypt(new Gson().toJson(obj));
        params.put("cipher", desStr);
        MyhttpClient.desPost(UrlConfig.USER_BANKINFO, params, new AsyncHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                showWaittingDialog(RegularPurchaseFirstActivity.this);
            }

            @Override
            public void onFinish() {
                super.onFinish();
                dismissWaittingDialog();
            }

            @Override
            public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
                super.onFailure(arg0, arg1, arg2, arg3);
                ToastUtil.show(RegularPurchaseFirstActivity.this, R.string.default_error);
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
                    if (TextUtils.isEmpty(desc))
                        return;
                    BaseModel<UserBindinfo> result = new Gson().fromJson(desc, new TypeToken<BaseModel<UserBindinfo>>() {
                    }.getType());
                    if (result.isSuccess()) {
                        UserBindinfo data = result.getData();
                        if (data == null)
                            return;
                        String id = PreferencesUtils.getValueFromSPMap(RegularPurchaseFirstActivity.this, PreferencesUtils.Keys.BANK_CARD_ID);
                        String bank_no = PreferencesUtils.getValueFromSPMap(RegularPurchaseFirstActivity.this, PreferencesUtils.Keys.BANK_CARD_NO);
                        getPayList();// 支付列表
                        if (data.getId().equals(id) && data.getBank_no().equals(bank_no))
                            return;
                        JinrApp.instance().state_bankBind = Integer.valueOf(data.getState_bankBind());
                        JinrApp.instance().state_tradePassword = Integer.valueOf(data.getState_tradePassword());
                        PreferencesUtils.putIntToSPMap(RegularPurchaseFirstActivity.this, PreferencesUtils.Keys.IS_BIND_CARD, Integer.valueOf(data.getState_bankBind()));
                        PreferencesUtils.putIntToSPMap(RegularPurchaseFirstActivity.this, PreferencesUtils.Keys.IS_SET_TRADE_PWD, Integer.valueOf(data.getState_tradePassword()));
                        PreferencesUtils.putValueToSPMap(RegularPurchaseFirstActivity.this, PreferencesUtils.Keys.BANK_BG_URL, data.getBgimg());
                        PreferencesUtils.putValueToSPMap(RegularPurchaseFirstActivity.this, PreferencesUtils.Keys.BANK_CARD_ID, data.getId());
                        PreferencesUtils.putValueToSPMap(RegularPurchaseFirstActivity.this, PreferencesUtils.Keys.BANK_CARD_NO, data.getBank_no());
                        PreferencesUtils.putValueToSPMap(RegularPurchaseFirstActivity.this, PreferencesUtils.Keys.BANK_NAME, data.getBank_name());
                        PreferencesUtils.putValueToSPMap(RegularPurchaseFirstActivity.this, PreferencesUtils.Keys.USER_TEL, data.getUser_tel());
                        PreferencesUtils.putValueToSPMap(RegularPurchaseFirstActivity.this, PreferencesUtils.Keys.CARD_LAST, data.getCard_last());
                    } else {
                        ToastUtil.show(RegularPurchaseFirstActivity.this, result.getMsg());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void setAnimation(final Button view) {
        Animation anim = AnimationUtils.loadAnimation(RegularPurchaseFirstActivity.this, R.anim.top_in);
        anim.setFillAfter(true);
        anim.setAnimationListener(new AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                view.clearAnimation();
                Animation anim = AnimationUtils.loadAnimation(RegularPurchaseFirstActivity.this, R.anim.top_out);
                anim.setStartOffset(5000);
                view.startAnimation(anim);
                anim.setAnimationListener(new AnimationListener() {

                    @Override
                    public void onAnimationStart(Animation animation) {
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        view.clearAnimation();
                        view.setVisibility(View.GONE);
                    }
                });
            }
        });
    }

    /**
     * 设置添加屏幕的背景透明度
     *
     * @param bgAlpha
     */
    public void backgroundAlpha(float bgAlpha) {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = bgAlpha; // 0.0-1.0
        getWindow().setAttributes(lp);
    }

    /**
     * 发送验证码
     */
    public void sendCode(boolean isSend) {
        SendMobileCode.getInstance().sendValidateCode(send_code, RegularPurchaseFirstActivity.this, user_id,
                requesPay.getOrder_num(), isSend, new Back() {
                    @Override
                    public void sms(String result) {
                        if (result != null && "".equals(result) != true) {
                            send_code.setClickable(true);
                        }
                    }
                });
    }

    // 查询支付结果
    protected void sendPaymentResult() throws Exception {
        showWaittingDialog(RegularPurchaseFirstActivity.this);
        JSONObject obj = new JSONObject();
        obj.put("uid", user_id);
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
                ToastUtil.show(RegularPurchaseFirstActivity.this, R.string.default_error);
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
                        Intent intent = new Intent(RegularPurchaseFirstActivity.this, PurchaseResultActivity.class);
                        intent.putExtra("payResult", pModel.getData());
                        sendBroadcast(new Intent().setAction("action.refresh_actdetail"));
                        startActivity(intent);
                        finish();
                    } else {
                        String msgStr = jsonObject.getString("msg");
                        if (msgStr == null || msgStr.equals("")) {
                            ToastUtil.show(RegularPurchaseFirstActivity.this, "查询交易结果失败，请前往交易记录查询");
                            return;
                        }
                        ToastUtil.show(RegularPurchaseFirstActivity.this, msgStr);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 新的活期转定期
     *
     * @throws Exception
     */
    protected void sendNewCurrentOut() throws Exception {
        Message msg = new Message();
        msg.what = REGULAR_MSG_KEYBOARD_VALIDATE_HIDE;
        Pophandler.sendMessage(msg);
        JSONObject obj = new JSONObject();
        obj.put("money", String.format("%.2f", Double.parseDouble(purchase)));
        obj.put("buy_type", cProjectMode.getGoods_type());
        obj.put("pay_type", payInfo.getGoods_type());
        obj.put("product_code", productCommonModel.getCode());
        obj.put("product_id", productCommonModel.getAssetid());
        obj.put("buss_pwd", password);
        obj.put("uid", user_id);
        if (event_key != null && !event_key.equals("")) {
            obj.put("event_key", event_key);
        }
        obj.put("platform", "4");
        RequestParams params = new RequestParams();
        String desStr = MyDES.encrypt(obj.toString());
        params.put("cipher", desStr);
        MyhttpClient.desPost(UrlConfig.ORDER_CREATE, params, new AsyncHttpResponseHandler() {

            @Override
            public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
                close_keyboard.setClickable(true);
                super.onFailure(arg0, arg1, arg2, arg3);
                ToastUtil.show(RegularPurchaseFirstActivity.this, R.string.default_error);
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
                    if (response == null || response.equals(""))
                        return;
                    JSONObject jsonObject = new JSONObject(response);
                    String cipher = jsonObject.getString("cipher");
                    final String desStr = MyDES.decrypt(cipher);
                    if (TextUtils.isEmpty(desStr))
                        return;
                    final JSONObject obj = new JSONObject(desStr);
                    if (obj.getInt("code") == 3002) {
                        Message msg = new Message();
                        msg.what = REGULAR_MSG_KEYBOARD_VALIDATE_ERROR_SHOW;
                        Pophandler.sendMessage(msg);
                        pw.setText("");
                        ToastUtil.show(RegularPurchaseFirstActivity.this, obj.getString("msg"));
                        return;
                    }
                    Message msg1 = new Message();
                    msg1.what = REGULAR_MSG_KEYBOARD_VALIDATE_SHOW;
                    Pophandler.sendMessage(msg1);
                    if (obj.getInt("code") == 2001) {
                        ToastUtil.show(RegularPurchaseFirstActivity.this, obj.getString("msg"));
                        MainActivity.instance.getSystemNews();
                        new Handler().postDelayed(new Runnable() {
                            public void run() {
                                try {
                                    RegularPurchaseFirstActivity.this.finish();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }, 1000);
                        return;
                    }
                    BaseModel<PaymentRegularResultObj> result = new Gson().fromJson(desStr, new TypeToken<BaseModel<PaymentRegularResultObj>>() {
                    }.getType());
                    if (result.isSuccess()) {
                        requesPay = result.getData();
                        if (isBalance) {
                            Intent intent = new Intent(RegularPurchaseFirstActivity.this, PurchaseResultActivity.class);
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
                        ToastUtil.show(RegularPurchaseFirstActivity.this, msgStr);
                    }

                } catch (Exception e) {
                    Message msg = new Message();
                    msg.what = REGULAR_MSG_KEYBOARD_VALIDATE_SHOW;
                    Pophandler.sendMessage(msg);
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    // 是否显示3倍收益
    private void sendAllMoney() throws Exception {
        RequestParams params = new RequestParams();
        String uid = PreferencesUtils.getValueFromSPMap(RegularPurchaseFirstActivity.this, PreferencesUtils.Keys.UID);
        JSONObject cipher = new JSONObject();
        cipher.put(PreferencesUtils.Keys.UID, uid);
        cipher.put("productcode", productCommonModel.getCode());
        params.put("cipher", MyDES.encrypt(cipher.toString()));
        MyhttpClient.desPost(UrlConfig.USER_REGULARALLMONEY, params, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                super.onStart();
                showWaittingDialog(RegularPurchaseFirstActivity.this);
            }

            @Override
            public void onFinish() {
                super.onFinish();
                dismissWaittingDialog();
                pay_bt.setClickable(true);
            }

            @Override
            public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
                super.onFailure(arg0, arg1, arg2, arg3);
                ToastUtil.show(RegularPurchaseFirstActivity.this, R.string.default_error);

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
                        JSONObject jsonObj = obj.getJSONObject("data");
                        if (jsonObj.getInt("isopen") == 1) {
                            tv_yestday_money.setText(jsonObj.getString("content_three"));
                            tv_yestday_money.setVisibility(View.VISIBLE);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
