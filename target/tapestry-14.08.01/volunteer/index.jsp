<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<meta name="viewport" content="width=device-width, initial-scale=1.0 user-scalable=no"></meta>

	<title>Tapestry</title>


  	<%@include file="volunteer_head.jsp" %>


	<style type="text/css">
		html,body{
			height:100%;
		}
		.bootstrap-datetimepicker-widget{
			z-index:9999;
		}
	</style>
	
<script type="text/javascript">
	function activenav() {
		var x = document.getElementById("navhome");
		x.style.backgroundColor="rgb(100, 100, 100)";
	}


	</script>
</head>
	
<body onload="activenav()">	

<%@include file="subNavi.jsp" %>


<!-- 	breadcrumb START-->	
<!-- 	<div id="crumbs"> 
		<ul>
			<li><a href="<c:url value="/client"/>"><img src="${pageContext.request.contextPath}/resources/images/home.png" height="20" width="20" />My Clients</a> </li>
			<c:if test="${not empty patient}">
				<li><a href="">
						<c:choose>
							<c:when test="${not empty patient.preferredName}">
								<b>${patient.preferredName} (${patient.gender})</b>
							</c:when>
							<c:otherwise>
								<b>${patient.displayName} (${patient.gender})</b>
							</c:otherwise>
						</c:choose>
					</a>
				</li>
			</c:if>		
		</ul>


	</div> -->

<!-- 	<div id="visitandbook" class="span12 btn-group">
		<a href="#bookAppointment" role="button" class="btn btn-primary pull-right" data-toggle="modal">Book appointment</a>
	</div> -->
<!-- 	breadcrumb END-->	
	
	<div class="container">		
	<h4 align="left">Welcome, ${name}</h4>			
		<c:choose>
			<c:when test="${reminding_appointments.size()>0}">
		     	<button class="btn btn-primary btn-lg" data-toggle="modal" data-target="#modalReminder">Coming Appointments</button>
		    </c:when>	   
		</c:choose>	
 			

	<c:if test="${not empty announcements}">
		<div class="row-fluid">
			<div class="span12">
				<p><strong>Announcements</strong></p>
				<div class="accordion" id="announcementsAccordion">
					<c:forEach items="${announcements}" var="a">
					<div class="accordion-group">
						<div class="accordion-heading">
							<a class="accordion-toggle" data-toggle="collapse" data-parent="announcementsAccordion" href="#msg${a.messageID}">${fn:substringAfter(a.subject, "ANNOUNCEMENT: ")}</a>
						</div>
						<div id="msg${a.messageID}" class="accordion-body collapse">
							<div class="accordion-inner">
								<p>${a.text}</p>
								<a class="btn btn-danger" href="<c:url value="/dismiss/${a.messageID}"/>">Dismiss</a>
							</div>
						</div>
					</div>
					</c:forEach>
				</div>
			</div>
		</div>
		</c:if>
		<c:if test="${not empty booked}">
			<div class="alert alert-info"><spring:message code="message_newAppointment_successful"/></div>
		</c:if>
		 <c:if test="${not empty noMachedTime }">
			 	<div  class="alert alert-info">
			 		<c:out value="${noMachedTime}" />
			 	</div>
			 </c:if>
		
		<div class="row-fluid">
			<!-- <div class="col-md-8"> -->
				<c:choose>
					<c:when test="${not empty patient}">
						<p> Select an appointment </p>
					</c:when>
					<c:otherwise>
						<h3 class="pagetitle">Appointments <span class="pagedesc">Click on a client name to start the visit</span></h3>

					</c:otherwise>
				</c:choose>
			<!-- </div> -->

			<!--<div class="col-md-2">
				 <a href="<c:url value="/view_activityLogs"/>" id="homebtn" class="btn">Activity Log</a> 
			</div>-->

			<!-- <div class="col-md-2">
				<a href="<c:url value="/view_narratives"/>" id="homebtn" class="btn">Narratives</a>
			</div> -->

		</div>

		<div class="row-fluid">				
			<c:forEach items="${approved_appointments}" var="aa">
	<!-- 								<div class="pname">
					<button type="button" class="cbutton" onclick="location.href='<c:url value="/patient/${aa.patientID}?appointmentId=${aa.appointmentID}"/>'">${aa.patient} <span class="app-date">${aa.date}</span> <span class="tright"> ${aa.time}</button> -->

				<!-- custom -->
					
					<div class="pname">
						<a href="<c:url value="/patient/${aa.patientID}?appointmentId=${aa.appointmentID}"/>">
							<div class="row cbutton">
								<div class="col-sm-6 col-xs-12">
									${aa.patient}
								</div>
								<div class="col-sm-5 col-xs-12">
									${aa.date}
								</div>
								<div class="col-sm-1 col-xs-12">
									${aa.time}
								</div>
							</div>
						</a>
					</div>
						
				<!-- custom -->
			</c:forEach>

		</div>

		<div class="row-fluid">				
			<h3 class="pagetitle">Pending Approval <span class="pagedesc"> The appointments below have not been approved by your volunteer coordinator </span></h3>
			<c:forEach items="${pending_appointments}" var="pa">
				<div class="pname">
					<div class="row pbutton">
						<div class="col-sm-6 col-xs-5">${pa.patient}</div> 
						<div class="col-sm-5 col-xs-5">${pa.date}</div> 
						<div class="col-sm-1 col-xs-2">${pa.time}</div>
				</div>
			</c:forEach>
		</div>

	
		<div class="row-fluid">						
			<div class="panel-group" id="accordion"> 
			  <div class="panel panel-default">
			    <div class="panel-heading">
			      <h4 class="panel-title">
					<a class="accordion-toggle" data-toggle="collapse" href="#collapseDeclined">
			        	Declined Appointments
			      	</a>
			      </h4>
				</div>
			  
		        <div id="collapseDeclined" class="panel-collapse collapse">
						<div class="panel-body">
		    				<c:forEach items="${declined_appointments}" var="da">
								<div class="pname">
								<div class="app-date"> ${da.date} </div>
									<button type="button" class="inactiveclr btn-lg btn-block cbutton">${da.patient} <span class="tright"> ${da.time}</button>
								</div>
							</c:forEach>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
	
	<!-- Modal Reminder-->
		<div class="modal fade" id="modalReminder" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
		  <div class="modal-dialog">
		    <div class="modal-content">
		      <div class="modal-header">
		        <button type="button" class="close" data-dismiss="modal"><span aria-hidden="true">&times;</span><span class="sr-only">Close</span></button>
		        <h4 class="modal-title" id="myModalLabel">Appointment Reminder</h4>
		      </div>
		      <div class="modal-body">		        
		        <table class="table">
					<tr>
						<th>Time</th>
						<th>Volunteer1</th>
						<th>Volunteer2</th>
					<!-- 	<th>Status</th> -->
						<th>Type</th>					
					</tr>
				<c:forEach items="${reminding_appointments}" var="a">
					<tr>
						<td>${a.date}  ${a.time} </td>
						<td>${a.volunteer}</td>
						<td>${a.partner}</td>
				<!-- 		<td>${a.status}</td> -->
						<td>${a.strType}</td>
					</tr>
				</c:forEach>
				</table>
		      </div>
		      <div class="modal-footer">
		      	<button type="button" class="btn btn-default" data-dismiss="modal">Close</button>		        
		      </div>
		      		     
		    </div>
		  </div>
		</div><!--end of Modal Reminder  -->
</body>
</html>
