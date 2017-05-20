package com.jinr.core.transfer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jinr.core.MainActivity;
import com.jinr.core.R;
import com.jinr.core.base.BaseActivity;
import com.jinr.core.config.UrlConfig;
import com.jinr.core.more.CommonWebActivity;
import com.jinr.core.security.tradepwd.FindTradePwd;
import com.jinr.core.trade.purchase.PurchaseLimitNoteActivity;
import com.jinr.core.trade.purchase.PurchaseResultActivity;
import com.jinr.core.trade.purchase.product.KeyBoardListener;
import com.jinr.core.ui.CustomDialog2;
import com.jinr.core.ui.CustomDialog2.OnDialogViewClickListener;
import com.jinr.core.utils.CommonUtil;
import com.jinr.core.utils.KeyboardUtil;
import com.jinr.core.utils.LoadingDialog;
import com.jinr.core.utils.MyDES;
import com.jinr.core.utils.MyEditText;
import com.jinr.core.utils.MyLog;
import com.jinr.core.utils.MyhttpClient;
import com.jinr.core.utils.PreferencesUtils;
import com.jinr.core.utils.PreferencesUtils.Keys;
import com.jinr.core.utils.ToastUtil;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLDecoder;

import model.BaseModel;
import model.CardReelMode;
import model.PaymentRegularResultObj;
import model.RegularRecord;

public class TransferActivity extends BaseActivity implements OnClickListener, OnDialogViewClickListener, KeyBoardListener.SoftkeyBoardListener {
    private TextView tv_protocol;
    private View view;
    private EditText pw;
    private ImageView close_keyboard;
    private LinearLayout keyboard_parts;
    private RelativeLayout loading_page;
    private ProgressBar loadingBar;
    private TextView forgot_passwordTextView;
    private PopupWindow window;
    final static int MSG_KEYBOARD_SHOW = 3;
    final static int MSG_KEYBOARD_HIDE = 4;
    final static int REGULAR_MSG_KEYBOARD_VALIDATE_ERROR_SHOW = 5;
    private String user_id;
    private RegularRecord mRegular;
    private LinearLayout parent;
    private CustomDialog2 mNoticeDialog;
    private LinearLayout ll_secondTransfer;
    private TextView tv_order;
    private TextView tv_capital;
    private TextView tv_income;
    private TextView tv_oldCapital;
    private TextView tv_oldIncome;
    private MyEditText et_discountMoney;
    private TextView tv_price;
    private TextView tv_fees;
    private TextView tv_include;
    private TextView tv_title;
    private TextView tv_days;
    private Button bt_sure;
    private ImageView image_back;
    private ImageView image_notice;
    private ImageView image_noticeTwo;
    private ImageView image_noticeThree;
    private ImageView image_noticeFour;
    private RelativeLayout rl_coupon;
    private TextView tv_coupon;
    private String mCoupon_id = "0";
    private String mCardReelUrl;
    private LoadingDialog mLoadingdialog;
    private KeyBoardListener mKeyBoardKListener;
    private boolean isCoupon = true;
    private Handler handler = new Handler();

    private Handler Pophandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_KEYBOARD_HIDE:// 隐藏键盘，开始验证密码
                    close_keyboard.setClickable(false);
                    keyboard_parts.setVisibility(View.INVISIBLE);
                    loading_page.setVisibility(View.VISIBLE);
                    loadingBar.setVisibility(View.VISIBLE);
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE | WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        setContentView(R.layout.activity_transfer);
        mRegular = (RegularRecord) getIntent().getExtras().get("regular");
        MyLog.e(TAG, "TransferActivity.onCreate：" + mRegular.toString());
        findViewById();
        initData();
        initUI();
        setListener();
        registerReceiver();
    }

    @Override
    protected void findViewById() {
        image_back = (ImageView) findViewById(R.id.title_left_img);
        tv_title = (TextView) findViewById(R.id.title_center_text);//标题
        tv_protocol = (TextView) findViewById(R.id.tv_protocol);//转让说明
        tv_order = (TextView) findViewById(R.id.tv_order);//单号
        tv_days = (TextView) findViewById(R.id.tv_days);//已持有几天
        tv_capital = (TextView) findViewById(R.id.tv_capital);//原始本金
        tv_income = (TextView) findViewById(R.id.tv_income);//已得收益
        tv_oldCapital = (TextView) findViewById(R.id.tv_oldcapital);//原购买金额
        tv_oldIncome = (TextView) findViewById(R.id.tv_oldincome);//原购买已得收益
        et_discountMoney = (MyEditText) findViewById(R.id.et_discountmoney);//折让金额即降价金额
        tv_price = (TextView) findViewById(R.id.tv_price);//出让金额即转让金额
        tv_fees = (TextView) findViewById(R.id.tv_fees);//手续费tv_
        tv_include = (TextView) findViewById(R.id.tv_include);//预计到账金额
        bt_sure = (Button) findViewById(R.id.bt_sure);//确定按钮
        parent = (LinearLayout) findViewById(R.id.parent);// 其他空白
        image_notice = (ImageView) findViewById(R.id.image_notice);//旁边的小问号
        image_noticeTwo = (ImageView) findViewById(R.id.image_noticeTwo);//旁边的小问号
        image_noticeThree = (ImageView) findViewById(R.id.image_noticeThree);//旁边的小问号
        image_noticeFour = (ImageView) findViewById(R.id.image_noticeFour);//旁边的小问号
        ll_secondTransfer = (LinearLayout) findViewById(R.id.ll_secondtransfer);//二次转让的时候才会有的布局
        rl_coupon = (RelativeLayout) findViewById(R.id.rl_coupon);//卡卷布局
        tv_coupon = (TextView) findViewById(R.id.tv_coupon);//卡卷描述
        mKeyBoardKListener = (KeyBoardListener) findViewById(R.id.mKeyBoardKListener);//卡卷描述
        mNoticeDialog = new CustomDialog2(this, R.layout.dialog_common_confirm, new int[]{R.id.dialog_confirm});//创建一些名词解释的dialog
        parent.setOnClickListener(this);
        image_back.setOnClickListener(this);
        tv_protocol.setOnClickListener(this);
        image_notice.setOnClickListener(this);
        image_noticeTwo.setOnClickListener(this);
        image_noticeThree.setOnClickListener(this);
        image_noticeFour.setOnClickListener(this);
        rl_coupon.setOnClickListener(this);
        mNoticeDialog.setOnDialogViewClickListener(this);
    }

    @Override
    protected void initData() {
        user_id = PreferencesUtils.getValueFromSPMap(this, Keys.UID);
    }

    @Override
    protected void initUI() {
        tv_title.setText("转让");
        tv_protocol.setText("转让说明");
        tv_protocol.setVisibility(View.VISIBLE);
        tv_order.setText(mRegular.getContractNum());
        tv_days.setText(mRegular.getTips());
        tv_oldCapital.setText(mRegular.getTransferMoney());
        tv_oldIncome.setText(mRegular.getDiscountMoney());
        tv_capital.setText(mRegular.getInvestMoney());
        tv_income.setText(mRegular.getInvestEarnings());
        // 判断是否是二次转让 @author Ysw created at 2017/2/22 11:27
        if ("yes".equals(mRegular.getBuyFromTransfer())) {
            ll_secondTransfer.setVisibility(View.VISIBLE);
        } else {
            ll_secondTransfer.setVisibility(View.GONE);

        }
        bt_sure.setBackgroundResource(R.drawable.btn_gray_bg);
        setHint();
    }

    @Override
    protected void setListener() {
        mKeyBoardKListener.setSoftKeyBoardListener(this);
        // EditText的输入变化监听 @author Ysw created at 2017/2/22 12:35
        et_discountMoney.addTextChangedListener(new TextWatcher() {
            String mAfterStr = "";
            String mBeforeStr = "";

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mAfterStr = s.toString();
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                mBeforeStr = s.toString();
            }

            @Override
            public void afterTextChanged(Editable s) {
                if ("".equals(s.toString()) && mBeforeStr.equals(mAfterStr)) {
                    setHint();
                    return;
                } else if ("".equals(s.toString()) || mBeforeStr.equals(mAfterStr)) {
                    setHint();
                    return;
                }
                // 判断用户是否直接点击小数点,index的起始值!=1即可 @author Ysw created at 2017/2/22 16:19
                int index = 100;
                if (mAfterStr.equals("")) {
                    setHint();
                    return;
                } else if (mAfterStr.contains(".")) {
                    index = mAfterStr.indexOf(".");
                    if (index == 0) {
                        s.clear();
                        s.append("0.");
                        mAfterStr = s.toString();
                    }
                    // 限制输入的小数为两位小数 @author Ysw created at 2017/2/22 16:19
                    if (mAfterStr.length() >= (index + 3)) {
                        mAfterStr = mAfterStr.substring(0, index + 3);
                    }
                    et_discountMoney.setText(mAfterStr);
                    et_discountMoney.setSelection(mAfterStr.length());
                }
            }
        });
    }

    /**
     * 计算折让金额范围 @author Ysw created at 2017/2/22 11:31
     */
    public float getReduceMoney() {
        BigDecimal max = new BigDecimal(mRegular.getMaxTransferAmt());
        BigDecimal min = new BigDecimal(mRegular.getMinTransferAmt());
        float reduceMoney = max.subtract(min).floatValue();
        return reduceMoney;
    }

    /**
     * 设置每个输入点的提示输入 @author Ysw created at 2017/2/22 12:50
     */
    public void setHint() {
        et_discountMoney.setHint(mRegular.getMinTransferAmt() + "~" + mRegular.getMaxTransferAmt());
        tv_price.setText("");
        tv_fees.setText("");
        tv_include.setText("");
        tv_price.setHint("当前本息-转让价格");
        tv_fees.setHint("转让金额*" + 100 * Double.parseDouble(mRegular.getServerAmountRate()) + "%");
        tv_include.setHint("0.00");
        bt_sure.setOnClickListener(null);
        bt_sure.setBackgroundResource(R.drawable.btn_gray_bg);
    }

    /**
     * 设置其他三处跟随折让金变化而变化的显示 @author Ysw created at 2017/2/22 12:57
     */
    public void setOtherText(double resuceMoney) {
        BigDecimal max = new BigDecimal(mRegular.getMaxTransferAmt());
        BigDecimal min = new BigDecimal(resuceMoney);
        float price = max.subtract(min).setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
        MyLog.e(TAG, "setOtherText: " + price);
        tv_price.setText("" + String.format("%.2f", price).replace("-", ""));
        tv_fees.setText("" + String.format("%.2f", (resuceMoney * 0.002)));
        tv_include.setText("" + String.format("%.2f", (resuceMoney * 0.998)));
        bt_sure.setOnClickListener(TransferActivity.this);
        bt_sure.setBackgroundResource(R.drawable.btn_bg_select);
    }

    /**
     * 关闭软键盘 @author Ysw created at 2017/2/21 15:52
     */
    private void closeKeyBoard() {
        InputMethodManager imm = (InputMethodManager) et_discountMoney.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm.isActive()) {
            imm.hideSoftInputFromWindow(et_discountMoney.getWindowToken(), 0);
        }
        et_discountMoney.clearFocus();
//        et_discountMoney.setFocusable(false);
        et_discountMoney.setClearIconVisible(false);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_sure:
                if (CommonUtil.isFastDoubleClick()) return;
                showPopwindow();
                break;
            case R.id.title_left_img:
                finish();
                break;
            case R.id.tv_protocol:
                Intent intent2 = new Intent(this, CommonWebActivity.class);
                intent2.putExtra("titleName", "转让说明");
                String url = UrlConfig.IP_V + UrlConfig.DAILYGAIN_TRANSINFO;
                intent2.putExtra("url", url);
                startActivity(intent2);
                break;
            case R.id.parent:
                closeKeyBoard();
                break;
            case R.id.rl_coupon:
                jumpToCoupon();
                break;
            case R.id.image_notice:
                if (!mNoticeDialog.isShowing()) {
                    mNoticeDialog.show();
                    mNoticeDialog.setContent(R.id.dialog_title, "当前可转让价格");
                    mNoticeDialog.setContent(R.id.dialog_content, getResources().getString(R.string.dialog_glossary1));
                }
                break;
            case R.id.image_noticeTwo:
                if (!mNoticeDialog.isShowing()) {
                    mNoticeDialog.show();
                    mNoticeDialog.setContent(R.id.dialog_title, "当前降价金额");
                    mNoticeDialog.setContent(R.id.dialog_content, getResources().getString(R.string.dialog_glossary2));
                }
                break;
            case R.id.image_noticeThree:
                if (!mNoticeDialog.isShowing()) {
                    mNoticeDialog.show();
                    mNoticeDialog.setContent(R.id.dialog_title, "预计手续费");
                    mNoticeDialog.setContent(R.id.dialog_content, getResources().getString(R.string.dialog_glossary3));
                }
                break;
            case R.id.image_noticeFour:
                if (!mNoticeDialog.isShowing()) {
                    mNoticeDialog.show();
                    mNoticeDialog.setContent(R.id.dialog_title, "预计到账金额");
                    mNoticeDialog.setContent(R.id.dialog_content, getResources().getString(R.string.dialog_glossary4));
                }
                break;
            default:
                break;
        }
    }

    /**
     * 新需求 自定义密码盘的popwindow
     */
    private void showPopwindow() {
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
                window.dismiss();
            }
        });
        new KeyboardUtil(view, window, this, pw);
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

            // 输入密码6位之后handler发送关闭 @author Ysw created at 2017/2/22 16:56
            @Override
            public void afterTextChanged(Editable s) {
                s = pw.getText();
                if (s.length() >= 6) {
                    String passwd = pw.getText().toString();
                    try {
                        Message message = new Message();
                        message.what = MSG_KEYBOARD_HIDE;
                        Pophandler.sendMessage(message);
                        CreateOrder(passwd);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        pw.addTextChangedListener(textWatcher);
    }

    /**
     * 创建转让订单 @author Ysw created at 2017/4/5 20:30
     */
    private void CreateOrder(String pwd) throws Exception {
        showWaittingDialog(TransferActivity.this);
        JSONObject obj = new JSONObject();
        obj.put("uid", user_id);
        obj.put("buss_pwd", pwd);
        obj.put("goods_type", mRegular.getGoods_type());
        obj.put("ori_order_id", mRegular.getOrderId());
        obj.put("product_code", mRegular.getProduct_code());
        obj.put("platform", 4);
        obj.put("transfer_money", et_discountMoney.getText().toString());
        obj.put("poundage", tv_fees.getText().toString());
        obj.put("coupon_id", mCoupon_id = mCoupon_id.equals("-1") ? "0" : mCoupon_id);
        obj.put("product_id", mRegular.getAssertsId());
        RequestParams params = new RequestParams();
        String desStr = MyDES.encrypt(obj.toString());
        params.put("cipher", desStr);
        MyhttpClient.desPost(UrlConfig.TRANSFER_BILL, params, new AsyncHttpResponseHandler() {
            @Override
            public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
                close_keyboard.setClickable(true);
                super.onFailure(arg0, arg1, arg2, arg3);
                dismissWaittingDialog();
                ToastUtil.show(TransferActivity.this, getResources().getString(R.string.default_error));
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
                    BaseModel<PaymentRegularResultObj> result = new Gson().fromJson(desStr, new TypeToken<BaseModel<PaymentRegularResultObj>>() {
                    }.getType());
                    if (result.isSuccess()) {
                        sendBroadcast(new Intent().setAction("action.refresh_actdetail"));
                        Intent intent = new Intent(TransferActivity.this, PurchaseResultActivity.class);
                        intent.putExtra("payResult", result.getData());
                        startActivity(intent);
                        Message msg = new Message();
                        msg.what = MSG_KEYBOARD_SHOW;
                        Pophandler.sendMessage(msg);
                    } else if (result.getCode() == 3002) {
                        pw.setText("");
                        Message msg = new Message();
                        msg.what = REGULAR_MSG_KEYBOARD_VALIDATE_ERROR_SHOW;
                        Pophandler.sendMessage(msg);
                        ToastUtil.show(TransferActivity.this, result.getMsg());
                    } else {
                        Message msg = new Message();
                        msg.what = MSG_KEYBOARD_SHOW;
                        Pophandler.sendMessage(msg);
                        ToastUtil.show(TransferActivity.this, result.getMsg());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    // 此广播的作用:刷新重新进入本页，如8.88活动页登陆成功和转入成功返回 @author Ysw created at 2017/2/22 16:57
    private BroadcastReceiver mRefreshBroadcastReceiver = new BroadcastReceiver() {
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

    // 点击其他地方，键盘关闭
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN && getCurrentFocus() != null && getCurrentFocus().getWindowToken() != null) {
            View v = getCurrentFocus();
            if (isShouldHideKeyboard(v, event)) {
                hideKeyboard(v.getWindowToken());
            }
        }
        return super.dispatchTouchEvent(event);
    }

    private boolean isShouldHideKeyboard(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText)) {
            int[] l = {0, 0};
            v.getLocationOnScreen(l);
            int left = l[0], top = l[1], bottom = top + v.getHeight(), right = left + v.getWidth();
            if (event.getRawX() > left && event.getRawX() < right && event.getRawY() > top && event.getRawY() < bottom) {
                return false;
            } else {
                return true;
            }
        }
        return false;
    }

    /**
     * 跳转至卡卷界面 @author Ysw created at 2017/3/30 20:26
     */
    private void jumpToCoupon() {
        if (mRegular != null) {
            String uid = "";
            try {
                uid = MyDES.encrypt(user_id);
            } catch (Exception e) {
                e.printStackTrace();
            }
            mCardReelUrl = UrlConfig.IP_V + UrlConfig.USER_COUPON + uid + "&coupon_id=" + mCoupon_id + "&money="
                    + et_discountMoney.getText().toString().trim() + "&product_num=" + mRegular.getProduct_num()
                    + "&randow=2561" + "&poundage=" + tv_fees.getText().toString().trim();
            Intent intent = new Intent(this, PurchaseLimitNoteActivity.class);
            intent.putExtra("type", "2");
            intent.putExtra("url", mCardReelUrl);
            startActivityForResult(intent, 2);
        }
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
            String couponMoney = split[2];//金额：有可能是固定值，有可能是一个比例值
            String couponUse_mode = split[3];//卡卷类型 1加息券、2抵扣券、3免手续费券
            String couponSelected_name = split[4];
            mCoupon_id = couponId;
            try {
                couponSelected_name = URLDecoder.decode(couponSelected_name, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            if (null != couponId && !couponId.equals("-1")) {
                isCoupon = true;
                String couponMoney_type = split[5];//金额类型：是用来区别金额是固定值还是比例值的。1是固定值、2是比例值。
                MyLog.e(TAG, "couponMoney_type: " + couponMoney_type);
                tv_coupon.setText(couponSelected_name);
                tv_coupon.setTextColor(Color.parseColor("#f37f72"));
                if (couponUse_mode.equals("3")) {
                    if ("1".equals(couponMoney_type)) {//减免手续费固定金额
                        Double money = Double.valueOf(couponMoney);
                        money = Double.valueOf(tv_fees.getText().toString().trim()) - money;
                        money = money > 0.0f ? money : 0.00f;
//                        tv_fees.setText(String.format("%.2f", money));
                        tv_include.setText(String.format("%.2f", Double.valueOf(et_discountMoney.getText().toString().trim()) - money));
                        tv_coupon.setText(couponSelected_name);
                        tv_coupon.setTextColor(Color.parseColor("#f37f72"));
                    } else if ("2".equals(couponMoney_type)) {//手续费比例
                        Double money = Double.valueOf(couponMoney) / 100f * Double.valueOf(tv_fees.getText().toString().trim());
                        money = Double.valueOf(tv_fees.getText().toString().trim()) - money;
                        money = money > 0.0f ? money : 0.00f;
//                        tv_fees.setText(String.format("%.2f", money));
                        tv_include.setText(String.format("%.2f", Double.valueOf(et_discountMoney.getText().toString().trim()) - money));
                        tv_coupon.setText(couponSelected_name);
                        tv_coupon.setTextColor(Color.parseColor("#f37f72"));
                    }
                }
            } else {
                isCoupon = false;
                tv_coupon.setText(couponSelected_name);
                tv_coupon.setTextColor(Color.parseColor("#f37f72"));
                if ("".equals(et_discountMoney.getText().toString().trim())) return;
                setOtherText(Float.valueOf(et_discountMoney.getText().toString()));
            }
        }
    }

    /**
     * 获取InputMethodManager，隐藏软键盘
     */
    private void hideKeyboard(IBinder token) {
        if (token != null) {
            InputMethodManager mInputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            mInputMethodManager.hideSoftInputFromWindow(token, InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    /**
     * Dialog确定按钮点击的回调 @author Ysw created at 2017/2/22 12:32
     */
    @Override
    public void OnDialogViewClick(CustomDialog2 dialog, View view) {
        dialog.dismiss();
        et_discountMoney.setFocusable(true);
        et_discountMoney.setFocusableInTouchMode(true);
        et_discountMoney.requestFocus();
        et_discountMoney.findFocus();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mRefreshBroadcastReceiver);
    }

    @Override
    public void keyBoardListen(boolean isShow) {
        if (!isShow) {
            if ("".equals(et_discountMoney.getText().toString())) return;
//            float rwduceMoney = Float.valueOf(et_discountMoney.getText().toString());
//            float maxMoney = Float.valueOf(mRegular.getMaxTransferAmt());
//            float minMoney = Float.valueOf(mRegular.getMinTransferAmt());
            Double rwduceMoney = new BigDecimal(Double.valueOf(et_discountMoney.getText().toString())).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
            Double maxMoney = new BigDecimal(Double.valueOf(mRegular.getMaxTransferAmt())).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
            Double minMoney = new BigDecimal(Double.valueOf(mRegular.getMinTransferAmt())).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
            if (rwduceMoney >= minMoney && rwduceMoney <= maxMoney) {
                setOtherText(rwduceMoney);
            } else if (rwduceMoney < minMoney) {
                et_discountMoney.setText("" + minMoney);
                setOtherText(minMoney);
            } else if (rwduceMoney > maxMoney) {
                et_discountMoney.setText("" + maxMoney);
                setOtherText(maxMoney);
            }
            try {
                if (isCoupon) {
                    getCoupon();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 获取卡卷最优质 @author Ysw created at 2017/3/30 10:26
     */
    private void getCoupon() throws Exception {
        JSONObject obj = new JSONObject();
        obj.put("uid", user_id);
        obj.put("money", et_discountMoney.getText().toString().trim());
        obj.put("product_num", mRegular.getProduct_num());
        obj.put("mCoupon_id", mCoupon_id);
        obj.put("poundage", "0");
        // 没有输入金额的时候不进行接口请求 @author Ysw created at 2017/3/31 11:55
        if ("".equals(et_discountMoney.getText().toString().trim())) return;
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
                ToastUtil.show(TransferActivity.this, R.string.default_error);
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
                        String coupon_type = model.getCoupon_type();
                        if ("3".equals(coupon_type)) {
                            if (model.getUse_mode().equals("1")) {//减免手续费固定金额
                                Double money = Double.valueOf(model.getMoney());
                                money = Double.valueOf(tv_fees.getText().toString().trim()) - money;
                                money = money > 0.0f ? money : 0.00f;
                                // tv_fees.setText(String.format("%.2f", money));
                                tv_include.setText(String.format("%.2f", Double.valueOf(et_discountMoney.getText().toString().trim()) - money));
                                tv_coupon.setText(model.getSelected_name());
                                tv_coupon.setTextColor(Color.parseColor("#f37f72"));
                            } else if (model.getUse_mode().equals("2")) {//手续费比例
                                Double money = Double.valueOf(model.getMoney()) / 100f * Double.valueOf(tv_fees.getText().toString().trim());
                                money = Double.valueOf(tv_fees.getText().toString().trim()) - money;
                                money = money > 0.0f ? money : 0.00f;
                                // tv_fees.setText(String.format("%.2f", money));
                                tv_include.setText(String.format("%.2f", Double.valueOf(et_discountMoney.getText().toString().trim()) - money));
                                tv_coupon.setText(model.getSelected_name());
                                tv_coupon.setTextColor(Color.parseColor("#f37f72"));
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
