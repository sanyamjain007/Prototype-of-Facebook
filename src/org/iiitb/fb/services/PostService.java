
package org.iiitb.fb.services;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.iiitb.fb.database.DataAccessObject;
import org.iiitb.fb.modals.Post;
import org.iiitb.fb.modals.Post_data;
import org.iiitb.fb.modals.Profile;

public class PostService {

	private Connection connection;

	public PostService() {
		connection = DataAccessObject.getInstance().Connect();
	}

	public Post createPost(InputStream fileInputStream, String fileName, String postData, int user_id) {

		if (postData.length() < 1) {
			postData = null;
		}

		System.out.println(postData);

		if (fileName != null) {

			OutputStream outputStream = null;

			fileName = "" + Calendar.getInstance().getTimeInMillis() + fileName;
			String path = PathSetup.imagePath+"users/"+user_id+"/images/";
			try {
				outputStream = new FileOutputStream(new File(path + fileName));

				int read = 0;
				byte[] bytes = new byte[1024];
				while ((read = fileInputStream.read(bytes)) != -1) {
					{
						outputStream.write(bytes, 0, read);
					}

				}
				outputStream.close();
				System.out.println("POst image upload !");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		Post post = null;
		int post_data_id = 0, post_id = 0;
		try {

			System.out.println(postData);

			PreparedStatement preparedstmnt = connection.prepareStatement(
					"INSERT INTO postdata(post_data_message, imageUrl,usedByNumberofusers) values(?,?,?)",
					Statement.RETURN_GENERATED_KEYS);
			preparedstmnt.setString(1, postData);
			preparedstmnt.setString(2, fileName);
			preparedstmnt.setInt(3, 1);

			preparedstmnt.executeUpdate();
			ResultSet rs = preparedstmnt.getGeneratedKeys();

			System.out.println("post data inserted");

			if (rs != null && rs.next()) {
				post_data_id = rs.getInt(1);

				preparedstmnt = connection.prepareStatement(
						"INSERT INTO post(user_id_fk, post_data_Id_fk,timeStamp) values(?,?,?)",
						Statement.RETURN_GENERATED_KEYS);
				preparedstmnt.setInt(1, user_id);
				preparedstmnt.setInt(2, post_data_id);

				java.util.Date date = new java.util.Date();
				Timestamp timstamp = new Timestamp(date.getTime());

				preparedstmnt.setTimestamp(3, timstamp);

				preparedstmnt.executeUpdate();
				rs = preparedstmnt.getGeneratedKeys();
				if (rs != null && rs.next()) {
					post_id = rs.getInt(1);

				}

				String fullName = new UserService().getUserFullName(user_id);

				Post_data post_data = new Post_data(postData, fileName);

				post = new Post(fullName, post_id,user_id, timstamp, post_data, null);

				System.out.println("post created" + post.getPost_date_time());
			}

		} catch (SQLException e) {

			e.printStackTrace();
		}

		return post;

	}

	public List<Post> getAllPost(int user_id) {

		List<Post> listOfPost = null;
		
		System.out.println(user_id);
		PreparedStatement preparedstmnt;
		try {
			preparedstmnt = connection.prepareStatement(
					"SELECT p.postId,p.timeStamp,pd.post_data_message,pd.imageUrl,p.user_id_fk FROM post as p INNER JOIN postdata as pd WHERE (p.post_data_Id_fk = pd.post_data_Id) AND user_id_fk = ? ORDER BY p.timeStamp DESC");

			preparedstmnt.setInt(1, user_id);

			String fullName = new UserService().getUserFullName(user_id);

			ResultSet rs = preparedstmnt.executeQuery();
			listOfPost = new ArrayList<>();

			while (rs.next()) {

				int post_id = rs.getInt(1);
				Timestamp timeStamp = rs.getTimestamp(2);
				String postMessage = rs.getString(3);
				String imageUrl = rs.getString(4);
				int user_id1=rs.getInt(5);
				System.out.println("post id :"+post_id);
				
				List<Profile>lisOfLike = new LikeService().getPostLikes(post_id);
				
				Post post = new Post(fullName, post_id,user_id1, timeStamp, new Post_data(postMessage, imageUrl), lisOfLike);
				post.setProfilePicUrl(new UserImageService().getProfilePicName(user_id1));
				System.out.println(post.getProfilePicUrl()+" .............................hello");
				listOfPost.add(post);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println("Get All user Post :" + listOfPost);
		return listOfPost;
	}

	public List<Post> getAllUserPost(int user_id) {

		List<Post> listOfPost = null;

		PreparedStatement preparedstmnt;
		try {

			preparedstmnt = connection.prepareStatement(
					"SELECT p.user_id_fk,p.postId,p.timeStamp,pd.post_data_message,pd.imageUrl FROM post as p INNER JOIN postdata as pd WHERE (p.post_data_Id_fk = pd.post_data_Id) AND (p.user_id_fk IN(SELECT friend_id_fk FROM friendlist where user_id_fk=?) OR p.user_id_fk=?) ORDER BY p.timeStamp DESC");
			preparedstmnt.setInt(1, user_id);
			preparedstmnt.setInt(2, user_id);

			ResultSet rs = preparedstmnt.executeQuery();
			listOfPost = new ArrayList<>();

			UserService service = new UserService();
			List<Profile>lisOfLike=null;
			while (rs.next()) {

				int user_and_friend_id = rs.getInt(1);
				int post_id = rs.getInt(2);
				Timestamp timeStamp = rs.getTimestamp(3);
				String postMessage = rs.getString(4);
				String imageUrl = rs.getString(5);

				String fullName = service.getUserFullName(user_and_friend_id);
				lisOfLike = new LikeService().getPostLikes(post_id);

				Post post = new Post(fullName, post_id, user_and_friend_id, timeStamp, new Post_data(postMessage, imageUrl), lisOfLike);
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