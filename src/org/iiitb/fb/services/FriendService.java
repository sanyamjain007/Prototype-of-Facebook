package org.iiitb.fb.services;

import java.sql.Array;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.iiitb.fb.database.DataAccessObject;
import org.iiitb.fb.modals.Profile;

public class FriendService {

	private Connection connection;

	public FriendService() {

		connection = DataAccessObject.getInstance().Connect();
	}

	public int sendFriendRequest(int user_id, int friend_id) {

		PreparedStatement preparedstmnt;
		int result = 0;
		try {
			preparedstmnt = connection
					.prepareStatement("insert into friendrequest (user_id_fk, friend_id_fk) values(?,?)");
			preparedstmnt.setInt(1, user_id);
			preparedstmnt.setInt(2, friend_id);

			result = preparedstmnt.executeUpdate();

			System.out.println("Friend Request :" + user_id + "  " + friend_id);

		} catch (SQLException e) {

			e.printStackTrace();
			return result;
		}

		return result;

	}

	public int confirmFriendRequest(int friend_id, int user_id) {

		PreparedStatement preparedstmnt;
		int result = 0;
		try {
			preparedstmnt = connection
					.prepareStatement("DELETE from friendrequest WHERE user_id_fk = ? AND friend_id_fk = ?");
			preparedstmnt.setInt(1, user_id);
			preparedstmnt.setInt(2, friend_id);

			result = preparedstmnt.executeUpdate();

			if (result > 0) {
				System.out.println("CONFORM Friend Request :" + user_id + "  "
						+ friend_id);
				preparedstmnt = connection
						.prepareStatement("INSERT INTO friendlist (user_id_fk, friend_id_fk) values (?,?), (?,?)");
				preparedstmnt.setInt(1, user_id);
				preparedstmnt.setInt(2, friend_id);
				preparedstmnt.setInt(3, friend_id);
				preparedstmnt.setInt(4, user_id);

				result = preparedstmnt.executeUpdate();
			}

		} catch (SQLException e) {

			e.printStackTrace();
			return result;
		}

		return result;

	}

	public int cancelFriendRequest(int user_id, int friend_id) {

		PreparedStatement preparedstmnt;
		int result = 0;
		try {
			preparedstmnt = connection
					.prepareStatement("DELETE from friendrequest WHERE (user_id_fk = ? AND friend_id_fk = ?) OR (friend_id_fk = ? AND user_id_fk = ?)");
			preparedstmnt.setInt(1, user_id);
			preparedstmnt.setInt(2, friend_id);

			preparedstmnt.setInt(3, user_id);
			preparedstmnt.setInt(4, friend_id);

			result = preparedstmnt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		System.out.println("cancel Friend Request by sender :" + user_id + "  "
				+ friend_id);
		return result;
	}

	public List<Profile> getMutualFriend(int user_id) {

		List<Profile> mutualfriendList = null;

		try {

			String sql = "select DISTINCT f.friend_id_fk from friendlist AS f WHERE f.user_id_fk IN(SELECT friend_id_fk FROM friendlist WHERE user_id_fk=?) AND f.friend_id_fk NOT IN (SELECT friend_id_fk FROM friendlist WHERE user_id_fk=?) AND f.friend_id_fk!=? and f.friend_id_fk NOT IN(SELECT fr.user_id_fk FROM friendrequest AS fr WHERE fr.user_id_fk=f.friend_id_fk AND fr.friend_id_fk=? UNION SELECT fr.friend_id_fk FROM friendrequest AS fr WHERE fr.friend_id_fk=f.friend_id_fk AND fr.user_id_fk=?) AND f.friend_id_fk NOT IN(SELECT fs.suggestedfriend_id_fk from friend_suggestion as fs WHERE fs.friend_id_fk=? AND fs.suggestedfriend_id_fk=f.friend_id_fk UNION SELECT fs1.friend_id_fk from friend_suggestion as fs1 WHERE fs1.friend_id_fk=f.friend_id_fk AND fs1.suggestedfriend_id_fk=?) ";

			PreparedStatement preparedStatement = connection
					.prepareStatement(sql);
			// System.out.println("after pre stmt in get Friend list");
			preparedStatement.setInt(1, user_id);
			preparedStatement.setInt(2, user_id);
			preparedStatement.setInt(3, user_id);
			preparedStatement.setInt(4, user_id);
			preparedStatement.setInt(5, user_id);
			preparedStatement.setInt(6, user_id);
			preparedStatement.setInt(7, user_id);

			ResultSet rs = preparedStatement.executeQuery();

			mutualfriendList = new ArrayList<Profile>();

			Services service = new Services();

			while (rs.next()) {

				Profile friendProfile = service.getUser(rs
						.getInt("friend_id_fk"));
				mutualfriendList.add(friendProfile);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return mutualfriendList;

	}

	public int isFriend(String userId, String friendId) {
		// TODO Auto-generated method stub
		String sql = null;
		PreparedStatement preparedStatement = null;
		ResultSet rs = null;
		try {
			sql = "Select * from friendrequest where user_id_fk=? AND friend_id_fk=?";
			preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setInt(1, Integer.parseInt(friendId));
			preparedStatement.setInt(2, Integer.parseInt(userId));
			rs = preparedStatement.executeQuery();

			if (rs.next()) {
				return 1;
			}

			sql = "Select * from friendrequest where user_id_fk=? AND friend_id_fk=?";
			preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setInt(1, Integer.parseInt(userId));
			preparedStatement.setInt(2, Integer.parseInt(friendId));
			rs = preparedStatement.executeQuery();

			if (rs.next()) {
				return 2;
			}

			sql = "Select * from friendlist where user_id_fk=? AND friend_id_fk=?";
			preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setInt(1, Integer.parseInt(userId));
			preparedStatement.setInt(2, Integer.parseInt(friendId));
			rs = preparedStatement.executeQuery();

			if (rs.next()) {
				return 3;
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return 0;
	}

	public List<Profile> getFriendRequest(int user_id) {

		List<Profile> listOfFriendRequest = null;

		PreparedStatement preparedstmnt;
		try {
			preparedstmnt = connection
					.prepareStatement("SELECT user_id_fk from friendrequest WHERE friend_id_fk = ?");
			preparedstmnt.setInt(1, user_id);

			ResultSet rs = preparedstmnt.executeQuery();

			System.out.println("Friend Request result :" + user_id);
			listOfFriendRequest = new ArrayList<>();
			while (rs.next()) {
				int id = rs.getInt(1);
				Services service = new Services();
				listOfFriendRequest.add(service.getUser(id));
			}

		} catch (SQLException e) {

			e.printStackTrace();
		}

		return listOfFriendRequest;

	}

	public int unfriendFromFriendList(int friend_id, int user_id) {
		// TODO Auto-generated method stub

		PreparedStatement preparedstmnt;
		int result = 0;
		try {
			preparedstmnt = connection
					.prepareStatement("DELETE from friendlist WHERE (user_id_fk = ? AND friend_id_fk = ?) OR (friend_id_fk = ? AND user_id_fk = ?)");
			preparedstmnt.setInt(1, user_id);
			preparedstmnt.setInt(2, friend_id);

			preparedstmnt.setInt(3, user_id);
			preparedstmnt.setInt(4, friend_id);

			result = preparedstmnt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
			return 0;
		}

		System.out.println("unfriend by sender :" + user_id + "  " + friend_id);
		return result;

	}

	public ArrayList<Profile> getFriendsToSuggest(String user_id,
			String friend_id) {
		// TODO Auto-generated method stub
		int result = 0;
		PreparedStatement preparedstmnt;
		ArrayList<Integer> arrayList = new ArrayList<Integer>();
		ArrayList<Profile> arrayProfiles = new ArrayList<Profile>();
		Profile profile;
		try {
			preparedstmnt = connection
					.prepareStatement("select friend_id_fk from friendlist WHERE (user_id_fk = ? AND friend_id_fk != ?)");
			preparedstmnt.setInt(1, Integer.parseInt(user_id));
			preparedstmnt.setInt(2, Integer.parseInt(friend_id));
			ResultSet rs = preparedstmnt.executeQuery();
			while (rs.next()) {
				System.out.print(rs.getInt(1) + " friend_Id of userId"
						+ user_id + " ");
				arrayList.add(rs.getInt(1));
			}
			System.out.println(arrayList + " before removing");
			System.out.println();
			preparedstmnt = connection
					.prepareStatement("select friend_id_fk from friendlist WHERE (user_id_fk = ? AND friend_id_fk!=?)");

			preparedstmnt.setInt(1, Integer.parseInt(friend_id));
			preparedstmnt.setInt(2, Integer.parseInt(user_id));

			rs = preparedstmnt.executeQuery();
			while (rs.next()) {
				System.out.print(rs.getInt(1) + " friend_Id of userId"
						+ friend_id + " ");
				result = rs.getInt(1);
				if (arrayList.contains(new Integer(result)))
					;
				arrayList.remove(new Integer(result));
			}
			System.out.println();
			System.out.println(arrayList + " after removing");

			String sql = "select friend_id_fk,suggestedfriend_id_fk from friend_suggestion WHERE (friend_id_fk= ? AND suggestedfriend_id_fk=?) OR (suggestedfriend_id_fk= ? AND friend_id_fk=?) ";
			ArrayList<Integer> newList = new ArrayList<Integer>();
			for (int i : arrayList) {

				preparedstmnt = connection.prepareStatement(sql);
				preparedstmnt.setInt(1, Integer.parseInt(friend_id));
				preparedstmnt.setInt(2, i);
				preparedstmnt.setInt(3, Integer.parseInt(friend_id));
				preparedstmnt.setInt(4, i);
				rs = preparedstmnt.executeQuery();
				// boolean result1=rs.next();
				// System.out.println(result1);

				while (rs.next()) {
					if(rs.getInt(1)==i)
						newList.add(rs.getInt(1));
					else if(rs.getInt(2)==i)
						newList.add(rs.getInt(2));
					// arrayList.remove(new Integer(i));
				}

			}
			System.out.println(newList);
			if (!newList.isEmpty()) {
				arrayList.removeAll(newList);
			}

			sql = "select * from profile WHERE user_id_fk = ? ";

			for (int i : arrayList) {

				preparedstmnt = connection.prepareStatement(sql);
				preparedstmnt.setInt(1, i);
				rs = preparedstmnt.executeQuery();
				if (rs.next()) {
					profile = new Profile();

					profile.setUser_id(rs.getInt(1));
					profile.setFirst_name(rs.getString(2));
					profile.setLast_name(rs.getString(3));
					profile.setDate_of_birth(rs.getDate(4));
					profile.setGender(rs.getString(5).toCharArray()[0]);
					profile.setContact_no(rs.getString(6));
					profile.setHome_town(rs.getString(7));
					profile.setHigh_school(rs.getString(8));
					profile.setCurrent_city(rs.getString(9));
					profile.setCollege(rs.getString(10));
					profile.setEmployer(rs.getString(11));
					profile.setGraduate_school(rs.getString(12));
					
					profile.setProfilePicUrl(new UserImageService()
							.getProfilePicName(profile.getUser_id()));
					arrayProfiles.add(profile);
				}

			}

		} catch (SQLException e) {
			e.printStackTrace();
			return arrayProfiles;
		}

		return arrayProfiles;
	}

	public int sendFriendSuggestion(int user_id, int friend_id,
			int suggestedFriend_id) {
		// TODO Auto-generated method stub
		System.out.println("ui " + user_id + " fi " + friend_id + " sfi "
				+ suggestedFriend_id);
		PreparedStatement preparedstmnt;
		String sql = "insert into friend_suggestion(user_id_fk, friend_id_fk,suggestedfriend_id_fk) values(?,?,?)";
		try {
			preparedstmnt = connection.prepareStatement(sql);
			preparedstmnt.setInt(1, user_id);
			preparedstmnt.setInt(2, friend_id);
			preparedstmnt.setInt(3, suggestedFriend_id);
			int i = preparedstmnt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
			return 0;
		}
		return 1;
	}

	public ArrayList<Profile> getFriendSuggestions(String user_id) {
		// TODO Auto-generated method stub
		ArrayList<Integer> arrayList = new ArrayList<Integer>();
		String sql = "select suggestedfriend_id_fk from friend_suggestion WHERE friend_id_fk = ? ";
		ArrayList<Profile> arrayProfiles = new ArrayList<Profile>();
		Profile profile = null;
		PreparedStatement preparedstmnt;
		try {
			preparedstmnt = connection.prepareStatement(sql);
			preparedstmnt.setInt(1, Integer.parseInt(user_id));
			ResultSet rs = preparedstmnt.executeQuery();
			while (rs.next()) {
				arrayList.add(rs.getInt(1));
			}

			sql = "select * from profile WHERE user_id_fk = ? ";

			for (int i : arrayList) {

				preparedstmnt = connection.prepareStatement(sql);
				preparedstmnt.setInt(1, i);
				rs = preparedstmnt.executeQuery();
				if (rs.next()) {
					profile = new Profile();

					profile.setUser_id(rs.getInt(1));
					profile.setFirst_name(rs.getString(2));
					profile.setLast_name(rs.getString(3));
					profile.setDate_of_birth(rs.getDate(4));
					profile.setGender(rs.getString(5).toCharArray()[0]);
					profile.setContact_no(rs.getString(6));
					profile.setHome_town(rs.getString(7));
					profile.setHigh_school(rs.getString(8));
					profile.setCurrent_city(rs.getString(9));
					profile.setCollege(rs.getString(10));
					profile.setEmployer(rs.getString(11));
					profile.setGraduate_school(rs.getString(12));
					
					profile.setProfilePicUrl(new UserImageService().getProfilePicName(profile.getUser_id()));
					arrayProfiles.add(profile);
				}

			}

		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}

		return arrayProfiles;
	}

	public int ignoreFriendSuggestion(int user_id, int friend_id) {
		// TODO Auto-generated method stub

		PreparedStatement preparedstmnt;
		int result = 0;
		try {
			preparedstmnt = connection
					.prepareStatement("DELETE from friend_suggestion WHERE (friend_id_fk = ? AND suggestedfriend_id_fk = ?) ");
			preparedstmnt.setInt(1, user_id);
			preparedstmnt.setInt(2, friend_id);

			result = preparedstmnt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		System.out.println("ignore Friend suggestion by user :" + user_id
				+ "  " + friend_id);
		return result;

	}

	public int addFriendSuggestion(int user_id, int friend_id) {
		// TODO Auto-generated method stub

		int result = sendFriendRequest(user_id, friend_id);
		System.out.println("result=" + result);
		result = ignoreFriendSuggestion(user_id, friend_id);
		System.out.println("resultIgnore=" + result);

		return 0;
	}

	public List<Profile> getCommonFriends(int user_id, int friend_id) {

		List<Profile> commonfriendList = null;

		try {

			String sql = "SELECT f1.friend_id_fk from (SELECT friend_id_fk FROM friendlist WHERE user_id_fk=?) AS f1 inner JOIN (SELECT friend_id_fk FROM friendlist WHERE user_id_fk=?) AS f2 ON f1.friend_id_fk= f2.friend_id_fk";
			System.out.println("inside service commo frnd");

			PreparedStatement preparedStatement = connection
					.prepareStatement(sql);
			// System.out.println("after pre stmt in get Friend list");
			preparedStatement.setInt(1, user_id);
			preparedStatement.setInt(2, friend_id);

			ResultSet rs = preparedStatement.executeQuery();

			commonfriendList = new ArrayList<Profile>();

			Services service = new Services();

			while (rs.next()) {

				Profile friendProfile = service.getUser(rs
						.getInt("friend_id_fk"));
				commonfriendList.add(friendProfile);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return commonfriendList;

	}

	public int countGetMutualFriendListForHover(int user_id, int friend_id) {

		int count = 0;
		try {

			String sql = "SELECT count(friend_id_fk) FROM friendlist where user_id_fk=? AND friend_id_fk !=? and friend_id_fk IN(SELECT friend_id_fk FROM friendlist where user_id_fk=? AND friend_id_fk !=?)";
			System.out
					.println("CountgetMutualFriendListForHover service commo frnd");

			PreparedStatement preparedStatement = connection
					.prepareStatement(sql);
			// System.out.println("after pre stmt in get Friend list");
			preparedStatement.setInt(1, user_id);
			preparedStatement.setInt(2, friend_id);
			preparedStatement.setInt(4, user_id);
			preparedStatement.setInt(3, friend_id);
			ResultSet rs = preparedStatement.executeQuery();
			if (rs.next()) {
				count = rs.getInt(1);

			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return count;

	}

	public List<String> getMutualFriendListForHover(int user_id, int friend_id) {

		List<String> listOfMutualFriends = null;
		try {

			String sql = "SELECT friend_id_fk FROM friendlist where user_id_fk=? AND friend_id_fk !=? and friend_id_fk IN(SELECT friend_id_fk FROM friendlist where user_id_fk=? AND friend_id_fk !=?)";
			System.out
					.println("CountgetMutualFriendListForHover service commo frnd");

			PreparedStatement preparedStatement = connection
					.prepareStatement(sql);
			// System.out.println("after pre stmt in get Friend list");
			preparedStatement.setInt(1, user_id);
			preparedStatement.setInt(2, friend_id);
			preparedStatement.setInt(4, user_id);
			preparedStatement.setInt(3, friend_id);
			ResultSet rs = preparedStatement.executeQuery();
			listOfMutualFriends = new ArrayList<>();
			UserService service = new UserService();
			while (rs.next()) {
				int id = rs.getInt(1);
				String fullName = service.getUserFullName(id);
				listOfMutualFriends.add(fullName);

			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return listOfMutualFriends;

	}

	public ArrayList<Profile> getFriendSuggestionsOnCity(String user_id,
			String city)

	{
		ArrayList<Integer> arrayList = new ArrayList<Integer>();
		String sql = "select f.suggestedfriend_id_fk from friend_suggestion as f WHERE friend_id_fk =? AND f.suggestedfriend_id_fk IN(select user_id_fk FROM profile WHERE mostLikedCity=?)";
		ArrayList<Profile> arrayProfiles = new ArrayList<Profile>();
		Profile profile = null;
		PreparedStatement preparedstmnt;
		try {
			preparedstmnt = connection.prepareStatement(sql);
			preparedstmnt.setInt(1, Integer.parseInt(user_id));
			preparedstmnt.setString(2, city);
			ResultSet rs = preparedstmnt.executeQuery();
			while (rs.next()) {
				arrayList.add(rs.getInt(1));
			}

			sql = "select * from profile WHERE user_id_fk = ? ";

			for (int i : arrayList) {

				preparedstmnt = connection.prepareStatement(sql);
				preparedstmnt.setInt(1, i);
				rs = preparedstmnt.executeQuery();
				if (rs.next()) {
					profile = new Profile();

					profile.setUser_id(rs.getInt(1));
					profile.setFirst_name(rs.getString(2));
					profile.setLast_name(rs.getString(3));
					profile.setDate_of_birth(rs.getDate(4));
					profile.setGender(rs.getString(5).toCharArray()[0]);
					profile.setContact_no(rs.getString(6));
					profile.setHome_town(rs.getString(7));
					profile.setHigh_school(rs.getString(8));
					profile.setCurrent_city(rs.getString(9));
					profile.setCollege(rs.getString(10));
					profile.setEmployer(rs.getString(11));
					profile.setGraduate_school(rs.getString(12));
					profile.setProfilePicUrl(new UserImageService()
							.getProfilePicName(profile.getUser_id()));
					arrayProfiles.add(profile);
				}

			}

		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}

		return arrayProfiles;

	}

	public List<Profile> findFriends(String name, String homeTown,
			String currentCity, String highSchool, String college, String employer,String graduateSchool, String id)
	{
		List<Profile> list = null;
		List<Profile> friends = null;
		String fullName[] = name.split("\\s+");
		String sql;
		System.out.println("GOING INSIDE TRY");
		try {
			if (fullName.length == 2)
				sql = "Select * from Profile where employer like '%"+employer+"' "
						+ "and graduateSchool like '%"+graduateSchool+"'"
						+ " and homeTown like '%" + homeTown
						+ "' and " + "highSchool like '%" + highSchool
						+ "' and currentCity like '%" + currentCity + "' and "
						+ "college like '%" + college
						+ "' and firstName like '%" + fullName[0] + "' and "
						+ "lastName like '%" + fullName[1] + "'";
			else
				sql = "Select * from Profile where employer like '%"+employer+"' "
						+ "and graduateSchool like '%"+graduateSchool+"'"
						+ " and homeTown like '%" + homeTown
						+ "' and " + "highSchool like '%" + highSchool
						+ "' and currentCity like '%" + currentCity + "' and "
						+ "college like '%" + college
						+ "' and firstName like '%" + name + "%'";

			System.out.println("=====================================================First wala  " + sql);
			PreparedStatement ps = connection.prepareStatement(sql);
			ResultSet rst = ps.executeQuery();
			System.out.println("out from first query");
			list = new ArrayList<Profile>();
			while (rst.next()) {
				Profile profile = new Profile();
				profile.setUser_id(rst.getInt(1));
				profile.setFirst_name(rst.getString(2));
				profile.setLast_name(rst.getString(3));
				profile.setDate_of_birth(rst.getDate(4));
				profile.setGender(rst.getString(5).toCharArray()[0]);
				profile.setContact_no(rst.getString(6));
				profile.setHome_town(rst.getString(7));
				profile.setHigh_school(rst.getString(8));
				profile.setCurrent_city(rst.getString(9));
				profile.setCollege(rst.getString(10));
				profile.setEmployer(rst.getString(11));
				profile.setGraduate_school(rst.getString(12));
				profile.setProfilePicUrl(new UserImageService().getProfilePicName(profile.getUser_id()));
				list.add(profile);
			}
			// System.out.println("before second wala");
			sql = "(select friend_id_fk from friendlist where user_id_fk ="
					+ id
					+ ") UNION "
					+ "(SELECT friend_id_fk from friendrequest WHERE user_id_fk ="
					+ id
					+ ") UNION "
					+ "(SELECT user_id_fk from friendrequest WHERE friend_id_fk ="
					+ id + ")";

			PreparedStatement preSt = connection.prepareStatement(sql);
			System.out.println(sql + " second wala00000000000000000000000000000000000000000000000000000000000000000000000");

			preSt = connection.prepareStatement(sql);
			rst = preSt.executeQuery();
			friends = new ArrayList<Profile>();
			while (rst.next()) {

				Profile profile = new UserService().getUser(rst.getInt(1));
				friends.add(profile);
			}
		}

		catch (SQLException e) {
			e.printStackTrace();
		}
		// System.out.println("Size of list is "+list.size());
		// for(Profile p: list)
		// {
		// System.out.println("Name in FINAL lists "+p.getFirst_name());
		// }
		// for(Profile p: friends)
		// {
		// System.out.println("Name in FRIEND lists "+p.getFirst_name());
		// }
		for (Profile p : friends) {
			for (Profile l : list) {
				if (p.getUser_id() == l.getUser_id()) {
					list.remove(l);
					break;
				}
			}

		}System.out.println(list.size());
		 for(Profile p:list)
		 {
		 
			 System.out.println(p.getFirst_name()+".........................");
		 }

		return list;
	}

}
