package com.jinr.core.balance;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.InputType;
import android.text.Selection;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
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
import com.jinr.core.R;
import com.jinr.core.bankCard.AddBankActivity;
import com.jinr.core.bankCard.SecondBandCardActivity;
import com.jinr.core.base.BaseActivity;
import com.jinr.core.config.UrlConfig;
import com.jinr.core.more.CommonWebActivity;
import com.jinr.core.security.tradepwd.FindTradePwd;
import com.jinr.core.security.tradepwd.SetTradePwdActivity;
import com.jinr.core.ui.NewCustomDialogNoTitleFinish;
import com.jinr.core.utils.CommonUtil;
import com.jinr.core.utils.KeyboardUtil;
import com.jinr.core.utils.MyDES;
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

import java.lang.reflect.Method;

import model.BaseModel;
import model.OrderObj;
import model.OrderObj.OrderData;
import model.OrderResultObj;
import model.OrderResultObj.OrderResultData;
import model.UidObj;
import model.UserBindinfo;

/**
 * 充值界面 @author Ysw created at 2017/3/14 11:32
 */
public class BalanceInActivity extends BaseActivity implements OnClickListener {

    private TextView title_center_text;
    private TextView tv_protocol;
    private ImageView title_left_img;
    private ImageView iv_bank_logo;//银行logo
    private TextView tv_bank_name;//银行名称
    private TextView tv_bank_num;//银行卡号后四位
    private TextView tv_bank_quota;//银行充值限额
    private EditText ed_input_money;//充值输入框
    private Button btn_balance_in;//充值按钮
    private String uid;
    private String input_money;  //用户输入的金额
    //密码键盘 pw
    private View view;
    private EditText pw;
    private ImageView close_keyboard;
    private TextView forgot_passwordTextView;
    private NewCustomDialogNoTitleFinish dialog_bind_card;
    private NewCustomDialogNoTitleFinish dialog_set_passwd;
    private LinearLayout llay_bank_info;
    //验证码view
    private View cview;
    private Button send_code;
    private LinearLayout keyboard_parts;
    private RelativeLayout loading_page;
    private ProgressBar loadingBar;
    private KeyboardUtil keyboardUtil;
    private PopupWindow window;
    protected String order_num;//订单号
    protected String validate;//验证码
    protected String passwd;//支付密码
    Handler pwdhandler;
    // final static int REGULAR_MSG_CREATE_ORDER = 1;
    final static int REGULAR_MSG_KEYBOARD_HIDE = 2;
    final static int REGULAR_MSG_KEYBOARD_VALIDATE_SHOW = 3;
    final static int REGULAR_MSG_KEYBOARD_VALIDATE_HIDE = 4;
    final static int REGULAR_MSG_KEYBOARD_VALIDATE_ERROR_SHOW = 5;
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_balancein);
        findViewById();
        setListener();
        initData();
    }

    @Override
    protected void initData() {
        uid = PreferencesUtils.getValueFromSPMap(this, PreferencesUtils.Keys.UID);
        pwdhandler = new Handler() {
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
                            btn_balance_in.setClickable(true);
                            close_keyboard.setClickable(false);
                            keyboard_parts.setVisibility(View.INVISIBLE);
                            loading_page.setVisibility(View.VISIBLE);
                            loadingBar.setVisibility(View.VISIBLE);
                            break;
                        case REGULAR_MSG_KEYBOARD_VALIDATE_ERROR_SHOW:
                            btn_balance_in.setClickable(true);
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
    protected void onResume() {
        super.onResume();
        try {
            getBindCardInfo();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void findViewById() {
        llay_bank_info = (LinearLayout) findViewById(R.id.llay_bank_info);
        title_center_text = (TextView) findViewById(R.id.title_center_text);
        tv_protocol = (TextView) findViewById(R.id.tv_protocol);
        tv_bank_name = (TextView) findViewById(R.id.tv_bank_name);
        tv_bank_num = (TextView) findViewById(R.id.tv_bank_num);
        tv_bank_quota = (TextView) findViewById(R.id.tv_bank_quota);
        title_left_img = (ImageView) findViewById(R.id.title_left_img);
        iv_bank_logo = (ImageView) findViewById(R.id.iv_bank_logo);
        ed_input_money = (EditText) findViewById(R.id.ed_input_money);
        btn_balance_in = (Button) findViewById(R.id.btn_balance_in);
        tv_protocol.setVisibility(View.VISIBLE);
        tv_protocol.setText("说明");
        title_center_text.setText("余额充值");
        initDialog();
    }

    @Override
    protected void initUI() {

    }

    @Override
    protected void setListener() {
        title_left_img.setOnClickListener(this);
        tv_protocol.setOnClickListener(this);
        btn_balance_in.setOnClickListener(this);

        ed_input_money.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String ss = s + "";

                if (TextUtils.isEmpty(ss)) {
                    btn_balance_in.setEnabled(false);
                    return;
                }

                double inputDouble = Double.parseDouble(ss);
                //充值的金额大于0，可点按钮
                if (inputDouble > 0) {
                    btn_balance_in.setEnabled(true);
                } else {
                    btn_balance_in.setEnabled(false);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                String str = s.toString();

                if (TextUtils.isEmpty(str)) return;

                //用户输入的金额大于可用活期、设置成可用活期
                double inputDouble = Double.parseDouble(str);
                if (50000 < inputDouble) {
                    ed_input_money.setText("50000");
                    Editable text = ed_input_money.getText();
                    Selection.setSelection(text, text.length());
                }

                //0后面不是小数点、数字不让输
                if (str.startsWith("0")) {
                    if (str.length() > 1) {
                        ed_input_money.setText("0");
                    }
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_left_img:
                BalanceInActivity.this.finish();
                break;

            case R.id.tv_protocol:
                Intent intent_bal = new Intent(BalanceInActivity.this, CommonWebActivity.class);
                intent_bal.putExtra("url", UrlConfig.IP_R + UrlConfig.BALANCE_EXPLAIN);
                intent_bal.putExtra("titleName", "余额说明");
                startActivity(intent_bal);

                break;

            case R.id.btn_balance_in:
                hideSystemKeyBoard();
                input_money = ed_input_money.getText().toString().trim();
                showPopwindow();
                break;

            default:
                break;
        }
    }

    private void initDialog() {
        dialog_bind_card = new NewCustomDialogNoTitleFinish(this, this.getResources().getString(R.string.bind_card_notice));// 绑定银行卡提示
        dialog_bind_card.btn_custom_dialog_sure.setText(this.getResources().getString(R.string.set_now_btn));
        dialog_bind_card.btn_custom_dialog_sure.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog_bind_card.dismiss();
                if (!PreferencesUtils.getValueFromSPMap(BalanceInActivity.this, PreferencesUtils.Keys.NAME).equals("") &&
                        !PreferencesUtils.getValueFromSPMap(BalanceInActivity.this, PreferencesUtils.Keys.ID_CARD).equals("")) {
                    Intent intent = new Intent(BalanceInActivity.this, SecondBandCardActivity.class);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(BalanceInActivity.this, AddBankActivity.class);
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
                Intent intent = new Intent(BalanceInActivity.this, SetTradePwdActivity.class);
                BalanceInActivity.this.startActivity(intent);
            }
        });
    }

    /**
     * 获取绑卡信息，需要优化，不需要每次进入界面的时候都调用 @author Ysw created at 2017/3/14 11:35
     */
    private void getBindCardInfo() throws Exception {
        RequestParams params = new RequestParams();
        UidObj obj = new UidObj(uid);
        String desStr = MyDES.encrypt(new Gson().toJson(obj));
        params.put("cipher", desStr);
        MyhttpClient.desPost(UrlConfig.USER_BANKINFO, params, new AsyncHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                showWaittingDialog(BalanceInActivity.this);
            }

            @Override
            public void onFinish() {
                super.onFinish();
                dismissWaittingDialog();
            }

            @Override
            public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
                super.onFailure(arg0, arg1, arg2, arg3);
                ToastUtil.show(BalanceInActivity.this, R.string.default_error);
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
                        String state_bankBind = data.getState_bankBind();
                        String state_tradePassword = data.getState_tradePassword();
                        if (state_bankBind.equals("0")) {
                            dialog_bind_card.show();
                            return;
                        } else if (state_tradePassword.equals("0")) {
                            dialog_set_passwd.show();
                            return;
                        }
                        llay_bank_info.setVisibility(View.VISIBLE);
                        String mBankName = data.getBank_name();
                        String mBankNum = data.getCard_last();
                        String logourl = data.getImg();
                        tv_bank_name.setText(mBankName);
                        tv_bank_num.setText("(尾号" + mBankNum + ")");
                        tv_bank_quota.setText("限额5万");
                        // 下载logo
                        ImageLoader.getInstance().displayImage(logourl, iv_bank_logo);
                    } else {
                        llay_bank_info.setVisibility(View.GONE);
                        ToastUtil.show(getApplication(), result.getMsg());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 余额转出接口 @author Ysw created at 2017/3/14 11:36
     */
    protected void sendBalInRequest() throws Exception {
        Message msg = new Message();
        msg.what = REGULAR_MSG_KEYBOARD_VALIDATE_HIDE;
        pwdhandler.sendMessage(msg);
        RequestParams params = new RequestParams();
        JSONObject cipher = new JSONObject();
        cipher.put("buss_pwd", passwd);
        cipher.put("buy_order", "");
        cipher.put("buy_type", "2");
        cipher.put("client_ip", "");
        cipher.put("event_key", "");
        cipher.put("money", input_money);
        cipher.put("pay_type", "1");
        cipher.put("platform", "4");
        cipher.put("product_code", "");
        cipher.put("product_id", "");
        cipher.put("uid", uid);
        params.put("cipher", MyDES.encrypt(cipher.toString()));
        MyhttpClient.desPost(UrlConfig.ORDER_CREATE, params, new AsyncHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
            }

            @Override
            public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
                super.onFailure(arg0, arg1, arg2, arg3);
                ToastUtil.show(BalanceInActivity.this, R.string.default_error);
                Message msg = new Message();
                msg.what = REGULAR_MSG_KEYBOARD_VALIDATE_SHOW;
                pwdhandler.sendMessage(msg);
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
                        OrderObj orderObj = new Gson().fromJson(cipher, OrderObj.class);
                        if (orderObj != null) {
                            OrderData data = orderObj.data;
                            if (data != null) {
                                order_num = data.order_num;
                                window.dismiss();
                                Message msg = new Message();
                                msg.what = REGULAR_MSG_KEYBOARD_HIDE;
                                pwdhandler.sendMessage(msg);
                            }
                        }

                    } else if (code == 3002) {//密码输错，置空
                        Message msg = new Message();
                        msg.what = REGULAR_MSG_KEYBOARD_VALIDATE_ERROR_SHOW;
                        pwdhandler.sendMessage(msg);
                        pw.setText("");
                        ToastUtil.show(BalanceInActivity.this, object.getString("msg"));
                    } else {//关闭
                        ToastUtil.show(getApplication(), object.getString("msg"));
                        window.dismiss();
                    }
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
     * 支付验证接口 @author Ysw created at 2017/3/14 11:38
     */
    private void payValidateRequest(String validateCode) throws Exception {
        Message msg = new Message();
        msg.what = REGULAR_MSG_KEYBOARD_VALIDATE_HIDE;
        pwdhandler.sendMessage(msg);
        RequestParams params = new RequestParams();
        JSONObject cipher = new JSONObject();
        cipher.put("order_num", order_num);
        cipher.put("uid", uid);
        cipher.put("validate_code", validateCode);
        params.put("cipher", MyDES.encrypt(cipher.toString()));
        MyhttpClient.desPost(UrlConfig.PAY_VALIDATE, params, new AsyncHttpResponseHandler() {
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
                Message msg = new Message();
                msg.what = REGULAR_MSG_KEYBOARD_VALIDATE_SHOW;
                pwdhandler.sendMessage(msg);
                ToastUtil.show(BalanceInActivity.this, R.string.default_error);
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
                        OrderObj orderObj = new Gson().fromJson(cipher, OrderObj.class);
                        if (orderObj != null) {
                            final OrderData data = orderObj.data;
                            if (data != null) {
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            getBalInResult(data.order_num, data.buy_type, data.pay_type);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }, 4000);
                            }
                        }
                    } else if (code == 3001) {//验证码输错
                        Message msg = new Message();
                        msg.what = REGULAR_MSG_KEYBOARD_VALIDATE_ERROR_SHOW;
                        pwdhandler.sendMessage(msg);
                        pw.setText("");
                        ToastUtil.show(getApplication(), object.getString("msg"));
                    } else {
                        ToastUtil.show(getApplication(), object.getString("msg"));
                        window.dismiss();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 订单结果详情查询接口 @author Ysw created at 2017/3/14 11:39
     */
    private void getBalInResult(String order_num, String buy_type, String pay_type) throws Exception {
        RequestParams params = new RequestParams();
        JSONObject cipher = new JSONObject();
        cipher.put("order_num", order_num);
        cipher.put("buy_type", buy_type);
        cipher.put("pay_type", pay_type);
        cipher.put("uid", uid);
        params.put("cipher", MyDES.encrypt(cipher.toString()));
        MyhttpClient.desPost(UrlConfig.ORDER_BAL_OUT_RESULT, params, new AsyncHttpResponseHandler() {
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
                ToastUtil.show(BalanceInActivity.this, R.string.default_error);
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
                        OrderResultObj orderObj = new Gson().fromJson(cipher, OrderResultObj.class);
                        if (orderObj != null) {
                            OrderResultData data = orderObj.data;
                            if (data != null) {
                                Intent intent = new Intent(BalanceInActivity.this, BalanceInResultActivity.class);
                                intent.putExtra("create_time", data.create_time);
                                intent.putExtra("one_content", data.one_content);
                                intent.putExtra("expect_time", data.expect_time);
                                intent.putExtra("two_content", data.two_content);
                                intent.putExtra("status", data.status);
                                startActivity(intent);
                                window.dismiss();
                                BalanceInActivity.this.finish();
                            }
                        }
                    } else {
                        window.dismiss();
                        ToastUtil.show(getApplication(), object.getString("msg"));
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
        SendMobileCode.getInstance().sendValidateCode(send_code, BalanceInActivity.this, uid, order_num, isSend, new Back() {
            @Override
            public void sms(String result) {
                if (result != null && !"".equals(result)) {
                    send_code.setClickable(true);
                }
            }
        });
    }

    /**
     * 弹出自定义密码键盘popwindow @author Ysw created at 2017/3/14 11:48
     */
    private void showPopwindow() {
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
        window.setOutsideTouchable(true);
        // 设置popWindow的显示和消失动画
        window.setAnimationStyle(R.style.dialogWindowAnim);
        // 在底部显示
        window.showAtLocation(view, Gravity.BOTTOM, 0, 0);
        // window.setCanceledOnTouchOutside(false);
        pw.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
            }
        });
        // 忘记密码点击事件 @author Ysw created at 2017/3/14 11:49
        forgot_passwordTextView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_real_info = new Intent(BalanceInActivity.this, FindTradePwd.class);
                startActivity(intent_real_info);
            }
        });
        // 关闭输入密码 @author Ysw created at 2017/3/14 11:49
        close_keyboard.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                UmUtils.customsEvent(BalanceInActivity.this, UmUtils.CURRENTIN_PASSWORDCLOSE_CLICK, UmUtils.CURRENTIN_PASSWORDCLOSE_HUM);
                window.dismiss();
            }
        });
        KeyboardUtil keyboardUtil = new KeyboardUtil(view, window, this, pw);
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

        // 密码输入监听 @author Ysw created at 2017/3/14 11:50
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            // 输入6位密码之后自动关闭密码键盘 @author Ysw created at 2017/3/14 11:50
            @Override
            public void afterTextChanged(Editable s) {
                s = pw.getText();
                if (s.length() >= 6) {
                    passwd = pw.getText().toString();
                    try {
                        sendBalInRequest();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        pw.addTextChangedListener(textWatcher);
    }

    private void hideSystemKeyBoard() {
        InputMethodManager imm = (InputMethodManager) (BalanceInActivity.this)
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(ed_input_money.getWindowToken(), 0);
    }

    /**
     * 自定义短信验证的popwindow @author Ysw created at 2017/3/14 11:52
     */
    private void showVerificationPopwindow() {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        cview = inflater.inflate(R.layout.layout_verification, null);
        pw = (EditText) cview.findViewById(R.id.register_et2);
        send_code = (Button) cview.findViewById(R.id.register_send_code);
        close_keyboard = (ImageView) cview.findViewById(R.id.close_keyboard);//
        // 关闭按钮
        keyboard_parts = (LinearLayout) cview.findViewById(R.id.keyboard_parts);//
        // 密码盘区域
        loading_page = (RelativeLayout) cview.findViewById(R.id.loading_page);//
        // loading区域
        loadingBar = (ProgressBar) cview.findViewById(R.id.progressbar);// 模糊进度条
        // forgot_passwordTextView = (TextView) view
        // .findViewById(R.id.forgot_password);
        window = new PopupWindow(cview, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT, true);
        // 实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(0xb0000000);
        window.setBackgroundDrawable(dw);
        window.setOutsideTouchable(true);
        // 设置popWindow的显示和消失动画
        window.setAnimationStyle(R.style.dialogWindowAnim);
        // 在底部显示
        window.showAtLocation(cview, Gravity.BOTTOM, 0, 0);
        // window.setCanceledOnTouchOutside(false);
        pw.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
            }
        });
        // 关闭短信验证输入键盘 @author Ysw created at 2017/3/14 11:53
        close_keyboard.setOnClickListener(new OnClickListener() {
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
        // 短信验证输入监听 @author Ysw created at 2017/3/14 11:53
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            /**
             * 输入6位验证码之后自动关闭短信验证键盘 @author Ysw created at 2017/3/14 11:54
             */
            @Override
            public void afterTextChanged(Editable s) {
                s = pw.getText();
                if (s.length() >= 6) {
                    validate = pw.getText().toString();
                    try {
                        payValidateRequest(validate);
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
}