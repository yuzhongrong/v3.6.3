package model;

import java.io.Serializable;

@SuppressWarnings("serial")
public class TransferSuccessInfo implements Serializable {
	private String remaining_days;
	private String transfer_money;
	private String rate;
	private String discount;
	private String product_name;

	public String getProduct_name() {
		return product_name;
	}

	public void setProduct_name(String product_name) {
		this.product_name = product_name;
	}

	public String getRemaining_days() {
		return remaining_days;
	}
	public void setRemaining_days(String remaining_days) {
		this.remaining_days = remaining_days;
	}
	public String getTransfer_money() {
		return transfer_money;
	}
	public void setTransfer_money(String transfer_money) {
		this.transfer_money = transfer_money;
	}
	public String getRate() {
		return rate;
	}
	public void setRate(String rate) {
		this.rate = rate;
	}
	public String getDiscount() {
		return discount;
	}
	public void setDiscount(String discount) {
		this.discount = discount;
	}
	
	
}
