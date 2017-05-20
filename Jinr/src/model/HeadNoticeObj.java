package model;

/**
 * 首页公告条
 */
public class HeadNoticeObj {
	public String edition; // 版本号
	public String content; // 内容
	public String url; // 地址

	public HeadNoticeObj(String edition, String content, String url) {
		super();
		this.edition = edition;
		this.content = content;
		this.url = url;
	}
}
