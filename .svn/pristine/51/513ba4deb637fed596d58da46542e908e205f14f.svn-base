package com.jinr.core.security.mobile;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.jinr.core.JinrApp;
import com.jinr.core.R;
import com.jinr.core.base.BaseFragment;
import com.jinr.core.config.UrlConfig;
import com.jinr.core.regist.ForgetPasswordActivity;
import com.jinr.core.utils.CommonUtil;
import com.jinr.core.utils.LoadingDialog;
import com.jinr.core.utils.MyDES;
import com.jinr.core.utils.MyhttpClient;
import com.jinr.core.utils.PreferencesUtils;
import com.jinr.core.utils.ToastUtil;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONObject;

import model.SCONF;

/**
 * 验证用户登录密码
 *
 * @author 52
 */
public class FrgChgTel_1 extends BaseFragment implements OnClickListener {

    private TextView txtPwd;
    private ImageView btnEye;
    private Button btnNext;
    private Boolean isEye = false;
    private String preTel; // 当前已绑定的手机
    private LoadingDialog loadingdialog;

    /**
     * 用户登录密码
     */
    private String _pwd;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.aty_chg_tel_1, container, false);
        initData();
        findViewById(view);
        initUI();
        setListener();
        return view;
    }

    @Override
    public void setListener() {
        txtPwd.addTextChangedListener(new watcher());
        btnNext.setOnClickListener(this);
        btnEye.setOnClickListener(this);
        btnNext.setClickable(false);
    }

    @Override
    public void initUI() {
        txtPwd.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD | InputType.TYPE_CLASS_TEXT);
    }

    @Override
    public void findViewById(View view) {
        txtPwd = (TextView) view.findViewById(R.id.txt_pwd);
        btnNext = (Button) view.findViewById(R.id.btn_next);
        btnEye = (ImageView) view.findViewById(R.id.btn_eye);
        view.findViewById(R.id.btn_forget_pwd).setOnClickListener(this);
    }

    @Override
    public void initData() {
        loadingdialog = new LoadingDialog(getActivity());
        preTel = PreferencesUtils.getValueFromSPMap(getActivity(), PreferencesUtils.Keys.TEL);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_next:
                try {
                    clickSure();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.btn_eye:
                setPwdEye();
                break;
            case R.id.btn_forget_pwd:
                Intent intentForgot = new Intent(getActivity(), ForgetPasswordActivity.class);
                startActivity(intentForgot);
                break;
            default:
                break;
        }
    }

    // -----------------------------------------------------

    /**
     * 登录密码校验
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
            String pwd = txtPwd.getText().toString();
            if (!pwd.equals("")) {
                btnNext.setClickable(true);
                btnNext.setBackgroundResource(R.drawable.btn_bg_select);
            } else {
                btnNext.setClickable(false);
                btnNext.setBackgroundResource(R.drawable.btn_blue_light_bg);
            }
        }
    }

    /**
     * 确认登录密码是否正确
     *
     * @throws Exception
     */
    private void clickSure() throws Exception {
        // 注册、忘记密码 只可以输入6-16 位混合(数字，字母)
        String pwd = txtPwd.getText().toString();
        RequestParams params = new RequestParams();
        String equipment_number = JinrApp.instance().g_imei;
        String password = pwd;
        String tel = preTel;
        String use_terminal = "android";
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("equipment_number", equipment_number);
        jsonObject.put("password", pwd);
        jsonObject.put("tel", preTel);
        jsonObject.put("use_terminal", use_terminal);
        params.put("cipher", MyDES.encrypt(jsonObject.toString()));
        _pwd = pwd;
        loadingdialog.show();
        MyhttpClient.desPost(UrlConfig.USER_LOGIN, params, new AsyncHttpResponseHandler() {
            @Override
            public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
                super.onFailure(arg0, arg1, arg2, arg3);
                loadingdialog.dismiss();
                ToastUtil.show(FrgChgTel_1.this.getActivity(), R.string.default_error);
            }

            @Override
            public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
                super.onSuccess(arg0, arg1, arg2);
                loadingdialog.dismiss();
                try {
                    String response = new String(arg2, "UTF-8");
                    response = CommonUtil.transResponse(response);
                    response = CommonUtil.transResponse(response);
                    JSONObject job = new JSONObject(response);
                    String cipher = job.getString("cipher");
                    String desc = MyDES.decrypt(cipher);
                    JSONObject obj = new JSONObject(desc);
                    int isSuccess = obj.getInt("code");
                    if (isSuccess == SCONF.SUCCESS) {// 可以进入到下一步
                        ChangeMobileStep1.instance.writeNewTel();
                        ChangeMobileStep1.instance.logPwd = _pwd;
                    } else {
                        ToastUtil.show(getActivity(), obj.getString("msg"));
                    }
                } catch (Exception e) {
                }
            }
        });

    }

    /**
     * 设置密码是否可见
     */
    private void setPwdEye() {
        isEye = !isEye;
        if (isEye) {
            btnEye.setImageResource(R.drawable.psw_show);
            txtPwd.setInputType(InputType.TYPE_TEXT_VARIATION_NORMAL
                    | InputType.TYPE_CLASS_TEXT);
        } else {
            btnEye.setImageResource(R.drawable.psw_disapper);
            txtPwd.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD
                    | InputType.TYPE_CLASS_TEXT);
        }
        // 设置输入框光标位置
        CharSequence text = txtPwd.getText();
        if (text instanceof Spannable) {
            Spannable spanText = (Spannable) text;
            Selection.setSelection(spanText, text.length());
        }
    }
}
