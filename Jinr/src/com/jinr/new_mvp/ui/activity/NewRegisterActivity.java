package com.jinr.new_mvp.ui.activity;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.Html;
import android.text.InputType;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jinr.core.JinrApp;
import com.jinr.core.R;
import com.jinr.new_mvp.presenter.RegisterPresenter;
import com.jinr.new_mvp.ui.activity.BaseActivity;
import com.jinr.core.config.EventBusKey;
import com.jinr.core.config.MessageType;
import com.jinr.core.config.UrlConfig;
import com.jinr.core.gift.share.MyGiftActivity;
import com.jinr.core.regist.BezierCurve;
import com.jinr.core.regist.EditTextChangeListener;
import com.jinr.core.regist.MyAnimationListener;
import com.jinr.core.regist.MyRegistEditText;
import com.jinr.core.regist.NewLoginActivity;
import com.jinr.core.regist.RegisterViewGroup;
import com.jinr.core.regist.RoundProgressBar;
import com.jinr.core.regist.XEditText;
import com.jinr.core.regular.MyAssetsActivity;
import com.jinr.core.ui.CannotReceiveTextDialog;
import com.jinr.core.ui.NewCustomDialogNoTitle;
import com.jinr.core.utils.CommonUtil;
import com.jinr.core.utils.DensityUtil;
import com.jinr.core.utils.MyDES;
import com.jinr.core.utils.MyLog;
import com.jinr.core.utils.MyhttpClient;
import com.jinr.core.utils.PreferencesUtils;
import com.jinr.core.utils.ToastUtil;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.lzy.okgo.OkGo;

import org.apache.http.Header;
import org.json.JSONObject;
import org.simple.eventbus.EventBus;

import model.BaseModel;
import model.SCONF;
import model.UserInfo;

public class NewRegisterActivity extends BaseActivity<RegisterPresenter> implements View.OnClickListener {

    private ImageView image_back;
    private BezierCurve bc_curve;
    private XEditText et_phoneNum;
    public Button bt_firstNext;
    private TextView tv_protocol;
    private RelativeLayout rl_first;
    private RelativeLayout rl_second;
    private TextView tv_secondPhoneNum;
    private MyRegistEditText et_message;
    private TextView tv_sendAgain;
    private RoundProgressBar pb_round;
    private TextView tv_noMassage;
    public Button bt_secondNext;
    private RelativeLayout rl_three;
    private MyRegistEditText et_password;
    private TextView tv_look;
    public Button bt_threeNext;
    // 控制动画的距离 @author Ysw created at 2017/3/10 15:06
    private int times;
    // 控制第几步的状态 @author Ysw created at 2017/3/10 15:07
    private float mState = 0f;
    // 控制顶部进度进度的值 @author Ysw created at 2017/3/10 15:08
    private float value = 2;
    private float oldValue = 2;
    // 倒计时的线程 @author Ysw created at 2017/3/10 15:20
    private Thread mThread;
    // 控制计时线程暂停开始 @author Ysw created at 2017/3/10 17:46
    private boolean isRunning;
    // 倒计时时间 @author Ysw created at 2017/3/10 15:21
    private int mSecond = 60;
    // 动画时长 @author Ysw created at 2017/3/14 10:33
    private int mDuration = 500;
    private Handler mHandler;
    private boolean isShow = true;
    private boolean mClick = true;
    private TextView tv_time;
    private RelativeLayout rl_ok;
    private RegisterViewGroup vg_register;
    private RelativeLayout rl_success;
    private CannotReceiveTextDialog dialog;




    protected void findViewById() {
        image_back = (ImageView) findViewById(R.id.image_back);
        bc_curve = (BezierCurve) findViewById(R.id.bc_curve);
        tv_time = (TextView) findViewById(R.id.tv_time);
        rl_ok = (RelativeLayout) findViewById(R.id.rl_ok);
        rl_success = (RelativeLayout) findViewById(R.id.rl_success);
        vg_register = (RegisterViewGroup) findViewById(R.id.vg_register);

        rl_first = (RelativeLayout) findViewById(R.id.rl_first);
        et_phoneNum = (XEditText) findViewById(R.id.et_phoneNum);
        et_phoneNum.setSeparator(" ");
        et_phoneNum.setPattern(new int[]{3, 4, 4});
        bt_firstNext = (Button) findViewById(R.id.bt_firstNext);
        tv_protocol = (TextView) findViewById(R.id.tv_protocol);

        rl_second = (RelativeLayout) findViewById(R.id.rl_second);
        tv_secondPhoneNum = (TextView) findViewById(R.id.tv_secondPhoneNum);
        et_message = (MyRegistEditText) findViewById(R.id.et_message);
        tv_sendAgain = (TextView) findViewById(R.id.tv_sendAgain);
        pb_round = (RoundProgressBar) findViewById(R.id.pb_round);
        tv_noMassage = (TextView) findViewById(R.id.tv_noMassage);
        bt_secondNext = (Button) findViewById(R.id.bt_secondNext);

        rl_three = (RelativeLayout) findViewById(R.id.rl_three);
        et_password = (MyRegistEditText) findViewById(R.id.et_password);
        tv_look = (TextView) findViewById(R.id.tv_look);
        bt_threeNext = (Button) findViewById(R.id.bt_threeNext);

        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 1:
                        pb_round.setVisibility(View.GONE);
                        tv_sendAgain.setVisibility(View.VISIBLE);
                        break;
                    case 2:
                        pb_round.setVisibility(View.VISIBLE);
                        tv_sendAgain.setVisibility(View.GONE);
                        break;
                }
                super.handleMessage(msg);
            }
        };
        String mobile = getIntent().getStringExtra("mobile");
        if (!TextUtils.isEmpty(mobile)) {
            if (mobile.length() == 11) {
                et_phoneNum.setTextToSeparate(mobile);
                bt_firstNext.setOnClickListener(NewRegisterActivity.this);
                bt_firstNext.setBackgroundResource(R.drawable.btn_blue_bg);
            }
        }
    }


    protected void setListener() {
        image_back.setOnClickListener(this);
        tv_protocol.setOnClickListener(this);
        tv_sendAgain.setOnClickListener(this);
        tv_noMassage.setOnClickListener(this);
        tv_look.setOnClickListener(this);
        rl_ok.setOnClickListener(this);
        et_phoneNum.addTextChangedListener(new EditTextChangeListener() {
            @Override
            public void afterTextChanged(Editable s) {
                String str = s.toString().trim();
                if (str.length() == 13) {//多了2个空格
                    bt_firstNext.setOnClickListener(NewRegisterActivity.this);
                    bt_firstNext.setBackgroundResource(R.drawable.btn_blue_bg);
                } else {
                    bt_firstNext.setOnClickListener(null);
                    bt_firstNext.setBackgroundResource(R.drawable.btn_blue_light_bg);
                }
            }
        });
        et_message.addTextChangedListener(new EditTextChangeListener() {
            @Override
            public void afterTextChanged(Editable s) {
                String str = s.toString().trim();
                if (str.length() == 6) {//验证码 6 位
                    bt_secondNext.setOnClickListener(NewRegisterActivity.this);
                    bt_secondNext.setBackgroundResource(R.drawable.btn_blue_bg);
                } else {
                    bt_secondNext.setOnClickListener(null);
                    bt_secondNext.setBackgroundResource(R.drawable.btn_blue_light_bg);
                }
            }
        });
        et_password.addTextChangedListener(new EditTextChangeListener() {
            @Override
            public void afterTextChanged(Editable s) {
                String str = s.toString().trim();
                if (str.length() >= 6) {//密码6位
                    bt_threeNext.setOnClickListener(NewRegisterActivity.this);
                    bt_threeNext.setBackgroundResource(R.drawable.btn_blue_bg);
                } else {
                    bt_threeNext.setOnClickListener(null);
                    bt_threeNext.setBackgroundResource(R.drawable.btn_blue_light_bg);
                }
            }
        });
    }




    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (CommonUtil.isFastDoubleClick()) return false;
            if (mState > 0) {
                if (mState == 1) {
                    isRunning = false;
                    mSecond = 61;
                }
                delete();
                backTranslationAnimation(times);
                times--;
                return true;
            } else {
                finish();
                return true;
            }
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.image_back:
                if (CommonUtil.isFastDoubleClick()) return;
                if (mState > 0) {
                    if (mState == 1) {
                        isRunning = false;
                        mSecond = 61;
                    }
                    delete();
                    backTranslationAnimation(times);
                    times--;
                } else {
                    finish();
                }
                break;
            case R.id.tv_protocol:
                //金融用户协议
                Intent intent = new Intent(this, MyGiftActivity.class);
                intent.putExtra("url", UrlConfig.IP_M + UrlConfig.APPHELP_REGP);
                intent.putExtra("title", "用户协议");
                startActivity(intent);
                break;
            case R.id.tv_sendAgain:
                if (CommonUtil.isFastDoubleClick()) return;
                try {
                  //  getPhoneMessageAgain(et_phoneNum.toString().trim());
                    getP().getPhoneMessage(et_phoneNum.toString().trim(),true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.tv_noMassage:
                //收不到短信
                dialog = new CannotReceiveTextDialog(this);
                dialog.close_img.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                dialog.setCancelable();
                dialog.show();
                break;
            case R.id.tv_look:
                visiblely();
                break;
            case R.id.bt_firstNext:
                if (CommonUtil.isFastDoubleClick()) return;
                prepaerFirst();
                break;
            case R.id.bt_secondNext:
                if (CommonUtil.isFastDoubleClick()) return;
                try {
                    getP().verifyMessage(et_phoneNum.toString().trim(), et_message.getText().toString().trim());
                  //  verifyMessage(et_phoneNum.toString().trim(), et_message.getText().toString().trim());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.bt_threeNext:
                String password = et_phoneNum.toString().trim();
                if (CommonUtil.isFastDoubleClick()) {
                    return;
                }
                // 注册、忘记密码 只可以输入6-16 位混合(数字，字母)
                boolean b = CommonUtil.isPwdRegular(password);
                boolean b2 = CommonUtil.isPwCh(password);
                if (!b) {
                    // 提示相应信息
                    ToastUtil.show(this, R.string.pwd_alert);
                    return;
                }
                if (b2) {
                    ToastUtil.show(this, R.string.pwd_alert);
                    return;
                }
                try {
                    CommonUtil.hideKeyboard(NewRegisterActivity.this);
                    getP().register(et_phoneNum.toString().trim(), et_message.getText().toString().trim(),
                            et_password.getText().toString().trim());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.rl_ok:
                if (CommonUtil.isFastDoubleClick()) return;
                mClick = false;
                jump();
                break;
            default:
                break;
        }
    }


    public void visiblely() {
        isShow = !isShow;
        if (isShow) {
            tv_look.setText("隐藏");
            et_password.setInputType(InputType.TYPE_CLASS_TEXT);
        } else {
            tv_look.setText("显示");
            et_password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);// 隐藏密码
        }
        CharSequence text = et_password.getText();
        if (text != null) {
            Spannable spanText = (Spannable) text;
            Selection.setSelection(spanText, text.length());
        }
    }


    /**
     * 准备获取验证码 @author Ysw created at 2017/3/10 16:16
     */
    public void prepaerFirst() {
        if (!DensityUtil.isMobileNO(et_phoneNum.toString().trim())) {
            ToastUtil.show(this, "您输入的电话号码不正确");
            return;
        } else {
            try {
              //  checkMobile(et_phoneNum.toString().trim());
                getP().NewcheckMobile(et_phoneNum.toString().trim());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 开始下一步位移动画 @author Ysw created at 2017/3/10 16:16
     */
    private void startTranslationAnimation(int times) {
        ObjectAnimator one = ObjectAnimator.ofFloat(rl_first, "translationX", times * rl_first.getWidth(), (times + 1) * rl_first.getWidth()).setDuration(mDuration);
        one.start();
        ObjectAnimator two = ObjectAnimator.ofFloat(rl_second, "translationX", times * rl_second.getWidth(), (times + 1) * rl_second.getWidth()).setDuration(mDuration);
        two.start();
        ObjectAnimator three = ObjectAnimator.ofFloat(rl_three, "translationX", times * rl_three.getWidth(), (times + 1) * rl_three.getWidth()).setDuration(mDuration);
        three.start();
        if (times == 0) {
            startTranslationAlphaAnimation(rl_first);
            deleteTranslationAlphaAnimation(rl_second);
        } else if (times == 1) {
            startTranslationAlphaAnimation(rl_second);
            deleteTranslationAlphaAnimation(rl_three);
        } else if (times == 2) {
            startTranslationAlphaAnimation(rl_three);
        }
    }

    /**
     * 开始下一步的透明度动画 @author Ysw created at 2017/3/14 9:38
     */
    private void startTranslationAlphaAnimation(final View view) {
        ValueAnimator animator = ValueAnimator.ofFloat(1.0f, 0.0f);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                Float value = (Float) animation.getAnimatedValue();
                view.setAlpha(value);
            }
        });
        animator.setDuration(mDuration);
        animator.start();
    }

    /**
     * 返回上一步的透明动画 @author Ysw created at 2017/3/14 9:38
     */
    private void deleteTranslationAlphaAnimation(final View view) {
        ValueAnimator animator = ValueAnimator.ofFloat(0.0f, 1.0f);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                Float value = (Float) animation.getAnimatedValue();
                view.setAlpha(value);
            }
        });
        animator.setDuration(mDuration);
        animator.start();
    }

    /**
     * 开始下一步顶部进度动画 @author Ysw created at 2017/3/10 15:13
     */
    private void changeValue(final boolean isRun) {
        if (mState == 3)
            return;
        mState++;
        if (value < 11) {
            value += 5;
        } else if (value > 11 && value < 21) {
            value += 9;
        } else {
            value = 2;
        }
        ValueAnimator animator = ValueAnimator.ofFloat(oldValue, value);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                bc_curve.setProgrelss(value, mState, false);
            }
        });

        animator.addListener(new MyAnimationListener() {
            @Override
            public void onAnimationEnd(Animator animation) {
                oldValue = value;
                if (isRun) {
                    startTiming();
                }
            }
        });
        animator.setDuration(mDuration);
        animator.start();
    }

    /**
     * 开始返回上一步的位移动画 @author Ysw created at 2017/3/10 16:17
     */
    private void backTranslationAnimation(int times) {
        ObjectAnimator one = ObjectAnimator.ofFloat(rl_first, "translationX", times * rl_first.getWidth(), (times - 1) * rl_first.getWidth()).setDuration(mDuration);
        one.start();
        ObjectAnimator two = ObjectAnimator.ofFloat(rl_second, "translationX", times * rl_second.getWidth(), (times - 1) * rl_second.getWidth()).setDuration(mDuration);
        two.start();
        ObjectAnimator three = ObjectAnimator.ofFloat(rl_three, "translationX", times * rl_three.getWidth(), (times - 1) * rl_three.getWidth()).setDuration(mDuration);
        three.start();
        if (times == 1) {
            startTranslationAlphaAnimation(rl_second);
            deleteTranslationAlphaAnimation(rl_first);
        } else if (times == 2) {
            startTranslationAlphaAnimation(rl_three);
            deleteTranslationAlphaAnimation(rl_second);
        } else if (times == 3) {
            vg_register.setVisibility(View.VISIBLE);
            rl_success.setVisibility(View.GONE);
            deleteTranslationAlphaAnimation(rl_three);
        }
    }

    /**
     * 返回上一步顶部进度动画 @author Ysw created at 2017/3/10 15:13
     */
    private void delete() {
        if (mState == 0)
            return;
        mState--;
        if (value > 20) {
            value -= 9;
        } else if (value > 2 && value < 13) {
            value -= 5;
        } else {
            value = 2;
        }
        ValueAnimator animator = ValueAnimator.ofFloat(oldValue, value);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                bc_curve.setProgrelss(value, mState, true);
            }
        });
        animator.addListener(new MyAnimationListener() {
            @Override
            public void onAnimationEnd(Animator animation) {
                oldValue = value;
                // 动画结束的时候进行UI的变更 @author Ysw created at 2017/3/10 15:58
                Message message = mHandler.obtainMessage();
                message.what = 2;
                mHandler.sendMessage(message);
            }
        });
        animator.setDuration(mDuration);
        animator.start();
    }

    /**
     * 开始一分钟的倒计时动画 @author Ysw created at 2017/3/10 15:23
     */
    private void startTiming() {
        mThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (isRunning) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (mSecond > 0) {
                        mSecond--;
                        pb_round.setProgress((60 - mSecond) * 6f, "" + mSecond);
                    } else {
                        pb_round.setProgress((60 - mSecond) * 6f, "");
                        Message message = mHandler.obtainMessage();
                        message.what = 1;
                        mHandler.sendMessage(message);
                        mSecond = 60;
                        isRunning = false;
                    }
                }
            }
        });
        mThread.start();
    }


    public void GetMessageOk(String phoneNum){

        tv_secondPhoneNum.setText(phoneNum);
        startTranslationAnimation(times);
        times++;
        changeValue(true);
        isRunning = true;

    }
    public void GetMessageAgainOk(){

        pb_round.setVisibility(View.VISIBLE);
        tv_sendAgain.setVisibility(View.GONE);
        isRunning = true;
        startTiming();

    }
    public void GetMessageFaile(boolean isnull){
        if(!isnull){
            bt_firstNext.setOnClickListener(NewRegisterActivity.this);

        }else{
            bt_firstNext.setOnClickListener(null);
        }

    }

    public void GetSecondMessageFaile(boolean isnull){
        if(!isnull){
            bt_secondNext.setOnClickListener(NewRegisterActivity.this);

        }else{
            bt_secondNext.setOnClickListener(null);
        }

    }

    public void GetthreeNextMessageFaile(boolean isnull){
        if(!isnull){
            bt_threeNext.setOnClickListener(NewRegisterActivity.this);

        }else{
            bt_threeNext.setOnClickListener(null);
        }

    }

    public void CheckMobileOk(){

        //已注册
        NewCustomDialogNoTitle dialog = new NewCustomDialogNoTitle(NewRegisterActivity.this, "");
        dialog.dialog_message.setText(Html.fromHtml("该手机号已被注册<br>" + "<font color='#0c72e3'>" + "请前往登录" + "</font>"));
        dialog.btn_custom_dialog_sure.setText("马上登录");
        dialog.btn_custom_dialog_sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NewRegisterActivity.this, NewLoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
        dialog.show();


    }



    public void VerifyOk(){
            startTranslationAnimation(times);
            times++;
            changeValue(false);

    }


    /**
     * 验证手机短信验证码是否正确 @author Ysw created at 2017/3/10 16:05
     */
    private void verifyMessage(final String phoneNum, final String phoneMessage) throws Exception {
        bt_secondNext.setOnClickListener(null);
        RequestParams params = new RequestParams();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("tel", phoneNum);
        jsonObject.put("code", phoneMessage);
        jsonObject.put("isDestroy", false);
        jsonObject.put("kw", MessageType.MESSAGE_MOBILE_ZCXX);
        params.put("cipher", MyDES.encrypt(jsonObject.toString()));
        MyhttpClient.desPost(UrlConfig.SMS_VERIFYUSER, params, new AsyncHttpResponseHandler() {
            @Override
            public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
                super.onFailure(arg0, arg1, arg2, arg3);
                bt_secondNext.setOnClickListener(NewRegisterActivity.this);
                ToastUtil.show(NewRegisterActivity.this, R.string.default_error);
            }

            @Override
            public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
                super.onSuccess(arg0, arg1, arg2);
                bt_secondNext.setOnClickListener(NewRegisterActivity.this);
                try {
                    String response = new String(arg2, "UTF-8");
                    response = CommonUtil.transResponse(response);
                    response = CommonUtil.transResponse(response);
                    JSONObject job = new JSONObject(response);
                    String cipher = job.getString("cipher");
                    String desc = MyDES.decrypt(cipher);
                    JSONObject obj = new JSONObject(desc);
                    int isSuccess = obj.getInt("code");
                    if (isSuccess == SCONF.SUCCESS) {
                        startTranslationAnimation(times);
                        times++;
                        changeValue(false);
                    } else {
                        ToastUtil.show(NewRegisterActivity.this, obj.getString("msg"));
                    }
                } catch (Exception ignored) {
                }
            }
        });
    }

    /**
     * 注册接口 @author Ysw created at 2017/3/10 16:14
     */
    public void register(final String phoneNum, String phoneMessage, final String password) throws Exception {
        bt_threeNext.setOnClickListener(null);
        RequestParams params = new RequestParams();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("tel", phoneNum);
        jsonObject.put("checksms", phoneMessage);
        jsonObject.put("password", password);
        jsonObject.put("type", "4");
        jsonObject.put("platform", UrlConfig.PLATFORM);
        params.put("cipher", MyDES.encrypt(jsonObject.toString()));
        MyhttpClient.desPost(UrlConfig.USER_REGISTER, params, new AsyncHttpResponseHandler() {

            @Override
            public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
                super.onFailure(arg0, arg1, arg2, arg3);
                bt_secondNext.setOnClickListener(NewRegisterActivity.this);
                ToastUtil.show(NewRegisterActivity.this, R.string.default_error);
            }


            @Override
            public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
                super.onSuccess(arg0, arg1, arg2);
                bt_secondNext.setOnClickListener(NewRegisterActivity.this);
               // dismissWaittingDialog();
                try {
                    String response = new String(arg2, "UTF-8");
                    response = CommonUtil.transResponse(response);
                    JSONObject jsonObject = new JSONObject(response);
                    String cipher = jsonObject.getString("cipher");
                    String desc = MyDES.decrypt(cipher);
                    JSONObject job = new JSONObject(desc);
                    MyLog.i("注册返回", response);
                    int code = job.getInt("code");
                    if (code == 1000) {
                        startTranslationAnimation(times);
                        times++;
                        changeValue(false);
                        vg_register.setVisibility(View.GONE);
                        rl_success.setVisibility(View.VISIBLE);
                        // 注册成功之后直接调用登录接口 @author Ysw created at 2017/3/14 11:13
                        autoLogin(phoneNum, password);
                    } else {
                        ToastUtil.show(getApplication(), job.getString("msg"));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 注册成功，自动登录 @author Ysw created at 2017/3/14 11:21
     */
    private void autoLogin(String phoneNum, String Password) throws Exception {
        RequestParams params = new RequestParams();
        JSONObject jsonObject = new JSONObject();
        // 获取手机的设备号码 @author Ysw created at 2017/3/14 11:15
        String equipment_number = JinrApp.instance().g_imei;
        jsonObject.put("tel", phoneNum);
        jsonObject.put("password", Password);
        jsonObject.put("equipment_number", equipment_number);
        jsonObject.put("use_terminal", "android");
        params.put("cipher", MyDES.encrypt(jsonObject.toString()));
        MyhttpClient.desPost(UrlConfig.USER_LOGIN, params, new AsyncHttpResponseHandler() {

            @Override
            public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
                super.onFailure(arg0, arg1, arg2, arg3);
               // dismissWaittingDialog();
                ToastUtil.show(NewRegisterActivity.this, R.string.default_error);
            }

            @Override
            public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
                super.onSuccess(arg0, arg1, arg2);
            //    dismissWaittingDialog();
                try {
                    String response = new String(arg2, "UTF-8");
                    response = CommonUtil.transResponse(response);
                    JSONObject jsonObject = new JSONObject(response);
                    String cipher = jsonObject.getString("cipher");
                    String desc = MyDES.decrypt(cipher);
                    MyLog.d("DES", "登录返回~~:" + desc);
                    if (TextUtils.isEmpty(response))
                        return;
                    BaseModel<UserInfo> result = new Gson().fromJson(desc, new TypeToken<BaseModel<UserInfo>>() {
                    }.getType());
                    if (result.isSuccess()) {
                        /**
                         * 成功登录之后对数据的保存和写入，具体还待分析 @author Ysw created at 2017/3/14 11:22
                         */
                        EventBus.getDefault().post(result.getData().getId(), EventBusKey.LOGIN_SUCCESS);
                        JinrApp.instance().state_bankBind = Integer.valueOf(result.getData().getBk());
                        PreferencesUtils.putIntToSPMap(NewRegisterActivity.this, PreferencesUtils.Keys.IS_BIND_CARD, Integer.valueOf(result.getData().getBk()));
                        PreferencesUtils.putValueToSPMap(NewRegisterActivity.this, PreferencesUtils.Keys.UID, result.getData().getId());
                        PreferencesUtils.putValueToSPMap(NewRegisterActivity.this, PreferencesUtils.Keys.NICKNAME, result.getData().getNikename());
                        PreferencesUtils.putValueToSPMap(NewRegisterActivity.this, PreferencesUtils.Keys.TEL, result.getData().getTel());
                        PreferencesUtils.putValueToSPMap(NewRegisterActivity.this, PreferencesUtils.Keys.NAME, result.getData().getName());
                        PreferencesUtils.putValueToSPMap(NewRegisterActivity.this, PreferencesUtils.Keys.BUSS_PWD, result.getData().getBuss_pwd());
                        PreferencesUtils.putValueToSPMap(NewRegisterActivity.this, PreferencesUtils.Keys.ID_CARD, result.getData().getCard_id());
                        PreferencesUtils.putBooleanToSPMap(NewRegisterActivity.this, PreferencesUtils.Keys.IS_LOGIN, true);
                        if (!result.getData().getId().equals(PreferencesUtils.getSValueFromSPMap(NewRegisterActivity.this, PreferencesUtils.Keys.UID))) {
                            PreferencesUtils.clearShPMap(NewRegisterActivity.this);
                            PreferencesUtils.putSValueToSPMap(NewRegisterActivity.this, PreferencesUtils.Keys.UID, result.getData().getId());
                        }
                        // 根据网络获取 判断 是否实名 有无支付密码
                        if (!"".equals(PreferencesUtils.getValueFromSPMap(NewRegisterActivity.this, PreferencesUtils.Keys.NAME)) &&
                                !"".equals(PreferencesUtils.getValueFromSPMap(NewRegisterActivity.this, PreferencesUtils.Keys.ID_CARD))) {// 实名
                            PreferencesUtils.putBooleanToSPMap(NewRegisterActivity.this, PreferencesUtils.Keys.IS_IDENTIFY, true);
                        }
                        if (!"".equals(PreferencesUtils.getValueFromSPMap(NewRegisterActivity.this, PreferencesUtils.Keys.BUSS_PWD))) {// 交易密码
                            PreferencesUtils.putBooleanToSPMap(NewRegisterActivity.this, PreferencesUtils.Keys.HAS_DEAL_PASSWD, true);
                        }
                        ToastUtil.show(getApplication(), "登录成功3S之后自动跳转到资产界面");
                        // 登录成功3S之后自动跳转到资产界面 @author Ysw created at 2017/3/14 11:20
                        autoJump();
                    } else {
                        ToastUtil.show(getApplication(), result.getMsg());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 注册成功后3s自动跳转 @author Ysw created at 2017/3/10 19:13
     */
    public void autoJump() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                int num = 3;
                while (num > 0) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    num--;
                    final int finalNum = num;
                    tv_time.post(new Runnable() {
                        @Override
                        public void run() {
                            tv_time.setText("(" + finalNum + ")" + "s");
                        }
                    });
                    if (num == 0 && mClick) {
                        Intent intent = new Intent();
                        intent.setClass(NewRegisterActivity.this, MyAssetsActivity.class);
                        intent.putExtra("isRegister", true);
                        startActivity(intent);
                        finish();
                    }
                }
            }
        }).start();
    }

    /**
     * 点击好的按钮进行相应的跳转 @author Ysw created at 2017/3/10 19:29
     */
    public void jump() {
        Intent intent = new Intent();
        intent.setClass(NewRegisterActivity.this, MyAssetsActivity.class);
        intent.putExtra("isRegister", true);
        startActivity(intent);
        finish();
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        findViewById();
        setListener();
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_new_register;
    }

    @Override
    public RegisterPresenter newP() {
        return new RegisterPresenter();
    }

    public void RegisterSuccess() {
        bt_secondNext.setOnClickListener(NewRegisterActivity.this);
        startTranslationAnimation(times);
        times++;
        changeValue(false);
        vg_register.setVisibility(View.GONE);
        rl_success.setVisibility(View.VISIBLE);


    }
}
