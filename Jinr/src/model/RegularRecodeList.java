package model;

import java.io.Serializable;
import java.util.ArrayList;

@SuppressWarnings("serial")
public class RegularRecodeList implements Serializable{
	private String code;
	private ArrayList<RegularRecord> list;
	private String count;
	private String total_count;
	public String getCount() {
		return count;
	}
	public void setCount(String count) {
		this.count = count;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public ArrayList<RegularRecord> getList() {
		return list;
	}
	public void setList(ArrayList<RegularRecord> list) {
		this.list = list;
	}
	public String getTotalCount() {
		return total_count;
	}
	public void setTotalCount(String total_count) {
		this.total_count = total_count;
	}

}
