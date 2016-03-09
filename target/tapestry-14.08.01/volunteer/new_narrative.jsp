<%@ page language="java" contentType="text/html; charset=UTF-8"  pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ page import='java.util.Date' %>
<%@ page import="java.text.SimpleDateFormat" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Tapestry Volunteer Add Narrative for Appointment</title>
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<link rel="icon" href="${pageContext.request.contextPath}/resources/images/favicon.ico" type="image/x-icon" />
		<link rel="shortcut icon" href="${pageContext.request.contextPath}/resources/images/favicon.ico" type="image/x-icon" />

		<link href="${pageContext.request.contextPath}/resources/css/bootstrap.min.css" rel="stylesheet" />
		<link href="${pageContext.request.contextPath}/resources/css/bootstrap-responsive.min.css" rel="stylesheet" />  		
		<link href="${pageContext.request.contextPath}/resources/css/font-awesome.css" rel="stylesheet">
		<link href="${pageContext.request.contextPath}/resources/css/bootstrap-datetimepicker.min.css" rel="stylesheet" />

		<script src="${pageContext.request.contextPath}/resources/js/jquery-2.0.3.min.js"></script>
		<script src="${pageContext.request.contextPath}/resources/js/bootstrap.min.js"></script>
		<script src="${pageContext.request.contextPath}/resources/js/bootstrap-datetimepicker.min.js"></script>
		<script src="${pageContext.request.contextPath}/resources/js/bootstrap-lightbox.js"></script>
		
		<!-- CUSTOM CSS -->
	<link href="${pageContext.request.contextPath}/resources/css/breadcrumb.css" rel="stylesheet" /> 
	<link href="${pageContext.request.contextPath}/resources/css/custom.css" rel="stylesheet" /> 
     

	  <link href='http://fonts.googleapis.com/css?family=Roboto+Slab' rel='stylesheet' type='text/css'>
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
<%@ include file="subNavi.jsp" %>
<!-- 	breadcrumb START-->	
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
			<li><a href=""><b>Narrative</b></a></li>
		</ul>
</div>
	<div class="content">
		<div class="row-fluid">
			<div class="span12">
				<form id="newNarrative" action="<c:url value="/add_narrative/${appointment.appointmentID}?flag=1"/>" method="POST">
					<table width="900">						
						<tr><%SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd"); String currentDate = sdf.format(new Date()); %>
							<td><label>Title : </label><input id="narrativeTitle" name="narrativeTitle" type="text" required></td>
							
							<td><label>Edit Time : </label><input id="narrativeDate" name="narrativeDate" data-format="yyyy-MM-dd" type="text" value = "<%= currentDate %>" readonly ></td>
						</tr>
						<tr>
							<td colspan ="2"><hr/></td>
						</tr>
						<tr>
							<td colspan = "2"><textarea name="narrativeContent" rows="10" cols="100"></textarea></td>
						</tr>				
					</table>
				</form>				
			</div>
		</div>
		<br/>
		<div>
		<input type="button" value="Cancel" class="btn btn-primary" onclick="javascript:history.go(-1)">		
		<button id="newNarrativeButton" data-loading-text="Loading..." type="submit"  form="newNarrative" class="btn btn-primary">Save</button>		
		</div>		
	</div>

</body>
</html>
