package com.amazonaws.lambda.demo;

import java.beans.PropertyVetoException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedHashMap;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

public class PostHandler implements RequestHandler<Object, String> {
	
	public String handleRequest(Object event, Context context) {
		
		context.getLogger().log("Input: " + event);
		LinkedHashMap input = (LinkedHashMap) event;
		if (input.get("userid") == null || input.get("day") == null || 
				input.get("timeinterval") == null || input.get("stepcount") == null) {
			return "400 Invalid Input";
		}

		int userid = Integer.parseInt((String)input.get("userid"));
		int day = Integer.parseInt((String)input.get("day"));
		int timeinterval = Integer.parseInt((String)input.get("timeinterval"));
		int stepcount = Integer.parseInt((String)input.get("stepcount"));
		
		try {
			insertData(userid, day, timeinterval, stepcount);
		} catch (Exception e) {
			e.printStackTrace();
			return "Post failed!";
		} 
		
		return "Post successed!" + userid + "/" + day + "/" + timeinterval + "/" + stepcount;
		
	}
	
	private void insertData(int userId, int day, int timeInterval, int stepCount) throws IOException, PropertyVetoException {
		Connection conn = null;
		try {
			conn = ConnectionPool.getInstance().getConnection();
			Statement stmt = null;
			stmt = conn.createStatement();
			String insertUser = "INSERT IGNORE INTO users(user_id, day_range, time_interval, step_count)" 
		               + "VALUES (" + userId + "," + day + ","  + timeInterval 
		               + "," + stepCount + ")";
			stmt.executeUpdate(insertUser);
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
