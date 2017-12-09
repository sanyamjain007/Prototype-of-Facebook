package org.iiitb.fb.services;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.iiitb.fb.database.DataAccessObject;
import org.iiitb.fb.modals.Comment;

public class CommentService {

	private Connection connection;

	public CommentService() {
		connection = DataAccessObject.getInstance().Connect();
	}

	public Comment addComment(int user_id, int post_id, String commentData) {

		PreparedStatement preparedstmnt;
		Comment comment = null;

		System.out.println("add comment funciton begin !");

		try {
			preparedstmnt = connection.prepareStatement(
					"INSERT INTO comment (user_id_fk,postId_fk,commentText,timeStamp) values(?,?,?,?)",
					Statement.RETURN_GENERATED_KEYS);
			preparedstmnt.setInt(1, user_id);
			preparedstmnt.setInt(2, post_id);
			preparedstmnt.setString(3, commentData);

			java.util.Date date = new java.util.Date();
			Timestamp timstamp = new Timestamp(date.getTime());

			preparedstmnt.setTimestamp(4, timstamp);

			preparedstmnt.executeUpdate();
			ResultSet rs = preparedstmnt.getGeneratedKeys();

			if (rs != null && rs.next()) {
				int comment_id = rs.getInt(1);

				UserService service = new UserService();

				String fullName = service.getUserFullName(user_id);
				comment = new Comment(comment_id, user_id, fullName, commentData, timstamp);
				comment.setProfilePicUrl(new UserImageService().getProfilePicName(user_id));
			}

			System.out.println("add comment :userid" + user_id + "  postid:" + post_id + " cooment: " + commentData);

		} catch (SQLException e) {

			e.printStackTrace();
		}
		return comment;
	}

	public List<Comment> getAllComment(int post_id) {

		PreparedStatement preparedstmnt;
		List<Comment> listOfComment = null;
		try {
			preparedstmnt = connection.prepareStatement(
					"SELECT commentId,user_id_fk,commentText,timeStamp FROM comment WHERE postId_fk = ?");
			preparedstmnt.setInt(1, post_id);

			ResultSet rs = preparedstmnt.executeQuery();
			System.out.println("post  :" + post_id);

			listOfComment = new ArrayList<>();

			while (rs.next()) {
				int commentId = rs.getInt(1);
				int user_id = rs.getInt(2);
				String commentData = rs.getString(3);
				Timestamp timeStamp = rs.getTimestamp(4);

				UserService service = new UserService();

				
				System.out.println(user_id);
				
				String fullName = service.getUserFullName(user_id);

				Comment comment = new Comment(commentId, user_id, fullName, commentData, timeStamp);
				comment.setProfilePicUrl(new UserImageService().getProfilePicName(user_id));
				listOfComment.add(comment);

			}

		} catch (SQLException e) {

			e.printStackTrace();

		}
		return listOfComment;
	}

}
