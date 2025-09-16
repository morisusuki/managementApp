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
	<form action="schedule" method="get" class="filter-form">
		<input type="month" id="startDate" name="date" 
			value="<c:out value="${param.date}"/>">
			<button type="submit" class="button">適用</button>
	</form>

	<table>
		<thead>
			<tr>
				<th>日時</th>
				<th>名前</th>
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
 				</tr>
			</c:forEach>
			<c:if test="${empty scheduleList}">
				<tr>
					<td colspan="2">シフト表が作られていません</td>
				</tr>
			</c:if>
		</tbody>
	</table>

</body>
</html>