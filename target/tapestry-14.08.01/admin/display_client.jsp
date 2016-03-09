<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn"  uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<html>
<head>
<title>Details of client</title>
<meta name="viewport" content="width=device-width, initial-scale=1.0"></meta>
<script src="${pageContext.request.contextPath}/resources/js/jquery-2.0.3.min.js"></script>
	<style type="text/css">
		.row-fluid{
			margin:10px;
		}
	</style>
	<script type="text/javascript">
	function passSurveyResultId(id)
	{
		document.getElementById("hSurveyResultId").value = id;		
	}
 
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

<div class="row">
	<div class="col-md-9">
		<h2>${patient.displayName}</h2>
		<h5>Research ID: ${patient.researchID}</h5>
	</div>
	<div class="col-md-3">		
		<a id="unDisablePatienttBtn" href="#modalDisableNotes" role="button" class="btn btn-danger" data-toggle="modal">Disable Patient</a>
		<a class="btn btn-warning" href="<c:url value="/edit_patient/${patient.patientID}"/>">Edit</a>
	</div>
</div>

<div class="row">
	<div class="col-md-4">
		<label class="control-label">Date of birth:</label> ${patient.bod}
	</div>

	<div class="col-md-4">
		<label class="control-label">Gender:</label> ${patient.gender}
	</div>

	<div class="col-md-4">
		<label class="control-label">Address:</label> ${patient.address}
	</div>
</div>

<div class="row">
	<div class="col-md-4">
		<label class="control-label">MRP:</label> ${patient.mrpFirstName} &nbsp${patient.mrpLastName}
	</div>

	<div class="col-md-4">
		<label class="control-label">Phone:</label> ${patient.homePhone}
	</div>

	<div class="col-md-4">
		<label class="control-label">Clinic:</label> ${patient.clinicName}
	</div>
</div>

<div class="row">
	<div class="col-md-4">
		<label class="control-label">Email:</label> ${patient.email}
	</div>

	<div class="col-md-4">
		<label class="control-label">PHR Verified:</label> ${patient.myOscarAuthentication}
	</div>
</div>


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
	<a href="<c:url value="/book_appointment/${patient.patientID}"/>" class="btn btn-primary" style="float:right">Book Appointment</a>	
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
			<c:if test="${site == 1}"> 
				<th>Report/PDF</th>
				<th>Report/HL7</th>		
			</c:if>	
			<c:if test="${site == 2}"> 
				<th>Report/PDF</th>
			</c:if>
		</tr>
		<c:forEach items="${completedVisits}" var="cVistits">
		<tr >
			<td>${cVistits.appointmentID}</td>	
			<td>${cVistits.date}</td>			
			<td>${cVistits.volunteer},&nbsp &nbsp ${cVistits.partner}</td>				
			<c:if test="${showReport && (site == 1)}">
				<td><a href="<c:url value="/download_report/${patient.patientID}?appointmentId=${cVistits.appointmentID}"/>">DOWNLOAD</a> </td>
				<td><a href="<c:url value="/generate_report_hl7/${patient.patientID}?appointmentId=${cVistits.appointmentID}"/>">DOWNLOAD</a> </td>				
			</c:if>			
			<c:if test="${showReport && (site == 2)}">
				<td><a href="<c:url value="/download_mgReport/${patient.patientID}?appointmentId=${cVistits.appointmentID}"/>">DOWNLOAD</a> </td>
			</c:if>
		</tr>
		</c:forEach>
	</table>	
	<c:if test="${not empty unCompletedVisits}">
		<a id="unCompleteVisitBtn" href="#modalUnCompleteVisit" role="button" class="btn btn-primary" data-toggle="modal">UnCompleted Visits</a>	
	</c:if>
	<h2>Surveys</h2> 
	<a style="float:right" class="btn btn-primary" href="<c:url value="/go_assign_survey/${patient.patientID}"/>">Assign Survey</a> 	
	
	<div style="float:right; width:180px;" class="container">                                        
	  <div class="dropdown">
	    <button class="btn btn-primary dropdown-toggle" type="button" data-toggle="dropdown">Download Report
	    <span class="caret"></span></button>
	    <ul class="dropdown-menu">
	    	<li><a href="<c:url value="/download_clientSurveyReport/${patient.patientID}?name=${patient.displayName}&hasObservernotes=false"/>">Download Report</a></li>

	      	<c:if test="${site == 3}"> 
		      <li>
		      	<a href="<c:url value="/download_clientSurveyReport/${patient.patientID}?name=${patient.displayName}&hasObservernotes=true"/>">Download Report with Observer Notes</a>
		      </li>
	  		</c:if>
	    </ul>
	  </div>
	</div>


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
		<tr>
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
			
			<!--  td>
				<c:if test="${not s.completed}">
					<a href="<c:url value="/complete_survey_results/${s.resultID}?patientId=${patient.patientID}"/>" class="btn btn-success">Complete Survey</a>
					<a id="completeBtn" href="#modalCompleteSurvey" role="button" class="lgbtn" data-toggle="modal" onclick="passSurveyResultId(${s.resultID})">Complete</a>
				</c:if>				
			</td>-->
		</tr>
		</c:forEach>
	</table>
	<hr>	
</div>
</div>
<!--
<div class="modal fade" id="modalCompleteSurvey" tabindex="-1" role="dialog" aria-labelledby="myModalLabel">
  <div class="modal-dialog" role="document">
    <div class="modal-content">
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
        <h4 class="modal-title" id="myModalLabel">
			
        </h4>
      </div>
      <div class="modal-body">
      	<form id="completSurveyFrm" method="post" action="<c:url value="/complete_survey_results/${patient.patientID}"/>">
      		<label>Complete Survey Notes:</label>
			<textarea class="form-control" name="noteBody" id="noteBody"></textarea><br />		
			<input type="hidden" id="hSurveyResultId" name="hSurveyResultId" value=""/>
		</form>
      </div>
      
      <div class="modal-footer">      
      	<input type="submit" form="completSurveyFrm" class="btn btn-primary" value="Submit" />
        <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
      </div>
    </div>
  </div>
</div>-->
 
<div class="modal fade" id="modalUnCompleteVisit" tabindex="-1" role="dialog" aria-labelledby="myModalLabel">
  <div class="modal-dialog" role="document">
    <div class="modal-content">
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
        <h4 class="modal-title" id="myModalLabel">
			
        </h4>
      </div>
      <div class="modal-body">
      	<h2>UnCompleted Visits</h2>
		<table  class="table table-striped" width="970" border="1">
			<tr>
				<th width="300">Visit #</th>
				<th width="300"> Visit Date</th>		
				<th>Assigned Volunteers</th>
				<th>Action</th>					
			</tr>
			<c:forEach items="${unCompletedVisits}" var="uv">
			<tr >
				<td>${uv.appointmentID}</td>	
				<td>${uv.date}</td>			
				<td>${uv.volunteer},&nbsp &nbsp ${cVistits.partner}</td>		
				<td><a href="<c:url value="/complete_visit_byAdmin/${uv.appointmentID}?patientId=${patient.patientID}"/>" class="btn btn-success">Complete Visit</a>
				</td>				
			</tr>
			</c:forEach>
		</table>	
      </div>
      
      <div class="modal-footer">       
        <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
      </div>
    </div>
  </div>
</div> 

<div class="modal fade" id="modalDisableNotes" tabindex="-1" role="dialog" aria-labelledby="myModalLabel">
  <div class="modal-dialog" role="document">
    <div class="modal-content">
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
        <h4 class="modal-title" id="myModalLabel">
			
        </h4>
      </div>
      <div class="modal-body">
      	<form id="diablePatientFrm" method="post" action="<c:url value="/disable_patient/${patient.patientID}"/>">

      		<label>Please explain why the patient is being disabled</label>
			<textarea class="form-control" name="noteBody" id="noteBody"></textarea><br />	
		</form>
      </div>
      
      <div class="modal-footer">      
      	<input type="submit" form="diablePatientFrm" class="btn btn-primary" value="Submit" />
        <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
      </div>
    </div>
  </div>
</div>
</body>
</html>