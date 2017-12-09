package org.iiitb.fb.services;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.iiitb.fb.database.DataAccessObject;
import org.iiitb.fb.modals.Group;
import org.iiitb.fb.modals.Post;
import org.iiitb.fb.modals.Post_data;
import org.iiitb.fb.modals.Profile;

public class GroupService {

	private Connection connection;

	public GroupService() {
		connection = DataAccessObject.getInstance().Connect();

	}

	public List<Group> getGroupList(int user_id) {

		List<Group> groupList = getMyPendingGroupList(user_id);
		groupList.addAll(getGroupListCreatedByMe(user_id));
		groupList.addAll(getMyGroupList(user_id));

		return groupList;
	}

	// group creation
	public Group createGroup(String groupName, int user_id) {
		Group group = null;

		try {
			PreparedStatement preparedstmnt = connection
					.prepareStatement(
							"insert into groupinfo (group_name,user_id_fk,created_datetime) values(?,?,?)",
							Statement.RETURN_GENERATED_KEYS);
			preparedstmnt.setString(1, groupName);
			preparedstmnt.setInt(2, user_id);

			long millis = System.currentTimeMillis();
			Date date = new Date(millis);
			preparedstmnt.setDate(3, date);

			preparedstmnt.executeUpdate();

			ResultSet rs = preparedstmnt.getGeneratedKeys();

			if (rs.next()) {
				int group_id = rs.getInt(1);

				// addGroupMember(group_id, user_id,0,1);

				preparedstmnt = connection
						.prepareStatement("insert into groupmemberdetails (group_id_fk,user_id_fk,is_admin,member_status) values(?,?,?,?)");
				preparedstmnt.setInt(1, group_id);
				preparedstmnt.setInt(2, user_id);
				preparedstmnt.setInt(3, 1);// for group owner 
											
				preparedstmnt.setInt(4, 1);
				preparedstmnt.executeUpdate();

				System.out.println("Group member/admin info updated !");

				group = new Group(group_id, groupName, user_id, date, "c");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return group;
	}

	// status 1 : joined, 2 : Pending 3: join request
	// groups joined by me
	public List<Group> getMyGroupList(int user_id) {

		List<Group> groupList = null;

		try {

			System.out.println(connection);

			String sql = "select gs.group_id,gs.group_name,gs.created_datetime from groupmemberdetails gi,groupinfo gs where gs.group_id=gi.group_id_fk and gi.user_id_fk = ? and is_admin=? and member_status = ?";
			PreparedStatement stmt = connection.prepareStatement(sql);
			// System.out.println("after pre stmt in get Friend list");
			stmt.setInt(1, user_id);
			stmt.setInt(2, 0);
			stmt.setInt(3, 1);
			ResultSet rs = stmt.executeQuery();
			groupList = new ArrayList<Group>();
			while (rs.next()) {
				Group group = new Group(rs.getInt(1), rs.getString(2), user_id,
						rs.getDate(3), "j");

				groupList.add(group);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return groupList;
	}

	// // invites pending groups
	public List<Group> getMyPendingGroupList(int user_id) {

		List<Group> groupList = null;

		try {

			String sql = "select gs.group_id,gs.group_name,gs.created_datetime from groupmemberdetails gi,groupinfo gs where (gs.group_id=gi.group_id_fk) and (gi.user_id_fk = ?) and (member_status = ?)";
			PreparedStatement stmt = connection.prepareStatement(sql);
			// System.out.println("after pre stmt in get Friend list");
			stmt.setInt(1, user_id);
			stmt.setInt(2, 2);
			ResultSet rs = stmt.executeQuery();
			groupList = new ArrayList<Group>();
			while (rs.next()) {

				Group group = new Group(rs.getInt(1), rs.getString(2), user_id,
						rs.getDate(3), "p");
				groupList.add(group);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return groupList;
	}

	// Groups created by me
	public List<Group> getGroupListCreatedByMe(int user_id) {

		List<Group> groupList = null;

		try {

			String sql = "select group_id,group_name,created_datetime from groupinfo where user_id_fk = ?";
			PreparedStatement stmt = connection.prepareStatement(sql);
			// System.out.println("after pre stmt in get Friend list");
			stmt.setInt(1, user_id);
			ResultSet rs = stmt.executeQuery();
			groupList = new ArrayList<Group>();

			System.out.println("GroupList created by me !");

			while (rs.next()) {

				Group group = new Group(rs.getInt(1), rs.getString(2), user_id,
						rs.getDate(3), "c");
				groupList.add(group);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return groupList;
	}

	public List<Group> getDiscoverableGroupList(int user_id) {

		List<Group> groupList = null;

		try {

			System.out.println(connection);

			String sql = "select group_id,group_name,created_datetime from groupinfo where group_id not in (select DISTINCT group_id_fk from groupmemberdetails where user_id_fk=?)";
			PreparedStatement stmt = connection.prepareStatement(sql);
			// System.out.println("after pre stmt in get Friend list");
			stmt.setInt(1, user_id);
			
			ResultSet rs = stmt.executeQuery();
			groupList = new ArrayList<Group>();
			while (rs.next()) {
				Group group = new Group(rs.getInt(1), rs.getString(2), user_id,
						rs.getDate(3), "j");

				groupList.add(group);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return groupList;
	}
	
	public int RemoveGroupMember(int user_id,int group_id, int member_id) {
		int mid=0;
		String sql1="select user_id_fk from groupinfo where group_id=?";
		PreparedStatement preparedstmnt2, preparedstmnt3, preparedstmnt4, preparedstmnt5;
		PreparedStatement ps;
		int result = 0;
		try {
			PreparedStatement preparedstmnt = connection.prepareStatement(sql1);
			int ownerid=0;
			preparedstmnt.setInt(1, group_id);

			ResultSet rs = preparedstmnt.executeQuery();
			while(rs.next())
			{
				ownerid=rs.getInt(1);
			}
			if(user_id==ownerid && user_id!=member_id)
			{
				String sql = "DELETE FROM groupmemberdetails WHERE group_id_fk=? AND user_id_fk = ?";
		
	
			PreparedStatement preparedstmnt1 = connection.prepareStatement(sql);

			preparedstmnt1.setInt(1, group_id);
			preparedstmnt1.setInt(2, member_id);

			result = preparedstmnt1.executeUpdate();
			}
			else if(user_id==ownerid && user_id==member_id)
			{
				
					preparedstmnt3 = connection
							.prepareStatement("DELETE from groupmemberdetails WHERE group_id_fk = ? AND user_id_fk = ?");
					preparedstmnt3.setInt(1, group_id);
					preparedstmnt3.setInt(2, member_id);
					result = preparedstmnt3.executeUpdate();

					String sql2 = "select user_id_fk from groupmemberdetails where group_id_fk = ? limit 1";
					preparedstmnt4 = connection.prepareStatement(sql2);
					preparedstmnt4.setInt(1, group_id);
					ResultSet rs1 = preparedstmnt4.executeQuery();
					while (rs1.next()) {
						mid = rs1.getInt(1);
					}

					System.out.println(mid);
					if (mid != 0) {
						preparedstmnt2 = connection
								.prepareStatement("update groupinfo set user_id_fk = ? where group_id=?");
						preparedstmnt2.setInt(1, mid);
						preparedstmnt2.setInt(2, group_id);
						result = preparedstmnt2.executeUpdate();
						
						ps = connection
								.prepareStatement("update groupmemberdetails set is_admin=? where group_id_fk=? and user_id_fk=?");
						ps.setInt(1, 1);
						ps.setInt(2, group_id);
						ps.setInt(3, mid);
						result = ps.executeUpdate();
					} else {
						preparedstmnt5 = connection
								.prepareStatement("DELETE from groupinfo WHERE group_id = ? AND user_id_fk = ?");
						preparedstmnt5.setInt(1, group_id);
						preparedstmnt5.setInt(2, member_id);
						result = preparedstmnt5.executeUpdate();
					}
				}
			
			else
			{
				result=0;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return result;
	}
	
	public int DeleteGroupMemberStatus(int group_id, int user_id) {
		String sql = "DELETE FROM groupmemberdetails WHERE 	group_id_fk=? AND user_id_fk = ?";
		int result = 0;
		try {
			PreparedStatement preparedstmnt = connection.prepareStatement(sql);

			preparedstmnt.setInt(1, group_id);
			preparedstmnt.setInt(2, user_id);

			result = preparedstmnt.executeUpdate();

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return result;
	}

	public int updateGroupMemberStatus(int group_id, int user_id, int status) {

		String sql = "UPDATE groupmemberdetails SET member_status = ? WHERE group_id_fk=? AND user_id_fk = ?";
		int result = 0;
		try {
			PreparedStatement preparedstmnt = connection.prepareStatement(sql);
			preparedstmnt.setInt(1, status);
			preparedstmnt.setInt(2, group_id);
			preparedstmnt.setInt(3, user_id);

			result = preparedstmnt.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return result;
	}

	//status :1(joined)
	public List<Profile> getMembersOfAGroup(int group_id) {

		List<Profile> listOfMember = null;

		try {

			String sql = "SELECT user_id_fk FROM groupmemberdetails WHERE group_id_fk=? and member_status=?";
			PreparedStatement stmt = connection.prepareStatement(sql);
			stmt.setInt(1, group_id);
			stmt.setInt(2, 1);

			ResultSet rs = stmt.executeQuery();
			listOfMember = new ArrayList<>();
			UserService service = new UserService();
			while (rs.next()) {

				int user_id = rs.getInt(1);

				Profile profile = service.getUserProfile(user_id);
				listOfMember.add(profile);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return listOfMember;
	}
	
	//get group join requests...status:3
		public List<Profile> getGroupJoinRequests(int user_id,int group_id) {

			List<Profile> listOfMember = null;
			int owner_id=0;
			try {
				//getting owner id of group
				String sql1="select user_id_fk from groupinfo where group_id=?";
				PreparedStatement stmt = connection.prepareStatement(sql1);
				stmt.setInt(1, group_id);

				ResultSet rs = stmt.executeQuery();
				while (rs.next()) {
					owner_id = rs.getInt(1);
				}
				if(owner_id==user_id)
				{
					
				
				String sql = "SELECT user_id_fk FROM groupmemberdetails WHERE group_id_fk=? and member_status=?";
				PreparedStatement stmt2 = connection.prepareStatement(sql);
				stmt2.setInt(1, group_id);
				stmt2.setInt(2, 3);

				ResultSet rs1 = stmt2.executeQuery();
				listOfMember = new ArrayList<>();
				UserService service = new UserService();
				while (rs1.next()) {

					int userid = rs1.getInt(1);

					Profile profile = service.getUserProfile(userid);
					listOfMember.add(profile);
				}
				}
				
			} catch (SQLException e) {
				e.printStackTrace();
			}

			return listOfMember;
		}
	

	//sending invites
	public Profile addMemberByAdmin(int group_id, int member_id) {

		PreparedStatement preparedstmnt = null;
		Profile profile = null;
		try {
			preparedstmnt = connection
					.prepareStatement("insert into groupmemberdetails(group_id_fk,user_id_fk,is_admin,member_status) values(?,?,?,?)");

			preparedstmnt.setInt(1, group_id);
			preparedstmnt.setInt(2, member_id);
			preparedstmnt.setInt(3, 0);
			preparedstmnt.setInt(4, 2);

			int result = preparedstmnt.executeUpdate();
			if (result > 0) {
				profile = new UserService().getUserProfile(member_id);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return profile;
	}

	public Group setGroupInfo(String groupName, int user_id) {
		Group group = null;
		long millis = System.currentTimeMillis();
		Date date = new Date(millis);
		try {
			PreparedStatement preparedstmnt = connection
					.prepareStatement(
							"insert into groupinfo (group_name,user_id_fk,created_on) values(?,?,?)",
							Statement.RETURN_GENERATED_KEYS);

			System.out.println(groupName);
			preparedstmnt.setString(1, groupName);
			preparedstmnt.setInt(2, user_id);
			preparedstmnt.setDate(3, date);

			preparedstmnt.executeUpdate();

			ResultSet rs = preparedstmnt.getGeneratedKeys();
			int id = 0;
			if (rs.next()) {
				id = rs.getInt(1);

			}

			PreparedStatement preparedstmnt2 = connection
					.prepareStatement("insert into groupmemberdetails (group_id_fk,user_id_fk,is_admin,member_status) values(?,?,?,?)");
			preparedstmnt2.setInt(1, id);
			preparedstmnt2.setInt(2, user_id);

			preparedstmnt2.setInt(3, 1);// group owner

			preparedstmnt2.setInt(4, 1);

			preparedstmnt2.executeUpdate();
			group = new Group(id, groupName, user_id, date, "c");

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return group;
	}

	// get a groupinfo
	public Group getGroup(int group_id) {
		ResultSet rs = null;
		Group group = null;

		try {

			System.out.println(connection);

			String sql = "select * from groupinfo where group_id=?";
			PreparedStatement stmt = connection.prepareStatement(sql);
			// System.out.println("after pre stmt in get Friend list");
			stmt.setInt(1, group_id);

			rs = stmt.executeQuery();
			group = new Group();
			if (rs.next()) {
				group = new Group(rs.getInt(1), rs.getString(2), rs.getInt(3),
						rs.getDate(4), "j");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return group;
	}

	// get group owner name
			public String getGroupOwnerName(int group_id,int user_id) {
				ResultSet rs = null;
				ResultSet rs1=null;
				int status=0;
				
				String ownername=null;
				try {
					int ownerid=0;
					System.out.println(connection);

					String sql1 = "select user_id_fk from groupmemberdetails where group_id_fk=? and is_admin=?";
					PreparedStatement stmt1 = connection.prepareStatement(sql1);
					// System.out.println("after pre stmt in get Friend list");
					stmt1.setInt(1, group_id);
					stmt1.setInt(2, 1);
					rs1 = stmt1.executeQuery();
					if (rs1.next()) 
					{
						 ownerid = rs1.getInt(1);					
					}
					if(ownerid!=0)
					{
						UserService us= new UserService();
						ownername=us.getUserFullName(ownerid);
						return ownername;
					}
					
				}
				catch (SQLException e) {
					e.printStackTrace();
				}
				return ownername;
			}
	// get user's status in a group
		public int getUserStatusInGroup(int group_id,int user_id) {
			ResultSet rs = null;
			
			int status=0;
			try {

				System.out.println(connection);

				String sql = "select member_status from groupmemberdetails where group_id_fk=? and user_id_fk=?";
				PreparedStatement stmt = connection.prepareStatement(sql);
				// System.out.println("after pre stmt in get Friend list");
				stmt.setInt(1, group_id);
				stmt.setInt(2, user_id);
				rs = stmt.executeQuery();
			
				if (rs.next()) 
				{
					status = rs.getInt(1);
					System.out.println("status :"+status);
					return status;
				}
			}
			catch (SQLException e) {
				e.printStackTrace();
			}
			return 0;
		}
	
	// Add group member
	public int Addmember(int user_id,int group_id, int member_id) {

		int result = 0;
		int isadmin=0;
		ResultSet rs=null;
		
		try {
		PreparedStatement preparedstmnt1 = connection
		.prepareStatement(
		"select user_id_fk from  groupinfo where group_id=? and user_id_fk=?");
		preparedstmnt1.setInt(1, group_id);
		preparedstmnt1.setInt(2, user_id);
		rs = preparedstmnt1.executeQuery();
		if (rs.next())
		{
		isadmin = rs.getInt(1);
		
		System.out.println(user_id+"  inside if "+member_id);
		}
		if(isadmin!=0)
		{
			PreparedStatement preparedstmnt = connection
					.prepareStatement(
							"insert into groupmemberdetails (group_id_fk,user_id_fk,is_admin,member_status) values(?,?,?,?)");
			
			System.out.println("adding group member");
			preparedstmnt.setInt(1, group_id);
			preparedstmnt.setInt(2, member_id);
			preparedstmnt.setInt(3, 0);
			preparedstmnt.setInt(4, 1);

			result = preparedstmnt.executeUpdate();
		}
		else
		{
			PreparedStatement preparedstmnt2 = connection
					.prepareStatement(
					"insert into groupmemberdetails (group_id_fk,user_id_fk,is_admin,member_status) values(?,?,?,?)");

					System.out.println("inviting friend");
					preparedstmnt2.setInt(1, group_id);
					preparedstmnt2.setInt(2, member_id);
					preparedstmnt2.setInt(3, 0);
					preparedstmnt2.setInt(4, 2);

					result = preparedstmnt2.executeUpdate();
		}
		} catch (SQLException e) {
		e.printStackTrace();
		return result;
		}
		return result;
		}
	
	//join group request *********status: 3(requested)
	public int joinGroupRequest(int group_id, int member_id) {

		PreparedStatement preparedstmnt = null;
		int result=0;
		try {
			preparedstmnt = connection
					.prepareStatement("insert into groupmemberdetails(group_id_fk,user_id_fk,is_admin,member_status) values(?,?,?,?)");

			preparedstmnt.setInt(1, group_id);
			preparedstmnt.setInt(2, member_id);
			preparedstmnt.setInt(3, 0);
			preparedstmnt.setInt(4, 3);

			result = preparedstmnt.executeUpdate();
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}
	
	// leave group
	public int leaveGroup(int member_id, int group_id) {

		PreparedStatement preparedstmnt, preparedstmnt1, preparedstmnt2, preparedstmnt3, preparedstmnt4, preparedstmnt5;
		PreparedStatement ps;
		int result = 0;
		int result1 = 0;

		int owner = 0;
		int mid = 0;
		try {
			System.out.println("ininin");
			String sql = "select count(group_id) from groupinfo where group_id = ? AND user_id_fk = ?";
			preparedstmnt = connection.prepareStatement(sql);
			preparedstmnt.setInt(1, group_id);
			preparedstmnt.setInt(2, member_id);
			ResultSet rs = preparedstmnt.executeQuery();
			while (rs.next()) {
				owner = rs.getInt(1);
			}
			System.out.println(owner);
			if (owner == 0) {
				preparedstmnt3 = connection
						.prepareStatement("DELETE from groupmemberdetails WHERE group_id_fk = ? AND user_id_fk = ?");
				preparedstmnt3.setInt(1, group_id);
				preparedstmnt3.setInt(2, member_id);
				result = preparedstmnt3.executeUpdate();
			} else {
				preparedstmnt1 = connection
						.prepareStatement("DELETE from groupmemberdetails WHERE group_id_fk = ? AND user_id_fk = ?");
				preparedstmnt1.setInt(1, group_id);
				preparedstmnt1.setInt(2, member_id);
				result = preparedstmnt1.executeUpdate();

				String sql1 = "select user_id_fk from groupmemberdetails where group_id_fk = ? limit 1";
				preparedstmnt4 = connection.prepareStatement(sql1);
				preparedstmnt4.setInt(1, group_id);
				ResultSet rs1 = preparedstmnt4.executeQuery();
				while (rs1.next()) {
					mid = rs1.getInt(1);
				}

				System.out.println(mid);
				if (mid != 0) {
					preparedstmnt2 = connection
							.prepareStatement("update groupinfo set user_id_fk = ? where group_id=?");
					preparedstmnt2.setInt(1, mid);
					preparedstmnt2.setInt(2, group_id);
					result = preparedstmnt2.executeUpdate();
					
					ps = connection
							.prepareStatement("update groupmemberdetails set is_admin=? where group_id_fk=? and user_id_fk=?");
					ps.setInt(1, 1);
					ps.setInt(2, group_id);
					ps.setInt(3, mid);
					result = ps.executeUpdate();
				} else {
					preparedstmnt5 = connection
							.prepareStatement("DELETE from groupinfo WHERE group_id = ? AND user_id_fk = ?");
					preparedstmnt5.setInt(1, group_id);
					preparedstmnt5.setInt(2, member_id);
					result = preparedstmnt5.executeUpdate();
				}
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		System.out.println("memberid leaving groupid :" + member_id + "  "
				+ group_id);
		return result;
	}

	public Post createPostInGroup(InputStream fileInputStream, String fileName,
			String postData, int user_id,int group_id) {

		Post post = new PostService().createPost(fileInputStream, fileName,
				postData, user_id);

		try {
			PreparedStatement preparedstmnt = connection
					.prepareStatement("INSERT INTO group_post(group_id_fk, post_id_fk) values(?,?)");
			preparedstmnt.setInt(1, group_id);
			preparedstmnt.setInt(2, post.getPost_id());

			int result = preparedstmnt.executeUpdate();
			if (result > 0) {
				return post;
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;

	}

	public List<Post> getAllGroupPost(int group_id) {

		List<Post> listOfPost = null;

		PreparedStatement preparedstmnt;
		try {

			preparedstmnt = connection
					.prepareStatement("SELECT p.user_id_fk,p.postId,p.timeStamp,pd.post_data_message,pd.imageUrl FROM group_post as g INNER JOIN post as p INNER JOIN postdata as pd WHERE (p.post_data_Id_fk = pd.post_data_Id) AND g.post_id_fk=p.postId AND group_id_fk=? ORDER BY p.timeStamp DESC");
			preparedstmnt.setInt(1, group_id);

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
				post.setProfilePicUrl(new UserImageService().getProfilePicName(user_and_friend_id));
				listOfPost.add(post);
			}
		} catch (SQLException e) {

			e.printStackTrace();
		}

		System.out.println("Get All Post :" + listOfPost);
		return listOfPost;

	}

}
