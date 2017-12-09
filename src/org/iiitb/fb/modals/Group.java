package org.iiitb.fb.modals;

import java.sql.Date;

public class Group {

	private int group_id;
	private String group_name;
	private int created_by;
	private Date created_on;
	private String type;
	
	
	
	public Group() {

	}



	public int getGroup_id() {
		return group_id;
	}



	public void setGroup_id(int group_id) {
		this.group_id = group_id;
	}



	public String getGroup_name() {
		return group_name;
	}



	public void setGroup_name(String group_name) {
		this.group_name = group_name;
	}



	public int getCreated_by() {
		return created_by;
	}



	public void setCreated_by(int created_by) {
		this.created_by = created_by;
	}



	public Date getCreated_on() {
		return created_on;
	}



	public void setCreated_on(Date created_on) {
		this.created_on = created_on;
	}



	public String getType() {
		return type;
	}



	public void setType(String type) {
		this.type = type;
	}



	public Group(int group_id, String group_name, int created_by, Date created_on, String type) {
		super();
		this.group_id = group_id;
		this.group_name = group_name;
		this.created_by = created_by;
		this.created_on = created_on;
		this.type = type;
	}



	@Override
	public String toString() {
		return "Group [group_id=" + group_id + ", group_name=" + group_name + ", created_by=" + created_by
				+ ", created_on=" + created_on + ", type=" + type + "]";
	}

	

}
