/*
 * MyhttpClient.java
 * @author Andrew Lee
 * 2014-10-20 上午11:30:12
 */
package com.jinr.core.utils;

import android.content.Context;
import android.text.TextUtils;

import com.jinr.core.JinrApp;
import com.jinr.core.config.UrlConfig;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.MySSLSocketFactory;
import com.loopj.android.http.RequestParams;

import org.apache.http.conn.ssl.SSLSocketFactory;
import org.json.JSONObject;

import java.io.InputStream;
import java.security.KeyStore;

/**
 * MyhttpClient.java description:
 *
 * @author Andrew Lee version 2014-10-20 上午11:30:12
 */
public class MyhttpClient {

    private static final String BASE_URL_DES = UrlConfig.BASE_IP_DES;
    public static AsyncHttpClient client = new AsyncHttpClient();
    public static AsyncHttpClient client_des = new AsyncHttpClient(true, 80, 443);
    final static int DEFAULT_TIMEOUT = 20 * 1000;
    private static boolean ISHTTPS = true;

    public static void desPost(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) throws Exception {
        // 头部参数
        String imei = JinrApp.instance().g_imei;
        JSONObject obj = new JSONObject();
        obj.put("code", url + imei + TimeUtil.getNowDate());
        obj.put("app_ver", JinrApp.instance().getVersion());
        String desHeader = MyDES.encrypt(obj.toString()).replace("\n", ""); // 头部格式不允许出现"\n"，否则影响body传参，导致服务端无法获取body参数
        client_des.setTimeout(DEFAULT_TIMEOUT);
        client_des.addHeader("code", desHeader);
        if (ISHTTPS) {
            client_des.setSSLSocketFactory(getSocketFactory(JinrApp.instance()));
        }

        MyLog.e("BaseUrl", "访问地址：" + BASE_URL_DES + url + "?" + params.toString());
        MyLog.e("sk", "头部参数：" + MyDES.decrypt(desHeader.replace("cipher=", "")));
        if (!TextUtils.isEmpty(params.toString())) {
            MyLog.e("sk", "请求参数：" + MyDES.decrypt(params.toString().replace("cipher=", "")));
        }

        client_des.post(getAbsoluteUrlDes(url), params, responseHandler);
    }

    private static String getAbsoluteUrlDes(String relativeUrl) {
//        MyLog.i("访问地址", BASE_URL_DES + relativeUrl);
        return BASE_URL_DES + relativeUrl;
    }

    private static SSLSocketFactory getSocketFactory(Context context) {
        SSLSocketFactory sslFactory = null;
        try {
            KeyStore keyStore = KeyStore.getInstance("BKS");
            InputStream instream = context.getAssets().open("jinr_api.bks");
            keyStore.load(instream, null);
            sslFactory = new MySSLSocketFactory(keyStore);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return sslFactory;
    }
}
