package com.jinr.new_mvp.network;

import android.util.Log;

import com.android.jnet.utils.Convert;

import org.json.JSONObject;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import model.BaseModel;
import okhttp3.Response;

/**
 * Created by yuzhongrong on 2017/5/12.
 */

public abstract class Md5JsonCallBack<T> extends JsonMD5CallBack<T> {




    public Md5JsonCallBack(JSONObject baseJson) {
        super(baseJson);


    }


    @Override
    public T subConvertSuccess(Response response, String result) {
        /*********转换**********/
        Type genType = getClass().getGenericSuperclass();
        Type[] params = ((ParameterizedType) genType).getActualTypeArguments();
        Type type = params[0];
        if (!(type instanceof ParameterizedType)) throw new IllegalStateException("没有填写泛型参数");
        Type rawType = ((ParameterizedType) type).getRawType();
        Type typeArgument = ((ParameterizedType) type).getActualTypeArguments()[0];
        // JsonReader jsonReader = new JsonReader(response.body().charStream());
        if (typeArgument == Void.class) {
            //无数据类型,表示没有data数据的情况（以  new DialogCallback<LzyResponse<Void>>(this)  以这种形式传递的泛型)
            T t = Convert.fromJson(result, rawType);
            response.close();
            return t;
        } else if (rawType == BaseModel.class) {
            //有数据类型，表示有data
            BaseModel<T> tBaseModel = Convert.fromJson(result, type);
            response.close();
            int code = tBaseModel.getCode();
            Log.d("Md5JsonCallBack", "mTokenFaile:");

            //这里的0是以下意思
            //一般来说服务器会和客户端约定一个数表示成功，其余的表示失败，这里根据实际情况修改
            if (code == 200) {

                //noinspection unchecked
                return (T)tBaseModel;
            } else if (code == 104) {
                //比如：用户授权信息无效，在此实现相应的逻辑，弹出对话或者跳转到其他页面等,该抛出错误，会在onError中回调。
                throw new IllegalStateException("用户授权信息无效");
            } else if (code == 105) {
                //比如：用户收取信息已过期，在此实现相应的逻辑，弹出对话或者跳转到其他页面等,该抛出错误，会在onError中回调。
                throw new IllegalStateException("用户收取信息已过期");
            } else if (code == 106) {
                //比如：用户账户被禁用，在此实现相应的逻辑，弹出对话或者跳转到其他页面等,该抛出错误，会在onError中回调。
                throw new IllegalStateException("用户账户被禁用");
            } else if (code == 300) {
                //比如：其他乱七八糟的等，在此实现相应的逻辑，弹出对话或者跳转到其他页面等,该抛出错误，会在onError中回调。
                throw new IllegalStateException("其他乱七八糟的等");
            }
            else if (code == 401) {
                //比如：其他乱七八糟的等，在此实现相应的逻辑，弹出对话或者跳转到其他页面等,该抛出错误，会在onError中回调。
             //  throw new IllegalStateException("其他乱七八糟的等");


            }


            else {
                throw new IllegalStateException("错误代码：" + code + "，错误信息：" + tBaseModel.getMsg());
            }
        } else {
            response.close();
            throw new IllegalStateException("基类错误无法解析!");
        }


        return null;
    }


}
