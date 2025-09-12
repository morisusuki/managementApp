<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<!DOCTYPE html>
<html lang="ja">
<head>
<meta charset="UTF-8">
<title>シフト表</title>
<link rel="stylesheet" href="style.css">
</head>
<body>

	<table>
		<thead>
			<tr>
				<th>日時</th>
				<th>名前</th>
			</tr>
		</thead>
		<tbody>
			<c:forEach var="sc" items="${scheduleList}">
				<tr>
					<td>${sc.userId}</td>
					<td>${sc.ScheduleDate}</td>				
					<%-- <fmt:parseDate value="${sc.ScheduleDate}" pattern="yyyy-MM-dd" var="date" type="date" /> --%>
					<%-- <fmt:formatDate value="${date}" var="newdate" type="date" pattern="yyyy/MM/dd" /> --%>
					<p>${date}</p>
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