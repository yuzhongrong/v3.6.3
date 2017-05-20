package model;

import java.io.Serializable;

public class MainAboutInfo implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -4719451083572129007L;
	private String content;
	private String name;
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
}
