package com.example.attendance.dao;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

import com.example.attendance.dto.Attendance;

public class AttendanceDAO {
	//	従業員の内容を呼び出すattendanceがいっぱい入ってるリスト
	private static final List<Attendance> attendanceRecords
									= new CopyOnWriteArrayList<>();
	
	public void checkIn(String userId) {
		Attendance attendance = new Attendance(userId);
		attendance.setCheckInTime(LocalDateTime.now());
		attendanceRecords.add(attendance);
	}
	
	public void checkOut(String userId) {
		attendanceRecords.stream()
				.filter(att -> userId.equals(att.getUserId()) &&
							   att.getCheckOutTime() == null)
				.findFirst()
				.ifPresent(att -> att.setCheckOutTime(LocalDateTime.now()));
	}
	
	//指定ユーザーの勤怠履歴をリストで取得
	public List<Attendance> findByUserId(String userId) {
		return attendanceRecords.stream()
				.filter(att -> userId.equals(att.getUserId()))
				.collect(Collectors.toList());
	}
	
	public List<Attendance> findAll() {
		return new ArrayList<>(attendanceRecords);
	}
	
	//条件検索(ユーザーID、出勤時間、退勤時間)
	public List<Attendance> findFilteredRecords
			(String userId, LocalDate startDate, LocalDate endDate) {
		return attendanceRecords.stream()
				.filter(att -> (userId == null ||
								userId.isEmpty() ||
								att.getUserId().equals(userId)))
				.filter(att -> (startDate == null || (
								  att.getCheckInTime() != null &&
								  !att.getCheckInTime().toLocalDate().isBefore(startDate))
								))
				.filter(att -> (endDate == null || (
									att.getCheckInTime() != null &&
									!att.getCheckInTime().toLocalDate().isAfter(endDate))
								))
				.collect(Collectors.toList());
	}
	
	//月単位の総労働時間の計算
	public Map<YearMonth, Long> getMonthlyWorkingHours(String userId) {
		return attendanceRecords.stream()
				.filter(att -> userId == null ||
							   userId.isEmpty() ||
							   att.getUserId().equals(userId))
				.filter(att -> att.getCheckInTime() != null &&
							   att.getCheckOutTime() != null)
				.collect(Collectors.groupingBy(
						att -> YearMonth.from(att.getCheckInTime()),
							   Collectors.summingLong(att -> ChronoUnit.HOURS.between(
								att.getCheckInTime(), att.getCheckOutTime())
								)));
	}
	
	//月単位での出勤日数の計算
	public Map<YearMonth, Long> getMonthlyCheckInCounts(String userId) {
		return attendanceRecords.stream()
				.filter(att -> userId == null ||
						userId.isEmpty() ||
						att.getUserId().equals(userId))
				.filter(att -> att.getCheckInTime() != null)
				.collect(Collectors.groupingBy(
						att -> YearMonth.from(att.getCheckInTime()),
						Collectors.counting()));
	}
	
	//管理者が手動で勤怠登録するやつ
	public void addManualAttendance
			(String userId, LocalDateTime checkIn,LocalDateTime checkOut) {
		Attendance newRecord = new Attendance(userId);
		newRecord.setCheckInTime(checkIn);
		newRecord.setCheckOutTime(checkOut);
		attendanceRecords.add(newRecord);
	}
	
	public boolean updateManualAttendance
			(String userId, LocalDateTime oldCheckIn,LocalDateTime oldCheckOut,
			LocalDateTime newCheckIn, LocalDateTime newCheckOut) {
		for (int i = 0; i < attendanceRecords.size(); i++) {
			Attendance att = attendanceRecords.get(i);
			if (att.getUserId().equals(userId) &&
				att.getCheckInTime().equals(oldCheckIn) &&
				(att.getCheckOutTime() == null ?
						oldCheckOut == null : att.getCheckOutTime().equals(oldCheckOut))) {
				att.setCheckInTime(newCheckIn);
				att.setCheckOutTime(newCheckOut);
				return true;
			}
		}
		return false;
	}
	
	public boolean deleteManualAttendance
			(String userId, LocalDateTime checkIn, LocalDateTime checkOut) {
		return attendanceRecords.removeIf(att ->
				att.getUserId().equals(userId) &&
				att.getCheckInTime().equals(checkIn) &&
				(att.getCheckOutTime() == null ? checkOut == null : att.getCheckOutTime().equals(checkOut)));
	}
}
