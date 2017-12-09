package org.iiitb.fb.services;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.iiitb.fb.database.DataAccessObject;
import org.iiitb.fb.modals.Login;
import org.iiitb.fb.modals.Profile;

import com.sun.imageio.plugins.common.InputStreamAdapter;

public class UserService {

	private Connection connection;

	public UserService() {
		connection = DataAccessObject.getInstance().Connect();
	}

	public Profile login(String emailID, String password) {
		Profile profile = null;
		PreparedStatement preparedstmnt;
		try {
			preparedstmnt = connection.prepareStatement("select user_id from login where userName = ? and password = ?");
			preparedstmnt.setString(1, emailID);
			preparedstmnt.setString(2, password);
			ResultSet rs = preparedstmnt.executeQuery();
			if (rs.next()) 
				profile = getUserProfile(rs.getInt("user_id"));

		} 
		catch (SQLException e) {
			e.printStackTrace();
		}

		return profile;
	}

	public ArrayList<Login> getAllUsers()
	{
	Login login;
	ArrayList<Login> list = new ArrayList<Login>();
	try{
	String sql = "Select * from login";
	PreparedStatement ps = connection.prepareStatement(sql);
	ResultSet rs = ps.executeQuery();
	while(rs.next())
	{
	login =  new Login();
	login.setUser_id(rs.getInt(1));
	login.setEmailId(rs.getString(2));
	login.setPassword(rs.getString(3));
	list.add(login);
	}
	System.out.println(list.size());

	for(Login l:list)
	{
	System.out.println(l.getEmailId()+"  "+l.getPassword());
	}

	}
	catch(SQLException e)
	{
	e.printStackTrace();
	}
	return list;
	}
	
	public List<Profile> search(String searchData, String userId) {
		List<Profile> searchProfileList = new ArrayList<Profile>();
		String[] nameOfString = searchData.split(" ");
		PreparedStatement pst = null;
		try {
			if (nameOfString.length > 1) {
				pst = connection.prepareStatement("select * from profile where (firstName LIKE ? and lastName LIKE ?) and user_id_fk<>?");
				pst.setString(1, "%" + nameOfString[0] + "%");
				pst.setString(2, "%" + nameOfString[1] + "%");
				pst.setInt(3, Integer.parseInt(userId));
			} else {
				pst = connection.prepareStatement("select * from profile where (firstName LIKE ? or lastName LIKE ?) and user_id_fk<>?");
				pst.setString(1, "%" + searchData + "%");
				pst.setString(2, "%" + searchData + "%");
				pst.setInt(3, Integer.parseInt(userId));
			}

			ResultSet rst = pst.executeQuery();

			while (rst.next()) {
				Profile searchProfile = new Profile();
				searchProfile.setUser_id(rst.getInt(1));
				searchProfile.setFirst_name(rst.getString(2));
				searchProfile.setLast_name(rst.getString(3));
				searchProfile.setDate_of_birth(rst.getDate(4));
				searchProfile.setGender(rst.getString(5).toCharArray()[0]);
				searchProfile.setContact_no(rst.getString(6));
				searchProfile.setProfilePicUrl(new UserImageService().getProfilePicName(searchProfile.getUser_id()));
				searchProfile.setHome_town(rst.getString(7));
				searchProfile.setHigh_school(rst.getString(8));
				searchProfile.setCurrent_city(rst.getString(9));
				searchProfile.setCollege(rst.getString(10));
				searchProfile.setEmployer(rst.getString(11));
				searchProfile.setGraduate_school(rst.getString(12));
				searchProfileList.add(searchProfile);

			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return searchProfileList;

	}

	public List<Profile> getFriendList(int user_id) {
		ResultSet rs = null;
		List<Profile> friendList = null;

		try {

			System.out.println(connection);
			String sql = "select friend_id_fk from friendlist where user_id_fk = ?";
			PreparedStatement stmt = connection.prepareStatement(sql);
			stmt.setInt(1, user_id);
			rs = stmt.executeQuery();
			friendList = new ArrayList<Profile>();
			while (rs.next()) {

				Profile friendProfile = getUser(rs.getInt("friend_id_fk"));
				friendList.add(friendProfile);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return friendList;
	}

	public Profile getUser(int user_id) throws SQLException {
		Profile userProfile = null;
		String sql = "select * from profile where user_id_fk = ?";
		PreparedStatement preparedstatement = connection.prepareStatement(sql);
		preparedstatement.setInt(1, user_id);
		System.out.println(preparedstatement);
		ResultSet rs = preparedstatement.executeQuery();

		if (rs.next()) {
			userProfile = new Profile();
			userProfile.setUser_id(rs.getInt(1));
			userProfile.setFirst_name(rs.getString(2));
			userProfile.setLast_name(rs.getString(3));
			userProfile.setDate_of_birth(rs.getDate(4));
			userProfile.setGender(rs.getString(5).toCharArray()[0]);
			userProfile.setContact_no(rs.getString(6));
			userProfile.setHome_town(rs.getString(7));
			userProfile.setHigh_school(rs.getString(8));
			userProfile.setCurrent_city(rs.getString(9));
			userProfile.setCollege(rs.getString(10));
			userProfile.setEmployer(rs.getString(11));
			userProfile.setGraduate_school(rs.getString(12));
			userProfile.setProfilePicUrl(new UserImageService().getProfilePicName(userProfile.getUser_id()));
			System.out.println(userProfile.getHome_town());
		}
		return userProfile;
	}

	public int validateEmailId(String emailID) {
		try {
			System.out.println(connection);
			PreparedStatement stmt = connection
					.prepareStatement("Select user_id from login where userName LIKE ?");

			System.out.println(stmt);
			stmt.setString(1, emailID);

			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				return 1;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}

	public Profile setUserInfo(String emailID, String password,
			String firstName, String lastName, String date_of_birth,
			String gender, String contact_info, String hometown) {
		
			
		Profile profile = null;
		if (validateEmailId(emailID) == 0) {

			try {
				PreparedStatement preparedstmnt = connection
						.prepareStatement("insert into login (userName, password) values(?,?)");
				preparedstmnt.setString(1, emailID);
				preparedstmnt.setString(2, password);

				System.out.println(emailID);

				preparedstmnt.executeUpdate();
				System.out.println("login-record successful");
				preparedstmnt = connection
						.prepareStatement("select user_id from login where userName=?");
				preparedstmnt.setString(1, emailID);

				ResultSet rs = preparedstmnt.executeQuery();

				System.out.println(emailID);
				int uid = Integer.MIN_VALUE;
				if (rs.next())
					uid = rs.getInt("user_id");

				System.out.println(uid);
				preparedstmnt = connection
						.prepareStatement("INSERT INTO `profile`(`user_id_fk`, `firstName`, `lastName`, `dob`, `gender`, `contact`, `homeTown`) VALUES (?,?,?,?,?,?,?)");
				preparedstmnt.setInt(1, uid);
				preparedstmnt.setString(2, firstName);
				preparedstmnt.setString(3, lastName);
				preparedstmnt.setDate(4, Date.valueOf(date_of_birth));
				System.out.println(emailID);
				preparedstmnt.setString(5, gender);
				preparedstmnt.setString(6, contact_info);
				preparedstmnt.setString(7, hometown);
				System.out.println("profile successful"+ preparedstmnt.executeUpdate());

				profile = new Profile(uid, firstName, lastName,Date.valueOf(date_of_birth), gender.charAt(0),contact_info, hometown,"Excellence","Bangalore","JEC","CISCO","RDVV");
				if (profile != null) {
					System.out.println(profile.getUser_id());
					new File(
							PathSetup.imagePath+"users/"
									+ profile.getUser_id() + "/images")
							.mkdirs();
					InputStream fileInputStream = null;
					OutputStream outputStream = null;
					String path = PathSetup.imagePath+"users/"
							+ profile.getUser_id() + "/";

					try {
						fileInputStream = new FileInputStream(
								PathSetup.imagePath+"defaultProfilePic.jpg");

						outputStream = new FileOutputStream(new File(path
								+ "profilePic.jpg"));
						int read = 0;
						byte[] bytes = new byte[1024];
						while ((read = fileInputStream.read(bytes)) != -1) {
							outputStream.write(bytes, 0, read);

						}
						profile.setProfilePicUrl("profilePic.jpg");
						outputStream.close();
						fileInputStream.close();
					} catch (Exception e) {
						e.printStackTrace();
					}

				}

			} catch (Exception e) {
				System.out.println("Exception raised!!" + e);
			}
		} else {
			System.out.println("userName " + emailID + " already exists!");
		}
		return profile;
	}

	public Profile getUserProfile(int user_id) {

		Profile userProfile = null;
		String sql = "select * from profile where user_id_fk = ?";
		PreparedStatement preparedstatement;
		try {
			preparedstatement = connection.prepareStatement(sql);
			preparedstatement.setInt(1, user_id);
			ResultSet rs = preparedstatement.executeQuery();

			if (rs.next()) {
				userProfile = new Profile();
				userProfile.setUser_id(rs.getInt(1));
				userProfile.setFirst_name(rs.getString(2));
				userProfile.setLast_name(rs.getString(3));
				userProfile.setDate_of_birth(rs.getDate(4));
				userProfile.setGender(rs.getString(5).toCharArray()[0]);
				userProfile.setContact_no(rs.getString(6));
				userProfile.setHome_town(rs.getString(7));
				userProfile.setHigh_school(rs.getString(8));
				userProfile.setCurrent_city(rs.getString(9));
				userProfile.setCollege(rs.getString(10));
				userProfile.setEmployer(rs.getString(11));
				userProfile.setGraduate_school(rs.getString(12));
				userProfile.setProfilePicUrl(new UserImageService().getProfilePicName(userProfile.getUser_id()));
			}
		} catch (SQLException e) {

			e.printStackTrace();
		}
		System.out.println("GetUserProfile !");
		return userProfile;
	}

	public String getUserFullName(int user_id) {

		String firstName, lastName;
		String sql = "SELECT firstName,lastName FROM profile WHERE user_id_fk = ?";
		PreparedStatement preparedstatement;
		try {
			preparedstatement = connection.prepareStatement(sql);
			preparedstatement.setInt(1, user_id);
			ResultSet rs = preparedstatement.executeQuery();

			if (rs.next()) {

				firstName = rs.getString(1);
				lastName = rs.getString(2);
				System.out.println("getfullName fullName:" + firstName + " "
						+ lastName);
				return firstName + " " + lastName;

			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return null;
	}

	public Profile updateDetails(String variableName, int id,String changedValue) {
		int success = 0;
		String query;
		try {
			if(variableName.equals("password"))			
				query = "update login set password = ? where user_id = ?";
			else
				query = "update profile set " + variableName+ " = ? WHERE user_id_fk = ?";
			PreparedStatement preparedstmnt = connection.prepareStatement(query);
			preparedstmnt.setString(1, changedValue);
			preparedstmnt.setInt(2, id);
			success = preparedstmnt.executeUpdate();
			if (success == 1) 	
				return getUserProfile(id);

		} 
		catch (Exception e) {
			System.out.println("Exception raised!!" + e);
		}

		return null;
	}

	
	public List<Profile> getFilterFriendList(int user_id,String filter) {
		ResultSet rs = null;
		List<Profile> friendList = null;
		if(filter.equalsIgnoreCase("All"))
			return getFriendList(user_id);
		
		try {

			System.out.println(connection);
			String sql = "SELECT f.friend_id_fk from friendlist as f,profile as p where f.user_id_fk=? and p.book=? and f.friend_id_fk=p.user_id_fk";
			PreparedStatement stmt = connection.prepareStatement(sql);
			// System.out.println("after pre stmt in get Friend list");
			stmt.setInt(1, user_id);
			stmt.setString(2, filter);
			rs = stmt.executeQuery();
			friendList = new ArrayList<Profile>();
			while (rs.next()) {

				Profile friendProfile = getUser(rs.getInt(1));
				friendList.add(friendProfile);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return friendList;
	}

	
}
