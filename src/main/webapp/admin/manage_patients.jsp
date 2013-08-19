<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<title>Tapestry Admin</title>
	<meta name="viewport" content="width=device-width, initial-scale=1.0"></meta>
	<link href="http://netdna.bootstrapcdn.com/twitter-bootstrap/2.3.2/css/bootstrap-combined.min.css" rel="stylesheet"></link>
	<script src="http://code.jquery.com/jquery-2.0.0.min.js"></script>
	<script src="http://netdna.bootstrapcdn.com/twitter-bootstrap/2.3.2/js/bootstrap.min.js"></script>

	<style type="text/css">
		.row-fluid{
			margin:10px;
		}
	</style>
</head>
	
<body>	
  <img src="<c:url value="/resources/images/logo.png"/>" />
	<div class="content">
		<%@include file="navbar.jsp" %>
		<div class="row-fluid">
			<h2>Patients</h2>
			<table class="table">
				<tr>
					<th>Name</th>
					<th>Preferred Name</th>
					<th>Volunteer</th>
					<th>Edit</th>
					<!-- <th>Remove</th> -->
				</tr>
                <c:forEach items="${patients}" var="p">
                <tr>
                    <td>${p.firstName} ${p.lastName} (${p.gender})</td>
                    <td>${p.preferredName}</td>
                    <td>${p.volunteerName}</td>
                    <td><a href="<c:url value="/edit_patient/${p.patientID}"/>" class="btn btn-info">Edit</a></td>
                    <!-- Disabling the ability to delete patients as data relating to a patient should not be deleted -->
                    <!-- <td><a href="<c:url value="/remove_patient/${p.patientID}"/>" class="btn btn-danger">Remove</a></td> -->
                </tr>
                </c:forEach>
			</table>
			<a href="#addPatient" class="btn btn-primary" data-toggle="modal">Add new</a>
		</div>
	</div>
	
	<!-- Modal -->
	<div id="addPatient" class="modal hide fade" tabindex="-1" role="dialog" aria-labelledby="modalHeader" aria-hidden="true">
  		<div class="modal-header">
    		<button type="button" class="close" data-dismiss="modal" aria-hidden="true">x</button>
    		<h3 id="modalHeader">Add Patient</h3>
  		</div>
  		<div class="modal-body">
  			<form id="newPatient" method="post" action="<c:url value="/add_patient"/>">
					<label>First Name:</label>
					<input type="text" name="firstname" required/>
					<label>Last Name:</label>
					<input type="text" name="lastname" required/>
					<label>Preferred Name:</label>
					<input type="text" name="preferredname"/>
					<label>Volunteer</label>
					<select name="volunteer" form="newPatient">
						<c:forEach items="${volunteers}" var="v">
						<option value="${v.userID}">${v.name}</option>
						</c:forEach>
					</select><br />
					<label>Gender</label>
					<select name="gender" form="newPatient">
						<option value="M">Male</option>
						<option value="F">Female</option>
						<option value="O">Other</option>
					</select>
					<label>Notes</label>
					<textarea name="notes"></textarea>
			</form>
  		</div>
  		<div class="modal-footer">
    		<button class="btn" data-dismiss="modal" aria-hidden="true">Close</button>
			<input class="btn btn-primary" form="newPatient" type="submit" value="Add" />
  		</div>
	</div>
</body>
</html>
