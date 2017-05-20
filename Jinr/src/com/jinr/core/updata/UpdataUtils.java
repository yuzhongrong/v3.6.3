package com.jinr.core.updata;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jinr.core.R;
import com.jinr.core.config.UrlConfig;
import com.jinr.core.utils.LoadingDialog;
import com.jinr.core.utils.MyDES;
import com.jinr.core.utils.MyhttpClient;
import com.jinr.core.utils.PreferencesUtils;
import com.jinr.core.utils.ToastUtil;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

import model.BaseModel;
import model.UpDataMode;

@SuppressLint("SimpleDateFormat")
public class UpdataUtils {
    private int mVersionCode;
    private LoadingDialog loadingdialog;
    private Context mContext;
    private boolean mIsShow;
    private int FromHomeActivity = 1;
    private int MustUpdaya = 2;

    public UpdataUtils(Context context, boolean isShow) {
        mContext = context;
        mIsShow = isShow;
        loadingdialog = new LoadingDialog(mContext);
    }

    // 检查是否需要更新 Ysw 2016.09.29
    public void checkUpdata(final int type) throws Throwable {
        final int version = getVersionInfo(mContext);
        RequestParams params = new RequestParams();
        JSONObject object = new JSONObject();
        object.put("app", "1");
        object.put("edition", "" + version);
        object.put("market", UrlConfig.PLATFORM);
        String desStr = MyDES.encrypt(object.toString());
        params.put("cipher", desStr);
        MyhttpClient.desPost(UrlConfig.APPUPDATA, params, new AsyncHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                if (mIsShow) loadingdialog.show();
            }

            @Override
            public void onFinish() {
                super.onFinish();
                if (mIsShow) loadingdialog.dismiss();
            }

            @Override
            public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
                super.onFailure(arg0, arg1, arg2, arg3);
                ToastUtil.show(mContext, R.string.default_error);
                if (mIsShow)
                    loadingdialog.dismiss();
            }

            @Override
            public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
                super.onSuccess(arg0, arg1, arg2);
                if (mIsShow) loadingdialog.dismiss();
                String response;
                try {
                    response = new String(arg2, "UTF-8");
                    JSONObject jsonObject = new JSONObject(response);
                    String cipher = jsonObject.getString("cipher");
                    String desc = MyDES.decrypt(cipher);
                    if (TextUtils.isEmpty(desc)) return;
                    BaseModel<UpDataMode> result = new Gson().fromJson(desc, new TypeToken<BaseModel<UpDataMode>>() {
                    }.getType());
                    if (result.isSuccess()) {
                        UpDataMode data = result.getData();
                        if (data == null)
                            return;
                        int newVersion = data.getVersionnumber();
                        if (newVersion > version) {
                            // 从首页进入且不是强制更新
                            if (type == FromHomeActivity && data.getConstraint() != MustUpdaya) {
                                if (newVersion != PreferencesUtils.getServiceVersionCood(mContext)) {
                                    UpdataNoticeDialog dialog = new UpdataNoticeDialog(mContext, data, type);
                                    dialog.show(((FragmentActivity) mContext).getSupportFragmentManager(), "");
                                    // 保存服务器中存有的版本号  Ysw 2016.09.27
                                    PreferencesUtils.saveVersionCood(mContext, newVersion);
                                }
                            } else {
                                // 保存服务器中存有的版本号  Ysw 2016.09.27
                                UpdataNoticeDialog dialog = new UpdataNoticeDialog(mContext, data, type);
                                dialog.show(((FragmentActivity) mContext).getSupportFragmentManager(), "");
                                PreferencesUtils.saveVersionCood(mContext, newVersion);
                            }
                        } else if (mIsShow) {
                            PreferencesUtils.saveVersionCood(mContext, newVersion);
                            ToastUtil.show(mContext, "亲！已经是最新版本了");
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    // 获取版本信息 Ysw 2016.09.27
    public int getVersionInfo(Context context) {
        try {
            mVersionCode = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return mVersionCode;
    }

    // 获取系统当前时间 Ysw 2016.09.27
    public int getSystemTime() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
        Date curDate = new Date(System.currentTimeMillis());
        String str = formatter.format(curDate);
        Integer valueOf = Integer.valueOf(str);
        return valueOf;
    }
}
