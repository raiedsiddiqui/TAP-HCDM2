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

<div class="row-fluid">

<div><h4><a href="<c:url value="/view_clients_admin"/>" >Client ></a> ${patient.displayName}</h4></div>

<table>
	<tr>
		<td colspan="2">
			<h2>${patient.displayName}<a href="<c:url value="/edit_patient/${patient.patientID}"/>">Edit</a></h2>
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
			<label>&nbsp MyOscar Verified :</label>&nbsp${patient.myOscarAuthentication}
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
 <!-- 
<h2>Availability</h2>

	<table width="100%" border="1">
		<tr>
			<td width ="200" valign="top">
				<table>
					<tr><th>Monday</th></tr>
					<c:choose>
						<c:when test="${fn:contains(monAvailability, 'non')}">
							<tr><td>N/A</td></tr>		
						</c:when>
						<c:otherwise >
						    <c:forEach items="${monAvailability}" var="moA">	
						    	<tr><td align="center">${moA}</td></tr>		
						    </c:forEach>							
						</c:otherwise>
					</c:choose>						
				</table>
			</td>
			<td width ="200" valign="top">
				<table>
					<tr><th>Tuesday</th></tr>
					<c:choose>
						<c:when test="${fn:contains(tueAvailability, 'non') }">
							<tr><td>N/A</td></tr>		
						</c:when>
						<c:otherwise >
						    <c:forEach items="${tueAvailability}" var="tuA">	
						    	<tr><td align="center">${tuA}</td></tr>		
						    </c:forEach>							
						</c:otherwise>
					</c:choose>
				</table>
			</td>
			<td width ="200" valign="top">
				<table>
					<tr><th>Wednesday</th></tr>
					<c:choose>
						<c:when test="${fn:contains(wedAvailability, 'non') }">
							<tr><td>N/A</td></tr>		
						</c:when>
						<c:otherwise >
						    <c:forEach items="${wedAvailability}" var="weA">	
						    	<tr><td align="center">${weA}</td></tr>		
						    </c:forEach>							
						</c:otherwise>
					</c:choose>
				</table>
			</td>
			<td width ="200" valign="top">
				<table>
					<tr><th>Thursday</th></tr>
					<c:choose>
						<c:when test="${fn:contains(thuAvailability, 'non') }">
							<tr><td>N/A</td></tr>		
						</c:when>
						<c:otherwise >
						    <c:forEach items="${thuAvailability}" var="thA">	
						    	<tr><td align="center">${thA}</td></tr>		
						    </c:forEach>							
						</c:otherwise>
					</c:choose>
				</table>
			</td>
			<td width ="200" valign="top">
			
				<table>
					<tr><th>Friday</th></tr>		
					<c:choose>
						<c:when test="${fn:contains(friAvailability, 'non')}">
							<tr><td>N/A</td></tr>		
						</c:when>
						<c:otherwise >
						    <c:forEach items="${friAvailability}" var="frA">	
						    	<tr><td align="center">${frA}</td></tr>		
						    </c:forEach>							
						</c:otherwise>
					</c:choose>
				</table>
			</td>
		
		</tr>
		
	</table>
 -->
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
			<c:if test="${cVistits.completed}">
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
		</tr>
		<c:forEach items="${surveys}" var="s">
		<tr >
			<td>${s.surveyTitle}</td>
			<td>${s.startDate}</td>
			<td>${s.editDate}</td>
			<td>${s.strCompleted}</td>
		</tr>
		</c:forEach>
	</table>
	

<hr>

	
</div>


</div>

</body>
</html>