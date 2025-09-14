package com.example.attendance.dto;
import java.time.LocalDateTime;

public class Attendance {
	
	private String userId;
	private LocalDateTime checkInTime;
	private LocalDateTime checkOutTime;
	
//	public Attendance(String userId) {
//		this.userId = userId;
//	}
	public Attendance(String userId, LocalDateTime checkInTime, LocalDateTime checkOutTime) {
		this.userId = userId;
		this.checkInTime = checkInTime;
		this.checkOutTime = checkOutTime;
	}
	public String getUserId() {
		return userId;
	}
	public LocalDateTime getCheckInTime() {
		return checkInTime;
	}
	public void setCheckInTime(LocalDateTime checkInTime) {
		this.checkInTime = checkInTime;
	}
	public LocalDateTime getCheckOutTime() {
		return checkOutTime;
	}
	public void setCheckOutTime(LocalDateTime checkOutTime) {
		this.checkOutTime = checkOutTime;
	}
}