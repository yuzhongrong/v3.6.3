package com.jinr.core.security.tradepwd;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jinr.core.JinrApp;
import com.jinr.core.R;
import com.jinr.core.config.UrlConfig;
import com.jinr.core.trade.purchase.CurrentPurchaseFirstActivity;
import com.jinr.core.utils.CommonUtil;
import com.jinr.core.utils.LoadingDialog;
import com.jinr.core.utils.MyDES;
import com.jinr.core.utils.MyLog;
import com.jinr.core.utils.MyhttpClient;
import com.jinr.core.utils.PasswordInputView2;
import com.jinr.core.utils.PreferencesUtils;
import com.jinr.core.utils.ToastUtil;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import model.BaseModel;

/**
 * 验证用户新交易密码第二次
 *
 * @author 640
 */
public class FrgSetTradePwd_2 extends Fragment implements OnClickListener {
    private PasswordInputView2 txtPwd;
    private Button btnSure;
    private LoadingDialog loadingdialog;
    private Button btn_notice;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.set_trade_pwd_2, container, false);
        initData();
        findViewById(view);
        initUI();
        setListener();
        return view;
    }

    private void findViewById(View view) {
        txtPwd = (PasswordInputView2) view.findViewById(R.id.txt_pwd);
        btnSure = (Button) view.findViewById(R.id.btn_sure);
        btn_notice = (Button) view.findViewById(R.id.btn_notice);
    }

    private void setListener() {
        btnSure.setOnClickListener(this);
        btnSure.setClickable(false);
        txtPwd.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int arg1, int arg2, int arg3) {
                String str = txtPwd.getText().toString();
                int len = str.length();
                if (len == 6) {
                    btnSure.setEnabled(true);
                    btnSure.setClickable(true);
                    btnSure.setBackgroundResource(R.drawable.btn_bg_select);
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(txtPwd.getWindowToken(), 0);
                    sureChg();
                } else if (len == 5) {
                    btnSure.setClickable(false);
                    btnSure.setBackgroundResource(R.drawable.btn_blue_light_bg);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int arg1, int arg2, int arg3) {
            }

            @Override
            public void afterTextChanged(Editable arg0) {
            }
        });
    }

    private void initUI() {
        txtPwd.setInputType(InputType.TYPE_CLASS_NUMBER);
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(txtPwd.getWindowToken(), 0);
    }

    private void initData() {
        loadingdialog = new LoadingDialog(getActivity());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_sure:
                sureChg();
                break;

            default:
                break;
        }
    }

    private void sureChg() {
        String pwd = txtPwd.getText().toString();
        if (SetTradePwdActivity.instance.pwd_1.equals(pwd)) {
            try {
                send();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            ToastUtil.show(getActivity(), R.string.passwd_equal_checked);
        }
    }

    protected void send() throws Exception {
        loadingdialog.show();
        RequestParams params = new RequestParams();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("uid", SetTradePwdActivity.instance.user_id);
        jsonObject.put("jypwd", SetTradePwdActivity.instance.pwd_1);
        params.put("cipher", MyDES.encrypt(jsonObject.toString()));
        MyhttpClient.desPost(UrlConfig.YONHU_SETJYPWD, params, new AsyncHttpResponseHandler() {
            @Override
            public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
                super.onFailure(arg0, arg1, arg2, arg3);
                loadingdialog.dismiss();
                ToastUtil.show(FrgSetTradePwd_2.this.getActivity(), R.string.default_error);
            }

            @Override
            public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
                super.onSuccess(arg0, arg1, arg2);
                loadingdialog.dismiss();

                try {
                    String response = new String(arg2, "UTF-8");
                    response = CommonUtil.transResponse(response);//去掉字符串中的空格符
                    JSONObject jsonObject = new JSONObject(response);
                    String cipher = jsonObject.getString("cipher");
                    String desc = MyDES.decrypt(cipher);
                    BaseModel<Integer> result = new Gson().fromJson(desc, new TypeToken<BaseModel<Integer>>() {}.getType());
                    if (result.isSuccess()) {
                        ToastUtil.show(getActivity(), R.string.set_deal_passwd_success);
                        JinrApp.instance().state_bankBind = 1; //更新转入页绑卡状态
                        JinrApp.instance().state_tradePassword = 1; //更新转入页交易密码设置状态
                        PreferencesUtils.putIntToSPMap(getActivity(), PreferencesUtils.Keys.IS_SET_TRADE_PWD, 1);
                        JinrApp.instance().is_fresh = true;
                        if (CurrentPurchaseFirstActivity.instance != null) {
                            CurrentPurchaseFirstActivity.instance.finish();
                        }
                        FrgSetTradePwd_2.this.getActivity().finish();
                    } else {
                        btn_notice.setVisibility(View.VISIBLE);
                        btn_notice.setText(R.string.passwd_equal_checked);
                        Animation anim = AnimationUtils.loadAnimation(FrgSetTradePwd_2.this.getActivity(), R.anim.top_in);
                        anim.setFillAfter(true);
                        btn_notice.startAnimation(anim);
                        anim.setAnimationListener(new AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {

                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {
                                btn_notice.clearAnimation();
                                Animation anim = AnimationUtils.loadAnimation(FrgSetTradePwd_2.this.getActivity(), R.anim.top_out);
                                anim.setStartOffset(5000);
                                btn_notice.startAnimation(anim);
                                anim.setAnimationListener(new AnimationListener() {
                                    @Override
                                    public void onAnimationStart(Animation animation) {
                                    }

                                    @Override
                                    public void onAnimationRepeat(Animation animation) {
                                    }

                                    @Override
                                    public void onAnimationEnd(Animation animation) {
                                        btn_notice.clearAnimation();
                                        btn_notice.setVisibility(View.GONE);
                                    }
                                });
                            }
                        });
                    }
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    MyLog.e("json解析错误", "解析错误");
                }
            }
        });
    }
}
