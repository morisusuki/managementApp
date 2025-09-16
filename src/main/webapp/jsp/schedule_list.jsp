<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<!DOCTYPE html>
<html lang="ja">
<head>
<meta charset="UTF-8">
<title>シフト表</title>
<link rel="stylesheet" href="style.css">
</head>
<body>
	<div class="container">
		<c:choose>
			<c:when test="${admin_check}">
				<h1>シフト管理</h1>
			</c:when>
			<c:otherwise>
				<h1>シフト表</h1>
			</c:otherwise>
		</c:choose>
		
		<div class="main-nav">
			<c:choose>
				<c:when test="${admin_check}">
					<a href="attendance?action=filter">勤怠履歴管理</a> 
					<a href="users?action=list">ユーザー管理</a>
					<a href="logout">ログアウト</a>
				</c:when>
				<c:when test="${login}">
					<a href="attendance?action=filter">従業員メニュー</a>
					<a href="logout">ログアウト</a>
				</c:when>
				<c:otherwise>
					<a href="login.jsp">ログイン</a>
				</c:otherwise>
			</c:choose>
		</div>
	
		<c:if test="${not empty sessionScope.message}">
			<p class="success-message">
				<c:out value="${sessionScope.message}" />
			</p>
			<c:remove var="message" scope="session" />
		</c:if>
	
		<form action="schedule" method="get" class="filter-form">
			<c:if test="${ admin_check }">
				<div>
					<label for="filterUserId">ユーザーID:</label> 
					<select name="filterUserId" id="filterUserId">
						<c:forEach var="user" items="${userList}">
							<c:choose>
								<c:when test="${ param.filterUserId == user.username }">
									<option value="${user.username}" selected>${user.username}</option>
								</c:when>
								<c:otherwise>
									<option value="${user.username}">${user.username}</option>
								</c:otherwise>
							</c:choose>
						</c:forEach>
					</select>
				</div>
			</c:if>
			<input type="hidden" name="action" value="schedule">
			<div>
				<label for="filterDate">指定期間:</label> 
				<input type="month" id="filterDate" name="date" 
					value="<c:out value="${param.date}"/>">
			</div> 
			
			<button type="submit" class="button">適用</button>
			<c:if test="${ admin_check }">
				<a href="#add_page" class="button">シフトを追加</a>
			</c:if>
		</form>
		
		<table>
			<thead>
				<tr>
					<th>名前</th>
					<th>日時</th>
					<c:if test="${admin_check}">
						<th>編集</th>
					</c:if>
				</tr>
			</thead>
			<tbody>
				<c:forEach var="schedule" items="${scheduleList}">
					<tr>
						<%-- <td><c:out value="${sc}"/></td> --%>
						<td>${schedule.userId}</td> 
	<%-- 					<td>${schedule.scheduleDate}</td> --%>
	 					<fmt:parseDate value="${schedule.scheduleDate}" pattern="yyyy-MM-dd'T'HH:mm" var="scDate" type="date"/>
						<fmt:formatDate value="${scDate}" var="newScDate" type="date" pattern="yyyy/MM/dd HH:mm" />
						<td>${newScDate}</td>
						<c:if test="${ admin_check }">
							<td class="table-actions">
								<!-- 編集ボタン -->
								<a href="schedule?action=edit&edit_userId=${schedule.userId}&edit_date=${schedule.scheduleDate}#edit_page" 
								   class="button">編集</a>
								<!-- 削除ボタン -->
								<form action="schedule" method="post" style="display: inline;">
									<input type="hidden" name="action" value="delete">
									<input type="hidden" name="userId" value="${schedule.userId}">
									<input type="hidden" name="scheduleDate" value="${schedule.scheduleDate}">
									<input type="submit" value="削除" class="button danger" onclick="return confirm('本当にこのシフトを削除しますか？');">
								</form>
							</td>
						</c:if>
	 				</tr>
				</c:forEach>
				<c:if test="${empty scheduleList}">
					<tr>
						<td colspan="2">シフト表が作られていません</td>
					</tr>
				</c:if>
			</tbody>
		</table>
		
		<c:if test="${admin_check}">
			<c:choose>
				<c:when test="${scheduleEdit != null}">
					<h2 id="edit_page">シフトの編集</h2>
					<form action="schedule" method="post">
						<input type="hidden" name="action" value="edit">
						<!-- ユーザーID選択 -->
						<p>
							<label for="userId">ユーザーID:</label> 
							<input type="text" id="userId" name="userId" value="${scheduleEdit.userId}" readonly>
						</p>
						<!-- 日時選択 -->
						<p>
							<label for="datetime">出勤時刻:</label>
							<input type="datetime-local" id="datetime" name="new_date" value="${scheduleEdit.scheduleDate}" required>
						</p>
						<input type="hidden" name="old_date" value="${scheduleEdit.scheduleDate}" required>
						<div class="button-group">
							<input type="submit" value="編集">
							<a href="schedule?action=schedule" class="button secondary">編集をやめる</a>
						</div>
					</form>
				</c:when>
	
				<c:otherwise>
					<h2 id="add_page">シフトの追加</h2>
					<form action="schedule" method="post">
						<input type="hidden" name="action" value="add">
						<!-- ユーザーID選択 -->
						<p>
							<label for="userId">ユーザーID:</label> 
							<select name="userId" id="userId" required>
								<c:forEach var="user" items="${userList}">
									<option value="${user.username}">${user.username}</option>
								</c:forEach>
							</select>
						</p>
						<!-- 日時選択 -->
						<p>
							<label for="datetime">出勤時刻:</label>
							<input type="datetime-local" id="datetime" name="scheduleDate" required>
						</p>
						<div class="button-group">
							<input type="submit" value="追加">
						</div>
					</form>
				</c:otherwise>
			</c:choose>
		</c:if>
	</div>
</body>
</html>