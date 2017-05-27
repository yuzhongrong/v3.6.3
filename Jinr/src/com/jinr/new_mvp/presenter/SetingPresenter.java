package com.jinr.new_mvp.presenter;

import android.content.Intent;

import com.jinr.core.MainActivity;
import com.jinr.core.config.AppManager;
import com.jinr.core.config.UrlConfig;
import com.jinr.core.utils.PreferencesUtils;
import com.jinr.core.utils.ToastUtil;
import com.jinr.new_mvp.network.DialogMd5JsonCallBack;
import com.jinr.new_mvp.ui.activity.SettingActivity;
import com.lzy.okgo.OkGo;

import org.json.JSONException;
import org.json.JSONObject;

import cn.droidlover.xdroidmvp.mvp.XPresent;
import model.SimpleResponse;
import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by: yuzhongrong on 2017/5/25.
 */

public class SetingPresenter extends XPresent<SettingActivity> {

    public void RequestLogout() throws JSONException {

        JSONObject params=new JSONObject();
        params.put("device_token",PreferencesUtils.getStringToKey(PreferencesUtils.Keys.DEVICE_TOKEN,""));
        OkGo.post(UrlConfig.BASE)
                .tag(getV())
                .execute(new DialogMd5JsonCallBack<SimpleResponse<Void>>(params) {

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        ToastUtil.show(getV(),e.getMessage());
                    }

                    @Override
                    public void onSuccess(SimpleResponse<Void> voidSimpleResponse, Call call, Response response) {

                        if(voidSimpleResponse.getCode()==200){

                            PreferencesUtils.putLastTelToSPMap(PreferencesUtils.Keys.TEL, PreferencesUtils.getValueFromSPMap(getV(), PreferencesUtils.Keys.TEL));
                            PreferencesUtils.clearSPMap(getV());
                            AppManager.getAppManager().finishAllActivity();

                            getV().startActivity(new Intent(getV(), MainActivity.class));

                            ToastUtil.show(getV(),voidSimpleResponse.getMsg());


                        }


                    }
                });



    }






}
