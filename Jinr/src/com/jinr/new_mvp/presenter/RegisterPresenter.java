package com.jinr.new_mvp.presenter;

import android.util.Log;

import com.google.gson.JsonObject;
import com.jinr.core.R;
import com.jinr.core.config.EventBusKey;
import com.jinr.core.config.UrlConfig;
import com.jinr.core.utils.PreferencesUtils;
import com.jinr.core.utils.ToastUtil;
import com.jinr.new_mvp.network.DialogMd5JsonCallBack;
import com.jinr.new_mvp.network.Md5JsonCallBack;
import com.jinr.new_mvp.params.SmsMethod;
import com.jinr.new_mvp.params.UserMethod;
import com.jinr.new_mvp.ui.activity.NewRegisterActivity;
import com.lzy.okgo.OkGo;

import org.json.JSONException;
import org.json.JSONObject;
import org.simple.eventbus.EventBus;

import cn.droidlover.xdroidmvp.mvp.XPresent;
import model.BaseModel;
import model.LoginResult;
import model.SimpleResponse;
import okhttp3.Call;
import okhttp3.Response;

import static com.jinr.new_mvp.params.UserMethod.REGISTER;

/**
 * Created by:yuzhongrong on 2017/5/31.
 */

public class RegisterPresenter extends XPresent<NewRegisterActivity>{


    /**
     * 检测手机是否注册
     * @param mobile
     * @throws Exception
     */
    public void NewcheckMobile(final String mobile) throws Exception {

        JSONObject params=new JSONObject();
        params.put("method",UserMethod.LOGIN_CHECKN);
        params.put("mobilephone",mobile);

        OkGo.post(UrlConfig.BASE)
                .tag(getV())
                .execute(new Md5JsonCallBack<SimpleResponse<Void>>(params) {

                    @Override
                    public void onError(Call call, Response response, Exception e) {

                        ToastUtil.show(getV(),e.getMessage());
                    }

                    @Override
                    public void onSuccess(SimpleResponse<Void> voidSimpleResponse, Call call, Response response) {
                        if(voidSimpleResponse.getCode()==200){
                            getV().CheckMobileOk();//已经注册跳转到登录
                        }else{
                            try {
                                //未注册去获取注册验证代码
                                getV().GetMessageFaile(true);
                                getPhoneMessage(mobile,false);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                    }
                });
    }



    /**
     * 获取手机短信验证码
     * 重新获取手机短信验证码，由于动画原因，故添加了个again
     * @param phoneNum
     */
   public void getPhoneMessage(final String phoneNum,final boolean again){

       JSONObject params=new JSONObject();
       try {
           params.put("method", SmsMethod.SMS_CODE);
           params.put("mobilephone",phoneNum);
           params.put("type", SmsMethod.Type.MESSAGE_MOBILE_ZCXX);
       } catch (JSONException e) {
           e.printStackTrace();
       }
       OkGo.post(UrlConfig.BASE)
               .tag(getV())
               .execute(new DialogMd5JsonCallBack<SimpleResponse<Void>>(params) {


                   @Override
                   public void onError(Call call, Response response, Exception e) {
                       super.onError(call, response, e);
                       getV().GetMessageFaile(false);
                       ToastUtil.show(getV(),e.getMessage());
                   }

                   @Override
                   public void onSuccess(SimpleResponse<Void> voidSimpleResponse, Call call, Response response) {
                       if(voidSimpleResponse.getCode()==200){
                           if(again==false) {
                               getV().GetMessageOk(phoneNum);
                           }else{
                               getV().GetMessageAgainOk();
                           }

                       }else{
                           ToastUtil.show(getV(), voidSimpleResponse.getMsg());
                       }
                   }
               });




   }


    /**
     * 验证手机短信验证码是否正确 @author Ysw created at 2017/3/10 16:05
     */
    public void verifyMessage(final String phoneNum, final String code) throws Exception {

         JSONObject  params=new JSONObject();
          params.put("method", SmsMethod.VERIFY);
          params.put("mobilephone",phoneNum);
          params.put("code",code);
          params.put("is_destroy","0");
          params.put("type",SmsMethod.Type.MESSAGE_MOBILE_ZCXX);
        OkGo.post(UrlConfig.BASE)
                .tag(getV())
                .execute(new Md5JsonCallBack<SimpleResponse<Void>>(params) {
                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        getV().GetSecondMessageFaile(false);
                        ToastUtil.show(getV(), response.message());
                    }

                    @Override
                    public void onSuccess(SimpleResponse<Void> voidSimpleResponse, Call call, Response response) {
                        getV().GetSecondMessageFaile(false);
                        getV().VerifyOk();
                    }
                });



    }



    /**
     * 注册接口 @author Ysw created at 2017/3/10 16:14
     */
    public void register(final String phoneNum, String phoneMessage, final String password) throws Exception {

        getV().GetthreeNextMessageFaile(true);
        JSONObject params=new JSONObject();
        params.put("",REGISTER);
        OkGo.post(UrlConfig.BASE)
                .tag(getV())
                .execute(new Md5JsonCallBack<SimpleResponse<Void>>(params) {

                    @Override
                    public void onError(Call call, Response response, Exception e) {

                    }

                    @Override
                    public void onSuccess(SimpleResponse<Void> voidSimpleResponse, Call call, Response response) {

                      if(voidSimpleResponse.getCode()==200){

                          getV().RegisterSuccess();//update ui
                          try {
                              autoLogin(phoneNum, password);
                          } catch (Exception e) {
                              e.printStackTrace();
                          }

                      }else{
                          ToastUtil.show(getV(), voidSimpleResponse.getMsg());
                      }
                    }
                });





    }

    /**
     * 注册成功，自动登录 @author Ysw created at 2017/3/14 11:21 此函数可在后期放到CommonPresenter中做个公共登录处理
     */


    private void autoLogin(String phoneNum, String password) {

        JSONObject params=new JSONObject();
        try {

            params.put("method", UserMethod.LOGIN);
            params.put("mobilephone",phoneNum);
            params.put("password",password);
            params.put("login_type","pwd");
            params.put("device_token", PreferencesUtils.getStringToKey(PreferencesUtils.Keys.DEVICE_TOKEN,""));

        } catch (JSONException e) {
            e.printStackTrace();
        }
        OkGo.post(UrlConfig.BASE)
                .tag(getV())
                .execute(new DialogMd5JsonCallBack<BaseModel<LoginResult>>(params) {

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        ToastUtil.show(getV(),response.message());
                    }

                    @Override
                    public void onSuccess(BaseModel<LoginResult> result, Call call, Response response) {

                        if(result.getCode()==200){
                            EventBus.getDefault().post("", EventBusKey.LOGIN_SUCCESS);
                            //PreferencesUtils.save(New_PreferencesUtils.LOGINRESULT,result.getData());//保存用户基本数据
                            PreferencesUtils.putValueToSPMap(getV(),PreferencesUtils.Keys.UID, result.getData().getUid());//保存登录状态
                            PreferencesUtils.putValueToSPMap(getV(), PreferencesUtils.Keys.TEL, result.getData().getMobilephone());
                            PreferencesUtils.putValueToSPMap(getV(), PreferencesUtils.Keys.NAME, result.getData().getName());
                            PreferencesUtils.putBooleanToSPMap(getV(),PreferencesUtils.Keys.IS_LOGIN, true);//保存登录状态

                            ToastUtil.show(getV(), "登录成功3S之后自动跳转到资产界面");
                            // 登录成功3S之后自动跳转到资产界面 @author Ysw created at 2017/3/14 11:20
                             getV().autoJump();



                        }else{

                            ToastUtil.show(getV(), result.getMsg());

                        }


                    }


                });


    }




}
