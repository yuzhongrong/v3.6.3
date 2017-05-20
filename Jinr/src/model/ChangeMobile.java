/*
 * ChangeMobile.java
 * @author Andrew Lee
 * 2014-10-23 下午5:05:28
 */
package model;

import java.io.Serializable;

/**
 * ChangeMobile.java
 * @description:
 * @author Andrew Lee
 * @version 
 * 2014-10-23 下午5:05:28
 */
public class ChangeMobile implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -2207438970245702178L;
	private int okay;
	private UserInfo info;
	public int getOkay() {
		return okay;
	}
	public void setOkay(int okay) {
		this.okay = okay;
	}
	public UserInfo getInfo() {
		return info;
	}
	public void setInfo(UserInfo info) {
		this.info = info;
	}
}
