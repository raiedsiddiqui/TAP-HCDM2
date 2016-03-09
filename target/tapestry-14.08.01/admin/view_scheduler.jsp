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
			<script src="${pageContext.request.contextPath}/resources/js/bootstrap-datetimepicker.min.js"></script>
			<script type="text/javascript" src="${pageContext.request.contextPath}/resources/js/printelement.js"></script>

		<style type="text/css">
			.row-fluid{
				margin:10px;
			}
		</style>	
		<script type="text/javascript">
		$(function(){
			$('#tp').datetimepicker({
				pickDate: false,
				pickSeconds: false
			});
			$('#dp').datetimepicker({
				pickTime: false,
				startDate: new Date()
  			});
  			
  			$('#bookAppt').click(function(){
		        var btn = $(this)
		        btn.button('loading')
		        setTimeout(function () {
		            btn.button('reset')
		        }, 3000)
		    });
  			
		});
		
		function validatePatient(){
			var selectedPatient = document.getElementById("patient");
			var pValue =selectedPatient.options[selectedPatient.selectedIndex].value;
			
			if (pValue == 0)
			{
				alert("Please select a patient who you would like to schedule an appointment for!");
				return false;
			}
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
		<c:if test="${not empty noAvailableVolunteers}">			
			<div class="alert alert-error"><spring:message code="message_volunteers_noPairsAvailable"/></div>
		</c:if>
		<c:if test="${not empty noFound}">			
			<div class="alert alert-error"><spring:message code="message_empty_result"/></div>
		</c:if>		
		<c:if test="${not empty noMatchTime}">			
			<div class="alert alert-error"><spring:message code="message_volunteers_noMatchTime"/></div>
		</c:if>
		<c:if test="${not empty misMatchedVolunteer}">			
			<div class="alert alert-error"><spring:message code="message_volunteers_misMatchedVolunteer"/></div>
		</c:if>			
		<c:if test="${not empty sameVolunteer}">			
			<div class="alert alert-error"><spring:message code="message_volunteers_sameVolunteer"/></div>
		</c:if>	
		<c:if test="${not empty successToCreateAppointment}">			
			<div class="alert alert-error"><spring:message code="message_newAppointment_successful"/></div>
		</c:if>			
		<c:if test="${not empty failedToCreateAppointment}">			
			<div class="alert alert-error"><spring:message code="message_newAppointment_failed"/></div>
		</c:if>		
		
	</div>
	
	<div><h4><a href="<c:url value="/manage_appointments"/>" >Appointments</a> > Scheduler</h4></div>
	
	<h3>Appointment Scheduler</h3>
<!-- 
	<form  id="schedulerForm" method="post" action="<c:url value="/view_matchTime"/>" onsubmit="return validatePatient()">	
		<c:set var="v1" value="${volunteerOne }"/>
		<c:set var="v2" value="${volunteerTwo }"/>	
		<label><h4>Select Patient/Client: </h4></label>
		<select name="patient" form="schedulerForm" id="patient" >
			<c:forEach items="${patients}" var="p">				
				<option value="${p.patientID}" <c:if test="${p.patientID eq selectedPatient}">selected</c:if>>${p.displayName}</option>
			</c:forEach>
		</select>
		
		<table >
			<tr>
				<td>
					<label><h4>Volunteer 1:</h4></label>
					<select name="volunteer1" form="schedulerForm" id ="volunteer1">
						<c:forEach items="${allvolunteers}" var="v">
							<option value="${v.volunteerId}" <c:if test="${v.volunteerId eq v1.volunteerId}">selected</c:if>>${v.displayName}</option>
						</c:forEach>
						
					</select>
				</td>
				<td>
					<label><h4>Volunteer 2:</h4></label>
					<select name="volunteer2" form="schedulerForm" id="volunteer2">
						<c:forEach items="${allvolunteers}" var="v">
							<option value="${v.volunteerId}" <c:if test="${v.volunteerId eq v2.volunteerId}">selected</c:if>>${v.displayName}</option>
						</c:forEach>
						
					</select>
				</td>
				<td>
					<input class="btn btn-primary" form="schedulerForm" type="submit" value="Go" />	
				</td>
			</tr>
		</table>		
	</form>
 -->
 <div class="input-group"> <span class="input-group-addon">Filter</span>
	<input id="filter" type="text" class="form-control" placeholder="Type here...">
</div>
	<table class="table table-striped">
	<thead>
		<tr>
			<th width="120">Volunteer One</th>
			<th width="100">Phone #</th>
			<th width="200">Email</th>
			<th width="120">Volunteer Two</th>
			<th width="100">Phone #</th>
			<th width="200">Email</th>
			<th width="200">Date/Time</th>			
			<th width="150">Action</th>
			
		</tr>
	<thead>
	<tbody class="searchable">
	<c:forEach items="${matcheList}" var="ml">	
		<tr>
			<td>${ml.vDisplayName}</td>						
			<td>${ml.vPhone}</td>
			<td>${ml.vEmail}</td>
			<td>${ml.pDisplayName}</td>						
			<td>${ml.pPhone}</td>
			<td>${ml.pEmail}</td>
			<td>${ml.matchedTime}</td>										
			<td><a href="<c:url value="/add_appointment/${ml.vId}?vId=${ml.pId}&time=${ml.matchedTime}"/>">Book Appointment</a></td>
			
		</tr>
	</c:forEach>
	<tbody>
	
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