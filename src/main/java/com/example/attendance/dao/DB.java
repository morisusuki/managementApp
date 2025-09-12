package com.example.attendance.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DB {
	private static final String URL  = "jdbc:postgresql://localhost:5432/managementdb";
	private static final String USER = "postgres";
	private static final String PASS = "postgres";

	static {
		try { 
			Class.forName("org.postgresql.Driver"); 
			System.out.println("PostgreSQLと連携に成功");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			throw new RuntimeException("ドライバのロードに失敗しました", e);
		}
	}

	public static Connection getConnection() throws SQLException {
		return DriverManager.getConnection(URL, USER, PASS);
	}
}