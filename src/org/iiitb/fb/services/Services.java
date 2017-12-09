package org.iiitb.fb.services;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.iiitb.fb.database.DataAccessObject;
import org.iiitb.fb.modals.Profile;

public class Services {
	public Profile getUser(int user_id) throws SQLException {
		Connection connection = DataAccessObject.getInstance().Connect();

		Profile userProfile = null;
		String sql = "select * from profile where user_id_fk = ?";
		PreparedStatement preparedstatement = connection.prepareStatement(sql);
		// System.out.println("Here");
		preparedstatement.setInt(1, user_id);

		// System.out.println(preparedstatement);

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
		return userProfile;
	}
}