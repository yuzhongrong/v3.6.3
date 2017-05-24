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
import com.jinr.core.bankCard.ChooseCityActivity;
import com.jinr.core.bankCard.SecondBandCardActivity;
import com.jinr.core.base.BaseActivity;
import com.jinr.core.config.EventBusKey;
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
import com.jinr.core.utils.ToastUtil;
import com.jinr.core.utils.UmUtils;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.apache.http.Header;
import org.json.JSONObject;
import org.simple.eventbus.Subscriber;

import model.BalanceOutInfoObj;
import model.BalanceOutInfoObj.BalanceOutInfoData;
import model.BaseModel;
import model.OrderObj;
import model.OrderObj.OrderData;
import model.UidObj;
import model.UserBindinfo;

/**
 * 余额转出界面 @author Ysw created at 2017/3/14 13:06
 */
public class BalanceOutActivity extends BaseActivity implements OnClickListener {

    private TextView title_center_text;
    private TextView tv_protocol;
    private ImageView title_left_img;
    private ImageView iv_bank_logo;// 银行logo
    private TextView tv_bank_name;// 银行名称
    private TextView tv_bank_num;// 银行卡号后四位
    private EditText ed_input_money;// 提现输入框
    private Button btn_balance_out;// 提现按钮
    private TextView tv_out_num; // 剩余提现次数
    private TextView tv_useful_bal;// 可用余额
    private String uid;
    private String input_money;  //用户输入的金额
    private double can_use_money;//可用余额
    //密码键盘 pw
    private View view;
    private EditText pw;
    private ImageView close_keyboard;
    private TextView forgot_passwordTextView;
    private PopupWindow window;
    private LinearLayout keyboard_parts;
    private RelativeLayout loading_page;
    private ProgressBar loadingBar;
    private NewCustomDialogNoTitleFinish dialog_bind_card;
    private NewCustomDialogNoTitleFinish dialog_set_passwd;
    private LinearLayout llay_bank_info;
    private Handler pwdhandler;
    // final static int REGULAR_MSG_CREATE_ORDER = 1;
    final static int REGULAR_MSG_KEYBOARD_HIDE = 2;
    final static int REGULAR_MSG_KEYBOARD_VALIDATE_SHOW = 3;
    final static int REGULAR_MSG_KEYBOARD_VALIDATE_HIDE = 4;
    final static int REGULAR_MSG_KEYBOARD_VALIDATE_ERROR_SHOW = 5;
    private boolean has_city;// 城市是否为空
    private NewCustomDialogNoTitleFinish dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_balanceout);
        findViewById();
        setListener();
        initData();
    }

    @Override
    protected void initData() {
        uid = PreferencesUtils.getValueFromSPMap(this, PreferencesUtils.Keys.UID);
        has_city = getIntent().getBooleanExtra("has_city", true);
        try {
            // 获取用户转出次数及可用余额 @author Ysw created at 2017/3/14 11:58
            getBalOutInfo();
        } catch (Exception e) {
            e.printStackTrace();
        }
        pwdhandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case REGULAR_MSG_KEYBOARD_HIDE:// 隐藏键盘，开始验证密码
                        close_keyboard.setClickable(false);
                        keyboard_parts.setVisibility(View.INVISIBLE);
                        loading_page.setVisibility(View.VISIBLE);
                        loadingBar.setVisibility(View.VISIBLE);
                        break;
                    case REGULAR_MSG_KEYBOARD_VALIDATE_SHOW:
                        if (window != null && window.isShowing()) {
                            window.dismiss();
                        }
                        break;
                    case REGULAR_MSG_KEYBOARD_VALIDATE_HIDE:
                        btn_balance_out.setClickable(true);
                        close_keyboard.setClickable(false);
                        keyboard_parts.setVisibility(View.INVISIBLE);
                        loading_page.setVisibility(View.VISIBLE);
                        loadingBar.setVisibility(View.VISIBLE);
                        break;
                    case REGULAR_MSG_KEYBOARD_VALIDATE_ERROR_SHOW:
                        btn_balance_out.setClickable(true);
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
    }


    @Override
    protected void findViewById() {
        //EventBus.getDefault().register(this);
        llay_bank_info = (LinearLayout) findViewById(R.id.llay_bank_info);
        title_center_text = (TextView) findViewById(R.id.title_center_text);
        tv_protocol = (TextView) findViewById(R.id.tv_protocol);
        tv_bank_name = (TextView) findViewById(R.id.tv_bank_name);
        tv_bank_num = (TextView) findViewById(R.id.tv_bank_num);
        tv_out_num = (TextView) findViewById(R.id.tv_out_num);
        tv_useful_bal = (TextView) findViewById(R.id.tv_useful_bal);
        title_left_img = (ImageView) findViewById(R.id.title_left_img);
        iv_bank_logo = (ImageView) findViewById(R.id.iv_bank_logo);
        ed_input_money = (EditText) findViewById(R.id.ed_input_money);
        btn_balance_out = (Button) findViewById(R.id.btn_balance_out);
        tv_protocol.setVisibility(View.VISIBLE);
        tv_protocol.setText("说明");
        title_center_text.setText("余额提现");

        initDialog();
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
    protected void initUI() {

    }

    @Override
    protected void setListener() {
        title_left_img.setOnClickListener(this);
        tv_protocol.setOnClickListener(this);
        btn_balance_out.setOnClickListener(this);
        ed_input_money.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String ss = s + "";
                if (TextUtils.isEmpty(ss)) {
                    btn_balance_out.setEnabled(false);
                    return;
                }
                if (ss.startsWith(".")) {
                    ed_input_money.setText("");
                    return;
                }
                //输入的金额小于可用的按钮可点
                double inputDouble = Double.parseDouble(ss);
                if (inputDouble > 0) {
                    btn_balance_out.setEnabled(true);
                } else {
                    btn_balance_out.setEnabled(false);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String str = s.toString();
                if (TextUtils.isEmpty(str)) {
                    btn_balance_out.setEnabled(false);
                    return;
                }
                if (str.startsWith(".")) return;
                //用户输入的金额大于可用余额、提示
                double inputDouble = Double.parseDouble(str);
                if (can_use_money < inputDouble) {
                    ed_input_money.setText(dealDigital(can_use_money + ""));
                    Editable text = ed_input_money.getText();
                    Selection.setSelection(text, text.length());
                }
                //0后面不是小数点、数字不让输
                int posDot = str.indexOf(".");
                if (str.startsWith("0")) {
                    if (str.length() > 1 && posDot != 1) {
                        ed_input_money.setText("0");
                    }
                }
                //两位小数
                if (posDot <= 0) return;
                if (str.length() - posDot - 1 > 2) {
                    s.delete(posDot + 3, posDot + 4);
                }
            }
        });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_left_img:
                BalanceOutActivity.this.finish();
                break;
            case R.id.tv_protocol:
                Intent intent_bal = new Intent(BalanceOutActivity.this, CommonWebActivity.class);
                intent_bal.putExtra("url", UrlConfig.IP_R + UrlConfig.BALANCE_EXPLAIN);
                intent_bal.putExtra("titleName", "余额说明");
                startActivity(intent_bal);
                break;
            case R.id.btn_balance_out:
                try {
                    judgeTakeOutMoney();
                } catch (Exception e) {
                    e.printStackTrace();
                }
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
                if (!PreferencesUtils.getValueFromSPMap(BalanceOutActivity.this, PreferencesUtils.Keys.NAME).equals("")
                        && !PreferencesUtils.getValueFromSPMap(BalanceOutActivity.this, PreferencesUtils.Keys.ID_CARD).equals("")) {
                    Intent intent = new Intent(BalanceOutActivity.this, SecondBandCardActivity.class);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(BalanceOutActivity.this, AddBankActivity.class);
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
                Intent intent = new Intent(BalanceOutActivity.this, SetTradePwdActivity.class);
                BalanceOutActivity.this.startActivity(intent);
            }
        });
    }

    /**
     * 获取余额转出的次数和可用的余额 @author Ysw created at 2017/3/14 11:58
     */
    protected void getBalOutInfo() throws Exception {
        RequestParams params = new RequestParams();
        JSONObject cipher = new JSONObject();
        cipher.put("uid", uid);
        cipher.put("goods_type", "2");
        params.put("cipher", MyDES.encrypt(cipher.toString()));
        MyhttpClient.desPost(UrlConfig.BALANCE_OUT_INFO, params, new AsyncHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                showWaittingDialog(BalanceOutActivity.this);
            }

            @Override
            public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
                super.onFailure(arg0, arg1, arg2, arg3);
                ToastUtil.show(BalanceOutActivity.this, R.string.default_error);
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
                        BalanceOutInfoObj outInfo = new Gson().fromJson(cipher, BalanceOutInfoObj.class);
                        if (null != outInfo) {
                            BalanceOutInfoData data = outInfo.data;
                            can_use_money = Double.parseDouble(data.money);
                            tv_out_num.setText(data.times);
                            tv_useful_bal.setText("当前余额￥" + dealDigital(data.money));
                        }
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

    /**
     * 判断输入金额是否大于余额接口请求 @author Ysw created at 2017/3/14 13:03
     */
    protected void judgeTakeOutMoney() throws Exception {
        input_money = ed_input_money.getText().toString().trim();
        RequestParams params = new RequestParams();
        JSONObject cipher = new JSONObject();
        cipher.put("uid", uid);
        cipher.put("money", input_money);
        params.put("cipher", MyDES.encrypt(cipher.toString()));
        MyhttpClient.desPost(UrlConfig.BALANCE_JUDGE_OUT_MONEY, params, new AsyncHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                showWaittingDialog(BalanceOutActivity.this);
            }

            @Override
            public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
                super.onFailure(arg0, arg1, arg2, arg3);
                btn_balance_out.setEnabled(false);
                ToastUtil.show(BalanceOutActivity.this, R.string.default_error);
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
                        showPopwindow();
                    } else {
                        String errorMsg = object.getString("msg");
                        ToastUtil.show(BalanceOutActivity.this, errorMsg + "");
                        getBalOutInfo();
                        btn_balance_out.setEnabled(false);
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

    /**
     * 获取绑卡相关信息 @author Ysw created at 2017/3/14 13:03
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
            }

            @Override
            public void onFinish() {
                super.onFinish();
            }

            @Override
            public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
                super.onFailure(arg0, arg1, arg2, arg3);
                ToastUtil.show(BalanceOutActivity.this, R.string.default_error);
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
                        if (!has_city) {
                            showCityDialog();
                            return;
                        }
                        llay_bank_info.setVisibility(View.VISIBLE);
                        String mBankName = data.getBank_name();
                        String mBankNum = data.getCard_last();
                        String logourl = data.getImg();
                        tv_bank_name.setText(mBankName);
                        tv_bank_num.setText("(尾号" + mBankNum + ")");
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
     * 余额转出请求接口 @author Ysw created at 2017/3/14 13:02
     */
    protected void sendBalOutRequest(String pwd) throws Exception {
        Message msg = new Message();
        msg.what = REGULAR_MSG_KEYBOARD_VALIDATE_HIDE;
        pwdhandler.sendMessage(msg);
        RequestParams params = new RequestParams();
        JSONObject cipher = new JSONObject();
        cipher.put("buss_pwd", pwd);
        cipher.put("buy_order", "");
        cipher.put("buy_type", "1");
        cipher.put("client_ip", "");
        cipher.put("event_key", "");
        cipher.put("money", input_money);
        cipher.put("pay_type", "2");
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
                ToastUtil.show(BalanceOutActivity.this, R.string.default_error);
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
                                //跳转到转出结果页
                                Intent intent = new Intent(BalanceOutActivity.this, BalanceOutResultActivity.class);
                                intent.putExtra("buy_type", data.buy_type);
                                intent.putExtra("order_num", data.order_num);
                                intent.putExtra("pay_type", data.pay_type);
                                intent.putExtra("uid", uid);
                                intent.putExtra("entrance", "balance");
                                startActivity(intent);
                                window.dismiss();
                                BalanceOutActivity.this.finish();
                            }
                        }
                    } else if (code == 3002) {//密码输错，置空
                        Message msg = new Message();
                        msg.what = REGULAR_MSG_KEYBOARD_VALIDATE_ERROR_SHOW;
                        pwdhandler.sendMessage(msg);
                        pw.setText("");
                        ToastUtil.show(BalanceOutActivity.this, object.getString("msg"));
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
     * 弹出自定义密码盘的popwindow @author Ysw created at 2017/3/14 12:58
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
        // 设置popWindow的显示和消失动画
        window.setAnimationStyle(R.style.dialogWindowAnim);
        // 在底部显示
        window.showAtLocation(view, Gravity.BOTTOM, 0, 0);
        pw.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
            }
        });
        forgot_passwordTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_real_info = new Intent(BalanceOutActivity.this, FindTradePwd.class);
                startActivity(intent_real_info);
            }
        });
        close_keyboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UmUtils.customsEvent(BalanceOutActivity.this, UmUtils.CURRENTIN_PASSWORDCLOSE_CLICK, UmUtils.CURRENTIN_PASSWORDCLOSE_HUM);
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
                    String passwd = pw.getText().toString();
                    try {
                        sendBalOutRequest(passwd);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        pw.addTextChangedListener(textWatcher);
    }

    /**
     * 截取、使小数点保持两位小数、 @author Ysw created at 2017/3/14 13:01
     */
    private String dealDigital(String str) {
        if (str.contains(".")) {
            String[] split = str.split("\\.");
            int length = split[1].length();
            if (length == 0) {
                return split[0] + "00";
            } else if (length == 1) {
                return str + "0";
            } else if (length == 2) {
                return str;
            } else {
                return split[0] + "." + split[1].substring(0, 2);
            }
        } else {
            return str + ".00";
        }
    }

    private void showCityDialog() {
        dialog = new NewCustomDialogNoTitleFinish(this, "到“选择开户城市”页面补充开户城市信息，将会加快提现到账速度");
        dialog.btn_custom_dialog_sure.setText("去补充");
        dialog.btn_custom_dialog_sure.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BalanceOutActivity.this, ChooseCityActivity.class);
                startActivity(intent);
            }
        });
        dialog.show();
    }

    // 添加城市成功后关闭页面
    @Subscriber(tag = EventBusKey.ADD_CITY_SUCCESD)
    public void dissmissDialog(boolean isChoose) {
        if (isChoose) {
            has_city = true;
            dialog.dismiss();
        }
    }
}
