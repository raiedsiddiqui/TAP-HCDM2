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
	
	<script type="text/javascript">
		function showAddUser(){
			document.getElementById("addUserDiv").style.display="block";
		}
	</script>

	<style type="text/css">
		.row-fluid{
			margin:10px;
		}
	</style>
</head>
	
<body>	
  <img src="${pageContext.request.contextPath}/resources/images/logo.png" />
	<div class="content">
		<div class="navbar navbar-inverse">
			<div class="navbar-inner">
				<div class="container">
					<a class="btn btn-navbar" data-toggle="collapse" data-target=".nav-collapse">
        					<span class="icon-bar"></span>
        					<span class="icon-bar"></span>
       				 		<span class="icon-bar"></span>
     					</a>
     					
     					<a class="brand" href="${pageContext.request.contextPath}/">Home</a>
     					<div class="nav-collapse collapse">
						<ul class="nav">
							<li><a href="${pageContext.request.contextPath}/manage_users">Manage Volunteers</a></li>
							<li><a href="${pageContext.request.contextPath}/manage_patients">Manage Patients</a></li>
							<li><a href="#">Manage Surveys</a></li>
							<li><a href="<c:url value="/j_spring_security_logout"/>">Log Out</a></li>
						</ul>
					</div>
				</div>	
			</div>
		</div>
		<div class="row-fluid">
			<h2>Users</h2>
			<table class="table">
				<tr>
					<th>Name</th>
					<th>Username</th>
					<th>Role</th>
					<th>Added</th>
					<th></th>
				</tr>
				<tr>
					<td>Admin</td>
					<td>admin</td>
					<td>ROLE_ADMIN</td>
					<td>2013-07-03 12:00</td>
					<td><a href="#" class="btn btn-danger">Remove</a></td>
				</tr>
			</table>
			<a class="btn btn-primary" onClick="showAddUser()">Add new</a>
		</div>
		<div class="row-fluid" id="addUserDiv" style="display:none";>
			<form>
				<fieldset>
					<legend>Add new user</legend>
					<label>Name:</label>
					<input type="text" name="name"/>
					<label>Username:</label>
					<input type="text" name="username"/>
					<label>Email</label>
					<input type="text" name="email"/>
					<label>Role</label>
					<input type="radio" name="role" value="ROLE_ADMIN">Administrator</input> <br/>
					<input type="radio" name="role" value="ROLE_USER">Caretaker</input> <br/>
					<input class="btn btn-primary" type="submit" value="Add" />
				</fieldset>
			</form>
		</div>
	</div>
</body>
</html>
