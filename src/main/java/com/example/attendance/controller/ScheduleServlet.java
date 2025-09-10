package com.example.attendance.controller;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


@WebServlet("/schedule")
public class ScheduleServlet extends HttpServlet {

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

	}


}

//ステップ1
//	今の月のシフト表が出てくるようにフィルターかけたい
//	そのデータを専用のjspに投げたい
//ステップ2
//	操作があったらその月のシフト表が出るようにしたい
//ステップ3
//	adminがシフト表の操作をできるようにしたい
//ステップ4
//	ログイン済みなら次の自分のシフトがいつかを表示させたい
//	(シフトがなければ専用の表示)
