package com.jinr.core.utils;

import android.content.Context;

import com.umeng.analytics.MobclickAgent;

public final class UmUtils {

    public final static String IMM_CLICK = "IMM_click";// 立即赚钱点击+开启召唤技能点击
    public final static String FRIEND_CLICK = "friend_click";// 微信好友 点击次数
    public final static String FRIEND_HUM = "friend_hum";// 微信好友 人数
    public final static String FRIEND_SUC = "friend_suc";// 微信好友 分享成功
    public final static String COF_CLICK = "cof_click";// 微信朋友圈 点击次数
    public final static String COF_HUM = "cof_hum";// 微信朋友圈 人数
    public final static String COF_SUC = "cof_suc";// 微信朋友圈 分享成功
    public final static String MES_CLICK = "mes_click";// 短信 点击次数
    public final static String MES_HUM = "mes_hum";// 短信 人数
    public final static String MES_SUC = "mes_suc";// 短信 分享成功
    public final static String QQ_CLICK = "QQ_click";// QQ 点击次数
    public final static String QQ_HUM = "QQ_hum";// QQ 人数
    public final static String QQ_SUC = "QQ_suc";// QQ 分享成功
    public final static String QQSPACE_CLICK = "QQspace_click";// QQ空间 点击次数
    public final static String QQSPACE_HUM = "QQspace_hum";// QQ空间 人数
    public final static String QQSPACE_SUC = "QQspace_suc";// QQ空间 分享成功

    public final static String WX_FD_CLICK = "fd_click";// 微信 点击次数（五十亿）
    public final static String WX_FD_HUM = "fd_PPnum";// 微信 人数（五十亿）
    public final static String WX_FD_SUC = "fd_suc";// 微信 分享成功（五十亿）
    public final static String FD_CIECLE_CLICK = "fd_circle_click";// 微信朋友圈
    // 点击次数（五十亿）
    public final static String FD_CIECLE_PPNUM = "fd_circle_PPnum";// 微信朋友圈
    // 人数（五十亿）
    public final static String FD_CIRCLE_SUC = "fd_circle_suc";// 微信朋友圈
    // 分享成功（五十亿）
    public final static String SMS_CLICK = "SMS_click";// 短信 点击次数（五十亿）
    public final static String SMS_PPNUM = "SMS_PPnum";// 短信 人数（五十亿）
    public final static String SMS_SUC = "SMS_suc";// 短信 分享成功（五十亿）
    public final static String CANCLE_CLICK = "cancel_click";// 取消分享（五十亿）

    public final static String WX_TEST_CLICK = "wx_test_click";// 微信 点击次数（测试）
    public final static String WX_TEST_HUM = "wx_test_hum";// 微信 人数（测试）
    public final static String WX_TEST_SUC = "wx_test_suc";// 微信 分享成功（测试）

    public final static String AD_CLICK = "Ad_click";// 广告页-点击次数
    public final static String AD_HUM = "Ad_hum";// 广告页-点击人数
    public final static String AD_SHUT_CLICK = "Ad_shut_click";// 广告页跳过-点击次数
    public final static String AD_SHUT_HUM = "Ad_shut_hum";// 广告页跳过-点击人数
    public final static String MAIN_CLICK = "Main_click";// 首页-点击次数
    public final static String MAIN_HUM = "Main_hum";// 首页-点击人数
    public final static String MAINCURRENT_CLICK = "Maincurrent_click";// 活期首页-点击次数
    public final static String MAINCURRENT_HUM = "Maincurrent_hum";// 活期首页-点击人数
    public final static String MAINCURRENT_ROLLIN_CLICK = "Maincurrent_rollin_click";// 活期首页-转入-点击次数
    public final static String MAINCURRENT_ROLLIN_HUM = "Maincurrent_rollin_hum";// 活期首页-转入-点击人数
    public final static String MAINCURRENT_ROLLOUT_CLICK = "Maincurrent_rollout_click";// 活期首页-转出-点击次数
    public final static String MAINCURRENT_ROLLOUT_HUM = "Maincurrent_rollout_hum";// 活期首页-转出-点击人数
    public final static String NEWSCENTER_CLICK = "Newscenter_click";// 消息中心-点击次数
    public final static String NEWSCENTER_NUM = "Newscenter_hum";// 消息中心-点击人数
    public final static String LOGO_CLICK = "logo_click";// 鲸鱼logo-点击次数
    public final static String LOGO_NUM = "logo_hum";// 鲸鱼logo-点击人数
    public final static String ASSETS_CLICK = "Assets_click";// 资产页-点击次数
    public final static String ASSETS_HUM = "Assets_hum";// 资产页-点击人数
    public final static String MAINREGULAR_CLICK = "Mainregular_click";// 定期首页-点击次数
    public final static String MAINREGULAR_HUM = "Mainregular_hum";// 定期首页-点击人数
    public final static String MAINREGULAR_ROLLIN_CLICK = "Mainregular_rollin_click";// 定期首页-转入-点击次数
    public final static String MAINREGULAR_ROLLIN_HUM = "Mainregular_rollin_hum";// 定期首页-转入-点击人数

    public final static String TOTALASSETS_CLICK = "Totalassets_click";// 总资产-点击次数
    public final static String TOTALASSETS_HUM = "Totalassets_hum";// 总资产-点击人数
    public final static String CURRENTASSETS_CLICK = "Currentassets_click";// 活期资产-点击次数
    public final static String CURRENTASSETS_HUM = "Currentassets_hum";// 活期资产-点击人数
    public final static String REGULARASSETS_CLICK = "Regularassets_click";// 定期资产-点击次数
    public final static String REGULARASSETS_HUM = "Regularassets_hum";// 定期资产-点击人数
    public final static String DAYPRODUCTASSETS_CLICK = "RZXassets_click";// 日增息资产-点击次数
    public final static String DAYPRODUCTASSETS_HUM = "RZXassets_hum";// 日增息资产-点击人数
    public final static String ASSETS_ROLLIN_CLICK = "Assets_rollin_click";// 资产页-转入--点击次数
    public final static String ASSETS_ROLLIN_HUM = "Assets_rollin_hum";// 资产页-转入-点击人数
    public final static String ASSETS_ROLLOUT_CLICK = "Assets_rollout_click";// 资产页-转出-点击人数
    public final static String ASSETS_ROLLOUT_HUM = "Assets_rollout_hum";// 资产页-转出-点击人数
    public final static String BANKCARD_CLICK = "Bankcard_click";// 银行卡-点击次数
    public final static String BANKCARD_HUM = "Bankcard_hum";// 银行卡-点击人数
    public final static String FIND_CLICK = "Find_click";// 发现-点击次数
    public final static String FIND_NUM = "Find_hum";// 发现-点击人数
    public final static String SECURITY_CLICK = "Security_click";// 安全-点击次数
    public final static String SECURITY_NUM = "Security_hum";// 安全-点击人数
    public final static String MORE_CLICK = "More_click";// 更多-点击次数
    public final static String MORE_HUM = "More_hum";// 更多-点击人数
    public final static String TELEPHONE_CLICK = "Telephone_click";// 客服电话-点击次数
    public final static String TELEPHONE_HUM = "Telephone_hum";// 客服电话-点击人数
    public final static String REGULARDETAIL_CLICK = "Regulardetail_click";// 定期详情-点击次数
    public final static String REGULARDETAIL_HUM = "Regulardetail_hum";// 定期详情-点击人数
    public final static String DAYPRODUCTDETAIL_CLICK = "RZXdetail_click";// 日增息详情-点击次数
    public final static String DAYPRODUCTDETAIL_HUM = "RZXdetail_hum";// 日增息详情-点击人数
    public final static String STRONGBOX_CLICK = "strongbox_cilck";// 保险箱详情-点击次数
    public final static String STRONGBOX_HUM = "strongbox_hum";// 保险箱详情-点击人数

    public final static String DAYPRODUCTDETAIL_ROLLIN_CLICK = "RZXdetail_rollin_click";// 日增息详情-转入-点击次数
    public final static String DAYPRODUCTDETAIL_ROLLIN_HUM = "RZXdetail_rollin_hum";// 日增息详情-转入-点击人数
    public final static String REGULARDETAIL_ROLLIN_CLICK = "Regulardetail_rollin_click";// 定期详情-转入-点击次数
    public final static String REGULARDETAIL_ROLLIN_HUM = "Regulardetail_rollin_hum";// 定期详情-转入-点击人数
    public final static String CURRENTASSETS_ROLLIN_CLICK = "Currentassets_rollin_click";// 活期资产-转入-点击次数
    public final static String CURRENTASSETS_ROLLIN_HUM = "Currentassets_rollin_hum";// 活期资产-转入-点击人数
    public final static String CURRENTASSETS_ROLLOUT_CLICK = "Currentassets_rollout_click";// 活期资产-转出-点击次数
    public final static String CURRENTASSETS_ROLLOUT_HUM = "Currentassets_rollout_hum";// 活期资产-转出-点击人数
    public final static String CURRENTASSETS_PROFIT_CLICK = "Currentassets_profit_click";// 活期资产-收益-点击次数
    public final static String CURRENTASSETS_PROFIT_HUM = "Currentassets_profit_hum";// 活期资产-收益-点击人数
    public final static String CURRENTASSETS_TOTALASSETS_CLICK = "Currentassets_totalassets_click";// 活期资产-总资产-点击人数
    public final static String CURRENTASSETS_TOTALASSETS_HUM = "Currentassets_totalassets_hum";// 活期资产-总资产-点击人数
    public final static String REGULARASSETS_ROLLIN_CLICK = "Regularassets_rollin_click";// 定期资产-转入-点击次数
    public final static String REGULARASSETS_ROLLIN_HUM = "Regularassets_rollin_hum";// 定期资产-转入-点击人数
    public final static String DAYPRODUCTASSETS_ROLLIN_CLICK = "RZXassets_rollin_click";// 日增息资产-转入-点击次数
    public final static String DAYPRODUCTASSETS_ROLLIN_HUM = "RZXassets_rollin_hum";// 日增息资产-转入-点击人数
    public final static String REGULARASSETS_TOTALASSETS_CLICK = "Regularassets_totalassets_click";// 定期资产-总资产-点击次数
    public final static String REGULARASSETS_TOTALASSETS_NUM = "Regularassets_totalassets_hum";// 定期资产-总资产-点击人数
    public final static String REGULARASSETS_TRADERECORD_CLICK = "Regularassets_Traderecord_click";// 定期资产-交易记录-点击次数
    public final static String REGULARASSETS_TRADERECORD_NUM = "Regularassets_Traderecord_hum";// 定期资产-交易记录-点击人数
    public final static String CURRENTIN_MONEY_CLICK = "Currentin_money_click";// 转入活期-输入金额-点击次数
    public final static String CURRENTIN_MONEY_HUM = "Currentin_money_hum";// 转入活期-输入金额-点击人数
    public final static String CURRENTIN_CHECK_CLICK = "Currentin_check_click";// 转入活期-确认转入-点击次数
    public final static String CURRENTIN_CHECK_HUM = "Currentin_check_hum";// 转入活期-确认转入-点击人数
    public final static String CURRENTOUT_MONEY_CLICK = "Currentout_money_click";// 转出活期-输入金额-点击次数
    public final static String CURRENTOUT_MONEY_HUM = "Currentout_money_hum";// 转出活期-输入金额-点击人数
    public final static String CURRENTOUT_CHECK_CLICK = "Currentout_check_click";// 活期转出-确认转出-点击次数
    public final static String CURRENTOUT_CHECK_HUM = "Currentout_check_hum";// 活期转出-确认转出-点击人数
    public final static String CURRENTIN_PASSWORDCLOSE_CLICK = "Currentin_passwordclose_click";// 转入活期-交易密码-关闭-点击次数
    public final static String CURRENTIN_PASSWORDCLOSE_HUM = "Currentin_passwordclose_hum";// 转入活期-交易密码-关闭-点击人数
    public final static String CURRENTIN_MESSAGE_CLICK = "Currentin_message_click";// 转入活期-验证码-关闭-点击次数
    public final static String CURRENTIN_MESSAGE_HUM = "Currentin_message_hum";// 转入活期-验证码-关闭-点击人数
    public final static String CURRENTOUT_PASSWORDCLOSE_CLICK = "Currentout_passwordclose_click";// 活期转出-交易密码-关闭-点击次数
    public final static String CURRENTOUT_PASSWORDCLOSE_HUM = "Currentout_passwordclose_hum";// 活期转出-交易密码-关闭-点击人数
    public final static String REGULARIN_MONEY_CLICK = "Regularin_money_click";// 转入定期-输入金额-点击次数
    public final static String REGULARIN_MONEY_HUM = "Regularin_money_hum";// 转入定期-输入金额-点击人数
    public final static String DAYPRODUCTIN_MONEY_CLICK = "RZXin_money_click";// 转入日增息-输入金额-点击次数
    public final static String DAYPRODUCTIN_MONEY_HUM = "RZXin_money_hum";// 转入-输入金额-点击人数
    public final static String REGULARIN_CHECK_CLICK = "Regularin_check_click";// 转入定期-确认转入-点击次数
    public final static String REGULARIN_CHECK_HUM = "Regularin_check_hum";// 转入定期-确认转入-点击人数
    public final static String DAYPRODUCTIN_CHECK_CLICK = "RZXin_check_click";// 转入日增息-确认转入-点击次数
    public final static String DAYPRODUCTIN_CHECK_HUM = "RZXin_check_hum";// 转入日增息-确认转入-点击人数
    public final static String REGULARIN_PASSWORDCLOSE_CLICK = "Regularin_passwordclose_click";// 转入定期-交易密码-关闭-点击次数
    public final static String REGULARIN_PASSWORDCLOSE_NUM = "Regularin_passwordclose_hum";// 转入定期-交易密码-关闭-点击人数
    public final static String REGULARIN_MESSAGE_CLICK = "Regularin_message_click";// 转入定期-验证码-关闭-点击次数
    public final static String REGULARIN_MESSAGE_NUM = "Regularin_message_hum";// 转入定期-验证码-关闭-点击人数

    public final static String DAYPRODUCTIN_PASSWORDCLOSE_CLICK = "RZXin_pwclose_click";// 转入日增息-交易密码-关闭-点击次数
    public final static String DAYPRODUCTIN_PASSWORDCLOSE_NUM = "RZXin_pwclose_hum";// 转入日增息-交易密码-关闭-点击人数
    public final static String DAYPRODUCTIN_MESSAGE_CLICK = "RZXin_message_click";// 转入日增息-验证码-关闭-点击次数
    public final static String DAYPRODUCTIN_MESSAGE_NUM = "RZXin_message_hum";// 转入日增息-验证码-关闭-点击人数

    public final static String DAYPRODUCT_TOTALASSETS_CLICK = "RZXassets_totalassets_click";// 定期资产-总资产-点击次数
    public final static String DAYPRODUCT_TOTALASSETS_NUM = "RZXassets_totalassets_hum";// 定期资产-总资产-点击人数
    public final static String DAYPRODUCT_TRADERECORD_CLICK = "RZXassets_Traderecord_click";// 日增息-交易记录-点击次数
    public final static String DAYPRODUCT_TRADERECORD_NUM = "RZXassets_Traderecord_hum";//日增息交易记录人数

    private static UmUtils umUtils;

    public static UmUtils getInstance() {
        if (umUtils == null) {
            umUtils = new UmUtils();
        }
        return umUtils;
    }

    public static void release() {
        if (umUtils != null) {
            umUtils = null;
        }
    }

    /**
     * @return 点击次数统计
     */

    public static void customEvent(Context context, String id) {
        MobclickAgent.onEvent(context, id);
    }

    /**
     * @return 人数统计
     */
    public static void customEvent(Context context, String id, String key) {
        if (PreferencesUtils.getSBooleanFromSPMap(context, key)) {
            MobclickAgent.onEvent(context, id);
            PreferencesUtils.putSBooleanToSPMap(context, key, false);
        }
    }

    /**
     * @return 人数和次数统计
     */
    public static void customsEvent(Context context, String id, String hum) {
        if (PreferencesUtils.getSBooleanFromSPMap(context, hum)) {
            MobclickAgent.onEvent(context, id);
            MobclickAgent.onEvent(context, hum);
            PreferencesUtils.putSBooleanToSPMap(context, hum, false);
        } else {
            MobclickAgent.onEvent(context, id);
        }
    }

    /**
     * @return 分享结果统计
     */
    public static void customEventResult(Context context, String platform, String actType, boolean result) {
        String id = "";
        if (result) {
            if ("ShortMessage".equals(platform)) {
                if (actType.contains("tenbillion")) {// 百亿
                    id = UmUtils.MES_SUC;
                } else {
                    id = UmUtils.SMS_SUC;
                }
            } else if ("Wechat".equals(platform)) {
                if (actType.contains("tenbillion")) {// 百亿
                    id = UmUtils.FRIEND_SUC;
                } else {
                    id = UmUtils.WX_FD_SUC;
                }
            } else if ("WechatMoments".equals(platform)) {
                if (actType.contains("tenbillion")) {// 百亿
                    id = UmUtils.COF_SUC;
                } else {
                    id = UmUtils.FD_CIRCLE_SUC;
                }
            } else if ("QQ".equals(platform)) {
                id = UmUtils.QQ_SUC;
            } else if ("QZone".equals(platform)) {
                id = UmUtils.QQSPACE_SUC;
            }
        } else {// 取消分享
            if (!actType.contains("tenbillion") && !actType.equals("")) {
                id = UmUtils.CANCLE_CLICK;
            }
        }
        MobclickAgent.onEvent(context, id);
    }
}
