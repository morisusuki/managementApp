package com.example.attendance.controller;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
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

import org.apache.catalina.User;
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

	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
										throws ServletException, IOException {
		HttpSession session = req.getSession(false);
		User user = (User) session.getAttribute("user");
		
		if (user == nul) {
			resp.sendRedirect("login.jsp");
			return;
		}
		
		String action = req.getParameter("action");
		
		if ("check_in".equals(action)) {
			attendanceDAO.checkIn(user.getUsername());
			session.setAttribute("successMessage", "出勤を記録しました");
		} else if ("check_out".equals(action)) {
			attendanceDAO.checkOut(user.getUsername());
			session.setAttribute("seccessMessage", "退勤を記録しました");
		} else if ("add_manual".equals(action) && "admin".equals(user.getRoles())) {
			String userId = req.getParameter("userId");
			String checkInStr = req.getParameter("checkInTime");
			String checkOutStr = req.getParameter("checkOutTime");
			
			try {
				LocalDateTime checkIn = LocalDateTime.parse(checkOutStr);
				LocalDateTime checkOut = checkOutStr != null &&
										!checkOutStr.isEmpty() ? LocalDateTime.parse(checkOutStr) :null;
			attendanceDAO.addManualAttendance(userId, checkIn, checkOut);
			session.setAttribute("successMessage", "勤怠管理を手動で追加しました");
			} catch (DateTimeParseException e) {
				session.setAttribute("errorMessage", "日付/時刻の形式が不正です");
			}
		} else if ("update_manual".equals(action) && "admin".equals(user.getRoles())) {
			String userId = req.getParameter("userId");
			LocalDateTime oldCheckIn =
					LocalDateTime.parse(req.getParameter("oldCheckInTime"));
			LocalDateTime oldCheckOut = req.getParameter("oldCheckOutTime") != null &&
										!req.getParameter("olcCheckOutTime").isEmpty() ?
										LocalDateTime.parse(req.getParameter("oldCheckOutTime")) : null;
			LocalDateTime newCheckIn = LocalDateTime.parse(req.getParameter("newCheckInTime"));
			LocalDateTime newCheckOut = req.getParameter("newCheckOutTime") != null &&
										!req.getParameter("newCheckOutTime").isEmpty() ?
										LocalDateTime.parse(req.getParameter("newCheckOutTime") : null ;)
		//p43 10行目まで
		}
	}

}
