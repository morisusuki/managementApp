package com.example.attendance.dao;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

//import com.example.attendance.dto.Attendance;
import com.example.attendance.dto.Schedule;

public class ScheduleDAO {
	private static final List<Schedule> scheduleList = new CopyOnWriteArrayList<>();
	
	//　テスト
	static {
		Random ran = new Random();
		for (int i = 0; i < 5; i++) {
			int y = ran.nextInt(6);
			int m = ran.nextInt(11) + 1;
			int d = ran.nextInt(29) + 1;
			Schedule s1 = new Schedule("mori", LocalDateTime.of(2020 + y, m, d, 17, 00, 00));
			
			scheduleList.add(s1);
		}	
		Schedule s1 = new Schedule("mori", LocalDateTime.of(2025, 9, 10, 17, 00, 00));
		scheduleList.add(s1);
		if (scheduleList.size() > 0) {
			System.out.println(scheduleList);
		}
	}
//	static {
//		users.put("admin1", new User("admin1", hashPassword("adminpass"), "admin", true));
////		users.put("employee1", new User("employee1", hashPassword("password"), "employee", true));
//	}
	
	//　スケジュール追加
	public void setSchedule(String userId, LocalDateTime date) {
		Schedule schedule = new Schedule(userId, date);
		scheduleList.add(schedule);
	}
	//　スケジュール変更	
	public boolean changeSchedule(String userId, LocalDateTime oldDate, LocalDateTime newDate) {
		for (int i = 0; i < scheduleList.size(); i++) {
			Schedule sc = scheduleList.get(i);
			if (sc.getUserId().equals(userId) && sc.getScheduleDate().equals(oldDate)) {
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
				sc.getScheduleDate().equals(date));
	}
	//　全日程渡し
	public List<Schedule> allSchedule() {
		return new ArrayList<>(scheduleList);
	}
	//	今の月の日程
	public List<Schedule> monthSchedule() {
//		今の年を取得　　
		int year = LocalDateTime.now().getYear();
//		今の月を取得　　
		int month = LocalDateTime.now().getMonth().getValue();
//		Comparator<Schedule> comparator = 
//				Comparator.comparing(Schedule::getScheduleDate).reversed();
		return scheduleList.stream().filter(sc -> 
					   (sc.getScheduleYear() == year && 
					   sc.getScheduleMonth() == month))
				.sorted(Comparator.comparing(Schedule::getScheduleDate))
				.collect(Collectors.toList());
	}

}