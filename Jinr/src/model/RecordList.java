/**  
 * Copyright © 2014 The Code. All rights reserved.
 *
 * @Title: RecordList.java
 * @Prject: Jinr
 * @Package: model
 * @Description: TODO
 * @author: Andrew Lee  
 * @date: 2014-12-23 下午2:36:04
 * @version: V1.0  
 */
package model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * @ClassName: RecordList
 * @Description: TODO
 * @author: Andrew Lee
 * @date: 2014-12-23 下午2:36:04
 */

public class RecordList implements Serializable{

	/**
	 * @fieldName: serialVersionUID
	 * @fieldType: long
	 * @Description: TODO
	 */
	private static final long serialVersionUID = -8479804755088334374L;
	private String count;
	private ArrayList<RecordList2> list;
	private String total_count;
	private int lastCnt;
	public String getCount() {
		return count;
	}
	public void setCount(String count) {
		this.count = count;
	}
	public ArrayList<RecordList2> getList() {
		return list;
	}
	public void setList(ArrayList<RecordList2> list) {
		this.list = list;
	}
	public String getTotal_count() {
		return total_count;
	}
	public void setTotal_count(String total_count) {
		this.total_count = total_count;
	}
	
//	public void
	
	

}
