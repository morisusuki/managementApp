package com.example.attendance.dao;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

//import com.example.attendance.dto.Attendance;
import com.example.attendance.dto.Schedule;

public class ScheduleDAO {
	private static final List<Schedule> scheduleList = new CopyOnWriteArrayList<>();
	
	//　スケジュール追加
	public void setSchedule(String userId, LocalDateTime date) {
		Schedule schedule = new Schedule(userId, date);
		scheduleList.add(schedule);
	}
	//　スケジュール変更	
	public boolean changeSchedule(String userId, LocalDateTime oldDate, LocalDateTime newDate) {
		for (int i = 0; i < scheduleList.size(); i++) {
			Schedule sc = scheduleList.get(i);
			if (sc.getUserId().equals(userId) && sc.getDate().equals(oldDate)) {
				sc.setDate(newDate);
				return true;
			}
		}
		return false;
	}
	//　スケジュール削除
	public boolean deleteSchedule(String userId, LocalDateTime date) {
		return scheduleList.removeIf(sc -> 
				sc.getUserId().equals(userId) && 
				sc.getDate().equals(date));
	}
	//　全日程渡し
	public List<Schedule> allSchedule() {
		return new ArrayList<>(scheduleList);
	}

}