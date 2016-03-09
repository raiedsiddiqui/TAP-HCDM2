<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xmlns:h="http://java.sun.com/jsf/html">
<head>
	<title>Tapestry Admin</title>
	<meta name="viewport" content="width=device-width, initial-scale=1.0"></meta>

	
	<script type="text/javascript">
		function showAddUser(){
			document.getElementById("addUserDiv").style.display="block";
		}
		
		function disable() {
			document.getElementById("site").value = "";
		    document.getElementById("site").disabled=true;
		}
		
		function enable() {
		    document.getElementById("site").disabled=false;
		}
		
	</script>

	<style type="text/css">
		.row-fluid{
			margin:10px;
		}
		
		td{
			width:20%;
		}
		
		form input{
			width:auto;
		}
	</style>
</head>
	
<body>	
	<div class="content">
		<%@include file="navbar.jsp" %>
		<div class="row-fluid">
			<c:if test="${(not empty total) && ( not empty active) }">			
				<h2>Users <small>${total} users, ${active} enabled</small></h2>			
			</c:if>
			
			<c:if test="${not empty failed}">
				<div class="alert alert-error">Failed to create user: Username already exists</div>
			</c:if>
			<div class="row-fluid">
				<form action="<c:url value="/manage_users"/>" method="post">
					<fieldset>
						<label>Name:</label>
						<input type="text" name="searchName" value="${searchName}" required />
						<input class="btn btn-primary" type="submit" value="Search" />
						<a href="#addUser" class="btn btn-primary" data-toggle="modal">Add new</a>

					</fieldset>
				</form>
			</div>
			<table class="table">
				<tr>
					<th>Name</th>
					<th>Username</th>
					<th>Role</th>
					<th>Site</th>
					<th>Organization</th>
					<th>Phone Number</th>
					<th>Enable/Disable</th>
					<th>Change password</th>
				</tr>
				<c:forEach items="${users}" var="u">
				<c:if test="${u.role eq 'ROLE_ADMIN'}">
				<tr class="info">
				</c:if>
				<c:if test="${u.role eq 'ROLE_USER'}">
				<tr>
				</c:if>
					<td>${u.name}</td>
					<td>${u.username}</td>
					<td>
						<c:if test="${u.role eq 'ROLE_ADMIN'}">Central Administrator</c:if>
						<c:if test="${u.role eq 'ROLE_USER'}">Volunteer</c:if>
						<c:if test="${u.role eq 'ROLE_LOCAL_ADMIN'}">Local Administrator</c:if>
						<c:if test="${u.role eq 'ROLE_RESEARCHER'}">Researcher</c:if>
						<c:if test="${u.role eq 'ROLE_CLINICIAN'}">Clinician</c:if>
					</td>
					<td>${u.siteName}</td>
					<td>${u.organizationName}</td>
					<td>${u.phoneNumber}</td>
					<td>
						<c:if test="${u.enabled eq 'true'}"><a href="<c:url value="/disable_user/${u.userID}"/>" class="btn btn-danger">Click to disable</a></c:if>
						<c:if test="${u.enabled eq 'false'}"><a href="<c:url value="/enable_user/${u.userID}"/>" class="btn">Click to enable</a></c:if>
					</td>
					<td>
						<button id="cpb-${u.userID}" class="btn btn-info" onclick="document.getElementById('cp-${u.userID}').style.display='block'; document.getElementById('cpb-${u.userID}').style.display='none'">Change password</button> 
						<form class="form-inline" method="post" style="display:none" id="cp-${u.userID}" action="<c:url value="/change_password/${u.userID}"/>">
							<input type="text" name="newPassword"/>
							<input type="submit" class="btn btn-primary" value="Change"/>
						</form>
					</td>
					<!-- Disabling remove user as we should not be removing data from the database -->
					<!-- <td><a href="<c:url value="/remove_user/${u.userID}"/>" class="btn btn-danger">Remove</a></td> -->
				</tr>
				</c:forEach>
			</table>
		</div>

		<div class="row-fluid" id="addUserDiv" style="display:none";>
			
		</div>
	</div>
	
	<!-- Modal -->
<div class="modal fade" id="addUser" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
  <div class="modal-dialog modal-lg">
    <div class="modal-content">
      <div class="modal-header">
    		<button type="button" class="close" data-dismiss="modal" aria-hidden="true">x</button>
    		<h3 id="modalHeader">Add User</h3>
  		</div>
  		<div class="modal-body">
  			<form id="newUser" action="<c:url value="/add_user"/>" method="post">
  				<label><h4>Personal Information</h4></label><br/>
  				<div class="row form-group">
  					<div class="col-md-6">
						<label>First Name:</label>
						<input type="text" name="firstname" class="form-control" required/>
					</div>
					<div class="col-md-6">
						<label>Last Name:</label>
						<input type="text" name="lastname" class="form-control" required/>	
					</div>
				</div>		
				<div class="row form-group">		
					<div class="col-md-6">	
						<label>Email</label>
						<input type="email" name="email" class="form-control" required/>
					</div>
					<div class="col-md-6">	
						<label>Phone #:</label>
						<input type="text" name="phonenumber" class="form-control" required/>
					</div>
				</div>
				<label><h4>User Account</h4></label><br/>
				<div class="row form-group">		
					<div class="col-md-6">
						<label>Username:</label>
						<input type="text" name="username" class="form-control" required/>
						</div>
					<div class="col-md-6">
						<label>Password: </label>
						<input name="password" type="password" class="form-control" required/>
					</div>
				</div>
				<div class="row form-group">
					<div class="col-md-6">
						<label>Site: </label>
						<select name="site" id="site" form="newUser" class="form-control">
							<option value=""></option>
							<c:forEach items="${sites}" var="s">
								<option value="${s.siteId}">${s.name}</option>
							</c:forEach>
						</select>
					</div>	
					<div class="col-md-6">
						<label>Organization:</label>									
						<select name="organization" form="newUser" class="form-control">
							<c:forEach items="${organizations}" var="o">
								<option value="${o.organizationId}">${o.name}</option>
							</c:forEach>
						</select>
					</div>
				</div>
				<label><h4>User Role</h4></label>
				<div class="row form-group">						
						<input type="radio" name="role" onclick="disable()" value="ROLE_ADMIN">Central Admin</input> <br/>
						<input type="radio" name="role" onclick="enable()" value="ROLE_LOCAL_ADMIN" >Local Admin</input> <br/>
						<input type="radio" name="role" onclick="enable()" value="ROLE_CLINICIAN" >Clinician</input> <br/>
						<input type="radio" name="role" onclick="enable()" value="ROLE_RESEARCHER" checked>Researcher</input> <br/>
				</div>
						
			</form>
  		</div>
	  		<div class="modal-footer">
	    		<button class="btn" data-dismiss="modal" aria-hidden="true">Close</button>
				<input class="btn btn-primary" form="newUser" type="submit" value="Add" />
	  		</div>
		</div>
	</div>
</div>
</body>
</html>
