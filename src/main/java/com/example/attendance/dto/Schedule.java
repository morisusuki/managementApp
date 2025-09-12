package com.example.attendance.dto;
import java.time.LocalDateTime;

public class Schedule {
	
	private String userId;
	private LocalDateTime ScheduleDate;
	
	public Schedule(String userId, LocalDateTime date) {
		this.userId = userId;
		this.ScheduleDate = date;
	}

	public void setDate(LocalDateTime date) {
		this.ScheduleDate = date;
	}

	public String getUserId() {
		return userId;
	}
	
	public LocalDateTime getScheduleDate() {
		return ScheduleDate;
	}
	
	public int getScheduleYear() {
		return ScheduleDate.getYear();
	}
	
	public int getScheduleMonth() {
		return ScheduleDate.getMonth().getValue();
	}
		
}
	