/*
 * UserInfo.java
 * @author Andrew Lee
 * 2014-10-21 上午9:24:18
 */
package model;

import java.io.Serializable;

/**
 * UserInfo.java
 * description:
 * @author Andrew Lee
 * version 
 * 2014-10-21 上午9:24:18
 */
public class UserInfo implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String id;
	private String nikename;
	private String email;
	private String tel;
	private String password;
	private String pwd_key;
	private String buss_pwd;
	private String name;
	private String is_lock;
	private String card_id;
	private String bk;
	
	
	
	
	public String getBk() {
		return bk;
	}
	public void setBk(String bk) {
		this.bk = bk;
	}
	public String getCard_id() {
		return card_id;
	}
	public void setCard_id(String card_id) {
		this.card_id = card_id;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getPwd_key() {
		return pwd_key;
	}
	public void setPwd_key(String pwd_key) {
		this.pwd_key = pwd_key;
	}
	public String getBuss_pwd() {
		return buss_pwd;
	}
	public void setBuss_pwd(String buss_pwd) {
		this.buss_pwd = buss_pwd;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getIs_lock() {
		return is_lock;
	}
	public void setIs_lock(String is_lock) {
		this.is_lock = is_lock;
	}
	
	
	
	public UserInfo() {
		super();
		// TODO Auto-generated constructor stub
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getNikename() {
		return nikename;
	}
	public void setNikename(String nikename) {
		this.nikename = nikename;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getTel() {
		return tel;
	}
	public void setTel(String tel) {
		this.tel = tel;
	}
	
	
	

}
