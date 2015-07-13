<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>KeyObservation</title>
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<%@include file="volunteer_head.jsp" %>
	<!-- 	CUSTOM CSS END -->
		
		<style type="text/css">
		html,body{
			height:100%;
		}
		.content{
/*			overflow-x:auto;
		overflow-y:auto;*/	
			border-radius:5px;
			-moz-border-radius:5px;
			-webkit-border-radius:5px;
			-o-border-radius:5px;
			-ms-border-radius:5px;

		}
		.content a{
			color:#ffffff;
		}
		textarea{
			width:90%;
			margin-right:10px;
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
		
	</style>
		
</head>
<body>

<div id="headerholder">	
<%@include file="subNavi.jsp" %>
</div>
<!-- 	breadcrumb START
	<div id="crumbs"> 
		<ul>
			<li> <a href="<c:url value="/"/>">Appointments</a> </li>
			<li><a href="<c:url value="/?patientId=${patient.patientID}"/>">
				<c:choose>
					<c:when test="${not empty patient.preferredName}">
						<b>${patient.preferredName} (${patient.gender})</b>
					</c:when>
					<c:otherwise>
						<b>${patient.firstName}  ${patient.lastName}(${patient.gender})</b>
					</c:otherwise>
				</c:choose>
				</a>
			</li>
			<li><a href="">${appointment.date}</a></li>
			<li><a href=""><b>Alerts and Key Observations</b></a></li>
		</ul>
	</div>
	breadcrumb END-->
<!-- Message display 
	<div id="visitandbook" class="span12 btn-group">
			<c:if test="${not empty patient.notes}">
				<a href="#modalNotes" class="btn btn-large btn-inverse lgbtn" role="button" data-toggle="modal"><i class="icon-info-sign icon-white"></i></a>
			</c:if>
			<c:if test="${not empty appointment}">
				<a href="<c:url value="/visit_complete/${appointment.appointmentID}"/>" role="button" class="btn btn-primary pull-right lgbtn">Visit Complete</a>
			</c:if>
			<a href="" role="button" class="btn btn-primary pull-right lgbtn" >Submit</a>
	</div>	
	-->
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

	<div id="new_narrative">
		<c:if test="${not empty NarrativeCreated}">					
			<div class ="alert alert-info"><spring:message code="message_newNarrative"/></div>
		</c:if>	
		
		<c:if test="${not appointment.hasNarrative}">
			<a href="<c:url value="/add_narrative"/>"><h2><button class="btn lgbtn pull-right" type="submit">Narrrative</button></h2></a> 
		</c:if>  
	</div>
</div>

	
	<form id="alertAndKeyObservationfrm" action="<c:url value="/saveAlertsAndKeyObservations"/>" method="post">
		<!-- <div class="row">
			<div class="col-md-12">
				<h2>Alerts</h2>
			</div>
		</div>
		<div class="row">
			<div class="col-md-12">
				<p>Are there any alerts that the physcian should be aware of ? </p><br/>
			</div>
		</div>
		<div class="row">
			<div class="col-md-12">
				<input type="textarea" id="visitAlerts" class="form-control" name="alerts"/><br/>
			</div>
		</div> -->
		<div class="row">
			<div class="col-md-12">
				<h2>Social Context</h2>
			</div>
		</div>

		<div class="row">
			<div class="col-md-12">
				<h4>Follow the example below </h4>

				<p><b>[client firstname]</b> is <b>[age]</b> years old. He/She has ____ children and is <b>(employed/volunteering/retired/homemaker/etc)</b>. He/She is living <b>(alone/with partner/with child(ren)/with family/etc)</b>. 
				<br/>
				</p>
				
				<p>The three most important things that I observed about him/her are:<p>
				
				<ol>
					<li>___________</li>
					<li>___________</li>
					<li>___________</li>
				</ol>
					
			</div>
		</div>

		<div class="row">
			<div class="col-md-12">
				<textarea id="visitAlerts" type="textarea" class="form-control" value="${keyObservation}" name="keyObservations"></textarea><br/>
			</div>
		</div>

		<div class="row">
			<div class="col-md-12">
				<input type="button" class="btn btn-primary pull-left" value="Cancel" onclick="javascript:history.go(-1)">
				<button class="pull-right btn lgbtn" type="submit">Next</button>
			</div>
		</div>
	</form>
</body>
</html>