package org.iiitb.fb.services;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.iiitb.fb.database.DataAccessObject;
import org.iiitb.fb.modals.Profile;

public class LikeService {

	private Connection connection;

	public LikeService() {
		connection = DataAccessObject.getInstance().Connect();
	}

	public Profile addLike(int user_id, int post_id) {

		PreparedStatement preparedstmnt;
		Profile profile = null;

		System.out.println("add like funciton begin !");

		try {
			preparedstmnt = connection.prepareStatement("INSERT INTO `like`(`user_id_fk`, `postId_fk`) VALUES(?,?)");
			preparedstmnt.setInt(1, user_id);
			preparedstmnt.setInt(2, post_id);

			int result = preparedstmnt.executeUpdate();

			if (result > 0) {
				UserService service = new UserService();

				String fullName = service.getUserFullName(user_id);
				profile = new Profile(user_id, fullName, null, null, null, null,null,null, null,null,null,null);

			}
			System.out.println("add like function :" + profile);

		} catch (SQLException e) {

			e.printStackTrace();
		}
		return profile;
	}

	public List<Profile> getPostLikes(int post_id) {

		PreparedStatement preparedstmnt;
		List<Profile> listOfLike = null;

		System.out.println("get like funciton begin !");

		try {
			preparedstmnt = connection.prepareStatement("SELECT `user_id_fk` FROM `like` WHERE `postId_fk`=?");

			preparedstmnt.setInt(1, post_id);

			ResultSet rs = preparedstmnt.executeQuery();
			listOfLike = new ArrayList<>();

			UserService service = new UserService();

			while (rs.next()) {

				int user_id = rs.getInt(1);
				String fullName = service.getUserFullName(user_id);
				Profile profile = new Profile(user_id, fullName,null,null, null,null, null, null,null,null,null,null);

				listOfLike.add(profile);

			}

		} catch (SQLException e) {

			e.printStackTrace();
		}

		return listOfLike;
	}

	public int updateLikeStatus(int user_id, int post_id) {

		PreparedStatement preparedstmnt = null;
		int result = 0;

		System.out.println("update like funciton begin !");

		try {
			preparedstmnt = connection.prepareStatement("DELETE FROM `like` WHERE user_id_fk =? AND postId_fk=?");

			preparedstmnt.setInt(1, user_id);
			preparedstmnt.setInt(2, post_id);

			result = preparedstmnt.executeUpdate();

		} catch (SQLException e) {

			e.printStackTrace();
		}
		return result;
	}
}
