package org.iiitb.fb.services;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.iiitb.fb.database.DataAccessObject;
import org.iiitb.fb.modals.Messages;

public class MessagesService {

	private Connection connection;

	public MessagesService() {
		connection = DataAccessObject.getInstance().Connect();
	}

	public List<Messages> getMessages(int user_id, int friend_id) {
		List<Messages> messageList = new ArrayList<Messages>();
		Messages mssg = null;
		PreparedStatement preparedstmnt;
		try {
			preparedstmnt = connection
					.prepareStatement("select * from messenger where sender_id=? and receiver_id=? or sender_id=? and receiver_id=?");
			preparedstmnt.setInt(1, user_id);
			preparedstmnt.setInt(2, friend_id);
			preparedstmnt.setInt(3, friend_id);
			preparedstmnt.setInt(4, user_id);
			ResultSet rs = preparedstmnt.executeQuery();

			while (rs.next()) {
				mssg = new Messages();
				mssg.setMessage_id(rs.getInt(1));
				mssg.setSender_id(rs.getInt(2));
				mssg.setReceiver_id(rs.getInt(3));
				mssg.setMessage(rs.getString(4));
				messageList.add(mssg);
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return messageList;

	}

	public Messages sendMessage(Messages mssg) {
		PreparedStatement preparedstmnt;
		try {
			preparedstmnt = connection
					.prepareStatement(
							"insert into messenger (sender_id, receiver_id, message) values (?,?,?)",
							Statement.RETURN_GENERATED_KEYS);
			preparedstmnt.setInt(1, mssg.getSender_id());
			preparedstmnt.setInt(2, mssg.getReceiver_id());
			preparedstmnt.setString(3, mssg.getMessage());
			preparedstmnt.executeUpdate();
			ResultSet rs = preparedstmnt.getGeneratedKeys();

			if (rs.next()) {
				mssg.setMessage_id(rs.getInt(1));
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return mssg;
	}
}
