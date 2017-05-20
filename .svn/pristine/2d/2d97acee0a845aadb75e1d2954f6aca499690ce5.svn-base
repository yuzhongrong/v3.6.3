package com.jinr.core.security.mobile;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.SmsMessage;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jinr.core.R;
import com.jinr.core.base.BaseFragment;
import com.jinr.core.config.MessageType;
import com.jinr.core.config.UrlConfig;
import com.jinr.core.ui.CannotCodeDialog;
import com.jinr.core.utils.AutoSmsUtil;
import com.jinr.core.utils.CommonUtil;
import com.jinr.core.utils.LoadingDialog;
import com.jinr.core.utils.MyDES;
import com.jinr.core.utils.MyhttpClient;
import com.jinr.core.utils.PreferencesUtils;
import com.jinr.core.utils.SendMobileCode;
import com.jinr.core.utils.SendMobileCode.Back;
import com.jinr.core.utils.ToastUtil;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONObject;

import model.SCONF;

/**
 * 短信验证码确认
 *
 * @author 52
 */
public class FrgChgTel_3 extends BaseFragment implements OnClickListener {

    private Button btnSure;// 确认修改
    private Button btnSend;// 获取验证码
    private EditText txtCaptcha;// 验证码输入框
    private TextView txtTelNo;
    private String telNo = "18888888888";// 手机号
    private String user_id = "";
    private LoadingDialog loadingdialog;
    // 自动填写验证码
    private BroadcastReceiver smsReceiver;
    private IntentFilter filter;
    private Handler handler;
    private String strContent;
    private LinearLayout tv_zc_layout;
    private Handler handlers = new Handler();
    private TextView no_message;
    private CannotCodeDialog dialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.aty_chg_tel_3, container, false);
        Bundle bundle = getArguments();
        try {
            telNo = bundle.get("tel").toString();
        } catch (Exception e) {
        }
        findViewById(view);
        initData();
        initUI();
        setListener();
        return view;
    }

    @Override
    public void setListener() {
        btnSure.setOnClickListener(this);
        btnSend.setOnClickListener(this);
        btnSure.setClickable(false);
        no_message.setOnClickListener(this);
        txtCaptcha.addTextChangedListener(new watcher());
    }

    @Override
    public void initUI() {
        txtCaptcha.setInputType(InputType.TYPE_CLASS_NUMBER);
        setTelNo(telNo);
        // 获取手机验证码自动填写
        handler = new Handler() {
            public void handleMessage(android.os.Message msg) {
                txtCaptcha.setText(strContent);
                if (!btnSure.isClickable()) {
                    btnSure.setClickable(true);
                }
            }
        };
        filter = new IntentFilter();
        filter.addAction("android.provider.Telephony.SMS_RECEIVED");
        filter.setPriority(Integer.MAX_VALUE);
        smsReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                try {
                    Object[] objs = (Object[]) intent.getExtras().get("pdus");
                    for (Object obj : objs) {
                        byte[] pdu = (byte[]) obj;
                        SmsMessage sms = SmsMessage.createFromPdu(pdu);
                        // 短信的内容
                        String message = sms.getMessageBody();
                        // 短息的手机号。。+86开头？
                        String from = sms.getOriginatingAddress();
                        if (!TextUtils.isEmpty(from)) {
                            String code = AutoSmsUtil.getInstance().patternCode(message);
                            if (!TextUtils.isEmpty(code)) {
                                strContent = code;
                                handler.sendEmptyMessage(1);
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        getActivity().registerReceiver(smsReceiver, filter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        getActivity().unregisterReceiver(smsReceiver);
    }

    @Override
    public void findViewById(View view) {
        btnSure = (Button) view.findViewById(R.id.btn_sure);
        btnSend = (Button) view.findViewById(R.id.btn_send);
        txtTelNo = (TextView) view.findViewById(R.id.txt_telno);
        txtCaptcha = (EditText) view.findViewById(R.id.txt_captcha);
        tv_zc_layout = (LinearLayout) view.findViewById(R.id.tv_zc_layout);
        no_message = (TextView) view.findViewById(R.id.no_message);
    }

    @Override
    public void initData() {
        user_id = PreferencesUtils.getValueFromSPMap(getActivity(), PreferencesUtils.Keys.UID);
        loadingdialog = new LoadingDialog(getActivity());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_send:
                getCaptcha();
                break;
            case R.id.btn_sure:
                String code = txtCaptcha.getText().toString();
                String pwd = ChangeMobileStep1.instance.logPwd;
                try {
                    clickSure(code, pwd);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.no_message:
                dialog = new CannotCodeDialog(FrgChgTel_3.this.getActivity());
                dialog.dialog_message.setText(getResources().getString(R.string.no_message_tip) +
                        PreferencesUtils.getCValueFromSPMap(getActivity(), PreferencesUtils.Keys.KEFU_PHONE));
                dialog.close_img.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                dialog.setCancelable();
                dialog.show();
                break;
        }
    }

    /**
     * 短信输入校验
     */
    private class watcher implements TextWatcher {
        @Override
        public void afterTextChanged(Editable s) {

        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            int lenCaptcha = s.length();
            if (lenCaptcha >= 6) {
                btnSure.setClickable(true);
                btnSure.setBackgroundResource(R.drawable.btn_bg_select);
            } else {
                btnSure.setClickable(false);
                btnSure.setBackgroundResource(R.drawable.btn_blue_light_bg);
            }
        }
    }

    private void setTelNo(String tel) {
        getCaptcha();
    }

    /**
     * 获取短信
     */
    private void getCaptcha() {
        SendMobileCode.getInstance().send_code(btnSend, getActivity(), telNo, MessageType.MESSAGE_MOBILE_XGBDSJHM, null, new Back() {
            @Override
            public void sms(String result) {
                if (result != null && "".equals(result) != true) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            handlers.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    txtTelNo.setText(telNo.toString().substring(0, 3) + "*****" + telNo.toString().substring(7, 11));
                                    tv_zc_layout.setVisibility(View.VISIBLE);
                                }
                            }, 2000);
                        }
                    }).start();
                }
            }
        });
    }

    /**
     * 确认修改手机号
     *
     * @throws Exception
     */
    private void clickSure(String code, String pwd) throws Exception {
        RequestParams params = new RequestParams();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("tel", telNo);
        jsonObject.put("checksms", code);
        jsonObject.put("password", pwd);
        jsonObject.put("uid", user_id);
        params.put("cipher", MyDES.encrypt(jsonObject.toString()));
        loadingdialog.show();
        MyhttpClient.desPost(UrlConfig.YONGHU_MODIFY_TEL, params, new AsyncHttpResponseHandler() {
            @Override
            public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
                super.onFailure(arg0, arg1, arg2, arg3);
                loadingdialog.dismiss();
                ToastUtil.show(FrgChgTel_3.this.getActivity(), R.string.default_error);
            }

            @Override
            public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
                super.onSuccess(arg0, arg1, arg2);
                loadingdialog.dismiss();
                try {
                    String response = new String(arg2, "UTF-8");
                    response = CommonUtil.transResponse(response);
                    JSONObject job = new JSONObject(response);
                    String cipher = job.getString("cipher");
                    String desc = MyDES.decrypt(cipher);
                    JSONObject obj = new JSONObject(desc);
                    int isSuccess = obj.getInt("code");
                    if (isSuccess == SCONF.SUCCESS) {
                        PreferencesUtils.putValueToSPMap(FrgChgTel_3.this.getActivity(), PreferencesUtils.Keys.TEL, telNo);
                        FrgChgTel_3.this.getActivity().finish();
                        ToastUtil.show(FrgChgTel_3.this.getActivity(), "修改成功");
                    } else {
                        String msg = obj.getString("msg");
                        ToastUtil.show(FrgChgTel_3.this.getActivity(), msg);
                    }
                } catch (Exception e) {
                }
            }
        });
    }
}
