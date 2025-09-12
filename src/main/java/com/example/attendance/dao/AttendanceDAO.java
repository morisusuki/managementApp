package com.example.attendance.dao;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

import com.example.attendance.dto.Attendance;
public class AttendanceDAO {
	
	//	従業員の内容を呼び出すattendanceがいっぱい入ってるリスト
	private static final List<Attendance> attendanceRecords = new CopyOnWriteArrayList<>();
	
	public void checkIn(String userId) throws SQLException {
//		Attendance attendance = new Attendance(userId);
//		attendance.setCheckInTime(LocalDateTime.now());
//		attendanceRecords.add(attendance);
		
		String sql = "INSERT INTO attendance(userid, checkintime) VALUES(?,?)";
		try (Connection con = DB.getConnection(); 
				PreparedStatement ps = con.prepareStatement(sql)){
			ps.setString(1, userId);
			ps.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void checkOut(String userId) throws SQLException {
//		attendanceRecords.stream()
//		.filter(att -> userId.equals(att.getUserId()) && att.getCheckOutTime() == null)
//		.findFirst()
//		.ifPresent(att -> att.setCheckOutTime(LocalDateTime.now()));
		
		//名前で検索,最新のやつ持ってくる,checkOutTime==null確認する
		String sql = "UPDATE attendance SET checkouttime = ? WHERE userid = ? AND checkouttime IS NULL ORDER BY checkintime DESC LIMIT 1";
		try (Connection con = DB.getConnection(); 
				PreparedStatement ps = con.prepareStatement(sql)){
			ps.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
			ps.setString(2, userId);
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	//指定ユーザーの勤怠履歴をリストで取得
	public List<Attendance> findByUserId(String userId) throws SQLException {
//		return attendanceRecords.stream()
//				.filter(att -> userId.equals(att.getUserId()))
//				.collect(Collectors.toList());
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
//		return attendanceRecords.stream()
//				.filter(att -> (userId == null || userId.isEmpty() || att.getUserId().equals(userId)))
//				.filter(att -> (startDate == null || (att.getCheckInTime() != null
//				&& !att.getCheckInTime().toLocalDate().isBefore(startDate))))
//				.filter(att -> (endDate == null || (att.getCheckInTime() != null
//				&& !att.getCheckInTime().toLocalDate().isAfter(endDate))))
//				.collect(Collectors.toList());
		
		String sql_u = null;
		String sql_s = null;
		String sql_e = null;
		if (userId != null || !userId.isEmpty()) { sql_u = "AND userid=?"; }
		if (startDate != null) { sql_s = "AND checkintime>=?"; }
		if (endDate != null) { sql_e = "AND checkintime<=?"; }
		String sql = "SELECT * FROM attendance WHERE 1=1" + sql_u + sql_s + sql_e;
		
		try (Connection con = DB.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
			int i = 1;
			if (sql_u != null) {
				ps.setString(i, userId);
				i += 1;
			}
			if (sql_s != null) {
				ps.setTimestamp(i, Timestamp.valueOf(startDate.atStartOfDay()));
				i += 1;
			}
			if (sql_e != null) {
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
	
	//月単位の総労働時間の計算
	public Map<String, Long> getMonthlyWorkingHours(String userId) throws SQLException {
//		return attendanceRecords.stream()
//				.filter(att -> userId == null || userId.isEmpty() || att.getUserId().equals(userId))
//				.filter(att -> att.getCheckInTime() != null && att.getCheckOutTime() != null)
//				.collect(Collectors.groupingBy(
//						att -> YearMonth.from(att.getCheckInTime()),
//						Collectors.summingLong(att -> ChronoUnit.HOURS.between(att.getCheckInTime(),
//								att.getCheckOutTime()))
//						));
		String sql_u = null;
		if (userId != null || !userId.isEmpty()) { sql_u = "AND userid=?"; }
		String sql = "SELECT to_char(checkintime, 'YYYY/MM') as month ,"
//				+ "to_char(sum(checkouttime - checkintime), 'HH:MI:SS')"
				+ "sum(extract(epoch from(checkouttime - checkintime))/3600) as count"
				+ "FROM attendance "+ sql_u + "group by month" ;
		
		try (Connection con = DB.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
			if (sql_u != null) {
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
	public Map<YearMonth, Long> getMonthlyCheckInCounts(String userId) throws SQLException {
		return attendanceRecords.stream()
				.filter(att -> userId == null || userId.isEmpty() || att.getUserId().equals(userId))
				.filter(att -> att.getCheckInTime() != null)
				.collect(Collectors.groupingBy(
						att -> YearMonth.from(att.getCheckInTime()),
						Collectors.counting()
						));
	}
	
	//管理者が手動で勤怠登録するやつ
	public void addManualAttendance(String userId, LocalDateTime checkIn, 
								LocalDateTime checkOut) throws SQLException {
		Attendance newRecord = new Attendance(userId);
		newRecord.setCheckInTime(checkIn);
		newRecord.setCheckOutTime(checkOut);
		attendanceRecords.add(newRecord);
	}
	
	//勤怠時刻の編集 上手くできたらtrueを返す
	public boolean updateManualAttendance(String userId, LocalDateTime oldCheckIn, 
			LocalDateTime oldCheckOut,LocalDateTime newCheckIn, LocalDateTime newCheckOut) 
			throws SQLException {
		for (int i = 0; i < attendanceRecords.size(); i++) {
			Attendance att = attendanceRecords.get(i);
			if (att.getUserId().equals(userId) &&
					att.getCheckInTime().equals(oldCheckIn) &&
					(att.getCheckOutTime() == null ? oldCheckOut == null : att.getCheckOutTime().equals(oldCheckOut))) {
				att.setCheckInTime(newCheckIn);
				att.setCheckOutTime(newCheckOut);
				return true;
			}
		}
		return false;
	}
	
	//勤怠記録の削除
	public boolean deleteManualAttendance(String userId, LocalDateTime checkIn, 
										LocalDateTime checkOut) throws SQLException {
		return attendanceRecords.removeIf(att ->
		att.getUserId().equals(userId) &&
		att.getCheckInTime().equals(checkIn) &&
		(att.getCheckOutTime() == null ? checkOut == null : att.getCheckOutTime().equals(checkOut))
				);
	}
	
	// return用のやつ
	private Attendance map(ResultSet rs) throws SQLException {
//		LocalDateTime in = (rs.getTimestamp("checkintime").toLocalDateTime() != null) ? rs.getTimestamp("checkintime").toLocalDateTime() : null;
	    Attendance at = new Attendance(rs.getString("userid"), 
	    					rs.getTimestamp("checkintime").toLocalDateTime(), 
	    					rs.getTimestamp("checkouttime").toLocalDateTime());
	    return at;
	}
}
