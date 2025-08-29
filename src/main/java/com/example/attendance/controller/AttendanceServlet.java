package com.example.attendance.controller;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.apache.naming.java.javaURLContextFactory;

import com.example.attendance.dao.AttendanceDAO;
import com.example.attendance.dto.Attendance;


@WebServlet("/AttendanceServlet")
public class AttendanceServlet extends HttpServlet {
	private final AttendanceDAO attendanceDAO = new AttendanceDAO();

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
		
		String message = (String) session.getAttribute("successMessage");
		if (message != null) {
			req.setAttribute("successMessage", message);
			session.removeAttribute("successMessage");
		}
		
		if ("export_csv".equals(action) && "admin".equals(user.getRole())) {
			exportCsv(req, resp);
		}
		
		else if ("filter".equals(action) && "admin".equals(user.getRole())) {
			String filterUserId = req.getParameter("filterUserId");
			String startDateStr = req.getParameter("startDate");
			String endDateStr = req.getParameter("endDate");
			LocalDate startDate = null;
			LocalDate endDate = null;
			
			try {
				if (startDateStr != null && !startDateStr.isEmpty()) {
					startDate = LocalDate.parse(startDateStr);
				}
				if (endDateStr != null && !endDateStr.isEmpty()) {
					endDate = LocalDate.parse(endDateStr);
				}
			}catch (DateTimeParseException e) {
				req.setAttribute("errorMessage", "日付の形式が不正です。");
			}
			
			List<Attendance> filteredRecords =
					AttendanceDAO.findFilteredRecords(filterUserId, startDate, endDate);
			req.setAttribute("allAttendanceRecords", filteredRecords);
			
			Map<String, Long> totalHoursByUser =
					filteredRecords.stream().collect(Collectors.groupingBy(
						Attendance::getUserId, Collectors.summingLong(att -> {
							if (att.getCheckInTime() != null && att.getCheckOutTime() != null) {
								return javaURLContextFactory.time.temporal.ChronoUntit.HOUSE.between(
									   att.getCheckInTime(), att.getCheckOutTime());
							}
							return 0L;
						})));

			req.setAttribute("totalHoursByUser", totalHoursByUser);
			
			req.setAttribute("monthlyWorkingHouse",
					attendanceDAO.getMonthlyWorkingHours(null));
			req.setAttribute("monthlyCheckInCounts",
					attendanceDAO.getMonthlyCheckInCounts(null));
			
			RequestDispatcher rd = req.getRequestDispatcher("/jsp/admin_menu.jsp");
			rd.forward(req, resp);
		} else {
			if ("admin".equals(user.getRole())) {
					req.setAttribute("allAttendanceRecords", attendanceDAO.findAll());
					Map<String, Long> totalHoursByUser =
						attendanceDAO.findAll().stream().collect(
							Collectors.groupingBy(Attendance::getUserId, Collectors.summingLong(
								att -> {
										if (att.getCheckInTime() != null && att.getCheckOutTime() != null) {
										return java.time.temporal.ChronoUnit.HOURS.between(
											   att.getCheckInTime(), att.getCheckOutTime());
										}
									return 0L;
								})));
				req.setAttribute("totalHoursByUser", totalHoursByUser);
				req.setAttribute("monthlyWorkingHours", attendanceDAO.getMonthlyWorkingHours(null));
				
				RequestDispatcher rd = RequestDispatcher.getRequestDispatcher("/jsp/admin_menu.jsp");
				rd.forward(req, resp);
			} else {
				req.setAttribute("attendanceRecords", AttendanceDAO.findByUserId(user.getUsername()));
				RequestDispatcher rd = RequestDispatcher.getRequestDispatcher("/jsp/employee_menu.jsp");
				rd.forward(req, resp);
			}
		}
	}
// p42DoPost前まで

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
