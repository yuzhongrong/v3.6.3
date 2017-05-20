package model;

public class ShareObj {
	public String title; // 标题
	public String sharetext; // 周分享文案
	public String logo; // 图片
	public String url; // 链接地址
	public String sharetextsm;// 短信分享文案
	public String urlsm;// 短信分享地址

	// public ShareObj(String title, String sharetext, String logo, String url,
	// String sharetextsm) {
	// super();
	// this.title = title;
	// this.sharetext = sharetext;
	// this.logo = logo;
	// this.url = url;
	// this.sharetextsm = sharetextsm;
	// }
	
	public String getTitle() {
		return title;
	}

	public String getUrlsm() {
		return urlsm;
	}

	public void setUrlsm(String urlsm) {
		this.urlsm = urlsm;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getSharetext() {
		return sharetext;
	}

	public void setSharetext(String sharetext) {
		this.sharetext = sharetext;
	}

	public String getLogo() {
		return logo;
	}

	public void setLogo(String logo) {
		this.logo = logo;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getSharetextsm() {
		return sharetextsm;
	}

	public void setSharetextsm(String sharetextsm) {
		this.sharetextsm = sharetextsm;
	}
}
