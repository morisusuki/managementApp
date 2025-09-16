package com.example.attendance.dto;
import java.time.LocalDateTime;

public class Schedule {
	
	private String userId;
	private LocalDateTime scheduleDate;
	
	public Schedule(String userId, LocalDateTime date) {
		this.userId = userId;
		this.scheduleDate = date;
	}

	public void setDate(LocalDateTime date) {
		this.scheduleDate = date;
	}

	public String getUserId() {
		return userId;
	}
	
	public LocalDateTime getScheduleDate() {
		return scheduleDate;
	}
	
	public int getScheduleYear() {
		return scheduleDate.getYear();
	}
	
	public int getScheduleMonth() {
		return scheduleDate.getMonth().getValue();
	}
		
}
	