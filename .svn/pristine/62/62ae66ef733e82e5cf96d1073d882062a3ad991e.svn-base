package com.jinr.core;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jinr.core.base.BaseData;
import com.jinr.core.config.UrlConfig;
import com.jinr.core.guide.GuideActivity;
import com.jinr.core.utils.CommonUtil;
import com.jinr.core.utils.FileUtil;
import com.jinr.core.utils.MyDES;
import com.jinr.core.utils.MyhttpClient;
import com.jinr.core.utils.PreferencesUtils;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONObject;

import model.AdvertModel;
import model.BaseModel;

/**
 * 闪屏界面
 *
 * @author Administrator
 * @since 2015-1-21 08:34:59
 */
public class ImageActivity extends Activity {
    public static ImageActivity imginstance = null;// 单例
    public boolean is_pref = false;
    private boolean isGet = false;// 是否有新的图片地址
    private boolean isGone = false;// 是否关闭掉广告页
    private boolean isShow = false;// 是否要重新下载广告页
    private BaseModel<AdvertModel> result;
    private Handler handler = new Handler();
    private Runnable runnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);
        if (CommonUtil.checkNetState(ImageActivity.this)) {
            try {
                getImgAdd();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        imginstance = this;
        is_pref = PreferencesUtils.getMaskBooleanFromSPMap(this, PreferencesUtils.Keys.IS_FIRST_PREF);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (runnable != null) {
            handler.removeCallbacks(runnable);
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        if (hasFocus) {
            handler.postDelayed(runnable = new Runnable() {
                @Override
                public void run() {
                    if (is_pref) {
                        Intent it = new Intent(ImageActivity.this, GuideActivity.class);
                        startActivity(it);
                        finish();
                    } else {
                        if (isGone) {
                            FileUtil.delAllFiles(BaseData.SD_CARD_PATH);
                            PreferencesUtils.putCValueToSPMap(ImageActivity.this, "advId", "");
                            PreferencesUtils.putCValueToSPMap(ImageActivity.this, "img", "");
                            PreferencesUtils.putCValueToSPMap(ImageActivity.this, "linkId", "");
                            Intent it = new Intent(ImageActivity.this, MainActivity.class);
                            startActivity(it);
                            finish();
                        } else {
                            if (isGet) {
                                Intent it = new Intent(ImageActivity.this, AdvertisementActivity.class);
                                it.putExtra("isShow", isShow);
                                it.putExtra("advert", result.getData());
                                startActivity(it);
                                finish();
                            } else {
                                if (!PreferencesUtils.getCValueFromSPMap(ImageActivity.this, "advId").equals("")) {
                                    Intent it = new Intent(ImageActivity.this, AdvertisementActivity.class);
                                    AdvertModel advertModel = new AdvertModel();
                                    advertModel.setId(PreferencesUtils.getCValueFromSPMap(ImageActivity.this, "advId"));
                                    advertModel.setImg(PreferencesUtils.getCValueFromSPMap(ImageActivity.this, "img"));
                                    advertModel.setLinkid(PreferencesUtils.getCValueFromSPMap(ImageActivity.this, "linkId"));
                                    it.putExtra("isShow", isShow);
                                    it.putExtra("advert", advertModel);
                                    startActivity(it);
                                    finish();
                                } else {
                                    Intent it = new Intent(ImageActivity.this, MainActivity.class);
                                    startActivity(it);
                                    finish();
                                }
                            }
                        }
                    }
                }
            }, 2000);
        }
        super.onWindowFocusChanged(hasFocus);
    }

    // 获取广告页的图片地址
    private void getImgAdd() throws Exception {
        RequestParams params = new RequestParams();
        JSONObject cipher = new JSONObject();
        cipher.put("type", "3");
        params.put("cipher", MyDES.encrypt(cipher.toString()));
        MyhttpClient.desPost(UrlConfig.USER_NOTICE, params, new AsyncHttpResponseHandler() {
            @Override
            public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
                super.onFailure(arg0, arg1, arg2, arg3);
                isGet = false;
            }

            @Override
            public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
                super.onSuccess(arg0, arg1, arg2);
                try {
                    String response = new String(arg2, "UTF-8");
                    response = CommonUtil.transResponse(response);
                    JSONObject jsb = new JSONObject(response);
                    String cipher = jsb.getString("cipher");
                    cipher = MyDES.decrypt(cipher);
                    result = new Gson().fromJson(cipher, new TypeToken<BaseModel<AdvertModel>>() {
                    }.getType());
                    if (result.isSuccess()) {
                        if (PreferencesUtils.getCValueFromSPMap(ImageActivity.this, "advId").equals(result.getData().getId())) {
                            Bitmap bitmap = FileUtil.readContentSDcardImg(result.getData().getId());
                            if (bitmap != null) {
                                isShow = false;
                            } else {
                                isShow = true;
                            }
                            isGet = false;
                        } else {
                            isShow = true;
                            isGet = true;
                        }
                    } else {
                        isGone = true;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
