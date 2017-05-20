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
import android.view.ViewGroup;
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
import com.jinr.core.MainActivity;
import com.jinr.core.R;
import com.jinr.core.base.BaseFragment;
import com.jinr.core.config.UrlConfig;
import com.jinr.core.security.tradepwd.FindTradePwd;
import com.jinr.core.ui.CustomDialog2;
import com.jinr.core.utils.CommonUtil;
import com.jinr.core.utils.KeyboardUtil;
import com.jinr.core.utils.MyDES;
import com.jinr.core.utils.MyhttpClient;
import com.jinr.core.utils.PreferencesUtils;
import com.jinr.core.utils.ToastUtil;
import com.jinr.core.utils.UmUtils;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONObject;

import model.BalanceOutInfoObj;
import model.BalanceOutInfoObj.BalanceOutInfoData;
import model.CurrentOutObj;
import model.CurrentOutObj.CurrentOutData;
import model.OrderObj;
import model.OrderObj.OrderData;

public class CurOutBalFragment extends BaseFragment implements CustomDialog2.OnDialogViewClickListener {

    private View layout_view;//布局view
    private EditText ed_input_money;//金额输入框
    private Button btn_balance_out;//转出按钮
    private TextView tv_out_time;//预期到账时间
    private String input_money;  //用户输入的金额
    private String uid;
    protected double can_use_money;//可转出金额
    //密码键盘 pw
    private View view;
    private EditText pw;
    private ImageView close_keyboard;
    private TextView forgot_passwordTextView;
    private PopupWindow window;
    private LinearLayout keyboard_parts;
    private RelativeLayout loading_page;
    private ProgressBar loadingBar;
    private boolean flag = false;
    private Handler pwdhandler;
    // final static int REGULAR_MSG_CREATE_ORDER = 1;
    final static int REGULAR_MSG_KEYBOARD_HIDE = 2;
    final static int REGULAR_MSG_KEYBOARD_VALIDATE_SHOW = 3;
    final static int REGULAR_MSG_KEYBOARD_VALIDATE_HIDE = 4;
    final static int REGULAR_MSG_KEYBOARD_VALIDATE_ERROR_SHOW = 5;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        layout_view = inflater.inflate(R.layout.fragment_curoutbal, container, false);
        findViewById(layout_view);
        setListener();
        initData();
        return layout_view;
    }

    @Override
    protected void initData() {
        uid = PreferencesUtils.getValueFromSPMap(getActivity(), PreferencesUtils.Keys.UID);
        try {
            getCurrentOutInfo();
            getCurrentOutTime();
            flag = true;
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
    protected void findViewById(View view) {
        ed_input_money = (EditText) view.findViewById(R.id.ed_input_money);
        btn_balance_out = (Button) view.findViewById(R.id.btn_balance_out);
        tv_out_time = (TextView) view.findViewById(R.id.tv_out_time);

    }

    @Override
    protected void initUI() {
    }

    /**
     * 当此页面可见的时候重新请求可转出余额及次数
     */
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        //加flag，setUserVisibleHint执行靠前、getActivity为null
        if (isVisibleToUser && flag == true) {
            try {
                ed_input_money.setText("");
                getCurrentOutInfo();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void setListener() {
        btn_balance_out.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                input_money = ed_input_money.getText().toString().trim();
                hideSystemKeyBoard();
                showPopwindow();
            }
        });

        ed_input_money.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String ss = s + "";
                if (TextUtils.isEmpty(ss)) {
                    btn_balance_out.setEnabled(false);
                    return;
                }
                //不让小数点开头
                if (ss.startsWith(".")) {
                    ed_input_money.setText("");
                    return;
                }
                //输入的金额大于0，按钮可点
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
                if (TextUtils.isEmpty(str)) return;
                if (str.startsWith(".")) return;
                //用户输入的金额大于可用活期、置空
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

    /**
     * 活期转出到余额
     *
     * @throws Exception
     */
    protected void sendBalOutRequest(String pwd) throws Exception {
        Message msg = new Message();
        msg.what = REGULAR_MSG_KEYBOARD_VALIDATE_HIDE;
        pwdhandler.sendMessage(msg);
        RequestParams params = new RequestParams();
        JSONObject cipher = new JSONObject();
        cipher.put("buss_pwd", pwd);
        cipher.put("buy_order", "");
        cipher.put("buy_type", "2");
        cipher.put("client_ip", "");
        cipher.put("event_key", "");
        cipher.put("money", input_money);
        cipher.put("pay_type", "3");
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
                ToastUtil.show(getActivity(), R.string.default_error);
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
                                Intent intent = new Intent(getActivity(), BalanceOutResultActivity.class);
                                intent.putExtra("buy_type", data.buy_type);
                                intent.putExtra("order_num", data.order_num);
                                intent.putExtra("pay_type", data.pay_type);
                                intent.putExtra("uid", uid);
                                intent.putExtra("entrance", "current");
                                startActivity(intent);
                                CurOutBalFragment.this.setUserVisibleHint(true);
                                window.dismiss();
                                getActivity().finish();
                            }
                        }
                    } else if (code == 3002) {//密码输错，置空
                        Message msg = new Message();
                        msg.what = REGULAR_MSG_KEYBOARD_VALIDATE_ERROR_SHOW;
                        pwdhandler.sendMessage(msg);
                        pw.setText("");
                        ToastUtil.show(getActivity(), object.getString("msg"));
                    } else {//关闭
                        ToastUtil.show(getActivity(), object.getString("msg"));
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


    // 获取活期可转出金额和次数
    private void getCurrentOutInfo() throws Exception {
        RequestParams params = new RequestParams();
        JSONObject cipher = new JSONObject();
        cipher.put("uid", uid);
        cipher.put("goods_type", "3");
        params.put("cipher", MyDES.encrypt(cipher.toString()));
        MyhttpClient.desPost(UrlConfig.BALANCE_OUT_INFO, params, new AsyncHttpResponseHandler() {
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
                ToastUtil.show(getActivity(), R.string.default_error);
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
                    BalanceOutInfoObj result = new Gson().fromJson(desc, BalanceOutInfoObj.class);
                    if (result != null) {
                        BalanceOutInfoData data = result.data;
                        if (data == null)
                            return;
                        can_use_money = Double.parseDouble(data.money);
                        ed_input_money.setHint("账户最多可转出" + dealDigital(data.money) + "元");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 转出到余额的预期时间
     *
     * @throws Exception
     */
    private void getCurrentOutTime() throws Exception {
        RequestParams params = new RequestParams();
        JSONObject cipher = new JSONObject();
        String desStr = MyDES.encrypt(cipher.toString());
        params.put("cipher", desStr);
        MyhttpClient.desPost(UrlConfig.Current_OUT_TIME, params, new AsyncHttpResponseHandler() {
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
                ToastUtil.show(getActivity(), R.string.default_error);
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
                        CurrentOutObj result = new Gson().fromJson(cipher, CurrentOutObj.class);
                        if (result != null) {
                            CurrentOutData data = result.data;
                            if (data == null)
                                return;
                            tv_out_time.setText(data.ye_time);
                        }
                    } else if (code == 10003) {
                        CustomDialog2 customDialog = new CustomDialog2(getActivity(), R.layout.dialog_cur_morning_prompt,
                                new int[]{R.id.tv_dialog_msg, R.id.tv_ok}, true);
                        customDialog.setOnDialogViewClickListener(CurOutBalFragment.this);
                        customDialog.show();
                        customDialog.setContent(R.id.tv_dialog_msg, "" + object.getString("msg"));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 自定义密码盘的popwindow @author Ysw created at 2017/3/14 13:13
     */
    private void showPopwindow() {
        // 利用layoutInflater获得View
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
                Intent intent_real_info = new Intent(MainActivity.instance, FindTradePwd.class);
                startActivity(intent_real_info);
            }
        });
        close_keyboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UmUtils.customsEvent(getActivity(), UmUtils.CURRENTIN_PASSWORDCLOSE_CLICK, UmUtils.CURRENTIN_PASSWORDCLOSE_HUM);
                window.dismiss();
            }
        });
        KeyboardUtil keyboardUtil = new KeyboardUtil(view, window, getActivity(), pw);
        pw.setFocusable(true);
        pw.setFocusableInTouchMode(true);
        pw.requestFocus();
        pw.setInputType(InputType.TYPE_NULL);
        WindowManager.LayoutParams lp = getActivity().getWindow().getAttributes();
        lp.alpha = 0.7f;
        getActivity().getWindow().setAttributes(lp);
        window.setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss() {
                WindowManager.LayoutParams lp = getActivity().getWindow().getAttributes();
                lp.alpha = 1f;
                getActivity().getWindow().setAttributes(lp);
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
                        sendBalOutRequest(passwd);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        pw.addTextChangedListener(textWatcher);
    }

    private void hideSystemKeyBoard() {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(ed_input_money.getWindowToken(), 0);
    }

    /**
     * 截取、使小数点保持两位小数、
     *
     * @param str
     * @return
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

    @Override
    public void OnDialogViewClick(CustomDialog2 dialog, View view) {
        getActivity().finish();
    }
}
