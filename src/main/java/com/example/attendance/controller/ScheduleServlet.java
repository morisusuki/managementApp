package com.example.attendance.controller;

import java.io.IOException;
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
		String action = req.getParameter("action");
		HttpSession session = req.getSession(false);
		User user = (User) session.getAttribute("user");
		if (user == null) {
			resp.sendRedirect("login.jsp");
			return;
		}
		
//		List<Schedule> scheduleList = scheduleDAO.monthSchedule();
		List<Schedule> scheduleList = scheduleDAO.allSchedule();
		req.setAttribute("scheduleList", scheduleList);
		RequestDispatcher rd = req.getRequestDispatcher("/jsp/schedule_list.jsp");
		rd.forward(req, resp);
//			RequestDispatcher rd = req.getRequestDispatcher("/jsp/admin_menu.jsp");


	}


}

//ステップ1
//	今の月のシフト表が出てくるようにフィルターかけたい
//	->必要なのはallScheduleのデータと今何月かのデータ
//	そのデータを専用のjspに投げたい
//ステップ2
//	操作があったらその月のシフト表が出るようにしたい
//ステップ3
//	adminがシフト表の操作をできるようにしたい
//ステップ4
//	ログイン済みなら次の自分のシフトがいつかを表示させたい
//	(シフトがなければ専用の表示)
