package org.iiitb.fb.modals;

public class Like {
	
	private int user_id;
	private String fullName;
	
	public Like() {

	}
	
	public Like(int user_id, String fullName) {
		super();
		this.user_id = user_id;
		this.fullName = fullName;
	}
	
	public int getUser_id() {
		return user_id;
	}
	public void setUser_id(int user_id) {
		this.user_id = user_id;
	}
	public String getFullName() {
		return fullName;
	}
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}
}
