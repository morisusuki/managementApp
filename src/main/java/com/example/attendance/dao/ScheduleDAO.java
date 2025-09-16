package com.example.attendance.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

//import com.example.attendance.dto.Attendance;
import com.example.attendance.dto.Schedule;

public class ScheduleDAO {
	private static final List<Schedule> scheduleList = new CopyOnWriteArrayList<>();
	
	//　テスト
	static {
//		Random ran = new Random();
//		for (int i = 0; i < 5; i++) {
//			int y = ran.nextInt(6);
//			int m = ran.nextInt(11) + 1;
//			int d = ran.nextInt(29) + 1;
//			Schedule s1 = new Schedule("mori", LocalDateTime.of(2020 + y, m, d, 17, 00, 00));
//			
//			scheduleList.add(s1);
//		}
		Schedule s1 = new Schedule("mori", LocalDateTime.of(2025, 9, 10, 17, 00, 00));
		scheduleList.add(s1);
		Schedule s2 = new Schedule("mori", LocalDateTime.of(2025, 9, 11, 17, 00, 00));
		scheduleList.add(s2);
	}
//	static {
//		users.put("admin1", new User("admin1", hashPassword("adminpass"), "admin", true));
////		users.put("employee1", new User("employee1", hashPassword("password"), "employee", true));
//	}
	
	//　スケジュール追加
	public void setSchedule(String userId, LocalDateTime date) {
//		Schedule schedule = new Schedule(userId, date);
//		scheduleList.add(schedule);
		
		String sql = "INSERT INTO schedule(userid, scheduledate) VALUES(?,?)";
		try (Connection con = DB.getConnection(); 
				PreparedStatement ps = con.prepareStatement(sql)){
			ps.setString(1, userId);
			ps.setTimestamp(2, Timestamp.valueOf(date));
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
	//　スケジュール変更(ユーザーの変更はできない)
	public boolean changeSchedule(String userId, LocalDateTime oldDate, LocalDateTime newDate) {
		
		String sql = "UPDATE schedule SET scheduledate = ? WHERE userid = ? AND scheduledate = ?";
		try (Connection con = DB.getConnection(); 
				PreparedStatement ps = con.prepareStatement(sql)){
			ps.setTimestamp(1, Timestamp.valueOf(newDate));
			ps.setString(2, userId);
			ps.setTimestamp(3, Timestamp.valueOf(oldDate));
			ps.executeUpdate();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
	//　スケジュール削除
	public boolean deleteSchedule(String userId, LocalDateTime date) {
//		return scheduleList.removeIf(sc -> 
//				sc.getUserId().equals(userId) && 
//				sc.getScheduleDate().equals(date));
		
		String sql = "DELETE FROM schedule WHERE userid = ? AND scheduledate = ?";
		try (Connection con = DB.getConnection(); 
			PreparedStatement ps = con.prepareStatement(sql)){
			ps.setString(1, userId);
			ps.setTimestamp(2, Timestamp.valueOf(date));
			ps.executeUpdate();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
	//　月単位で日程渡し
	public List<Schedule> monthSchedule(String userId, String date) {
//		return new ArrayList<>(scheduleList);
		String sql = "select * from schedule where userid = ? AND to_char(scheduledate,'yyyy-mm-dd') like ?";
		try (Connection con = DB.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
			ps.setString(1, userId);
			ps.setString(2, date);
			ResultSet rs = ps.executeQuery();
			List<Schedule> list= new ArrayList<>();
			while (rs.next()) {
//				System.out.println("test");
				list.add(map(rs));
			}
			return list;
		} catch (SQLException e) {
				e.printStackTrace();
		}
		return null;
	}
	
	// return用のやつ
	private Schedule map(ResultSet rs) throws SQLException {

		Schedule sc = new Schedule(rs.getString("userid"), 
				rs.getTimestamp("ScheduleDate").toLocalDateTime());	
//		System.out.println(rs.getString("userid") + rs.getTimestamp("ScheduleDate").toLocalDateTime());
		return sc;

	}

}