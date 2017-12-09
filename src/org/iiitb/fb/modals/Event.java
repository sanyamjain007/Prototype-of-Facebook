package org.iiitb.fb.modals;

import java.util.Date;

public class Event {
	
	private int event_id;
	private String event_name;
	private String hosted_by;
	private Date start_date; 
	private Date end_date;
	private String location;
	private String description;
	private String summary;
	private String status;
	
	public Event(){
		
	}
	
	
	public Event(int event_id, String event_name, String hosted_by, Date start_date, Date end_date, String location, String description, String summary, String status){
		this.event_id=event_id;
		this.event_name=event_name;
		this.hosted_by=hosted_by;
		this.start_date=start_date;
		this.end_date=end_date;
		this.location=location;
		this.description=description;
		this.status=status;
		this.summary=summary;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public int getEvent_id() {
		return event_id;
	}

	public void setEvent_id(int event_id) {
		this.event_id = event_id;
	}

	public String getEvent_name() {
		return event_name;
	}

	public void setEvent_name(String event_name) {
		this.event_name = event_name;
	}
	
	public String getHosted_by() {
		return hosted_by;
	}

	public void setHosted_by(String user_id) {
		this.hosted_by = user_id;
	}

	public Date getStart_date() {
		return start_date;
	}

	public void setStart_date(Date start_date) {
		this.start_date = start_date;
	}

	public Date getEnd_date() {
		return end_date;
	}

	public void setEnd_date(Date end_date) {
		this.end_date = end_date;
	}
	
	public String getDescription() {
		return description;
	}

	public void setDescription(String desc) {
		this.description = desc;
	}
	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}
}
