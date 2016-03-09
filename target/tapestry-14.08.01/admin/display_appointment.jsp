<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn"  uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Details of Appointment</title>
<meta name="viewport" content="width=device-width, initial-scale=1.0"></meta>
		
 	   <script type="text/javascript">
 	  function showV1Narratives(){
			document.getElementById("displayV1NarrativeDiv").style.display="block";
		}
		
		function showV2Narratives(){
			document.getElementById("displayV2NarrativeDiv").style.display="block";
		}
		
	</script>
 	   
	<style type="text/css">
		.row-fluid{
			margin:10px;
		}
		.bootstrap-datetimepicker-widget{
			z-index:9999;
		}

		div.modal-dialog {
			width:90%;
			height:600px;
		}

		.picker--opened .picker__holder {
			background: none;
		}

		.modal-content {
			height: 100%;
		}
	</style>

</head>
<body>
<div class="content">
<%@include file="navbar.jsp" %>

<div class="row-fluid">

<div><h4><a href="<c:url value="/manage_appointments"/>" >Appointment ></a> Client Name</h4></div>

<table>
	<tr>
		<td colspan="2">
			<label>&nbsp Client Name: </label>${appointment.patient}
		</td>		
	</tr>
	<tr>
		<td colspan="2">
			<label>&nbsp Date of Visit:</label>&nbsp ${appointment.date}
		</td>
	<c:if test="${isCentralAdmin}">
		<tr>
			<td colspan="2">
				<label >&nbsp Report :</label><a href="">View Report</a>
			</td>
		</tr>
	</c:if>	
	
	<tr>
		<td>
			<label>&nbsp Volunteer One: </label>&nbsp${appointment.volunteer}
		</td>
		<td>
			<label>&nbsp Volunteer Two :</label>&nbsp${appointment.partner}
		</td>
	</tr>
	<tr>
		<td>
			<label >&nbsp Narrative   </label>
			<c:if test="${not empty narratives1}">
				<a href="#narrative1" class="btn btn-primary" data-toggle="modal">View</a>
			</c:if>			
		</td>
		<td>
			<label>&nbsp Narrative   </label>
			<c:if test="${not empty narratives2}">
				<a href="#narrative2" class="btn btn-primary" data-toggle="modal">View</a>
			</c:if>	
			
	</tr>
</table>

	<h3>Alerts</h3>		${alerts}
<!-- 
	<h2>Detailed Log</h2>
	<table   width="970" border="1">
		<tr>
			
			<th width="500">User Activity</th>
			
			<th>Date</th>
			<th>Time</th>
			
		</tr>
		<c:forEach items="${activities}" var="a">
		
		<tr >
			<td>${a.description }</td>
			<td>${a.date}</td>			
			<td>${a.time}</td>
		</tr>
		</c:forEach>
	</table>	 -->
</div>
</div>
<div class="row-fluid" id="displayV1NarrativeDiv" style="display:none">				
</div>
<div class="row-fluid" id="displayV2NarrativeDiv" style="display:none">				
</div>

<div class="modal fade" id="narrative1" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
 <div class="modal-dialog">
	    <div class="modal-content">
	  		<div class="modal-header">
	    		<button type="button" class="close" data-dismiss="modal" aria-hidden="true">x</button>
	    		<h3 id="modalHeader">Narrative</h3>
	  		</div>
	  		<div class="modal-body">
	  			<table border="1">
		  			<tr>
						<th width="200">Title</th>
						<th width="300">Content</th>
						<th width="400">Edit Date</th>
					</tr>
					<c:forEach items="${narratives1}" var="n1">
					<tr>
						<td>${n1.title}</td>
						<td>${n1.contents}</td>
						<td>${n1.editDate}</td>
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

<div class="modal fade" id="narrative2" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
 <div class="modal-dialog">
	    <div class="modal-content">
	  		<div class="modal-header">
	    		<button type="button" class="close" data-dismiss="modal" aria-hidden="true">x</button>
	    		<h3 id="modalHeader">Narrative</h3>
	  		</div>
	  		<div class="modal-body">
	  			<table border="1">
		  			<tr>
						<th width="200">Title</th>
						<th width="300">Content</th>
						<th width="400">Edit Date</th>
					</tr>
					<c:forEach items="${narratives2}" var="n2">
					<tr>
						<td>${n2.title}</td>
						<td>${n2.contents}</td>
						<td>${n2.editDate}</td>
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


</body>
</html>