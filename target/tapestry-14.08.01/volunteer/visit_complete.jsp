<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<title>Tapestry</title>
	<meta name="viewport" content="width=device-width, initial-scale=1.0 user-scalable=no"></meta>

	<%@include file="volunteer_head.jsp" %>
	<style type="text/css">
		html,body{
			height:100%;
		}
		.content{
			overflow-x:auto;
			border-radius:5px;
			-moz-border-radius:5px;
			-webkit-border-radius:5px;
			-o-border-radius:5px;
			-ms-border-radius:5px;
		}
		
		.btn-primary{
			margin-bottom:10px;
		}
		
		.modal-backdrop{
			z-index:0;
		}
		
		.lightbox{
			z-index:1;
		}
		.thumbnail{
			width:320px;
		}
		input[type="radio"] {
			height:50px;	
			width:50px;
		}
	</style>
	
	<script type="text/javascript">
		function showChangePassword(){
			document.getElementById("changePassword").style.display="block";
		}
	</script>
	
</head>
	
<body>
  <div id="headerholder"> 
    <div class="row">
      <div class="col-md-3 tpurple logoheight">
        <img id="logo" src="<c:url value="/resources/images/logow.png"/>" />
      </div>

    <div class="col-md-9 tblack" style="height:63px;">

    </div>
  </div>
</div>
<!-- 	breadcrumb START
	<div id="crumbs"> 
		<ul>
			<li><a href="<c:url value="/client"/>">My Clients</a> </li>
			<li><a href="<c:url value="/?patientId=${patient.patientID}"/>">
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
			<li><a href="<c:url value="/patient/${patient.patientID}?appointmentId=${appointment.appointmentID}"/>">${appointment.date}</a></li>
			<li><a href="">
					<b>Visit Complete</b>
				</a>
			</li>
		</ul>	
	</div>
	breadcrumb END-->

<div class="row">
	<div class="col-md-6">
		<c:if test="${not empty patient}">
			<c:choose>
				<c:when test="${not empty patient.preferredName}">
					<p class="patientname">${patient.preferredName}</p>
				</c:when>
				<c:otherwise>
					<p class="patientname">${patient.displayName}</p>
				</c:otherwise>
			</c:choose>
		</c:if>
		<span class="surveycomp">${appointment.date}</span>
	</div>
</div>

	<div class="content">
		<form id="appt-form" method="post" action="<c:url value="/complete_visit/${appointment.appointmentID}"/>">
	 		<!--<h2> Date: ${appointment.date} </h2>  <br /> 
 	 		<input id="contactedcheck" type="radio" name="contacted_admin" id="contacted_admin" value="true" /> Contacted Ernie
	 		<br />-->
	 		<div> ${alertsLabel}</div>
	 		<!-- 
	 		<h4> What does the clinic need to know about today's visit?
			<br/>Example: unsafe walking, poor access to food, patient not behaving appropriately </h4><br /> -->
	 		<textarea  name="visitAlerts" id="visitAlerts"></textarea><br />
	 		
	 		<div class="row">
	 			<br>
		 		<a href="<c:url value="/patient/${appointment.patientID}?appointmentId=${appointment.appointmentID}"/>" class="tleft btn btn-danger">Cancel</a>
	 			<input class="btn lgbtn pull-right" type="submit" value="Submit" />
 			</div>
 		</form>
 		
 	</div>
 </body>
 </html>