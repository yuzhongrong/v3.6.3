package com.jinr.core.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.jinr.core.R;
import com.jinr.core.gift.share.Constants;
import com.jinr.core.utils.PreferencesUtils;
import com.jinr.core.utils.UmUtils;
import com.tencent.mm.sdk.openapi.BaseReq;
import com.tencent.mm.sdk.openapi.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

/**
 * 微信客户端回调activity示例
 *
 * @author CQJ
 * @description 不能用其他名字
 */
public class WXEntryActivity extends Activity implements IWXAPIEventHandler {
    private IWXAPI api;
    public static WXEntryActivity myinstance = null;
    public static WXEntryActivity instance = null;
    public static WXEntryActivity getinstance() {
        myinstance = new WXEntryActivity();
        return myinstance;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        api = WXAPIFactory.createWXAPI(this, Constants.APP_ID, false);
        api.handleIntent(getIntent(), this);
        instance = this;
        super.onCreate(savedInstanceState);

    }



    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        api.handleIntent(intent, this);
    }

    /**
     * 微信发送请求到第三方应用时，会回调到该方法
     */
    @Override
    public void onReq(BaseReq req) {
        //
    }

    /**
     * 第三方应用发送到微信的请求处理后的响应结果，会回调到该方法
     */
    @Override
    public void onResp(BaseResp resp) {
        int result = 0;
        switch (resp.errCode) {
            case BaseResp.ErrCode.ERR_OK:
                result = R.string.errcode_success;
                if (PreferencesUtils.Keys.SHARE_INIT) {
                    if (PreferencesUtils.Keys.CLICK_WX) {
                        UmUtils.customEventResult(this, "Wechat",
                                PreferencesUtils.Keys.ACT_TYPE, true);
                    } else {
                        UmUtils.customEventResult(this, "WechatMoments",
                                PreferencesUtils.Keys.ACT_TYPE, true);
                    }
                }
                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                result = R.string.errcode_cancel;
                if (PreferencesUtils.Keys.SHARE_INIT) {
                    UmUtils.customEventResult(this, "Wechat",
                            PreferencesUtils.Keys.ACT_TYPE, false);
                }
                break;
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
                result = R.string.errcode_deny;
                break;
            default:
                result = R.string.errcode_unknown;
                break;
        }

        Toast.makeText(this, result, Toast.LENGTH_LONG).show();
        finish();
    }
}
