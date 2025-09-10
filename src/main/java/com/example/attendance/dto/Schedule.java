package com.example.attendance.dto;
import java.time.LocalDateTime;

public class Schedule {
	
	private String userId;
	private LocalDateTime date;
	
	public Schedule(String userId, LocalDateTime date) {
		this.userId = userId;
		this.date = date;
	}

	public void setDate(LocalDateTime date) {
		this.date = date;
	}

	public String getUserId() {
		return userId;
	}
	
	public LocalDateTime getScheduleDate() {
		return date;
	}
	
	public int getScheduleYear() {
		return date.getYear();
	}
	
	public int getScheduleMonth() {
		return date.getMonth().getValue();
	}
		
}
	