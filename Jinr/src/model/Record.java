/*
 * Record.java
 * @author Andrew Lee
 * 2014-10-27 上午9:42:48
 */
package model;

import java.io.Serializable;

/**
 * Record.java
 * @description:
 * @author Andrew Lee
 * @version 
 * 2014-10-27 上午9:42:48
 */
public class Record implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1513335788630033618L;
	
	private String buy_type;
	private String c_time;
	private String event_key;
	private String goods_type;
	private String money;
	private String pay_type;
	private String status;
	private String title;
	private String type;
	private String order_id;
	private String url;
	private String contract_num;
	public String getBuy_type() {
		return buy_type;
	}
	public void setBuy_type(String buy_type) {
		this.buy_type = buy_type;
	}
	public String getC_time() {
		return c_time;
	}
	public void setC_time(String c_time) {
		this.c_time = c_time;
	}
	public String getEvent_key() {
		return event_key;
	}
	public void setEvent_key(String event_key) {
		this.event_key = event_key;
	}
	public String getGoods_type() {
		return goods_type;
	}
	public void setGoods_type(String goods_type) {
		this.goods_type = goods_type;
	}
	public String getMoney() {
		return money;
	}
	public void setMoney(String money) {
		this.money = money;
	}
	public String getPay_type() {
		return pay_type;
	}
	public void setPay_type(String pay_type) {
		this.pay_type = pay_type;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getOrder_id() {
		return order_id;
	}
	public void setOrder_id(String order_id) {
		this.order_id = order_id;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getContract_num() {
		return contract_num;
	}
	public void setContract_num(String contract_num) {
		this.contract_num = contract_num;
	}
	
	

}
