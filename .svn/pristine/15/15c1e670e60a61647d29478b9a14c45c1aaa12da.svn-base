package com.jinr.core.trade.purchase;

import android.app.ActionBar.LayoutParams;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.Html;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
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
import com.jinr.core.config.EventBusKey;
import com.jinr.core.config.MessageWhat;
import com.jinr.core.config.UrlConfig;
import com.jinr.core.security.tradepwd.FindTradePwd;
import com.jinr.core.security.tradepwd.SetTradePwdActivity;
import com.jinr.core.trade.PayListAdapter;
import com.jinr.core.trade.purchase.product.KeyBoardListener;
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
import com.jinr.core.utils.UmUtils;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.apache.http.Header;
import org.json.JSONObject;
import org.simple.eventbus.EventBus;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.net.URLDecoder;
import java.text.DecimalFormat;
import java.util.ArrayList;

import model.BaseModel;
import model.CardReelMode;
import model.CommonProjectMode;
import model.PayInfo;
import model.PayInfoList;
import model.PaymentRegularResultObj;
import model.ProductCommonModel;
import model.UidObj;
import model.UserBindinfo;

/**
 * 活期专用转入页面
 *
 * @author Administrator
 */
public class CurrentPurchaseFirstActivity extends BaseActivity implements OnClickListener, KeyBoardListener.SoftkeyBoardListener {
    public static final String TAG = "CurrentPurchaseFirstActivity";
    private ImageView title_left_img; // title左边图片
    private TextView title_center_text; // title标题
    private TextView puchase_tips_tv;// 50000限额字符串
    private Button send_code;
    private ScrollView mScrollView;
    private String purchase = "0";
    private String user_id = "";
    private Double putMoney = 0.00;
    private Double minMoney = 0.00;
    private ProductCommonModel productCommonModel;
    private NewCustomDialogNoTitleFinish dialog_bind_card;
    private NewCustomDialogNoTitleFinish dialog_set_passwd;
    public static CurrentPurchaseFirstActivity instance = null;
    private PopupWindow window;
    private View view;
    private View cview;
    private TextView forgot_passwordTextView;
    final static int REGULAR_MSG_KEYBOARD_HIDE = 2;
    final static int REGULAR_MSG_KEYBOARD_VALIDATE_SHOW = 3;
    final static int REGULAR_MSG_KEYBOARD_VALIDATE_HIDE = 4;
    final static int REGULAR_MSG_KEYBOARD_VALIDATE_ERROR_SHOW = 5;
    private LinearLayout keyboard_parts;
    private RelativeLayout loading_page;
    private ProgressBar loadingBar;
    private ImageView close_keyboard;
    private EditText pw;// 密码输入框
    private KeyboardUtil keyboardUtil;
    private PopupWindow popupWindow;//选择支付方式
    private boolean isBalance = true;
    private String mBankName;
    private String mCardLast;
    private PaymentRegularResultObj requesPay;
    private BaseModel<PaymentRegularResultObj> pModel;
    private String strContent;
    private String event_key = "";
    private long mLimitMoney;
    private String url;
    private String mCardReelUrl;
    private String mCoupon_id = "0";
    private Double PURCHASE_MIN_MONEY = 100.00;//起投金额
    private ArrayList<PayInfo> payInfoList = new ArrayList<>();
    private ListView listView;
    private PayListAdapter adapter;
    private CommonProjectMode mProjectMode;
    private PayInfo payInfo;
    private String password;
    private boolean isUseingCoupon = true;
    private TextView tv_protocol;
    private TextView tv_expect;
    private TextView tv_improve;
    private TextView tv_rate;
    private TextView tv_depict;
    private TextView tv_card;
    private ImageView image_card;
    private TextView tv_limit;
    private TextView tv_expectTime;
    private TextView tv_actual;
    private RelativeLayout rl_turnin;
    private RelativeLayout rl_current;
    private RelativeLayout rl_product;
    private TextView tv_improve_two;
    private TextView tv_minRate;
    private TextView tv_upRate;
    private TextView tv_maxRate;
    private TextView tv_depict_two;
    private EditText et_money;
    private RelativeLayout rl_changePayType;
    private LoadingDialog mLoadingdialog;
    private String mName;
    private RelativeLayout rl_bottom;
    private TextView tv_min;
    private TextView tv_max;
    private TextView tv_title;
    private RelativeLayout rl_coupon;
    private KeyBoardListener mKeyBoardKListener;
    private TextView tv_coupon;
    private boolean isShow;
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
    private Handler Pophandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
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
                    rl_turnin.setOnClickListener(CurrentPurchaseFirstActivity.this);
                    close_keyboard.setClickable(false);
                    keyboard_parts.setVisibility(View.INVISIBLE);
                    loading_page.setVisibility(View.VISIBLE);
                    loadingBar.setVisibility(View.VISIBLE);
                    break;
                case REGULAR_MSG_KEYBOARD_VALIDATE_ERROR_SHOW:
                    rl_turnin.setOnClickListener(CurrentPurchaseFirstActivity.this);
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


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE | WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        setContentView(R.layout.regular_purchase_first);
        productCommonModel = (ProductCommonModel) getIntent().getSerializableExtra("regular");
        mLoadingdialog = new LoadingDialog(this);
        instance = CurrentPurchaseFirstActivity.this;
        findViewById();
        initUI();
        initData();
        setListener();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 显示银行卡信息
        user_id = PreferencesUtils.getValueFromSPMap(this, PreferencesUtils.Keys.UID);
        if (!isBalance) {
            mBankName = PreferencesUtils.getValueFromSPMap(this, PreferencesUtils.Keys.BANK_NAME);
            mCardLast = PreferencesUtils.getValueFromSPMap(this, PreferencesUtils.Keys.CARD_LAST);
        }
        // 未绑定银行卡或未设置交易密码之后dialog重新生成
        showDialog();
    }

    @Override
    protected void findViewById() {
        title_left_img = (ImageView) findViewById(R.id.title_left_img);
        title_center_text = (TextView) findViewById(R.id.title_center_text);
        tv_protocol = (TextView) findViewById(R.id.tv_protocol);
        rl_current = (RelativeLayout) findViewById(R.id.rl_current);//活期使用的界面
        tv_expect = (TextView) findViewById(R.id.tv_expect);
        tv_improve = (TextView) findViewById(R.id.tv_improve);
        tv_rate = (TextView) findViewById(R.id.tv_rate);
        tv_depict = (TextView) findViewById(R.id.tv_depict);

        rl_product = (RelativeLayout) findViewById(R.id.rl_product);//定期、日增息、保险箱使用的界面
        tv_improve_two = (TextView) findViewById(R.id.tv_improve_two);
        tv_minRate = (TextView) findViewById(R.id.tv_minRate);
        tv_upRate = (TextView) findViewById(R.id.tv_upRate);
        tv_maxRate = (TextView) findViewById(R.id.tv_maxRate);
        tv_min = (TextView) findViewById(R.id.tv_min);
        tv_max = (TextView) findViewById(R.id.tv_max);
        tv_maxRate = (TextView) findViewById(R.id.tv_maxRate);
        tv_title = (TextView) findViewById(R.id.tv_title);//日增息、保险箱中的Title
        tv_depict_two = (TextView) findViewById(R.id.tv_depict_two);

        rl_changePayType = (RelativeLayout) findViewById(R.id.rl_changePayType);
        tv_card = (TextView) findViewById(R.id.tv_card);
        image_card = (ImageView) findViewById(R.id.image_card);
        tv_limit = (TextView) findViewById(R.id.tv_limit);
        tv_coupon = (TextView) findViewById(R.id.tv_coupon);
        tv_expectTime = (TextView) findViewById(R.id.tv_expectTime);
        tv_actual = (TextView) findViewById(R.id.tv_actual);
        et_money = (EditText) findViewById(R.id.et_money);
        rl_turnin = (RelativeLayout) findViewById(R.id.rl_turnin);
        rl_bottom = (RelativeLayout) findViewById(R.id.rl_bottom);
        rl_coupon = (RelativeLayout) findViewById(R.id.rl_coupon);
        mScrollView = (ScrollView) findViewById(R.id.scroll_view_redresable);
        mKeyBoardKListener = (KeyBoardListener) findViewById(R.id.mKeyBoardKListener);
    }


    @Override
    protected void initUI() {
        mName = productCommonModel.getName();
        if (mName.contains("详情")) mName = mName.replace("详情", "");
        title_center_text.setText("转入" + mName);
        tv_protocol.setText("限额说明");
        tv_protocol.setVisibility(View.GONE);
        if (mName != null && (mName.equals("活期") || mName.equals("定期")) ||
                mName != null && (mName.equals("活期详情") || mName.equals("定期详情"))) {
            rl_current.setVisibility(View.VISIBLE);
            rl_product.setVisibility(View.GONE);
        } else {
            rl_current.setVisibility(View.GONE);
            rl_product.setVisibility(View.VISIBLE);
        }
        initDialog();
    }

    @Override
    protected void initData() {
        if (null != productCommonModel && null != productCommonModel.getEventKey())
            event_key = productCommonModel.getEventKey();
        mBankName = PreferencesUtils.getValueFromSPMap(this, PreferencesUtils.Keys.BANK_NAME);
        mCardLast = PreferencesUtils.getValueFromSPMap(this, PreferencesUtils.Keys.CARD_LAST);
        user_id = PreferencesUtils.getValueFromSPMap(this, PreferencesUtils.Keys.UID);
        try {
            getProductDetail();
            getMultipleNetWork();
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }

    @Override
    protected void setListener() {
        title_left_img.setOnClickListener(this);
        tv_protocol.setOnClickListener(this);
        rl_changePayType.setOnClickListener(this);
        rl_turnin.setOnClickListener(this);
        et_money.setOnClickListener(this);
        rl_coupon.setOnClickListener(this);
        mKeyBoardKListener.setSoftKeyBoardListener(this);
        et_money.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int arg1, int arg2, int arg3) {
                if (s.length() > 0) {
                    long input_money = Long.parseLong(s.toString());
                    if (s.toString().startsWith("0")) et_money.setText("");
                    if (mLimitMoney != 0) {
                        if (mLimitMoney < input_money) {
                            et_money.setText(String.valueOf(mLimitMoney));
                            et_money.setSelection(s.length() - 1);
                            rl_turnin.setOnClickListener(CurrentPurchaseFirstActivity.this);
                            rl_turnin.setBackgroundColor(getResources().getColor(R.color.blue_btn_bg));
                        } else if (mLimitMoney > input_money && input_money >= PURCHASE_MIN_MONEY) {
                            rl_turnin.setOnClickListener(CurrentPurchaseFirstActivity.this);
                            rl_turnin.setBackgroundColor(getResources().getColor(R.color.blue_btn_bg));
                        } else {
                            rl_turnin.setOnClickListener(null);
                            rl_turnin.setBackgroundColor(getResources().getColor(R.color.gray_bg_btn));
                        }
                    } else {
                        ToastUtil.show(CurrentPurchaseFirstActivity.this, "已超过限额");
                        et_money.setText("");
                        rl_turnin.setOnClickListener(null);
                        rl_turnin.setBackgroundColor(getResources().getColor(R.color.gray_bg_btn));
                    }
                    if (isBalance) {
                        if (Double.parseDouble(s.toString()) > mLimitMoney) et_money.setText(mLimitMoney + "");
                    } else {
                        if (Double.parseDouble(s.toString()) > 50000) et_money.setText("50000");
                    }
                    et_money.setSelection(et_money.getText().length());
                    tv_actual.setText(et_money.getText());
                } else {
                    rl_turnin.setOnClickListener(null);
                    rl_turnin.setBackgroundColor(getResources().getColor(R.color.gray_bg_btn));
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int arg1, int arg2, int arg3) {
            }

            @Override
            public void afterTextChanged(Editable arg0) {

            }
        });
        et_money.setOnEditorActionListener(new EditText.OnEditorActionListener() {
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


    private void initDialog() {
        dialog_bind_card = new NewCustomDialogNoTitleFinish(this, this.getResources().getString(R.string.bind_card_notice));
        dialog_bind_card.btn_custom_dialog_sure.setText(this.getResources().getString(R.string.set_now_btn));
        dialog_bind_card.btn_custom_dialog_sure.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog_bind_card.dismiss();
                if (!PreferencesUtils.getValueFromSPMap(CurrentPurchaseFirstActivity.this, PreferencesUtils.Keys.NAME).equals("")
                        && !PreferencesUtils.getValueFromSPMap(CurrentPurchaseFirstActivity.this, PreferencesUtils.Keys.ID_CARD).equals("")) {
                    Intent intent = new Intent(CurrentPurchaseFirstActivity.this, SecondBandCardActivity.class);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(CurrentPurchaseFirstActivity.this, AddBankActivity.class);
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
                Intent intent = new Intent(CurrentPurchaseFirstActivity.this, SetTradePwdActivity.class);
                CurrentPurchaseFirstActivity.this.startActivity(intent);
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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_left_img:
                PreferencesUtils.Keys.BACK_TO_ACT = 0;
                PreferencesUtils.Keys.BACK_TO_GIFT = false;
                finish();
                break;
            case R.id.rl_coupon:
                jumpToCoupon();
                break;
            case R.id.tv_protocol:
                gotoIntro();
                break;
            case R.id.rl_changePayType:
                if (payInfoList == null || payInfoList.size() <= 0) {
                    ToastUtil.show(CurrentPurchaseFirstActivity.this, "请稍后再试");
                    return;
                }
                if (isShow) {
                    CommonUtil.hideKeyboard(this);
                } else {
                    choosePayStyle();
                }
                break;
            case R.id.rl_turnin:
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(rl_turnin, InputMethodManager.SHOW_FORCED);
                imm.hideSoftInputFromWindow(rl_turnin.getWindowToken(), 0); // 强制隐藏键盘
                if (!CommonUtil.isFastDoubleClick()) {
                    if (productCommonModel.getName().equals("定期")) {
                        UmUtils.customsEvent(CurrentPurchaseFirstActivity.this, UmUtils.REGULARIN_CHECK_CLICK, UmUtils.REGULARIN_CHECK_HUM);
                    } else {
                        UmUtils.customsEvent(CurrentPurchaseFirstActivity.this, UmUtils.DAYPRODUCTIN_CHECK_CLICK, UmUtils.DAYPRODUCTIN_CHECK_HUM);
                    }
                    rl_turnin.setOnClickListener(null);
                    purchase = et_money.getText().toString().trim();
                    if (purchase.equals("")) {
                        ToastUtil.show(this, getResources().getString(R.string.please_input) + getResources().getString(R.string.purchase_no));
                    } else {
                        if (Double.parseDouble(purchase) >= PURCHASE_MIN_MONEY) {
                            try {
                                if (isBalance) {
                                    if (Double.parseDouble(tv_actual.getText().toString().replace("元", "")) > putMoney) {
                                        if (payInfo.getGoods_type().equals("3")) {
                                            ToastUtil.show(this, "活期账户金额不足");
                                        } else if (payInfo.getGoods_type().equals("2")) {
                                            ToastUtil.show(this, "余额账户金额不足");
                                        } else {
                                            ToastUtil.show(this, "当前账户金额不足");
                                        }
                                        choosePayStyle();
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

    /**
     * 跳转至卡卷界面 @author Ysw created at 2017/3/30 20:26
     */
    private void jumpToCoupon() {
        if (mProjectMode != null) {
            String uid = "";
            try {
                uid = MyDES.encrypt(user_id);
            } catch (Exception e) {
                e.printStackTrace();
            }
            mCardReelUrl = UrlConfig.IP_V + UrlConfig.USER_COUPON + uid + "&coupon_id=" + mCoupon_id + "&money="
                    + et_money.getText().toString().trim() + "&product_num=" + mProjectMode.getProduct_num() + "&randow=2561";
            Intent intent = new Intent(this, PurchaseLimitNoteActivity.class);
            intent.putExtra("type", "2");
            intent.putExtra("url", mCardReelUrl);
            startActivityForResult(intent, 2);
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
     * 先判断网络是否可用，进入限额说明界面
     */
    private void gotoIntro() {
        if (null != url && !url.equals("")) {
            Intent intent = new Intent(this, PurchaseLimitNoteActivity.class);
            intent.putExtra("type", "1");
            intent.putExtra("url", url);
            startActivity(intent);
        } else {
            ToastUtil.show(CurrentPurchaseFirstActivity.this, "请稍后再试");
        }
    }

    /**
     * 自定义密码盘的popwindow @author Ysw created at 2017/3/15 10:34
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
        // 这三个控件是显示支付金额和购买产品名 @author Ysw created at 2017/3/31 17:14
        RelativeLayout rl_info = (RelativeLayout) view.findViewById(R.id.rl_info);
        TextView tv_payMoney = (TextView) view.findViewById(R.id.tv_payMoney);
        TextView tv_productName = (TextView) view.findViewById(R.id.tv_productName);
        rl_info.setVisibility(View.GONE);
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
        forgot_passwordTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_real_info = new Intent(MainActivity.instance, FindTradePwd.class);
                startActivity(intent_real_info);
            }
        });
        close_keyboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (productCommonModel.getName().equals("定期")) {
                    UmUtils.customsEvent(CurrentPurchaseFirstActivity.this, UmUtils.REGULARIN_PASSWORDCLOSE_CLICK, UmUtils.REGULARIN_PASSWORDCLOSE_NUM);
                } else {
                    UmUtils.customsEvent(CurrentPurchaseFirstActivity.this, UmUtils.DAYPRODUCTIN_PASSWORDCLOSE_CLICK, UmUtils.DAYPRODUCTIN_PASSWORDCLOSE_NUM);
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

            @Override
            public void afterTextChanged(Editable s) {
                s = pw.getText();
                if (s.length() >= 6) {
                    password = pw.getText().toString();
                    try {
                        purchase = et_money.getText().toString();
                        CreateOrder();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        rl_turnin.setOnClickListener(CurrentPurchaseFirstActivity.this);
        pw.addTextChangedListener(textWatcher);
        //购买产品信息
        rl_info.setVisibility(View.VISIBLE);
        tv_payMoney.setText("¥" + new DecimalFormat("#####0.00").format(Double.parseDouble(tv_actual.getText().toString().replace("元", ""))));
        tv_productName.setText(mName);
    }

    /**
     * 选择支付方式 @author Ysw created at 2017/3/30 10:46
     */
    private void choosePayStyle() {
        final String pushMoney = (TextUtils.isEmpty(et_money.getText().toString()) ? "0" : et_money.getText().toString());
        if (pushMoney.equals("") || Double.valueOf(pushMoney) < PURCHASE_MIN_MONEY) {
            minMoney = PURCHASE_MIN_MONEY;
        } else {
            minMoney = Double.valueOf(tv_actual.getText().toString().replace("元", ""));
        }
        View view = LayoutInflater.from(CurrentPurchaseFirstActivity.this).inflate(R.layout.choose_payment_terms, null);
        listView = (ListView) view.findViewById(R.id.pay_list);
        listView.setOverScrollMode(View.OVER_SCROLL_NEVER);// 防止魅族手机出现HOLD悬停
        adapter = new PayListAdapter(this, payInfoList, minMoney);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                PayInfo minPayInfo = payInfoList.get(position);
                if (minPayInfo.getMoney() == null) {
                    ToastUtil.show(CurrentPurchaseFirstActivity.this, "网络不给力，请稍后再试");
                    return;
                }
                if (minPayInfo.getGoods_type().equals("1")) {
                    payInfo = minPayInfo;
                    putMoney = Double.valueOf(minPayInfo.getMoney());
                    tv_expectTime.setText(minPayInfo.getExpected_time());
                    isBalance = false;
                    popupWindow.dismiss();
                    tv_card.setText(minPayInfo.getTitle());
                    image_card.setVisibility(View.VISIBLE);
                    ImageLoader.getInstance().displayImage(minPayInfo.getImg(), image_card);
                    if (Double.parseDouble(pushMoney) > 50000) {
                        et_money.setText("50000");
                        try {
                            getCoupon();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    tv_protocol.setVisibility(View.VISIBLE);
                } else if (minMoney > Double.valueOf(minPayInfo.getMoney())) {//置灰不可点击
                    return;
                } else {
                    payInfo = minPayInfo;
                    putMoney = Double.valueOf(minPayInfo.getMoney());
                    tv_expectTime.setText(minPayInfo.getExpected_time());
                    isBalance = true;
                    popupWindow.dismiss();
                    tv_card.setText(minPayInfo.getTitle());
                    image_card.setVisibility(View.GONE);
                    tv_protocol.setVisibility(View.GONE);
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
        rl_turnin.setOnClickListener(CurrentPurchaseFirstActivity.this);
    }


    /**
     * 获取支付列表 @author Ysw created at 2017/3/30 10:26
     */
    private void getPayList() throws Exception {
        JSONObject obj = new JSONObject();
        obj.put("uid", user_id);
        obj.put("goods_type", mProjectMode.getGoods_type());
        obj.put("pay_list_type", mProjectMode.getPay_list_type());
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
                ToastUtil.show(CurrentPurchaseFirstActivity.this, R.string.default_error);
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
                    BaseModel<PayInfoList> result = new Gson().fromJson(desStr, new TypeToken<BaseModel<PayInfoList>>() {
                    }.getType());
                    if (result.isSuccess()) {
                        boolean isMin = true;
                        for (int i = 0; i < result.getData().getList().size(); i++) {
                            payInfoList.add(result.getData().getList().get(i));
                            if (Double.parseDouble(result.getData().getList().get(i).getMoney()) > PURCHASE_MIN_MONEY) {
                                if (isMin) {
                                    payInfo = result.getData().getList().get(i);
                                    putMoney = Double.valueOf(payInfo.getMoney());
                                    isMin = false;
                                    isBalance = true;
                                    tv_expectTime.setText(payInfo.getExpected_time());
                                    tv_card.setText(payInfo.getTitle());
                                    if (payInfo.getGoods_type().equals("1")) {
                                        tv_protocol.setVisibility(View.VISIBLE);
                                        image_card.setVisibility(View.VISIBLE);
                                        ImageLoader.getInstance().displayImage(payInfo.getImg(), image_card);
                                        isBalance = false;
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
     * 获取卡卷最优质 @author Ysw created at 2017/3/30 10:26
     */
    private void getCoupon() throws Exception {
        JSONObject obj = new JSONObject();
        obj.put("uid", user_id);
        obj.put("money", et_money.getText().toString().trim());
        obj.put("product_num", mProjectMode.getProduct_num());
        obj.put("product_num", mProjectMode.getProduct_num());
        // 没有输入金额的时候不进行接口请求 @author Ysw created at 2017/3/31 11:55
        if ("".equals(et_money.getText().toString().trim())) return;
        RequestParams params = new RequestParams();
        String desStr = MyDES.encrypt(obj.toString());
        params.put("cipher", desStr);
        MyhttpClient.desPost(UrlConfig.CARD_REEL, params, new AsyncHttpResponseHandler() {
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
                ToastUtil.show(CurrentPurchaseFirstActivity.this, R.string.default_error);
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
                    BaseModel<CardReelMode> result = new Gson().fromJson(desStr, new TypeToken<BaseModel<CardReelMode>>() {
                    }.getType());
                    if (result.isSuccess()) {
                        CardReelMode model = result.getData();
                        mCoupon_id = model.getId();
                        if (model.getUse_mode().equals("1")) {
                            Double money = Double.valueOf(et_money.getText().toString().trim());
                            money = money - Double.valueOf(model.getMoney()) > 0 ? money - Double.valueOf(model.getMoney()) : 0;
                            tv_actual.setText(new DecimalFormat("#####0.00").format(money) + "元");
                            tv_coupon.setText(model.getSelected_name());
                            tv_coupon.setTextColor(Color.parseColor("#f37f72"));
                        } else if (model.getUse_mode().equals("2")) {
                            Double money = Double.valueOf(et_money.getText().toString().trim());
                            tv_actual.setText(new DecimalFormat("#####0.00").format(money) + "元");
                            tv_coupon.setText(model.getSelected_name());
                            tv_coupon.setTextColor(Color.parseColor("#f37f72"));
                        } else {
                            Double money = Double.valueOf(et_money.getText().toString().trim());
                            tv_actual.setText(new DecimalFormat("#####0.00").format(money) + "元");
                            tv_coupon.setText(model.getSelected_name());
                            tv_coupon.setTextColor(Color.parseColor("#f37f72"));
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 限额接口 @author Ysw created at 2017/3/30 10:45
     */
    private void getLimitMoney() throws Exception {
        JSONObject obj = new JSONObject();
        obj.put("uid", user_id);
        obj.put("goods_type", mProjectMode.getGoods_type());
        RequestParams params = new RequestParams();
        String desStr = MyDES.encrypt(obj.toString());
        params.put("cipher", desStr);
        MyhttpClient.desPost(UrlConfig.BUY_LIMIT, params, new AsyncHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
            }

            @Override
            public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
                super.onFailure(arg0, arg1, arg2, arg3);
                ToastUtil.show(CurrentPurchaseFirstActivity.this, R.string.default_error);
            }

            @Override
            public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
                super.onSuccess(arg0, arg1, arg2);
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
                        JSONObject dataObj = new JSONObject(obj.getString("data"));
                        mLimitMoney = dataObj.getLong("money");
                        url = dataObj.getString("url");
                        MyLog.e(TAG, "onSuccess: " + url);
                    } else {
                        mLimitMoney = 0;
                    }
                    tv_limit.setText("您的账户本次最多可转入" + mLimitMoney + "元");
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
     * 产品详情页 @author Ysw created at 2017/3/30 10:45
     */
    private void getProductDetail() throws Exception {
        JSONObject obj = new JSONObject();
        obj.put("uid", PreferencesUtils.getValueFromSPMap(this, PreferencesUtils.Keys.UID));
        obj.put("assetid", productCommonModel.getAssetid());
        RequestParams params = new RequestParams();
        String desStr = MyDES.encrypt(obj.toString());
        params.put("cipher", desStr);
        MyhttpClient.desPost(UrlConfig.USER_DETAIL, params, new AsyncHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                mLoadingdialog.show();
            }

            @Override
            public void onFinish() {
                super.onFinish();
                mLoadingdialog.dismiss();
            }

            @Override
            public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
                super.onFailure(arg0, arg1, arg2, arg3);
                ToastUtil.show(CurrentPurchaseFirstActivity.this, R.string.default_error);
                mLoadingdialog.dismiss();
            }

            @Override
            public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
                super.onSuccess(arg0, arg1, arg2);
                mLoadingdialog.dismiss();
                try {
                    String response = new String(arg2, "UTF-8");
                    response = CommonUtil.transResponse(response);
                    if (response == null || response.equals("")) return;
                    JSONObject jsonObject = new JSONObject(response);
                    String cipher = jsonObject.getString("cipher");
                    String desStr = MyDES.decrypt(cipher);
                    if (TextUtils.isEmpty(desStr)) return;
                    BaseModel<CommonProjectMode> result = new Gson().fromJson(desStr, new TypeToken<BaseModel<CommonProjectMode>>() {
                    }.getType());
                    if (result.isSuccess()) {
                        mProjectMode = result.getData();
                        if (mProjectMode == null) return;
                        getLimitMoney();
                        getBindCardInfo();
                        reFreshUI();
                    } else {
                        PURCHASE_MIN_MONEY = 0.00;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void reFreshUI() {
        PURCHASE_MIN_MONEY = Double.parseDouble(mProjectMode.getMin_amount());
        et_money.setHint(PURCHASE_MIN_MONEY + "元起投");
        if (mProjectMode.getName().equals("活期") || mProjectMode.getName().equals("定期")
                || mProjectMode.getName().equals("活期详情") || mProjectMode.getName().equals("定期详情")) {
            tv_rate.setText(mProjectMode.getInvestRateFinish());
            tv_expect.setText(mProjectMode.getContent());
            tv_depict.setText(mProjectMode.getLower_txt());
        } else {
            tv_minRate.setText(mProjectMode.getInvest_rate());
            tv_maxRate.setText(mProjectMode.getInvest_rate_finish());
            tv_upRate.setText(mProjectMode.getRate());
            tv_max.setText(mProjectMode.getInvest_rate_finish_content());
            tv_min.setText(mProjectMode.getInvest_rate_content());
            tv_title.setText(mProjectMode.getTitle());
            tv_depict_two.setText(mProjectMode.getLower_txt());
        }
    }

    // 获取绑定卡的信息
    private void getBindCardInfo() throws Exception {
        RequestParams params = new RequestParams();
        UidObj obj = new UidObj(PreferencesUtils.getValueFromSPMap(CurrentPurchaseFirstActivity.this, PreferencesUtils.Keys.UID));
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
                ToastUtil.show(CurrentPurchaseFirstActivity.this, R.string.default_error);
            }

            @Override
            public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
                super.onSuccess(arg0, arg1, arg2);
                String response;
                try {
                    response = new String(arg2, "UTF-8");
                    JSONObject jsonObject = new JSONObject(response);
                    String cipher = jsonObject.getString("cipher");
                    String desStr = MyDES.decrypt(cipher);
                    if (TextUtils.isEmpty(desStr)) return;
                    BaseModel<UserBindinfo> result = new Gson().fromJson(desStr, new TypeToken<BaseModel<UserBindinfo>>() {
                    }.getType());
                    if (result.isSuccess()) {
                        UserBindinfo data = result.getData();
                        if (data == null) return;
                        String id = PreferencesUtils.getValueFromSPMap(CurrentPurchaseFirstActivity.this, PreferencesUtils.Keys.BANK_CARD_ID);
                        String bank_no = PreferencesUtils.getValueFromSPMap(CurrentPurchaseFirstActivity.this, PreferencesUtils.Keys.BANK_CARD_NO);
                        getPayList();// 支付列表
                        if (data.getId().equals(id) && data.getBank_no().equals(bank_no)) return;
                        JinrApp.instance().state_bankBind = Integer.valueOf(data.getState_bankBind());
                        JinrApp.instance().state_tradePassword = Integer.valueOf(data.getState_tradePassword());
                        PreferencesUtils.putIntToSPMap(CurrentPurchaseFirstActivity.this, PreferencesUtils.Keys.IS_BIND_CARD, Integer.valueOf(data.getState_bankBind()));
                        PreferencesUtils.putIntToSPMap(CurrentPurchaseFirstActivity.this, PreferencesUtils.Keys.IS_SET_TRADE_PWD, Integer.valueOf(data.getState_tradePassword()));
                        PreferencesUtils.putValueToSPMap(CurrentPurchaseFirstActivity.this, PreferencesUtils.Keys.BANK_BG_URL, data.getBgimg());
                        PreferencesUtils.putValueToSPMap(CurrentPurchaseFirstActivity.this, PreferencesUtils.Keys.BANK_CARD_ID, data.getId());
                        PreferencesUtils.putValueToSPMap(CurrentPurchaseFirstActivity.this, PreferencesUtils.Keys.BANK_CARD_NO, data.getBank_no());
                        PreferencesUtils.putValueToSPMap(CurrentPurchaseFirstActivity.this, PreferencesUtils.Keys.BANK_NAME, data.getBank_name());
                        PreferencesUtils.putValueToSPMap(CurrentPurchaseFirstActivity.this, PreferencesUtils.Keys.USER_TEL, data.getUser_tel());
                        PreferencesUtils.putValueToSPMap(CurrentPurchaseFirstActivity.this, PreferencesUtils.Keys.CARD_LAST, data.getCard_last());
                    } else {
                        ToastUtil.show(CurrentPurchaseFirstActivity.this, result.getMsg());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void setAnimation(final Button view) {
        Animation anim = AnimationUtils.loadAnimation(CurrentPurchaseFirstActivity.this, R.anim.top_in);
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
                Animation anim = AnimationUtils.loadAnimation(CurrentPurchaseFirstActivity.this, R.anim.top_out);
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
     * 创建订单 @author Ysw created at 2017/3/30 10:44
     */
    protected void CreateOrder() throws Exception {
        Message msg = new Message();
        msg.what = REGULAR_MSG_KEYBOARD_VALIDATE_HIDE;
        Pophandler.sendMessage(msg);
        JSONObject obj = new JSONObject();
        obj.put("money", String.format("%.2f", Double.parseDouble(purchase)));
        obj.put("buy_type", mProjectMode.getGoods_type());
        obj.put("pay_type", payInfo.getGoods_type());
        obj.put("product_code", productCommonModel.getCode());
        obj.put("product_id", productCommonModel.getAssetid());
        obj.put("buss_pwd", password);
        if (mCoupon_id.equals("-1")) mCoupon_id = "0";
        obj.put("coupon_id", mCoupon_id);
        obj.put("uid", user_id);
        if (event_key != null && !event_key.equals("")) obj.put("event_key", event_key);
        obj.put("platform", "4");
        RequestParams params = new RequestParams();
        String desStr = MyDES.encrypt(obj.toString());
        params.put("cipher", desStr);
        MyhttpClient.desPost(UrlConfig.ORDER_CREATE, params, new AsyncHttpResponseHandler() {
            @Override
            public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
                close_keyboard.setClickable(true);
                super.onFailure(arg0, arg1, arg2, arg3);
                ToastUtil.show(CurrentPurchaseFirstActivity.this, R.string.default_error);
                Message msg = new Message();
                msg.what = REGULAR_MSG_KEYBOARD_VALIDATE_SHOW;
                Pophandler.sendMessage(msg);
            }

            @Override
            public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
                super.onSuccess(arg0, arg1, arg2);
                try {
                    String response = new String(arg2, "UTF-8");
                    response = CommonUtil.transResponse(response);
                    if (response == null || response.equals("")) return;
                    JSONObject jsonObject = new JSONObject(response);
                    String cipher = jsonObject.getString("cipher");
                    final String desStr = MyDES.decrypt(cipher);
                    if (TextUtils.isEmpty(desStr)) return;
                    final JSONObject obj = new JSONObject(desStr);
                    if (obj.getInt("code") == 3002) {
                        Message msg = new Message();
                        msg.what = REGULAR_MSG_KEYBOARD_VALIDATE_ERROR_SHOW;
                        Pophandler.sendMessage(msg);
                        pw.setText("");
                        ToastUtil.show(CurrentPurchaseFirstActivity.this, obj.getString("msg"));
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
                            //通知ProductFragment刷新三倍收益
                            EventBus.getDefault().post(true, EventBusKey.REFRESH_MULTIPLE);
                            Intent intent = new Intent(CurrentPurchaseFirstActivity.this, PurchaseResultActivity.class);
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
                        ToastUtil.show(CurrentPurchaseFirstActivity.this, msgStr);
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

    /**
     * 三倍收益接口 @author Ysw created at 2017/3/30 12:47
     */
    private void getMultipleNetWork() throws Exception {
        RequestParams params = new RequestParams();
        String uid = PreferencesUtils.getValueFromSPMap(CurrentPurchaseFirstActivity.this, PreferencesUtils.Keys.UID);
        JSONObject cipher = new JSONObject();
        cipher.put(PreferencesUtils.Keys.UID, uid);
        cipher.put("productcode", productCommonModel.getCode());
        params.put("cipher", MyDES.encrypt(cipher.toString()));
        MyhttpClient.desPost(UrlConfig.USER_REGULARALLMONEY, params, new AsyncHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
            }

            @Override
            public void onFinish() {
                super.onFinish();
                rl_turnin.setOnClickListener(CurrentPurchaseFirstActivity.this);
            }

            @Override
            public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
                super.onFailure(arg0, arg1, arg2, arg3);
                ToastUtil.show(CurrentPurchaseFirstActivity.this, R.string.default_error);
            }

            @Override
            public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
                super.onSuccess(arg0, arg1, arg2);
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
                        JSONObject jsonObj = obj.getJSONObject("data");
                        if (jsonObj.getInt("isopen") == 1) {
                            if (mName.equals("活期") || mName.equals("定期")) {
                                tv_improve.setText(jsonObj.getString("content_three"));
                                tv_improve.setVisibility(View.VISIBLE);
                            } else {
                                tv_improve_two.setText(jsonObj.getString("content_three"));
                                tv_improve_two.setVisibility(View.VISIBLE);
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
     * 自定义短信验证的popwindow @author Ysw created at 2017/3/15 10:35
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
        close_keyboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (productCommonModel.getName().equals("定期")) {
                    UmUtils.customsEvent(CurrentPurchaseFirstActivity.this, UmUtils.REGULARIN_MESSAGE_CLICK, UmUtils.REGULARIN_MESSAGE_NUM);
                } else {
                    UmUtils.customsEvent(CurrentPurchaseFirstActivity.this, UmUtils.DAYPRODUCTIN_MESSAGE_CLICK, UmUtils.DAYPRODUCTIN_MESSAGE_NUM);
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
     * 支付验证 @author Ysw created at 2017/3/30 10:46
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
                ToastUtil.show(CurrentPurchaseFirstActivity.this, R.string.default_error);
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

    /**
     * 发送验证码
     */
    public void sendCode(boolean isSend) {
        SendMobileCode.getInstance().sendValidateCode(send_code, CurrentPurchaseFirstActivity.this, user_id,
                requesPay.getOrder_num(), isSend, new Back() {
                    @Override
                    public void sms(String result) {
                        if (result != null && !"".equals(result)) {
                            send_code.setClickable(true);
                        }
                    }
                });
    }

    /**
     * 查询支付结果 @author Ysw created at 2017/3/30 17:39
     */
    protected void sendPaymentResult() throws Exception {
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
                mLoadingdialog.dismiss();
                ToastUtil.show(CurrentPurchaseFirstActivity.this, R.string.default_error);
            }

            @Override
            public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
                super.onSuccess(arg0, arg1, arg2);
                mLoadingdialog.dismiss();
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
                        Intent intent = new Intent(CurrentPurchaseFirstActivity.this, PurchaseResultActivity.class);
                        intent.putExtra("payResult", pModel.getData());
                        sendBroadcast(new Intent().setAction("action.refresh_actdetail"));
                        startActivity(intent);
                        finish();
                    } else {
                        String msgStr = jsonObject.getString("msg");
                        if (msgStr == null || msgStr.equals("")) {
                            ToastUtil.show(CurrentPurchaseFirstActivity.this, "查询交易结果失败，请前往交易记录查询");
                            return;
                        }
                        ToastUtil.show(CurrentPurchaseFirstActivity.this, msgStr);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * 系统软键盘弹出监听 @author Ysw created at 2017/3/30 19:53
     */
    @Override
    public void keyBoardListen(boolean show) {
        this.isShow = show;
        if (isShow) {
            scollFull(1);
            rl_bottom.setVisibility(View.INVISIBLE);
            et_money.setSelection(et_money.getText().toString().trim().length());
        } else {
            scollFull(0);
            rl_bottom.setVisibility(View.VISIBLE);
            try {
                if (isUseingCoupon) {
                    getCoupon();
                } else {
                    Double money = Double.valueOf(et_money.getText().toString().trim());
                    tv_actual.setText(new DecimalFormat("#####0.00").format(money) + "元");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * ScrollView自动滑动顶部和底部，需要开启线程不能直接调用 @author Ysw created at 2017/3/30 20:09
     */
    private void scollFull(final int type) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (type == 0) {
                    mScrollView.fullScroll(ScrollView.FOCUS_UP);
                } else {
                    mScrollView.fullScroll(ScrollView.FOCUS_DOWN);
                }
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == 2) {
            setResult(data.getStringExtra("url"));
        }
    }

    /**
     * 卡卷界面WebView界面的js交互后的回调，注意需要解码 @author Ysw created at 2017/3/31 11:17
     */
    public void setResult(String url) {
        if (url != null && !"".equals(url)) {
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
                    String str = et_money.getText().toString().trim().equals("") ? "0" : et_money.getText().toString().trim();
                    Double money = Double.valueOf(str);
                    money = money - Double.valueOf(couponMoney) > 0 ? money - Double.valueOf(couponMoney) : 0;
                    tv_actual.setText(new DecimalFormat("#####0.00").format(money) + "元");
                } else {
                    String str = et_money.getText().toString().trim().equals("") ? "0" : et_money.getText().toString().trim();
                    tv_actual.setText(new DecimalFormat("#####0.00").format(Double.parseDouble(str)) + "元");
                }
                isUseingCoupon = true;
            } else {
                tv_coupon.setText(couponSelected_name);
                tv_coupon.setTextColor(Color.parseColor("#f37f72"));
                String str = et_money.getText().toString().trim().equals("") ? "0" : et_money.getText().toString().trim();
                if ("".equals(str)) return;
                tv_actual.setText(new DecimalFormat("#####0.00").format(Double.parseDouble(str)) + "元");
                isUseingCoupon = false;
            }
        }
    }
}
