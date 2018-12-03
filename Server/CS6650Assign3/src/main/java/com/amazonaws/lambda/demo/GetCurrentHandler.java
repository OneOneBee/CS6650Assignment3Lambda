package com.amazonaws.lambda.demo;

import java.beans.PropertyVetoException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedHashMap;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

public class GetCurrentHandler implements RequestHandler<Object, String> {
	
	public String handleRequest(Object event, Context context) {
		context.getLogger().log("Input: " + event);
		LinkedHashMap input = (LinkedHashMap) event;
		if (input.get("userid") == null) {
			return "400 Invalid Input";
		}
		int userid = Integer.parseInt((String)input.get("userid"));
		String result = "-1";
		
		try {
//			result = serverDao.selectSteps(userid);
			result = getCurrent(userid);
		} catch (Exception e) {
			e.printStackTrace();
			return "Get current failed!";	
		}
		
		return result;
	}
	
	private String getCurrent(int userId) throws IOException, PropertyVetoException {
		int res = 0;
		Connection conn = null;
		try {
			conn = ConnectionPool.getInstance().getConnection();
			Statement stmt = null;
			stmt = conn.createStatement();
			String getStep = "SELECT" +
	                "    SUM(step_count)" +
	                " FROM" +
	                "    users" +
	                " WHERE" +
	                "    user_id = '" + userId + "';";
			ResultSet rs = stmt.executeQuery(getStep);
            while(rs.next()){
                res += rs.getInt("SUM(step_count)");
            }
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return String.valueOf(res);
	}
}
