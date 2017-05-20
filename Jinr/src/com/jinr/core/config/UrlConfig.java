/*
 * UrlConfig.java
 * @author Andrew Lee
 * 2014-10-20 上午10:56:19
 */
package com.jinr.core.config;

import com.jinr.core.JinrApp;
import com.umeng.analytics.AnalyticsConfig;

/**
 * UrlConfig.java description:
 *
 * @author Andrew Lee version 2014-10-20 上午10:56:19
 */
public class UrlConfig {
    // 360 andmarket assist91 baidu other ourweb xiaomi yingyongbao sina quanmin
    // weibo test |id|
    public static String PLATFORM = AnalyticsConfig.getChannel(JinrApp.instance()); // 渠道
    public static final String KEY = "12345678"; // DES ECB加密模式下，密钥只能是8位
    public static String SMS_IDENTIFY = "561"; // 短信新版本区分
    // -----------------------------M站url----------------------------------------//
    public static final String IP_M = "https://jrdev32.jingyubank.com/Appm/Edition3/";// 开发机
//    public static final String IP_M = "https://apinpre.jinr.com/Appm/Edition3/";//预发布
//	public static final String IP_M = "https://apipre.jinr.com/Appm/Edition3/";//公测版
 //   public static final String IP_M = "https://api.jinr.com/Appm/Edition3/";//正式机

    // -----------------------------日增息url----------------`------------------------//
    public static final String IP_R = "https://jrdev32.jingyubank.com/Appm/Dailygain/";// 开发机
//    public static final String IP_R = "https://apinpre.jinr.com/Appm/Dailygain/";//预发布
//    public static final String IP_R = "https://apipre.jinr.com/Appm/Dailygain/";//公测版
 //   public static final String IP_R = "https://api.jinr.com/Appm/Dailygain/";//正式机

  //   -----------------------------V_url---------------------------------------//
    public static final String IP_V = "https://jrdev32.jingyubank.com/Appm/V3_6_1/";// 开发机
//    public static final String IP_V = "https://apinpre.jinr.com/Appm/V3_6_1/";//预发布
//    public static final String IP_V = "https://apipre.jinr.com/Appm/V3_6_1/";//公测版
  //  public static final String IP_V = "https://api.jinr.com/Appm/V3_6_1/";//正式机

    // -----------------------------新url------------------------------
    public static final String BASE_IP_DES = "https://jrdev32.jingyubank.com/Api/V3_6_1/";//开发机
//    public static final String BASE_IP_DES = "https://apinpre.jinr.com/Api/V3_6_1/";//预发布
//	public static final String BASE_IP_DES = "https://apipre.jinr.com/api/V3_6_1/";//公测版
 //   public static final String BASE_IP_DES = "https://api.jinr.com/api/V3_6_1/";//正式

    public static final String PRODUCT_DETAIL = "homeAssetDetail?uid=";//产品详情
    public static final String PRODUCT_PROTOCOL = "assertProtocol?id=";//产品协议
    public static final String DAILYGAIN_FAQ = "programs";//新的常见问题
    public static final String WELFARE_LIST = "boon?uid=";//福利列表
    public static final String WELFARE_RULES = "boonRule";//福利使用规则
    public static final String BOON_DETAIL = "boonDetail?id=";//福利详情
    public static final String USER_COUPON = "userCoupon?uid=";//卡卷列表
    public static final String DAILYGAIN_TRANSINFO = "transInfo";//转让说明
    public static final String CURRENT_EXPLAIN = "outInstruct";//活期转出说明
    public static final String PLATFORM_DATA = "platformData";//营运数据
    public static final String NEWS_NOTIOC = "noticeListnew";//新的公告
    public static final String DAILY_SAFE = "safe";//安全保障
    public static final String DAILY_REDEEM_CODE = "redeemCode?uid=";//兑换码
    public static final String TRANSFER_AGREEMENT = "transferAgreement";//转让协议
    public static final String BALANCE_EXPLAIN = "balanceInfo";//余额说明
    public static final String APPHELP_YYBG = "yybg2";// 运营报告
    public static final String APPHELP_REGP = "regP";// 用户注册协议
    public static final String APPHELP_BANKP = "bankP";// 服务协议
    public static final String APPHELP_ABOUT = "about2";// 关于我们
    public static final String HB = "showhb?uid=";//新版红包
    public static final String HB_LICAI_DETAIL = "licaiDetails2?id=";// 理财红包详情
    public static final String HB_JIAXI_DETAIL = "jiaxiDetails2?id="; // 加息券详情
    public static final String HB_XIANJIN_DETAIL = "xianjinDetails2?id=";// 现金红包详情
    public static final String ACTIVITY_ID = "&id=";// 活动详情（发送手机号）
    public static final String NOTICE_DETAIL = "gonggaoDetails?id=";// 公告详情
    public static final String ACTIVITY_LIST = "activityList?uid=";// 活动消息列表
    public static final String ACTIVITY_TEL = "huodongDetails2?tel=";// 活动详情(判断条件url)
    public static final String APPHELP_JYB = "jyb2";// 解绑银行卡
    public static final String USER_LOGIN = "User/login"; // 登录
    public static final String USER_INVITE_CODE = "User/inviteCodePlaceholder";//邀请码提示文案
    public static final String USER_BANKINFO = "yonghu/userBankInfo"; // 新的用户绑卡加以密码信息
    public static final String USER_REGISTER = "User/register"; // 注册
    public static final String USER_JUDGE_TEL = "User/judge_tel"; // 验证手机号码
    public static final String SUM_MONEY_INVESTOR = "Index/sum_money_investor";// 交易额和投资人
    public static final String FEEDBACK_LIST = "Information/feedback_list"; // 我的反馈列表
    public static final String HEAD_NOTICE = "Information/important_notice";// 紧急公告
    public static final String GETCARDTYPE = "account/getcardtype";// 获取支持的银行卡列表
    public static final String GET_ZUHE = "Index/get_zuhe";// 组合列表
    public static final String YEEPAY_BANKCARD_CHECK = "Yeepay/bankcardCheck";// 投资通卡类型查询
    public static final String YEEPAY_BANKCARD_REQUEST = "Yeepay/bindcardRequest";// 投资通绑卡银行卡
    public static final String YEEPAY_BANKCARD_SEND_SMS = "Yeepay/bindcardSendsms";// 投资通重新发送绑卡短信
    public static final String YEEPAY_BANKCARD_CHECK_SMS = "Yeepay/bindcardChecksms";// 投资通绑定银行卡短信确认
    public static final String SMS_SENDSMS = "sms/sendsms"; // 短信验证码接口
    public static final String YONHU_PASSWORD = "Yonghu/modify_password"; // 修改登录密码
    public static final String YONHU_SETJYPWD = "yonghu/setjypwd"; // 设置交易密码
    public static final String YONGHU_FINDPWD = "yonghu/findTransPwd";// 找回交易密码
    public static final String USER_BUSSPWD = "User/verify_busspwd"; // 验证交易密码
    public static final String YONGHU_CARD = "yonghu/verify_card"; // 验证身份证号码
    public static final String MONEY = "yonghu/money";// 获取用户收益接口
    public static final String VERIFY_BUSSPWD = "User/verify_busspwd";// 验证交易密码
    public static final String GET_RECORD = "Order/tradeRecord";// 交易列表
    public static final String JY_DETAIL = "Order/query";// 交易记录详情
    public static final String SMS_VERIFYUSER = "Sms/verifyuser";// 验证短信是否正确
    public static final String YONGHU_MODIFY_TEL = "yonghu/modify_tel";// 修改手机号码
    public static final String ADD_ZCORDER = "Zcorder/addzcOrder";// 提现
    public static final String ZCORDER_USERTIME = "Zcorder/usertxTime";// 转出到帐时间
    public static final String FORGET_PASSWD = "user/forget_password";// 忘记密码
    public static final String FORMER_INSERT_CITY = "User/formerInsertCity";
    public static final String FEEDBACK = "Information/Feedback";// 反馈
    public static final String PROFIT_AMOUNT = "yonghu/Cumulativesy";// 累计收益列表
    public static final String DAY_PRODUCT_PROFIT = "Cunbeiaccount/accumulativeyieldRecords";//日增息累计收益
    public static final String INSERT_CITY = "User/insertCity";// 添加城市
    public static final String SHOUYI_URL = "index/shouyulv_url";// 鲸鱼服务中心
    public static final String NOW_PLUSYIELD = "account/now_plusyield";// 获取加息券收益率
    public static final String NEWS_CENTER_READ = "Information/messageCenter_edition";// 系统消息未读红点
    public static final String FIRST_CHARGE = "Index/active_switch";// 首充和注册5000开关
    public static final String ACT_SHARE = "Zixun/share_text";// 活动分享文案
    public static final String CUNBEI_LIST = "Cunbeiaccount/getCunbeiList";// 获取存呗相关产品
    public static final String CUNBEI_RECORDS = "account/getAssertsLists";// 获取存呗单个资产投资记录
    public static final String PRODUCT_DETAIL_SECOND = "Cunbeiaccount/assertSafety";// 定期详情二
    public static final String MY_PRODUCT = "Cunbeiaccount/userAssertDetail";// 我的定期
    public static final String APPUPDATA = "User/appEdition";// APP版本更新
    public static final String TOTALA_CCOUNT_DETAILS = "Account/getAllAccount";// 获取总资产（定期、活期、日增息）
    public static final String USER_TXTIME = "Cunbeiaccount/usertxTime";//新的活期定期转出到帐时间
    public static final String USER_DETAIL = "Cunbeiaccount/getCunbeiDetail";//详情页
    public static final String USER_NOTICE = "Zixun/activity_notice";//启动页广告
    public static final String ACT_LIST = "Information/activity_list";//活动列表
    public static final String USER_REGULARALLMONEY = "Cunbeiaccount/firstHb";//定期转入三倍红包
    public static final String USER_YESERDAY_PROFIT = "Cunbeiaccount/yesterdayIncome";//昨日收益
    public static final String USER_ALL_PROFIT = "Cunbeiaccount/incomeSummary";//累计收益汇总
    public static final String USER_PRODUCT_PROFIT = "Cunbeiaccount/incomeHistory";//产品收益
    public static final String ACT_BANNER_LIST = "Zixun/activity_notice_list";//活动banner列表
    public static final String ABOUNT_JINR = "Index/AbountJINR";//未登录页面图文数据
    public static final String PAY_LIST = "Pay/getPayList";//获取付款方式列表
    public static final String BALANCE_OUT_INFO = "Balance/getTakeOutTimesMoney";//获取余额提现次数和可用余额
    public static final String BALANCE_JUDGE_OUT_MONEY = "Balance/judgeTakeOutMoney";//判断转出余额是否超出
    public static final String ORDER_CREATE = "Order/create";//订单创建（充值、提现、产品流转）
    public static final String ORDER_BAL_OUT_RESULT = "Pay/query";//订单查询
    public static final String PAY_VALIDATE = "Pay/payValidate";//银行卡支付验证
    public static final String Current_OUT_TIME = "Zcorder/usertxTime";// 用户转出提示到账时间 银行名称 金额 输入金额界面提示
    public static final String TRANSFER_MARKET = "Transfer/market";//转让市场
    public static final String TRANSFER_INFO = "Transfer/info";//转让信息(购买转让页)
    public static final String PURCHASE_RESULT = "Pay/query";//转入详情
    public static final String USER_ACCOUNT = "account/getUserAccount";//定期、日增息   资产信息
    public static final String TRANSFER_BILL = "Transfer/newBill";//转让订单
    public static final String TRANSFER_REVOKE = "Transfer/revoke";//撤销转让
    public static final String PAY_SENDSMS = "Pay/resendSms";//支付短信重发
    public static final String BUY_LIMIT = "Account/buyLimit";//[活|定|日]-可购买限额
    public static final String TRANSFER_REVOKE_COUNT = "Transfer/getCanRevokeTimes";//撤销转让次数
    public static final String USER_ADDRESSINFO = "consigneeinfo/details";//用户收货地址
    public static final String USER_ADDRESSINFO_LIST = "consigneeinfo/conList";//用户收货地址列表
    public static final String TRANSFER_SUCCESS = "Transfer/successTransfer";//已成功转让列表
    public static final String TOTAL_ASSETS_INCOME = "Cunbeiaccount/getIncomeInfo";//累计收益和昨日收益
    public static final String CREATE_ADDRESS = "consigneeinfo/create";//创建收货地址
    public static final String DELETE_ADDRESS = "consigneeinfo/delete";//删除收货地址
    public static final String ISDEFAULT_ADDRESS = "consigneeinfo/isdefault";//设置默认收货地址
    public static final String VERIFY_CODE_LOGIN = "User/loginByPhoneCode";//验证码登录
    public static final String BANK_NAME = "Yeepay/getBankCardName";//获取银行卡所属银行
    public static final String DEAL_PASSWORD = "user/isuser";//是否设置交易密码及绑卡
    public static final String MAIN_PRODUCT_LIMIT = "Index/getProductLimit";//产品线限购接口
    public static final String CARD_REEL = "Coupon/getBestMatchCoupon";//卡卷接口

    /*new url*/

    public static final String BASE = "http://appdev.jinr.com/";//base
    public  static final String key="sdf6gfd";
    public  static final String appid="1000000001";
    public   static final String LOGIN="user.login";
    public   static final String LOGIN_CHECKN="user.mobilephone.check";





}