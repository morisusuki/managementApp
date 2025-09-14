package com.example.attendance.dao;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.attendance.dto.Attendance;
public class AttendanceDAO {
	
	//	従業員の内容を呼び出すattendanceがいっぱい入ってるリスト
//	private static final List<Attendance> attendanceRecords = new CopyOnWriteArrayList<>();
	
	public void checkIn(String userId) throws SQLException {		
		String sql = "INSERT INTO attendance(userid, checkintime) VALUES(?,?)";
		try (Connection con = DB.getConnection(); 
				PreparedStatement ps = con.prepareStatement(sql)){
			ps.setString(1, userId);
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
			String nowTimeStr =  LocalDateTime.now().format(formatter);
			LocalDateTime nowTime = LocalDateTime.parse(nowTimeStr, formatter);
			ps.setTimestamp(2, Timestamp.valueOf(nowTime));
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void checkOut(String userId) throws SQLException {
		//名前で検索,最新のやつ持ってくる,checkOutTime==null確認する
		String sql = "UPDATE attendance SET checkouttime=? WHERE checkintime = (SELECT checkintime FROM attendance WHERE userid = ? AND checkouttime IS NULL ORDER BY checkintime DESC LIMIT 1)";
		try (Connection con = DB.getConnection(); 
				PreparedStatement ps = con.prepareStatement(sql)){
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
			String nowTimeStr =  LocalDateTime.now().format(formatter);
			LocalDateTime nowTime = LocalDateTime.parse(nowTimeStr, formatter);
			ps.setTimestamp(1, Timestamp.valueOf(nowTime));
			ps.setString(2, userId);
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	//指定ユーザーの勤怠履歴をリストで取得
	public List<Attendance> findByUserId(String userId) throws SQLException {
		String sql = "SELECT * FROM attendance WHERE userid=? ORDER BY checkintime DESC";
		try (Connection con = DB.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
			ps.setString(1, userId);
			ResultSet rs = ps.executeQuery();
			List<Attendance> list= new ArrayList<>();
			while (rs.next()) {
				list.add(map(rs));
			}
			return list;
		} catch (SQLException e) {
				e.printStackTrace();
		}
		return null;
	}
	
	public List<Attendance> findAll() throws SQLException {
//			return new ArrayList<>(attendanceRecords);
		String sql = "SELECT * FROM attendance WHERE ORDER BY checkintime DESC";
		try (Connection con = DB.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
			ResultSet rs = ps.executeQuery();
			List<Attendance> list= new ArrayList<>();
			while (rs.next()) {
				list.add(map(rs));
			}
			return list;
		} catch (SQLException e) {
				e.printStackTrace();
		}
		return null;
	}
	
	// ----------　ORDER BYの条件追加もできたらやりたい　---------- //
	//条件検索(ユーザーID、出勤時間、退勤時間)
	public List<Attendance> findFilteredRecords(String userId, LocalDate startDate, 
												LocalDate endDate) throws SQLException {
		boolean sql_u = false;
		boolean sql_s = false;
		boolean sql_e = false;
		String sql = "SELECT * FROM attendance WHERE 1=1 ";
		if (userId != null) { sql += "AND userid=? "; sql_u = true; }
		if (startDate != null) { sql += "AND checkintime>=? "; sql_s = true; }
		if (endDate != null) { sql += "AND checkintime<=?"; sql_e = true; }
		
		try (Connection con = DB.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
			int i = 1;
			if (sql_u) {
				ps.setString(i, userId);
				i += 1;
			}
			if (sql_s) {
				ps.setTimestamp(i, Timestamp.valueOf(startDate.atStartOfDay()));
				i += 1;
			}
			if (sql_e) {
				ps.setTimestamp(i, Timestamp.valueOf(endDate.atStartOfDay()));
			}
			ResultSet rs = ps.executeQuery();
			List<Attendance> list= new ArrayList<>();
			while (rs.next()) {
				list.add(map(rs));
			}
			return list;
		} catch (SQLException e) {
				e.printStackTrace();
		}
		return null;
	}
	
	// オリジナル　ユーザー一人当たりの総労働時間 X 全員分
	public Map<String, Long> totalHoursByUser() {
		String sql = "SELECT userid, sum(extract(epoch from(checkouttime - checkintime))/3600) as count FROM attendance GROUP by userid";
		
		try (Connection con = DB.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
			ResultSet rs = ps.executeQuery();
			Map<String,Long> list= new HashMap<>();
			while (rs.next()) {
				list.put(rs.getString("userid"), rs.getLong("count"));
			}
			return list;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	//月単位の総労働時間の計算
	public Map<String, Long> getMonthlyWorkingHours(String userId) 
											throws SQLException {

		String sql = "SELECT to_char(checkintime, 'YYYY/MM') as month ,sum(extract(epoch from(checkouttime - checkintime))/3600) as count FROM attendance ";
		if (userId != null) { sql += "AND userid=?"; }
		sql += "group by month";
//				+ "to_char(sum(checkouttime - checkintime), 'HH:MI:SS')"
		
		try (Connection con = DB.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
			if (userId != null) {
				ps.setString(1, userId);
			}
			ResultSet rs = ps.executeQuery();
			 Map<String,Long> list= new HashMap<>();
			while (rs.next()) {
				list.put(rs.getString("month"), rs.getLong("count"));
			}
			return list;
		} catch (SQLException e) {
				e.printStackTrace();
		}
		return null;
	}

	
	//月単位での出勤日数の計算
	public Map<String, Long> getMonthlyCheckInCounts(String userId) throws SQLException {
//		return attendanceRecords.stream()
//				.filter(att -> userId == null || userId.isEmpty() || att.getUserId().equals(userId))
//				.filter(att -> att.getCheckInTime() != null)
//				.collect(Collectors.groupingBy(
//						att -> YearMonth.from(att.getCheckInTime()),
//						Collectors.counting()
//						));
		String sql = "SELECT to_char(checkintime, 'YYYY/MM') as month ,count(userid) as count FROM attendance ";
		if (userId != null) { sql += "AND userid=?"; }
		sql += "group by month" ;
//				+ "to_char(sum(checkouttime - checkintime), 'HH:MI:SS')"
		
		try (Connection con = DB.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
			if (userId != null) {
				ps.setString(1, userId);
			}
			ResultSet rs = ps.executeQuery();
			 Map<String,Long> list= new HashMap<>();
			while (rs.next()) {
				list.put(rs.getString("month"), rs.getLong("count"));
			}
			return list;
		} catch (SQLException e) {
				e.printStackTrace();
		}
		return null;
	}
	
	//管理者が手動で勤怠登録するやつ
	public void addManualAttendance(String userId, LocalDateTime checkIn, 
								LocalDateTime checkOut) throws SQLException {
		String sql = "INSERT INTO attendance(userid, checkintime, checkouttime) VALUES(?,?,?)";
		try (Connection con = DB.getConnection(); 
				PreparedStatement ps = con.prepareStatement(sql)){
			ps.setString(1, userId);
			ps.setTimestamp(2, Timestamp.valueOf(checkIn));
			ps.setTimestamp(3, Timestamp.valueOf(checkOut));
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
	
	//勤怠時刻の編集 上手くできたらtrueを返す
	public boolean updateManualAttendance(String userId, LocalDateTime oldCheckIn, 
			LocalDateTime oldCheckOut,LocalDateTime newCheckIn, LocalDateTime newCheckOut) 
			throws SQLException {
//		for (int i = 0; i < attendanceRecords.size(); i++) {
//			Attendance att = attendanceRecords.get(i);
//			if (att.getUserId().equals(userId) &&
//					att.getCheckInTime().equals(oldCheckIn) &&
//					(att.getCheckOutTime() == null ? oldCheckOut == null : att.getCheckOutTime().equals(oldCheckOut))) {
//				att.setCheckInTime(newCheckIn);
//				att.setCheckOutTime(newCheckOut);
//				return true;
//			}
//		}
//		return false;
		
		String sql = "UPDATE attendance SET checkintime = ?, checkouttime=? WHERE userid = ? AND checkintime=?";
		try (Connection con = DB.getConnection(); 
				PreparedStatement ps = con.prepareStatement(sql)){
			ps.setTimestamp(1, Timestamp.valueOf(newCheckIn));
			ps.setTimestamp(2, Timestamp.valueOf(newCheckOut));
			ps.setString(3, userId);
			ps.setTimestamp(4, Timestamp.valueOf(oldCheckIn));
			ps.executeUpdate();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	//勤怠記録の削除
	public boolean deleteManualAttendance(String userId, LocalDateTime checkIn, 
										LocalDateTime checkOut) throws SQLException {
//		return attendanceRecords.removeIf(att ->
//		att.getUserId().equals(userId) &&
//		att.getCheckInTime().equals(checkIn) &&
//		(att.getCheckOutTime() == null ? checkOut == null : att.getCheckOutTime().equals(checkOut))
//				);
		
		String sql = "DELETE FROM attendance WHERE userid = ? AND checkintime=? AND checkouttime=?";
	try (Connection con = DB.getConnection(); 
			PreparedStatement ps = con.prepareStatement(sql)){
		ps.setString(1, userId);
		ps.setTimestamp(2, Timestamp.valueOf(checkIn));
		ps.setTimestamp(3, Timestamp.valueOf(checkOut));
		ps.executeUpdate();
		return true;
	} catch (SQLException e) {
		e.printStackTrace();
	}
	return false;
	}
	
	// return用のやつ
	private Attendance map(ResultSet rs) throws SQLException {
		if (rs.getTimestamp("checkouttime") != null) {
			Attendance at = new Attendance(rs.getString("userid"), 
					rs.getTimestamp("checkintime").toLocalDateTime(), 
					rs.getTimestamp("checkouttime").toLocalDateTime());	
			return at;
		} else {
			Attendance at = new Attendance(rs.getString("userid"), 
					rs.getTimestamp("checkintime").toLocalDateTime(), 
					null);	
			return at;
		}
	}
}
