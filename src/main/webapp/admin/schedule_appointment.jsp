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
		$(function(){
			$('#dp').datetimepicker({
				pickDate: false,
				pickSeconds: false
			});
			  			
  			$('#bookAppt').click(function(){
		        var btn = $(this)
		        btn.button('loading')
		        setTimeout(function () {
		            btn.button('reset')
		        }, 3000)
		    });
  			
		});
		
	</script>
	</head>
<body>
<div class="content">
<%@include file="navbar.jsp" %>
<div><h4><a href="<c:url value="/manage_appointments"/>" >Appointments</a> > Scheduler</h4></div>	
	<h3>Appointment Scheduler</h3>
 <form id="appt-form" method="post" action="<c:url value="/schedule_appointment"/>">
 	<table>
 		<tr>
 			<td>
 				<label>Patient:</label>
 			</td>
 			<td>
 				<select name="patient" form="appt-form">
					<c:forEach items="${patients}" var="p">							
						<option value="${p.patientID}" <c:if test="${p.patientID eq selectedPatient}">selected</c:if>>${p.firstName} ${p.lastName}</option>
					</c:forEach>
				</select>
 			</td>
 		</tr>
 		<tr>
 			<td>
 				<label>Volunteer:</label>
 			</td>
 			<td>
 				<input data-format="hh:mm:00" type="text" name="volunteer" value="${selectedVolunteer}">	 				
 			</td>
 		</tr>
 		<tr>
 			<td>
 				<label>Partner:</label>
 			</td>
 			<td>
 				<input data-format="hh:mm:00" type="text" name="partner" value="${selectedPartner}">
 			</td>
 		</tr>
 		<tr>
 			<td>
 				<label>Date:</label>		
 			</td>
 			<td>
 				<div id="tp" class="input-append">
					<input data-format="YYYY-MM-dd" type="text" name="appointmentDate" value="${selectedDate}">				   
				</div>
 			</td>
 		</tr>
 		<tr>
 			<td>
 				<label>Time:</label>
 			</td>
 			<td>
 				<div id="tp" class="input-append">
					<input data-format="hh:mm:00" type="text" name="appointmentTime" value="${selectedTime}">				   
				</div>
 			</td>
 		</tr> 		
 		<tr> 			
 			<td colspan="2">
 			<a href="<c:url value="/go_scheduler"/>" class="btn btn-primary" data-toggle="modal">Cancel</a> 	
 				<button id="bookAppt" data-loading-text="Loading..." type="submit" value="Book"  class="btn btn-primary">Book</button>
 				
 			</td>
 		</tr>
 	</table> 
				
  </form>
</div>
</body>
</html>