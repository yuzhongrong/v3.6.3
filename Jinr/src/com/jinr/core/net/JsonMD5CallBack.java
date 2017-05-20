package com.jinr.core.net;

import android.util.Log;

import com.android.jnet.Jnetapp;
import com.android.jnet.utils.CommonUtil;
import com.android.jnet.utils.GetImsi;
import com.jinr.core.JinrApp;
import com.jinr.core.config.UrlConfig;
import com.jinr.core.utils.MD5;
import com.lzy.okgo.callback.AbsCallback;
import com.lzy.okgo.request.BaseRequest;

import org.json.JSONException;
import org.json.JSONObject;

import cn.droidlover.xdroidmvp.log.XLog;
import okhttp3.Response;

/**
 * Created by yuzhongrong on 2017/5/12.
 */

public abstract class JsonMD5CallBack<T> extends AbsCallback<T> {


    private JSONObject baseJson;
    public JsonMD5CallBack(JSONObject baseJson){

        this.baseJson=baseJson;


    }


    @Override
    public void onBefore(BaseRequest request) {
        super.onBefore(request);

        // 主要用于在所有请求之前添加公共的请求头或请求参数
        // 例如登录授权的 token
        // 使用的设备信息
        // 可以随意添加,也可以什么都不传
        // 还可以在这里对所有的参数进行加密，均在这里实现

        String imei = GetImsi.getImeiAll(Jnetapp.getInstance());
        String md5Str="";
        try {

            baseJson.put("v",JinrApp.instance().getVersion());
            baseJson.put("appid", UrlConfig.appid);
            baseJson.put("timestamp",System.currentTimeMillis());
            baseJson.put("unique_id",JinrApp.instance().g_imei);
            String jsonStr=baseJson.toString()+UrlConfig.key;
            md5Str= MD5.getMD5(jsonStr).toUpperCase();

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        Log.d("JsonMD5CallBack", "md5Str----->:" + md5Str);

        request.params("sign",md5Str)
               .params("data",baseJson.toString());


    }




    @Override
    public T convertSuccess(Response response) throws Exception {

        String jsonReader = response.body().string();
        jsonReader = CommonUtil.transResponse(jsonReader);
        XLog.json(jsonReader);
       // JSONObject jsb = new JSONObject(jsonReader);
      //  String cipher = jsb.getString("cipher");
      //  cipher = MyDES.decrypt(cipher);
        return  subConvertSuccess(response,jsonReader);


    }


    public abstract T subConvertSuccess(Response response,String cipher);//由依賴項目子類實現 可以參考forexample






}
