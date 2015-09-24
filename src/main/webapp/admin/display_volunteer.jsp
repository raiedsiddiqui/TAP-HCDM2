<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn"  uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Details of volunteer</title>
<meta name="viewport" content="width=device-width, initial-scale=1.0"></meta>

	<script src="${pageContext.request.contextPath}/resources/js/tapestryUtils.js"></script>	
	<script type="text/javascript">
		function showdisplayActivityLog(){
			document.getElementById("displayActivityLogDiv").style.display="block";
		}
		
		function showdisplayMessage(){
			document.getElementById("displayMessageDiv").style.display="block";
		}
	</script>

</head>
<body>
<div class="content">
	<%@include file="navbar.jsp" %>

	<div class="row-fluid">

<div><h4><a href="<c:url value="/view_volunteers"/>"> Volunteers ></a> ${volunteer.displayName}</h4></div>
<div class="row">
	<div class="col-md-10">
			<h2>${volunteer.displayName}<a href="<c:url value="/modify_volunteer/${volunteer.volunteerId}"/>"><span style="font-size:15px;">Edit</span></a><c:if test="${not vl.showDelete}">
							<a href="<c:url value="/delete_volunteer/${volunteer.volunteerId}"/>" Onclick="return confirmDelete()" class="btn btn-danger"><span style="font-size:15px;">Delete</span></a>
						</c:if></h2>
	</div>
	<div class="col-md-1">
			<a  href="#displayActivityLog" class="btn btn-primary" data-toggle="modal">Activity Log</a>
	</div>
	<div class="col-md-1">
			<a href="#displayMessage" class="btn btn-primary" data-toggle="modal">Message</a>
	</div>
</div>

 

	<div class="row form-group">
		<div class="col-md-2">
	    	<label class="control-label">Username:</label>
	    </div>
	    <div class="col-md-2">
	      	<p class="form-control-static">${volunteer.userName}</p>
	  	</div>
	
		<div class="col-md-2">
			<label class="control-label">Address:</label>
		</div>
		<div class="col-md-2">
	  		<p class="form-control-static">${volunteer.address}</p>
		</div>
		<div class="col-md-2">
	    	<label class="control-label">Experience:</label>
	    </div>
	    <div class="col-md-2">
	      	<p class="form-control-static"><c:if test="${volunteer.experienceLevel eq 'E'}">Experienced</c:if>
		<c:if test="${volunteer.experienceLevel eq 'B'}">Beginner</c:if><c:if test="${volunteer.experienceLevel eq 'I'}">Intermediate</c:if></p>
	  	</div>
  </div>

	<div class="row form-group">
		<div class="col-md-2">
	    	<label class="control-label">City:</label>
	    </div>
	    <div class="col-md-2">
	      	<p class="form-control-static">${volunteer.city}</p>
	  	</div>
	
		<div class="col-md-2">
			<label class="control-label">Postal Code:</label>
		</div>
		<div class="col-md-2">
	  		<p class="form-control-static">${volunteer.postalCode}</p>
		</div>
		<div class="col-md-2">
	    	<label class="control-label">Home Phone #:</label>
	    </div>
	    <div class="col-md-2">
	      	<p class="form-control-static">${volunteer.homePhone}</p>
	  	</div>
  </div>


	<div class="row form-group">
		<div class="col-md-2">
	    	<label class="control-label">Cell Phone #:</label>
	    </div>
	    <div class="col-md-2">
	      	<p class="form-control-static">${volunteer.cellPhone}</p>
	  	</div>
	
		<div class="col-md-2">
			<label class="control-label">Email: </label>
		</div>
		<div class="col-md-2">
	  		<p class="form-control-static">${volunteer.email}</p>
		</div>
		
		<div class="col-md-2">
			<label class="control-label">Organization: </label>
		</div>
		<div class="col-md-2">
	  		<p class="form-control-static">${volunteer.organization}</p>
		</div>
		
  </div>

	<div class="row form-group">
		<div class="col-md-2">
	    	<label class="control-label">Emergency Contact:</label>
	    </div>
	    <div class="col-md-2">
	      	<p class="form-control-static">${volunteer.emergencyContact}</p>
	  	</div>
	
		<div class="col-md-2">
			<label class="control-label">Emergency #: </label>
		</div>
		<div class="col-md-2">
	  		<p class="form-control-static">${volunteer.emergencyPhone}</p>
		</div>
  </div>

<h2 class="pagetitleadmin">Notes/Comments</h2>
<div class="row">
	<div class="col-md-6">
  		<p class="form-control-static">${volunteer.notes}</p>
	</div>
</div>
<h2 class="pagetitleadmin">Availability</h2>
<div>
	<table width="100%" border="1" class="table table-striped">
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
</div>
	<h2 class="pagetitleadmin">Upcoming Visits</h2>
	<table  class="table table-striped" width="970" border="1">
		<tr>
			
			<th width="500">Visit Date</th>
			
			<th>Client</th>
		</tr>
		<c:forEach items="${upcomingVisits}" var="uVistits">
		<tr >
			<td>${uVistits.date}</td>
			
			<td>${uVistits.patient}</td>
		</tr>
		</c:forEach>
	</table>
	<h2 class="pagetitleadmin">Completed Visits</h2>
	<table  class="table table-striped" width="970" border="1">
		<tr>
			<th width="300">Visit #</th>
			<th width="300"> Visit Date</th>		
			<th>Client</th>
		</tr>
		<c:forEach items="${completedVisits}" var="cVistits">
		<tr >
			<td>${cVistits.appointmentID}</td>
			<td>${cVistits.date}</td>
			
			<td>${cVistits.patient}</td>
		</tr>
		</c:forEach>
	</table>
	
	<h2>Surveys </h2><h3><a href="<c:url value="/download_volunteerSurveyReport/${volunteer.volunteerId}?name=${volunteer.displayName}"/>">Download Report</a> </h3>
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
			
			<td><a href="<c:url value="/show_volunteerSurvey/${s.resultID}"/>">${s.surveyTitle}</a></td>
			<td>${s.startDate}</td>
			<td>${s.editDate}</td>
			<td>${s.strCompleted}</td>
			<td><a href="<c:url value="/delete_volunteerSurvey/${s.resultID}"/>" class="btn btn-danger">Remove</a></td> 
			<td>
				<c:choose>
					<c:when test="${s.completed}">
					<a href="<c:url value="/view_volunteer_survey_results/${s.resultID}"/>" class="btn btn-success">View Results</a>
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

	<div class="row-fluid" id="displayActivityLogDiv" style="display:none";>
				
	</div>
	<div class="row-fluid" id="displayMessageDiv" style="display:none";>
				
	</div>
</div>

<!-- Modal -->
<div class="modal fade" id="displayActivityLog" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
 	<div class="modal-dialog">
	    <div class="modal-content">
	  		<div class="modal-header">
	    		<button type="button" class="close" data-dismiss="modal" aria-hidden="true">x</button>
	    		<h3 id="modalHeader">Activity Log</h3>
	  		</div>
	  		<div class="modal-body">
	  			<table border="1">
		  			<tr>
						<th width="200">Date</th>
						<th width="300">Time</th>
						<th width="400">Description</th>
					</tr>
					<c:forEach items="${activityLogs}" var="al">
					<tr>
						<td>${al.date}</td>
						<td>${al.time}</td>
						<td>${al.description}</td>
					</tr>
					</c:forEach>
	  			</table>
	  		
	  		</div>
	  		<div class="modal-footer">
	    		<button class="btn" data-dismiss="modal" aria-hidden="true">Close</button>
				
	  		</div>
		</div>
	</div>
</div>	

<div class="modal fade" id="displayMessage" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
	<div class="modal-dialog">
	    <div class="modal-content">
	  		<div class="modal-header">
	    		<button type="button" class="close" data-dismiss="modal" aria-hidden="true">x</button>
	    		<h3 id="modalHeader">In Box</h3>
	  		</div>
	  		<div class="modal-body">
	  			<table class="table">
					<tr>
						<th>Sender</th>
						<th>Subject</th>
						<th>Date</th>
					</tr>
					<c:forEach items="${messages}" var="m">
					<c:choose>
						<c:when test="${! m.read}"><tr onClick="document.location='<c:url value="/view_message/${m.messageID}"/>';" style="font-weight:bold;"></c:when>
						<c:otherwise><tr onClick="document.location='<c:url value="/view_message/${m.messageID}"/>';"></c:otherwise>
					</c:choose>
						<td>${m.sender}</td>
						<td>${m.subject}</td>
						<td>${m.date}</td>
					</tr>
					</c:forEach>
				</table>
	  		</div>
	  		<div class="modal-footer">
	    		<button class="btn" data-dismiss="modal" aria-hidden="true">Close</button>
	  		</div>
		</div>
	</div>
</div>

</div>

</body>
</html>