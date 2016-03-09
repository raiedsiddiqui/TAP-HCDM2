<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Tapestry Volunteer Appointment PHR Authentication</title>
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<%@include file="volunteer_head.jsp" %>

		
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
			<li><a href="">	<b>Authenticate PHR</b>	</a> </li>
		</ul>

	</div>
 	breadcrumb END-->	

<h2>PHR Authentication</h2> 
<form method="post" id="myoscarAuthentication-form" action="<c:url value="/authenticate_myoscar/${patient.volunteer}?patientId=${patient.patientID}"/>">
<div class="row">
	<div class="col-md-6">
		<h3>Client</h3>
	</div>
</div>
<div class="row form-group">
	<div class="col-md-6">
		<label>First Name:</label>
		<input type="text" clas="form-control" id="client_first_name" name="client_first_name" value="${patient.firstName}"/>
	</div>
	<div class="col-md-6">
		<label>Last Name:</label>
		<input type="text" clas="form-control" id="client_last_name" name="client_last_name" value="${patient.lastName}"/>
	</div>
</div>
<div class="row">
	<div class="col-md-6">
		<h3>Volunteer</h3>
	</div>
</div>
<div class="row form-group">
	<div class="col-md-6">
		<label>First Name:</label>
		<input type="text" clas="form-control" id="volunteer_first_name" name="volunteer_first_name" value="${vFirstName}"/>
	</div>
	<div class="col-md-6">
		<label>Last Name:</label>
		<input type="text" clas="form-control" d="volunteer_last_name" name="volunteer_last_name" value="${vLastName}"/>
	</div>
</div>

<div class="row">
<input class="btn lgbtn pull-r" type="submit" value="Authenticate" />
</form>
<!-- removed class="completevisitbtn"
 --></div>

<!-- 
<table width = "1020">
	<tr>
		<input type="textarea" style="width: 1200px; height: 80px;" value="${termsInfo }">

	</tr>
	<tr>
		<table>
			<tr>
				<td width="500"><label>e-signature</label></td>
				<td width="520">
				<form method="post" id="myoscarAuthentication-form" action="<c:url value="/authenticate_myoscar/${patient.volunteer}?patientId=${patient.patientID}"/>">
					<table>
						<tr>
							<td><label><h3>Client: </h3></label></td>
						</tr>
						<tr>
							<td><label>First Name:</label></td>
						</tr>
						<tr>
							<td><input id="client_first_name" name="client_first_name" value="${patient.firstName}"/></td>
						</tr>
						<tr>
							<td><label>Last Name:</label></td>
						</tr>
						<tr>
							<td><input id="client_last_name" name="client_last_name" value="${patient.lastName}"/></td>
						</tr>
						<tr>
							<td><label><h3>Volunteer: </h3></label></td>
						</tr>
						<tr>
							<td><label>First Name:: </label></td>							
						</tr>
						<tr>
							<td><input id="volunteer_first_name" name="volunteer_first_name" value="${vFirstName}"/></td>
						</tr>
						<tr>
							<td><label>Last Name:</label></td>
						</tr>
						<tr>
							<td><input d="volunteer_last_name" name="volunteer_last_name" value="${vLastName}"/></td>
						</tr>
						<tr>
							<td>&nbsp</td>
						</tr>
						<tr> -->

						<!--  
							<td><a href="<c:url value="/authenticate_myoscar/${patient.volunteer}?patientId=${patient.patientID}"/>" role="button" class="btn btn-primary pull-right lgbtn">Authenticate </a></td>
						-->
<!-- 							<td>
								<input class="completevisitbtn btn btn-primary pull-right" type="submit" value="Authenticate" />
							</td>
						</tr>
					</table>
					</form>
				</td>
			</tr>
		</table>
	</tr>
</table> -->
  
</body>
</html>