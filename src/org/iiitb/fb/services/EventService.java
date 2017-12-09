package org.iiitb.fb.services;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.iiitb.fb.database.DataAccessObject;
import org.iiitb.fb.modals.Event;
import org.iiitb.fb.modals.Post;
import org.iiitb.fb.modals.Post_data;
import org.iiitb.fb.modals.Profile;

public class EventService {

	private Connection connection;

	public EventService() {
		connection = DataAccessObject.getInstance().Connect();
	}

	public List<Event> getEventList1(int user_id,int friend_id) {
		// List<Event> groupList = getInviteList(user_id);
		// groupList.addAll(getHostedList(user_id));
		// groupList.addAll(getInterestedAndGoingList(user_id));
		List<Event> groupList = new ArrayList<Event>();
		
		String sql = "select es.event_id, es.event_name, es.user_id_fk, es.start_date, es.end_date, es.location, es.description,es.summary, ei.status from eventmemberdetails ei,eventinfo es where (es.event_id=ei.event_id_fk) and (ei.user_id_fk = ?) and (ei.status=?)";
		String sql1 = "select * from profile where user_id_fk=?";
		PreparedStatement stmt, stmt1;
		try {
			stmt = connection.prepareStatement(sql);
			stmt1 = connection.prepareStatement(sql1);
			stmt.setInt(1, friend_id);
			stmt.setString(2, "accepted");
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				Event event = new Event(rs.getInt(1), rs.getString(2), null,
						rs.getDate(4), rs.getDate(5), rs.getString(6),
						rs.getString(7), rs.getString(8),rs.getString(9));

				int id = rs.getInt(3);
				stmt1.setInt(1, id);
				ResultSet rs1 = stmt1.executeQuery();

				if (rs1.next())
					event.setHosted_by(rs1.getString(2) + " "
							+ rs1.getString(3));
				System.out.println(event.getHosted_by() + "...........hello");
				groupList.add(event);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return groupList;
	}
	
	public List<Event> getEventList(int user_id) {
		// List<Event> groupList = getInviteList(user_id);
		// groupList.addAll(getHostedList(user_id));
		// groupList.addAll(getInterestedAndGoingList(user_id));
		List<Event> groupList = new ArrayList<Event>();
		
		System.out.println("id :::::::"+user_id);
		String sql = "select es.event_id, es.event_name, es.user_id_fk, es.start_date, es.end_date, es.location, es.description,es.summary, ei.status from eventmemberdetails ei,eventinfo es where (es.event_id=ei.event_id_fk) and (ei.user_id_fk = ?)";
		String sql1 = "select * from profile where user_id_fk=?";
		PreparedStatement stmt, stmt1;
		try {
			stmt = connection.prepareStatement(sql);
			stmt1 = connection.prepareStatement(sql1);
			stmt.setInt(1, user_id);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				Event event = new Event(rs.getInt(1), rs.getString(2), null,
						rs.getDate(4), rs.getDate(5), rs.getString(6),
						rs.getString(7), rs.getString(8),rs.getString(9));

				int id = rs.getInt(3);
				stmt1.setInt(1, id);
				ResultSet rs1 = stmt1.executeQuery();

				if (rs1.next())
					event.setHosted_by(rs1.getString(2) + " "
							+ rs1.getString(3));
				System.out.println(event.getHosted_by() + "...........hello");
				groupList.add(event);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return groupList;
	}

	// event creation
	public Event createEvent(String name, int uid, Date start, Date end,
			String loc, String desc) {
		int event_id = 0;
		Event event = null;

		try {
			String sql = "insert into eventinfo (event_name, user_id_fk, start_date, end_date, location, description) values (?,?,?,?,?,?)";
			PreparedStatement stm = connection.prepareStatement(sql,
					Statement.RETURN_GENERATED_KEYS);
			stm.setString(1, name);
			stm.setInt(2, uid);
			stm.setDate(3, start);
			stm.setDate(4, end);
			stm.setString(5, loc);
			stm.setString(6, desc);
			stm.executeUpdate();
			ResultSet rs = stm.getGeneratedKeys();
			System.out.println("statement" + stm);

			if (rs.next())
				event_id = rs.getInt(1);

			sql = "insert into eventmemberdetails values (?, ?, ?)";
			stm = connection.prepareStatement(sql);
			String sql1 = "select * from profile where user_id_fk=?";
			PreparedStatement stm1 = connection.prepareStatement(sql1);
			stm.setInt(1, event_id);
			stm.setInt(2, uid);
			stm.setString(3, "host");
			stm.executeUpdate();
			event = new Event(event_id, name, null, start, end, loc, desc,
					"host", null);
			int id = uid;
			stm1.setInt(1, id);
			rs = stm1.executeQuery();
			if (rs.next())
				event.setHosted_by(rs.getString(2) + " " + rs.getString(3));
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return event;
	}

	// get event info
	public Event getEvent(int event_id) {
		ResultSet rs = null;
		Event event = null;
		try {
			String sql = "select * from eventinfo where event_id=?";
			PreparedStatement stmt = connection.prepareStatement(sql);
			String sql1 = "select * from profile where user_id_fk=?";
			PreparedStatement stmt1 = connection.prepareStatement(sql1);
			stmt.setInt(1, event_id);
			rs = stmt.executeQuery();
			if (rs.next())
				event = new Event(rs.getInt(1), rs.getString(2), null,
						rs.getDate(4), rs.getDate(5), rs.getString(6),
						rs.getString(7), rs.getString(8),null);
			int id = rs.getInt(3);
			stmt1.setInt(1, id);
			rs = stmt1.executeQuery();
			if (rs.next())
				event.setHosted_by(rs.getString(2) + " " + rs.getString(3));
			System.out.println("hosted by........." + event.getHosted_by());
			return event;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public Post createPostInEvent(InputStream fileInputStream, String fileName,
			String postData, int user_id, int event_id) {
		Post post = new PostService().createPost(fileInputStream, fileName,
				postData, user_id);
		try {
			PreparedStatement preparedstmnt = connection
					.prepareStatement("INSERT INTO event_post(event_id_fk, post_id_fk) values(?,?)");
			preparedstmnt.setInt(1, event_id);
			preparedstmnt.setInt(2, post.getPost_id());
			int result = preparedstmnt.executeUpdate();
			if (result > 0)
				return post;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public List<Post> getAllEventPost(int event_id) {
		List<Post> listOfPost = null;
		PreparedStatement preparedstmnt;
		try {
			preparedstmnt = connection
					.prepareStatement("SELECT p.user_id_fk,p.postId,p.timeStamp,pd.post_data_message,pd.imageUrl FROM event_post as e INNER JOIN post as p INNER JOIN postdata as pd WHERE (p.post_data_Id_fk = pd.post_data_Id) AND e.post_id_fk=p.postId AND event_id_fk=? ORDER BY p.timeStamp DESC");
			preparedstmnt.setInt(1, event_id);
			ResultSet rs = preparedstmnt.executeQuery();
			listOfPost = new ArrayList<>();
			UserService service = new UserService();
			List<Profile> lisOfLike = null;
			while (rs.next()) {
				int user_and_friend_id = rs.getInt(1);
				int post_id = rs.getInt(2);
				Timestamp timeStamp = rs.getTimestamp(3);
				String postMessage = rs.getString(4);
				String imageUrl = rs.getString(5);
				String fullName = service.getUserFullName(user_and_friend_id);
				lisOfLike = new LikeService().getPostLikes(post_id);
				Post post = new Post(fullName, post_id, user_and_friend_id,
						timeStamp, new Post_data(postMessage, imageUrl),
						lisOfLike);
				post.setProfilePicUrl(new UserImageService()
						.getProfilePicName(user_and_friend_id));
				listOfPost.add(post);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return listOfPost;
	}

	public int changeStatus(int event_id, int user_id, String status) {
		String sql = "UPDATE eventmemberdetails SET status = ? WHERE event_id_fk=? AND user_id_fk = ?";
		int result = 0;
		try {
			PreparedStatement preparedstmnt = connection.prepareStatement(sql);
			preparedstmnt.setString(1, status);
			preparedstmnt.setInt(2, event_id);
			preparedstmnt.setInt(3, user_id);
			result = preparedstmnt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}

	public int changeDiscoveryStatus(int event_id, int user_id, String status) {
		String sql = "insert into eventmemberdetails values (?,?,?)";
		int result = 0;
		try {
			PreparedStatement preparedstmnt = connection.prepareStatement(sql);
			preparedstmnt.setInt(1, event_id);
			preparedstmnt.setInt(2, user_id);
			preparedstmnt.setString(3, status);
			result = preparedstmnt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}

	public int deleteEvent(int event_id, int user_id) {
		int result = 0;
		try {
			PreparedStatement preparedstmnt = connection
					.prepareStatement("delete from eventmemberdetails WHERE event_id_fk=? AND user_id_fk = ?");
			preparedstmnt.setInt(1, event_id);
			preparedstmnt.setInt(2, user_id);
			result = preparedstmnt.executeUpdate();
			return result;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}

	public int[] getCount(int event_id) {
		int count[] = new int[2];
		try {
			System.out.println("inside count service");
			PreparedStatement stm = connection
					.prepareStatement("select count(*) from eventmemberdetails where event_id_fk=? and status=?");
			stm.setInt(1, event_id);
			stm.setString(2, "going");
			ResultSet rs = stm.executeQuery();
			// count = new ArrayList<Integer>();
			if (rs.next())
				count[0] = rs.getInt(1);

			stm = connection
					.prepareStatement("select count(*) from eventmemberdetails where event_id_fk=? and status=?");
			stm.setInt(1, event_id);
			stm.setString(2, "May Be");
			rs = stm.executeQuery();
			if (rs.next())
				count[1] = rs.getInt(1);
			System.out.println("printing in count service : " + count[0] + " "
					+ count[1]);
			return count;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public ArrayList<Profile> getFriendsToInvite(String user_id, String event_id) {
		ArrayList<Profile> inviteList = new ArrayList<Profile>();
		try {
			String sql = "SELECT * FROM profile WHERE user_id_fk IN "
					+ "(SELECT friend_id_fk FROM friendlist WHERE user_id_fk = ? AND friend_id_fk NOT IN "
					+ "(SELECT user_id_fk FROM `eventmemberdetails` WHERE event_id_fk = ?))";

			System.out.println(user_id + "  " + event_id);

			PreparedStatement preparedStatement = connection
					.prepareStatement(sql);
			preparedStatement.setInt(1, Integer.parseInt(user_id));
			preparedStatement.setInt(2, Integer.parseInt(event_id));
			ResultSet rs = preparedStatement.executeQuery();
			while (rs.next()) {
				Profile inviteProfile = new Profile();
				inviteProfile.setUser_id(rs.getInt(1));
				inviteProfile.setFirst_name(rs.getString(2));
				inviteProfile.setLast_name(rs.getString(3));
				inviteProfile.setDate_of_birth(rs.getDate(4));
				inviteProfile.setGender(rs.getString(5).toCharArray()[0]);
				inviteProfile.setContact_no(rs.getString(6));
				inviteProfile.setHome_town(rs.getString(7));
				inviteProfile.setHigh_school(rs.getString(8));
				inviteProfile.setCurrent_city(rs.getString(9));
				inviteProfile.setCollege(rs.getString(10));
				inviteProfile.setProfilePicUrl(new UserImageService().getProfilePicName(inviteProfile.getUser_id()));
				inviteList.add(inviteProfile);
				System.out.println("invite " + inviteProfile.getUser_id());
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return inviteList;
	}

	public int sendFriendInvitation(String userId, String eventId) {
		int rs = -1;
		System.out.println("reached service class event invite");
		try {
			String sql = "insert into eventmemberdetails values (?,?,?)";
			PreparedStatement ps = connection.prepareStatement(sql);
			ps.setInt(1, Integer.parseInt(eventId));
			ps.setInt(2, Integer.parseInt(userId));
			ps.setString(3, "invite");
			rs = ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return rs;
	}

	public List<Event> getDiscoverEvent(int user_id) {

		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd");
		LocalDate localDate = LocalDate.now();
		System.out.println(dtf.format(localDate)); // 2016/11/16

		List<Event> discover_list = new ArrayList<Event>();
		Event event = null;
		ResultSet rst,rs;
		try {
			String sql = "select * from eventinfo where event_id NOT IN (select event_id_fk from eventmemberdetails where user_id_fk =?)";
			PreparedStatement ps = connection.prepareStatement(sql);
			ps.setInt(1, user_id);
			rst = ps.executeQuery();

			System.out.println("in service : ");
			String sql1="select * from profile where user_id_fk=?";
			ps=connection.prepareStatement(sql1);
			while (rst.next()) {
				event = new Event();
				ps.setInt(1, rst.getInt(3));
				rs=ps.executeQuery();
				System.out.println("event id in service = " + rst.getInt(1));
				event.setEvent_id(rst.getInt(1));
				event.setEvent_name(rst.getString(2));
				if(rs.next())
				event.setHosted_by(rs.getString(2)+" "+rs.getString(3));
				event.setStart_date(rst.getDate(4));
				event.setEnd_date(rst.getDate(5));
				event.setLocation(rst.getString(6));
				event.setDescription(rst.getString(7));
				discover_list.add(event);
				System.out.println(discover_list);
			}
			return discover_list;

		} catch (Exception e) {
			System.out.println(e);
		}
		return null;
	}

	// getting all upcoming events
	public List<Event> getComingEventList(int user_id) {
		List<Event> groupList = getEventList(user_id);
		List<Event> eventList = new ArrayList<Event>();
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		java.util.Date date = new java.util.Date();
		for (int i = 0; i < groupList.size(); i++) {
			java.util.Date date2 = null;
			try {
				date2 = dateFormat.parse(groupList.get(i).getEnd_date()
						.toString());
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (date2.after(date)) {
				eventList.add(groupList.get(i));
			}
		}
		return eventList;
	}
	
	// get all past events
	public List<Event> getPastEventList(int user_id) {
		List<Event> EventList = getEventList(user_id);
		List<Event> pastEventList = new ArrayList<Event>();
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		// DateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd");
		java.util.Date date = new java.util.Date();
		// System.out.println("hello1"+dateFormat.format(date));
		// java.util.Date date1=null;
		// System.out.println("hello2"+date1);
		for (int i = 0; i < EventList.size(); i++) {
			// System.out.println("hello3"+pastEventList.get(i).getEnd_date());
			java.util.Date date2 = null;
			try {
				date2 = dateFormat.parse(EventList.get(i).getEnd_date()
						.toString());
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (date.after(date2))
				pastEventList.add(EventList.get(i));
		}
		return pastEventList;
	}

	public String addSummary(int event_id, String summary) {
		String sql = "update eventinfo set summary=? where event_id = ?";
		PreparedStatement ps;
		try {
			ps = connection.prepareStatement(sql);
			ps.setString(1, summary);
			ps.setInt(2, event_id);
			int success = ps.executeUpdate();
			if (success == 1) {
				sql = "select summary from eventinfo where event_id="
						+ event_id;
				ps = connection.prepareStatement(sql);
				ResultSet rs = ps.executeQuery();
				while (rs.next()) {
					System.out.println(rs.getString(1));
					return rs.getString(1);
				}
			}
		} catch (SQLException e) {
			System.out.println(e.toString());
		}
		return null;
	}

	public List<Profile> Interestedmembers(int event_id)
	{
	List<Profile>  userlist = new ArrayList<Profile>();
	try {
	String sql = "SELECT `user_id_fk` FROM `eventmemberdetails` WHERE event_id_fk=? AND status='May Be' ";
	PreparedStatement ps = connection.prepareStatement(sql);
	ps.setInt(1, event_id);
	ResultSet rs = ps.executeQuery();

	while (rs.next()) {
	Profile user = new Profile();
	UserService obj = new UserService();
	user = obj.getUserProfile(rs.getInt(1));
	userlist.add(user);
	}

	} catch (Exception e) {
	System.out.println(e);
	}
	return userlist;
	}
}
