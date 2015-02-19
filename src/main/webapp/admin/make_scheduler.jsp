<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<title>Scheduler in Appointment</title>
		<meta name="viewport" content="width=device-width, initial-scale=1.0"></meta>

	
			<script type="text/javascript" src="${pageContext.request.contextPath}/resources/js/printelement.js"></script>
	

		<style type="text/css">
			.row-fluid{
				margin:10px;
			}
			
		</style>
		
		<script type="text/javascript">
			function printTable(){
				$('.table').printThis();
			}
			
			function getMetchedVolunteers()
			{
				var v = document.getElementById("volunteer1");
				var selectedV = v.options[e.selectedIndex].value;
				
				alert("hi");
				
				alert(selectedV);
			}
		</script>
	</head>
<body>
	<div class="content">
		<%@include file="navbar.jsp" %>
	
		<div class="row-fluid">
			<c:if test="${not empty noAvailableTime}">			
				<div class="alert alert-error"><spring:message code="message_volunteer_noAvailable"/></div>
			</c:if>		

		<c:if test="${not empty noMatchTime}">			
			<div class="alert alert-error"><spring:message code="message_volunteers_noMatchTime"/></div>
		</c:if>		
	</div>
	
	<div><h4><a href="<c:url value="/view_appointments"/>" >Appointments</a> > Scheduler</h4></div>
	<h3>Appointment Scheduler</h3>

	<form  id="schedulerForm" method="post" action="<c:url value="/view_matchTime"/>">
		<c:set var="v1" value="${volunteerOne }"/>
		<c:set var="v2" value="${volunteerTwo }"/>
		<label><h4>Select Patient/Client: </h4></label>
		<select name="patient" form="schedulerForm">
			<c:forEach items="${patients}" var="p">
				<option value="${p.patientID}" <c:if test="${p.patientID eq selectedPatient}">selected</c:if>>${p.displayName}</option>
			</c:forEach>
		</select>
		<table>
		<tr>
			<td>
				<label><h4>Volunteer 1:</h4></label>
				<select name="volunteer1" form="schedulerForm" id ="volunteer1" onChange="getMetchedVolunteers()">
					<c:forEach items="${volunteers}" var="v">
						<option value="${v.volunteerId}" <c:if test="${v.volunteerId eq v1.volunteerId}">selected</c:if>>${v.displayName}</option>
					</c:forEach>
					
				</select>
			</td>
			<td>
				<label><h4>Volunteer 2:</h4></label>
				<select name="volunteer2" form="schedulerForm" id="volunteer2">
					<c:forEach items="${volunteers}" var="v">
						<option value="${v.volunteerId}" <c:if test="${v.volunteerId eq v2.volunteerId}">selected</c:if>>${v.displayName}</option>
					</c:forEach>
					
				</select>
			</td>
			<td><input class="btn btn-primary" form="schedulerForm" type="submit" value="Go" /></td>
		</tr>
	</table>
</form>
<div class="input-group"> <span class="input-group-addon">Filter</span>
	<input id="filter" type="text" class="form-control" placeholder="Type here...">
</div>
<table class="table table-striped">
	<thead>
	<tr>
		<th width="150">Volunteer One</th>
		<th width="100">Phone #</th>
		<th width="200">Email</th>
		<th width="150">Volunteer Two</th>
		<th width="100">Phone #</th>
		<th width="200">Email</th>
		<th width="150">Time Match</th>		
		<th>Action</th>
		
	</tr>
	<thead>
	
	<tbody class="searchable">
	<c:forEach items="${matchedAvailability}" var="ml">
		
			<tr>
				<td>${v1.displayName}</td>						
				<td>${v1.homePhone}</td>
				<td>${v1.email}</td>
				<td>${v2.displayName}</td>						
				<td>${v2.homePhone}</td>
				<td>${v2.email}</td>
				<td>${ml}</td>						
				<td><a href="<c:url value="/book_appointment/${v1.volunteerId}?vId=${v2.volunteerId}&pId=${selectedPatient}&time=${ml}"/>">Book Appointment</a></td>
				
			</tr>
		</c:forEach>
	</tbody>
		
</table>
</div>
	<script type="text/javascript">
$(document).ready(function () {

    (function ($) {

        $('#filter').keyup(function () {

            var rex = new RegExp($(this).val(), 'i');
            $('.searchable tr').hide();
            $('.searchable tr').filter(function () {
                return rex.test($(this).text());
            }).show();

        })

    }(jQuery));

});

	</script>
</body>
</html>