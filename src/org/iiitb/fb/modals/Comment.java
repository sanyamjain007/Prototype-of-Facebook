package org.iiitb.fb.modals;

import java.sql.Timestamp;


public class Comment {

	private int comment_id;
	private int user_id;
	private String userName;
	private String commentMessage;
	private Timestamp comment_Date_Time;
private String profilePicUrl;
	public Comment() {

	}
	
	public Comment(int comment_id, int user_id, String userName, String commentMessage, Timestamp comment_Date_Time) {
		super();
		this.comment_id = comment_id;
		this.user_id = user_id;
		this.userName = userName;
		this.commentMessage = commentMessage;
		this.comment_Date_Time = comment_Date_Time;
	}



	public int getComment_id() {
		return comment_id;
	}

	public void setComment_id(int comment_id) {
		this.comment_id = comment_id;
	}

	public int getUser_id() {
		return user_id;
	}

	public void setUser_id(int user_id) {
		this.user_id = user_id;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getCommentMessage() {
		return commentMessage;
	}

	public void setCommentMessage(String commentMessage) {
		this.commentMessage = commentMessage;
	}

	public Timestamp getComment_Date_Time() {
		return comment_Date_Time;
	}

	public void setComment_Date_Time(Timestamp comment_Date_Time) {
		this.comment_Date_Time = comment_Date_Time;
	}

	public String getProfilePicUrl() {
		return profilePicUrl;
	}

	public void setProfilePicUrl(String profilePicUrl) {
		this.profilePicUrl = profilePicUrl;
	}
}
