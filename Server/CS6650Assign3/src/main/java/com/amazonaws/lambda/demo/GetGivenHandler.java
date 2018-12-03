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

public class GetGivenHandler implements RequestHandler<Object, String> {
	
    @Override
    public String handleRequest(Object event, Context context) {
		context.getLogger().log("Input: " + event);
		LinkedHashMap input = (LinkedHashMap) event;
		if (input.get("userid") == null || input.get("day") == null) {
			return "400 Invalid Input";
		}
		int userid = Integer.parseInt((String)input.get("userid"));
		int day = Integer.parseInt((String)input.get("day"));
		String result = "-1";
		
		try {
//			result = serverDao.getStepCountOfDay(userid, day);
			result = getSingle(userid, day);
		} catch (Exception e) {
			e.printStackTrace();
			return "Get current failed!";	
		}
		
		return result;
    }
    
    private String getSingle(int userId, int day) throws IOException, PropertyVetoException {
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
		            "    user_id = '" + userId +
		            "' And day_range = '" + day +"';";
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
