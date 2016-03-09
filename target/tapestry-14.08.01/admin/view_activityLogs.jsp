<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Volunteer Activity Logs/Admin</title>
<script type="text/javascript">
function validateVolunteer(){
	var selectedVolunteer = document.getElementById("search_volunteer");
	var vValue =selectedVolunteer.options[selectedVolunteer.selectedIndex].value;
	
	if (vValue == 0)
	{
		alert("Please select a volunteer first!");
		return false;
	}
}
</script>

</head>
<body>
<div class="content">
	<%@ include file="navbar.jsp"%>
		<div class="row-fluid">
			<div class="span12">
				<h3>Activity Logs </h3>
				<c:if test="${not empty activityUpdate }">
					<div class ="alert alert-info"><spring:message code="message_modifyActivity"/></div>
				</c:if>
				<c:if test="${not empty emptyActivityLogs}">
					<div class ="alert alert-info"><spring:message code="message_emptyActivityLogs"/></div>
				</c:if>
				<div class="row-fluid">
				<form id="searchVolunteerForActivity" action="<c:url value="/view_activity_admin"/>" method="POST" onsubmit="return validateVolunteer()">
					<fieldset>
						<label>Volunteer:</label>
						<select name="search_volunteer" form="searchVolunteerForActivity" id="search_volunteer" >
							<c:forEach items="${volunteers}" var="v">				
								<option value="${v.volunteerId}" <c:if test="${v.volunteerId eq selectedVolunteer}">selected</c:if> >${v.displayName}</option>
							</c:forEach>
						</select>
						<input class="btn btn-primary" type="submit" value="Search" />
					</fieldset>
				</form>
			</div>

				<div class="tab-content">
					<div class="tab-pane active" id="all">						
						<table class="table">
							<tr>
								<th>Date</th>
								<th>Time</th>
								<th>Description</th>
								<th>Volunteer</th>
								
							</tr>
	
							<c:forEach items="${activityLogs}" var="al">
								<tr>
									<td>${al.date}</a></td>									
									<td>${al.time}</td>
									<td width = 260>${al.description}</td>
									<td>${al.volunteerName}</td>									
								</tr>
							</c:forEach>
						</table>
	
					</div>
				</div>
				
			</div>
		</div>
		
	</div>

</body>
</html>