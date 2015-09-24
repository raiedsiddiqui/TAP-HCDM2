<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<title>Volunteers in Admin</title>
		<meta name="viewport" content="width=device-width, initial-scale=1.0"></meta>
			<script src="${pageContext.request.contextPath}/resources/js/tapestryUtils.js"></script>	
			<script type="text/javascript" src="${pageContext.request.contextPath}/resources/js/printelement.js"></script>	

		<style type="text/css">
			.row-fluid{
				margin:10px;
			}
		</style>
		
	</head>
	
	<body>
	<div class="content">
		<%@include file="navbar.jsp" %>
		<c:if test="${not empty volunteerCreated}">
			<div class ="alert alert-info"><spring:message code="message_newVolunteer"/></div>
		</c:if>
		<c:if test="${not empty volunteerDeleted }">
			<div class ="alert alert-info"><spring:message code="message_removeVolunteer"/></div>
		</c:if>
		<c:if test="${not empty volunteerUpdate }">
			<div class ="alert alert-info"><spring:message code="message_modifyVolunteer"/></div>
		</c:if>		
		<div class="row-fluid">
			<h2>Volunteers </h2>
			<div class="row">
				<div class="col-md-10">
					<form action="<c:url value="/view_volunteers"/>" method="POST">
						<fieldset>
							<input type="text" name="searchName" value="${searchName}" placeholder="name" required />
							<input class="btn btn-primary" type="submit" value="Search" />
						</fieldset>
					</form>
				</div>
				<div class="col-md-2">
					<a href="<c:url value="/new_volunteer"/>" class="btn btn-primary" data-toggle="modal">New Volunteer</a>			
				</div>
			</div>
			
			
			<table class="table table-striped">
				<tr>
					<th>Name</th>
					<th>Username</th>
					<th>Experience</th>
					<th>City</th>
					<th>Organization</th>
					<th>Phone Number</th>
					<!-- <th></th> -->
				</tr>
				<c:forEach items="${volunteers}" var="vl">
					<tr>
						<td><a href="<c:url value="/display_volunteer/${vl.volunteerId}"/>">${vl.displayName}</a></td>						
						<td>${vl.userName}</td>
						<td>${vl.experienceLevel}</td>
						<td>${vl.city}</td>
						<td>${vl.organization}</td>
						<td>${vl.homePhone}</td>
						<td><a href="<c:url value="/modify_volunteer/${vl.volunteerId}"/>" class="btn btn-info">Edit</a></td>						
				<!--		<c:if test="${not vl.showDelete}">
							<td><a href="<c:url value="/delete_volunteer/${vl.volunteerId}"/>" Onclick="return confirmDelete()" class="btn btn-danger">Delete</a></td>
						</c:if> -->
					</tr>
				</c:forEach>
			</table>
		</div>
	</div>
	
	</body>
</html>