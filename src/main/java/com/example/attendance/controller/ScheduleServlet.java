package com.example.attendance.controller;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import com.example.attendance.dao.ScheduleDAO;
import com.example.attendance.dao.UserDAO;
import com.example.attendance.dto.Schedule;
import com.example.attendance.dto.User;


@WebServlet("/schedule")
public class ScheduleServlet extends HttpServlet {
	private final ScheduleDAO scheduleDAO = new ScheduleDAO();
	private final UserDAO userDAO = new UserDAO();

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
									throws ServletException, IOException {
		try {
		HttpSession session = req.getSession(false);
		
		String message = (String) session.getAttribute("message");
		if (message != null) {
			req.setAttribute("message", message);
			session.removeAttribute("message");
		}
		
		User user = (User) session.getAttribute("user");
		boolean login = false;
		boolean admin_check = false;
		if (user != null) {
			login = true;
			if (user.getRole().equals("admin")) {
				admin_check = true;
			}
		}
		req.setAttribute("login", login);

		String dateStr = req.getParameter("date");
		if (dateStr == null || dateStr.isEmpty()) {
			StringBuilder d = new StringBuilder(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
			d.delete(7, 10);
			dateStr = d.toString();
		}
		dateStr += "%";
		
//		従業員,未ログイン用ページ
		if (!admin_check) {
//	従業員用リスト
			if (login) {
				List<Schedule> scheduleList = scheduleDAO.monthSchedule(user.getUsername(), dateStr);
				req.setAttribute("scheduleList", scheduleList);
			}
//	未ログイン用リスト
			else {
				List<Schedule> scheduleList = scheduleDAO.allSchedule(dateStr);
				req.setAttribute("scheduleList", scheduleList);
			}
			req.setAttribute("admin_check", admin_check);
			RequestDispatcher rd = req.getRequestDispatcher("/jsp/schedule_list.jsp");
			rd.forward(req, resp);
		}
		
//	管理者用ページ
		//	編集切り替え
		else {
			//	ユーザー名でのフィルタ確認
			String userId = req.getParameter("filterUserId");
			if (userId != null && !userId.isEmpty()) {
				List<Schedule> scheduleList = scheduleDAO.monthSchedule(userId, dateStr);
				req.setAttribute("scheduleList", scheduleList);
			} else {
				List<Schedule> scheduleList = scheduleDAO.allSchedule(dateStr);	
				req.setAttribute("scheduleList", scheduleList);
			}
			
			req.setAttribute("admin_check", admin_check);
			req.setAttribute("userList", userDAO.getAllUsers());
			
			if (req.getParameter("action").equals("edit") && admin_check) {
				String edit_userId = req.getParameter("edit_userId");
				String edit_dateStr = req.getParameter("edit_date");
				LocalDateTime edit_scheduleDate = LocalDateTime.parse(edit_dateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm"));
				Schedule scheduleEdit = new Schedule(edit_userId, edit_scheduleDate);
				req.setAttribute("scheduleEdit", scheduleEdit);
				
			}	
			RequestDispatcher rd = req.getRequestDispatcher("/jsp/schedule_list.jsp");
			rd.forward(req, resp);
		}


	//			RequestDispatcher rd = req.getRequestDispatcher("/jsp/admin_menu.jsp");
		} catch (SQLException e) {
			e.printStackTrace();
			req.setAttribute("errorMessage","エラーが発生しました");
			RequestDispatcher rd = req.getRequestDispatcher("/login.jsp");
			rd.forward(req, resp);
		}

	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws
	ServletException, IOException {
		
		
		String action = req.getParameter("action");
		HttpSession session = req.getSession(false);
		User user = (User) session.getAttribute("user");
		
		String message = (String) session.getAttribute("message");
		if (message != null) {
			req.setAttribute("message", message);
			session.removeAttribute("message");
		}
		
//	削除機能
		if (action.equals("delete") && user.getRole().equals("admin")) {
			String userId = req.getParameter("userId");
			String scheduleDateStr = req.getParameter("scheduleDate");
			LocalDateTime scheduleDate = LocalDateTime.parse(scheduleDateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm"));
			if (scheduleDAO.deleteSchedule(userId, scheduleDate)) {
				session.setAttribute("successMessage", "シフトを削除しました。");
			} else {
				session.setAttribute("errorMessage", "シフトの削除に失敗しました。");
			}
		} 
		
//　編集機能
		else if (action.equals("edit") && user.getRole().equals("admin")) {
			String userId = req.getParameter("userId");
			String newDateStr = req.getParameter("new_date");
			String oldDateStr = req.getParameter("old_date");
			LocalDateTime newScheduleDate = LocalDateTime.parse(newDateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm"));
			LocalDateTime oldScheduleDate = LocalDateTime.parse(oldDateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm"));
			
			if (scheduleDAO.changeSchedule(userId, oldScheduleDate, newScheduleDate)) {
				session.setAttribute("message", "シフトを追加しました。");
			} else {
				session.setAttribute("message", "シフトの追加に失敗しました。");
			}
		}
		
//　シフト追加機能
		else if (action.equals("add") && user.getRole().equals("admin")) {
			String userId = req.getParameter("userId");
			String dateStr = req.getParameter("scheduleDate");
			try {
				LocalDateTime scheduleDate = LocalDateTime.parse(dateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm"));
				scheduleDAO.setSchedule(userId, scheduleDate);
				session.setAttribute("message", "シフトを追加しました。");
			} catch (DateTimeParseException e) {
				session.setAttribute("message", "シフトの追加に失敗しました。");
			}
			
		}
		
		if (req.getParameter("date") != null) {
			resp.sendRedirect("schedule?action=schedule"
					+ "&date=" + req.getParameter("date"));
		} else {
			resp.sendRedirect("schedule?action=schedule");
		}
	}


}

//ステップ1　完了
//	今の月のシフト表が出てくるようにフィルターかけたい
//	->必要なのはallScheduleのデータと今何月かのデータ
//	そのデータを専用のjspに投げたい
//ステップ2　完了
//	操作があったらその月のシフト表が出るようにしたい
//ステップ3　完了
//	adminがシフト表の操作をできるようにしたい
//ステップ4　完了
//	ログイン済みなら次の自分のシフトがいつかを表示させたい
//	(シフトがなければ専用の表示)
//ステップ5　完了
//	戻る時のボタン追加
//ステップ6　完了
//	管理者画面で名前フィルタ
//ステップ7　完了
//	ログアウト時でも閲覧可能に
