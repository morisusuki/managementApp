package com.example.attendance.dao;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.example.attendance.dto.User;

public class UserDAO {
	//ユーザー情報がまとめられたリスト(ユーザー名, User型の情報)
	private static final Map<String, User> users = new HashMap<>();
	
	//初期ユーザーの設定
	static {
		users.put("oomori", new User("oomori", hashPassword("morimori"), "employee", true));
		users.put("admin1", new User("admin1", hashPassword("adminpass"), "admin", true));
	}
	
	//与えられたユーザー名からユーザー情報を取得
	public User findByUsername(String username) {
		return users.get(username);
	}
	
	//与えられたパスワードが一致するか比較
	public boolean verifyPassword(String username, String password) {
		User user = findByUsername(username);
		return user!= null && user.isEnabled() &&
			   user.getPassword().equals(hashPassword(password));
	}
	
	//全ユーザー情報をリストで渡す
	public Collection<User> getAllUsers() {
		return users.values();
	}
	
	//新規ユーザーの追加
	public void addUser(User user) {
		users.put(user.getUsername(), user);
	}
	
	//既存ユーザー情報の更新
	public void updateUser(User user) {
		users.put(user.getUsername(), user);
	}
	
	//既存ユーザーの削除
	public void deleteUser(String username) {
		users.remove(username);
	}
	
	//新しいパスワードを設定(旧パスワードとの比較はしない)
	public void resetPassword(String username, String newPassword) {
		User user = users.get(username);
		if (user != null) {
			users.put(username,  new User(
				user.getUsername(), hashPassword(newPassword),
				user.getRole(), user.isEnabled()));
		}
	}
	//ユーザーアカウントの有効/無効を切り替える
	public void toggleUserEnabled(String username, boolean enabled) {
		User user = users.get(username);
		if (user!= null) {
			users.put(username, new User(user.getUsername(),
					  user.getPassword(), user.getRole(), enabled));
		}
	}
	
	//パスワードをハッシュ化するやつ(よくわからん)
	public static String hashPassword(String password) {
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			byte[] hashedBytes = md.digest(password.getBytes());
			StringBuffer sb = new StringBuffer();
			for (byte b : hashedBytes) {
				sb.append(String.format("%02x", b));
			}
			return sb.toString();
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
	}

}
