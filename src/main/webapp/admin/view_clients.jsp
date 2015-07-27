<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>Clients in Admin</title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0"></meta>
	
			<script type="text/javascript" src="${pageContext.request.contextPath}/resources/js/printelement.js"></script>
	

		<style type="text/css">
			.row-fluid{
				margin:10px;
			}
		</style>
		
		<script type="text/javascript">
			function printTable(){
				$('.table').printThis();
			}
		</script>
</head>
<body>
<div class="content">
		<%@include file="navbar.jsp" %>
		<c:if test="${not empty noSearchName}">
			<div class ="alert alert-info"><spring:message code="message_noSearchName"/></div>
		</c:if>
		<c:if test="${not empty misMatchedVolunteer}">			
			<div class="alert alert-danger"><spring:message code="message_volunteers_misMatchedVolunteer"/></div>
		</c:if>	
		<c:if test="${not empty sameVolunteer}">			
			<div class="alert alert-danger"><spring:message code="message_volunteers_sameVolunteer"/></div>
		</c:if>	
		<c:if test="${not empty updatePatientSuccessfully}">			
			<div class="alert alert-info"><spring:message code="message_patient_updateSuccessful"/></div>
		</c:if>	
		<c:if test="${not empty createPatientSuccessfully}">			
			<div class="alert alert-success"><spring:message code="message_patient_createSuccessful"/></div>
		</c:if>
		<c:if test="${not empty emptyPatients}">
					<div class ="alert alert-info"><spring:message code="message_emptyPatients"/></div>
				</c:if>
		
	<div class="row-fluid">
			<h2>Clients </h2>
			<div class="row-fluid">
				<div class="col-md-10">
					<form action="<c:url value="/view_clients_admin"/>" id="searchPatients" method="POST">
						<fieldset>
							<label>Name:</label>
							<input type="text" name="searchName" form="searchPatients" value="${searchName}" required />
							<input class="btn btn-primary" type="submit" value="Search" />
						</fieldset>
					</form>
				</div>
				<div class="col-md-2">
					<a href="<c:url value="/add_client"/>" class="btn btn-primary" data-toggle="modal">Add New Client</a>
				</div>

			</div>

			
			<table class="table">
				<tr>
					<!-- <th>Client ID</th> -->
					<th>Research ID</th>
					<th>Name</th>
					<!-- <th>DOB</th>
					<th>Age</th> -->
					<th>Gender</th>
					<th>Clinic</th>
					<th>MRP</th>
					<!-- <th>City</th>
					<th>Phone Number</th> -->
					<th>Volunteer1</th>
					<th>Volunteer2</th>

					
				</tr>
				
				<c:forEach items="${patients}" var="p">
					<tr>
						<!-- <td>${p.patientID}</td> -->
						<td>${p.researchID}</td>
						<td><a href="<c:url value="/display_client/${p.patientID}"/>">${p.firstName} ${p.lastName}</a></td>						
						<!-- <td>${p.bod}</td>
						<td>${p.age}</td> -->
						<td>${p.gender}</td>
						<td>${p.clinicName}</td>
						<td>${p.mrpFirstName} ${p.mrpLastName}</td>
						<!-- <td>${p.city}</td>
						<td>${p.homePhone}</td> -->
						<td>${p.volunteerName}</td>
	                    <td>${p.partnerName}</td>
						
					</tr>
				</c:forEach>
			</table>
	</div>
		
</div>

</body>
</html>