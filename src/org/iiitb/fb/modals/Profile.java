package org.iiitb.fb.modals;

import java.sql.Date;

public class Profile {

	private int user_id;
	private String first_name;
	private String last_name;
	private Date date_of_birth;
	private Character gender;
	private String contact_no;
	private String home_town;
	private String profilePicUrl;
	private String high_school;
	private String current_city;
	private String college;
	private String employer;
	private String graduate_school;


	public Profile() {
	}

	

	

	public Profile(int user_id, String first_name, String last_name, Date date_of_birth, Character gender,
			String contact_no, String home_town, String high_school, String current_city, String college,
			String employer, String graduate_school) {
		super();
		this.user_id = user_id;
		this.first_name = first_name;
		this.last_name = last_name;
		this.date_of_birth = date_of_birth;
		this.gender = gender;
		this.contact_no = contact_no;
		this.home_town = home_town;
		this.high_school = high_school;
		this.current_city = current_city;
		this.college = college;
		this.employer = employer;
		this.graduate_school = graduate_school;
	}



	public String getProfilePicUrl() {
		return profilePicUrl;
	}

	public void setProfilePicUrl(String profilePicUrl) {
		this.profilePicUrl = profilePicUrl;
	}

	public int getUser_id() {
		return user_id;
	}

	public void setUser_id(int user_id) {
		this.user_id = user_id;
	}

	public String getFirst_name() {
		return first_name;
	}

	public void setFirst_name(String first_name) {
		this.first_name = first_name;
	}

	public String getLast_name() {
		return last_name;
	}

	public void setLast_name(String last_name) {
		this.last_name = last_name;
	}

	public Date getDate_of_birth() {
		return date_of_birth;
	}

	public void setDate_of_birth(Date date_of_birth) {
		this.date_of_birth = date_of_birth;
	}

	public Character getGender() {
		return gender;
	}

	public void setGender(Character gender) {
		this.gender = gender;
	}

	public String getContact_no() {
		return contact_no;
	}

	public void setContact_no(String contact_no) {
		this.contact_no = contact_no;
	}

	public String getHome_town() {
		return home_town;
	}

	public void setHome_town(String home_town) {
		this.home_town = home_town;
	}

	public String getHigh_school() {
		return high_school;
	}

	public void setHigh_school(String high_school) {
		this.high_school = high_school;
	}

	public String getCurrent_city() {
		return current_city;
	}

	public void setCurrent_city(String current_city) {
		this.current_city = current_city;
	}

	public String getCollege() {
		return college;
	}

	public void setCollege(String college) {
		this.college = college;
	}
	
	public String getEmployer() {
		return employer;
	}

	public void setEmployer(String employer) {
		this.employer = employer;
	}

	public String getGraduate_school() {
		return graduate_school;
	}

	public void setGraduate_school(String graduate_school) {
		this.graduate_school = graduate_school;
	}

	@Override
	public String toString() {
		return "Profile [user_id=" + user_id + ", first_name=" + first_name
				+ ", last_name=" + last_name + ", date_of_birth="
				+ date_of_birth + ", gender=" + gender + ", contact_no="
				+ contact_no + ", home_town=" + home_town + ", high_school="
				+ high_school + ", current_city=" + current_city + ", college="
				+ college + ", employer=" + employer +", graduate_school=" + graduate_school +"]";
	}

}