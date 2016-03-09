<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Tapestry Volunteer Add Plans for Appointment</title>
	<meta name="viewport" content="width=device-width, initial-scale=1.0 user-scalable=no"></meta>
<%@include file="volunteer_head.jsp" %>
		
</head>
<body>
<%@include file="subNavi.jsp" %>

<!-- 	breadcrumb START-->	
<!-- 	<div id="crumbs"> 
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
			<li><a href=""><b>Plan</b></a></li>
		</ul>
	</div> -->
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
<h3 class="pagetitle">Add Plan</h3>

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

	
<form id="plansfrm" action="<c:url value="/savePlans"/>" method="post">
<div class="row">
	<div class="col-md-1">
		<label>1</label>
	</div>
	<div class="col-md-11">
		<select class="form-control" name="plan1" form="plansfrm">
			<c:forEach items="${plans}" var="p">							
				<option value="${p}" >${p}</option>
			</c:forEach>
		</select>
	</div>
</div>
<br/>
<div class="row">
	<div class="col-md-1">
		<label>2</label>
	</div>
	<div class="col-md-11">
		<select class="form-control" name="plan2" form="plansfrm">
			<c:forEach items="${plans}" var="p">							
				<option value="${p}" >${p}</option>
			</c:forEach>
		</select>
	</div>
</div>
<br/>
<div class="row">
	<div class="col-md-1">
		<label>3</label>
	</div>
	<div class="col-md-11">
		<select class="form-control" name="plan3" form="plansfrm">
			<c:forEach items="${plans}" var="p">							
				<option value="${p}" >${p}</option>
			</c:forEach>
		</select>
	</div>
</div>
<br/>
<div class="row">
	<div class="col-md-1">
		<label>4</label>
	</div>
	<div class="col-md-11">
		<select class="form-control" name="plan4" form="plansfrm">
			<c:forEach items="${plans}" var="p">							
				<option value="${p}" >${p}</option>
			</c:forEach>
		</select>
	</div>
</div>
<br/>
<div class="row">
	<div class="col-md-1">
		<label>5</label>
	</div>
	<div class="col-md-11">
		<select class="form-control" name="plan5" form="plansfrm">
			<c:forEach items="${plans}" var="p">							
				<option value="${p}" >${p}</option>
			</c:forEach>
		</select>
	</div>
</div>
<br/>
<div class="row">
	<div class="col-md-1">
	<label>6 Specify </label>
		</div>
	<div class="col-md-11">
		<input type="textarea" class="form-control" rows="8" cols="50" name="planSpecify"/><br/>
	</div>
</div>
<br/>
<div class="row">
	<input type="button" value="Cancel" class="btn btn-primary" onclick="javascript:history.go(-1)">
	<button type="submit" align="right" class="lgbtn">Submit</button>
</div>
</form>
</body>
</html>