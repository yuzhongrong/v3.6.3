package model;

import java.io.Serializable;

public class Feddbackinfo implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public String id;
	// public String user_id;
	public String problem;
	// public String answer;
	// public String type;
	public String image;
	public String c_time;
	public String imgSite;

	public Feddbackinfo(String id,
	// String user_id,
			String problem,
			// String answer,
			// String type,
			String image, String c_time,String imgSite) {

		this.id = id;
		// this.user_id=user_id;
		this.problem = problem;
		this.problem = problem;
		this.imgSite = imgSite;
		// this.answer=answer;
		// this.type=type;
		this.image = image;
		this.c_time = c_time;

	}

}
