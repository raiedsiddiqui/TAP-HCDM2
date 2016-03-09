<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn"  uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<meta name="viewport" content="width=device-width, initial-scale=1.0 user-scalable=no"></meta>
	<title>Book Appointment</title>	

</head>
<body>

<div class="content">
<div id="navigation_bar">
	<%@ include file="navbar.jsp"%>
	<c:if test="${not empty noSearchName}">
		<div class ="alert alert-info"><spring:message code="message_noSearchName"/></div>
	</c:if>	
</div>
	
	
	<div class="row-fluid" id="bookAppt_admin">
		<%@ include file="../book_appointment.jsp"%>
	</div>
	
</div>
</body>
</html>