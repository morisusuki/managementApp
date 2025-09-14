package com.example.attendance.controller;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import com.example.attendance.dao.AttendanceDAO;
import com.example.attendance.dao.UserDAO;
import com.example.attendance.dto.Attendance;
import com.example.attendance.dto.User;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
	private final UserDAO userDAO = new UserDAO();
	private final AttendanceDAO attendanceDAO = new AttendanceDAO();
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) 
			throws	ServletException, IOException {
		
		try {
			String username = req.getParameter("username");
			String password = req.getParameter("password");
			User user = userDAO.findByUsername(username);

			if (user != null && user.isEnabled() && userDAO.verifyPassword(username,password)) {
				HttpSession session = req.getSession();
				session.setAttribute("user", user);
				session.setAttribute("successMessage","ログインしました。"); // Store insession
				
				if ("admin".equals(user.getRole())) {
					req.setAttribute("allAttendanceRecords", attendanceDAO.findAll());
					List<Attendance> filteredRecords = attendanceDAO.findFilteredRecords(null, null, null);
					req.setAttribute("allAttendanceRecords", filteredRecords);
					Map<String, Long> totalHoursByUser = attendanceDAO.getMonthlyWorkingHours(null);
					req.setAttribute("totalHoursByUser", totalHoursByUser);
					req.setAttribute("monthlyWorkingHours",
							attendanceDAO.getMonthlyWorkingHours(null));
					req.setAttribute("monthlyCheckInCounts",
							attendanceDAO.getMonthlyCheckInCounts(null));
					RequestDispatcher rd = req.getRequestDispatcher("/jsp/admin_menu.jsp");
					rd.forward(req, resp);
				} else {
					//のちのちgetMonthlyWorkingHoursも導入したい
					//	attendanceDAO.getMonthlyWorkingHours(username);
					req.setAttribute("attendanceRecords",attendanceDAO.findByUserId(user.getUsername()));
					RequestDispatcher rd = req.getRequestDispatcher("/jsp/employee_menu.jsp");
					rd.forward(req, resp);
				}
			} else {
				req.setAttribute("errorMessage","ユーザーID またはパスワードが不正です。またはアカウントが無効です。");
						RequestDispatcher rd = req.getRequestDispatcher("/login.jsp");
								rd.forward(req, resp);
					}
		} catch (SQLException e) {
			e.printStackTrace();

			req.setAttribute("errorMessage","エラーが発生しました");
			RequestDispatcher rd = req.getRequestDispatcher("/login.jsp");
			rd.forward(req, resp);
		}
	}
}