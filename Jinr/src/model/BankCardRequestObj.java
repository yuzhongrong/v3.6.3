package model;

import java.io.Serializable;

public class BankCardRequestObj implements Serializable {
	/**
	 * 绑卡请求
	 */
	private static final long serialVersionUID = 1L;
	public String requestid; // 绑卡请求编号
	public String sms; // 短信发送方 【YEEPAY 易宝发送 BANK 银行发送】
}
