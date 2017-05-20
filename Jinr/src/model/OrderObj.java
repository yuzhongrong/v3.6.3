package model;

public class OrderObj {

	public int code;
	public OrderData data;
	
	public class OrderData{
		public String order_num;
		public String pay_type;
		public String buy_type;
	}
}
