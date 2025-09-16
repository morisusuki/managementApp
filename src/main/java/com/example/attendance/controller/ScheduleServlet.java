package com.example.attendance.controller;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import com.example.attendance.dao.ScheduleDAO;
import com.example.attendance.dto.Schedule;
import com.example.attendance.dto.User;


@WebServlet("/schedule")
public class ScheduleServlet extends HttpServlet {
	private final ScheduleDAO scheduleDAO = new ScheduleDAO();

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
									throws ServletException, IOException {
//		String action = req.getParameter("action");
//		try {
			HttpSession session = req.getSession(false);
			User user = (User) session.getAttribute("user");
			if (user == null) {
				resp.sendRedirect("login.jsp");
				return;
			}
			String dateStr = req.getParameter("date");
			if (dateStr != null) {
				System.out.println(dateStr);
			} else {
				StringBuilder d = new StringBuilder(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
				d.delete(7, 10);
				dateStr = d.toString();
			}
			dateStr += "%";
			List<Schedule> scheduleList = scheduleDAO.monthSchedule(user.getUsername(), dateStr);
			req.setAttribute("scheduleList", scheduleList);
			RequestDispatcher rd = req.getRequestDispatcher("/jsp/schedule_list.jsp");
			rd.forward(req, resp);
	//			RequestDispatcher rd = req.getRequestDispatcher("/jsp/admin_menu.jsp");
//		} catch (SQLException e) {
//			e.printStackTrace();
//			req.setAttribute("errorMessage","エラーが発生しました");
//			RequestDispatcher rd = req.getRequestDispatcher("/login.jsp");
//			rd.forward(req, resp);
//		}

	}


}

//ステップ1　完了
//	今の月のシフト表が出てくるようにフィルターかけたい
//	->必要なのはallScheduleのデータと今何月かのデータ
//	そのデータを専用のjspに投げたい
//ステップ2　完了
//	操作があったらその月のシフト表が出るようにしたい
//ステップ3
//	adminがシフト表の操作をできるようにしたい
//ステップ4
//	ログイン済みなら次の自分のシフトがいつかを表示させたい
//	(シフトがなければ専用の表示)
