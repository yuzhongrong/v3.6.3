package com.jinr.core.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.CountDownTimer;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jinr.core.R;
import com.jinr.core.config.MessageType;
import com.jinr.core.config.UrlConfig;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONObject;

import model.BankCardRequestObj;
import model.BaseModel;

/**
 * 发送验证码()关闭当前activit 可以重新设置
 *
 * @author 604
 */
public class SendMobileCode {

    private static SendMobileCode instance = null;

    // public volatile int time = 60;

    private String code = "";
    public boolean isFirst = true;

    private SendMobileCode() {
    }

    private static synchronized void syncInit() {
        if (instance == null) {
            instance = new SendMobileCode();
        }
    }

    public static SendMobileCode getInstance() {
        if (instance == null) {
            syncInit();
        }
        return instance;
    }

    public interface Back {
        void sms(String result);

    }

    @SuppressLint("HandlerLeak")
    public synchronized void send_code(final View v, final Context context, String tel, String type, String money, final Back back) {
        ((Button) v).setClickable(false);
        /** 定义一个倒计时的内部类 */
        class TimeCount extends CountDownTimer {
            int count = 60;

            public TimeCount(long millisInFuture, long countDownInterval) {
                super(millisInFuture, countDownInterval);// 参数依次为总时长,和计时的时间间隔
            }

            @Override
            public void onFinish() {// 计时完毕时触发
                ((Button) v).setTextColor(context.getResources().getColor(R.color.phone_font_color));
                ((Button) v).setText(R.string.agin_modify_code);
                ((Button) v).setClickable(true);
            }

            @Override
            public void onTick(long millisUntilFinished) {// 计时过程显示
                ((Button) v).setClickable(false);
                ((Button) v).setTextColor(context.getResources().getColor(R.color.gray_text));
                String newMessageInfo = "<font color='#1f7ee9'><b>" + millisUntilFinished / 1000 + "s" + "</b></font>" + context.getResources().getString(R.string.format_f_f);
                ((Button) v).setText(Html.fromHtml(newMessageInfo));
            }
        }

        if (tel.length() == 11) {
            RequestParams params = new RequestParams();
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("tel", tel);
                jsonObject.put("kw", type);
                params.put("cipher", MyDES.encrypt(jsonObject.toString()));
            } catch (Exception e1) {
                e1.printStackTrace();
            }
            if (type.equals(MessageType.MESSAGE_MOBILE_ZCZJ)) {// 转出 参数money
                params.put("money", money);
            }
            try {
                MyhttpClient.desPost(UrlConfig.SMS_SENDSMS, params, new AsyncHttpResponseHandler() {

                    @Override
                    public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
                        super.onFailure(arg0, arg1, arg2, arg3);
                        ToastUtil.show(context, R.string.default_error);
                        ((Button) v).setClickable(true);
                    }

                    @Override
                    public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
                        super.onSuccess(arg0, arg1, arg2);
                        String response;
                        try {
                            response = new String(arg2, "UTF-8");
                            response = response.substring(response.indexOf("{"));
                            JSONObject jsonObject = new JSONObject(response);
                            String cipher = jsonObject.getString("cipher");
                            String desc = MyDES.decrypt(cipher);
                            BaseModel<String> result = new Gson().fromJson(desc, new TypeToken<BaseModel<String>>() {
                            }.getType());
                            if (result.isSuccess()) {
                                code = result.getData();
                                back.sms(code);
                                new TimeCount(60990, 1000).start();
                            } else {
                                back.sms("0");
                                ToastUtil.show(context, result.getMsg());
                                ((Button) v).setClickable(true);
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            if (tel.length() != 11) {
                ToastUtil.show(context, R.string.mobile_no_checked);
                ((Button) v).setClickable(true);
            }
        }
    }

    @SuppressLint("HandlerLeak")
    public synchronized void sendCode(final View v, final Context context, String tel, String requestid, final Back back) {
        ((Button) v).setClickable(false);
        /** 定义一个倒计时的内部类 */
        class TimeCount extends CountDownTimer {
            int count = 60;

            public TimeCount(long millisInFuture, long countDownInterval) {
                super(millisInFuture, countDownInterval);// 参数依次为总时长,和计时的时间间隔
            }

            @Override
            public void onFinish() {// 计时完毕时触发
                MyLog.i("count======" + count--);
                ((Button) v).setTextColor(context.getResources().getColor(R.color.phone_font_color));
                ((Button) v).setText(context.getResources().getString(
                        R.string.agin_modify_code));
                ((Button) v).setClickable(true);
            }

            @Override
            public void onTick(long millisUntilFinished) {// 计时过程显示
                ((Button) v).setClickable(false);
                ((Button) v).setTextColor(context.getResources().getColor(R.color.gray_text));
                String newMessageInfo = "<font color='#1f7ee9'><b>" + millisUntilFinished / 1000 + "s" + "</b></font>" + context.getResources().getString(R.string.format_f_f);
                ((Button) v).setText(Html.fromHtml(newMessageInfo));
            }
        }

        if (isFirst) {
            isFirst = false;
            new TimeCount(60990, 1000).start();
            return;
        }
        if (tel.length() == 11) {
            try {
                // 转JSON
                JSONObject obj = new JSONObject();
                obj.put("requestid", requestid);
                RequestParams params = new RequestParams();
                String desStr = MyDES.encrypt(obj.toString());
                params.put("cipher", desStr);
                MyLog.d("DES", "加密json Str :" + desStr);
                MyLog.d("DES", "解密json Str :" + MyDES.decrypt(desStr));
                MyhttpClient.desPost(UrlConfig.YEEPAY_BANKCARD_SEND_SMS, params, new AsyncHttpResponseHandler() {

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
                        ToastUtil.show(context, R.string.default_error);
                        ((Button) v).setClickable(true);
                    }

                    @Override
                    public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
                        super.onSuccess(arg0, arg1, arg2);
                        String response;
                        try {
                            response = new String(arg2, "UTF-8");
                            response = CommonUtil.transResponse(response);
                            if (response == null || response.equals("")) {
                                return;
                            }
                            JSONObject jsonObject = new JSONObject(response); // 解析
                            String cipher = jsonObject.getString("cipher");
                            String desStr = MyDES.decrypt(cipher); // 解密
                            if (TextUtils.isEmpty(desStr))
                                return;
                            BaseModel<BankCardRequestObj> result = new Gson().fromJson(desStr, new TypeToken<BaseModel<BankCardRequestObj>>() {
                            }.getType());
                            if (result == null) {
                                ((Button) v).setClickable(true);
                                return;
                            }
                            if (result.isSuccess()) {
                                code = result.getData().requestid;
                                back.sms(code);
                                new TimeCount(60990, 1000).start();
                            } else {
                                back.sms("0");
                                ToastUtil.show(context, result.getMsg());
                                ((Button) v).setClickable(true);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            if (tel.length() != 11) {
                ToastUtil.show(context, R.string.mobile_no_checked);
                ((Button) v).setClickable(true);
            }
        }
    }

    public synchronized void sendValidateCode(final View v, final Context context, final Back back) {

        ((Button) v).setClickable(false);
        /** 定义一个倒计时的内部类 */
        class TimeCount extends CountDownTimer {
            int count = 60;

            public TimeCount(long millisInFuture, long countDownInterval) {
                super(millisInFuture, countDownInterval);// 参数依次为总时长,和计时的时间间隔
            }

            @Override
            public void onFinish() {// 计时完毕时触发
                MyLog.i("count======" + count--);
                ((Button) v).setTextColor(context.getResources().getColor(R.color.white));
                ((Button) v).setText(context.getResources().getString(
                        R.string.agin_modify_code));
                ((Button) v).setClickable(true);
            }

            @Override
            public void onTick(long millisUntilFinished) {// 计时过程显示
                ((Button) v).setClickable(false);
                ((Button) v).setTextColor(context.getResources().getColor(R.color.white));
                String newMessageInfo = "";
                newMessageInfo = "<font color='#80ffffff'><b>" + millisUntilFinished / 1000 + "s";
                ((Button) v).setText(Html.fromHtml(newMessageInfo));
            }
        }
        if (isFirst) {
            isFirst = false;
            new TimeCount(60990, 1000).start();
            return;
        }

    }

    /**
     * 支付 验证码验证  点击重新获取再次倒计时
     * 966
     *
     * @param v
     * @param context
     * @param back
     */
    public synchronized void sendValDealCode(final View v, final Context context, final Back back) {

        /** 定义一个倒计时的内部类 */
        class TimeCount extends CountDownTimer {
            int count = 60;

            public TimeCount(long millisInFuture, long countDownInterval) {
                super(millisInFuture, countDownInterval);// 参数依次为总时长,和计时的时间间隔
            }

            @Override
            public void onFinish() {// 计时完毕时触发
                MyLog.i("count======" + count--);
                ((Button) v).setTextColor(context.getResources().getColor(R.color.white));
                ((Button) v).setText(context.getResources().getString(
                        R.string.agin_modify_code));
                ((Button) v).setClickable(true);
            }

            @Override
            public void onTick(long millisUntilFinished) {// 计时过程显示
                ((Button) v).setClickable(false);
                ((Button) v).setTextColor(context.getResources().getColor(R.color.white));
                String newMessageInfo = "";
                newMessageInfo = "<font color='#80ffffff'><b>" + millisUntilFinished / 1000 + "s";
                ((Button) v).setText(Html.fromHtml(newMessageInfo));
            }
        }

        ((Button) v).setClickable(false);
        new TimeCount(60990, 1000).start();

    }

    public synchronized void sendValidateCode(final View v, final Context context, String uid, String orderId, boolean isSend, final Back back) {
        ((Button) v).setClickable(false);
        /** 定义一个倒计时的内部类 */
        class TimeCount extends CountDownTimer {
            int count = 60;

            public TimeCount(long millisInFuture, long countDownInterval) {
                super(millisInFuture, countDownInterval);// 参数依次为总时长,和计时的时间间隔
            }

            @Override
            public void onFinish() {// 计时完毕时触发
                ((Button) v).setTextColor(context.getResources().getColor(
                        R.color.white));
                ((Button) v).setText(context.getResources().getString(
                        R.string.agin_modify_code));
                ((Button) v).setClickable(true);
            }

            @Override
            public void onTick(long millisUntilFinished) {// 计时过程显示
                ((Button) v).setClickable(false);
                ((Button) v).setTextColor(context.getResources().getColor(R.color.white));
                String newMessageInfo = "";
                newMessageInfo = "<font color='#80ffffff'><b>" + millisUntilFinished / 1000 + "s";
                ((Button) v).setText(Html.fromHtml(newMessageInfo));
            }
        }
        if (isFirst) {
            isFirst = false;
            new TimeCount(60990, 1000).start();
            return;
        }
        try {
            if (isSend) {
                JSONObject obj = new JSONObject();
                obj.put("uid", uid);
                obj.put("order_num", orderId);
                RequestParams params = new RequestParams();
                String desStr = MyDES.encrypt(obj.toString());
                params.put("cipher", desStr);
                MyhttpClient.desPost(UrlConfig.PAY_SENDSMS, params, new AsyncHttpResponseHandler() {

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
                        ToastUtil.show(context, R.string.default_error);
                        ((Button) v).setClickable(true);
                    }

                    @Override
                    public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
                        super.onSuccess(arg0, arg1, arg2);
                        String response;
                        try {
                            response = new String(arg2, "UTF-8");
                            response = CommonUtil.transResponse(response);
                            if (response == null || response.equals("")) {
                                return;
                            }
                            JSONObject jsonObject = new JSONObject(response); // 解析
                            String cipher = jsonObject.getString("cipher");
                            String desStr = MyDES.decrypt(cipher); // 解密
                            if (TextUtils.isEmpty(desStr))
                                return;
                            BaseModel<BankCardRequestObj> result = new Gson().fromJson(desStr, new TypeToken<BaseModel<BankCardRequestObj>>() {
                            }.getType());
                            if (result == null) {
                                ((Button) v).setClickable(true);
                                return;
                            }
                            if (result.isSuccess()) {
                                code = result.getData().requestid;
                                back.sms(code);
                                new TimeCount(60990, 1000).start();
                            } else {
                                back.sms("0");
                                ToastUtil.show(context, result.getMsg());
                                ((Button) v).setClickable(true);
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            } else {
                new TimeCount(60990, 1000).start();
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @param context
     * @param activity
     * @param textView 广告页跳转
     */
    public void goToActivity(final Context context, final TextView textView) {
        class TimeCount extends CountDownTimer {

            public TimeCount(long millisInFuture, long countDownInterval) {
                super(millisInFuture, countDownInterval);
            }

            @Override
            public void onFinish() {
            }

            @Override
            public void onTick(long millisUntilFinished) {
                textView.setText("跳过 " + millisUntilFinished / 1000 + "s");
            }
        }
        new TimeCount(4000, 1000).start();
    }
}
