package org.iiitb.fb.modals;

import java.sql.Timestamp;
import java.util.List;

public class Post {

	private String fullName;
	private int post_id;
	private int user_id;
	private String profilePicUrl;

	private Timestamp post_date_time;
	private Post_data post_data;

	private List<Profile> listOfProfile;

	public Post() {
		super();
	}

	public Post(String fullName, int post_id,int user_id, Timestamp post_date_time, Post_data post_data,
			List<Profile> listOfProfile) {
		super();
		this.fullName = fullName;
		this.post_id = post_id;
		this.user_id=user_id;
		this.post_date_time = post_date_time;
		this.post_data = post_data;
		this.listOfProfile = listOfProfile;
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

	public int getPost_id() {
		return post_id;
	}

	public void setPost_id(int post_id) {
		this.post_id = post_id;
	}

	public Timestamp getPost_date_time() {
		return post_date_time;
	}

	public void setPost_date_time(Timestamp post_date_time) {
		this.post_date_time = post_date_time;
	}

	public Post_data getPost_data() {
		return post_data;
	}

	public void setPost_data(Post_data post_data) {
		this.post_data = post_data;
	}

	public List<Profile> getListOfProfile() {
		return listOfProfile;
	}

	public void setListOfProfile(List<Profile> listOfProfile) {
		this.listOfProfile = listOfProfile;
	}
	public String getProfilePicUrl() {
		return profilePicUrl;
	}

	public void setProfilePicUrl(String profilePicUrl) {
		this.profilePicUrl = profilePicUrl;
	}
	@Override
	public String toString() {
		return "Post [fullName=" + fullName + ", post_id=" + post_id + ", post_date_time=" + post_date_time
				+ ", post_data=" + post_data + ", listOfProfile=" + listOfProfile + "]";
	}

	
	
	
}
