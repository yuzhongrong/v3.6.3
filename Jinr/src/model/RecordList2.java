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
 * @param <T>
 * @date: 2014-12-23 下午2:36:04
 */

public class RecordList2<T> implements Serializable{
	/**
	 * @fieldName: serialVersionUID
	 * @fieldType: long
	 * @Description: TODO
	 */
	private static final long serialVersionUID = 4495469098599560344L;
	private String count,c_group_time;
	private ArrayList<Record> list;
	public String getCount() {
		return count;
	}
	public void setCount(String count) {
		this.count = count;
	}
	public ArrayList<Record> getList() {
		return list;
	}
	public ArrayList<Record> getRecordList() {
		return list;
	}
	public void setList(ArrayList<Record> list) {
		this.list = list;
	}
	public String getGroup_time() {
		return c_group_time;
	}
	public void setGroup_time(String c_group_time) {
		this.c_group_time = c_group_time;
	}
//	public void

	

}
