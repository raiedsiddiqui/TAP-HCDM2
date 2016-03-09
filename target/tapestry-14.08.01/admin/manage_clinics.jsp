<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Tapestry Admin</title>
<meta name="viewport" content="width=device-width, initial-scale=1.0"></meta>
			<script src="${pageContext.request.contextPath}/resources/js/tapestryUtils.js"></script>	
			<script type="text/javascript" src="${pageContext.request.contextPath}/resources/js/printelement.js"></script>
</head>
<body>
	<div class="content">
		<%@include file="navbar.jsp" %>
		<c:if test="${not empty clinicCreated}">
			<div id="message" class ="alert alert-info"><spring:message code="message_newClinic"/></div>
		</c:if>		
		<c:if test="${not empty clinicUpdated}">
			<div id="message" class ="alert alert-info"><spring:message code="message_updateClinic"/></div>
		</c:if>	
		<c:if test="${not empty clinicDeleted}">
			<div id="message" class ="alert alert-info"><spring:message code="message_deleteClinic"/></div>
		</c:if>	
		
		<div class="row">		
			<div class="col-md-9">
				<h2>Clinics </h2>
			</div>
			<div class="col-md-3">
				<a href="<c:url value="/new_clinic"/>" class="btn btn-primary" data-toggle="modal">New Clinic</a>
			</div>				
		</div>
		
		<div id="clinic_list">
			<table class="table">
				<tr>
					<th>Name</th>
					<th>Address</th> 
					<th>Phone</th>
					<th>Site</th>				
				</tr>
				<c:forEach items="${clinics}" var="c">
					<tr>
						<td><a href="<c:url value="/edit_clinic/${c.clinicId}"/>">${c.clinicName}</a></td>						
						<td>${c.address} </td>
						<td>${c.phone}</td>
						<td>${c.siteName}</td>
					</tr>
				</c:forEach>
			</table>
		</div>	
	</div>
</body>
</html>