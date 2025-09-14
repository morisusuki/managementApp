package com.example.attendance.controller;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Collection;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import com.example.attendance.dao.UserDAO;
import com.example.attendance.dto.User;
@WebServlet("/users")
public class UserServlet extends HttpServlet {
	private final UserDAO userDAO = new UserDAO();
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		
		try {
			
			String action = req.getParameter("action");
			HttpSession session = req.getSession(false);
			User currentUser = (User) session.getAttribute("user");
					if (currentUser == null || !"admin".equals(currentUser.getRole())) {
						resp.sendRedirect("login.jsp");
						return;
					}
					// Retrieve and clear message from session
					String message = (String) session.getAttribute("successMessage");
					if (message != null) {
						req.setAttribute("successMessage", message);
						session.removeAttribute("successMessage");
					}
					if ("list".equals(action) || action == null) {
						Collection<User> users = userDAO.getAllUsers();
//　確認用
//						if (users != null) {
//							System.out.println("users存在あり");
//						} else {
//							System.out.println("users存在なし");
//						}
//						
						req.setAttribute("users", users);
						RequestDispatcher rd =
								req.getRequestDispatcher("/jsp/user_management.jsp");
						rd.forward(req, resp);
					} else if ("edit".equals(action)) {
						String username = req.getParameter("username");
						User user = userDAO.findByUsername(username);
						req.setAttribute("userToEdit", user);
						Collection<User> users = userDAO.getAllUsers();
						req.setAttribute("users", users);
						RequestDispatcher rd =
								req.getRequestDispatcher("/jsp/user_management.jsp");
						rd.forward(req, resp);
					} else {
						resp.sendRedirect("users?action=list");
					}
		} catch (SQLException e) {
			e.printStackTrace();
			req.setAttribute("errorMessage","エラーが発生しました");
			RequestDispatcher rd = req.getRequestDispatcher("/login.jsp");
			rd.forward(req, resp);
		}
	}
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		
		try {
			
			req.setCharacterEncoding("UTF-8");
			String action = req.getParameter("action");
			HttpSession session = req.getSession(false);
			User currentUser = (User) session.getAttribute("user");
					if (currentUser == null || !"admin"
					.equals(currentUser.getRole())) {
						resp.sendRedirect("login.jsp");
						return;
					}
					if ("add".equals(action)) {
						String username = req.getParameter("username");
								String password = req.getParameter("password");
								String role = req.getParameter("role");
								if (userDAO.findByUsername(username) == null) {
//	パスワードhash化悩み中
//									userDAO.addUser(new User(username,
//											UserDAO.hashPassword(password), role));
									userDAO.addUser(new User(username, password, role));
									session.setAttribute("successMessage"," ユ ー ザ ー を 追 加 し ま した。");
								} else {
									req.setAttribute("errorMessage","ユーザーID は既に存在します。");
								}
					} else if ("update".equals(action)) {
						System.out.println("test1-2");
						String username = req.getParameter("username");
						String password = req.getParameter("password");
						String role = req.getParameter("role");
						boolean enabled = req.getParameter("enabled") != null;
						User existingUser = userDAO.findByUsername(username);
						if (existingUser != null) {
							userDAO.updateUser(new User(username,
									password, role, enabled), existingUser);
							session.setAttribute("successMessage","ユーザー情報を更新しました。");
						}
					} else if ("delete".equals(action)) {
						String username = req.getParameter("username");
								userDAO.deleteUser(username);
								session.setAttribute("successMessage","ユーザーを削除しました。");
//					} else if ("reset_password".equals(action)) {
//						System.out.println("test1");
//						String username = req.getParameter("username");
//						String newPassword = req.getParameter("newPassword");
//						userDAO.resetPassword(username, newPassword);
//						session.setAttribute("successMessage", username + "のパスワードをリセットしました。(デフォルトパスワード: " + newPassword + ")");
					} else if ("toggle_enabled".equals(action)) {
						String username = req.getParameter("username");
						boolean enabled = Boolean.parseBoolean(req.getParameter("enabled"));
						userDAO.toggleUserEnabled(username, enabled);
						session.setAttribute("successMessage", username + "のアカウントを"
								+ (enabled ? "有効" : "無効") + "にしました。");
					}
					resp.sendRedirect("users?action=list");
		} catch (SQLException e) {
			e.printStackTrace();
			req.setAttribute("errorMessage","エラーが発生しました");
			RequestDispatcher rd = req.getRequestDispatcher("/login.jsp");
			rd.forward(req, resp);
		}
	}
}