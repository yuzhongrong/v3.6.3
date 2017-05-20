package model;

import java.io.Serializable;

@SuppressWarnings("serial")
public class AdvertModel implements Serializable{

	private String id;
	private String title;
	private String img;
	private String description;
	private String link;
	private String linkid;
	public String getLinkid() {
		return linkid;
	}
	public void setLinkid(String linkid) {
		this.linkid = linkid;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getImg() {
		return img;
	}
	public void setImg(String img) {
		this.img = img;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getLink() {
		return link;
	}
	public void setLink(String link) {
		this.link = link;
	}
}
