package model;

import java.io.Serializable;

@SuppressWarnings("serial")
public class TransferInfo implements Serializable{
	
	public static  String ORDER_BY_TYPE_RATE= "today_rate";
	public static  String ORDER_BY_TYPE_MONEY = "transfer_money";
	public static  String ORDER_BY_TYPE_DIS = "discount";
	public static  String ORDER_BY_TYPE_TIME = "remain_time";
	
	public static  String STOR_TYPE_ASC = "asc";
	public static  String STOR_TYPE_DESC = "desc";
	
	private String discount;
	private String remain_time;
	private String today_rate;
	private String transfer_money;
	private String transfer_order_id;
	private String product_name;
	private String tubiaoContent;

	public String getTubiaoContent() {
		return tubiaoContent;
	}

	public void setTubiaoContent(String tubiaoContent) {
		this.tubiaoContent = tubiaoContent;
	}

	public String getProduct_name() {
		return product_name;
	}

	public void setProduct_name(String product_name) {
		this.product_name = product_name;
	}

	public String getDiscount() {
		return discount;
	}
	public void setDiscount(String discount) {
		this.discount = discount;
	}
	public String getRemain_time() {
		return remain_time;
	}
	public void setRemain_time(String remain_time) {
		this.remain_time = remain_time;
	}
	public String getToday_rate() {
		return today_rate;
	}
	public void setToday_rate(String today_rate) {
		this.today_rate = today_rate;
	}
	public String getTransfer_money() {
		return transfer_money;
	}
	public void setTransfer_money(String transfer_money) {
		this.transfer_money = transfer_money;
	}
	public String getTransfer_order_id() {
		return transfer_order_id;
	}
	public void setTransfer_order_id(String transfer_order_id) {
		this.transfer_order_id = transfer_order_id;
	}
	
	

}
