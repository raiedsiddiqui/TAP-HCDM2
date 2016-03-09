<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<title>Tapestry Admin</title>
	<meta name="viewport" content="width=device-width, initial-scale=1.0"></meta>

	<style type="text/css">
		.row-fluid{
			margin:10px;
		}
	</style>
</head>
	
<body>	
	<div class="content">
		<%@include file="navbar.jsp" %>
		<!-- Button trigger modal -->
		
		<h4 align="left">Welcome, ${name}    </h4>	
		<c:choose>
			<c:when test="${appointments.size()>0}">
		     	<button class="btn btn-primary btn-lg" data-toggle="modal" data-target="#modalReminder">Coming Appointments</button>
		    </c:when>	   
		</c:choose>
		
		<div class="row-fluid">
			<div class="span12">
				<h2>Tapestry Admin</h2>
				<p>Click a link in the navbar to begin</p>
				<p>You can:</p>
				<ul>
					<li>Add a patient</li>
					<li>Add a volunteer or administrator</li>
					<li>Manage installed surveys</li>
					<li>Send messages to volunteers</li>
				</ul>
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
						<th>Status</th>
						<th>Type</th>					
					</tr>
				<c:forEach items="${appointments}" var="a">
					<tr>
						<td>${a.date}  ${a.time} </td>
						<td>${a.volunteer}</td>
						<td>${a.partner}</td>
						<td>${a.status}</td>
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
		</div>

		
	</div>
</body>
</html>
