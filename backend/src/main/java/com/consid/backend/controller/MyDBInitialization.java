package com.consid.backend.controller;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class MyDBInitialization {
	private String connection_url;
	private final String username;
	private final String password;
	private Connection connection;
	private final String dbName;

	public MyDBInitialization(String username, String password, String dbName, String host, String port) {
		this.username = username;
		this.password = password;
		this.dbName = dbName;
		connection_url = "jdbc:mysql://" + host + ":" + port + "?useSSL=false";
	}

	public void closeConnection() {
		try {
			if (connection != null && !connection.isClosed())
				connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void createDB() {
		String sql = "CREATE DATABASE IF NOT EXISTS " + dbName;

		try (Connection conn = DriverManager.getConnection(connection_url, username, password);
				PreparedStatement stmt = conn.prepareStatement(sql)) {
			this.connection = conn;
			stmt.execute();
			System.out.println("The database " + dbName + " has been created succeefuly!!");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
