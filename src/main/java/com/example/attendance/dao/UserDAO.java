package com.example.attendance.dao;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.example.attendance.dto.User;
public class UserDAO {
	//ユーザー情報がまとめられたリスト(ユーザー名, User型の情報)
	private static final Map<String, User> users = new HashMap<>();
	


	//初期ユーザーの設定
	static {
		users.put("admin1", new User("admin1", hashPassword("adminpass"), "admin", true));
//		users.put("employee1", new User("employee1", hashPassword("password"), "employee", true));
	}
	
	private static Connection getConnection() throws SQLException {
		String url = "jdbc:postgresql:managementdb";
		String user = "postgres";
		String pass = "postgres";
		
		Connection con = DriverManager.getConnection(url, user, pass);
		con.setAutoCommit(false);
		
		return con;
	}
	
	
	//与えられたユーザー名からユーザー情報を取得
	public User findByUsername(String username)  throws SQLException{
		String sql = "SELECT * FROM users WHERE username=?";
		try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
			ps.setString(1, username);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					return map(rs);
				}
			} 
		}	
		catch (SQLException e) {
				e.printStackTrace();
		}
		return null;
	}
	
	//与えられたパスワードが一致するか比較
	public boolean verifyPassword(String username, String password)  throws SQLException{
		User user = findByUsername(username);
		if (user == null || !user.isEnabled()) {
			return false;
		}
		return user != null && user.isEnabled() 
			   && user.getPassword().equals(hashPassword(password));
		
	}
	
	//全ユーザー情報をリストで渡す
	public Collection<User> getAllUsers()  throws SQLException{
		
		String sql = "SELECT * FROM users";
		try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
			ResultSet rs = ps.executeQuery();
			Collection<User> list= new ArrayList<>();
			while (rs.next()) {
				list.add(map(rs));
			}
		} catch (SQLException e) {
				e.printStackTrace();
		}
		return null;
	}
	
	//新規ユーザーの追加
	public void addUser(User user)  throws SQLException {
//		users.put(user.getUsername(), user);
		
		String sql = "INSERT INTO users(username,password,role,enabled) VALUES(?,?,?,?)";
		try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
			ps.setString(1, user.getUsername());
			ps.setString(2, user.getPassword());
			ps.setString(3, user.getRole());
			ps.setBoolean(4, true);
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
	
	//既存ユーザー情報の更新
	public void updateUser(User user, User oldUser)  throws SQLException{
//		users.put(user.getUsername(), user);
		String sql = "UPDATE users SET username = ?, role = ?, enabled = ? WHERE username = ?";
		try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
			ps.setString(1, user.getUsername());
			ps.setString(2, user.getRole());
			ps.setBoolean(3, user.isEnabled());
			ps.setString(4, oldUser.getUsername());
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	//既存ユーザーの削除
	public void deleteUser(String username)  throws SQLException{
		users.remove(username);
	}
	
	//新しいパスワードを設定
	public void resetPassword(String username, String newPassword)  throws SQLException{
		User user = users.get(username);
		if (user != null) {
			users.put(username, new User(user.getUsername(), hashPassword(newPassword), user.getRole(),
					user.isEnabled()));
		}
	}
	
	//ユーザーアカウントの有効/無効を切り替える
	public void toggleUserEnabled(String username, boolean enabled)  throws SQLException{
		User user = users.get(username);
		if (user != null) {
			users.put(username, new User(user.getUsername(), user.getPassword(), user.getRole(),
					enabled));
		}
	}
	
	//パスワードをハッシュ化するやつ(よくわからん)
	public static String hashPassword(String password) {
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			byte[] hashedBytes = md.digest(password.getBytes());
			StringBuilder sb = new StringBuilder();
			for (byte b : hashedBytes) {
				sb.append(String.format("%02x", b));
			}
			return sb.toString();
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
	}
	
	// return用のやつ
	private User map(ResultSet rs) throws SQLException {
		    User user = new User(rs.getString("username"),
		    		rs.getString("password"),
		    		rs.getString("role"),
		    		rs.getBoolean("enabled"));
		    return user;
	}
	
}