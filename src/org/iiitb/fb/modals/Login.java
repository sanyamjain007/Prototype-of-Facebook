package org.iiitb.fb.modals;

public class Login {
	
	private int user_id;
	private String emailId;
	private String password;
	
	public Login()
	{
		
	}
	public Login(int user_id, String emailId, String password) {
		super();
		this.setUser_id(user_id);
		this.setEmailId(emailId);
		this.setPassword(password);
	}

	public int getUser_id() {
		return user_id;
	}

	public void setUser_id(int user_id) {
		this.user_id = user_id;
	}

	public String getEmailId() {
		return emailId;
	}

	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
}
