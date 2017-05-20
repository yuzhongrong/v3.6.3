package com.jinr.core.security.mobile;

import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.jinr.core.R;
import com.jinr.core.base.BaseFragment;
import com.jinr.core.config.UrlConfig;
import com.jinr.core.utils.CommonUtil;
import com.jinr.core.utils.LoadingDialog;
import com.jinr.core.utils.MyDES;
import com.jinr.core.utils.MyhttpClient;
import com.jinr.core.utils.ToastUtil;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import model.SCONF;

/**
 * 输入手机号
 *
 * @author 52
 */
public class FrgChgTel_2 extends BaseFragment implements OnClickListener {

    private Button btnNext;
    private TextView txtTel;
    private LoadingDialog loadingdialog;
    private IntentFilter filter;
    private Handler handler;
    private BroadcastReceiver smsReceiver;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.aty_chg_tel_2, container, false);
        initData();
        findViewById(view);
        initUI();
        setListener();
        return view;
    }

    @Override
    public void setListener() {
        btnNext.setOnClickListener(this);
        txtTel.addTextChangedListener(new watcher());
    }

    @Override
    public void initUI() {

    }

    private void registerReceiver(BroadcastReceiver smsReceiver2, IntentFilter filter2) {

    }

    @Override
    public void findViewById(View view) {
        btnNext = (Button) view.findViewById(R.id.btn_next);
        txtTel = (TextView) view.findViewById(R.id.txt_tel);
        btnNext.setEnabled(false);
    }

    @Override
    public void initData() {
        loadingdialog = new LoadingDialog(getActivity());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_next:
                try {
                    doGetCaptcha();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            default:
                break;
        }
    }

    /**
     * 手机号校验
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
            int telLen = txtTel.length();
            if (telLen == 11) {
                btnNext.setEnabled(true);
                btnNext.setClickable(true);
                btnNext.setBackgroundResource(R.drawable.btn_bg_select);
            } else {
                btnNext.setClickable(false);
                btnNext.setBackgroundResource(R.drawable.btn_blue_light_bg);
            }
        }
    }

    private void doGetCaptcha() throws Exception {
        RequestParams params = new RequestParams();
        String tel = txtTel.getText().toString();
        Pattern p = Pattern.compile("^1\\d{10}$");
        Matcher m = p.matcher(tel);
        if (!m.matches()) {
            ToastUtil.show(getActivity(), "该号码不存在");
            return;
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("tel", tel);
        params.put("cipher", MyDES.encrypt(jsonObject.toString()));
        loadingdialog.show();
        MyhttpClient.desPost(UrlConfig.USER_JUDGE_TEL, params, new AsyncHttpResponseHandler() {
            @Override
            public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
                super.onFailure(arg0, arg1, arg2, arg3);
                loadingdialog.dismiss();
                ToastUtil.show(FrgChgTel_2.this.getActivity(), R.string.default_error);
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
                    if (isSuccess == SCONF.FAIL) {// 可以进入到下一步
                        CharSequence text = txtTel.getText();
                        ChangeMobileStep1.instance.getCaptcha(text.toString());
                    } else if (isSuccess == SCONF.SUCCESS) {
                        ToastUtil.show(getActivity(), "该手机" + "号已被注册");
                    } else {
                        String msg = job.getString("msg");
                        ToastUtil.show(getActivity(), msg);
                    }
                } catch (Exception e) {
                }
            }
        });
    }
}
