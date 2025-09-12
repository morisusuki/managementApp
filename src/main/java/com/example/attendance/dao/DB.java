package com.example.attendance.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DB {
	private static final String URL  = "jdbc:postgresql:managementdb";
	private static final String USER = "postgres";
	private static final String PASS = "postgres";

	static {
		try { Class.forName("org.postgresql.Driver"); }
		catch (ClassNotFoundException e) {
			throw new RuntimeException("PostgreSQL JDBCドライバが見つかりません（WEB-INF/libにjarを配置してください）", e);
		}
	}

	public static Connection get() throws SQLException {
		return DriverManager.getConnection(URL, USER, PASS);
	}
}