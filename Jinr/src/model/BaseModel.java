/*
 * Base_Model.java
 * @author Andrew Lee
 * 2014-10-20 上午11:02:04
 */
package model;

import java.io.Serializable;

/**
 * Base_Model.java description:
 * 
 * @author Andrew Lee version 2014-10-20 上午11:02:04
 */
public class BaseModel<T> implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3676969309433142938L;
	private int code;
	private String msg;
	private T data;

	public BaseModel() {
		super();
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}

	public boolean isSuccess() {
		if (this.getCode() == 1000) {
			return true;
		} else {
			return false;
		}
	}
}
