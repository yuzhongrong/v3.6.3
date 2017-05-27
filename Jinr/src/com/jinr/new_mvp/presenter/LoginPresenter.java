package com.jinr.new_mvp.presenter;

import android.util.Log;

import com.jinr.core.config.EventBusKey;
import com.jinr.core.config.UrlConfig;
import com.jinr.core.utils.PreferencesUtils;
import com.jinr.core.utils.ToastUtil;
import com.jinr.new_mvp.network.DialogMd5JsonCallBack;
import com.jinr.new_mvp.network.Md5JsonCallBack;
import com.jinr.new_mvp.params.UserMethod;
import com.jinr.new_mvp.ui.activity.NewLoginActivity;
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

/**
 * Created by: yuzhongrong on 2017/5/25.
 */

public class LoginPresenter extends XPresent<NewLoginActivity> {

    private static final String TAG="LoginPresenter" ;




    public void login(String mobilephone,String password){
        NewCheckMobile(mobilephone,password);
    }





    private  void NewCheckMobile(final String mobilephone,final String password){



        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("method", UserMethod.LOGIN_CHECKN);
            jsonObject.put("mobilephone",mobilephone);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        OkGo.post(UrlConfig.BASE)
                .tag(getV())
                .execute(new Md5JsonCallBack<SimpleResponse<Void>>(jsonObject) {

                    @Override
                    public void onError(Call call, Response response, Exception e) {

                        Log.d(TAG, "onError:");
                        ToastUtil.show(getV(),response.message());
                    }

                    @Override
                    public void onSuccess(SimpleResponse<Void> voidSimpleResponse, Call call, Response response) {

                        Log.d(TAG, "onSuccess:");
                        if(voidSimpleResponse.getCode()==200){

                            //登录
                            NewLogin(mobilephone,password);


                        }else{

                            getV().ToRegist(mobilephone);

                        }
                    }
                });


    }





    private void NewLogin(String tel,String password) {

        JSONObject params=new JSONObject();
        try {

            params.put("method", UserMethod.LOGIN);
            params.put("mobilephone",tel);
            params.put("password",password);
            params.put("login_type","pwd");
            params.put("device_token",PreferencesUtils.getStringToKey(PreferencesUtils.Keys.DEVICE_TOKEN,""));

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
                        Log.d(TAG, "voidLoginResult:" + result);
                        if(result.getCode()==200){
                            getV().ClearLock();
                            EventBus.getDefault().post("", EventBusKey.LOGIN_SUCCESS);
                            //PreferencesUtils.save(New_PreferencesUtils.LOGINRESULT,result.getData());//保存用户基本数据
                            PreferencesUtils.putValueToSPMap(getV(),PreferencesUtils.Keys.UID, result.getData().getUid());//保存登录状态
                            PreferencesUtils.putValueToSPMap(getV(), PreferencesUtils.Keys.TEL, result.getData().getMobilephone());
                            PreferencesUtils.putValueToSPMap(getV(), PreferencesUtils.Keys.NAME, result.getData().getName());
                            PreferencesUtils.putBooleanToSPMap(getV(),PreferencesUtils.Keys.IS_LOGIN, true);//保存登录状态

                            //通知ProductFragment刷新三倍收益
                            EventBus.getDefault().post(true, EventBusKey.REFRESH_MULTIPLE);
                            getV().finish();




                        }else{

                            ToastUtil.show(getV(), result.getMsg());

                        }


                    }


                });


    }





}
