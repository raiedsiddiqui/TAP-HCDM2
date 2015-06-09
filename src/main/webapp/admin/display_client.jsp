<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn"  uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Details of client</title>
<meta name="viewport" content="width=device-width, initial-scale=1.0"></meta>

	
	<script type="text/javascript">
		
	</script>

</head>
<body>
<div class="content">
<%@include file="navbar.jsp" %>
<!-- for redirecting purposes -->
<input type ="hidden" value="${patient}" name = "patient" />
<!-- for redirecting purposes -->
<div class="row-fluid">

<div><h4><a href="<c:url value="/view_clients_admin"/>" >Client ></a> ${patient.displayName}</h4></div>

<table>
	<tr>
		<td colspan="2">
			<h2>${patient.displayName}<a href="<c:url value="/edit_patient/${patient.patientID}"/>">Edit</a></h2>
			<a class="btn btn-warning" href="#">Disable Patient Profile</a>
		</td>		
	</tr>
	<tr>
		<td>
			<label>&nbsp Date of birth:</label>&nbsp ${patient.bod}
		</td>
		<td>
			<label >&nbsp Gender:</label>&nbsp${patient.gender}
		</td>
	<tr>
		<td>
			<label >&nbsp Address :</label>&nbsp${patient.address}
		</td>
		<td>
			<label>&nbsp MRP:</label>&nbsp${patient.mrpFirstName} &nbsp${patient.mrpLastName}
		</td>
	</tr>
	<tr>
		<td>
			<label>&nbsp Phone #:</label>&nbsp${patient.homePhone}
		</td>
		<td>
			<label >&nbsp Clinic :</label>&nbsp${patient.clinicName}
		</td>
	</tr>
	<tr>
		<td>
			<label>&nbsp Email :</label>&nbsp${patient.email}
		</td>
		<td>
			<label>&nbsp PHR Verified :</label>&nbsp${patient.myOscarAuthentication}
	</tr>
	
</table>

		<table width="1020" class="table table-striped">
			<tr>
				<td width="600">
					<table>
						<tr>
							<td><label>&nbsp Alerts: </label></td>
						</tr>
						<tr>
							<td>&nbsp ${patient.alerts}</td>
						</tr>
					</table>	
				</td>
				<td>
					<table>
						<tr>
							<td><label>Assigned Volunteers:</label></td>
						</tr>
						<tr>
							<td>${volunteer1}</td>
						</tr>
						<tr>
							<td>${volunteer2}</td>
						</tr>
					</table>
				</td>
			</tr>
		</table>
 
	<h2>Upcoming Visits</h2>
	<table  class="table table-striped" width="970" border="1">
		<tr>			
			<th width="500">Visit Date</th>			
			<th>Assigned Volunteers</th>			
		</tr>
		<c:forEach items="${upcomingVisits}" var="uVistits">		
		<tr >
			<td>${uVistits.date}</td>			
			<td>${uVistits.volunteer},&nbsp &nbsp ${uVistits.partner}</td>			
		</tr>
		</c:forEach>
	</table>
	<h2>Completed Visits</h2>
	<table  class="table table-striped" width="970" border="1">
		<tr>
			<th width="300">Visit #</th>
			<th width="300"> Visit Date</th>		
			<th>Assigned Volunteers</th>
			<th>Report/PDF</th>
			<th>Report/HL7</th>			
		</tr>
		<c:forEach items="${completedVisits}" var="cVistits">
		<tr >
			<td>${cVistits.appointmentID}</td>	
			<td>${cVistits.date}</td>			
			<td>${cVistits.volunteer},&nbsp &nbsp ${cVistits.partner}</td>		
			
			<c:if test="${showReport}">
				<td><a href="<c:url value="/download_report/${patient.patientID}?appointmentId=${cVistits.appointmentID}"/>">DOWNLOAD</a> </td>
				<td><a href="<c:url value="/generate_report_hl7/${patient.patientID}?appointmentId=${cVistits.appointmentID}"/>">DOWNLOAD</a> </td>
			</c:if>
		</tr>
		</c:forEach>
	</table>
	
	<h2>Surveys <a href="<c:url value="/go_assign_survey/${patient.patientID}"/>">Assign Survey</a> </h2>
	<table  class="table table-striped" width="970" border="1">
		<tr>
			<th width="200">Assigned Surveys</th>
			<th width="250"> Date Started</th>		
			<th width="250">Last Edited</th>
			<th>Completed Status</th>
			<th>Delete</th>
			<th>Results</th>
		</tr>
		<c:forEach items="${surveys}" var="s">
		<tr >
			
			<td><a href="<c:url value="/show_survey/${s.resultID}"/>">${s.surveyTitle}</a></td>
			<td>${s.startDate}</td>
			<td>${s.editDate}</td>
			<td>${s.strCompleted}</td>
			<td><a href="<c:url value="/delete_survey/${s.resultID}"/>" class="btn btn-danger">Remove</a></td>
			<td>
				<c:choose>
					<c:when test="${s.completed}">
					<a href="<c:url value="/view_survey_results/${s.resultID}"/>" class="btn btn-success">View Results</a>
					</c:when>
					<c:otherwise>
					<a href="#" class="btn btn-success disabled">View Results</a>
					</c:otherwise>
				</c:choose>
			</td>
		</tr>
		</c:forEach>
	</table>
	

<hr>

	
</div>


</div>

</body>
</html>