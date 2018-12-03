package com.amazonaws.lambda.demo;

import org.apache.commons.dbcp2.BasicDataSource;

import java.sql.SQLException;


public class ConnectionPool {

    private static BasicDataSource basicDataSource;

    private ConnectionPool(){}

    public static BasicDataSource getInstance() {
        if (basicDataSource != null) {
            return basicDataSource;
        }
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        basicDataSource = new BasicDataSource();
        basicDataSource.setUrl("jdbc:mysql://cs6650lambda.cqiiwgdmulxt.us-west-2.rds.amazonaws.com:3306/mydb?useSSL=false");
        basicDataSource.setUsername("admin");
        basicDataSource.setPassword("adminadmin");
        basicDataSource.setMinIdle(1);
        basicDataSource.setMaxIdle(300);
        basicDataSource.setMaxTotal(300);
        basicDataSource.setMaxOpenPreparedStatements(100000000);
        return basicDataSource;
    }

    public static void close() {
        try {
            basicDataSource.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

